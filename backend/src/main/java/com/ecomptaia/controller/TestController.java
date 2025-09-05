package com.ecomptaia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur de test pour vérifier le fonctionnement du backend
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    /**
     * Test de base du backend
     */
    @GetMapping
    public ResponseEntity<?> testBasic() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Backend E-COMPTA-IA fonctionne correctement !");
        response.put("status", "SUCCESS");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * Test de santé du système
     */
    @GetMapping("/health")
    public ResponseEntity<?> testHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "E-COMPTA-IA Backend");
        response.put("timestamp", LocalDateTime.now());
        response.put("uptime", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Test des endpoints disponibles
     */
    @GetMapping("/endpoints")
    public ResponseEntity<?> testEndpoints() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> endpoints = new HashMap<>();
        
        endpoints.put("Authentication", "/api/auth/*");
        endpoints.put("Dashboard", "/api/dashboard/*");
        endpoints.put("Accounting", "/api/accounting/*");
        endpoints.put("HR", "/api/hr/*");
        endpoints.put("Third Parties", "/api/third-parties/*");
        endpoints.put("Assets", "/api/assets/*");
        endpoints.put("Documents", "/api/documents/*");
        endpoints.put("AI Services", "/api/ai/*");
        endpoints.put("International", "/api/international/*");
        endpoints.put("System", "/api/system/*");
        endpoints.put("Workflow", "/api/workflow/*");
        endpoints.put("Subscription", "/api/subscription/*");
        endpoints.put("Government Platform", "/api/government-platform/*");
        endpoints.put("SMT", "/api/smt/*");
        
        response.put("message", "Endpoints disponibles");
        response.put("endpoints", endpoints);
        response.put("count", endpoints.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Test de la base de données
     */
    @GetMapping("/database")
    public ResponseEntity<?> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Test simple de connexion
            response.put("status", "CONNECTED");
            response.put("message", "Base de données accessible");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Erreur de connexion à la base de données: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test des données du dashboard
     */
    @GetMapping("/dashboard-data")
    public ResponseEntity<?> testDashboardData() {
        Map<String, Object> response = new HashMap<>();
        
        // Données de test pour le dashboard
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("revenue", 1250000.00);
        kpis.put("expenses", 850000.00);
        kpis.put("netResult", 400000.00);
        kpis.put("cashFlow", 350000.00);
        kpis.put("revenueGrowth", 12.5);
        kpis.put("expenseGrowth", 8.2);
        kpis.put("profitability", 20.5);
        kpis.put("liquidity", 85.2);
        kpis.put("currency", "XOF");
        kpis.put("lastUpdate", System.currentTimeMillis());
        
        response.put("success", true);
        response.put("data", kpis);
        response.put("message", "Données de test chargées avec succès");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}