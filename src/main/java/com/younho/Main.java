package com.younho;

import com.younho.hazelcast.HazelcastEventListener;
import com.younho.hazelcast.HazelcastManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HazelcastManager.class, HazelcastEventListener.class);
    }
}