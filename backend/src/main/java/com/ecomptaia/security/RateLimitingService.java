package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitingService {

    @Value("${rate.limit.requests-per-minute:100}")
    private int maxRequestsPerMinute;

    @Value("${rate.limit.burst-capacity:200}")
    private int burstCapacity;

    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

    public boolean isAllowed(String clientId) {
        RequestCounter counter = requestCounters.computeIfAbsent(clientId, k -> new RequestCounter());
        return counter.isAllowed();
    }

    public void recordRequest(String clientId) {
        RequestCounter counter = requestCounters.get(clientId);
        if (counter != null) {
            counter.recordRequest();
        }
    }

    @CacheEvict(value = "rateLimit", allEntries = true)
    public void clearExpiredCounters() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(1);
        requestCounters.entrySet().removeIf(entry -> 
            entry.getValue().getLastRequestTime().isBefore(cutoff));
    }

    private class RequestCounter {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private volatile LocalDateTime lastRequestTime = LocalDateTime.now();
        private volatile LocalDateTime windowStart = LocalDateTime.now();

        public boolean isAllowed() {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(windowStart.plusMinutes(1))) {
                requestCount.set(0);
                windowStart = now;
            }
            int currentCount = requestCount.get();
            return currentCount < burstCapacity && currentCount < maxRequestsPerMinute;
        }

        public void recordRequest() {
            requestCount.incrementAndGet();
            lastRequestTime = LocalDateTime.now();
        }

        public LocalDateTime getLastRequestTime() {
            return lastRequestTime;
        }
    }

    public Map<String, Object> getRateLimitStats(String clientId) {
        RequestCounter counter = requestCounters.get(clientId);
        if (counter == null) {
            return Map.of("clientId", clientId, "status", "no_requests", "remaining", maxRequestsPerMinute);
        }
        return Map.of(
            "clientId", clientId,
            "requestsInCurrentWindow", counter.requestCount.get(),
            "maxRequestsPerMinute", maxRequestsPerMinute,
            "burstCapacity", burstCapacity,
            "lastRequestTime", counter.lastRequestTime,
            "windowStart", counter.windowStart
        );
    }
}
