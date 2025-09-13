package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EntitÃ© pour les rÃ¨gles de conformitÃ© par pays
 */
@Entity
@Table(name = "compliance_rules")
public class ComplianceRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String ruleName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RuleType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RuleCategory category;
    
    @Column(nullable = false)
    private Integer priority; // 1-10
    
    @Column(nullable = false)
    private Boolean isMandatory;
    
    @Column(columnDefinition = "TEXT")
    private String validationLogic; // Logique de validation en JSON
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(columnDefinition = "TEXT")
    private String helpText;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuration_id")
    private InternationalConfiguration configuration;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Ã‰numÃ©rations
    public enum RuleType {
        VALIDATION, CALCULATION, FORMAT, BUSINESS_LOGIC, REGULATORY
    }
    
    public enum RuleCategory {
        ACCOUNTING, TAX, REPORTING, AUDIT, COMPLIANCE
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public RuleType getType() { return type; }
    public void setType(RuleType type) { this.type = type; }
    
    public RuleCategory getCategory() { return category; }
    public void setCategory(RuleCategory category) { this.category = category; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }
    
    public String getValidationLogic() { return validationLogic; }
    public void setValidationLogic(String validationLogic) { this.validationLogic = validationLogic; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getHelpText() { return helpText; }
    public void setHelpText(String helpText) { this.helpText = helpText; }
    
    public InternationalConfiguration getConfiguration() { return configuration; }
    public void setConfiguration(InternationalConfiguration configuration) { this.configuration = configuration; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}




