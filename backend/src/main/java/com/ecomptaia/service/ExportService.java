package com.ecomptaia.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service d'export et téléchargement des documents comptables
 */
@Service
public class ExportService {

    /**
     * Export du journal général en PDF
     */
    public Map<String, Object> exportGeneralJournalPDF(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Génération des données du journal
            List<Map<String, Object>> journalEntries = generateJournalEntries(companyId, startDate, endDate);
            
            // Calcul des totaux
            BigDecimal totalDebit = journalEntries.stream()
                .map(entry -> (BigDecimal) entry.get("debit"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalCredit = journalEntries.stream()
                .map(entry -> (BigDecimal) entry.get("credit"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Informations du document
            Map<String, Object> documentInfo = new HashMap<>();
            documentInfo.put("documentType", "JOURNAL_GENERAL");
            documentInfo.put("companyId", companyId);
            documentInfo.put("startDate", startDate);
            documentInfo.put("endDate", endDate);
            documentInfo.put("generationDate", LocalDateTime.now());
            documentInfo.put("totalEntries", journalEntries.size());
            documentInfo.put("totalDebit", totalDebit);
            documentInfo.put("totalCredit", totalCredit);
            documentInfo.put("isBalanced", totalDebit.compareTo(totalCredit) == 0);
            
            // Simulation du fichier PDF
            String fileName = "journal_general_" + companyId + "_" + 
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" +
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            
            documentInfo.put("fileName", fileName);
            documentInfo.put("fileSize", "245760"); // Simulation
            documentInfo.put("downloadUrl", "/exports/" + fileName);
            
            result.put("documentInfo", documentInfo);
            result.put("journalEntries", journalEntries);
            result.put("message", "Journal général exporté avec succès");
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de l'export: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Export du grand livre en PDF
     */
    public Map<String, Object> exportGeneralLedgerPDF(Long companyId, LocalDate asOfDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Génération des données du grand livre
            List<Map<String, Object>> ledgerEntries = generateLedgerEntries(companyId, asOfDate);
            
            // Calcul des soldes par compte
            Map<String, Object> accountBalances = calculateAccountBalances(ledgerEntries);
            
            // Informations du document
            Map<String, Object> documentInfo = new HashMap<>();
            documentInfo.put("documentType", "GRAND_LIVRE");
            documentInfo.put("companyId", companyId);
            documentInfo.put("asOfDate", asOfDate);
            documentInfo.put("generationDate", LocalDateTime.now());
            documentInfo.put("totalAccounts", accountBalances.size());
            documentInfo.put("totalEntries", ledgerEntries.size());
            
            // Simulation du fichier PDF
            String fileName = "grand_livre_" + companyId + "_" + 
                asOfDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            
            documentInfo.put("fileName", fileName);
            documentInfo.put("fileSize", "512000"); // Simulation
            documentInfo.put("downloadUrl", "/exports/" + fileName);
            
            result.put("documentInfo", documentInfo);
            result.put("ledgerEntries", ledgerEntries);
            result.put("accountBalances", accountBalances);
            result.put("message", "Grand livre exporté avec succès");
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de l'export: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Export de la balance en PDF
     */
    public Map<String, Object> exportTrialBalancePDF(Long companyId, LocalDate asOfDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Génération des données de la balance
            List<Map<String, Object>> balanceEntries = generateBalanceEntries(companyId, asOfDate);
            
            // Calcul des totaux
            BigDecimal totalDebit = balanceEntries.stream()
                .map(entry -> (BigDecimal) entry.get("debit"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalCredit = balanceEntries.stream()
                .map(entry -> (BigDecimal) entry.get("credit"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Informations du document
            Map<String, Object> documentInfo = new HashMap<>();
            documentInfo.put("documentType", "BALANCE");
            documentInfo.put("companyId", companyId);
            documentInfo.put("asOfDate", asOfDate);
            documentInfo.put("generationDate", LocalDateTime.now());
            documentInfo.put("totalAccounts", balanceEntries.size());
            documentInfo.put("totalDebit", totalDebit);
            documentInfo.put("totalCredit", totalCredit);
            documentInfo.put("isBalanced", totalDebit.compareTo(totalCredit) == 0);
            
            // Simulation du fichier PDF
            String fileName = "balance_" + companyId + "_" + 
                asOfDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            
            documentInfo.put("fileName", fileName);
            documentInfo.put("fileSize", "128000"); // Simulation
            documentInfo.put("downloadUrl", "/exports/" + fileName);
            
            result.put("documentInfo", documentInfo);
            result.put("balanceEntries", balanceEntries);
            result.put("message", "Balance exportée avec succès");
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de l'export: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Export des états financiers en PDF
     */
    public Map<String, Object> exportFinancialStatementsPDF(Long companyId, LocalDate asOfDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Génération des états financiers
            Map<String, Object> balanceSheet = generateBalanceSheet(companyId, asOfDate);
            Map<String, Object> incomeStatement = generateIncomeStatement(companyId, asOfDate);
            Map<String, Object> cashFlowStatement = generateCashFlowStatement(companyId, asOfDate);
            
            // Informations du document
            Map<String, Object> documentInfo = new HashMap<>();
            documentInfo.put("documentType", "ETATS_FINANCIERS");
            documentInfo.put("companyId", companyId);
            documentInfo.put("asOfDate", asOfDate);
            documentInfo.put("generationDate", LocalDateTime.now());
            documentInfo.put("includesBalanceSheet", true);
            documentInfo.put("includesIncomeStatement", true);
            documentInfo.put("includesCashFlowStatement", true);
            
            // Simulation du fichier PDF
            String fileName = "etats_financiers_" + companyId + "_" + 
                asOfDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            
            documentInfo.put("fileName", fileName);
            documentInfo.put("fileSize", "1024000"); // Simulation
            documentInfo.put("downloadUrl", "/exports/" + fileName);
            
            result.put("documentInfo", documentInfo);
            result.put("balanceSheet", balanceSheet);
            result.put("incomeStatement", incomeStatement);
            result.put("cashFlowStatement", cashFlowStatement);
            result.put("message", "États financiers exportés avec succès");
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de l'export: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Export en Excel
     */
    public Map<String, Object> exportToExcel(String documentType, Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Génération des données selon le type
            List<Map<String, Object>> data = new ArrayList<>();
            
            switch (documentType.toUpperCase()) {
                case "JOURNAL":
                    data = generateJournalEntries(companyId, startDate, endDate);
                    break;
                case "GRAND_LIVRE":
                    data = generateLedgerEntries(companyId, endDate);
                    break;
                case "BALANCE":
                    data = generateBalanceEntries(companyId, endDate);
                    break;
                default:
                    throw new IllegalArgumentException("Type de document non supporté: " + documentType);
            }
            
            // Informations du document
            Map<String, Object> documentInfo = new HashMap<>();
            documentInfo.put("documentType", documentType + "_EXCEL");
            documentInfo.put("companyId", companyId);
            documentInfo.put("startDate", startDate);
            documentInfo.put("endDate", endDate);
            documentInfo.put("generationDate", LocalDateTime.now());
            documentInfo.put("totalRows", data.size());
            
            // Simulation du fichier Excel
            String fileName = documentType.toLowerCase() + "_" + companyId + "_" + 
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" +
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            
            documentInfo.put("fileName", fileName);
            documentInfo.put("fileSize", "128000"); // Simulation
            documentInfo.put("downloadUrl", "/exports/" + fileName);
            
            result.put("documentInfo", documentInfo);
            result.put("data", data);
            result.put("message", "Export Excel réussi");
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de l'export Excel: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Validation qualité avant export
     */
    public Map<String, Object> validateExportQuality(Long companyId, String documentType, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<String> warnings = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            // Vérifications de base
            if (startDate.isAfter(endDate)) {
                errors.add("La date de début ne peut pas être postérieure à la date de fin");
            }
            
            if (startDate.isBefore(LocalDate.of(2020, 1, 1))) {
                warnings.add("La période d'export commence avant 2020");
            }
            
            // Vérifications spécifiques selon le type
            switch (documentType.toUpperCase()) {
                case "JOURNAL":
                    // Vérification de l'équilibre du journal
                    List<Map<String, Object>> journalEntries = generateJournalEntries(companyId, startDate, endDate);
                    BigDecimal totalDebit = journalEntries.stream()
                        .map(entry -> (BigDecimal) entry.get("debit"))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalCredit = journalEntries.stream()
                        .map(entry -> (BigDecimal) entry.get("credit"))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    if (totalDebit.compareTo(totalCredit) != 0) {
                        errors.add("Le journal n'est pas équilibré (Débit: " + totalDebit + ", Crédit: " + totalCredit + ")");
                    }
                    break;
                    
                case "GRAND_LIVRE":
                    // Vérification de la cohérence des comptes
                    List<Map<String, Object>> ledgerEntries = generateLedgerEntries(companyId, endDate);
                    if (ledgerEntries.isEmpty()) {
                        warnings.add("Aucune écriture trouvée pour la période");
                    }
                    break;
                    
                case "BALANCE":
                    // Vérification de l'équilibre de la balance
                    List<Map<String, Object>> balanceEntries = generateBalanceEntries(companyId, endDate);
                    BigDecimal balanceDebit = balanceEntries.stream()
                        .map(entry -> (BigDecimal) entry.get("debit"))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal balanceCredit = balanceEntries.stream()
                        .map(entry -> (BigDecimal) entry.get("credit"))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    if (balanceDebit.compareTo(balanceCredit) != 0) {
                        errors.add("La balance n'est pas équilibrée (Débit: " + balanceDebit + ", Crédit: " + balanceCredit + ")");
                    }
                    break;
            }
            
            result.put("warnings", warnings);
            result.put("errors", errors);
            result.put("isValid", errors.isEmpty());
            result.put("hasWarnings", !warnings.isEmpty());
            result.put("message", errors.isEmpty() ? "Validation réussie" : "Validation échouée");
            result.put("status", errors.isEmpty() ? "SUCCESS" : "ERROR");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la validation: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    // Méthodes utilitaires pour générer les données
    private List<Map<String, Object>> generateJournalEntries(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> entries = new ArrayList<>();
        
        // Simulation d'écritures de journal
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("entryNumber", "ECR-" + companyId + "-" + i);
            entry.put("entryDate", startDate.plusDays(i));
            entry.put("accountNumber", "401" + i);
            entry.put("accountLabel", "Compte " + i);
            entry.put("description", "Écriture " + i);
            entry.put("debit", new BigDecimal("1000.00").multiply(new BigDecimal(i)));
            entry.put("credit", BigDecimal.ZERO);
            entry.put("reference", "REF-" + i);
            
            entries.add(entry);
        }
        
        return entries;
    }

    private List<Map<String, Object>> generateLedgerEntries(Long companyId, LocalDate asOfDate) {
        List<Map<String, Object>> entries = new ArrayList<>();
        
        // Simulation d'écritures de grand livre
        for (int i = 1; i <= 15; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("accountNumber", "401" + i);
            entry.put("accountLabel", "Compte " + i);
            entry.put("openingBalance", new BigDecimal("5000.00"));
            entry.put("debit", new BigDecimal("1000.00").multiply(new BigDecimal(i)));
            entry.put("credit", new BigDecimal("500.00").multiply(new BigDecimal(i)));
            entry.put("closingBalance", new BigDecimal("5500.00").multiply(new BigDecimal(i)));
            
            entries.add(entry);
        }
        
        return entries;
    }

    private List<Map<String, Object>> generateBalanceEntries(Long companyId, LocalDate asOfDate) {
        List<Map<String, Object>> entries = new ArrayList<>();
        
        // Simulation d'écritures de balance
        for (int i = 1; i <= 20; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("accountNumber", "401" + i);
            entry.put("accountLabel", "Compte " + i);
            entry.put("debit", new BigDecimal("1000.00").multiply(new BigDecimal(i)));
            entry.put("credit", new BigDecimal("500.00").multiply(new BigDecimal(i)));
            entry.put("balance", new BigDecimal("500.00").multiply(new BigDecimal(i)));
            
            entries.add(entry);
        }
        
        return entries;
    }

    private Map<String, Object> calculateAccountBalances(List<Map<String, Object>> ledgerEntries) {
        Map<String, Object> balances = new HashMap<>();
        
        for (Map<String, Object> entry : ledgerEntries) {
            String accountNumber = (String) entry.get("accountNumber");
            BigDecimal closingBalance = (BigDecimal) entry.get("closingBalance");
            balances.put(accountNumber, closingBalance);
        }
        
        return balances;
    }

    private Map<String, Object> generateBalanceSheet(Long companyId, LocalDate asOfDate) {
        Map<String, Object> balanceSheet = new HashMap<>();
        balanceSheet.put("totalAssets", new BigDecimal("1500000.00"));
        balanceSheet.put("totalLiabilities", new BigDecimal("800000.00"));
        balanceSheet.put("totalEquity", new BigDecimal("700000.00"));
        balanceSheet.put("asOfDate", asOfDate);
        return balanceSheet;
    }

    private Map<String, Object> generateIncomeStatement(Long companyId, LocalDate asOfDate) {
        Map<String, Object> incomeStatement = new HashMap<>();
        incomeStatement.put("totalRevenue", new BigDecimal("2000000.00"));
        incomeStatement.put("totalExpenses", new BigDecimal("1500000.00"));
        incomeStatement.put("netIncome", new BigDecimal("500000.00"));
        incomeStatement.put("asOfDate", asOfDate);
        return incomeStatement;
    }

    private Map<String, Object> generateCashFlowStatement(Long companyId, LocalDate asOfDate) {
        Map<String, Object> cashFlowStatement = new HashMap<>();
        cashFlowStatement.put("operatingCashFlow", new BigDecimal("400000.00"));
        cashFlowStatement.put("investingCashFlow", new BigDecimal("-100000.00"));
        cashFlowStatement.put("financingCashFlow", new BigDecimal("-200000.00"));
        cashFlowStatement.put("netCashFlow", new BigDecimal("100000.00"));
        cashFlowStatement.put("asOfDate", asOfDate);
        return cashFlowStatement;
    }
}
