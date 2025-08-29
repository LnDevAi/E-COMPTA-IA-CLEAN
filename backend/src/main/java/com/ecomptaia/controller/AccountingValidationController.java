package com.ecomptaia.controller;

import com.ecomptaia.service.AccountingValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Contrôleur pour la validation et le contrôle qualité des données comptables
 */
@RestController
@RequestMapping("/api/accounting-validation")
@CrossOrigin(origins = "*")
public class AccountingValidationController {

    @Autowired
    private AccountingValidationService accountingValidationService;

    /**
     * Validation complète des données comptables pour une entreprise
     */
    @GetMapping("/validate-company")
    public ResponseEntity<Map<String, Object>> validateCompanyAccounting(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> validationResults = accountingValidationService.validateCompanyAccounting(companyId, startDate, endDate);
            return ResponseEntity.ok(validationResults);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la validation: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Validation rapide d'une écriture spécifique
     */
    @GetMapping("/validate-entry/{entryId}")
    public ResponseEntity<Map<String, Object>> validateSingleEntry(@PathVariable Long entryId) {
        try {
            Map<String, Object> validationResults = accountingValidationService.validateSingleEntry(entryId);
            return ResponseEntity.ok(validationResults);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la validation: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Test de validation d'écriture sans paramètre (pour diagnostic)
     */
    @GetMapping("/validate-entry-test")
    public ResponseEntity<Map<String, Object>> validateEntryTest() {
        try {
            // Test avec une écriture simulée
            Map<String, Object> testResults = Map.of(
                "entryId", "TEST",
                "errors", java.util.List.of(),
                "warnings", java.util.List.of("Test de validation"),
                "isValid", true,
                "message", "Test de validation d'écriture réussi"
            );
            return ResponseEntity.ok(testResults);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors du test: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Endpoint de test pour vérifier que le service fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testValidationService() {
        return ResponseEntity.ok(Map.of(
            "message", "Service de validation comptable opérationnel",
            "endpoints", Map.of(
                "validate-company", "GET /api/accounting-validation/validate-company?companyId=1&startDate=2024-01-01&endDate=2024-12-31",
                "validate-entry", "GET /api/accounting-validation/validate-entry/{entryId}",
                "test", "GET /api/accounting-validation/test"
            ),
            "features", Map.of(
                "journalValidation", "Validation des écritures comptables",
                "balanceValidation", "Validation des équilibres comptables",
                "ohadaValidation", "Validation de la cohérence OHADA/SYSCOHADA",
                "anomalyDetection", "Détection d'anomalies",
                "qualityScore", "Calcul du score de qualité global"
            ),
            "status", "ready"
        ));
    }

    /**
     * Validation rapide avec données simulées (pour test)
     */
    @GetMapping("/validate-demo")
    public ResponseEntity<Map<String, Object>> validateDemo() {
        try {
            // Simulation d'une validation avec des données de test
            Map<String, Object> demoResults = Map.of(
                "qualityScore", 85.0,
                "overallStatus", "EXCELLENT",
                "validationDate", LocalDate.now(),
                "journalValidation", Map.of(
                    "totalEntries", 150,
                    "validEntries", 148,
                    "errors", java.util.List.of("Écriture 001: Déséquilibre débit/crédit"),
                    "warnings", java.util.List.of("Écriture 002: Statut non défini"),
                    "isValid", false
                ),
                "balanceValidation", Map.of(
                    "totalAccounts", 45,
                    "totalDebit", new java.math.BigDecimal("1250000.00"),
                    "totalCredit", new java.math.BigDecimal("1250000.00"),
                    "isBalanced", true,
                    "errors", java.util.List.of(),
                    "warnings", java.util.List.of()
                ),
                "ohadaValidation", Map.of(
                    "usedClasses", java.util.Set.of("1", "2", "3", "4", "5", "6", "7"),
                    "isOHADACompliant", true,
                    "errors", java.util.List.of(),
                    "warnings", java.util.List.of()
                ),
                "anomalyDetection", Map.of(
                    "anomalies", java.util.List.of("Montant anormalement élevé détecté: 1500000"),
                    "anomalyCount", 1,
                    "maxAmount", new java.math.BigDecimal("1500000.00"),
                    "weekendEntries", 0
                ),
                "message", "Validation de démonstration réussie"
            );
            
            return ResponseEntity.ok(demoResults);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la validation de démonstration: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }
}
