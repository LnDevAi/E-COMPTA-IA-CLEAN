ackage com.ecomptaia.service;

import com.ecomptaia.entity.DocumentWorkflow;
import com.ecomptaia.entity.DocumentApproval;
import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.repository.DocumentWorkflowRepository;
import com.ecomptaia.repository.DocumentApprovalRepository;
import com.ecomptaia.repository.GedDocumentRepository;
import com.ecomptaia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des workflows d'approbation de documents
 */
@Service
@Transactional
public class WorkflowManagementService {
    
    @Autowired
    private DocumentWorkflowRepository workflowRepository;
    
    @Autowired
    private DocumentApprovalRepository approvalRepository;
    
    @Autowired
    private GedDocumentRepository documentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // ==================== GESTION DES WORKFLOWS ====================
    
    /**
     * Récupère tous les workflows d'une entreprise
     */
    public List<DocumentWorkflow> getWorkflowsByCompany(Long companyId) {
        return workflowRepository.findByCompanyId(companyId);
    }
    
    /**
     * Récupère un workflow par son ID
     */
    public Optional<DocumentWorkflow> getWorkflowById(Long workflowId) {
        return workflowRepository.findById(workflowId);
    }
    
    /**
     * Récupère les workflows actifs par type de document
     */
    public List<DocumentWorkflow> getActiveWorkflowsByDocumentType(Long companyId, GedDocument.DocumentType documentType) {
        List<DocumentWorkflow> workflows = workflowRepository.findByCompanyIdAndDocumentType(companyId, documentType);
        return workflows.stream()
                       .filter(DocumentWorkflow::getIsActive)
                       .collect(Collectors.toList());
    }
    
    /**
     * Crée un nouveau workflow
     */
    public DocumentWorkflow createWorkflow(DocumentWorkflow workflow) {
        workflow.setCreatedAt(LocalDateTime.now());
        return workflowRepository.save(workflow);
    }
    
    /**
     * Met à jour un workflow existant
     */
    public DocumentWorkflow updateWorkflow(Long workflowId, DocumentWorkflow workflowData) {
        DocumentWorkflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow(() -> new RuntimeException("Workflow non trouvé"));
        
        workflow.setWorkflowName(workflowData.getWorkflowName());
        workflow.setDescription(workflowData.getDescription());
        workflow.setDocumentType(workflowData.getDocumentType());
        workflow.setIsActive(workflowData.getIsActive());
        workflow.setRequiresApproval(workflowData.getRequiresApproval());
        workflow.setApprovalLevels(workflowData.getApprovalLevels());
        workflow.setAutoApprove(workflowData.getAutoApprove());
        workflow.setAutoArchive(workflowData.getAutoArchive());
        workflow.setArchiveDays(workflowData.getArchiveDays());
        workflow.setRetentionYears(workflowData.getRetentionYears());
        workflow.setUpdatedAt(LocalDateTime.now());
        workflow.setUpdatedBy(workflowData.getUpdatedBy());
        
        return workflowRepository.save(workflow);
    }
    
    /**
     * Supprime un workflow
     */
    public void deleteWorkflow(Long workflowId) {
        workflowRepository.deleteById(workflowId);
    }
    
    // ==================== GESTION DES APPROBATIONS ====================
    
    /**
     * Récupère les approbations en attente pour un utilisateur
     */
    public List<DocumentApproval> getPendingApprovalsForUser(Long userId, Long companyId) {
        return approvalRepository.findPendingApprovalsByCompanyAndApprover(companyId, userId);
    }
    
    /**
     * Récupère les approbations d'un document
     */
    public List<DocumentApproval> getDocumentApprovals(Long documentId) {
        return approvalRepository.findByDocumentIdOrderByApprovalLevelAsc(documentId);
    }
    
