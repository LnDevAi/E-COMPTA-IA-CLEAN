package com.ecomptaia.service;

import com.ecomptaia.accounting.entity.AccountingStandard;

import com.ecomptaia.elasticsearch.DocumentSearchEntity;
import com.ecomptaia.elasticsearch.DocumentSearchRepository;
import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.repository.GedDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la recherche full-text avec Elasticsearch
 */
@Service
@Transactional
public class ElasticsearchSearchService {
    
    @Autowired
    private DocumentSearchRepository searchRepository;
    
    @Autowired
    private GedDocumentRepository documentRepository;
    
    // ==================== INDEXATION ====================
    
    /**
     * Indexe un document dans Elasticsearch
     */
    public void indexDocument(GedDocument document) {
        try {
            DocumentSearchEntity searchEntity = convertToSearchEntity(document);
            searchRepository.save(searchEntity);
        } catch (Exception e) {
            // Log l'erreur mais ne pas faire échouer l'opération principale
            System.err.println("Erreur lors de l'indexation du document " + document.getId() + ": " + e.getMessage());
        }
    }
    
    /**
     * Indexe plusieurs documents
     */
    public void indexDocuments(List<GedDocument> documents) {
        try {
            List<DocumentSearchEntity> searchEntities = documents.stream()
                .map(this::convertToSearchEntity)
                .collect(Collectors.toList());
            searchRepository.saveAll(searchEntities);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'indexation des documents: " + e.getMessage());
        }
    }
    
    /**
     * Supprime un document de l'index
     */
    public void removeDocument(Long documentId) {
        try {
            searchRepository.deleteById(documentId.toString());
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du document " + documentId + ": " + e.getMessage());
        }
    }
    
    /**
     * Met à jour l'index d'un document
     */
    public void updateDocument(GedDocument document) {
        try {
            DocumentSearchEntity searchEntity = convertToSearchEntity(document);
            searchRepository.save(searchEntity);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du document " + document.getId() + ": " + e.getMessage());
        }
    }
    
    // ==================== RECHERCHE ====================
    
    /**
     * Recherche full-text dans tous les documents
     */
    public Page<DocumentSearchEntity> searchDocuments(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return searchRepository.searchByContent(searchTerm, pageable);
    }
    
    /**
     * Recherche full-text dans les documents d'une entreprise
     */
    public Page<DocumentSearchEntity> searchDocumentsByCompany(Long companyId, String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return searchRepository.searchByCompanyAndContent(companyId, searchTerm, pageable);
    }
    
    /**
     * Recherche par type de document
     */
    public List<DocumentSearchEntity> searchByDocumentType(Long companyId, String documentType) {
        return searchRepository.findByDocumentTypeAndCompanyId(documentType, companyId);
    }
    
    /**
     * Recherche par catégorie
     */
    public List<DocumentSearchEntity> searchByCategory(Long companyId, String category) {
        return searchRepository.findByCategoryAndCompanyId(category, companyId);
    }
    
    /**
     * Recherche par statut
     */
    public List<DocumentSearchEntity> searchByStatus(Long companyId, String status) {
        return searchRepository.findByStatusAndCompanyId(status, companyId);
    }
    
    /**
     * Recherche par niveau de sécurité
     */
    public List<DocumentSearchEntity> searchBySecurityLevel(Long companyId, String securityLevel) {
        return searchRepository.findBySecurityLevelAndCompanyId(securityLevel, companyId);
    }
    
    /**
     * Recherche par créateur
     */
    public List<DocumentSearchEntity> searchByCreator(Long companyId, Long createdBy) {
        return searchRepository.findByCreatedByAndCompanyId(createdBy, companyId);
    }
    
    /**
     * Recherche par période
     */
    public List<DocumentSearchEntity> searchByDateRange(Long companyId, LocalDateTime startDate, LocalDateTime endDate) {
        return searchRepository.findByCreatedAtBetweenAndCompanyId(startDate, endDate, companyId);
    }
    
    /**
     * Recherche par tags
     */
    public List<DocumentSearchEntity> searchByTags(Long companyId, String tag) {
        return searchRepository.findByTagsContainingAndCompanyId(companyId, tag);
    }
    
    /**
     * Recherche par nom de fichier
     */
    public List<DocumentSearchEntity> searchByFileName(Long companyId, String fileName) {
        return searchRepository.findByFileNameContainingAndCompanyId(fileName, companyId);
    }
    
    /**
     * Recherche par code de document
     */
    public List<DocumentSearchEntity> searchByDocumentCode(Long companyId, String documentCode) {
        return searchRepository.findByDocumentCodeContainingAndCompanyId(documentCode, companyId);
    }
    
    /**
     * Recherche par module de référence
     */
    public List<DocumentSearchEntity> searchByModuleReference(Long companyId, String moduleReference) {
        return searchRepository.findByModuleReferenceAndCompanyId(moduleReference, companyId);
    }
    
    /**
     * Recherche par type d'entité de référence
     */
    public List<DocumentSearchEntity> searchByEntityReferenceType(Long companyId, String entityReferenceType) {
        return searchRepository.findByEntityReferenceTypeAndCompanyId(entityReferenceType, companyId);
    }
    
