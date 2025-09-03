package com.ecomptaia.service;

import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.model.ohada.*;
import com.ecomptaia.repository.AccountEntryRepository;
import com.ecomptaia.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de reporting conforme aux standards OHADA/SYSCOHADA
 */
@Service
public class OHADAReportingService {

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private OHADAMappingService ohadaMappingService;

    /**
     * Générer le Grand Livre OHADA
     */
    public Map<String, Object> generateGeneralLedgerOHADA(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> ledger = new HashMap<>();
        
        // Récupérer toutes les écritures validées de la période
        List<JournalEntry> entries = journalEntryRepository.findValidatedEntriesByDateRange(companyId, startDate, endDate);
        
        // Grouper par compte
        Map<String, List<AccountEntry>> entriesByAccount = new HashMap<>();
        
        for (JournalEntry entry : entries) {
            List<AccountEntry> accountEntries = accountEntryRepository.findByJournalEntryId(entry.getId());
            for (AccountEntry accountEntry : accountEntries) {
                String accountNumber = accountEntry.getAccountNumber();
                entriesByAccount.computeIfAbsent(accountNumber, k -> new java.util.ArrayList<>()).add(accountEntry);
            }
        }
        
        // Calculer les soldes par compte
        Map<String, BigDecimal> accountBalances = new HashMap<>();
        Map<String, String> accountNames = new HashMap<>();
        
        for (Map.Entry<String, List<AccountEntry>> entry : entriesByAccount.entrySet()) {
            String accountNumber = entry.getKey();
            List<AccountEntry> accountEntries = entry.getValue();
            
            BigDecimal totalDebit = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            
            for (AccountEntry accountEntry : accountEntries) {
                if ("DEBIT".equals(accountEntry.getAccountType())) {
                    totalDebit = totalDebit.add(accountEntry.getAmount());
                } else if ("CREDIT".equals(accountEntry.getAccountType())) {
                    totalCredit = totalCredit.add(accountEntry.getAmount());
                }
                
                if (accountNames.get(accountNumber) == null) {
                    accountNames.put(accountNumber, accountEntry.getAccountName());
                }
            }
            
            // Calcul du solde selon la classe de compte SYSCOHADA
            String accountClass = accountNumber.substring(0, 1);
            boolean isDebitAccount = isDebitAccountClass(accountClass);
            
            BigDecimal solde;
            if (isDebitAccount) {
                solde = totalDebit.subtract(totalCredit);
            } else {
                solde = totalCredit.subtract(totalDebit);
            }
            
            accountBalances.put(accountNumber, solde);
        }
        
        // Organiser par classe de compte SYSCOHADA
        Map<String, List<Map<String, Object>>> accountsByClass = organizeByAccountClass(accountBalances, accountNames);
        
        ledger.put("companyId", companyId);
        ledger.put("startDate", startDate);
        ledger.put("endDate", endDate);
        ledger.put("accountsByClass", accountsByClass);
        ledger.put("totalAccounts", accountBalances.size());
        ledger.put("totalEntries", entries.size());
        ledger.put("generatedAt", LocalDateTime.now());
        ledger.put("standard", "OHADA/SYSCOHADA");
        
        return ledger;
    }

