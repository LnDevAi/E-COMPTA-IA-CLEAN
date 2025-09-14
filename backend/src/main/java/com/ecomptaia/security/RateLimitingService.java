package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service de limitation de taux pour E-COMPTA-IA INTERNATIONAL
 * Protège contre les attaques par déni de service et les abus
 */
@Service
public class RateLimitingService {

    @Value("${app.security.rate-limit-requests-per-minute:100}")
    private int requestsPerMinute;

    @Value("${app.security.rate-limit-burst-capacity:200}")
    private int burstCapacity;

    // Cache des requêtes par IP
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitCache = new ConcurrentHashMap<>();

    /**
     * Vérifie si une requête est autorisée pour une IP donnée
     */
    public boolean isRequestAllowed(String clientIp) {
        if (clientIp == null || clientIp.isEmpty()) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        RateLimitInfo rateLimitInfo = rateLimitCache.computeIfAbsent(clientIp, 
            k -> new RateLimitInfo());

        // Nettoyer les anciennes entrées
        cleanupExpiredEntries(currentTime);

        // Vérifier le rate limiting
        return rateLimitInfo.isRequestAllowed(currentTime, requestsPerMinute, burstCapacity);
    }

    /**
     * Obtient le nombre de requêtes restantes pour une IP
     */
    public int getRemainingRequests(String clientIp) {
        if (clientIp == null || clientIp.isEmpty()) {
            return 0;
        }

        RateLimitInfo rateLimitInfo = rateLimitCache.get(clientIp);
        if (rateLimitInfo == null) {
            return requestsPerMinute;
        }

        long currentTime = System.currentTimeMillis();
        return rateLimitInfo.getRemainingRequests(currentTime, requestsPerMinute);
    }

    /**
     * Obtient le temps de réinitialisation pour une IP
     */
    public long getResetTime(String clientIp) {
        if (clientIp == null || clientIp.isEmpty()) {
            return 0;
        }

        RateLimitInfo rateLimitInfo = rateLimitCache.get(clientIp);
        if (rateLimitInfo == null) {
            return System.currentTimeMillis() + 60000; // 1 minute
        }

        return rateLimitInfo.getResetTime();
    }

    /**
     * Nettoie les entrées expirées du cache
     */
    private void cleanupExpiredEntries(long currentTime) {
        rateLimitCache.entrySet().removeIf(entry -> 
            entry.getValue().isExpired(currentTime));
    }

    /**
     * Classe interne pour stocker les informations de rate limiting
     */
    private static class RateLimitInfo {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
        private final AtomicLong lastRequestTime = new AtomicLong(System.currentTimeMillis());

        public boolean isRequestAllowed(long currentTime, int requestsPerMinute, int burstCapacity) {
            long windowStartTime = windowStart.get();
            
            // Si la fenêtre de temps est expirée, réinitialiser
            if (currentTime - windowStartTime > 60000) { // 1 minute
                windowStart.set(currentTime);
                requestCount.set(0);
            }

            // Vérifier le burst capacity
            if (currentTime - lastRequestTime.get() < 1000) { // Moins d'1 seconde
                if (requestCount.get() >= burstCapacity) {
                    return false;
                }
            }

            // Vérifier le rate limit par minute
            if (requestCount.get() >= requestsPerMinute) {
                return false;
            }

            // Autoriser la requête
            requestCount.incrementAndGet();
            lastRequestTime.set(currentTime);
            return true;
        }

        public int getRemainingRequests(long currentTime, int requestsPerMinute) {
            long windowStartTime = windowStart.get();
            
            // Si la fenêtre de temps est expirée, réinitialiser
            if (currentTime - windowStartTime > 60000) {
                return requestsPerMinute;
            }

            return Math.max(0, requestsPerMinute - requestCount.get());
        }

        public long getResetTime() {
            return windowStart.get() + 60000; // 1 minute
        }

        public boolean isExpired(long currentTime) {
            return currentTime - windowStart.get() > 300000; // 5 minutes
        }
    }
}