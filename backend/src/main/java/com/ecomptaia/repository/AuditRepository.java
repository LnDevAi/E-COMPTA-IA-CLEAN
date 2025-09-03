package com.ecomptaia.repository;

import com.ecomptaia.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    // ==================== REQUÊTES PAR ENTREPRISE ====================

    List<Audit> findByEntrepriseId(Long entrepriseId);

    List<Audit> findByEntrepriseIdOrderByCreatedAtDesc(Long entrepriseId);

    // ==================== REQUÊTES PAR TYPE D'AUDIT ====================

    List<Audit> findByEntrepriseIdAndAuditType(Long entrepriseId, Audit.AuditType auditType);

    List<Audit> findByEntrepriseIdAndAuditTypeOrderByCreatedAtDesc(Long entrepriseId, Audit.AuditType auditType);

    // ==================== REQUÊTES PAR STATUT ====================

    List<Audit> findByEntrepriseIdAndAuditStatus(Long entrepriseId, Audit.AuditStatus auditStatus);

    List<Audit> findByEntrepriseIdAndAuditStatusOrderByCreatedAtDesc(Long entrepriseId, Audit.AuditStatus auditStatus);

    // ==================== REQUÊTES PAR PRIORITÉ ====================

    List<Audit> findByEntrepriseIdAndAuditPriority(Long entrepriseId, Audit.AuditPriority auditPriority);

    List<Audit> findByEntrepriseIdAndAuditPriorityOrderByCreatedAtDesc(Long entrepriseId, Audit.AuditPriority auditPriority);

    // ==================== REQUÊTES PAR AUDITEUR ====================

    List<Audit> findByEntrepriseIdAndAuditorId(Long entrepriseId, Long auditorId);

    List<Audit> findByEntrepriseIdAndAuditorIdOrderByCreatedAtDesc(Long entrepriseId, Long auditorId);

    // ==================== REQUÊTES PAR PORTÉE ====================

    List<Audit> findByEntrepriseIdAndAuditScope(Long entrepriseId, Audit.AuditScope auditScope);

    List<Audit> findByEntrepriseIdAndAuditScopeOrderByCreatedAtDesc(Long entrepriseId, Audit.AuditScope auditScope);

    // ==================== REQUÊTES DE RECHERCHE ====================

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND (LOWER(a.auditName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.auditDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.auditCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Audit> searchAudits(@Param("entrepriseId") Long entrepriseId, @Param("keyword") String keyword);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND (LOWER(a.auditName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.auditDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.auditCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.createdAt DESC")
    List<Audit> searchAuditsOrderByCreatedAtDesc(@Param("entrepriseId") Long entrepriseId, @Param("keyword") String keyword);

    // ==================== REQUÊTES DE DATES ====================

    List<Audit> findByEntrepriseIdAndPlannedStartDateBetween(Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate);

    List<Audit> findByEntrepriseIdAndPlannedEndDateBetween(Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate);

    List<Audit> findByEntrepriseIdAndActualStartDateBetween(Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate);

    List<Audit> findByEntrepriseIdAndActualEndDateBetween(Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate);

    List<Audit> findByEntrepriseIdAndCreatedAtBetween(Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== REQUÊTES DE SURVEILLANCE ====================

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND a.plannedEndDate <= :date AND a.auditStatus != 'COMPLETED'")
    List<Audit> findOverdueAudits(@Param("entrepriseId") Long entrepriseId, @Param("date") LocalDateTime date);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND a.plannedStartDate BETWEEN :startDate AND :endDate")
    List<Audit> findUpcomingAudits(@Param("entrepriseId") Long entrepriseId, 
                                  @Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND a.auditStatus = 'IN_PROGRESS'")
    List<Audit> findInProgressAudits(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND a.auditStatus = 'PLANNED'")
    List<Audit> findPlannedAudits(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND a.followUpRequired = true")
    List<Audit> findAuditsRequiringFollowUp(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND a.approvalStatus = 'PENDING'")
    List<Audit> findPendingApprovalAudits(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE STATISTIQUES ====================

    @Query("SELECT COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.auditStatus = :status")
    Long countByEntrepriseIdAndAuditStatus(@Param("entrepriseId") Long entrepriseId, 
                                         @Param("status") Audit.AuditStatus auditStatus);

    @Query("SELECT COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.auditType = :type")
    Long countByEntrepriseIdAndAuditType(@Param("entrepriseId") Long entrepriseId, 
                                       @Param("type") Audit.AuditType auditType);

    @Query("SELECT COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.auditPriority = :priority")
    Long countByEntrepriseIdAndAuditPriority(@Param("entrepriseId") Long entrepriseId, 
                                           @Param("priority") Audit.AuditPriority auditPriority);

    @Query("SELECT COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.overallRating = :rating")
    Long countByEntrepriseIdAndOverallRating(@Param("entrepriseId") Long entrepriseId, 
                                           @Param("rating") Audit.OverallRating overallRating);

    // ==================== REQUÊTES DE RAPPORT ====================

    @Query("SELECT a.auditType, COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId GROUP BY a.auditType")
    List<Object[]> getAuditsByType(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a.auditStatus, COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId GROUP BY a.auditStatus")
    List<Object[]> getAuditsByStatus(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a.auditPriority, COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId GROUP BY a.auditPriority")
    List<Object[]> getAuditsByPriority(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a.overallRating, COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId GROUP BY a.overallRating")
    List<Object[]> getAuditsByOverallRating(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a.auditScope, COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId GROUP BY a.auditScope")
    List<Object[]> getAuditsByScope(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE PERFORMANCE ====================

    @Query("SELECT AVG(a.auditScore) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.auditScore IS NOT NULL")
    Double getAverageAuditScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(a.complianceScore) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.complianceScore IS NOT NULL")
    Double getAverageComplianceScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(a.riskScore) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.riskScore IS NOT NULL")
    Double getAverageRiskScore(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(a.auditScore) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.auditType = :type AND a.auditScore IS NOT NULL")
    Double getAverageAuditScoreByType(@Param("entrepriseId") Long entrepriseId, 
                                    @Param("type") Audit.AuditType auditType);

    @Query("SELECT AVG(a.complianceScore) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.auditType = :type AND a.complianceScore IS NOT NULL")
    Double getAverageComplianceScoreByType(@Param("entrepriseId") Long entrepriseId, 
                                         @Param("type") Audit.AuditType auditType);

    // ==================== REQUÊTES DE COÛTS ====================

    @Query("SELECT SUM(a.actualCost) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.actualCost IS NOT NULL")
    Double getTotalActualCost(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT SUM(a.budgetAmount) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.budgetAmount IS NOT NULL")
    Double getTotalBudgetAmount(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(a.actualCost) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.actualCost IS NOT NULL")
    Double getAverageActualCost(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(a.budgetAmount) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.budgetAmount IS NOT NULL")
    Double getAverageBudgetAmount(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE FILTRAGE AVANCÉ ====================

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND (:auditType IS NULL OR a.auditType = :auditType) " +
           "AND (:auditStatus IS NULL OR a.auditStatus = :auditStatus) " +
           "AND (:auditPriority IS NULL OR a.auditPriority = :auditPriority) " +
           "AND (:auditScope IS NULL OR a.auditScope = :auditScope) " +
           "AND (:overallRating IS NULL OR a.overallRating = :overallRating) " +
           "AND (:startDate IS NULL OR a.plannedStartDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.plannedEndDate <= :endDate) " +
           "ORDER BY a.createdAt DESC")
    List<Audit> findFilteredAudits(@Param("entrepriseId") Long entrepriseId,
                                   @Param("auditType") Audit.AuditType auditType,
                                   @Param("auditStatus") Audit.AuditStatus auditStatus,
                                   @Param("auditPriority") Audit.AuditPriority auditPriority,
                                   @Param("auditScope") Audit.AuditScope auditScope,
                                   @Param("overallRating") Audit.OverallRating overallRating,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    // ==================== REQUÊTES DE RAPPORTS GÉNÉRÉS ====================

    @Query("SELECT COUNT(a) FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.reportGenerated = true")
    Long countReportsGenerated(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.reportGenerated = true")
    List<Audit> findAuditsWithReports(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId AND a.reportGenerated = false")
    List<Audit> findAuditsWithoutReports(@Param("entrepriseId") Long entrepriseId);

    // ==================== REQUÊTES DE SUIVI ====================

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND a.followUpDate <= :date AND a.followUpRequired = true")
    List<Audit> findOverdueFollowUpAudits(@Param("entrepriseId") Long entrepriseId, 
                                         @Param("date") LocalDateTime date);

    @Query("SELECT a FROM Audit a WHERE a.entrepriseId = :entrepriseId " +
           "AND a.followUpDate BETWEEN :startDate AND :endDate AND a.followUpRequired = true")
    List<Audit> findUpcomingFollowUpAudits(@Param("entrepriseId") Long entrepriseId, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
}





