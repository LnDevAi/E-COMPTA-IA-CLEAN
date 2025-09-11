package com.ecomptaia.entity;

import jakarta.persistence.*;

/**
 * EntitÃ© pour les exigences de reporting par pays
 */
@Entity
@Table(name = "reporting_requirements")
public class ReportingRequirement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String reportName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportFrequency frequency;
    
    @Column(nullable = false)
    private Integer dueDay; // Jour du mois oÃ¹ le rapport est dÃ»
    
    @Column(nullable = false)
    private Boolean isMandatory;
    
    @Column(columnDefinition = "TEXT")
    private String template; // Template du rapport en JSON
    
    @Column(columnDefinition = "TEXT")
    private String validationRules; // RÃ¨gles de validation en JSON
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuration_id")
    private InternationalConfiguration configuration;
    
    // Ã‰numÃ©rations
    public enum ReportType {
        FINANCIAL, TAX, STATISTICAL, COMPLIANCE, AUDIT
    }
    
    public enum ReportFrequency {
        MONTHLY, QUARTERLY, ANNUALLY, ON_DEMAND
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ReportType getType() { return type; }
    public void setType(ReportType type) { this.type = type; }
    
    public ReportFrequency getFrequency() { return frequency; }
    public void setFrequency(ReportFrequency frequency) { this.frequency = frequency; }
    
    public Integer getDueDay() { return dueDay; }
    public void setDueDay(Integer dueDay) { this.dueDay = dueDay; }
    
    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }
    
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    
    public String getValidationRules() { return validationRules; }
    public void setValidationRules(String validationRules) { this.validationRules = validationRules; }
    
    public InternationalConfiguration getConfiguration() { return configuration; }
    public void setConfiguration(InternationalConfiguration configuration) { this.configuration = configuration; }
}




