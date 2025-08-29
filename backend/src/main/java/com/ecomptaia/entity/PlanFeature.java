package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.ecomptaia.entity.SubscriptionPlan;

@Entity
@Table(name = "plan_features")
public class PlanFeature {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;
    
    @Column(name = "feature_code", nullable = false)
    private String featureCode;
    
    @Column(name = "feature_name", nullable = false)
    private String featureName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "feature_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeatureType featureType;
    
    @Column(name = "is_included", nullable = false)
    private Boolean isIncluded = true;
    
    @Column(name = "limit_value")
    private Integer limitValue;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeatureStatus status = FeatureStatus.ACTIVE;
    
    @Column(name = "version")
    private Integer version = 1;
    
    // Constructeurs
    public PlanFeature() {
        this.createdAt = LocalDateTime.now();
    }
    
    public PlanFeature(SubscriptionPlan subscriptionPlan, String featureCode, String featureName, FeatureType featureType) {
        this();
        this.subscriptionPlan = subscriptionPlan;
        this.featureCode = featureCode;
        this.featureName = featureName;
        this.featureType = featureType;
    }
    
    // Énumérations
    public enum FeatureType {
        MODULE, FUNCTION, LIMIT, INTEGRATION, SUPPORT, CUSTOMIZATION
    }
    
    public enum FeatureStatus {
        ACTIVE, INACTIVE, DEPRECATED, BETA
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }
    
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }
    
    public String getFeatureCode() {
        return featureCode;
    }
    
    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }
    
    public String getFeatureName() {
        return featureName;
    }
    
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public FeatureType getFeatureType() {
        return featureType;
    }
    
    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }
    
    public Boolean getIsIncluded() {
        return isIncluded;
    }
    
    public void setIsIncluded(Boolean isIncluded) {
        this.isIncluded = isIncluded;
    }
    
    public Integer getLimitValue() {
        return limitValue;
    }
    
    public void setLimitValue(Integer limitValue) {
        this.limitValue = limitValue;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
    
    public FeatureStatus getStatus() {
        return status;
    }
    
    public void setStatus(FeatureStatus status) {
        this.status = status;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    // Méthodes utilitaires
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public boolean isActive() {
        return this.isIncluded && this.status == FeatureStatus.ACTIVE;
    }
    
    public boolean hasLimit() {
        return this.limitValue != null && this.limitValue > 0;
    }
    
    @Override
    public String toString() {
        return "PlanFeature{" +
                "id=" + id +
                ", featureCode='" + featureCode + '\'' +
                ", featureName='" + featureName + '\'' +
                ", featureType=" + featureType +
                ", isIncluded=" + isIncluded +
                ", limitValue=" + limitValue +
                ", status=" + status +
                '}';
    }
}