    /**
     * Approuve un document
     */
    public DocumentApproval approveDocument(Long approvalId, String comments, Long approverId) {
        DocumentApproval approval = approvalRepository.findById(approvalId)
            .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));
        
        approval.setApprovalStatus(DocumentApproval.ApprovalStatus.APPROVED);
        approval.setApprovalDate(LocalDateTime.now());
        approval.setComments(comments);
        approval.setUpdatedAt(LocalDateTime.now());
        
        // Vérifier si toutes les approbations sont terminées
        checkAndUpdateDocumentStatus(approval.getDocumentId());
        
        return approvalRepository.save(approval);
    }
    
    /**
     * Rejette un document
     */
    public DocumentApproval rejectDocument(Long approvalId, String comments, Long approverId) {
        DocumentApproval approval = approvalRepository.findById(approvalId)
            .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));
        
        approval.setApprovalStatus(DocumentApproval.ApprovalStatus.REJECTED);
        approval.setApprovalDate(LocalDateTime.now());
        approval.setComments(comments);
        approval.setUpdatedAt(LocalDateTime.now());
        
        // Mettre à jour le statut du document
        updateDocumentStatus(approval.getDocumentId(), GedDocument.DocumentStatus.REJECTED);
        
        return approvalRepository.save(approval);
    }
    
    /**
     * Annule une approbation
     */
    public DocumentApproval cancelApproval(Long approvalId, String comments, Long approverId) {
        DocumentApproval approval = approvalRepository.findById(approvalId)
            .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));
        
        approval.setApprovalStatus(DocumentApproval.ApprovalStatus.CANCELLED);
        approval.setApprovalDate(LocalDateTime.now());
        approval.setComments(comments);
        approval.setUpdatedAt(LocalDateTime.now());
        
        return approvalRepository.save(approval);
    }
    
    // ==================== INITIALISATION DES WORKFLOWS ====================
    
    /**
     * Initialise le workflow d'approbation pour un document
     */
    public void initializeDocumentWorkflow(Long documentId, Long companyId) {
        GedDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document non trouvé"));
        
        // Trouver le workflow approprié pour ce type de document
        List<DocumentWorkflow> workflows = getActiveWorkflowsByDocumentType(companyId, document.getDocumentType());
        
        if (workflows.isEmpty()) {
            throw new RuntimeException("Aucun workflow actif trouvé pour le type de document: " + document.getDocumentType());
        }
        
        DocumentWorkflow workflow = workflows.get(0); // Prendre le premier workflow actif
        
        // Créer les approbations selon les niveaux du workflow
        for (int level = 1; level <= workflow.getApprovalLevels(); level++) {
            DocumentApproval approval = new DocumentApproval();
            approval.setDocumentId(documentId);
            approval.setApprovalLevel(level);
            approval.setApproverId(getApproverForLevel(level, companyId));
            approval.setApproverName(getApproverName(approval.getApproverId()));
            approval.setCompanyId(companyId);
            approval.setCountryCode(document.getCountryCode());
            approval.setAccountingStandard(document.getAccountingStandard());
            approval.setIsRequired(level <= 2); // Les 2 premiers niveaux sont obligatoires
            approval.setDueDate(LocalDateTime.now().plusDays(level * 2)); // Échéance progressive
            
            approvalRepository.save(approval);
        }
        
        // Mettre à jour le statut du document
        updateDocumentStatus(documentId, GedDocument.DocumentStatus.PENDING_APPROVAL);
    }
    
    // ==================== STATISTIQUES ET RAPPORTS ====================
    
    /**
     * Récupère les statistiques des workflows pour une entreprise
     */
    public Map<String, Object> getWorkflowStatistics(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques des approbations
        List<DocumentApproval> allApprovals = approvalRepository.findByCompanyId(companyId);
        
        long totalApprovals = allApprovals.size();
        long pendingApprovals = allApprovals.stream()
            .filter(a -> a.getApprovalStatus() == DocumentApproval.ApprovalStatus.PENDING)
            .count();
        long approvedApprovals = allApprovals.stream()
            .filter(a -> a.getApprovalStatus() == DocumentApproval.ApprovalStatus.APPROVED)
            .count();
        long rejectedApprovals = allApprovals.stream()
            .filter(a -> a.getApprovalStatus() == DocumentApproval.ApprovalStatus.REJECTED)
            .count();
        long overdueApprovals = allApprovals.stream()
            .filter(a -> a.getIsOverdue() != null && a.getIsOverdue())
            .count();
        
        stats.put("totalApprovals", totalApprovals);
        stats.put("pendingApprovals", pendingApprovals);
        stats.put("approvedApprovals", approvedApprovals);
        stats.put("rejectedApprovals", rejectedApprovals);
        stats.put("overdueApprovals", overdueApprovals);
        
        // Statistiques des workflows
        List<DocumentWorkflow> workflows = getWorkflowsByCompany(companyId);
        stats.put("totalWorkflows", workflows.size());
        stats.put("activeWorkflows", workflows.stream().filter(DocumentWorkflow::getIsActive).count());
        
        // Temps moyen de traitement
        double avgProcessingTime = calculateAverageProcessingTime(allApprovals);
        stats.put("averageProcessingTime", avgProcessingTime);
        
        return stats;
    }
    
    /**
     * Récupère les documents en attente d'approbation
     */
    public List<Map<String, Object>> getDocumentsPendingApproval(Long companyId) {
        List<DocumentApproval> pendingApprovals = approvalRepository.findByCompanyIdAndApprovalStatus(
            companyId, DocumentApproval.ApprovalStatus.PENDING);
        
        return pendingApprovals.stream()
            .map(approval -> {
                Map<String, Object> docInfo = new HashMap<>();
                Optional<GedDocument> document = documentRepository.findById(approval.getDocumentId());
                
                if (document.isPresent()) {
                    GedDocument doc = document.get();
                    docInfo.put("documentId", doc.getId());
                    docInfo.put("documentTitle", doc.getTitle());
                    docInfo.put("documentCode", doc.getDocumentCode());
                    docInfo.put("documentType", doc.getDocumentType());
                    docInfo.put("approvalLevel", approval.getApprovalLevel());
                    docInfo.put("approverId", approval.getApproverId());
                    docInfo.put("approverName", approval.getApproverName());
                    docInfo.put("dueDate", approval.getDueDate());
                    docInfo.put("isOverdue", approval.getIsOverdue());
                    docInfo.put("createdAt", approval.getCreatedAt());
                }
                
                return docInfo;
            })
            .collect(Collectors.toList());
    }
    
    // ==================== MÉTHODES PRIVÉES ====================
    
    /**
     * Vérifie et met à jour le statut du document selon les approbations
     */
    private void checkAndUpdateDocumentStatus(Long documentId) {
        List<DocumentApproval> approvals = getDocumentApprovals(documentId);
        
        boolean allApproved = approvals.stream()
            .filter(a -> a.getIsRequired())
            .allMatch(a -> a.getApprovalStatus() == DocumentApproval.ApprovalStatus.APPROVED);
        
        boolean anyRejected = approvals.stream()
            .anyMatch(a -> a.getApprovalStatus() == DocumentApproval.ApprovalStatus.REJECTED);
        
        if (anyRejected) {
            updateDocumentStatus(documentId, GedDocument.DocumentStatus.REJECTED);
        } else if (allApproved) {
            updateDocumentStatus(documentId, GedDocument.DocumentStatus.APPROVED);
        }
    }
    
    /**
     * Met à jour le statut d'un document
     */
    private void updateDocumentStatus(Long documentId, GedDocument.DocumentStatus status) {
        GedDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document non trouvé"));
        
        document.setStatus(status);
        document.setUpdatedAt(LocalDateTime.now());
        
        documentRepository.save(document);
    }
    
    /**
     * Obtient l'approbateur pour un niveau donné
     */
    private Long getApproverForLevel(Integer level, Long companyId) {
        // Logique de sélection des approbateurs basée sur le niveau
        String roleName;
        switch (level) {
            case 1:
                roleName = "COMPTABLE"; // Premier niveau : comptable
                break;
            case 2:
                roleName = "MANAGER_FINANCIER"; // Deuxième niveau : manager financier
                break;
            case 3:
                roleName = "DIRECTEUR"; // Troisième niveau : directeur
                break;
            default:
                roleName = "ADMIN"; // Niveaux supérieurs : admin
                break;
        }
        
        // Récupérer les utilisateurs avec ce rôle dans l'entreprise
        List<com.ecomptaia.entity.User> users = userRepository.findByCompanyIdAndRoleNameAndIsActiveTrue(companyId, roleName);
        
        if (!users.isEmpty()) {
            // Retourner le premier utilisateur trouvé (on pourrait implémenter une logique plus sophistiquée)
            return users.get(0).getId();
        }
        
        // Si aucun utilisateur avec ce rôle, chercher un admin de l'entreprise
        List<com.ecomptaia.entity.User> admins = userRepository.findByCompanyIdAndIsAdminTrue(companyId);
        if (!admins.isEmpty()) {
            return admins.get(0).getId();
        }
        
        // En dernier recours, retourner le premier utilisateur actif de l'entreprise
        List<com.ecomptaia.entity.User> activeUsers = userRepository.findByCompanyIdAndIsActiveTrue(companyId);
        if (!activeUsers.isEmpty()) {
            return activeUsers.get(0).getId();
        }
        
        // Valeur par défaut si aucun utilisateur trouvé
        return 1L;
    }
    
    /**
     * Obtient le nom de l'approbateur
     */
    private String getApproverName(Long approverId) {
        // Récupérer l'utilisateur depuis la base de données
        Optional<com.ecomptaia.entity.User> userOpt = userRepository.findById(approverId);
        
        if (userOpt.isPresent()) {
            com.ecomptaia.entity.User user = userOpt.get();
            return user.getFirstName() + " " + user.getLastName();
        }
        
        // Retourner un nom par défaut si l'utilisateur n'est pas trouvé
        return "Utilisateur " + approverId;
    }
    
    /**
     * Calcule le temps moyen de traitement
     */
    private double calculateAverageProcessingTime(List<DocumentApproval> approvals) {
        List<DocumentApproval> completedApprovals = approvals.stream()
            .filter(a -> a.getApprovalStatus() == DocumentApproval.ApprovalStatus.APPROVED && 
                        a.getApprovalDate() != null)
            .collect(Collectors.toList());
        
        if (completedApprovals.isEmpty()) {
            return 0.0;
        }
        
        double totalDays = completedApprovals.stream()
            .mapToDouble(a -> java.time.Duration.between(a.getCreatedAt(), a.getApprovalDate()).toDays())
            .sum();
        
        return totalDays / completedApprovals.size();
    }
}
