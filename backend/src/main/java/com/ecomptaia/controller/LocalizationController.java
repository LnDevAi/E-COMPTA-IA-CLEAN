package com.ecomptaia.controller;

import com.ecomptaia.service.LocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

/**
 * Contrôleur pour la localisation (devises, langues, formats)
 */
@RestController
@RequestMapping("/api/localization")
@CrossOrigin(origins = "*")
public class LocalizationController {

    @Autowired
    private LocalizationService localizationService;

    /**
     * Obtenir la configuration de localisation pour un pays
     */
    @GetMapping("/country/{countryCode}")
    public ResponseEntity<Map<String, Object>> getLocalizationConfig(@PathVariable String countryCode) {
        try {
            Map<String, Object> result = localizationService.getLocalizationConfig(countryCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération de la configuration: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Formater un montant selon la devise du pays
     */
    @GetMapping("/format-amount")
    public ResponseEntity<Map<String, Object>> formatAmount(
            @RequestParam BigDecimal amount,
            @RequestParam String countryCode) {
        try {
            String formattedAmount = localizationService.formatAmount(amount, countryCode);
            return ResponseEntity.ok(Map.of(
                "amount", amount,
                "countryCode", countryCode,
                "formattedAmount", formattedAmount,
                "currency", localizationService.getCurrency(countryCode)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors du formatage: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Formater une date selon le format du pays
     */
    @GetMapping("/format-date")
    public ResponseEntity<Map<String, Object>> formatDate(
            @RequestParam String date,
            @RequestParam String countryCode) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            String formattedDate = localizationService.formatDate(localDate, countryCode);
            return ResponseEntity.ok(Map.of(
                "date", date,
                "countryCode", countryCode,
                "formattedDate", formattedDate
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors du formatage de la date: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Convertir un montant d'une devise à une autre
     */
    @GetMapping("/convert-currency")
    public ResponseEntity<Map<String, Object>> convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        try {
            BigDecimal convertedAmount = localizationService.convertCurrency(amount, fromCurrency, toCurrency);
            return ResponseEntity.ok(Map.of(
                "originalAmount", amount,
                "fromCurrency", fromCurrency,
                "toCurrency", toCurrency,
                "convertedAmount", convertedAmount,
                "exchangeRate", convertedAmount.divide(amount, 4, RoundingMode.HALF_UP)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la conversion: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir la langue du pays
     */
    @GetMapping("/language/{countryCode}")
    public ResponseEntity<Map<String, Object>> getLanguage(@PathVariable String countryCode) {
        try {
            String language = localizationService.getLanguage(countryCode);
            return ResponseEntity.ok(Map.of(
                "countryCode", countryCode,
                "language", language
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération de la langue: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir la devise du pays
     */
    @GetMapping("/currency/{countryCode}")
    public ResponseEntity<Map<String, Object>> getCurrency(@PathVariable String countryCode) {
        try {
            String currency = localizationService.getCurrency(countryCode);
            return ResponseEntity.ok(Map.of(
                "countryCode", countryCode,
                "currency", currency
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération de la devise: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir le fuseau horaire du pays
     */
    @GetMapping("/timezone/{countryCode}")
    public ResponseEntity<Map<String, Object>> getTimezone(@PathVariable String countryCode) {
        try {
            String timezone = localizationService.getTimezone(countryCode);
            return ResponseEntity.ok(Map.of(
                "countryCode", countryCode,
                "timezone", timezone
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération du fuseau horaire: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir tous les pays supportés avec leurs configurations
     */
    @GetMapping("/supported-countries")
    public ResponseEntity<Map<String, Object>> getAllSupportedCountries() {
        try {
            Map<String, Object> result = localizationService.getAllSupportedCountries();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération des pays: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir les devises supportées
     */
    @GetMapping("/supported-currencies")
    public ResponseEntity<Map<String, Object>> getSupportedCurrencies() {
        try {
            Map<String, Object> result = localizationService.getSupportedCurrencies();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération des devises: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir les langues supportées
     */
    @GetMapping("/supported-languages")
    public ResponseEntity<Map<String, Object>> getSupportedLanguages() {
        try {
            Map<String, Object> result = localizationService.getSupportedLanguages();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération des langues: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Endpoint de test pour vérifier que le service fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testLocalizationService() {
                    Map<String, Object> response = new HashMap<>();
            response.put("message", "Service de localisation opérationnel");
            
            Map<String, String> endpoints = new HashMap<>();
            endpoints.put("country", "GET /api/localization/country/{countryCode}");
            endpoints.put("format-amount", "GET /api/localization/format-amount?amount=1000000&countryCode=BF");
            endpoints.put("format-date", "GET /api/localization/format-date?date=2024-12-31&countryCode=BF");
            endpoints.put("convert-currency", "GET /api/localization/convert-currency?amount=1000000&fromCurrency=XOF&toCurrency=EUR");
            endpoints.put("language", "GET /api/localization/language/{countryCode}");
            endpoints.put("currency", "GET /api/localization/currency/{countryCode}");
            endpoints.put("timezone", "GET /api/localization/timezone/{countryCode}");
            endpoints.put("supported-countries", "GET /api/localization/supported-countries");
            endpoints.put("supported-currencies", "GET /api/localization/supported-currencies");
            endpoints.put("supported-languages", "GET /api/localization/supported-languages");
            endpoints.put("test", "GET /api/localization/test");
            
            Map<String, String> features = new HashMap<>();
            features.put("localizationConfig", "Configuration complète par pays");
            features.put("amountFormatting", "Formatage des montants selon la devise locale");
            features.put("dateFormatting", "Formatage des dates selon le format local");
            features.put("currencyConversion", "Conversion entre devises");
            features.put("languageDetection", "Détection automatique de la langue");
            features.put("currencyDetection", "Détection automatique de la devise");
            features.put("timezoneDetection", "Détection automatique du fuseau horaire");
            
            response.put("endpoints", endpoints);
            response.put("features", features);
            response.put("status", "ready");
            
            return ResponseEntity.ok(response);
    }

    /**
     * Démonstration avec des exemples concrets
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> getDemo() {
        try {
            Map<String, Object> demo = new HashMap<>();
            Map<String, Object> examples = new HashMap<>();
            
            Map<String, Object> burkinaFaso = new HashMap<>();
            burkinaFaso.put("countryCode", "BF");
            burkinaFaso.put("currency", "XOF (Franc CFA)");
            burkinaFaso.put("language", "fr (Français)");
            burkinaFaso.put("dateFormat", "dd/MM/yyyy");
            burkinaFaso.put("timezone", "Africa/Ouagadougou");
            burkinaFaso.put("formattedAmount", "1 000 000,00 XOF (Franc CFA)");
            burkinaFaso.put("formattedDate", "31/12/2024");
            
            Map<String, Object> france = new HashMap<>();
            france.put("countryCode", "FR");
            france.put("currency", "EUR (Euro)");
            france.put("language", "fr (Français)");
            france.put("dateFormat", "dd/MM/yyyy");
            france.put("timezone", "Europe/Paris");
            france.put("formattedAmount", "1 000 000,00 EUR (Euro)");
            france.put("formattedDate", "31/12/2024");
            
            Map<String, Object> unitedStates = new HashMap<>();
            unitedStates.put("countryCode", "US");
            unitedStates.put("currency", "USD (US Dollar)");
            unitedStates.put("language", "en (English)");
            unitedStates.put("dateFormat", "MM/dd/yyyy");
            unitedStates.put("timezone", "America/New_York");
            unitedStates.put("formattedAmount", "1,000,000.00 USD (US Dollar)");
            unitedStates.put("formattedDate", "12/31/2024");
            
            examples.put("burkinaFaso", burkinaFaso);
            examples.put("france", france);
            examples.put("unitedStates", unitedStates);
            
            demo.put("examples", examples);
            demo.put("message", "Démonstration de la localisation complète");
            demo.put("description", "La plateforme s'adapte automatiquement à la devise, langue et format du pays");
            
            return ResponseEntity.ok(demo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la génération de la démonstration: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }
}
