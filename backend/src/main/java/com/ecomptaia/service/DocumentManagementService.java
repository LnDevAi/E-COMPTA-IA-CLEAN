package com.ecomptaia.service;

import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.entity.DocumentVersion;
import com.ecomptaia.entity.DocumentWorkflow;
import com.ecomptaia.repository.GedDocumentRepository;
import com.ecomptaia.repository.DocumentVersionRepository;
import com.ecomptaia.repository.DocumentWorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion électronique des documents
 */
@Service
@Transactional
public class DocumentManagementService {

    @Autowired
    private GedDocumentRepository gedDocumentRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    private DocumentWorkflowRepository documentWorkflowRepository;

    /**
     * Uploader un document
     */
    public GedDocument uploadDocument(String fileName, String fileType, String description, 
                                    Long companyId, Long userId, MultipartFile file) throws IOException {
        GedDocument document = new GedDocument();
        document.setFileName(fileName);
        document.setFileType(fileType);
        document.setDescription(description);
        document.setCompanyId(companyId);
        document.setUploadedBy(userId);
        document.setFileSize(file.getSize());
        document.setUploadDate(LocalDateTime.now());
        document.setIsActive(true);
        document.setStatus(GedDocument.DocumentStatus.DRAFT);
        
        // Simuler le stockage du fichier (en production, utiliser un service de stockage)
        document.setFilePath("/documents/" + companyId + "/" + fileName);
        
        GedDocument savedDocument = gedDocumentRepository.save(document);
        
        // Créer la première version
        createDocumentVersion(savedDocument.getId(), fileName, "Version initiale", userId);
        
        return savedDocument;
    }

    /**
     * Créer une nouvelle version d'un document
     */
    public DocumentVersion createDocumentVersion(Long documentId, String versionName, 
                                               String description, Long userId) {
        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(documentId);
        version.setVersionName(versionName);
        version.setDescription(description);
        version.setCreatedBy(userId);
        version.setCreatedAt(LocalDateTime.now());
        version.setIsActive(true);
        
        return documentVersionRepository.save(version);
    }

    /**
     * Créer un workflow d'approbation pour un document
     */
    public DocumentWorkflow createDocumentWorkflow(Long documentId, String workflowName, 
                                                  String description, Long companyId) {
        DocumentWorkflow workflow = new DocumentWorkflow();
        workflow.setDocumentId(documentId);
        workflow.setWorkflowName(workflowName);
        workflow.setDescription(description);
        workflow.setCompanyId(companyId);
        workflow.setStatus("PENDING");
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setIsActive(true);
        
        return documentWorkflowRepository.save(workflow);
    }

    /**
     * Approuver un document
     */
    public DocumentWorkflow approveDocument(Long workflowId, Long approverId, String comments) {
        Optional<DocumentWorkflow> workflowOpt = documentWorkflowRepository.findById(workflowId);
        if (workflowOpt.isPresent()) {
            DocumentWorkflow workflow = workflowOpt.get();
            workflow.setStatus("APPROVED");
            workflow.setApprovedBy(approverId);
            workflow.setApprovalDate(LocalDateTime.now());
            workflow.setComments(comments);
            workflow.setUpdatedAt(LocalDateTime.now());
            return documentWorkflowRepository.save(workflow);
        }
        return null;
    }

    /**
     * Rejeter un document
     */
    public DocumentWorkflow rejectDocument(Long workflowId, Long approverId, String reason) {
        Optional<DocumentWorkflow> workflowOpt = documentWorkflowRepository.findById(workflowId);
        if (workflowOpt.isPresent()) {
            DocumentWorkflow workflow = workflowOpt.get();
            workflow.setStatus("REJECTED");
            workflow.setApprovedBy(approverId);
            workflow.setApprovalDate(LocalDateTime.now());
            workflow.setComments(reason);
            workflow.setUpdatedAt(LocalDateTime.now());
            return documentWorkflowRepository.save(workflow);
        }
        return null;
    }

