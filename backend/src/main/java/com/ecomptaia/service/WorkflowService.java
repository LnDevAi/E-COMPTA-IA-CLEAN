package com.ecomptaia.service;

import com.ecomptaia.entity.Workflow;
import com.ecomptaia.entity.WorkflowInstance;
import com.ecomptaia.entity.WorkflowApproval;
import com.ecomptaia.repository.WorkflowRepository;
import com.ecomptaia.repository.WorkflowInstanceRepository;
import com.ecomptaia.repository.WorkflowApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Autowired
    private WorkflowApprovalRepository workflowApprovalRepository;

    // ==================== GESTION DES WORKFLOWS ====================

    /**
     * Créer un nouveau workflow
     */
    public Workflow createWorkflow(String workflowName, Workflow.WorkflowType workflowType, 
                                 Long entrepriseId, String description) {
        Workflow workflow = new Workflow(workflowName, workflowType, entrepriseId);
        workflow.setDescription(description);
        return workflowRepository.save(workflow);
    }

    /**
     * Activer un workflow
     */
    public Workflow activateWorkflow(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow non trouvé"));
        workflow.setIsActive(true);
        return workflowRepository.save(workflow);
    }

    /**
     * Désactiver un workflow
     */
    public Workflow deactivateWorkflow(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow non trouvé"));
        workflow.setIsActive(false);
        return workflowRepository.save(workflow);
    }

    // ==================== GESTION DES INSTANCES DE WORKFLOW ====================

    /**
     * Créer une nouvelle instance de workflow
     */
    public WorkflowInstance createWorkflowInstance(Long workflowId, String instanceName, 
                                                 String entityType, Long entityId, 
                                                 Long initiatedBy, Long entrepriseId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow non trouvé"));

        WorkflowInstance instance = new WorkflowInstance(workflowId, instanceName, entityType, 
                                                       entityId, initiatedBy, entrepriseId);
        instance.setTotalLevels(workflow.getMaxApprovalLevels());
        instance.setDueDate(LocalDateTime.now().plusDays(7)); // Par défaut 7 jours

        instance = workflowInstanceRepository.save(instance);

        // Créer les approbations pour chaque niveau
        createApprovalsForInstance(instance);

        return instance;
    }

    /**
     * Créer les approbations pour une instance
     */
    private void createApprovalsForInstance(WorkflowInstance instance) {
        Workflow workflow = workflowRepository.findById(instance.getWorkflowId())
                .orElseThrow(() -> new RuntimeException("Workflow non trouvé"));

        // Simulation des approbateurs (dans un vrai système, cela viendrait de la configuration)
        List<Long> approvers = Arrays.asList(1L, 2L, 3L); // IDs des approbateurs

        for (int level = 1; level <= workflow.getMaxApprovalLevels(); level++) {
            Long approverId = approvers.get((level - 1) % approvers.size());
            
            WorkflowApproval approval = new WorkflowApproval(instance.getId(), approverId, level, instance.getEntrepriseId());
            approval.setDueDate(LocalDateTime.now().plusDays(3)); // 3 jours pour chaque niveau
            
            workflowApprovalRepository.save(approval);
        }
    }

    // ==================== GESTION DES APPROBATIONS ====================

    /**
     * Approuver une demande
     */
    public WorkflowApproval approveRequest(Long approvalId, String comments) {
        WorkflowApproval approval = workflowApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));

        approval.setApprovalStatus(WorkflowApproval.ApprovalStatus.APPROVED);
        approval.setApprovalDate(LocalDateTime.now());
        approval.setComments(comments);
        approval.setApprovalReason("Approuvé par l'utilisateur");

        approval = workflowApprovalRepository.save(approval);

        // Vérifier si c'est la dernière approbation
        checkWorkflowCompletion(approval.getWorkflowInstanceId());

        return approval;
    }

    /**
     * Rejeter une demande
     */
    public WorkflowApproval rejectRequest(Long approvalId, String comments, String reason) {
        WorkflowApproval approval = workflowApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));

        approval.setApprovalStatus(WorkflowApproval.ApprovalStatus.REJECTED);
        approval.setApprovalDate(LocalDateTime.now());
        approval.setComments(comments);
        approval.setApprovalReason(reason);

        approval = workflowApprovalRepository.save(approval);

        // Marquer l'instance comme rejetée
        WorkflowInstance instance = workflowInstanceRepository.findById(approval.getWorkflowInstanceId())
                .orElseThrow(() -> new RuntimeException("Instance non trouvée"));
        instance.setCurrentStatus(WorkflowInstance.InstanceStatus.REJECTED);
        instance.setCompletedAt(LocalDateTime.now());
        workflowInstanceRepository.save(instance);

        return approval;
    }

    /**
     * Déléguer une approbation
     */
    public WorkflowApproval delegateApproval(Long approvalId, Long delegatedTo, String reason) {
        WorkflowApproval approval = workflowApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));

        approval.setApprovalStatus(WorkflowApproval.ApprovalStatus.DELEGATED);
        approval.setIsDelegated(true);
        approval.setDelegatedTo(delegatedTo);
        approval.setDelegationReason(reason);
        approval.setUpdatedAt(LocalDateTime.now());

        return workflowApprovalRepository.save(approval);
    }

    /**
     * Vérifier la completion du workflow
     */
    private void checkWorkflowCompletion(Long workflowInstanceId) {
        List<WorkflowApproval> approvals = workflowApprovalRepository.findByWorkflowInstanceId(workflowInstanceId);
        
        boolean allApproved = approvals.stream()
                .allMatch(a -> a.getApprovalStatus() == WorkflowApproval.ApprovalStatus.APPROVED);

        if (allApproved) {
            WorkflowInstance instance = workflowInstanceRepository.findById(workflowInstanceId)
                    .orElseThrow(() -> new RuntimeException("Instance non trouvée"));
            instance.setCurrentStatus(WorkflowInstance.InstanceStatus.APPROVED);
            instance.setCompletedAt(LocalDateTime.now());
            workflowInstanceRepository.save(instance);
        }
    }

    // ==================== TÂCHES AUTOMATISÉES ====================

    /**
     * Vérifier les approbations en retard et déclencher l'escalade
     */
    @Scheduled(fixedRate = 300000) // Toutes les 5 minutes
    public void checkOverdueApprovals() {
        LocalDateTime now = LocalDateTime.now();
        List<WorkflowApproval> overdueApprovals = workflowApprovalRepository.findOverdueApprovals(now);

        for (WorkflowApproval approval : overdueApprovals) {
            if (!approval.getEscalationTriggered()) {
                triggerEscalation(approval);
            }
        }
    }

    /**
     * Envoyer des rappels pour les approbations en attente
     */
    @Scheduled(fixedRate = 3600000) // Toutes les heures
    public void sendReminders() {
        LocalDateTime reminderDate = LocalDateTime.now().minusHours(24);
        List<WorkflowApproval> approvalsNeedingReminder = workflowApprovalRepository.findApprovalsNeedingReminder(reminderDate);

        for (WorkflowApproval approval : approvalsNeedingReminder) {
            sendReminder(approval);
        }
    }

    /**
     * Déclencher l'escalade
     */
    private void triggerEscalation(WorkflowApproval approval) {
        approval.setEscalationTriggered(true);
        approval.setEscalationDate(LocalDateTime.now());
        workflowApprovalRepository.save(approval);

        // Marquer l'instance comme escaladée
        WorkflowInstance instance = workflowInstanceRepository.findById(approval.getWorkflowInstanceId())
                .orElseThrow(() -> new RuntimeException("Instance non trouvée"));
        instance.setCurrentStatus(WorkflowInstance.InstanceStatus.ESCALATED);
        workflowInstanceRepository.save(instance);
    }

    /**
     * Envoyer un rappel
     */
    private void sendReminder(WorkflowApproval approval) {
        approval.setReminderSent(true);
        approval.setReminderDate(LocalDateTime.now());
        workflowApprovalRepository.save(approval);

        // Ici, on pourrait envoyer un email ou une notification
        System.out.println("Rappel envoyé pour l'approbation ID: " + approval.getId());
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Obtenir les statistiques des workflows
     */
    public Map<String, Object> getWorkflowStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalWorkflows", workflowRepository.countByEntrepriseId(entrepriseId));
        stats.put("activeWorkflows", workflowRepository.countByEntrepriseIdAndIsActiveTrue(entrepriseId));
        stats.put("totalInstances", workflowInstanceRepository.countByEntrepriseId(entrepriseId));
        stats.put("pendingInstances", workflowInstanceRepository.countByEntrepriseIdAndCurrentStatus(entrepriseId, WorkflowInstance.InstanceStatus.PENDING));
        stats.put("inProgressInstances", workflowInstanceRepository.countByEntrepriseIdAndCurrentStatus(entrepriseId, WorkflowInstance.InstanceStatus.IN_PROGRESS));
        stats.put("completedInstances", workflowInstanceRepository.countByEntrepriseIdAndCurrentStatus(entrepriseId, WorkflowInstance.InstanceStatus.APPROVED));
        stats.put("rejectedInstances", workflowInstanceRepository.countByEntrepriseIdAndCurrentStatus(entrepriseId, WorkflowInstance.InstanceStatus.REJECTED));
        stats.put("escalatedInstances", workflowInstanceRepository.countByEntrepriseIdAndCurrentStatus(entrepriseId, WorkflowInstance.InstanceStatus.ESCALATED));
        
        return stats;
    }

    /**
     * Obtenir les approbations en attente d'un utilisateur
     */
    public List<WorkflowApproval> getPendingApprovalsForUser(Long userId) {
        return workflowApprovalRepository.findPendingApprovalsByApprover(userId);
    }

    /**
     * Obtenir les instances urgentes
     */
    public List<WorkflowInstance> getUrgentInstances() {
        return workflowInstanceRepository.findUrgentInstances();
    }

    /**
     * Obtenir les instances en retard
     */
    public List<WorkflowInstance> getOverdueInstances() {
        return workflowInstanceRepository.findOverdueInstances(LocalDateTime.now());
    }
}




