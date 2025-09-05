package com.ecomptaia.controller;

import com.ecomptaia.service.InternationalAccountingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour l'internationalisation des systèmes comptables
 */
@RestController
@RequestMapping("/api/international-accounting")
@CrossOrigin(origins = "*")
public class InternationalAccountingController {

    @Autowired
    private InternationalAccountingService internationalAccountingService;

    /**
     * Obtenir le système comptable pour un pays
     */
    @GetMapping("/country/{countryCode}")
    public ResponseEntity<Map<String, Object>> getAccountingSystemForCountry(@PathVariable String countryCode) {
        try {
            Map<String, Object> result = internationalAccountingService.getAccountingSystemForCountry(countryCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération du système comptable: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir tous les pays supportés
     */
    @GetMapping("/supported-countries")
    public ResponseEntity<Map<String, Object>> getSupportedCountries() {
        try {
            Map<String, Object> result = internationalAccountingService.getSupportedCountries();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération des pays supportés: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir la configuration d'un système comptable
     */
    @GetMapping("/system/{accountingSystem}")
    public ResponseEntity<Map<String, Object>> getAccountingSystemConfig(@PathVariable String accountingSystem) {
        try {
            Map<String, Object> result = internationalAccountingService.getAccountingSystemConfig(accountingSystem);
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
     * Valider la compatibilité d'un pays avec un système comptable
     */
    @GetMapping("/validate-compatibility")
    public ResponseEntity<Map<String, Object>> validateCountryCompatibility(
            @RequestParam String countryCode,
            @RequestParam String accountingSystem) {
        try {
            Map<String, Object> result = internationalAccountingService.validateCountryCompatibility(countryCode, accountingSystem);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la validation: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir les règles de validation pour un système comptable
     */
    @GetMapping("/validation-rules/{accountingSystem}")
    public ResponseEntity<Map<String, Object>> getValidationRules(@PathVariable String accountingSystem) {
        try {
            Map<String, Object> result = internationalAccountingService.getValidationRules(accountingSystem);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération des règles: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Obtenir les fonctionnalités disponibles pour un système comptable
     */
    @GetMapping("/features/{accountingSystem}")
    public ResponseEntity<Map<String, Object>> getAvailableFeatures(@PathVariable String accountingSystem) {
        try {
            Map<String, Object> result = internationalAccountingService.getAvailableFeatures(accountingSystem);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération des fonctionnalités: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Endpoint de test pour vérifier que le service fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testInternationalAccountingService() {
        return ResponseEntity.ok(Map.of(
            "message", "Service d'internationalisation comptable opérationnel",
            "endpoints", Map.of(
                "country", "GET /api/international-accounting/country/{countryCode}",
                "supported-countries", "GET /api/international-accounting/supported-countries",
                "system", "GET /api/international-accounting/system/{accountingSystem}",
                "validate-compatibility", "GET /api/international-accounting/validate-compatibility?countryCode=BF&accountingSystem=SYSCOHADA",
                "validation-rules", "GET /api/international-accounting/validation-rules/{accountingSystem}",
                "features", "GET /api/international-accounting/features/{accountingSystem}",
                "test", "GET /api/international-accounting/test"
            ),
            "features", Map.of(
                "countryMapping", "Mapping automatique pays → système comptable",
                "systemConfig", "Configuration détaillée des systèmes comptables",
                "compatibilityValidation", "Validation de compatibilité pays/système",
                "validationRules", "Règles de validation par système",
                "availableFeatures", "Fonctionnalités disponibles par système"
            ),
            "supportedSystems", java.util.List.of("SYSCOHADA", "PCG", "IFRS", "US_GAAP", "HGB", "UK_GAAP", "ASPE", "BR_GAAP", "IND_AS", "CAS", "JGAAP", "AASB"),
            "status", "ready"
        ));
    }

    /**
     * Démonstration avec des exemples concrets
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> getDemo() {
        try {
            Map<String, Object> demo = Map.of(
                "examples", Map.of(
                    "burkinaFaso", Map.of(
                        "countryCode", "BF",
                        "accountingSystem", "SYSCOHADA",
                        "currency", "XOF",
                        "language", "fr",
                        "features", java.util.List.of("balance_sheet", "income_statement", "cash_flow", "annexes")
                    ),
                    "france", Map.of(
                        "countryCode", "FR",
                        "accountingSystem", "PCG",
                        "currency", "EUR",
                        "language", "fr",
                        "features", java.util.List.of("balance_sheet", "income_statement", "cash_flow")
                    ),
                    "unitedStates", Map.of(
                        "countryCode", "US",
                        "accountingSystem", "US_GAAP",
                        "currency", "USD",
                        "language", "en",
                        "features", java.util.List.of("balance_sheet", "income_statement", "cash_flow")
                    )
                ),
                "message", "Démonstration de l'internationalisation comptable",
                "description", "La plateforme s'adapte automatiquement au système comptable du pays sélectionné"
            );
            
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








