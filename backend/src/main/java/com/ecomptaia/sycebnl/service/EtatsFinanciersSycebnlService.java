package com.ecomptaia.sycebnl.service;

import com.ecomptaia.sycebnl.entity.*;
import com.ecomptaia.sycebnl.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service principal pour la génération des états financiers SYCEBNL
 * avec mapping automatique des comptes vers les postes
 */
@Service
@Transactional(readOnly = true)
public class EtatsFinanciersSycebnlService {
    
    private final MappingComptesPostesRepository mappingRepository;
    private final EtatFinancierSycebnlRepository etatRepository;
    private final NoteAnnexeSycebnlRepository noteRepository;
    private final ComptabiliteSycebnlService comptabiliteService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public EtatsFinanciersSycebnlService(
            MappingComptesPostesRepository mappingRepository,
            EtatFinancierSycebnlRepository etatRepository,
            NoteAnnexeSycebnlRepository noteRepository,
            ComptabiliteSycebnlService comptabiliteService,
            ObjectMapper objectMapper) {
        this.mappingRepository = mappingRepository;
        this.etatRepository = etatRepository;
        this.noteRepository = noteRepository;
        this.comptabiliteService = comptabiliteService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Génère un état financier complet avec mapping automatique
     */
    @Transactional
    public EtatFinancierSycebnl genererEtatFinancier(Long exerciceId, 
                                                     EtatFinancierSycebnl.TypeSysteme typeSysteme,
                                                     EtatFinancierSycebnl.TypeEtat typeEtat) {
        
        // Récupération de l'exercice (à adapter selon votre structure)
        // ExerciceSMT exercice = exerciceRepository.findById(exerciceId)
        //         .orElseThrow(() -> new EntityNotFoundException("Exercice non trouvé"));
        
        // Création de l'état financier
        EtatFinancierSycebnl etat = EtatFinancierSycebnl.builder()
                .typeSysteme(typeSysteme)
                .typeEtat(typeEtat)
                .dateArrete(LocalDate.now()) // À adapter selon l'exercice
                .statut(EtatFinancierSycebnl.StatutEtat.BROUILLON)
                .generePar("SYCEBNL - Génération automatique")
                .dateCreation(LocalDateTime.now())
                .build();
        
        // Récupération des mappings pour cet état
        List<MappingComptesPostes> mappings = mappingRepository
                .findByPaysCodeAndStandardComptableAndTypeSystemeAndTypeEtatAndActifTrueOrderByOrdreAffichage(
                        "BF", "SYSCOHADA", 
                        MappingComptesPostes.TypeSysteme.valueOf(typeSysteme.name()),
                        MappingComptesPostes.TypeEtat.valueOf(typeEtat.name()));
        
        // Génération des données de l'état avec mapping automatique
        Map<String, Object> donneesEtat = genererDonneesEtatAvecMapping(mappings);
        
        try {
            etat.setDonneesJson(objectMapper.writeValueAsString(donneesEtat));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sérialisation des données", e);
        }
        
        // Calcul des totaux
        calculerTotauxEtat(etat, donneesEtat);
        
        // Vérification de l'équilibre pour les bilans
        if (typeEtat == EtatFinancierSycebnl.TypeEtat.BILAN) {
            etat.setEquilibre(verifierEquilibreBilan(etat));
        }
        
        return etatRepository.save(etat);
    }
    
    /**
     * Génère les données de l'état avec mapping automatique des comptes
     */
    private Map<String, Object> genererDonneesEtatAvecMapping(List<MappingComptesPostes> mappings) {
        Map<String, Object> donnees = new LinkedHashMap<>();
        
        for (MappingComptesPostes mapping : mappings) {
            Map<String, Object> poste = new HashMap<>();
            poste.put("code", mapping.getPosteCode());
            poste.put("libelle", mapping.getPosteLibelle());
            poste.put("niveau", mapping.getNiveau());
            poste.put("estTotal", mapping.getEstTotal());
            poste.put("signeNormal", mapping.getSigneNormal().name());
            poste.put("ordreAffichage", mapping.getOrdreAffichage());
            
            // Calcul du solde du poste en agrégeant les comptes
            BigDecimal soldePoste = calculerSoldePoste(mapping);
            poste.put("solde", soldePoste);
            
            // Formatage du montant pour l'affichage
            poste.put("soldeFormate", formaterMontant(soldePoste));
            
            donnees.put(mapping.getPosteCode(), poste);
        }
        
        return donnees;
    }
    
    /**
     * Calcule le solde d'un poste en agrégeant les comptes selon les patterns
     */
    private BigDecimal calculerSoldePoste(MappingComptesPostes mapping) {
        // Utilisation du service de comptabilité pour calculer le solde par patterns
        return comptabiliteService.calculerSoldePosteParPatterns(
                mapping.getComptesPattern(), 
                LocalDate.now(), // À adapter selon l'exercice
                mapping.getSigneNormal().name()
        );
    }
    
    /**
     * Calcule les totaux de l'état financier
     */
    private void calculerTotauxEtat(EtatFinancierSycebnl etat, Map<String, Object> donnees) {
        BigDecimal totalActif = BigDecimal.ZERO;
        BigDecimal totalPassif = BigDecimal.ZERO;
        BigDecimal totalProduits = BigDecimal.ZERO;
        BigDecimal totalCharges = BigDecimal.ZERO;
        
        for (Map.Entry<String, Object> entry : donnees.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> poste = (Map<String, Object>) entry.getValue();
            BigDecimal solde = (BigDecimal) poste.get("solde");
            String signeNormal = (String) poste.get("signeNormal");
            
            // Calcul selon le type d'état
            switch (etat.getTypeEtat()) {
                case BILAN:
                    if ("DEBIT".equals(signeNormal)) {
                        totalActif = totalActif.add(solde);
                    } else {
                        totalPassif = totalPassif.add(solde);
                    }
                    break;
                case COMPTE_RESULTAT:
                case RECETTES_DEPENSES:
                    if ("CREDIT".equals(signeNormal)) {
                        totalProduits = totalProduits.add(solde);
                    } else {
                        totalCharges = totalCharges.add(solde);
                    }
                    break;
                case TABLEAU_FLUX:
                case SITUATION_TRESORERIE:
                    // Pour les tableaux de flux et situations de trésorerie
                    if ("DEBIT".equals(signeNormal)) {
                        totalActif = totalActif.add(solde);
                    } else {
                        totalPassif = totalPassif.add(solde);
                    }
                    break;
                case ANNEXES:
                    // Les annexes n'ont pas de totaux à calculer
                    break;
            }
        }
        
        etat.setTotalActif(totalActif);
        etat.setTotalPassif(totalPassif);
        etat.setTotalProduits(totalProduits);
        etat.setTotalCharges(totalCharges);
        
        // Calcul du résultat net
        if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT ||
            etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.RECETTES_DEPENSES) {
            etat.setResultatNet(comptabiliteService.calculerResultatNet(totalProduits, totalCharges));
        }
    }
    
