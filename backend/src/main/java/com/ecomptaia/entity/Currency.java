package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currencies")
public class Currency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 3)
    private String code; // EUR, USD, XOF, etc.
    
    @Column(nullable = false)
    private String name; // Euro, Dollar US, Franc CFA, etc.
    
    @Column(length = 10)
    private String symbol; // €, $, FCFA, etc.
    
    @Column(precision = 10, scale = 4)
    private BigDecimal exchangeRate; // Taux par rapport à l'EUR (devise de référence)
    
    @Column
    private Boolean isActive = true;
    
    @Column
    private LocalDateTime lastUpdated;
    
    @Column(length = 50)
    private String country; // Pays d'origine de la devise
    
    @Column(length = 500)
    private String description;
    
    // Constructeurs
    public Currency() {}
    
    public Currency(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.isActive = true;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public Currency(String code, String name, String symbol, BigDecimal exchangeRate, String country) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.exchangeRate = exchangeRate;
        this.country = country;
        this.isActive = true;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { 
        this.exchangeRate = exchangeRate; 
        this.lastUpdated = LocalDateTime.now();
    }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return code + " - " + name + " (" + symbol + ")";
    }
}





