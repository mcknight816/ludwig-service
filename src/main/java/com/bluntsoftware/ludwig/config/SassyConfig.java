package com.bluntsoftware.ludwig.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix="saasy")
public class SassyConfig {

    @Value("${saasy.uri}")
    private String uri;

    @Value("${saasy.appId}")
    private String appId;

    public static String SASSY_URI;
    public static String SASSY_APP_ID;

    @Value("${saasy.uri}")
    public void setNameStatic(String uri){
        SassyConfig.SASSY_URI = uri;
    }

    @Value("${saasy.appId}")
    public void setAppIdStatic(String appId){
        SassyConfig.SASSY_APP_ID = appId;
    }

}
