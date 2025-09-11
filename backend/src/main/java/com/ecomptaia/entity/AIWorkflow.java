package com.ecomptaia.entity;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.security.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * EntitÃ© pour les workflows intelligents automatisÃ©s par l'IA
 * RÃ©volutionnaire vs TOMPRO - Automatisation intelligente
 */
@Entity
@Table(name = "ai_workflows")
public class AIWorkflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkflowTrigger trigger;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Integer successRate = 0; // 0-100
    
    @Column
    private LocalDateTime lastExecuted;
    
    @Column
    private LocalDateTime nextExecution;
    
    @Column(nullable = false)
    private Integer executionCount = 0;
    
    @Column(nullable = false)
    private Integer failureCount = 0;
    
    @Column(columnDefinition = "TEXT")
    private String conditions; // Conditions de dÃ©clenchement en JSON
    
    @Column(columnDefinition = "TEXT")
    private String actions; // Actions Ã  exÃ©cuter en JSON
    
    @Column(columnDefinition = "TEXT")
    private String errorHandling; // Gestion d'erreurs en JSON
    
    @Column(columnDefinition = "TEXT")
    private String notifications; // Notifications en JSON
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkflowExecution> executions = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column(length = 20)
    private String version;
    
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    // Constructeurs
    public AIWorkflow() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public AIWorkflow(String name, String description, WorkflowTrigger trigger) {
        this();
        this.name = name;
        this.description = description;
        this.trigger = trigger;
    }
    
    // Ã‰numÃ©rations
    public enum WorkflowTrigger {
        AUTOMATIC, MANUAL, SCHEDULED, EVENT_BASED, CONDITION_BASED
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public WorkflowTrigger getTrigger() { return trigger; }
    public void setTrigger(WorkflowTrigger trigger) { this.trigger = trigger; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getSuccessRate() { return successRate; }
    public void setSuccessRate(Integer successRate) { this.successRate = successRate; }
    
    public LocalDateTime getLastExecuted() { return lastExecuted; }
    public void setLastExecuted(LocalDateTime lastExecuted) { this.lastExecuted = lastExecuted; }
    
    public LocalDateTime getNextExecution() { return nextExecution; }
    public void setNextExecution(LocalDateTime nextExecution) { this.nextExecution = nextExecution; }
    
    public Integer getExecutionCount() { return executionCount; }
    public void setExecutionCount(Integer executionCount) { this.executionCount = executionCount; }
    
    public Integer getFailureCount() { return failureCount; }
    public void setFailureCount(Integer failureCount) { this.failureCount = failureCount; }
    
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    
    public String getActions() { return actions; }
    public void setActions(String actions) { this.actions = actions; }
    
    public String getErrorHandling() { return errorHandling; }
    public void setErrorHandling(String errorHandling) { this.errorHandling = errorHandling; }
    
    public String getNotifications() { return notifications; }
    public void setNotifications(String notifications) { this.notifications = notifications; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public List<WorkflowExecution> getExecutions() { return executions; }
    public void setExecutions(List<WorkflowExecution> executions) { this.executions = executions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "AIWorkflow{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", trigger=" + trigger +
                ", isActive=" + isActive +
                ", successRate=" + successRate +
                ", executionCount=" + executionCount +
                '}';
    }
}




