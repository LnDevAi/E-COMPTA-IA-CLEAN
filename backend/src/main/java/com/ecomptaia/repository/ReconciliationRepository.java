package com.ecomptaia.repository;

import com.ecomptaia.entity.Reconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long> {

    List<Reconciliation> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<Reconciliation> findByBankAccountIdOrderByCreatedAtDesc(Long bankAccountId);

    List<Reconciliation> findByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, String status);

    Optional<Reconciliation> findByReconciliationNumberAndCompanyId(String reconciliationNumber, Long companyId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.status = 'COMPLETED' ORDER BY r.completedAt DESC")
    List<Reconciliation> findCompletedReconciliationsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.status = 'IN_PROGRESS' ORDER BY r.createdAt DESC")
    List<Reconciliation> findInProgressReconciliationsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.difference <> 0 ORDER BY r.createdAt DESC")
    List<Reconciliation> findReconciliationsWithDifferences(@Param("companyId") Long companyId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND (r.difference = 0 OR r.difference IS NULL) ORDER BY r.createdAt DESC")
    List<Reconciliation> findBalancedReconciliations(@Param("companyId") Long companyId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.isAutoReconciled = true ORDER BY r.createdAt DESC")
    List<Reconciliation> findAutoReconciledByCompany(@Param("companyId") Long companyId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.reconciliationMethod = :method ORDER BY r.createdAt DESC")
    List<Reconciliation> findByReconciliationMethod(@Param("companyId") Long companyId, @Param("method") String method);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.startDate >= :startDate AND r.endDate <= :endDate ORDER BY r.createdAt DESC")
    List<Reconciliation> findByDateRange(@Param("companyId") Long companyId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.reconciliationDate >= :startDate AND r.reconciliationDate <= :endDate ORDER BY r.reconciliationDate DESC")
    List<Reconciliation> findByReconciliationDateRange(@Param("companyId") Long companyId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.reconciliationNumber LIKE %:searchTerm% ORDER BY r.createdAt DESC")
    List<Reconciliation> searchByReconciliationNumber(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.notes LIKE %:searchTerm% ORDER BY r.createdAt DESC")
    List<Reconciliation> searchByNotes(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm);

    @Query("SELECT COUNT(r) FROM Reconciliation r WHERE r.companyId = :companyId AND r.status = 'COMPLETED'")
    Long countCompletedReconciliationsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(r) FROM Reconciliation r WHERE r.companyId = :companyId AND r.status = 'IN_PROGRESS'")
    Long countInProgressReconciliationsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(r) FROM Reconciliation r WHERE r.companyId = :companyId AND r.status = 'DRAFT'")
    Long countDraftReconciliationsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(r) FROM Reconciliation r WHERE r.companyId = :companyId AND r.status = 'CANCELLED'")
    Long countCancelledReconciliationsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(r) FROM Reconciliation r WHERE r.companyId = :companyId AND r.isAutoReconciled = true")
    Long countAutoReconciledByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(r) FROM Reconciliation r WHERE r.companyId = :companyId AND r.difference <> 0")
    Long countReconciliationsWithDifferences(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(r) FROM Reconciliation r WHERE r.companyId = :companyId AND (r.difference = 0 OR r.difference IS NULL)")
    Long countBalancedReconciliations(@Param("companyId") Long companyId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.isLocked = true ORDER BY r.lockedAt DESC")
    List<Reconciliation> findLockedReconciliationsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.completedBy = :userId ORDER BY r.completedAt DESC")
    List<Reconciliation> findByCompletedBy(@Param("companyId") Long companyId, @Param("userId") Long userId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.createdBy = :userId ORDER BY r.createdAt DESC")
    List<Reconciliation> findByCreatedBy(@Param("companyId") Long companyId, @Param("userId") Long userId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.bankAccountId = :bankAccountId AND r.status = 'COMPLETED' ORDER BY r.completedAt DESC")
    List<Reconciliation> findCompletedByBankAccount(@Param("companyId") Long companyId, @Param("bankAccountId") Long bankAccountId);

    @Query("SELECT r FROM Reconciliation r WHERE r.companyId = :companyId AND r.bankAccountId = :bankAccountId AND r.status = 'IN_PROGRESS' ORDER BY r.createdAt DESC")
    List<Reconciliation> findInProgressByBankAccount(@Param("companyId") Long companyId, @Param("bankAccountId") Long bankAccountId);
}
