package com.ecomptaia.repository;

import com.ecomptaia.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    // Trouver par entreprise
    List<Workflow> findByEntrepriseId(Long entrepriseId);

    // Trouver par type de workflow
    List<Workflow> findByWorkflowType(Workflow.WorkflowType workflowType);

    // Trouver par entreprise et type
    List<Workflow> findByEntrepriseIdAndWorkflowType(Long entrepriseId, Workflow.WorkflowType workflowType);

    // Trouver les workflows actifs
    List<Workflow> findByIsActiveTrue();

    // Trouver les workflows actifs par entreprise
    List<Workflow> findByEntrepriseIdAndIsActiveTrue(Long entrepriseId);

    // Trouver par nom de workflow
    List<Workflow> findByWorkflowNameContainingIgnoreCase(String workflowName);

    // Trouver par entreprise et nom
    List<Workflow> findByEntrepriseIdAndWorkflowNameContainingIgnoreCase(Long entrepriseId, String workflowName);

    // Compter par entreprise
    Long countByEntrepriseId(Long entrepriseId);

    // Compter les workflows actifs par entreprise
    Long countByEntrepriseIdAndIsActiveTrue(Long entrepriseId);

    // Compter par type de workflow
    Long countByWorkflowType(Workflow.WorkflowType workflowType);

    // Trouver les workflows récents
    @Query("SELECT w FROM Workflow w WHERE w.entrepriseId = :entrepriseId ORDER BY w.createdAt DESC")
    List<Workflow> findRecentWorkflows(@Param("entrepriseId") Long entrepriseId);

    // Trouver les workflows avec escalade activée
    @Query("SELECT w FROM Workflow w WHERE w.autoEscalationEnabled = true AND w.isActive = true")
    List<Workflow> findWorkflowsWithEscalation();

    // Trouver par nombre de niveaux d'approbation
    List<Workflow> findByMaxApprovalLevels(Integer maxApprovalLevels);

    // Trouver les workflows nécessitant une approbation finale
    @Query("SELECT w FROM Workflow w WHERE w.requiresFinalApproval = true AND w.isActive = true")
    List<Workflow> findWorkflowsRequiringFinalApproval();
}





