ackage com.ecomptaia.repository;

import com.ecomptaia.security.entity.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecomptaia.entity.AIInsight;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour les insights IA
 * Révolutionnaire vs TOMPRO - IA native intégrée
 */
@Repository
public interface AIInsightRepository extends JpaRepository<AIInsight, Long> {
    
    /**
     * Trouver les insights d'une entreprise non résolus
     */
    Page<AIInsight> findByCompanyIdAndIsResolvedFalse(Long companyId, Pageable pageable);
    
    /**
     * Trouver les insights d'une entreprise non résolus (sans pagination)
     */
    List<AIInsight> findByCompanyIdAndIsResolvedFalse(Long companyId);
    
    /**
     * Trouver les insights par catégorie
     */
    List<AIInsight> findByCompanyIdAndCategoryAndIsResolvedFalse(Long companyId, AIInsight.InsightCategory category);
    
    /**
     * Trouver les insights par type
     */
    List<AIInsight> findByCompanyIdAndTypeAndIsResolvedFalse(Long companyId, AIInsight.InsightType type);
    
    /**
     * Trouver les insights prioritaires
     */
    List<AIInsight> findByCompanyIdAndPriorityGreaterThanEqualAndIsResolvedFalse(Long companyId, Integer priority);
    
    /**
     * Trouver les insights par entreprise
     */
    List<AIInsight> findByCompanyId(Long companyId);
    
    /**
     * Trouver les insights expirés
     */
    @Query("SELECT i FROM AIInsight i WHERE i.expiresAt < :now AND i.isResolved = false")
    List<AIInsight> findExpiredInsights(@Param("now") LocalDateTime now);
    
    /**
     * Trouver les insights par impact
     */
    List<AIInsight> findByCompanyIdAndImpactAndIsResolvedFalse(Long companyId, AIInsight.ImpactLevel impact);
    
    /**
     * Trouver les insights par source
     */
    List<AIInsight> findByCompanyIdAndSourceAndIsResolvedFalse(Long companyId, String source);
    
    /**
     * Compter les insights par catégorie
     */
    @Query("SELECT i.category, COUNT(i) FROM AIInsight i WHERE i.company.id = :companyId AND i.isResolved = false GROUP BY i.category")
    List<Object[]> countInsightsByCategory(@Param("companyId") Long companyId);
    
    /**
     * Trouver les insights créés dans une période
     */
    List<AIInsight> findByCompanyIdAndCreatedAtBetween(Long companyId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouver les insights par confiance minimale
     */
    List<AIInsight> findByCompanyIdAndConfidenceGreaterThanEqualAndIsResolvedFalse(Long companyId, Integer minConfidence);
}


