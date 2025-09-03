package com.ecomptaia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    // Cache en mémoire pour les fonctionnalités avancées
    private final Map<String, Map<String, Long>> ttlCache = new ConcurrentHashMap<>();

    // ==================== GESTION BASIQUE DU CACHE ====================

    /**
     * Mettre en cache une valeur
     */
    public void put(String cacheName, String key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * Mettre en cache une valeur avec TTL personnalisé
     */
    public void put(String cacheName, String key, Object value, Duration ttl) {
        put(cacheName, key, value);
        
        // Stocker le TTL en mémoire
        ttlCache.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>())
                .put(key, System.currentTimeMillis() + ttl.toMillis());
    }

    /**
     * Récupérer une valeur du cache
     */
    public <T> T get(String cacheName, String key, Class<T> clazz) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                return clazz.cast(wrapper.get());
            }
        }
        return null;
    }

    /**
     * Récupérer une valeur du cache avec fallback
     */
    public <T> T getOrElse(String cacheName, String key, Class<T> clazz, T defaultValue) {
        T value = get(cacheName, key, clazz);
        return value != null ? value : defaultValue;
    }

    /**
     * Supprimer une valeur du cache
     */
    public void evict(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    /**
     * Vider tout un cache
     */
    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Vérifier si une clé existe dans le cache
     */
    public boolean exists(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            return cache.get(key) != null;
        }
        return false;
    }

    // ==================== GESTION AVANCÉE DU CACHE ====================

    /**
     * Mettre en cache plusieurs valeurs
     */
    public void putAll(String cacheName, Map<String, Object> entries) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            entries.forEach(cache::put);
        }
    }

    /**
     * Récupérer plusieurs valeurs du cache
     */
    public Map<String, Object> getAll(String cacheName, List<String> keys) {
        Map<String, Object> result = new HashMap<>();
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            for (String key : keys) {
                Cache.ValueWrapper wrapper = cache.get(key);
                if (wrapper != null) {
                    result.put(key, wrapper.get());
                }
            }
        }
        return result;
    }

    /**
     * Mettre en cache avec pattern de clé
     */
    public void putWithPattern(String cacheName, String pattern, String key, Object value) {
        String fullKey = pattern + "::" + key;
        put(cacheName, fullKey, value);
    }

    /**
     * Récupérer avec pattern de clé
     */
    public <T> T getWithPattern(String cacheName, String pattern, String key, Class<T> clazz) {
        String fullKey = pattern + "::" + key;
        return get(cacheName, fullKey, clazz);
    }

    /**
     * Supprimer avec pattern de clé
     */
    public void evictWithPattern(String cacheName, String pattern, String key) {
        String fullKey = pattern + "::" + key;
        evict(cacheName, fullKey);
    }

    // ==================== GESTION DES HASHES ====================

    /**
     * Mettre en cache un hash
     */
    public void putHash(String cacheName, String key, Map<String, Object> hash) {
        put(cacheName, key, hash);
    }

    /**
     * Récupérer un hash complet
     */
    @SuppressWarnings("unchecked")
    public Map<Object, Object> getHash(String cacheName, String key) {
        Object value = get(cacheName, key, Object.class);
        if (value instanceof Map) {
            return (Map<Object, Object>) value;
        }
        return new HashMap<>();
    }

    /**
     * Récupérer un champ d'un hash
     */
    public Object getHashField(String cacheName, String key, String field) {
        Map<Object, Object> hash = getHash(cacheName, key);
        return hash.get(field);
    }

    /**
     * Mettre à jour un champ d'un hash
     */
    public void putHashField(String cacheName, String key, String field, Object value) {
        Map<Object, Object> hash = getHash(cacheName, key);
        hash.put(field, value);
        put(cacheName, key, hash);
    }

    // ==================== GESTION DES LISTES ====================

    /**
     * Ajouter à une liste
     */
    public void addToList(String cacheName, String key, Object value) {
        List<Object> list = getList(cacheName, key);
        list.add(value);
        put(cacheName, key, list);
    }

    /**
     * Récupérer une liste
     */
    @SuppressWarnings("unchecked")
    public List<Object> getList(String cacheName, String key) {
        Object value = get(cacheName, key, Object.class);
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return new ArrayList<>();
    }

    /**
     * Récupérer une portion de liste
     */
    public List<Object> getListRange(String cacheName, String key, long start, long end) {
        List<Object> list = getList(cacheName, key);
        int size = list.size();
        int startIndex = (int) Math.max(0, start);
        int endIndex = (int) Math.min(size, end < 0 ? size : end + 1);
        return list.subList(startIndex, endIndex);
    }

    // ==================== GESTION DES SETS ====================

    /**
     * Ajouter à un set
     */
    public void addToSet(String cacheName, String key, Object value) {
        Set<Object> set = getSet(cacheName, key);
        set.add(value);
        put(cacheName, key, set);
    }

    /**
     * Récupérer un set
     */
    @SuppressWarnings("unchecked")
    public Set<Object> getSet(String cacheName, String key) {
        Object value = get(cacheName, key, Object.class);
        if (value instanceof Set) {
            return (Set<Object>) value;
        }
        return new HashSet<>();
    }

    /**
     * Vérifier si un élément est dans un set
     */
    public boolean isInSet(String cacheName, String key, Object value) {
        Set<Object> set = getSet(cacheName, key);
        return set.contains(value);
    }

    // ==================== GESTION DES MÉTRIQUES ====================

    /**
     * Incrémenter un compteur
     */
    public Long increment(String cacheName, String key) {
        return increment(cacheName, key, 1L);
    }

    /**
     * Incrémenter un compteur avec valeur
     */
    public Long increment(String cacheName, String key, long delta) {
        Long currentValue = getCounter(cacheName, key);
        Long newValue = currentValue + delta;
        setCounter(cacheName, key, newValue);
        return newValue;
    }

    /**
     * Définir un compteur
     */
    public void setCounter(String cacheName, String key, long value) {
        put(cacheName, key, value);
    }

    /**
     * Récupérer un compteur
     */
    public Long getCounter(String cacheName, String key) {
        Object value = get(cacheName, key, Object.class);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    // ==================== GESTION DES TTL ====================

    /**
     * Définir un TTL pour une clé
     */
    public void setTTL(String cacheName, String key, Duration ttl) {
        ttlCache.computeIfAbsent(cacheName, k -> new ConcurrentHashMap<>())
                .put(key, System.currentTimeMillis() + ttl.toMillis());
    }

    /**
     * Récupérer le TTL restant
     */
    public Duration getTTL(String cacheName, String key) {
        Map<String, Long> cacheTtl = ttlCache.get(cacheName);
        if (cacheTtl != null) {
            Long expiryTime = cacheTtl.get(key);
            if (expiryTime != null) {
                long remaining = expiryTime - System.currentTimeMillis();
                return remaining > 0 ? Duration.ofMillis(remaining) : Duration.ZERO;
            }
        }
        return Duration.ZERO;
    }

    /**
     * Supprimer le TTL (rendre permanent)
     */
    public void removeTTL(String cacheName, String key) {
        Map<String, Long> cacheTtl = ttlCache.get(cacheName);
        if (cacheTtl != null) {
            cacheTtl.remove(key);
        }
    }

    // ==================== GESTION DES STATISTIQUES ====================

    /**
     * Obtenir les statistiques du cache
     */
    public Map<String, Object> getCacheStats(String cacheName) {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques de base
        stats.put("cacheName", cacheName);
        stats.put("timestamp", new Date());
        
        // Compter les clés (approximatif)
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                // Pour un cache simple, on ne peut pas compter facilement les clés
                stats.put("keyCount", "N/A (cache simple)");
            } else {
                stats.put("keyCount", 0);
            }
        } catch (Exception e) {
            stats.put("keyCount", "N/A");
        }
        
        // Taille mémoire (approximatif)
        try {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            stats.put("memoryUsage", formatBytes(usedMemory));
        } catch (Exception e) {
            stats.put("memoryUsage", "N/A");
        }
        
        return stats;
    }

    /**
     * Obtenir les statistiques globales
     */
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("timestamp", new Date());
        stats.put("cacheManager", cacheManager.getClass().getSimpleName());
        stats.put("cacheType", "ConcurrentMapCacheManager (Simple)");
        
        // Statistiques système
        try {
            Runtime runtime = Runtime.getRuntime();
            stats.put("totalMemory", formatBytes(runtime.totalMemory()));
            stats.put("freeMemory", formatBytes(runtime.freeMemory()));
            stats.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
            stats.put("maxMemory", formatBytes(runtime.maxMemory()));
        } catch (Exception e) {
            stats.put("systemInfo", "Non disponible");
        }
        
        return stats;
    }

    // ==================== UTILITAIRES ====================

    /**
     * Générer une clé de cache avec préfixe
     */
    public String generateKey(String prefix, Object... parts) {
        StringBuilder key = new StringBuilder(prefix);
        for (Object part : parts) {
            key.append("::").append(part);
        }
        return key.toString();
    }

    /**
     * Vérifier la connectivité du cache
     */
    public boolean isCacheConnected() {
        try {
            return cacheManager != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formater les bytes en format lisible
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Nettoyer les caches expirés
     */
    public void cleanup() {
        // Cette méthode peut être appelée périodiquement pour nettoyer les caches
        // Redis gère automatiquement l'expiration, mais on peut ajouter une logique personnalisée ici
    }
}
