package com.ecomptaia.controller;

import com.ecomptaia.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // ==================== DASHBOARD FINANCIER ====================

    /**
     * Dashboard financier principal
     */
    @GetMapping("/financial")
    public ResponseEntity<Map<String, Object>> getFinancialDashboard(
            @RequestParam Long companyId,
            @RequestParam(required = false) String period) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> dashboard = dashboardService.getFinancialDashboard(companyId, period);
            response.put("success", true);
            response.put("data", dashboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * KPIs financiers en temps réel
     */
    @GetMapping("/financial/kpis")
    public ResponseEntity<Map<String, Object>> getFinancialKPIs(
            @RequestParam Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> kpis = dashboardService.getFinancialKPIs(companyId);
            response.put("success", true);
            response.put("data", kpis);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Graphiques de performance financière
     */
    @GetMapping("/financial/charts")
    public ResponseEntity<Map<String, Object>> getFinancialCharts(
            @RequestParam Long companyId,
            @RequestParam(required = false) String chartType,
            @RequestParam(required = false) String period) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> charts = dashboardService.getFinancialCharts(companyId, chartType, period);
            response.put("success", true);
            response.put("data", charts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== DASHBOARD OPÉRATIONNEL ====================

    /**
     * Dashboard opérationnel
     */
    @GetMapping("/operational")
    public ResponseEntity<Map<String, Object>> getOperationalDashboard(
            @RequestParam Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> dashboard = dashboardService.getOperationalDashboard(companyId);
            response.put("success", true);
            response.put("data", dashboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Métriques opérationnelles
     */
    @GetMapping("/operational/metrics")
    public ResponseEntity<Map<String, Object>> getOperationalMetrics(
            @RequestParam Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> metrics = dashboardService.getOperationalMetrics(companyId);
            response.put("success", true);
            response.put("data", metrics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== DASHBOARD ANALYTIQUE ====================

    /**
     * Dashboard analytique
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalyticsDashboard(
            @RequestParam Long companyId,
            @RequestParam(required = false) String analysisType) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> dashboard = dashboardService.getAnalyticsDashboard(companyId, analysisType);
            response.put("success", true);
            response.put("data", dashboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Tendances et analyses
     */
    @GetMapping("/analytics/trends")
    public ResponseEntity<Map<String, Object>> getAnalyticsTrends(
            @RequestParam Long companyId,
            @RequestParam(required = false) String metric,
            @RequestParam(required = false) String period) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> trends = dashboardService.getAnalyticsTrends(companyId, metric, period);
            response.put("success", true);
            response.put("data", trends);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== DASHBOARD DE PERFORMANCE ====================

    /**
     * Dashboard de performance
     */
    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceDashboard(
            @RequestParam Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> dashboard = dashboardService.getPerformanceDashboard(companyId);
            response.put("success", true);
            response.put("data", dashboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Métriques de performance système
     */
    @GetMapping("/performance/system")
    public ResponseEntity<Map<String, Object>> getSystemPerformanceMetrics() {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> metrics = dashboardService.getSystemPerformanceMetrics();
            response.put("success", true);
            response.put("data", metrics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== DASHBOARD PERSONNALISÉ ====================

    /**
     * Dashboard personnalisé
     */
    @GetMapping("/custom")
    public ResponseEntity<Map<String, Object>> getCustomDashboard(
            @RequestParam Long companyId,
            @RequestParam(required = false) String widgets) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> dashboard = dashboardService.getCustomDashboard(companyId, widgets);
            response.put("success", true);
            response.put("data", dashboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Sauvegarder une configuration de dashboard
     */
    @PostMapping("/custom/save")
    public ResponseEntity<Map<String, Object>> saveCustomDashboard(
            @RequestParam Long companyId,
            @RequestParam Long userId,
            @RequestBody Map<String, Object> configuration) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> saved = dashboardService.saveCustomDashboard(companyId, userId, configuration);
            response.put("success", true);
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test du dashboard
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDashboard() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> testData = dashboardService.getTestDashboardData();
            response.put("success", true);
            response.put("message", "Dashboard test réussi");
            response.put("data", testData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du test : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}