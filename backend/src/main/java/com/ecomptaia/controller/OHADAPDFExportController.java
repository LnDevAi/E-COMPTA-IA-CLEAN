package com.ecomptaia.controller;

import com.ecomptaia.service.OHADAPDFExportService;
import com.ecomptaia.service.OHADAReportingService;
import com.ecomptaia.service.SimplePDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

/**
 * Contrôleur pour l'export PDF des états financiers OHADA/SYSCOHADA
 */
@RestController
@RequestMapping("/api/ohada-pdf")
@CrossOrigin(origins = "*")
public class OHADAPDFExportController {

    @Autowired
    private OHADAPDFExportService ohadaPDFExportService;
    
    @Autowired
    private OHADAReportingService ohadaReportingService;
    
    @Autowired
    private SimplePDFService simplePDFService;

    /**
     * Exporter le Bilan en PDF
     */
    @GetMapping("/balance-sheet")
    public ResponseEntity<byte[]> exportBalanceSheetPDF(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName) {
        
        try {
            // Récupération des données OHADA
            Map<String, Object> balanceSheetData = ohadaReportingService.generateBalanceSheetOHADA(companyId, asOfDate);
            
            // Vérification des erreurs
            if (balanceSheetData.containsKey("error")) {
                String errorMsg = "Erreur dans les données OHADA: " + balanceSheetData.get("error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg.getBytes());
            }
            
            // Vérification de la présence du bilan
            if (!balanceSheetData.containsKey("bilan") || balanceSheetData.get("bilan") == null) {
                String errorMsg = "Aucune donnée de bilan trouvée pour companyId=" + companyId + " et date=" + asOfDate;
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg.getBytes());
            }
            
            // Génération du PDF
            byte[] pdfBytes = ohadaPDFExportService.generateBalanceSheetPDF(
                (com.ecomptaia.model.ohada.BilanSYSCOHADA) balanceSheetData.get("bilan"),
                companyName,
                asOfDate
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bilan-ohada-" + asOfDate + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (ClassCastException e) {
            String errorMsg = "Erreur de type de données: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMsg.getBytes());
        } catch (IOException e) {
            String errorMsg = "Erreur lors de la génération du PDF: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMsg.getBytes());
        } catch (Exception e) {
            String errorMsg = "Erreur inattendue: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMsg.getBytes());
        }
    }

    /**
     * Exporter le Compte de Résultat en PDF
     */
    @GetMapping("/income-statement")
    public ResponseEntity<byte[]> exportIncomeStatementPDF(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName) {
        
        try {
            Map<String, Object> incomeStatementData = ohadaReportingService.generateIncomeStatementOHADA(companyId, startDate, endDate);
            
            if (incomeStatementData.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur: " + incomeStatementData.get("error")).getBytes());
            }
            
            byte[] pdfBytes = ohadaPDFExportService.generateIncomeStatementPDF(
                (com.ecomptaia.model.ohada.CompteResultatSYSCOHADA) incomeStatementData.get("compteResultat"),
                companyName,
                startDate,
                endDate
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "compte-resultat-ohada-" + startDate + "-" + endDate + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Exporter le Tableau des Flux de Trésorerie en PDF
     */
    @GetMapping("/cash-flow-statement")
    public ResponseEntity<byte[]> exportCashFlowPDF(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName) {
        
        try {
            Map<String, Object> cashFlowData = ohadaReportingService.generateCashFlowStatementOHADA(companyId, startDate, endDate);
            
            if (cashFlowData.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur: " + cashFlowData.get("error")).getBytes());
            }
            
            byte[] pdfBytes = ohadaPDFExportService.generateCashFlowPDF(
                (com.ecomptaia.model.ohada.TableauFluxSYSCOHADA) cashFlowData.get("tableauFlux"),
                companyName,
                startDate,
                endDate
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "flux-tresorerie-ohada-" + startDate + "-" + endDate + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Exporter les Annexes en PDF
     */
    @GetMapping("/annexes")
    public ResponseEntity<byte[]> exportAnnexesPDF(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName) {
        
        try {
            Map<String, Object> annexesData = ohadaReportingService.generateAnnexesOHADA(companyId, asOfDate);
            
            if (annexesData.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur: " + annexesData.get("error")).getBytes());
            }
            
            byte[] pdfBytes = ohadaPDFExportService.generateAnnexesPDF(
                (com.ecomptaia.model.ohada.AnnexesSYSCOHADA) annexesData.get("annexes"),
                companyName,
                asOfDate
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "annexes-ohada-" + asOfDate + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Exporter tous les états financiers en un seul PDF
     */
    @GetMapping("/complete-financial-statements")
    public ResponseEntity<byte[]> exportCompleteFinancialStatementsPDF(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName) {
        
        try {
            Map<String, Object> allStatementsData = ohadaReportingService.generateAllFinancialStatementsOHADA(companyId, startDate, endDate);
            
            if (allStatementsData.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur: " + allStatementsData.get("error")).getBytes());
            }
            
            byte[] pdfBytes = ohadaPDFExportService.generateCompleteFinancialStatementsPDF(
                (com.ecomptaia.model.ohada.BilanSYSCOHADA) allStatementsData.get("bilan"),
                (com.ecomptaia.model.ohada.CompteResultatSYSCOHADA) allStatementsData.get("compteResultat"),
                (com.ecomptaia.model.ohada.TableauFluxSYSCOHADA) allStatementsData.get("tableauFlux"),
                (com.ecomptaia.model.ohada.AnnexesSYSCOHADA) allStatementsData.get("annexes"),
                companyName,
                startDate,
                endDate
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "etats-financiers-complets-ohada-" + startDate + "-" + endDate + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Exporter le Bilan en PDF (hybride - données réelles + simulées si nécessaire)
     */
    @GetMapping("/balance-sheet-hybrid")
    public ResponseEntity<byte[]> exportBalanceSheetPDFHybrid(
            @RequestParam(required = false) Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName) {
        
        try {
            com.ecomptaia.model.ohada.BilanSYSCOHADA bilan;
            boolean isDemoData = false;
            
            // Si companyId n'est pas fourni ou si on a des problèmes d'accès, utiliser des données simulées
            if (companyId == null) {
                isDemoData = true;
                bilan = new com.ecomptaia.model.ohada.BilanSYSCOHADA();
                
                // Ajout de données simulées
                bilan.affecterValeur("211", new java.math.BigDecimal("50000"), new java.math.BigDecimal("45000")); // Immobilisations incorporelles
                bilan.affecterValeur("213", new java.math.BigDecimal("150000"), new java.math.BigDecimal("140000")); // Immobilisations corporelles
                bilan.affecterValeur("215", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Immobilisations financières
                bilan.affecterValeur("31", new java.math.BigDecimal("75000"), new java.math.BigDecimal("70000")); // Stocks
                bilan.affecterValeur("411", new java.math.BigDecimal("120000"), new java.math.BigDecimal("110000")); // Clients
                bilan.affecterValeur("53", new java.math.BigDecimal("45000"), new java.math.BigDecimal("40000")); // Caisse
                bilan.affecterValeur("101", new java.math.BigDecimal("200000"), new java.math.BigDecimal("180000")); // Capital social
                bilan.affecterValeur("106", new java.math.BigDecimal("50000"), new java.math.BigDecimal("40000")); // Réserves
                bilan.affecterValeur("120", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Résultat de l'exercice
                bilan.affecterValeur("164", new java.math.BigDecimal("80000"), new java.math.BigDecimal("75000")); // Emprunts bancaires
                bilan.affecterValeur("401", new java.math.BigDecimal("90000"), new java.math.BigDecimal("85000")); // Fournisseurs
                bilan.affecterValeur("421", new java.math.BigDecimal("15000"), new java.math.BigDecimal("12000")); // Personnel
                
                bilan.calculerTotaux();
            } else {
                try {
                    // Tentative de récupération des données OHADA réelles
                    Map<String, Object> balanceSheetData = ohadaReportingService.generateBalanceSheetOHADA(companyId, asOfDate);
                    
                    // Vérification si les données réelles sont disponibles
                    if (balanceSheetData.containsKey("error") || !balanceSheetData.containsKey("bilan") || balanceSheetData.get("bilan") == null) {
                        // Utilisation de données simulées
                        isDemoData = true;
                        bilan = new com.ecomptaia.model.ohada.BilanSYSCOHADA();
                        
                        // Ajout de données simulées
                        bilan.affecterValeur("211", new java.math.BigDecimal("50000"), new java.math.BigDecimal("45000")); // Immobilisations incorporelles
                        bilan.affecterValeur("213", new java.math.BigDecimal("150000"), new java.math.BigDecimal("140000")); // Immobilisations corporelles
                        bilan.affecterValeur("215", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Immobilisations financières
                        bilan.affecterValeur("31", new java.math.BigDecimal("75000"), new java.math.BigDecimal("70000")); // Stocks
                        bilan.affecterValeur("411", new java.math.BigDecimal("120000"), new java.math.BigDecimal("110000")); // Clients
                        bilan.affecterValeur("53", new java.math.BigDecimal("45000"), new java.math.BigDecimal("40000")); // Caisse
                        bilan.affecterValeur("101", new java.math.BigDecimal("200000"), new java.math.BigDecimal("180000")); // Capital social
                        bilan.affecterValeur("106", new java.math.BigDecimal("50000"), new java.math.BigDecimal("40000")); // Réserves
                        bilan.affecterValeur("120", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Résultat de l'exercice
                        bilan.affecterValeur("164", new java.math.BigDecimal("80000"), new java.math.BigDecimal("75000")); // Emprunts bancaires
                        bilan.affecterValeur("401", new java.math.BigDecimal("90000"), new java.math.BigDecimal("85000")); // Fournisseurs
                        bilan.affecterValeur("421", new java.math.BigDecimal("15000"), new java.math.BigDecimal("12000")); // Personnel
                        
                        bilan.calculerTotaux();
                    } else {
                        // Utilisation des données réelles
                        bilan = (com.ecomptaia.model.ohada.BilanSYSCOHADA) balanceSheetData.get("bilan");
                    }
                } catch (Exception e) {
                    // En cas d'erreur, utiliser des données simulées
                    isDemoData = true;
                    bilan = new com.ecomptaia.model.ohada.BilanSYSCOHADA();
                    
                    // Ajout de données simulées
                    bilan.affecterValeur("211", new java.math.BigDecimal("50000"), new java.math.BigDecimal("45000")); // Immobilisations incorporelles
                    bilan.affecterValeur("213", new java.math.BigDecimal("150000"), new java.math.BigDecimal("140000")); // Immobilisations corporelles
                    bilan.affecterValeur("215", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Immobilisations financières
                    bilan.affecterValeur("31", new java.math.BigDecimal("75000"), new java.math.BigDecimal("70000")); // Stocks
                    bilan.affecterValeur("411", new java.math.BigDecimal("120000"), new java.math.BigDecimal("110000")); // Clients
                    bilan.affecterValeur("53", new java.math.BigDecimal("45000"), new java.math.BigDecimal("40000")); // Caisse
                    bilan.affecterValeur("101", new java.math.BigDecimal("200000"), new java.math.BigDecimal("180000")); // Capital social
                    bilan.affecterValeur("106", new java.math.BigDecimal("50000"), new java.math.BigDecimal("40000")); // Réserves
                    bilan.affecterValeur("120", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Résultat de l'exercice
                    bilan.affecterValeur("164", new java.math.BigDecimal("80000"), new java.math.BigDecimal("75000")); // Emprunts bancaires
                    bilan.affecterValeur("401", new java.math.BigDecimal("90000"), new java.math.BigDecimal("85000")); // Fournisseurs
                    bilan.affecterValeur("421", new java.math.BigDecimal("15000"), new java.math.BigDecimal("12000")); // Personnel
                    
                    bilan.calculerTotaux();
                }
            }
            
            // Génération du PDF
            byte[] pdfBytes = ohadaPDFExportService.generateBalanceSheetPDF(bilan, companyName, asOfDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = isDemoData ? "bilan-ohada-demo-" + asOfDate + ".pdf" : "bilan-ohada-" + asOfDate + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            String errorMsg = "Erreur lors de la génération du PDF hybride: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMsg.getBytes());
        }
    }

    /**
     * Exporter le Bilan en PDF avec données simulées (pour test)
     */
    @GetMapping("/balance-sheet-demo")
    public ResponseEntity<byte[]> exportBalanceSheetPDFDemo(
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            // Création d'un bilan OHADA simulé pour test
            com.ecomptaia.model.ohada.BilanSYSCOHADA bilanDemo = new com.ecomptaia.model.ohada.BilanSYSCOHADA();
            
            // Ajout de données simulées
            bilanDemo.affecterValeur("211", new java.math.BigDecimal("50000"), new java.math.BigDecimal("45000")); // Immobilisations incorporelles
            bilanDemo.affecterValeur("213", new java.math.BigDecimal("150000"), new java.math.BigDecimal("140000")); // Immobilisations corporelles
            bilanDemo.affecterValeur("215", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Immobilisations financières
            bilanDemo.affecterValeur("31", new java.math.BigDecimal("75000"), new java.math.BigDecimal("70000")); // Stocks
            bilanDemo.affecterValeur("411", new java.math.BigDecimal("120000"), new java.math.BigDecimal("110000")); // Clients
            bilanDemo.affecterValeur("53", new java.math.BigDecimal("45000"), new java.math.BigDecimal("40000")); // Caisse
            bilanDemo.affecterValeur("101", new java.math.BigDecimal("200000"), new java.math.BigDecimal("180000")); // Capital social
            bilanDemo.affecterValeur("106", new java.math.BigDecimal("50000"), new java.math.BigDecimal("40000")); // Réserves
            bilanDemo.affecterValeur("120", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Résultat de l'exercice
            bilanDemo.affecterValeur("164", new java.math.BigDecimal("80000"), new java.math.BigDecimal("75000")); // Emprunts bancaires
            bilanDemo.affecterValeur("401", new java.math.BigDecimal("90000"), new java.math.BigDecimal("85000")); // Fournisseurs
            bilanDemo.affecterValeur("421", new java.math.BigDecimal("15000"), new java.math.BigDecimal("12000")); // Personnel
            
            bilanDemo.calculerTotaux();
            
            // Génération du PDF
            byte[] pdfBytes = ohadaPDFExportService.generateBalanceSheetPDF(bilanDemo, companyName, asOfDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bilan-ohada-demo-" + asOfDate + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            String errorMsg = "Erreur lors de la génération du PDF de démonstration: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMsg.getBytes());
        }
    }

    /**
     * Exporter le Bilan en PDF (test sans companyId)
     */
    @GetMapping("/balance-sheet-test")
    public ResponseEntity<byte[]> exportBalanceSheetPDFTest(
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            // Utilisation directe de données simulées
            com.ecomptaia.model.ohada.BilanSYSCOHADA bilan = new com.ecomptaia.model.ohada.BilanSYSCOHADA();
            
            // Ajout de données simulées
            bilan.affecterValeur("211", new java.math.BigDecimal("50000"), new java.math.BigDecimal("45000")); // Immobilisations incorporelles
            bilan.affecterValeur("213", new java.math.BigDecimal("150000"), new java.math.BigDecimal("140000")); // Immobilisations corporelles
            bilan.affecterValeur("215", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Immobilisations financières
            bilan.affecterValeur("31", new java.math.BigDecimal("75000"), new java.math.BigDecimal("70000")); // Stocks
            bilan.affecterValeur("411", new java.math.BigDecimal("120000"), new java.math.BigDecimal("110000")); // Clients
            bilan.affecterValeur("53", new java.math.BigDecimal("45000"), new java.math.BigDecimal("40000")); // Caisse
            bilan.affecterValeur("101", new java.math.BigDecimal("200000"), new java.math.BigDecimal("180000")); // Capital social
            bilan.affecterValeur("106", new java.math.BigDecimal("50000"), new java.math.BigDecimal("40000")); // Réserves
            bilan.affecterValeur("120", new java.math.BigDecimal("25000"), new java.math.BigDecimal("20000")); // Résultat de l'exercice
            bilan.affecterValeur("164", new java.math.BigDecimal("80000"), new java.math.BigDecimal("75000")); // Emprunts bancaires
            bilan.affecterValeur("401", new java.math.BigDecimal("90000"), new java.math.BigDecimal("85000")); // Fournisseurs
            bilan.affecterValeur("421", new java.math.BigDecimal("15000"), new java.math.BigDecimal("12000")); // Personnel
            
            bilan.calculerTotaux();
            
            // Génération du PDF
            byte[] pdfBytes = ohadaPDFExportService.generateBalanceSheetPDF(bilan, companyName, asOfDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bilan-ohada-test-" + asOfDate + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            String errorMsg = "Erreur lors de la génération du PDF de test: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMsg.getBytes());
        }
    }

    /**
     * Endpoint de debug pour diagnostiquer les données OHADA
     */
    @GetMapping("/debug-ohada-data")
    public ResponseEntity<Map<String, Object>> debugOHADAData(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            Map<String, Object> balanceSheetData = ohadaReportingService.generateBalanceSheetOHADA(companyId, asOfDate);
            
            return ResponseEntity.ok(Map.of(
                "message", "Données OHADA récupérées",
                "hasError", balanceSheetData.containsKey("error"),
                "error", balanceSheetData.get("error"),
                "hasBilan", balanceSheetData.containsKey("bilan"),
                "bilanType", balanceSheetData.get("bilan") != null ? balanceSheetData.get("bilan").getClass().getSimpleName() : "null",
                "dataKeys", balanceSheetData.keySet(),
                "status", "debug_complete"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Erreur lors de la récupération des données OHADA",
                    "error", e.getMessage(),
                    "stackTrace", e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "N/A"
                ));
        }
    }

    /**
     * Endpoint de test simple pour vérifier la génération PDF
     */
    @GetMapping("/test-simple")
    public ResponseEntity<byte[]> testSimplePDF(
            @RequestParam(defaultValue = "E-COMPTA-IA") String companyName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        
        try {
            byte[] pdfBytes = simplePDFService.generateSimpleTestPDF(companyName, asOfDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test-pdf-" + asOfDate + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Endpoint de test simple pour diagnostiquer le problème 403
     */
    @GetMapping("/test-403")
    public ResponseEntity<Map<String, Object>> test403() {
        return ResponseEntity.ok(Map.of(
            "message", "Test 403 - Endpoint accessible",
            "status", "success",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    /**
     * Endpoint de test pour vérifier que le service PDF fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testPDFExportService() {
        return ResponseEntity.ok(Map.of(
            "message", "Service d'export PDF OHADA opérationnel",
            "endpoints", Map.of(
                "test-simple", "GET /api/ohada-pdf/test-simple?companyName=E-COMPTA-IA&asOfDate=2024-12-31",
                "balance-sheet", "GET /api/ohada-pdf/balance-sheet?companyId=1&asOfDate=2024-12-31",
                "income-statement", "GET /api/ohada-pdf/income-statement?companyId=1&startDate=2024-01-01&endDate=2024-12-31",
                "cash-flow-statement", "GET /api/ohada-pdf/cash-flow-statement?companyId=1&startDate=2024-01-01&endDate=2024-12-31",
                "annexes", "GET /api/ohada-pdf/annexes?companyId=1&asOfDate=2024-12-31",
                "complete-financial-statements", "GET /api/ohada-pdf/complete-financial-statements?companyId=1&startDate=2024-01-01&endDate=2024-12-31"
            ),
            "status", "ready"
        ));
    }
}
