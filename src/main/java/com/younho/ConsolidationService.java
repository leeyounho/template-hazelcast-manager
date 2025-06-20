package com.younho;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class ConsolidationService {
    private static final Logger log = LoggerFactory.getLogger(ConsolidationService.class);
    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public ConsolidationService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public String triggerConsolidation(String key, boolean deduplicate) {
        log.info("Triggering consolidation for key '{}'. Deduplicate: {}", key, deduplicate);
        IExecutorService executor = hazelcastInstance.getExecutorService("consolidation-executor");
        ConsolidationTask task = new ConsolidationTask(key, deduplicate);
        Future<String> future = executor.submitToKeyOwner(task, key);

        try {
            String result = future.get();
            log.info("Consolidation task finished with result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error during consolidation task execution", e);
            return "Error: " + e.getMessage();
        }
    }
}