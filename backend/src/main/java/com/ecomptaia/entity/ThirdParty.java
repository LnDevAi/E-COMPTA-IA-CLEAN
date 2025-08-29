package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "third_parties")
public class ThirdParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code; // Code unique du tiers

    @Column(nullable = false, length = 20)
    private String accountNumber; // Numéro de sous-compte (classe 4) - ex: 4010001, 4110001

    @Column(nullable = false)
    private String name; // Nom/Raison sociale

    @Column(length = 20)
    private String type; // CLIENT, FOURNISSEUR, BOTH

    @Column(length = 20)
    private String category; // Particulier, Entreprise, Association, etc.

    @Column
    private String taxIdentificationNumber; // Numéro fiscal

    @Column
    private String vatNumber; // Numéro de TVA

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String postalCode;

    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays

    @Column
    private String phone;

    @Column
    private String email;

    @Column
    private String website;

    @Column(length = 3)
    private String currency; // Devise préférée

    @Column(precision = 15, scale = 2)
    private BigDecimal creditLimit; // Limite de crédit

    @Column(precision = 15, scale = 2)
    private BigDecimal currentBalance; // Solde actuel

    @Column(length = 20)
    private String paymentTerms; // Conditions de paiement

    @Column
    private Integer paymentDelay; // Délai de paiement en jours

    @Column
    private Boolean isActive = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false, length = 20)
    private String accountingStandard; // Standard comptable

    @Column(length = 500)
    private String notes; // Notes additionnelles

    @Column
    private String bankAccount; // Compte bancaire

    @Column
    private String bankName; // Nom de la banque

    @Column
    private String swiftCode; // Code SWIFT

    @Column
    private String iban; // Code IBAN

    @Column
    private String contactPerson; // Personne de contact

    @Column
    private String contactPhone; // Téléphone de contact

    @Column
    private String contactEmail; // Email de contact

    // Constructeurs
    public ThirdParty() {}

    public ThirdParty(String code, String name, String type, String countryCode, 
                     Long companyId, String accountingStandard) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.countryCode = countryCode;
        this.companyId = companyId;
        this.accountingStandard = accountingStandard;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ThirdParty(String code, String accountNumber, String name, String type, String countryCode, 
                     Long companyId, String accountingStandard) {
        this.code = code;
        this.accountNumber = accountNumber;
        this.name = name;
        this.type = type;
        this.countryCode = countryCode;
        this.companyId = companyId;
        this.accountingStandard = accountingStandard;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTaxIdentificationNumber() { return taxIdentificationNumber; }
    public void setTaxIdentificationNumber(String taxIdentificationNumber) { this.taxIdentificationNumber = taxIdentificationNumber; }

    public String getVatNumber() { return vatNumber; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public Integer getPaymentDelay() { return paymentDelay; }
    public void setPaymentDelay(Integer paymentDelay) { this.paymentDelay = paymentDelay; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    @Override
    public String toString() {
        return code + " - " + name + " (" + type + ")";
    }
}

