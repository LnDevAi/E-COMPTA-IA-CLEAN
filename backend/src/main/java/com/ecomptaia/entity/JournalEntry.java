package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entryNumber; // Numéro d'écriture

    @Column(nullable = false)
    private LocalDate entryDate; // Date de l'écriture

    @Column(nullable = false)
    private String description; // Description de l'écriture

    @Column(nullable = false, length = 20)
    private String journalType; // Type de journal (ACHATS, VENTES, BANQUE, etc.)

    @Column(nullable = false, length = 3)
    private String currency; // Devise de l'écriture

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalDebit; // Total débit

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalCredit; // Total crédit

    @Column(nullable = false, length = 20)
    private String status; // BROUILLON, VALIDÉ, ANNULE

    @Column(length = 500)
    private String notes; // Notes additionnelles

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays

    @Column(nullable = false, length = 20)
    private String accountingStandard; // Standard comptable

    @Column
    private String reference; // Référence externe

    @Column
    private String documentNumber; // Numéro de document source

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private Long createdBy; // ID de l'utilisateur créateur

    @Column
    private Long validatedBy; // ID de l'utilisateur validateur

    @Column
    private LocalDateTime validatedAt; // Date de validation

    @Column
    private Boolean isReconciled = false; // Si l'écriture est rapprochée

    @Column
    private Boolean isPosted = false; // Si l'écriture est comptabilisée

    // Constructeurs
    public JournalEntry() {}

    public JournalEntry(String entryNumber, LocalDate entryDate, String description, 
                       String journalType, String currency, BigDecimal totalDebit, 
                       BigDecimal totalCredit, Long companyId, String countryCode, 
                       String accountingStandard) {
        this.entryNumber = entryNumber;
        this.entryDate = entryDate;
        this.description = description;
        this.journalType = journalType;
        this.currency = currency;
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.status = "BROUILLON";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntryNumber() { return entryNumber; }
    public void setEntryNumber(String entryNumber) { this.entryNumber = entryNumber; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getJournalType() { return journalType; }
    public void setJournalType(String journalType) { this.journalType = journalType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }

    public BigDecimal getTotalCredit() { return totalCredit; }
    public void setTotalCredit(BigDecimal totalCredit) { this.totalCredit = totalCredit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getValidatedBy() { return validatedBy; }
    public void setValidatedBy(Long validatedBy) { this.validatedBy = validatedBy; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }

    public Boolean getIsReconciled() { return isReconciled; }
    public void setIsReconciled(Boolean isReconciled) { this.isReconciled = isReconciled; }

    public Boolean getIsPosted() { return isPosted; }
    public void setIsPosted(Boolean isPosted) { this.isPosted = isPosted; }

    @Override
    public String toString() {
        return entryNumber + " - " + description + " (" + status + ")";
    }
}





