package com.ecomptaia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PerformanceService {

    @Autowired
    private CacheManager cacheManager;

    // Métriques de performance
    private final Map<String, AtomicLong> requestCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> responseTimeCounters = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> responseTimeHistory = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> errorCounters = new ConcurrentHashMap<>();

    // ==================== MÉTRIQUES DE BASE ====================

    /**
     * Enregistrer une requête
     */
    public void recordRequest(String endpoint) {
        requestCounters.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * Enregistrer le temps de réponse
     */
    public void recordResponseTime(String endpoint, long responseTimeMs) {
        responseTimeCounters.computeIfAbsent(endpoint, k -> new AtomicLong(0)).addAndGet(responseTimeMs);
        
        responseTimeHistory.computeIfAbsent(endpoint, k -> new ArrayList<>()).add(responseTimeMs);
        
        // Garder seulement les 100 dernières mesures
        List<Long> history = responseTimeHistory.get(endpoint);
        if (history.size() > 100) {
            history.remove(0);
        }
    }

    /**
     * Enregistrer une erreur
     */
    public void recordError(String endpoint) {
        errorCounters.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
    }

    // ==================== STATISTIQUES DE PERFORMANCE ====================

    /**
     * Obtenir les statistiques de performance
     */
    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("timestamp", LocalDateTime.now());
        stats.put("systemInfo", getSystemInfo());
        stats.put("memoryInfo", getMemoryInfo());
        stats.put("threadInfo", getThreadInfo());
        stats.put("endpointStats", getEndpointStats());
        stats.put("cacheStats", getCacheStats());
        
        return stats;
    }

    /**
     * Obtenir les informations système
     */
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        systemInfo.put("availableProcessors", runtime.availableProcessors());
        systemInfo.put("totalMemory", formatBytes(runtime.totalMemory()));
        systemInfo.put("freeMemory", formatBytes(runtime.freeMemory()));
        systemInfo.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        systemInfo.put("maxMemory", formatBytes(runtime.maxMemory()));
        systemInfo.put("memoryUsagePercent", 
            String.format("%.2f%%", 
                ((double)(runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory()) * 100));
        
        return systemInfo;
    }

    /**
     * Obtenir les informations mémoire
     */
    public Map<String, Object> getMemoryInfo() {
        Map<String, Object> memoryInfo = new HashMap<>();
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // Heap Memory
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        memoryInfo.put("heapUsed", formatBytes(heapUsed));
        memoryInfo.put("heapMax", formatBytes(heapMax));
        memoryInfo.put("heapUsagePercent", 
            String.format("%.2f%%", ((double) heapUsed / heapMax) * 100));
        
        // Non-Heap Memory
        long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
        long nonHeapMax = memoryBean.getNonHeapMemoryUsage().getMax();
        memoryInfo.put("nonHeapUsed", formatBytes(nonHeapUsed));
        memoryInfo.put("nonHeapMax", formatBytes(nonHeapMax));
        if (nonHeapMax > 0) {
            memoryInfo.put("nonHeapUsagePercent", 
                String.format("%.2f%%", ((double) nonHeapUsed / nonHeapMax) * 100));
        }
        
        return memoryInfo;
    }

    /**
     * Obtenir les informations sur les threads
     */
    public Map<String, Object> getThreadInfo() {
        Map<String, Object> threadInfo = new HashMap<>();
        
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        threadInfo.put("threadCount", threadBean.getThreadCount());
        threadInfo.put("peakThreadCount", threadBean.getPeakThreadCount());
        threadInfo.put("totalStartedThreadCount", threadBean.getTotalStartedThreadCount());
        threadInfo.put("daemonThreadCount", threadBean.getDaemonThreadCount());
        
        return threadInfo;
    }

    /**
     * Obtenir les statistiques par endpoint
     */
    public Map<String, Object> getEndpointStats() {
        Map<String, Object> endpointStats = new HashMap<>();
        
        for (String endpoint : requestCounters.keySet()) {
            Map<String, Object> stats = new HashMap<>();
            
            long requestCount = requestCounters.get(endpoint).get();
            long totalResponseTime = responseTimeCounters.get(endpoint).get();
            long errorCount = errorCounters.getOrDefault(endpoint, new AtomicLong(0)).get();
            
            stats.put("requestCount", requestCount);
            stats.put("errorCount", errorCount);
            stats.put("successRate", requestCount > 0 ? 
                String.format("%.2f%%", ((double)(requestCount - errorCount) / requestCount) * 100) : "0%");
            
            if (requestCount > 0) {
                double avgResponseTime = (double) totalResponseTime / requestCount;
                stats.put("averageResponseTime", String.format("%.2f ms", avgResponseTime));
            }
            
            // Calculer les percentiles
            List<Long> history = responseTimeHistory.get(endpoint);
            if (history != null && !history.isEmpty()) {
                List<Long> sortedHistory = new ArrayList<>(history);
                Collections.sort(sortedHistory);
                
                stats.put("minResponseTime", sortedHistory.get(0) + " ms");
                stats.put("maxResponseTime", sortedHistory.get(sortedHistory.size() - 1) + " ms");
                stats.put("medianResponseTime", calculatePercentile(sortedHistory, 50) + " ms");
                stats.put("p95ResponseTime", calculatePercentile(sortedHistory, 95) + " ms");
                stats.put("p99ResponseTime", calculatePercentile(sortedHistory, 99) + " ms");
            }
            
            endpointStats.put(endpoint, stats);
        }
        
        return endpointStats;
    }

    /**
     * Obtenir les statistiques du cache
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> cacheStats = new HashMap<>();
        
        if (cacheManager != null) {
            String[] cacheNames = cacheManager.getCacheNames().toArray(new String[0]);
            cacheStats.put("cacheCount", cacheNames.length);
            cacheStats.put("cacheNames", Arrays.asList(cacheNames));
        } else {
            cacheStats.put("cacheCount", 0);
            cacheStats.put("cacheNames", new ArrayList<>());
        }
        
        return cacheStats;
    }

    // ==================== OPTIMISATIONS ====================

    /**
     * Optimiser la mémoire
     */
    public void optimizeMemory() {
        System.gc(); // Forcer le garbage collector
    }

    /**
     * Vérifier les fuites mémoire
     */
    public Map<String, Object> checkMemoryLeaks() {
        Map<String, Object> leakInfo = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long beforeGC = runtime.totalMemory() - runtime.freeMemory();
        
        System.gc();
        
        try {
            Thread.sleep(1000); // Attendre que le GC se termine
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long afterGC = runtime.totalMemory() - runtime.freeMemory();
        long freedMemory = beforeGC - afterGC;
        
        leakInfo.put("memoryBeforeGC", formatBytes(beforeGC));
        leakInfo.put("memoryAfterGC", formatBytes(afterGC));
        leakInfo.put("freedMemory", formatBytes(freedMemory));
        leakInfo.put("potentialLeak", freedMemory > 10 * 1024 * 1024); // Plus de 10MB libérés
        
        return leakInfo;
    }

    /**
     * Analyser les performances des endpoints
     */
    public Map<String, Object> analyzeEndpointPerformance() {
        Map<String, Object> analysis = new HashMap<>();
        
        List<Map<String, Object>> slowEndpoints = new ArrayList<>();
        List<Map<String, Object>> errorProneEndpoints = new ArrayList<>();
        
        for (String endpoint : requestCounters.keySet()) {
            long requestCount = requestCounters.get(endpoint).get();
            long totalResponseTime = responseTimeCounters.get(endpoint).get();
            long errorCount = errorCounters.getOrDefault(endpoint, new AtomicLong(0)).get();
            
            if (requestCount > 0) {
                double avgResponseTime = (double) totalResponseTime / requestCount;
                double errorRate = (double) errorCount / requestCount;
                
                // Endpoints lents (> 1000ms en moyenne)
                if (avgResponseTime > 1000) {
                    Map<String, Object> slowEndpoint = new HashMap<>();
                    slowEndpoint.put("endpoint", endpoint);
                    slowEndpoint.put("averageResponseTime", String.format("%.2f ms", avgResponseTime));
                    slowEndpoint.put("requestCount", requestCount);
                    slowEndpoints.add(slowEndpoint);
                }
                
                // Endpoints avec beaucoup d'erreurs (> 5%)
                if (errorRate > 0.05) {
                    Map<String, Object> errorProneEndpoint = new HashMap<>();
                    errorProneEndpoint.put("endpoint", endpoint);
                    errorProneEndpoint.put("errorRate", String.format("%.2f%%", errorRate * 100));
                    errorProneEndpoint.put("errorCount", errorCount);
                    errorProneEndpoint.put("requestCount", requestCount);
                    errorProneEndpoints.add(errorProneEndpoint);
                }
            }
        }
        
        analysis.put("slowEndpoints", slowEndpoints);
        analysis.put("errorProneEndpoints", errorProneEndpoints);
        analysis.put("recommendations", generateRecommendations(slowEndpoints, errorProneEndpoints));
        
        return analysis;
    }

    // ==================== UTILITAIRES ====================

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
     * Calculer un percentile
     */
    private long calculatePercentile(List<Long> values, int percentile) {
        if (values.isEmpty()) return 0;
        
        int index = (int) Math.ceil((percentile / 100.0) * values.size()) - 1;
        return values.get(Math.max(0, index));
    }

    /**
     * Générer des recommandations
     */
    private List<String> generateRecommendations(List<Map<String, Object>> slowEndpoints, 
                                                List<Map<String, Object>> errorProneEndpoints) {
        List<String> recommendations = new ArrayList<>();
        
        if (!slowEndpoints.isEmpty()) {
            recommendations.add("Considérer l'ajout de cache pour les endpoints lents");
            recommendations.add("Optimiser les requêtes de base de données");
            recommendations.add("Implémenter la pagination pour les gros volumes de données");
        }
        
        if (!errorProneEndpoints.isEmpty()) {
            recommendations.add("Améliorer la gestion d'erreurs pour les endpoints problématiques");
            recommendations.add("Ajouter des validations de données");
            recommendations.add("Implémenter des retry mechanisms");
        }
        
        if (slowEndpoints.isEmpty() && errorProneEndpoints.isEmpty()) {
            recommendations.add("Performance optimale détectée");
        }
        
        return recommendations;
    }

    /**
     * Réinitialiser les métriques
     */
    public void resetMetrics() {
        requestCounters.clear();
        responseTimeCounters.clear();
        responseTimeHistory.clear();
        errorCounters.clear();
    }

    /**
     * Données de test pour le service de performance
     */
    public Map<String, Object> getTestPerformanceData() {
        Map<String, Object> testData = new HashMap<>();
        
        testData.put("message", "Service de performance opérationnel");
        testData.put("timestamp", LocalDateTime.now());
        testData.put("version", "1.0.0");
        
        // Simuler quelques métriques
        recordRequest("test-endpoint");
        recordResponseTime("test-endpoint", 150);
        recordRequest("test-endpoint");
        recordResponseTime("test-endpoint", 200);
        
        testData.put("performanceStats", getPerformanceStats());
        testData.put("memoryLeakCheck", checkMemoryLeaks());
        testData.put("endpointAnalysis", analyzeEndpointPerformance());
        
        return testData;
    }
}




