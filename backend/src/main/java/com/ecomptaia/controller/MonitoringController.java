package com.ecomptaia.controller;

import com.ecomptaia.entity.Metric;
import com.ecomptaia.repository.MetricRepository;
import com.ecomptaia.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*")
public class MonitoringController {

    @Autowired
    private MonitoringService monitoringService;
    
    @Autowired
    private MetricRepository metricRepository;

    // ==================== CRÉATION DE MÉTRIQUES ====================

    /**
     * Créer une nouvelle métrique
     */
    @PostMapping("/metrics/create")
    public ResponseEntity<Map<String, Object>> createMetric(
            @RequestParam String metricName,
            @RequestParam String metricType,
            @RequestParam Double value,
            @RequestParam String category,
            @RequestParam String source,
            @RequestParam(required = false) Long entrepriseId,
            @RequestParam(required = false) Long utilisateurId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Metric.MetricType type = Metric.MetricType.valueOf(metricType.toUpperCase());
            Metric.MetricCategory cat = Metric.MetricCategory.valueOf(category.toUpperCase());
            
            Metric metric = monitoringService.createMetric(metricName, type, value, cat, source, entrepriseId, utilisateurId);
            
            response.put("success", true);
            response.put("message", "Métrique créée avec succès");
            response.put("metric", metric);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Créer une métrique avec seuils
     */
    @PostMapping("/metrics/create-with-thresholds")
    public ResponseEntity<Map<String, Object>> createMetricWithThresholds(
            @RequestParam String metricName,
            @RequestParam String metricType,
            @RequestParam Double value,
            @RequestParam String category,
            @RequestParam String source,
            @RequestParam(required = false) Double thresholdWarning,
            @RequestParam(required = false) Double thresholdCritical,
            @RequestParam(required = false) Long entrepriseId,
            @RequestParam(required = false) Long utilisateurId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Metric.MetricType type = Metric.MetricType.valueOf(metricType.toUpperCase());
            Metric.MetricCategory cat = Metric.MetricCategory.valueOf(category.toUpperCase());
            
            Metric metric = monitoringService.createMetricWithThresholds(metricName, type, value, cat, source, 
                                                                        thresholdWarning, thresholdCritical, entrepriseId, utilisateurId);
            
            response.put("success", true);
            response.put("message", "Métrique créée avec seuils avec succès");
            response.put("metric", metric);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== RECHERCHE ET RÉCUPÉRATION ====================

    /**
     * Obtenir les métriques par entreprise
     */
    @GetMapping("/metrics/by-entreprise")
    public ResponseEntity<Map<String, Object>> getMetricsByEntreprise(@RequestParam Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Metric> metrics = monitoringService.getMetricsByEntreprise(entrepriseId);
            
            response.put("success", true);
            response.put("metrics", metrics);
            response.put("count", metrics.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les métriques par catégorie
     */
    @GetMapping("/metrics/by-category")
    public ResponseEntity<Map<String, Object>> getMetricsByCategory(@RequestParam String category) {
        Map<String, Object> response = new HashMap<>();
        try {
            Metric.MetricCategory cat = Metric.MetricCategory.valueOf(category.toUpperCase());
            List<Metric> metrics = monitoringService.getMetricsByCategory(cat);
            
            response.put("success", true);
            response.put("metrics", metrics);
            response.put("count", metrics.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les métriques par statut
     */
    @GetMapping("/metrics/by-status")
    public ResponseEntity<Map<String, Object>> getMetricsByStatus(@RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            Metric.MetricStatus stat = Metric.MetricStatus.valueOf(status.toUpperCase());
            List<Metric> metrics = monitoringService.getMetricsByStatus(stat);
            
            response.put("success", true);
            response.put("metrics", metrics);
            response.put("count", metrics.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les métriques critiques
     */
    @GetMapping("/metrics/critical")
    public ResponseEntity<Map<String, Object>> getCriticalMetrics() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Metric> metrics = monitoringService.getCriticalMetrics();
            
            response.put("success", true);
            response.put("metrics", metrics);
            response.put("count", metrics.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les métriques d'avertissement
     */
    @GetMapping("/metrics/warning")
    public ResponseEntity<Map<String, Object>> getWarningMetrics() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Metric> metrics = monitoringService.getWarningMetrics();
            
            response.put("success", true);
            response.put("metrics", metrics);
            response.put("count", metrics.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Recherche avancée de métriques
     */
    @GetMapping("/metrics/search")
    public ResponseEntity<Map<String, Object>> searchMetrics(
            @RequestParam(required = false) Long entrepriseId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String metricName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Metric.MetricCategory cat = category != null ? Metric.MetricCategory.valueOf(category.toUpperCase()) : null;
            Metric.MetricType type = metricType != null ? Metric.MetricType.valueOf(metricType.toUpperCase()) : null;
            Metric.MetricStatus stat = status != null ? Metric.MetricStatus.valueOf(status.toUpperCase()) : null;
            
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
            
            List<Metric> metrics = monitoringService.searchMetrics(entrepriseId, cat, type, stat, source, metricName, start, end);
            
            response.put("success", true);
            response.put("metrics", metrics);
            response.put("count", metrics.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== STATISTIQUES ====================

    /**
     * Obtenir les statistiques de métriques
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getMetricStatistics(@RequestParam Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = monitoringService.getMetricStatistics(entrepriseId);
            
            response.put("success", true);
            response.put("statistics", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les métriques de performance
     */
    @GetMapping("/performance-metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics(@RequestParam Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> metrics = monitoringService.getPerformanceMetrics(entrepriseId);
            
            response.put("success", true);
            response.put("metrics", metrics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== RAPPORTS ====================

    /**
     * Générer un rapport de performance
     */
    @GetMapping("/reports/performance")
    public ResponseEntity<Map<String, Object>> generatePerformanceReport(
            @RequestParam Long entrepriseId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            Map<String, Object> report = monitoringService.generatePerformanceReport(entrepriseId, start, end);
            
            response.put("success", true);
            response.put("report", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== COLLECTE MANUELLE ====================

    /**
     * Déclencher la collecte manuelle des métriques système
     */
    @PostMapping("/collect-system-metrics")
    public ResponseEntity<Map<String, Object>> collectSystemMetrics() {
        Map<String, Object> response = new HashMap<>();
        try {
            monitoringService.collectSystemMetrics();
            
            response.put("success", true);
            response.put("message", "Collecte des métriques système déclenchée");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Vérifier les alertes manuellement
     */
    @PostMapping("/check-alerts")
    public ResponseEntity<Map<String, Object>> checkAlerts() {
        Map<String, Object> response = new HashMap<>();
        try {
            monitoringService.checkAlerts();
            
            response.put("success", true);
            response.put("message", "Vérification des alertes effectuée");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== UTILITAIRES ====================

    /**
     * Obtenir les types de métriques disponibles
     */
    @GetMapping("/metric-types")
    public ResponseEntity<Map<String, Object>> getMetricTypes() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> types = new HashMap<>();
            for (Metric.MetricType type : Metric.MetricType.values()) {
                types.put(type.name(), type.getDescription());
            }
            
            response.put("success", true);
            response.put("types", types);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les catégories de métriques disponibles
     */
    @GetMapping("/metric-categories")
    public ResponseEntity<Map<String, Object>> getMetricCategories() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> categories = new HashMap<>();
            for (Metric.MetricCategory category : Metric.MetricCategory.values()) {
                categories.put(category.name(), category.getDescription());
            }
            
            response.put("success", true);
            response.put("categories", categories);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statuts de métriques disponibles
     */
    @GetMapping("/metric-statuses")
    public ResponseEntity<Map<String, Object>> getMetricStatuses() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> statuses = new HashMap<>();
            for (Metric.MetricStatus status : Metric.MetricStatus.values()) {
                statuses.put(status.name(), status.getDescription());
            }
            
            response.put("success", true);
            response.put("statuses", statuses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== TEST ====================

    /**
     * Endpoint de test pour créer des données de test
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> createTestData() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> testData = monitoringService.getTestData();
            
            response.put("success", true);
            response.put("message", "Données de test créées avec succès");
            response.put("data", testData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint de test simple
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module de monitoring opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("features", new String[]{
            "Collecte de métriques système",
            "Monitoring des performances",
            "Alertes et notifications",
            "Tableaux de bord",
            "Historique des métriques",
            "Analyse des tendances",
            "Rapports de performance"
        });
        return ResponseEntity.ok(response);
    }

        /**
     * Endpoint de diagnostic pour tester la base de données
     */
    @GetMapping("/diagnostic")
    public ResponseEntity<Map<String, Object>> diagnosticEndpoint() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Test simple de création d'une métrique
            Metric testMetric = new Metric("diagnostic.test", Metric.MetricType.GAUGE, 100.0,
                                         Metric.MetricCategory.SYSTEM, "diagnostic");
            testMetric.setEntrepriseId(1L);
            testMetric.setUtilisateurId(1L);

            Metric savedMetric = metricRepository.save(testMetric);

            response.put("success", true);
            response.put("message", "Test de base de données réussi");
            response.put("metricCreated", true);
            response.put("metricId", savedMetric.getId());
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur de base de données: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test simple pour créer une métrique
     */
    @PostMapping("/test-create-metric")
    public ResponseEntity<Map<String, Object>> testCreateMetric() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Créer une métrique de test simple
            Metric testMetric = new Metric("test.cpu.usage", Metric.MetricType.GAUGE, 75.5,
                                         Metric.MetricCategory.CPU, "test");
            testMetric.setEntrepriseId(1L);
            testMetric.setUtilisateurId(1L);
            testMetric.setUnit("%");
            testMetric.setDescription("Test de création de métrique via endpoint simplifié");

            Metric savedMetric = metricRepository.save(testMetric);

            response.put("success", true);
            response.put("message", "Métrique de test créée avec succès");
            response.put("metric", savedMetric);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test pour récupérer les métriques d'une entreprise
     */
    @GetMapping("/test-get-metrics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testGetMetrics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Metric> metrics = monitoringService.getMetricsByEntreprise(entrepriseId);
            
            response.put("success", true);
            response.put("message", "Métriques récupérées avec succès");
            response.put("metrics", metrics);
            response.put("count", metrics.size());
            response.put("entrepriseId", entrepriseId);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test pour les statistiques
     */
    @GetMapping("/test-stats/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testStats(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = monitoringService.getMetricStatistics(entrepriseId);
            
            response.put("success", true);
            response.put("message", "Statistiques récupérées avec succès");
            response.put("stats", stats);
            response.put("entrepriseId", entrepriseId);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des stats: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour créer la table metrics
     */
    @PostMapping("/create-table")
    public ResponseEntity<Map<String, Object>> createTable() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = monitoringService.createTableIfNotExists();
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("tableCreated", result.get("tableCreated"));
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création de la table: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour créer la table metrics sans transaction
     */
    @PostMapping("/create-table-new")
    public ResponseEntity<Map<String, Object>> createTableNew() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = monitoringService.createTableWithoutTransaction();
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("tableCreated", result.get("tableCreated"));
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création de la table: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour vérifier si la table metrics existe
     */
    @GetMapping("/check-table")
    public ResponseEntity<Map<String, Object>> checkTable() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = monitoringService.checkTableExists();
            
            response.put("success", result.get("success"));
            response.put("tableExists", result.get("tableExists"));
            response.put("message", result.get("message"));
            response.put("count", result.get("count"));
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("tableExists", false);
            response.put("message", "Erreur lors de la vérification de la table: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour créer la table metrics via SQL direct
     */
    @PostMapping("/create-table-sql")
    public ResponseEntity<Map<String, Object>> createTableWithSQL() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = monitoringService.createTableWithSQL();
            
            response.put("success", result.get("success"));
            response.put("message", result.get("message"));
            response.put("tableCreated", result.get("tableCreated"));
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création de la table via SQL: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }
}
