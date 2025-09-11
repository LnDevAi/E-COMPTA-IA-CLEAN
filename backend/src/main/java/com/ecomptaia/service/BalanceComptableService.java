package com.ecomptaia.service;

import com.ecomptaia.entity.EcritureComptable;
import com.ecomptaia.entity.LigneEcriture;
import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.Account;
import com.ecomptaia.entity.FinancialPeriod;
import com.ecomptaia.entity.BalanceComptable;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BalanceComptableService {
    
    private final BalanceComptableRepository balanceRepository;
    private final SoldeCompteRepository soldeRepository;
    private final EcritureComptableRepository ecritureRepository;
    private final LigneEcritureRepository ligneRepository;
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;
    private final FinancialPeriodRepository periodRepository;
    
    @Autowired
    public BalanceComptableService(BalanceComptableRepository balanceRepository,
                                 SoldeCompteRepository soldeRepository,
                                 EcritureComptableRepository ecritureRepository,
                                 LigneEcritureRepository ligneRepository,
                                 AccountRepository accountRepository,
                                 CompanyRepository companyRepository,
                                 FinancialPeriodRepository periodRepository) {
        this.balanceRepository = balanceRepository;
        this.soldeRepository = soldeRepository;
        this.ecritureRepository = ecritureRepository;
        this.ligneRepository = ligneRepository;
        this.accountRepository = accountRepository;
        this.companyRepository = companyRepository;
        this.periodRepository = periodRepository;
    }
    
    // ==================== CRUD BASIQUE ====================
    
    /**
     * Créer une nouvelle balance comptable
     */
    public BalanceComptable createBalance(BalanceComptable balance) {
        balance.setDateCreation(LocalDateTime.now());
        balance.setStatut("DRAFT");
        return balanceRepository.save(balance);
    }
    
    /**
     * Obtenir une balance par ID
     */
    public Optional<BalanceComptable> getBalanceById(Long id) {
        return balanceRepository.findById(id);
    }
    
    /**
     * Obtenir toutes les balances d'une entreprise
     */
    public List<BalanceComptable> getBalancesByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
        return balanceRepository.findByCompanyOrderByDateBalanceDesc(company);
    }
    
    /**
     * Obtenir toutes les balances d'un exercice
     */
    public List<BalanceComptable> getBalancesByExercice(Long exerciceId) {
        FinancialPeriod exercice = periodRepository.findById(exerciceId)
            .orElseThrow(() -> new RuntimeException("Exercice non trouvé"));
        return balanceRepository.findByExerciceOrderByDateBalanceDesc(exercice);
    }
    
    /**
     * Supprimer une balance
     */
    public void deleteBalance(Long id) {
        BalanceComptable balance = balanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Balance non trouvée"));
        soldeRepository.deleteByBalance(balance);
        balanceRepository.delete(balance);
    }
    
    // ==================== GÉNÉRATION AUTOMATIQUE ====================
    
    /**
     * Générer automatiquement une balance comptable
     */
    public BalanceComptable genererBalanceAutomatique(Long companyId, Long exerciceId, 
                                                     LocalDate dateBalance, String standardComptable) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
        FinancialPeriod exercice = periodRepository.findById(exerciceId)
            .orElseThrow(() -> new RuntimeException("Exercice non trouvé"));
        if (balanceRepository.existsByCompanyAndDateBalance(company, dateBalance)) {
            throw new RuntimeException("Une balance existe déjà pour cette date");
        }
        BalanceComptable balance = new BalanceComptable(company, exercice, dateBalance, standardComptable);
        balance.setDateDebut(exercice.getStartDate());
        balance.setDateFin(exercice.getEndDate());
        balance.setStatut("GENERATED");
        balance.setDateCreation(LocalDateTime.now());
        genererSoldesComptes(balance);
        calculerTotauxBalance(balance);
        balance.verifierEquilibre();
        return balanceRepository.save(balance);
    }
    
    private void genererSoldesComptes(BalanceComptable balance) {
        Company company = balance.getCompany();
        LocalDate dateDebut = balance.getDateDebut();
        LocalDate dateFin = balance.getDateFin();
        List<Account> comptes = accountRepository.findByCompanyAndIsActiveTrueOrderByAccountNumberAsc(company);
        for (Account compte : comptes) {
            SoldeCompte solde = new SoldeCompte(balance, compte);
            calculerSoldesDebut(solde, dateDebut);
            calculerMouvements(solde, dateDebut, dateFin);
            solde.calculerSoldeFinal();
            soldeRepository.save(solde);
        }
    }
    
    private void calculerSoldesDebut(SoldeCompte solde, LocalDate dateDebut) {
        Account compte = solde.getCompte();
        Company company = compte.getCompany();
        List<EcritureComptable> ecrituresAvant = ecritureRepository.findByEntrepriseOrderByDateEcritureDesc(company)
            .stream()
            .filter(e -> e.getDateEcriture().isBefore(dateDebut))
            .collect(Collectors.toList());
        BigDecimal soldeDebutDebit = BigDecimal.ZERO;
        BigDecimal soldeDebutCredit = BigDecimal.ZERO;
        for (EcritureComptable ecriture : ecrituresAvant) {
            List<LigneEcriture> lignes = ligneRepository.findByEcritureAndCompte(ecriture, compte);
            for (LigneEcriture ligne : lignes) {
                if (ligne.getDebit() != null) {
                    soldeDebutDebit = soldeDebutDebit.add(ligne.getDebit());
                }
                if (ligne.getCredit() != null) {
                    soldeDebutCredit = soldeDebutCredit.add(ligne.getCredit());
                }
            }
        }
        if (compte.getOpeningBalance() != null) {
            if (compte.getOpeningBalance().compareTo(BigDecimal.ZERO) > 0) {
                soldeDebutDebit = soldeDebutDebit.add(compte.getOpeningBalance());
            } else {
                soldeDebutCredit = soldeDebutCredit.add(compte.getOpeningBalance().abs());
            }
        }
        solde.setSoldeDebutDebit(soldeDebutDebit);
        solde.setSoldeDebutCredit(soldeDebutCredit);
    }
    
    private void calculerMouvements(SoldeCompte solde, LocalDate dateDebut, LocalDate dateFin) {
        Account compte = solde.getCompte();
        Company company = compte.getCompany();
        List<EcritureComptable> ecritures = ecritureRepository.findByEntrepriseOrderByDateEcritureDesc(company)
            .stream()
            .filter(e -> !e.getDateEcriture().isBefore(dateDebut) && !e.getDateEcriture().isAfter(dateFin))
            .collect(Collectors.toList());
        BigDecimal mouvementDebit = BigDecimal.ZERO;
        BigDecimal mouvementCredit = BigDecimal.ZERO;
        int nombreMouvements = 0;
        LocalDate dateDernierMouvement = null;
        for (EcritureComptable ecriture : ecritures) {
            List<LigneEcriture> lignes = ligneRepository.findByEcritureAndCompte(ecriture, compte);
            for (LigneEcriture ligne : lignes) {
                if (ligne.getDebit() != null) {
                    mouvementDebit = mouvementDebit.add(ligne.getDebit());
                }
                if (ligne.getCredit() != null) {
                    mouvementCredit = mouvementCredit.add(ligne.getCredit());
                }
                nombreMouvements++;
                if (dateDernierMouvement == null || ecriture.getDateEcriture().isAfter(dateDernierMouvement)) {
                    dateDernierMouvement = ecriture.getDateEcriture();
                }
            }
        }
        solde.setMouvementDebit(mouvementDebit);
        solde.setMouvementCredit(mouvementCredit);
        solde.setNombreMouvements(nombreMouvements);
        solde.setDateDernierMouvement(dateDernierMouvement);
    }
    
    private void calculerTotauxBalance(BalanceComptable balance) {
        List<SoldeCompte> soldes = soldeRepository.findByBalanceOrderByNumeroCompte(balance);
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal soldeDebit = BigDecimal.ZERO;
        BigDecimal soldeCredit = BigDecimal.ZERO;
        int nombreComptes = soldes.size();
        int nombreMouvements = 0;
        for (SoldeCompte solde : soldes) {
            if (solde.getMouvementDebit() != null) {
                totalDebit = totalDebit.add(solde.getMouvementDebit());
            }
            if (solde.getMouvementCredit() != null) {
                totalCredit = totalCredit.add(solde.getMouvementCredit());
            }
            if (solde.getSoldeFinDebit() != null) {
                soldeDebit = soldeDebit.add(solde.getSoldeFinDebit());
            }
            if (solde.getSoldeFinCredit() != null) {
                soldeCredit = soldeCredit.add(solde.getSoldeFinCredit());
            }
            if (solde.getNombreMouvements() != null) {
                nombreMouvements += solde.getNombreMouvements();
            }
        }
        balance.setTotalDebit(totalDebit);
        balance.setTotalCredit(totalCredit);
        balance.setSoldeDebit(soldeDebit);
        balance.setSoldeCredit(soldeCredit);
        balance.setNombreComptes(nombreComptes);
        balance.setNombreMouvements(nombreMouvements);
    }
    
    // ==================== VALIDATION ET PUBLICATION ====================
    public BalanceComptable validerBalance(Long balanceId, Long userId) {
        BalanceComptable balance = balanceRepository.findById(balanceId)
            .orElseThrow(() -> new RuntimeException("Balance non trouvée"));
        balance.valider(userId);
        balance.setDateModification(LocalDateTime.now());
        return balanceRepository.save(balance);
    }
    
    public BalanceComptable publierBalance(Long balanceId) {
        BalanceComptable balance = balanceRepository.findById(balanceId)
            .orElseThrow(() -> new RuntimeException("Balance non trouvée"));
        if (!balance.isValidated()) {
            throw new RuntimeException("La balance doit être validée avant publication");
        }
        balance.publier();
        return balanceRepository.save(balance);
    }
    
    // ==================== RECHERCHE ET FILTRAGE ====================
    public List<BalanceComptable> rechercherBalances(Long companyId, String standardComptable, 
                                                    String statut, LocalDate dateDebut, LocalDate dateFin) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
        List<BalanceComptable> balances = balanceRepository.findByCompanyOrderByDateBalanceDesc(company);
        return balances.stream()
            .filter(b -> standardComptable == null || standardComptable.equals(b.getStandardComptable()))
            .filter(b -> statut == null || statut.equals(b.getStatut()))
            .filter(b -> dateDebut == null || !b.getDateBalance().isBefore(dateDebut))
            .filter(b -> dateFin == null || !b.getDateBalance().isAfter(dateFin))
            .collect(Collectors.toList());
    }
    
    public List<SoldeCompte> getSoldesByBalance(Long balanceId) {
        BalanceComptable balance = balanceRepository.findById(balanceId)
            .orElseThrow(() -> new RuntimeException("Balance non trouvée"));
        return soldeRepository.findByBalanceOrderByNumeroCompte(balance);
    }
    
    public List<SoldeCompte> getSoldesByClasse(Long balanceId, Integer classe) {
        BalanceComptable balance = balanceRepository.findById(balanceId)
            .orElseThrow(() -> new RuntimeException("Balance non trouvée"));
        return soldeRepository.findByBalanceAndClasse(balance, classe);
    }
    
    public List<SoldeCompte> getSoldesByNature(Long balanceId, String nature) {
        BalanceComptable balance = balanceRepository.findById(balanceId)
            .orElseThrow(() -> new RuntimeException("Balance non trouvée"));
        return soldeRepository.findByBalanceAndNature(balance, nature);
    }
    
    public Map<String, Object> getStatistiquesBalance(Long balanceId) {
        BalanceComptable balance = balanceRepository.findById(balanceId)
            .orElseThrow(() -> new RuntimeException("Balance non trouvée"));
        Map<String, Object> stats = new HashMap<>();
        stats.put("balance", balance);
        stats.put("totalDebit", balance.getTotalDebit());
        stats.put("totalCredit", balance.getTotalCredit());
        stats.put("soldeDebit", balance.getSoldeDebit());
        stats.put("soldeCredit", balance.getSoldeCredit());
        stats.put("equilibre", balance.getEquilibre());
        stats.put("nombreComptes", balance.getNombreComptes());
        stats.put("nombreMouvements", balance.getNombreMouvements());
        Map<Integer, Long> statsParClasse = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            long count = soldeRepository.findByBalanceAndClasse(balance, i).size();
            statsParClasse.put(i, count);
        }
        stats.put("statsParClasse", statsParClasse);
        Map<String, Long> statsParNature = new HashMap<>();
        String[] natures = {"ACTIF", "PASSIF", "CHARGES", "PRODUITS"};
        for (String nature : natures) {
            long count = soldeRepository.findByBalanceAndNature(balance, nature).size();
            statsParNature.put(nature, count);
        }
        return stats;
    }
}




