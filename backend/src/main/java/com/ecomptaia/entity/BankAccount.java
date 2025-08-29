package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false)
    private String accountNumber; // Numéro de compte bancaire

    @Column(nullable = false)
    private String bankName; // Nom de la banque

    @Column
    private String branchName; // Nom de l'agence

    @Column
    private String swiftCode; // Code SWIFT

    @Column
    private String iban; // Code IBAN

    @Column(nullable = false, length = 3)
    private String currency; // Devise du compte

    @Column(precision = 15, scale = 2)
    private BigDecimal openingBalance; // Solde d'ouverture

    @Column(precision = 15, scale = 2)
    private BigDecimal currentBalance; // Solde actuel

    @Column
    private LocalDateTime lastReconciliationDate; // Dernière date de rapprochement

    @Column
    private Boolean isActive = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays

    @Column(nullable = false, length = 20)
    private String accountingStandard; // Standard comptable

    @Column(length = 20)
    private String accountType; // CHECKING, SAVINGS, CREDIT

    @Column
    private String accountHolder; // Titulaire du compte

    @Column
    private String contactPerson; // Personne de contact

    @Column
    private String contactPhone; // Téléphone de contact

    @Column
    private String contactEmail; // Email de contact

    @Column(length = 500)
    private String notes; // Notes additionnelles

    @Column
    private Boolean isReconciled = false; // Si le compte est rapproché

    @Column
    private LocalDateTime reconciledAt; // Date de rapprochement

    @Column
    private Long reconciledBy; // ID de l'utilisateur qui a rapproché

    // Constructeurs
    public BankAccount() {}

    public BankAccount(Long companyId, String accountNumber, String bankName, 
                      String currency, String countryCode, String accountingStandard) {
        this.companyId = companyId;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.currency = currency;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public LocalDateTime getLastReconciliationDate() { return lastReconciliationDate; }
    public void setLastReconciliationDate(LocalDateTime lastReconciliationDate) { this.lastReconciliationDate = lastReconciliationDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getIsReconciled() { return isReconciled; }
    public void setIsReconciled(Boolean isReconciled) { this.isReconciled = isReconciled; }

    public LocalDateTime getReconciledAt() { return reconciledAt; }
    public void setReconciledAt(LocalDateTime reconciledAt) { this.reconciledAt = reconciledAt; }

    public Long getReconciledBy() { return reconciledBy; }
    public void setReconciledBy(Long reconciledBy) { this.reconciledBy = reconciledBy; }

    @Override
    public String toString() {
        return bankName + " - " + accountNumber + " (" + currency + ")";
    }
}
