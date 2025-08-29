package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliations")
public class Reconciliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false)
    private Long bankAccountId; // ID du compte bancaire

    @Column(nullable = false)
    private String reconciliationNumber; // Numéro de rapprochement

    @Column(nullable = false, length = 20)
    private String status; // DRAFT, IN_PROGRESS, COMPLETED, CANCELLED

    @Column
    private LocalDateTime reconciliationDate; // Date de rapprochement

    @Column(precision = 15, scale = 2)
    private BigDecimal bankStatementBalance; // Solde du relevé bancaire

    @Column(precision = 15, scale = 2)
    private BigDecimal bookBalance; // Solde comptable

    @Column(precision = 15, scale = 2)
    private BigDecimal difference; // Différence entre les deux soldes

    @Column
    private LocalDateTime startDate; // Date de début de période

    @Column
    private LocalDateTime endDate; // Date de fin de période

    @Column
    private Long createdBy; // ID de l'utilisateur créateur

    @Column
    private Long completedBy; // ID de l'utilisateur qui a terminé

    @Column
    private LocalDateTime completedAt; // Date de finalisation

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays

    @Column(nullable = false, length = 20)
    private String accountingStandard; // Standard comptable

    @Column(length = 1000)
    private String notes; // Notes additionnelles

    @Column
    private Boolean isAutoReconciled = false; // Si le rapprochement est automatique

    @Column
    private Integer totalTransactions; // Nombre total de transactions

    @Column
    private Integer reconciledTransactions; // Nombre de transactions rapprochées

    @Column
    private Integer unreconciledTransactions; // Nombre de transactions non rapprochées

    @Column
    private BigDecimal totalReconciledAmount; // Montant total rapproché

    @Column
    private BigDecimal totalUnreconciledAmount; // Montant total non rapproché

    @Column
    private String reconciliationMethod; // MANUAL, AUTO, HYBRID

    @Column
    private String bankStatementFile; // Fichier du relevé bancaire

    @Column
    private String bankStatementFormat; // Format du relevé (CSV, OFX, QIF, etc.)

    @Column
    private Boolean isLocked = false; // Si le rapprochement est verrouillé

    @Column
    private LocalDateTime lockedAt; // Date de verrouillage

    @Column
    private Long lockedBy; // ID de l'utilisateur qui a verrouillé

    @Column
    private String lockReason; // Raison du verrouillage

    // Constructeurs
    public Reconciliation() {}

    public Reconciliation(Long companyId, Long bankAccountId, String reconciliationNumber, 
                         String countryCode, String accountingStandard) {
        this.companyId = companyId;
        this.bankAccountId = bankAccountId;
        this.reconciliationNumber = reconciliationNumber;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.status = "DRAFT";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Long getBankAccountId() { return bankAccountId; }
    public void setBankAccountId(Long bankAccountId) { this.bankAccountId = bankAccountId; }

    public String getReconciliationNumber() { return reconciliationNumber; }
    public void setReconciliationNumber(String reconciliationNumber) { this.reconciliationNumber = reconciliationNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getReconciliationDate() { return reconciliationDate; }
    public void setReconciliationDate(LocalDateTime reconciliationDate) { this.reconciliationDate = reconciliationDate; }

    public BigDecimal getBankStatementBalance() { return bankStatementBalance; }
    public void setBankStatementBalance(BigDecimal bankStatementBalance) { this.bankStatementBalance = bankStatementBalance; }

    public BigDecimal getBookBalance() { return bookBalance; }
    public void setBookBalance(BigDecimal bookBalance) { this.bookBalance = bookBalance; }

    public BigDecimal getDifference() { return difference; }
    public void setDifference(BigDecimal difference) { this.difference = difference; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getCompletedBy() { return completedBy; }
    public void setCompletedBy(Long completedBy) { this.completedBy = completedBy; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getIsAutoReconciled() { return isAutoReconciled; }
    public void setIsAutoReconciled(Boolean isAutoReconciled) { this.isAutoReconciled = isAutoReconciled; }

    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }

    public Integer getReconciledTransactions() { return reconciledTransactions; }
    public void setReconciledTransactions(Integer reconciledTransactions) { this.reconciledTransactions = reconciledTransactions; }

    public Integer getUnreconciledTransactions() { return unreconciledTransactions; }
    public void setUnreconciledTransactions(Integer unreconciledTransactions) { this.unreconciledTransactions = unreconciledTransactions; }

    public BigDecimal getTotalReconciledAmount() { return totalReconciledAmount; }
    public void setTotalReconciledAmount(BigDecimal totalReconciledAmount) { this.totalReconciledAmount = totalReconciledAmount; }

    public BigDecimal getTotalUnreconciledAmount() { return totalUnreconciledAmount; }
    public void setTotalUnreconciledAmount(BigDecimal totalUnreconciledAmount) { this.totalUnreconciledAmount = totalUnreconciledAmount; }

    public String getReconciliationMethod() { return reconciliationMethod; }
    public void setReconciliationMethod(String reconciliationMethod) { this.reconciliationMethod = reconciliationMethod; }

    public String getBankStatementFile() { return bankStatementFile; }
    public void setBankStatementFile(String bankStatementFile) { this.bankStatementFile = bankStatementFile; }

    public String getBankStatementFormat() { return bankStatementFormat; }
    public void setBankStatementFormat(String bankStatementFormat) { this.bankStatementFormat = bankStatementFormat; }

    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }

    public LocalDateTime getLockedAt() { return lockedAt; }
    public void setLockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; }

    public Long getLockedBy() { return lockedBy; }
    public void setLockedBy(Long lockedBy) { this.lockedBy = lockedBy; }

    public String getLockReason() { return lockReason; }
    public void setLockReason(String lockReason) { this.lockReason = lockReason; }

    @Override
    public String toString() {
        return reconciliationNumber + " - " + status + " (Compte: " + bankAccountId + ")";
    }
}


