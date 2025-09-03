package com.ecomptaia.model.ohada;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Modèle de Tableau des Flux de Trésorerie conforme aux standards OHADA/SYSCOHADA
 */
public class TableauFluxSYSCOHADA {
    
    // FLUX DE TRÉSORERIE LIÉS À L'ACTIVITÉ
    private Map<String, PosteFluxTresorerie> fluxActivite = new HashMap<>();
    
    // FLUX DE TRÉSORERIE LIÉS AUX INVESTISSEMENTS
    private Map<String, PosteFluxTresorerie> fluxInvestissement = new HashMap<>();
    
    // FLUX DE TRÉSORERIE LIÉS AU FINANCEMENT
    private Map<String, PosteFluxTresorerie> fluxFinancement = new HashMap<>();
    
    // TOTAUX
    private BigDecimal totalFluxActivite = BigDecimal.ZERO;
    private BigDecimal totalFluxInvestissement = BigDecimal.ZERO;
    private BigDecimal totalFluxFinancement = BigDecimal.ZERO;
    private BigDecimal variationTresorerie = BigDecimal.ZERO;
    private BigDecimal tresorerieDebut = BigDecimal.ZERO;
    private BigDecimal tresorerieFin = BigDecimal.ZERO;
    
    public TableauFluxSYSCOHADA() {
        initialiserStructure();
    }
    
    private void initialiserStructure() {
        // FLUX DE TRÉSORERIE LIÉS À L'ACTIVITÉ
        fluxActivite.put("FA", new PosteFluxTresorerie("FA", "Capacité d'autofinancement", 1));
        fluxActivite.put("FB", new PosteFluxTresorerie("FB", "Variation du besoin en fonds de roulement", 1));
        fluxActivite.put("FC", new PosteFluxTresorerie("FC", "Variation des stocks", 2));
        fluxActivite.put("FD", new PosteFluxTresorerie("FD", "Variation des créances", 2));
        fluxActivite.put("FE", new PosteFluxTresorerie("FE", "Variation des dettes", 2));
        fluxActivite.put("FF", new PosteFluxTresorerie("FF", "Autres variations", 2));
        
        // FLUX DE TRÉSORERIE LIÉS AUX INVESTISSEMENTS
        fluxInvestissement.put("FG", new PosteFluxTresorerie("FG", "Acquisitions d'immobilisations", 1));
        fluxInvestissement.put("FH", new PosteFluxTresorerie("FH", "Cessions d'immobilisations", 1));
        fluxInvestissement.put("FI", new PosteFluxTresorerie("FI", "Produits de cessions", 2));
        fluxInvestissement.put("FJ", new PosteFluxTresorerie("FJ", "Autres flux d'investissement", 1));
        
        // FLUX DE TRÉSORERIE LIÉS AU FINANCEMENT
        fluxFinancement.put("FK", new PosteFluxTresorerie("FK", "Augmentation de capital", 1));
        fluxFinancement.put("FL", new PosteFluxTresorerie("FL", "Emprunts", 1));
        fluxFinancement.put("FM", new PosteFluxTresorerie("FM", "Remboursements d'emprunts", 1));
        fluxFinancement.put("FN", new PosteFluxTresorerie("FN", "Dividendes versés", 1));
        fluxFinancement.put("FO", new PosteFluxTresorerie("FO", "Autres flux de financement", 1));
    }
    
    public void affecterValeur(String codePoste, BigDecimal valeur) {
        PosteFluxTresorerie poste = fluxActivite.get(codePoste);
        if (poste != null) {
            poste.setValeur(valeur);
            return;
        }
        
        poste = fluxInvestissement.get(codePoste);
        if (poste != null) {
            poste.setValeur(valeur);
            return;
        }
        
        poste = fluxFinancement.get(codePoste);
        if (poste != null) {
            poste.setValeur(valeur);
        }
    }
    
    public void calculerTotaux() {
        // Calcul des totaux par section
        totalFluxActivite = fluxActivite.values().stream()
            .map(PosteFluxTresorerie::getValeur)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalFluxInvestissement = fluxInvestissement.values().stream()
            .map(PosteFluxTresorerie::getValeur)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalFluxFinancement = fluxFinancement.values().stream()
            .map(PosteFluxTresorerie::getValeur)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Variation de trésorerie
        variationTresorerie = totalFluxActivite.add(totalFluxInvestissement).add(totalFluxFinancement);
        
        // Trésorerie finale = Trésorerie début + Variation
        tresorerieFin = tresorerieDebut.add(variationTresorerie);
    }
    
    public void setTresorerieDebut(BigDecimal tresorerieDebut) {
        this.tresorerieDebut = tresorerieDebut != null ? tresorerieDebut : BigDecimal.ZERO;
    }
    
    // Getters
    public Map<String, PosteFluxTresorerie> getFluxActivite() { return fluxActivite; }
    public Map<String, PosteFluxTresorerie> getFluxInvestissement() { return fluxInvestissement; }
    public Map<String, PosteFluxTresorerie> getFluxFinancement() { return fluxFinancement; }
    
    public BigDecimal getTotalFluxActivite() { return totalFluxActivite; }
    public BigDecimal getTotalFluxInvestissement() { return totalFluxInvestissement; }
    public BigDecimal getTotalFluxFinancement() { return totalFluxFinancement; }
    public BigDecimal getVariationTresorerie() { return variationTresorerie; }
    public BigDecimal getTresorerieDebut() { return tresorerieDebut; }
    public BigDecimal getTresorerieFin() { return tresorerieFin; }
}






