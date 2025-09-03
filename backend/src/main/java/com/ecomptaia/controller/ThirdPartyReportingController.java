package com.ecomptaia.controller;

import com.ecomptaia.service.ThirdPartyReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Contrôleur pour les rapports spécifiques aux tiers
 */
@RestController
@RequestMapping("/api/third-party-reporting")
@CrossOrigin(origins = "*")
public class ThirdPartyReportingController {

    @Autowired
    private ThirdPartyReportingService thirdPartyReportingService;

    /**
     * Générer le Grand Livre d'un tiers spécifique
     * GET /api/third-party-reporting/ledger?companyId=1&thirdPartyId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/ledger")
    public ResponseEntity<Map<String, Object>> generateThirdPartyLedger(
            @RequestParam Long companyId,
            @RequestParam Long thirdPartyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> ledger = thirdPartyReportingService.generateThirdPartyLedger(
                companyId, thirdPartyId, startDate, endDate);
            return ResponseEntity.ok(ledger);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Grand Livre du tiers",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer la Balance des Tiers (Balance Âgée)
     * GET /api/third-party-reporting/balance?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> generateThirdPartyBalance(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            Map<String, Object> balance = thirdPartyReportingService.generateThirdPartyBalance(companyId, asOfDate);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération de la Balance des Tiers",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer l'Échéancier des Tiers
     * GET /api/third-party-reporting/schedule?companyId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/schedule")
    public ResponseEntity<Map<String, Object>> generateThirdPartySchedule(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> schedule = thirdPartyReportingService.generateThirdPartySchedule(
                companyId, startDate, endDate);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération de l'Échéancier des Tiers",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer l'Analyse des Créances et Dettes
     * GET /api/third-party-reporting/receivables-analysis?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/receivables-analysis")
    public ResponseEntity<Map<String, Object>> generateReceivablesAnalysis(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            Map<String, Object> analysis = thirdPartyReportingService.generateReceivablesAnalysis(companyId, asOfDate);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération de l'Analyse des Créances",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer le Rapport de Recouvrement
     * GET /api/third-party-reporting/collection-report?companyId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/collection-report")
    public ResponseEntity<Map<String, Object>> generateCollectionReport(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> report = thirdPartyReportingService.generateCollectionReport(
                companyId, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Rapport de Recouvrement",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Informations sur les rapports tiers disponibles
     * GET /api/third-party-reporting/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getReportingInfo() {
        Map<String, Object> info = Map.of(
            "service", "Third Party Reporting Service",
            "version", "1.0",
            "availableReports", new String[]{
                "Grand Livre par Tiers",
                "Balance des Tiers (Balance Âgée)",
                "Échéancier des Tiers",
                "Analyse des Créances et Dettes",
                "Rapport de Recouvrement"
            },
            "endpoints", Map.of(
                "ledger", "/api/third-party-reporting/ledger",
                "balance", "/api/third-party-reporting/balance",
                "schedule", "/api/third-party-reporting/schedule",
                "receivables-analysis", "/api/third-party-reporting/receivables-analysis",
                "collection-report", "/api/third-party-reporting/collection-report"
            ),
            "timestamp", java.time.LocalDateTime.now()
        );
        return ResponseEntity.ok(info);
    }

    /**
     * Test complet des rapports tiers
     * GET /api/third-party-reporting/test/complete?companyId=1
     */
    @GetMapping("/test/complete")
    public ResponseEntity<Map<String, Object>> testCompleteReporting(@RequestParam Long companyId) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate startOfYear = LocalDate.of(today.getYear(), 1, 1);
            
            Map<String, Object> results = Map.of(
                "balanceTest", thirdPartyReportingService.generateThirdPartyBalance(companyId, today),
                "receivablesAnalysisTest", thirdPartyReportingService.generateReceivablesAnalysis(companyId, today),
                "scheduleTest", thirdPartyReportingService.generateThirdPartySchedule(companyId, startOfYear, today),
                "collectionReportTest", thirdPartyReportingService.generateCollectionReport(companyId, startOfYear, today),
                "message", "Tests des rapports tiers complets effectués",
                "timestamp", java.time.LocalDateTime.now()
            );
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors des tests",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }
}





