package com.ecomptaia.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * EntitÃ© pour la configuration internationale
 * RÃ©volutionnaire vs TOMPRO - Multi-pays natif
 */
@Entity
@Table(name = "international_configurations")
public class InternationalConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 3)
    private String countryCode; // ISO 3166-1 alpha-3
    
    @Column(nullable = false, length = 100)
    private String countryName;
    
    @Column(nullable = false, length = 50)
    private String region; // WAEMU, CEMAC, EU, etc.
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountingStandard accountingStandard;
    
    @Column(nullable = false, length = 3)
    private String defaultCurrency;
    
    @ElementCollection
    @CollectionTable(name = "supported_currencies", joinColumns = @JoinColumn(name = "config_id"))
    @Column(name = "currency_code", length = 3)
    private List<String> supportedCurrencies = new ArrayList<>();
    
    @Column(nullable = false, length = 5)
    private String locale; // fr-FR, en-US, etc.
    
    @Column(nullable = false, length = 50)
    private String timezone;
    
    @Column(nullable = false, length = 20)
    private String dateFormat;
    
    @Column(nullable = false, length = 20)
    private String numberFormat;
    
    @Column(nullable = false, length = 20)
    private String currencyFormat;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComplianceRule> complianceRules = new ArrayList<>();
    
    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaxConfiguration> taxConfigurations = new ArrayList<>();
    
    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReportingRequirement> reportingRequirements = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // Configuration additionnelle en JSON
    
    // Constructeurs
    public InternationalConfiguration() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public InternationalConfiguration(String countryCode, String countryName, String region, AccountingStandard standard) {
        this();
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.region = region;
        this.accountingStandard = standard;
    }
    
    // Ã‰numÃ©rations
    public enum AccountingStandard {
        SYCEBNL, IFRS, GAAP, OHADA, LOCAL
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public AccountingStandard getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(AccountingStandard accountingStandard) { this.accountingStandard = accountingStandard; }
    
    public String getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }
    
    public List<String> getSupportedCurrencies() { return supportedCurrencies; }
    public void setSupportedCurrencies(List<String> supportedCurrencies) { this.supportedCurrencies = supportedCurrencies; }
    
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
    
    public String getNumberFormat() { return numberFormat; }
    public void setNumberFormat(String numberFormat) { this.numberFormat = numberFormat; }
    
    public String getCurrencyFormat() { return currencyFormat; }
    public void setCurrencyFormat(String currencyFormat) { this.currencyFormat = currencyFormat; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<ComplianceRule> getComplianceRules() { return complianceRules; }
    public void setComplianceRules(List<ComplianceRule> complianceRules) { this.complianceRules = complianceRules; }
    
    public List<TaxConfiguration> getTaxConfigurations() { return taxConfigurations; }
    public void setTaxConfigurations(List<TaxConfiguration> taxConfigurations) { this.taxConfigurations = taxConfigurations; }
    
    public List<ReportingRequirement> getReportingRequirements() { return reportingRequirements; }
    public void setReportingRequirements(List<ReportingRequirement> reportingRequirements) { this.reportingRequirements = reportingRequirements; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "InternationalConfiguration{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", region='" + region + '\'' +
                ", accountingStandard=" + accountingStandard +
                ", defaultCurrency='" + defaultCurrency + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}




