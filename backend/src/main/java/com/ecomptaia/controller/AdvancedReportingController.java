package com.ecomptaia.controller;

import com.ecomptaia.entity.FinancialReport;
import com.ecomptaia.entity.ReportNote;
import com.ecomptaia.service.AdvancedReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advanced-reporting")
@CrossOrigin(origins = "*")
public class AdvancedReportingController {

    @Autowired
    private AdvancedReportingService advancedReportingService;

    // ==================== GÉNÉRATION DE RAPPORTS ====================

    /**
     * Générer un rapport financier complet
     */
    @PostMapping("/generate-complete-report")
    public ResponseEntity<Map<String, Object>> generateCompleteReport(
            @RequestParam String reportName,
            @RequestParam String periodStart,
            @RequestParam String periodEnd,
            @RequestParam Long entrepriseId,
            @RequestParam Long generatedBy) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime start = LocalDateTime.parse(periodStart);
            LocalDateTime end = LocalDateTime.parse(periodEnd);
            
            FinancialReport report = advancedReportingService.generateCompleteFinancialReport(
                reportName, start, end, entrepriseId, generatedBy);
            
            response.put("success", true);
            response.put("message", "Rapport financier complet généré avec succès");
            response.put("report", report);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la génération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Générer un bilan comptable
     */
    @PostMapping("/generate-balance-sheet")
    public ResponseEntity<Map<String, Object>> generateBalanceSheet(
            @RequestParam String reportName,
            @RequestParam String periodStart,
            @RequestParam String periodEnd,
            @RequestParam Long entrepriseId,
            @RequestParam Long generatedBy) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime start = LocalDateTime.parse(periodStart);
            LocalDateTime end = LocalDateTime.parse(periodEnd);
            
            FinancialReport report = advancedReportingService.generateBalanceSheet(
                reportName, start, end, entrepriseId, generatedBy);
            
            response.put("success", true);
            response.put("message", "Bilan comptable généré avec succès");
            response.put("report", report);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la génération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Générer un compte de résultat
     */
    @PostMapping("/generate-income-statement")
    public ResponseEntity<Map<String, Object>> generateIncomeStatement(
            @RequestParam String reportName,
            @RequestParam String periodStart,
            @RequestParam String periodEnd,
            @RequestParam Long entrepriseId,
            @RequestParam Long generatedBy) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime start = LocalDateTime.parse(periodStart);
            LocalDateTime end = LocalDateTime.parse(periodEnd);
            
            FinancialReport report = advancedReportingService.generateIncomeStatement(
                reportName, start, end, entrepriseId, generatedBy);
            
            response.put("success", true);
            response.put("message", "Compte de résultat généré avec succès");
            response.put("report", report);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la génération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Générer un tableau de flux de trésorerie
     */
    @PostMapping("/generate-cash-flow-statement")
    public ResponseEntity<Map<String, Object>> generateCashFlowStatement(
            @RequestParam String reportName,
            @RequestParam String periodStart,
            @RequestParam String periodEnd,
            @RequestParam Long entrepriseId,
            @RequestParam Long generatedBy) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime start = LocalDateTime.parse(periodStart);
            LocalDateTime end = LocalDateTime.parse(periodEnd);
            
            FinancialReport report = advancedReportingService.generateCashFlowStatement(
                reportName, start, end, entrepriseId, generatedBy);
            
            response.put("success", true);
            response.put("message", "Tableau de flux de trésorerie généré avec succès");
            response.put("report", report);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la génération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    // ==================== GESTION DES RAPPORTS ====================