    /**
     * Vérifie l'équilibre d'un bilan (Actif = Passif)
     */
    private boolean verifierEquilibreBilan(EtatFinancierSycebnl etat) {
        return comptabiliteService.verifierEquilibreBilan(etat.getTotalActif(), etat.getTotalPassif());
    }
    
    /**
     * Génère les notes annexes pour un état financier
     */
    @Transactional
    public List<NoteAnnexeSycebnl> genererNotesAnnexes(Long etatFinancierId) {
        EtatFinancierSycebnl etat = etatRepository.findById(etatFinancierId)
                .orElseThrow(() -> new RuntimeException("État financier non trouvé"));
        
        List<NoteAnnexeSycebnl> notes = new ArrayList<>();
        
        // Génération des notes selon le type de système
        if (etat.getTypeSysteme() == EtatFinancierSycebnl.TypeSysteme.NORMAL) {
            notes.addAll(genererNotesSystemeNormal(etat));
        } else {
            notes.addAll(genererNotesSystemeMinimal(etat));
        }
        
        return noteRepository.saveAll(notes);
    }
    
    /**
     * Génère les notes annexes pour le système normal
     */
    private List<NoteAnnexeSycebnl> genererNotesSystemeNormal(EtatFinancierSycebnl etat) {
        List<NoteAnnexeSycebnl> notes = new ArrayList<>();
        
        // Note 1: Règles et méthodes comptables
        notes.add(creerNote(etat, "Note 1", "Règles et méthodes comptables", 
                NoteAnnexeSycebnl.TypeNote.NOTE_1_REGLES_METHODES, 1,
                genererContenuNoteReglesMethodes()));
        
        // Note 2: Immobilisations
        notes.add(creerNote(etat, "Note 2", "Immobilisations", 
                NoteAnnexeSycebnl.TypeNote.NOTE_2_IMMOBILISATIONS, 2,
                genererContenuNoteImmobilisations()));
        
        // Note 3: Stocks
        notes.add(creerNote(etat, "Note 3", "Stocks", 
                NoteAnnexeSycebnl.TypeNote.NOTE_3_STOCKS, 3,
                genererContenuNoteStocks()));
        
        // Note 4: Créances
        notes.add(creerNote(etat, "Note 4", "Créances", 
                NoteAnnexeSycebnl.TypeNote.NOTE_4_CREANCES, 4,
                genererContenuNoteCreances()));
        
        // Note 5: Dettes
        notes.add(creerNote(etat, "Note 5", "Dettes", 
                NoteAnnexeSycebnl.TypeNote.NOTE_5_DETTES, 5,
                genererContenuNoteDettes()));
        
        // Note 6: Capitaux propres
        notes.add(creerNote(etat, "Note 6", "Capitaux propres", 
                NoteAnnexeSycebnl.TypeNote.NOTE_6_CAPITAUX_PROPRES, 6,
                genererContenuNoteCapitauxPropres()));
        
        // Note 7: Charges
        notes.add(creerNote(etat, "Note 7", "Charges", 
                NoteAnnexeSycebnl.TypeNote.NOTE_7_CHARGES, 7,
                genererContenuNoteCharges()));
        
        // Note 8: Produits
        notes.add(creerNote(etat, "Note 8", "Produits", 
                NoteAnnexeSycebnl.TypeNote.NOTE_8_PRODUITS, 8,
                genererContenuNoteProduits()));
        
        // Note 9: Engagements hors bilan
        notes.add(creerNote(etat, "Note 9", "Engagements hors bilan", 
                NoteAnnexeSycebnl.TypeNote.NOTE_9_ENGAGEMENTS_HORS_BILAN, 9,
                genererContenuNoteEngagementsHorsBilan()));
        
        // Note 10: Événements postérieurs
        notes.add(creerNote(etat, "Note 10", "Événements postérieurs à la clôture", 
                NoteAnnexeSycebnl.TypeNote.NOTE_10_EVENEMENTS_POSTERIEURS, 10,
                genererContenuNoteEvenementsPosterieurs()));
        
        return notes;
    }
    
