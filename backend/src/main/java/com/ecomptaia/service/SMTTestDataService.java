package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Service pour générer des données de test pour le système SMT
 */
@Service
public class SMTTestDataService {

    @Autowired
    private EntrepriseSMTRepository entrepriseSMTRepository;
    
    @Autowired
    private ExerciceSMTRepository exerciceSMTRepository;
    
    @Autowired
    private CompteTresorerieRepository compteTresorerieRepository;
    
    @Autowired
    private LivreRecetteRepository livreRecetteRepository;
    
    @Autowired
    private LivreDepenseRepository livreDepenseRepository;

    private final Random random = new Random();

    /**
     * Générer des données de test complètes pour SMT
     */
    public Map<String, Object> genererDonneesTestSMT() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. Créer une entreprise SMT
            EntrepriseSMT entreprise = creerEntrepriseTest();
            entrepriseSMTRepository.save(entreprise);
            
            // 2. Créer un exercice SMT
            ExerciceSMT exercice = creerExerciceTest(entreprise);
            exerciceSMTRepository.save(exercice);
            
            // 3. Créer des comptes de trésorerie
            creerComptesTresorerieTest(entreprise);
            
            // 4. Créer des recettes
            creerRecettesTest(entreprise, exercice);
            
            // 5. Créer des dépenses
            creerDepensesTest(entreprise, exercice);
            
            result.put("status", "SUCCESS");
            result.put("message", "Données de test SMT générées avec succès");
            result.put("entrepriseId", entreprise.getId());
            result.put("exerciceId", exercice.getId());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de la génération des données de test: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Créer une entreprise de test
     */
    private EntrepriseSMT creerEntrepriseTest() {
        EntrepriseSMT entreprise = new EntrepriseSMT("ENTREPRISE TEST SMT SARL", "NCT" + System.currentTimeMillis(), "CI", "PETITE", 50000000.0);
        entreprise.setNumeroRegistreCommerce("RC" + System.currentTimeMillis());
        entreprise.setAdresse("123 Avenue Test, Ville Test");
        entreprise.setTelephone("+225 0123456789");
        entreprise.setEmail("test@smt-entreprise.com");
        entreprise.setRepresentantLegal("M. Test Représentant");
        entreprise.setNumeroCNSS("CNSS" + System.currentTimeMillis());
        entreprise.setActivitePrincipale("Commerce");
        entreprise.setDevise("XOF");
        entreprise.setDateCreation(LocalDateTime.now());
        entreprise.setStatut("ACTIVE");
        entreprise.setVersion("1.0");
        
        return entreprise;
    }

