package com.ecomptaia.model.ohada;

import java.math.BigDecimal;

/**
 * Représente un poste du tableau des flux de trésorerie OHADA/SYSCOHADA
 */
public class PosteFluxTresorerie {
    private String code;
    private String libelle;
    private int niveau;
    private BigDecimal valeur = BigDecimal.ZERO;
    
    public PosteFluxTresorerie(String code, String libelle, int niveau) {
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
    
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { 
        this.valeur = valeur != null ? valeur : BigDecimal.ZERO; 
    }
}