    /**
     * Génère les notes annexes pour le système minimal
     */
    private List<NoteAnnexeSycebnl> genererNotesSystemeMinimal(EtatFinancierSycebnl etat) {
        List<NoteAnnexeSycebnl> notes = new ArrayList<>();
        
        // Note SMT 1: Règles et méthodes comptables
        notes.add(creerNote(etat, "Note 1", "Règles et méthodes comptables", 
                NoteAnnexeSycebnl.TypeNote.NOTE_SMT_1_REGLES_METHODES, 1,
                genererContenuNoteSMTReglesMethodes()));
        
        // Note SMT 2: Immobilisations
        notes.add(creerNote(etat, "Note 2", "Immobilisations", 
                NoteAnnexeSycebnl.TypeNote.NOTE_SMT_2_IMMOBILISATIONS, 2,
                genererContenuNoteSMTImmobilisations()));
        
        // Note SMT 3: Trésorerie
        notes.add(creerNote(etat, "Note 3", "Trésorerie", 
                NoteAnnexeSycebnl.TypeNote.NOTE_SMT_3_TRESORERIE, 3,
                genererContenuNoteSMTTresorerie()));
        
        // Note SMT 4: Fonds propres
        notes.add(creerNote(etat, "Note 4", "Fonds propres", 
                NoteAnnexeSycebnl.TypeNote.NOTE_SMT_4_FONDS_PROPRES, 4,
                genererContenuNoteSMTFondsPropres()));
        
        // Note SMT 5: Dettes
        notes.add(creerNote(etat, "Note 5", "Dettes", 
                NoteAnnexeSycebnl.TypeNote.NOTE_SMT_5_DETTES, 5,
                genererContenuNoteSMTDettes()));
        
        // Note SMT 6: Ressources
        notes.add(creerNote(etat, "Note 6", "Ressources", 
                NoteAnnexeSycebnl.TypeNote.NOTE_SMT_6_RESSOURCES, 6,
                genererContenuNoteSMTRessources()));
        
        // Note SMT 7: Charges
        notes.add(creerNote(etat, "Note 7", "Charges", 
                NoteAnnexeSycebnl.TypeNote.NOTE_SMT_7_CHARGES, 7,
                genererContenuNoteSMTCharges()));
        
        // Note SMT 8: Événements postérieurs
        notes.add(creerNote(etat, "Note 8", "Événements postérieurs à la clôture", 
                NoteAnnexeSycebnl.TypeNote.NOTE_SMT_8_EVENEMENTS_POSTERIEURS, 8,
                genererContenuNoteSMTEvenementsPosterieurs()));
        
        return notes;
    }
    
    /**
     * Crée une note annexe
     */
    private NoteAnnexeSycebnl creerNote(EtatFinancierSycebnl etat, String numeroNote, String titreNote,
                                       NoteAnnexeSycebnl.TypeNote typeNote, Integer ordreAffichage, String contenu) {
        return NoteAnnexeSycebnl.builder()
                .etatFinancier(etat)
                .numeroNote(numeroNote)
                .titreNote(titreNote)
                .typeNote(typeNote)
                .contenuNote(contenu)
                .ordreAffichage(ordreAffichage)
                .dateCreation(LocalDateTime.now())
                .build();
    }
    
