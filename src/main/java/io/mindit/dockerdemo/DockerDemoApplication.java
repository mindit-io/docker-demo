package io.mindit.dockerdemo;

import io.mindit.dockerdemo.es.ElasticsearchConfig;
import java.util.stream.Stream;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.support.DatabaseStartupValidator;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "io.mindit.dockerdemo.es")
@EnableJpaRepositories(basePackages = "io.mindit.dockerdemo.jpa")
@EnableConfigurationProperties(ElasticsearchConfig.class)
@Slf4j
public class DockerDemoApplication {

    @Bean
    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
        // excellent tutorial on how to wait for database startup:
        // https://deinum.biz/2020-06-30-Wait-for-database-startup/
        var dsv = new DatabaseStartupValidator();
        dsv.setDataSource(dataSource);
        dsv.setValidationQuery(DatabaseDriver.ORACLE.getValidationQuery());
        dsv.setInterval(5); // retry from 5 to 5 seconds
        dsv.setTimeout(300); // fail after 5 minutes
        return dsv;
    }

    @Bean
    public BeanFactoryPostProcessor dependsOnPostProcessor() {
        return bf -> {
            // Let beans that need the database depend on the DatabaseStartupValidator
            String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
            Stream.of(jpa)
                .map(bf::getBeanDefinition)
                .forEach(it -> it.setDependsOn("databaseStartupValidator"));
        };
    }

    @Bean
    public RestHighLevelClient client(ElasticsearchConfig elasticsearchConfig) {
        log.debug("Connecting to elasticsearch host: {}, port: {}",
            elasticsearchConfig.getHost(), elasticsearchConfig.getPort());
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo(elasticsearchConfig.getConnectionString())
            .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate(ElasticsearchConfig elasticsearchConfig) {
        return new ElasticsearchRestTemplate(client(elasticsearchConfig));
    }

    public static void main(String[] args) {
        SpringApplication.run(DockerDemoApplication.class, args);
    }

}
