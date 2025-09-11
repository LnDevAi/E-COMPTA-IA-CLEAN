ackage com.ecomptaia.sycebnl.service;

import com.ecomptaia.sycebnl.entity.EtatFinancierSycebnl;
import com.ecomptaia.sycebnl.entity.MappingComptesPostes;
import com.ecomptaia.sycebnl.repository.EtatFinancierSycebnlRepository;
import com.ecomptaia.sycebnl.repository.MappingComptesPostesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service de validation des états financiers SYCEBNL
 * Vérifie la cohérence, l'équilibre et la conformité des états financiers
 */
@Service
@Transactional(readOnly = true)
public class ValidationEtatsFinanciersService {
    
    private final EtatFinancierSycebnlRepository etatRepository;
    private final MappingComptesPostesRepository mappingRepository;
    private final ComptabiliteSycebnlService comptabiliteService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public ValidationEtatsFinanciersService(
            EtatFinancierSycebnlRepository etatRepository,
            MappingComptesPostesRepository mappingRepository,
            ComptabiliteSycebnlService comptabiliteService,
            ObjectMapper objectMapper) {
        this.etatRepository = etatRepository;
        this.mappingRepository = mappingRepository;
        this.comptabiliteService = comptabiliteService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Valide un état financier complet
     * 
     * @param etatFinancierId ID de l'état financier à valider
     * @return Résultat de la validation
     */
    @Transactional
    public Map<String, Object> validerEtatFinancier(Long etatFinancierId) {
        EtatFinancierSycebnl etat = etatRepository.findById(etatFinancierId)
                .orElseThrow(() -> new RuntimeException("État financier non trouvé"));
        
        Map<String, Object> resultatValidation = new HashMap<>();
        List<String> erreurs = new ArrayList<>();
        List<String> avertissements = new ArrayList<>();
        boolean valide = true;
        
        // 1. Validation de l'équilibre
        Map<String, Object> validationEquilibre = validerEquilibre(etat);
        if (!(Boolean) validationEquilibre.get("valide")) {
            @SuppressWarnings("unchecked")
            List<String> erreursEquilibre = (List<String>) validationEquilibre.get("erreurs");
            erreurs.addAll(erreursEquilibre);
            valide = false;
        }
        
        // 2. Validation des totaux
        Map<String, Object> validationTotaux = validerTotaux(etat);
        if (!(Boolean) validationTotaux.get("valide")) {
            @SuppressWarnings("unchecked")
            List<String> erreursTotaux = (List<String>) validationTotaux.get("erreurs");
            erreurs.addAll(erreursTotaux);
            valide = false;
        }
        
        // 3. Validation de la cohérence des données
        Map<String, Object> validationCohérence = validerCohérence(etat);
        if (!(Boolean) validationCohérence.get("valide")) {
            @SuppressWarnings("unchecked")
            List<String> erreursCohérence = (List<String>) validationCohérence.get("erreurs");
            erreurs.addAll(erreursCohérence);
            valide = false;
        }
        
        // 4. Validation de la conformité OHADA
        Map<String, Object> validationConformite = validerConformiteOHADA(etat);
        if (!(Boolean) validationConformite.get("valide")) {
            @SuppressWarnings("unchecked")
            List<String> avertissementsConformite = (List<String>) validationConformite.get("avertissements");
            avertissements.addAll(avertissementsConformite);
        }
        
        // 5. Validation des notes annexes
        Map<String, Object> validationNotes = validerNotesAnnexes(etat);
        if (!(Boolean) validationNotes.get("valide")) {
            @SuppressWarnings("unchecked")
            List<String> avertissementsNotes = (List<String>) validationNotes.get("avertissements");
            avertissements.addAll(avertissementsNotes);
        }
        
        // Mise à jour du statut si valide
        if (valide && erreurs.isEmpty()) {
            etat.setStatut(EtatFinancierSycebnl.StatutEtat.VALIDE);
            etat.setValidePar("SYCEBNL - Validation automatique");
            etat.setDateValidation(LocalDateTime.now());
            etatRepository.save(etat);
        }
        
        resultatValidation.put("valide", valide);
        resultatValidation.put("erreurs", erreurs);
        resultatValidation.put("avertissements", avertissements);
        resultatValidation.put("nombreErreurs", erreurs.size());
        resultatValidation.put("nombreAvertissements", avertissements.size());
        resultatValidation.put("dateValidation", LocalDateTime.now());
        
        return resultatValidation;
    }
    
    /**
     * Valide l'équilibre d'un état financier
     */
    private Map<String, Object> validerEquilibre(EtatFinancierSycebnl etat) {
        Map<String, Object> resultat = new HashMap<>();
        List<String> erreurs = new ArrayList<>();
        boolean valide = true;
        
        if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.BILAN) {
            // Pour un bilan, l'actif doit égaler le passif
            if (!comptabiliteService.verifierEquilibreBilan(etat.getTotalActif(), etat.getTotalPassif())) {
                erreurs.add(String.format("Bilan déséquilibré : Actif (%s) ≠ Passif (%s)", 
                        comptabiliteService.formaterMontant(etat.getTotalActif()),
                        comptabiliteService.formaterMontant(etat.getTotalPassif())));
                valide = false;
            }
        } else if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT ||
                   etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.RECETTES_DEPENSES) {
            // Pour un compte de résultat, vérifier que le résultat net = Produits - Charges
            BigDecimal resultatCalcule = comptabiliteService.calculerResultatNet(
                    etat.getTotalProduits(), etat.getTotalCharges());
            
            if (etat.getResultatNet() == null || 
                !resultatCalcule.equals(etat.getResultatNet())) {
                erreurs.add(String.format("Résultat net incohérent : Calculé (%s) ≠ Enregistré (%s)", 
                        comptabiliteService.formaterMontant(resultatCalcule),
                        comptabiliteService.formaterMontant(etat.getResultatNet())));
                valide = false;
            }
        }
        
