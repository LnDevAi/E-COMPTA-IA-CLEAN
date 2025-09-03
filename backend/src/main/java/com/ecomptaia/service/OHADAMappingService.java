package com.ecomptaia.service;

import com.ecomptaia.model.ohada.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service de mapping entre les comptes comptables et les postes OHADA/SYSCOHADA
 */
@Service
public class OHADAMappingService {
    
    // Mapping des comptes vers les postes du Bilan SYSCOHADA
    private final Map<String, String[]> mappingBilan = new HashMap<>();
    
    // Mapping des comptes vers les postes du Compte de Résultat SYSCOHADA
    private final Map<String, String[]> mappingCompteResultat = new HashMap<>();
    
    public OHADAMappingService() {
        initialiserMappingBilan();
        initialiserMappingCompteResultat();
    }
    
    private void initialiserMappingBilan() {
        // ACTIF IMMOBILISÉ
        mappingBilan.put("AD", new String[]{"21"}); // Immobilisations incorporelles
        mappingBilan.put("AF", new String[]{"211", "212", "213"}); // Brevets, licences, logiciels
        mappingBilan.put("AG", new String[]{"214", "215"}); // Fonds commercial
        mappingBilan.put("AI", new String[]{"22", "23", "24"}); // Immobilisations corporelles
        mappingBilan.put("AJ", new String[]{"221"}); // Terrains
        mappingBilan.put("AK", new String[]{"222"}); // Bâtiments
        mappingBilan.put("AL", new String[]{"223", "224"}); // Aménagements
        mappingBilan.put("AM", new String[]{"231", "232", "233", "234"}); // Matériel
        mappingBilan.put("AN", new String[]{"245"}); // Matériel de transport
        mappingBilan.put("AP", new String[]{"238"}); // Avances sur immobilisations
        mappingBilan.put("AQ", new String[]{"26", "27"}); // Immobilisations financières
        
        // ACTIF CIRCULANT
        mappingBilan.put("BA", new String[]{"85"}); // Actif circulant HAO
        mappingBilan.put("BB", new String[]{"31", "32", "33", "34", "35", "36", "37", "38"}); // Stocks
        mappingBilan.put("BG", new String[]{"40", "41", "42", "43", "44", "45", "46", "47", "48"}); // Créances
        mappingBilan.put("BI", new String[]{"411", "416", "418"}); // Clients
        mappingBilan.put("BJ", new String[]{"425", "427", "428", "444", "445", "446", "447", "448"}); // Autres créances
        mappingBilan.put("BS", new String[]{"51", "52", "53", "54", "57", "58"}); // Trésorerie actif
        mappingBilan.put("BU", new String[]{"476"}); // Écart de conversion actif
        
        // PASSIF - CAPITAUX PROPRES
        mappingBilan.put("CA", new String[]{"101", "104", "105"}); // Capital
        mappingBilan.put("CB", new String[]{"109"}); // Capital non appelé
        mappingBilan.put("CD", new String[]{"105"}); // Écarts de réévaluation
        mappingBilan.put("CE", new String[]{"111", "112", "113"}); // Réserves
        mappingBilan.put("CF", new String[]{"121"}); // Report à nouveau
        mappingBilan.put("CG", new String[]{"130"}); // Résultat
        mappingBilan.put("CH", new String[]{"141", "142"}); // Subventions
        mappingBilan.put("CJ", new String[]{"151", "152", "153"}); // Provisions réglementées
        
        // PASSIF - DETTES FINANCIÈRES
        mappingBilan.put("DA", new String[]{"16", "17"}); // Emprunts et dettes financières
        mappingBilan.put("DC", new String[]{"191", "192", "193", "194", "195", "196", "197", "198"}); // Provisions
        
        // PASSIF CIRCULANT
        mappingBilan.put("DH", new String[]{"86"}); // Dettes circulantes HAO
        mappingBilan.put("DI", new String[]{"419"}); // Clients avances reçues
        mappingBilan.put("DJ", new String[]{"401", "403", "408"}); // Fournisseurs
        mappingBilan.put("DK", new String[]{"421", "422", "423", "424", "425", "426", "427", "428"}); // Dettes fiscales et sociales
        mappingBilan.put("DM", new String[]{"444", "445", "446", "447", "448"}); // Autres dettes
        mappingBilan.put("DN", new String[]{"499"}); // Provisions court terme
        mappingBilan.put("DR", new String[]{"565", "566"}); // Trésorerie passif
        mappingBilan.put("DV", new String[]{"477"}); // Écart de conversion passif
    }
    
