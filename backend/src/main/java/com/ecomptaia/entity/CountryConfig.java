package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "country_configs")
public class CountryConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3)
    private String countryCode; // Code ISO du pays (BF, CI, FR, US, etc.)

    @Column(nullable = false, length = 100)
    private String countryName; // Nom du pays

    @Column(nullable = false, length = 10)
    private String currency; // Code devise (XOF, EUR, USD, etc.)

    @Column(nullable = false, length = 20)
    private String accountingStandard; // SYSCOHADA, IFRS, US_GAAP, PCG, etc.

    @Column(nullable = false, length = 20)
    private String systemType; // NORMAL, MINIMAL, BOTH

    @Column(length = 500)
    private String businessCreationPlatform; // Plateforme de création d'entreprise

    @Column(length = 500)
    private String businessCreationWebsite; // Site web officiel

    @Column
    private Boolean businessCreationApiAvailable; // API disponible pour création

    @Column(length = 500)
    private String businessCreationApiUrl; // URL de l'API

    @Column(length = 500)
    private String taxAdministrationWebsite; // Site administration fiscale

    @Column(length = 500)
    private String taxAdministrationApiUrl; // API administration fiscale

    @Column(length = 500)
    private String socialSecurityWebsite; // Site sécurité sociale

    @Column(length = 500)
    private String socialSecurityApiUrl; // API sécurité sociale

    @Column(length = 500)
    private String centralBankWebsite; // Site banque centrale

    @Column(length = 500)
    private String centralBankApiUrl; // API banque centrale

    @Column(length = 1000)
    private String paymentSystems; // Systèmes de paiement (JSON)

    @Column(length = 1000)
    private String officialApis; // APIs officielles (JSON)

    @Column(length = 1000)
    private String regulations; // Réglementations (JSON)

    @Column(length = 1000)
    private String economicIndicators; // Indicateurs économiques (JSON)

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, TESTING

    @Column(length = 10)
    private String version = "1.0";

    // Constructeurs
    public CountryConfig() {}

    public CountryConfig(String countryCode, String countryName, String currency, 
                        String accountingStandard, String systemType) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.currency = currency;
        this.accountingStandard = accountingStandard;
        this.systemType = systemType;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public String getSystemType() { return systemType; }
    public void setSystemType(String systemType) { this.systemType = systemType; }

    public String getBusinessCreationPlatform() { return businessCreationPlatform; }
    public void setBusinessCreationPlatform(String businessCreationPlatform) { this.businessCreationPlatform = businessCreationPlatform; }

    public String getBusinessCreationWebsite() { return businessCreationWebsite; }
    public void setBusinessCreationWebsite(String businessCreationWebsite) { this.businessCreationWebsite = businessCreationWebsite; }

    public Boolean getBusinessCreationApiAvailable() { return businessCreationApiAvailable; }
    public void setBusinessCreationApiAvailable(Boolean businessCreationApiAvailable) { this.businessCreationApiAvailable = businessCreationApiAvailable; }

    public String getBusinessCreationApiUrl() { return businessCreationApiUrl; }
    public void setBusinessCreationApiUrl(String businessCreationApiUrl) { this.businessCreationApiUrl = businessCreationApiUrl; }

    public String getTaxAdministrationWebsite() { return taxAdministrationWebsite; }
    public void setTaxAdministrationWebsite(String taxAdministrationWebsite) { this.taxAdministrationWebsite = taxAdministrationWebsite; }

    public String getTaxAdministrationApiUrl() { return taxAdministrationApiUrl; }
    public void setTaxAdministrationApiUrl(String taxAdministrationApiUrl) { this.taxAdministrationApiUrl = taxAdministrationApiUrl; }

    public String getSocialSecurityWebsite() { return socialSecurityWebsite; }
    public void setSocialSecurityWebsite(String socialSecurityWebsite) { this.socialSecurityWebsite = socialSecurityWebsite; }

    public String getSocialSecurityApiUrl() { return socialSecurityApiUrl; }
    public void setSocialSecurityApiUrl(String socialSecurityApiUrl) { this.socialSecurityApiUrl = socialSecurityApiUrl; }

    public String getCentralBankWebsite() { return centralBankWebsite; }
    public void setCentralBankWebsite(String centralBankWebsite) { this.centralBankWebsite = centralBankWebsite; }

    public String getCentralBankApiUrl() { return centralBankApiUrl; }
    public void setCentralBankApiUrl(String centralBankApiUrl) { this.centralBankApiUrl = centralBankApiUrl; }

    public String getPaymentSystems() { return paymentSystems; }
    public void setPaymentSystems(String paymentSystems) { this.paymentSystems = paymentSystems; }

    public String getOfficialApis() { return officialApis; }
    public void setOfficialApis(String officialApis) { this.officialApis = officialApis; }

    public String getRegulations() { return regulations; }
    public void setRegulations(String regulations) { this.regulations = regulations; }

    public String getEconomicIndicators() { return economicIndicators; }
    public void setEconomicIndicators(String economicIndicators) { this.economicIndicators = economicIndicators; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    @Override
    public String toString() {
        return "CountryConfig{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", accountingStandard='" + accountingStandard + '\'' +
                ", systemType='" + systemType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}




