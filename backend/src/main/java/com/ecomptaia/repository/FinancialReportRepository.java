package com.ecomptaia.repository;

import com.ecomptaia.entity.FinancialReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport, Long> {

    // ==================== RECHERCHE PAR TYPE ====================

    List<FinancialReport> findByReportTypeOrderByReportDateDesc(FinancialReport.ReportType reportType);

    List<FinancialReport> findByReportTypeAndEntrepriseIdOrderByReportDateDesc(FinancialReport.ReportType reportType, Long entrepriseId);

    // ==================== RECHERCHE PAR STATUT ====================

    List<FinancialReport> findByStatusOrderByReportDateDesc(FinancialReport.ReportStatus status);

    List<FinancialReport> findByStatusAndEntrepriseIdOrderByReportDateDesc(FinancialReport.ReportStatus status, Long entrepriseId);

    // ==================== RECHERCHE PAR ENTREPRISE ====================

    List<FinancialReport> findByEntrepriseIdOrderByReportDateDesc(Long entrepriseId);

    List<FinancialReport> findByEntrepriseIdAndReportTypeOrderByReportDateDesc(Long entrepriseId, FinancialReport.ReportType reportType);

    List<FinancialReport> findByEntrepriseIdAndStatusOrderByReportDateDesc(Long entrepriseId, FinancialReport.ReportStatus status);

    // ==================== RECHERCHE PAR PÉRIODE ====================

    List<FinancialReport> findByPeriodStartBetweenOrderByPeriodStartDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<FinancialReport> findByPeriodEndBetweenOrderByPeriodEndDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<FinancialReport> findByEntrepriseIdAndPeriodStartBetweenOrderByPeriodStartDesc(Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate);

    List<FinancialReport> findByEntrepriseIdAndPeriodEndBetweenOrderByPeriodEndDesc(Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== RECHERCHE PAR CRÉATEUR ====================

    List<FinancialReport> findByGeneratedByOrderByCreatedAtDesc(Long generatedBy);

    List<FinancialReport> findByGeneratedByAndEntrepriseIdOrderByCreatedAtDesc(Long generatedBy, Long entrepriseId);

    // ==================== STATISTIQUES ====================

    @Query("SELECT COUNT(fr) FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(fr) FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId AND fr.reportType = :reportType")
    Long countByEntrepriseIdAndReportType(@Param("entrepriseId") Long entrepriseId, @Param("reportType") FinancialReport.ReportType reportType);

    @Query("SELECT COUNT(fr) FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId AND fr.status = :status")
    Long countByEntrepriseIdAndStatus(@Param("entrepriseId") Long entrepriseId, @Param("status") FinancialReport.ReportStatus status);

    @Query("SELECT COUNT(fr) FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId AND fr.periodStart BETWEEN :startDate AND :endDate")
    Long countByEntrepriseIdAndPeriodBetween(@Param("entrepriseId") Long entrepriseId, 
                                            @Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    // ==================== RAPPORTS RÉCENTS ====================

    @Query("SELECT fr FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId ORDER BY fr.createdAt DESC")
    List<FinancialReport> findRecentReports(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT fr FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId AND fr.reportType = :reportType ORDER BY fr.createdAt DESC")
    List<FinancialReport> findRecentReportsByType(@Param("entrepriseId") Long entrepriseId, @Param("reportType") FinancialReport.ReportType reportType);

    // ==================== RECHERCHE AVANCÉE ====================

    @Query("SELECT fr FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId " +
           "AND (:reportType IS NULL OR fr.reportType = :reportType) " +
           "AND (:status IS NULL OR fr.status = :status) " +
           "AND (:reportName IS NULL OR fr.reportName LIKE %:reportName%) " +
           "AND fr.periodStart BETWEEN :startDate AND :endDate " +
           "ORDER BY fr.periodStart DESC")
    List<FinancialReport> findReportsWithCriteria(@Param("entrepriseId") Long entrepriseId,
                                                 @Param("reportType") FinancialReport.ReportType reportType,
                                                 @Param("status") FinancialReport.ReportStatus status,
                                                 @Param("reportName") String reportName,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    // ==================== RAPPORTS PAR STANDARD COMPTABLE ====================

    List<FinancialReport> findByAccountingStandardOrderByCreatedAtDesc(FinancialReport.AccountingStandard accountingStandard);

    List<FinancialReport> findByEntrepriseIdAndAccountingStandardOrderByCreatedAtDesc(Long entrepriseId, FinancialReport.AccountingStandard accountingStandard);

    // ==================== RAPPORTS PAR DEVISE ====================

    List<FinancialReport> findByCurrencyOrderByCreatedAtDesc(String currency);

    List<FinancialReport> findByEntrepriseIdAndCurrencyOrderByCreatedAtDesc(Long entrepriseId, String currency);

    // ==================== RAPPORTS COMPLETS ====================

    @Query("SELECT fr FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId AND fr.reportType = 'COMPLETE_FINANCIAL_REPORT' ORDER BY fr.createdAt DESC")
    List<FinancialReport> findCompleteFinancialReports(@Param("entrepriseId") Long entrepriseId);

    // ==================== RAPPORTS AVEC NOTES ====================

    @Query("SELECT fr FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId AND fr.notesCount > 0 ORDER BY fr.notesCount DESC")
    List<FinancialReport> findReportsWithNotes(@Param("entrepriseId") Long entrepriseId);

    // ==================== RAPPORTS PAR TAILLE DE FICHIER ====================

    @Query("SELECT fr FROM FinancialReport fr WHERE fr.entrepriseId = :entrepriseId AND fr.fileSize IS NOT NULL ORDER BY fr.fileSize DESC")
    List<FinancialReport> findReportsByFileSize(@Param("entrepriseId") Long entrepriseId);
}




