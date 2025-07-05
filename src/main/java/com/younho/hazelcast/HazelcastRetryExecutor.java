package com.younho.hazelcast;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class HazelcastRetryExecutor {

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <K, V> V get(IMap<K, V> map, K id) {
        return map.get(id);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <K, V> Collection<V> getValues(IMap<K, V> map, Predicate<K, V> predicate) {
        return map.values(predicate);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <K, V> void put(IMap<K, V> map, K id, V data) {
        map.put(id, data);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <K, V> void putAll(IMap<K, V> map, Map<K, V> recordMap) {
        map.putAll(recordMap);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <K, V> void delete(IMap<K, V> map, K id) {
        map.delete(id);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <K, V, R> Map<K, R> executeOnEntries(IMap<K, V> map, Predicate<K, V> predicate, EntryProcessor<K, V, R> processor) {
        return map.executeOnEntries(processor, predicate);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public long newId(FlakeIdGenerator idGenerator) {
        return idGenerator.newId();
    }
}