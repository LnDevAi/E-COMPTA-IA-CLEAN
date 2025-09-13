package com.ecomptaia.sycebnl.controller;

import com.ecomptaia.sycebnl.entity.EtatFinancierSycebnl;
import com.ecomptaia.sycebnl.entity.NoteAnnexeSycebnl;
import com.ecomptaia.sycebnl.service.EtatsFinanciersSycebnlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour les états financiers SYCEBNL
 * Expose les endpoints pour la génération et la gestion des états financiers
 */
@RestController
@RequestMapping("/api/sycebnl/etats-financiers")
@CrossOrigin(origins = "*")
public class EtatsFinanciersSycebnlController {
    
    private final EtatsFinanciersSycebnlService etatsFinanciersService;
    
    @Autowired
    public EtatsFinanciersSycebnlController(EtatsFinanciersSycebnlService etatsFinanciersService) {
        this.etatsFinanciersService = etatsFinanciersService;
    }
    
    /**
     * Génère un état financier complet
     * 
     * @param exerciceId ID de l'exercice comptable
     * @param typeSysteme Type de système (NORMAL ou MINIMAL)
     * @param typeEtat Type d'état financier (BILAN, COMPTE_RESULTAT, etc.)
     * @return État financier généré
     */
    @PostMapping("/generer")
    public ResponseEntity<Map<String, Object>> genererEtatFinancier(
            @RequestParam Long exerciceId,
            @RequestParam EtatFinancierSycebnl.TypeSysteme typeSysteme,
            @RequestParam EtatFinancierSycebnl.TypeEtat typeEtat) {
        
        try {
            EtatFinancierSycebnl etat = etatsFinanciersService.genererEtatFinancier(
                    exerciceId, typeSysteme, typeEtat);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "État financier généré avec succès");
            response.put("etatFinancier", etat);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la génération de l'état financier: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Génère un bilan pour un exercice donné
     * 
     * @param exerciceId ID de l'exercice comptable
     * @param typeSysteme Type de système (NORMAL ou MINIMAL)
     * @return Bilan généré
     */
    @PostMapping("/bilan")
    public ResponseEntity<Map<String, Object>> genererBilan(
            @RequestParam Long exerciceId,
            @RequestParam EtatFinancierSycebnl.TypeSysteme typeSysteme) {
        
        try {
            EtatFinancierSycebnl bilan = etatsFinanciersService.genererEtatFinancier(
                    exerciceId, typeSysteme, EtatFinancierSycebnl.TypeEtat.BILAN);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bilan généré avec succès");
            response.put("bilan", bilan);
            response.put("equilibre", bilan.getEquilibre());
            response.put("totalActif", bilan.getTotalActif());
            response.put("totalPassif", bilan.getTotalPassif());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la génération du bilan: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Génère un compte de résultat pour un exercice donné
     * 
     * @param exerciceId ID de l'exercice comptable
     * @param typeSysteme Type de système (NORMAL ou MINIMAL)
     * @return Compte de résultat généré
     */
    @PostMapping("/compte-resultat")
    public ResponseEntity<Map<String, Object>> genererCompteResultat(
            @RequestParam Long exerciceId,
            @RequestParam EtatFinancierSycebnl.TypeSysteme typeSysteme) {
        
        try {
            EtatFinancierSycebnl.TypeEtat typeEtat = (typeSysteme == EtatFinancierSycebnl.TypeSysteme.MINIMAL) 
                    ? EtatFinancierSycebnl.TypeEtat.RECETTES_DEPENSES 
                    : EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT;
            
            EtatFinancierSycebnl compteResultat = etatsFinanciersService.genererEtatFinancier(
                    exerciceId, typeSysteme, typeEtat);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Compte de résultat généré avec succès");
            response.put("compteResultat", compteResultat);
            response.put("totalProduits", compteResultat.getTotalProduits());
            response.put("totalCharges", compteResultat.getTotalCharges());
            response.put("resultatNet", compteResultat.getResultatNet());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la génération du compte de résultat: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Génère une situation de trésorerie pour le système minimal
     * 
     * @param exerciceId ID de l'exercice comptable
     * @return Situation de trésorerie générée
     */
    @PostMapping("/situation-tresorerie")
    public ResponseEntity<Map<String, Object>> genererSituationTresorerie(
            @RequestParam Long exerciceId) {
        
        try {
            EtatFinancierSycebnl situation = etatsFinanciersService.genererEtatFinancier(
                    exerciceId, EtatFinancierSycebnl.TypeSysteme.MINIMAL, 
                    EtatFinancierSycebnl.TypeEtat.SITUATION_TRESORERIE);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Situation de trésorerie générée avec succès");
            response.put("situationTresorerie", situation);
            response.put("totalDisponibilites", situation.getTotalActif());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la génération de la situation de trésorerie: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Génère un tableau des flux de trésorerie pour le système normal
     * 
     * @param exerciceId ID de l'exercice comptable
     * @return Tableau des flux de trésorerie généré
     */
    @PostMapping("/tableau-flux")
    public ResponseEntity<Map<String, Object>> genererTableauFlux(
            @RequestParam Long exerciceId) {
        
        try {
            EtatFinancierSycebnl tableauFlux = etatsFinanciersService.genererEtatFinancier(
                    exerciceId, EtatFinancierSycebnl.TypeSysteme.NORMAL, 
                    EtatFinancierSycebnl.TypeEtat.TABLEAU_FLUX);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tableau des flux de trésorerie généré avec succès");
            response.put("tableauFlux", tableauFlux);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la génération du tableau des flux: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Génère les notes annexes pour un état financier
     * 
     * @param etatFinancierId ID de l'état financier
     * @return Notes annexes générées
     */
    @PostMapping("/{etatFinancierId}/notes-annexes")
    public ResponseEntity<Map<String, Object>> genererNotesAnnexes(
            @PathVariable Long etatFinancierId) {
        
        try {
            List<NoteAnnexeSycebnl> notes = etatsFinanciersService.genererNotesAnnexes(etatFinancierId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notes annexes générées avec succès");
            response.put("notesAnnexes", notes);
            response.put("nombreNotes", notes.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la génération des notes annexes: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Génère tous les états financiers pour un exercice donné
     * 
     * @param exerciceId ID de l'exercice comptable
     * @param typeSysteme Type de système (NORMAL ou MINIMAL)
     * @return Tous les états financiers générés
     */
    @PostMapping("/generer-tous")
    public ResponseEntity<Map<String, Object>> genererTousEtatsFinanciers(
            @RequestParam Long exerciceId,
            @RequestParam EtatFinancierSycebnl.TypeSysteme typeSysteme) {
        
        try {
            Map<String, Object> resultats = new HashMap<>();
            
            if (typeSysteme == EtatFinancierSycebnl.TypeSysteme.NORMAL) {
                // Génération des états du système normal
                EtatFinancierSycebnl bilan = etatsFinanciersService.genererEtatFinancier(
                        exerciceId, typeSysteme, EtatFinancierSycebnl.TypeEtat.BILAN);
                EtatFinancierSycebnl compteResultat = etatsFinanciersService.genererEtatFinancier(
                        exerciceId, typeSysteme, EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT);
                EtatFinancierSycebnl tableauFlux = etatsFinanciersService.genererEtatFinancier(
                        exerciceId, typeSysteme, EtatFinancierSycebnl.TypeEtat.TABLEAU_FLUX);
                
                resultats.put("bilan", bilan);
                resultats.put("compteResultat", compteResultat);
                resultats.put("tableauFlux", tableauFlux);
                
                // Génération des notes annexes pour le bilan
                List<NoteAnnexeSycebnl> notesBilan = etatsFinanciersService.genererNotesAnnexes(bilan.getId());
                resultats.put("notesAnnexes", notesBilan);
                
            } else {
                // Génération des états du système minimal
                EtatFinancierSycebnl recettesDepenses = etatsFinanciersService.genererEtatFinancier(
                        exerciceId, typeSysteme, EtatFinancierSycebnl.TypeEtat.RECETTES_DEPENSES);
                EtatFinancierSycebnl situationTresorerie = etatsFinanciersService.genererEtatFinancier(
                        exerciceId, typeSysteme, EtatFinancierSycebnl.TypeEtat.SITUATION_TRESORERIE);
                
                resultats.put("recettesDepenses", recettesDepenses);
                resultats.put("situationTresorerie", situationTresorerie);
                
                // Génération des notes annexes pour l'état des recettes et dépenses
                List<NoteAnnexeSycebnl> notesSMT = etatsFinanciersService.genererNotesAnnexes(recettesDepenses.getId());
                resultats.put("notesAnnexes", notesSMT);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tous les états financiers générés avec succès");
            response.put("typeSysteme", typeSysteme.name());
            response.put("resultats", resultats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la génération des états financiers: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Endpoint de test pour vérifier le fonctionnement du module
     * 
     * @return Informations sur le module
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testModule() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module États Financiers SYCEBNL opérationnel");
        response.put("version", "1.0.0");
        response.put("fonctionnalites", List.of(
                "Génération automatique des états financiers",
                "Mapping automatique des comptes vers les postes",
                "Support des systèmes Normal et Minimal de Trésorerie",
                "Génération automatique des notes annexes",
                "Validation de l'équilibre des bilans",
                "Calcul automatique des totaux"
        ));
        response.put("etatsSupportes", Map.of(
                "SYSTEME_NORMAL", List.of("BILAN", "COMPTE_RESULTAT", "TABLEAU_FLUX", "ANNEXES"),
                "SYSTEME_MINIMAL", List.of("RECETTES_DEPENSES", "SITUATION_TRESORERIE")
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint d'information sur le module
     * 
     * @return Informations détaillées sur le module
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getModuleInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("module", "États Financiers SYCEBNL");
        response.put("description", "Module de génération automatique des états financiers avec mapping des comptes");
        response.put("version", "1.0.0");
        response.put("auteur", "E-COMPTA-IA");
        response.put("dateCreation", "2024");
        
        Map<String, Object> specifications = new HashMap<>();
        specifications.put("standardsSupportes", List.of("SYSCOHADA", "OHADA"));
        specifications.put("systemesSupportes", List.of("NORMAL", "MINIMAL"));
        specifications.put("paysSupportes", List.of("BF", "CI", "SN", "ML", "NE", "TG", "BJ"));
        specifications.put("devisesSupportees", List.of("XOF", "EUR", "USD"));
        
        response.put("specifications", specifications);
        
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("genererEtatFinancier", "POST /api/sycebnl/etats-financiers/generer");
        endpoints.put("genererBilan", "POST /api/sycebnl/etats-financiers/bilan");
        endpoints.put("genererCompteResultat", "POST /api/sycebnl/etats-financiers/compte-resultat");
        endpoints.put("genererSituationTresorerie", "POST /api/sycebnl/etats-financiers/situation-tresorerie");
        endpoints.put("genererTableauFlux", "POST /api/sycebnl/etats-financiers/tableau-flux");
        endpoints.put("genererNotesAnnexes", "POST /api/sycebnl/etats-financiers/{id}/notes-annexes");
        endpoints.put("genererTousEtats", "POST /api/sycebnl/etats-financiers/generer-tous");
        
        response.put("endpoints", endpoints);
        
        return ResponseEntity.ok(response);
    }
}
