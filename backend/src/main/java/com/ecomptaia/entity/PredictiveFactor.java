package com.ecomptaia.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * EntitÃ© pour les facteurs d'influence des prÃ©dictions
 */
@Entity
@Table(name = "predictive_factors")
public class PredictiveFactor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false)
    private Integer impact; // -100 Ã  +100
    
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal weight; // 0 Ã  1
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Boolean isControllable;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_id")
    private PredictiveMetric metric;
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getImpact() { return impact; }
    public void setImpact(Integer impact) { this.impact = impact; }
    
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsControllable() { return isControllable; }
    public void setIsControllable(Boolean isControllable) { this.isControllable = isControllable; }
    
    public PredictiveMetric getMetric() { return metric; }
    public void setMetric(PredictiveMetric metric) { this.metric = metric; }
}






