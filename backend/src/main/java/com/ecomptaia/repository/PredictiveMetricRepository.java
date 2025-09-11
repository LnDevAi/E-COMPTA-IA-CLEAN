ackage com.ecomptaia.repository;

import com.ecomptaia.security.entity.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecomptaia.entity.PredictiveMetric;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour les métriques prédictives
 * Révolutionnaire vs TOMPRO - Analytics prédictives avancées
 */
@Repository
public interface PredictiveMetricRepository extends JpaRepository<PredictiveMetric, Long> {
    
    /**
     * Trouver les métriques par entreprise
     */
    List<PredictiveMetric> findByCompanyId(Long companyId);
    
    /**
     * Trouver les métriques par catégorie
     */
    List<PredictiveMetric> findByCompanyIdAndCategory(Long companyId, PredictiveMetric.MetricCategory category);
    
    /**
     * Trouver les métriques par tendance
     */
    List<PredictiveMetric> findByCompanyIdAndTrend(Long companyId, PredictiveMetric.TrendDirection trend);
    
    /**
     * Trouver les métriques par confiance minimale
     */
    List<PredictiveMetric> findByCompanyIdAndConfidenceGreaterThanEqual(Long companyId, Integer minConfidence);
    
    /**
     * Trouver les métriques mises à jour récemment
     */
    List<PredictiveMetric> findByCompanyIdAndLastUpdatedAfter(Long companyId, LocalDateTime since);
    
    /**
     * Trouver les métriques nécessitant une mise à jour
     */
    @Query("SELECT m FROM PredictiveMetric m WHERE m.nextUpdate <= :now")
    List<PredictiveMetric> findMetricsNeedingUpdate(@Param("now") LocalDateTime now);
    
    /**
     * Trouver les métriques par version du modèle
     */
    List<PredictiveMetric> findByCompanyIdAndModelVersion(Long companyId, String modelVersion);
    
    /**
     * Compter les métriques par catégorie
     */
    @Query("SELECT m.category, COUNT(m) FROM PredictiveMetric m WHERE m.company.id = :companyId GROUP BY m.category")
    List<Object[]> countMetricsByCategory(@Param("companyId") Long companyId);
    
    /**
     * Trouver les métriques avec tendance positive
     */
    @Query("SELECT m FROM PredictiveMetric m WHERE m.company.id = :companyId AND m.trend = 'INCREASING' AND m.isPositive = true")
    List<PredictiveMetric> findPositiveTrendingMetrics(@Param("companyId") Long companyId);
    
    /**
     * Trouver les métriques avec tendance négative
     */
    @Query("SELECT m FROM PredictiveMetric m WHERE m.company.id = :companyId AND m.trend = 'DECREASING' AND m.isPositive = false")
    List<PredictiveMetric> findNegativeTrendingMetrics(@Param("companyId") Long companyId);
    
    /**
     * Calculer la confiance moyenne par catégorie
     */
    @Query("SELECT m.category, AVG(m.confidence) FROM PredictiveMetric m WHERE m.company.id = :companyId GROUP BY m.category")
    List<Object[]> getAverageConfidenceByCategory(@Param("companyId") Long companyId);
}




