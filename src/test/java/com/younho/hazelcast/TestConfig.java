package com.younho.hazelcast;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@ComponentScan(basePackages = "com.younho.hazelcast")
@EnableRetry
public class TestConfig {
    public TestConfig() {
        System.setProperty("serverName", "TEST_SERVER");
        System.setProperty("appName", "TEST_APP");
        System.setProperty("msgGroup", "TEST_MSG_GROUP");
    }
}
