package com.ecomptaia.crm.entity;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.entity.ThirdParty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * EntitÃ© CRM Customer - Profil client enrichi avec intelligence artificielle
 * IntÃ¨gre les donnÃ©es ThirdParty existantes avec des fonctionnalitÃ©s CRM avancÃ©es
 */
@Entity
@Table(name = "crm_customers")
public class CrmCustomer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "third_party_id")
    private ThirdParty thirdParty;
    
    // === SCORING IA ===
    @Column(name = "ai_score")
    private Integer aiScore;
    
    @Column(name = "churn_probability", precision = 5, scale = 4)
    private BigDecimal churnProbability;
    
    @Column(name = "lifetime_value_predicted", precision = 15, scale = 2)
    private BigDecimal lifetimeValuePredicted;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_segment")
    private CustomerSegment customerSegment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_behavior")
    private PaymentBehavior paymentBehavior;
    
    @Column(name = "satisfaction_score")
    private Integer satisfactionScore;
    
    // === DONNÃ‰ES COMPORTEMENTALES ===
    @Column(name = "avg_payment_delay")
    private Integer avgPaymentDelay;
    
    @Column(name = "total_revenue_generated", precision = 15, scale = 2)
    private BigDecimal totalRevenueGenerated = BigDecimal.ZERO;
    
    @Column(name = "last_purchase_date")
    private LocalDateTime lastPurchaseDate;
    
    @Column(name = "purchase_frequency", precision = 8, scale = 2)
    private BigDecimal purchaseFrequency;
    
    // === PRÃ‰FÃ‰RENCES COMMUNICATION ===
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_channels", columnDefinition = "jsonb")
    private Set<String> preferredChannels = new HashSet<>();
    
    @Column(name = "email_opt_in")
    private Boolean emailOptIn = true;
    
    @Column(name = "sms_opt_in")
    private Boolean smsOptIn = true;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "social_media_handles", columnDefinition = "jsonb")
    private Map<String, String> socialMediaHandles = new HashMap<>();
    
    @Column(name = "best_contact_time")
    private String bestContactTime;
    
    @Column(name = "language_preference")
    private String languagePreference = "fr";
    
    @Column(name = "timezone")
    private String timezone;
    
    // === INTÃ‰GRATIONS EXTERNES ===
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "external_ids", columnDefinition = "jsonb")
    private Map<String, String> externalIds = new HashMap<>();
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // === CONSTRUCTEURS ===
    
    public CrmCustomer() {}
    
    public CrmCustomer(Long id, Company company, ThirdParty thirdParty, Integer aiScore, 
                      BigDecimal churnProbability, BigDecimal lifetimeValuePredicted, 
                      CustomerSegment customerSegment, PaymentBehavior paymentBehavior, 
                      Integer satisfactionScore, Integer avgPaymentDelay, 
                      BigDecimal totalRevenueGenerated, LocalDateTime lastPurchaseDate, 
                      BigDecimal purchaseFrequency, Set<String> preferredChannels, 
                      Boolean emailOptIn, Boolean smsOptIn, Map<String, String> socialMediaHandles, 
                      String bestContactTime, String languagePreference, String timezone, 
                      Map<String, String> externalIds, Boolean isActive, 
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.company = company;
        this.thirdParty = thirdParty;
        this.aiScore = aiScore;
        this.churnProbability = churnProbability;
        this.lifetimeValuePredicted = lifetimeValuePredicted;
        this.customerSegment = customerSegment;
        this.paymentBehavior = paymentBehavior;
        this.satisfactionScore = satisfactionScore;
        this.avgPaymentDelay = avgPaymentDelay;
        this.totalRevenueGenerated = totalRevenueGenerated;
        this.lastPurchaseDate = lastPurchaseDate;
        this.purchaseFrequency = purchaseFrequency;
        this.preferredChannels = preferredChannels;
        this.emailOptIn = emailOptIn;
        this.smsOptIn = smsOptIn;
        this.socialMediaHandles = socialMediaHandles;
        this.bestContactTime = bestContactTime;
        this.languagePreference = languagePreference;
        this.timezone = timezone;
        this.externalIds = externalIds;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // === GETTERS ET SETTERS ===
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public ThirdParty getThirdParty() { return thirdParty; }
    public void setThirdParty(ThirdParty thirdParty) { this.thirdParty = thirdParty; }
    
    public Integer getAiScore() { return aiScore; }
    public void setAiScore(Integer aiScore) { this.aiScore = aiScore; }
    
    public BigDecimal getChurnProbability() { return churnProbability; }
    public void setChurnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; }
    
    public BigDecimal getLifetimeValuePredicted() { return lifetimeValuePredicted; }
    public void setLifetimeValuePredicted(BigDecimal lifetimeValuePredicted) { this.lifetimeValuePredicted = lifetimeValuePredicted; }
    
    public CustomerSegment getCustomerSegment() { return customerSegment; }
    public void setCustomerSegment(CustomerSegment customerSegment) { this.customerSegment = customerSegment; }
    
    public PaymentBehavior getPaymentBehavior() { return paymentBehavior; }
    public void setPaymentBehavior(PaymentBehavior paymentBehavior) { this.paymentBehavior = paymentBehavior; }
    
    public Integer getSatisfactionScore() { return satisfactionScore; }
    public void setSatisfactionScore(Integer satisfactionScore) { this.satisfactionScore = satisfactionScore; }
    
    public Integer getAvgPaymentDelay() { return avgPaymentDelay; }
    public void setAvgPaymentDelay(Integer avgPaymentDelay) { this.avgPaymentDelay = avgPaymentDelay; }
    
    public BigDecimal getTotalRevenueGenerated() { return totalRevenueGenerated; }
    public void setTotalRevenueGenerated(BigDecimal totalRevenueGenerated) { this.totalRevenueGenerated = totalRevenueGenerated; }
    
    public LocalDateTime getLastPurchaseDate() { return lastPurchaseDate; }
    public void setLastPurchaseDate(LocalDateTime lastPurchaseDate) { this.lastPurchaseDate = lastPurchaseDate; }
    
    public BigDecimal getPurchaseFrequency() { return purchaseFrequency; }
    public void setPurchaseFrequency(BigDecimal purchaseFrequency) { this.purchaseFrequency = purchaseFrequency; }
    
    public Set<String> getPreferredChannels() { return preferredChannels; }
    public void setPreferredChannels(Set<String> preferredChannels) { this.preferredChannels = preferredChannels; }
    
    public Boolean getEmailOptIn() { return emailOptIn; }
    public void setEmailOptIn(Boolean emailOptIn) { this.emailOptIn = emailOptIn; }
    
    public Boolean getSmsOptIn() { return smsOptIn; }
    public void setSmsOptIn(Boolean smsOptIn) { this.smsOptIn = smsOptIn; }
    
    public Map<String, String> getSocialMediaHandles() { return socialMediaHandles; }
    public void setSocialMediaHandles(Map<String, String> socialMediaHandles) { this.socialMediaHandles = socialMediaHandles; }
    
    public String getBestContactTime() { return bestContactTime; }
    public void setBestContactTime(String bestContactTime) { this.bestContactTime = bestContactTime; }
    
    public String getLanguagePreference() { return languagePreference; }
    public void setLanguagePreference(String languagePreference) { this.languagePreference = languagePreference; }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public Map<String, String> getExternalIds() { return externalIds; }
    public void setExternalIds(Map<String, String> externalIds) { this.externalIds = externalIds; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // === BUILDER PATTERN ===
    
    public static CrmCustomerBuilder builder() {
        return new CrmCustomerBuilder();
    }
    
    public static class CrmCustomerBuilder {
        private Long id;
        private Company company;
        private ThirdParty thirdParty;
        private Integer aiScore;
        private BigDecimal churnProbability;
        private BigDecimal lifetimeValuePredicted;
        private CustomerSegment customerSegment;
        private PaymentBehavior paymentBehavior;
        private Integer satisfactionScore;
        private Integer avgPaymentDelay;
        private BigDecimal totalRevenueGenerated = BigDecimal.ZERO;
        private LocalDateTime lastPurchaseDate;
        private BigDecimal purchaseFrequency;
        private Set<String> preferredChannels = new HashSet<>();
        private Boolean emailOptIn = true;
        private Boolean smsOptIn = true;
        private Map<String, String> socialMediaHandles = new HashMap<>();
        private String bestContactTime;
        private String languagePreference = "fr";
        private String timezone;
        private Map<String, String> externalIds = new HashMap<>();
        private Boolean isActive = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public CrmCustomerBuilder id(Long id) { this.id = id; return this; }
        public CrmCustomerBuilder company(Company company) { this.company = company; return this; }
        public CrmCustomerBuilder thirdParty(ThirdParty thirdParty) { this.thirdParty = thirdParty; return this; }
        public CrmCustomerBuilder aiScore(Integer aiScore) { this.aiScore = aiScore; return this; }
        public CrmCustomerBuilder churnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; return this; }
        public CrmCustomerBuilder lifetimeValuePredicted(BigDecimal lifetimeValuePredicted) { this.lifetimeValuePredicted = lifetimeValuePredicted; return this; }
        public CrmCustomerBuilder customerSegment(CustomerSegment customerSegment) { this.customerSegment = customerSegment; return this; }
        public CrmCustomerBuilder paymentBehavior(PaymentBehavior paymentBehavior) { this.paymentBehavior = paymentBehavior; return this; }
        public CrmCustomerBuilder satisfactionScore(Integer satisfactionScore) { this.satisfactionScore = satisfactionScore; return this; }
        public CrmCustomerBuilder avgPaymentDelay(Integer avgPaymentDelay) { this.avgPaymentDelay = avgPaymentDelay; return this; }
        public CrmCustomerBuilder totalRevenueGenerated(BigDecimal totalRevenueGenerated) { this.totalRevenueGenerated = totalRevenueGenerated; return this; }
        public CrmCustomerBuilder lastPurchaseDate(LocalDateTime lastPurchaseDate) { this.lastPurchaseDate = lastPurchaseDate; return this; }
        public CrmCustomerBuilder purchaseFrequency(BigDecimal purchaseFrequency) { this.purchaseFrequency = purchaseFrequency; return this; }
        public CrmCustomerBuilder preferredChannels(Set<String> preferredChannels) { this.preferredChannels = preferredChannels; return this; }
        public CrmCustomerBuilder emailOptIn(Boolean emailOptIn) { this.emailOptIn = emailOptIn; return this; }
        public CrmCustomerBuilder smsOptIn(Boolean smsOptIn) { this.smsOptIn = smsOptIn; return this; }
        public CrmCustomerBuilder socialMediaHandles(Map<String, String> socialMediaHandles) { this.socialMediaHandles = socialMediaHandles; return this; }
        public CrmCustomerBuilder bestContactTime(String bestContactTime) { this.bestContactTime = bestContactTime; return this; }
        public CrmCustomerBuilder languagePreference(String languagePreference) { this.languagePreference = languagePreference; return this; }
        public CrmCustomerBuilder timezone(String timezone) { this.timezone = timezone; return this; }
        public CrmCustomerBuilder externalIds(Map<String, String> externalIds) { this.externalIds = externalIds; return this; }
        public CrmCustomerBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public CrmCustomerBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public CrmCustomerBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public CrmCustomer build() {
            return new CrmCustomer(id, company, thirdParty, aiScore, churnProbability, lifetimeValuePredicted,
                                 customerSegment, paymentBehavior, satisfactionScore, avgPaymentDelay,
                                 totalRevenueGenerated, lastPurchaseDate, purchaseFrequency, preferredChannels,
                                 emailOptIn, smsOptIn, socialMediaHandles, bestContactTime, languagePreference,
                                 timezone, externalIds, isActive, createdAt, updatedAt);
        }
    }
    
    // === ENUMS ===
    
    /**
     * Segments de clients basÃ©s sur l'analyse comportementale
     */
    public enum CustomerSegment {
        VIP_HIGH_VALUE("Client VIP haute valeur"),
        STRATEGIC_ACCOUNT("Compte stratÃ©gique"), 
        GROWING_BUSINESS("Entreprise en croissance"),
        STABLE_REGULAR("Client rÃ©gulier stable"),
        OCCASIONAL_BUYER("Acheteur occasionnel"),
        PRICE_SENSITIVE("Sensible au prix"),
        PAYMENT_DELAYED("Paiement diffÃ©rÃ©"),
        AT_RISK_CHURN("Ã€ risque de dÃ©sabonnement");
        
        private final String description;
        
        CustomerSegment(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Comportements de paiement identifiÃ©s par IA
     */
    public enum PaymentBehavior {
        EARLY_PAYER("Paiement anticipÃ©"),
        PROMPT_PAYER("Paiement ponctuel"),
        REGULAR_DELAY("Retard rÃ©gulier"),
        NEGOTIATOR("NÃ©gociateur"),
        PROBLEMATIC_PAYER("Paiement problÃ©matique"),
        CASH_ONLY("EspÃ¨ces uniquement");
        
        private final String description;
        
        PaymentBehavior(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

