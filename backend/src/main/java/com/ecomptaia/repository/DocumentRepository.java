package com.ecomptaia.repository;

import com.ecomptaia.entity.GedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<GedDocument, Long> {
    
    // Recherche par entreprise
    List<GedDocument> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    
    // Recherche par type de document
    List<GedDocument> findByCompanyIdAndDocumentTypeOrderByCreatedAtDesc(Long companyId, GedDocument.DocumentType documentType);
    
    // Recherche par statut
    List<GedDocument> findByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, GedDocument.DocumentStatus status);
    
    // Recherche par niveau de sécurité
    List<GedDocument> findByCompanyIdAndSecurityLevelOrderByCreatedAtDesc(Long companyId, GedDocument.SecurityLevel securityLevel);
    
    // Recherche par catégorie
    List<GedDocument> findByCompanyIdAndCategoryOrderByCreatedAtDesc(Long companyId, String category);
    
    // Recherche par module de référence
    List<GedDocument> findByCompanyIdAndModuleReferenceOrderByCreatedAtDesc(Long companyId, String moduleReference);
    
    // Recherche par entité de référence
    List<GedDocument> findByCompanyIdAndEntityReferenceTypeAndEntityReferenceIdOrderByCreatedAtDesc(
        Long companyId, String entityReferenceType, Long entityReferenceId);
    
    // Recherche par créateur
    List<GedDocument> findByCompanyIdAndCreatedByOrderByCreatedAtDesc(Long companyId, Long createdBy);
    
    // Recherche par approbateur
    List<GedDocument> findByCompanyIdAndApprovedByOrderByApprovedAtDesc(Long companyId, Long approvedBy);
    
    // Recherche par date de création
    List<GedDocument> findByCompanyIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long companyId, LocalDate startDate, LocalDate endDate);
    
    // Recherche par date d'expiration
    List<GedDocument> findByCompanyIdAndExpiryDateBeforeOrderByExpiryDateAsc(Long companyId, LocalDate expiryDate);
    
    // Recherche par documents archivés
    List<GedDocument> findByCompanyIdAndIsArchivedOrderByArchiveDateDesc(Long companyId, Boolean isArchived);
    
    // Recherche par version
    List<GedDocument> findByCompanyIdAndParentDocumentIdOrderByVersionDesc(Long companyId, Long parentDocumentId);
    
    // Recherche par version actuelle
    List<GedDocument> findByCompanyIdAndIsCurrentVersionOrderByCreatedAtDesc(Long companyId, Boolean isCurrentVersion);
    
    // Recherche par code de document
    Optional<GedDocument> findByCompanyIdAndDocumentCode(Long companyId, String documentCode);
    
    // Recherche par nom de fichier
    List<GedDocument> findByCompanyIdAndFileNameContainingIgnoreCaseOrderByCreatedAtDesc(Long companyId, String fileName);
    
    // Recherche par titre
    List<GedDocument> findByCompanyIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(Long companyId, String title);
    
    // Recherche par description
    List<GedDocument> findByCompanyIdAndDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(Long companyId, String description);
    
    // Recherche par tags
    @Query("SELECT d FROM GedDocument d WHERE d.companyId = :companyId AND d.tags LIKE %:tag% ORDER BY d.createdAt DESC")
    List<GedDocument> findByCompanyIdAndTagsContainingOrderByCreatedAtDesc(@Param("companyId") Long companyId, @Param("tag") String tag);
    
    // Recherche par taille de fichier
    List<GedDocument> findByCompanyIdAndFileSizeGreaterThanOrderByFileSizeDesc(Long companyId, Long minFileSize);
    
    // Recherche par type de fichier
    List<GedDocument> findByCompanyIdAndFileTypeOrderByCreatedAtDesc(Long companyId, String fileType);
    
    // Recherche par MIME type
    List<GedDocument> findByCompanyIdAndMimeTypeOrderByCreatedAtDesc(Long companyId, String mimeType);
    
    // Recherche par documents chiffrés
    List<GedDocument> findByCompanyIdAndIsEncryptedOrderByCreatedAtDesc(Long companyId, Boolean isEncrypted);
    
    // Recherche par checksum
    Optional<GedDocument> findByCompanyIdAndChecksum(Long companyId, String checksum);
    
    // Recherche par documents en attente d'approbation
    List<GedDocument> findByCompanyIdAndStatusAndIsCurrentVersionOrderByCreatedAtDesc(
        Long companyId, GedDocument.DocumentStatus status, Boolean isCurrentVersion);
    
    // Recherche par documents expirés
    @Query("SELECT d FROM GedDocument d WHERE d.companyId = :companyId AND d.expiryDate < :currentDate ORDER BY d.expiryDate ASC")
    List<GedDocument> findExpiredDocuments(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);
    
    // Recherche par documents à archiver
    @Query("SELECT d FROM GedDocument d WHERE d.companyId = :companyId AND d.createdAt < :archiveDate AND d.isArchived = false ORDER BY d.createdAt ASC")
    List<GedDocument> findDocumentsToArchive(@Param("companyId") Long companyId, @Param("archiveDate") LocalDate archiveDate);
    
    // Statistiques par type de document
    @Query("SELECT d.documentType, COUNT(d) FROM GedDocument d WHERE d.companyId = :companyId GROUP BY d.documentType")
    List<Object[]> getDocumentTypeStats(@Param("companyId") Long companyId);
    
    // Statistiques par statut
    @Query("SELECT d.status, COUNT(d) FROM GedDocument d WHERE d.companyId = :companyId GROUP BY d.status")
    List<Object[]> getDocumentStatusStats(@Param("companyId") Long companyId);
    
    // Statistiques par niveau de sécurité
    @Query("SELECT d.securityLevel, COUNT(d) FROM GedDocument d WHERE d.companyId = :companyId GROUP BY d.securityLevel")
    List<Object[]> getSecurityLevelStats(@Param("companyId") Long companyId);
    
    // Statistiques par catégorie
    @Query("SELECT d.category, COUNT(d) FROM GedDocument d WHERE d.companyId = :companyId AND d.category IS NOT NULL GROUP BY d.category")
    List<Object[]> getCategoryStats(@Param("companyId") Long companyId);
    
    // Recherche full-text (titre, description, tags)
    @Query("SELECT d FROM GedDocument d WHERE d.companyId = :companyId AND " +
           "(LOWER(d.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY d.createdAt DESC")
    List<GedDocument> searchDocuments(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm);
    
    // Recherche avancée avec filtres multiples
    @Query("SELECT d FROM GedDocument d WHERE d.companyId = :companyId " +
           "AND (:documentType IS NULL OR d.documentType = :documentType) " +
           "AND (:status IS NULL OR d.status = :status) " +
           "AND (:securityLevel IS NULL OR d.securityLevel = :securityLevel) " +
           "AND (:category IS NULL OR d.category = :category) " +
           "AND (:moduleReference IS NULL OR d.moduleReference = :moduleReference) " +
           "ORDER BY d.createdAt DESC")
    List<GedDocument> findDocumentsWithFilters(
        @Param("companyId") Long companyId,
        @Param("documentType") GedDocument.DocumentType documentType,
        @Param("status") GedDocument.DocumentStatus status,
        @Param("securityLevel") GedDocument.SecurityLevel securityLevel,
        @Param("category") String category,
        @Param("moduleReference") String moduleReference
    );
}


