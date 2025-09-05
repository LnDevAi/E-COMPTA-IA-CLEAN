package com.ecomptaia.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/basic")
@CrossOrigin(origins = "*")
public class BasicTestController {

    @GetMapping("/test")
    public Map<String, Object> basicTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Backend fonctionne");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
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
        response.put("message", "Données chargées");
        
        return response;
    }
}
