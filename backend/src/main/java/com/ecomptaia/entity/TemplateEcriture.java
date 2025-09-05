package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "templates_ecritures")
public class TemplateEcriture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom", nullable = false)
    private String nom;
    
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Column(name = "standard_comptable", nullable = false)
    private String standardComptable;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "categorie", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategorieTemplate categorie;
    
    @Column(name = "pays_applicable")
    private String paysApplicable;
    
    @Column(name = "taux_tva_defaut", precision = 5, scale = 4)
    private BigDecimal tauxTvaDefaut;
    
    @Column(name = "devise_defaut")
    private String deviseDefaut;
    
    @Column(name = "comptes_pattern", columnDefinition = "TEXT")
    private String comptesPattern;
    
    @Column(name = "variables", columnDefinition = "TEXT")
    private String variables;
    
    @Column(name = "formules", columnDefinition = "TEXT")
    private String formules;
    
    @Column(name = "mots_cles", columnDefinition = "TEXT")
    private String motsCles;
    
    @Column(name = "is_actif", nullable = false)
    private Boolean isActif;
    
    @Column(name = "ordre_affichage")
    private Integer ordreAffichage;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Enums
    public enum CategorieTemplate {
        VENTE, ACHAT, PAIE, TRESORERIE, IMMOBILISATION, STOCK, 
        FISCAL, FINANCEMENT, REGULARISATION, CHANGE, PRODUCTION, 
        PROVISION, LOCATION, ASSURANCE, SUBVENTION, SPECIAL
    }
    
    // Constructeurs
    public TemplateEcriture() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActif = true;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getStandardComptable() { return standardComptable; }
    public void setStandardComptable(String standardComptable) { this.standardComptable = standardComptable; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public CategorieTemplate getCategorie() { return categorie; }
    public void setCategorie(CategorieTemplate categorie) { this.categorie = categorie; }
    
    public String getPaysApplicable() { return paysApplicable; }
    public void setPaysApplicable(String paysApplicable) { this.paysApplicable = paysApplicable; }
    
    public BigDecimal getTauxTvaDefaut() { return tauxTvaDefaut; }
    public void setTauxTvaDefaut(BigDecimal tauxTvaDefaut) { this.tauxTvaDefaut = tauxTvaDefaut; }
    
    public String getDeviseDefaut() { return deviseDefaut; }
    public void setDeviseDefaut(String deviseDefaut) { this.deviseDefaut = deviseDefaut; }
    
    public String getComptesPattern() { return comptesPattern; }
    public void setComptesPattern(String comptesPattern) { this.comptesPattern = comptesPattern; }
    
    public String getVariables() { return variables; }
    public void setVariables(String variables) { this.variables = variables; }
    
    public String getFormules() { return formules; }
    public void setFormules(String formules) { this.formules = formules; }
    
    public String getMotsCles() { return motsCles; }
    public void setMotsCles(String motsCles) { this.motsCles = motsCles; }
    
    public Boolean getIsActif() { return isActif; }
    public void setIsActif(Boolean isActif) { this.isActif = isActif; }
    
    public Integer getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(Integer ordreAffichage) { this.ordreAffichage = ordreAffichage; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}




