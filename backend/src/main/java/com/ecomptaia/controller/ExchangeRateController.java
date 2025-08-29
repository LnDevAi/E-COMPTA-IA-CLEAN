package com.ecomptaia.controller;

import com.ecomptaia.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour l'API de taux de change
 */
@RestController
@RequestMapping("/api/exchange-rates")
@CrossOrigin(origins = "*")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    /**
     * Obtenir les taux de change pour une devise de base
     */
    @GetMapping("/rates/{baseCurrency}")
    public ResponseEntity<Map<String, Object>> getExchangeRates(@PathVariable String baseCurrency) {
        try {
            Map<String, Object> rates = exchangeRateService.getExchangeRates(baseCurrency.toUpperCase());
            return ResponseEntity.ok(rates);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des taux de change: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Convertir un montant d'une devise à une autre
     */
    @GetMapping("/convert")
    public ResponseEntity<Map<String, Object>> convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        
        try {
            BigDecimal convertedAmount = exchangeRateService.convertCurrency(amount, fromCurrency.toUpperCase(), toCurrency.toUpperCase());
            BigDecimal exchangeRate = exchangeRateService.getExchangeRate(fromCurrency.toUpperCase(), toCurrency.toUpperCase());
            
            Map<String, Object> result = new HashMap<>();
            result.put("originalAmount", amount);
            result.put("originalCurrency", fromCurrency.toUpperCase());
            result.put("convertedAmount", convertedAmount);
            result.put("targetCurrency", toCurrency.toUpperCase());
            result.put("exchangeRate", exchangeRate);
            result.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la conversion: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Convertir un prix d'abonnement
     */
    @GetMapping("/convert-subscription")
    public ResponseEntity<Map<String, Object>> convertSubscriptionPrice(
            @RequestParam BigDecimal basePrice,
            @RequestParam String baseCurrency,
            @RequestParam String targetCurrency) {
        
        try {
            Map<String, Object> result = exchangeRateService.convertSubscriptionPrice(
                basePrice, baseCurrency.toUpperCase(), targetCurrency.toUpperCase());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la conversion du prix d'abonnement: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtenir les devises supportées
     */
    @GetMapping("/currencies")
    public ResponseEntity<Map<String, Object>> getSupportedCurrencies() {
        try {
            Map<String, Object> currencies = exchangeRateService.getSupportedCurrencies();
            return ResponseEntity.ok(currencies);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des devises: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Vérifier si une devise est supportée
     */
    @GetMapping("/currencies/{currency}/supported")
    public ResponseEntity<Map<String, Object>> isCurrencySupported(@PathVariable String currency) {
        try {
            boolean supported = exchangeRateService.isCurrencySupported(currency.toUpperCase());
            
            Map<String, Object> result = new HashMap<>();
            result.put("currency", currency.toUpperCase());
            result.put("supported", supported);
            result.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la vérification: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtenir le taux de change entre deux devises
     */
    @GetMapping("/rate")
    public ResponseEntity<Map<String, Object>> getExchangeRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        
        try {
            BigDecimal rate = exchangeRateService.getExchangeRate(fromCurrency.toUpperCase(), toCurrency.toUpperCase());
            
            Map<String, Object> result = new HashMap<>();
            result.put("fromCurrency", fromCurrency.toUpperCase());
            result.put("toCurrency", toCurrency.toUpperCase());
            result.put("rate", rate);
            result.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération du taux: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Rafraîchir le cache des taux de change
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshExchangeRates() {
        try {
            exchangeRateService.refreshExchangeRates();
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Cache des taux de change rafraîchi avec succès");
            result.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors du rafraîchissement: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conversion en lot (batch)
     */
    @PostMapping("/convert-batch")
    public ResponseEntity<Map<String, Object>> convertBatch(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> conversions = (Map<String, Object>) request.get("conversions");
            String fromCurrency = (String) request.get("fromCurrency");
            String toCurrency = (String) request.get("toCurrency");
            
            Map<String, Object> results = new HashMap<>();
            
            for (Map.Entry<String, Object> entry : conversions.entrySet()) {
                String key = entry.getKey();
                BigDecimal amount = new BigDecimal(entry.getValue().toString());
                BigDecimal converted = exchangeRateService.convertCurrency(amount, fromCurrency.toUpperCase(), toCurrency.toUpperCase());
                results.put(key, converted);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("fromCurrency", fromCurrency.toUpperCase());
            response.put("toCurrency", toCurrency.toUpperCase());
            response.put("conversions", results);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la conversion en lot: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Informations sur l'API de taux de change
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("api", "API Taux de Change E-COMPTA-IA");
        info.put("version", "1.0");
        info.put("description", "Service de conversion de devises pour les prix localisés");
        info.put("source", "exchangerate-api.com");
        info.put("fallback", "Taux fixes en cas d'indisponibilité de l'API");
        
        Map<String, String> features = new HashMap<>();
        features.put("conversion", "Conversion de montants entre devises");
        features.put("subscription", "Conversion de prix d'abonnement");
        features.put("batch", "Conversion en lot");
        features.put("cache", "Mise en cache des taux de change");
        features.put("fallback", "Taux de secours en cas d'erreur");
        info.put("features", features);
        
        Map<String, String> supported = new HashMap<>();
        supported.put("major", "USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY, SEK, NZD");
        supported.put("ohada", "XOF, XAF, CDF, GMD, GHS, NGN, SLL, TND, MAD, EGP");
        supported.put("others", "INR, BRL, MXN, KRW, SGD, HKD, NOK, DKK, PLN, CZK");
        info.put("supportedCurrencies", supported);
        
        return ResponseEntity.ok(info);
    }
}




