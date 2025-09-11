ackage com.ecomptaia.crm.dto;

import com.ecomptaia.crm.entity.CrmCustomer.CustomerSegment;
import com.ecomptaia.crm.entity.CrmCustomer.PaymentBehavior;

import java.math.BigDecimal;

/**
 * DTO pour l'intelligence client calculée par IA
 * Contient les scores et prédictions pour un client
 */
public class CustomerIntelligence {
    
    private Integer aiScore;
    private CustomerSegment segment;
    private BigDecimal churnProbability;
    private BigDecimal predictedLTV;
    private PaymentBehavior paymentBehavior;
    private Integer satisfactionScore;
    private String riskLevel;
    private String recommendations;
    
    // Métriques comportementales
    private BigDecimal avgOrderValue;
    private Integer purchaseFrequency;
    private Integer daysSinceLastPurchase;
    private BigDecimal totalSpent;
    private Integer totalOrders;
    
    // Prédictions
    private BigDecimal nextPurchaseProbability;
    private BigDecimal nextPurchaseAmount;
    private Integer daysToNextPurchase;
    
    // Segmentation avancée
    private String behavioralProfile;
    private String engagementLevel;
    private String loyaltyStatus;
    
    // === CONSTRUCTEURS ===
    
    public CustomerIntelligence() {}
    
    public CustomerIntelligence(Integer aiScore, CustomerSegment segment, BigDecimal churnProbability, 
                               BigDecimal predictedLTV, PaymentBehavior paymentBehavior, 
                               Integer satisfactionScore, String riskLevel, String recommendations,
                               BigDecimal avgOrderValue, Integer purchaseFrequency, 
                               Integer daysSinceLastPurchase, BigDecimal totalSpent, 
                               Integer totalOrders, BigDecimal nextPurchaseProbability, 
                               BigDecimal nextPurchaseAmount, Integer daysToNextPurchase,
                               String behavioralProfile, String engagementLevel, String loyaltyStatus) {
        this.aiScore = aiScore;
        this.segment = segment;
        this.churnProbability = churnProbability;
        this.predictedLTV = predictedLTV;
        this.paymentBehavior = paymentBehavior;
        this.satisfactionScore = satisfactionScore;
        this.riskLevel = riskLevel;
        this.recommendations = recommendations;
        this.avgOrderValue = avgOrderValue;
        this.purchaseFrequency = purchaseFrequency;
        this.daysSinceLastPurchase = daysSinceLastPurchase;
        this.totalSpent = totalSpent;
        this.totalOrders = totalOrders;
        this.nextPurchaseProbability = nextPurchaseProbability;
        this.nextPurchaseAmount = nextPurchaseAmount;
        this.daysToNextPurchase = daysToNextPurchase;
        this.behavioralProfile = behavioralProfile;
        this.engagementLevel = engagementLevel;
        this.loyaltyStatus = loyaltyStatus;
    }
    
    // === GETTERS ET SETTERS ===
    
    public Integer getAiScore() { return aiScore; }
    public void setAiScore(Integer aiScore) { this.aiScore = aiScore; }
    
    public CustomerSegment getSegment() { return segment; }
    public void setSegment(CustomerSegment segment) { this.segment = segment; }
    
