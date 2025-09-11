package com.ecomptaia.sycebnl.service;

import com.ecomptaia.sycebnl.entity.AnalyseIASycebnl;
import com.ecomptaia.sycebnl.entity.PieceJustificativeSycebnl;
import com.ecomptaia.sycebnl.repository.AnalyseIASycebnlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service pour l'analyse IA des pièces justificatives SYCEBNL
 */
@Service
@Transactional
public class AnalyseIAService {
    
    private final AnalyseIASycebnlRepository analyseIARepository;
    
    @Autowired
    public AnalyseIAService(AnalyseIASycebnlRepository analyseIARepository) {
        this.analyseIARepository = analyseIARepository;
    }
    
    /**
     * Analyse un document avec IA
     */
    public AnalyseIASycebnl analyserDocument(PieceJustificativeSycebnl pieceJustificative) {
        // Créer une nouvelle analyse IA
        AnalyseIASycebnl analyse = new AnalyseIASycebnl();
        analyse.setPieceJustificative(pieceJustificative);
        analyse.setStatutAnalyse(AnalyseIASycebnl.StatutAnalyse.EN_COURS);
        analyse.setModeleIAUtilise("GPT-4");
        analyse.setVersionModele("4.0");
        
        try {
            // Simuler l'analyse IA
            long startTime = System.currentTimeMillis();
            
            // Analyser le texte OCR pour extraire les informations
            String texteOCR = pieceJustificative.getTexteOCR();
            if (texteOCR == null || texteOCR.trim().isEmpty()) {
                throw new RuntimeException("Aucun texte OCR disponible pour l'analyse IA");
            }
            
            // Extraire les informations avec IA
            extraireInformationsAvecIA(texteOCR, analyse);
            
            long endTime = System.currentTimeMillis();
            
            // Calculer la confiance globale
            BigDecimal confianceGlobale = calculerConfianceIA(analyse);
            analyse.setConfianceGlobale(confianceGlobale);
            analyse.setTempsTraitementMs(endTime - startTime);
            analyse.setStatutAnalyse(AnalyseIASycebnl.StatutAnalyse.TERMINEE);
            
            // Sauvegarder l'analyse
            analyse = analyseIARepository.save(analyse);
            
            return analyse;
            
        } catch (Exception e) {
            analyse.setStatutAnalyse(AnalyseIASycebnl.StatutAnalyse.ERREUR);
            analyse.setErreurAnalyse(e.getMessage());
            analyseIARepository.save(analyse);
            throw new RuntimeException("Erreur lors de l'analyse IA : " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les analyses IA d'une pièce justificative
     */
    @Transactional(readOnly = true)
    public java.util.List<AnalyseIASycebnl> getAnalysesByPJ(Long pieceJustificativeId) {
        return analyseIARepository.findByPieceJustificativeIdOrderByDateAnalyseDesc(pieceJustificativeId);
    }
    
    /**
     * Récupère la dernière analyse IA d'une pièce justificative
     */
    @Transactional(readOnly = true)
    public java.util.Optional<AnalyseIASycebnl> getDerniereAnalyseByPJ(Long pieceJustificativeId) {
        java.util.List<AnalyseIASycebnl> analyses = analyseIARepository.findDerniereAnalyseParPJ(pieceJustificativeId);
        return analyses.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(analyses.get(0));
    }
    
    /**
     * Récupère les analyses avec faible confiance
     */
    @Transactional(readOnly = true)
    public java.util.List<AnalyseIASycebnl> getAnalysesFaibleConfiance(BigDecimal seuilConfiance) {
        return analyseIARepository.findByConfianceFaible(seuilConfiance);
    }
    
    // Méthodes utilitaires privées
    
    private void extraireInformationsAvecIA(String texteOCR, AnalyseIASycebnl analyse) {
        // Simulation de l'extraction d'informations avec IA
        // Dans une vraie implémentation, on utiliserait GPT-4, Claude, ou un modèle spécialisé
        
        // Détecter le type de document
        String typeDocument = detecterTypeDocument(texteOCR);
        analyse.setTypeDocumentDetecte(typeDocument);
        analyse.setConfianceTypeDocument(BigDecimal.valueOf(0.95));
        
        // Extraire le montant
        BigDecimal montant = extraireMontant(texteOCR);
        analyse.setMontantDetecte(montant);
        analyse.setConfianceMontant(BigDecimal.valueOf(0.90));
        
        // Extraire la devise
        String devise = extraireDevise(texteOCR);
        analyse.setDeviseDetectee(devise);
        
        // Extraire la date
        LocalDate date = extraireDate(texteOCR);
        analyse.setDateDetectee(date);
        analyse.setConfianceDate(BigDecimal.valueOf(0.85));
        
        // Extraire le fournisseur
        String fournisseur = extraireFournisseur(texteOCR);
        analyse.setFournisseurDetecte(fournisseur);
        analyse.setConfianceFournisseur(BigDecimal.valueOf(0.80));
        
        // Extraire le numéro de facture
        String numeroFacture = extraireNumeroFacture(texteOCR);
        analyse.setNumeroFacture(numeroFacture);
        analyse.setConfianceNumeroFacture(BigDecimal.valueOf(0.75));
        
        // Extraire la description
        String description = extraireDescription(texteOCR);
        analyse.setDescriptionDetectee(description);
        analyse.setConfianceDescription(BigDecimal.valueOf(0.70));
        
        // Extraire la TVA
        BigDecimal tva = extraireTVA(texteOCR);
        analyse.setTvaDetectee(tva);
        analyse.setConfianceTVA(BigDecimal.valueOf(0.85));
        
        // Calculer les montants HT et TTC
        if (montant != null && tva != null) {
            BigDecimal montantHT = montant.divide(BigDecimal.ONE.add(tva.divide(BigDecimal.valueOf(100))), 2, java.math.RoundingMode.HALF_UP);
            analyse.setMontantHT(montantHT);
            analyse.setMontantTTC(montant);
        }
    }
    
    private String detecterTypeDocument(String texte) {
        String texteLower = texte.toLowerCase();
        
        if (texteLower.contains("facture")) {
            return "FACTURE";
        } else if (texteLower.contains("reçu") || texteLower.contains("recu")) {
            return "RECU";
        } else if (texteLower.contains("bon de commande")) {
            return "BON_COMMANDE";
        } else if (texteLower.contains("bon de livraison")) {
            return "BON_LIVRAISON";
        } else {
            return "AUTRE";
        }
    }
    
    private BigDecimal extraireMontant(String texte) {
        // Rechercher les montants dans le texte
        Pattern pattern = Pattern.compile("(?:total|montant|somme)[\\s:]*([0-9,]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            String montantStr = matcher.group(1).replace(",", "");
            try {
                return new BigDecimal(montantStr);
            } catch (NumberFormatException e) {
                // Ignorer et continuer
            }
        }
        
        // Recherche alternative pour les montants TTC
        pattern = Pattern.compile("ttc[\\s:]*([0-9,]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            String montantStr = matcher.group(1).replace(",", "");
            try {
                return new BigDecimal(montantStr);
            } catch (NumberFormatException e) {
                // Ignorer et continuer
            }
        }
        
        return null;
    }
    
    private String extraireDevise(String texte) {
        // Rechercher la devise dans le texte
        Pattern pattern = Pattern.compile("(?:devise|currency)[\\s:]*([A-Z]{3})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Par défaut, XOF pour le Sénégal
        return "XOF";
    }
    
    private LocalDate extraireDate(String texte) {
        // Rechercher les dates dans le texte
        Pattern pattern = Pattern.compile("(\\d{1,2})[\\s/.-](\\d{1,2})[\\s/.-](\\d{4})");
        Matcher matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            try {
                int jour = Integer.parseInt(matcher.group(1));
                int mois = Integer.parseInt(matcher.group(2));
                int annee = Integer.parseInt(matcher.group(3));
                return LocalDate.of(annee, mois, jour);
            } catch (Exception e) {
                // Ignorer et continuer
            }
        }
        
        return null;
    }
    
    private String extraireFournisseur(String texte) {
        // Rechercher le fournisseur dans le texte
        Pattern pattern = Pattern.compile("(?:fournisseur|supplier|vendor)[\\s:]*([^\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Recherche alternative
        pattern = Pattern.compile("^([A-Z][^\\n]+(?:SARL|SA|SAS|SRL|LTD|INC))", Pattern.MULTILINE);
        matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return null;
    }
    
    private String extraireNumeroFacture(String texte) {
        // Rechercher le numéro de facture
        Pattern pattern = Pattern.compile("(?:facture|invoice|n°|no|number)[\\s:]*([A-Z0-9-]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return null;
    }
    
    private String extraireDescription(String texte) {
        // Extraire la description des produits/services
        Pattern pattern = Pattern.compile("(?:désignation|description|item)[\\s:]*([^\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return null;
    }
    
    private BigDecimal extraireTVA(String texte) {
        // Rechercher le taux de TVA
        Pattern pattern = Pattern.compile("tva[\\s:]*([0-9]+(?:\\.[0-9]+)?)%", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texte);
        
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (NumberFormatException e) {
                // Ignorer et continuer
            }
        }
        
        return null;
    }
    
    private BigDecimal calculerConfianceIA(AnalyseIASycebnl analyse) {
        // Calculer la confiance globale basée sur les confiances individuelles
        double confiance = 0.0;
        int nbElements = 0;
        
        if (analyse.getConfianceTypeDocument() != null) {
            confiance += analyse.getConfianceTypeDocument().doubleValue();
            nbElements++;
        }
        
        if (analyse.getConfianceMontant() != null) {
            confiance += analyse.getConfianceMontant().doubleValue();
            nbElements++;
        }
        
        if (analyse.getConfianceDate() != null) {
            confiance += analyse.getConfianceDate().doubleValue();
            nbElements++;
        }
        
        if (analyse.getConfianceFournisseur() != null) {
            confiance += analyse.getConfianceFournisseur().doubleValue();
            nbElements++;
        }
        
        if (nbElements > 0) {
            return BigDecimal.valueOf(confiance / nbElements);
        }
        
        return BigDecimal.valueOf(0.5); // Confiance par défaut
    }
}
