package com.ecomptaia.service;

import com.ecomptaia.entity.Country;
import com.ecomptaia.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service pour l'internationalisation des systèmes comptables
 */
@Service
public class InternationalAccountingService {

    @Autowired
    private CountryRepository countryRepository;

    // Mapping des pays vers leurs systèmes comptables
    private static final Map<String, String> COUNTRY_ACCOUNTING_SYSTEMS = Map.ofEntries(
        Map.entry("BF", "SYSCOHADA"), // Burkina Faso
        Map.entry("BJ", "SYSCOHADA"), // Bénin
        Map.entry("CM", "SYSCOHADA"), // Cameroun
        Map.entry("CF", "SYSCOHADA"), // République centrafricaine
        Map.entry("TD", "SYSCOHADA"), // Tchad
        Map.entry("KM", "SYSCOHADA"), // Comores
        Map.entry("CG", "SYSCOHADA"), // Congo
        Map.entry("CI", "SYSCOHADA"), // Côte d'Ivoire
        Map.entry("GA", "SYSCOHADA"), // Gabon
        Map.entry("GN", "SYSCOHADA"), // Guinée
        Map.entry("GQ", "SYSCOHADA"), // Guinée équatoriale
        Map.entry("ML", "SYSCOHADA"), // Mali
        Map.entry("NE", "SYSCOHADA"), // Niger
        Map.entry("SN", "SYSCOHADA"), // Sénégal
        Map.entry("TG", "SYSCOHADA"), // Togo
        Map.entry("FR", "PCG"), // France
        Map.entry("DE", "HGB"), // Allemagne
        Map.entry("US", "US_GAAP"), // États-Unis
        Map.entry("GB", "UK_GAAP"), // Royaume-Uni
        Map.entry("CA", "ASPE"), // Canada
        Map.entry("BR", "BR_GAAP"), // Brésil
        Map.entry("IN", "IND_AS"), // Inde
        Map.entry("CN", "CAS"), // Chine
        Map.entry("JP", "JGAAP"), // Japon
        Map.entry("AU", "AASB") // Australie
    );

    // Configuration des systèmes comptables
    private static final Map<String, Map<String, Object>> ACCOUNTING_SYSTEM_CONFIG = Map.of(
        "SYSCOHADA", Map.of(
            "name", "Système Comptable OHADA",
            "description", "Système comptable harmonisé pour les pays de l'OHADA",
            "currency", "XOF",
            "language", "fr",
            "accountClasses", List.of("1", "2", "3", "4", "5", "6", "7"),
            "features", List.of("balance_sheet", "income_statement", "cash_flow", "annexes"),
            "validationRules", List.of("ohada_balance", "ohada_income", "ohada_cash_flow")
        ),
        "PCG", Map.of(
            "name", "Plan Comptable Général",
            "description", "Système comptable français",
            "currency", "EUR",
            "language", "fr",
            "accountClasses", List.of("1", "2", "3", "4", "5", "6", "7"),
            "features", List.of("balance_sheet", "income_statement", "cash_flow"),
            "validationRules", List.of("french_balance", "french_income")
        ),
        "IFRS", Map.of(
            "name", "International Financial Reporting Standards",
            "description", "Standards internationaux de reporting financier",
            "currency", "USD",
            "language", "en",
            "accountClasses", List.of("1", "2", "3", "4", "5", "6", "7"),
            "features", List.of("balance_sheet", "income_statement", "cash_flow", "equity"),
            "validationRules", List.of("ifrs_balance", "ifrs_income", "ifrs_cash_flow")
        ),
        "US_GAAP", Map.of(
            "name", "US Generally Accepted Accounting Principles",
            "description", "Principes comptables généralement admis aux États-Unis",
            "currency", "USD",
            "language", "en",
            "accountClasses", List.of("1", "2", "3", "4", "5", "6", "7"),
            "features", List.of("balance_sheet", "income_statement", "cash_flow"),
            "validationRules", List.of("us_balance", "us_income")
        )
    );

