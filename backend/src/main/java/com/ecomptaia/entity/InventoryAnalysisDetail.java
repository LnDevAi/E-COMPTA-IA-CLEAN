package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_analysis_details")
public class InventoryAnalysisDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long analysisId; // ID de l'analyse parente

    @Column(nullable = false)
    private String itemCode; // Code de l'article (asset ou inventory)

    @Column(nullable = false)
    private String itemName; // Nom de l'article

    @Column(nullable = false, length = 20)
    private String itemType; // ASSET, INVENTORY

    @Column(precision = 15, scale = 2)
    private BigDecimal accountingQuantity; // Quantité comptable

    @Column(precision = 15, scale = 2)
    private BigDecimal physicalQuantity; // Quantité physique

    @Column(precision = 15, scale = 2)
    private BigDecimal quantityVariance; // Écart de quantité

    @Column(precision = 15, scale = 2)
    private BigDecimal accountingValue; // Valeur comptable

    @Column(precision = 15, scale = 2)
    private BigDecimal physicalValue; // Valeur physique

    @Column(precision = 15, scale = 2)
    private BigDecimal valueVariance; // Écart de valeur

    @Column(precision = 15, scale = 2)
    private BigDecimal unitPrice; // Prix unitaire

    @Column(length = 20)
    private String varianceType; // SURPLUS, SHORTAGE, NONE

    @Column(precision = 15, scale = 2)
    private BigDecimal variancePercentage; // Pourcentage d'écart

    @Column(length = 500)
    private String varianceReason; // Raison de l'écart

    @Column(length = 20)
    private String proposedAction; // Action proposée (ADJUSTMENT, INVESTIGATION, NONE)

    @Column
    private Boolean isReconciled = false; // Si l'écart a été réconcilié

    @Column
    private LocalDateTime reconciledAt; // Date de réconciliation

    @Column
    private Long reconciledBy; // ID de l'utilisateur qui a réconcilié

    @Column(length = 500)
    private String reconciliationNotes; // Notes de réconciliation

    @Column
    private String accountingEntryReference; // Référence de l'écriture comptable de correction

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays

    @Column(nullable = false, length = 20)
    private String accountingStandard; // Standard comptable

    // Constructeurs
    public InventoryAnalysisDetail() {}

    public InventoryAnalysisDetail(Long analysisId, String itemCode, String itemName, 
                                 String itemType, Long companyId, String countryCode, 
                                 String accountingStandard) {
        this.analysisId = analysisId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemType = itemType;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAnalysisId() { return analysisId; }
    public void setAnalysisId(Long analysisId) { this.analysisId = analysisId; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public BigDecimal getAccountingQuantity() { return accountingQuantity; }
    public void setAccountingQuantity(BigDecimal accountingQuantity) { this.accountingQuantity = accountingQuantity; }

    public BigDecimal getPhysicalQuantity() { return physicalQuantity; }
    public void setPhysicalQuantity(BigDecimal physicalQuantity) { this.physicalQuantity = physicalQuantity; }

    public BigDecimal getQuantityVariance() { return quantityVariance; }
    public void setQuantityVariance(BigDecimal quantityVariance) { this.quantityVariance = quantityVariance; }

    public BigDecimal getAccountingValue() { return accountingValue; }
    public void setAccountingValue(BigDecimal accountingValue) { this.accountingValue = accountingValue; }

    public BigDecimal getPhysicalValue() { return physicalValue; }
    public void setPhysicalValue(BigDecimal physicalValue) { this.physicalValue = physicalValue; }

    public BigDecimal getValueVariance() { return valueVariance; }
    public void setValueVariance(BigDecimal valueVariance) { this.valueVariance = valueVariance; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getVarianceType() { return varianceType; }
    public void setVarianceType(String varianceType) { this.varianceType = varianceType; }

    public BigDecimal getVariancePercentage() { return variancePercentage; }
    public void setVariancePercentage(BigDecimal variancePercentage) { this.variancePercentage = variancePercentage; }

    public String getVarianceReason() { return varianceReason; }
    public void setVarianceReason(String varianceReason) { this.varianceReason = varianceReason; }

    public String getProposedAction() { return proposedAction; }
    public void setProposedAction(String proposedAction) { this.proposedAction = proposedAction; }

    public Boolean getIsReconciled() { return isReconciled; }
    public void setIsReconciled(Boolean isReconciled) { this.isReconciled = isReconciled; }

    public LocalDateTime getReconciledAt() { return reconciledAt; }
    public void setReconciledAt(LocalDateTime reconciledAt) { this.reconciledAt = reconciledAt; }

    public Long getReconciledBy() { return reconciledBy; }
    public void setReconciledBy(Long reconciledBy) { this.reconciledBy = reconciledBy; }

    public String getReconciliationNotes() { return reconciliationNotes; }
    public void setReconciliationNotes(String reconciliationNotes) { this.reconciliationNotes = reconciliationNotes; }

    public String getAccountingEntryReference() { return accountingEntryReference; }
    public void setAccountingEntryReference(String accountingEntryReference) { this.accountingEntryReference = accountingEntryReference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }
}


