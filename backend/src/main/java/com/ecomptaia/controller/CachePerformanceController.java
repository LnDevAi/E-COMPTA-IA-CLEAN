package com.ecomptaia.controller;

import com.ecomptaia.service.CacheService;
import com.ecomptaia.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class CachePerformanceController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private PerformanceService performanceService;

    // ==================== GESTION DU CACHE ====================

    /**
     * Mettre en cache une valeur
     */
    @PostMapping("/cache/put")
    public ResponseEntity<Map<String, Object>> putCache(
            @RequestParam String cacheName,
            @RequestParam String key,
            @RequestBody Object value) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            cacheService.put(cacheName, key, value);
            response.put("success", true);
            response.put("message", "Valeur mise en cache avec succès");
            response.put("cacheName", cacheName);
            response.put("key", key);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Récupérer une valeur du cache
     */
    @GetMapping("/cache/get")
    public ResponseEntity<Map<String, Object>> getCache(
            @RequestParam String cacheName,
            @RequestParam String key) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Object value = cacheService.get(cacheName, key, Object.class);
            response.put("success", true);
            response.put("cacheName", cacheName);
            response.put("key", key);
            response.put("value", value);
            response.put("exists", value != null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Supprimer une valeur du cache
     */
    @DeleteMapping("/cache/evict")
    public ResponseEntity<Map<String, Object>> evictCache(
            @RequestParam String cacheName,
            @RequestParam String key) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            cacheService.evict(cacheName, key);
            response.put("success", true);
            response.put("message", "Valeur supprimée du cache");
            response.put("cacheName", cacheName);
            response.put("key", key);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Vider un cache complet
     */
    @DeleteMapping("/cache/clear")
    public ResponseEntity<Map<String, Object>> clearCache(
            @RequestParam String cacheName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            cacheService.clear(cacheName);
            response.put("success", true);
            response.put("message", "Cache vidé avec succès");
            response.put("cacheName", cacheName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Vérifier si une clé existe dans le cache
     */
    @GetMapping("/cache/exists")
    public ResponseEntity<Map<String, Object>> cacheExists(
            @RequestParam String cacheName,
            @RequestParam String key) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists = cacheService.exists(cacheName, key);
            response.put("success", true);
            response.put("cacheName", cacheName);
            response.put("key", key);
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== STATISTIQUES DU CACHE ====================

    /**
     * Obtenir les statistiques d'un cache
     */
    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats(
            @RequestParam String cacheName) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = cacheService.getCacheStats(cacheName);
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statistiques globales
     */
    @GetMapping("/cache/stats/global")
    public ResponseEntity<Map<String, Object>> getGlobalCacheStats() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = cacheService.getGlobalStats();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Vérifier la connectivité du cache
     */
    @GetMapping("/cache/health")
    public ResponseEntity<Map<String, Object>> checkCacheHealth() {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean connected = cacheService.isCacheConnected();
            response.put("success", true);
            response.put("connected", connected);
            response.put("message", connected ? "Cache connecté" : "Cache non connecté");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== MÉTRIQUES DE PERFORMANCE ====================

    /**
     * Enregistrer une requête
     */
    @PostMapping("/metrics/record-request")
    public ResponseEntity<Map<String, Object>> recordRequest(
            @RequestParam String endpoint) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            performanceService.recordRequest(endpoint);
            response.put("success", true);
            response.put("message", "Requête enregistrée");
            response.put("endpoint", endpoint);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Enregistrer le temps de réponse
     */
    @PostMapping("/metrics/record-response-time")
    public ResponseEntity<Map<String, Object>> recordResponseTime(
            @RequestParam String endpoint,
            @RequestParam long responseTimeMs) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            performanceService.recordResponseTime(endpoint, responseTimeMs);
            response.put("success", true);
            response.put("message", "Temps de réponse enregistré");
            response.put("endpoint", endpoint);
            response.put("responseTimeMs", responseTimeMs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Enregistrer une erreur
     */
    @PostMapping("/metrics/record-error")
    public ResponseEntity<Map<String, Object>> recordError(
            @RequestParam String endpoint) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            performanceService.recordError(endpoint);
            response.put("success", true);
            response.put("message", "Erreur enregistrée");
            response.put("endpoint", endpoint);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== STATISTIQUES DE PERFORMANCE ====================

    /**
     * Obtenir les statistiques de performance
     */
    @GetMapping("/metrics/stats")
    public ResponseEntity<Map<String, Object>> getPerformanceStats() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = performanceService.getPerformanceStats();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les informations système
     */
    @GetMapping("/metrics/system")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> systemInfo = performanceService.getPerformanceStats();
            response.put("success", true);
            response.put("data", systemInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les informations mémoire
     */
    @GetMapping("/metrics/memory")
    public ResponseEntity<Map<String, Object>> getMemoryInfo() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> memoryInfo = performanceService.getPerformanceStats();
            response.put("success", true);
            response.put("data", memoryInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== OPTIMISATIONS ====================

    /**
     * Optimiser la mémoire
     */
    @PostMapping("/optimize/memory")
    public ResponseEntity<Map<String, Object>> optimizeMemory() {
        Map<String, Object> response = new HashMap<>();
        try {
            performanceService.optimizeMemory();
            response.put("success", true);
            response.put("message", "Optimisation mémoire effectuée");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Vérifier les fuites mémoire
     */
    @GetMapping("/optimize/memory-leaks")
    public ResponseEntity<Map<String, Object>> checkMemoryLeaks() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> leakInfo = performanceService.checkMemoryLeaks();
            response.put("success", true);
            response.put("data", leakInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Analyser les performances des endpoints
     */
    @GetMapping("/optimize/analysis")
    public ResponseEntity<Map<String, Object>> analyzeEndpointPerformance() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> analysis = performanceService.analyzeEndpointPerformance();
            response.put("success", true);
            response.put("data", analysis);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== GESTION DES MÉTRIQUES ====================

    /**
     * Réinitialiser les métriques
     */
    @PostMapping("/metrics/reset")
    public ResponseEntity<Map<String, Object>> resetMetrics() {
        Map<String, Object> response = new HashMap<>();
        try {
            performanceService.resetMetrics();
            response.put("success", true);
            response.put("message", "Métriques réinitialisées");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== TEST ET DIAGNOSTIC ====================

    /**
     * Test du système de cache et performance
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testCachePerformance() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> testData = performanceService.getTestPerformanceData();
            response.put("success", true);
            response.put("message", "Système de cache et performance opérationnel");
            response.put("data", testData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du test : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Diagnostic complet
     */
    @GetMapping("/diagnostic")
    public ResponseEntity<Map<String, Object>> fullDiagnostic() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> diagnostic = new HashMap<>();
            
            // Test de connectivité du cache
            diagnostic.put("cacheConnected", cacheService.isCacheConnected());
            
            // Statistiques de performance
            diagnostic.put("performanceStats", performanceService.getPerformanceStats());
            
            // Vérification des fuites mémoire
            diagnostic.put("memoryLeaks", performanceService.checkMemoryLeaks());
            
            // Analyse des endpoints
            diagnostic.put("endpointAnalysis", performanceService.analyzeEndpointPerformance());
            
            response.put("success", true);
            response.put("message", "Diagnostic complet effectué");
            response.put("data", diagnostic);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du diagnostic : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
