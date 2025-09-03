package com.ecomptaia.service;

import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.repository.AccountEntryRepository;
import com.ecomptaia.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de validation et contrôle qualité des données comptables OHADA/SYSCOHADA
 */
@Service
public class AccountingValidationService {

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    /**
     * Validation complète des données comptables pour une entreprise
     */
    public Map<String, Object> validateCompanyAccounting(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> validationResults = new HashMap<>();
        
        try {
            // 1. Validation des écritures comptables
            Map<String, Object> journalValidation = validateJournalEntries(companyId, startDate, endDate);
            validationResults.put("journalValidation", journalValidation);
            
            // 2. Validation des équilibres comptables
            Map<String, Object> balanceValidation = validateAccountingBalances(companyId, startDate, endDate);
            validationResults.put("balanceValidation", balanceValidation);
            
            // 3. Validation de la cohérence OHADA
            Map<String, Object> ohadaValidation = validateOHADAConsistency(companyId, startDate, endDate);
            validationResults.put("ohadaValidation", ohadaValidation);
            
            // 4. Détection d'anomalies
            Map<String, Object> anomalyDetection = detectAnomalies(companyId, startDate, endDate);
            validationResults.put("anomalyDetection", anomalyDetection);
            
            // 5. Score global de qualité
            double qualityScore = calculateQualityScore(validationResults);
            validationResults.put("qualityScore", qualityScore);
            validationResults.put("overallStatus", qualityScore >= 80 ? "EXCELLENT" : qualityScore >= 60 ? "BON" : "À_AMÉLIORER");
            
            validationResults.put("validationDate", LocalDate.now());
            validationResults.put("period", Map.of("startDate", startDate, "endDate", endDate));
            
        } catch (Exception e) {
            validationResults.put("error", "Erreur lors de la validation: " + e.getMessage());
            validationResults.put("qualityScore", 0.0);
            validationResults.put("overallStatus", "ERREUR");
        }
        
        return validationResults;
    }

    /**
     * Validation des écritures comptables
     */
    private Map<String, Object> validateJournalEntries(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> results = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            List<JournalEntry> journalEntries = journalEntryRepository.findByCompanyIdAndEntryDateBetween(companyId, startDate, endDate);
            
            // Vérification du nombre d'écritures
            if (journalEntries.isEmpty()) {
                warnings.add("Aucune écriture comptable trouvée pour la période");
            }
            
            // Validation de chaque écriture
            for (JournalEntry entry : journalEntries) {
                // Vérification de l'équilibre débit/crédit
                if (entry.getTotalDebit() == null || entry.getTotalCredit() == null) {
                    errors.add("Écriture " + entry.getEntryNumber() + ": Totaux débit/crédit manquants");
                } else if (entry.getTotalDebit().compareTo(entry.getTotalCredit()) != 0) {
                    errors.add("Écriture " + entry.getEntryNumber() + ": Déséquilibre débit/crédit (" + 
                             entry.getTotalDebit() + " ≠ " + entry.getTotalCredit() + ")");
                }
                
                // Vérification des dates
                if (entry.getEntryDate() == null) {
                    errors.add("Écriture " + entry.getEntryNumber() + ": Date manquante");
                }
                
                // Vérification du statut
                if (entry.getStatus() == null) {
                    warnings.add("Écriture " + entry.getEntryNumber() + ": Statut non défini");
                }
            }
            
            results.put("totalEntries", journalEntries.size());
            results.put("validEntries", journalEntries.size() - errors.size());
            results.put("errors", errors);
            results.put("warnings", warnings);
            results.put("isValid", errors.isEmpty());
            
        } catch (Exception e) {
            results.put("error", "Erreur lors de la validation des écritures: " + e.getMessage());
            results.put("isValid", false);
        }
        
