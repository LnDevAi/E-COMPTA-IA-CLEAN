package com.ecomptaia.model.ohada;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Représente un poste du bilan OHADA/SYSCOHADA
 */
public class PosteBilan {
    private String code;
    private String libelle;
    private int niveau;
    private BigDecimal valeurExerciceCourant = BigDecimal.ZERO;
    private BigDecimal valeurExercicePrecedent = BigDecimal.ZERO;
    
    public PosteBilan(String code, String libelle, int niveau) {
        this.code = code;
        this.libelle = libelle;
        this.niveau = niveau;
    }
    
    // Getters et Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    public int getNiveau() { return niveau; }
    public void setNiveau(int niveau) { this.niveau = niveau; }
    
    public BigDecimal getValeurExerciceCourant() { return valeurExerciceCourant; }
    public void setValeurExerciceCourant(BigDecimal valeurExerciceCourant) { 
        this.valeurExerciceCourant = valeurExerciceCourant != null ? valeurExerciceCourant : BigDecimal.ZERO; 
    }
    
    public BigDecimal getValeurExercicePrecedent() { return valeurExercicePrecedent; }
    public void setValeurExercicePrecedent(BigDecimal valeurExercicePrecedent) { 
        this.valeurExercicePrecedent = valeurExercicePrecedent != null ? valeurExercicePrecedent : BigDecimal.ZERO; 
    }
    
    public BigDecimal getVariation() {
        return valeurExerciceCourant.subtract(valeurExercicePrecedent);
    }
    
    public double getTauxVariation() {
        if (valeurExercicePrecedent.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getVariation().divide(valeurExercicePrecedent, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100")).doubleValue();
    }
}





