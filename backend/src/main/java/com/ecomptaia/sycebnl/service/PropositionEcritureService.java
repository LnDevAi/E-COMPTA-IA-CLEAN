package com.ecomptaia.sycebnl.service;

import com.ecomptaia.entity.Account;
import com.ecomptaia.sycebnl.entity.AnalyseIASycebnl;
import com.ecomptaia.sycebnl.entity.LignePropositionEcritureSycebnl;
import com.ecomptaia.sycebnl.entity.PieceJustificativeSycebnl;
import com.ecomptaia.sycebnl.entity.PropositionEcritureSycebnl;
import com.ecomptaia.sycebnl.repository.PropositionEcritureSycebnlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la génération de propositions d'écritures comptables SYCEBNL
 */
@Service
@Transactional
public class PropositionEcritureService {
    
    private final PropositionEcritureSycebnlRepository propositionRepository;
    
    @Autowired
    public PropositionEcritureService(
            PropositionEcritureSycebnlRepository propositionRepository) {
        this.propositionRepository = propositionRepository;
    }
    
    /**
     * Génère des propositions d'écritures pour une pièce justificative
     */
    public List<PropositionEcritureSycebnl> genererPropositions(PieceJustificativeSycebnl pieceJustificative) {
        List<PropositionEcritureSycebnl> propositions = new ArrayList<>();
        
        try {
            // Récupérer l'analyse IA
            Optional<AnalyseIASycebnl> analyseIAOpt = getDerniereAnalyseIA(pieceJustificative.getId());
            if (analyseIAOpt.isEmpty()) {
                throw new RuntimeException("Aucune analyse IA disponible pour cette pièce justificative");
            }
            
            AnalyseIASycebnl analyseIA = analyseIAOpt.get();
            
            // Générer les propositions selon le type de document
            String typeDocument = analyseIA.getTypeDocumentDetecte();
            
            switch (typeDocument) {
                case "FACTURE":
                    propositions.addAll(genererPropositionsFacture(pieceJustificative, analyseIA));
                    break;
                case "RECU":
                    propositions.addAll(genererPropositionsRecu(pieceJustificative, analyseIA));
                    break;
                case "BON_COMMANDE":
                    propositions.addAll(genererPropositionsBonCommande(pieceJustificative, analyseIA));
                    break;
                default:
                    propositions.addAll(genererPropositionsGenerique(pieceJustificative, analyseIA));
                    break;
            }
            
            return propositions;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération des propositions : " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère des propositions pour une facture
     */
    private List<PropositionEcritureSycebnl> genererPropositionsFacture(
            PieceJustificativeSycebnl pieceJustificative, AnalyseIASycebnl analyseIA) {
        
        List<PropositionEcritureSycebnl> propositions = new ArrayList<>();
        
        // Proposition 1 : Facture fournisseur classique
        PropositionEcritureSycebnl proposition1 = creerPropositionBase(
            pieceJustificative, 
            "FACTURE_FOURNISSEUR",
            "Facture fournisseur - " + pieceJustificative.getNumeroPJ(),
            analyseIA.getMontantDetecte()
        );
        
        // Créer les lignes de la proposition
        List<LignePropositionEcritureSycebnl> lignes1 = new ArrayList<>();
        
        // Ligne 1 : Compte de charge (débit)
        Account compteCharge = trouverCompteParPattern("60%"); // Comptes de charges
        if (compteCharge != null) {
            LignePropositionEcritureSycebnl ligne1 = new LignePropositionEcritureSycebnl(
                proposition1, compteCharge, "Prestation fournisseur", 
                analyseIA.getMontantHT(), null, 1);
            ligne1.setConfianceLigne(BigDecimal.valueOf(0.90));
            ligne1.setJustificationLigne("Compte de charge standard pour facture fournisseur");
            ligne1.setRegleAppliquee("REGLE_FACTURE_FOURNISSEUR");
            lignes1.add(ligne1);
        }
        
        // Ligne 2 : TVA déductible (débit)
        if (analyseIA.getTvaDetectee() != null && analyseIA.getTvaDetectee().compareTo(BigDecimal.ZERO) > 0) {
            Account compteTVA = trouverCompteParPattern("445%"); // TVA déductible
            if (compteTVA != null) {
                BigDecimal montantTVA = analyseIA.getMontantTTC().subtract(analyseIA.getMontantHT());
                LignePropositionEcritureSycebnl ligne2 = new LignePropositionEcritureSycebnl(
                    proposition1, compteTVA, "TVA déductible", 
                    montantTVA, null, 2);
                ligne2.setConfianceLigne(BigDecimal.valueOf(0.85));
                ligne2.setJustificationLigne("TVA déductible sur facture fournisseur");
                ligne2.setRegleAppliquee("REGLE_TVA_DEDUCTIBLE");
                lignes1.add(ligne2);
            }
        }
        
        // Ligne 3 : Compte fournisseur (crédit)
        Account compteFournisseur = trouverCompteParPattern("401%"); // Fournisseurs
        if (compteFournisseur != null) {
            LignePropositionEcritureSycebnl ligne3 = new LignePropositionEcritureSycebnl(
                proposition1, compteFournisseur, "Dette fournisseur", 
                null, analyseIA.getMontantTTC(), 3);
            ligne3.setConfianceLigne(BigDecimal.valueOf(0.95));
            ligne3.setJustificationLigne("Dette envers le fournisseur");
            ligne3.setRegleAppliquee("REGLE_FOURNISSEUR");
            lignes1.add(ligne3);
        }
        
        proposition1.setLignesProposition(lignes1);
        proposition1.setConfianceProposition(calculerConfianceProposition(lignes1));
        propositions.add(propositionRepository.save(proposition1));
        
        return propositions;
    }
    
    /**
     * Génère des propositions pour un reçu
     */
    private List<PropositionEcritureSycebnl> genererPropositionsRecu(
            PieceJustificativeSycebnl pieceJustificative, AnalyseIASycebnl analyseIA) {
        
        List<PropositionEcritureSycebnl> propositions = new ArrayList<>();
        
        // Proposition : Encaissement client
        PropositionEcritureSycebnl proposition = creerPropositionBase(
            pieceJustificative, 
            "ENCAISSEMENT_CLIENT",
            "Encaissement client - " + pieceJustificative.getNumeroPJ(),
            analyseIA.getMontantDetecte()
        );
        
        // Créer les lignes de la proposition
        List<LignePropositionEcritureSycebnl> lignes = new ArrayList<>();
        
        // Ligne 1 : Compte banque/caisse (débit)
        Account compteBanque = trouverCompteParPattern("5%"); // Comptes de trésorerie
        if (compteBanque != null) {
            LignePropositionEcritureSycebnl ligne1 = new LignePropositionEcritureSycebnl(
                proposition, compteBanque, "Encaissement", 
                analyseIA.getMontantDetecte(), null, 1);
            ligne1.setConfianceLigne(BigDecimal.valueOf(0.90));
            ligne1.setJustificationLigne("Encaissement en banque/caisse");
            ligne1.setRegleAppliquee("REGLE_ENCAISSEMENT");
            lignes.add(ligne1);
        }
        
        // Ligne 2 : Compte client (crédit)
        Account compteClient = trouverCompteParPattern("411%"); // Clients
        if (compteClient != null) {
            LignePropositionEcritureSycebnl ligne2 = new LignePropositionEcritureSycebnl(
                proposition, compteClient, "Règlement client", 
                null, analyseIA.getMontantDetecte(), 2);
            ligne2.setConfianceLigne(BigDecimal.valueOf(0.95));
            ligne2.setJustificationLigne("Règlement de la créance client");
            ligne2.setRegleAppliquee("REGLE_CLIENT");
            lignes.add(ligne2);
        }
        
        proposition.setLignesProposition(lignes);
        proposition.setConfianceProposition(calculerConfianceProposition(lignes));
        propositions.add(propositionRepository.save(proposition));
        
        return propositions;
    }
    
    /**
     * Génère des propositions pour un bon de commande
     */
    private List<PropositionEcritureSycebnl> genererPropositionsBonCommande(
            PieceJustificativeSycebnl pieceJustificative, AnalyseIASycebnl analyseIA) {
        
        List<PropositionEcritureSycebnl> propositions = new ArrayList<>();
        
        // Proposition : Engagement de commande
        PropositionEcritureSycebnl proposition = creerPropositionBase(
            pieceJustificative, 
            "AUTRE",
            "Engagement commande - " + pieceJustificative.getNumeroPJ(),
            analyseIA.getMontantDetecte()
        );
        
        // Créer les lignes de la proposition
        List<LignePropositionEcritureSycebnl> lignes = new ArrayList<>();
        
        // Ligne 1 : Compte d'engagement (débit)
        Account compteEngagement = trouverCompteParPattern("408%"); // Engagements
        if (compteEngagement != null) {
            LignePropositionEcritureSycebnl ligne1 = new LignePropositionEcritureSycebnl(
                proposition, compteEngagement, "Engagement commande", 
                analyseIA.getMontantDetecte(), null, 1);
            ligne1.setConfianceLigne(BigDecimal.valueOf(0.80));
            ligne1.setJustificationLigne("Engagement de commande");
            ligne1.setRegleAppliquee("REGLE_ENGAGEMENT");
            lignes.add(ligne1);
        }
        
        // Ligne 2 : Compte d'engagement (crédit)
        if (compteEngagement != null) {
            LignePropositionEcritureSycebnl ligne2 = new LignePropositionEcritureSycebnl(
                proposition, compteEngagement, "Engagement fournisseur", 
                null, analyseIA.getMontantDetecte(), 2);
            ligne2.setConfianceLigne(BigDecimal.valueOf(0.80));
            ligne2.setJustificationLigne("Engagement envers le fournisseur");
            ligne2.setRegleAppliquee("REGLE_ENGAGEMENT");
            lignes.add(ligne2);
        }
        
        proposition.setLignesProposition(lignes);
        proposition.setConfianceProposition(calculerConfianceProposition(lignes));
        propositions.add(propositionRepository.save(proposition));
        
        return propositions;
    }
    
    /**
     * Génère des propositions génériques
     */
    private List<PropositionEcritureSycebnl> genererPropositionsGenerique(
            PieceJustificativeSycebnl pieceJustificative, AnalyseIASycebnl analyseIA) {
        
        List<PropositionEcritureSycebnl> propositions = new ArrayList<>();
        
        // Proposition générique
        PropositionEcritureSycebnl proposition = creerPropositionBase(
            pieceJustificative, 
            "AUTRE",
            "Écriture générique - " + pieceJustificative.getNumeroPJ(),
            analyseIA.getMontantDetecte()
        );
        
        // Créer les lignes de la proposition
        List<LignePropositionEcritureSycebnl> lignes = new ArrayList<>();
        
        // Ligne 1 : Compte générique (débit)
        Account compteGenerique = trouverCompteParPattern("6%"); // Comptes de charges
        if (compteGenerique != null) {
            LignePropositionEcritureSycebnl ligne1 = new LignePropositionEcritureSycebnl(
                proposition, compteGenerique, "Charge générique", 
                analyseIA.getMontantDetecte(), null, 1);
            ligne1.setConfianceLigne(BigDecimal.valueOf(0.60));
            ligne1.setJustificationLigne("Écriture générique - à vérifier");
            ligne1.setRegleAppliquee("REGLE_GENERIQUE");
            lignes.add(ligne1);
        }
        
        // Ligne 2 : Compte générique (crédit)
        Account compteGenerique2 = trouverCompteParPattern("4%"); // Comptes de tiers
        if (compteGenerique2 != null) {
            LignePropositionEcritureSycebnl ligne2 = new LignePropositionEcritureSycebnl(
                proposition, compteGenerique2, "Tiers générique", 
                null, analyseIA.getMontantDetecte(), 2);
            ligne2.setConfianceLigne(BigDecimal.valueOf(0.60));
            ligne2.setJustificationLigne("Écriture générique - à vérifier");
            ligne2.setRegleAppliquee("REGLE_GENERIQUE");
            lignes.add(ligne2);
        }
        
        proposition.setLignesProposition(lignes);
        proposition.setConfianceProposition(calculerConfianceProposition(lignes));
        propositions.add(propositionRepository.save(proposition));
        
        return propositions;
    }
    
    /**
     * Crée une proposition de base
     */
    private PropositionEcritureSycebnl creerPropositionBase(
            PieceJustificativeSycebnl pieceJustificative,
            String typeEcriture,
            String libelle,
            BigDecimal montant) {
        
        PropositionEcritureSycebnl proposition = new PropositionEcritureSycebnl();
        proposition.setPieceJustificative(pieceJustificative);
        proposition.setNumeroProposition(genererNumeroProposition(pieceJustificative.getId()));
        proposition.setLibelleProposition(libelle);
        proposition.setDateProposition(pieceJustificative.getDatePiece());
        proposition.setMontantTotal(montant);
        proposition.setDevise(pieceJustificative.getDevise());
        proposition.setTypeEcriture(PropositionEcritureSycebnl.TypeEcritureProposition.valueOf(typeEcriture));
        proposition.setStatutProposition(PropositionEcritureSycebnl.StatutProposition.PROPOSEE);
        proposition.setCreePar(pieceJustificative.getUtilisateur().getId());
        
        return proposition;
    }
    
    /**
     * Trouve un compte par pattern
     */
    private Account trouverCompteParPattern(String pattern) {
        // Cette méthode utilise le plan comptable SYCEBNL
        // Pour l'instant, on retourne null car les comptes seront créés dynamiquement
        // Dans une implémentation complète, on utiliserait le PlanComptableSycebnlService
        return null;
    }
    
    /**
     * Calcule la confiance d'une proposition
     */
    private BigDecimal calculerConfianceProposition(List<LignePropositionEcritureSycebnl> lignes) {
        if (lignes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        double confianceTotale = 0.0;
        for (LignePropositionEcritureSycebnl ligne : lignes) {
            if (ligne.getConfianceLigne() != null) {
                confianceTotale += ligne.getConfianceLigne().doubleValue();
            }
        }
        
        return BigDecimal.valueOf(confianceTotale / lignes.size());
    }
    
    /**
     * Génère un numéro de proposition
     */
    private String genererNumeroProposition(Long pjId) {
        return "PROP-" + pjId + "-" + System.currentTimeMillis();
    }
    
    /**
     * Récupère la dernière analyse IA d'une PJ
     */
    private Optional<AnalyseIASycebnl> getDerniereAnalyseIA(Long pjId) {
        // Cette méthode devrait utiliser le service d'analyse IA
        // Pour l'instant, on simule
        return Optional.empty();
    }
}
