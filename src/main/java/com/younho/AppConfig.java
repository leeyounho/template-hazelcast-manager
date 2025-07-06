package com.younho;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
@ComponentScan(basePackages = "com.younho.hazelcast")
public class AppConfig {
    public AppConfig() {
        System.setProperty("serverName", "TEST_SERVER");
        System.setProperty("appName", "TEST_APP");
        System.setProperty("msgGroup", "TEST_MSG_GROUP");
    }
}
