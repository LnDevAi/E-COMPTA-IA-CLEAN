package com.ecomptaia.controller;

import com.ecomptaia.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.repository.JournalEntryRepository;

@RestController
@RequestMapping("/api/reporting")
@CrossOrigin(origins = "*")
public class ReportingController {

    @Autowired
    private ReportingService reportingService;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    /**
     * Générer le Grand Livre
     * GET /api/reporting/general-ledger?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/general-ledger")
    public ResponseEntity<Map<String, Object>> generateGeneralLedger(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            // Pour le Grand Livre, on prend toutes les écritures jusqu'à la date spécifiée
            LocalDate startDate = LocalDate.of(1900, 1, 1);
            Map<String, Object> ledger = reportingService.generateGeneralLedger(companyId, startDate, asOfDate);
            return ResponseEntity.ok(ledger);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Grand Livre",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer la Balance Générale
     * GET /api/reporting/trial-balance?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/trial-balance")
    public ResponseEntity<Map<String, Object>> generateTrialBalance(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            Map<String, Object> trialBalance = reportingService.generateTrialBalance(companyId, asOfDate);
            return ResponseEntity.ok(trialBalance);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération de la Balance Générale",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer le Bilan
     * GET /api/reporting/balance-sheet?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/balance-sheet")
    public ResponseEntity<Map<String, Object>> generateBalanceSheet(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            Map<String, Object> balanceSheet = reportingService.generateBalanceSheet(companyId, asOfDate);
            return ResponseEntity.ok(balanceSheet);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Bilan",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer le Compte de Résultat
     * GET /api/reporting/income-statement?companyId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/income-statement")
    public ResponseEntity<Map<String, Object>> generateIncomeStatement(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> incomeStatement = reportingService.generateIncomeStatement(companyId, startDate, endDate);
            return ResponseEntity.ok(incomeStatement);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Compte de Résultat",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtenir les statistiques de reporting
     * GET /api/reporting/statistics?companyId=1
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getReportingStatistics(@RequestParam Long companyId) {
        try {
            Map<String, Object> statistics = reportingService.getReportingStatistics(companyId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la récupération des statistiques",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer tous les rapports pour une période
     * GET /api/reporting/all-reports?companyId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/all-reports")
    public ResponseEntity<Map<String, Object>> generateAllReports(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> allReports = Map.of(
                "companyId", companyId,
                "startDate", startDate,
                "endDate", endDate,
                "generalLedger", reportingService.generateGeneralLedger(companyId, startDate, endDate),
                "trialBalance", reportingService.generateTrialBalance(companyId, endDate),
                "balanceSheet", reportingService.generateBalanceSheet(companyId, endDate),
                "incomeStatement", reportingService.generateIncomeStatement(companyId, startDate, endDate),
                "generatedAt", java.time.LocalDateTime.now()
            );
            
            return ResponseEntity.ok(allReports);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération des rapports",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint de test pour vérifier le service
     * GET /api/reporting/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testReportingService() {
        Map<String, Object> response = Map.of(
            "message", "Service de reporting opérationnel",
            "endpoints", Map.of(
                "generalLedger", "GET /api/reporting/general-ledger",
                "trialBalance", "GET /api/reporting/trial-balance",
                "balanceSheet", "GET /api/reporting/balance-sheet",
                "incomeStatement", "GET /api/reporting/income-statement",
                "statistics", "GET /api/reporting/statistics",
                "allReports", "GET /api/reporting/all-reports"
            ),
            "status", "SUCCESS",
            "timestamp", java.time.LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de diagnostic pour le Grand Livre
     * GET /api/reporting/debug-general-ledger?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/debug-general-ledger")
    public ResponseEntity<Map<String, Object>> debugGeneralLedger(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            // Test simple pour diagnostiquer le problème
            LocalDate startDate = LocalDate.of(1900, 1, 1);
            
            // Vérifier les données disponibles
            List<JournalEntry> entries = journalEntryRepository.findValidatedEntriesByDateRange(companyId, startDate, asOfDate);
            
            Map<String, Object> debug = Map.of(
                "companyId", companyId,
                "startDate", startDate,
                "asOfDate", asOfDate,
                "entriesFound", entries.size(),
                "entries", entries.stream().map(e -> Map.of(
                    "id", e.getId(),
                    "entryNumber", e.getEntryNumber(),
                    "description", e.getDescription(),
                    "status", e.getStatus()
                )).collect(java.util.stream.Collectors.toList()),
                "status", "SUCCESS"
            );
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors du diagnostic du Grand Livre",
                "message", e.getMessage(),
                "stackTrace", e.getStackTrace(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }
}
