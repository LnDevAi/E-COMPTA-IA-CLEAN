package com.ecomptaia.controller;

import com.ecomptaia.service.AdvancedAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour l'IA avancée
 */
@RestController
@RequestMapping("/api/advanced-ai")
@CrossOrigin(origins = "*")
public class AdvancedAIController {

    @Autowired
    private AdvancedAIService advancedAIService;

    /**
     * Analyse prédictive des flux de trésorerie
     */
    @GetMapping("/predict-cash-flow")
    public ResponseEntity<Map<String, Object>> predictCashFlow(
            @RequestParam Long companyId,
            @RequestParam(defaultValue = "12") int monthsAhead) {
        
        return ResponseEntity.ok(advancedAIService.predictCashFlow(companyId, monthsAhead));
    }

    /**
     * Détection d'anomalies dans les écritures comptables
     */
    @GetMapping("/detect-anomalies")
    public ResponseEntity<Map<String, Object>> detectAnomalies(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        return ResponseEntity.ok(advancedAIService.detectAccountingAnomalies(companyId, startDate, endDate));
    }

    /**
     * Recommandations d'optimisation fiscale
     */
    @GetMapping("/tax-optimization")
    public ResponseEntity<Map<String, Object>> getTaxOptimizationRecommendations(
            @RequestParam Long companyId) {
        
        return ResponseEntity.ok(advancedAIService.getTaxOptimizationRecommendations(companyId));
    }

    /**
     * Analyse de sentiment des données financières
     */
    @GetMapping("/financial-sentiment")
    public ResponseEntity<Map<String, Object>> analyzeFinancialSentiment(
            @RequestParam Long companyId) {
        
        return ResponseEntity.ok(advancedAIService.analyzeFinancialSentiment(companyId));
    }

    /**
     * Assistant IA pour les questions comptables
     */
    @PostMapping("/accounting-assistant")
    public ResponseEntity<Map<String, Object>> getAIAccountingAssistant(
            @RequestParam String question,
            @RequestParam(defaultValue = "") String context) {
        
        return ResponseEntity.ok(advancedAIService.getAIAccountingAssistant(question, context));
    }

    /**
     * Analyse prédictive des risques financiers
     */
    @GetMapping("/predict-risks")
    public ResponseEntity<Map<String, Object>> predictFinancialRisks(
            @RequestParam Long companyId) {
        
        return ResponseEntity.ok(advancedAIService.predictFinancialRisks(companyId));
    }

    /**
     * Optimisation automatique des écritures comptables
     */
    @GetMapping("/optimize-entries")
    public ResponseEntity<Map<String, Object>> optimizeAccountingEntries(
            @RequestParam Long companyId) {
        
        return ResponseEntity.ok(advancedAIService.optimizeAccountingEntries(companyId));
    }

    // === ENDPOINTS DE TEST ===

    /**
     * Test de prédiction de flux de trésorerie
     */
    @GetMapping("/test/cash-flow-prediction")
    public ResponseEntity<Map<String, Object>> testCashFlowPrediction() {
        return ResponseEntity.ok(advancedAIService.predictCashFlow(1L, 6));
    }

    /**
     * Test de détection d'anomalies
     */
    @GetMapping("/test/anomaly-detection")
    public ResponseEntity<Map<String, Object>> testAnomalyDetection() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();
        return ResponseEntity.ok(advancedAIService.detectAccountingAnomalies(1L, startDate, endDate));
    }

    /**
     * Test d'optimisation fiscale
     */
    @GetMapping("/test/tax-optimization")
    public ResponseEntity<Map<String, Object>> testTaxOptimization() {
        return ResponseEntity.ok(advancedAIService.getTaxOptimizationRecommendations(1L));
    }

    /**
     * Test d'analyse de sentiment
     */
    @GetMapping("/test/financial-sentiment")
    public ResponseEntity<Map<String, Object>> testFinancialSentiment() {
        return ResponseEntity.ok(advancedAIService.analyzeFinancialSentiment(1L));
    }

    /**
     * Test de l'assistant IA
     */
    @GetMapping("/test/accounting-assistant")
    public ResponseEntity<Map<String, Object>> testAccountingAssistant() {
        return ResponseEntity.ok(advancedAIService.getAIAccountingAssistant(
            "Comment calculer la TVA déductible ?", "Contexte fiscal OHADA"));
    }

    /**
     * Test de prédiction des risques
     */
    @GetMapping("/test/risk-prediction")
    public ResponseEntity<Map<String, Object>> testRiskPrediction() {
        return ResponseEntity.ok(advancedAIService.predictFinancialRisks(1L));
    }

    /**
     * Test d'optimisation des écritures
     */
    @GetMapping("/test/entry-optimization")
    public ResponseEntity<Map<String, Object>> testEntryOptimization() {
        return ResponseEntity.ok(advancedAIService.optimizeAccountingEntries(1L));
    }

    /**
     * Informations sur l'IA avancée
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAIInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("systeme", "IA Avancée E-COMPTA-IA");
        info.put("version", "2.0");
        info.put("description", "Intelligence artificielle avancée pour l'analyse comptable et financière");
        
        Map<String, String> capabilities = new HashMap<>();
        capabilities.put("prediction", "Analyse prédictive des flux de trésorerie");
        capabilities.put("anomaly", "Détection d'anomalies comptables");
        capabilities.put("optimization", "Optimisation fiscale et comptable");
        capabilities.put("sentiment", "Analyse de sentiment financier");
        capabilities.put("assistant", "Assistant IA pour questions comptables");
        capabilities.put("risk", "Prédiction des risques financiers");
        info.put("capabilities", capabilities);
        
        Map<String, String> technologies = new HashMap<>();
        technologies.put("machineLearning", "Algorithmes de machine learning");
        technologies.put("nlp", "Traitement du langage naturel");
        technologies.put("predictiveAnalytics", "Analytics prédictifs");
        technologies.put("anomalyDetection", "Détection d'anomalies");
        technologies.put("optimization", "Algorithmes d'optimisation");
        info.put("technologies", technologies);
        
        Map<String, String> benefits = new HashMap<>();
        benefits.put("efficiency", "Amélioration de l'efficacité comptable");
        benefits.put("accuracy", "Réduction des erreurs");
        benefits.put("insights", "Insights financiers avancés");
        benefits.put("compliance", "Conformité renforcée");
        benefits.put("optimization", "Optimisation fiscale");
        info.put("benefits", benefits);
        
        return ResponseEntity.ok(info);
    }

    /**
     * Test complet de toutes les fonctionnalités IA
     */
    @GetMapping("/test/all-features")
    public ResponseEntity<Map<String, Object>> testAllFeatures() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("cashFlowPrediction", advancedAIService.predictCashFlow(1L, 6));
            result.put("anomalyDetection", advancedAIService.detectAccountingAnomalies(1L, 
                LocalDateTime.now().minusMonths(1), LocalDateTime.now()));
            result.put("taxOptimization", advancedAIService.getTaxOptimizationRecommendations(1L));
            result.put("financialSentiment", advancedAIService.analyzeFinancialSentiment(1L));
            result.put("accountingAssistant", advancedAIService.getAIAccountingAssistant(
                "Qu'est-ce que l'amortissement comptable ?", "Contexte OHADA"));
            result.put("riskPrediction", advancedAIService.predictFinancialRisks(1L));
            result.put("entryOptimization", advancedAIService.optimizeAccountingEntries(1L));
            
            result.put("status", "SUCCESS");
            result.put("message", "Tous les tests IA avancée ont été exécutés avec succès");
            result.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors des tests: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
}




