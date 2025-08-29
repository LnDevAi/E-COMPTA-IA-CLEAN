package com.ecomptaia.model.ohada;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Modèle de Compte de Résultat conforme aux standards OHADA/SYSCOHADA
 */
public class CompteResultatSYSCOHADA {
    
    // PRODUITS
    private Map<String, PosteCompteResultat> produits = new HashMap<>();
    
    // CHARGES
    private Map<String, PosteCompteResultat> charges = new HashMap<>();
    
    // SOLDES INTERMÉDIAIRES DE GESTION (SIG)
    private BigDecimal margeBrute = BigDecimal.ZERO;
    private BigDecimal valeurAjoutee = BigDecimal.ZERO;
    private BigDecimal excedentBrutExploitation = BigDecimal.ZERO;
    private BigDecimal resultatExploitation = BigDecimal.ZERO;
    private BigDecimal resultatFinancier = BigDecimal.ZERO;
    private BigDecimal resultatExceptionnel = BigDecimal.ZERO;
    private BigDecimal resultatAvantImpot = BigDecimal.ZERO;
    private BigDecimal resultatNet = BigDecimal.ZERO;
    
    // TOTAUX
    private BigDecimal totalProduits = BigDecimal.ZERO;
    private BigDecimal totalCharges = BigDecimal.ZERO;
    
    public CompteResultatSYSCOHADA() {
        initialiserStructure();
    }
    
    private void initialiserStructure() {
        // PRODUITS
        produits.put("TA", new PosteCompteResultat("TA", "VENTES DE MARCHANDISES", 1));
        produits.put("TB", new PosteCompteResultat("TB", "VENTES DE PRODUITS FABRIQUÉS", 1));
        produits.put("TC", new PosteCompteResultat("TC", "VENTES DE SERVICES", 1));
        produits.put("TD", new PosteCompteResultat("TD", "PRODUITS ACCESSOIRES", 1));
        produits.put("TF", new PosteCompteResultat("TF", "PRODUCTION IMMOBILISÉE", 1));
        produits.put("TH", new PosteCompteResultat("TH", "AUTRES PRODUITS", 1));
        produits.put("TI", new PosteCompteResultat("TI", "TRANSFERTS DE CHARGES", 1));
        produits.put("TK", new PosteCompteResultat("TK", "REVENUS FINANCIERS", 1));
        produits.put("TN", new PosteCompteResultat("TN", "PRODUITS DE CESSIONS", 1));
        produits.put("TO", new PosteCompteResultat("TO", "PRODUITS HAO", 1));
        
        // CHARGES
        charges.put("RA", new PosteCompteResultat("RA", "ACHATS DE MARCHANDISES", 1));
        charges.put("RB", new PosteCompteResultat("RB", "VARIATION DES STOCKS DE MARCHANDISES", 1));
        charges.put("RC", new PosteCompteResultat("RC", "ACHATS DE MATIÈRES PREMIÈRES", 1));
        charges.put("RD", new PosteCompteResultat("RD", "VARIATION DES STOCKS DE MATIÈRES", 1));
        charges.put("RE", new PosteCompteResultat("RE", "AUTRES ACHATS", 1));
        charges.put("RF", new PosteCompteResultat("RF", "VARIATION DES AUTRES STOCKS", 1));
        charges.put("RG", new PosteCompteResultat("RG", "TRANSPORTS", 1));
        charges.put("RH", new PosteCompteResultat("RH", "SERVICES EXTÉRIEURS", 1));
        charges.put("RI", new PosteCompteResultat("RI", "IMPÔTS ET TAXES", 1));
        charges.put("RJ", new PosteCompteResultat("RJ", "AUTRES CHARGES", 1));
        charges.put("RK", new PosteCompteResultat("RK", "CHARGES DE PERSONNEL", 1));
        charges.put("RL", new PosteCompteResultat("RL", "DOTATIONS D'AMORTISSEMENTS", 1));
        charges.put("RM", new PosteCompteResultat("RM", "FRAIS FINANCIERS", 1));
        charges.put("RO", new PosteCompteResultat("RO", "VALEURS COMPTABLES DE CESSIONS", 1));
        charges.put("RP", new PosteCompteResultat("RP", "CHARGES HAO", 1));
        charges.put("RS", new PosteCompteResultat("RS", "IMPÔTS SUR LE RÉSULTAT", 1));
    }
    
