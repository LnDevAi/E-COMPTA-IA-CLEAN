package com.ecomptaia.service;

import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.repository.AccountEntryRepository;
import com.ecomptaia.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class TestDataService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    /**
     * Générer des données de test pour les rapports financiers
     */
    public void generateTestFinancialData(Long companyId) {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        
        // Générer des écritures de test pour l'année 2024
        generateTestJournalEntries(companyId, startDate, endDate);
    }

    private void generateTestJournalEntries(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<JournalEntry> entries = new ArrayList<>();
        
        // 1. Écriture d'ouverture - Capital social
        JournalEntry openingEntry = new JournalEntry();
        openingEntry.setCompanyId(companyId);
        openingEntry.setEntryNumber("EC-2024-001");
        openingEntry.setEntryDate(LocalDate.of(2024, 1, 1));
        openingEntry.setJournalType("OUVERTURE");
        openingEntry.setDescription("Écriture d'ouverture - Capital social");
        openingEntry.setStatus("VALIDÉ");
        openingEntry.setIsPosted(true);
        openingEntry.setCurrency("EUR");
        openingEntry.setCountryCode("FR");
        openingEntry.setAccountingStandard("PCG");
        openingEntry.setCreatedAt(LocalDateTime.now());
        openingEntry.setCreatedBy(1L);
        openingEntry.setValidatedAt(LocalDateTime.now());
        openingEntry.setValidatedBy(1L);
        
        entries.add(openingEntry);
        
        // 2. Achat de marchandises
        JournalEntry purchaseEntry = new JournalEntry();
        purchaseEntry.setCompanyId(companyId);
        purchaseEntry.setEntryNumber("EC-2024-002");
        purchaseEntry.setEntryDate(LocalDate.of(2024, 1, 15));
        purchaseEntry.setJournalType("ACHATS");
        purchaseEntry.setDescription("Achat de marchandises");
        purchaseEntry.setStatus("VALIDÉ");
        purchaseEntry.setIsPosted(true);
        purchaseEntry.setCurrency("EUR");
        purchaseEntry.setCountryCode("FR");
        purchaseEntry.setAccountingStandard("PCG");
        purchaseEntry.setCreatedAt(LocalDateTime.now());
        purchaseEntry.setCreatedBy(1L);
        purchaseEntry.setValidatedAt(LocalDateTime.now());
        purchaseEntry.setValidatedBy(1L);
        
        entries.add(purchaseEntry);
        
        // 3. Vente de marchandises
        JournalEntry saleEntry = new JournalEntry();
        saleEntry.setCompanyId(companyId);
        saleEntry.setEntryNumber("EC-2024-003");
        saleEntry.setEntryDate(LocalDate.of(2024, 1, 20));
        saleEntry.setJournalType("VENTES");
        saleEntry.setDescription("Vente de marchandises");
        saleEntry.setStatus("VALIDÉ");
        saleEntry.setIsPosted(true);
        saleEntry.setCurrency("EUR");
        saleEntry.setCountryCode("FR");
        saleEntry.setAccountingStandard("PCG");
        saleEntry.setCreatedAt(LocalDateTime.now());
        saleEntry.setCreatedBy(1L);
        saleEntry.setValidatedAt(LocalDateTime.now());
        saleEntry.setValidatedBy(1L);
        
        entries.add(saleEntry);
        
        // 4. Charges de personnel
        JournalEntry salaryEntry = new JournalEntry();
        salaryEntry.setCompanyId(companyId);
        salaryEntry.setEntryNumber("EC-2024-004");
        salaryEntry.setEntryDate(LocalDate.of(2024, 1, 31));
        salaryEntry.setJournalType("CHARGES");
        salaryEntry.setDescription("Charges de personnel");
        salaryEntry.setStatus("VALIDÉ");
        salaryEntry.setIsPosted(true);
        salaryEntry.setCurrency("EUR");
        salaryEntry.setCountryCode("FR");
        salaryEntry.setAccountingStandard("PCG");
        salaryEntry.setCreatedAt(LocalDateTime.now());
        salaryEntry.setCreatedBy(1L);
        salaryEntry.setValidatedAt(LocalDateTime.now());
        salaryEntry.setValidatedBy(1L);
        
        entries.add(salaryEntry);
        
        // 5. Amortissement
        JournalEntry depreciationEntry = new JournalEntry();
        depreciationEntry.setCompanyId(companyId);
        depreciationEntry.setEntryNumber("EC-2024-005");
        depreciationEntry.setEntryDate(LocalDate.of(2024, 1, 31));
        depreciationEntry.setJournalType("AMORTISSEMENTS");
        depreciationEntry.setDescription("Amortissement des immobilisations");
        depreciationEntry.setStatus("VALIDÉ");
        depreciationEntry.setIsPosted(true);
        depreciationEntry.setCurrency("EUR");
        depreciationEntry.setCountryCode("FR");
        depreciationEntry.setAccountingStandard("PCG");
        depreciationEntry.setCreatedAt(LocalDateTime.now());
        depreciationEntry.setCreatedBy(1L);
        depreciationEntry.setValidatedAt(LocalDateTime.now());
        depreciationEntry.setValidatedBy(1L);
        
        entries.add(depreciationEntry);
        
        // Calculer les totaux débit/crédit pour chaque écriture
        Map<String, BigDecimal> entryTotals = calculateEntryTotals();
        
        // Appliquer les totaux aux écritures
        for (JournalEntry entry : entries) {
            BigDecimal total = entryTotals.get(entry.getEntryNumber());
            if (total != null) {
                entry.setTotalDebit(total);
                entry.setTotalCredit(total);
            }
        }
        
        // Sauvegarder les écritures
        List<JournalEntry> savedEntries = journalEntryRepository.saveAll(entries);
        
        // Générer les comptes correspondants
        generateTestAccountEntries(savedEntries);
    }

    private void generateTestAccountEntries(List<JournalEntry> journalEntries) {
        List<AccountEntry> accountEntries = new ArrayList<>();
        
        for (JournalEntry journalEntry : journalEntries) {
            switch (journalEntry.getEntryNumber()) {
                case "EC-2024-001": // Écriture d'ouverture
                    // Débit : Banque (512)
                    AccountEntry bankDebit = new AccountEntry();
                    bankDebit.setJournalEntryId(journalEntry.getId());
                    bankDebit.setCompanyId(journalEntry.getCompanyId());
                    bankDebit.setAccountNumber("512000");
                    bankDebit.setAccountName("Banque");
                    bankDebit.setAccountType("DEBIT");
                    bankDebit.setAmount(new BigDecimal("100000.00"));
                                         bankDebit.setDescription("Capital social");
                     bankDebit.setCountryCode("FR");
                     bankDebit.setAccountingStandard("PCG");
                     bankDebit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(bankDebit);
                    
                    // Crédit : Capital social (101)
                    AccountEntry capitalCredit = new AccountEntry();
                    capitalCredit.setJournalEntryId(journalEntry.getId());
                    capitalCredit.setCompanyId(journalEntry.getCompanyId());
                    capitalCredit.setAccountNumber("101000");
                    capitalCredit.setAccountName("Capital social");
                    capitalCredit.setAccountType("CREDIT");
                    capitalCredit.setAmount(new BigDecimal("100000.00"));
                                         capitalCredit.setDescription("Capital social");
                     capitalCredit.setCountryCode("FR");
                     capitalCredit.setAccountingStandard("PCG");
                     capitalCredit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(capitalCredit);
                    break;
                    
                case "EC-2024-002": // Achat de marchandises
                    // Débit : Achats de marchandises (607)
                    AccountEntry purchasesDebit = new AccountEntry();
                    purchasesDebit.setJournalEntryId(journalEntry.getId());
                    purchasesDebit.setCompanyId(journalEntry.getCompanyId());
                    purchasesDebit.setAccountNumber("607000");
                    purchasesDebit.setAccountName("Achats de marchandises");
                    purchasesDebit.setAccountType("DEBIT");
                    purchasesDebit.setAmount(new BigDecimal("50000.00"));
                                         purchasesDebit.setDescription("Achat de marchandises");
                     purchasesDebit.setCountryCode("FR");
                     purchasesDebit.setAccountingStandard("PCG");
                     purchasesDebit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(purchasesDebit);
                    
                    // Crédit : Fournisseurs (401)
                    AccountEntry suppliersCredit = new AccountEntry();
                    suppliersCredit.setJournalEntryId(journalEntry.getId());
                    suppliersCredit.setCompanyId(journalEntry.getCompanyId());
                    suppliersCredit.setAccountNumber("401000");
                    suppliersCredit.setAccountName("Fournisseurs");
                    suppliersCredit.setAccountType("CREDIT");
                    suppliersCredit.setAmount(new BigDecimal("50000.00"));
                                         suppliersCredit.setDescription("Fournisseurs");
                     suppliersCredit.setCountryCode("FR");
                     suppliersCredit.setAccountingStandard("PCG");
                     suppliersCredit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(suppliersCredit);
                    break;
                    
                case "EC-2024-003": // Vente de marchandises
                    // Débit : Clients (411)
                    AccountEntry clientsDebit = new AccountEntry();
                    clientsDebit.setJournalEntryId(journalEntry.getId());
                    clientsDebit.setCompanyId(journalEntry.getCompanyId());
                    clientsDebit.setAccountNumber("411000");
                    clientsDebit.setAccountName("Clients");
                    clientsDebit.setAccountType("DEBIT");
                    clientsDebit.setAmount(new BigDecimal("80000.00"));
                                         clientsDebit.setDescription("Clients");
                     clientsDebit.setCountryCode("FR");
                     clientsDebit.setAccountingStandard("PCG");
                     clientsDebit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(clientsDebit);
                    
                    // Crédit : Ventes de marchandises (707)
                    AccountEntry salesCredit = new AccountEntry();
                    salesCredit.setJournalEntryId(journalEntry.getId());
                    salesCredit.setCompanyId(journalEntry.getCompanyId());
                    salesCredit.setAccountNumber("707000");
                    salesCredit.setAccountName("Ventes de marchandises");
                    salesCredit.setAccountType("CREDIT");
                    salesCredit.setAmount(new BigDecimal("80000.00"));
                                         salesCredit.setDescription("Ventes de marchandises");
                     salesCredit.setCountryCode("FR");
                     salesCredit.setAccountingStandard("PCG");
                     salesCredit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(salesCredit);
                    break;
                    
                case "EC-2024-004": // Charges de personnel
                    // Débit : Charges de personnel (641)
                    AccountEntry salaryDebit = new AccountEntry();
                    salaryDebit.setJournalEntryId(journalEntry.getId());
                    salaryDebit.setCompanyId(journalEntry.getCompanyId());
                    salaryDebit.setAccountNumber("641000");
                    salaryDebit.setAccountName("Charges de personnel");
                    salaryDebit.setAccountType("DEBIT");
                    salaryDebit.setAmount(new BigDecimal("15000.00"));
                                         salaryDebit.setDescription("Charges de personnel");
                     salaryDebit.setCountryCode("FR");
                     salaryDebit.setAccountingStandard("PCG");
                     salaryDebit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(salaryDebit);
                    
                    // Crédit : Banque (512)
                    AccountEntry bankCredit = new AccountEntry();
                    bankCredit.setJournalEntryId(journalEntry.getId());
                    bankCredit.setCompanyId(journalEntry.getCompanyId());
                    bankCredit.setAccountNumber("512000");
                    bankCredit.setAccountName("Banque");
                    bankCredit.setAccountType("CREDIT");
                    bankCredit.setAmount(new BigDecimal("15000.00"));
                                         bankCredit.setDescription("Paiement des salaires");
                     bankCredit.setCountryCode("FR");
                     bankCredit.setAccountingStandard("PCG");
                     bankCredit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(bankCredit);
                    break;
                    
                case "EC-2024-005": // Amortissement
                    // Débit : Dotations aux amortissements (681)
                    AccountEntry depreciationDebit = new AccountEntry();
                    depreciationDebit.setJournalEntryId(journalEntry.getId());
                    depreciationDebit.setCompanyId(journalEntry.getCompanyId());
                    depreciationDebit.setAccountNumber("681000");
                    depreciationDebit.setAccountName("Dotations aux amortissements");
                    depreciationDebit.setAccountType("DEBIT");
                    depreciationDebit.setAmount(new BigDecimal("5000.00"));
                                         depreciationDebit.setDescription("Amortissement des immobilisations");
                     depreciationDebit.setCountryCode("FR");
                     depreciationDebit.setAccountingStandard("PCG");
                     depreciationDebit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(depreciationDebit);
                    
                    // Crédit : Amortissements des immobilisations (281)
                    AccountEntry amortizationCredit = new AccountEntry();
                    amortizationCredit.setJournalEntryId(journalEntry.getId());
                    amortizationCredit.setCompanyId(journalEntry.getCompanyId());
                    amortizationCredit.setAccountNumber("281000");
                    amortizationCredit.setAccountName("Amortissements des immobilisations");
                    amortizationCredit.setAccountType("CREDIT");
                    amortizationCredit.setAmount(new BigDecimal("5000.00"));
                                         amortizationCredit.setDescription("Amortissement des immobilisations");
                     amortizationCredit.setCountryCode("FR");
                     amortizationCredit.setAccountingStandard("PCG");
                     amortizationCredit.setCreatedAt(LocalDateTime.now());
                     accountEntries.add(amortizationCredit);
                    break;
            }
        }
        
        // Sauvegarder tous les comptes
        accountEntryRepository.saveAll(accountEntries);
    }

    /**
     * Calculer les totaux débit/crédit pour chaque écriture
     */
    private Map<String, BigDecimal> calculateEntryTotals() {
        Map<String, BigDecimal> totals = new HashMap<>();
        
        // Écriture d'ouverture
        totals.put("EC-2024-001", new BigDecimal("100000.00"));
        
        // Achat de marchandises
        totals.put("EC-2024-002", new BigDecimal("50000.00"));
        
        // Vente de marchandises
        totals.put("EC-2024-003", new BigDecimal("80000.00"));
        
        // Charges de personnel
        totals.put("EC-2024-004", new BigDecimal("15000.00"));
        
        // Amortissement
        totals.put("EC-2024-005", new BigDecimal("5000.00"));
        
        return totals;
    }

    /**
     * Nettoyer les données de test
     */
    public void cleanTestData(Long companyId) {
        // Supprimer les comptes d'abord
        List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
        accountEntryRepository.deleteAll(accountEntries);
        
        // Puis supprimer les écritures
        List<JournalEntry> journalEntries = journalEntryRepository.findByCompanyId(companyId);
        journalEntryRepository.deleteAll(journalEntries);
    }
}
