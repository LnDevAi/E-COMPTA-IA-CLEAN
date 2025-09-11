package com.ecomptaia.sycebnl.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entité pour le plan comptable SYCEBNL
 * Plan comptable spécialisé pour les entités à but non lucratif
 */
@Entity
@Table(name = "plan_comptable_sycebnl")
public class PlanComptableSycebnl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_compte", nullable = false, unique = true, length = 10)
    private String numeroCompte;
    
    @Column(name = "intitule_compte", nullable = false, length = 255)
    private String intituleCompte;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "classe_compte", nullable = false, length = 20)
    private ClasseCompte classeCompte;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_compte", nullable = false, length = 20)
    private TypeCompte typeCompte;
    
    @Column(name = "niveau", nullable = false)
    private Integer niveau = 1;
    
    @Column(name = "compte_parent", length = 10)
    private String compteParent;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sens_normal", nullable = false, length = 10)
    private SensNormalCompte sensNormal;
    
    @Column(name = "utilise_systeme_normal")
    private Boolean utiliseSystemeNormal = true;
    
    @Column(name = "utilise_smt")
    private Boolean utiliseSMT = true;
    
    @Column(name = "obligatoire_ong")
    private Boolean obligatoireONG = false;
    
    @Column(name = "description_utilisation", columnDefinition = "TEXT")
    private String descriptionUtilisation;
    
    @Column(name = "actif")
    private Boolean actif = true;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
    
    // Constructeurs
    public PlanComptableSycebnl() {
    }
    
    public PlanComptableSycebnl(Long id, String numeroCompte, String intituleCompte, 
                               ClasseCompte classeCompte, TypeCompte typeCompte, 
                               Integer niveau, String compteParent, SensNormalCompte sensNormal, 
                               Boolean utiliseSystemeNormal, Boolean utiliseSMT, 
                               Boolean obligatoireONG, String descriptionUtilisation, 
                               Boolean actif, LocalDateTime dateCreation) {
        this.id = id;
        this.numeroCompte = numeroCompte;
        this.intituleCompte = intituleCompte;
        this.classeCompte = classeCompte;
        this.typeCompte = typeCompte;
        this.niveau = niveau;
        this.compteParent = compteParent;
        this.sensNormal = sensNormal;
        this.utiliseSystemeNormal = utiliseSystemeNormal;
        this.utiliseSMT = utiliseSMT;
        this.obligatoireONG = obligatoireONG;
        this.descriptionUtilisation = descriptionUtilisation;
        this.actif = actif;
        this.dateCreation = dateCreation;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }
    
    public String getIntituleCompte() { return intituleCompte; }
    public void setIntituleCompte(String intituleCompte) { this.intituleCompte = intituleCompte; }
    
    public ClasseCompte getClasseCompte() { return classeCompte; }
    public void setClasseCompte(ClasseCompte classeCompte) { this.classeCompte = classeCompte; }
    
    public TypeCompte getTypeCompte() { return typeCompte; }
    public void setTypeCompte(TypeCompte typeCompte) { this.typeCompte = typeCompte; }
    
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    
    public String getCompteParent() { return compteParent; }
    public void setCompteParent(String compteParent) { this.compteParent = compteParent; }
    
    public SensNormalCompte getSensNormal() { return sensNormal; }
    public void setSensNormal(SensNormalCompte sensNormal) { this.sensNormal = sensNormal; }
    
    public Boolean getUtiliseSystemeNormal() { return utiliseSystemeNormal; }
    public void setUtiliseSystemeNormal(Boolean utiliseSystemeNormal) { this.utiliseSystemeNormal = utiliseSystemeNormal; }
    
    public Boolean getUtiliseSMT() { return utiliseSMT; }
    public void setUtiliseSMT(Boolean utiliseSMT) { this.utiliseSMT = utiliseSMT; }
    
    public Boolean getObligatoireONG() { return obligatoireONG; }
    public void setObligatoireONG(Boolean obligatoireONG) { this.obligatoireONG = obligatoireONG; }
    
    public String getDescriptionUtilisation() { return descriptionUtilisation; }
    public void setDescriptionUtilisation(String descriptionUtilisation) { this.descriptionUtilisation = descriptionUtilisation; }
    
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    // Méthode builder
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private String numeroCompte;
        private String intituleCompte;
        private ClasseCompte classeCompte;
        private TypeCompte typeCompte;
        private Integer niveau = 1;
        private String compteParent;
        private SensNormalCompte sensNormal;
        private Boolean utiliseSystemeNormal = true;
        private Boolean utiliseSMT = true;
        private Boolean obligatoireONG = false;
        private String descriptionUtilisation;
        private Boolean actif = true;
        private LocalDateTime dateCreation;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder numeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; return this; }
        public Builder intituleCompte(String intituleCompte) { this.intituleCompte = intituleCompte; return this; }
        public Builder classeCompte(ClasseCompte classeCompte) { this.classeCompte = classeCompte; return this; }
        public Builder typeCompte(TypeCompte typeCompte) { this.typeCompte = typeCompte; return this; }
        public Builder niveau(Integer niveau) { this.niveau = niveau; return this; }
        public Builder compteParent(String compteParent) { this.compteParent = compteParent; return this; }
        public Builder sensNormal(SensNormalCompte sensNormal) { this.sensNormal = sensNormal; return this; }
        public Builder utiliseSystemeNormal(Boolean utiliseSystemeNormal) { this.utiliseSystemeNormal = utiliseSystemeNormal; return this; }
        public Builder utiliseSMT(Boolean utiliseSMT) { this.utiliseSMT = utiliseSMT; return this; }
        public Builder obligatoireONG(Boolean obligatoireONG) { this.obligatoireONG = obligatoireONG; return this; }
        public Builder descriptionUtilisation(String descriptionUtilisation) { this.descriptionUtilisation = descriptionUtilisation; return this; }
        public Builder actif(Boolean actif) { this.actif = actif; return this; }
        public Builder dateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; return this; }
        
        public PlanComptableSycebnl build() {
            return new PlanComptableSycebnl(id, numeroCompte, intituleCompte, classeCompte, typeCompte, 
                                          niveau, compteParent, sensNormal, utiliseSystemeNormal, utiliseSMT, 
                                          obligatoireONG, descriptionUtilisation, actif, dateCreation);
        }
    }
    
    // Enums
    public enum ClasseCompte {
        CLASSE_1("FINANCEMENT PERMANENT"),
        CLASSE_2("IMMOBILISATIONS"),
        CLASSE_3("STOCKS"),
        CLASSE_4("CRÉANCES ET DETTES"),
        CLASSE_5("COMPTES FINANCIERS"),
        CLASSE_6("CHARGES"),
        CLASSE_7("PRODUITS"),
        CLASSE_8("ENGAGEMENTS HORS BILAN");
        
        private final String libelle;
        
        ClasseCompte(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
    
    public enum TypeCompte {
        CLASSE, COMPTE, SOUS_COMPTE
    }
    
    public enum SensNormalCompte {
        DEBITEUR, CREDITEUR
    }
}
