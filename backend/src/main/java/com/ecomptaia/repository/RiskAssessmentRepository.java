package com.ecomptaia.repository;

import com.ecomptaia.entity.Risk;
import com.ecomptaia.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    // ==================== REQUÊTES PAR RISQUE ====================

    List<RiskAssessment> findByRiskId(Long riskId);

    List<RiskAssessment> findByRiskIdOrderByAssessmentDateDesc(Long riskId);

    List<RiskAssessment> findByRiskIdAndAssessmentType(Long riskId, RiskAssessment.AssessmentType assessmentType);

    // ==================== REQUÊTES PAR ENTREPRISE ====================

    List<RiskAssessment> findByEntrepriseId(Long entrepriseId);

    List<RiskAssessment> findByEntrepriseIdOrderByAssessmentDateDesc(Long entrepriseId);

    List<RiskAssessment> findByEntrepriseIdAndAssessmentType(Long entrepriseId, RiskAssessment.AssessmentType assessmentType);

    List<RiskAssessment> findByEntrepriseIdAndAssessmentStatus(Long entrepriseId, RiskAssessment.AssessmentStatus assessmentStatus);

    List<RiskAssessment> findByEntrepriseIdAndApprovalStatus(Long entrepriseId, RiskAssessment.ApprovalStatus approvalStatus);

    List<RiskAssessment> findByEntrepriseIdAndComplianceStatus(Long entrepriseId, RiskAssessment.ComplianceStatus complianceStatus);

    // ==================== REQUÊTES PAR ÉVALUATEUR ====================

    List<RiskAssessment> findByAssessorId(Long assessorId);

    List<RiskAssessment> findByAssessorIdOrderByAssessmentDateDesc(Long assessorId);

    List<RiskAssessment> findByAssessorIdAndAssessmentStatus(Long assessorId, RiskAssessment.AssessmentStatus assessmentStatus);

    List<RiskAssessment> findByApprovedBy(Long approvedBy);

    // ==================== REQUÊTES PAR TYPE D'ÉVALUATION ====================

    List<RiskAssessment> findByAssessmentType(RiskAssessment.AssessmentType assessmentType);

    List<RiskAssessment> findByAssessmentTypeAndAssessmentStatus(RiskAssessment.AssessmentType assessmentType, RiskAssessment.AssessmentStatus assessmentStatus);

    // ==================== REQUÊTES PAR STATUT ====================

    List<RiskAssessment> findByAssessmentStatus(RiskAssessment.AssessmentStatus assessmentStatus);

    List<RiskAssessment> findByAssessmentStatusIn(List<RiskAssessment.AssessmentStatus> statuses);

    List<RiskAssessment> findByApprovalStatus(RiskAssessment.ApprovalStatus approvalStatus);

    List<RiskAssessment> findByComplianceStatus(RiskAssessment.ComplianceStatus complianceStatus);

    // ==================== REQUÊTES PAR DATE ====================

    List<RiskAssessment> findByAssessmentDateAfter(LocalDateTime date);

    List<RiskAssessment> findByAssessmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<RiskAssessment> findByCreatedAtAfter(LocalDateTime date);

    List<RiskAssessment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<RiskAssessment> findByNextAssessmentDateBefore(LocalDateTime date);

    List<RiskAssessment> findByApprovalDateAfter(LocalDateTime date);

    // ==================== REQUÊTES PAR NIVEAU DE RISQUE ====================

    List<RiskAssessment> findByRiskLevel(Risk.RiskLevel riskLevel);

    List<RiskAssessment> findByRiskLevelIn(List<Risk.RiskLevel> riskLevels);

    List<RiskAssessment> findByResidualRiskLevel(Risk.RiskLevel residualRiskLevel);

    List<RiskAssessment> findByResidualRiskLevelIn(List<Risk.RiskLevel> riskLevels);

    // ==================== REQUÊTES PAR SCORE ====================

    List<RiskAssessment> findByRiskScoreGreaterThan(Integer minScore);

    List<RiskAssessment> findByRiskScoreBetween(Integer minScore, Integer maxScore);

    List<RiskAssessment> findByResidualRiskScoreGreaterThan(Integer minScore);

    List<RiskAssessment> findByResidualRiskScoreBetween(Integer minScore, Integer maxScore);

    // ==================== REQUÊTES COMPLEXES ====================

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED' AND ra.riskScore >= :minScore ORDER BY ra.riskScore DESC")
    List<RiskAssessment> findHighRiskAssessments(@Param("entrepriseId") Long entrepriseId, @Param("minScore") Integer minScore);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED' AND ra.residualRiskScore >= :minScore ORDER BY ra.residualRiskScore DESC")
    List<RiskAssessment> findHighResidualRiskAssessments(@Param("entrepriseId") Long entrepriseId, @Param("minScore") Integer minScore);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.nextAssessmentDate <= :date AND ra.assessmentStatus = 'COMPLETED'")
    List<RiskAssessment> findAssessmentsNeedingReview(@Param("entrepriseId") Long entrepriseId, @Param("date") LocalDateTime date);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.approvalStatus = 'PENDING' AND ra.assessmentStatus = 'COMPLETED'")
    List<RiskAssessment> findPendingApprovalAssessments(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.complianceStatus = 'NON_COMPLIANT'")
    List<RiskAssessment> findNonCompliantAssessments(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.complianceScore < :minScore")
    List<RiskAssessment> findLowComplianceAssessments(@Param("entrepriseId") Long entrepriseId, @Param("minScore") Integer minScore);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.mitigationEffectiveness < :minEffectiveness")
    List<RiskAssessment> findLowMitigationEffectivenessAssessments(@Param("entrepriseId") Long entrepriseId, @Param("minEffectiveness") Integer minEffectiveness);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.controlEffectiveness < :minEffectiveness")
    List<RiskAssessment> findLowControlEffectivenessAssessments(@Param("entrepriseId") Long entrepriseId, @Param("minEffectiveness") Integer minEffectiveness);

    // ==================== REQUÊTES DE STATISTIQUES ====================

    @Query("SELECT COUNT(ra) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED'")
    Long countCompletedAssessments(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(ra) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED' AND ra.riskLevel = :riskLevel")
    Long countAssessmentsByRiskLevel(@Param("entrepriseId") Long entrepriseId, @Param("riskLevel") Risk.RiskLevel riskLevel);

    @Query("SELECT COUNT(ra) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED' AND ra.residualRiskLevel = :riskLevel")
    Long countAssessmentsByResidualRiskLevel(@Param("entrepriseId") Long entrepriseId, @Param("riskLevel") Risk.RiskLevel riskLevel);

    @Query("SELECT COUNT(ra) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.approvalStatus = :approvalStatus")
    Long countAssessmentsByApprovalStatus(@Param("entrepriseId") Long entrepriseId, @Param("approvalStatus") RiskAssessment.ApprovalStatus approvalStatus);

    @Query("SELECT COUNT(ra) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.complianceStatus = :complianceStatus")
    Long countAssessmentsByComplianceStatus(@Param("entrepriseId") Long entrepriseId, @Param("complianceStatus") RiskAssessment.ComplianceStatus complianceStatus);

    @Query("SELECT AVG(ra.riskScore) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED'")
    Double getAverageRiskScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(ra.residualRiskScore) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED'")
    Double getAverageResidualRiskScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(ra.complianceScore) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED'")
    Double getAverageComplianceScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(ra.mitigationEffectiveness) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED'")
    Double getAverageMitigationEffectiveness(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(ra.controlEffectiveness) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED'")
    Double getAverageControlEffectiveness(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT MAX(ra.riskScore) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED'")
    Integer getMaxRiskScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT MAX(ra.residualRiskScore) FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED'")
    Integer getMaxResidualRiskScore(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE TENDANCES ====================

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentDate BETWEEN :startDate AND :endDate ORDER BY ra.assessmentDate")
    List<RiskAssessment> findAssessmentsByDateRange(@Param("entrepriseId") Long entrepriseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.riskId = :riskId AND ra.assessmentDate BETWEEN :startDate AND :endDate ORDER BY ra.assessmentDate")
    List<RiskAssessment> findRiskAssessmentsByDateRange(@Param("riskId") Long riskId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ==================== REQUÊTES DE RECHERCHE ====================

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND (LOWER(ra.assessmentNotes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(ra.recommendations) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<RiskAssessment> searchAssessmentsByKeyword(@Param("entrepriseId") Long entrepriseId, @Param("keyword") String keyword);

    // ==================== REQUÊTES DE SURVEILLANCE ====================

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED' AND ra.residualRiskScore > ra.riskScore")
    List<RiskAssessment> findAssessmentsWithIncreasedResidualRisk(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED' AND ra.complianceScore < 3")
    List<RiskAssessment> findLowComplianceAssessments(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.entrepriseId = :entrepriseId AND ra.assessmentStatus = 'COMPLETED' AND ra.mitigationEffectiveness < 3")
    List<RiskAssessment> findLowMitigationAssessments(@Param("entrepriseId") Long entrepriseId);
}
