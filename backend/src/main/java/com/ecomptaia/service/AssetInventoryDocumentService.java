package com.ecomptaia.service;

import com.ecomptaia.entity.AssetInventoryDocument;
import com.ecomptaia.repository.AssetInventoryDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AssetInventoryDocumentService {

    @Autowired
    private AssetInventoryDocumentRepository documentRepository;

    /**
     * Créer un document pour une opération d'immobilisation ou de stock
     */
    public AssetInventoryDocument createDocument(Long companyId, String documentType, String title, 
                                               String relatedEntityType, Long relatedEntityId, 
                                               String relatedEntityCode, String countryCode, 
                                               String accountingStandard) {
        try {
            String documentNumber = "DOC_" + System.currentTimeMillis();
            AssetInventoryDocument document = new AssetInventoryDocument(
                companyId, documentNumber, documentType, title, relatedEntityType, 
                countryCode, accountingStandard
            );
            document.setRelatedEntityId(relatedEntityId);
            document.setRelatedEntityCode(relatedEntityCode);
            document.setDocumentDate(LocalDateTime.now());
            
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du document: " + e.getMessage());
        }
    }

    /**
     * Associer un fichier à un document
     */
    public AssetInventoryDocument attachFileToDocument(Long documentId, String filePath, String fileType, 
                                                     Long fileSize, String originalFileName) {
        try {
            AssetInventoryDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
            
            document.setFilePath(filePath);
            document.setFileType(fileType);
            document.setFileSize(fileSize);
            document.setOriginalFileName(originalFileName);
            document.setUpdatedAt(LocalDateTime.now());
            
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'attachement du fichier: " + e.getMessage());
        }
    }

    /**
     * Valider un document
     */
    public AssetInventoryDocument validateDocument(Long documentId, Long validatedBy) {
        try {
            AssetInventoryDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
            
            document.setStatus("VALIDATED");
            document.setValidatedBy(validatedBy);
            document.setValidatedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());
            
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la validation du document: " + e.getMessage());
        }
    }

    /**
     * Archiver un document
     */
    public AssetInventoryDocument archiveDocument(Long documentId, Long archivedBy) {
        try {
            AssetInventoryDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
            
            document.setIsArchived(true);
            document.setArchivedBy(archivedBy);
            document.setArchivedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());
            
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'archivage du document: " + e.getMessage());
        }
    }

    /**
     * Récupérer tous les documents d'une entreprise
     */
    public List<AssetInventoryDocument> getDocumentsByCompany(Long companyId) {
        return documentRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    /**
     * Récupérer les documents par type
     */
    public List<AssetInventoryDocument> getDocumentsByType(Long companyId, String documentType) {
        return documentRepository.findByCompanyIdAndDocumentTypeOrderByCreatedAtDesc(companyId, documentType);
    }

    /**
     * Récupérer les documents par entité liée
     */
    public List<AssetInventoryDocument> getDocumentsByRelatedEntity(Long companyId, Long entityId, String entityType) {
        return documentRepository.findByRelatedEntity(companyId, entityId, entityType);
    }

    /**
     * Récupérer les documents par code d'entité
     */
    public List<AssetInventoryDocument> getDocumentsByRelatedEntityCode(Long companyId, String entityCode) {
        return documentRepository.findByRelatedEntityCode(companyId, entityCode);
    }

    /**
     * Récupérer les documents non réconciliés
     */
    public List<AssetInventoryDocument> getUnreconciledDocuments(Long companyId) {
        return documentRepository.findUnreconciledDocumentsByCompany(companyId);
    }

    /**
     * Récupérer les documents réconciliés
     */
    public List<AssetInventoryDocument> getReconciledDocuments(Long companyId) {
        return documentRepository.findReconciledDocumentsByCompany(companyId);
    }

    /**
     * Récupérer les documents actifs
     */
    public List<AssetInventoryDocument> getActiveDocuments(Long companyId) {
        return documentRepository.findActiveDocumentsByCompany(companyId);
    }

    /**
     * Récupérer les documents archivés
     */
    public List<AssetInventoryDocument> getArchivedDocuments(Long companyId) {
        return documentRepository.findArchivedDocumentsByCompany(companyId);
    }

    /**
     * Récupérer les documents avec écritures comptables
     */
    public List<AssetInventoryDocument> getDocumentsWithAccountingEntries(Long companyId) {
        return documentRepository.findDocumentsWithAccountingEntries(companyId);
    }

    /**
     * Obtenir les statistiques des documents
     */
    public Map<String, Object> getDocumentStatistics(Long companyId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Statistiques par type de document
            long assetPurchaseDocs = documentRepository.countByDocumentType(companyId, "ASSET_PURCHASE");
            long assetMaintenanceDocs = documentRepository.countByDocumentType(companyId, "ASSET_MAINTENANCE");
            long inventoryReceiptDocs = documentRepository.countByDocumentType(companyId, "INVENTORY_RECEIPT");
            long inventoryIssueDocs = documentRepository.countByDocumentType(companyId, "INVENTORY_ISSUE");
            long inventoryTransferDocs = documentRepository.countByDocumentType(companyId, "INVENTORY_TRANSFER");
            long inventoryAdjustmentDocs = documentRepository.countByDocumentType(companyId, "INVENTORY_ADJUSTMENT");

            stats.put("byType", Map.of(
                "assetPurchase", assetPurchaseDocs,
                "assetMaintenance", assetMaintenanceDocs,
                "inventoryReceipt", inventoryReceiptDocs,
                "inventoryIssue", inventoryIssueDocs,
                "inventoryTransfer", inventoryTransferDocs,
                "inventoryAdjustment", inventoryAdjustmentDocs
            ));

            // Statistiques par statut
            long validatedDocs = documentRepository.countValidatedDocumentsByCompany(companyId);
            long draftDocs = documentRepository.countDraftDocumentsByCompany(companyId);

            stats.put("byStatus", Map.of(
                "validated", validatedDocs,
                "draft", draftDocs
            ));

            return stats;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul des statistiques des documents: " + e.getMessage());
        }
    }
}





