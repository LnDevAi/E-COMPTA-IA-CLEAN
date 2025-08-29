package com.ecomptaia.controller;

import com.ecomptaia.entity.*;
import com.ecomptaia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.ecomptaia.repository.TemplateEcritureRepository;
import com.ecomptaia.repository.EcritureComptableRepository;
import com.ecomptaia.repository.CompanyRepository;
import com.ecomptaia.repository.FinancialPeriodRepository;
import com.ecomptaia.repository.UserRepository;

@RestController
@RequestMapping("/api/ecritures")
@CrossOrigin(origins = "*")
public class EcritureComptableController {
    
    @Autowired
    private EcritureComptableService ecritureService;
    
    @Autowired
    private EcritureComptableAIService aiService;
    
    @Autowired
    private TemplateEcritureRepository templateRepository;
    
    @Autowired
    private EcritureComptableRepository ecritureRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private FinancialPeriodRepository financialPeriodRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // ==================== CRUD BASIQUE ====================
    
    /**
     * Créer une nouvelle écriture comptable
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEcriture(@RequestBody EcritureComptable ecriture) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecritureCreee = ecritureService.createEcriture(ecriture);
            response.put("success", true);
            response.put("message", "Écriture créée avec succès");
            response.put("data", ecritureCreee);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Initialiser les données de base pour les tests
     */
    @PostMapping("/init-test-data")
    public ResponseEntity<Map<String, Object>> initTestData() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Vérifier si les données existent déjà
            List<Company> companies = companyRepository.findAll();
            List<FinancialPeriod> periods = financialPeriodRepository.findAll();
            List<User> users = userRepository.findAll();
            
            Map<String, Object> dataCreated = new HashMap<>();
            
            // Créer une entreprise de test si elle n'existe pas
            if (companies.isEmpty()) {
                Company company = new Company();
                company.setName("Entreprise Test OHADA");
                company.setSiret("12345678901234");
                company.setCountryCode("BF");
                company.setCountryName("Burkina Faso");
                company.setAccountingStandard("SYSCOHADA");
                company.setOhadaSystemType("SN");
                company.setCurrency("XOF");
                company.setLocale("fr_BF");
                company.setBusinessType("PME");
                company.setIsActive(true);
                company = companyRepository.save(company);
                dataCreated.put("company_created", company);
            } else {
                dataCreated.put("company_exists", companies.get(0));
            }
            
            // Créer un exercice de test si il n'existe pas
            if (periods.isEmpty()) {
                FinancialPeriod period = new FinancialPeriod();
                period.setCompanyId(companies.isEmpty() ? 1L : companies.get(0).getId());
                period.setPeriodName("Exercice 2024");
                period.setStartDate(LocalDate.of(2024, 1, 1));
                period.setEndDate(LocalDate.of(2024, 12, 31));
                period.setStatus("OPEN");
                period.setIsCurrent(true);
                period.setIsLocked(false);
                period.setCountryCode("BF");
                period.setAccountingStandard("SYSCOHADA");
                period = financialPeriodRepository.save(period);
                dataCreated.put("period_created", period);
            } else {
                dataCreated.put("period_exists", periods.get(0));
            }
            
            // Créer un utilisateur de test si il n'existe pas
            if (users.isEmpty()) {
                User user = new User();
                user.setFirstName("Test");
                user.setLastName("User");
                user.setEmail("test@example.com");
                user.setPassword("password123");
                user = userRepository.save(user);
                dataCreated.put("user_created", user);
            } else {
                dataCreated.put("user_exists", users.get(0));
            }
            
