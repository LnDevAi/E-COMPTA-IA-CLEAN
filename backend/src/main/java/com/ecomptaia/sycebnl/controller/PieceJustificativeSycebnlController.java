package com.ecomptaia.sycebnl.controller;

import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.User;
import com.ecomptaia.sycebnl.entity.PieceJustificativeSycebnl;
import com.ecomptaia.sycebnl.entity.PropositionEcritureSycebnl;
import com.ecomptaia.sycebnl.service.PieceJustificativeSycebnlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ContrÃ´leur REST pour la gestion des piÃ¨ces justificatives comptables SYCEBNL
 */
@RestController
@RequestMapping("/api/sycebnl/pieces-justificatives")
@CrossOrigin(origins = "*")
public class PieceJustificativeSycebnlController {
    
    private final PieceJustificativeSycebnlService pjService;
    
    @Autowired
    public PieceJustificativeSycebnlController(PieceJustificativeSycebnlService pjService) {
        this.pjService = pjService;
    }
    
    /**
     * TÃ©lÃ©charge une nouvelle piÃ¨ce justificative
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPieceJustificative(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("libellePJ") String libellePJ,
            @RequestParam("datePiece") String datePiece,
            @RequestParam("typePJ") String typePJ,
            @RequestParam("entrepriseId") Long entrepriseId,
            @RequestParam("utilisateurId") Long utilisateurId) {
        
        try {
            // Validation des paramÃ¨tres
            if (fichier.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est requis");
            }
            
            if (libellePJ == null || libellePJ.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Le libellÃ© est requis");
            }
            
            // CrÃ©er les objets nÃ©cessaires (dans une vraie implÃ©mentation, on les rÃ©cupÃ©rerait depuis la base)
            Company entreprise = new Company();
            entreprise.setId(entrepriseId);
            
            User utilisateur = new User();
            utilisateur.setId(utilisateurId);
            
            // Convertir la date
            LocalDate date = LocalDate.parse(datePiece);
            
            // Convertir le type
            PieceJustificativeSycebnl.TypePieceJustificative type = 
                PieceJustificativeSycebnl.TypePieceJustificative.valueOf(typePJ);
            
            // TÃ©lÃ©charger la piÃ¨ce justificative
            PieceJustificativeSycebnl pj = pjService.telechargerPieceJustificative(
                fichier, libellePJ, date, type, entreprise, utilisateur);
            
            return ResponseEntity.ok(pj);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors du tÃ©lÃ©chargement du fichier : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la crÃ©ation de la piÃ¨ce justificative : " + e.getMessage());
        }
    }
    
    /**
     * RÃ©cupÃ¨re une piÃ¨ce justificative par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPieceJustificative(@PathVariable Long id) {
        try {
            Optional<PieceJustificativeSycebnl> pj = pjService.getPieceJustificativeById(id);
            if (pj.isPresent()) {
                return ResponseEntity.ok(pj.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la rÃ©cupÃ©ration de la piÃ¨ce justificative : " + e.getMessage());
        }
    }
    
    /**
     * RÃ©cupÃ¨re toutes les piÃ¨ces justificatives d'une entreprise
     */
    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<?> getPiecesJustificativesByEntreprise(@PathVariable Long entrepriseId) {
        try {
            List<PieceJustificativeSycebnl> pjs = pjService.getPiecesJustificativesByEntreprise(entrepriseId);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la rÃ©cupÃ©ration des piÃ¨ces justificatives : " + e.getMessage());
        }
    }
    
    /**
     * RÃ©cupÃ¨re les piÃ¨ces justificatives par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<?> getPiecesJustificativesByStatut(@PathVariable String statut) {
        try {
            PieceJustificativeSycebnl.StatutTraitement statutEnum = 
                PieceJustificativeSycebnl.StatutTraitement.valueOf(statut);
            List<PieceJustificativeSycebnl> pjs = pjService.getPiecesJustificativesByStatut(statutEnum);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la rÃ©cupÃ©ration des piÃ¨ces justificatives : " + e.getMessage());
        }
    }
    
    /**
     * DÃ©marre l'analyse OCR d'une piÃ¨ce justificative
     */
    @PostMapping("/{id}/analyse-ocr")
    public ResponseEntity<?> demarrerAnalyseOCR(@PathVariable Long id) {
        try {
            pjService.demarrerAnalyseOCR(id);
            return ResponseEntity.ok("Analyse OCR dÃ©marrÃ©e avec succÃ¨s");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors du dÃ©marrage de l'analyse OCR : " + e.getMessage());
        }
    }
    
