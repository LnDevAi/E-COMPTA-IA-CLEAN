package com.ecomptaia.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CountrySpecificRules {

    // Règles métier par pays
    private final Map<String, BusinessRules> rulesByCountry = new HashMap<>();
    
    // Règles de validation par standard comptable
    private final Map<String, ValidationRules> validationByStandard = new HashMap<>();

    public CountrySpecificRules() {
        initializeRules();
    }

    private void initializeRules() {
        // === RÈGLES SÉNÉGAL (SYSCOHADA) ===
        BusinessRules senegalRules = new BusinessRules();
        senegalRules.setCountryCode("SN");
        senegalRules.setAccountingStandard("SYSCOHADA");
        senegalRules.setCurrency("XOF");
        senegalRules.setLocale("fr_SN");
        senegalRules.setVatRate(18.0);
        senegalRules.setCorporateTaxRate(30.0);
        senegalRules.setMaxVatThreshold(50000000.0); // 50M XOF
        senegalRules.setRequireVatNumber(true);
        senegalRules.setRequireTaxId(true);
        senegalRules.setDecimalPlaces(0); // Pas de décimales pour XOF
        senegalRules.setDateFormat("dd/MM/yyyy");
        senegalRules.setNumberFormat("#,##0");
        rulesByCountry.put("SN", senegalRules);

        // === RÈGLES FRANCE (PCG) ===
        BusinessRules franceRules = new BusinessRules();
        franceRules.setCountryCode("FR");
        franceRules.setAccountingStandard("PCG");
        franceRules.setCurrency("EUR");
        franceRules.setLocale("fr_FR");
        franceRules.setVatRate(20.0);
        franceRules.setCorporateTaxRate(25.0);
        franceRules.setMaxVatThreshold(82800.0); // 82,800 EUR
        franceRules.setRequireVatNumber(true);
        franceRules.setRequireTaxId(true);
        franceRules.setDecimalPlaces(2);
        franceRules.setDateFormat("dd/MM/yyyy");
        franceRules.setNumberFormat("#,##0.00");
        rulesByCountry.put("FR", franceRules);

        // === RÈGLES ÉTATS-UNIS (US GAAP) ===
        BusinessRules usaRules = new BusinessRules();
        usaRules.setCountryCode("US");
        usaRules.setAccountingStandard("US_GAAP");
        usaRules.setCurrency("USD");
        usaRules.setLocale("en_US");
        usaRules.setVatRate(0.0); // Pas de TVA fédérale
        usaRules.setCorporateTaxRate(21.0);
        usaRules.setMaxVatThreshold(0.0);
        usaRules.setRequireVatNumber(false);
        usaRules.setRequireTaxId(true);
        usaRules.setDecimalPlaces(2);
        usaRules.setDateFormat("MM/dd/yyyy");
        usaRules.setNumberFormat("#,##0.00");
        rulesByCountry.put("US", usaRules);

        // === RÈGLES ALLEMAGNE (HGB) ===
        BusinessRules germanyRules = new BusinessRules();
        germanyRules.setCountryCode("DE");
        germanyRules.setAccountingStandard("HGB");
        germanyRules.setCurrency("EUR");
        germanyRules.setLocale("de_DE");
        germanyRules.setVatRate(19.0);
        germanyRules.setCorporateTaxRate(15.0);
        germanyRules.setMaxVatThreshold(22000.0); // 22,000 EUR
        germanyRules.setRequireVatNumber(true);
        germanyRules.setRequireTaxId(true);
        germanyRules.setDecimalPlaces(2);
        germanyRules.setDateFormat("dd.MM.yyyy");
        germanyRules.setNumberFormat("#,##0.00");
        rulesByCountry.put("DE", germanyRules);

        // === RÈGLES CÔTE D'IVOIRE (SYSCOHADA) ===
        BusinessRules coteDIvoireRules = new BusinessRules();
        coteDIvoireRules.setCountryCode("CI");
        coteDIvoireRules.setAccountingStandard("SYSCOHADA");
        coteDIvoireRules.setCurrency("XOF");
        coteDIvoireRules.setLocale("fr_CI");
        coteDIvoireRules.setVatRate(18.0);
        coteDIvoireRules.setCorporateTaxRate(25.0);
        coteDIvoireRules.setMaxVatThreshold(50000000.0);
        coteDIvoireRules.setRequireVatNumber(true);
        coteDIvoireRules.setRequireTaxId(true);
        coteDIvoireRules.setDecimalPlaces(0);
        coteDIvoireRules.setDateFormat("dd/MM/yyyy");
        coteDIvoireRules.setNumberFormat("#,##0");
        rulesByCountry.put("CI", coteDIvoireRules);

        // === RÈGLES DE VALIDATION PAR STANDARD ===
        
        // SYSCOHADA
        ValidationRules syscohadaValidation = new ValidationRules();
        syscohadaValidation.setStandard("SYSCOHADA");
        syscohadaValidation.setAccountNumberLength(10);
        syscohadaValidation.setAccountNumberFormat("^[1-7]\\d{9}$");
        syscohadaValidation.setRequireAccountClass(true);
        syscohadaValidation.setMaxAccountClasses(7);
        syscohadaValidation.setRequireBalancedEntries(true);
        syscohadaValidation.setRequireAnalyticalAccounts(false);
        validationByStandard.put("SYSCOHADA", syscohadaValidation);

        // PCG
        ValidationRules pcgValidation = new ValidationRules();
        pcgValidation.setStandard("PCG");
        pcgValidation.setAccountNumberLength(10);
        pcgValidation.setAccountNumberFormat("^[1-8]\\d{9}$");
        pcgValidation.setRequireAccountClass(true);
        pcgValidation.setMaxAccountClasses(8);
        pcgValidation.setRequireBalancedEntries(true);
        pcgValidation.setRequireAnalyticalAccounts(true);
        validationByStandard.put("PCG", pcgValidation);

        // US GAAP
        ValidationRules usGaapValidation = new ValidationRules();
        usGaapValidation.setStandard("US_GAAP");
        usGaapValidation.setAccountNumberLength(6);
        usGaapValidation.setAccountNumberFormat("^\\d{6}$");
        usGaapValidation.setRequireAccountClass(false);
        usGaapValidation.setMaxAccountClasses(0);
        usGaapValidation.setRequireBalancedEntries(true);
        usGaapValidation.setRequireAnalyticalAccounts(false);
        validationByStandard.put("US_GAAP", usGaapValidation);
    }

    public BusinessRules getRulesForCountry(String countryCode) {
        return rulesByCountry.get(countryCode.toUpperCase());
    }

    public ValidationRules getValidationForStandard(String standard) {
        return validationByStandard.get(standard.toUpperCase());
    }

    public boolean isCountrySupported(String countryCode) {
        return rulesByCountry.containsKey(countryCode.toUpperCase());
    }

    public boolean isStandardSupported(String standard) {
        return validationByStandard.containsKey(standard.toUpperCase());
    }

    // Classes internes pour les règles
    public static class BusinessRules {
        private String countryCode;
        private String accountingStandard;
        private String currency;
        private String locale;
        private Double vatRate;
        private Double corporateTaxRate;
        private Double maxVatThreshold;
        private Boolean requireVatNumber;
        private Boolean requireTaxId;
        private Integer decimalPlaces;
        private String dateFormat;
        private String numberFormat;

        // Getters et Setters
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

        public String getAccountingStandard() { return accountingStandard; }
        public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getLocale() { return locale; }
        public void setLocale(String locale) { this.locale = locale; }

        public Double getVatRate() { return vatRate; }
        public void setVatRate(Double vatRate) { this.vatRate = vatRate; }

        public Double getCorporateTaxRate() { return corporateTaxRate; }
        public void setCorporateTaxRate(Double corporateTaxRate) { this.corporateTaxRate = corporateTaxRate; }

        public Double getMaxVatThreshold() { return maxVatThreshold; }
        public void setMaxVatThreshold(Double maxVatThreshold) { this.maxVatThreshold = maxVatThreshold; }

        public Boolean getRequireVatNumber() { return requireVatNumber; }
        public void setRequireVatNumber(Boolean requireVatNumber) { this.requireVatNumber = requireVatNumber; }

        public Boolean getRequireTaxId() { return requireTaxId; }
        public void setRequireTaxId(Boolean requireTaxId) { this.requireTaxId = requireTaxId; }

        public Integer getDecimalPlaces() { return decimalPlaces; }
        public void setDecimalPlaces(Integer decimalPlaces) { this.decimalPlaces = decimalPlaces; }

        public String getDateFormat() { return dateFormat; }
        public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }

        public String getNumberFormat() { return numberFormat; }
        public void setNumberFormat(String numberFormat) { this.numberFormat = numberFormat; }
    }

    public static class ValidationRules {
        private String standard;
        private Integer accountNumberLength;
        private String accountNumberFormat;
        private Boolean requireAccountClass;
        private Integer maxAccountClasses;
        private Boolean requireBalancedEntries;
        private Boolean requireAnalyticalAccounts;

        // Getters et Setters
        public String getStandard() { return standard; }
        public void setStandard(String standard) { this.standard = standard; }

        public Integer getAccountNumberLength() { return accountNumberLength; }
        public void setAccountNumberLength(Integer accountNumberLength) { this.accountNumberLength = accountNumberLength; }

        public String getAccountNumberFormat() { return accountNumberFormat; }
        public void setAccountNumberFormat(String accountNumberFormat) { this.accountNumberFormat = accountNumberFormat; }

        public Boolean getRequireAccountClass() { return requireAccountClass; }
        public void setRequireAccountClass(Boolean requireAccountClass) { this.requireAccountClass = requireAccountClass; }

        public Integer getMaxAccountClasses() { return maxAccountClasses; }
        public void setMaxAccountClasses(Integer maxAccountClasses) { this.maxAccountClasses = maxAccountClasses; }

        public Boolean getRequireBalancedEntries() { return requireBalancedEntries; }
        public void setRequireBalancedEntries(Boolean requireBalancedEntries) { this.requireBalancedEntries = requireBalancedEntries; }

        public Boolean getRequireAnalyticalAccounts() { return requireAnalyticalAccounts; }
        public void setRequireAnalyticalAccounts(Boolean requireAnalyticalAccounts) { this.requireAnalyticalAccounts = requireAnalyticalAccounts; }
    }
}