    /**
     * Créer un exercice de test
     */
    private ExerciceSMT creerExerciceTest(EntrepriseSMT entreprise) {
        ExerciceSMT exercice = new ExerciceSMT(entreprise, 2024, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        exercice.setDevise("XOF");
        exercice.setEstCloture(false);
        exercice.setDateCreation(LocalDateTime.now());
        exercice.setStatut("OUVERT");
        exercice.setVersion("1.0");
        
        return exercice;
    }

    /**
     * Créer des comptes de trésorerie de test
     */
    private void creerComptesTresorerieTest(EntrepriseSMT entreprise) {
        // Compte Caisse
        CompteTresorerie caisse = new CompteTresorerie(entreprise, "Caisse principale", "CAISSE");
        caisse.setNumeroCompte("CAISSE-001");
        caisse.setSoldeInitial(new BigDecimal("500000"));
        caisse.setSoldeActuel(new BigDecimal("750000"));
        caisse.setDevise("XOF");
        caisse.setEstActif(true);
        caisse.setDateCreation(LocalDateTime.now());
        caisse.setStatut("ACTIVE");
        caisse.setVersion("1.0");
        compteTresorerieRepository.save(caisse);
        
        // Compte Bancaire
        CompteTresorerie banque = new CompteTresorerie(entreprise, "Compte bancaire principal", "BANQUE");
        banque.setNumeroCompte("BANQUE-001");
        banque.setNomBanque("Banque Test");
        banque.setSoldeInitial(new BigDecimal("2000000"));
        banque.setSoldeActuel(new BigDecimal("3500000"));
        banque.setDevise("XOF");
        banque.setEstActif(true);
        banque.setDateCreation(LocalDateTime.now());
        banque.setStatut("ACTIVE");
        banque.setVersion("1.0");
        compteTresorerieRepository.save(banque);
        
        // Compte Mobile Money
        CompteTresorerie mobileMoney = new CompteTresorerie(entreprise, "Compte Mobile Money", "MOBILE_MONEY");
        mobileMoney.setNumeroCompte("MOBILE-001");
        mobileMoney.setSoldeInitial(new BigDecimal("100000"));
        mobileMoney.setSoldeActuel(new BigDecimal("250000"));
        mobileMoney.setDevise("XOF");
        mobileMoney.setEstActif(true);
        mobileMoney.setDateCreation(LocalDateTime.now());
        mobileMoney.setStatut("ACTIVE");
        mobileMoney.setVersion("1.0");
        compteTresorerieRepository.save(mobileMoney);
    }

    /**
     * Créer des recettes de test
     */
    private void creerRecettesTest(EntrepriseSMT entreprise, ExerciceSMT exercice) {
        String[] types = {"VENTES", "SERVICES", "LOCATIONS", "COMMISSIONS"};
        String[] modes = {"ESPECES", "VIREMENT", "CHEQUE", "MOBILE_MONEY"};
        String[] tiers = {"Client Alpha", "Client Beta", "Client Gamma", "Client Delta"};
        
        // Récupérer les comptes de trésorerie
        java.util.List<CompteTresorerie> comptes = compteTresorerieRepository.findByEntrepriseIdOrderByNomCompte(entreprise.getId());
        
        for (int i = 1; i <= 20; i++) {
            LivreRecette recette = new LivreRecette(exercice, comptes.get(i % comptes.size()), LocalDate.now().minusDays(random.nextInt(30)), "Recette " + i, types[random.nextInt(types.length)], new BigDecimal(50000 + random.nextInt(200000)));
            recette.setNumeroPiece("REC-" + String.format("%06d", i));
            recette.setTiers(tiers[random.nextInt(tiers.length)]);
            recette.setModePaiement(modes[random.nextInt(modes.length)]);
            recette.setDateCreation(LocalDateTime.now());
            recette.setStatut("VALIDE");
            recette.setDevise("XOF");
            recette.setVersion("1.0");
            
            livreRecetteRepository.save(recette);
        }
    }

    /**
     * Créer des dépenses de test
     */
    private void creerDepensesTest(EntrepriseSMT entreprise, ExerciceSMT exercice) {
        String[] types = {"ACHATS", "SERVICES", "SALAIRES", "CHARGES_SOCIALES", "IMPOTS"};
        String[] modes = {"ESPECES", "VIREMENT", "CHEQUE", "MOBILE_MONEY"};
        String[] tiers = {"Fournisseur A", "Fournisseur B", "Fournisseur C", "Fournisseur D"};
        
        // Récupérer les comptes de trésorerie
        java.util.List<CompteTresorerie> comptes = compteTresorerieRepository.findByEntrepriseIdOrderByNomCompte(entreprise.getId());
        
        for (int i = 1; i <= 15; i++) {
            LivreDepense depense = new LivreDepense(exercice, comptes.get(i % comptes.size()), LocalDate.now().minusDays(random.nextInt(30)), "Dépense " + i, types[random.nextInt(types.length)], new BigDecimal(25000 + random.nextInt(150000)));
            depense.setNumeroPiece("DEP-" + String.format("%06d", i));
            depense.setTiers(tiers[random.nextInt(tiers.length)]);
            depense.setModePaiement(modes[random.nextInt(modes.length)]);
            depense.setDateCreation(LocalDateTime.now());
            depense.setStatut("VALIDE");
            depense.setDevise("XOF");
            depense.setVersion("1.0");
            
            livreDepenseRepository.save(depense);
        }
    }

    /**
     * Nettoyer les données de test
     */
    public Map<String, Object> nettoyerDonneesTestSMT() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Supprimer dans l'ordre pour respecter les contraintes de clés étrangères
            livreDepenseRepository.deleteAll();
            livreRecetteRepository.deleteAll();
            compteTresorerieRepository.deleteAll();
            exerciceSMTRepository.deleteAll();
            entrepriseSMTRepository.deleteAll();
            
            result.put("status", "SUCCESS");
            result.put("message", "Données de test SMT supprimées avec succès");
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de la suppression des données de test: " + e.getMessage());
        }
        
        return result;
    }
}
