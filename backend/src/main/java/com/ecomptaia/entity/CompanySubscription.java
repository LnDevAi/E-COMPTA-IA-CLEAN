package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "company_subscriptions")
public class CompanySubscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;
    
    @Column(name = "subscription_code", nullable = false, unique = true)
    private String subscriptionCode;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "billing_cycle", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan.BillingCycle billingCycle;
    
    @Column(name = "base_price_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePriceUSD;
    
    @Column(name = "local_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal localPrice;
    
    @Column(name = "local_currency", nullable = false, length = 3)
    private String localCurrency;
    
    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal exchangeRate;
    
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;
    
    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate;
    
    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;
    
    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "version")
    private Integer version = 1;
    
    // Relations
    @OneToMany(mappedBy = "companySubscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubscriptionUsage> usageRecords;
    
    @OneToMany(mappedBy = "companySubscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubscriptionPayment> payments;
    
    // Constructeurs
    public CompanySubscription() {
        this.createdAt = LocalDateTime.now();
    }
    
    public CompanySubscription(Company company, SubscriptionPlan subscriptionPlan, String subscriptionCode, LocalDate startDate) {
        this();
        this.company = company;
        this.subscriptionPlan = subscriptionPlan;
        this.subscriptionCode = subscriptionCode;
        this.startDate = startDate;
        this.billingCycle = subscriptionPlan.getBillingCycle();
        this.basePriceUSD = subscriptionPlan.getBasePriceUSD();
        this.localCurrency = subscriptionPlan.getCurrency();
    }
    
    // Énumérations
    public enum SubscriptionStatus {
        ACTIVE, SUSPENDED, CANCELLED, EXPIRED, PENDING, TRIAL
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Company getCompany() {
        return company;
    }
    
    public void setCompany(Company company) {
        this.company = company;
    }
    
    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }
    
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }
    
    public String getSubscriptionCode() {
        return subscriptionCode;
    }
    
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public SubscriptionPlan.BillingCycle getBillingCycle() {
        return billingCycle;
    }
    
    public void setBillingCycle(SubscriptionPlan.BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }
    
    public BigDecimal getBasePriceUSD() {
        return basePriceUSD;
    }
    
    public void setBasePriceUSD(BigDecimal basePriceUSD) {
        this.basePriceUSD = basePriceUSD;
    }
    
    public BigDecimal getLocalPrice() {
        return localPrice;
    }
    
    public void setLocalPrice(BigDecimal localPrice) {
        this.localPrice = localPrice;
    }
    
    public String getLocalCurrency() {
        return localCurrency;
    }
    
    public void setLocalCurrency(String localCurrency) {
        this.localCurrency = localCurrency;
    }
    
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
    
    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
    
    public SubscriptionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }
    
    public Boolean getAutoRenew() {
        return autoRenew;
    }
    
    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }
    
    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
    
    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }
    
    public void setNextPaymentDate(LocalDate nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }
    
    public LocalDate getCancellationDate() {
        return cancellationDate;
    }
    
    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
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
    
    public List<SubscriptionUsage> getUsageRecords() {
        return usageRecords;
    }
    
    public void setUsageRecords(List<SubscriptionUsage> usageRecords) {
        this.usageRecords = usageRecords;
    }
    
    public List<SubscriptionPayment> getPayments() {
        return payments;
    }
    
    public void setPayments(List<SubscriptionPayment> payments) {
        this.payments = payments;
    }
    
    // Méthodes utilitaires
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE || this.status == SubscriptionStatus.TRIAL;
    }
    
    public boolean isExpired() {
        return this.endDate != null && this.endDate.isBefore(LocalDate.now());
    }
    
    public boolean isTrial() {
        return this.status == SubscriptionStatus.TRIAL;
    }
    
    public void calculateFinalPrice() {
        if (this.localPrice == null) {
            this.localPrice = this.basePriceUSD;
        }
        
        BigDecimal price = this.localPrice;
        
        if (this.discountPercentage != null && this.discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = price.multiply(this.discountPercentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            price = price.subtract(discount);
        }
        
        this.finalPrice = price.setScale(2, RoundingMode.HALF_UP);
    }
    
    public void setLocalizedPricing(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
        if (exchangeRate != null && exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            this.localPrice = this.basePriceUSD.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.localPrice = this.basePriceUSD;
        }
        calculateFinalPrice();
    }
    
    @Override
    public String toString() {
        return "CompanySubscription{" +
                "id=" + id +
                ", subscriptionCode='" + subscriptionCode + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", billingCycle=" + billingCycle +
                ", status=" + status +
                ", finalPrice=" + finalPrice +
                ", localCurrency='" + localCurrency + '\'' +
                '}';
    }
}
