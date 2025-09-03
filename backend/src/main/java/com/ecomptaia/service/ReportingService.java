package com.ecomptaia.service;

import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.repository.AccountEntryRepository;
import com.ecomptaia.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class ReportingService {

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    /**
     * Générer le Grand Livre (conforme SYSCOHADA)
     */
    public Map<String, Object> generateGeneralLedger(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> ledger = new HashMap<>();
        
        // Récupérer toutes les écritures validées de la période
        List<JournalEntry> entries = journalEntryRepository.findValidatedEntriesByDateRange(companyId, startDate, endDate);
        
        // Grouper par compte
        Map<String, List<AccountEntry>> entriesByAccount = new HashMap<>();
        
        for (JournalEntry entry : entries) {
            List<AccountEntry> accountEntries = accountEntryRepository.findByJournalEntryId(entry.getId());
            for (AccountEntry accountEntry : accountEntries) {
                String accountNumber = accountEntry.getAccountNumber();
                entriesByAccount.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(accountEntry);
            }
        }
        
        // Calculer les soldes par compte selon SYSCOHADA
        Map<String, Map<String, Object>> accountBalances = new HashMap<>();
        
        for (Map.Entry<String, List<AccountEntry>> entry : entriesByAccount.entrySet()) {
            String accountNumber = entry.getKey();
            List<AccountEntry> accountEntries = entry.getValue();
            
            // Trier par date de création
            accountEntries.sort(Comparator.comparing(AccountEntry::getCreatedAt));
            
            BigDecimal totalDebit = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            BigDecimal soldePrecedent = BigDecimal.ZERO;
            
            // Calculer le solde selon la classe de compte SYSCOHADA
            String accountClass = accountNumber.substring(0, 1);
            boolean isDebitAccount = isDebitAccountClass(accountClass);
            
            for (AccountEntry accountEntry : accountEntries) {
                if ("DEBIT".equals(accountEntry.getAccountType())) {
                    totalDebit = totalDebit.add(accountEntry.getAmount());
                } else if ("CREDIT".equals(accountEntry.getAccountType())) {
                    totalCredit = totalCredit.add(accountEntry.getAmount());
                }
            }
            
            // Calcul du solde selon SYSCOHADA
            BigDecimal soldeFinal;
            if (isDebitAccount) {
                soldeFinal = soldePrecedent.add(totalDebit).subtract(totalCredit);
            } else {
                soldeFinal = soldePrecedent.add(totalCredit).subtract(totalDebit);
            }
            
            Map<String, Object> accountBalance = new HashMap<>();
            accountBalance.put("accountNumber", accountNumber);
            accountBalance.put("accountName", accountEntries.get(0).getAccountName());
            accountBalance.put("accountClass", accountClass);
            accountBalance.put("soldePrecedent", soldePrecedent);
            accountBalance.put("totalDebit", totalDebit);
            accountBalance.put("totalCredit", totalCredit);
            accountBalance.put("soldeFinal", soldeFinal);
            accountBalance.put("isDebitAccount", isDebitAccount);
            accountBalance.put("entries", accountEntries);
            accountBalance.put("nombreEcritures", accountEntries.size());
            
            accountBalances.put(accountNumber, accountBalance);
        }
        
        // Organiser par classe de compte SYSCOHADA
        Map<String, Map<String, Object>> accountsByClass = organizeByAccountClass(accountBalances);
        
        ledger.put("companyId", companyId);
        ledger.put("startDate", startDate);
        ledger.put("endDate", endDate);
        ledger.put("accountsByClass", accountsByClass);
        ledger.put("totalAccounts", accountBalances.size());
        ledger.put("totalEntries", entries.size());
        ledger.put("generatedAt", LocalDateTime.now());
        
        return ledger;
    }

    /**
     * Générer la Balance Générale (conforme SYSCOHADA)
     */
    public Map<String, Object> generateTrialBalance(Long companyId, LocalDate asOfDate) {
        Map<String, Object> trialBalance = new HashMap<>();
        
        // Récupérer toutes les écritures validées jusqu'à la date
        List<JournalEntry> entries = journalEntryRepository.findValidatedEntriesByDateRange(
            companyId, LocalDate.of(1900, 1, 1), asOfDate);
        
        // Grouper par compte et calculer les soldes
        Map<String, BigDecimal> accountBalances = new HashMap<>();
        Map<String, String> accountNames = new HashMap<>();
        Map<String, String> accountClasses = new HashMap<>();
        
        for (JournalEntry entry : entries) {
            List<AccountEntry> accountEntries = accountEntryRepository.findByJournalEntryId(entry.getId());
            for (AccountEntry accountEntry : accountEntries) {
                String accountNumber = accountEntry.getAccountNumber();
                accountNames.put(accountNumber, accountEntry.getAccountName());
                accountClasses.put(accountNumber, accountNumber.substring(0, 1));
                
                BigDecimal currentBalance = accountBalances.getOrDefault(accountNumber, BigDecimal.ZERO);
                if ("DEBIT".equals(accountEntry.getAccountType())) {
                    currentBalance = currentBalance.add(accountEntry.getAmount());
                } else if ("CREDIT".equals(accountEntry.getAccountType())) {
                    currentBalance = currentBalance.subtract(accountEntry.getAmount());
                }
                accountBalances.put(accountNumber, currentBalance);
            }
        }
        
        // Organiser par classe de compte SYSCOHADA
        Map<String, List<Map<String, Object>>> accountsByClass = new HashMap<>();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        
        for (Map.Entry<String, BigDecimal> entry : accountBalances.entrySet()) {
            String accountNumber = entry.getKey();
            BigDecimal balance = entry.getValue();
            String accountName = accountNames.get(accountNumber);
            String accountClass = accountClasses.get(accountNumber);
            
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("accountNumber", accountNumber);
            accountInfo.put("accountName", accountName);
            accountInfo.put("accountClass", accountClass);
            accountInfo.put("debit", balance.compareTo(BigDecimal.ZERO) > 0 ? balance : BigDecimal.ZERO);
            accountInfo.put("credit", balance.compareTo(BigDecimal.ZERO) < 0 ? balance.abs() : BigDecimal.ZERO);
            accountInfo.put("solde", balance);
            accountInfo.put("isDebitAccount", isDebitAccountClass(accountClass));
            
            accountsByClass.computeIfAbsent(accountClass, k -> new ArrayList<>()).add(accountInfo);
            
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                totalDebit = totalDebit.add(balance);
            } else {
                totalCredit = totalCredit.add(balance.abs());
            }
        }
        
        // Trier les comptes par numéro dans chaque classe
        for (List<Map<String, Object>> accounts : accountsByClass.values()) {
            accounts.sort(Comparator.comparing(a -> (String) a.get("accountNumber")));
        }
        
        // Calculer les totaux par classe
        Map<String, Map<String, Object>> totalsByClass = calculateTotalsByClass(accountsByClass);
        
        trialBalance.put("companyId", companyId);
        trialBalance.put("asOfDate", asOfDate);
        trialBalance.put("accountsByClass", accountsByClass);
        trialBalance.put("totalsByClass", totalsByClass);
        trialBalance.put("totalDebit", totalDebit);
        trialBalance.put("totalCredit", totalCredit);
        trialBalance.put("difference", totalDebit.subtract(totalCredit));
        trialBalance.put("isBalanced", totalDebit.compareTo(totalCredit) == 0);
        trialBalance.put("generatedAt", LocalDateTime.now());
        
        return trialBalance;
    }

    /**
     * Générer le Bilan (conforme SYSCOHADA)
     */
    public Map<String, Object> generateBalanceSheet(Long companyId, LocalDate asOfDate) {
        Map<String, Object> balanceSheet = new HashMap<>();
        
        // Récupérer la balance générale
        Map<String, Object> trialBalance = generateTrialBalance(companyId, asOfDate);
        
        // Organiser par catégorie SYSCOHADA
        Map<String, BigDecimal> actifImmobilise = new HashMap<>();
        Map<String, BigDecimal> actifCirculant = new HashMap<>();
        Map<String, BigDecimal> tresorerieActif = new HashMap<>();
        Map<String, BigDecimal> capitauxPropres = new HashMap<>();
        Map<String, BigDecimal> dettesFinancieres = new HashMap<>();
        Map<String, BigDecimal> passifCirculant = new HashMap<>();
        Map<String, BigDecimal> tresoreriePassif = new HashMap<>();
        
        Object accountsByClassObj = trialBalance.get("accountsByClass");
        if (!(accountsByClassObj instanceof Map)) {
            throw new IllegalArgumentException("Format de données invalide pour accountsByClass");
        }
        @SuppressWarnings("unchecked")
        Map<String, List<Map<String, Object>>> accountsByClass = 
            (Map<String, List<Map<String, Object>>>) accountsByClassObj;
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : accountsByClass.entrySet()) {
            String accountClass = entry.getKey();
            List<Map<String, Object>> accounts = entry.getValue();
            
            BigDecimal classTotal = BigDecimal.ZERO;
            for (Map<String, Object> account : accounts) {
                BigDecimal solde = (BigDecimal) account.get("solde");
                classTotal = classTotal.add(solde);
            }
            
            // Classification selon SYSCOHADA
            switch (accountClass) {
                case "1": // Comptes de capitaux
                    capitauxPropres.put("Classe 1 - Capitaux", classTotal);
                    break;
                case "2": // Comptes d'immobilisations
                    actifImmobilise.put("Classe 2 - Immobilisations", classTotal);
                    break;
                case "3": // Comptes de stocks et en-cours
                    actifCirculant.put("Classe 3 - Stocks", classTotal);
                    break;
                case "4": // Comptes de tiers
                    // Déterminer si c'est actif ou passif selon le solde
                    if (classTotal.compareTo(BigDecimal.ZERO) > 0) {
                        actifCirculant.put("Classe 4 - Créances", classTotal);
                    } else {
                        passifCirculant.put("Classe 4 - Dettes", classTotal.abs());
                    }
                    break;
                case "5": // Comptes financiers
                    tresorerieActif.put("Classe 5 - Trésorerie", classTotal);
                    break;
                case "6": // Comptes de charges
                case "7": // Comptes de produits
                    // Ces comptes ne sont pas au bilan mais au compte de résultat
                    break;
            }
        }
        
        BigDecimal totalActif = actifImmobilise.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add)
            .add(actifCirculant.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
            .add(tresorerieActif.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
            
        BigDecimal totalPassif = capitauxPropres.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add)
            .add(dettesFinancieres.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
            .add(passifCirculant.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
            .add(tresoreriePassif.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        
        balanceSheet.put("companyId", companyId);
        balanceSheet.put("asOfDate", asOfDate);
        balanceSheet.put("actifImmobilise", actifImmobilise);
        balanceSheet.put("actifCirculant", actifCirculant);
        balanceSheet.put("tresorerieActif", tresorerieActif);
        balanceSheet.put("capitauxPropres", capitauxPropres);
        balanceSheet.put("dettesFinancieres", dettesFinancieres);
        balanceSheet.put("passifCirculant", passifCirculant);
        balanceSheet.put("tresoreriePassif", tresoreriePassif);
        balanceSheet.put("totalActif", totalActif);
        balanceSheet.put("totalPassif", totalPassif);
        balanceSheet.put("isBalanced", totalActif.compareTo(totalPassif) == 0);
        balanceSheet.put("generatedAt", LocalDateTime.now());
        
        return balanceSheet;
    }

    /**
     * Générer le Compte de Résultat (conforme SYSCOHADA)
     */
    public Map<String, Object> generateIncomeStatement(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> incomeStatement = new HashMap<>();
        
        // Récupérer les écritures de la période
        List<JournalEntry> entries = journalEntryRepository.findValidatedEntriesByDateRange(companyId, startDate, endDate);
        
        Map<String, BigDecimal> produits = new HashMap<>();
        Map<String, BigDecimal> charges = new HashMap<>();
        
        for (JournalEntry entry : entries) {
            List<AccountEntry> accountEntries = accountEntryRepository.findByJournalEntryId(entry.getId());
            for (AccountEntry accountEntry : accountEntries) {
                String accountNumber = accountEntry.getAccountNumber();
                String accountClass = accountNumber.substring(0, 1);
                
                BigDecimal amount = accountEntry.getAmount();
                if ("DEBIT".equals(accountEntry.getAccountType())) {
                    amount = amount.negate();
                }
                
                // Classification selon SYSCOHADA
                if ("6".equals(accountClass)) { // Charges
                    charges.put(accountEntry.getAccountName(), 
                        charges.getOrDefault(accountEntry.getAccountName(), BigDecimal.ZERO).add(amount.abs()));
                } else if ("7".equals(accountClass)) { // Produits
                    produits.put(accountEntry.getAccountName(), 
                        produits.getOrDefault(accountEntry.getAccountName(), BigDecimal.ZERO).add(amount));
                }
            }
        }
        
        BigDecimal totalProduits = produits.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCharges = charges.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal resultatNet = totalProduits.subtract(totalCharges);
        
        incomeStatement.put("companyId", companyId);
        incomeStatement.put("startDate", startDate);
        incomeStatement.put("endDate", endDate);
        incomeStatement.put("produits", produits);
        incomeStatement.put("charges", charges);
        incomeStatement.put("totalProduits", totalProduits);
        incomeStatement.put("totalCharges", totalCharges);
        incomeStatement.put("resultatNet", resultatNet);
        incomeStatement.put("isProfitable", resultatNet.compareTo(BigDecimal.ZERO) > 0);
        incomeStatement.put("generatedAt", LocalDateTime.now());
        
        return incomeStatement;
    }

    /**
     * Déterminer si une classe de compte est de type débit selon SYSCOHADA
     */
    private boolean isDebitAccountClass(String accountClass) {
        // Classes 1, 2, 3, 4, 5 sont de type débit (actif)
        // Classes 6, 7 sont de type crédit (charges/produits)
        return "12345".contains(accountClass);
    }

    /**
     * Organiser les comptes par classe SYSCOHADA
     */
    private Map<String, Map<String, Object>> organizeByAccountClass(Map<String, Map<String, Object>> accountBalances) {
        Map<String, Map<String, Object>> accountsByClass = new HashMap<>();
        
        for (Map.Entry<String, Map<String, Object>> entry : accountBalances.entrySet()) {
            String accountNumber = entry.getKey();
            Map<String, Object> accountData = entry.getValue();
            String accountClass = accountNumber.substring(0, 1);
            
            accountsByClass.computeIfAbsent(accountClass, k -> new HashMap<>()).put(accountNumber, accountData);
        }
        
        return accountsByClass;
    }

    /**
     * Calculer les totaux par classe de compte
     */
    private Map<String, Map<String, Object>> calculateTotalsByClass(Map<String, List<Map<String, Object>>> accountsByClass) {
        Map<String, Map<String, Object>> totalsByClass = new HashMap<>();
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : accountsByClass.entrySet()) {
            String accountClass = entry.getKey();
            List<Map<String, Object>> accounts = entry.getValue();
            
            BigDecimal totalDebit = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            BigDecimal totalSolde = BigDecimal.ZERO;
            
            for (Map<String, Object> account : accounts) {
                totalDebit = totalDebit.add((BigDecimal) account.get("debit"));
                totalCredit = totalCredit.add((BigDecimal) account.get("credit"));
                totalSolde = totalSolde.add((BigDecimal) account.get("solde"));
            }
            
            Map<String, Object> classTotal = new HashMap<>();
            classTotal.put("totalDebit", totalDebit);
            classTotal.put("totalCredit", totalCredit);
            classTotal.put("totalSolde", totalSolde);
            classTotal.put("nombreComptes", accounts.size());
            
            totalsByClass.put(accountClass, classTotal);
        }
        
        return totalsByClass;
    }

    /**
     * Statistiques de reporting
     */
    public Map<String, Object> getReportingStatistics(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(today.getYear(), 1, 1);
        
        // Balance générale au 31/12 de l'année précédente
        Map<String, Object> lastYearBalance = generateTrialBalance(companyId, 
            LocalDate.of(today.getYear() - 1, 12, 31));
        
        // Balance générale actuelle
        Map<String, Object> currentBalance = generateTrialBalance(companyId, today);
        
        // Compte de résultat de l'année
        Map<String, Object> incomeStatement = generateIncomeStatement(companyId, startOfYear, today);
        
        stats.put("lastYearBalance", lastYearBalance);
        stats.put("currentBalance", currentBalance);
        stats.put("incomeStatement", incomeStatement);
        stats.put("generatedAt", LocalDateTime.now());
        
        return stats;
    }
}
