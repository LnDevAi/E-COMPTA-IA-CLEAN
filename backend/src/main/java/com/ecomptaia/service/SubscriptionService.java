package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionService {
    
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;
    
    @Autowired
    private PlanFeatureRepository planFeatureRepository;
    
    @Autowired
    private CompanySubscriptionRepository companySubscriptionRepository;
    
    @Autowired
    private SubscriptionUsageRepository subscriptionUsageRepository;
    
    @Autowired
    private SubscriptionPaymentRepository subscriptionPaymentRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private ExchangeRateService exchangeRateService;
    
    // ==================== GESTION DES PLANS ====================
    
    /**
     * Créer un nouveau plan d'abonnement
     */
    public SubscriptionPlan createSubscriptionPlan(SubscriptionPlan plan) {
        // Générer un code unique si non fourni
        if (plan.getPlanCode() == null || plan.getPlanCode().isEmpty()) {
            plan.setPlanCode("PLAN_" + System.currentTimeMillis());
        }
        
        // Valider le plan
        validateSubscriptionPlan(plan);
        
        return subscriptionPlanRepository.save(plan);
    }
    
    /**
     * Obtenir tous les plans actifs
     */
    public List<SubscriptionPlan> getActivePlans() {
        return subscriptionPlanRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }
    
    /**
     * Obtenir un plan par code
     */
    public Optional<SubscriptionPlan> getPlanByCode(String planCode) {
        return subscriptionPlanRepository.findByPlanCode(planCode);
    }
    
    /**
     * Obtenir les plans recommandés pour une entreprise
     */
    public List<SubscriptionPlan> getRecommendedPlans(Long companyId, Integer maxUsers, Integer maxCompanies) {
        return subscriptionPlanRepository.findRecommendedPlans(maxUsers, maxCompanies);
    }
    
    /**
     * Obtenir les plans avec prix localisés
     */
    public List<Map<String, Object>> getLocalizedPlans(String targetCurrency) {
        List<SubscriptionPlan> plans = getActivePlans();
        List<Map<String, Object>> localizedPlans = new ArrayList<>();
        
        for (SubscriptionPlan plan : plans) {
            Map<String, Object> localizedPlan = new HashMap<>();
            localizedPlan.put("plan", plan);
            
            // Obtenir le taux de change
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate("USD", targetCurrency);
            if (exchangeRate != null) {
                BigDecimal localizedPrice = plan.getLocalizedPrice(targetCurrency, exchangeRate);
                localizedPlan.put("localizedPrice", localizedPrice);
                localizedPlan.put("exchangeRate", exchangeRate);
                localizedPlan.put("currency", targetCurrency);
            }
            
            localizedPlans.add(localizedPlan);
        }
        
        return localizedPlans;
    }
    
    // ==================== GESTION DES FONCTIONNALITÉS ====================
    
    /**
     * Ajouter une fonctionnalité à un plan
     */
    public PlanFeature addFeatureToPlan(Long planId, PlanFeature feature) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        
        feature.setSubscriptionPlan(plan);
        return planFeatureRepository.save(feature);
    }
    
    /**
     * Obtenir les fonctionnalités d'un plan
     */
    public List<PlanFeature> getPlanFeatures(Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        
        return planFeatureRepository.findBySubscriptionPlanOrderBySortOrderAsc(plan);
    }
    
    /**
     * Obtenir les fonctionnalités actives d'un plan
     */
    public List<PlanFeature> getActivePlanFeatures(Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        
        return planFeatureRepository.findBySubscriptionPlanAndIsIncludedTrue(plan);
    }
    
    // ==================== GESTION DES SOUSCRIPTIONS ====================
    
    /**
     * Créer une nouvelle souscription
     */
    public CompanySubscription createSubscription(Long companyId, Long planId, String currency, BigDecimal discountPercentage) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        
        // Vérifier si l'entreprise a déjà une souscription active
        List<CompanySubscription> activeSubscriptions = companySubscriptionRepository.findByCompanyAndStatusIn(
                company, Arrays.asList(CompanySubscription.SubscriptionStatus.ACTIVE, CompanySubscription.SubscriptionStatus.TRIAL)
        );
        
        if (!activeSubscriptions.isEmpty()) {
            throw new RuntimeException("L'entreprise a déjà une souscription active");
        }
        
        // Créer la souscription
        String subscriptionCode = "SUB_" + companyId + "_" + System.currentTimeMillis();
        CompanySubscription subscription = new CompanySubscription(company, plan, subscriptionCode, LocalDate.now());
        
        // Configurer le prix localisé
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate("USD", currency);
        subscription.setLocalizedPricing(exchangeRate);
        
        // Appliquer la remise si fournie
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            subscription.setDiscountPercentage(discountPercentage);
            subscription.calculateFinalPrice();
        }
        
        // Calculer la date de fin selon le cycle de facturation
        LocalDate endDate = calculateEndDate(LocalDate.now(), plan.getBillingCycle());
        subscription.setEndDate(endDate);
        
        // Calculer la date du prochain paiement
        LocalDate nextPaymentDate = calculateNextPaymentDate(LocalDate.now(), plan.getBillingCycle());
        subscription.setNextPaymentDate(nextPaymentDate);
        
        return companySubscriptionRepository.save(subscription);
    }
    
    /**
     * Obtenir les souscriptions d'une entreprise
     */
    public List<CompanySubscription> getCompanySubscriptions(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
        
        return companySubscriptionRepository.findByCompany(company);
    }
    
    /**
     * Obtenir la souscription active d'une entreprise
     */
    public Optional<CompanySubscription> getActiveSubscription(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
        
        return companySubscriptionRepository.findByCompanyAndStatus(company, CompanySubscription.SubscriptionStatus.ACTIVE);
    }
    
    /**
     * Renouveler une souscription
     */
    public CompanySubscription renewSubscription(Long subscriptionId) {
        CompanySubscription subscription = companySubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Souscription non trouvée"));
        
        if (!subscription.isActive()) {
            throw new RuntimeException("La souscription n'est pas active");
        }
        
        // Calculer la nouvelle période
        LocalDate newStartDate = subscription.getEndDate().plusDays(1);
        LocalDate newEndDate = calculateEndDate(newStartDate, subscription.getBillingCycle());
        
        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscription.setNextPaymentDate(calculateNextPaymentDate(newStartDate, subscription.getBillingCycle()));
        
        return companySubscriptionRepository.save(subscription);
    }
    
    /**
     * Annuler une souscription
     */
    public CompanySubscription cancelSubscription(Long subscriptionId, String reason) {
        CompanySubscription subscription = companySubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Souscription non trouvée"));
        
        subscription.setStatus(CompanySubscription.SubscriptionStatus.CANCELLED);
        subscription.setCancellationDate(LocalDate.now());
        subscription.setCancellationReason(reason);
        subscription.setAutoRenew(false);
        
        return companySubscriptionRepository.save(subscription);
    }
    
    // ==================== GESTION DE L'UTILISATION ====================
    
    /**
     * Enregistrer l'utilisation d'une fonctionnalité
     */
    public SubscriptionUsage recordFeatureUsage(Long subscriptionId, String featureCode, String featureName, Integer usageCount) {
        CompanySubscription subscription = companySubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Souscription non trouvée"));
        
        if (!subscription.isActive()) {
            throw new RuntimeException("La souscription n'est pas active");
        }
        
        // Vérifier si l'utilisation existe déjà pour aujourd'hui
        Optional<SubscriptionUsage> existingUsage = subscriptionUsageRepository
                .findByCompanySubscriptionAndFeatureCode(subscription, featureCode);
        
        SubscriptionUsage usage;
        if (existingUsage.isPresent()) {
            usage = existingUsage.get();
            usage.addUsage(usageCount);
        } else {
            usage = new SubscriptionUsage(subscription, featureCode, featureName, LocalDate.now());
            usage.setUsageCount(usageCount);
        }
        
        // Vérifier les limites du plan
        List<PlanFeature> planFeatures = getActivePlanFeatures(subscription.getSubscriptionPlan().getId());
        Optional<PlanFeature> feature = planFeatures.stream()
                .filter(f -> f.getFeatureCode().equals(featureCode))
                .findFirst();
        
        if (feature.isPresent() && feature.get().hasLimit()) {
            usage.setLimitValue(feature.get().getLimitValue());
            // Calculer le pourcentage d'utilisation
            if (usage.getLimitValue() != null && usage.getLimitValue() > 0) {
                double percentage = (usage.getUsageCount() * 100.0) / usage.getLimitValue();
                usage.setUsagePercentage(percentage);
                usage.setIsOverLimit(usage.getUsageCount() > usage.getLimitValue());
            }
        }
        
        return subscriptionUsageRepository.save(usage);
    }
    
    /**
     * Obtenir l'utilisation d'une entreprise
     */
    public List<SubscriptionUsage> getCompanyUsage(Long companyId) {
        return subscriptionUsageRepository.findByCompanyId(companyId);
    }
    
    /**
     * Obtenir les alertes d'utilisation
     */
    public List<SubscriptionUsage> getUsageAlerts() {
        return subscriptionUsageRepository.findUsageAlerts();
    }
    
    // ==================== GESTION DES PAIEMENTS ====================
    
    /**
     * Enregistrer un paiement
     */
    public SubscriptionPayment recordPayment(Long subscriptionId, BigDecimal amount, String currency, String paymentMethod, String transactionId) {
        CompanySubscription subscription = companySubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Souscription non trouvée"));
        
        String paymentCode = "PAY_" + subscriptionId + "_" + System.currentTimeMillis();
        SubscriptionPayment payment = new SubscriptionPayment(subscription, paymentCode, LocalDate.now(), amount, currency);
        
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);
        payment.setStatus(SubscriptionPayment.PaymentStatus.COMPLETED);
        
        // Calculer le montant en USD
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(currency, "USD");
        payment.setExchangeRateAndCalculateUSD(exchangeRate);
        
        // Mettre à jour la souscription
        subscription.setLastPaymentDate(LocalDate.now());
        subscription.setNextPaymentDate(calculateNextPaymentDate(LocalDate.now(), subscription.getBillingCycle()));
        companySubscriptionRepository.save(subscription);
        
        return subscriptionPaymentRepository.save(payment);
    }
    
    /**
     * Obtenir les paiements d'une entreprise
     */
    public List<SubscriptionPayment> getCompanyPayments(Long companyId) {
        return subscriptionPaymentRepository.findByCompanyId(companyId);
    }
    
    // ==================== STATISTIQUES ET RAPPORTS ====================
    
    /**
     * Obtenir les statistiques des plans
     */
    public Map<String, Object> getPlanStatistics() {
        Object[] stats = subscriptionPlanRepository.getPlanStatistics();
        Map<String, Object> result = new HashMap<>();
        
        if (stats != null && stats.length >= 6) {
            result.put("totalPlans", stats[0]);
            result.put("activePlans", stats[1]);
            result.put("featuredPlans", stats[2]);
            result.put("averagePrice", stats[3]);
            result.put("minPrice", stats[4]);
            result.put("maxPrice", stats[5]);
        }
        
        return result;
    }
    
    /**
     * Obtenir les statistiques des souscriptions
     */
    public Map<String, Object> getSubscriptionStatistics() {
        Object[] stats = companySubscriptionRepository.getSubscriptionStatistics();
        Map<String, Object> result = new HashMap<>();
        
        if (stats != null && stats.length >= 7) {
            result.put("totalSubscriptions", stats[0]);
            result.put("activeSubscriptions", stats[1]);
            result.put("trialSubscriptions", stats[2]);
            result.put("cancelledSubscriptions", stats[3]);
            result.put("totalRevenue", stats[4]);
            result.put("averagePrice", stats[5]);
            result.put("autoRenewSubscriptions", stats[6]);
        }
        
        return result;
    }
    
    /**
     * Obtenir les statistiques des paiements
     */
    public Map<String, Object> getPaymentStatistics() {
        Object[] stats = subscriptionPaymentRepository.getPaymentStatistics();
        Map<String, Object> result = new HashMap<>();
        
        if (stats != null && stats.length >= 8) {
            result.put("totalPayments", stats[0]);
            result.put("completedPayments", stats[1]);
            result.put("pendingPayments", stats[2]);
            result.put("failedPayments", stats[3]);
            result.put("refundedPayments", stats[4]);
            result.put("totalAmount", stats[5]);
            result.put("averageAmount", stats[6]);
            result.put("totalAmountUSD", stats[7]);
        }
        
        return result;
    }
    
    /**
     * Obtenir les statistiques d'utilisation
     */
    public Map<String, Object> getUsageStatistics() {
        Object[] stats = subscriptionUsageRepository.getUsageStatistics();
        Map<String, Object> result = new HashMap<>();
        
        if (stats != null && stats.length >= 6) {
            result.put("totalUsageRecords", stats[0]);
            result.put("totalUsage", stats[1]);
            result.put("overLimitRecords", stats[2]);
            result.put("limitedFeatures", stats[3]);
            result.put("uniqueFeatures", stats[4]);
            result.put("activeCompanies", stats[5]);
        }
        
        return result;
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    /**
     * Valider un plan d'abonnement
     */
    private void validateSubscriptionPlan(SubscriptionPlan plan) {
        if (plan.getPlanName() == null || plan.getPlanName().trim().isEmpty()) {
            throw new RuntimeException("Le nom du plan est requis");
        }
        
        if (plan.getBasePriceUSD() == null || plan.getBasePriceUSD().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Le prix de base doit être positif");
        }
        
        if (plan.getBillingCycle() == null) {
            throw new RuntimeException("Le cycle de facturation est requis");
        }
    }
    
    /**
     * Calculer la date de fin selon le cycle de facturation
     */
    private LocalDate calculateEndDate(LocalDate startDate, SubscriptionPlan.BillingCycle billingCycle) {
        switch (billingCycle) {
            case MONTHLY:
                return startDate.plusMonths(1).minusDays(1);
            case QUARTERLY:
                return startDate.plusMonths(3).minusDays(1);
            case SEMI_ANNUAL:
                return startDate.plusMonths(6).minusDays(1);
            case ANNUAL:
                return startDate.plusYears(1).minusDays(1);
            default:
                return startDate.plusMonths(1).minusDays(1);
        }
    }
    
    /**
     * Calculer la date du prochain paiement
     */
    private LocalDate calculateNextPaymentDate(LocalDate startDate, SubscriptionPlan.BillingCycle billingCycle) {
        switch (billingCycle) {
            case MONTHLY:
                return startDate.plusMonths(1);
            case QUARTERLY:
                return startDate.plusMonths(3);
            case SEMI_ANNUAL:
                return startDate.plusMonths(6);
            case ANNUAL:
                return startDate.plusYears(1);
            default:
                return startDate.plusMonths(1);
        }
    }
    
    /**
     * Vérifier si une fonctionnalité est disponible pour une entreprise
     */
    public boolean isFeatureAvailable(Long companyId, String featureCode) {
        Optional<CompanySubscription> subscription = getActiveSubscription(companyId);
        if (!subscription.isPresent()) {
            return false;
        }
        
        List<PlanFeature> features = getActivePlanFeatures(subscription.get().getSubscriptionPlan().getId());
        return features.stream()
                .anyMatch(f -> f.getFeatureCode().equals(featureCode) && f.isActive());
    }
    
    /**
     * Vérifier si l'utilisation d'une fonctionnalité est dans les limites
     */
    public boolean isFeatureUsageWithinLimit(Long companyId, String featureCode) {
        Optional<CompanySubscription> subscription = getActiveSubscription(companyId);
        if (!subscription.isPresent()) {
            return false;
        }
        
        Optional<SubscriptionUsage> usage = subscriptionUsageRepository
                .findByCompanySubscriptionAndFeatureCode(subscription.get(), featureCode);
        
        return !usage.isPresent() || !usage.get().getIsOverLimit();
    }
}
