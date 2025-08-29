package com.ecomptaia.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service pour la localisation complète (devises, langues, formats)
 */
@Service
public class LocalizationService {

    // Configuration complète par pays
    private static final Map<String, Map<String, Object>> COUNTRY_LOCALIZATION = Map.of(
        "BF", Map.of( // Burkina Faso
            "currency", "XOF",
            "currencyName", "Franc CFA",
            "language", "fr",
            "languageName", "Français",
            "dateFormat", "dd/MM/yyyy",
            "numberFormat", "#,##0.00",
            "decimalSeparator", ",",
            "thousandsSeparator", " ",
            "timezone", "Africa/Ouagadougou",
            "locale", "fr-BF"
        ),
        "CI", Map.of( // Côte d'Ivoire
            "currency", "XOF",
            "currencyName", "Franc CFA",
            "language", "fr",
            "languageName", "Français",
            "dateFormat", "dd/MM/yyyy",
            "numberFormat", "#,##0.00",
            "decimalSeparator", ",",
            "thousandsSeparator", " ",
            "timezone", "Africa/Abidjan",
            "locale", "fr-CI"
        ),
        "SN", Map.of( // Sénégal
            "currency", "XOF",
            "currencyName", "Franc CFA",
            "language", "fr",
            "languageName", "Français",
            "dateFormat", "dd/MM/yyyy",
            "numberFormat", "#,##0.00",
            "decimalSeparator", ",",
            "thousandsSeparator", " ",
            "timezone", "Africa/Dakar",
            "locale", "fr-SN"
        ),
        "FR", Map.of( // France
            "currency", "EUR",
            "currencyName", "Euro",
            "language", "fr",
            "languageName", "Français",
            "dateFormat", "dd/MM/yyyy",
            "numberFormat", "#,##0.00",
            "decimalSeparator", ",",
            "thousandsSeparator", " ",
            "timezone", "Europe/Paris",
            "locale", "fr-FR"
        ),
        "US", Map.of( // États-Unis
            "currency", "USD",
            "currencyName", "US Dollar",
            "language", "en",
            "languageName", "English",
            "dateFormat", "MM/dd/yyyy",
            "numberFormat", "#,##0.00",
            "decimalSeparator", ".",
            "thousandsSeparator", ",",
            "timezone", "America/New_York",
            "locale", "en-US"
        ),
        "GB", Map.of( // Royaume-Uni
            "currency", "GBP",
            "currencyName", "British Pound",
            "language", "en",
            "languageName", "English",
            "dateFormat", "dd/MM/yyyy",
            "numberFormat", "#,##0.00",
            "decimalSeparator", ".",
            "thousandsSeparator", ",",
            "timezone", "Europe/London",
            "locale", "en-GB"
        ),
        "DE", Map.of( // Allemagne
            "currency", "EUR",
            "currencyName", "Euro",
            "language", "de",
            "languageName", "Deutsch",
            "dateFormat", "dd.MM.yyyy",
            "numberFormat", "#,##0.00",
            "decimalSeparator", ",",
            "thousandsSeparator", ".",
            "timezone", "Europe/Berlin",
            "locale", "de-DE"
        )
    );

    // Taux de change (simulés - à remplacer par un service réel)
    private static final Map<String, BigDecimal> EXCHANGE_RATES = Map.of(
        "XOF", new BigDecimal("0.00152"), // 1 XOF = 0.00152 EUR
        "EUR", new BigDecimal("1.0"),     // 1 EUR = 1.0 EUR
        "USD", new BigDecimal("0.85"),    // 1 USD = 0.85 EUR
        "GBP", new BigDecimal("1.18"),    // 1 GBP = 1.18 EUR
        "JPY", new BigDecimal("0.0076"),  // 1 JPY = 0.0076 EUR
        "CNY", new BigDecimal("0.13"),    // 1 CNY = 0.13 EUR
        "INR", new BigDecimal("0.011"),   // 1 INR = 0.011 EUR
        "BRL", new BigDecimal("0.16"),    // 1 BRL = 0.16 EUR
        "CAD", new BigDecimal("0.68"),    // 1 CAD = 0.68 EUR
        "AUD", new BigDecimal("0.62")     // 1 AUD = 0.62 EUR
    );

