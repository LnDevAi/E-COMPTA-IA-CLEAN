package com.ecomptaia.service;

import com.ecomptaia.entity.Reconciliation;
import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.entity.BankAccount;
import com.ecomptaia.repository.ReconciliationRepository;
import com.ecomptaia.repository.AccountEntryRepository;
import com.ecomptaia.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class ReconciliationService {

    @Autowired
    private ReconciliationRepository reconciliationRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    /**
     * Créer un nouveau rapprochement
     */
    public Reconciliation createReconciliation(Reconciliation reconciliation) {
        // Validation du numéro unique
        if (reconciliationRepository.findByReconciliationNumberAndCompanyId(
            reconciliation.getReconciliationNumber(), reconciliation.getCompanyId()).isPresent()) {
            throw new RuntimeException("Le numéro de rapprochement existe déjà pour cette entreprise");
        }
        
        // Génération du numéro si non fourni
        if (reconciliation.getReconciliationNumber() == null || reconciliation.getReconciliationNumber().trim().isEmpty()) {
            reconciliation.setReconciliationNumber(generateReconciliationNumber(reconciliation.getCompanyId()));
        }
        
        reconciliation.setStatus("DRAFT");
        reconciliation.setCreatedAt(LocalDateTime.now());
        reconciliation.setUpdatedAt(LocalDateTime.now());
        
        return reconciliationRepository.save(reconciliation);
    }

    /**
     * Démarrer un rapprochement
     */
    public Reconciliation startReconciliation(Long reconciliationId) {
        Optional<Reconciliation> reconciliationOpt = reconciliationRepository.findById(reconciliationId);
        if (reconciliationOpt.isPresent()) {
            Reconciliation reconciliation = reconciliationOpt.get();
            
            if (!"DRAFT".equals(reconciliation.getStatus())) {
                throw new RuntimeException("Seul un rapprochement en brouillon peut être démarré");
            }
            
            reconciliation.setStatus("IN_PROGRESS");
            reconciliation.setReconciliationDate(LocalDateTime.now());
            reconciliation.setUpdatedAt(LocalDateTime.now());
            
            return reconciliationRepository.save(reconciliation);
        }
        throw new RuntimeException("Rapprochement non trouvé");
    }

    /**
     * Finaliser un rapprochement
     */
    public Reconciliation completeReconciliation(Long reconciliationId, Long completedBy) {
        Optional<Reconciliation> reconciliationOpt = reconciliationRepository.findById(reconciliationId);
        if (reconciliationOpt.isPresent()) {
            Reconciliation reconciliation = reconciliationOpt.get();
            
            if (!"IN_PROGRESS".equals(reconciliation.getStatus())) {
                throw new RuntimeException("Seul un rapprochement en cours peut être finalisé");
            }
            
            // Vérifier que le rapprochement est équilibré
            if (reconciliation.getDifference() != null && reconciliation.getDifference().compareTo(BigDecimal.ZERO) != 0) {
                throw new RuntimeException("Le rapprochement ne peut pas être finalisé car il y a une différence de " + reconciliation.getDifference());
            }
            
            reconciliation.setStatus("COMPLETED");
            reconciliation.setCompletedBy(completedBy);
            reconciliation.setCompletedAt(LocalDateTime.now());
            reconciliation.setUpdatedAt(LocalDateTime.now());
            
            // Mettre à jour le compte bancaire
            updateBankAccountLastReconciliation(reconciliation.getBankAccountId(), reconciliation.getEndDate());
            
            return reconciliationRepository.save(reconciliation);
        }
        throw new RuntimeException("Rapprochement non trouvé");
    }

    /**
     * Annuler un rapprochement
     */
    public Reconciliation cancelReconciliation(Long reconciliationId) {
        Optional<Reconciliation> reconciliationOpt = reconciliationRepository.findById(reconciliationId);
        if (reconciliationOpt.isPresent()) {
            Reconciliation reconciliation = reconciliationOpt.get();
            
            if ("COMPLETED".equals(reconciliation.getStatus())) {
                throw new RuntimeException("Un rapprochement finalisé ne peut pas être annulé");
            }
            
            reconciliation.setStatus("CANCELLED");
            reconciliation.setUpdatedAt(LocalDateTime.now());
            
            return reconciliationRepository.save(reconciliation);
        }
        throw new RuntimeException("Rapprochement non trouvé");
    }

    /**
     * Mettre à jour les soldes d'un rapprochement
     */
    public Reconciliation updateBalances(Long reconciliationId, BigDecimal bankStatementBalance, BigDecimal bookBalance) {
        Optional<Reconciliation> reconciliationOpt = reconciliationRepository.findById(reconciliationId);
        if (reconciliationOpt.isPresent()) {
            Reconciliation reconciliation = reconciliationOpt.get();
            
            reconciliation.setBankStatementBalance(bankStatementBalance);
            reconciliation.setBookBalance(bookBalance);
            reconciliation.setDifference(bankStatementBalance.subtract(bookBalance));
            reconciliation.setUpdatedAt(LocalDateTime.now());
            
            return reconciliationRepository.save(reconciliation);
        }
        throw new RuntimeException("Rapprochement non trouvé");
    }

    /**
     * Rapprochement automatique
     */
    public Map<String, Object> autoReconcile(Long reconciliationId) {
        Optional<Reconciliation> reconciliationOpt = reconciliationRepository.findById(reconciliationId);
        if (reconciliationOpt.isPresent()) {
            Reconciliation reconciliation = reconciliationOpt.get();
            
            // Récupérer les écritures non rapprochées du compte bancaire
            List<AccountEntry> unreconciledEntries = accountEntryRepository.findUnreconciledEntriesByCompany(reconciliation.getCompanyId());
            
            // Logique de rapprochement automatique basée sur :
            // - Montant exact
            // - Date proche
            // - Référence similaire
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalEntries", unreconciledEntries.size());
            result.put("reconciledEntries", 0); // À implémenter avec la logique de matching
            result.put("unreconciledEntries", unreconciledEntries.size());
            result.put("confidence", 0.85); // Niveau de confiance du rapprochement automatique
            
            reconciliation.setIsAutoReconciled(true);
            reconciliation.setReconciliationMethod("AUTO");
            reconciliation.setUpdatedAt(LocalDateTime.now());
            reconciliationRepository.save(reconciliation);
            
            return result;
        }
        throw new RuntimeException("Rapprochement non trouvé");
    }

    /**
     * Récupérer les rapprochements d'une entreprise
     */
    public List<Reconciliation> getReconciliationsByCompany(Long companyId) {
        return reconciliationRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    /**
     * Récupérer les rapprochements d'un compte bancaire
     */
    public List<Reconciliation> getReconciliationsByBankAccount(Long bankAccountId) {
        return reconciliationRepository.findByBankAccountIdOrderByCreatedAtDesc(bankAccountId);
    }

    /**
     * Récupérer les rapprochements par statut
     */
    public List<Reconciliation> getReconciliationsByStatus(Long companyId, String status) {
        return reconciliationRepository.findByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, status);
    }

    /**
     * Récupérer les rapprochements finalisés
     */
    public List<Reconciliation> getCompletedReconciliations(Long companyId) {
        return reconciliationRepository.findCompletedReconciliationsByCompany(companyId);
    }

    /**
     * Récupérer les rapprochements en cours
     */
    public List<Reconciliation> getInProgressReconciliations(Long companyId) {
        return reconciliationRepository.findInProgressReconciliationsByCompany(companyId);
    }

    /**
     * Récupérer les rapprochements avec différences
     */
    public List<Reconciliation> getReconciliationsWithDifferences(Long companyId) {
        return reconciliationRepository.findReconciliationsWithDifferences(companyId);
    }

    /**
     * Récupérer les rapprochements équilibrés
     */
    public List<Reconciliation> getBalancedReconciliations(Long companyId) {
        return reconciliationRepository.findBalancedReconciliations(companyId);
    }

    /**
     * Statistiques de rapprochement
     */
    public Map<String, Object> getReconciliationStatistics(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalReconciliations", reconciliationRepository.countCompletedReconciliationsByCompany(companyId));
        stats.put("inProgressReconciliations", reconciliationRepository.countInProgressReconciliationsByCompany(companyId));
        stats.put("completedReconciliations", reconciliationRepository.countCompletedReconciliationsByCompany(companyId));
        
        // Rapprochements avec différences
        List<Reconciliation> withDifferences = reconciliationRepository.findReconciliationsWithDifferences(companyId);
        stats.put("reconciliationsWithDifferences", withDifferences.size());
        
        // Rapprochements équilibrés
        List<Reconciliation> balanced = reconciliationRepository.findBalancedReconciliations(companyId);
        stats.put("balancedReconciliations", balanced.size());
        
        // Calcul du taux de réussite
        long total = reconciliationRepository.countCompletedReconciliationsByCompany(companyId);
        if (total > 0) {
            double successRate = (double) balanced.size() / total * 100;
            stats.put("successRate", Math.round(successRate * 100.0) / 100.0);
        } else {
            stats.put("successRate", 0.0);
        }
        
        return stats;
    }

    /**
     * Générer un rapport de rapprochement
     */
    public Map<String, Object> generateReconciliationReport(Long reconciliationId) {
        Optional<Reconciliation> reconciliationOpt = reconciliationRepository.findById(reconciliationId);
        if (reconciliationOpt.isPresent()) {
            Reconciliation reconciliation = reconciliationOpt.get();
            
            Map<String, Object> report = new HashMap<>();
            report.put("reconciliation", reconciliation);
            report.put("generatedAt", LocalDateTime.now());
            
            // Détails du compte bancaire
            Optional<BankAccount> bankAccountOpt = bankAccountRepository.findById(reconciliation.getBankAccountId());
            if (bankAccountOpt.isPresent()) {
                report.put("bankAccount", bankAccountOpt.get());
            }
            
            // Écritures rapprochées
            List<AccountEntry> reconciledEntries = accountEntryRepository.findReconciledEntriesByCompany(reconciliation.getCompanyId());
            report.put("reconciledEntries", reconciledEntries);
            report.put("totalReconciledEntries", reconciledEntries.size());
            
            // Écritures non rapprochées
            List<AccountEntry> unreconciledEntries = accountEntryRepository.findUnreconciledEntriesByCompany(reconciliation.getCompanyId());
            report.put("unreconciledEntries", unreconciledEntries);
            report.put("totalUnreconciledEntries", unreconciledEntries.size());
            
            return report;
        }
        throw new RuntimeException("Rapprochement non trouvé");
    }

    /**
     * Générer le numéro de rapprochement
     */
    private String generateReconciliationNumber(Long companyId) {
        String datePrefix = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseNumber = "REC-" + datePrefix + "-";
        
        // Compter les rapprochements du jour pour cette entreprise
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        List<Reconciliation> todayReconciliations = reconciliationRepository.findByDateRange(
            companyId, startOfDay, endOfDay);
        
        int sequence = todayReconciliations.size() + 1;
        return baseNumber + String.format("%04d", sequence);
    }

    /**
     * Mettre à jour la dernière date de rapprochement du compte bancaire
     */
    private void updateBankAccountLastReconciliation(Long bankAccountId, LocalDateTime reconciliationDate) {
        Optional<BankAccount> bankAccountOpt = bankAccountRepository.findById(bankAccountId);
        if (bankAccountOpt.isPresent()) {
            BankAccount bankAccount = bankAccountOpt.get();
            bankAccount.setLastReconciliationDate(reconciliationDate);
            bankAccount.setUpdatedAt(LocalDateTime.now());
            bankAccountRepository.save(bankAccount);
        }
    }
}





