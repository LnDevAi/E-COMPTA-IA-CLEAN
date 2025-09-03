package com.ecomptaia.repository;

import com.ecomptaia.entity.InventoryAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryAnalysisRepository extends JpaRepository<InventoryAnalysis, Long> {

    List<InventoryAnalysis> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<InventoryAnalysis> findByCompanyIdAndAnalysisTypeOrderByCreatedAtDesc(Long companyId, String analysisType);

    List<InventoryAnalysis> findByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, String status);

    Optional<InventoryAnalysis> findByAnalysisNumberAndCompanyId(String analysisNumber, Long companyId);

    @Query("SELECT ia FROM InventoryAnalysis ia WHERE ia.companyId = :companyId AND ia.analysisDate BETWEEN :startDate AND :endDate ORDER BY ia.analysisDate DESC")
    List<InventoryAnalysis> findByDateRange(@Param("companyId") Long companyId, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ia FROM InventoryAnalysis ia WHERE ia.companyId = :companyId AND ia.isReportGenerated = true ORDER BY ia.createdAt DESC")
    List<InventoryAnalysis> findWithReportsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT ia FROM InventoryAnalysis ia WHERE ia.companyId = :companyId AND ia.isAccountingEntriesGenerated = true ORDER BY ia.createdAt DESC")
    List<InventoryAnalysis> findWithAccountingEntriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(ia) FROM InventoryAnalysis ia WHERE ia.companyId = :companyId AND ia.status = 'COMPLETED'")
    Long countCompletedAnalysesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(ia) FROM InventoryAnalysis ia WHERE ia.companyId = :companyId AND ia.status = 'DRAFT'")
    Long countDraftAnalysesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT ia FROM InventoryAnalysis ia WHERE ia.companyId = :companyId AND ia.totalVariance > :threshold ORDER BY ia.totalVariance DESC")
    List<InventoryAnalysis> findAnalysesWithHighVariance(@Param("companyId") Long companyId, 
                                                        @Param("threshold") java.math.BigDecimal threshold);
}



