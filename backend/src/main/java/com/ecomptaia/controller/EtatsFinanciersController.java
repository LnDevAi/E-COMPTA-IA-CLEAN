package com.ecomptaia.controller;

import com.ecomptaia.entity.BalanceComptable;
import com.ecomptaia.entity.EtatFinancier;
import com.ecomptaia.entity.SoldeCompte;
import com.ecomptaia.service.BalanceComptableService;
import com.ecomptaia.service.EtatsFinanciersAutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des états financiers
 * Génération automatique de la balance, bilan, compte de résultat, etc.
 */
@RestController
@RequestMapping("/api/etats-financiers")
@CrossOrigin(origins = "*")
public class EtatsFinanciersController {
    
    @Autowired
    private BalanceComptableService balanceComptableService;
    
    @Autowired
    private EtatsFinanciersAutoService etatsFinanciersAutoService;
    
    // ==================== BALANCE COMPTABLE ====================
    
    /**
     * Générer automatiquement une balance comptable
     */
    @PostMapping("/balance/generer")
    public ResponseEntity<BalanceComptable> genererBalance(
            @RequestParam Long companyId,
            @RequestParam Long exerciceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateBalance,
            @RequestParam(defaultValue = "SYSCOHADA") String standardComptable) {
        
        try {
            BalanceComptable balance = balanceComptableService.genererBalanceAutomatique(
                companyId, exerciceId, dateBalance, standardComptable);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir toutes les balances d'une entreprise
     */
    @GetMapping("/balance/entreprise/{companyId}")
    public ResponseEntity<List<BalanceComptable>> getBalancesByCompany(@PathVariable Long companyId) {
        try {
            List<BalanceComptable> balances = balanceComptableService.getBalancesByCompany(companyId);
            return ResponseEntity.ok(balances);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir toutes les balances d'un exercice
     */
    @GetMapping("/balance/exercice/{exerciceId}")
    public ResponseEntity<List<BalanceComptable>> getBalancesByExercice(@PathVariable Long exerciceId) {
        try {
            List<BalanceComptable> balances = balanceComptableService.getBalancesByExercice(exerciceId);
            return ResponseEntity.ok(balances);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir une balance par ID
     */
    @GetMapping("/balance/{id}")
    public ResponseEntity<BalanceComptable> getBalanceById(@PathVariable Long id) {
        try {
            Optional<BalanceComptable> balance = balanceComptableService.getBalanceById(id);
            return balance.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir les soldes d'une balance
     */
    @GetMapping("/balance/{id}/soldes")
    public ResponseEntity<List<SoldeCompte>> getSoldesByBalance(@PathVariable Long id) {
        try {
            List<SoldeCompte> soldes = balanceComptableService.getSoldesByBalance(id);
            return ResponseEntity.ok(soldes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir les soldes par classe
     */
    @GetMapping("/balance/{id}/soldes/classe/{classe}")
    public ResponseEntity<List<SoldeCompte>> getSoldesByClasse(
            @PathVariable Long id, @PathVariable Integer classe) {
        try {
            List<SoldeCompte> soldes = balanceComptableService.getSoldesByClasse(id, classe);
            return ResponseEntity.ok(soldes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir les soldes par nature
     */
    @GetMapping("/balance/{id}/soldes/nature/{nature}")
    public ResponseEntity<List<SoldeCompte>> getSoldesByNature(
            @PathVariable Long id, @PathVariable String nature) {
        try {
            List<SoldeCompte> soldes = balanceComptableService.getSoldesByNature(id, nature);
            return ResponseEntity.ok(soldes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Valider une balance
     */
    @PostMapping("/balance/{id}/valider")
    public ResponseEntity<BalanceComptable> validerBalance(
            @PathVariable Long id, @RequestParam Long userId) {
        try {
            BalanceComptable balance = balanceComptableService.validerBalance(id, userId);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Publier une balance
     */
    @PostMapping("/balance/{id}/publier")
    public ResponseEntity<BalanceComptable> publierBalance(@PathVariable Long id) {
        try {
            BalanceComptable balance = balanceComptableService.publierBalance(id);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir les statistiques d'une balance
     */
    @GetMapping("/balance/{id}/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiquesBalance(@PathVariable Long id) {
        try {
            Map<String, Object> stats = balanceComptableService.getStatistiquesBalance(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // ==================== ÉTATS FINANCIERS ====================
    
    /**
     * Générer automatiquement tous les états financiers
     */
    @PostMapping("/generer-tous")
    public ResponseEntity<Map<String, Object>> genererTousLesEtatsFinanciers(
            @RequestParam Long companyId,
            @RequestParam Long exerciceId,
            @RequestParam(defaultValue = "SYSCOHADA") String standardComptable) {
        
        try {
            Map<String, Object> etats = etatsFinanciersAutoService.genererTousLesEtatsFinanciers(
                companyId, exerciceId, standardComptable);
            return ResponseEntity.ok(etats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Générer le Bilan SYSCOHADA
     */
    @PostMapping("/bilan/generer")
    public ResponseEntity<EtatFinancier> genererBilan(
            @RequestParam Long companyId,
            @RequestParam Long exerciceId,
            @RequestParam(defaultValue = "SYSCOHADA") String standardComptable) {
        
        try {
            EtatFinancier bilan = etatsFinanciersAutoService.genererBilan(
                companyId, exerciceId, standardComptable);
            return ResponseEntity.ok(bilan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Générer le Compte de Résultat SYSCOHADA
     */
    @PostMapping("/compte-resultat/generer")
    public ResponseEntity<EtatFinancier> genererCompteResultat(
            @RequestParam Long companyId,
            @RequestParam Long exerciceId,
            @RequestParam(defaultValue = "SYSCOHADA") String standardComptable) {
        
        try {
            EtatFinancier compteResultat = etatsFinanciersAutoService.genererCompteResultat(
                companyId, exerciceId, standardComptable);
            return ResponseEntity.ok(compteResultat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Générer le Tableau de Flux de Trésorerie
     */
    @PostMapping("/flux-tresorerie/generer")
    public ResponseEntity<EtatFinancier> genererTableauFluxTresorerie(
            @RequestParam Long companyId,
            @RequestParam Long exerciceId,
            @RequestParam(defaultValue = "SYSCOHADA") String standardComptable) {
        
        try {
            EtatFinancier tableauFlux = etatsFinanciersAutoService.genererTableauFluxTresorerie(
                companyId, exerciceId, standardComptable);
            return ResponseEntity.ok(tableauFlux);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Générer les Annexes
     */
    @PostMapping("/annexes/generer")
    public ResponseEntity<EtatFinancier> genererAnnexes(
            @RequestParam Long companyId,
            @RequestParam Long exerciceId,
            @RequestParam(defaultValue = "SYSCOHADA") String standardComptable) {
        
        try {
            EtatFinancier annexes = etatsFinanciersAutoService.genererAnnexes(
                companyId, exerciceId, standardComptable);
            return ResponseEntity.ok(annexes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir tous les états financiers d'un exercice
     */
    @GetMapping("/exercice/{exerciceId}")
    public ResponseEntity<List<EtatFinancier>> getEtatsFinanciersByExercice(@PathVariable Long exerciceId) {
        try {
            List<EtatFinancier> etats = etatsFinanciersAutoService.getEtatsFinanciersByExercice(exerciceId);
            return ResponseEntity.ok(etats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir un état financier par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EtatFinancier> getEtatFinancierById(@PathVariable Long id) {
        try {
            Optional<EtatFinancier> etat = etatsFinanciersAutoService.getEtatFinancierById(id);
            return etat.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir les états financiers par type
     */
    @GetMapping("/type/{typeEtat}")
    public ResponseEntity<List<EtatFinancier>> getEtatsFinanciersByType(@PathVariable String typeEtat) {
        try {
            List<EtatFinancier> etats = etatsFinanciersAutoService.getEtatsFinanciersByType(typeEtat);
            return ResponseEntity.ok(etats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtenir les états financiers par standard comptable
     */
    @GetMapping("/standard/{standardComptable}")
    public ResponseEntity<List<EtatFinancier>> getEtatsFinanciersByStandard(@PathVariable String standardComptable) {
        try {
            List<EtatFinancier> etats = etatsFinanciersAutoService.getEtatsFinanciersByStandard(standardComptable);
            return ResponseEntity.ok(etats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // ==================== RECHERCHE AVANCÉE ====================
    
    /**
     * Rechercher les balances avec filtres
     */
    @GetMapping("/balance/rechercher")
    public ResponseEntity<List<BalanceComptable>> rechercherBalances(
            @RequestParam Long companyId,
            @RequestParam(required = false) String standardComptable,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        try {
            List<BalanceComptable> balances = balanceComptableService.rechercherBalances(
                companyId, standardComptable, statut, dateDebut, dateFin);
            return ResponseEntity.ok(balances);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