    /**
     * Obtenir la configuration de localisation pour un pays
     */
    public Map<String, Object> getLocalizationConfig(String countryCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> config = COUNTRY_LOCALIZATION.get(countryCode.toUpperCase());
            
            if (config == null) {
                result.put("error", "Configuration de localisation non trouvée pour: " + countryCode);
                result.put("defaultConfig", getDefaultConfig());
                return result;
            }
            
            result.put("countryCode", countryCode.toUpperCase());
            result.put("config", config);
            result.put("isSupported", true);
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération de la configuration: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Formater un montant selon la devise du pays
     */
    public String formatAmount(BigDecimal amount, String countryCode) {
        try {
            Map<String, Object> config = COUNTRY_LOCALIZATION.get(countryCode.toUpperCase());
            if (config == null) {
                return amount.toString() + " EUR"; // Valeur par défaut
            }
            
            String currency = (String) config.get("currency");
            String currencyName = (String) config.get("currencyName");
            String decimalSeparator = (String) config.get("decimalSeparator");
            String thousandsSeparator = (String) config.get("thousandsSeparator");
            
            // Formatage basique (à améliorer avec NumberFormat)
            String formattedAmount = formatNumber(amount, decimalSeparator, thousandsSeparator);
            
            return formattedAmount + " " + currency + " (" + currencyName + ")";
            
        } catch (Exception e) {
            return amount.toString() + " EUR";
        }
    }

    /**
     * Formater une date selon le format du pays
     */
    public String formatDate(LocalDate date, String countryCode) {
        try {
            Map<String, Object> config = COUNTRY_LOCALIZATION.get(countryCode.toUpperCase());
            if (config == null) {
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // Format ISO par défaut
            }
            
            String dateFormat = (String) config.get("dateFormat");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            
            return date.format(formatter);
            
        } catch (Exception e) {
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    /**
     * Convertir un montant d'une devise à une autre
     */
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        try {
            BigDecimal fromRate = EXCHANGE_RATES.get(fromCurrency.toUpperCase());
            BigDecimal toRate = EXCHANGE_RATES.get(toCurrency.toUpperCase());
            
            if (fromRate == null || toRate == null) {
                return amount; // Pas de conversion possible
            }
            
            // Conversion via EUR (devise de référence)
            BigDecimal amountInEUR = amount.multiply(fromRate);
            return amountInEUR.divide(toRate, 2, BigDecimal.ROUND_HALF_UP);
            
        } catch (Exception e) {
            return amount;
        }
    }

    /**
     * Obtenir la langue du pays
     */
    public String getLanguage(String countryCode) {
        try {
            Map<String, Object> config = COUNTRY_LOCALIZATION.get(countryCode.toUpperCase());
            if (config == null) {
                return "en"; // Anglais par défaut
            }
            
            return (String) config.get("language");
            
        } catch (Exception e) {
            return "en";
        }
    }

    /**
     * Obtenir la devise du pays
     */
    public String getCurrency(String countryCode) {
        try {
            Map<String, Object> config = COUNTRY_LOCALIZATION.get(countryCode.toUpperCase());
            if (config == null) {
                return "EUR"; // Euro par défaut
            }
            
            return (String) config.get("currency");
            
        } catch (Exception e) {
            return "EUR";
        }
    }

    /**
     * Obtenir le fuseau horaire du pays
     */
    public String getTimezone(String countryCode) {
        try {
            Map<String, Object> config = COUNTRY_LOCALIZATION.get(countryCode.toUpperCase());
            if (config == null) {
                return "UTC"; // UTC par défaut
            }
            
            return (String) config.get("timezone");
            
        } catch (Exception e) {
            return "UTC";
        }
    }

    /**
     * Obtenir tous les pays supportés avec leurs configurations
     */
    public Map<String, Object> getAllSupportedCountries() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> countries = new ArrayList<>();
            
            for (Map.Entry<String, Map<String, Object>> entry : COUNTRY_LOCALIZATION.entrySet()) {
                String countryCode = entry.getKey();
                Map<String, Object> config = entry.getValue();
                
                Map<String, Object> countryInfo = new HashMap<>();
                countryInfo.put("countryCode", countryCode);
                countryInfo.put("currency", config.get("currency"));
                countryInfo.put("currencyName", config.get("currencyName"));
                countryInfo.put("language", config.get("language"));
                countryInfo.put("languageName", config.get("languageName"));
                countryInfo.put("dateFormat", config.get("dateFormat"));
                countryInfo.put("timezone", config.get("timezone"));
                countryInfo.put("locale", config.get("locale"));
                
                countries.add(countryInfo);
            }
            
            result.put("countries", countries);
            result.put("totalCountries", countries.size());
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération des pays: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Obtenir les devises supportées
     */
    public Map<String, Object> getSupportedCurrencies() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Set<String> currencies = new HashSet<>();
            Map<String, String> currencyNames = new HashMap<>();
            
            for (Map<String, Object> config : COUNTRY_LOCALIZATION.values()) {
                String currency = (String) config.get("currency");
                String currencyName = (String) config.get("currencyName");
                
                currencies.add(currency);
                currencyNames.put(currency, currencyName);
            }
            
            result.put("currencies", new ArrayList<>(currencies));
            result.put("currencyNames", currencyNames);
            result.put("totalCurrencies", currencies.size());
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération des devises: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Obtenir les langues supportées
     */
    public Map<String, Object> getSupportedLanguages() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Set<String> languages = new HashSet<>();
            Map<String, String> languageNames = new HashMap<>();
            
            for (Map<String, Object> config : COUNTRY_LOCALIZATION.values()) {
                String language = (String) config.get("language");
                String languageName = (String) config.get("languageName");
                
                languages.add(language);
                languageNames.put(language, languageName);
            }
            
            result.put("languages", new ArrayList<>(languages));
            result.put("languageNames", languageNames);
            result.put("totalLanguages", languages.size());
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la récupération des langues: " + e.getMessage());
        }
        
        return result;
    }

    // Méthodes utilitaires privées
    private Map<String, Object> getDefaultConfig() {
        return Map.of(
            "currency", "EUR",
            "currencyName", "Euro",
            "language", "en",
            "languageName", "English",
            "dateFormat", "yyyy-MM-dd",
            "numberFormat", "#,##0.00",
            "decimalSeparator", ".",
            "thousandsSeparator", ",",
            "timezone", "UTC",
            "locale", "en-US"
        );
    }

    private String formatNumber(BigDecimal number, String decimalSeparator, String thousandsSeparator) {
        String numStr = number.toString();
        
        // Formatage basique (à améliorer)
        if (numStr.contains(".")) {
            String[] parts = numStr.split("\\.");
            String integerPart = parts[0];
            String decimalPart = parts[1];
            
            // Ajout des séparateurs de milliers
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < integerPart.length(); i++) {
                if (i > 0 && (integerPart.length() - i) % 3 == 0) {
                    formatted.append(thousandsSeparator);
                }
                formatted.append(integerPart.charAt(i));
            }
            
            return formatted.toString() + decimalSeparator + decimalPart;
        }
        
        return numStr;
    }
}





