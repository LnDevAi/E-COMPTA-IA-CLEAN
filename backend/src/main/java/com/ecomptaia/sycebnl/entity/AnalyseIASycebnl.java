package com.ecomptaia.sycebnl.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité pour les analyses IA des pièces justificatives SYCEBNL
 */
@Entity
@Table(name = "analyses_ia_sycebnl")
public class AnalyseIASycebnl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piece_justificative_id", nullable = false)
    private PieceJustificativeSycebnl pieceJustificative;
    
    @Column(name = "type_document_detecte", length = 50)
    private String typeDocumentDetecte;
    
    @Column(name = "confiance_type_document", precision = 3, scale = 2)
    private BigDecimal confianceTypeDocument;
    
    @Column(name = "montant_detecte", precision = 15, scale = 2)
    private BigDecimal montantDetecte;
    
    @Column(name = "confiance_montant", precision = 3, scale = 2)
    private BigDecimal confianceMontant;
    
    @Column(name = "devise_detectee", length = 3)
    private String deviseDetectee;
    
    @Column(name = "date_detectee")
    private java.time.LocalDate dateDetectee;
    
    @Column(name = "confiance_date", precision = 3, scale = 2)
    private BigDecimal confianceDate;
    
    @Column(name = "fournisseur_detecte", length = 255)
    private String fournisseurDetecte;
    
    @Column(name = "confiance_fournisseur", precision = 3, scale = 2)
    private BigDecimal confianceFournisseur;
    
    @Column(name = "client_detecte", length = 255)
    private String clientDetecte;
    
    @Column(name = "confiance_client", precision = 3, scale = 2)
    private BigDecimal confianceClient;
    
    @Column(name = "numero_facture", length = 100)
    private String numeroFacture;
    
    @Column(name = "confiance_numero_facture", precision = 3, scale = 2)
    private BigDecimal confianceNumeroFacture;
    
    @Column(name = "description_detectee", columnDefinition = "TEXT")
    private String descriptionDetectee;
    
    @Column(name = "confiance_description", precision = 3, scale = 2)
    private BigDecimal confianceDescription;
    
    @Column(name = "tva_detectee", precision = 5, scale = 2)
    private BigDecimal tvaDetectee;
    
    @Column(name = "confiance_tva", precision = 3, scale = 2)
    private BigDecimal confianceTVA;
    
    @Column(name = "montant_ht", precision = 15, scale = 2)
    private BigDecimal montantHT;
    
    @Column(name = "montant_ttc", precision = 15, scale = 2)
    private BigDecimal montantTTC;
    
    @Column(name = "confiance_globale", precision = 3, scale = 2)
    private BigDecimal confianceGlobale;
    
    @Column(name = "modele_ia_utilise", length = 100)
    private String modeleIAUtilise;
    
    @Column(name = "version_modele", length = 20)
    private String versionModele;
    
    @Column(name = "parametres_ia", columnDefinition = "TEXT")
    private String parametresIA; // JSON des paramètres utilisés
    
    @Column(name = "temps_traitement_ms")
    private Long tempsTraitementMs;
    
    @Column(name = "date_analyse", nullable = false)
    private LocalDateTime dateAnalyse;
    
    @Column(name = "statut_analyse", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatutAnalyse statutAnalyse = StatutAnalyse.EN_COURS;
    
    @Column(name = "erreur_analyse", columnDefinition = "TEXT")
    private String erreurAnalyse;
    
    @Column(name = "suggestions_ia", columnDefinition = "TEXT")
    private String suggestionsIA; // JSON des suggestions de l'IA
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des métadonnées
    
    @PrePersist
    protected void onCreate() {
        dateAnalyse = LocalDateTime.now();
    }
    
    // Constructeurs
    public AnalyseIASycebnl() {
    }
    
    public AnalyseIASycebnl(PieceJustificativeSycebnl pieceJustificative, String typeDocumentDetecte, 
                          BigDecimal confianceGlobale, String modeleIAUtilise) {
        this.pieceJustificative = pieceJustificative;
        this.typeDocumentDetecte = typeDocumentDetecte;
        this.confianceGlobale = confianceGlobale;
        this.modeleIAUtilise = modeleIAUtilise;
        this.statutAnalyse = StatutAnalyse.TERMINEE;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public PieceJustificativeSycebnl getPieceJustificative() { return pieceJustificative; }
    public void setPieceJustificative(PieceJustificativeSycebnl pieceJustificative) { this.pieceJustificative = pieceJustificative; }
    
    public String getTypeDocumentDetecte() { return typeDocumentDetecte; }
    public void setTypeDocumentDetecte(String typeDocumentDetecte) { this.typeDocumentDetecte = typeDocumentDetecte; }
    
    public BigDecimal getConfianceTypeDocument() { return confianceTypeDocument; }
    public void setConfianceTypeDocument(BigDecimal confianceTypeDocument) { this.confianceTypeDocument = confianceTypeDocument; }
    
    public BigDecimal getMontantDetecte() { return montantDetecte; }
    public void setMontantDetecte(BigDecimal montantDetecte) { this.montantDetecte = montantDetecte; }
    
    public BigDecimal getConfianceMontant() { return confianceMontant; }
    public void setConfianceMontant(BigDecimal confianceMontant) { this.confianceMontant = confianceMontant; }
    
    public String getDeviseDetectee() { return deviseDetectee; }
    public void setDeviseDetectee(String deviseDetectee) { this.deviseDetectee = deviseDetectee; }
    
    public java.time.LocalDate getDateDetectee() { return dateDetectee; }
    public void setDateDetectee(java.time.LocalDate dateDetectee) { this.dateDetectee = dateDetectee; }
    
    public BigDecimal getConfianceDate() { return confianceDate; }
    public void setConfianceDate(BigDecimal confianceDate) { this.confianceDate = confianceDate; }
    
    public String getFournisseurDetecte() { return fournisseurDetecte; }
    public void setFournisseurDetecte(String fournisseurDetecte) { this.fournisseurDetecte = fournisseurDetecte; }
    
    public BigDecimal getConfianceFournisseur() { return confianceFournisseur; }
    public void setConfianceFournisseur(BigDecimal confianceFournisseur) { this.confianceFournisseur = confianceFournisseur; }
    
    public String getClientDetecte() { return clientDetecte; }
    public void setClientDetecte(String clientDetecte) { this.clientDetecte = clientDetecte; }
    
    public BigDecimal getConfianceClient() { return confianceClient; }
    public void setConfianceClient(BigDecimal confianceClient) { this.confianceClient = confianceClient; }
    
    public String getNumeroFacture() { return numeroFacture; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }
    
    public BigDecimal getConfianceNumeroFacture() { return confianceNumeroFacture; }
    public void setConfianceNumeroFacture(BigDecimal confianceNumeroFacture) { this.confianceNumeroFacture = confianceNumeroFacture; }
    
    public String getDescriptionDetectee() { return descriptionDetectee; }
    public void setDescriptionDetectee(String descriptionDetectee) { this.descriptionDetectee = descriptionDetectee; }
    
    public BigDecimal getConfianceDescription() { return confianceDescription; }
    public void setConfianceDescription(BigDecimal confianceDescription) { this.confianceDescription = confianceDescription; }
    
    public BigDecimal getTvaDetectee() { return tvaDetectee; }
    public void setTvaDetectee(BigDecimal tvaDetectee) { this.tvaDetectee = tvaDetectee; }
    
    public BigDecimal getConfianceTVA() { return confianceTVA; }
    public void setConfianceTVA(BigDecimal confianceTVA) { this.confianceTVA = confianceTVA; }
    
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT; }
    
    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC; }
    
    public BigDecimal getConfianceGlobale() { return confianceGlobale; }
    public void setConfianceGlobale(BigDecimal confianceGlobale) { this.confianceGlobale = confianceGlobale; }
    
    public String getModeleIAUtilise() { return modeleIAUtilise; }
    public void setModeleIAUtilise(String modeleIAUtilise) { this.modeleIAUtilise = modeleIAUtilise; }
    
    public String getVersionModele() { return versionModele; }
    public void setVersionModele(String versionModele) { this.versionModele = versionModele; }
    
    public String getParametresIA() { return parametresIA; }
    public void setParametresIA(String parametresIA) { this.parametresIA = parametresIA; }
    
    public Long getTempsTraitementMs() { return tempsTraitementMs; }
    public void setTempsTraitementMs(Long tempsTraitementMs) { this.tempsTraitementMs = tempsTraitementMs; }
    
    public LocalDateTime getDateAnalyse() { return dateAnalyse; }
    public void setDateAnalyse(LocalDateTime dateAnalyse) { this.dateAnalyse = dateAnalyse; }
    
    public StatutAnalyse getStatutAnalyse() { return statutAnalyse; }
    public void setStatutAnalyse(StatutAnalyse statutAnalyse) { this.statutAnalyse = statutAnalyse; }
    
    public String getErreurAnalyse() { return erreurAnalyse; }
    public void setErreurAnalyse(String erreurAnalyse) { this.erreurAnalyse = erreurAnalyse; }
    
    public String getSuggestionsIA() { return suggestionsIA; }
    public void setSuggestionsIA(String suggestionsIA) { this.suggestionsIA = suggestionsIA; }
    
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