    /**
     * Obtenir tous les documents d'une entreprise
     */
    public List<GedDocument> getCompanyDocuments(Long companyId) {
        return gedDocumentRepository.findByCompanyIdAndIsActiveTrueOrderByUploadDateDesc(companyId);
    }

    /**
     * Obtenir les versions d'un document
     */
    public List<DocumentVersion> getDocumentVersions(Long documentId) {
        return documentVersionRepository.findByDocumentIdAndIsActiveTrueOrderByCreatedAtDesc(documentId);
    }

    /**
     * Obtenir les workflows d'un document
     */
    public List<DocumentWorkflow> getDocumentWorkflows(Long documentId) {
        return documentWorkflowRepository.findByDocumentIdAndIsActiveTrueOrderByCreatedAtDesc(documentId);
    }

    /**
     * Rechercher des documents par nom
     */
    public List<GedDocument> searchDocumentsByName(String fileName, Long companyId) {
        return gedDocumentRepository.findByFileNameContainingIgnoreCaseAndCompanyIdAndIsActiveTrue(fileName, companyId);
    }

    /**
     * Rechercher des documents par type
     */
    public List<GedDocument> searchDocumentsByType(String fileType, Long companyId) {
        return gedDocumentRepository.findByFileTypeAndCompanyIdAndIsActiveTrue(fileType, companyId);
    }

    /**
     * Supprimer un document (soft delete)
     */
    public void deleteDocument(Long documentId) {
        Optional<GedDocument> documentOpt = gedDocumentRepository.findById(documentId);
        if (documentOpt.isPresent()) {
            GedDocument document = documentOpt.get();
            document.setIsActive(false);
            document.setDeletedAt(LocalDateTime.now());
            gedDocumentRepository.save(document);
        }
    }

    /**
     * Mettre à jour les métadonnées d'un document
     */
    public GedDocument updateDocumentMetadata(Long documentId, String fileName, String description) {
        Optional<GedDocument> documentOpt = gedDocumentRepository.findById(documentId);
        if (documentOpt.isPresent()) {
            GedDocument document = documentOpt.get();
            document.setFileName(fileName);
            document.setDescription(description);
            document.setUpdatedAt(LocalDateTime.now());
            return gedDocumentRepository.save(document);
        }
        return null;
    }

    /**
     * Obtenir les statistiques des documents
     */
    public Object getDocumentStatistics(Long companyId) {
        List<GedDocument> documents = getCompanyDocuments(companyId);
        List<DocumentWorkflow> workflows = documentWorkflowRepository.findByCompanyIdAndIsActiveTrue(companyId);
        
        long totalDocs = documents.size();
        long pendingWf = workflows.stream().filter(w -> "PENDING".equals(w.getStatus())).count();
        long approvedWf = workflows.stream().filter(w -> "APPROVED".equals(w.getStatus())).count();
        long rejectedWf = workflows.stream().filter(w -> "REJECTED".equals(w.getStatus())).count();
        
        return new Object() {
            public final long totalDocuments = totalDocs;
            public final long pendingWorkflows = pendingWf;
            public final long approvedWorkflows = approvedWf;
            public final long rejectedWorkflows = rejectedWf;
            public final LocalDateTime lastUpdate = LocalDateTime.now();
            
            @Override
            public String toString() {
                return String.format("DocumentStats{docs=%d, pending=%d, approved=%d, rejected=%d, updated=%s}", 
                    totalDocuments, pendingWorkflows, approvedWorkflows, rejectedWorkflows, lastUpdate);
            }
        };
    }

    /**
     * Récupérer tous les documents
     */
    public List<GedDocument> findAll() {
        return gedDocumentRepository.findAll();
    }

    /**
     * Sauvegarder un document
     */
    public GedDocument save(GedDocument document) {
        return gedDocumentRepository.save(document);
    }
}