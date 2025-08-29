package com.ecomptaia.repository;

import com.ecomptaia.entity.ComplianceRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComplianceRequirementRepository extends JpaRepository<ComplianceRequirement, Long> {

    // ==================== REQUÊTES PAR ENTREPRISE ====================

    List<ComplianceRequirement> findByEntrepriseId(Long entrepriseId);

    List<ComplianceRequirement> findByEntrepriseIdAndIsActiveTrue(Long entrepriseId);

    // ==================== REQUÊTES PAR CATÉGORIE ====================

    List<ComplianceRequirement> findByEntrepriseIdAndRequirementCategory(Long entrepriseId, 
                                                                       ComplianceRequirement.RequirementCategory category);

    List<ComplianceRequirement> findByEntrepriseIdAndRequirementCategoryAndIsActiveTrue(Long entrepriseId, 
                                                                                      ComplianceRequirement.RequirementCategory category);

    // ==================== REQUÊTES PAR TYPE ====================

    List<ComplianceRequirement> findByEntrepriseIdAndRequirementType(Long entrepriseId, 
                                                                   ComplianceRequirement.RequirementType type);

    List<ComplianceRequirement> findByEntrepriseIdAndRequirementTypeAndIsActiveTrue(Long entrepriseId, 
                                                                                  ComplianceRequirement.RequirementType type);

    // ==================== REQUÊTES PAR STATUT ====================

    List<ComplianceRequirement> findByEntrepriseIdAndComplianceStatus(Long entrepriseId, 
                                                                    ComplianceRequirement.ComplianceStatus status);

    List<ComplianceRequirement> findByEntrepriseIdAndComplianceStatusAndIsActiveTrue(Long entrepriseId, 
                                                                                   ComplianceRequirement.ComplianceStatus status);

    // ==================== REQUÊTES PAR PRIORITÉ ====================

    List<ComplianceRequirement> findByEntrepriseIdAndPriorityLevel(Long entrepriseId, 
                                                                 ComplianceRequirement.PriorityLevel priority);

    List<ComplianceRequirement> findByEntrepriseIdAndPriorityLevelAndIsActiveTrue(Long entrepriseId, 
                                                                                ComplianceRequirement.PriorityLevel priority);

    // ==================== REQUÊTES PAR NIVEAU DE RISQUE ====================

    List<ComplianceRequirement> findByEntrepriseIdAndRiskLevel(Long entrepriseId, 
                                                             ComplianceRequirement.RiskLevel riskLevel);

    List<ComplianceRequirement> findByEntrepriseIdAndRiskLevelAndIsActiveTrue(Long entrepriseId, 
                                                                            ComplianceRequirement.RiskLevel riskLevel);

    // ==================== REQUÊTES DE RECHERCHE ====================

    @Query("SELECT cr FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId " +
           "AND (LOWER(cr.requirementName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.requirementDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.requirementCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ComplianceRequirement> searchRequirements(@Param("entrepriseId") Long entrepriseId, 
                                                 @Param("keyword") String keyword);

    @Query("SELECT cr FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId " +
           "AND cr.isActive = true " +
           "AND (LOWER(cr.requirementName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.requirementDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.requirementCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ComplianceRequirement> searchActiveRequirements(@Param("entrepriseId") Long entrepriseId, 
                                                       @Param("keyword") String keyword);

    // ==================== REQUÊTES DE CONFORMITÉ ====================

    List<ComplianceRequirement> findByEntrepriseIdAndComplianceStatusAndNextComplianceDateBefore(Long entrepriseId, 
                                                                                               ComplianceRequirement.ComplianceStatus status, 
                                                                                               LocalDateTime date);

    List<ComplianceRequirement> findByEntrepriseIdAndComplianceStatusAndNextComplianceDateBetween(Long entrepriseId, 
                                                                                                ComplianceRequirement.ComplianceStatus status, 
                                                                                                LocalDateTime startDate, 
                                                                                                LocalDateTime endDate);

    // ==================== REQUÊTES DE STATISTIQUES ====================

    @Query("SELECT COUNT(cr) FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(cr) FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId AND cr.isActive = true")
    Long countActiveByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(cr) FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId AND cr.complianceStatus = :status")
    Long countByEntrepriseIdAndComplianceStatus(@Param("entrepriseId") Long entrepriseId, 
                                               @Param("status") ComplianceRequirement.ComplianceStatus status);

    @Query("SELECT COUNT(cr) FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId AND cr.requirementCategory = :category")
    Long countByEntrepriseIdAndCategory(@Param("entrepriseId") Long entrepriseId, 
                                       @Param("category") ComplianceRequirement.RequirementCategory category);

    @Query("SELECT COUNT(cr) FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId AND cr.priorityLevel = :priority")
    Long countByEntrepriseIdAndPriority(@Param("entrepriseId") Long entrepriseId, 
                                       @Param("priority") ComplianceRequirement.PriorityLevel priority);

    @Query("SELECT COUNT(cr) FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId AND cr.riskLevel = :riskLevel")
    Long countByEntrepriseIdAndRiskLevel(@Param("entrepriseId") Long entrepriseId, 
                                        @Param("riskLevel") ComplianceRequirement.RiskLevel riskLevel);

    // ==================== REQUÊTES DE SURVEILLANCE ====================

    @Query("SELECT cr FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId " +
           "AND cr.nextComplianceDate <= :date AND cr.complianceStatus != 'COMPLIANT'")
    List<ComplianceRequirement> findOverdueRequirements(@Param("entrepriseId") Long entrepriseId, 
                                                      @Param("date") LocalDateTime date);

    @Query("SELECT cr FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId " +
           "AND cr.nextComplianceDate BETWEEN :startDate AND :endDate")
    List<ComplianceRequirement> findUpcomingRequirements(@Param("entrepriseId") Long entrepriseId, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT cr FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId " +
           "AND cr.complianceStatus = 'NON_COMPLIANT' AND cr.priorityLevel = 'CRITICAL'")
    List<ComplianceRequirement> findCriticalNonCompliantRequirements(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT cr FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId " +
           "AND cr.complianceStatus = 'NON_COMPLIANT' AND cr.priorityLevel = 'HIGH'")
    List<ComplianceRequirement> findHighPriorityNonCompliantRequirements(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE RAPPORT ====================

    @Query("SELECT cr.requirementCategory, COUNT(cr) FROM ComplianceRequirement cr " +
           "WHERE cr.entrepriseId = :entrepriseId GROUP BY cr.requirementCategory")
    List<Object[]> getRequirementsByCategory(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT cr.complianceStatus, COUNT(cr) FROM ComplianceRequirement cr " +
           "WHERE cr.entrepriseId = :entrepriseId GROUP BY cr.complianceStatus")
    List<Object[]> getRequirementsByStatus(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT cr.priorityLevel, COUNT(cr) FROM ComplianceRequirement cr " +
           "WHERE cr.entrepriseId = :entrepriseId GROUP BY cr.priorityLevel")
    List<Object[]> getRequirementsByPriority(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT cr.riskLevel, COUNT(cr) FROM ComplianceRequirement cr " +
           "WHERE cr.entrepriseId = :entrepriseId GROUP BY cr.riskLevel")
    List<Object[]> getRequirementsByRiskLevel(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE PERFORMANCE ====================

    @Query("SELECT AVG(cr.complianceScore) FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId AND cr.complianceScore IS NOT NULL")
    Double getAverageComplianceScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(cr.complianceScore) FROM ComplianceRequirement cr " +
           "WHERE cr.entrepriseId = :entrepriseId AND cr.requirementCategory = :category AND cr.complianceScore IS NOT NULL")
    Double getAverageComplianceScoreByCategory(@Param("entrepriseId") Long entrepriseId, 
                                              @Param("category") ComplianceRequirement.RequirementCategory category);

    // ==================== REQUÊTES DE FILTRAGE AVANCÉ ====================

    @Query("SELECT cr FROM ComplianceRequirement cr WHERE cr.entrepriseId = :entrepriseId " +
           "AND (:category IS NULL OR cr.requirementCategory = :category) " +
           "AND (:type IS NULL OR cr.requirementType = :type) " +
           "AND (:status IS NULL OR cr.complianceStatus = :status) " +
           "AND (:priority IS NULL OR cr.priorityLevel = :priority) " +
           "AND (:riskLevel IS NULL OR cr.riskLevel = :riskLevel) " +
           "AND cr.isActive = true")
    List<ComplianceRequirement> findFilteredRequirements(@Param("entrepriseId") Long entrepriseId,
                                                        @Param("category") ComplianceRequirement.RequirementCategory category,
                                                        @Param("type") ComplianceRequirement.RequirementType type,
                                                        @Param("status") ComplianceRequirement.ComplianceStatus status,
                                                        @Param("priority") ComplianceRequirement.PriorityLevel priority,
                                                        @Param("riskLevel") ComplianceRequirement.RiskLevel riskLevel);
}




