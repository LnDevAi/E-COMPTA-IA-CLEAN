package com.ecomptaia.service;

import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.entity.DocumentWorkflow;
import com.ecomptaia.entity.DocumentApproval;
import com.ecomptaia.repository.DocumentRepository;
import com.ecomptaia.repository.DocumentWorkflowRepository;
import com.ecomptaia.repository.DocumentApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentManagementService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentWorkflowRepository workflowRepository;

    @Autowired
    private DocumentApprovalRepository approvalRepository;

    // ==================== GESTION DES DOCUMENTS ====================

    /**
     * Upload et création d'un nouveau document
     */
    public Map<String, Object> uploadDocument(MultipartFile file, String title, String description,
                                            GedDocument.DocumentType documentType, String category, String tags,
                                            GedDocument.SecurityLevel securityLevel, Long companyId, String countryCode,
                                            String accountingStandard, Long createdBy, String moduleReference,
                                            Long entityReferenceId, String entityReferenceType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Générer un code unique pour le document
            String documentCode = generateDocumentCode(documentType, companyId);
            
            // Déterminer le chemin de stockage
            String uploadDir = "uploads/documents/" + companyId + "/" + documentType.toString().toLowerCase();
            Path uploadPath = Paths.get(uploadDir);
            
            // Créer le répertoire s'il n'existe pas
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Générer un nom de fichier unique
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = documentCode + "_" + System.currentTimeMillis() + fileExtension;
            String filePath = uploadPath.resolve(fileName).toString();
            
            // Sauvegarder le fichier
            Files.copy(file.getInputStream(), Paths.get(filePath));
            
            // Calculer le checksum
            String checksum = calculateChecksum(file.getBytes());
            
            // Créer l'entité Document
            GedDocument document = new GedDocument();
            document.setDocumentCode(documentCode);
            document.setTitle(title);
            document.setDescription(description);
            document.setFileName(fileName);
            document.setFilePath(filePath);
            document.setFileSize(file.getSize());
            document.setFileType(fileExtension.substring(1));
            document.setMimeType(file.getContentType());
            document.setDocumentType(documentType);
            document.setCategory(category);
            document.setTags(tags);
            document.setSecurityLevel(securityLevel);
            document.setModuleReference(moduleReference);
            document.setEntityReferenceId(entityReferenceId);
            document.setEntityReferenceType(entityReferenceType);
            document.setCompanyId(companyId);
            document.setCountryCode(countryCode);
            document.setAccountingStandard(accountingStandard);
            document.setCreatedBy(createdBy);
            document.setChecksum(checksum);
            
            // Sauvegarder le document
            GedDocument savedDocument = documentRepository.save(document);
            
            // Appliquer le workflow d'approbation si nécessaire
            applyDocumentWorkflow(savedDocument);
            
            response.put("success", true);
            response.put("message", "Document uploadé avec succès");
            response.put("document", savedDocument);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'upload: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * Obtenir un document par ID
     */
    public GedDocument getDocumentById(Long documentId, Long companyId) {
        return documentRepository.findById(documentId)
                .filter(doc -> doc.getCompanyId().equals(companyId))
                .orElse(null);
    }

    /**
     * Obtenir tous les documents d'une entreprise
     */
    public List<GedDocument> getAllDocuments(Long companyId) {
        return documentRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    /**
     * Rechercher des documents
     */
    public List<GedDocument> searchDocuments(Long companyId, String searchTerm) {
        return documentRepository.searchDocuments(companyId, searchTerm);
    }

    /**
     * Filtrer les documents
     */
    public List<GedDocument> filterDocuments(Long companyId, GedDocument.DocumentType documentType,
                                        GedDocument.DocumentStatus status, GedDocument.SecurityLevel securityLevel,
                                        String category, String moduleReference) {
        return documentRepository.findDocumentsWithFilters(companyId, documentType, status, securityLevel, category, moduleReference);
    }

    /**
     * Mettre à jour un document
     */
    public Map<String, Object> updateDocument(Long documentId, String title, String description,
                                            String category, String tags, GedDocument.SecurityLevel securityLevel,
                                            Long companyId, Long updatedBy) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            GedDocument document = getDocumentById(documentId, companyId);
            if (document == null) {
                response.put("success", false);
                response.put("message", "Document non trouvé");
                return response;
            }
            
            document.setTitle(title);
            document.setDescription(description);
            document.setCategory(category);
            document.setTags(tags);
            document.setSecurityLevel(securityLevel);
            document.setUpdatedBy(updatedBy);
            document.setUpdatedAt(LocalDateTime.now());
            
            GedDocument updatedDocument = documentRepository.save(document);
            
            response.put("success", true);
            response.put("message", "Document mis à jour avec succès");
            response.put("document", updatedDocument);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * Supprimer un document (logique)
     */
    public Map<String, Object> deleteDocument(Long documentId, Long companyId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            GedDocument document = getDocumentById(documentId, companyId);
            if (document == null) {
                response.put("success", false);
                response.put("message", "Document non trouvé");
                return response;
            }
            
            document.setStatus(GedDocument.DocumentStatus.DELETED);
            document.setUpdatedAt(LocalDateTime.now());
            
            documentRepository.save(document);
            
            response.put("success", true);
            response.put("message", "Document supprimé avec succès");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
        }
        
        return response;
    }

    // ==================== GESTION DES WORKFLOWS ====================

    /**
     * Créer un workflow de document
     */
    public Map<String, Object> createWorkflow(String workflowCode, String workflowName, String description,
                                            GedDocument.DocumentType documentType, Boolean requiresApproval,
                                            Integer approvalLevels, Boolean autoApprove, Boolean autoArchive,
                                            Integer archiveDays, Integer retentionYears, Long companyId,
                                            String countryCode, String accountingStandard, Long createdBy) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DocumentWorkflow workflow = new DocumentWorkflow();
            workflow.setWorkflowCode(workflowCode);
            workflow.setWorkflowName(workflowName);
            workflow.setDescription(description);
            workflow.setDocumentType(documentType);
            workflow.setRequiresApproval(requiresApproval);
            workflow.setApprovalLevels(approvalLevels);
            workflow.setAutoApprove(autoApprove);
            workflow.setAutoArchive(autoArchive);
            workflow.setArchiveDays(archiveDays);
            workflow.setRetentionYears(retentionYears);
            workflow.setCompanyId(companyId);
            workflow.setCountryCode(countryCode);
            workflow.setAccountingStandard(accountingStandard);
            workflow.setCreatedBy(createdBy);
            
            DocumentWorkflow savedWorkflow = workflowRepository.save(workflow);
            
            response.put("success", true);
            response.put("message", "Workflow créé avec succès");
            response.put("workflow", savedWorkflow);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création du workflow: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * Obtenir tous les workflows d'une entreprise
     */
    public List<DocumentWorkflow> getAllWorkflows(Long companyId) {
        return workflowRepository.findByCompanyIdOrderByWorkflowNameAsc(companyId);
    }

    /**
     * Obtenir un workflow par code
     */
    public DocumentWorkflow getWorkflowByCode(String workflowCode, Long companyId) {
        return workflowRepository.findByCompanyIdAndWorkflowCode(companyId, workflowCode).orElse(null);
    }

    // ==================== GESTION DES APPROBATIONS ====================

    /**
     * Appliquer le workflow d'approbation à un document
     */
    private void applyDocumentWorkflow(GedDocument document) {
        DocumentWorkflow workflow = getWorkflowByCode(document.getDocumentType().toString(), document.getCompanyId());
        
        if (workflow != null && workflow.getRequiresApproval()) {
            // Créer les approbations nécessaires
            for (int level = 1; level <= workflow.getApprovalLevels(); level++) {
                DocumentApproval approval = new DocumentApproval();
                approval.setDocumentId(document.getId());
                approval.setApprovalLevel(level);
                approval.setApproverId(1L); // ID par défaut, à adapter selon la logique métier
                approval.setApproverName("Approbateur " + level);
                approval.setCompanyId(document.getCompanyId());
                approval.setCountryCode(document.getCountryCode());
                approval.setAccountingStandard(document.getAccountingStandard());
                approval.setDueDate(LocalDateTime.now().plusDays(7)); // Échéance par défaut
                
                approvalRepository.save(approval);
            }
            
            document.setStatus(GedDocument.DocumentStatus.PENDING_APPROVAL);
            documentRepository.save(document);
        } else if (workflow != null && workflow.getAutoApprove()) {
            document.setStatus(GedDocument.DocumentStatus.APPROVED);
            document.setApprovedBy(document.getCreatedBy());
            document.setApprovedAt(LocalDateTime.now());
            documentRepository.save(document);
        }
    }

    /**
     * Approuver un document
     */
    public Map<String, Object> approveDocument(Long documentId, Long approverId, String comments, Long companyId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            GedDocument document = getDocumentById(documentId, companyId);
            if (document == null) {
                response.put("success", false);
                response.put("message", "Document non trouvé");
                return response;
            }
            
            // Trouver l'approbation en cours
            List<DocumentApproval> approvals = approvalRepository.findByDocumentIdOrderByApprovalLevelAsc(documentId);
            DocumentApproval currentApproval = approvals.stream()
                    .filter(a -> a.getApprovalStatus() == DocumentApproval.ApprovalStatus.PENDING)
                    .findFirst()
                    .orElse(null);
            
            if (currentApproval == null) {
                response.put("success", false);
                response.put("message", "Aucune approbation en attente");
                return response;
            }
            
            // Marquer l'approbation comme approuvée
            currentApproval.setApprovalStatus(DocumentApproval.ApprovalStatus.APPROVED);
            currentApproval.setApprovalDate(LocalDateTime.now());
            currentApproval.setComments(comments);
            approvalRepository.save(currentApproval);
            
            // Vérifier si toutes les approbations sont terminées
            boolean allApproved = approvals.stream()
                    .allMatch(a -> a.getApprovalStatus() == DocumentApproval.ApprovalStatus.APPROVED);
            
            if (allApproved) {
                document.setStatus(GedDocument.DocumentStatus.APPROVED);
                document.setApprovedBy(approverId);
                document.setApprovedAt(LocalDateTime.now());
                documentRepository.save(document);
            }
            
            response.put("success", true);
            response.put("message", "Document approuvé avec succès");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'approbation: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * Rejeter un document
     */
    public Map<String, Object> rejectDocument(Long documentId, Long approverId, String comments, Long companyId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            GedDocument document = getDocumentById(documentId, companyId);
            if (document == null) {
                response.put("success", false);
                response.put("message", "Document non trouvé");
                return response;
            }
            
            // Marquer toutes les approbations comme rejetées
            List<DocumentApproval> approvals = approvalRepository.findByDocumentIdOrderByApprovalLevelAsc(documentId);
            for (DocumentApproval approval : approvals) {
                if (approval.getApprovalStatus() == DocumentApproval.ApprovalStatus.PENDING) {
                    approval.setApprovalStatus(DocumentApproval.ApprovalStatus.REJECTED);
                    approval.setApprovalDate(LocalDateTime.now());
                    approval.setComments(comments);
                    approvalRepository.save(approval);
                }
            }
            
            document.setStatus(GedDocument.DocumentStatus.REJECTED);
            documentRepository.save(document);
            
            response.put("success", true);
            response.put("message", "Document rejeté");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du rejet: " + e.getMessage());
        }
        
        return response;
    }

    // ==================== FONCTIONS UTILITAIRES ====================

    /**
     * Générer un code de document unique
     */
    private String generateDocumentCode(GedDocument.DocumentType documentType, Long companyId) {
        String prefix = documentType.toString().substring(0, 3);
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + "_" + companyId + "_" + timestamp;
    }

    /**
     * Calculer le checksum d'un fichier
     */
    private String calculateChecksum(byte[] fileBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(fileBytes);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Obtenir les statistiques des documents
     */
    public Map<String, Object> getDocumentStats(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques par type de document
        List<Object[]> typeStats = documentRepository.getDocumentTypeStats(companyId);
        Map<String, Long> typeCount = new HashMap<>();
        for (Object[] stat : typeStats) {
            @SuppressWarnings("unchecked")
            Long count = (Long) stat[1];
            typeCount.put(stat[0].toString(), count);
        }
        stats.put("byType", typeCount);
        
        // Statistiques par statut
        List<Object[]> statusStats = documentRepository.getDocumentStatusStats(companyId);
        Map<String, Long> statusCount = new HashMap<>();
        for (Object[] stat : statusStats) {
            @SuppressWarnings("unchecked")
            Long count = (Long) stat[1];
            statusCount.put(stat[0].toString(), count);
        }
        stats.put("byStatus", statusCount);
        
        // Statistiques par niveau de sécurité
        List<Object[]> securityStats = documentRepository.getSecurityLevelStats(companyId);
        Map<String, Long> securityCount = new HashMap<>();
        for (Object[] stat : securityStats) {
            @SuppressWarnings("unchecked")
            Long count = (Long) stat[1];
            securityCount.put(stat[0].toString(), count);
        }
        stats.put("bySecurityLevel", securityCount);
        
        // Statistiques par catégorie
        List<Object[]> categoryStats = documentRepository.getCategoryStats(companyId);
        Map<String, Long> categoryCount = new HashMap<>();
        for (Object[] stat : categoryStats) {
            @SuppressWarnings("unchecked")
            Long count = (Long) stat[1];
            categoryCount.put(stat[0].toString(), count);
        }
        stats.put("byCategory", categoryCount);
        
        return stats;
    }

    /**
     * Obtenir les documents expirés
     */
    public List<GedDocument> getExpiredDocuments(Long companyId) {
        return documentRepository.findExpiredDocuments(companyId, LocalDate.now());
    }

    /**
     * Obtenir les documents à archiver
     */
    public List<GedDocument> getDocumentsToArchive(Long companyId) {
        // Méthode simplifiée pour les tests
        return documentRepository.findAll();
    }

    // ==================== MÉTHODES SIMPLIFIÉES POUR LES TESTS ====================

    /**
     * Récupérer tous les documents (pour les tests)
     */
    public List<GedDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * Créer un document (version simplifiée pour les tests)
     */
    public GedDocument createDocument(GedDocument document) {
        return documentRepository.save(document);
    }

    /**
     * Mettre à jour un document (version simplifiée pour les tests)
     */
    public GedDocument updateDocument(Long id, GedDocument document) {
        document.setId(id);
        return documentRepository.save(document);
    }

    /**
     * Supprimer un document (version simplifiée pour les tests)
     */
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}


