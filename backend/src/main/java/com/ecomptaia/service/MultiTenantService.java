ackage com.ecomptaia.service;

pa
import com.ecomptaia.accounting.entity.ChartOfAccounts;
ckage com.ecomptaia.service;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.entity.LocaleSettings;
import com.ecomptaia.entity.CompanySubscription;
import com.ecomptaia.entity.SubscriptionPlan;
import com.ecomptaia.accounting.entity.AccountingStandard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.HashMap;

/**
 * Service pour la gestion du multi-tenant international
 * GÃ¨re l'isolation des donnÃ©es par entreprise et les configurations par pays
 */
@Service
public class MultiTenantService {
    
    @Autowired
    private AccountingStandardService accountingStandardService;
    
    @Autowired
    private LocalizationService localizationService;
    
    /**
     * CrÃ©e une nouvelle entreprise avec configuration multi-tenant
     */
    public Company createCompanyWithTenantConfig(String name, String countryCode, String currency, 
                                                AccountingStandard accountingStandard) {
        Company company = new Company();
        company.setName(name);
        company.setCountryCode(countryCode);
        company.setCountryName(getCountryName(countryCode));
        company.setCurrency(currency);
        company.setAccountingStandard(accountingStandard.getCode());
        company.setLocale(getDefaultLocale(countryCode));
        company.setBusinessType("SME"); // Par dÃ©faut
        company.setIsActive(true);
        
        return company;
    }
    
    /**
     * Configure les paramÃ¨tres de localisation pour une entreprise
     */
    public LocaleSettings configureLocaleSettings(Company company) {
        LocaleSettings localeSettings = new LocaleSettings();
        localeSettings.setCompanyId(company.getId());
        localeSettings.setLanguageCode(localizationService.getDefaultLanguage(company.getCountryCode()));
        localeSettings.setCountryCode(company.getCountryCode());
        localeSettings.setCurrencyCode(company.getCurrency());
        
        // Appliquer les formats par dÃ©faut selon le pays
        LocaleSettings defaultSettings = localizationService.getDefaultLocaleSettings(company.getCountryCode());
        localeSettings.setDateFormat(defaultSettings.getDateFormat());
        localeSettings.setTimeFormat(defaultSettings.getTimeFormat());
        localeSettings.setNumberFormat(defaultSettings.getNumberFormat());
        localeSettings.setDecimalSeparator(defaultSettings.getDecimalSeparator());
        localeSettings.setThousandsSeparator(defaultSettings.getThousandsSeparator());
        localeSettings.setTimezone(defaultSettings.getTimezone());
        
        return localeSettings;
    }
    
    /**
     * CrÃ©e un abonnement adaptÃ© au pays et Ã  la devise
     */
    public CompanySubscription createLocalizedSubscription(Company company, SubscriptionPlan plan) {
        CompanySubscription subscription = new CompanySubscription();
        subscription.setCompany(company);
        subscription.setSubscriptionPlan(plan);
        subscription.setSubscriptionCode(generateSubscriptionCode(company));
        subscription.setStartDate(LocalDate.now());
        subscription.setBillingCycle(plan.getBillingCycle());
        subscription.setBasePriceUSD(plan.getBasePriceUSD());
        subscription.setLocalCurrency(company.getCurrency());
        subscription.setStatus(CompanySubscription.SubscriptionStatus.ACTIVE);
        
        // Calculer le prix localisÃ© (sera fait par le service de pricing)
        subscription.setLocalPrice(plan.getBasePriceUSD()); // Temporaire
        
        return subscription;
    }
    
    /**
     * VÃ©rifie si une entreprise a accÃ¨s Ã  une fonctionnalitÃ©
     */
    public boolean hasFeatureAccess(Company company, String featureName) {
        // Pour l'instant, toutes les entreprises ont accÃ¨s aux fonctionnalitÃ©s de base
        // Cette logique sera implÃ©mentÃ©e avec la relation CompanySubscription
        return company != null && company.isActive();
    }
    
    /**
     * VÃ©rifie les limites d'utilisation pour une entreprise
     */
    public boolean checkUsageLimits(Company company, String resourceType, int requestedAmount) {
        // Pour l'instant, pas de limites strictes
        // Cette logique sera implÃ©mentÃ©e avec la relation CompanySubscription
        return company != null && company.isActive();
    }
    