        resultat.put("valide", valide);
        resultat.put("erreurs", erreurs);
        return resultat;
    }
    
    /**
     * Valide les totaux d'un état financier
     */
    private Map<String, Object> validerTotaux(EtatFinancierSycebnl etat) {
        Map<String, Object> resultat = new HashMap<>();
        List<String> erreurs = new ArrayList<>();
        boolean valide = true;
        
        try {
            // Récupération des données JSON
            @SuppressWarnings("unchecked")
            Map<String, Object> donnees = objectMapper.readValue(etat.getDonneesJson(), Map.class);
            
            // Calcul des totaux à partir des données
            BigDecimal totalActifCalcule = BigDecimal.ZERO;
            BigDecimal totalPassifCalcule = BigDecimal.ZERO;
            BigDecimal totalProduitsCalcule = BigDecimal.ZERO;
            BigDecimal totalChargesCalcule = BigDecimal.ZERO;
            
            for (Map.Entry<String, Object> entry : donnees.entrySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> poste = (Map<String, Object>) entry.getValue();
                BigDecimal solde = new BigDecimal(poste.get("solde").toString());
                String signeNormal = (String) poste.get("signeNormal");
                
                if ("DEBIT".equals(signeNormal)) {
                    totalActifCalcule = totalActifCalcule.add(solde);
                } else {
                    totalPassifCalcule = totalPassifCalcule.add(solde);
                }
                
                // Pour les comptes de résultat
                if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT ||
                    etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.RECETTES_DEPENSES) {
                    if ("CREDIT".equals(signeNormal)) {
                        totalProduitsCalcule = totalProduitsCalcule.add(solde);
                    } else {
                        totalChargesCalcule = totalChargesCalcule.add(solde);
                    }
                }
            }
            
            // Vérification des totaux
            if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.BILAN) {
                if (!totalActifCalcule.equals(etat.getTotalActif())) {
                    erreurs.add(String.format("Total actif incohérent : Calculé (%s) ≠ Enregistré (%s)", 
                            comptabiliteService.formaterMontant(totalActifCalcule),
                            comptabiliteService.formaterMontant(etat.getTotalActif())));
                    valide = false;
                }
                
                if (!totalPassifCalcule.equals(etat.getTotalPassif())) {
                    erreurs.add(String.format("Total passif incohérent : Calculé (%s) ≠ Enregistré (%s)", 
                            comptabiliteService.formaterMontant(totalPassifCalcule),
                            comptabiliteService.formaterMontant(etat.getTotalPassif())));
                    valide = false;
                }
            } else if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT ||
                       etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.RECETTES_DEPENSES) {
                if (!totalProduitsCalcule.equals(etat.getTotalProduits())) {
                    erreurs.add(String.format("Total produits incohérent : Calculé (%s) ≠ Enregistré (%s)", 
                            comptabiliteService.formaterMontant(totalProduitsCalcule),
                            comptabiliteService.formaterMontant(etat.getTotalProduits())));
                    valide = false;
                }
                
                if (!totalChargesCalcule.equals(etat.getTotalCharges())) {
                    erreurs.add(String.format("Total charges incohérent : Calculé (%s) ≠ Enregistré (%s)", 
                            comptabiliteService.formaterMontant(totalChargesCalcule),
                            comptabiliteService.formaterMontant(etat.getTotalCharges())));
                    valide = false;
                }
            }
            
        } catch (Exception e) {
            erreurs.add("Erreur lors de la validation des totaux : " + e.getMessage());
            valide = false;
        }
        
        resultat.put("valide", valide);
        resultat.put("erreurs", erreurs);
        return resultat;
    }
    
    /**
     * Valide la cohérence des données d'un état financier
     */
    private Map<String, Object> validerCohérence(EtatFinancierSycebnl etat) {
        Map<String, Object> resultat = new HashMap<>();
        List<String> erreurs = new ArrayList<>();
        boolean valide = true;
        
        // Vérification des champs obligatoires
        if (etat.getDateArrete() == null) {
            erreurs.add("Date d'arrêté manquante");
            valide = false;
        }
        
        if (etat.getTypeSysteme() == null) {
            erreurs.add("Type de système manquant");
            valide = false;
        }
        
        if (etat.getTypeEtat() == null) {
            erreurs.add("Type d'état manquant");
            valide = false;
        }
        
        if (etat.getDonneesJson() == null || etat.getDonneesJson().isEmpty()) {
            erreurs.add("Données JSON manquantes");
            valide = false;
        }
        
        // Vérification de la cohérence des montants
        if (etat.getTotalActif() != null && etat.getTotalActif().compareTo(BigDecimal.ZERO) < 0) {
            erreurs.add("Total actif ne peut pas être négatif");
            valide = false;
        }
        
        if (etat.getTotalPassif() != null && etat.getTotalPassif().compareTo(BigDecimal.ZERO) < 0) {
            erreurs.add("Total passif ne peut pas être négatif");
            valide = false;
        }
        
        if (etat.getTotalProduits() != null && etat.getTotalProduits().compareTo(BigDecimal.ZERO) < 0) {
            erreurs.add("Total produits ne peut pas être négatif");
            valide = false;
        }
        
        if (etat.getTotalCharges() != null && etat.getTotalCharges().compareTo(BigDecimal.ZERO) < 0) {
            erreurs.add("Total charges ne peut pas être négatif");
            valide = false;
        }
        
        resultat.put("valide", valide);
        resultat.put("erreurs", erreurs);
        return resultat;
    }
    
    /**
     * Valide la conformité OHADA d'un état financier
     */
    private Map<String, Object> validerConformiteOHADA(EtatFinancierSycebnl etat) {
        Map<String, Object> resultat = new HashMap<>();
        List<String> avertissements = new ArrayList<>();
        boolean valide = true;
        
        // Vérification des postes obligatoires selon le type d'état
        List<MappingComptesPostes> mappings = mappingRepository
                .findByPaysCodeAndStandardComptableAndTypeSystemeAndTypeEtatAndActifTrueOrderByOrdreAffichage(
                        "BF", "SYSCOHADA", 
                        MappingComptesPostes.TypeSysteme.valueOf(etat.getTypeSysteme().name()),
                        MappingComptesPostes.TypeEtat.valueOf(etat.getTypeEtat().name()));
        
        if (mappings.isEmpty()) {
            avertissements.add("Aucun mapping trouvé pour ce type d'état financier");
            valide = false;
        }
        
        // Vérification des postes totaux obligatoires
        List<MappingComptesPostes> postesTotaux = mappingRepository.findPostesTotaux(
                "BF", "SYSCOHADA", 
                MappingComptesPostes.TypeSysteme.valueOf(etat.getTypeSysteme().name()),
                MappingComptesPostes.TypeEtat.valueOf(etat.getTypeEtat().name()));
        
        if (postesTotaux.isEmpty()) {
            avertissements.add("Aucun poste total trouvé pour ce type d'état financier");
        }
        
        // Vérification de la présence des soldes intermédiaires de gestion pour le compte de résultat
        if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT) {
            boolean hasSoldesIntermediaires = mappings.stream()
                    .anyMatch(m -> m.getPosteCode().startsWith("X"));
            
            if (!hasSoldesIntermediaires) {
                avertissements.add("Soldes intermédiaires de gestion manquants dans le compte de résultat");
            }
        }
        
        resultat.put("valide", valide);
        resultat.put("avertissements", avertissements);
        return resultat;
    }
    
    /**
     * Valide les notes annexes d'un état financier
     */
    private Map<String, Object> validerNotesAnnexes(EtatFinancierSycebnl etat) {
        Map<String, Object> resultat = new HashMap<>();
        List<String> avertissements = new ArrayList<>();
        boolean valide = true;
        
        // Vérification du nombre minimum de notes selon le système
        int nombreNotesMinimum = (etat.getTypeSysteme() == EtatFinancierSycebnl.TypeSysteme.NORMAL) ? 10 : 8;
        
        if (etat.getNotesAnnexes() == null || etat.getNotesAnnexes().size() < nombreNotesMinimum) {
            avertissements.add(String.format("Nombre de notes annexes insuffisant : %d trouvées, %d minimum requis",
                    etat.getNotesAnnexes() != null ? etat.getNotesAnnexes().size() : 0,
                    nombreNotesMinimum));
        }
        
        // Vérification de la présence des notes obligatoires
        if (etat.getNotesAnnexes() != null) {
            boolean hasNoteRegles = etat.getNotesAnnexes().stream()
                    .anyMatch(n -> n.getTypeNote().toString().contains("REGLES_METHODES"));
            
            if (!hasNoteRegles) {
                avertissements.add("Note sur les règles et méthodes comptables manquante");
            }
            
            boolean hasNoteEvenements = etat.getNotesAnnexes().stream()
                    .anyMatch(n -> n.getTypeNote().toString().contains("EVENEMENTS_POSTERIEURS"));
            
            if (!hasNoteEvenements) {
                avertissements.add("Note sur les événements postérieurs manquante");
            }
        }
        
        resultat.put("valide", valide);
        resultat.put("avertissements", avertissements);
        return resultat;
    }
    
    /**
     * Valide tous les états financiers d'un exercice
     * 
     * @param exerciceId ID de l'exercice
     * @return Résultat de la validation globale
     */
    public Map<String, Object> validerTousEtatsFinanciers(Long exerciceId) {
        List<EtatFinancierSycebnl> etats = etatRepository.findByExerciceIdOrderByDateArreteDesc(exerciceId);
        
        Map<String, Object> resultatGlobal = new HashMap<>();
        List<Map<String, Object>> resultatsIndividuels = new ArrayList<>();
        int nombreEtatsValides = 0;
        int nombreEtatsInvalides = 0;
        
        for (EtatFinancierSycebnl etat : etats) {
            Map<String, Object> resultat = validerEtatFinancier(etat.getId());
            resultatsIndividuels.add(resultat);
            
            if ((Boolean) resultat.get("valide")) {
                nombreEtatsValides++;
            } else {
                nombreEtatsInvalides++;
            }
        }
        
        resultatGlobal.put("exerciceId", exerciceId);
        resultatGlobal.put("nombreEtatsTotal", etats.size());
        resultatGlobal.put("nombreEtatsValides", nombreEtatsValides);
        resultatGlobal.put("nombreEtatsInvalides", nombreEtatsInvalides);
        resultatGlobal.put("resultatsIndividuels", resultatsIndividuels);
        resultatGlobal.put("dateValidation", LocalDateTime.now());
        
        return resultatGlobal;
    }
    
    /**
     * Génère un rapport de validation détaillé
     * 
     * @param etatFinancierId ID de l'état financier
     * @return Rapport de validation
     */
    public Map<String, Object> genererRapportValidation(Long etatFinancierId) {
        EtatFinancierSycebnl etat = etatRepository.findById(etatFinancierId)
                .orElseThrow(() -> new RuntimeException("État financier non trouvé"));
        
        Map<String, Object> rapport = validerEtatFinancier(etatFinancierId);
        
        // Ajout d'informations détaillées
        rapport.put("etatFinancier", Map.of(
                "id", etat.getId(),
                "typeSysteme", etat.getTypeSysteme(),
                "typeEtat", etat.getTypeEtat(),
                "dateArrete", etat.getDateArrete(),
                "statut", etat.getStatut()
        ));
        
        rapport.put("statistiques", Map.of(
                "totalActif", comptabiliteService.formaterMontant(etat.getTotalActif()),
                "totalPassif", comptabiliteService.formaterMontant(etat.getTotalPassif()),
                "totalProduits", comptabiliteService.formaterMontant(etat.getTotalProduits()),
                "totalCharges", comptabiliteService.formaterMontant(etat.getTotalCharges()),
                "resultatNet", comptabiliteService.formaterMontant(etat.getResultatNet()),
                "equilibre", etat.getEquilibre()
        ));
        
        return rapport;
    }
}