    /**
     * Obtenir le système comptable pour un pays
     */
    public Map<String, Object> getAccountingSystemForCountry(String countryCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String accountingSystem = COUNTRY_ACCOUNTING_SYSTEMS.get(countryCode.toUpperCase());
            
            if (accountingSystem == null) {
                result.put("error", "Système comptable non défini pour le pays: " + countryCode);
                result.put("defaultSystem", "IFRS");
                return result;
            }
            
            Map<String, Object> config = ACCOUNTING_SYSTEM_CONFIG.get(accountingSystem);
            if (config == null) {
                result.put("error", "Configuration non trouvée pour le système: " + accountingSystem);
                return result;
            }
            
            result.put("countryCode", countryCode.toUpperCase());
            result.put("accountingSystem", accountingSystem);
            result.put("config", config);
            result.put("isSupported", true);
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération du système comptable: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Obtenir tous les pays supportés
     */
    public Map<String, Object> getSupportedCountries() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> countries = new ArrayList<>();
            
            for (Map.Entry<String, String> entry : COUNTRY_ACCOUNTING_SYSTEMS.entrySet()) {
                String countryCode = entry.getKey();
                String accountingSystem = entry.getValue();
                
                Map<String, Object> countryInfo = new HashMap<>();
                countryInfo.put("countryCode", countryCode);
                countryInfo.put("accountingSystem", accountingSystem);
                
                Map<String, Object> systemConfig = ACCOUNTING_SYSTEM_CONFIG.get(accountingSystem);
                if (systemConfig != null) {
                    countryInfo.put("systemName", systemConfig.get("name"));
                    countryInfo.put("currency", systemConfig.get("currency"));
                    countryInfo.put("language", systemConfig.get("language"));
                }
                
                countries.add(countryInfo);
            }
            
            result.put("countries", countries);
            result.put("totalCountries", countries.size());
            result.put("supportedSystems", new ArrayList<>(ACCOUNTING_SYSTEM_CONFIG.keySet()));
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération des pays supportés: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Obtenir la configuration d'un système comptable
     */
    public Map<String, Object> getAccountingSystemConfig(String accountingSystem) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> config = ACCOUNTING_SYSTEM_CONFIG.get(accountingSystem.toUpperCase());
            
            if (config == null) {
                result.put("error", "Système comptable non supporté: " + accountingSystem);
                return result;
            }
            
            result.put("accountingSystem", accountingSystem.toUpperCase());
            result.put("config", config);
            result.put("isSupported", true);
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération de la configuration: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Valider la compatibilité d'un pays avec un système comptable
     */
    public Map<String, Object> validateCountryCompatibility(String countryCode, String accountingSystem) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String expectedSystem = COUNTRY_ACCOUNTING_SYSTEMS.get(countryCode.toUpperCase());
            
            if (expectedSystem == null) {
                result.put("isCompatible", false);
                result.put("message", "Pays non supporté: " + countryCode);
                result.put("recommendedSystem", "IFRS");
                return result;
            }
            
            boolean isCompatible = expectedSystem.equalsIgnoreCase(accountingSystem);
            
            result.put("isCompatible", isCompatible);
            result.put("countryCode", countryCode.toUpperCase());
            result.put("expectedSystem", expectedSystem);
            result.put("providedSystem", accountingSystem);
            
            if (isCompatible) {
                result.put("message", "Configuration compatible");
            } else {
                result.put("message", "Système comptable incorrect pour ce pays");
                result.put("recommendedSystem", expectedSystem);
            }
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la validation: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Obtenir les règles de validation pour un système comptable
     */
    public Map<String, Object> getValidationRules(String accountingSystem) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> config = ACCOUNTING_SYSTEM_CONFIG.get(accountingSystem.toUpperCase());
            
            if (config == null) {
                result.put("error", "Système comptable non supporté: " + accountingSystem);
                return result;
            }
            
            @SuppressWarnings("unchecked")
            List<String> validationRules = (List<String>) config.get("validationRules");
            
            result.put("accountingSystem", accountingSystem.toUpperCase());
            result.put("validationRules", validationRules);
            result.put("totalRules", validationRules != null ? validationRules.size() : 0);
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération des règles: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Obtenir les fonctionnalités disponibles pour un système comptable
     */
    public Map<String, Object> getAvailableFeatures(String accountingSystem) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> config = ACCOUNTING_SYSTEM_CONFIG.get(accountingSystem.toUpperCase());
            
            if (config == null) {
                result.put("error", "Système comptable non supporté: " + accountingSystem);
                return result;
            }
            
            @SuppressWarnings("unchecked")
            List<String> features = (List<String>) config.get("features");
            
            result.put("accountingSystem", accountingSystem.toUpperCase());
            result.put("features", features);
            result.put("totalFeatures", features != null ? features.size() : 0);
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération des fonctionnalités: " + e.getMessage());
        }
        
        return result;
    }
}





