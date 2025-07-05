package com.younho.hazelcast;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@Component
public class HazelcastRetryExecutor {

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public DCOLDataHist get(IMap<Long, DCOLDataHist> map, Serializable id) {
        return map.get(id);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Collection<DCOLDataHist> getValues(IMap<Long, DCOLDataHist> map, PagingPredicate<Long, DCOLDataHist> predicate) {
        return map.values(predicate);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void put(IMap<Long, DCOLDataHist> map, Long id, DCOLDataHist data) {
        map.put(id, data);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void putAll(IMap<Long, DCOLDataHist> map, Map<Long, DCOLDataHist> recordMap) {
        map.putAll(recordMap);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void delete(IMap<Long, DCOLDataHist> map, Long id) {
        map.delete(id);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void executeOnEntries(IMap<Long, DCOLDataHist> map, Predicate<Long, DCOLDataHist> predicate, EntryProcessor<Long, DCOLDataHist, Void> processor) {
        map.executeOnEntries(processor, predicate);
    }

    @Retryable(value = HazelcastException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public long newId(FlakeIdGenerator idGenerator) {
        return idGenerator.newId();
    }
}
