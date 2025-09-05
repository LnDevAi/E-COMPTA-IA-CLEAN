package com.ecomptaia.controller;

import com.ecomptaia.service.FinancialDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Contrôleur pour les tableaux de bord financiers et analyses
 */
@RestController
@RequestMapping("/api/financial-dashboard")
@CrossOrigin(origins = "*")
public class FinancialDashboardController {

    @Autowired
    private FinancialDashboardService financialDashboardService;

    /**
     * Tableau de bord principal avec tous les KPIs
     */
    @GetMapping("/main-dashboard")
    public ResponseEntity<Map<String, Object>> getMainDashboard(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> dashboard = financialDashboardService.getMainDashboard(companyId, startDate, endDate);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la génération du tableau de bord: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * KPI de rentabilité
     */
    @GetMapping("/profitability")
    public ResponseEntity<Map<String, Object>> getProfitabilityKPI(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> profitability = financialDashboardService.getProfitabilityKPI(companyId, startDate, endDate);
            return ResponseEntity.ok(profitability);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors du calcul de la rentabilité: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * KPI de liquidité
     */
    @GetMapping("/liquidity")
    public ResponseEntity<Map<String, Object>> getLiquidityKPI(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> liquidity = financialDashboardService.getLiquidityKPI(companyId, startDate, endDate);
            return ResponseEntity.ok(liquidity);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors du calcul de la liquidité: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Endpoint de test pour vérifier que le service fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDashboardService() {
        return ResponseEntity.ok(Map.of(
            "message", "Service de tableaux de bord financiers opérationnel",
            "endpoints", Map.of(
                "main-dashboard", "GET /api/financial-dashboard/main-dashboard?companyId=1&startDate=2024-01-01&endDate=2024-12-31",
                "profitability", "GET /api/financial-dashboard/profitability?companyId=1&startDate=2024-01-01&endDate=2024-12-31",
                "liquidity", "GET /api/financial-dashboard/liquidity?companyId=1&startDate=2024-01-01&endDate=2024-12-31",
                "test", "GET /api/financial-dashboard/test"
            ),
            "features", Map.of(
                "mainKPIs", "Chiffre d'affaires, charges, résultat net, marge brute",
                "trends", "Analyses de tendances et croissance",
                "accountDistribution", "Répartition par classe de compte OHADA",
                "monthlyActivity", "Activité mensuelle et moyennes",
                "alerts", "Alertes et notifications automatiques",
                "profitability", "KPI de rentabilité détaillé",
                "liquidity", "KPI de liquidité et ratios"
            ),
            "status", "ready"
        ));
    }

    /**
     * Tableau de bord de démonstration avec données simulées
     */
    @GetMapping("/demo-dashboard")
    public ResponseEntity<Map<String, Object>> getDemoDashboard() {
        try {
            Map<String, Object> demoDashboard = Map.of(
                "kpis", Map.of(
                    "revenue", new java.math.BigDecimal("2500000.00"),
                    "expenses", new java.math.BigDecimal("1800000.00"),
                    "netResult", new java.math.BigDecimal("700000.00"),
                    "grossMargin", new java.math.BigDecimal("750000.00"),
                    "totalEntries", 1250,
                    "uniqueAccounts", 45,
                    "profitabilityRatio", new java.math.BigDecimal("28.00")
                ),
                "trends", Map.of(
                    "monthlyTrends", Map.of(
                        "JANUARY", new java.math.BigDecimal("180000.00"),
                        "FEBRUARY", new java.math.BigDecimal("195000.00"),
                        "MARCH", new java.math.BigDecimal("210000.00"),
                        "APRIL", new java.math.BigDecimal("225000.00"),
                        "MAY", new java.math.BigDecimal("240000.00"),
                        "JUNE", new java.math.BigDecimal("255000.00")
                    ),
                    "growthRate", new java.math.BigDecimal("8.33"),
                    "trendDirection", "POSITIVE"
                ),
                "accountDistribution", Map.of(
                    "byClass", Map.of(
                        "Capitaux", new java.math.BigDecimal("500000.00"),
                        "Immobilisations", new java.math.BigDecimal("800000.00"),
                        "Stocks", new java.math.BigDecimal("300000.00"),
                        "Tiers", new java.math.BigDecimal("400000.00"),
                        "Financier", new java.math.BigDecimal("200000.00"),
                        "Charges", new java.math.BigDecimal("1800000.00"),
                        "Produits", new java.math.BigDecimal("2500000.00")
                    ),
                    "totalAmount", new java.math.BigDecimal("6500000.00")
                ),
                "monthlyActivity", Map.of(
                    "entriesByMonth", Map.of(
                        "JANUARY", 180,
                        "FEBRUARY", 195,
                        "MARCH", 210,
                        "APRIL", 225,
                        "MAY", 240,
                        "JUNE", 255
                    ),
                    "amountsByMonth", Map.of(
                        "JANUARY", new java.math.BigDecimal("180000.00"),
                        "FEBRUARY", new java.math.BigDecimal("195000.00"),
                        "MARCH", new java.math.BigDecimal("210000.00"),
                        "APRIL", new java.math.BigDecimal("225000.00"),
                        "MAY", new java.math.BigDecimal("240000.00"),
                        "JUNE", new java.math.BigDecimal("255000.00")
                    ),
                    "averageEntriesPerMonth", 208
                ),
                "alerts", java.util.List.of(
                    "✅ Activité comptable normale",
                    "💰 Montant élevé détecté: 2500000 - Vérification recommandée",
                    "📈 Croissance positive de 8.33%"
                ),
                "generatedAt", LocalDate.now(),
                "period", Map.of("startDate", "2024-01-01", "endDate", "2024-12-31"),
                "message", "Tableau de bord de démonstration généré avec succès"
            );
            
            return ResponseEntity.ok(demoDashboard);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la génération du tableau de bord de démonstration: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }
}








