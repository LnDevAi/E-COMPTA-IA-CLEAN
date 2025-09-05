package com.ecomptaia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "✅ E-COMPTA-IA Backend fonctionne !");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        
        // Données de test pour le dashboard
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("revenue", 2450000);
        dashboardData.put("expenses", 1850000);
        dashboardData.put("profit", 600000);
        dashboardData.put("employees", 45);
        dashboardData.put("revenueGrowth", 12.5);
        dashboardData.put("expensesGrowth", 8.2);
        dashboardData.put("profitGrowth", 18.7);
        dashboardData.put("employeesGrowth", 3);
        
        response.put("data", dashboardData);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "✅ Test réussi !");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}