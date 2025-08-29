package com.ecomptaia.controller;

import com.ecomptaia.service.TestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test-data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestDataController {

    @Autowired
    private TestDataService testDataService;

    /**
     * Générer des données de test pour les rapports financiers
     * POST /api/test-data/generate-financial-data?companyId=1
     */
    @PostMapping("/generate-financial-data")
    public ResponseEntity<Map<String, Object>> generateFinancialTestData(@RequestParam Long companyId) {
        try {
            testDataService.generateTestFinancialData(companyId);
            
            Map<String, Object> response = Map.of(
                "message", "Données de test financières générées avec succès",
                "companyId", companyId,
                "status", "SUCCESS",
                "timestamp", java.time.LocalDateTime.now()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération des données de test",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Nettoyer les données de test
     * DELETE /api/test-data/clean?companyId=1
     */
    @DeleteMapping("/clean")
    public ResponseEntity<Map<String, Object>> cleanTestData(@RequestParam Long companyId) {
        try {
            testDataService.cleanTestData(companyId);
            
            Map<String, Object> response = Map.of(
                "message", "Données de test nettoyées avec succès",
                "companyId", companyId,
                "status", "SUCCESS",
                "timestamp", java.time.LocalDateTime.now()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors du nettoyage des données de test",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint de test pour vérifier le service
     * GET /api/test-data/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testTestDataService() {
        Map<String, Object> response = Map.of(
            "message", "Service de données de test opérationnel",
            "endpoints", Map.of(
                "generateFinancialData", "POST /api/test-data/generate-financial-data",
                "cleanTestData", "DELETE /api/test-data/clean"
            ),
            "status", "SUCCESS",
            "timestamp", java.time.LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }
}


