package com.ecomptaia.security.entity;

import com.ecomptaia.accounting.entity.AccountingStandard;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3, unique = true)
    private String code; // Code pays ISO (ex: FR, SN, US)

    @Column(nullable = false, length = 100)
    private String name; // Nom du pays

    @Column(length = 100)
    private String nativeName; // Nom dans la langue locale

    @Column(length = 3)
    private String currencyCode; // Code devise (ex: EUR, XOF, USD)

    @Column(length = 50)
    private String currencyName; // Nom de la devise

    @Column(length = 20)
    private String accountingStandard; // Standard comptable (ex: PCG, SYSCOHADA, IFRS)

    @Column(length = 10)
    private String locale; // Locale (ex: fr_FR, fr_SN, en_US)

    @Column
    private Double vatRate; // Taux de TVA par défaut

    @Column
    private Double corporateTaxRate; // Taux d'impôt sur les sociétés

    @Column
    private Boolean isActive = true; // Si le pays est actif

    @Column
    private Integer easeOfDoingBusinessIndex; // Indice de facilité de faire des affaires

    @Column(length = 50)
    private String ohadaZone; // Zone OHADA

    @Column(length = 50)
    private String region; // Région

    @Column(length = 50)
    private String officialLanguage; // Langue officielle

    @Column(length = 50)
    private String timezone; // Fuseau horaire

    @Column(length = 50)
    private String continent; // Continent

    @Column
    private Long population; // Population

    @Column
    private Double gdp; // PIB

    @Column
    private Double developmentIndex; // Indice de développement

    @Column(length = 50)
    private String economyType; // Type d'économie

    @Column(length = 50)
    private String taxSystem; // Système fiscal

    @Column(length = 50)
    private String socialSecuritySystem; // Système de sécurité sociale

    @Column(length = 500)
    private String accountingRegulations; // Réglementations comptables

    @Column(length = 500)
    private String taxStandards; // Normes fiscales

    @Column
    private Double inflationRate; // Taux d'inflation

    @Column
    private Double unemploymentRate; // Taux de chômage

    @Column
    private Integer corruptionIndex; // Indice de corruption

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Constructeurs
    public Country() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Country(String code, String name) {
        this();
        this.code = code;
        this.name = name;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getAccountingStandard() {
        return accountingStandard;
    }

    public void setAccountingStandard(String accountingStandard) {
        this.accountingStandard = accountingStandard;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Double getVatRate() {
        return vatRate;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }

    public Double getCorporateTaxRate() {
        return corporateTaxRate;
    }

    public void setCorporateTaxRate(Double corporateTaxRate) {
        this.corporateTaxRate = corporateTaxRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getEaseOfDoingBusinessIndex() {
        return easeOfDoingBusinessIndex;
    }

    public void setEaseOfDoingBusinessIndex(Integer easeOfDoingBusinessIndex) {
        this.easeOfDoingBusinessIndex = easeOfDoingBusinessIndex;
    }

    public String getOhadaZone() {
        return ohadaZone;
    }

    public void setOhadaZone(String ohadaZone) {
        this.ohadaZone = ohadaZone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOfficialLanguage() {
        return officialLanguage;
    }

    public void setOfficialLanguage(String officialLanguage) {
        this.officialLanguage = officialLanguage;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public Double getGdp() {
        return gdp;
    }

    public void setGdp(Double gdp) {
        this.gdp = gdp;
    }

    public Double getDevelopmentIndex() {
        return developmentIndex;
    }

    public void setDevelopmentIndex(Double developmentIndex) {
        this.developmentIndex = developmentIndex;
    }

    public String getEconomyType() {
        return economyType;
    }

    public void setEconomyType(String economyType) {
        this.economyType = economyType;
    }

    public String getTaxSystem() {
        return taxSystem;
    }

    public void setTaxSystem(String taxSystem) {
        this.taxSystem = taxSystem;
    }

    public String getSocialSecuritySystem() {
        return socialSecuritySystem;
    }

    public void setSocialSecuritySystem(String socialSecuritySystem) {
        this.socialSecuritySystem = socialSecuritySystem;
    }

    public String getAccountingRegulations() {
        return accountingRegulations;
    }

    public void setAccountingRegulations(String accountingRegulations) {
        this.accountingRegulations = accountingRegulations;
    }

    public String getTaxStandards() {
        return taxStandards;
    }

    public void setTaxStandards(String taxStandards) {
        this.taxStandards = taxStandards;
    }

    public Double getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(Double inflationRate) {
        this.inflationRate = inflationRate;
    }

    public Double getUnemploymentRate() {
        return unemploymentRate;
    }

    public void setUnemploymentRate(Double unemploymentRate) {
        this.unemploymentRate = unemploymentRate;
    }

    public Integer getCorruptionIndex() {
        return corruptionIndex;
    }

    public void setCorruptionIndex(Integer corruptionIndex) {
        this.corruptionIndex = corruptionIndex;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", accountingStandard='" + accountingStandard + '\'' +
                '}';
    }
}



