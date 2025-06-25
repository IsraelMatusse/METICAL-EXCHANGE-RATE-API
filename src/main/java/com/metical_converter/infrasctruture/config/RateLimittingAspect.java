package com.metical_converter.infrasctruture.config;

import com.metical_converter.infrasctruture.exceptions.RateLimitExceededException;
import com.metical_converter.infrasctruture.middleware.ClientContext;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class RateLimittingAspect {

    private static final ConcurrentHashMap<String, RateLimit> rateLimits = new ConcurrentHashMap<>();
    private static final int REQUEST_LIMIT = 30;
    private static final long TIME_WINDOW = 60000; // 60 segundos
    private static final long BLOCK_DURATION = 120000; // 2 minutos

    @Before("@annotation(com.metical_converter.infrasctruture.config.RateLimited)")
    public void beforeRequest() throws RateLimitExceededException {
        String clientId = ClientContext.getCurrentTenant();
        RateLimit rateLimit = rateLimits.computeIfAbsent(clientId, k -> new RateLimit());
        if (!rateLimit.tryAcquire()) {
            throw new RateLimitExceededException("Rate limit exceded");
        }
    }

    private static class RateLimit {
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicLong windowStartTime = new AtomicLong(System.currentTimeMillis());
        private final AtomicLong blockEndTime = new AtomicLong(0);

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now < blockEndTime.get()) {
                return false;
            }
            if (now - windowStartTime.get() > TIME_WINDOW) {
                count.set(0);
                windowStartTime.set(now);
            }
            int currentCount = count.get();
            if (currentCount < REQUEST_LIMIT) {
                count.incrementAndGet();
                return true;
            } else {
                blockEndTime.set(now + BLOCK_DURATION);
                return false;
            }
        }
    }



}

