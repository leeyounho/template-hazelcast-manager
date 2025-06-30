package com.younho.hazelcast;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.younho.hazelcast")
class TestConfig {
    public TestConfig() {
        System.setProperty("serverName", "TEST_SERVER");
        System.setProperty("appName", "TEST_APP");
        System.setProperty("msgGroup", "TEST_MSG_GROUP");
    }
}
