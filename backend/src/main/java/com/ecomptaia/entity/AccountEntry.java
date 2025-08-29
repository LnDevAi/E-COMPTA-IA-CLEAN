package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_entries")
public class AccountEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long journalEntryId; // ID de l'écriture parente

    @Column(nullable = false, length = 20)
    private String accountNumber; // Numéro de compte

    @Column(nullable = false)
    private String accountName; // Nom du compte

    @Column(nullable = false, length = 20)
    private String accountType; // DEBIT, CREDIT

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount; // Montant

    @Column(length = 500)
    private String description; // Description de la ligne

    @Column(length = 20)
    private String thirdPartyCode; // Code du tiers (optionnel)

    @Column
    private String reference; // Référence externe

    @Column
    private String documentNumber; // Numéro de document

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

    @Column
    private Boolean isReconciled = false; // Si la ligne est rapprochée

    @Column
    private String reconciliationReference; // Référence de rapprochement

    @Column
    private LocalDateTime reconciledAt; // Date de rapprochement

    @Column
    private Long reconciledBy; // ID de l'utilisateur qui a rapproché

    // Constructeurs
    public AccountEntry() {}

    public AccountEntry(Long journalEntryId, String accountNumber, String accountName, 
                       String accountType, BigDecimal amount, Long companyId, 
                       String countryCode, String accountingStandard) {
        this.journalEntryId = journalEntryId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.accountType = accountType;
        this.amount = amount;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getJournalEntryId() { return journalEntryId; }
    public void setJournalEntryId(Long journalEntryId) { this.journalEntryId = journalEntryId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getThirdPartyCode() { return thirdPartyCode; }
    public void setThirdPartyCode(String thirdPartyCode) { this.thirdPartyCode = thirdPartyCode; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

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

    public Boolean getIsReconciled() { return isReconciled; }
    public void setIsReconciled(Boolean isReconciled) { this.isReconciled = isReconciled; }

    public String getReconciliationReference() { return reconciliationReference; }
    public void setReconciliationReference(String reconciliationReference) { this.reconciliationReference = reconciliationReference; }

    public LocalDateTime getReconciledAt() { return reconciledAt; }
    public void setReconciledAt(LocalDateTime reconciledAt) { this.reconciledAt = reconciledAt; }

    public Long getReconciledBy() { return reconciledBy; }
    public void setReconciledBy(Long reconciledBy) { this.reconciledBy = reconciledBy; }

    @Override
    public String toString() {
        return accountNumber + " - " + accountType + " " + amount;
    }
}





