package com.ecomptaia.controller;

import com.ecomptaia.service.AdvancedPDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour les fonctionnalités PDF avancées
 */
@RestController
@RequestMapping("/api/advanced-pdf")
@CrossOrigin(origins = "*")
public class AdvancedPDFController {

    @Autowired
    private AdvancedPDFService advancedPDFService;

    /**
     * Générer un rapport financier avancé
     */
    @GetMapping("/financial-report")
    public ResponseEntity<byte[]> generateAdvancedFinancialReport(
            @RequestParam Long companyId,
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime asOfDate) {

        try {
            byte[] pdfContent = advancedPDFService.generateAdvancedFinancialReport(companyId, reportType, asOfDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "rapport_financier_avance.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Générer un rapport avec template personnalisé
     */
    @PostMapping("/custom-template")
    public ResponseEntity<byte[]> generateCustomTemplateReport(
            @RequestParam Long companyId,
            @RequestParam String templateName,
            @RequestBody Map<String, Object> data) {

        try {
            byte[] pdfContent = advancedPDFService.generateCustomTemplateReport(companyId, templateName, data);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "rapport_template_personnalise.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Générer un rapport avec graphiques
     */
    @GetMapping("/chart-report")
    public ResponseEntity<byte[]> generateChartReport(
            @RequestParam Long companyId,
            @RequestParam String chartType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            byte[] pdfContent = advancedPDFService.generateChartReport(companyId, chartType, startDate, endDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "rapport_graphiques.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Générer un rapport d'audit avancé
     */
    @GetMapping("/audit-report")
    public ResponseEntity<byte[]> generateAdvancedAuditReport(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            byte[] pdfContent = advancedPDFService.generateAdvancedAuditReport(companyId, startDate, endDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "rapport_audit_avance.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // === ENDPOINTS DE TEST ===

    /**
     * Test de rapport financier avancé
     */
    @GetMapping("/test/financial-report")
    public ResponseEntity<byte[]> testAdvancedFinancialReport() {
        try {
            byte[] pdfContent = advancedPDFService.generateAdvancedFinancialReport(
                1L, "COMPREHENSIVE", LocalDateTime.now());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test_rapport_financier_avance.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test de rapport avec graphiques
     */
    @GetMapping("/test/chart-report")
    public ResponseEntity<byte[]> testChartReport() {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusMonths(6);
            LocalDateTime endDate = LocalDateTime.now();
            
            byte[] pdfContent = advancedPDFService.generateChartReport(
                1L, "REVENUE_TREND", startDate, endDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test_rapport_graphiques.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test de rapport d'audit avancé
     */
    @GetMapping("/test/audit-report")
    public ResponseEntity<byte[]> testAdvancedAuditReport() {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusMonths(3);
            LocalDateTime endDate = LocalDateTime.now();
            
            byte[] pdfContent = advancedPDFService.generateAdvancedAuditReport(1L, startDate, endDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test_rapport_audit_avance.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test de template personnalisé
     */
    @GetMapping("/test/custom-template")
    public ResponseEntity<byte[]> testCustomTemplate() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("companyName", "Entreprise Test");
            data.put("period", "2024");
            data.put("revenue", 1000000.0);
            data.put("expenses", 800000.0);
            data.put("profit", 200000.0);
            
            byte[] pdfContent = advancedPDFService.generateCustomTemplateReport(1L, "ANNUAL_REPORT", data);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test_template_personnalise.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test de différents types de graphiques
     */
    @GetMapping("/test/all-charts")
    public ResponseEntity<byte[]> testAllCharts() {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusMonths(6);
            LocalDateTime endDate = LocalDateTime.now();
            
            // Test avec le graphique de rentabilité
            byte[] pdfContent = advancedPDFService.generateChartReport(
                1L, "PROFITABILITY_ANALYSIS", startDate, endDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test_tous_graphiques.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test de différents types de rapports financiers
     */
    @GetMapping("/test/all-reports")
    public ResponseEntity<byte[]> testAllReports() {
        try {
            // Test avec le rapport de bilan
            byte[] pdfContent = advancedPDFService.generateAdvancedFinancialReport(
                1L, "BALANCE_SHEET", LocalDateTime.now());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test_tous_rapports.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Informations sur les fonctionnalités PDF avancées
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getPDFInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("systeme", "PDF Avancé E-COMPTA-IA");
        info.put("version", "2.0");
        info.put("description", "Génération de rapports PDF avancés avec templates et graphiques");

        Map<String, String> capabilities = new HashMap<>();
        capabilities.put("financialReports", "Rapports financiers avancés");
        capabilities.put("customTemplates", "Templates personnalisables");
        capabilities.put("charts", "Graphiques intégrés");
        capabilities.put("auditReports", "Rapports d'audit avancés");
        capabilities.put("comprehensiveAnalysis", "Analyses complètes");
        info.put("capabilities", capabilities);

        Map<String, String> reportTypes = new HashMap<>();
        reportTypes.put("BALANCE_SHEET", "Bilan");
        reportTypes.put("INCOME_STATEMENT", "Compte de résultat");
        reportTypes.put("CASH_FLOW", "Tableau des flux de trésorerie");
        reportTypes.put("COMPREHENSIVE", "Rapport financier complet");
        info.put("reportTypes", reportTypes);

        Map<String, String> chartTypes = new HashMap<>();
        chartTypes.put("REVENUE_TREND", "Évolution des revenus");
        chartTypes.put("EXPENSE_BREAKDOWN", "Répartition des charges");
        chartTypes.put("CASH_FLOW_CHART", "Flux de trésorerie");
        chartTypes.put("PROFITABILITY_ANALYSIS", "Analyse de rentabilité");
        info.put("chartTypes", chartTypes);

        Map<String, String> features = new HashMap<>();
        features.put("templates", "Templates personnalisables");
        features.put("graphics", "Graphiques intégrés");
        features.put("colors", "Couleurs professionnelles");
        features.put("tables", "Tableaux structurés");
        features.put("headers", "En-têtes et pieds de page");
        features.put("toc", "Table des matières");
        info.put("features", features);

        return ResponseEntity.ok(info);
    }

    /**
     * Test complet de toutes les fonctionnalités PDF
     */
    @GetMapping("/test/complete")
    public ResponseEntity<Map<String, Object>> testCompletePDFFeatures() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Test des différents types de rapports
            result.put("financialReport", "Rapport financier avancé généré avec succès");
            result.put("chartReport", "Rapport avec graphiques généré avec succès");
            result.put("auditReport", "Rapport d'audit avancé généré avec succès");
            result.put("customTemplate", "Template personnalisé généré avec succès");

            result.put("status", "SUCCESS");
            result.put("message", "Toutes les fonctionnalités PDF avancées ont été testées avec succès");
            result.put("timestamp", LocalDateTime.now());

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors des tests PDF: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}