    public void affecterValeur(String codePoste, BigDecimal valeur) {
        PosteCompteResultat poste = produits.get(codePoste);
        if (poste != null) {
            poste.setValeur(valeur);
            return;
        }
        
        poste = charges.get(codePoste);
        if (poste != null) {
            poste.setValeur(valeur);
        }
    }
    
    public void calculerSoldesIntermediaires() {
        // Calcul des totaux
        totalProduits = produits.values().stream()
            .map(PosteCompteResultat::getValeur)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalCharges = charges.values().stream()
            .map(PosteCompteResultat::getValeur)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calcul des SIG selon SYSCOHADA
        
        // 1. Marge brute = Ventes - Achats de marchandises - Variation stocks marchandises
        BigDecimal ventes = getValeurProduit("TA");
        BigDecimal achatsMarchandises = getValeurCharge("RA");
        BigDecimal variationStocksMarchandises = getValeurCharge("RB");
        margeBrute = ventes.subtract(achatsMarchandises).subtract(variationStocksMarchandises);
        
        // 2. Valeur ajoutée = Marge brute + Production - Consommations
        BigDecimal production = getValeurProduit("TB").add(getValeurProduit("TC"))
            .add(getValeurProduit("TD")).add(getValeurProduit("TF"));
        BigDecimal consommations = getValeurCharge("RC").add(getValeurCharge("RD"))
            .add(getValeurCharge("RE")).add(getValeurCharge("RF"))
            .add(getValeurCharge("RG")).add(getValeurCharge("RH"))
            .add(getValeurCharge("RI")).add(getValeurCharge("RJ"));
        valeurAjoutee = margeBrute.add(production).subtract(consommations);
        
        // 3. Excédent brut d'exploitation = Valeur ajoutée - Charges de personnel
        BigDecimal chargesPersonnel = getValeurCharge("RK");
        excedentBrutExploitation = valeurAjoutee.subtract(chargesPersonnel);
        
        // 4. Résultat d'exploitation = EBE - Dotations d'amortissements
        BigDecimal dotationsAmort = getValeurCharge("RL");
        resultatExploitation = excedentBrutExploitation.subtract(dotationsAmort);
        
        // 5. Résultat financier = Revenus financiers - Frais financiers
        BigDecimal revenusFinanciers = getValeurProduit("TK");
        BigDecimal fraisFinanciers = getValeurCharge("RM");
        resultatFinancier = revenusFinanciers.subtract(fraisFinanciers);
        
        // 6. Résultat exceptionnel = Produits exceptionnels - Charges exceptionnelles
        BigDecimal produitsExceptionnels = getValeurProduit("TN");
        BigDecimal chargesExceptionnelles = getValeurCharge("RO");
        resultatExceptionnel = produitsExceptionnels.subtract(chargesExceptionnelles);
        
        // 7. Résultat avant impôt = Résultat exploitation + Résultat financier + Résultat exceptionnel
        resultatAvantImpot = resultatExploitation.add(resultatFinancier).add(resultatExceptionnel);
        
        // 8. Résultat net = Résultat avant impôt - Impôts sur le résultat
        BigDecimal impotsResultat = getValeurCharge("RS");
        resultatNet = resultatAvantImpot.subtract(impotsResultat);
    }
    
    private BigDecimal getValeurProduit(String code) {
        PosteCompteResultat poste = produits.get(code);
        return poste != null ? poste.getValeur() : BigDecimal.ZERO;
    }
    
    private BigDecimal getValeurCharge(String code) {
        PosteCompteResultat poste = charges.get(code);
        return poste != null ? poste.getValeur() : BigDecimal.ZERO;
    }
    
    // Getters
    public Map<String, PosteCompteResultat> getProduits() { return produits; }
    public Map<String, PosteCompteResultat> getCharges() { return charges; }
    
    public BigDecimal getMargeBrute() { return margeBrute; }
    public BigDecimal getValeurAjoutee() { return valeurAjoutee; }
    public BigDecimal getExcedentBrutExploitation() { return excedentBrutExploitation; }
    public BigDecimal getResultatExploitation() { return resultatExploitation; }
    public BigDecimal getResultatFinancier() { return resultatFinancier; }
    public BigDecimal getResultatExceptionnel() { return resultatExceptionnel; }
    public BigDecimal getResultatAvantImpot() { return resultatAvantImpot; }
    public BigDecimal getResultatNet() { return resultatNet; }
    
    public BigDecimal getTotalProduits() { return totalProduits; }
    public BigDecimal getTotalCharges() { return totalCharges; }
}





