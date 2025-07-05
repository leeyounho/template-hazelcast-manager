package com.younho.hazelcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Component
public class HazelcastRetryListener implements RetryListener {

    private static final Logger logger = LoggerFactory.getLogger(HazelcastRetryListener.class);

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (context.getRetryCount() > 0 && throwable == null) {
            logger.info("[Hazelcast Retry] Operation succeeded after {} attempt(s).", context.getRetryCount());
        }
        if (throwable != null) {
            logger.error("[Hazelcast Retry] Operation failed after {} attempt(s).", context.getRetryCount(), throwable);
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        logger.warn("[Hazelcast Retry] Operation failed on attempt {}. Retrying...", context.getRetryCount(), throwable);
    }
}