    /**
     * Générer le Bilan OHADA
     */
    public Map<String, Object> generateBalanceSheetOHADA(Long companyId, LocalDate asOfDate) {
        // Récupérer les soldes des comptes
        Map<String, BigDecimal> soldesComptes = getAccountBalances(companyId, asOfDate);
        
        // Générer le bilan OHADA
        BilanSYSCOHADA bilan = ohadaMappingService.genererBilanOHADA(soldesComptes);
        
        // Convertir en Map pour la réponse JSON
        Map<String, Object> balanceSheet = new HashMap<>();
        balanceSheet.put("companyId", companyId);
        balanceSheet.put("asOfDate", asOfDate);
        balanceSheet.put("standard", "OHADA/SYSCOHADA");
        balanceSheet.put("actifImmobilise", convertPostesToMap(bilan.getActifImmobilise()));
        balanceSheet.put("actifCirculant", convertPostesToMap(bilan.getActifCirculant()));
        balanceSheet.put("tresorerieActif", convertPostesToMap(bilan.getTresorerieActif()));
        balanceSheet.put("capitauxPropres", convertPostesToMap(bilan.getCapitauxPropres()));
        balanceSheet.put("dettesFinancieres", convertPostesToMap(bilan.getDettesFinancieres()));
        balanceSheet.put("passifCirculant", convertPostesToMap(bilan.getPassifCirculant()));
        balanceSheet.put("tresoreriePassif", convertPostesToMap(bilan.getTresoreriePassif()));
        
        // Totaux
        balanceSheet.put("totalActifImmobilise", bilan.getTotalActifImmobilise());
        balanceSheet.put("totalActifCirculant", bilan.getTotalActifCirculant());
        balanceSheet.put("totalTresorerieActif", bilan.getTotalTresorerieActif());
        balanceSheet.put("totalActif", bilan.getTotalActif());
        balanceSheet.put("totalCapitauxPropres", bilan.getTotalCapitauxPropres());
        balanceSheet.put("totalDettesFinancieres", bilan.getTotalDettesFinancieres());
        balanceSheet.put("totalPassifCirculant", bilan.getTotalPassifCirculant());
        balanceSheet.put("totalTresoreriePassif", bilan.getTotalTresoreriePassif());
        balanceSheet.put("totalPassif", bilan.getTotalPassif());
        balanceSheet.put("isBalanced", bilan.getTotalActif().compareTo(bilan.getTotalPassif()) == 0);
        balanceSheet.put("generatedAt", LocalDateTime.now());
        
        return balanceSheet;
    }

    /**
     * Générer le Compte de Résultat OHADA
     */
    public Map<String, Object> generateIncomeStatementOHADA(Long companyId, LocalDate startDate, LocalDate endDate) {
        // Récupérer les soldes des comptes de la période
        Map<String, BigDecimal> soldesComptes = getAccountBalancesForPeriod(companyId, startDate, endDate);
        
        // Générer le compte de résultat OHADA
        CompteResultatSYSCOHADA compteResultat = ohadaMappingService.genererCompteResultatOHADA(soldesComptes);
        
        // Convertir en Map pour la réponse JSON
        Map<String, Object> incomeStatement = new HashMap<>();
        incomeStatement.put("companyId", companyId);
        incomeStatement.put("startDate", startDate);
        incomeStatement.put("endDate", endDate);
        incomeStatement.put("standard", "OHADA/SYSCOHADA");
        incomeStatement.put("produits", convertPostesToMap(compteResultat.getProduits()));
        incomeStatement.put("charges", convertPostesToMap(compteResultat.getCharges()));
        
        // Soldes intermédiaires de gestion (SIG)
        incomeStatement.put("margeBrute", compteResultat.getMargeBrute());
        incomeStatement.put("valeurAjoutee", compteResultat.getValeurAjoutee());
        incomeStatement.put("excedentBrutExploitation", compteResultat.getExcedentBrutExploitation());
        incomeStatement.put("resultatExploitation", compteResultat.getResultatExploitation());
        incomeStatement.put("resultatFinancier", compteResultat.getResultatFinancier());
        incomeStatement.put("resultatExceptionnel", compteResultat.getResultatExceptionnel());
        incomeStatement.put("resultatAvantImpot", compteResultat.getResultatAvantImpot());
        incomeStatement.put("resultatNet", compteResultat.getResultatNet());
        
        // Totaux
        incomeStatement.put("totalProduits", compteResultat.getTotalProduits());
        incomeStatement.put("totalCharges", compteResultat.getTotalCharges());
        incomeStatement.put("isProfitable", compteResultat.getResultatNet().compareTo(BigDecimal.ZERO) > 0);
        incomeStatement.put("generatedAt", LocalDateTime.now());
        
        return incomeStatement;
    }

