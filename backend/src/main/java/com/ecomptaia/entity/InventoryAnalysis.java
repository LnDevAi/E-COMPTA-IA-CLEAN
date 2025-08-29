package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_analysis")
public class InventoryAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false)
    private String analysisNumber; // Numéro d'analyse

    @Column(nullable = false)
    private String analysisType; // ASSET_ANALYSIS, INVENTORY_ANALYSIS, COMPREHENSIVE

    @Column(nullable = false)
    private LocalDateTime analysisDate; // Date de l'analyse

    @Column(nullable = false, length = 20)
    private String status; // DRAFT, IN_PROGRESS, COMPLETED, VALIDATED

    @Column(precision = 15, scale = 2)
    private BigDecimal totalAccountingValue; // Valeur comptable totale

    @Column(precision = 15, scale = 2)
    private BigDecimal totalPhysicalValue; // Valeur physique totale

    @Column(precision = 15, scale = 2)
    private BigDecimal totalVariance; // Écart total

    @Column(precision = 15, scale = 2)
    private BigDecimal variancePercentage; // Pourcentage d'écart

    @Column
    private Integer totalItemsAnalyzed; // Nombre total d'articles analysés

    @Column
    private Integer itemsWithVariance; // Nombre d'articles avec écart

    @Column(length = 1000)
    private String analysisNotes; // Notes d'analyse

    @Column(length = 20)
    private String analysisMethod; // Méthode d'analyse utilisée

    @Column
    private LocalDateTime physicalInventoryDate; // Date de l'inventaire physique

    @Column
    private String physicalInventoryReference; // Référence de l'inventaire physique

    @Column
    private Long createdBy; // ID de l'utilisateur créateur

    @Column
    private Long validatedBy; // ID de l'utilisateur validateur

    @Column
    private LocalDateTime validatedAt; // Date de validation

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays

    @Column(nullable = false, length = 20)
    private String accountingStandard; // Standard comptable

    @Column
    private Boolean isReportGenerated = false; // Si le rapport a été généré

    @Column
    private String reportFilePath; // Chemin vers le rapport généré

    @Column
    private Boolean isAccountingEntriesGenerated = false; // Si les écritures comptables ont été générées

    @Column
    private String accountingEntriesReference; // Référence des écritures comptables générées

    // Constructeurs
    public InventoryAnalysis() {}

    public InventoryAnalysis(Long companyId, String analysisNumber, String analysisType, 
                           String countryCode, String accountingStandard) {
        this.companyId = companyId;
        this.analysisNumber = analysisNumber;
        this.analysisType = analysisType;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.status = "DRAFT";
        this.analysisDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getAnalysisNumber() { return analysisNumber; }
    public void setAnalysisNumber(String analysisNumber) { this.analysisNumber = analysisNumber; }

    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }

    public LocalDateTime getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAccountingValue() { return totalAccountingValue; }
    public void setTotalAccountingValue(BigDecimal totalAccountingValue) { this.totalAccountingValue = totalAccountingValue; }

    public BigDecimal getTotalPhysicalValue() { return totalPhysicalValue; }
    public void setTotalPhysicalValue(BigDecimal totalPhysicalValue) { this.totalPhysicalValue = totalPhysicalValue; }

    public BigDecimal getTotalVariance() { return totalVariance; }
    public void setTotalVariance(BigDecimal totalVariance) { this.totalVariance = totalVariance; }

    public BigDecimal getVariancePercentage() { return variancePercentage; }
    public void setVariancePercentage(BigDecimal variancePercentage) { this.variancePercentage = variancePercentage; }

    public Integer getTotalItemsAnalyzed() { return totalItemsAnalyzed; }
    public void setTotalItemsAnalyzed(Integer totalItemsAnalyzed) { this.totalItemsAnalyzed = totalItemsAnalyzed; }

    public Integer getItemsWithVariance() { return itemsWithVariance; }
    public void setItemsWithVariance(Integer itemsWithVariance) { this.itemsWithVariance = itemsWithVariance; }

    public String getAnalysisNotes() { return analysisNotes; }
    public void setAnalysisNotes(String analysisNotes) { this.analysisNotes = analysisNotes; }

    public String getAnalysisMethod() { return analysisMethod; }
    public void setAnalysisMethod(String analysisMethod) { this.analysisMethod = analysisMethod; }

    public LocalDateTime getPhysicalInventoryDate() { return physicalInventoryDate; }
    public void setPhysicalInventoryDate(LocalDateTime physicalInventoryDate) { this.physicalInventoryDate = physicalInventoryDate; }

    public String getPhysicalInventoryReference() { return physicalInventoryReference; }
    public void setPhysicalInventoryReference(String physicalInventoryReference) { this.physicalInventoryReference = physicalInventoryReference; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getValidatedBy() { return validatedBy; }
    public void setValidatedBy(Long validatedBy) { this.validatedBy = validatedBy; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public Boolean getIsReportGenerated() { return isReportGenerated; }
    public void setIsReportGenerated(Boolean isReportGenerated) { this.isReportGenerated = isReportGenerated; }

    public String getReportFilePath() { return reportFilePath; }
    public void setReportFilePath(String reportFilePath) { this.reportFilePath = reportFilePath; }

    public Boolean getIsAccountingEntriesGenerated() { return isAccountingEntriesGenerated; }
    public void setIsAccountingEntriesGenerated(Boolean isAccountingEntriesGenerated) { this.isAccountingEntriesGenerated = isAccountingEntriesGenerated; }

    public String getAccountingEntriesReference() { return accountingEntriesReference; }
    public void setAccountingEntriesReference(String accountingEntriesReference) { this.accountingEntriesReference = accountingEntriesReference; }
}


