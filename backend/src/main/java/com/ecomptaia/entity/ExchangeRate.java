package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 3)
    private String fromCurrency; // Devise source
    
    @Column(nullable = false, length = 3)
    private String toCurrency; // Devise cible
    
    @Column(precision = 10, scale = 6, nullable = false)
    private BigDecimal rate; // Taux de change
    
    @Column(nullable = false)
    private LocalDate date; // Date du taux
    
    @Column
    private LocalDateTime createdAt;
    
    @Column(length = 100)
    private String source; // Source du taux (API, manuel, etc.)
    
    @Column
    private Boolean isActive = true;
    
    @Column(length = 500)
    private String notes;
    
    // Constructeurs
    public ExchangeRate() {}
    
    public ExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate, LocalDate date) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.date = date;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public ExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate, LocalDate date, String source) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.date = date;
        this.source = source;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    
    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    @Override
    public String toString() {
        return fromCurrency + "/" + toCurrency + " = " + rate + " (" + date + ")";
    }
}





