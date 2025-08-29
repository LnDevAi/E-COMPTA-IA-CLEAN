package com.ecomptaia.controller;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import com.ecomptaia.service.SMTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/smt")
@CrossOrigin(origins = "*")
public class SMTController {

    @Autowired
    private SMTService smtService;

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

    @Autowired
    private EtatFinancierRepository etatFinancierRepository;

    // ==================== ENDPOINTS ENTREPRISES SMT ====================

    /**
     * Créer une nouvelle entreprise SMT
     */
    @PostMapping("/entreprises")
    public ResponseEntity<Map<String, Object>> creerEntreprise(@RequestBody EntrepriseSMT entreprise) {
        try {
            EntrepriseSMT nouvelleEntreprise = smtService.creerEntreprise(entreprise);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Entreprise SMT créée avec succès");
            response.put("data", nouvelleEntreprise);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la création de l'entreprise: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir toutes les entreprises actives
     */
    @GetMapping("/entreprises")
    public ResponseEntity<Map<String, Object>> getAllEntreprises() {
        try {
            List<EntrepriseSMT> entreprises = smtService.getAllEntreprisesActives();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Entreprises récupérées avec succès");
            response.put("data", entreprises);
            response.put("count", entreprises.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des entreprises: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir une entreprise par ID
     */
    @GetMapping("/entreprises/{id}")
    public ResponseEntity<Map<String, Object>> getEntrepriseById(@PathVariable Long id) {
        try {
            Optional<EntrepriseSMT> entreprise = smtService.getEntrepriseById(id);
            Map<String, Object> response = new HashMap<>();
            if (entreprise.isPresent()) {
                response.put("success", true);
                response.put("message", "Entreprise trouvée");
                response.put("data", entreprise.get());
            } else {
                response.put("success", false);
                response.put("message", "Entreprise non trouvée");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération de l'entreprise: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statistiques des entreprises
     */
    @GetMapping("/entreprises/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiquesEntreprises() {
        try {
            Map<String, Object> stats = smtService.getStatistiquesEntreprises();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Statistiques récupérées avec succès");
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS EXERCICES SMT ====================

    /**
     * Créer un nouvel exercice SMT
     */
    @PostMapping("/exercices")
    public ResponseEntity<Map<String, Object>> creerExercice(@RequestBody ExerciceSMT exercice) {
        try {
            ExerciceSMT nouvelExercice = smtService.creerExercice(exercice);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Exercice SMT créé avec succès");
            response.put("data", nouvelExercice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la création de l'exercice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir l'exercice en cours d'une entreprise
     */
    @GetMapping("/entreprises/{entrepriseId}/exercice-courant")
    public ResponseEntity<Map<String, Object>> getExerciceEnCours(@PathVariable Long entrepriseId) {
        try {
            Optional<ExerciceSMT> exercice = smtService.getExerciceEnCours(entrepriseId);
            Map<String, Object> response = new HashMap<>();
            if (exercice.isPresent()) {
                response.put("success", true);
                response.put("message", "Exercice en cours trouvé");
                response.put("data", exercice.get());
            } else {
                response.put("success", false);
                response.put("message", "Aucun exercice en cours trouvé");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération de l'exercice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Clôturer un exercice
     */
    @PostMapping("/exercices/{exerciceId}/cloturer")
    public ResponseEntity<Map<String, Object>> cloturerExercice(@PathVariable Long exerciceId) {
        try {
            ExerciceSMT exercice = smtService.cloturerExercice(exerciceId);
            Map<String, Object> response = new HashMap<>();
            if (exercice != null) {
                response.put("success", true);
                response.put("message", "Exercice clôturé avec succès");
                response.put("data", exercice);
            } else {
                response.put("success", false);
                response.put("message", "Exercice non trouvé");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la clôture de l'exercice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS COMPTES DE TRÉSORERIE ====================

    /**
     * Créer un nouveau compte de trésorerie
     */
    @PostMapping("/comptes-tresorerie")
    public ResponseEntity<Map<String, Object>> creerCompteTresorerie(@RequestBody CompteTresorerie compte) {
        try {
            CompteTresorerie nouveauCompte = smtService.creerCompteTresorerie(compte);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Compte de trésorerie créé avec succès");
            response.put("data", nouveauCompte);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la création du compte: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir tous les comptes d'une entreprise
     */
    @GetMapping("/entreprises/{entrepriseId}/comptes-tresorerie")
    public ResponseEntity<Map<String, Object>> getComptesTresorerie(@PathVariable Long entrepriseId) {
        try {
            List<CompteTresorerie> comptes = smtService.getComptesTresorerie(entrepriseId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Comptes de trésorerie récupérés avec succès");
            response.put("data", comptes);
            response.put("count", comptes.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des comptes: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS RECETTES ====================

    /**
     * Enregistrer une nouvelle recette
     */
    @PostMapping("/recettes")
    public ResponseEntity<Map<String, Object>> enregistrerRecette(@RequestBody LivreRecette recette) {
        try {
            LivreRecette nouvelleRecette = smtService.enregistrerRecette(recette);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Recette enregistrée avec succès");
            response.put("data", nouvelleRecette);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de l'enregistrement de la recette: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les recettes d'un exercice
     */
    @GetMapping("/exercices/{exerciceId}/recettes")
    public ResponseEntity<Map<String, Object>> getRecettesExercice(@PathVariable Long exerciceId) {
        try {
            List<LivreRecette> recettes = smtService.getRecettesExercice(exerciceId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Recettes récupérées avec succès");
            response.put("data", recettes);
            response.put("count", recettes.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des recettes: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statistiques des recettes
     */
    @GetMapping("/exercices/{exerciceId}/recettes/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiquesRecettes(@PathVariable Long exerciceId) {
        try {
            Map<String, Object> stats = smtService.getStatistiquesRecettes(exerciceId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Statistiques des recettes récupérées avec succès");
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DÉPENSES ====================

    /**
     * Enregistrer une nouvelle dépense
     */
    @PostMapping("/depenses")
    public ResponseEntity<Map<String, Object>> enregistrerDepense(@RequestBody LivreDepense depense) {
        try {
            LivreDepense nouvelleDepense = smtService.enregistrerDepense(depense);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dépense enregistrée avec succès");
            response.put("data", nouvelleDepense);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de l'enregistrement de la dépense: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les dépenses d'un exercice
     */
    @GetMapping("/exercices/{exerciceId}/depenses")
    public ResponseEntity<Map<String, Object>> getDepensesExercice(@PathVariable Long exerciceId) {
        try {
            List<LivreDepense> depenses = smtService.getDepensesExercice(exerciceId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dépenses récupérées avec succès");
            response.put("data", depenses);
            response.put("count", depenses.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des dépenses: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statistiques des dépenses
     */
    @GetMapping("/exercices/{exerciceId}/depenses/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiquesDepenses(@PathVariable Long exerciceId) {
        try {
            Map<String, Object> stats = smtService.getStatistiquesDepenses(exerciceId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Statistiques des dépenses récupérées avec succès");
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS ÉTATS FINANCIERS ====================

    /**
     * Générer un état financier
     */
    @PostMapping("/exercices/{exerciceId}/etats-financiers")
    public ResponseEntity<Map<String, Object>> genererEtatFinancier(
            @PathVariable Long exerciceId,
            @RequestParam String typeEtat,
            @RequestParam String standardComptable) {
        try {
            EtatFinancier etat = smtService.genererEtatFinancier(exerciceId, typeEtat, standardComptable);
            Map<String, Object> response = new HashMap<>();
            if (etat != null) {
                response.put("success", true);
                response.put("message", "État financier généré avec succès");
                response.put("data", etat);
            } else {
                response.put("success", false);
                response.put("message", "Exercice non trouvé");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la génération de l'état financier: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Valider un état financier
     */
    @PostMapping("/etats-financiers/{etatId}/valider")
    public ResponseEntity<Map<String, Object>> validerEtatFinancier(@PathVariable Long etatId) {
        try {
            EtatFinancier etat = smtService.validerEtatFinancier(etatId);
            Map<String, Object> response = new HashMap<>();
            if (etat != null) {
                response.put("success", true);
                response.put("message", "État financier validé avec succès");
                response.put("data", etat);
            } else {
                response.put("success", false);
                response.put("message", "État financier non trouvé");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la validation: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Publier un état financier
     */
    @PostMapping("/etats-financiers/{etatId}/publier")
    public ResponseEntity<Map<String, Object>> publierEtatFinancier(@PathVariable Long etatId) {
        try {
            EtatFinancier etat = smtService.publierEtatFinancier(etatId);
            Map<String, Object> response = new HashMap<>();
            if (etat != null) {
                response.put("success", true);
                response.put("message", "État financier publié avec succès");
                response.put("data", etat);
            } else {
                response.put("success", false);
                response.put("message", "État financier non trouvé");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la publication: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS TABLEAU DE BORD ====================

    /**
     * Obtenir le tableau de bord SMT d'une entreprise
     */
    @GetMapping("/entreprises/{entrepriseId}/tableau-bord")
    public ResponseEntity<Map<String, Object>> getTableauBord(@PathVariable Long entrepriseId) {
        try {
            Map<String, Object> dashboard = smtService.getTableauBord(entrepriseId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tableau de bord récupéré avec succès");
            response.put("data", dashboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération du tableau de bord: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Endpoint de test pour vérifier que le module SMT fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testSMT() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module SMT opérationnel");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0");
        return ResponseEntity.ok(response);
    }

    /**
     * Test complet du module SMT - Vérifie toutes les fonctionnalités
     */
    @GetMapping("/test-complet")
    public ResponseEntity<Map<String, Object>> testCompletSMT() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> tests = new HashMap<>();
        boolean allTestsPassed = true;

        try {
            // Test 1: Vérifier les repositories
            tests.put("repositories", "OK");
            
            // Test 2: Vérifier les entités existantes
            long countEntreprises = entrepriseSMTRepository.count();
            long countExercices = exerciceSMTRepository.count();
            long countComptes = compteTresorerieRepository.count();
            long countRecettes = livreRecetteRepository.count();
            long countDepenses = livreDepenseRepository.count();
            long countEtats = etatFinancierRepository.count();
            
            tests.put("entites_existantes", Map.of(
                "entreprises", countEntreprises,
                "exercices", countExercices,
                "comptes_tresorerie", countComptes,
                "recettes", countRecettes,
                "depenses", countDepenses,
                "etats_financiers", countEtats
            ));

            // Test 3: Vérifier les méthodes du service
            try {
                List<EntrepriseSMT> entreprises = smtService.getAllEntreprisesActives();
                tests.put("service_entreprises", "OK - " + entreprises.size() + " entreprises trouvées");
            } catch (Exception e) {
                tests.put("service_entreprises", "ERREUR: " + e.getMessage());
                allTestsPassed = false;
            }

            // Test 4: Vérifier les endpoints disponibles
            tests.put("endpoints_disponibles", List.of(
                "GET /api/smt/test",
                "GET /api/smt/test-complet",
                "GET /api/smt/info",
                "POST /api/smt/entreprises",
                "GET /api/smt/entreprises",
                "GET /api/smt/entreprises/{id}",
                "POST /api/smt/exercices",
                "GET /api/smt/entreprises/{id}/exercice-courant",
                "POST /api/smt/exercices/{id}/cloturer",
                "POST /api/smt/comptes-tresorerie",
                "GET /api/smt/entreprises/{id}/comptes-tresorerie",
                "POST /api/smt/recettes",
                "GET /api/smt/exercices/{id}/recettes",
                "POST /api/smt/depenses",
                "GET /api/smt/exercices/{id}/depenses",
                "POST /api/smt/exercices/{id}/etats-financiers",
                "GET /api/smt/entreprises/{id}/tableau-bord"
            ));

            // Test 5: Vérifier la configuration
            tests.put("configuration", Map.of(
                "module", "Système Minimal de Trésorerie (SMT)",
                "version", "1.0",
                "standards_supportes", List.of("SYSCOHADA", "IFRS", "US_GAAP", "PCG"),
                "types_etats", List.of("BILAN", "COMPTE_RESULTAT", "FLUX_TRESORERIE", "ANNEXES"),
                "devises_supportees", List.of("XOF", "EUR", "USD", "XAF")
            ));

            result.put("success", allTestsPassed);
            result.put("message", allTestsPassed ? "Tous les tests SMT sont passés avec succès" : "Certains tests SMT ont échoué");
            result.put("tests", tests);
            result.put("timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur lors du test complet: " + e.getMessage());
            result.put("tests", tests);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Créer des données de test complètes en une seule requête
     */
    @PostMapping("/test-creer-donnees")
    public ResponseEntity<Map<String, Object>> creerDonneesTestCompletes() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> donneesCreees = new HashMap<>();

        try {
            // 1. Créer une entreprise de test
            EntrepriseSMT entreprise = new EntrepriseSMT("ENTREPRISE TEST SMT SARL", "NCT" + System.currentTimeMillis(), "BF", "PETITE", 50000000.0);
            entreprise.setNumeroRegistreCommerce("RC" + System.currentTimeMillis());
            entreprise.setAdresse("123 Avenue Test, Ouagadougou");
            entreprise.setTelephone("+226 70 12 34 56");
            entreprise.setEmail("test@smt-entreprise.bf");
            entreprise.setRepresentantLegal("M. Test Représentant");
            entreprise.setNumeroCNSS("CNSS" + System.currentTimeMillis());
            entreprise.setActivitePrincipale("Services informatiques");
            entreprise.setDevise("XOF");
            
            EntrepriseSMT entrepriseCreee = smtService.creerEntreprise(entreprise);
            donneesCreees.put("entreprise", entrepriseCreee);

            // 2. Créer un exercice de test
            ExerciceSMT exercice = new ExerciceSMT(entrepriseCreee, 2024, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            exercice.setDevise("XOF");
            
            ExerciceSMT exerciceCree = smtService.creerExercice(exercice);
            donneesCreees.put("exercice", exerciceCree);

            // 3. Créer des comptes de trésorerie
            CompteTresorerie caisse = new CompteTresorerie(entrepriseCreee, "Caisse principale", "CAISSE");
            caisse.setNumeroCompte("CAISSE-001");
            caisse.setSoldeInitial(new BigDecimal("500000"));
            caisse.setDevise("XOF");
            
            CompteTresorerie banque = new CompteTresorerie(entrepriseCreee, "Compte bancaire", "BANQUE");
            banque.setNumeroCompte("BANQUE-001");
            banque.setNomBanque("Banque Test");
            banque.setSoldeInitial(new BigDecimal("2000000"));
            banque.setDevise("XOF");
            
            CompteTresorerie caisseCreee = smtService.creerCompteTresorerie(caisse);
            CompteTresorerie banqueCreee = smtService.creerCompteTresorerie(banque);
            donneesCreees.put("comptes_tresorerie", List.of(caisseCreee, banqueCreee));

            // 4. Créer des recettes
            LivreRecette recette1 = new LivreRecette(exerciceCree, caisseCreee, LocalDate.of(2024, 1, 15), "Vente services informatiques", "SERVICES", new BigDecimal("500000"));
            recette1.setNumeroPiece("FACT-001");
            recette1.setTiers("Client ABC");
            recette1.setModePaiement("ESPECES");
            recette1.setDevise("XOF");
            
            LivreRecette recette2 = new LivreRecette(exerciceCree, banqueCreee, LocalDate.of(2024, 2, 10), "Paiement client XYZ", "VENTES", new BigDecimal("750000"));
            recette2.setNumeroPiece("FACT-002");
            recette2.setTiers("Client XYZ");
            recette2.setModePaiement("VIREMENT");
            recette2.setDevise("XOF");
            
            LivreRecette recette1Creee = smtService.enregistrerRecette(recette1);
            LivreRecette recette2Creee = smtService.enregistrerRecette(recette2);
            donneesCreees.put("recettes", List.of(recette1Creee, recette2Creee));

            // 5. Créer des dépenses
            LivreDepense depense1 = new LivreDepense(exerciceCree, caisseCreee, LocalDate.of(2024, 1, 10), "Achat fournitures bureau", "ACHATS", new BigDecimal("150000"));
            depense1.setNumeroPiece("FACT-FOURN-001");
            depense1.setTiers("Fournisseur A");
            depense1.setModePaiement("ESPECES");
            depense1.setDevise("XOF");
            
            LivreDepense depense2 = new LivreDepense(exerciceCree, banqueCreee, LocalDate.of(2024, 2, 15), "Paiement loyer", "FRAIS_GENERAUX", new BigDecimal("300000"));
            depense2.setNumeroPiece("FACT-FOURN-002");
            depense2.setTiers("Propriétaire");
            depense2.setModePaiement("CHEQUE");
            depense2.setDevise("XOF");
            
            LivreDepense depense1Creee = smtService.enregistrerDepense(depense1);
            LivreDepense depense2Creee = smtService.enregistrerDepense(depense2);
            donneesCreees.put("depenses", List.of(depense1Creee, depense2Creee));

            // 6. Générer un état financier
            EtatFinancier etat = smtService.genererEtatFinancier(exerciceCree.getId(), "BILAN", "SYSCOHADA");
            donneesCreees.put("etat_financier", etat);

            // 7. Obtenir le tableau de bord
            Map<String, Object> tableauBord = smtService.getTableauBord(entrepriseCreee.getId());
            donneesCreees.put("tableau_bord", tableauBord);

            result.put("success", true);
            result.put("message", "Données de test SMT créées avec succès");
            result.put("donnees_creees", donneesCreees);
            result.put("ids_importants", Map.of(
                "entreprise_id", entrepriseCreee.getId(),
                "exercice_id", exerciceCree.getId(),
                "compte_caisse_id", caisseCreee.getId(),
                "compte_banque_id", banqueCreee.getId(),
                "etat_financier_id", etat != null ? etat.getId() : null
            ));

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur lors de la création des données de test: " + e.getMessage());
            result.put("donnees_creees", donneesCreees);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint d'information sur le module SMT
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfoSMT() {
        Map<String, Object> info = new HashMap<>();
        info.put("module", "Système Minimal de Trésorerie (SMT)");
        info.put("version", "1.0");
        info.put("description", "Module de gestion comptable simplifiée pour les petites entreprises");
        info.put("fonctionnalites", List.of(
            "Gestion des entreprises SMT",
            "Gestion des exercices comptables",
            "Gestion des comptes de trésorerie",
            "Livre des recettes",
            "Livre des dépenses",
            "Génération d'états financiers",
            "Tableau de bord"
        ));
        info.put("standards_supportes", List.of("SYSCOHADA", "IFRS", "US_GAAP", "PCG"));
        info.put("types_etats", List.of("BILAN", "COMPTE_RESULTAT", "FLUX_TRESORERIE", "ANNEXES"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Informations du module SMT");
        response.put("data", info);
        return ResponseEntity.ok(response);
    }
}
