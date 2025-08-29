package com.ecomptaia.model.ohada;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Modèle de Bilan conforme aux standards OHADA/SYSCOHADA
 */
public class BilanSYSCOHADA {
    
    // ACTIF
    private Map<String, PosteBilan> actifImmobilise = new HashMap<>();
    private Map<String, PosteBilan> actifCirculant = new HashMap<>();
    private Map<String, PosteBilan> tresorerieActif = new HashMap<>();
    
    // PASSIF
    private Map<String, PosteBilan> capitauxPropres = new HashMap<>();
    private Map<String, PosteBilan> dettesFinancieres = new HashMap<>();
    private Map<String, PosteBilan> passifCirculant = new HashMap<>();
    private Map<String, PosteBilan> tresoreriePassif = new HashMap<>();
    
    // TOTAUX
    private BigDecimal totalActifImmobilise = BigDecimal.ZERO;
    private BigDecimal totalActifCirculant = BigDecimal.ZERO;
    private BigDecimal totalTresorerieActif = BigDecimal.ZERO;
    private BigDecimal totalActif = BigDecimal.ZERO;
    
    private BigDecimal totalCapitauxPropres = BigDecimal.ZERO;
    private BigDecimal totalDettesFinancieres = BigDecimal.ZERO;
    private BigDecimal totalPassifCirculant = BigDecimal.ZERO;
    private BigDecimal totalTresoreriePassif = BigDecimal.ZERO;
    private BigDecimal totalPassif = BigDecimal.ZERO;
    
    public BilanSYSCOHADA() {
        initialiserStructure();
    }
    
