package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class SubscriptionDataInitializer implements CommandLineRunner {
    
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;
    
    @Autowired
    private PlanFeatureRepository planFeatureRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialiser les données seulement si aucun plan n'existe
        if (subscriptionPlanRepository.count() == 0) {
            initializeSubscriptionData();
        }
    }
    
    private void initializeSubscriptionData() {
        // Créer les plans d'abonnement
        createSubscriptionPlans();
        
        // Créer les fonctionnalités pour chaque plan
        createPlanFeatures();
    }
    
    private void createSubscriptionPlans() {
        // Plan Gratuit
        SubscriptionPlan planGratuit = new SubscriptionPlan(
            "Plan Gratuit",
            "FREE",
            "Plan gratuit avec fonctionnalités de base pour les petites entreprises",
            new BigDecimal("0.00"),
            "USD",
            SubscriptionPlan.BillingCycle.MONTHLY
        );
        planGratuit.setMaxUsers(1);
        planGratuit.setMaxCompanies(1);
        planGratuit.setStorageLimitGB(1);
        planGratuit.setIsFeatured(false);
        planGratuit.setSortOrder(1);
        subscriptionPlanRepository.save(planGratuit);
        
        // Plan Starter
        SubscriptionPlan planStarter = new SubscriptionPlan(
            "Plan Starter",
            "STARTER",
            "Plan de démarrage pour les petites entreprises",
            new BigDecimal("29.99"),
            "USD",
            SubscriptionPlan.BillingCycle.MONTHLY
        );
        planStarter.setMaxUsers(5);
        planStarter.setMaxCompanies(2);
        planStarter.setStorageLimitGB(10);
        planStarter.setIsFeatured(true);
        planStarter.setSortOrder(2);
        subscriptionPlanRepository.save(planStarter);
        
        // Plan Professional
        SubscriptionPlan planProfessional = new SubscriptionPlan(
            "Plan Professional",
            "PROFESSIONAL",
            "Plan professionnel pour les entreprises moyennes",
            new BigDecimal("99.99"),
            "USD",
            SubscriptionPlan.BillingCycle.MONTHLY
        );
        planProfessional.setMaxUsers(20);
        planProfessional.setMaxCompanies(5);
        planProfessional.setStorageLimitGB(50);
        planProfessional.setIsFeatured(true);
        planProfessional.setSortOrder(3);
        subscriptionPlanRepository.save(planProfessional);
        
        // Plan Enterprise
        SubscriptionPlan planEnterprise = new SubscriptionPlan(
            "Plan Enterprise",
            "ENTERPRISE",
            "Plan entreprise pour les grandes organisations",
            new BigDecimal("299.99"),
            "USD",
            SubscriptionPlan.BillingCycle.MONTHLY
        );
        planEnterprise.setMaxUsers(100);
        planEnterprise.setMaxCompanies(20);
        planEnterprise.setStorageLimitGB(200);
        planEnterprise.setIsFeatured(false);
        planEnterprise.setSortOrder(4);
        subscriptionPlanRepository.save(planEnterprise);
        
        // Plan OHADA Premium
        SubscriptionPlan planOHADAPremium = new SubscriptionPlan(
            "Plan OHADA Premium",
            "OHADA_PREMIUM",
            "Plan spécialisé pour les entreprises OHADA avec fonctionnalités avancées",
            new BigDecimal("199.99"),
            "USD",
            SubscriptionPlan.BillingCycle.MONTHLY
        );
        planOHADAPremium.setMaxUsers(50);
        planOHADAPremium.setMaxCompanies(10);
        planOHADAPremium.setStorageLimitGB(100);
        planOHADAPremium.setIsFeatured(true);
        planOHADAPremium.setSortOrder(5);
        subscriptionPlanRepository.save(planOHADAPremium);
    }
    
    private void createPlanFeatures() {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();
        
        for (SubscriptionPlan plan : plans) {
            switch (plan.getPlanCode()) {
                case "FREE":
                    createFreePlanFeatures(plan);
                    break;
                case "STARTER":
                    createStarterPlanFeatures(plan);
                    break;
                case "PROFESSIONAL":
                    createProfessionalPlanFeatures(plan);
                    break;
                case "ENTERPRISE":
                    createEnterprisePlanFeatures(plan);
                    break;
                case "OHADA_PREMIUM":
                    createOHADAPremiumPlanFeatures(plan);
                    break;
            }
        }
    }
    
    private void createFreePlanFeatures(SubscriptionPlan plan) {
        // Fonctionnalités de base
        createFeature(plan, "BASIC_ACCOUNTING", "Comptabilité de base", PlanFeature.FeatureType.MODULE, 100);
        createFeature(plan, "BASIC_REPORTS", "Rapports de base", PlanFeature.FeatureType.FUNCTION, 10);
        createFeature(plan, "BASIC_USERS", "Utilisateurs de base", PlanFeature.FeatureType.LIMIT, 1);
        createFeature(plan, "BASIC_STORAGE", "Stockage de base", PlanFeature.FeatureType.LIMIT, 1);
        createFeature(plan, "EMAIL_SUPPORT", "Support par email", PlanFeature.FeatureType.SUPPORT, null);
    }
    
    private void createStarterPlanFeatures(SubscriptionPlan plan) {
        // Fonctionnalités du plan gratuit + plus
        createFeature(plan, "BASIC_ACCOUNTING", "Comptabilité de base", PlanFeature.FeatureType.MODULE, 1000);
        createFeature(plan, "BASIC_REPORTS", "Rapports de base", PlanFeature.FeatureType.FUNCTION, 100);
        createFeature(plan, "ADVANCED_REPORTS", "Rapports avancés", PlanFeature.FeatureType.FUNCTION, 50);
        createFeature(plan, "BASIC_USERS", "Utilisateurs de base", PlanFeature.FeatureType.LIMIT, 5);
        createFeature(plan, "BASIC_STORAGE", "Stockage de base", PlanFeature.FeatureType.LIMIT, 10);
        createFeature(plan, "EMAIL_SUPPORT", "Support par email", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "CHAT_SUPPORT", "Support par chat", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "BASIC_EXPORT", "Export de base", PlanFeature.FeatureType.FUNCTION, 50);
    }
    
    private void createProfessionalPlanFeatures(SubscriptionPlan plan) {
        // Fonctionnalités du plan starter + plus
        createFeature(plan, "BASIC_ACCOUNTING", "Comptabilité de base", PlanFeature.FeatureType.MODULE, 10000);
        createFeature(plan, "ADVANCED_ACCOUNTING", "Comptabilité avancée", PlanFeature.FeatureType.MODULE, 5000);
        createFeature(plan, "BASIC_REPORTS", "Rapports de base", PlanFeature.FeatureType.FUNCTION, 500);
        createFeature(plan, "ADVANCED_REPORTS", "Rapports avancés", PlanFeature.FeatureType.FUNCTION, 200);
        createFeature(plan, "CUSTOM_REPORTS", "Rapports personnalisés", PlanFeature.FeatureType.FUNCTION, 50);
        createFeature(plan, "BASIC_USERS", "Utilisateurs de base", PlanFeature.FeatureType.LIMIT, 20);
        createFeature(plan, "BASIC_STORAGE", "Stockage de base", PlanFeature.FeatureType.LIMIT, 50);
        createFeature(plan, "EMAIL_SUPPORT", "Support par email", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "CHAT_SUPPORT", "Support par chat", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "PHONE_SUPPORT", "Support téléphonique", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "BASIC_EXPORT", "Export de base", PlanFeature.FeatureType.FUNCTION, 200);
        createFeature(plan, "ADVANCED_EXPORT", "Export avancé", PlanFeature.FeatureType.FUNCTION, 100);
        createFeature(plan, "API_ACCESS", "Accès API", PlanFeature.FeatureType.INTEGRATION, 1000);
        createFeature(plan, "BASIC_AI", "IA de base", PlanFeature.FeatureType.FUNCTION, 100);
    }
    
    private void createEnterprisePlanFeatures(SubscriptionPlan plan) {
        // Fonctionnalités du plan professional + plus
        createFeature(plan, "BASIC_ACCOUNTING", "Comptabilité de base", PlanFeature.FeatureType.MODULE, 100000);
        createFeature(plan, "ADVANCED_ACCOUNTING", "Comptabilité avancée", PlanFeature.FeatureType.MODULE, 50000);
        createFeature(plan, "ENTERPRISE_ACCOUNTING", "Comptabilité entreprise", PlanFeature.FeatureType.MODULE, 25000);
        createFeature(plan, "BASIC_REPORTS", "Rapports de base", PlanFeature.FeatureType.FUNCTION, 2000);
        createFeature(plan, "ADVANCED_REPORTS", "Rapports avancés", PlanFeature.FeatureType.FUNCTION, 1000);
        createFeature(plan, "CUSTOM_REPORTS", "Rapports personnalisés", PlanFeature.FeatureType.FUNCTION, 500);
        createFeature(plan, "BASIC_USERS", "Utilisateurs de base", PlanFeature.FeatureType.LIMIT, 100);
        createFeature(plan, "BASIC_STORAGE", "Stockage de base", PlanFeature.FeatureType.LIMIT, 200);
        createFeature(plan, "EMAIL_SUPPORT", "Support par email", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "CHAT_SUPPORT", "Support par chat", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "PHONE_SUPPORT", "Support téléphonique", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "PRIORITY_SUPPORT", "Support prioritaire", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "BASIC_EXPORT", "Export de base", PlanFeature.FeatureType.FUNCTION, 1000);
        createFeature(plan, "ADVANCED_EXPORT", "Export avancé", PlanFeature.FeatureType.FUNCTION, 500);
        createFeature(plan, "API_ACCESS", "Accès API", PlanFeature.FeatureType.INTEGRATION, 10000);
        createFeature(plan, "BASIC_AI", "IA de base", PlanFeature.FeatureType.FUNCTION, 1000);
        createFeature(plan, "ADVANCED_AI", "IA avancée", PlanFeature.FeatureType.FUNCTION, 500);
        createFeature(plan, "CUSTOM_INTEGRATION", "Intégrations personnalisées", PlanFeature.FeatureType.INTEGRATION, 10);
        createFeature(plan, "WHITE_LABEL", "Marque blanche", PlanFeature.FeatureType.CUSTOMIZATION, null);
    }
    
    private void createOHADAPremiumPlanFeatures(SubscriptionPlan plan) {
        // Fonctionnalités spécialisées OHADA
        createFeature(plan, "BASIC_ACCOUNTING", "Comptabilité de base", PlanFeature.FeatureType.MODULE, 50000);
        createFeature(plan, "ADVANCED_ACCOUNTING", "Comptabilité avancée", PlanFeature.FeatureType.MODULE, 25000);
        createFeature(plan, "OHADA_COMPLIANCE", "Conformité OHADA", PlanFeature.FeatureType.MODULE, null);
        createFeature(plan, "SYSCOHADA_STANDARDS", "Standards SYSCOHADA", PlanFeature.FeatureType.MODULE, null);
        createFeature(plan, "SMT_ACCOUNTING", "Comptabilité SMT", PlanFeature.FeatureType.MODULE, null);
        createFeature(plan, "BASIC_REPORTS", "Rapports de base", PlanFeature.FeatureType.FUNCTION, 1000);
        createFeature(plan, "ADVANCED_REPORTS", "Rapports avancés", PlanFeature.FeatureType.FUNCTION, 500);
        createFeature(plan, "OHADA_REPORTS", "Rapports OHADA", PlanFeature.FeatureType.FUNCTION, 200);
        createFeature(plan, "BASIC_USERS", "Utilisateurs de base", PlanFeature.FeatureType.LIMIT, 50);
        createFeature(plan, "BASIC_STORAGE", "Stockage de base", PlanFeature.FeatureType.LIMIT, 100);
        createFeature(plan, "EMAIL_SUPPORT", "Support par email", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "CHAT_SUPPORT", "Support par chat", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "OHADA_SUPPORT", "Support OHADA spécialisé", PlanFeature.FeatureType.SUPPORT, null);
        createFeature(plan, "BASIC_EXPORT", "Export de base", PlanFeature.FeatureType.FUNCTION, 500);
        createFeature(plan, "ADVANCED_EXPORT", "Export avancé", PlanFeature.FeatureType.FUNCTION, 200);
        createFeature(plan, "API_ACCESS", "Accès API", PlanFeature.FeatureType.INTEGRATION, 5000);
        createFeature(plan, "BASIC_AI", "IA de base", PlanFeature.FeatureType.FUNCTION, 500);
        createFeature(plan, "OHADA_AI", "IA spécialisée OHADA", PlanFeature.FeatureType.FUNCTION, 200);
        createFeature(plan, "MULTI_CURRENCY", "Multi-devises", PlanFeature.FeatureType.FUNCTION, null);
        createFeature(plan, "LOCAL_TAXES", "Taxes locales", PlanFeature.FeatureType.FUNCTION, null);
    }
    
    private void createFeature(SubscriptionPlan plan, String code, String name, PlanFeature.FeatureType type, Integer limit) {
        PlanFeature feature = new PlanFeature(plan, code, name, type);
        feature.setDescription("Fonctionnalité " + name + " pour le plan " + plan.getPlanName());
        feature.setLimitValue(limit);
        feature.setSortOrder((int) (planFeatureRepository.count() + 1));
        planFeatureRepository.save(feature);
    }
}
