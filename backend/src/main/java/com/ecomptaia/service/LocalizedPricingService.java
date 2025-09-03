package com.ecomptaia.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Service pour gérer les prix localisés selon la devise du pays du client
 */
@Service
public class LocalizedPricingService {

    // Taux de change de référence (EUR comme devise de base)
    private final Map<String, BigDecimal> exchangeRates = new HashMap<>();
    
    // Prix de base en EUR pour chaque type d'abonnement
    private final Map<String, BigDecimal> basePricesEUR = new HashMap<>();
    
    public LocalizedPricingService() {
        initializeExchangeRates();
        initializeBasePrices();
    }
    
    /**
     * Initialiser les taux de change (taux approximatifs)
     */
    private void initializeExchangeRates() {
        // Devises OHADA
        exchangeRates.put("XOF", new BigDecimal("655.957")); // Franc CFA BCEAO
        exchangeRates.put("XAF", new BigDecimal("655.957")); // Franc CFA BEAC
        exchangeRates.put("CDF", new BigDecimal("2700.00")); // Franc congolais
        
        // Devises internationales
        exchangeRates.put("USD", new BigDecimal("1.08"));
        exchangeRates.put("GBP", new BigDecimal("0.85"));
        exchangeRates.put("JPY", new BigDecimal("160.00"));
        exchangeRates.put("CAD", new BigDecimal("1.45"));
        exchangeRates.put("AUD", new BigDecimal("1.65"));
        exchangeRates.put("CHF", new BigDecimal("0.95"));
        exchangeRates.put("CNY", new BigDecimal("7.80"));
        exchangeRates.put("INR", new BigDecimal("90.00"));
        exchangeRates.put("BRL", new BigDecimal("5.40"));
        exchangeRates.put("MXN", new BigDecimal("18.50"));
        exchangeRates.put("ZAR", new BigDecimal("20.50"));
        exchangeRates.put("NGN", new BigDecimal("980.00"));
        exchangeRates.put("EGP", new BigDecimal("33.50"));
        exchangeRates.put("KES", new BigDecimal("170.00"));
        exchangeRates.put("GHS", new BigDecimal("13.50"));
        exchangeRates.put("MAD", new BigDecimal("10.80"));
        exchangeRates.put("TND", new BigDecimal("3.35"));
        exchangeRates.put("DZD", new BigDecimal("145.00"));
        exchangeRates.put("LYD", new BigDecimal("5.25"));
        exchangeRates.put("SDG", new BigDecimal("650.00"));
        exchangeRates.put("ETB", new BigDecimal("60.00"));
        exchangeRates.put("UGX", new BigDecimal("4100.00"));
        exchangeRates.put("TZS", new BigDecimal("2800.00"));
        exchangeRates.put("MWK", new BigDecimal("1800.00"));
        exchangeRates.put("ZMW", new BigDecimal("28.50"));
        exchangeRates.put("BWP", new BigDecimal("14.50"));
        exchangeRates.put("NAD", new BigDecimal("20.50"));
        exchangeRates.put("SZL", new BigDecimal("20.50"));
        exchangeRates.put("LSL", new BigDecimal("20.50"));
        exchangeRates.put("EUR", BigDecimal.ONE); // Devise de référence
    }
    
    /**
     * Initialiser les prix de base en EUR
     */
    private void initializeBasePrices() {
        // Prix mensuels en EUR
        basePricesEUR.put("STARTER", new BigDecimal("29.99"));
        basePricesEUR.put("PROFESSIONAL", new BigDecimal("79.99"));
        basePricesEUR.put("ENTERPRISE", new BigDecimal("199.99"));
        
        // Prix trimestriels en EUR (avec réduction de 10%)
        basePricesEUR.put("STARTER_QUARTERLY", new BigDecimal("80.97")); // 29.99 * 3 * 0.9
        basePricesEUR.put("PROFESSIONAL_QUARTERLY", new BigDecimal("215.97")); // 79.99 * 3 * 0.9
        basePricesEUR.put("ENTERPRISE_QUARTERLY", new BigDecimal("539.97")); // 199.99 * 3 * 0.9
        
        // Prix annuels en EUR (avec réduction de 20%)
        basePricesEUR.put("STARTER_YEARLY", new BigDecimal("287.90")); // 29.99 * 12 * 0.8
        basePricesEUR.put("PROFESSIONAL_YEARLY", new BigDecimal("767.90")); // 79.99 * 12 * 0.8
        basePricesEUR.put("ENTERPRISE_YEARLY", new BigDecimal("1919.90")); // 199.99 * 12 * 0.8
    }
    
