package com.fidecent.fbn.hx.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "azure.activedirectory.jose")
public class JoseConfigurationProperties {
    private int connectTimeout;
    private int readTimeout;
    private int sizeLimit;
}
