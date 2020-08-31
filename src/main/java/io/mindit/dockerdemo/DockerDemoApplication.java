package io.mindit.dockerdemo;

import java.util.stream.Stream;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.DatabaseStartupValidator;

@SpringBootApplication
public class DockerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerDemoApplication.class, args);
    }

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
}
