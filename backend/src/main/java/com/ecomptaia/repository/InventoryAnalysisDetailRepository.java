package com.ecomptaia.repository;

import com.ecomptaia.entity.InventoryAnalysisDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InventoryAnalysisDetailRepository extends JpaRepository<InventoryAnalysisDetail, Long> {

    List<InventoryAnalysisDetail> findByAnalysisId(Long analysisId);

    List<InventoryAnalysisDetail> findByCompanyId(Long companyId);

    List<InventoryAnalysisDetail> findByItemCodeAndCompanyId(String itemCode, Long companyId);

    List<InventoryAnalysisDetail> findByItemTypeAndCompanyId(String itemType, Long companyId);

    @Query("SELECT iad FROM InventoryAnalysisDetail iad WHERE iad.analysisId = :analysisId AND iad.isReconciled = false")
    List<InventoryAnalysisDetail> findUnreconciledByAnalysis(@Param("analysisId") Long analysisId);

    @Query("SELECT iad FROM InventoryAnalysisDetail iad WHERE iad.analysisId = :analysisId AND iad.isReconciled = true")
    List<InventoryAnalysisDetail> findReconciledByAnalysis(@Param("analysisId") Long analysisId);

    @Query("SELECT iad FROM InventoryAnalysisDetail iad WHERE iad.companyId = :companyId AND iad.varianceType = :varianceType")
    List<InventoryAnalysisDetail> findByVarianceType(@Param("companyId") Long companyId, 
                                                    @Param("varianceType") String varianceType);

    @Query("SELECT iad FROM InventoryAnalysisDetail iad WHERE iad.companyId = :companyId AND iad.valueVariance > :threshold ORDER BY iad.valueVariance DESC")
    List<InventoryAnalysisDetail> findHighValueVariances(@Param("companyId") Long companyId, 
                                                        @Param("threshold") BigDecimal threshold);

    @Query("SELECT iad FROM InventoryAnalysisDetail iad WHERE iad.companyId = :companyId AND iad.quantityVariance > :threshold ORDER BY iad.quantityVariance DESC")
    List<InventoryAnalysisDetail> findHighQuantityVariances(@Param("companyId") Long companyId, 
                                                           @Param("threshold") BigDecimal threshold);

    @Query("SELECT iad FROM InventoryAnalysisDetail iad WHERE iad.companyId = :companyId AND iad.proposedAction = :action")
    List<InventoryAnalysisDetail> findByProposedAction(@Param("companyId") Long companyId, 
                                                      @Param("action") String action);

    @Query("SELECT COUNT(iad) FROM InventoryAnalysisDetail iad WHERE iad.analysisId = :analysisId AND iad.varianceType != 'NONE'")
    Long countItemsWithVarianceByAnalysis(@Param("analysisId") Long analysisId);

    @Query("SELECT COUNT(iad) FROM InventoryAnalysisDetail iad WHERE iad.analysisId = :analysisId AND iad.isReconciled = true")
    Long countReconciledItemsByAnalysis(@Param("analysisId") Long analysisId);
}


