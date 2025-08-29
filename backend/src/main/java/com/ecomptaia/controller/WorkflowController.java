package com.ecomptaia.controller;

import com.ecomptaia.entity.Workflow;
import com.ecomptaia.entity.WorkflowInstance;
import com.ecomptaia.entity.WorkflowApproval;
import com.ecomptaia.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test de base du module
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module de workflow et approbations opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("features", List.of(
            "Workflows d'approbation personnalisables",
            "Gestion des approbateurs et délégations",
            "Types de workflows multiples",
            "Suivi et traçabilité complète",
            "Notifications intelligentes",
            "Escalade automatique",
            "Rappels et alertes"
        ));
        return ResponseEntity.ok(response);
    }

    /**
     * Test de création de workflow comptable
     */
    @PostMapping("/test-create-accounting-workflow")
    public ResponseEntity<Map<String, Object>> testCreateAccountingWorkflow() {
        Map<String, Object> response = new HashMap<>();
        try {
            Workflow workflow = workflowService.createWorkflow(
                "Workflow Approbation Écritures Comptables", 
                Workflow.WorkflowType.ACCOUNTING_ENTRY,
                1L, 
                "Workflow pour l'approbation des écritures comptables importantes"
            );

            response.put("success", true);
            response.put("message", "Workflow comptable créé avec succès");
            response.put("workflow", workflow);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de création d'instance de workflow
     */
    @PostMapping("/test-create-workflow-instance")
    public ResponseEntity<Map<String, Object>> testCreateWorkflowInstance() {
        Map<String, Object> response = new HashMap<>();
        try {
            WorkflowInstance instance = workflowService.createWorkflowInstance(
                1L, // workflowId
                "Approbation Écriture Comptable #12345", 
                "EcritureComptable", 
                12345L, 
                1L, // initiatedBy
                1L  // entrepriseId
            );

            response.put("success", true);
            response.put("message", "Instance de workflow créée avec succès");
            response.put("instance", instance);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test d'approbation de demande
     */
    @PostMapping("/test-approve-request/{approvalId}")
    public ResponseEntity<Map<String, Object>> testApproveRequest(@PathVariable Long approvalId) {
        Map<String, Object> response = new HashMap<>();
        try {
            WorkflowApproval approval = workflowService.approveRequest(
                approvalId, 
                "Écriture comptable conforme aux normes OHADA"
            );

            response.put("success", true);
            response.put("message", "Demande approuvée avec succès");
            response.put("approval", approval);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'approbation: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de rejet de demande
     */
    @PostMapping("/test-reject-request/{approvalId}")
    public ResponseEntity<Map<String, Object>> testRejectRequest(@PathVariable Long approvalId) {
        Map<String, Object> response = new HashMap<>();
        try {
            WorkflowApproval approval = workflowService.rejectRequest(
                approvalId, 
                "Écriture comptable non conforme", 
                "Montant incorrect et pièces justificatives manquantes"
            );

            response.put("success", true);
            response.put("message", "Demande rejetée avec succès");
            response.put("approval", approval);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du rejet: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de délégation d'approbation
     */
    @PostMapping("/test-delegate-approval/{approvalId}")
    public ResponseEntity<Map<String, Object>> testDelegateApproval(@PathVariable Long approvalId) {
        Map<String, Object> response = new HashMap<>();
        try {
            WorkflowApproval approval = workflowService.delegateApproval(
                approvalId, 
                2L, // delegatedTo
                "Absence temporaire, délégation au responsable adjoint"
            );

            response.put("success", true);
            response.put("message", "Approbation déléguée avec succès");
            response.put("approval", approval);
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la délégation: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test des statistiques complètes
     */
    @GetMapping("/test-statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testStatistics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = workflowService.getWorkflowStatistics(entrepriseId);
            List<WorkflowInstance> urgentInstances = workflowService.getUrgentInstances();
            List<WorkflowInstance> overdueInstances = workflowService.getOverdueInstances();

            response.put("success", true);
            response.put("message", "Statistiques récupérées avec succès");
            response.put("statistics", stats);
            response.put("urgentInstances", urgentInstances.size());
            response.put("overdueInstances", overdueInstances.size());
            response.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    // ==================== ENDPOINTS DE GESTION ====================

    /**
     * Créer un workflow
     */
    @PostMapping("/workflows")
    public ResponseEntity<Map<String, Object>> createWorkflow(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String workflowName = (String) request.get("workflowName");
            String workflowTypeStr = (String) request.get("workflowType");
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());
            String description = (String) request.get("description");

            Workflow.WorkflowType workflowType = Workflow.WorkflowType.valueOf(workflowTypeStr);
            Workflow workflow = workflowService.createWorkflow(workflowName, workflowType, entrepriseId, description);

            response.put("success", true);
            response.put("message", "Workflow créé avec succès");
            response.put("workflow", workflow);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Créer une instance de workflow
     */
    @PostMapping("/instances")
    public ResponseEntity<Map<String, Object>> createWorkflowInstance(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long workflowId = Long.valueOf(request.get("workflowId").toString());
            String instanceName = (String) request.get("instanceName");
            String entityType = (String) request.get("entityType");
            Long entityId = Long.valueOf(request.get("entityId").toString());
            Long initiatedBy = Long.valueOf(request.get("initiatedBy").toString());
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());

            WorkflowInstance instance = workflowService.createWorkflowInstance(
                workflowId, instanceName, entityType, entityId, initiatedBy, entrepriseId
            );

            response.put("success", true);
            response.put("message", "Instance de workflow créée avec succès");
            response.put("instance", instance);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Approuver une demande
     */
    @PostMapping("/approvals/{approvalId}/approve")
    public ResponseEntity<Map<String, Object>> approveRequest(@PathVariable Long approvalId, 
                                                             @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String comments = (String) request.get("comments");
            WorkflowApproval approval = workflowService.approveRequest(approvalId, comments);

            response.put("success", true);
            response.put("message", "Demande approuvée avec succès");
            response.put("approval", approval);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Rejeter une demande
     */
    @PostMapping("/approvals/{approvalId}/reject")
    public ResponseEntity<Map<String, Object>> rejectRequest(@PathVariable Long approvalId, 
                                                            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String comments = (String) request.get("comments");
            String reason = (String) request.get("reason");
            WorkflowApproval approval = workflowService.rejectRequest(approvalId, comments, reason);

            response.put("success", true);
            response.put("message", "Demande rejetée avec succès");
            response.put("approval", approval);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les statistiques
     */
    @GetMapping("/statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getStatistics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = workflowService.getWorkflowStatistics(entrepriseId);
            response.put("success", true);
            response.put("statistics", stats);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les approbations en attente d'un utilisateur
     */
    @GetMapping("/approvals/pending/{userId}")
    public ResponseEntity<Map<String, Object>> getPendingApprovals(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<WorkflowApproval> approvals = workflowService.getPendingApprovalsForUser(userId);
            response.put("success", true);
            response.put("approvals", approvals);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}




