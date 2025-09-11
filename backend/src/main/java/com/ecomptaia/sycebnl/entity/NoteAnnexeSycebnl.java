package com.ecomptaia.sycebnl.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entité pour les notes annexes des états financiers SYCEBNL
 */
@Entity
@Table(name = "notes_annexes_sycebnl")
public class NoteAnnexeSycebnl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etat_financier_id", nullable = false)
    private EtatFinancierSycebnl etatFinancier;
    
    @Column(name = "numero_note", nullable = false, length = 10)
    private String numeroNote;
    
    @Column(name = "titre_note", nullable = false, length = 255)
    private String titreNote;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_note", nullable = false, length = 50)
    private TypeNote typeNote;
    
    @Column(name = "contenu_note", columnDefinition = "TEXT", nullable = false)
    private String contenuNote;
    
    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage;
    
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
    public NoteAnnexeSycebnl() {
    }
    
    public NoteAnnexeSycebnl(Long id, EtatFinancierSycebnl etatFinancier, String numeroNote, 
                            String titreNote, TypeNote typeNote, String contenuNote, 
                            Integer ordreAffichage, LocalDateTime dateCreation, 
                            LocalDateTime dateModification) {
        this.id = id;
        this.etatFinancier = etatFinancier;
        this.numeroNote = numeroNote;
        this.titreNote = titreNote;
        this.typeNote = typeNote;
        this.contenuNote = contenuNote;
        this.ordreAffichage = ordreAffichage;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public EtatFinancierSycebnl getEtatFinancier() { return etatFinancier; }
    public void setEtatFinancier(EtatFinancierSycebnl etatFinancier) { this.etatFinancier = etatFinancier; }
    
    public String getNumeroNote() { return numeroNote; }
    public void setNumeroNote(String numeroNote) { this.numeroNote = numeroNote; }
    
    public String getTitreNote() { return titreNote; }
    public void setTitreNote(String titreNote) { this.titreNote = titreNote; }
    
    public TypeNote getTypeNote() { return typeNote; }
    public void setTypeNote(TypeNote typeNote) { this.typeNote = typeNote; }
    
    public String getContenuNote() { return contenuNote; }
    public void setContenuNote(String contenuNote) { this.contenuNote = contenuNote; }
    
    public Integer getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(Integer ordreAffichage) { this.ordreAffichage = ordreAffichage; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    
    // Méthode builder
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private EtatFinancierSycebnl etatFinancier;
        private String numeroNote;
        private String titreNote;
        private TypeNote typeNote;
        private String contenuNote;
        private Integer ordreAffichage;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder etatFinancier(EtatFinancierSycebnl etatFinancier) { this.etatFinancier = etatFinancier; return this; }
        public Builder numeroNote(String numeroNote) { this.numeroNote = numeroNote; return this; }
        public Builder titreNote(String titreNote) { this.titreNote = titreNote; return this; }
        public Builder typeNote(TypeNote typeNote) { this.typeNote = typeNote; return this; }
        public Builder contenuNote(String contenuNote) { this.contenuNote = contenuNote; return this; }
        public Builder ordreAffichage(Integer ordreAffichage) { this.ordreAffichage = ordreAffichage; return this; }
        public Builder dateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; return this; }
        public Builder dateModification(LocalDateTime dateModification) { this.dateModification = dateModification; return this; }
        
        public NoteAnnexeSycebnl build() {
            return new NoteAnnexeSycebnl(id, etatFinancier, numeroNote, titreNote, typeNote, 
                                       contenuNote, ordreAffichage, dateCreation, dateModification);
        }
    }
    
    // Enums pour les types de notes
    public enum TypeNote {
        // Notes système normal
        NOTE_1_REGLES_METHODES,
        NOTE_2_IMMOBILISATIONS,
        NOTE_3_STOCKS,
        NOTE_4_CREANCES,
        NOTE_5_DETTES,
        NOTE_6_CAPITAUX_PROPRES,
        NOTE_7_CHARGES,
        NOTE_8_PRODUITS,
        NOTE_9_ENGAGEMENTS_HORS_BILAN,
        NOTE_10_EVENEMENTS_POSTERIEURS,
        
        // Notes système minimal (SMT)
        NOTE_SMT_1_REGLES_METHODES,
        NOTE_SMT_2_IMMOBILISATIONS,
        NOTE_SMT_3_TRESORERIE,
        NOTE_SMT_4_FONDS_PROPRES,
        NOTE_SMT_5_DETTES,
        NOTE_SMT_6_RESSOURCES,
        NOTE_SMT_7_CHARGES,
        NOTE_SMT_8_EVENEMENTS_POSTERIEURS
    }
}
