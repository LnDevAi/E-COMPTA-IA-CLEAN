package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "taxes")
public class Tax {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays (SN, FR, US, etc.)
    
    @Column(nullable = false)
    private String countryName; // Nom du pays
    
    @Column(nullable = false)
    private String taxType; // Type de taxe (VAT, CORPORATE_INCOME, WITHHOLDING, etc.)
    
    @Column(nullable = false)
    private String taxName; // Nom de la taxe
    
    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal rate; // Taux de la taxe (en pourcentage)
    
    @Column(length = 10)
    private String currency; // Devise de la taxe
    
    @Column
    private BigDecimal minimumThreshold; // Seuil minimum
    
    @Column
    private BigDecimal maximumThreshold; // Seuil maximum
    
    @Column(length = 500)
    private String description; // Description de la taxe
    
    @Column
    private Boolean isActive = true;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column(length = 100)
    private String source; // Source de l'information
    
    @Column(length = 50)
    private String frequency; // Fréquence (monthly, quarterly, annually)
    
    @Column
    private Boolean isMandatory = true; // Si la taxe est obligatoire
    
    // Constructeurs
    public Tax() {}
    
    public Tax(String countryCode, String countryName, String taxType, String taxName, BigDecimal rate, String currency) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.taxType = taxType;
        this.taxName = taxName;
        this.rate = rate;
        this.currency = currency;
        this.isActive = true;
        this.isMandatory = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Tax(String countryCode, String countryName, String taxType, String taxName, 
               BigDecimal rate, String currency, BigDecimal minimumThreshold, BigDecimal maximumThreshold, 
               String description, String frequency) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.taxType = taxType;
        this.taxName = taxName;
        this.rate = rate;
        this.currency = currency;
        this.minimumThreshold = minimumThreshold;
        this.maximumThreshold = maximumThreshold;
        this.description = description;
        this.frequency = frequency;
        this.isActive = true;
        this.isMandatory = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    
    public String getTaxType() { return taxType; }
    public void setTaxType(String taxType) { this.taxType = taxType; }
    
    public String getTaxName() { return taxName; }
    public void setTaxName(String taxName) { this.taxName = taxName; }
    
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { 
        this.rate = rate; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public BigDecimal getMinimumThreshold() { return minimumThreshold; }
    public void setMinimumThreshold(BigDecimal minimumThreshold) { this.minimumThreshold = minimumThreshold; }
    
    public BigDecimal getMaximumThreshold() { return maximumThreshold; }
    public void setMaximumThreshold(BigDecimal maximumThreshold) { this.maximumThreshold = maximumThreshold; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    
    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }
    
    @Override
    public String toString() {
        return countryName + " - " + taxName + " (" + rate + "%)";
    }
}