    /**
     * DÃ©marre l'analyse IA d'une piÃ¨ce justificative
     */
    @PostMapping("/{id}/analyse-ia")
    public ResponseEntity<?> demarrerAnalyseIA(@PathVariable Long id) {
        try {
            pjService.demarrerAnalyseIA(id);
            return ResponseEntity.ok("Analyse IA dÃ©marrÃ©e avec succÃ¨s");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors du dÃ©marrage de l'analyse IA : " + e.getMessage());
        }
    }
    
    /**
     * GÃ©nÃ¨re les propositions d'Ã©critures pour une piÃ¨ce justificative
     */
    @PostMapping("/{id}/generer-propositions")
    public ResponseEntity<?> genererPropositions(@PathVariable Long id) {
        try {
            pjService.genererPropositionsEcritures(id);
            return ResponseEntity.ok("Propositions gÃ©nÃ©rÃ©es avec succÃ¨s");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la gÃ©nÃ©ration des propositions : " + e.getMessage());
        }
    }
    
    /**
     * RÃ©cupÃ¨re les propositions d'une piÃ¨ce justificative
     */
    @GetMapping("/{id}/propositions")
    public ResponseEntity<?> getPropositions(@PathVariable Long id) {
        try {
            List<PropositionEcritureSycebnl> propositions = pjService.getPropositionsByPJ(id);
            return ResponseEntity.ok(propositions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la rÃ©cupÃ©ration des propositions : " + e.getMessage());
        }
    }
    
    /**
     * Valide une proposition d'Ã©criture
     */
    @PostMapping("/propositions/{propositionId}/valider")
    public ResponseEntity<?> validerProposition(
            @PathVariable Long propositionId,
            @RequestParam("validateurId") Long validateurId,
            @RequestParam(value = "commentaires", required = false) String commentaires) {
        
        try {
            pjService.validerProposition(propositionId, validateurId, commentaires);
            return ResponseEntity.ok("Proposition validÃ©e avec succÃ¨s");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la validation de la proposition : " + e.getMessage());
        }
    }
    
    /**
     * GÃ©nÃ¨re l'Ã©criture comptable Ã  partir d'une proposition validÃ©e
     */
    @PostMapping("/propositions/{propositionId}/generer-ecriture")
    public ResponseEntity<?> genererEcritureComptable(@PathVariable Long propositionId) {
        try {
            pjService.genererEcritureComptable(propositionId);
            return ResponseEntity.ok("Ã‰criture comptable gÃ©nÃ©rÃ©e avec succÃ¨s");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la gÃ©nÃ©ration de l'Ã©criture : " + e.getMessage());
        }
    }
    
    /**
     * RÃ©cupÃ¨re les types de piÃ¨ces justificatives disponibles
     */
    @GetMapping("/types")
    public ResponseEntity<?> getTypesPiecesJustificatives() {
        try {
            PieceJustificativeSycebnl.TypePieceJustificative[] types = 
                PieceJustificativeSycebnl.TypePieceJustificative.values();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la rÃ©cupÃ©ration des types : " + e.getMessage());
        }
    }
    
    /**
     * RÃ©cupÃ¨re les statuts de traitement disponibles
     */
    @GetMapping("/statuts")
    public ResponseEntity<?> getStatutsTraitement() {
        try {
            PieceJustificativeSycebnl.StatutTraitement[] statuts = 
                PieceJustificativeSycebnl.StatutTraitement.values();
            return ResponseEntity.ok(statuts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la rÃ©cupÃ©ration des statuts : " + e.getMessage());
        }
    }
}

