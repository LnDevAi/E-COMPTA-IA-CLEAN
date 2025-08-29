package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_instances")
public class WorkflowInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;

    @Column(name = "instance_name", nullable = false)
    private String instanceName;

    @Column(name = "entity_type", nullable = false)
    private String entityType; // Type d'entité (EcritureComptable, Document, etc.)

    @Column(name = "entity_id", nullable = false)
    private Long entityId; // ID de l'entité concernée

    @Column(name = "current_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InstanceStatus currentStatus;

    @Column(name = "current_level")
    private Integer currentLevel; // Niveau d'approbation actuel

    @Column(name = "total_levels")
    private Integer totalLevels; // Nombre total de niveaux

    @Column(name = "initiated_by", nullable = false)
    private Long initiatedBy;

    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des métadonnées

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum InstanceStatus {
        PENDING("En attente"),
        IN_PROGRESS("En cours"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté"),
        CANCELLED("Annulé"),
        ESCALATED("Escaladé"),
        COMPLETED("Terminé");

        private final String description;

        InstanceStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Priority {
        LOW("Faible"),
        NORMAL("Normal"),
        HIGH("Élevé"),
        URGENT("Urgent"),
        CRITICAL("Critique");

        private final String description;

        Priority(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public WorkflowInstance() {
        this.initiatedAt = LocalDateTime.now();
        this.currentStatus = InstanceStatus.PENDING;
        this.currentLevel = 1;
        this.priority = Priority.NORMAL;
    }

    public WorkflowInstance(Long workflowId, String instanceName, String entityType, 
                          Long entityId, Long initiatedBy, Long entrepriseId) {
        this();
        this.workflowId = workflowId;
        this.instanceName = instanceName;
        this.entityType = entityType;
        this.entityId = entityId;
        this.initiatedBy = initiatedBy;
        this.entrepriseId = entrepriseId;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public InstanceStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(InstanceStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getTotalLevels() {
        return totalLevels;
    }

    public void setTotalLevels(Integer totalLevels) {
        this.totalLevels = totalLevels;
    }

    public Long getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(Long initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public LocalDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(LocalDateTime initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}




