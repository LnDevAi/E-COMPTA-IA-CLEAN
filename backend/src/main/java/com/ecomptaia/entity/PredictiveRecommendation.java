package com.ecomptaia.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EntitÃ© pour les recommandations liÃ©es aux prÃ©dictions
 */
@Entity
@Table(name = "predictive_recommendations")
public class PredictiveRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String recommendation;
    
    @Column(nullable = false)
    private Integer priority; // 1-10
    
    @Column(nullable = false)
    private Boolean isImplemented = false;
    
    @Column
    private LocalDateTime implementedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_id")
    private PredictiveMetric metric;
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Boolean getIsImplemented() { return isImplemented; }
    public void setIsImplemented(Boolean isImplemented) { this.isImplemented = isImplemented; }
    
    public LocalDateTime getImplementedAt() { return implementedAt; }
    public void setImplementedAt(LocalDateTime implementedAt) { this.implementedAt = implementedAt; }
    
    public PredictiveMetric getMetric() { return metric; }
    public void setMetric(PredictiveMetric metric) { this.metric = metric; }
}