    /**
     * RÃ©cupÃ¨re la configuration comptable pour une entreprise
     */
    public Map<String, Object> getAccountingConfig(Company company) {
        Map<String, Object> config = new HashMap<>();
        
        // Standard comptable
        AccountingStandard standard = accountingStandardService.getRecommendedStandardForCountry(company.getCountryCode());
        config.put("accountingStandard", standard);
        config.put("chartOfAccounts", accountingStandardService.getChartOfAccounts(standard));
        
        // Devises supportÃ©es
        config.put("supportedCurrencies", accountingStandardService.getSupportedCurrencies(standard));
        
        // RÃ©gions supportÃ©es
        config.put("supportedRegions", accountingStandardService.getSupportedRegions(standard));
        
        // MÃ©tadonnÃ©es du standard
        config.put("standardMetadata", accountingStandardService.getStandardMetadata(standard));
        
        return config;
    }
    
    /**
     * RÃ©cupÃ¨re la configuration de localisation pour une entreprise
     */
    public Map<String, Object> getLocalizationConfig(Company company) {
        Map<String, Object> config = new HashMap<>();
        
        // ParamÃ¨tres de localisation
        LocaleSettings localeSettings = configureLocaleSettings(company);
        config.put("localeSettings", localeSettings);
        
        // Pays supportÃ©s
        config.put("supportedCountries", localizationService.getSupportedCountries());
        
        // Langues supportÃ©es
        config.put("supportedLanguages", localizationService.getSupportedLanguages());
        
        // Devises supportÃ©es
        config.put("supportedCurrencies", localizationService.getSupportedCurrencies());
        
        return config;
    }
    
    /**
     * GÃ©nÃ¨re un code d'abonnement unique
     */
    private String generateSubscriptionCode(Company company) {
        String prefix = company.getCountryCode().toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + "-" + timestamp.substring(timestamp.length() - 8);
    }
    
    /**
     * RÃ©cupÃ¨re le nom du pays
     */
    private String getCountryName(String countryCode) {
        Map<String, String> countries = localizationService.getSupportedCountries();
        return countries.getOrDefault(countryCode, countryCode);
    }
    
    /**
     * RÃ©cupÃ¨re la locale par dÃ©faut
     */
    private String getDefaultLocale(String countryCode) {
        String language = localizationService.getDefaultLanguage(countryCode);
        return language + "_" + countryCode;
    }
    
    /**
     * VÃ©rifie si une entreprise peut utiliser une fonctionnalitÃ© IA
     */
    public boolean canUseAIFeature(Company company, String aiFeature) {
        if (!hasFeatureAccess(company, "AI_FEATURES")) {
            return false;
        }
        
        // VÃ©rifications spÃ©cifiques par fonctionnalitÃ© IA
        switch (aiFeature.toUpperCase()) {
            case "DOCUMENT_ANALYSIS":
                return hasFeatureAccess(company, "AI_DOCUMENT_ANALYSIS");
            case "FINANCIAL_PREDICTION":
                return hasFeatureAccess(company, "AI_FINANCIAL_PREDICTION");
            case "AUTOMATED_ENTRIES":
                return hasFeatureAccess(company, "AI_AUTOMATED_ENTRIES");
            default:
                return hasFeatureAccess(company, "AI_BASIC");
        }
    }
    
    /**
     * RÃ©cupÃ¨re les statistiques d'utilisation pour une entreprise
     */
    public Map<String, Object> getUsageStatistics(Company company) {
        Map<String, Object> stats = new HashMap<>();
        
        if (company == null) {
            return stats;
        }
        
        // Statistiques de base de l'entreprise
        stats.put("companyName", company.getName());
        stats.put("countryCode", company.getCountryCode());
        stats.put("currency", company.getCurrency());
        stats.put("isActive", company.isActive());
        stats.put("createdAt", company.getCreatedAt());
        
        // Statistiques d'abonnement - implÃ©mentation future
        stats.put("subscriptionStatus", "Not implemented yet");
        
        return stats;
    }
}