    private void initialiserStructure() {
        // ACTIF IMMOBILISÉ
        actifImmobilise.put("AD", new PosteBilan("AD", "IMMOBILISATIONS INCORPORELLES", 1));
        actifImmobilise.put("AF", new PosteBilan("AF", "Brevets, licences, logiciels et droits similaires", 2));
        actifImmobilise.put("AG", new PosteBilan("AG", "Fonds commercial et droit au bail", 2));
        actifImmobilise.put("AH", new PosteBilan("AH", "Autres immobilisations incorporelles", 2));
        actifImmobilise.put("AI", new PosteBilan("AI", "IMMOBILISATIONS CORPORELLES", 1));
        actifImmobilise.put("AJ", new PosteBilan("AJ", "Terrains", 2));
        actifImmobilise.put("AK", new PosteBilan("AK", "Bâtiments", 2));
        actifImmobilise.put("AL", new PosteBilan("AL", "Aménagements, agencements et installations", 2));
        actifImmobilise.put("AM", new PosteBilan("AM", "Matériel, mobilier et actifs biologiques", 2));
        actifImmobilise.put("AN", new PosteBilan("AN", "Matériel de transport", 2));
        actifImmobilise.put("AP", new PosteBilan("AP", "Avances et acomptes versés sur immobilisations", 2));
        actifImmobilise.put("AQ", new PosteBilan("AQ", "IMMOBILISATIONS FINANCIÈRES", 1));
        actifImmobilise.put("AR", new PosteBilan("AR", "Prêts", 2));
        actifImmobilise.put("AS", new PosteBilan("AS", "Autres créances financières", 2));
        actifImmobilise.put("AT", new PosteBilan("AT", "Titres de participation", 2));
        actifImmobilise.put("AU", new PosteBilan("AU", "Autres titres immobilisés", 2));
        
        // ACTIF CIRCULANT
        actifCirculant.put("BA", new PosteBilan("BA", "ACTIF CIRCULANT HAO", 1));
        actifCirculant.put("BB", new PosteBilan("BB", "STOCKS ET EN-COURS", 1));
        actifCirculant.put("BC", new PosteBilan("BC", "Matières premières et fournitures", 2));
        actifCirculant.put("BD", new PosteBilan("BD", "En-cours de production", 2));
        actifCirculant.put("BE", new PosteBilan("BE", "Produits finis", 2));
        actifCirculant.put("BF", new PosteBilan("BF", "Marchandises", 2));
        actifCirculant.put("BG", new PosteBilan("BG", "CRÉANCES", 1));
        actifCirculant.put("BH", new PosteBilan("BH", "Clients et comptes rattachés", 2));
        actifCirculant.put("BI", new PosteBilan("BI", "Clients", 3));
        actifCirculant.put("BJ", new PosteBilan("BJ", "Autres créances", 2));
        actifCirculant.put("BK", new PosteBilan("BK", "État et autres collectivités publiques", 2));
        actifCirculant.put("BL", new PosteBilan("BL", "Personnel", 2));
        actifCirculant.put("BM", new PosteBilan("BM", "Autres débiteurs", 2));
        actifCirculant.put("BN", new PosteBilan("BN", "Charges constatées d'avance", 2));
        actifCirculant.put("BO", new PosteBilan("BO", "Produits constatés d'avance", 2));
        actifCirculant.put("BP", new PosteBilan("BP", "Intérêts courus", 2));
        actifCirculant.put("BQ", new PosteBilan("BQ", "Autres créances", 2));
        actifCirculant.put("BR", new PosteBilan("BR", "Écart de conversion actif", 2));
        
        // TRÉSORERIE ACTIF
        tresorerieActif.put("BS", new PosteBilan("BS", "TRÉSORERIE ACTIF", 1));
        tresorerieActif.put("BT", new PosteBilan("BT", "Valeurs mobilières de placement", 2));
        tresorerieActif.put("BU", new PosteBilan("BU", "Banques, établissements financiers et assimilés", 2));
        tresorerieActif.put("BV", new PosteBilan("BV", "Caisse", 2));
        tresorerieActif.put("BW", new PosteBilan("BW", "Chèques et valeurs à encaisser", 2));
        
        // CAPITAUX PROPRES
        capitauxPropres.put("CA", new PosteBilan("CA", "CAPITAUX PROPRES", 1));
        capitauxPropres.put("CB", new PosteBilan("CB", "Capital social", 2));
        capitauxPropres.put("CC", new PosteBilan("CC", "Capital non appelé", 2));
        capitauxPropres.put("CD", new PosteBilan("CD", "Écarts de réévaluation", 2));
        capitauxPropres.put("CE", new PosteBilan("CE", "Réserves", 2));
        capitauxPropres.put("CF", new PosteBilan("CF", "Report à nouveau", 2));
        capitauxPropres.put("CG", new PosteBilan("CG", "Résultat de l'exercice", 2));
        capitauxPropres.put("CH", new PosteBilan("CH", "Subventions d'investissement", 2));
        capitauxPropres.put("CI", new PosteBilan("CI", "Provisions réglementées", 2));
        
        // DETTES FINANCIÈRES
        dettesFinancieres.put("DA", new PosteBilan("DA", "DETTES FINANCIÈRES", 1));
        dettesFinancieres.put("DB", new PosteBilan("DB", "Emprunts et dettes financières", 2));
        dettesFinancieres.put("DC", new PosteBilan("DC", "Provisions pour risques et charges", 2));
        dettesFinancieres.put("DD", new PosteBilan("DD", "Provisions pour risques", 2));
        dettesFinancieres.put("DE", new PosteBilan("DE", "Provisions pour charges", 2));
        
        // PASSIF CIRCULANT
        passifCirculant.put("DH", new PosteBilan("DH", "PASSIF CIRCULANT HAO", 1));
        passifCirculant.put("DI", new PosteBilan("DI", "DETTES", 1));
        passifCirculant.put("DJ", new PosteBilan("DJ", "Fournisseurs et comptes rattachés", 2));
        passifCirculant.put("DK", new PosteBilan("DK", "Fournisseurs", 3));
        passifCirculant.put("DL", new PosteBilan("DL", "Fournisseurs d'immobilisations", 3));
        passifCirculant.put("DM", new PosteBilan("DM", "État et autres collectivités publiques", 2));
        passifCirculant.put("DN", new PosteBilan("DN", "Personnel", 2));
        passifCirculant.put("DO", new PosteBilan("DO", "Autres créanciers", 2));
        passifCirculant.put("DP", new PosteBilan("DP", "Charges constatées d'avance", 2));
        passifCirculant.put("DQ", new PosteBilan("DQ", "Produits constatés d'avance", 2));
        passifCirculant.put("DR", new PosteBilan("DR", "Intérêts courus", 2));
        passifCirculant.put("DS", new PosteBilan("DS", "Autres dettes", 2));
        passifCirculant.put("DT", new PosteBilan("DT", "Écart de conversion passif", 2));
        
        // TRÉSORERIE PASSIF
        tresoreriePassif.put("DU", new PosteBilan("DU", "TRÉSORERIE PASSIF", 1));
        tresoreriePassif.put("DV", new PosteBilan("DV", "Banques, établissements financiers et assimilés", 2));
        tresoreriePassif.put("DW", new PosteBilan("DW", "Crédits d'escompte", 2));
        tresoreriePassif.put("DX", new PosteBilan("DX", "Crédits de trésorerie", 2));
    }
    
