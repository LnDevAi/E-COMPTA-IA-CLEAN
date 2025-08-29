package com.ecomptaia.controller;

import com.ecomptaia.entity.Tax;
import com.ecomptaia.service.TaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tax")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaxController {

    @Autowired
    private TaxService taxService;

    // === CALCUL DES TAXES ===
    
    @GetMapping("/calculate")
    public ResponseEntity<?> calculateTax(
            @RequestParam BigDecimal amount,
            @RequestParam String countryCode,
            @RequestParam String taxType) {
        try {
            BigDecimal taxAmount = taxService.calculateTaxAmount(amount, countryCode, taxType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("amount", amount);
            response.put("countryCode", countryCode);
            response.put("taxType", taxType);
            response.put("taxAmount", taxAmount);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/calculate-all")
    public ResponseEntity<?> calculateAllTaxes(
            @RequestParam BigDecimal amount,
            @RequestParam String countryCode,
            @RequestParam(defaultValue = "EUR") String targetCurrency) {
        try {
            Map<String, Object> result = taxService.calculateAllTaxes(amount, countryCode, targetCurrency);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === GESTION DES TAXES ===
    
    @GetMapping("/countries")
    public ResponseEntity<?> getAllCountriesWithTaxes() {
        List<String> countries = taxService.getAllCountriesWithTaxes();
        Map<String, Object> response = new HashMap<>();
        response.put("countries", countries);
        response.put("total", countries.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/countries/{countryCode}")
    public ResponseEntity<?> getTaxesByCountry(@PathVariable String countryCode) {
        List<Tax> taxes = taxService.getTaxesByCountry(countryCode);
        Map<String, Object> response = new HashMap<>();
        response.put("countryCode", countryCode);
        response.put("taxes", taxes);
        response.put("total", taxes.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/countries/{countryCode}/mandatory")
    public ResponseEntity<?> getMandatoryTaxesByCountry(@PathVariable String countryCode) {
        List<Tax> taxes = taxService.getMandatoryTaxesByCountry(countryCode);
        Map<String, Object> response = new HashMap<>();
        response.put("countryCode", countryCode);
        response.put("mandatoryTaxes", taxes);
        response.put("total", taxes.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/countries/{countryCode}/types")
    public ResponseEntity<?> getTaxTypesByCountry(@PathVariable String countryCode) {
        List<String> taxTypes = taxService.getTaxTypesByCountry(countryCode);
        Map<String, Object> response = new HashMap<>();
        response.put("countryCode", countryCode);
        response.put("taxTypes", taxTypes);
        response.put("total", taxTypes.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/countries/{countryCode}/types/{taxType}")
    public ResponseEntity<?> getTaxesByCountryAndType(
            @PathVariable String countryCode,
            @PathVariable String taxType) {
        List<Tax> taxes = taxService.getTaxesByCountryAndType(countryCode, taxType);
        Map<String, Object> response = new HashMap<>();
        response.put("countryCode", countryCode);
        response.put("taxType", taxType);
        response.put("taxes", taxes);
        response.put("total", taxes.size());
        return ResponseEntity.ok(response);
    }

    // === TYPES DE TAXES ===
    
    @GetMapping("/types")
    public ResponseEntity<?> getAllTaxTypes() {
        List<String> taxTypes = taxService.getAllTaxTypes();
        Map<String, Object> response = new HashMap<>();
        response.put("taxTypes", taxTypes);
        response.put("total", taxTypes.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types/{taxType}")
    public ResponseEntity<?> getTaxesByType(@PathVariable String taxType) {
        List<Tax> taxes = taxService.getTaxesByType(taxType);
        Map<String, Object> response = new HashMap<>();
        response.put("taxType", taxType);
        response.put("taxes", taxes);
        response.put("total", taxes.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types/{taxType}/top")
    public ResponseEntity<?> getTopTaxRates(
            @PathVariable String taxType,
            @RequestParam(defaultValue = "10") int limit) {
        List<Tax> taxes = taxService.getTopTaxRates(taxType, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("taxType", taxType);
        response.put("topTaxes", taxes);
        response.put("limit", limit);
        response.put("total", taxes.size());
        return ResponseEntity.ok(response);
    }

    // === COMPARAISON ===
    
    @PostMapping("/compare")
    public ResponseEntity<?> compareTaxRates(
            @RequestParam String taxType,
            @RequestBody List<String> countryCodes) {
        try {
            Map<String, Object> comparison = taxService.compareTaxRates(taxType, countryCodes);
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === GESTION CRUD ===
    
    @PostMapping("/taxes")
    public ResponseEntity<?> addTax(@RequestBody Map<String, Object> request) {
        try {
            String countryCode = (String) request.get("countryCode");
            String countryName = (String) request.get("countryName");
            String taxType = (String) request.get("taxType");
            String taxName = (String) request.get("taxName");
            BigDecimal rate = new BigDecimal(request.get("rate").toString());
            String currency = (String) request.get("currency");
            String description = (String) request.get("description");
            String frequency = (String) request.get("frequency");

            Tax tax = taxService.addTax(countryCode, countryName, taxType, taxName, rate, currency, description, frequency);
            return ResponseEntity.ok(tax);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/taxes/{id}")
    public ResponseEntity<?> updateTax(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal newRate = new BigDecimal(request.get("rate").toString());
            String description = (String) request.get("description");

            Tax tax = taxService.updateTax(id, newRate, description);
            return ResponseEntity.ok(tax);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === ENDPOINTS DE TEST ===
    
    @GetMapping("/test")
    public ResponseEntity<?> testTaxSystem() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Système de taxes opérationnel");
        response.put("availableEndpoints", List.of(
            "GET /api/tax/calculate",
            "GET /api/tax/calculate-all",
            "GET /api/tax/countries",
            "GET /api/tax/types",
            "POST /api/tax/compare"
        ));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}





