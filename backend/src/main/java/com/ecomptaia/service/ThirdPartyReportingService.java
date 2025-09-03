package com.ecomptaia.service;

import com.ecomptaia.entity.ThirdParty;
import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.repository.ThirdPartyRepository;
import com.ecomptaia.repository.JournalEntryRepository;
import com.ecomptaia.repository.AccountEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de reporting spécifique aux tiers
 */
@Service
public class ThirdPartyReportingService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    /**
     * Générer le Grand Livre d'un tiers spécifique
     */
    public Map<String, Object> generateThirdPartyLedger(Long companyId, Long thirdPartyId, 
                                                       LocalDate startDate, LocalDate endDate) {
        Map<String, Object> ledger = new HashMap<>();
        
        // Récupérer le tiers
        Optional<ThirdParty> thirdPartyOpt = thirdPartyRepository.findById(thirdPartyId);
        if (!thirdPartyOpt.isPresent()) {
            throw new RuntimeException("Tiers non trouvé");
        }
        ThirdParty thirdParty = thirdPartyOpt.get();
        
        // Récupérer toutes les écritures de la période
        List<JournalEntry> entries = journalEntryRepository.findValidatedEntriesByDateRange(companyId, startDate, endDate);
        
        // Filtrer les écritures liées à ce tiers (comptes de tiers)
        List<Map<String, Object>> thirdPartyEntries = new ArrayList<>();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal soldePrecedent = BigDecimal.ZERO;
        
        for (JournalEntry entry : entries) {
            List<AccountEntry> accountEntries = accountEntryRepository.findByJournalEntryId(entry.getId());
            
            for (AccountEntry accountEntry : accountEntries) {
                // Vérifier si c'est le compte spécifique du tiers
                if (isAccountForThirdParty(accountEntry.getAccountNumber(), thirdParty.getAccountNumber())) {
                    
                    Map<String, Object> entryData = new HashMap<>();
                    entryData.put("date", entry.getEntryDate());
                    entryData.put("journalEntryId", entry.getId());
                    entryData.put("reference", entry.getReference());
                    entryData.put("description", entry.getDescription());
                    entryData.put("accountNumber", accountEntry.getAccountNumber());
                    entryData.put("accountName", accountEntry.getAccountName());
                    entryData.put("debit", "DEBIT".equals(accountEntry.getAccountType()) ? accountEntry.getAmount() : BigDecimal.ZERO);
                    entryData.put("credit", "CREDIT".equals(accountEntry.getAccountType()) ? accountEntry.getAmount() : BigDecimal.ZERO);
                    entryData.put("accountType", accountEntry.getAccountType());
                    
                    thirdPartyEntries.add(entryData);
                    
                    if ("DEBIT".equals(accountEntry.getAccountType())) {
                        totalDebit = totalDebit.add(accountEntry.getAmount());
                    } else {
                        totalCredit = totalCredit.add(accountEntry.getAmount());
                    }
                }
            }
        }
        
        // Trier par date
        thirdPartyEntries.sort(Comparator.comparing(e -> (LocalDate) e.get("date")));
        
        // Calculer le solde final
        BigDecimal soldeFinal = soldePrecedent.add(totalDebit).subtract(totalCredit);
        
        ledger.put("thirdParty", thirdParty);
        ledger.put("startDate", startDate);
        ledger.put("endDate", endDate);
        ledger.put("soldePrecedent", soldePrecedent);
        ledger.put("totalDebit", totalDebit);
        ledger.put("totalCredit", totalCredit);
        ledger.put("soldeFinal", soldeFinal);
        ledger.put("entries", thirdPartyEntries);
        ledger.put("nombreEcritures", thirdPartyEntries.size());
        ledger.put("generatedAt", LocalDateTime.now());
        
        return ledger;
    }

    /**
     * Générer la Balance des Tiers (Balance Âgée)
     */
    public Map<String, Object> generateThirdPartyBalance(Long companyId, LocalDate asOfDate) {
        Map<String, Object> balance = new HashMap<>();
        
        // Récupérer tous les tiers actifs
        List<ThirdParty> allThirdParties = thirdPartyRepository.findByCompanyIdAndIsActiveTrue(companyId);
        
        // Séparer les débiteurs et créanciers
        List<Map<String, Object>> debtors = new ArrayList<>();
        List<Map<String, Object>> creditors = new ArrayList<>();
        
        BigDecimal totalReceivables = BigDecimal.ZERO;
        BigDecimal totalPayables = BigDecimal.ZERO;
        
        for (ThirdParty thirdParty : allThirdParties) {
            if (thirdParty.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
                // Débiteur
                Map<String, Object> debtor = createThirdPartyBalanceEntry(thirdParty, asOfDate);
                debtors.add(debtor);
                totalReceivables = totalReceivables.add(thirdParty.getCurrentBalance());
            } else if (thirdParty.getCurrentBalance().compareTo(BigDecimal.ZERO) < 0) {
                // Créancier
                Map<String, Object> creditor = createThirdPartyBalanceEntry(thirdParty, asOfDate);
                creditors.add(creditor);
                totalPayables = totalPayables.add(thirdParty.getCurrentBalance().abs());
            }
        }
        
        // Trier par montant décroissant
        debtors.sort((a, b) -> ((BigDecimal) b.get("balance")).compareTo((BigDecimal) a.get("balance")));
        creditors.sort((a, b) -> ((BigDecimal) b.get("balance")).compareTo((BigDecimal) a.get("balance")));
        
        balance.put("asOfDate", asOfDate);
        balance.put("debtors", debtors);
        balance.put("creditors", creditors);
        balance.put("totalReceivables", totalReceivables);
        balance.put("totalPayables", totalPayables);
        balance.put("netPosition", totalReceivables.subtract(totalPayables));
        balance.put("totalThirdParties", allThirdParties.size());
        balance.put("totalDebtors", debtors.size());
        balance.put("totalCreditors", creditors.size());
        balance.put("generatedAt", LocalDateTime.now());
        
        return balance;
    }

    /**
     * Générer l'Échéancier des Tiers
     */
    public Map<String, Object> generateThirdPartySchedule(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> schedule = new HashMap<>();
        
        // Récupérer tous les tiers avec des soldes
        List<ThirdParty> thirdParties = thirdPartyRepository.findByCompanyIdAndIsActiveTrue(companyId)
            .stream()
            .filter(tp -> tp.getCurrentBalance().compareTo(BigDecimal.ZERO) != 0)
            .collect(Collectors.toList());
        
        List<Map<String, Object>> scheduleEntries = new ArrayList<>();
        
        for (ThirdParty thirdParty : thirdParties) {
            // Calculer les échéances basées sur les conditions de paiement
            List<Map<String, Object>> dueDates = calculateDueDates(thirdParty, startDate, endDate);
            
            for (Map<String, Object> dueDate : dueDates) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("thirdParty", thirdParty);
                entry.put("dueDate", dueDate.get("dueDate"));
                entry.put("amount", dueDate.get("amount"));
                entry.put("daysUntilDue", dueDate.get("daysUntilDue"));
                entry.put("status", dueDate.get("status"));
                entry.put("type", thirdParty.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0 ? "RECEIVABLE" : "PAYABLE");
                
                scheduleEntries.add(entry);
            }
        }
        
        // Trier par date d'échéance
        scheduleEntries.sort(Comparator.comparing(e -> (LocalDate) e.get("dueDate")));
        
        // Grouper par statut
        Map<String, List<Map<String, Object>>> entriesByStatus = scheduleEntries.stream()
            .collect(Collectors.groupingBy(e -> (String) e.get("status")));
        
        schedule.put("startDate", startDate);
        schedule.put("endDate", endDate);
        schedule.put("allEntries", scheduleEntries);
        schedule.put("entriesByStatus", entriesByStatus);
        schedule.put("totalEntries", scheduleEntries.size());
        schedule.put("generatedAt", LocalDateTime.now());
        
        return schedule;
    }

    /**
     * Générer l'Analyse des Créances et Dettes
     */
    public Map<String, Object> generateReceivablesAnalysis(Long companyId, LocalDate asOfDate) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Récupérer les débiteurs
        List<ThirdParty> debtors = thirdPartyRepository.findDebtorsByCompany(companyId);
        
        // Analyser par tranche d'âge
        Map<String, BigDecimal> receivablesByAge = new HashMap<>();
        Map<String, Integer> countByAge = new HashMap<>();
        
        BigDecimal totalReceivables = BigDecimal.ZERO;
        int totalDebtors = debtors.size();
        
        for (ThirdParty debtor : debtors) {
            BigDecimal balance = debtor.getCurrentBalance();
            totalReceivables = totalReceivables.add(balance);
            
            // Simuler l'âge des créances (en production, calculer à partir des écritures)
            int ageInDays = calculateReceivableAge(debtor);
            String ageCategory = categorizeAge(ageInDays);
            
            receivablesByAge.merge(ageCategory, balance, BigDecimal::add);
            countByAge.merge(ageCategory, 1, Integer::sum);
        }
        
        // Calculer les pourcentages
        Map<String, Double> percentagesByAge = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : receivablesByAge.entrySet()) {
            double percentage = totalReceivables.compareTo(BigDecimal.ZERO) > 0 
                ? entry.getValue().divide(totalReceivables, 4, RoundingMode.HALF_UP).doubleValue() * 100
                : 0.0;
            percentagesByAge.put(entry.getKey(), percentage);
        }
        
        analysis.put("asOfDate", asOfDate);
        analysis.put("totalReceivables", totalReceivables);
        analysis.put("totalDebtors", totalDebtors);
        analysis.put("receivablesByAge", receivablesByAge);
        analysis.put("countByAge", countByAge);
        analysis.put("percentagesByAge", percentagesByAge);
        analysis.put("averageReceivable", totalDebtors > 0 ? totalReceivables.divide(BigDecimal.valueOf(totalDebtors), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        analysis.put("generatedAt", LocalDateTime.now());
        
        return analysis;
    }

    /**
     * Générer le Rapport de Recouvrement
     */
    public Map<String, Object> generateCollectionReport(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        
        // Récupérer les débiteurs avec des créances anciennes
        List<ThirdParty> debtors = thirdPartyRepository.findDebtorsByCompany(companyId);
        
        List<Map<String, Object>> overdueAccounts = new ArrayList<>();
        List<Map<String, Object>> riskAccounts = new ArrayList<>();
        
        BigDecimal totalOverdue = BigDecimal.ZERO;
        BigDecimal totalAtRisk = BigDecimal.ZERO;
        
        for (ThirdParty debtor : debtors) {
            int ageInDays = calculateReceivableAge(debtor);
            
            if (ageInDays > 90) { // Plus de 90 jours
                Map<String, Object> overdue = createCollectionEntry(debtor, ageInDays, "OVERDUE");
                overdueAccounts.add(overdue);
                totalOverdue = totalOverdue.add(debtor.getCurrentBalance());
            } else if (ageInDays > 60) { // Entre 60 et 90 jours
                Map<String, Object> risk = createCollectionEntry(debtor, ageInDays, "AT_RISK");
                riskAccounts.add(risk);
                totalAtRisk = totalAtRisk.add(debtor.getCurrentBalance());
            }
        }
        
        // Trier par âge décroissant
        overdueAccounts.sort((a, b) -> ((Integer) b.get("ageInDays")).compareTo((Integer) a.get("ageInDays")));
        riskAccounts.sort((a, b) -> ((Integer) b.get("ageInDays")).compareTo((Integer) a.get("ageInDays")));
        
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("overdueAccounts", overdueAccounts);
        report.put("riskAccounts", riskAccounts);
        report.put("totalOverdue", totalOverdue);
        report.put("totalAtRisk", totalAtRisk);
        report.put("totalExposure", totalOverdue.add(totalAtRisk));
        report.put("overdueCount", overdueAccounts.size());
        report.put("riskCount", riskAccounts.size());
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }

    // Méthodes utilitaires privées

    private boolean isAccountForThirdParty(String accountNumber, String thirdPartyCode) {
        // Utiliser le numéro de compte du tiers pour la correspondance
        // Le numéro de compte tiers est un sous-compte de la classe 4
        return accountNumber.equals(thirdPartyCode);
    }

    private Map<String, Object> createThirdPartyBalanceEntry(ThirdParty thirdParty, LocalDate asOfDate) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("thirdParty", thirdParty);
        entry.put("balance", thirdParty.getCurrentBalance());
        entry.put("asOfDate", asOfDate);
        entry.put("ageInDays", calculateReceivableAge(thirdParty));
        entry.put("ageCategory", categorizeAge(calculateReceivableAge(thirdParty)));
        return entry;
    }

    private List<Map<String, Object>> calculateDueDates(ThirdParty thirdParty, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> dueDates = new ArrayList<>();
        
        // Simuler les échéances basées sur les conditions de paiement
        Integer paymentDelay = thirdParty.getPaymentDelay() != null ? thirdParty.getPaymentDelay() : 30;
        
        LocalDate dueDate = startDate.plusDays(paymentDelay);
        if (!dueDate.isAfter(endDate)) {
            Map<String, Object> due = new HashMap<>();
            due.put("dueDate", dueDate);
            due.put("amount", thirdParty.getCurrentBalance().abs());
            due.put("daysUntilDue", (int) ChronoUnit.DAYS.between(LocalDate.now(), dueDate));
            due.put("status", dueDate.isBefore(LocalDate.now()) ? "OVERDUE" : 
                             dueDate.isBefore(LocalDate.now().plusDays(7)) ? "DUE_SOON" : "FUTURE");
            dueDates.add(due);
        }
        
        return dueDates;
    }

    private int calculateReceivableAge(ThirdParty thirdParty) {
        // En production, calculer l'âge réel à partir des écritures
        // Pour l'instant, simulation basée sur la date de création
        if (thirdParty.getCreatedAt() != null) {
            return (int) ChronoUnit.DAYS.between(thirdParty.getCreatedAt().toLocalDate(), LocalDate.now());
        }
        return new Random().nextInt(120) + 1; // Simulation 1-120 jours
    }

    private String categorizeAge(int ageInDays) {
        if (ageInDays <= 30) return "0-30 jours";
        if (ageInDays <= 60) return "31-60 jours";
        if (ageInDays <= 90) return "61-90 jours";
        if (ageInDays <= 120) return "91-120 jours";
        return "Plus de 120 jours";
    }

    private Map<String, Object> createCollectionEntry(ThirdParty thirdParty, int ageInDays, String status) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("thirdParty", thirdParty);
        entry.put("balance", thirdParty.getCurrentBalance());
        entry.put("ageInDays", ageInDays);
        entry.put("ageCategory", categorizeAge(ageInDays));
        entry.put("status", status);
        entry.put("lastContact", thirdParty.getUpdatedAt());
        return entry;
    }
}
