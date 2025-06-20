package com.younho;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.multimap.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataCollectionService {
    private static final Logger log = LoggerFactory.getLogger(DataCollectionService.class);
    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public DataCollectionService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void collectEvent(String key, Event event) {
        MultiMap<String, Event> multiMap = hazelcastInstance.getMultiMap("events");
        multiMap.put(key, event);
        log.info("Collected event for key '{}': {}", key, event);
    }
}