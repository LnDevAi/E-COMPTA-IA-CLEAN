ackage com.ecomptaia.controller;

import com.ecomptaia.entity.DocumentWorkflow;
import com.ecomptaia.entity.DocumentApproval;
import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.service.WorkflowManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des workflows d'approbation de documents
 */
@RestController
@RequestMapping("/api/workflow-management")
@CrossOrigin(origins = "*")
public class WorkflowManagementController {
    
    @Autowired
    private WorkflowManagementService workflowService;
    
    // ==================== GESTION DES WORKFLOWS ====================
    
    /**
     * Récupère tous les workflows d'une entreprise
     */
    @GetMapping("/workflows")
    public ResponseEntity<List<DocumentWorkflow>> getWorkflowsByCompany(
            @RequestParam Long companyId) {
        List<DocumentWorkflow> workflows = workflowService.getWorkflowsByCompany(companyId);
        return ResponseEntity.ok(workflows);
    }
    
    /**
     * Récupère un workflow par son ID
     */
    @GetMapping("/workflows/{workflowId}")
    public ResponseEntity<DocumentWorkflow> getWorkflowById(@PathVariable Long workflowId) {
        Optional<DocumentWorkflow> workflow = workflowService.getWorkflowById(workflowId);
        return workflow.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Récupère les workflows actifs par type de document
     */
    @GetMapping("/workflows/active")
    public ResponseEntity<List<DocumentWorkflow>> getActiveWorkflowsByDocumentType(
            @RequestParam Long companyId,
            @RequestParam GedDocument.DocumentType documentType) {
        List<DocumentWorkflow> workflows = workflowService.getActiveWorkflowsByDocumentType(companyId, documentType);
        return ResponseEntity.ok(workflows);
    }
    
    /**
     * Crée un nouveau workflow
     */
    @PostMapping("/workflows")
    public ResponseEntity<DocumentWorkflow> createWorkflow(@RequestBody DocumentWorkflow workflow) {
        DocumentWorkflow createdWorkflow = workflowService.createWorkflow(workflow);
        return ResponseEntity.ok(createdWorkflow);
    }
    
    /**
     * Met à jour un workflow existant
     */
    @PutMapping("/workflows/{workflowId}")
    public ResponseEntity<DocumentWorkflow> updateWorkflow(
            @PathVariable Long workflowId,
            @RequestBody DocumentWorkflow workflowData) {
        try {
            DocumentWorkflow updatedWorkflow = workflowService.updateWorkflow(workflowId, workflowData);
            return ResponseEntity.ok(updatedWorkflow);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Supprime un workflow
     */
    @DeleteMapping("/workflows/{workflowId}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long workflowId) {
        try {
            workflowService.deleteWorkflow(workflowId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ==================== GESTION DES APPROBATIONS ====================
    
    /**
     * Récupère les approbations en attente pour un utilisateur
     */
    @GetMapping("/approvals/pending")
    public ResponseEntity<List<DocumentApproval>> getPendingApprovalsForUser(
            @RequestParam Long userId,
            @RequestParam Long companyId) {
        List<DocumentApproval> approvals = workflowService.getPendingApprovalsForUser(userId, companyId);
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * Récupère les approbations d'un document
     */
    @GetMapping("/approvals/document/{documentId}")
    public ResponseEntity<List<DocumentApproval>> getDocumentApprovals(@PathVariable Long documentId) {
        List<DocumentApproval> approvals = workflowService.getDocumentApprovals(documentId);
        return ResponseEntity.ok(approvals);
    }
    
    /**
     * Approuve un document
     */
    @PostMapping("/approvals/{approvalId}/approve")
    public ResponseEntity<DocumentApproval> approveDocument(
            @PathVariable Long approvalId,
            @RequestBody Map<String, String> request) {
        try {
            String comments = request.get("comments");
            Long approverId = Long.valueOf(request.get("approverId"));
            DocumentApproval approval = workflowService.approveDocument(approvalId, comments, approverId);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Rejette un document
     */
    @PostMapping("/approvals/{approvalId}/reject")
    public ResponseEntity<DocumentApproval> rejectDocument(
            @PathVariable Long approvalId,
            @RequestBody Map<String, String> request) {
        try {
            String comments = request.get("comments");
            Long approverId = Long.valueOf(request.get("approverId"));
            DocumentApproval approval = workflowService.rejectDocument(approvalId, comments, approverId);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Annule une approbation
     */
    @PostMapping("/approvals/{approvalId}/cancel")
    public ResponseEntity<DocumentApproval> cancelApproval(
            @PathVariable Long approvalId,
            @RequestBody Map<String, String> request) {
        try {
            String comments = request.get("comments");
            Long approverId = Long.valueOf(request.get("approverId"));
            DocumentApproval approval = workflowService.cancelApproval(approvalId, comments, approverId);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ==================== INITIALISATION DES WORKFLOWS ====================
    
    /**
     * Initialise le workflow d'approbation pour un document
     */
    @PostMapping("/workflows/initialize")
    public ResponseEntity<Map<String, Object>> initializeDocumentWorkflow(
            @RequestBody Map<String, Long> request) {
        try {
            Long documentId = request.get("documentId");
            Long companyId = request.get("companyId");
            workflowService.initializeDocumentWorkflow(documentId, companyId);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Workflow initialisé avec succès",
                "documentId", documentId
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ==================== STATISTIQUES ET RAPPORTS ====================
    
    /**
     * Récupère les statistiques des workflows pour une entreprise
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getWorkflowStatistics(@RequestParam Long companyId) {
        Map<String, Object> statistics = workflowService.getWorkflowStatistics(companyId);
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Récupère les documents en attente d'approbation
     */
    @GetMapping("/documents/pending-approval")
    public ResponseEntity<List<Map<String, Object>>> getDocumentsPendingApproval(@RequestParam Long companyId) {
        List<Map<String, Object>> documents = workflowService.getDocumentsPendingApproval(companyId);
        return ResponseEntity.ok(documents);
    }
    
    // ==================== ENDPOINTS DE SANTÉ ====================
    
    /**
     * Vérifie l'état de santé du service de workflow
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "WorkflowManagementService",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
}