    /**
     * Générer le Tableau des Flux de Trésorerie OHADA
     */
    public Map<String, Object> generateCashFlowStatementOHADA(Long companyId, LocalDate startDate, LocalDate endDate) {
        // Récupérer les données nécessaires
        Map<String, BigDecimal> soldesComptes = getAccountBalancesForPeriod(companyId, startDate, endDate);
        CompteResultatSYSCOHADA compteResultat = ohadaMappingService.genererCompteResultatOHADA(soldesComptes);
        
        // Générer les bilans de début et fin de période
        BilanSYSCOHADA bilanDebut = ohadaMappingService.genererBilanOHADA(getAccountBalances(companyId, startDate.minusDays(1)));
        BilanSYSCOHADA bilanFin = ohadaMappingService.genererBilanOHADA(getAccountBalances(companyId, endDate));
        
        // Générer le tableau des flux de trésorerie
        TableauFluxSYSCOHADA tableauFlux = ohadaMappingService.genererTableauFluxOHADA(compteResultat, bilanFin, bilanDebut);
        
        // Convertir en Map pour la réponse JSON
        Map<String, Object> cashFlowStatement = new HashMap<>();
        cashFlowStatement.put("companyId", companyId);
        cashFlowStatement.put("startDate", startDate);
        cashFlowStatement.put("endDate", endDate);
        cashFlowStatement.put("standard", "OHADA/SYSCOHADA");
        cashFlowStatement.put("fluxActivite", convertPostesToMap(tableauFlux.getFluxActivite()));
        cashFlowStatement.put("fluxInvestissement", convertPostesToMap(tableauFlux.getFluxInvestissement()));
        cashFlowStatement.put("fluxFinancement", convertPostesToMap(tableauFlux.getFluxFinancement()));
        
        // Totaux
        cashFlowStatement.put("totalFluxActivite", tableauFlux.getTotalFluxActivite());
        cashFlowStatement.put("totalFluxInvestissement", tableauFlux.getTotalFluxInvestissement());
        cashFlowStatement.put("totalFluxFinancement", tableauFlux.getTotalFluxFinancement());
        cashFlowStatement.put("variationTresorerie", tableauFlux.getVariationTresorerie());
        cashFlowStatement.put("tresorerieDebut", tableauFlux.getTresorerieDebut());
        cashFlowStatement.put("tresorerieFin", tableauFlux.getTresorerieFin());
        cashFlowStatement.put("generatedAt", LocalDateTime.now());
        
        return cashFlowStatement;
    }

    /**
     * Générer les Annexes OHADA
     */
    public Map<String, Object> generateAnnexesOHADA(Long companyId, LocalDate asOfDate) {
        // Récupérer les données nécessaires
        Map<String, BigDecimal> soldesComptes = getAccountBalances(companyId, asOfDate);
        Map<String, Object> donnees = new HashMap<>();
        donnees.put("soldesComptes", soldesComptes);
        donnees.put("asOfDate", asOfDate);
        donnees.put("companyId", companyId);
        
        // Générer les annexes
        AnnexesSYSCOHADA annexes = ohadaMappingService.genererAnnexesOHADA(donnees);
        
        // Convertir en Map pour la réponse JSON
        Map<String, Object> annexesResponse = new HashMap<>();
        annexesResponse.put("companyId", companyId);
        annexesResponse.put("asOfDate", asOfDate);
        annexesResponse.put("standard", "OHADA/SYSCOHADA");
        annexesResponse.put("notes", convertNotesToMap(annexes.getNotes()));
        annexesResponse.put("generatedAt", LocalDateTime.now());
        
        return annexesResponse;
    }

    /**
     * Générer tous les états financiers OHADA
     */
    public Map<String, Object> generateAllFinancialStatementsOHADA(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> allStatements = new HashMap<>();
        
        allStatements.put("companyId", companyId);
        allStatements.put("startDate", startDate);
        allStatements.put("endDate", endDate);
        allStatements.put("standard", "OHADA/SYSCOHADA");
        allStatements.put("generalLedger", generateGeneralLedgerOHADA(companyId, startDate, endDate));
        allStatements.put("balanceSheet", generateBalanceSheetOHADA(companyId, endDate));
        allStatements.put("incomeStatement", generateIncomeStatementOHADA(companyId, startDate, endDate));
        allStatements.put("cashFlowStatement", generateCashFlowStatementOHADA(companyId, startDate, endDate));
        allStatements.put("annexes", generateAnnexesOHADA(companyId, endDate));
        allStatements.put("generatedAt", LocalDateTime.now());
        
        return allStatements;
    }

