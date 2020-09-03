package io.mindit.dockerdemo.es;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@ConstructorBinding
@ConfigurationProperties(prefix = "elasticsearch")
@Getter
@AllArgsConstructor
public class ElasticsearchConfig {

    private final String host;
    private final String port;

    public String getConnectionString() {
        return host + ":" + port;
    }

}
