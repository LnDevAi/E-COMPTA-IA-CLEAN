package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.ecomptaia.entity.CompanySubscription;

@Entity
@Table(name = "subscription_usage")
@SuppressWarnings("unused")
public class SubscriptionUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_subscription_id", nullable = false)
    private CompanySubscription companySubscription;
    
    @Column(name = "feature_code", nullable = false)
    private String featureCode;
    
    @Column(name = "feature_name", nullable = false)
    private String featureName;
    
    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;
    
    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;
    
    @Column(name = "limit_value")
    private Integer limitValue;
    
    @Column(name = "usage_percentage")
    private Double usagePercentage;
    
    @Column(name = "is_over_limit", nullable = false)
    private Boolean isOverLimit = false;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "version")
    private Integer version = 1;
    
    // Constructeurs
    public SubscriptionUsage() {
        this.createdAt = LocalDateTime.now();
    }
    
    public SubscriptionUsage(CompanySubscription companySubscription, String featureCode, String featureName, LocalDate usageDate) {
        this();
        this.companySubscription = companySubscription;
        this.featureCode = featureCode;
        this.featureName = featureName;
        this.usageDate = usageDate;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public CompanySubscription getCompanySubscription() {
        return companySubscription;
    }
    
    public void setCompanySubscription(CompanySubscription companySubscription) {
        this.companySubscription = companySubscription;
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
    
    public LocalDate getUsageDate() {
        return usageDate;
    }
    
    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }
    
    public Integer getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }
    
    public Integer getLimitValue() {
        return limitValue;
    }
    
    public void setLimitValue(Integer limitValue) {
        this.limitValue = limitValue;
    }
    
    public Double getUsagePercentage() {
        return usagePercentage;
    }
    
    public void setUsagePercentage(Double usagePercentage) {
        this.usagePercentage = usagePercentage;
    }
    
    public Boolean getIsOverLimit() {
        return isOverLimit;
    }
    
    public void setIsOverLimit(Boolean isOverLimit) {
        this.isOverLimit = isOverLimit;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public void incrementUsage() {
        this.usageCount++;
        calculateUsagePercentage();
        checkOverLimit();
    }
    
    public void addUsage(Integer count) {
        this.usageCount += count;
        calculateUsagePercentage();
        checkOverLimit();
    }
    
    private void calculateUsagePercentage() {
        if (this.limitValue != null && this.limitValue > 0) {
            this.usagePercentage = (double) this.usageCount / this.limitValue * 100;
        }
    }
    
    private void checkOverLimit() {
        if (this.limitValue != null && this.limitValue > 0) {
            this.isOverLimit = this.usageCount > this.limitValue;
        }
    }
    
    public boolean hasLimit() {
        return this.limitValue != null && this.limitValue > 0;
    }
    
    public boolean isNearLimit() {
        if (!hasLimit()) return false;
        return this.usagePercentage != null && this.usagePercentage >= 80.0;
    }
    
    public Integer getRemainingUsage() {
        if (!hasLimit()) return null;
        return Math.max(0, this.limitValue - this.usageCount);
    }
    
    @Override
    public String toString() {
        return "SubscriptionUsage{" +
                "id=" + id +
                ", featureCode='" + featureCode + '\'' +
                ", featureName='" + featureName + '\'' +
                ", usageDate=" + usageDate +
                ", usageCount=" + usageCount +
                ", limitValue=" + limitValue +
                ", usagePercentage=" + usagePercentage +
                ", isOverLimit=" + isOverLimit +
                '}';
    }
}