            response.put("success", true);
            response.put("message", "Données de test initialisées avec succès");
            response.put("data", dataCreated);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'initialisation : " + e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Créer une écriture de test simple
     */
    @PostMapping("/test-simple")
    public ResponseEntity<Map<String, Object>> createEcritureTest(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Créer une écriture avec les données minimales
            EcritureComptable ecriture = new EcritureComptable();
            
            // Remplir les champs obligatoires
            ecriture.setDateEcriture(LocalDate.now());
            ecriture.setDatePiece(LocalDate.now());
            ecriture.setLibelle((String) request.getOrDefault("libelle", "Écriture de test"));
            ecriture.setTypeEcriture(EcritureComptable.TypeEcriture.NORMALE);
            ecriture.setStatut(EcritureComptable.StatutEcriture.BROUILLON);
            ecriture.setDevise((String) request.getOrDefault("devise", "XOF"));
            ecriture.setSource(EcritureComptable.SourceEcriture.MANUELLE);
            
            // Générer un numéro de pièce automatiquement
            String numeroPiece = "ECR-" + LocalDate.now().getYear() + "-" + 
                               String.format("%02d", LocalDate.now().getMonthValue()) + "-" +
                               String.format("%04d", (int)(Math.random() * 10000));
            ecriture.setNumeroPiece(numeroPiece);
            
            // Récupérer les vraies entités existantes
            List<Company> companies = companyRepository.findAll();
            List<FinancialPeriod> periods = financialPeriodRepository.findAll();
            List<User> users = userRepository.findAll();
            
            if (companies.isEmpty() || periods.isEmpty() || users.isEmpty()) {
                response.put("success", false);
                response.put("message", "Veuillez d'abord initialiser les données de test avec POST /api/ecritures/init-test-data");
                return ResponseEntity.badRequest().body(response);
            }
            
            ecriture.setEntreprise(companies.get(0));
            ecriture.setExercice(periods.get(0));
            ecriture.setUtilisateur(users.get(0));
            
            // Créer des lignes de test si fournies
            if (request.containsKey("montant")) {
                BigDecimal montant = new BigDecimal(request.get("montant").toString());
                ecriture.setTotalDebit(montant);
                ecriture.setTotalCredit(montant);
                
                // Créer une ligne de test
                LigneEcriture ligne = new LigneEcriture();
                ligne.setLibelleLigne("Ligne de test");
                ligne.setDebit(montant);
                ligne.setCredit(BigDecimal.ZERO);
                ligne.setOrdre(1);
                
                List<LigneEcriture> lignes = new ArrayList<>();
                lignes.add(ligne);
                ecriture.setLignes(lignes);
            } else {
                ecriture.setTotalDebit(BigDecimal.ZERO);
                ecriture.setTotalCredit(BigDecimal.ZERO);
            }
            
            // Sauvegarder l'écriture
            EcritureComptable ecritureCreee = ecritureRepository.save(ecriture);
            
            response.put("success", true);
            response.put("message", "Écriture de test créée avec succès");
            response.put("data", ecritureCreee);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création : " + e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir une écriture par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEcriture(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecriture = ecritureService.getEcritureById(id);
            response.put("success", true);
            response.put("data", ecriture);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Mettre à jour une écriture
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEcriture(@PathVariable UUID id, @RequestBody EcritureComptable ecriture) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecritureMiseAJour = ecritureService.updateEcriture(id, ecriture);
            response.put("success", true);
            response.put("message", "Écriture mise à jour avec succès");
            response.put("data", ecritureMiseAJour);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Supprimer une écriture
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEcriture(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ecritureService.deleteEcriture(id);
            response.put("success", true);
            response.put("message", "Écriture supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir toutes les écritures
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEcritures() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcritureComptable> ecritures = ecritureRepository.findAll();
            response.put("success", true);
            response.put("data", ecritures);
            response.put("count", ecritures.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ==================== VALIDATION ====================
    
    /**
     * Valider une écriture
     */
    @PostMapping("/{id}/valider")
    public ResponseEntity<Map<String, Object>> validerEcriture(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecriture = ecritureService.validerEcriture(id);
            response.put("success", true);
            response.put("message", "Écriture validée avec succès");
            response.put("data", ecriture);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la validation : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Annuler la validation d'une écriture
     */
    @PostMapping("/{id}/annuler-validation")
    public ResponseEntity<Map<String, Object>> annulerValidation(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecriture = ecritureService.annulerValidation(id);
            response.put("success", true);
            response.put("message", "Validation annulée avec succès");
            response.put("data", ecriture);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Clôturer une écriture
     */
    @PostMapping("/{id}/cloturer")
    public ResponseEntity<Map<String, Object>> cloturerEcriture(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecriture = ecritureService.cloturerEcriture(id);
            response.put("success", true);
            response.put("message", "Écriture clôturée avec succès");
            response.put("data", ecriture);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ==================== TEMPLATES ====================
    
    /**
     * Obtenir tous les templates actifs
     */
    @GetMapping("/templates")
    public ResponseEntity<Map<String, Object>> getAllTemplates() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<TemplateEcriture> templates = ecritureService.getAllTemplatesActifs();
            response.put("success", true);
            response.put("data", templates);
            response.put("count", templates.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir les templates recommandés
     */
    @GetMapping("/templates/recommandes")
    public ResponseEntity<Map<String, Object>> getTemplatesRecommandes(
            @RequestParam String standardComptable,
            @RequestParam String operation) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<TemplateEcriture> templates = ecritureService.getTemplatesRecommandes(standardComptable, operation);
            response.put("success", true);
            response.put("data", templates);
            response.put("count", templates.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Créer une écriture à partir d'un template
     */
    @PostMapping("/templates/{templateCode}")
    public ResponseEntity<Map<String, Object>> createFromTemplate(
            @PathVariable String templateCode,
            @RequestBody Map<String, Object> variables) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecriture = ecritureService.createEcritureFromTemplate(templateCode, variables);
            response.put("success", true);
            response.put("message", "Écriture créée à partir du template avec succès");
            response.put("data", ecriture);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ==================== IA ====================
    
    /**
     * Analyser une opération avec l'IA
     */
    @PostMapping("/ia/analyser")
    public ResponseEntity<Map<String, Object>> analyserOperation(
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String description = request.get("description");
            String standardComptable = request.get("standardComptable");
            String pays = request.get("pays");
            
            Map<String, Object> analyse = aiService.analyserOperation(description, standardComptable, pays);
            response.put("success", true);
            response.put("data", analyse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'analyse : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Générer une écriture automatique
     */
    @PostMapping("/ia/generer")
    public ResponseEntity<Map<String, Object>> genererEcritureAutomatique(
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String description = (String) request.get("description");
            Map<String, Object> parametres = (Map<String, Object>) request.get("parametres");
            
            EcritureComptable ecriture = aiService.genererEcritureAutomatique(description, parametres);
            response.put("success", true);
            response.put("message", "Écriture générée automatiquement");
            response.put("data", ecriture);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la génération : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Valider une écriture avec l'IA
     */
    @PostMapping("/{id}/ia/valider")
    public ResponseEntity<Map<String, Object>> validerEcritureIA(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecriture = ecritureService.getEcritureById(id);
            Map<String, Object> validation = aiService.validerEcritureIA(ecriture);
            response.put("success", true);
            response.put("data", validation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la validation IA : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Suggérer des améliorations
     */
    @GetMapping("/{id}/ia/suggestions")
    public ResponseEntity<Map<String, Object>> getSuggestionsIA(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecriture = ecritureService.getEcritureById(id);
            List<String> suggestions = aiService.suggererAmeliorations(ecriture);
            response.put("success", true);
            response.put("data", suggestions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ==================== FONCTIONNALITÉS IA ====================
    
    /**
     * Analyser un document avec l'IA
     */
    @PostMapping("/ai/analyze-document")
    public ResponseEntity<Map<String, Object>> analyzeDocument(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String documentContent = (String) request.get("documentContent");
            String documentType = (String) request.get("documentType");
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());
            
            Map<String, Object> result = aiService.analyserDocument(documentContent, documentType, entrepriseId);
            response.put("success", true);
            response.put("message", "Document analysé avec succès");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'analyse : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir des recommandations IA
     */
    @GetMapping("/ai/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations(
            @RequestParam Long entrepriseId,
            @RequestParam(required = false) String type) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> recommendations = aiService.getRecommendations(entrepriseId, type);
            response.put("success", true);
            response.put("message", "Recommandations récupérées avec succès");
            response.put("data", recommendations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Optimiser une écriture avec l'IA
     */
    @PostMapping("/ai/optimize")
    public ResponseEntity<Map<String, Object>> optimizeEcriture(@RequestBody EcritureComptable ecriture) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcritureComptable ecritureOptimisee = aiService.optimiserEcriture(ecriture);
            response.put("success", true);
            response.put("message", "Écriture optimisée avec succès");
            response.put("data", ecritureOptimisee);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'optimisation : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ==================== RECHERCHE ====================
    
    /**
     * Recherche avancée d'écritures
     */
    @PostMapping("/recherche")
    public ResponseEntity<Map<String, Object>> searchEcritures(@RequestBody Map<String, Object> criteres) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcritureComptable> ecritures = ecritureService.searchEcritures(criteres);
            response.put("success", true);
            response.put("data", ecritures);
            response.put("count", ecritures.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la recherche : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Recherche par texte
     */
    @GetMapping("/recherche/texte")
    public ResponseEntity<Map<String, Object>> searchByTexte(
            @RequestParam Long entrepriseId,
            @RequestParam String texte) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Créer un objet Company temporaire pour la recherche
            Company entreprise = new Company();
            entreprise.setId(entrepriseId);
            
            List<EcritureComptable> ecritures = ecritureService.searchByTexte(entreprise, texte);
            response.put("success", true);
            response.put("data", ecritures);
            response.put("count", ecritures.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ==================== STATISTIQUES ====================
    
    /**
     * Obtenir les statistiques des écritures
     */
    @GetMapping("/statistiques/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getStatistiques(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Créer un objet Company temporaire
            Company entreprise = new Company();
            entreprise.setId(entrepriseId);
            
            Map<String, Object> statistiques = ecritureService.getStatistiques(entreprise);
            response.put("success", true);
            response.put("data", statistiques);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ==================== ENDPOINTS DE TEST ====================
    
    /**
     * Test complet du module écritures
     */
    @GetMapping("/test-complet")
    public ResponseEntity<Map<String, Object>> testComplet() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> tests = new HashMap<>();
        
        try {
            // Test 1: Compter les écritures
            long countEcritures = ecritureRepository.count();
            tests.put("nombre_ecritures", countEcritures);
            
            // Test 2: Compter les templates
            long countTemplates = templateRepository.count();
            tests.put("nombre_templates", countTemplates);
            
            // Test 3: Vérifier les templates actifs
            List<TemplateEcriture> templatesActifs = templateRepository.findByIsActifTrueOrderByOrdreAffichageAsc();
            tests.put("templates_actifs", templatesActifs.size());
            
            // Test 4: Vérifier les écritures par statut
            List<EcritureComptable> ecrituresBrouillon = ecritureRepository.findByStatutOrderByDateEcritureDesc(EcritureComptable.StatutEcriture.BROUILLON);
            List<EcritureComptable> ecrituresValidees = ecritureRepository.findByStatutOrderByDateEcritureDesc(EcritureComptable.StatutEcriture.VALIDEE);
            tests.put("ecritures_brouillon", ecrituresBrouillon.size());
            tests.put("ecritures_validees", ecrituresValidees.size());
            
            // Test 5: Vérifier les écritures par source
            List<EcritureComptable> ecrituresManuelles = ecritureRepository.findBySourceOrderByDateEcritureDesc(EcritureComptable.SourceEcriture.MANUELLE);
            List<EcritureComptable> ecrituresIA = ecritureRepository.findBySourceOrderByDateEcritureDesc(EcritureComptable.SourceEcriture.IA);
            tests.put("ecritures_manuelles", ecrituresManuelles.size());
            tests.put("ecritures_ia", ecrituresIA.size());
            
            // Test 6: Vérifier les templates par catégorie
            Map<String, Long> templatesParCategorie = new HashMap<>();
            for (TemplateEcriture.CategorieTemplate categorie : TemplateEcriture.CategorieTemplate.values()) {
                List<TemplateEcriture> templates = templateRepository.findByCategorieOrderByOrdreAffichageAsc(categorie);
                templatesParCategorie.put(categorie.name(), (long) templates.size());
            }
            tests.put("templates_par_categorie", templatesParCategorie);
            
            response.put("success", true);
            response.put("message", "Test complet réussi");
            response.put("tests", tests);
            response.put("timestamp", new Date());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du test : " + e.getMessage());
            response.put("tests", tests);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Créer des données de test
     */
    @PostMapping("/test-creer-donnees")
    public ResponseEntity<Map<String, Object>> creerDonneesTest() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> donneesCreees = new HashMap<>();
        
        try {
            // Créer des templates de test
            List<TemplateEcriture> templatesCrees = creerTemplatesTest();
            donneesCreees.put("templates_crees", templatesCrees.size());
            
            // Créer des écritures de test
            List<EcritureComptable> ecrituresCrees = creerEcrituresTest();
            donneesCreees.put("ecritures_crees", ecrituresCrees.size());
            
            response.put("success", true);
            response.put("message", "Données de test créées avec succès");
            response.put("donnees_creees", donneesCreees);
            response.put("timestamp", new Date());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la création des données : " + e.getMessage());
            response.put("donnees_creees", donneesCreees);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    private List<TemplateEcriture> creerTemplatesTest() {
        List<TemplateEcriture> templates = new ArrayList<>();
        
        // Template Vente avec TVA
        TemplateEcriture templateVente = new TemplateEcriture();
        templateVente.setNom("Vente de marchandises avec TVA");
        templateVente.setCode("VENTE_TVA_OHADA");
        templateVente.setStandardComptable("SYSCOHADA");
        templateVente.setDescription("Écriture de vente avec TVA collectée");
        templateVente.setCategorie(TemplateEcriture.CategorieTemplate.VENTE);
        templateVente.setPaysApplicable("BF");
        templateVente.setTauxTvaDefaut(new java.math.BigDecimal("0.18"));
        templateVente.setDeviseDefaut("XOF");
        templateVente.setComptesPattern("[{\"position\":\"debit\",\"compte_pattern\":\"411%\",\"libelle\":\"Clients\",\"formule\":\"montant_ht * (1 + taux_tva)\"},{\"position\":\"credit\",\"compte_pattern\":\"701%\",\"libelle\":\"Ventes de marchandises\",\"formule\":\"montant_ht\"},{\"position\":\"credit\",\"compte_pattern\":\"4434%\",\"libelle\":\"TVA collectée\",\"formule\":\"montant_ht * taux_tva\"}]");
        templateVente.setVariables("{\"montant_ht\":\"decimal\",\"taux_tva\":\"decimal\",\"client_id\":\"UUID\",\"facture_numero\":\"string\"}");
        templateVente.setMotsCles("vente,facturation,client,ca,chiffre d'affaires");
        templateVente.setOrdreAffichage(1);
        templates.add(templateRepository.save(templateVente));
        
        // Template Achat avec TVA
        TemplateEcriture templateAchat = new TemplateEcriture();
        templateAchat.setNom("Achat de marchandises avec TVA");
        templateAchat.setCode("ACHAT_TVA_OHADA");
        templateAchat.setStandardComptable("SYSCOHADA");
        templateAchat.setDescription("Achat avec TVA déductible");
        templateAchat.setCategorie(TemplateEcriture.CategorieTemplate.ACHAT);
        templateAchat.setPaysApplicable("BF");
        templateAchat.setTauxTvaDefaut(new java.math.BigDecimal("0.18"));
        templateAchat.setDeviseDefaut("XOF");
        templateAchat.setComptesPattern("[{\"position\":\"debit\",\"compte_pattern\":\"601%\",\"libelle\":\"Achats de marchandises\",\"formule\":\"montant_ht\"},{\"position\":\"debit\",\"compte_pattern\":\"4431%\",\"libelle\":\"TVA déductible\",\"formule\":\"montant_ht * taux_tva\"},{\"position\":\"credit\",\"compte_pattern\":\"401%\",\"libelle\":\"Fournisseurs\",\"formule\":\"montant_ht * (1 + taux_tva)\"}]");
        templateAchat.setVariables("{\"montant_ht\":\"decimal\",\"taux_tva\":\"decimal\",\"fournisseur_id\":\"UUID\",\"facture_numero\":\"string\"}");
        templateAchat.setMotsCles("achat,fournisseur,approvisionnement,commande");
        templateAchat.setOrdreAffichage(2);
        templates.add(templateRepository.save(templateAchat));
        
        return templates;
    }
    
    private List<EcritureComptable> creerEcrituresTest() {
        List<EcritureComptable> ecritures = new ArrayList<>();
        
        // Créer une écriture de test simple
        EcritureComptable ecriture = new EcritureComptable();
        ecriture.setNumeroPiece("ECR-2024-01-0001");
        ecriture.setDateEcriture(java.time.LocalDate.now());
        ecriture.setDatePiece(java.time.LocalDate.now());
        ecriture.setLibelle("Écriture de test");
        ecriture.setTypeEcriture(EcritureComptable.TypeEcriture.NORMALE);
        ecriture.setStatut(EcritureComptable.StatutEcriture.BROUILLON);
        ecriture.setSource(EcritureComptable.SourceEcriture.MANUELLE);
        ecriture.setDevise("XOF");
        ecriture.setTotalDebit(new java.math.BigDecimal("100000"));
        ecriture.setTotalCredit(new java.math.BigDecimal("100000"));
        
        // Créer des objets temporaires pour les relations
        Company entreprise = new Company();
        entreprise.setId(1L);
        ecriture.setEntreprise(entreprise);
        
        FinancialPeriod exercice = new FinancialPeriod();
        exercice.setId(1L);
        ecriture.setExercice(exercice);
        
        User utilisateur = new User();
        utilisateur.setId(1L);
        ecriture.setUtilisateur(utilisateur);
        
        ecritures.add(ecritureRepository.save(ecriture));
        
        return ecritures;
    }
}
