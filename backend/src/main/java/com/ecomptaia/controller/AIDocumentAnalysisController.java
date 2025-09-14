package com.ecomptaia.controller;

import com.ecomptaia.service.AIDocumentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Contrôleur pour l'analyse IA des documents comptables
 */
@RestController
@RequestMapping("/api/ai-document-analysis")
@CrossOrigin(origins = {"http://localhost:4200", "https://ecomptaia.com"}, allowCredentials = "true")
public class AIDocumentAnalysisController {

    @Autowired
    private AIDocumentAnalysisService aiDocumentAnalysisService;

    /**
     * Analyser un document et extraire les informations comptables
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeDocument(
            @RequestParam String documentContent,
            @RequestParam String documentType,
            @RequestParam String countryCode) {
        try {
            Map<String, Object> result = aiDocumentAnalysisService.analyzeDocument(documentContent, documentType, countryCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'analyse du document: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Détecter les anomalies et fraudes potentielles
     */
    @PostMapping("/detect-anomalies")
    public ResponseEntity<Map<String, Object>> detectAnomalies(
            @RequestBody Map<String, Object> extractedData,
            @RequestParam String countryCode) {
        try {
            Map<String, Object> result = aiDocumentAnalysisService.detectAnomalies(extractedData, countryCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la détection d'anomalies: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Détecter les anomalies avec des données simulées (pour test)
     */
    @GetMapping("/detect-anomalies-test")
    public ResponseEntity<Map<String, Object>> detectAnomaliesTest(
            @RequestParam(defaultValue = "FR") String countryCode) {
        try {
            // Données simulées pour le test
            Map<String, Object> extractedData = new HashMap<>();
            extractedData.put("numero", "FAC-TEST-001");
            extractedData.put("date", "2024-12-31");
            extractedData.put("fournisseur", "Fournisseur Test");
            extractedData.put("montant_ht", new java.math.BigDecimal("75000.00"));
            extractedData.put("tva", new java.math.BigDecimal("15000.00"));
            extractedData.put("montant_ttc", new java.math.BigDecimal("90000.00"));
            extractedData.put("devise", "EUR");
            
            Map<String, Object> result = aiDocumentAnalysisService.detectAnomalies(extractedData, countryCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la détection d'anomalies: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Automatiser la création d'écritures comptables
     */
    @PostMapping("/automate-journal-entry")
    public ResponseEntity<Map<String, Object>> automateJournalEntry(
            @RequestBody Map<String, Object> extractedData,
            @RequestParam String countryCode) {
        try {
            Map<String, Object> result = aiDocumentAnalysisService.automateJournalEntry(extractedData, countryCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'automatisation: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Automatiser la création d'écritures comptables avec des données simulées (pour test)
     */
    @GetMapping("/automate-journal-entry-test")
    public ResponseEntity<Map<String, Object>> automateJournalEntryTest(
            @RequestParam(defaultValue = "FR") String countryCode) {
        try {
            // Données simulées pour le test
            Map<String, Object> extractedData = new HashMap<>();
            extractedData.put("numero", "FAC-AUTO-001");
            extractedData.put("date", "2024-12-31");
            extractedData.put("fournisseur", "Fournisseur Auto");
            extractedData.put("montant_ht", new java.math.BigDecimal("1000.00"));
            extractedData.put("tva", new java.math.BigDecimal("200.00"));
            extractedData.put("montant_ttc", new java.math.BigDecimal("1200.00"));
            extractedData.put("devise", "EUR");
            
            Map<String, Object> result = aiDocumentAnalysisService.automateJournalEntry(extractedData, countryCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'automatisation: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Endpoint de test pour vérifier que le service fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testAIDocumentAnalysisService() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service d'analyse IA des documents opérationnel");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("analyze", "POST /api/ai-document-analysis/analyze");
        endpoints.put("detect-anomalies", "POST /api/ai-document-analysis/detect-anomalies");
        endpoints.put("detect-anomalies-test", "GET /api/ai-document-analysis/detect-anomalies-test");
        endpoints.put("automate-journal-entry", "POST /api/ai-document-analysis/automate-journal-entry");
        endpoints.put("automate-journal-entry-test", "GET /api/ai-document-analysis/automate-journal-entry-test");
        endpoints.put("test", "GET /api/ai-document-analysis/test");
        endpoints.put("demo", "GET /api/ai-document-analysis/demo");
        
        Map<String, String> features = new HashMap<>();
        features.put("documentAnalysis", "Analyse intelligente des documents comptables");
        features.put("anomalyDetection", "Détection automatique d'anomalies et fraudes");
        features.put("journalEntryAutomation", "Automatisation des écritures comptables");
        features.put("accountingSuggestions", "Suggestions comptables intelligentes");
        features.put("fraudDetection", "Détection de fraude et validation");
        
        response.put("endpoints", endpoints);
        response.put("features", features);
        response.put("supportedDocumentTypes", java.util.List.of("FACTURE", "RECU", "BON_LIVRAISON", "DEVIS", "BORDEREAU", "TICKET_CAISSE"));
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
            
            // Exemple d'analyse de facture
            Map<String, Object> factureExample = new HashMap<>();
            factureExample.put("documentType", "FACTURE");
            factureExample.put("countryCode", "FR");
            factureExample.put("extractedData", Map.of(
                "numero", "FAC-2024-001",
                "date", "2024-12-31",
                "fournisseur", "Fournisseur Demo",
                "montant_ht", "1000.00",
                "tva", "200.00",
                "montant_ttc", "1200.00"
            ));
            factureExample.put("confidenceScore", 0.95);
            factureExample.put("suggestions", java.util.List.of(
                "Compte: 401 - Fournisseurs",
                "TVA: 4456 - TVA déductible",
                "Action: VALIDER"
            ));
            
            // Exemple de détection d'anomalie
            Map<String, Object> anomalyExample = new HashMap<>();
            anomalyExample.put("type", "AMOUNT_ANOMALY");
            anomalyExample.put("severity", "HIGH");
            anomalyExample.put("description", "Montant très élevé détecté");
            anomalyExample.put("value", "75000.00");
            anomalyExample.put("riskLevel", "HIGH");
            
            examples.put("factureAnalysis", factureExample);
            examples.put("anomalyDetection", anomalyExample);
            
            demo.put("examples", examples);
            demo.put("message", "Démonstration de l'analyse IA des documents");
            demo.put("description", "L'IA analyse automatiquement les documents et génère des suggestions comptables");
            
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