    public void affecterValeur(String codePoste, BigDecimal valeurExerciceCourant, BigDecimal valeurExercicePrecedent) {
        PosteBilan poste = trouverPoste(codePoste);
        if (poste != null) {
            poste.setValeurExerciceCourant(valeurExerciceCourant);
            poste.setValeurExercicePrecedent(valeurExercicePrecedent);
        }
    }
    
    private PosteBilan trouverPoste(String code) {
        PosteBilan poste = actifImmobilise.get(code);
        if (poste != null) return poste;
        
        poste = actifCirculant.get(code);
        if (poste != null) return poste;
        
        poste = tresorerieActif.get(code);
        if (poste != null) return poste;
        
        poste = capitauxPropres.get(code);
        if (poste != null) return poste;
        
        poste = dettesFinancieres.get(code);
        if (poste != null) return poste;
        
        poste = passifCirculant.get(code);
        if (poste != null) return poste;
        
        poste = tresoreriePassif.get(code);
        return poste;
    }
    
    public void calculerTotaux() {
        // Calcul des totaux ACTIF
        totalActifImmobilise = actifImmobilise.values().stream()
            .map(PosteBilan::getValeurExerciceCourant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalActifCirculant = actifCirculant.values().stream()
            .map(PosteBilan::getValeurExerciceCourant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalTresorerieActif = tresorerieActif.values().stream()
            .map(PosteBilan::getValeurExerciceCourant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalActif = totalActifImmobilise.add(totalActifCirculant).add(totalTresorerieActif);
        
        // Calcul des totaux PASSIF
        totalCapitauxPropres = capitauxPropres.values().stream()
            .map(PosteBilan::getValeurExerciceCourant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalDettesFinancieres = dettesFinancieres.values().stream()
            .map(PosteBilan::getValeurExerciceCourant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalPassifCirculant = passifCirculant.values().stream()
            .map(PosteBilan::getValeurExerciceCourant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalTresoreriePassif = tresoreriePassif.values().stream()
            .map(PosteBilan::getValeurExerciceCourant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalPassif = totalCapitauxPropres.add(totalDettesFinancieres)
            .add(totalPassifCirculant).add(totalTresoreriePassif);
    }
    
    // Getters
    public Map<String, PosteBilan> getActifImmobilise() { return actifImmobilise; }
    public Map<String, PosteBilan> getActifCirculant() { return actifCirculant; }
    public Map<String, PosteBilan> getTresorerieActif() { return tresorerieActif; }
    public Map<String, PosteBilan> getCapitauxPropres() { return capitauxPropres; }
    public Map<String, PosteBilan> getDettesFinancieres() { return dettesFinancieres; }
    public Map<String, PosteBilan> getPassifCirculant() { return passifCirculant; }
    public Map<String, PosteBilan> getTresoreriePassif() { return tresoreriePassif; }
    
    public BigDecimal getTotalActifImmobilise() { return totalActifImmobilise; }
    public BigDecimal getTotalActifCirculant() { return totalActifCirculant; }
    public BigDecimal getTotalTresorerieActif() { return totalTresorerieActif; }
    public BigDecimal getTotalActif() { return totalActif; }
    
    public BigDecimal getTotalCapitauxPropres() { return totalCapitauxPropres; }
    public BigDecimal getTotalDettesFinancieres() { return totalDettesFinancieres; }
    public BigDecimal getTotalPassifCirculant() { return totalPassifCirculant; }
    public BigDecimal getTotalTresoreriePassif() { return totalTresoreriePassif; }
    public BigDecimal getTotalPassif() { return totalPassif; }
}