    /**
     * Obtenir les statistiques des rapports
     */
    @GetMapping("/statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getReportStatistics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = advancedReportingService.getReportStatistics(entrepriseId);
            
            response.put("success", true);
            response.put("message", "Statistiques récupérées avec succès");
            response.put("statistics", stats);
            response.put("entrepriseId", entrepriseId);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les rapports récents
     */
    @GetMapping("/recent-reports/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getRecentReports(
            @PathVariable Long entrepriseId,
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<FinancialReport> reports = advancedReportingService.getRecentReports(entrepriseId, limit);
            
            response.put("success", true);
            response.put("message", "Rapports récents récupérés avec succès");
            response.put("reports", reports);
            response.put("count", reports.size());
            response.put("entrepriseId", entrepriseId);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les notes d'un rapport
     */
    @GetMapping("/report-notes/{financialReportId}")
    public ResponseEntity<Map<String, Object>> getReportNotes(@PathVariable Long financialReportId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ReportNote> notes = advancedReportingService.getReportNotes(financialReportId);
            
            response.put("success", true);
            response.put("message", "Notes du rapport récupérées avec succès");
            response.put("notes", notes);
            response.put("count", notes.size());
            response.put("financialReportId", financialReportId);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    // ==================== GESTION DES STATUTS ====================

    /**
     * Approuver un rapport
     */
    @PostMapping("/approve-report/{reportId}")
    public ResponseEntity<Map<String, Object>> approveReport(@PathVariable Long reportId) {
        Map<String, Object> response = new HashMap<>();
        try {
            FinancialReport report = advancedReportingService.approveReport(reportId);
            
            response.put("success", true);
            response.put("message", "Rapport approuvé avec succès");
            response.put("report", report);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'approbation: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Publier un rapport
     */
    @PostMapping("/publish-report/{reportId}")
    public ResponseEntity<Map<String, Object>> publishReport(@PathVariable Long reportId) {
        Map<String, Object> response = new HashMap<>();
        try {
            FinancialReport report = advancedReportingService.publishReport(reportId);
            
            response.put("success", true);
            response.put("message", "Rapport publié avec succès");
            response.put("report", report);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la publication: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Archiver un rapport
     */
    @PostMapping("/archive-report/{reportId}")
    public ResponseEntity<Map<String, Object>> archiveReport(@PathVariable Long reportId) {
        Map<String, Object> response = new HashMap<>();
        try {
            FinancialReport report = advancedReportingService.archiveReport(reportId);
            
            response.put("success", true);
            response.put("message", "Rapport archivé avec succès");
            response.put("report", report);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'archivage: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    // ==================== TEST ====================

    /**
     * Endpoint de test simple
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module de rapports avancés opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("features", new String[]{
            "Rapports financiers complets",
            "Bilan comptable",
            "Compte de résultat", 
            "Tableau de flux de trésorerie",
            "Notes annexes automatiques",
            "Analytics en temps réel",
            "Rapports personnalisés",
            "Export multi-format",
            "Planification automatique",
            "Conformité OHADA"
        });
        return ResponseEntity.ok(response);
    }

    /**
     * Test de génération de rapport complet
     */
    @PostMapping("/test-generate-complete-report")
    public ResponseEntity<Map<String, Object>> testGenerateCompleteReport() {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
            
            FinancialReport report = advancedReportingService.generateCompleteFinancialReport(
                "Test - Rapport complet " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                start, end, 1L, 1L);
            
            response.put("success", true);
            response.put("message", "Rapport complet OHADA généré avec succès");
            response.put("report", report);
            response.put("notesCount", report.getNotesCount());
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la génération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de génération de rapport OHADA complet avec toutes les notes
     */
    @PostMapping("/test-generate-ohada-complete-report")
    public ResponseEntity<Map<String, Object>> testGenerateOHADACompleteReport() {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
            
            FinancialReport report = advancedReportingService.generateCompleteFinancialReport(
                "Test - Rapport OHADA complet " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                start, end, 1L, 1L);
            
            response.put("success", true);
            response.put("message", "Rapport OHADA complet avec toutes les notes généré avec succès");
            response.put("report", report);
            response.put("notesCount", report.getNotesCount());
            response.put("expectedNotes", 30); // Nombre total de notes OHADA
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la génération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test de génération de bilan
     */
    @PostMapping("/test-generate-balance-sheet")
    public ResponseEntity<Map<String, Object>> testGenerateBalanceSheet() {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
            
            FinancialReport report = advancedReportingService.generateBalanceSheet(
                "Test - Bilan " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                start, end, 1L, 1L);
            
            response.put("success", true);
            response.put("message", "Bilan de test généré avec succès");
            response.put("report", report);
            response.put("notesCount", report.getNotesCount());
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la génération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Test des statistiques
     */
    @GetMapping("/test-statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testStatistics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = advancedReportingService.getReportStatistics(entrepriseId);
            
            response.put("success", true);
            response.put("message", "Statistiques de test récupérées avec succès");
            response.put("stats", stats);
            response.put("entrepriseId", entrepriseId);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            response.put("timestamp", LocalDateTime.now());
        }
        return ResponseEntity.ok(response);
    }
}