    // Méthodes utilitaires privées
    private Map<String, BigDecimal> getAccountBalances(Long companyId, LocalDate asOfDate) {
        List<JournalEntry> entries = journalEntryRepository.findValidatedEntriesByDateRange(
            companyId, LocalDate.of(1900, 1, 1), asOfDate);
        
        Map<String, BigDecimal> accountBalances = new HashMap<>();
        
        for (JournalEntry entry : entries) {
            List<AccountEntry> accountEntries = accountEntryRepository.findByJournalEntryId(entry.getId());
            for (AccountEntry accountEntry : accountEntries) {
                String accountNumber = accountEntry.getAccountNumber();
                BigDecimal currentBalance = accountBalances.getOrDefault(accountNumber, BigDecimal.ZERO);
                
                if ("DEBIT".equals(accountEntry.getAccountType())) {
                    currentBalance = currentBalance.add(accountEntry.getAmount());
                } else if ("CREDIT".equals(accountEntry.getAccountType())) {
                    currentBalance = currentBalance.subtract(accountEntry.getAmount());
                }
                
                accountBalances.put(accountNumber, currentBalance);
            }
        }
        
        return accountBalances;
    }

    private Map<String, BigDecimal> getAccountBalancesForPeriod(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<JournalEntry> entries = journalEntryRepository.findValidatedEntriesByDateRange(companyId, startDate, endDate);
        
        Map<String, BigDecimal> accountBalances = new HashMap<>();
        
        for (JournalEntry entry : entries) {
            List<AccountEntry> accountEntries = accountEntryRepository.findByJournalEntryId(entry.getId());
            for (AccountEntry accountEntry : accountEntries) {
                String accountNumber = accountEntry.getAccountNumber();
                BigDecimal currentBalance = accountBalances.getOrDefault(accountNumber, BigDecimal.ZERO);
                
                if ("DEBIT".equals(accountEntry.getAccountType())) {
                    currentBalance = currentBalance.add(accountEntry.getAmount());
                } else if ("CREDIT".equals(accountEntry.getAccountType())) {
                    currentBalance = currentBalance.subtract(accountEntry.getAmount());
                }
                
                accountBalances.put(accountNumber, currentBalance);
            }
        }
        
        return accountBalances;
    }

    private boolean isDebitAccountClass(String accountClass) {
        return "12345".contains(accountClass);
    }

    private Map<String, List<Map<String, Object>>> organizeByAccountClass(Map<String, BigDecimal> accountBalances, Map<String, String> accountNames) {
        Map<String, List<Map<String, Object>>> accountsByClass = new HashMap<>();
        
        for (Map.Entry<String, BigDecimal> entry : accountBalances.entrySet()) {
            String accountNumber = entry.getKey();
            BigDecimal balance = entry.getValue();
            String accountName = accountNames.get(accountNumber);
            String accountClass = accountNumber.substring(0, 1);
            
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("accountNumber", accountNumber);
            accountInfo.put("accountName", accountName);
            accountInfo.put("accountClass", accountClass);
            accountInfo.put("solde", balance);
            accountInfo.put("isDebitAccount", isDebitAccountClass(accountClass));
            
            accountsByClass.computeIfAbsent(accountClass, k -> new java.util.ArrayList<>()).add(accountInfo);
        }
        
        // Trier les comptes par numéro dans chaque classe
        for (List<Map<String, Object>> accounts : accountsByClass.values()) {
            accounts.sort((a, b) -> ((String) a.get("accountNumber")).compareTo((String) b.get("accountNumber")));
        }
        
        return accountsByClass;
    }

    private Map<String, Object> convertPostesToMap(Map<String, ?> postes) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, ?> entry : postes.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private Map<String, Object> convertNotesToMap(Map<String, NoteAnnexe> notes) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, NoteAnnexe> entry : notes.entrySet()) {
            Map<String, Object> noteMap = new HashMap<>();
            NoteAnnexe note = entry.getValue();
            noteMap.put("numero", note.getNumero());
            noteMap.put("titre", note.getTitre());
            noteMap.put("description", note.getDescription());
            noteMap.put("contenu", note.getContenu());
            noteMap.put("donnees", note.getDonnees());
            result.put(entry.getKey(), noteMap);
        }
        return result;
    }
}





