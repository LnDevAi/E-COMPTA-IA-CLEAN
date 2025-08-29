package com.ecomptaia.service;

import com.ecomptaia.entity.Metric;
import com.ecomptaia.repository.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
@Transactional
public class MonitoringService {

    @Autowired
    private MetricRepository metricRepository;

    // ==================== COLLECTE DE MÉTRIQUES ====================

    /**
     * Collecter les métriques système
     */
    @Scheduled(fixedRate = 60000) // Toutes les minutes
    public void collectSystemMetrics() {
        try {
            // Métriques mémoire
            collectMemoryMetrics();
            
            // Métriques CPU
            collectCpuMetrics();
            
            // Métriques threads
            collectThreadMetrics();
            
            // Métriques application
            collectApplicationMetrics();
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la collecte des métriques système: " + e.getMessage());
        }
    }

    /**
     * Collecter les métriques mémoire
     */
    private void collectMemoryMetrics() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // Mémoire utilisée
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        Metric usedMemoryMetric = new Metric("memory.used", Metric.MetricType.GAUGE, 
                                           (double) usedMemory / (1024 * 1024), 
                                           Metric.MetricCategory.MEMORY, "system");
        usedMemoryMetric.setUnit("MB");
        usedMemoryMetric.setDescription("Mémoire heap utilisée");
        usedMemoryMetric.setThresholdWarning(512.0); // 512MB
        usedMemoryMetric.setThresholdCritical(1024.0); // 1GB
        evaluateMetricStatus(usedMemoryMetric);
        metricRepository.save(usedMemoryMetric);
        
        // Mémoire maximale
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        Metric maxMemoryMetric = new Metric("memory.max", Metric.MetricType.GAUGE, 
                                          (double) maxMemory / (1024 * 1024), 
                                          Metric.MetricCategory.MEMORY, "system");
        maxMemoryMetric.setUnit("MB");
        maxMemoryMetric.setDescription("Mémoire heap maximale");
        metricRepository.save(maxMemoryMetric);
        
