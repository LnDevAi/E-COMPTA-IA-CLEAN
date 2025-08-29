package com.ecomptaia.repository;

import com.ecomptaia.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {

    // Trouver par entreprise
    List<WorkflowInstance> findByEntrepriseId(Long entrepriseId);

    // Trouver par workflow
    List<WorkflowInstance> findByWorkflowId(Long workflowId);

    // Trouver par statut
    List<WorkflowInstance> findByCurrentStatus(WorkflowInstance.InstanceStatus currentStatus);

    // Trouver par entreprise et statut
    List<WorkflowInstance> findByEntrepriseIdAndCurrentStatus(Long entrepriseId, WorkflowInstance.InstanceStatus currentStatus);

    // Trouver par initiateur
    List<WorkflowInstance> findByInitiatedBy(Long initiatedBy);

    // Trouver par type d'entité
    List<WorkflowInstance> findByEntityType(String entityType);

    // Trouver par entité spécifique
    List<WorkflowInstance> findByEntityTypeAndEntityId(String entityType, Long entityId);

    // Trouver par priorité
    List<WorkflowInstance> findByPriority(WorkflowInstance.Priority priority);

    // Trouver les instances en retard
    @Query("SELECT wi FROM WorkflowInstance wi WHERE wi.dueDate < :now AND wi.currentStatus IN ('PENDING', 'IN_PROGRESS')")
    List<WorkflowInstance> findOverdueInstances(@Param("now") LocalDateTime now);

    // Trouver les instances urgentes
    @Query("SELECT wi FROM WorkflowInstance wi WHERE wi.priority IN ('URGENT', 'CRITICAL') AND wi.currentStatus IN ('PENDING', 'IN_PROGRESS')")
    List<WorkflowInstance> findUrgentInstances();

    // Trouver les instances par période
    @Query("SELECT wi FROM WorkflowInstance wi WHERE wi.initiatedAt BETWEEN :startDate AND :endDate")
    List<WorkflowInstance> findInstancesByPeriod(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    // Compter par entreprise
    Long countByEntrepriseId(Long entrepriseId);

    // Compter par statut
    Long countByCurrentStatus(WorkflowInstance.InstanceStatus currentStatus);

    // Compter par entreprise et statut
    Long countByEntrepriseIdAndCurrentStatus(Long entrepriseId, WorkflowInstance.InstanceStatus currentStatus);

    // Trouver les instances récentes
    @Query("SELECT wi FROM WorkflowInstance wi WHERE wi.entrepriseId = :entrepriseId ORDER BY wi.initiatedAt DESC")
    List<WorkflowInstance> findRecentInstances(@Param("entrepriseId") Long entrepriseId);

    // Trouver les instances escaladées
    @Query("SELECT wi FROM WorkflowInstance wi WHERE wi.currentStatus = 'ESCALATED'")
    List<WorkflowInstance> findEscalatedInstances();

    // Trouver par niveau d'approbation actuel
    List<WorkflowInstance> findByCurrentLevel(Integer currentLevel);

    // Trouver les instances complétées
    @Query("SELECT wi FROM WorkflowInstance wi WHERE wi.currentStatus IN ('APPROVED', 'REJECTED', 'COMPLETED')")
    List<WorkflowInstance> findCompletedInstances();
}




