package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EntitÃ© pour les paramÃ¨tres de localisation par entreprise
 */
@Entity
@Table(name = "locale_settings")
public class LocaleSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(name = "language_code", nullable = false, length = 5)
    private String languageCode; // fr, en, es, de, etc.
    
    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode; // FR, US, SN, CI, etc.
    
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode; // EUR, USD, XOF, XAF, etc.
    
    @Column(name = "date_format", nullable = false)
    private String dateFormat; // dd/MM/yyyy, MM/dd/yyyy, yyyy-MM-dd
    
    @Column(name = "time_format", nullable = false)
    private String timeFormat; // 24h, 12h
    
    @Column(name = "number_format", nullable = false)
    private String numberFormat; // 1,234.56 ou 1 234,56
    
    @Column(name = "decimal_separator", nullable = false, length = 1)
    private String decimalSeparator; // . ou ,
    
    @Column(name = "thousands_separator", nullable = false, length = 1)
    private String thousandsSeparator; // , ou ' ou espace
    
    @Column(name = "timezone", nullable = false)
    private String timezone; // Europe/Paris, America/New_York, etc.
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public LocaleSettings() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocaleSettings(Long companyId, String languageCode, String countryCode, String currencyCode) {
        this();
        this.companyId = companyId;
        this.languageCode = languageCode;
        this.countryCode = countryCode;
        this.currencyCode = currencyCode;
        setDefaultFormats(countryCode);
    }
    
    /**
     * DÃ©finit les formats par dÃ©faut selon le pays
     */
    private void setDefaultFormats(String countryCode) {
        switch (countryCode.toUpperCase()) {
            case "FR":
                this.dateFormat = "dd/MM/yyyy";
                this.timeFormat = "24h";
                this.numberFormat = "1 234,56";
                this.decimalSeparator = ",";
                this.thousandsSeparator = " ";
                this.timezone = "Europe/Paris";
                break;
            case "US":
                this.dateFormat = "MM/dd/yyyy";
                this.timeFormat = "12h";
                this.numberFormat = "1,234.56";
                this.decimalSeparator = ".";
                this.thousandsSeparator = ",";
                this.timezone = "America/New_York";
                break;
            case "SN": case "CI": case "BF": case "ML": case "NE": case "TG":
                this.dateFormat = "dd/MM/yyyy";
                this.timeFormat = "24h";
                this.numberFormat = "1 234,56";
                this.decimalSeparator = ",";
                this.thousandsSeparator = " ";
                this.timezone = "Africa/Dakar";
                break;
            case "DE":
                this.dateFormat = "dd.MM.yyyy";
                this.timeFormat = "24h";
                this.numberFormat = "1.234,56";
                this.decimalSeparator = ",";
                this.thousandsSeparator = ".";
                this.timezone = "Europe/Berlin";
                break;
            default:
                this.dateFormat = "yyyy-MM-dd";
                this.timeFormat = "24h";
                this.numberFormat = "1,234.56";
                this.decimalSeparator = ".";
                this.thousandsSeparator = ",";
                this.timezone = "UTC";
        }
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
    
    public String getTimeFormat() { return timeFormat; }
    public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }
    
    public String getNumberFormat() { return numberFormat; }
    public void setNumberFormat(String numberFormat) { this.numberFormat = numberFormat; }
    
    public String getDecimalSeparator() { return decimalSeparator; }
    public void setDecimalSeparator(String decimalSeparator) { this.decimalSeparator = decimalSeparator; }
    
    public String getThousandsSeparator() { return thousandsSeparator; }
    public void setThousandsSeparator(String thousandsSeparator) { this.thousandsSeparator = thousandsSeparator; }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Retourne la locale complÃ¨te (ex: fr_FR, en_US)
     */
    public String getFullLocale() {
        return languageCode + "_" + countryCode;
    }
    
    /**
     * Retourne la locale Java
     */
    public java.util.Locale getJavaLocale() {
        return new java.util.Locale(languageCode, countryCode);
    }
}