        // Pourcentage d'utilisation
        double usagePercent = (double) usedMemory / maxMemory * 100;
        Metric usagePercentMetric = new Metric("memory.usage.percent", Metric.MetricType.GAUGE, 
                                             usagePercent, Metric.MetricCategory.MEMORY, "system");
        usagePercentMetric.setUnit("%");
        usagePercentMetric.setDescription("Pourcentage d'utilisation de la mémoire");
        usagePercentMetric.setThresholdWarning(70.0); // 70%
        usagePercentMetric.setThresholdCritical(90.0); // 90%
        evaluateMetricStatus(usagePercentMetric);
        metricRepository.save(usagePercentMetric);
    }

    /**
     * Collecter les métriques CPU
     */
    private void collectCpuMetrics() {
        // Simulation des métriques CPU (en production, utiliser OperatingSystemMXBean)
        double cpuUsage = Math.random() * 100; // Simulation
        Metric cpuMetric = new Metric("cpu.usage", Metric.MetricType.GAUGE, 
                                    cpuUsage, Metric.MetricCategory.CPU, "system");
        cpuMetric.setUnit("%");
        cpuMetric.setDescription("Utilisation du CPU");
        cpuMetric.setThresholdWarning(80.0); // 80%
        cpuMetric.setThresholdCritical(95.0); // 95%
        evaluateMetricStatus(cpuMetric);
        metricRepository.save(cpuMetric);
        
        // Nombre de processeurs
        int processors = Runtime.getRuntime().availableProcessors();
        Metric processorsMetric = new Metric("cpu.processors", Metric.MetricType.GAUGE, 
                                           (double) processors, Metric.MetricCategory.CPU, "system");
        processorsMetric.setDescription("Nombre de processeurs disponibles");
        metricRepository.save(processorsMetric);
    }

    /**
     * Collecter les métriques threads
     */
    private void collectThreadMetrics() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        // Nombre de threads actifs
        int threadCount = threadBean.getThreadCount();
        Metric threadCountMetric = new Metric("threads.active", Metric.MetricType.GAUGE, 
                                            (double) threadCount, Metric.MetricCategory.SYSTEM, "system");
        threadCountMetric.setDescription("Nombre de threads actifs");
        threadCountMetric.setThresholdWarning(100.0); // 100 threads
        threadCountMetric.setThresholdCritical(200.0); // 200 threads
        evaluateMetricStatus(threadCountMetric);
        metricRepository.save(threadCountMetric);
        
        // Nombre de threads démarrés
        long totalStarted = threadBean.getTotalStartedThreadCount();
        Metric totalStartedMetric = new Metric("threads.total.started", Metric.MetricType.COUNTER, 
                                             (double) totalStarted, Metric.MetricCategory.SYSTEM, "system");
        totalStartedMetric.setDescription("Nombre total de threads démarrés");
        metricRepository.save(totalStartedMetric);
    }

    /**
     * Collecter les métriques application
     */
    private void collectApplicationMetrics() {
        // Temps de fonctionnement
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        Metric uptimeMetric = new Metric("application.uptime", Metric.MetricType.GAUGE, 
                                       (double) uptime / (1000 * 60), Metric.MetricCategory.APPLICATION, "system");
        uptimeMetric.setUnit("minutes");
        uptimeMetric.setDescription("Temps de fonctionnement de l'application");
        metricRepository.save(uptimeMetric);
        
        // Nombre de classes chargées
        int loadedClasses = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
        Metric loadedClassesMetric = new Metric("application.classes.loaded", Metric.MetricType.GAUGE, 
                                              (double) loadedClasses, Metric.MetricCategory.APPLICATION, "system");
        loadedClassesMetric.setDescription("Nombre de classes chargées");
        metricRepository.save(loadedClassesMetric);
    }

    /**
     * Évaluer le statut d'une métrique basé sur les seuils
     */
    private void evaluateMetricStatus(Metric metric) {
        if (metric.getThresholdCritical() != null && metric.getValue() >= metric.getThresholdCritical()) {
            metric.setStatus(Metric.MetricStatus.CRITICAL);
        } else if (metric.getThresholdWarning() != null && metric.getValue() >= metric.getThresholdWarning()) {
            metric.setStatus(Metric.MetricStatus.WARNING);
        } else {
            metric.setStatus(Metric.MetricStatus.NORMAL);
        }
    }

    // ==================== CRÉATION DE MÉTRIQUES ====================

    /**
     * Créer une nouvelle métrique
     */
    public Metric createMetric(String metricName, Metric.MetricType metricType, Double value, 
                              Metric.MetricCategory category, String source, Long entrepriseId, Long utilisateurId) {
        
        Metric metric = new Metric(metricName, metricType, value, category, source);
        metric.setEntrepriseId(entrepriseId);
        metric.setUtilisateurId(utilisateurId);
        
        // Évaluer le statut si des seuils sont définis
        if (metric.getThresholdWarning() != null || metric.getThresholdCritical() != null) {
            evaluateMetricStatus(metric);
        }
        
        return metricRepository.save(metric);
    }

    /**
     * Créer une métrique avec seuils
     */
    public Metric createMetricWithThresholds(String metricName, Metric.MetricType metricType, Double value, 
                                            Metric.MetricCategory category, String source, 
                                            Double thresholdWarning, Double thresholdCritical,
                                            Long entrepriseId, Long utilisateurId) {
        
        Metric metric = new Metric(metricName, metricType, value, category, source);
        metric.setEntrepriseId(entrepriseId);
        metric.setUtilisateurId(utilisateurId);
        metric.setThresholdWarning(thresholdWarning);
        metric.setThresholdCritical(thresholdCritical);
        
        evaluateMetricStatus(metric);
        
        return metricRepository.save(metric);
    }

    // ==================== RECHERCHE ET RÉCUPÉRATION ====================

    /**
     * Obtenir les métriques par entreprise
     */
    public List<Metric> getMetricsByEntreprise(Long entrepriseId) {
        return metricRepository.findLatestMetricsByEntreprise(entrepriseId);
    }

    /**
     * Obtenir les métriques par catégorie
     */
    public List<Metric> getMetricsByCategory(Metric.MetricCategory category) {
        return metricRepository.findByCategoryOrderByTimestampDesc(category);
    }

    /**
     * Obtenir les métriques par statut
     */
    public List<Metric> getMetricsByStatus(Metric.MetricStatus status) {
        return metricRepository.findByStatusOrderByTimestampDesc(status);
    }

    /**
     * Obtenir les métriques critiques
     */
    public List<Metric> getCriticalMetrics() {
        return metricRepository.findCriticalMetrics();
    }

    /**
     * Obtenir les métriques d'avertissement
     */
    public List<Metric> getWarningMetrics() {
        return metricRepository.findWarningMetrics();
    }

    /**
     * Recherche avancée de métriques
     */
    public List<Metric> searchMetrics(Long entrepriseId, Metric.MetricCategory category, 
                                     Metric.MetricType metricType, Metric.MetricStatus status,
                                     String source, String metricName, LocalDateTime startDate, LocalDateTime endDate) {
        return metricRepository.findMetricsWithCriteria(entrepriseId, category, metricType, 
                                                      status, source, metricName, startDate, endDate);
    }

    // ==================== STATISTIQUES ====================

    /**
     * Obtenir les statistiques de métriques
     */
    public Map<String, Object> getMetricStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Comptages
        stats.put("totalMetrics", metricRepository.countByEntrepriseId(entrepriseId));
        stats.put("normalMetrics", metricRepository.countByEntrepriseIdAndStatus(entrepriseId, Metric.MetricStatus.NORMAL));
        stats.put("warningMetrics", metricRepository.countByEntrepriseIdAndStatus(entrepriseId, Metric.MetricStatus.WARNING));
        stats.put("criticalMetrics", metricRepository.countByEntrepriseIdAndStatus(entrepriseId, Metric.MetricStatus.CRITICAL));
        
        // Comptages par catégorie
        for (Metric.MetricCategory category : Metric.MetricCategory.values()) {
            Long count = metricRepository.countByEntrepriseIdAndCategory(entrepriseId, category);
            stats.put(category.name().toLowerCase() + "Metrics", count);
        }
        
        // Dernières métriques
        stats.put("latestMetrics", metricRepository.findLatestMetricsByEntreprise(entrepriseId));
        
        return stats;
    }

    /**
     * Obtenir les métriques de performance
     */
    public Map<String, Object> getPerformanceMetrics(Long entrepriseId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Métriques mémoire
        Double avgMemoryUsage = metricRepository.getAverageValueByEntrepriseAndMetric(entrepriseId, "memory.usage.percent");
        Double maxMemoryUsage = metricRepository.getMaxValueByEntrepriseAndMetric(entrepriseId, "memory.usage.percent");
        metrics.put("averageMemoryUsage", avgMemoryUsage);
        metrics.put("maxMemoryUsage", maxMemoryUsage);
        
        // Métriques CPU
        Double avgCpuUsage = metricRepository.getAverageValueByEntrepriseAndMetric(entrepriseId, "cpu.usage");
        Double maxCpuUsage = metricRepository.getMaxValueByEntrepriseAndMetric(entrepriseId, "cpu.usage");
        metrics.put("averageCpuUsage", avgCpuUsage);
        metrics.put("maxCpuUsage", maxCpuUsage);
        
        // Métriques threads
        Double avgThreadCount = metricRepository.getAverageValueByEntrepriseAndMetric(entrepriseId, "threads.active");
        Double maxThreadCount = metricRepository.getMaxValueByEntrepriseAndMetric(entrepriseId, "threads.active");
        metrics.put("averageThreadCount", avgThreadCount);
        metrics.put("maxThreadCount", maxThreadCount);
        
        return metrics;
    }

    // ==================== ALERTES ====================

    /**
     * Vérifier les alertes
     */
    @Scheduled(fixedRate = 30000) // Toutes les 30 secondes
    public void checkAlerts() {
        try {
            // Vérifier les métriques critiques
            List<Metric> criticalMetrics = getCriticalMetrics();
            if (!criticalMetrics.isEmpty()) {
                System.out.println("🚨 ALERTE CRITIQUE: " + criticalMetrics.size() + " métriques critiques détectées");
                for (Metric metric : criticalMetrics) {
                    System.out.println("  - " + metric.getMetricName() + ": " + metric.getValue() + " " + metric.getUnit());
                }
            }
            
            // Vérifier les métriques d'avertissement
            List<Metric> warningMetrics = getWarningMetrics();
            if (!warningMetrics.isEmpty()) {
                System.out.println("⚠️ AVERTISSEMENT: " + warningMetrics.size() + " métriques en avertissement");
                for (Metric metric : warningMetrics) {
                    System.out.println("  - " + metric.getMetricName() + ": " + metric.getValue() + " " + metric.getUnit());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification des alertes: " + e.getMessage());
        }
    }

    // ==================== RAPPORTS ====================

    /**
     * Générer un rapport de performance
     */
    public Map<String, Object> generatePerformanceReport(Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> report = new HashMap<>();
        
        report.put("period", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        report.put("entrepriseId", entrepriseId);
        
        // Statistiques générales
        List<Metric> periodMetrics = metricRepository.findByTimestampBetweenAndEntrepriseIdOrderByTimestampDesc(startDate, endDate, entrepriseId);
        report.put("totalMetrics", periodMetrics.size());
        
        // Métriques par catégorie
        Map<String, Long> categoryStats = new HashMap<>();
        for (Metric.MetricCategory category : Metric.MetricCategory.values()) {
            long count = periodMetrics.stream()
                    .filter(m -> m.getCategory() == category)
                    .count();
            categoryStats.put(category.name(), count);
        }
        report.put("categoryStats", categoryStats);
        
        // Métriques par statut
        Map<String, Long> statusStats = new HashMap<>();
        for (Metric.MetricStatus status : Metric.MetricStatus.values()) {
            long count = periodMetrics.stream()
                    .filter(m -> m.getStatus() == status)
                    .count();
            statusStats.put(status.name(), count);
        }
        report.put("statusStats", statusStats);
        
        // Tendances
        report.put("performanceMetrics", getPerformanceMetrics(entrepriseId));
        
        return report;
    }

    // ==================== UTILITAIRES ====================

    /**
     * Nettoyer les anciennes métriques
     */
    @Scheduled(cron = "0 0 2 * * ?") // Tous les jours à 2h du matin
    public void cleanupOldMetrics() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30); // Garder 30 jours
            List<Metric> oldMetrics = metricRepository.findByTimestampBetweenOrderByTimestampDesc(
                    LocalDateTime.MIN, cutoffDate);
            
            for (Metric metric : oldMetrics) {
                metricRepository.delete(metric);
            }
            
            System.out.println("🧹 Nettoyage terminé: " + oldMetrics.size() + " anciennes métriques supprimées");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du nettoyage des anciennes métriques: " + e.getMessage());
        }
    }

    /**
     * Obtenir les données de test
     */
    public Map<String, Object> getTestData() {
        Map<String, Object> testData = new HashMap<>();
        
        // Créer quelques métriques de test
        Metric metric1 = createMetric("test.cpu.usage", Metric.MetricType.GAUGE, 75.5, 
                                     Metric.MetricCategory.CPU, "test", 1L, 1L);
        metric1.setUnit("%");
        metric1.setThresholdWarning(70.0);
        metric1.setThresholdCritical(90.0);
        evaluateMetricStatus(metric1);
        metricRepository.save(metric1);
        
        Metric metric2 = createMetric("test.memory.usage", Metric.MetricType.GAUGE, 85.2, 
                                     Metric.MetricCategory.MEMORY, "test", 1L, 1L);
        metric2.setUnit("%");
        metric2.setThresholdWarning(80.0);
        metric2.setThresholdCritical(95.0);
        evaluateMetricStatus(metric2);
        metricRepository.save(metric2);
        
        testData.put("message", "Données de test créées avec succès");
        testData.put("metricsCreated", 2);
        testData.put("metric1", metric1);
        testData.put("metric2", metric2);
        
        return testData;
    }

    /**
     * Créer la table metrics si elle n'existe pas
     */
    public Map<String, Object> createTableIfNotExists() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Essayer de créer une métrique de test pour forcer la création de la table
            Metric testMetric = new Metric("table.test", Metric.MetricType.GAUGE, 0.0, 
                                         Metric.MetricCategory.SYSTEM, "system");
            testMetric.setEntrepriseId(1L);
            testMetric.setUtilisateurId(1L);
            
            Metric savedMetric = metricRepository.save(testMetric);
            
            // Supprimer la métrique de test
            metricRepository.delete(savedMetric);
            
            result.put("success", true);
            result.put("message", "Table metrics créée avec succès");
            result.put("tableCreated", true);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur lors de la création de la table: " + e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
        }
        return result;
    }

    /**
     * Créer la table metrics sans transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Object> createTableWithoutTransaction() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Essayer de créer une métrique de test pour forcer la création de la table
            Metric testMetric = new Metric("table.test", Metric.MetricType.GAUGE, 0.0, 
                                         Metric.MetricCategory.SYSTEM, "system");
            testMetric.setEntrepriseId(1L);
            testMetric.setUtilisateurId(1L);
            
            Metric savedMetric = metricRepository.save(testMetric);
            
            // Supprimer la métrique de test
            metricRepository.delete(savedMetric);
            
            result.put("success", true);
            result.put("message", "Table metrics créée avec succès");
            result.put("tableCreated", true);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur lors de la création de la table: " + e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
        }
        return result;
    }

    /**
     * Vérifier si la table metrics existe
     */
    public Map<String, Object> checkTableExists() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Essayer de compter les métriques pour vérifier si la table existe
            long count = metricRepository.count();
            result.put("success", true);
            result.put("tableExists", true);
            result.put("message", "Table metrics existe et contient " + count + " enregistrements");
            result.put("count", count);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("tableExists", false);
            result.put("message", "Table metrics n'existe pas: " + e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
        }
        return result;
    }

    /**
     * Créer la table metrics via SQL direct
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> createTableWithSQL() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Script SQL pour créer la table
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS metrics (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    metric_name VARCHAR(255) NOT NULL,
                    metric_type VARCHAR(50) NOT NULL,
                    metric_value DOUBLE NOT NULL,
                    unit VARCHAR(50),
                    category VARCHAR(50) NOT NULL,
                    source VARCHAR(255) NOT NULL,
                    timestamp DATETIME NOT NULL,
                    description TEXT,
                    tags TEXT,
                    threshold_warning DOUBLE,
                    threshold_critical DOUBLE,
                    status VARCHAR(50) NOT NULL,
                    entreprise_id BIGINT,
                    utilisateur_id BIGINT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;
            
            // Exécuter le script SQL
            jdbcTemplate.execute(createTableSQL);
            
            // Créer les index
            String[] indexSQLs = {
                "CREATE INDEX IF NOT EXISTS idx_metrics_entreprise_id ON metrics(entreprise_id)",
                "CREATE INDEX IF NOT EXISTS idx_metrics_category ON metrics(category)",
                "CREATE INDEX IF NOT EXISTS idx_metrics_status ON metrics(status)",
                "CREATE INDEX IF NOT EXISTS idx_metrics_timestamp ON metrics(timestamp)",
                "CREATE INDEX IF NOT EXISTS idx_metrics_metric_name ON metrics(metric_name)",
                "CREATE INDEX IF NOT EXISTS idx_metrics_source ON metrics(source)"
            };
            
            for (String indexSQL : indexSQLs) {
                try {
                    jdbcTemplate.execute(indexSQL);
                } catch (Exception e) {
                    // Ignorer les erreurs d'index déjà existants
                }
            }
            
            // Insérer une métrique de test
            String insertSQL = """
                INSERT INTO metrics (metric_name, metric_type, metric_value, category, source, timestamp, status, entreprise_id, utilisateur_id, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            
            jdbcTemplate.update(insertSQL, 
                "test.table.created", "GAUGE", 1.0, "SYSTEM", "database", 
                LocalDateTime.now(), "NORMAL", 1L, 1L, 
                "Métrique de test pour vérifier la création de la table");
            
            result.put("success", true);
            result.put("message", "Table metrics créée avec succès via SQL direct");
            result.put("tableCreated", true);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur lors de la création de la table via SQL: " + e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
        }
        return result;
    }
}