    /**
     * Recherche par ID d'entité de référence
     */
    public List<DocumentSearchEntity> searchByEntityReferenceId(Long companyId, Long entityReferenceId) {
        return searchRepository.findByEntityReferenceIdAndCompanyId(entityReferenceId, companyId);
    }
    
    /**
     * Recherche par écriture comptable
     */
    public List<DocumentSearchEntity> searchByEcritureId(Long companyId, Long ecritureId) {
        return searchRepository.findByEcritureIdAndCompanyId(ecritureId, companyId);
    }
    
    /**
     * Recherche de documents archivés
     */
    public List<DocumentSearchEntity> searchArchivedDocuments(Long companyId, Boolean isArchived) {
        return searchRepository.findByIsArchivedAndCompanyId(isArchived, companyId);
    }
    
    /**
     * Recherche de documents expirés
     */
    public List<DocumentSearchEntity> searchExpiredDocuments(Long companyId) {
        return searchRepository.findExpiredDocuments(companyId, LocalDateTime.now());
    }
    
    /**
     * Recherche de documents expirant bientôt
     */
    public List<DocumentSearchEntity> searchDocumentsExpiringSoon(Long companyId, int daysAhead) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(daysAhead);
        return searchRepository.findDocumentsExpiringSoon(companyId, now, future);
    }
    
    /**
     * Recherche par version actuelle
     */
    public List<DocumentSearchEntity> searchCurrentVersions(Long companyId, Boolean isCurrentVersion) {
        return searchRepository.findByIsCurrentVersionAndCompanyId(isCurrentVersion, companyId);
    }
    
    /**
     * Recherche par taille de fichier
     */
    public List<DocumentSearchEntity> searchByFileSize(Long companyId, Long minSize, Long maxSize) {
        return searchRepository.findByFileSizeBetweenAndCompanyId(companyId, minSize, maxSize);
    }
    
    /**
     * Recherche par type MIME
     */
    public List<DocumentSearchEntity> searchByMimeType(Long companyId, String mimeType) {
        return searchRepository.findByMimeTypeAndCompanyId(mimeType, companyId);
    }
    
    /**
     * Recherche par type de fichier
     */
    public List<DocumentSearchEntity> searchByFileType(Long companyId, String fileType) {
        return searchRepository.findByFileTypeAndCompanyId(fileType, companyId);
    }
    
    /**
     * Recherche par pays et standard comptable
     */
    public List<DocumentSearchEntity> searchByCountryAndAccountingStandard(Long companyId, String countryCode, String accountingStandard) {
        return searchRepository.findByCountryCodeAndAccountingStandardAndCompanyId(countryCode, accountingStandard, companyId);
    }
    
    /**
     * Recherche complexe avec plusieurs critères
     */
    public List<DocumentSearchEntity> complexSearch(Long companyId, String documentType, String status, String securityLevel, LocalDateTime startDate, LocalDateTime endDate) {
        return searchRepository.findComplexSearch(companyId, documentType, status, securityLevel, startDate, endDate);
    }
    
    /**
     * Recherche par métadonnées
     */
    public List<DocumentSearchEntity> searchByMetadata(Long companyId, String metadataKey, String metadataValue) {
        return searchRepository.findByMetadataKeyAndValue(companyId, metadataKey, metadataValue);
    }
    
    /**
     * Recherche par contenu de métadonnées
     */
    public List<DocumentSearchEntity> searchByMetadataValue(Long companyId, String metadataValue) {
        return searchRepository.findByMetadataValueContaining(companyId, metadataValue);
    }
    
    /**
     * Recherche de documents récents
     */
    public List<DocumentSearchEntity> searchRecentDocuments(Long companyId, int daysBack) {
        LocalDateTime since = LocalDateTime.now().minusDays(daysBack);
        return searchRepository.findRecentDocuments(companyId, since);
    }
    
    /**
     * Recherche de documents modifiés récemment
     */
    public List<DocumentSearchEntity> searchRecentlyUpdatedDocuments(Long companyId, int daysBack) {
        LocalDateTime since = LocalDateTime.now().minusDays(daysBack);
        return searchRepository.findRecentlyUpdatedDocuments(companyId, since);
    }
    
    /**
     * Recherche par utilisateur qui a approuvé
     */
    public List<DocumentSearchEntity> searchByApprover(Long companyId, Long approvedBy) {
        return searchRepository.findByApprovedByAndCompanyId(approvedBy, companyId);
    }
    
    /**
     * Recherche par période d'approbation
     */
    public List<DocumentSearchEntity> searchByApprovalPeriod(Long companyId, LocalDateTime startDate, LocalDateTime endDate) {
        return searchRepository.findByApprovedAtBetweenAndCompanyId(companyId, startDate, endDate);
    }
    
    /**
     * Recherche par checksum (pour détecter les doublons)
     */
    public List<DocumentSearchEntity> searchByChecksum(Long companyId, String checksum) {
        return searchRepository.findByChecksumAndCompanyId(checksum, companyId);
    }
    
    /**
     * Recherche de documents chiffrés
     */
    public List<DocumentSearchEntity> searchEncryptedDocuments(Long companyId, Boolean isEncrypted) {
        return searchRepository.findByIsEncryptedAndCompanyId(isEncrypted, companyId);
    }
    
    /**
     * Recherche par période de rétention
     */
    public List<DocumentSearchEntity> searchByRetentionPeriod(Long companyId, Integer minYears, Integer maxYears) {
        return searchRepository.findByRetentionPeriodBetweenAndCompanyId(companyId, minYears, maxYears);
    }
    
    // ==================== UTILITAIRES ====================
    
    /**
     * Convertit un GedDocument en DocumentSearchEntity
     */
    private DocumentSearchEntity convertToSearchEntity(GedDocument document) {
        DocumentSearchEntity searchEntity = new DocumentSearchEntity();
        
        searchEntity.setId(document.getId().toString());
        searchEntity.setDocumentId(document.getId());
        searchEntity.setTitle(document.getTitle());
        searchEntity.setDescription(document.getDescription());
        searchEntity.setDocumentCode(document.getDocumentCode());
        searchEntity.setFileName(document.getFileName());
        searchEntity.setFilePath(document.getFilePath());
        searchEntity.setFileSize(document.getFileSize());
        searchEntity.setFileType(document.getFileType());
        searchEntity.setMimeType(document.getMimeType());
        searchEntity.setDocumentType(document.getDocumentType().toString());
        searchEntity.setCategory(document.getCategory());
        searchEntity.setTags(document.getTags());
        searchEntity.setVersion(document.getVersion());
        searchEntity.setIsCurrentVersion(document.getIsCurrentVersion());
        searchEntity.setStatus(document.getStatus().toString());
        searchEntity.setSecurityLevel(document.getSecurityLevel().toString());
        searchEntity.setExpiryDate(document.getExpiryDate() != null ? document.getExpiryDate().atStartOfDay() : null);
        searchEntity.setIsArchived(document.getIsArchived());
        searchEntity.setArchiveDate(document.getArchiveDate() != null ? document.getArchiveDate().atStartOfDay() : null);
        searchEntity.setRetentionPeriodYears(document.getRetentionPeriodYears());
        searchEntity.setModuleReference(document.getModuleReference());
        searchEntity.setEntityReferenceId(document.getEntityReferenceId());
        searchEntity.setEntityReferenceType(document.getEntityReferenceType());
        searchEntity.setEcritureId(document.getEcritureId());
        searchEntity.setCompanyId(document.getCompanyId());
        searchEntity.setCountryCode(document.getCountryCode());
        searchEntity.setAccountingStandard(document.getAccountingStandard());
        searchEntity.setCreatedAt(document.getCreatedAt());
        searchEntity.setUpdatedAt(document.getUpdatedAt());
        searchEntity.setCreatedBy(document.getCreatedBy());
        searchEntity.setUpdatedBy(document.getUpdatedBy());
        searchEntity.setApprovedBy(document.getApprovedBy());
        searchEntity.setApprovedAt(document.getApprovedAt());
        searchEntity.setChecksum(document.getChecksum());
        searchEntity.setIsEncrypted(document.getIsEncrypted());
        
        // Créer le contenu recherchable
        StringBuilder searchableContent = new StringBuilder();
        searchableContent.append(document.getTitle()).append(" ");
        if (document.getDescription() != null) {
            searchableContent.append(document.getDescription()).append(" ");
        }
        if (document.getTags() != null) {
            searchableContent.append(document.getTags()).append(" ");
        }
        if (document.getCategory() != null) {
            searchableContent.append(document.getCategory()).append(" ");
        }
        if (document.getDocumentCode() != null) {
            searchableContent.append(document.getDocumentCode()).append(" ");
        }
        if (document.getFileName() != null) {
            searchableContent.append(document.getFileName()).append(" ");
        }
        
        searchEntity.setSearchableContent(searchableContent.toString());
        
        return searchEntity;
    }
    
    /**
     * Récupère un document depuis la base de données par son ID Elasticsearch
     */
    public Optional<GedDocument> getDocumentById(String elasticsearchId) {
        try {
            Long documentId = Long.parseLong(elasticsearchId);
            return documentRepository.findById(documentId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Reconstruit l'index complet
     */
    public void rebuildIndex() {
        try {
            // Supprimer tous les documents de l'index
            searchRepository.deleteAll();
            
            // Récupérer tous les documents de la base de données
            List<GedDocument> allDocuments = documentRepository.findAll();
            
            // Les indexer dans Elasticsearch
            indexDocuments(allDocuments);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la reconstruction de l'index: " + e.getMessage());
        }
    }
    
    /**
     * Vérifie la santé de l'index
     */
    public boolean isIndexHealthy() {
        try {
            long count = searchRepository.count();
            return count >= 0; // Si on peut compter, l'index est accessible
        } catch (Exception e) {
            return false;
        }
    }
}