    private void initialiserMappingCompteResultat() {
        // PRODUITS
        mappingCompteResultat.put("TA", new String[]{"701"}); // Ventes marchandises
        mappingCompteResultat.put("TB", new String[]{"702", "703"}); // Ventes produits fabriqués
        mappingCompteResultat.put("TC", new String[]{"704", "705", "706"}); // Services vendus
        mappingCompteResultat.put("TD", new String[]{"707", "708"}); // Produits accessoires
        mappingCompteResultat.put("TF", new String[]{"721", "722"}); // Production immobilisée
        mappingCompteResultat.put("TH", new String[]{"754", "758"}); // Autres produits
        mappingCompteResultat.put("TI", new String[]{"781", "791"}); // Transferts de charges
        mappingCompteResultat.put("TK", new String[]{"771", "772", "773", "774", "776", "777"}); // Revenus financiers
        mappingCompteResultat.put("TN", new String[]{"775"}); // Produits cessions
        mappingCompteResultat.put("TO", new String[]{"84"}); // Produits HAO
        
        // CHARGES
        mappingCompteResultat.put("RA", new String[]{"601"}); // Achats marchandises
        mappingCompteResultat.put("RB", new String[]{"6031"}); // Variation stocks marchandises
        mappingCompteResultat.put("RC", new String[]{"602"}); // Achats matières premières
        mappingCompteResultat.put("RD", new String[]{"6032"}); // Variation stocks matières
        mappingCompteResultat.put("RE", new String[]{"604", "605", "606", "607", "608"}); // Autres achats
        mappingCompteResultat.put("RF", new String[]{"6033", "6034"}); // Variation autres stocks
        mappingCompteResultat.put("RG", new String[]{"61"}); // Transports
        mappingCompteResultat.put("RH", new String[]{"62"}); // Services extérieurs
        mappingCompteResultat.put("RI", new String[]{"63"}); // Impôts et taxes
        mappingCompteResultat.put("RJ", new String[]{"65"}); // Autres charges
        mappingCompteResultat.put("RK", new String[]{"66"}); // Charges personnel
        mappingCompteResultat.put("RL", new String[]{"681", "691"}); // Dotations amortissements
        mappingCompteResultat.put("RM", new String[]{"671", "672", "673", "674", "675", "676", "677", "678"}); // Frais financiers
        mappingCompteResultat.put("RO", new String[]{"675"}); // Valeurs comptables cessions
        mappingCompteResultat.put("RP", new String[]{"87"}); // Charges HAO
        mappingCompteResultat.put("RS", new String[]{"891", "892"}); // Impôts sur résultat
    }
    
    /**
     * Générer le Bilan OHADA à partir des données comptables
     */
    public BilanSYSCOHADA genererBilanOHADA(Map<String, BigDecimal> soldesComptes) {
        BilanSYSCOHADA bilan = new BilanSYSCOHADA();
        
        // Affecter les valeurs selon le mapping
        for (Map.Entry<String, String[]> entry : mappingBilan.entrySet()) {
            String codePoste = entry.getKey();
            String[] patternsComptes = entry.getValue();
            
            BigDecimal valeur = calculerValeurPoste(soldesComptes, patternsComptes);
            bilan.affecterValeur(codePoste, valeur, BigDecimal.ZERO); // Valeur exercice précédent à 0 pour l'instant
        }
        
        bilan.calculerTotaux();
        return bilan;
    }
    
