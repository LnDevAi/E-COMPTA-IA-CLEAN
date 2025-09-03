package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_payments")
public class SubscriptionPayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_subscription_id", nullable = false)
    private CompanySubscription companySubscription;
    
    @Column(name = "payment_code", nullable = false, unique = true)
    private String paymentCode;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal exchangeRate;
    
    @Column(name = "amount_usd", precision = 10, scale = 2)
    private BigDecimal amountUSD;
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(name = "billing_period_start")
    private LocalDate billingPeriodStart;
    
    @Column(name = "billing_period_end")
    private LocalDate billingPeriodEnd;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "version")
    private Integer version = 1;
    
    // Constructeurs
    public SubscriptionPayment() {
        this.createdAt = LocalDateTime.now();
    }
    
    public SubscriptionPayment(CompanySubscription companySubscription, String paymentCode, LocalDate paymentDate, BigDecimal amount, String currency) {
        this();
        this.companySubscription = companySubscription;
        this.paymentCode = paymentCode;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.currency = currency;
    }
    
    // Énumérations
    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
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
    
    public String getPaymentCode() {
        return paymentCode;
    }
    
    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    
    public BigDecimal getAmountUSD() {
        return amountUSD;
    }
    
    public void setAmountUSD(BigDecimal amountUSD) {
        this.amountUSD = amountUSD;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public LocalDate getBillingPeriodStart() {
        return billingPeriodStart;
    }
    
    public void setBillingPeriodStart(LocalDate billingPeriodStart) {
        this.billingPeriodStart = billingPeriodStart;
    }
    
    public LocalDate getBillingPeriodEnd() {
        return billingPeriodEnd;
    }
    
    public void setBillingPeriodEnd(LocalDate billingPeriodEnd) {
        this.billingPeriodEnd = billingPeriodEnd;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }
    
    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }
    
    public boolean isFailed() {
        return this.status == PaymentStatus.FAILED;
    }
    
    public boolean isRefunded() {
        return this.status == PaymentStatus.REFUNDED || this.status == PaymentStatus.PARTIALLY_REFUNDED;
    }
    
    public void calculateAmountUSD() {
        if (this.exchangeRate != null && this.exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            this.amountUSD = this.amount.divide(this.exchangeRate, 2, RoundingMode.HALF_UP);
        } else if ("USD".equals(this.currency)) {
            this.amountUSD = this.amount;
        }
    }
    
    public void setExchangeRateAndCalculateUSD(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
        calculateAmountUSD();
    }
    
    public String getStatusDisplayName() {
        switch (this.status) {
            case PENDING: return "En attente";
            case COMPLETED: return "Complété";
            case FAILED: return "Échoué";
            case CANCELLED: return "Annulé";
            case REFUNDED: return "Remboursé";
            case PARTIALLY_REFUNDED: return "Partiellement remboursé";
            default: return this.status.name();
        }
    }
    
    @Override
    public String toString() {
        return "SubscriptionPayment{" +
                "id=" + id +
                ", paymentCode='" + paymentCode + '\'' +
                ", paymentDate=" + paymentDate +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
