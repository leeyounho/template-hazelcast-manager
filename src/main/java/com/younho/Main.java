package com.younho;

import com.younho.hazelcast.HazelcastManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // for test
        System.setProperty("msgGroup", "TEST_MSG_GROUP");
        System.setProperty("serverName", "TEST_SERVER_NAME");
        System.setProperty("appName", "TEST_APP_NAME");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HazelcastManager.class);

//        // Bean 가져오기
//        DataCollectionService dataSvc = context.getBean(DataCollectionService.class);
//        ConsolidationService conSvc = context.getBean(ConsolidationService.class);
//
//        String currentZone = System.getProperty("hz.zone");
//        logger.info("********** Application Started in Zone: {} **********", currentZone);
//
//        // 하나의 노드에서만 테스트 로직을 실행하도록 제어
//        if (isLeaderNode(currentZone)) {
//            runTestSequence(dataSvc, conSvc);
//        } else {
//            logger.info("This is a follower node, waiting for tasks from other members...");
//        }
    }

    private static void runTestSequence(DataCollectionService dataSvc, ConsolidationService conSvc) {
        try {
            logger.info("This is a leader node, starting test sequence...");
            String testKey = "order-group-123";

            logger.info("--- 1. Data Collection ---");
            dataSvc.collectEvent(testKey, new Event("Apple"));
            dataSvc.collectEvent(testKey, new Event("Banana"));
            dataSvc.collectEvent(testKey, new Event("Apple")); // Duplicate data
            logger.info("--- Data Collection Finished ---\n");

            Thread.sleep(3000); // Wait for logs to be easily readable

            logger.info("--- 2. Consolidation (Deduplication Enabled) ---");
            conSvc.triggerConsolidation(testKey, true);
            logger.info("--- Consolidation Finished ---\n");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Main thread interrupted", e);
        }
    }

    private static boolean isLeaderNode(String zone) {
        // 실제 운영환경에서는 Zookeeper, etcd 등을 사용하거나
        // 첫번째로 클러스터에 붙는 멤버를 리더로 정하는 등의 로직이 필요합니다.
        // 여기서는 테스트 편의를 위해 호스트네임에 '01' 또는 'a'가 포함되면 리더로 간주합니다.
        return zone != null && (zone.contains("01") || zone.endsWith("a"));
    }

    private static String getZoneFromHostname() throws UnknownHostException {
        String hostname = System.getenv("HOSTNAME");
        if (hostname == null || hostname.isEmpty()) {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        return hostname;
    }
}