    /**
     * Générer le Compte de Résultat OHADA à partir des données comptables
     */
    public CompteResultatSYSCOHADA genererCompteResultatOHADA(Map<String, BigDecimal> soldesComptes) {
        CompteResultatSYSCOHADA compteResultat = new CompteResultatSYSCOHADA();
        
        // Affecter les valeurs selon le mapping
        for (Map.Entry<String, String[]> entry : mappingCompteResultat.entrySet()) {
            String codePoste = entry.getKey();
            String[] patternsComptes = entry.getValue();
            
            BigDecimal valeur = calculerValeurPoste(soldesComptes, patternsComptes);
            compteResultat.affecterValeur(codePoste, valeur);
        }
        
        compteResultat.calculerSoldesIntermediaires();
        return compteResultat;
    }
    
    /**
     * Calculer la valeur d'un poste en agrégeant les comptes correspondants
     */
    private BigDecimal calculerValeurPoste(Map<String, BigDecimal> soldesComptes, String[] patterns) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (String pattern : patterns) {
            for (Map.Entry<String, BigDecimal> entry : soldesComptes.entrySet()) {
                String numeroCompte = entry.getKey();
                BigDecimal solde = entry.getValue();
                
                if (correspondAuPattern(numeroCompte, pattern)) {
                    total = total.add(solde);
                }
            }
        }
        
        return total;
    }
    
    /**
     * Vérifier si un numéro de compte correspond à un pattern
     */
    private boolean correspondAuPattern(String numeroCompte, String pattern) {
        // Convertir le pattern en regex
        String regex = "^" + pattern.replace("%", ".*");
        return Pattern.matches(regex, numeroCompte);
    }
    
    /**
     * Générer le Tableau des Flux de Trésorerie OHADA
     */
    public TableauFluxSYSCOHADA genererTableauFluxOHADA(
            CompteResultatSYSCOHADA compteResultat,
            BilanSYSCOHADA bilanCourant,
            BilanSYSCOHADA bilanPrecedent) {
        
        TableauFluxSYSCOHADA tableauFlux = new TableauFluxSYSCOHADA();
        
        // Capacité d'autofinancement (CAFG)
        BigDecimal resultatNet = compteResultat.getResultatNet();
        BigDecimal dotationsAmort = compteResultat.getCharges().get("RL").getValeur();
        BigDecimal cafg = resultatNet.add(dotationsAmort);
        tableauFlux.affecterValeur("FA", cafg);
        
        // Variations des postes de l'actif et du passif circulant
        BigDecimal variationStocks = calculerVariation(bilanCourant, bilanPrecedent, "BB");
        BigDecimal variationCreances = calculerVariation(bilanCourant, bilanPrecedent, "BG");
        BigDecimal variationDettes = calculerVariation(bilanCourant, bilanPrecedent, "DJ");
        
        tableauFlux.affecterValeur("FC", variationStocks.negate());
        tableauFlux.affecterValeur("FD", variationCreances.negate());
        tableauFlux.affecterValeur("FE", variationDettes);
        
        // Flux d'investissement
        BigDecimal acquisitionsImmob = calculerVariation(bilanCourant, bilanPrecedent, "AI");
        BigDecimal cessionsImmob = compteResultat.getProduits().get("TN").getValeur();
        
        tableauFlux.affecterValeur("FG", acquisitionsImmob.negate());
        tableauFlux.affecterValeur("FI", cessionsImmob);
        
        tableauFlux.calculerTotaux();
        return tableauFlux;
    }
    
    /**
     * Calculer la variation d'un poste entre deux bilans
     */
    private BigDecimal calculerVariation(BilanSYSCOHADA bilanCourant, BilanSYSCOHADA bilanPrecedent, String codePoste) {
        // Cette méthode simplifiée - en réalité il faudrait accéder aux postes spécifiques
        return BigDecimal.ZERO;
    }
    
    /**
     * Générer les Annexes OHADA
     */
    public AnnexesSYSCOHADA genererAnnexesOHADA(Map<String, Object> donnees) {
        AnnexesSYSCOHADA annexes = new AnnexesSYSCOHADA();
        
        // Générer les notes principales
        annexes.genererNote3A(donnees);
        annexes.genererNote3C(donnees);
        annexes.genererNote6(donnees);
        annexes.genererNote7(donnees);
        annexes.genererNote27A(donnees);
        
        return annexes;
    }
}





