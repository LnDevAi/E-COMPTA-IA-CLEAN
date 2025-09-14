package com.ecomptaia.elasticsearch;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Elasticsearch pour la recherche de documents
 */
@Profile({"!test", "!simple"})
@Repository
public interface DocumentSearchRepository extends ElasticsearchRepository<DocumentSearchEntity, String> {
    
    /**
     * Recherche full-text dans le contenu des documents
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^2\", \"description^1.5\", \"content\", \"tags\", \"searchableContent\"], \"type\": \"best_fields\", \"fuzziness\": \"AUTO\"}}")
    Page<DocumentSearchEntity> searchByContent(String searchTerm, Pageable pageable);
    
    /**
     * Recherche par entreprise et contenu
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"multi_match\": {\"query\": \"?1\", \"fields\": [\"title^2\", \"description^1.5\", \"content\", \"tags\", \"searchableContent\"], \"type\": \"best_fields\", \"fuzziness\": \"AUTO\"}}]}}")
    Page<DocumentSearchEntity> searchByCompanyAndContent(Long companyId, String searchTerm, Pageable pageable);
    
    /**
     * Recherche par type de document
     */
    List<DocumentSearchEntity> findByDocumentTypeAndCompanyId(String documentType, Long companyId);
    
    /**
     * Recherche par catégorie
     */
    List<DocumentSearchEntity> findByCategoryAndCompanyId(String category, Long companyId);
    
    /**
     * Recherche par statut
     */
    List<DocumentSearchEntity> findByStatusAndCompanyId(String status, Long companyId);
    
    /**
     * Recherche par niveau de sécurité
     */
    List<DocumentSearchEntity> findBySecurityLevelAndCompanyId(String securityLevel, Long companyId);
    
    /**
     * Recherche par créateur
     */
    List<DocumentSearchEntity> findByCreatedByAndCompanyId(Long createdBy, Long companyId);
    
    /**
     * Recherche par période de création
     */
    List<DocumentSearchEntity> findByCreatedAtBetweenAndCompanyId(LocalDateTime startDate, LocalDateTime endDate, Long companyId);
    
    /**
     * Recherche par tags
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"match\": {\"tags\": \"?1\"}}]}}")
    List<DocumentSearchEntity> findByTagsContainingAndCompanyId(Long companyId, String tag);
    
    /**
     * Recherche par nom de fichier
     */
    List<DocumentSearchEntity> findByFileNameContainingAndCompanyId(String fileName, Long companyId);
    
    /**
     * Recherche par code de document
     */
    List<DocumentSearchEntity> findByDocumentCodeContainingAndCompanyId(String documentCode, Long companyId);
    
    /**
     * Recherche par module de référence
     */
    List<DocumentSearchEntity> findByModuleReferenceAndCompanyId(String moduleReference, Long companyId);
    
    /**
     * Recherche par type d'entité de référence
     */
    List<DocumentSearchEntity> findByEntityReferenceTypeAndCompanyId(String entityReferenceType, Long companyId);
    
    /**
     * Recherche par ID d'entité de référence
     */
    List<DocumentSearchEntity> findByEntityReferenceIdAndCompanyId(Long entityReferenceId, Long companyId);
    
    /**
     * Recherche par écriture comptable
     */
    List<DocumentSearchEntity> findByEcritureIdAndCompanyId(Long ecritureId, Long companyId);
    
    /**
     * Recherche de documents archivés
     */
    List<DocumentSearchEntity> findByIsArchivedAndCompanyId(Boolean isArchived, Long companyId);
    
    /**
     * Recherche de documents expirés
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"range\": {\"expiryDate\": {\"lt\": \"?1\"}}}]}}")
    List<DocumentSearchEntity> findExpiredDocuments(Long companyId, LocalDateTime currentDate);
    
    /**
     * Recherche de documents expirant bientôt
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"range\": {\"expiryDate\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}}")
    List<DocumentSearchEntity> findDocumentsExpiringSoon(Long companyId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Recherche par version actuelle
     */
    List<DocumentSearchEntity> findByIsCurrentVersionAndCompanyId(Boolean isCurrentVersion, Long companyId);
    
    /**
     * Recherche par taille de fichier
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"range\": {\"fileSize\": {\"gte\": ?1, \"lte\": ?2}}}]}}")
    List<DocumentSearchEntity> findByFileSizeBetweenAndCompanyId(Long companyId, Long minSize, Long maxSize);
    
    /**
     * Recherche par type MIME
     */
    List<DocumentSearchEntity> findByMimeTypeAndCompanyId(String mimeType, Long companyId);
    
    /**
     * Recherche par type de fichier
     */
    List<DocumentSearchEntity> findByFileTypeAndCompanyId(String fileType, Long companyId);
    
    /**
     * Recherche par pays et standard comptable
     */
    List<DocumentSearchEntity> findByCountryCodeAndAccountingStandardAndCompanyId(String countryCode, String accountingStandard, Long companyId);
    
    /**
     * Recherche complexe avec plusieurs critères
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"term\": {\"documentType\": \"?1\"}}, {\"term\": {\"status\": \"?2\"}}, {\"term\": {\"securityLevel\": \"?3\"}}, {\"range\": {\"createdAt\": {\"gte\": \"?4\", \"lte\": \"?5\"}}}]}}")
    List<DocumentSearchEntity> findComplexSearch(Long companyId, String documentType, String status, String securityLevel, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Recherche par métadonnées
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"nested\": {\"path\": \"metadata\", \"query\": {\"bool\": {\"must\": [{\"term\": {\"metadata.key\": \"?1\"}}, {\"match\": {\"metadata.value\": \"?2\"}}]}}}}]}}")
    List<DocumentSearchEntity> findByMetadataKeyAndValue(Long companyId, String metadataKey, String metadataValue);
    
    /**
     * Recherche par contenu de métadonnées
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"nested\": {\"path\": \"metadata\", \"query\": {\"match\": {\"metadata.value\": \"?1\"}}}}]}}")
    List<DocumentSearchEntity> findByMetadataValueContaining(Long companyId, String metadataValue);
    
    /**
     * Recherche de documents récents
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"range\": {\"createdAt\": {\"gte\": \"?1\"}}}]}}")
    List<DocumentSearchEntity> findRecentDocuments(Long companyId, LocalDateTime since);
    
    /**
     * Recherche de documents modifiés récemment
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"range\": {\"updatedAt\": {\"gte\": \"?1\"}}}]}}")
    List<DocumentSearchEntity> findRecentlyUpdatedDocuments(Long companyId, LocalDateTime since);
    
    /**
     * Recherche par utilisateur qui a approuvé
     */
    List<DocumentSearchEntity> findByApprovedByAndCompanyId(Long approvedBy, Long companyId);
    
    /**
     * Recherche par période d'approbation
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"range\": {\"approvedAt\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}}")
    List<DocumentSearchEntity> findByApprovedAtBetweenAndCompanyId(Long companyId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Recherche par checksum (pour détecter les doublons)
     */
    List<DocumentSearchEntity> findByChecksumAndCompanyId(String checksum, Long companyId);
    
    /**
     * Recherche de documents chiffrés
     */
    List<DocumentSearchEntity> findByIsEncryptedAndCompanyId(Boolean isEncrypted, Long companyId);
    
    /**
     * Recherche par période de rétention
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"companyId\": ?0}}, {\"range\": {\"retentionPeriodYears\": {\"gte\": ?1, \"lte\": ?2}}}]}}")
    List<DocumentSearchEntity> findByRetentionPeriodBetweenAndCompanyId(Long companyId, Integer minYears, Integer maxYears);
}