    public BigDecimal getChurnProbability() { return churnProbability; }
    public void setChurnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; }
    
    public BigDecimal getPredictedLTV() { return predictedLTV; }
    public void setPredictedLTV(BigDecimal predictedLTV) { this.predictedLTV = predictedLTV; }
    
    public PaymentBehavior getPaymentBehavior() { return paymentBehavior; }
    public void setPaymentBehavior(PaymentBehavior paymentBehavior) { this.paymentBehavior = paymentBehavior; }
    
    public Integer getSatisfactionScore() { return satisfactionScore; }
    public void setSatisfactionScore(Integer satisfactionScore) { this.satisfactionScore = satisfactionScore; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    public BigDecimal getAvgOrderValue() { return avgOrderValue; }
    public void setAvgOrderValue(BigDecimal avgOrderValue) { this.avgOrderValue = avgOrderValue; }
    
    public Integer getPurchaseFrequency() { return purchaseFrequency; }
    public void setPurchaseFrequency(Integer purchaseFrequency) { this.purchaseFrequency = purchaseFrequency; }
    
    public Integer getDaysSinceLastPurchase() { return daysSinceLastPurchase; }
    public void setDaysSinceLastPurchase(Integer daysSinceLastPurchase) { this.daysSinceLastPurchase = daysSinceLastPurchase; }
    
    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
    
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    
    public BigDecimal getNextPurchaseProbability() { return nextPurchaseProbability; }
    public void setNextPurchaseProbability(BigDecimal nextPurchaseProbability) { this.nextPurchaseProbability = nextPurchaseProbability; }
    
    public BigDecimal getNextPurchaseAmount() { return nextPurchaseAmount; }
    public void setNextPurchaseAmount(BigDecimal nextPurchaseAmount) { this.nextPurchaseAmount = nextPurchaseAmount; }
    
    public Integer getDaysToNextPurchase() { return daysToNextPurchase; }
    public void setDaysToNextPurchase(Integer daysToNextPurchase) { this.daysToNextPurchase = daysToNextPurchase; }
    
    public String getBehavioralProfile() { return behavioralProfile; }
    public void setBehavioralProfile(String behavioralProfile) { this.behavioralProfile = behavioralProfile; }
    
    public String getEngagementLevel() { return engagementLevel; }
    public void setEngagementLevel(String engagementLevel) { this.engagementLevel = engagementLevel; }
    
    public String getLoyaltyStatus() { return loyaltyStatus; }
    public void setLoyaltyStatus(String loyaltyStatus) { this.loyaltyStatus = loyaltyStatus; }
    
    // === BUILDER PATTERN ===
    
    public static CustomerIntelligenceBuilder builder() {
        return new CustomerIntelligenceBuilder();
    }
    
    public static class CustomerIntelligenceBuilder {
        private Integer aiScore;
        private CustomerSegment segment;
        private BigDecimal churnProbability;
        private BigDecimal predictedLTV;
        private PaymentBehavior paymentBehavior;
        private Integer satisfactionScore;
        private String riskLevel;
        private String recommendations;
        private BigDecimal avgOrderValue;
        private Integer purchaseFrequency;
        private Integer daysSinceLastPurchase;
        private BigDecimal totalSpent;
        private Integer totalOrders;
        private BigDecimal nextPurchaseProbability;
        private BigDecimal nextPurchaseAmount;
        private Integer daysToNextPurchase;
        private String behavioralProfile;
        private String engagementLevel;
        private String loyaltyStatus;
        
        public CustomerIntelligenceBuilder aiScore(Integer aiScore) { this.aiScore = aiScore; return this; }
        public CustomerIntelligenceBuilder segment(CustomerSegment segment) { this.segment = segment; return this; }
        public CustomerIntelligenceBuilder churnProbability(BigDecimal churnProbability) { this.churnProbability = churnProbability; return this; }
        public CustomerIntelligenceBuilder predictedLTV(BigDecimal predictedLTV) { this.predictedLTV = predictedLTV; return this; }
        public CustomerIntelligenceBuilder paymentBehavior(PaymentBehavior paymentBehavior) { this.paymentBehavior = paymentBehavior; return this; }
        public CustomerIntelligenceBuilder satisfactionScore(Integer satisfactionScore) { this.satisfactionScore = satisfactionScore; return this; }
        public CustomerIntelligenceBuilder riskLevel(String riskLevel) { this.riskLevel = riskLevel; return this; }
        public CustomerIntelligenceBuilder recommendations(String recommendations) { this.recommendations = recommendations; return this; }
        public CustomerIntelligenceBuilder avgOrderValue(BigDecimal avgOrderValue) { this.avgOrderValue = avgOrderValue; return this; }
        public CustomerIntelligenceBuilder purchaseFrequency(Integer purchaseFrequency) { this.purchaseFrequency = purchaseFrequency; return this; }
        public CustomerIntelligenceBuilder daysSinceLastPurchase(Integer daysSinceLastPurchase) { this.daysSinceLastPurchase = daysSinceLastPurchase; return this; }
        public CustomerIntelligenceBuilder totalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; return this; }
        public CustomerIntelligenceBuilder totalOrders(Integer totalOrders) { this.totalOrders = totalOrders; return this; }
        public CustomerIntelligenceBuilder nextPurchaseProbability(BigDecimal nextPurchaseProbability) { this.nextPurchaseProbability = nextPurchaseProbability; return this; }
        public CustomerIntelligenceBuilder nextPurchaseAmount(BigDecimal nextPurchaseAmount) { this.nextPurchaseAmount = nextPurchaseAmount; return this; }
        public CustomerIntelligenceBuilder daysToNextPurchase(Integer daysToNextPurchase) { this.daysToNextPurchase = daysToNextPurchase; return this; }
        public CustomerIntelligenceBuilder behavioralProfile(String behavioralProfile) { this.behavioralProfile = behavioralProfile; return this; }
        public CustomerIntelligenceBuilder engagementLevel(String engagementLevel) { this.engagementLevel = engagementLevel; return this; }
        public CustomerIntelligenceBuilder loyaltyStatus(String loyaltyStatus) { this.loyaltyStatus = loyaltyStatus; return this; }
        
        public CustomerIntelligence build() {
            return new CustomerIntelligence(aiScore, segment, churnProbability, predictedLTV, paymentBehavior,
                                         satisfactionScore, riskLevel, recommendations, avgOrderValue, purchaseFrequency,
                                         daysSinceLastPurchase, totalSpent, totalOrders, nextPurchaseProbability,
                                         nextPurchaseAmount, daysToNextPurchase, behavioralProfile, engagementLevel, loyaltyStatus);
        }
    }
}

