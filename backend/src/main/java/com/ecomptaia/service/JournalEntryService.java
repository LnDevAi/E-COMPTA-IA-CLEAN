package com.ecomptaia.service;

import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.repository.JournalEntryRepository;
import com.ecomptaia.repository.AccountEntryRepository;
import com.ecomptaia.config.CountrySpecificRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private CountrySpecificRules countryRules;

    /**
     * Créer une nouvelle écriture comptable
     */
    public JournalEntry createJournalEntry(JournalEntry entry, List<AccountEntry> accountEntries) {
        // Validation de l'équilibre
        validateEntryBalance(accountEntries);
        
        // Validation selon le standard comptable
        validateByAccountingStandard(entry, accountEntries);
        
        // Génération du numéro d'écriture
        entry.setEntryNumber(generateEntryNumber(entry.getCompanyId()));
        
        // Sauvegarde de l'écriture
        JournalEntry savedEntry = journalEntryRepository.save(entry);
        
        // Sauvegarde des lignes d'écriture
        for (AccountEntry accountEntry : accountEntries) {
            accountEntry.setJournalEntryId(savedEntry.getId());
            accountEntryRepository.save(accountEntry);
        }
        
        return savedEntry;
    }

    /**
     * Valider une écriture
     */
    public JournalEntry validateEntry(Long entryId, Long validatedBy) {
        Optional<JournalEntry> entryOpt = journalEntryRepository.findById(entryId);
        if (entryOpt.isPresent()) {
            JournalEntry entry = entryOpt.get();
            entry.setStatus("VALIDÉ");
            entry.setValidatedBy(validatedBy);
            entry.setValidatedAt(LocalDateTime.now());
            entry.setIsPosted(true);
            entry.setUpdatedAt(LocalDateTime.now());
            return journalEntryRepository.save(entry);
        }
        throw new RuntimeException("Écriture non trouvée");
    }

    /**
     * Annuler une écriture
     */
    public JournalEntry cancelEntry(Long entryId) {
        Optional<JournalEntry> entryOpt = journalEntryRepository.findById(entryId);
        if (entryOpt.isPresent()) {
            JournalEntry entry = entryOpt.get();
            if ("VALIDÉ".equals(entry.getStatus())) {
                throw new RuntimeException("Impossible d'annuler une écriture validée");
            }
            entry.setStatus("ANNULE");
            entry.setUpdatedAt(LocalDateTime.now());
            return journalEntryRepository.save(entry);
        }
        throw new RuntimeException("Écriture non trouvée");
    }

    /**
     * Récupérer les écritures d'une entreprise
     */
    public List<JournalEntry> getEntriesByCompany(Long companyId) {
        return journalEntryRepository.findByCompanyId(companyId);
    }

    /**
     * Récupérer les écritures par statut
     */
    public List<JournalEntry> getEntriesByStatus(Long companyId, String status) {
        return journalEntryRepository.findByCompanyIdAndStatus(companyId, status);
    }

    /**
     * Récupérer les écritures par période
     */
    public List<JournalEntry> getEntriesByDateRange(Long companyId, LocalDate startDate, LocalDate endDate) {
        return journalEntryRepository.findByCompanyIdAndEntryDateBetween(companyId, startDate, endDate);
    }

    /**
     * Récupérer les écritures par type de journal
     */
    public List<JournalEntry> getEntriesByJournalType(Long companyId, String journalType) {
        return journalEntryRepository.findByCompanyIdAndJournalType(companyId, journalType);
    }

    /**
     * Récupérer les écritures comptabilisées
     */
    public List<JournalEntry> getPostedEntries(Long companyId) {
        return journalEntryRepository.findPostedEntriesByCompany(companyId);
    }

    /**
     * Récupérer les brouillons
     */
    public List<JournalEntry> getDraftEntries(Long companyId) {
        return journalEntryRepository.findDraftEntriesByCompany(companyId);
    }

    /**
     * Récupérer les écritures non rapprochées
     */
    public List<JournalEntry> getUnreconciledEntries(Long companyId) {
        return journalEntryRepository.findUnreconciledEntriesByCompany(companyId);
    }

    /**
     * Statistiques des écritures
     */
    public Map<String, Object> getEntryStatistics(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalEntries", journalEntryRepository.countValidatedEntriesByCompany(companyId));
        stats.put("draftEntries", journalEntryRepository.countDraftEntriesByCompany(companyId));
        stats.put("journalTypes", journalEntryRepository.findDistinctJournalTypesByCompany(companyId));
        
        return stats;
    }

    /**
     * Récupérer toutes les écritures (pour les tests)
     */
    public List<JournalEntry> getAllEntries() {
        return journalEntryRepository.findAll();
    }

    /**
     * Validation de l'équilibre débit/crédit
     */
    private void validateEntryBalance(List<AccountEntry> accountEntries) {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        
        for (AccountEntry entry : accountEntries) {
            if ("DEBIT".equals(entry.getAccountType())) {
                totalDebit = totalDebit.add(entry.getAmount());
            } else if ("CREDIT".equals(entry.getAccountType())) {
                totalCredit = totalCredit.add(entry.getAmount());
            }
        }
        
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new RuntimeException("L'écriture n'est pas équilibrée. Débit: " + totalDebit + ", Crédit: " + totalCredit);
        }
    }

    /**
     * Validation selon le standard comptable
     */
    private void validateByAccountingStandard(JournalEntry entry, List<AccountEntry> accountEntries) {
        CountrySpecificRules.ValidationRules rules = countryRules.getValidationForStandard(entry.getAccountingStandard());
        
        if (rules != null && rules.getRequireBalancedEntries()) {
            // Validation déjà faite dans validateEntryBalance
        }
        
        // Validation du format des numéros de compte
        for (AccountEntry accountEntry : accountEntries) {
            if (rules != null && rules.getAccountNumberFormat() != null) {
                if (!accountEntry.getAccountNumber().matches(rules.getAccountNumberFormat())) {
                    throw new RuntimeException("Format de compte invalide pour " + entry.getAccountingStandard());
                }
            }
        }
    }

    /**
     * Génération du numéro d'écriture
     */
    private String generateEntryNumber(Long companyId) {
        // Format: JE-YYYYMMDD-XXXX (ex: JE-20240115-0001)
        String datePrefix = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseNumber = "JE-" + datePrefix + "-";
        
        // Compter les écritures du jour pour cette entreprise
        LocalDate today = LocalDate.now();
        List<JournalEntry> todayEntries = journalEntryRepository.findByCompanyIdAndEntryDateBetween(
            companyId, today, today);
        
        int sequence = todayEntries.size() + 1;
        return baseNumber + String.format("%04d", sequence);
    }
}
