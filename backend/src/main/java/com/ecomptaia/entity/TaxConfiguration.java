package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Entité pour la configuration fiscale par pays
 */
@Entity
@Table(name = "tax_configurations")
public class TaxConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String taxName;
    
    @Column(nullable = false, length = 20)
    private String taxCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaxType type;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column
    private LocalDateTime effectiveFrom;
    
    @Column
    private LocalDateTime effectiveTo;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuration_id")
    private InternationalConfiguration configuration;
    
    // Énumérations
    public enum TaxType {
        VAT, INCOME_TAX, CORPORATE_TAX, PAYROLL_TAX, CUSTOMS_DUTY, STAMP_TAX
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTaxName() { return taxName; }
    public void setTaxName(String taxName) { this.taxName = taxName; }
    
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }
    
    public TaxType getType() { return type; }
    public void setType(TaxType type) { this.type = type; }
    
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    
    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public InternationalConfiguration getConfiguration() { return configuration; }
    public void setConfiguration(InternationalConfiguration configuration) { this.configuration = configuration; }
}




