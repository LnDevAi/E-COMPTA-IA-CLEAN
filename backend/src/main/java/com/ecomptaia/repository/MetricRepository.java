package com.ecomptaia.repository;

import com.ecomptaia.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {

    // ==================== RECHERCHE PAR CATÉGORIE ====================

    List<Metric> findByCategoryOrderByTimestampDesc(Metric.MetricCategory category);

    List<Metric> findByCategoryAndEntrepriseIdOrderByTimestampDesc(Metric.MetricCategory category, Long entrepriseId);

    // ==================== RECHERCHE PAR TYPE ====================

    List<Metric> findByMetricTypeOrderByTimestampDesc(Metric.MetricType metricType);

    List<Metric> findByMetricTypeAndEntrepriseIdOrderByTimestampDesc(Metric.MetricType metricType, Long entrepriseId);

    // ==================== RECHERCHE PAR STATUT ====================

    List<Metric> findByStatusOrderByTimestampDesc(Metric.MetricStatus status);

    List<Metric> findByStatusAndEntrepriseIdOrderByTimestampDesc(Metric.MetricStatus status, Long entrepriseId);

    // ==================== RECHERCHE PAR SOURCE ====================

    List<Metric> findBySourceOrderByTimestampDesc(String source);

    List<Metric> findBySourceAndEntrepriseIdOrderByTimestampDesc(String source, Long entrepriseId);

    // ==================== RECHERCHE PAR NOM ====================

    List<Metric> findByMetricNameOrderByTimestampDesc(String metricName);

    List<Metric> findByMetricNameAndEntrepriseIdOrderByTimestampDesc(String metricName, Long entrepriseId);

    // ==================== RECHERCHE PAR DATE ====================

    @Query("SELECT m FROM Metric m WHERE m.timestamp BETWEEN :startDate AND :endDate ORDER BY m.timestamp DESC")
    List<Metric> findByTimestampBetweenOrderByTimestampDesc(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT m FROM Metric m WHERE m.timestamp BETWEEN :startDate AND :endDate AND m.entrepriseId = :entrepriseId ORDER BY m.timestamp DESC")
    List<Metric> findByTimestampBetweenAndEntrepriseIdOrderByTimestampDesc(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("entrepriseId") Long entrepriseId);

    // ==================== RECHERCHE PAR VALEUR ====================

    @Query("SELECT m FROM Metric m WHERE m.value > :minValue ORDER BY m.value DESC")
    List<Metric> findByValueGreaterThanOrderByValueDesc(@Param("minValue") Double minValue);

    @Query("SELECT m FROM Metric m WHERE m.value BETWEEN :minValue AND :maxValue ORDER BY m.value DESC")
    List<Metric> findByValueBetweenOrderByValueDesc(
            @Param("minValue") Double minValue,
            @Param("maxValue") Double maxValue);

    // ==================== RECHERCHE AVANCÉE ====================

    @Query("SELECT m FROM Metric m WHERE " +
           "(:entrepriseId IS NULL OR m.entrepriseId = :entrepriseId) AND " +
           "(:category IS NULL OR m.category = :category) AND " +
           "(:metricType IS NULL OR m.metricType = :metricType) AND " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:source IS NULL OR m.source = :source) AND " +
           "(:metricName IS NULL OR m.metricName = :metricName) AND " +
           "(:startDate IS NULL OR m.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR m.timestamp <= :endDate) " +
           "ORDER BY m.timestamp DESC")
    List<Metric> findMetricsWithCriteria(
            @Param("entrepriseId") Long entrepriseId,
            @Param("category") Metric.MetricCategory category,
            @Param("metricType") Metric.MetricType metricType,
            @Param("status") Metric.MetricStatus status,
            @Param("source") String source,
            @Param("metricName") String metricName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ==================== COMPTAGE ====================

    @Query("SELECT COUNT(m) FROM Metric m WHERE m.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(m) FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.category = :category")
    Long countByEntrepriseIdAndCategory(@Param("entrepriseId") Long entrepriseId, @Param("category") Metric.MetricCategory category);

    @Query("SELECT COUNT(m) FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.status = :status")
    Long countByEntrepriseIdAndStatus(@Param("entrepriseId") Long entrepriseId, @Param("status") Metric.MetricStatus status);

    // ==================== STATISTIQUES ====================

    @Query("SELECT AVG(m.value) FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.metricName = :metricName")
    Double getAverageValueByEntrepriseAndMetric(@Param("entrepriseId") Long entrepriseId, @Param("metricName") String metricName);

    @Query("SELECT MAX(m.value) FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.metricName = :metricName")
    Double getMaxValueByEntrepriseAndMetric(@Param("entrepriseId") Long entrepriseId, @Param("metricName") String metricName);

    @Query("SELECT MIN(m.value) FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.metricName = :metricName")
    Double getMinValueByEntrepriseAndMetric(@Param("entrepriseId") Long entrepriseId, @Param("metricName") String metricName);

    // ==================== DERNIÈRES MÉTRIQUES ====================

    @Query("SELECT m FROM Metric m WHERE m.entrepriseId = :entrepriseId ORDER BY m.timestamp DESC")
    List<Metric> findLatestMetricsByEntreprise(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT m FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.metricName = :metricName ORDER BY m.timestamp DESC")
    List<Metric> findLatestMetricsByEntrepriseAndName(@Param("entrepriseId") Long entrepriseId, @Param("metricName") String metricName);

    // ==================== MÉTRIQUES CRITIQUES ====================

    @Query("SELECT m FROM Metric m WHERE m.status = 'CRITICAL' ORDER BY m.timestamp DESC")
    List<Metric> findCriticalMetrics();

    @Query("SELECT m FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.status = 'CRITICAL' ORDER BY m.timestamp DESC")
    List<Metric> findCriticalMetricsByEntreprise(@Param("entrepriseId") Long entrepriseId);

    // ==================== MÉTRIQUES D'AVERTISSEMENT ====================

    @Query("SELECT m FROM Metric m WHERE m.status = 'WARNING' ORDER BY m.timestamp DESC")
    List<Metric> findWarningMetrics();

    @Query("SELECT m FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.status = 'WARNING' ORDER BY m.timestamp DESC")
    List<Metric> findWarningMetricsByEntreprise(@Param("entrepriseId") Long entrepriseId);

    // ==================== MÉTRIQUES PAR PÉRIODE ====================

    @Query("SELECT m FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.timestamp >= :since ORDER BY m.timestamp DESC")
    List<Metric> findMetricsSinceByEntreprise(@Param("entrepriseId") Long entrepriseId, @Param("since") LocalDateTime since);

    @Query("SELECT m FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.metricName = :metricName AND m.timestamp >= :since ORDER BY m.timestamp DESC")
    List<Metric> findMetricsSinceByEntrepriseAndName(@Param("entrepriseId") Long entrepriseId, @Param("metricName") String metricName, @Param("since") LocalDateTime since);

    // ==================== MÉTRIQUES PAR HEURE ====================

    @Query("SELECT m FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.timestamp >= :startOfHour AND m.timestamp < :endOfHour ORDER BY m.timestamp DESC")
    List<Metric> findMetricsByHour(@Param("entrepriseId") Long entrepriseId, @Param("startOfHour") LocalDateTime startOfHour, @Param("endOfHour") LocalDateTime endOfHour);

    // ==================== MÉTRIQUES PAR JOUR ====================

    @Query("SELECT m FROM Metric m WHERE m.entrepriseId = :entrepriseId AND m.timestamp >= :startOfDay AND m.timestamp < :endOfDay ORDER BY m.timestamp DESC")
    List<Metric> findMetricsByDay(@Param("entrepriseId") Long entrepriseId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}







