package com.ecomptaia.controller;

import com.ecomptaia.entity.CountryConfig;
import com.ecomptaia.entity.ExpansionAnalysis;
import com.ecomptaia.service.InternationalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/international")
@CrossOrigin(origins = "*")
public class InternationalController {

    @Autowired
    private InternationalService internationalService;

    // =====================================================
    // GESTION DES PAYS
    // =====================================================

    /**
     * GET /api/international/countries - Liste des pays supportés
     */
    @GetMapping("/countries")
    public ResponseEntity<Map<String, Object>> getSupportedCountries() {
        try {
            List<String> countries = internationalService.getSupportedCountries();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "message", "Pays supportés récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays supportés",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/countries/{code} - Configuration d'un pays
     */
    @GetMapping("/countries/{code}")
    public ResponseEntity<Map<String, Object>> getCountryConfig(@PathVariable String code) {
        try {
            CountryConfig config = internationalService.getCountryConfig(code.toUpperCase());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", config,
                "message", "Configuration pays récupérée avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Pays non supporté ou erreur de récupération",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/countries/ohada - Pays OHADA
     */
    @GetMapping("/countries/ohada")
    public ResponseEntity<Map<String, Object>> getOHADACountries() {
        try {
            List<CountryConfig> countries = internationalService.getOHADACountries();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "message", "Pays OHADA récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays OHADA",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/countries/developed - Pays développés
     */
    @GetMapping("/countries/developed")
    public ResponseEntity<Map<String, Object>> getDevelopedCountries() {
        try {
            List<CountryConfig> countries = internationalService.getDevelopedCountries();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "message", "Pays développés récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays développés",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/countries/standard/{standard} - Pays par standard comptable
     */
    @GetMapping("/countries/standard/{standard}")
    public ResponseEntity<Map<String, Object>> getCountriesByAccountingStandard(@PathVariable String standard) {
        try {
            List<CountryConfig> countries = internationalService.getCountriesByAccountingStandard(standard.toUpperCase());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "standard", standard,
                "message", "Pays par standard comptable récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays par standard",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/countries/system/{systemType} - Pays par type de système
     */
    @GetMapping("/countries/system/{systemType}")
    public ResponseEntity<Map<String, Object>> getCountriesBySystemType(@PathVariable String systemType) {
        try {
            List<CountryConfig> countries = internationalService.getCountriesBySystemType(systemType.toUpperCase());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "systemType", systemType,
                "message", "Pays par type de système récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays par type de système",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/countries/continent/{continent} - Pays par continent
     */
    @GetMapping("/countries/continent/{continent}")
    public ResponseEntity<Map<String, Object>> getCountriesByContinent(@PathVariable String continent) {
        try {
            List<CountryConfig> countries = internationalService.getCountriesByContinent(continent.toUpperCase());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "continent", continent,
                "message", "Pays par continent récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays par continent",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/countries/apis/complete - Pays avec APIs complètes
     */
    @GetMapping("/countries/apis/complete")
    public ResponseEntity<Map<String, Object>> getCountriesWithCompleteApis() {
        try {
            List<CountryConfig> countries = internationalService.getCountriesWithCompleteApis();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "message", "Pays avec APIs complètes récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays avec APIs complètes",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/countries/payment-systems - Pays avec systèmes de paiement
     */
    @GetMapping("/countries/payment-systems")
    public ResponseEntity<Map<String, Object>> getCountriesWithPaymentSystems() {
        try {
            List<CountryConfig> countries = internationalService.getCountriesWithPaymentSystems();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "message", "Pays avec systèmes de paiement récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays avec systèmes de paiement",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =====================================================
    // ANALYSE D'EXPANSION
    // =====================================================

    /**
     * POST /api/international/expansion/analyze/{countryCode} - Analyser le potentiel d'expansion
     */
    @PostMapping("/expansion/analyze/{countryCode}")
    public ResponseEntity<Map<String, Object>> analyzeExpansionPotential(@PathVariable String countryCode) {
        try {
            ExpansionAnalysis analysis = internationalService.analyzeExpansionPotential(countryCode.toUpperCase());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", analysis,
                "message", "Analyse d'expansion générée avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de l'analyse d'expansion",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/expansion/priorities - Priorités d'expansion
     */
    @GetMapping("/expansion/priorities")
    public ResponseEntity<Map<String, Object>> getExpansionPriorities(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ExpansionAnalysis> priorities = internationalService.getExpansionPriorities(limit);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", priorities,
                "count", priorities.size(),
                "limit", limit,
                "message", "Priorités d'expansion récupérées avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des priorités d'expansion",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/expansion/recommended - Pays recommandés
     */
    @GetMapping("/expansion/recommended")
    public ResponseEntity<Map<String, Object>> getRecommendedCountries() {
        try {
            List<ExpansionAnalysis> countries = internationalService.getRecommendedCountries();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "message", "Pays recommandés récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays recommandés",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/expansion/consider - Pays à considérer
     */
    @GetMapping("/expansion/consider")
    public ResponseEntity<Map<String, Object>> getConsiderCountries() {
        try {
            List<ExpansionAnalysis> countries = internationalService.getConsiderCountries();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "message", "Pays à considérer récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays à considérer",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/expansion/not-recommended - Pays non recommandés
     */
    @GetMapping("/expansion/not-recommended")
    public ResponseEntity<Map<String, Object>> getNotRecommendedCountries() {
        try {
            List<ExpansionAnalysis> countries = internationalService.getNotRecommendedCountries();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", countries,
                "count", countries.size(),
                "message", "Pays non recommandés récupérés avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des pays non recommandés",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =====================================================
    // VALIDATION D'INTÉGRATION
    // =====================================================

    /**
     * POST /api/international/validation/{countryCode} - Valider l'intégration d'un pays
     */
    @PostMapping("/validation/{countryCode}")
    public ResponseEntity<Map<String, Object>> validateCountryIntegration(@PathVariable String countryCode) {
        try {
            Map<String, Object> validation = internationalService.validateCountryIntegration(countryCode.toUpperCase());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", validation,
                "message", "Validation d'intégration effectuée avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la validation d'intégration",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =====================================================
    // STATISTIQUES
    // =====================================================

    /**
     * GET /api/international/statistics/accounting-standards - Statistiques par standard comptable
     */
    @GetMapping("/statistics/accounting-standards")
    public ResponseEntity<Map<String, Object>> getStatisticsByAccountingStandard() {
        try {
            Map<String, Long> stats = internationalService.getStatisticsByAccountingStandard();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", stats,
                "message", "Statistiques par standard comptable récupérées avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des statistiques",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/statistics/currencies - Statistiques par devise
     */
    @GetMapping("/statistics/currencies")
    public ResponseEntity<Map<String, Object>> getStatisticsByCurrency() {
        try {
            Map<String, Long> stats = internationalService.getStatisticsByCurrency();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", stats,
                "message", "Statistiques par devise récupérées avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des statistiques",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/statistics/status - Statistiques par statut
     */
    @GetMapping("/statistics/status")
    public ResponseEntity<Map<String, Object>> getStatisticsByStatus() {
        try {
            Map<String, Long> stats = internationalService.getStatisticsByStatus();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", stats,
                "message", "Statistiques par statut récupérées avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des statistiques",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/statistics/system-types - Statistiques par type de système
     */
    @GetMapping("/statistics/system-types")
    public ResponseEntity<Map<String, Object>> getStatisticsBySystemType() {
        try {
            Map<String, Long> stats = internationalService.getStatisticsBySystemType();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", stats,
                "message", "Statistiques par type de système récupérées avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des statistiques",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =====================================================
    // GESTION DES CONFIGURATIONS
    // =====================================================

    /**
     * POST /api/international/countries - Créer une configuration pays
     */
    @PostMapping("/countries")
    public ResponseEntity<Map<String, Object>> createCountryConfig(@RequestBody CountryConfig config) {
        try {
            CountryConfig savedConfig = internationalService.saveCountryConfig(config);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", savedConfig,
                "message", "Configuration pays créée avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la création de la configuration pays",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * PUT /api/international/countries/{id} - Mettre à jour une configuration pays
     */
    @PutMapping("/countries/{id}")
    public ResponseEntity<Map<String, Object>> updateCountryConfig(@PathVariable Long id, @RequestBody CountryConfig config) {
        try {
            config.setId(id);
            CountryConfig updatedConfig = internationalService.saveCountryConfig(config);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", updatedConfig,
                "message", "Configuration pays mise à jour avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la mise à jour de la configuration pays",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/international/countries/{code}/activate - Activer un pays
     */
    @PostMapping("/countries/{code}/activate")
    public ResponseEntity<Map<String, Object>> activateCountry(@PathVariable String code) {
        try {
            internationalService.activateCountry(code.toUpperCase());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Pays activé avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de l'activation du pays",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/international/countries/{code}/deactivate - Désactiver un pays
     */
    @PostMapping("/countries/{code}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateCountry(@PathVariable String code) {
        try {
            internationalService.deactivateCountry(code.toUpperCase());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Pays désactivé avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la désactivation du pays",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =====================================================
    // ENDPOINTS DE TEST
    // =====================================================

    /**
     * GET /api/international/test/complete - Test complet du système international
     */
    @GetMapping("/test/complete")
    public ResponseEntity<Map<String, Object>> testCompleteSystem() {
        try {
            Map<String, Object> testResults = Map.of(
                "supportedCountries", internationalService.getSupportedCountries().size(),
                "ohadaCountries", internationalService.getOHADACountries().size(),
                "developedCountries", internationalService.getDevelopedCountries().size(),
                "countriesWithApis", internationalService.getCountriesWithCompleteApis().size(),
                "countriesWithPaymentSystems", internationalService.getCountriesWithPaymentSystems().size(),
                "expansionPriorities", internationalService.getExpansionPriorities(5).size(),
                "recommendedCountries", internationalService.getRecommendedCountries().size(),
                "systemStatus", "OPERATIONAL",
                "message", "Système international opérationnel"
            );
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", testResults,
                "message", "Test complet effectué avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors du test complet",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/international/info - Informations sur le système international
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        try {
            Map<String, Object> info = Map.of(
                "systemName", "E-COMPTA-IA International",
                "version", "1.0.0",
                "description", "Système de gestion comptable internationale avec support multi-pays",
                "features", List.of(
                    "Configuration pays dynamique",
                    "Standards comptables multiples (SYSCOHADA, IFRS, US GAAP, PCG)",
                    "APIs gouvernementales intégrées",
                    "Systèmes de paiement locaux",
                    "Analyse d'expansion géographique",
                    "Validation d'intégration automatique"
                ),
                "supportedStandards", List.of("SYSCOHADA", "IFRS", "US_GAAP", "PCG", "HGB"),
                "supportedSystems", List.of("NORMAL", "MINIMAL", "BOTH"),
                "message", "Système international prêt pour conquête mondiale"
            );
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", info,
                "message", "Informations système récupérées avec succès"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "success", false,
                "error", "Erreur lors de la récupération des informations",
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }
}







