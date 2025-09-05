package com.ecomptaia.controller;

import com.ecomptaia.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

/**
 * Contrôleur pour l'export et téléchargement des documents comptables
 */
@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExportController {

    @Autowired
    private ExportService exportService;

    /**
     * Export du journal général en PDF
     */
    @GetMapping("/journal-pdf")
    public ResponseEntity<Map<String, Object>> exportJournalPDF(
            @RequestParam("companyId") Long companyId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            Map<String, Object> result = exportService.exportGeneralJournalPDF(companyId, start, end);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'export: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Export du grand livre en PDF
     */
    @GetMapping("/ledger-pdf")
    public ResponseEntity<Map<String, Object>> exportLedgerPDF(
            @RequestParam("companyId") Long companyId,
            @RequestParam("asOfDate") String asOfDate) {
        
        try {
            LocalDate date = LocalDate.parse(asOfDate);
            
            Map<String, Object> result = exportService.exportGeneralLedgerPDF(companyId, date);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'export: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Export de la balance en PDF
     */
    @GetMapping("/balance-pdf")
    public ResponseEntity<Map<String, Object>> exportBalancePDF(
            @RequestParam("companyId") Long companyId,
            @RequestParam("asOfDate") String asOfDate) {
        
        try {
            LocalDate date = LocalDate.parse(asOfDate);
            
            Map<String, Object> result = exportService.exportTrialBalancePDF(companyId, date);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'export: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Export des états financiers en PDF
     */
    @GetMapping("/financial-statements-pdf")
    public ResponseEntity<Map<String, Object>> exportFinancialStatementsPDF(
            @RequestParam("companyId") Long companyId,
            @RequestParam("asOfDate") String asOfDate) {
        
        try {
            LocalDate date = LocalDate.parse(asOfDate);
            
            Map<String, Object> result = exportService.exportFinancialStatementsPDF(companyId, date);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'export: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Export en Excel
     */
    @GetMapping("/excel")
    public ResponseEntity<Map<String, Object>> exportToExcel(
            @RequestParam("documentType") String documentType,
            @RequestParam("companyId") Long companyId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            Map<String, Object> result = exportService.exportToExcel(documentType, companyId, start, end);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de l'export Excel: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Validation qualité avant export
     */
    @GetMapping("/validate-quality")
    public ResponseEntity<Map<String, Object>> validateExportQuality(
            @RequestParam("companyId") Long companyId,
            @RequestParam("documentType") String documentType,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            Map<String, Object> result = exportService.validateExportQuality(companyId, documentType, start, end);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la validation: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Test de l'export
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testExport() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service d'export opérationnel");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("journal-pdf", "GET /api/export/journal-pdf?companyId=1&startDate=2024-01-01&endDate=2024-12-31");
        endpoints.put("ledger-pdf", "GET /api/export/ledger-pdf?companyId=1&asOfDate=2024-12-31");
        endpoints.put("balance-pdf", "GET /api/export/balance-pdf?companyId=1&asOfDate=2024-12-31");
        endpoints.put("financial-statements-pdf", "GET /api/export/financial-statements-pdf?companyId=1&asOfDate=2024-12-31");
        endpoints.put("excel", "GET /api/export/excel?documentType=JOURNAL&companyId=1&startDate=2024-01-01&endDate=2024-12-31");
        endpoints.put("validate-quality", "GET /api/export/validate-quality?companyId=1&documentType=JOURNAL&startDate=2024-01-01&endDate=2024-12-31");
        endpoints.put("test", "GET /api/export/test");
        endpoints.put("demo", "GET /api/export/demo");
        
        Map<String, String> features = new HashMap<>();
        features.put("pdfExport", "Export PDF des documents comptables");
        features.put("excelExport", "Export Excel des données tabulaires");
        features.put("qualityValidation", "Validation qualité avant export");
        features.put("financialStatements", "Export des états financiers");
        features.put("journalExport", "Export du journal général");
        features.put("ledgerExport", "Export du grand livre");
        features.put("balanceExport", "Export de la balance");
        features.put("archiving", "Archivage automatique des exports");
        
        response.put("endpoints", endpoints);
        response.put("features", features);
        response.put("supportedFormats", java.util.List.of("PDF", "EXCEL"));
        response.put("supportedDocuments", java.util.List.of(
            "JOURNAL", "GRAND_LIVRE", "BALANCE", "ETATS_FINANCIERS"
        ));
        response.put("status", "ready");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Démonstration de l'export
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> getDemo() {
        try {
            Map<String, Object> demo = new HashMap<>();
            Map<String, Object> examples = new HashMap<>();
            
            // Exemple d'export journal
            Map<String, Object> journalExample = new HashMap<>();
            journalExample.put("documentType", "JOURNAL_GENERAL");
            journalExample.put("fileName", "journal_general_1_20240101_20241231.pdf");
            journalExample.put("fileSize", "245760");
            journalExample.put("downloadUrl", "/exports/journal_general_1_20240101_20241231.pdf");
            journalExample.put("totalEntries", 10);
            journalExample.put("isBalanced", true);
            
            // Exemple d'export grand livre
            Map<String, Object> ledgerExample = new HashMap<>();
            ledgerExample.put("documentType", "GRAND_LIVRE");
            ledgerExample.put("fileName", "grand_livre_1_20241231.pdf");
            ledgerExample.put("fileSize", "512000");
            ledgerExample.put("downloadUrl", "/exports/grand_livre_1_20241231.pdf");
            ledgerExample.put("totalAccounts", 15);
            
            // Exemple d'export balance
            Map<String, Object> balanceExample = new HashMap<>();
            balanceExample.put("documentType", "BALANCE");
            balanceExample.put("fileName", "balance_1_20241231.pdf");
            balanceExample.put("fileSize", "128000");
            balanceExample.put("downloadUrl", "/exports/balance_1_20241231.pdf");
            balanceExample.put("totalAccounts", 20);
            balanceExample.put("isBalanced", true);
            
            // Exemple d'export états financiers
            Map<String, Object> financialExample = new HashMap<>();
            financialExample.put("documentType", "ETATS_FINANCIERS");
            financialExample.put("fileName", "etats_financiers_1_20241231.pdf");
            financialExample.put("fileSize", "1024000");
            financialExample.put("downloadUrl", "/exports/etats_financiers_1_20241231.pdf");
            financialExample.put("includesBalanceSheet", true);
            financialExample.put("includesIncomeStatement", true);
            financialExample.put("includesCashFlowStatement", true);
            
            examples.put("journalExport", journalExample);
            examples.put("ledgerExport", ledgerExample);
            examples.put("balanceExport", balanceExample);
            examples.put("financialStatementsExport", financialExample);
            
            demo.put("examples", examples);
            demo.put("message", "Démonstration du service d'export");
            demo.put("description", "Export PDF et Excel des documents comptables");
            
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








