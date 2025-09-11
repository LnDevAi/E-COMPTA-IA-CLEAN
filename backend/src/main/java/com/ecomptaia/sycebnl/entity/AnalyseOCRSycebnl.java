package com.ecomptaia.sycebnl.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité pour les analyses OCR des pièces justificatives SYCEBNL
 */
@Entity
@Table(name = "analyses_ocr_sycebnl")
public class AnalyseOCRSycebnl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piece_justificative_id", nullable = false)
    private PieceJustificativeSycebnl pieceJustificative;
    
    @Column(name = "texte_extrait", columnDefinition = "TEXT", nullable = false)
    private String texteExtrait;
    
    @Column(name = "confiance_globale", precision = 3, scale = 2)
    private BigDecimal confianceGlobale;
    
    @Column(name = "langue_detectee", length = 10)
    private String langueDetectee;
    
    @Column(name = "nombre_mots")
    private Integer nombreMots;
    
    @Column(name = "nombre_lignes")
    private Integer nombreLignes;
    
    @Column(name = "mots_avec_faible_confiance", columnDefinition = "TEXT")
    private String motsAvecFaibleConfiance; // JSON des mots avec leur confiance
    
    @Column(name = "zones_detectees", columnDefinition = "TEXT")
    private String zonesDetectees; // JSON des zones détectées (coordonnées)
    
    @Column(name = "moteur_ocr_utilise", length = 50)
    private String moteurOCRUtilise;
    
    @Column(name = "version_moteur", length = 20)
    private String versionMoteur;
    
    @Column(name = "parametres_ocr", columnDefinition = "TEXT")
    private String parametresOCR; // JSON des paramètres utilisés
    
    @Column(name = "temps_traitement_ms")
    private Long tempsTraitementMs;
    
    @Column(name = "date_analyse", nullable = false)
    private LocalDateTime dateAnalyse;
    
    @Column(name = "statut_analyse", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatutAnalyse statutAnalyse = StatutAnalyse.EN_COURS;
    
    @Column(name = "erreur_analyse", columnDefinition = "TEXT")
    private String erreurAnalyse;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des métadonnées
    
    @PrePersist
    protected void onCreate() {
        dateAnalyse = LocalDateTime.now();
    }
    
    // Constructeurs
    public AnalyseOCRSycebnl() {
    }
    
    public AnalyseOCRSycebnl(PieceJustificativeSycebnl pieceJustificative, String texteExtrait, 
                           BigDecimal confianceGlobale, String moteurOCRUtilise) {
        this.pieceJustificative = pieceJustificative;
        this.texteExtrait = texteExtrait;
        this.confianceGlobale = confianceGlobale;
        this.moteurOCRUtilise = moteurOCRUtilise;
        this.statutAnalyse = StatutAnalyse.TERMINEE;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public PieceJustificativeSycebnl getPieceJustificative() { return pieceJustificative; }
    public void setPieceJustificative(PieceJustificativeSycebnl pieceJustificative) { this.pieceJustificative = pieceJustificative; }
    
    public String getTexteExtrait() { return texteExtrait; }
    public void setTexteExtrait(String texteExtrait) { this.texteExtrait = texteExtrait; }
    
    public BigDecimal getConfianceGlobale() { return confianceGlobale; }
    public void setConfianceGlobale(BigDecimal confianceGlobale) { this.confianceGlobale = confianceGlobale; }
    
    public String getLangueDetectee() { return langueDetectee; }
    public void setLangueDetectee(String langueDetectee) { this.langueDetectee = langueDetectee; }
    
    public Integer getNombreMots() { return nombreMots; }
    public void setNombreMots(Integer nombreMots) { this.nombreMots = nombreMots; }
    
    public Integer getNombreLignes() { return nombreLignes; }
    public void setNombreLignes(Integer nombreLignes) { this.nombreLignes = nombreLignes; }
    
    public String getMotsAvecFaibleConfiance() { return motsAvecFaibleConfiance; }
    public void setMotsAvecFaibleConfiance(String motsAvecFaibleConfiance) { this.motsAvecFaibleConfiance = motsAvecFaibleConfiance; }
    
    public String getZonesDetectees() { return zonesDetectees; }
    public void setZonesDetectees(String zonesDetectees) { this.zonesDetectees = zonesDetectees; }
    
    public String getMoteurOCRUtilise() { return moteurOCRUtilise; }
    public void setMoteurOCRUtilise(String moteurOCRUtilise) { this.moteurOCRUtilise = moteurOCRUtilise; }
    
    public String getVersionMoteur() { return versionMoteur; }
    public void setVersionMoteur(String versionMoteur) { this.versionMoteur = versionMoteur; }
    
    public String getParametresOCR() { return parametresOCR; }
    public void setParametresOCR(String parametresOCR) { this.parametresOCR = parametresOCR; }
    
    public Long getTempsTraitementMs() { return tempsTraitementMs; }
    public void setTempsTraitementMs(Long tempsTraitementMs) { this.tempsTraitementMs = tempsTraitementMs; }
    
    public LocalDateTime getDateAnalyse() { return dateAnalyse; }
    public void setDateAnalyse(LocalDateTime dateAnalyse) { this.dateAnalyse = dateAnalyse; }
    
    public StatutAnalyse getStatutAnalyse() { return statutAnalyse; }
    public void setStatutAnalyse(StatutAnalyse statutAnalyse) { this.statutAnalyse = statutAnalyse; }
    
    public String getErreurAnalyse() { return erreurAnalyse; }
    public void setErreurAnalyse(String erreurAnalyse) { this.erreurAnalyse = erreurAnalyse; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    // Enums
    public enum StatutAnalyse {
        EN_COURS("En cours"),
        TERMINEE("Terminée"),
        ERREUR("Erreur"),
        ANNULEE("Annulée");
        
        private final String libelle;
        
        StatutAnalyse(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
}
