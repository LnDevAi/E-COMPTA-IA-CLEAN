package com.ecomptaia.controller;

import com.ecomptaia.entity.AutomatedTask;
import com.ecomptaia.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AIController {

    @Autowired
    private AIService aiService;

    // === ANALYSE DE DOCUMENTS ===
    
    @PostMapping("/analyze-document")
    public ResponseEntity<?> analyzeDocument(@RequestBody Map<String, Object> request) {
        try {
            String documentText = (String) request.get("documentText");
            String countryCode = (String) request.get("countryCode");
            String accountingStandard = (String) request.get("accountingStandard");

            Map<String, Object> analysis = aiService.analyzeDocument(documentText, countryCode, accountingStandard);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === CALCUL INTELLIGENT DES TAXES ===
    
    @PostMapping("/calculate-taxes")
    public ResponseEntity<?> calculateTaxesWithAI(@RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String countryCode = (String) request.get("countryCode");
            String businessType = (String) request.get("businessType");

            Map<String, Object> result = aiService.calculateTaxesWithAI(amount, countryCode, businessType);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === RECOMMANDATIONS COMPTABLES ===
    
    @PostMapping("/recommendations")
    public ResponseEntity<?> getAccountingRecommendations(@RequestBody Map<String, Object> request) {
        try {
            String countryCode = (String) request.get("countryCode");
            String businessType = (String) request.get("businessType");
            BigDecimal annualRevenue = new BigDecimal(request.get("annualRevenue").toString());

            Map<String, Object> recommendations = aiService.getAccountingRecommendations(countryCode, businessType, annualRevenue);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === DÉTECTION DE FRAUDE ===
    
    @PostMapping("/fraud-detection")
    public ResponseEntity<?> detectFraud(@RequestBody Map<String, Object> request) {
        try {
            String transactionData = (String) request.get("transactionData");
            String countryCode = (String) request.get("countryCode");

            Map<String, Object> fraudAnalysis = aiService.detectFraud(transactionData, countryCode);
            return ResponseEntity.ok(fraudAnalysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === TRAITEMENT ASYNCHRONE ===
    
    @PostMapping("/process-task/{taskId}")
    public ResponseEntity<?> processTaskAsync(@PathVariable Long taskId) {
        try {
            CompletableFuture<Map<String, Object>> future = aiService.processTaskAsync(taskId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", taskId);
            response.put("status", "processing");
            response.put("message", "Tâche en cours de traitement");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === GESTION DES TÂCHES ===
    
    @GetMapping("/tasks/pending")
    public ResponseEntity<?> getPendingTasks() {
        List<AutomatedTask> tasks = aiService.getPendingTasks();
        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasks);
        response.put("total", tasks.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/status/{status}")
    public ResponseEntity<?> getTasksByStatus(@PathVariable String status) {
        List<AutomatedTask> tasks = aiService.getTasksByStatus(status);
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("tasks", tasks);
        response.put("total", tasks.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/country/{countryCode}")
    public ResponseEntity<?> getTasksByCountry(@PathVariable String countryCode) {
        List<AutomatedTask> tasks = aiService.getTasksByCountry(countryCode);
        Map<String, Object> response = new HashMap<>();
        response.put("countryCode", countryCode);
        response.put("tasks", tasks);
        response.put("total", tasks.size());
        return ResponseEntity.ok(response);
    }

    // === STATISTIQUES IA ===
    
    @GetMapping("/stats")
    public ResponseEntity<?> getAISystemStats() {
        Map<String, Object> stats = aiService.getAISystemStats();
        return ResponseEntity.ok(stats);
    }

    // === ENDPOINTS DE TEST ===
    
    @GetMapping("/test")
    public ResponseEntity<?> testAISystem() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Système d'IA opérationnel");
        response.put("availableFeatures", List.of(
            "Analyse automatique de documents",
            "Calcul intelligent des taxes",
            "Recommandations comptables",
            "Détection de fraude",
            "Traitement asynchrone"
        ));
        response.put("aiModels", List.of(
            "GPT-4 - Analyse de documents",
            "TaxAI - Calcul des taxes",
            "AccountingAI - Recommandations",
            "FraudAI - Détection de fraude"
        ));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    // === DÉMONSTRATION RAPIDE ===
    
    @GetMapping("/demo/document-analysis")
    public ResponseEntity<?> demoDocumentAnalysis() {
        try {
            String sampleDocument = "Facture N° F2024-001\nFournisseur: ABC SARL\nMontant: 150,000 XOF\nDate: 15/01/2024\nTVA: 18%";
            
            Map<String, Object> analysis = aiService.analyzeDocument(sampleDocument, "SN", "SYSCOHADA");
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/demo/tax-calculation")
    public ResponseEntity<?> demoTaxCalculation() {
        try {
            Map<String, Object> result = aiService.calculateTaxesWithAI(
                new BigDecimal("1000000"), "SN", "SME"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/demo/recommendations")
    public ResponseEntity<?> demoRecommendations() {
        try {
            Map<String, Object> recommendations = aiService.getAccountingRecommendations(
                "SN", "SME", new BigDecimal("500000")
            );
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/demo/fraud-detection")
    public ResponseEntity<?> demoFraudDetection() {
        try {
            String sampleTransaction = "{\"amount\": 50000, \"frequency\": \"daily\", \"pattern\": \"regular\"}";
            Map<String, Object> fraudAnalysis = aiService.detectFraud(sampleTransaction, "SN");
            return ResponseEntity.ok(fraudAnalysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}