    /**
     * Obtenir le prix localisé pour un abonnement
     */
    public BigDecimal getLocalizedPrice(String planName, String currency, String billingCycle) {
        String priceKey = planName + "_" + billingCycle;
        BigDecimal basePriceEUR = basePricesEUR.get(priceKey);
        
        if (basePriceEUR == null) {
            // Si pas de prix spécifique pour le cycle, utiliser le prix mensuel
            basePriceEUR = basePricesEUR.get(planName);
        }
        
        if (basePriceEUR == null) {
            throw new IllegalArgumentException("Plan d'abonnement non trouvé: " + planName);
        }
        
        return convertToLocalCurrency(basePriceEUR, currency);
    }
    
    /**
     * Convertir un prix EUR vers la devise locale
     */
    public BigDecimal convertToLocalCurrency(BigDecimal priceEUR, String targetCurrency) {
        if ("EUR".equals(targetCurrency)) {
            return priceEUR;
        }
        
        BigDecimal exchangeRate = exchangeRates.get(targetCurrency);
        if (exchangeRate == null) {
            // Si la devise n'est pas supportée, retourner le prix en EUR
            return priceEUR;
        }
        
        return priceEUR.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Obtenir le taux de change pour une devise
     */
    public BigDecimal getExchangeRate(String currency) {
        return exchangeRates.getOrDefault(currency, BigDecimal.ONE);
    }
    
    /**
     * Obtenir tous les taux de change
     */
    public Map<String, BigDecimal> getAllExchangeRates() {
        return new HashMap<>(exchangeRates);
    }
    
    /**
     * Mettre à jour un taux de change
     */
    public void updateExchangeRate(String currency, BigDecimal rate) {
        exchangeRates.put(currency, rate);
    }
    
    /**
     * Obtenir les prix de base en EUR
     */
    public Map<String, BigDecimal> getBasePricesEUR() {
        return new HashMap<>(basePricesEUR);
    }
    
    /**
     * Calculer le prix avec remise selon le cycle de facturation
     */
    public BigDecimal calculateDiscountedPrice(BigDecimal basePrice, String billingCycle) {
        switch (billingCycle.toUpperCase()) {
            case "MONTHLY":
                return basePrice;
            case "QUARTERLY":
                return basePrice.multiply(new BigDecimal("3"))
                               .multiply(new BigDecimal("0.9")) // 10% de réduction
                               .setScale(2, RoundingMode.HALF_UP);
            case "YEARLY":
                return basePrice.multiply(new BigDecimal("12"))
                               .multiply(new BigDecimal("0.8")) // 20% de réduction
                               .setScale(2, RoundingMode.HALF_UP);
            default:
                return basePrice;
        }
    }
    
    /**
     * Obtenir le symbole de devise
     */
    public String getCurrencySymbol(String currency) {
        Map<String, String> symbols = new HashMap<>();
        symbols.put("EUR", "€");
        symbols.put("USD", "$");
        symbols.put("GBP", "£");
        symbols.put("XOF", "CFA");
        symbols.put("XAF", "FCFA");
        symbols.put("CDF", "FC");
        symbols.put("JPY", "¥");
        symbols.put("CNY", "¥");
        symbols.put("INR", "₹");
        symbols.put("BRL", "R$");
        symbols.put("MXN", "$");
        symbols.put("ZAR", "R");
        symbols.put("NGN", "₦");
        symbols.put("EGP", "E£");
        symbols.put("KES", "KSh");
        symbols.put("GHS", "GH₵");
        symbols.put("MAD", "MAD");
        symbols.put("TND", "TND");
        symbols.put("DZD", "DZD");
        symbols.put("LYD", "LYD");
        symbols.put("SDG", "SDG");
        symbols.put("ETB", "ETB");
        symbols.put("UGX", "UGX");
        symbols.put("TZS", "TZS");
        symbols.put("MWK", "MWK");
        symbols.put("ZMW", "ZMW");
        symbols.put("BWP", "P");
        symbols.put("NAD", "N$");
        symbols.put("SZL", "SZL");
        symbols.put("LSL", "LSL");
        
        return symbols.getOrDefault(currency, currency);
    }
}





