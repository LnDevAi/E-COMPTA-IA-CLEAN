ackage com.ecomptaia.controller;

import com.ecomptaia.entity.PieceJustificativeComptable;
import com.ecomptaia.entity.EcritureComptable;
import com.ecomptaia.service.PieceJustificativeComptableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des Pièces Justificatives Comptables
 * Gère l'upload, l'analyse OCR/IA et la génération d'écritures
 */
@RestController
@RequestMapping("/api/pieces-justificatives-comptables")
@CrossOrigin(origins = "*")
public class PieceJustificativeComptableController {
    
    @Autowired
    private PieceJustificativeComptableService pjService;
    
    // ==================== UPLOAD ET TRAITEMENT ====================
    
    /**
     * Télécharger une PJ comptable
     */
    @PostMapping("/upload")
    public ResponseEntity<PieceJustificativeComptable> uploadPieceJustificative(
            @RequestParam("file") MultipartFile file,
            @RequestParam("companyId") Long companyId,
            @RequestParam("exerciceId") Long exerciceId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "libelle", required = false) String libelle,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("typeDocument") PieceJustificativeComptable.TypeDocument typeDocument) {
        
        try {
            PieceJustificativeComptable pj = pjService.uploadPieceJustificative(
                file, companyId, exerciceId, userId, libelle, description, typeDocument);
            return ResponseEntity.ok(pj);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Traiter automatiquement une PJ (OCR + IA)
     */
    @PostMapping("/{id}/traiter")
    public ResponseEntity<String> traiterPieceJustificative(@PathVariable Long id) {
        try {
            pjService.traiterPieceJustificativeAutomatiquement(id);
            return ResponseEntity.ok("Traitement démarré avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors du traitement: " + e.getMessage());
        }
    }
    
    /**
     * Traiter OCR d'une PJ
     */
    @PostMapping("/{id}/ocr")
    public ResponseEntity<String> traiterOCR(@PathVariable Long id) {
        try {
            Optional<PieceJustificativeComptable> pjOpt = pjService.getPJById(id);
            if (pjOpt.isPresent()) {
                pjService.traiterOCR(pjOpt.get());
                return ResponseEntity.ok("Traitement OCR démarré avec succès");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors du traitement OCR: " + e.getMessage());
        }
    }
    
    /**
     * Traiter IA d'une PJ
     */
    @PostMapping("/{id}/ia")
    public ResponseEntity<String> traiterIA(@PathVariable Long id) {
        try {
            Optional<PieceJustificativeComptable> pjOpt = pjService.getPJById(id);
            if (pjOpt.isPresent()) {
                pjService.traiterIA(pjOpt.get());
                return ResponseEntity.ok("Traitement IA démarré avec succès");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors du traitement IA: " + e.getMessage());
        }
    }
    
    // ==================== VALIDATION ET GÉNÉRATION D'ÉCRITURES ====================
    
    /**
     * Valider les propositions et générer l'écriture
     */
    @PostMapping("/{id}/valider-et-generer")
    public ResponseEntity<EcritureComptable> validerEtGenererEcriture(
            @PathVariable Long id,
            @RequestBody String propositionsValidees,
            @RequestParam("userId") Long userId) {
        
        try {
            EcritureComptable ecriture = pjService.validerEtGenererEcriture(id, propositionsValidees, userId);
            return ResponseEntity.ok(ecriture);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ==================== CONSULTATION ====================
    
    /**
     * Obtenir une PJ par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PieceJustificativeComptable> getPJById(@PathVariable Long id) {
        Optional<PieceJustificativeComptable> pj = pjService.getPJById(id);
        return pj.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtenir toutes les PJ d'une entreprise
     */
    @GetMapping("/entreprise/{companyId}")
    public ResponseEntity<List<PieceJustificativeComptable>> getPJByCompany(@PathVariable Long companyId) {
        try {
            List<PieceJustificativeComptable> pjs = pjService.getPJByCompany(companyId);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir toutes les PJ d'un exercice
     */
    @GetMapping("/exercice/{exerciceId}")
    public ResponseEntity<List<PieceJustificativeComptable>> getPJByExercice(@PathVariable Long exerciceId) {
        try {
            List<PieceJustificativeComptable> pjs = pjService.getPJByExercice(exerciceId);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir les PJ par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<PieceJustificativeComptable>> getPJByStatut(
            @PathVariable PieceJustificativeComptable.StatutTraitement statut) {
        try {
            List<PieceJustificativeComptable> pjs = pjService.getPJByStatut(statut);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Recherche avancée
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<PieceJustificativeComptable>> rechercheAvancee(
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "exerciceId", required = false) Long exerciceId,
            @RequestParam(value = "statut", required = false) PieceJustificativeComptable.StatutTraitement statut,
            @RequestParam(value = "typeDocument", required = false) PieceJustificativeComptable.TypeDocument typeDocument,
            @RequestParam(value = "dateDebut", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(value = "dateFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(value = "montantMin", required = false) BigDecimal montantMin,
            @RequestParam(value = "montantMax", required = false) BigDecimal montantMax) {
        
        try {
            List<PieceJustificativeComptable> pjs = pjService.rechercheAvancee(
                companyId, exerciceId, statut, typeDocument, dateDebut, dateFin, montantMin, montantMax);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir les statistiques
     */
    @GetMapping("/statistiques/{companyId}")
    public ResponseEntity<Map<String, Object>> getStatistiques(@PathVariable Long companyId) {
        try {
            Map<String, Object> stats = pjService.getStatistiques(companyId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ==================== GESTION DES ÉTATS ====================
    
    /**
     * Obtenir les PJ en attente de traitement OCR
     */
    @GetMapping("/en-attente-ocr")
    public ResponseEntity<List<PieceJustificativeComptable>> getPJEnAttenteOCR() {
        try {
            // Cette méthode devrait être implémentée dans le service
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir les PJ en attente de traitement IA
     */
    @GetMapping("/en-attente-ia")
    public ResponseEntity<List<PieceJustificativeComptable>> getPJEnAttenteIA() {
        try {
            // Cette méthode devrait être implémentée dans le service
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir les PJ prêtes pour validation
     */
    @GetMapping("/pretes-validation")
    public ResponseEntity<List<PieceJustificativeComptable>> getPJPretesPourValidation() {
        try {
            List<PieceJustificativeComptable> pjs = pjService.getPJByStatut(
                PieceJustificativeComptable.StatutTraitement.PROPOSITIONS_READY);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir les PJ avec écritures générées
     */
    @GetMapping("/avec-ecritures")
    public ResponseEntity<List<PieceJustificativeComptable>> getPJAvecEcritures() {
        try {
            List<PieceJustificativeComptable> pjs = pjService.getPJByStatut(
                PieceJustificativeComptable.StatutTraitement.ECRITURE_GENERE);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ==================== GESTION DES TYPES DE DOCUMENTS ====================
    
    /**
     * Obtenir les types de documents disponibles
     */
    @GetMapping("/types-documents")
    public ResponseEntity<PieceJustificativeComptable.TypeDocument[]> getTypesDocuments() {
        return ResponseEntity.ok(PieceJustificativeComptable.TypeDocument.values());
    }
    
    /**
     * Obtenir les statuts de traitement disponibles
     */
    @GetMapping("/statuts-traitement")
    public ResponseEntity<PieceJustificativeComptable.StatutTraitement[]> getStatutsTraitement() {
        return ResponseEntity.ok(PieceJustificativeComptable.StatutTraitement.values());
    }
    
    // ==================== GESTION DES FICHIERS ====================
    
    /**
     * Télécharger le fichier d'une PJ
     */
    @GetMapping("/{id}/fichier")
    public ResponseEntity<byte[]> downloadFichier(@PathVariable Long id) {
        try {
            Optional<PieceJustificativeComptable> pjOpt = pjService.getPJById(id);
            if (pjOpt.isPresent()) {
                // Ici, vous devriez implémenter la logique de téléchargement du fichier
                // Pour l'instant, on retourne une réponse vide
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir les métadonnées d'une PJ
     */
    @GetMapping("/{id}/metadonnees")
    public ResponseEntity<Map<String, Object>> getMetadonnees(@PathVariable Long id) {
        try {
            Optional<PieceJustificativeComptable> pjOpt = pjService.getPJById(id);
            if (pjOpt.isPresent()) {
                PieceJustificativeComptable pj = pjOpt.get();
                Map<String, Object> metadonnees = new HashMap<>();
                metadonnees.put("id", pj.getId());
                metadonnees.put("numeroPJ", pj.getNumeroPJ());
                metadonnees.put("nomFichier", pj.getNomFichier());
                metadonnees.put("typeFichier", pj.getTypeFichier());
                metadonnees.put("tailleFichier", pj.getTailleFichier());
                metadonnees.put("dateUpload", pj.getDateUpload());
                metadonnees.put("statutTraitement", pj.getStatutTraitement());
                metadonnees.put("ocrConfidence", pj.getOcrConfidence());
                metadonnees.put("iaConfidence", pj.getIaConfidence());
                metadonnees.put("montantDetecte", pj.getMontantDetecte());
                metadonnees.put("deviseDetectee", pj.getDeviseDetectee());
                return ResponseEntity.ok(metadonnees);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ==================== GESTION DES PROPOSITIONS ====================
    
    /**
     * Obtenir les propositions d'écritures d'une PJ
     */
    @GetMapping("/{id}/propositions")
    public ResponseEntity<String> getPropositions(@PathVariable Long id) {
        try {
            Optional<PieceJustificativeComptable> pjOpt = pjService.getPJById(id);
            if (pjOpt.isPresent()) {
                PieceJustificativeComptable pj = pjOpt.get();
                return ResponseEntity.ok(pj.getPropositionsEcritures());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir les comptes suggérés d'une PJ
     */
    @GetMapping("/{id}/comptes-suggeres")
    public ResponseEntity<String> getComptesSuggeres(@PathVariable Long id) {
        try {
            Optional<PieceJustificativeComptable> pjOpt = pjService.getPJById(id);
            if (pjOpt.isPresent()) {
                PieceJustificativeComptable pj = pjOpt.get();
                return ResponseEntity.ok(pj.getComptesSuggerees());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Obtenir la fiche d'imputation d'une PJ
     */
    @GetMapping("/{id}/fiche-imputation")
    public ResponseEntity<String> getFicheImputation(@PathVariable Long id) {
        try {
            Optional<PieceJustificativeComptable> pjOpt = pjService.getPJById(id);
            if (pjOpt.isPresent()) {
                PieceJustificativeComptable pj = pjOpt.get();
                return ResponseEntity.ok(pj.getFicheImputation());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ==================== GESTION DES ERREURS ====================
    
    /**
     * Obtenir les PJ en erreur
     */
    @GetMapping("/erreurs")
    public ResponseEntity<List<PieceJustificativeComptable>> getPJEnErreur() {
        try {
            List<PieceJustificativeComptable> pjs = pjService.getPJByStatut(
                PieceJustificativeComptable.StatutTraitement.ERROR);
            return ResponseEntity.ok(pjs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Relancer le traitement d'une PJ en erreur
     */
    @PostMapping("/{id}/relancer-traitement")
    public ResponseEntity<String> relancerTraitement(@PathVariable Long id) {
        try {
            pjService.traiterPieceJustificativeAutomatiquement(id);
            return ResponseEntity.ok("Traitement relancé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors du relancement: " + e.getMessage());
        }
    }
}
