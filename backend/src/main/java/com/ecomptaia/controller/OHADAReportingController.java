package com.ecomptaia.controller;

import com.ecomptaia.service.OHADAReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Contrôleur pour les états financiers conformes aux standards OHADA/SYSCOHADA
 */
@RestController
@RequestMapping("/api/ohada-reporting")
@CrossOrigin(origins = "*")
public class OHADAReportingController {

    @Autowired
    private OHADAReportingService ohadaReportingService;

    /**
     * Générer le Grand Livre OHADA
     * GET /api/ohada-reporting/general-ledger?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/general-ledger")
    public ResponseEntity<Map<String, Object>> generateGeneralLedgerOHADA(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            // Pour le Grand Livre, on prend toutes les écritures jusqu'à la date spécifiée
            LocalDate startDate = LocalDate.of(1900, 1, 1);
            Map<String, Object> ledger = ohadaReportingService.generateGeneralLedgerOHADA(companyId, startDate, asOfDate);
            return ResponseEntity.ok(ledger);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Grand Livre OHADA",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer le Bilan OHADA
     * GET /api/ohada-reporting/balance-sheet?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/balance-sheet")
    public ResponseEntity<Map<String, Object>> generateBalanceSheetOHADA(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            Map<String, Object> balanceSheet = ohadaReportingService.generateBalanceSheetOHADA(companyId, asOfDate);
            return ResponseEntity.ok(balanceSheet);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Bilan OHADA",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer le Compte de Résultat OHADA
     * GET /api/ohada-reporting/income-statement?companyId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/income-statement")
    public ResponseEntity<Map<String, Object>> generateIncomeStatementOHADA(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> incomeStatement = ohadaReportingService.generateIncomeStatementOHADA(companyId, startDate, endDate);
            return ResponseEntity.ok(incomeStatement);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Compte de Résultat OHADA",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer le Tableau des Flux de Trésorerie OHADA
     * GET /api/ohada-reporting/cash-flow-statement?companyId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/cash-flow-statement")
    public ResponseEntity<Map<String, Object>> generateCashFlowStatementOHADA(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> cashFlowStatement = ohadaReportingService.generateCashFlowStatementOHADA(companyId, startDate, endDate);
            return ResponseEntity.ok(cashFlowStatement);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération du Tableau des Flux de Trésorerie OHADA",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer les Annexes OHADA
     * GET /api/ohada-reporting/annexes?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/annexes")
    public ResponseEntity<Map<String, Object>> generateAnnexesOHADA(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            Map<String, Object> annexes = ohadaReportingService.generateAnnexesOHADA(companyId, asOfDate);
            return ResponseEntity.ok(annexes);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération des Annexes OHADA",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Générer tous les états financiers OHADA
     * GET /api/ohada-reporting/all-statements?companyId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/all-statements")
    public ResponseEntity<Map<String, Object>> generateAllFinancialStatementsOHADA(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            Map<String, Object> allStatements = ohadaReportingService.generateAllFinancialStatementsOHADA(companyId, startDate, endDate);
            return ResponseEntity.ok(allStatements);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors de la génération des états financiers OHADA",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint de test pour vérifier le service OHADA
     * GET /api/ohada-reporting/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testOHADAReportingService() {
        Map<String, Object> response = Map.of(
            "message", "Service de reporting OHADA/SYSCOHADA opérationnel",
            "standard", "OHADA/SYSCOHADA",
            "endpoints", Map.of(
                "generalLedger", "GET /api/ohada-reporting/general-ledger",
                "balanceSheet", "GET /api/ohada-reporting/balance-sheet",
                "incomeStatement", "GET /api/ohada-reporting/income-statement",
                "cashFlowStatement", "GET /api/ohada-reporting/cash-flow-statement",
                "annexes", "GET /api/ohada-reporting/annexes",
                "allStatements", "GET /api/ohada-reporting/all-statements"
            ),
            "features", Map.of(
                "bilan", "Structure complète conforme SYSCOHADA avec codes de postes",
                "compteResultat", "Soldes intermédiaires de gestion (SIG)",
                "fluxTresorerie", "Tableau des flux de trésorerie",
                "annexes", "Notes explicatives détaillées",
                "mapping", "Mapping précis comptes vers postes OHADA"
            ),
            "status", "SUCCESS",
            "timestamp", java.time.LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de diagnostic pour le service OHADA
     * GET /api/ohada-reporting/debug?companyId=1&asOfDate=2024-12-31
     */
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugOHADAReportingService(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            // Test simple pour diagnostiquer le service OHADA
            LocalDate startDate = LocalDate.of(1900, 1, 1);
            
            Map<String, Object> debug = Map.of(
                "companyId", companyId,
                "startDate", startDate,
                "asOfDate", asOfDate,
                "standard", "OHADA/SYSCOHADA",
                "service", "OHADAReportingService",
                "status", "SUCCESS",
                "message", "Service OHADA opérationnel",
                "timestamp", java.time.LocalDateTime.now()
            );
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", "Erreur lors du diagnostic du service OHADA",
                "message", e.getMessage(),
                "status", "ERROR"
            );
            return ResponseEntity.badRequest().body(error);
        }
    }
}





