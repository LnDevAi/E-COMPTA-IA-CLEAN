package com.ecomptaia.sycebnl.service;

import com.ecomptaia.entity.LigneEcriture;
import com.ecomptaia.repository.LigneEcritureRepository;
import com.ecomptaia.sycebnl.entity.PlanComptableSycebnl;
import com.ecomptaia.sycebnl.repository.PlanComptableSycebnlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service de comptabilité SYCEBNL pour le calcul des soldes par pattern
 * Permet de calculer les soldes des comptes selon des patterns (ex: "20%", "3%")
 */
@Service
@Transactional(readOnly = true)
public class ComptabiliteSycebnlService {
    
    private final LigneEcritureRepository ligneEcritureRepository;
    private final PlanComptableSycebnlRepository planComptableRepository;
    
    @Autowired
    public ComptabiliteSycebnlService(LigneEcritureRepository ligneEcritureRepository,
                                    PlanComptableSycebnlRepository planComptableRepository) {
        this.ligneEcritureRepository = ligneEcritureRepository;
        this.planComptableRepository = planComptableRepository;
    }
    
    /**
     * Calcule le solde d'un compte spécifique à une date donnée
     * 
     * @param numeroCompte Numéro du compte
     * @param dateArrete Date d'arrêté
     * @return Solde du compte
     */
    public BigDecimal calculerSoldeCompte(String numeroCompte, LocalDate dateArrete) {
        try {
            // Rechercher toutes les lignes d'écriture pour ce compte
            List<LigneEcriture> lignes = ligneEcritureRepository.findByCompteNumero(numeroCompte);
            
            BigDecimal solde = BigDecimal.ZERO;
            for (LigneEcriture ligne : lignes) {
                // Vérifier que l'écriture est antérieure ou égale à la date d'arrêté
                if (ligne.getEcriture() != null && 
                    ligne.getEcriture().getDateEcriture() != null &&
                    !ligne.getEcriture().getDateEcriture().isAfter(dateArrete)) {
                    
                    if (ligne.getDebit() != null) {
                        solde = solde.add(ligne.getDebit());
                    }
                    if (ligne.getCredit() != null) {
                        solde = solde.subtract(ligne.getCredit());
                    }
                }
            }
            return solde;
        } catch (Exception e) {
            // En cas d'erreur, retourner zéro plutôt que de faire planter l'application
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcule le solde de tous les comptes correspondant à un pattern
     * 
     * @param pattern Pattern de recherche (ex: "20%", "3%", "411%")
     * @param dateArrete Date d'arrêté
     * @return Solde total des comptes correspondant au pattern
     */
    public BigDecimal calculerSoldeParPattern(String pattern, LocalDate dateArrete) {
        try {
            // Convertir le pattern en pattern SQL LIKE
            String patternLike = convertirPatternEnLike(pattern);
            
            // Rechercher tous les comptes du plan comptable SYCEBNL qui correspondent au pattern
            List<PlanComptableSycebnl> comptes = planComptableRepository.findByNumeroCompteLikeAndActifTrue(patternLike);
            
            BigDecimal soldeTotal = BigDecimal.ZERO;
            for (PlanComptableSycebnl compte : comptes) {
                BigDecimal soldeCompte = calculerSoldeCompte(compte.getNumeroCompte(), dateArrete);
                soldeTotal = soldeTotal.add(soldeCompte);
            }
            return soldeTotal;
        } catch (Exception e) {
            // En cas d'erreur, retourner zéro
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcule le solde de plusieurs patterns
     * 
     * @param patterns Liste des patterns
     * @param dateArrete Date d'arrêté
     * @return Solde total de tous les patterns
     */
    public BigDecimal calculerSoldeParPatterns(List<String> patterns, LocalDate dateArrete) {
        BigDecimal soldeTotal = BigDecimal.ZERO;
        
        for (String pattern : patterns) {
            BigDecimal soldePattern = calculerSoldeParPattern(pattern, dateArrete);
            soldeTotal = soldeTotal.add(soldePattern);
        }
        
        return soldeTotal;
    }
    
    /**
     * Convertit un pattern de compte en pattern SQL LIKE
     * 
     * @param pattern Pattern (ex: "20%", "3%", "411%")
     * @return Pattern SQL LIKE correspondant
     */
    private String convertirPatternEnLike(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return "%";
        }
        
        // Remplacer % par % pour SQL LIKE
        return pattern.replace("%", "%");
    }
    
    /**
     * Convertit un pattern de compte en regex
     * 
     * @param pattern Pattern (ex: "20%", "3%", "411%")
     * @return Regex correspondante
     */
    private String convertirPatternEnRegex(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return ".*";
        }
        
        // Remplacer % par .* pour matcher n'importe quels caractères
        String regex = pattern.replace("%", ".*");
        
        // Ajouter ^ et $ pour matcher exactement
        return "^" + regex + "$";
    }
    
    /**
     * Vérifie si un numéro de compte correspond à un pattern
     * 
     * @param numeroCompte Numéro du compte
     * @param pattern Pattern à vérifier
     * @return true si le compte correspond au pattern
     */
    public boolean compteCorrespondAuPattern(String numeroCompte, String pattern) {
        if (numeroCompte == null || pattern == null) {
            return false;
        }
        
        String regex = convertirPatternEnRegex(pattern);
        return Pattern.matches(regex, numeroCompte);
    }
    
    /**
     * Calcule le solde d'un compte avec gestion des signes
     * 
     * @param numeroCompte Numéro du compte
     * @param dateArrete Date d'arrêté
     * @param sensNormal Sens normal du compte (DEBITEUR ou CREDITEUR)
     * @return Solde avec le bon signe
     */
    public BigDecimal calculerSoldeCompteAvecSens(String numeroCompte, LocalDate dateArrete, String sensNormal) {
        BigDecimal solde = calculerSoldeCompte(numeroCompte, dateArrete);
        
        // Si le compte est créditeur et a un solde débiteur, on inverse le signe
        if ("CREDITEUR".equals(sensNormal) && solde.compareTo(BigDecimal.ZERO) < 0) {
            return solde.abs();
        }
        
        return solde;
    }
    
    /**
     * Calcule le solde d'un poste en agrégeant plusieurs comptes
     * 
     * @param numerosComptes Liste des numéros de comptes
     * @param dateArrete Date d'arrêté
     * @param sensNormal Sens normal du poste
     * @return Solde total du poste
     */
    public BigDecimal calculerSoldePoste(List<String> numerosComptes, LocalDate dateArrete, String sensNormal) {
        BigDecimal soldeTotal = BigDecimal.ZERO;
        
        for (String numeroCompte : numerosComptes) {
            BigDecimal soldeCompte = calculerSoldeCompteAvecSens(numeroCompte, dateArrete, sensNormal);
            soldeTotal = soldeTotal.add(soldeCompte);
        }
        
        return soldeTotal;
    }
    
    /**
     * Calcule le solde d'un poste par patterns
     * 
     * @param patterns Liste des patterns de comptes
     * @param dateArrete Date d'arrêté
     * @param sensNormal Sens normal du poste
     * @return Solde total du poste
     */
    public BigDecimal calculerSoldePosteParPatterns(List<String> patterns, LocalDate dateArrete, String sensNormal) {
        BigDecimal soldeTotal = BigDecimal.ZERO;
        
        for (String pattern : patterns) {
            BigDecimal soldePattern = calculerSoldeParPattern(pattern, dateArrete);
            
            // Ajuster le signe selon le sens normal
            if ("CREDITEUR".equals(sensNormal) && soldePattern.compareTo(BigDecimal.ZERO) < 0) {
                soldePattern = soldePattern.abs();
            }
            
            soldeTotal = soldeTotal.add(soldePattern);
        }
        
        return soldeTotal;
    }
    
    /**
     * Vérifie l'équilibre d'un bilan
     * 
     * @param totalActif Total de l'actif
     * @param totalPassif Total du passif
     * @return true si le bilan est équilibré
     */
    public boolean verifierEquilibreBilan(BigDecimal totalActif, BigDecimal totalPassif) {
        if (totalActif == null || totalPassif == null) {
            return false;
        }
        
        // Tolérance de 0.01 pour les arrondis
        BigDecimal difference = totalActif.subtract(totalPassif).abs();
        return difference.compareTo(BigDecimal.valueOf(0.01)) <= 0;
    }
    
    /**
     * Calcule le résultat net
     * 
     * @param totalProduits Total des produits
     * @param totalCharges Total des charges
     * @return Résultat net (Produits - Charges)
     */
    public BigDecimal calculerResultatNet(BigDecimal totalProduits, BigDecimal totalCharges) {
        if (totalProduits == null) totalProduits = BigDecimal.ZERO;
        if (totalCharges == null) totalCharges = BigDecimal.ZERO;
        
        return totalProduits.subtract(totalCharges);
    }
    
    /**
     * Formate un montant pour l'affichage
     * 
     * @param montant Montant à formater
     * @return Montant formaté
     */
    public String formaterMontant(BigDecimal montant) {
        if (montant == null) {
            return "0,00";
        }
        
        return String.format("%,.2f", montant);
    }
    
    /**
     * Formate un montant avec devise
     * 
     * @param montant Montant à formater
     * @param devise Devise (XOF, EUR, USD)
     * @return Montant formaté avec devise
     */
    public String formaterMontantAvecDevise(BigDecimal montant, String devise) {
        if (montant == null) {
            return "0,00 " + devise;
        }
        
        return String.format("%,.2f %s", montant, devise);
    }
    
    /**
     * Calcule le pourcentage d'un montant par rapport à un total
     * 
     * @param montant Montant partiel
     * @param total Montant total
     * @return Pourcentage (0-100)
     */
    public double calculerPourcentage(BigDecimal montant, BigDecimal total) {
        if (montant == null || total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        return montant.divide(total, 4, java.math.RoundingMode.HALF_UP)
                     .multiply(BigDecimal.valueOf(100))
                     .doubleValue();
    }
}