        return results;
    }

    /**
     * Validation des équilibres comptables
     */
    private Map<String, Object> validateAccountingBalances(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> results = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
            
            // Calcul des soldes par compte
            Map<String, BigDecimal> accountBalances = new HashMap<>();
            
            for (AccountEntry entry : accountEntries) {
                String accountNumber = entry.getAccountNumber();
                BigDecimal amount = entry.getAmount();
                
                if (accountBalances.containsKey(accountNumber)) {
                    accountBalances.put(accountNumber, accountBalances.get(accountNumber).add(amount));
                } else {
                    accountBalances.put(accountNumber, amount);
                }
            }
            
            // Vérification de la cohérence des soldes
            BigDecimal totalDebit = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            
            for (Map.Entry<String, BigDecimal> balance : accountBalances.entrySet()) {
                if (balance.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    totalDebit = totalDebit.add(balance.getValue());
                } else {
                    totalCredit = totalCredit.add(balance.getValue().abs());
                }
            }
            
            // Vérification de l'équilibre global
            if (totalDebit.compareTo(totalCredit) != 0) {
                errors.add("Déséquilibre global: Débit=" + totalDebit + ", Crédit=" + totalCredit);
            }
            
            results.put("totalAccounts", accountBalances.size());
            results.put("totalDebit", totalDebit);
            results.put("totalCredit", totalCredit);
            results.put("isBalanced", totalDebit.compareTo(totalCredit) == 0);
            results.put("errors", errors);
            results.put("warnings", warnings);
            
        } catch (Exception e) {
            results.put("error", "Erreur lors de la validation des équilibres: " + e.getMessage());
            results.put("isBalanced", false);
        }
        
        return results;
    }

    /**
     * Validation de la cohérence OHADA/SYSCOHADA
     */
    private Map<String, Object> validateOHADAConsistency(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> results = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
            
            // Vérification des classes de comptes OHADA
            Set<String> validClasses = Set.of("1", "2", "3", "4", "5", "6", "7");
            Set<String> usedClasses = new HashSet<>();
            
            for (AccountEntry entry : accountEntries) {
                String accountNumber = entry.getAccountNumber();
                if (accountNumber != null && accountNumber.length() >= 1) {
                    String accountClass = accountNumber.substring(0, 1);
                    usedClasses.add(accountClass);
                    
                    // Vérification de la validité de la classe
                    if (!validClasses.contains(accountClass)) {
                        errors.add("Compte " + accountNumber + ": Classe de compte invalide (" + accountClass + ")");
                    }
                }
            }
            
            // Vérification de la présence des classes principales
            if (!usedClasses.contains("1")) {
                warnings.add("Classe 1 (Comptes de capitaux) non utilisée");
            }
            if (!usedClasses.contains("2")) {
                warnings.add("Classe 2 (Comptes d'immobilisations) non utilisée");
            }
            if (!usedClasses.contains("3")) {
                warnings.add("Classe 3 (Comptes de stocks) non utilisée");
            }
            if (!usedClasses.contains("4")) {
                warnings.add("Classe 4 (Comptes de tiers) non utilisée");
            }
            if (!usedClasses.contains("5")) {
                warnings.add("Classe 5 (Comptes financiers) non utilisée");
            }
            if (!usedClasses.contains("6")) {
                warnings.add("Classe 6 (Comptes de charges) non utilisée");
            }
            if (!usedClasses.contains("7")) {
                warnings.add("Classe 7 (Comptes de produits) non utilisée");
            }
            
            results.put("usedClasses", usedClasses);
            results.put("validClasses", validClasses);
            results.put("errors", errors);
            results.put("warnings", warnings);
            results.put("isOHADACompliant", errors.isEmpty());
            
        } catch (Exception e) {
            results.put("error", "Erreur lors de la validation OHADA: " + e.getMessage());
            results.put("isOHADACompliant", false);
        }
        
        return results;
    }

    /**
     * Détection d'anomalies
     */
    private Map<String, Object> detectAnomalies(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> results = new HashMap<>();
        List<String> anomalies = new ArrayList<>();
        
        try {
            List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
            
            // Détection des montants anormalement élevés
            BigDecimal maxAmount = accountEntries.stream()
                .map(AccountEntry::getAmount)
                .filter(Objects::nonNull)
                .map(BigDecimal::abs)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            if (maxAmount.compareTo(new BigDecimal("1000000")) > 0) {
                anomalies.add("Montant anormalement élevé détecté: " + maxAmount);
            }
            
            // Détection des comptes avec beaucoup de mouvements
            Map<String, Long> accountMovements = accountEntries.stream()
                .collect(Collectors.groupingBy(AccountEntry::getAccountNumber, Collectors.counting()));
            
            accountMovements.entrySet().stream()
                .filter(entry -> entry.getValue() > 100)
                .forEach(entry -> anomalies.add("Compte " + entry.getKey() + " avec " + entry.getValue() + " mouvements (suspicion d'anomalie)"));
            
            // Détection des écritures en week-end (via les écritures comptables)
            List<JournalEntry> journalEntries = journalEntryRepository.findByCompanyIdAndEntryDateBetween(companyId, startDate, endDate);
            long weekendEntries = journalEntries.stream()
                .filter(entry -> entry.getEntryDate() != null)
                .filter(entry -> {
                    int dayOfWeek = entry.getEntryDate().getDayOfWeek().getValue();
                    return dayOfWeek == 6 || dayOfWeek == 7; // Samedi ou dimanche
                })
                .count();
            
            if (weekendEntries > 0) {
                anomalies.add(weekendEntries + " écritures en week-end détectées");
            }
            
            results.put("anomalies", anomalies);
            results.put("anomalyCount", anomalies.size());
            results.put("maxAmount", maxAmount);
            results.put("weekendEntries", weekendEntries);
            
        } catch (Exception e) {
            results.put("error", "Erreur lors de la détection d'anomalies: " + e.getMessage());
        }
        
        return results;
    }

    /**
     * Calcul du score de qualité global
     */
    private double calculateQualityScore(Map<String, Object> validationResults) {
        double score = 100.0;
        
        // Pénalités pour les erreurs
        Object journalValidationObj = validationResults.get("journalValidation");
        if (journalValidationObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> journalValidation = (Map<String, Object>) journalValidationObj;
            if (!(Boolean) journalValidation.get("isValid")) {
                score -= 30.0;
            }
        }
        
        Object balanceValidationObj = validationResults.get("balanceValidation");
        if (balanceValidationObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> balanceValidation = (Map<String, Object>) balanceValidationObj;
            if (!(Boolean) balanceValidation.get("isBalanced")) {
                score -= 40.0;
            }
        }
        
        Object ohadaValidationObj = validationResults.get("ohadaValidation");
        if (ohadaValidationObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> ohadaValidation = (Map<String, Object>) ohadaValidationObj;
            if (!(Boolean) ohadaValidation.get("isOHADACompliant")) {
                score -= 20.0;
            }
        }
        
        Object anomalyDetectionObj = validationResults.get("anomalyDetection");
        if (anomalyDetectionObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> anomalyDetection = (Map<String, Object>) anomalyDetectionObj;
            Object anomalyCountObj = anomalyDetection.get("anomalyCount");
            if (anomalyCountObj instanceof Integer) {
                int anomalyCount = (Integer) anomalyCountObj;
                score -= anomalyCount * 5.0;
            }
        }
        
        return Math.max(0.0, score);
    }

    /**
     * Validation rapide pour une écriture spécifique
     */
    public Map<String, Object> validateSingleEntry(Long entryId) {
        Map<String, Object> results = new HashMap<>();
        
        try {
            JournalEntry entry = journalEntryRepository.findById(entryId).orElse(null);
            if (entry == null) {
                results.put("error", "Écriture non trouvée");
                return results;
            }
            
            List<String> errors = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            
            // Vérification de l'équilibre
            if (entry.getTotalDebit() != null && entry.getTotalCredit() != null) {
                if (entry.getTotalDebit().compareTo(entry.getTotalCredit()) != 0) {
                    errors.add("Déséquilibre débit/crédit");
                }
            } else {
                errors.add("Totaux manquants");
            }
            
            // Vérification de la date
            if (entry.getEntryDate() == null) {
                errors.add("Date manquante");
            }
            
            results.put("entryId", entryId);
            results.put("errors", errors);
            results.put("warnings", warnings);
            results.put("isValid", errors.isEmpty());
            
        } catch (Exception e) {
            results.put("error", "Erreur lors de la validation: " + e.getMessage());
            results.put("isValid", false);
        }
        
        return results;
    }
}
