package com.ecomptaia.repository;

import com.ecomptaia.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskRepository extends JpaRepository<Risk, Long> {

    // ==================== REQUÊTES PAR ENTREPRISE ====================

    List<Risk> findByEntrepriseId(Long entrepriseId);

    List<Risk> findByEntrepriseIdAndIsActiveTrue(Long entrepriseId);

    List<Risk> findByEntrepriseIdAndRiskCategory(Long entrepriseId, Risk.RiskCategory riskCategory);

    List<Risk> findByEntrepriseIdAndRiskType(Long entrepriseId, Risk.RiskType riskType);

    List<Risk> findByEntrepriseIdAndRiskLevel(Long entrepriseId, Risk.RiskLevel riskLevel);

    List<Risk> findByEntrepriseIdAndMitigationStatus(Long entrepriseId, Risk.MitigationStatus mitigationStatus);

    // ==================== REQUÊTES PAR NIVEAU DE RISQUE ====================

    List<Risk> findByRiskLevel(Risk.RiskLevel riskLevel);

    List<Risk> findByRiskLevelIn(List<Risk.RiskLevel> riskLevels);

    List<Risk> findByRiskScoreGreaterThan(Integer minScore);

    List<Risk> findByRiskScoreBetween(Integer minScore, Integer maxScore);

    // ==================== REQUÊTES PAR CATÉGORIE ET TYPE ====================

    List<Risk> findByRiskCategory(Risk.RiskCategory riskCategory);

    List<Risk> findByRiskType(Risk.RiskType riskType);

    List<Risk> findByRiskCategoryAndRiskType(Risk.RiskCategory riskCategory, Risk.RiskType riskType);

    // ==================== REQUÊTES PAR STATUT ====================

    List<Risk> findByIsActiveTrue();

    List<Risk> findByRequiresImmediateActionTrue();

    List<Risk> findByMitigationStatus(Risk.MitigationStatus mitigationStatus);

    List<Risk> findByMitigationStatusIn(List<Risk.MitigationStatus> statuses);

    // ==================== REQUÊTES PAR DATE ====================

    List<Risk> findByCreatedAtAfter(LocalDateTime date);

    List<Risk> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Risk> findByLastAssessmentDateBefore(LocalDateTime date);

    List<Risk> findByNextAssessmentDateBefore(LocalDateTime date);

    List<Risk> findByDueDateBefore(LocalDateTime date);

    // ==================== REQUÊTES PAR TENDANCE ====================

    List<Risk> findByTrendDirection(Risk.TrendDirection trendDirection);

    List<Risk> findByTrendDirectionIn(List<Risk.TrendDirection> directions);

    // ==================== REQUÊTES COMPLEXES ====================

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.riskScore >= :minScore AND r.isActive = true")
    List<Risk> findHighRiskRisks(@Param("entrepriseId") Long entrepriseId, @Param("minScore") Integer minScore);

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.requiresImmediateAction = true AND r.isActive = true")
    List<Risk> findUrgentRisks(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.mitigationStatus = 'NOT_STARTED' AND r.riskLevel IN ('HIGH', 'VERY_HIGH', 'CRITICAL')")
    List<Risk> findCriticalUnmitigatedRisks(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.nextAssessmentDate <= :date AND r.isActive = true")
    List<Risk> findRisksNeedingAssessment(@Param("entrepriseId") Long entrepriseId, @Param("date") LocalDateTime date);

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.dueDate <= :date AND r.mitigationStatus != 'COMPLETED'")
    List<Risk> findOverdueRisks(@Param("entrepriseId") Long entrepriseId, @Param("date") LocalDateTime date);

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.historicalOccurrences > 0 ORDER BY r.historicalOccurrences DESC")
    List<Risk> findRisksWithHistoricalOccurrences(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE STATISTIQUES ====================

    @Query("SELECT COUNT(r) FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.isActive = true")
    Long countActiveRisksByEntreprise(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(r) FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.riskLevel = :riskLevel")
    Long countRisksByLevel(@Param("entrepriseId") Long entrepriseId, @Param("riskLevel") Risk.RiskLevel riskLevel);

    @Query("SELECT COUNT(r) FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.riskCategory = :category")
    Long countRisksByCategory(@Param("entrepriseId") Long entrepriseId, @Param("category") Risk.RiskCategory category);

    @Query("SELECT COUNT(r) FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.mitigationStatus = :status")
    Long countRisksByMitigationStatus(@Param("entrepriseId") Long entrepriseId, @Param("status") Risk.MitigationStatus status);

    @Query("SELECT AVG(r.riskScore) FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.isActive = true")
    Double getAverageRiskScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT MAX(r.riskScore) FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.isActive = true")
    Integer getMaxRiskScore(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE RECHERCHE ====================

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND (LOWER(r.riskName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.riskDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Risk> searchRisksByKeyword(@Param("entrepriseId") Long entrepriseId, @Param("keyword") String keyword);

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.responsiblePerson LIKE CONCAT('%', :person, '%')")
    List<Risk> findRisksByResponsiblePerson(@Param("entrepriseId") Long entrepriseId, @Param("person") String person);

    // ==================== REQUÊTES DE CONFORMITÉ ====================

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.complianceRequirements IS NOT NULL AND r.complianceRequirements != ''")
    List<Risk> findComplianceRisks(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE SURVEILLANCE ====================

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.alertThreshold IS NOT NULL AND r.riskScore >= r.alertThreshold")
    List<Risk> findRisksAboveThreshold(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT r FROM Risk r WHERE r.entrepriseId = :entrepriseId AND r.trendDirection = 'INCREASING' AND r.riskLevel IN ('HIGH', 'VERY_HIGH', 'CRITICAL')")
    List<Risk> findEscalatingRisks(@Param("entrepriseId") Long entrepriseId);
}







