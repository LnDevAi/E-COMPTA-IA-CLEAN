package com.ecomptaia.sycebnl.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité pour le mapping des comptes vers les postes des états financiers
 * Permet de définir comment les comptes comptables sont agrégés dans les états financiers
 */
@Entity
@Table(name = "mapping_comptes_postes")
public class MappingComptesPostes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pays_code", nullable = false, length = 2)
    private String paysCode;
    
    @Column(name = "standard_comptable", nullable = false, length = 20)
    private String standardComptable;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_systeme", nullable = false, length = 10)
    private TypeSysteme typeSysteme;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_etat", nullable = false, length = 50)
    private TypeEtat typeEtat;
    
    @Column(name = "poste_code", nullable = false, length = 10)
    private String posteCode;
    
    @Column(name = "poste_libelle", nullable = false, length = 255)
    private String posteLibelle;
    
    @ElementCollection
    @CollectionTable(name = "mapping_comptes_patterns", joinColumns = @JoinColumn(name = "mapping_id"))
    @Column(name = "compte_pattern")
    private List<String> comptesPattern;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "signe_normal", nullable = false, length = 10)
    private SensNormalCompte signeNormal;
    
    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage;
    
    @Column(name = "niveau", nullable = false)
    private Integer niveau = 1;
    
    @Column(name = "est_total")
    private Boolean estTotal = false;
    
    @Column(name = "actif")
    private Boolean actif = true;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
    
    // Constructeurs
    public MappingComptesPostes() {
    }
    
    public MappingComptesPostes(Long id, String paysCode, String standardComptable, TypeSysteme typeSysteme, 
                               TypeEtat typeEtat, String posteCode, String posteLibelle, 
                               List<String> comptesPattern, SensNormalCompte signeNormal, 
                               Integer ordreAffichage, Integer niveau, Boolean estTotal, 
                               Boolean actif, LocalDateTime dateCreation, LocalDateTime dateModification) {
        this.id = id;
        this.paysCode = paysCode;
        this.standardComptable = standardComptable;
        this.typeSysteme = typeSysteme;
        this.typeEtat = typeEtat;
        this.posteCode = posteCode;
        this.posteLibelle = posteLibelle;
        this.comptesPattern = comptesPattern;
        this.signeNormal = signeNormal;
        this.ordreAffichage = ordreAffichage;
        this.niveau = niveau;
        this.estTotal = estTotal;
        this.actif = actif;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPaysCode() { return paysCode; }
    public void setPaysCode(String paysCode) { this.paysCode = paysCode; }
    
    public String getStandardComptable() { return standardComptable; }
    public void setStandardComptable(String standardComptable) { this.standardComptable = standardComptable; }
    
    public TypeSysteme getTypeSysteme() { return typeSysteme; }
    public void setTypeSysteme(TypeSysteme typeSysteme) { this.typeSysteme = typeSysteme; }
    
    public TypeEtat getTypeEtat() { return typeEtat; }
    public void setTypeEtat(TypeEtat typeEtat) { this.typeEtat = typeEtat; }
    
    public String getPosteCode() { return posteCode; }
    public void setPosteCode(String posteCode) { this.posteCode = posteCode; }
    
    public String getPosteLibelle() { return posteLibelle; }
    public void setPosteLibelle(String posteLibelle) { this.posteLibelle = posteLibelle; }
    
    public List<String> getComptesPattern() { return comptesPattern; }
    public void setComptesPattern(List<String> comptesPattern) { this.comptesPattern = comptesPattern; }
    
    public SensNormalCompte getSigneNormal() { return signeNormal; }
    public void setSigneNormal(SensNormalCompte signeNormal) { this.signeNormal = signeNormal; }
    
    public Integer getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(Integer ordreAffichage) { this.ordreAffichage = ordreAffichage; }
    
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    
    public Boolean getEstTotal() { return estTotal; }
    public void setEstTotal(Boolean estTotal) { this.estTotal = estTotal; }
    
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    
    // Enums
    public enum TypeSysteme {
        NORMAL, MINIMAL
    }
    
    public enum TypeEtat {
        BILAN, COMPTE_RESULTAT, TABLEAU_FLUX, ANNEXES, RECETTES_DEPENSES, SITUATION_TRESORERIE
    }
    
    public enum SensNormalCompte {
        DEBIT, CREDIT
    }
}
