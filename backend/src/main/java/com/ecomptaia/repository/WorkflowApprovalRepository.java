package com.ecomptaia.repository;

import com.ecomptaia.entity.WorkflowApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkflowApprovalRepository extends JpaRepository<WorkflowApproval, Long> {

    // Trouver par instance de workflow
    List<WorkflowApproval> findByWorkflowInstanceId(Long workflowInstanceId);

    // Trouver par approbateur
    List<WorkflowApproval> findByApproverId(Long approverId);

    // Trouver par statut d'approbation
    List<WorkflowApproval> findByApprovalStatus(WorkflowApproval.ApprovalStatus approvalStatus);

    // Trouver par niveau d'approbation
    List<WorkflowApproval> findByApprovalLevel(Integer approvalLevel);

    // Trouver par instance et niveau
    List<WorkflowApproval> findByWorkflowInstanceIdAndApprovalLevel(Long workflowInstanceId, Integer approvalLevel);

    // Trouver les approbations en attente
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.approvalStatus = 'PENDING'")
    List<WorkflowApproval> findPendingApprovals();

    // Trouver les approbations en attente par approbateur
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.approverId = :approverId AND wa.approvalStatus = 'PENDING'")
    List<WorkflowApproval> findPendingApprovalsByApprover(@Param("approverId") Long approverId);

    // Trouver les approbations en retard
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.dueDate < :now AND wa.approvalStatus = 'PENDING'")
    List<WorkflowApproval> findOverdueApprovals(@Param("now") LocalDateTime now);

    // Trouver les approbations déléguées
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.isDelegated = true")
    List<WorkflowApproval> findDelegatedApprovals();

    // Trouver les approbations escaladées
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.escalationTriggered = true")
    List<WorkflowApproval> findEscalatedApprovals();

    // Trouver par entreprise
    List<WorkflowApproval> findByEntrepriseId(Long entrepriseId);

    // Trouver par entreprise et statut
    List<WorkflowApproval> findByEntrepriseIdAndApprovalStatus(Long entrepriseId, WorkflowApproval.ApprovalStatus approvalStatus);

    // Trouver les approbations récentes
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.entrepriseId = :entrepriseId ORDER BY wa.createdAt DESC")
    List<WorkflowApproval> findRecentApprovals(@Param("entrepriseId") Long entrepriseId);

    // Trouver par période
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.createdAt BETWEEN :startDate AND :endDate")
    List<WorkflowApproval> findApprovalsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    // Compter par entreprise
    Long countByEntrepriseId(Long entrepriseId);

    // Compter par statut
    Long countByApprovalStatus(WorkflowApproval.ApprovalStatus approvalStatus);

    // Compter par entreprise et statut
    Long countByEntrepriseIdAndApprovalStatus(Long entrepriseId, WorkflowApproval.ApprovalStatus approvalStatus);

    // Compter les approbations en attente par approbateur
    @Query("SELECT COUNT(wa) FROM WorkflowApproval wa WHERE wa.approverId = :approverId AND wa.approvalStatus = 'PENDING'")
    Long countPendingApprovalsByApprover(@Param("approverId") Long approverId);

    // Trouver les approbations avec notifications non envoyées
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.notificationSent = false AND wa.approvalStatus = 'PENDING'")
    List<WorkflowApproval> findApprovalsWithoutNotification();

    // Trouver les approbations nécessitant des rappels
    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.reminderSent = false AND wa.dueDate < :reminderDate AND wa.approvalStatus = 'PENDING'")
    List<WorkflowApproval> findApprovalsNeedingReminder(@Param("reminderDate") LocalDateTime reminderDate);
}




