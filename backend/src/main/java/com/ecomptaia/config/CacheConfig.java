package com.ecomptaia.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    // ==================== CONFIGURATION DU CACHE SIMPLE ====================

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Définir les noms des caches
        cacheManager.setCacheNames(Arrays.asList(
            "configurations",    // Cache des configurations (TTL court car données critiques)
            "users",            // Cache des utilisateurs (TTL moyen)
            "companies",        // Cache des entreprises (TTL long car données stables)
            "ecritures",        // Cache des écritures comptables (TTL court car données fréquemment modifiées)
            "accounts",         // Cache des comptes (TTL long car données stables)
            "thirdParties",     // Cache des tiers (TTL moyen)
            "documents",        // Cache des documents (TTL court)
            "notifications",    // Cache des notifications (TTL très court)
            "dashboards",       // Cache des dashboards (TTL court car données dynamiques)
            "search",           // Cache des recherches (TTL court)
            "reports",          // Cache des rapports (TTL moyen)
            "metrics",          // Cache des métriques (TTL court)
            "exchangeRates",    // Cache des taux de change (TTL court)
            "subscriptionPrices", // Cache des prix d'abonnement (TTL moyen)
            "currencyRates"     // Cache des taux de devise (TTL court)
        ));
        
        return cacheManager;
    }
}
