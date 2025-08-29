package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "automated_tasks")
public class AutomatedTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String taskName; // Nom de la tâche
    
    @Column(nullable = false)
    private String taskType; // Type de tâche (DOCUMENT_ANALYSIS, TAX_CALCULATION, etc.)
    
    @Column(nullable = false)
    private String status; // Status (PENDING, PROCESSING, COMPLETED, FAILED)
    
    @Column(length = 1000)
    private String description; // Description de la tâche
    
    @Column(length = 1000)
    private String inputData; // Données d'entrée (JSON)
    
    @Column(length = 2000)
    private String outputData; // Résultats (JSON)
    
    @Column(length = 500)
    private String errorMessage; // Message d'erreur si échec
    
    @Column
    private Double confidence; // Niveau de confiance de l'IA (0-1)
    
    @Column
    private Integer processingTime; // Temps de traitement en millisecondes
    
    @Column(nullable = false)
    private String countryCode; // Pays concerné
    
    @Column
    private String accountingStandard; // Standard comptable
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private Boolean isActive = true;
    
    @Column(length = 100)
    private String aiModel; // Modèle d'IA utilisé
    
    @Column
    private Integer priority = 1; // Priorité (1-5, 5 étant le plus élevé)
    
    // Constructeurs
    public AutomatedTask() {}
    
    public AutomatedTask(String taskName, String taskType, String description, String inputData, String countryCode) {
        this.taskName = taskName;
        this.taskType = taskType;
        this.description = description;
        this.inputData = inputData;
        this.countryCode = countryCode;
        this.status = "PENDING";
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    public AutomatedTask(String taskName, String taskType, String description, String inputData, 
                        String countryCode, String accountingStandard, Integer priority) {
        this.taskName = taskName;
        this.taskType = taskType;
        this.description = description;
        this.inputData = inputData;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.priority = priority;
        this.status = "PENDING";
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }
    
    public String getOutputData() { return outputData; }
    public void setOutputData(String outputData) { this.outputData = outputData; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    public Integer getProcessingTime() { return processingTime; }
    public void setProcessingTime(Integer processingTime) { this.processingTime = processingTime; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    @Override
    public String toString() {
        return taskName + " (" + status + ") - " + countryCode;
    }
}





