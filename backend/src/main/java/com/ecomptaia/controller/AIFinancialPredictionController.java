package com.ecomptaia.controller;

import com.ecomptaia.service.AIFinancialPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Contrôleur pour les prédictions financières IA
 */
@RestController
@RequestMapping("/api/ai-financial-prediction")
@CrossOrigin(origins = "*")
public class AIFinancialPredictionController {

    @Autowired
    private AIFinancialPredictionService aiFinancialPredictionService;

    /**
     * Prédire les flux de trésorerie futurs
     */
    @GetMapping("/predict-cash-flow")
    public ResponseEntity<Map<String, Object>> predictCashFlow(
            @RequestParam Long companyId,
            @RequestParam(defaultValue = "12") int monthsAhead) {
        try {
            Map<String, Object> result = aiFinancialPredictionService.predictCashFlow(companyId, monthsAhead);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la prédiction des flux de trésorerie: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Prédire la rentabilité future
     */
    @GetMapping("/predict-profitability")
    public ResponseEntity<Map<String, Object>> predictProfitability(
            @RequestParam Long companyId,
            @RequestParam(defaultValue = "12") int monthsAhead) {
        try {
            Map<String, Object> result = aiFinancialPredictionService.predictProfitability(companyId, monthsAhead);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la prédiction de rentabilité: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Analyser les tendances et patterns
     */
    @GetMapping("/analyze-trends")
    public ResponseEntity<Map<String, Object>> analyzeTrends(
            @RequestParam Long companyId,
            @RequestParam String analysisType) {
        try {
            Map<String, Object> result = aiFinancialPredictionService.analyzeTrends(companyId, analysisType);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'analyse des tendances: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Générer des recommandations financières
     */
    @GetMapping("/generate-recommendations")
    public ResponseEntity<Map<String, Object>> generateFinancialRecommendations(
            @RequestParam Long companyId) {
        try {
            Map<String, Object> result = aiFinancialPredictionService.generateFinancialRecommendations(companyId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la génération des recommandations: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Prédire les risques financiers
     */
    @GetMapping("/predict-risks")
    public ResponseEntity<Map<String, Object>> predictFinancialRisks(
            @RequestParam Long companyId) {
        try {
            Map<String, Object> result = aiFinancialPredictionService.predictFinancialRisks(companyId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la prédiction des risques: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Endpoint de test pour vérifier que le service fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testAIFinancialPredictionService() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service de prédictions financières IA opérationnel");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("predict-cash-flow", "GET /api/ai-financial-prediction/predict-cash-flow?companyId=1&monthsAhead=12");
        endpoints.put("predict-profitability", "GET /api/ai-financial-prediction/predict-profitability?companyId=1&monthsAhead=12");
        endpoints.put("analyze-trends", "GET /api/ai-financial-prediction/analyze-trends?companyId=1&analysisType=REVENUE");
        endpoints.put("generate-recommendations", "GET /api/ai-financial-prediction/generate-recommendations?companyId=1");
        endpoints.put("predict-risks", "GET /api/ai-financial-prediction/predict-risks?companyId=1");
        endpoints.put("test", "GET /api/ai-financial-prediction/test");
        endpoints.put("demo", "GET /api/ai-financial-prediction/demo");
        
        Map<String, String> features = new HashMap<>();
        features.put("cashFlowPrediction", "Prédiction des flux de trésorerie futurs");
        features.put("profitabilityPrediction", "Prédiction de la rentabilité future");
        features.put("trendAnalysis", "Analyse des tendances et patterns");
        features.put("financialRecommendations", "Recommandations financières intelligentes");
        features.put("riskPrediction", "Prédiction des risques financiers");
        features.put("aiPowered", "Alimenté par l'intelligence artificielle");
        
        response.put("endpoints", endpoints);
        response.put("features", features);
        response.put("supportedAnalysisTypes", java.util.List.of("REVENUE", "EXPENSES", "CASH_FLOW", "PROFITABILITY"));
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
            
            // Exemple de prédiction de flux de trésorerie
            Map<String, Object> cashFlowExample = new HashMap<>();
            cashFlowExample.put("type", "CASH_FLOW_PREDICTION");
            cashFlowExample.put("monthsAhead", 12);
            cashFlowExample.put("averagePrediction", "125,000 EUR");
            cashFlowExample.put("trend", "INCREASING");
            cashFlowExample.put("confidence", 0.85);
            cashFlowExample.put("riskLevel", "MEDIUM");
            
            // Exemple de prédiction de rentabilité
            Map<String, Object> profitabilityExample = new HashMap<>();
            profitabilityExample.put("type", "PROFITABILITY_PREDICTION");
            profitabilityExample.put("monthsAhead", 12);
            profitabilityExample.put("averageProfitMargin", "22.5%");
            profitabilityExample.put("trend", "INCREASING");
            profitabilityExample.put("growthRate", "12.5%");
            profitabilityExample.put("confidence", 0.88);
            
            // Exemple de recommandations
            Map<String, Object> recommendationsExample = new HashMap<>();
            recommendationsExample.put("type", "FINANCIAL_RECOMMENDATIONS");
            recommendationsExample.put("totalRecommendations", 5);
            recommendationsExample.put("priorityRecommendations", 2);
            recommendationsExample.put("estimatedImpact", "150,000 EUR");
            recommendationsExample.put("timeframe", "12 mois");
            
            examples.put("cashFlowPrediction", cashFlowExample);
            examples.put("profitabilityPrediction", profitabilityExample);
            examples.put("financialRecommendations", recommendationsExample);
            
            demo.put("examples", examples);
            demo.put("message", "Démonstration des prédictions financières IA");
            demo.put("description", "L'IA analyse les données historiques et prédit les tendances futures");
            
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





