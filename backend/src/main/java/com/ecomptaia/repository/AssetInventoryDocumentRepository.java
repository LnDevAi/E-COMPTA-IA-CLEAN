package com.ecomptaia.repository;

import com.ecomptaia.entity.AssetInventoryDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetInventoryDocumentRepository extends JpaRepository<AssetInventoryDocument, Long> {

    List<AssetInventoryDocument> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<AssetInventoryDocument> findByCompanyIdAndDocumentTypeOrderByCreatedAtDesc(Long companyId, String documentType);

    List<AssetInventoryDocument> findByCompanyIdAndRelatedEntityTypeOrderByCreatedAtDesc(Long companyId, String relatedEntityType);

    List<AssetInventoryDocument> findByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, String status);

    Optional<AssetInventoryDocument> findByDocumentNumberAndCompanyId(String documentNumber, Long companyId);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.relatedEntityId = :entityId AND aid.relatedEntityType = :entityType ORDER BY aid.createdAt DESC")
    List<AssetInventoryDocument> findByRelatedEntity(@Param("companyId") Long companyId, 
                                                    @Param("entityId") Long entityId, 
                                                    @Param("entityType") String entityType);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.relatedEntityCode = :entityCode ORDER BY aid.createdAt DESC")
    List<AssetInventoryDocument> findByRelatedEntityCode(@Param("companyId") Long companyId, 
                                                        @Param("entityCode") String entityCode);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.supplierCode = :supplierCode ORDER BY aid.createdAt DESC")
    List<AssetInventoryDocument> findBySupplierCode(@Param("companyId") Long companyId, 
                                                   @Param("supplierCode") String supplierCode);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.isReconciled = false ORDER BY aid.createdAt DESC")
    List<AssetInventoryDocument> findUnreconciledDocumentsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.isReconciled = true ORDER BY aid.createdAt DESC")
    List<AssetInventoryDocument> findReconciledDocumentsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.isArchived = false ORDER BY aid.createdAt DESC")
    List<AssetInventoryDocument> findActiveDocumentsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.isArchived = true ORDER BY aid.archivedAt DESC")
    List<AssetInventoryDocument> findArchivedDocumentsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.documentDate BETWEEN :startDate AND :endDate ORDER BY aid.documentDate DESC")
    List<AssetInventoryDocument> findByDateRange(@Param("companyId") Long companyId, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT aid FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.accountingEntryReference IS NOT NULL ORDER BY aid.createdAt DESC")
    List<AssetInventoryDocument> findDocumentsWithAccountingEntries(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(aid) FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.documentType = :documentType")
    Long countByDocumentType(@Param("companyId") Long companyId, @Param("documentType") String documentType);

    @Query("SELECT COUNT(aid) FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.status = 'VALIDATED'")
    Long countValidatedDocumentsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(aid) FROM AssetInventoryDocument aid WHERE aid.companyId = :companyId AND aid.status = 'DRAFT'")
    Long countDraftDocumentsByCompany(@Param("companyId") Long companyId);
}


