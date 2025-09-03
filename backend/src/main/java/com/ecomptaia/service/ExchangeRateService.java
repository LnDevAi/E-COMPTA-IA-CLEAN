package com.ecomptaia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service pour la gestion des taux de change
 * Utilise l'API gratuite exchangerate-api.com
 */
@Service
public class ExchangeRateService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_BASE_URL = "https://api.exchangerate-api.com/v4/latest/";
    
    // Devises principales supportées
    private static final String[] SUPPORTED_CURRENCIES = {
        "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "SEK", "NZD",
        // OHADA
        "XOF", "XAF", "CDF", "GMD", "GHS", "NGN", "SLL", "TND", "MAD", "EGP",
        // Autres pays
        "INR", "BRL", "MXN", "KRW", "SGD", "HKD", "NOK", "DKK", "PLN", "CZK"
    };

    /**
     * Obtenir le taux de change actuel
     */
    @Cacheable(value = "exchangeRates", key = "#baseCurrency")
    public Map<String, Object> getExchangeRates(String baseCurrency) {
        try {
            String url = API_BASE_URL + baseCurrency;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null) {
                response.put("timestamp", LocalDateTime.now());
                response.put("source", "exchangerate-api.com");
                return response;
            }
            
            return createFallbackRates(baseCurrency);
            
        } catch (Exception e) {
            return createFallbackRates(baseCurrency);
        }
    }

    /**
     * Convertir un montant d'une devise à une autre
     */
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        try {
            Map<String, Object> rates = getExchangeRates(fromCurrency);
            @SuppressWarnings("unchecked")
            Map<String, Double> ratesMap = (Map<String, Double>) rates.get("rates");
            
            if (ratesMap != null && ratesMap.containsKey(toCurrency)) {
                double rate = ratesMap.get(toCurrency);
                return amount.multiply(BigDecimal.valueOf(rate))
                           .setScale(2, RoundingMode.HALF_UP);
            }
            
        } catch (Exception e) {
            // En cas d'erreur, utiliser des taux de change fixes
            return convertWithFixedRates(amount, fromCurrency, toCurrency);
        }
        
        return amount; // Retourner le montant original si conversion impossible
    }

    /**
     * Obtenir le taux de change entre deux devises
     */
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        try {
            Map<String, Object> rates = getExchangeRates(fromCurrency);
            @SuppressWarnings("unchecked")
            Map<String, Double> ratesMap = (Map<String, Double>) rates.get("rates");
            
            if (ratesMap != null && ratesMap.containsKey(toCurrency)) {
                return BigDecimal.valueOf(ratesMap.get(toCurrency));
            }
            
        } catch (Exception e) {
            return getFixedExchangeRate(fromCurrency, toCurrency);
        }
        
        return BigDecimal.ONE;
    }

    /**
     * Convertir un prix d'abonnement dans la devise locale
     */
    public Map<String, Object> convertSubscriptionPrice(BigDecimal basePrice, String baseCurrency, String targetCurrency) {
        Map<String, Object> result = new HashMap<>();
        
        BigDecimal convertedPrice = convertCurrency(basePrice, baseCurrency, targetCurrency);
        BigDecimal exchangeRate = getExchangeRate(baseCurrency, targetCurrency);
        
        result.put("originalPrice", basePrice);
        result.put("originalCurrency", baseCurrency);
        result.put("convertedPrice", convertedPrice);
        result.put("targetCurrency", targetCurrency);
        result.put("exchangeRate", exchangeRate);
        result.put("conversionDate", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return result;
    }

    /**
     * Obtenir les devises supportées
     */
    public Map<String, Object> getSupportedCurrencies() {
        Map<String, Object> result = new HashMap<>();
        result.put("currencies", SUPPORTED_CURRENCIES);
        result.put("count", SUPPORTED_CURRENCIES.length);
        result.put("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return result;
    }

    /**
     * Rafraîchir le cache des taux de change
     */
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void refreshExchangeRates() {
        // Cette méthode vide le cache pour forcer le rechargement
    }

    /**
     * Créer des taux de change de secours
     */
    private Map<String, Object> createFallbackRates(String baseCurrency) {
        Map<String, Object> fallback = new HashMap<>();
        Map<String, Double> rates = new HashMap<>();
        
        // Taux de change fixes pour les devises principales
        switch (baseCurrency) {
            case "USD":
                rates.put("EUR", 0.85);
                rates.put("GBP", 0.73);
                rates.put("XOF", 550.0);
                rates.put("XAF", 550.0);
                rates.put("CDF", 2000.0);
                break;
            case "EUR":
                rates.put("USD", 1.18);
                rates.put("GBP", 0.86);
                rates.put("XOF", 655.957);
                rates.put("XAF", 655.957);
                rates.put("CDF", 2350.0);
                break;
            case "XOF":
                rates.put("USD", 0.0018);
                rates.put("EUR", 0.0015);
                rates.put("GBP", 0.0013);
                rates.put("XAF", 1.0);
                rates.put("CDF", 3.6);
                break;
            case "XAF":
                rates.put("USD", 0.0018);
                rates.put("EUR", 0.0015);
                rates.put("GBP", 0.0013);
                rates.put("XOF", 1.0);
                rates.put("CDF", 3.6);
                break;
            default:
                rates.put("USD", 1.0);
                rates.put("EUR", 0.85);
                rates.put("GBP", 0.73);
                rates.put("XOF", 550.0);
                rates.put("XAF", 550.0);
        }
        
        fallback.put("base", baseCurrency);
        fallback.put("rates", rates);
        fallback.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        fallback.put("timestamp", LocalDateTime.now());
        fallback.put("source", "fallback");
        
        return fallback;
    }

    /**
     * Conversion avec des taux fixes
     */
    private BigDecimal convertWithFixedRates(BigDecimal amount, String fromCurrency, String toCurrency) {
        BigDecimal rate = getFixedExchangeRate(fromCurrency, toCurrency);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Obtenir un taux de change fixe
     */
    private BigDecimal getFixedExchangeRate(String fromCurrency, String toCurrency) {
        // Taux de change fixes pour les devises principales
        if ("USD".equals(fromCurrency)) {
            switch (toCurrency) {
                case "EUR": return new BigDecimal("0.85");
                case "GBP": return new BigDecimal("0.73");
                case "XOF": return new BigDecimal("550.0");
                case "XAF": return new BigDecimal("550.0");
                case "CDF": return new BigDecimal("2000.0");
            }
        } else if ("EUR".equals(fromCurrency)) {
            switch (toCurrency) {
                case "USD": return new BigDecimal("1.18");
                case "GBP": return new BigDecimal("0.86");
                case "XOF": return new BigDecimal("655.957");
                case "XAF": return new BigDecimal("655.957");
                case "CDF": return new BigDecimal("2350.0");
            }
        } else if ("XOF".equals(fromCurrency)) {
            switch (toCurrency) {
                case "USD": return new BigDecimal("0.0018");
                case "EUR": return new BigDecimal("0.0015");
                case "GBP": return new BigDecimal("0.0013");
                case "XAF": return new BigDecimal("1.0");
                case "CDF": return new BigDecimal("3.6");
            }
        } else if ("XAF".equals(fromCurrency)) {
            switch (toCurrency) {
                case "USD": return new BigDecimal("0.0018");
                case "EUR": return new BigDecimal("0.0015");
                case "GBP": return new BigDecimal("0.0013");
                case "XOF": return new BigDecimal("1.0");
                case "CDF": return new BigDecimal("3.6");
            }
        }
        
        return BigDecimal.ONE; // Taux par défaut
    }

    /**
     * Conversion asynchrone
     */
    public CompletableFuture<BigDecimal> convertCurrencyAsync(BigDecimal amount, String fromCurrency, String toCurrency) {
        return CompletableFuture.supplyAsync(() -> convertCurrency(amount, fromCurrency, toCurrency));
    }

    /**
     * Vérifier si une devise est supportée
     */
    public boolean isCurrencySupported(String currency) {
        for (String supported : SUPPORTED_CURRENCIES) {
            if (supported.equals(currency)) {
                return true;
            }
        }
        return false;
    }
}




