package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictive_metrics")
public class PredictiveMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private MetricCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TrendDirection trend;
    
    @Column
    private Integer confidence;
    
    @Column
    private Boolean isPositive;
    
    @Column
    private String modelVersion;
    
    @Column
    private LocalDateTime lastUpdated;
    
    @Column
    private LocalDateTime nextUpdate;
    
    public enum MetricCategory {
        REVENUE, COST, CASH_FLOW, INVENTORY, PAYROLL, RISK, OTHER
    }
    
    public enum TrendDirection {
        INCREASING, DECREASING, STABLE, VOLATILE
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public MetricCategory getCategory() { return category; }
    public void setCategory(MetricCategory category) { this.category = category; }
    
    public TrendDirection getTrend() { return trend; }
    public void setTrend(TrendDirection trend) { this.trend = trend; }
    
    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }
    
    public Boolean getIsPositive() { return isPositive; }
    public void setIsPositive(Boolean positive) { isPositive = positive; }
    
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public LocalDateTime getNextUpdate() { return nextUpdate; }
    public void setNextUpdate(LocalDateTime nextUpdate) { this.nextUpdate = nextUpdate; }
}