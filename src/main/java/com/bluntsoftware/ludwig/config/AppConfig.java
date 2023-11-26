package com.bluntsoftware.ludwig.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix="app")
public class AppConfig {

    @Value("${app.host}")
    private String host;
}
