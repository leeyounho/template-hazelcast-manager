package com.younho;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.multimap.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class ConsolidationTask implements Callable<String>, Serializable, HazelcastInstanceAware {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ConsolidationTask.class);

    private final String consolidationKey;
    private final boolean deduplicate;
    private transient HazelcastInstance hazelcastInstance;

    public ConsolidationTask(String consolidationKey, boolean deduplicate) {
        this.consolidationKey = consolidationKey;
        this.deduplicate = deduplicate;
    }

    @Override
    public String call() {
        log.info("Executing ConsolidationTask for key '{}'. Deduplicate: {}", consolidationKey, deduplicate);
        MultiMap<String, Event> multiMap = hazelcastInstance.getMultiMap("events");
        Collection<Event> originalData = multiMap.get(this.consolidationKey);

        if (originalData == null || originalData.isEmpty()) {
            return "No data found for key: " + this.consolidationKey;
        }

        Collection<Event> dataToProcess;
        if (this.deduplicate) {
            log.info("Deduplication is enabled. Original size: {}", originalData.size());
            dataToProcess = new LinkedHashSet<>(originalData);
            log.info("Deduplicated size: {}", dataToProcess.size());
        } else {
            log.info("Deduplication is disabled. Processing with original size: {}", originalData.size());
            dataToProcess = originalData;
        }

        String consolidatedData = dataToProcess.stream()
                .map(Event::getData)
                .collect(Collectors.joining(", "));

        log.info("Simulating message sending: [{}]", consolidatedData);
        multiMap.remove(this.consolidationKey);
        log.info("Removed all data for key: {}", this.consolidationKey);

        return String.format("Successfully processed %d events. Message sent.", dataToProcess.size());
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}