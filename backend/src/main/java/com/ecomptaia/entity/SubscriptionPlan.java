package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "plan_name", nullable = false, unique = true)
    private String planName;
    
    @Column(name = "plan_code", nullable = false, unique = true)
    private String planCode;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "base_price_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePriceUSD;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @Column(name = "billing_cycle", nullable = false)
    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;
    
    @Column(name = "max_users")
    private Integer maxUsers;
    
    @Column(name = "max_companies")
    private Integer maxCompanies;
    
    @Column(name = "storage_limit_gb")
    private Integer storageLimitGB;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanStatus status = PlanStatus.ACTIVE;
    
    @Column(name = "version")
    private Integer version = 1;
    
    // Relations
    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanFeature> features;
    
    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompanySubscription> companySubscriptions;
    
    // Constructeurs
    public SubscriptionPlan() {
        this.createdAt = LocalDateTime.now();
    }
    
    public SubscriptionPlan(String planName, String planCode, String description, BigDecimal basePriceUSD, String currency, BillingCycle billingCycle) {
        this();
        this.planName = planName;
        this.planCode = planCode;
        this.description = description;
        this.basePriceUSD = basePriceUSD;
        this.currency = currency;
        this.billingCycle = billingCycle;
    }
    
    // Énumérations
    public enum BillingCycle {
        MONTHLY, QUARTERLY, SEMI_ANNUAL, ANNUAL
    }
    
    public enum PlanStatus {
        ACTIVE, INACTIVE, DEPRECATED, BETA
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPlanName() {
        return planName;
    }
    
    public void setPlanName(String planName) {
        this.planName = planName;
    }
    
    public String getPlanCode() {
        return planCode;
    }
    
    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getBasePriceUSD() {
        return basePriceUSD;
    }
    
    public void setBasePriceUSD(BigDecimal basePriceUSD) {
        this.basePriceUSD = basePriceUSD;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BillingCycle getBillingCycle() {
        return billingCycle;
    }
    
    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }
    
    public Integer getMaxUsers() {
        return maxUsers;
    }
    
    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }
    
    public Integer getMaxCompanies() {
        return maxCompanies;
    }
    
    public void setMaxCompanies(Integer maxCompanies) {
        this.maxCompanies = maxCompanies;
    }
    
    public Integer getStorageLimitGB() {
        return storageLimitGB;
    }
    
    public void setStorageLimitGB(Integer storageLimitGB) {
        this.storageLimitGB = storageLimitGB;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
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
    
    public PlanStatus getStatus() {
        return status;
    }
    
    public void setStatus(PlanStatus status) {
        this.status = status;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public List<PlanFeature> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<PlanFeature> features) {
        this.features = features;
    }
    
    public List<CompanySubscription> getCompanySubscriptions() {
        return companySubscriptions;
    }
    
    public void setCompanySubscriptions(List<CompanySubscription> companySubscriptions) {
        this.companySubscriptions = companySubscriptions;
    }
    
    // Méthodes utilitaires
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public boolean isActive() {
        return this.isActive && this.status == PlanStatus.ACTIVE;
    }
    
    public BigDecimal getLocalizedPrice(String targetCurrency, BigDecimal exchangeRate) {
        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            return this.basePriceUSD;
        }
        return this.basePriceUSD.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public String toString() {
        return "SubscriptionPlan{" +
                "id=" + id +
                ", planName='" + planName + '\'' +
                ", planCode='" + planCode + '\'' +
                ", basePriceUSD=" + basePriceUSD +
                ", currency='" + currency + '\'' +
                ", billingCycle=" + billingCycle +
                ", isActive=" + isActive +
                ", status=" + status +
                '}';
    }
}