    /**
     * Formate un montant pour l'affichage
     */
    private String formaterMontant(BigDecimal montant) {
        return comptabiliteService.formaterMontant(montant);
    }
    
    // Méthodes de génération du contenu des notes (à implémenter selon vos besoins)
    private String genererContenuNoteReglesMethodes() {
        return "1.1 RÉFÉRENTIEL COMPTABLE APPLICABLE\n" +
               "Les comptes sont tenus selon le référentiel SYCEBNL-OHADA.\n\n" +
               "1.2 PRINCIPES COMPTABLES RETENUS\n" +
               "- Comptabilité d'engagement\n" +
               "- Évaluation au coût historique\n" +
               "- Prudence dans l'évaluation";
    }
    
    private String genererContenuNoteImmobilisations() {
        return "2.1 IMMOBILISATIONS INCORPORELLES\n" +
               "Les immobilisations incorporelles sont amorties selon la méthode linéaire.\n\n" +
               "2.2 IMMOBILISATIONS CORPORELLES\n" +
               "Les immobilisations corporelles sont évaluées au coût d'acquisition.";
    }
    
    private String genererContenuNoteStocks() {
        return "3.1 ÉVALUATION DES STOCKS\n" +
               "Les stocks sont évalués selon la méthode du coût moyen pondéré.";
    }
    
    private String genererContenuNoteCreances() {
        return "4.1 CRÉANCES CLIENTS\n" +
               "Les créances clients sont évaluées à leur valeur nominale.";
    }
    
    private String genererContenuNoteDettes() {
        return "5.1 DETTES FOURNISSEURS\n" +
               "Les dettes fournisseurs sont évaluées à leur valeur nominale.";
    }
    
    private String genererContenuNoteCapitauxPropres() {
        return "6.1 CAPITAL SOCIAL\n" +
               "Le capital social est libéré à 100%.";
    }
    
    private String genererContenuNoteCharges() {
        return "7.1 RÉPARTITION DES CHARGES\n" +
               "Les charges sont réparties par nature et par fonction.";
    }
    
    private String genererContenuNoteProduits() {
        return "8.1 RÉPARTITION DES PRODUITS\n" +
               "Les produits sont répartis par nature et par fonction.";
    }
    
    private String genererContenuNoteEngagementsHorsBilan() {
        return "9.1 ENGAGEMENTS DONNÉS\n" +
               "Aucun engagement donné significatif.\n\n" +
               "9.2 ENGAGEMENTS REÇUS\n" +
               "Aucun engagement reçu significatif.";
    }
    
    private String genererContenuNoteEvenementsPosterieurs() {
        return "10.1 ÉVÉNEMENTS POSTÉRIEURS À LA CLÔTURE\n" +
               "Aucun événement postérieur significatif n'a été identifié.";
    }
    
    // Méthodes pour les notes SMT
    private String genererContenuNoteSMTReglesMethodes() {
        return "1.1 RÉFÉRENTIEL COMPTABLE APPLICABLE\n" +
               "Les comptes sont tenus selon le Système Minimal de Trésorerie (SMT) du référentiel SYCEBNL-OHADA.\n\n" +
               "1.2 PRINCIPES COMPTABLES RETENUS\n" +
               "- Comptabilité d'engagement simplifiée\n" +
               "- Évaluation au coût historique\n" +
               "- Prudence dans l'évaluation";
    }
    
    private String genererContenuNoteSMTImmobilisations() {
        return "2.1 IMMOBILISATIONS\n" +
               "Les immobilisations sont amorties selon la méthode linéaire.";
    }
    
    private String genererContenuNoteSMTTresorerie() {
        return "3.1 TRÉSORERIE\n" +
               "La trésorerie comprend les disponibilités en caisse et en banque.";
    }
    
    private String genererContenuNoteSMTFondsPropres() {
        return "4.1 FONDS PROPRES\n" +
               "Les fonds propres comprennent les fonds associatifs et les réserves.";
    }
    
    private String genererContenuNoteSMTDettes() {
        return "5.1 DETTES\n" +
               "Les dettes sont évaluées à leur valeur nominale.";
    }
    
    private String genererContenuNoteSMTRessources() {
        return "6.1 RESSOURCES\n" +
               "Les ressources comprennent les dons, subventions et prestations.";
    }
    
    private String genererContenuNoteSMTCharges() {
        return "7.1 CHARGES\n" +
               "Les charges sont réparties par nature.";
    }
    
    private String genererContenuNoteSMTEvenementsPosterieurs() {
        return "8.1 ÉVÉNEMENTS POSTÉRIEURS À LA CLÔTURE\n" +
               "Aucun événement postérieur significatif n'a été identifié.";
    }
}
