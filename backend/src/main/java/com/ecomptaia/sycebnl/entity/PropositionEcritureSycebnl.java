package com.ecomptaia.sycebnl.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité pour les propositions d'écritures comptables SYCEBNL
 */
@Entity
@Table(name = "propositions_ecritures_sycebnl")
public class PropositionEcritureSycebnl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piece_justificative_id", nullable = false)
    private PieceJustificativeSycebnl pieceJustificative;
    
    @Column(name = "numero_proposition", nullable = false, length = 50)
    private String numeroProposition;
    
    @Column(name = "libelle_proposition", nullable = false, length = 255)
    private String libelleProposition;
    
    @Column(name = "date_proposition", nullable = false)
    private LocalDate dateProposition;
    
    @Column(name = "montant_total", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTotal;
    
    @Column(name = "devise", length = 3, nullable = false)
    private String devise = "XOF";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_ecriture", nullable = false, length = 30)
    private TypeEcritureProposition typeEcriture;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_proposition", nullable = false, length = 20)
    private StatutProposition statutProposition = StatutProposition.PROPOSEE;
    
    @Column(name = "confiance_proposition", precision = 3, scale = 2)
    private BigDecimal confianceProposition;
    
    @Column(name = "commentaires_proposition", columnDefinition = "TEXT")
    private String commentairesProposition;
    
    @Column(name = "justification_proposition", columnDefinition = "TEXT")
    private String justificationProposition;
    
    @Column(name = "regles_appliquees", columnDefinition = "TEXT")
    private String reglesAppliquees; // JSON des règles appliquées
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "cree_par")
    private Long creePar;
    
    @Column(name = "valide_par")
    private Long validePar;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @Column(name = "commentaires_validation", columnDefinition = "TEXT")
    private String commentairesValidation;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des métadonnées
    
    // Relations avec les lignes de proposition
    @OneToMany(mappedBy = "proposition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LignePropositionEcritureSycebnl> lignesProposition;
    
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
    public PropositionEcritureSycebnl() {
    }
    
    public PropositionEcritureSycebnl(PieceJustificativeSycebnl pieceJustificative, String numeroProposition, 
                                    String libelleProposition, LocalDate dateProposition, 
                                    BigDecimal montantTotal, TypeEcritureProposition typeEcriture) {
        this.pieceJustificative = pieceJustificative;
        this.numeroProposition = numeroProposition;
        this.libelleProposition = libelleProposition;
        this.dateProposition = dateProposition;
        this.montantTotal = montantTotal;
        this.typeEcriture = typeEcriture;
        this.statutProposition = StatutProposition.PROPOSEE;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public PieceJustificativeSycebnl getPieceJustificative() { return pieceJustificative; }
    public void setPieceJustificative(PieceJustificativeSycebnl pieceJustificative) { this.pieceJustificative = pieceJustificative; }
    
    public String getNumeroProposition() { return numeroProposition; }
    public void setNumeroProposition(String numeroProposition) { this.numeroProposition = numeroProposition; }
    
    public String getLibelleProposition() { return libelleProposition; }
    public void setLibelleProposition(String libelleProposition) { this.libelleProposition = libelleProposition; }
    
    public LocalDate getDateProposition() { return dateProposition; }
    public void setDateProposition(LocalDate dateProposition) { this.dateProposition = dateProposition; }
    
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    
    public TypeEcritureProposition getTypeEcriture() { return typeEcriture; }
    public void setTypeEcriture(TypeEcritureProposition typeEcriture) { this.typeEcriture = typeEcriture; }
    
    public StatutProposition getStatutProposition() { return statutProposition; }
    public void setStatutProposition(StatutProposition statutProposition) { this.statutProposition = statutProposition; }
    
    public BigDecimal getConfianceProposition() { return confianceProposition; }
    public void setConfianceProposition(BigDecimal confianceProposition) { this.confianceProposition = confianceProposition; }
    
    public String getCommentairesProposition() { return commentairesProposition; }
    public void setCommentairesProposition(String commentairesProposition) { this.commentairesProposition = commentairesProposition; }
    
    public String getJustificationProposition() { return justificationProposition; }
    public void setJustificationProposition(String justificationProposition) { this.justificationProposition = justificationProposition; }
    
    public String getReglesAppliquees() { return reglesAppliquees; }
    public void setReglesAppliquees(String reglesAppliquees) { this.reglesAppliquees = reglesAppliquees; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    
    public Long getCreePar() { return creePar; }
    public void setCreePar(Long creePar) { this.creePar = creePar; }
    
    public Long getValidePar() { return validePar; }
    public void setValidePar(Long validePar) { this.validePar = validePar; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    
    public String getCommentairesValidation() { return commentairesValidation; }
    public void setCommentairesValidation(String commentairesValidation) { this.commentairesValidation = commentairesValidation; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public List<LignePropositionEcritureSycebnl> getLignesProposition() { return lignesProposition; }
    public void setLignesProposition(List<LignePropositionEcritureSycebnl> lignesProposition) { this.lignesProposition = lignesProposition; }
    
    // Enums
    public enum TypeEcritureProposition {
        FACTURE_FOURNISSEUR("Facture fournisseur"),
        FACTURE_CLIENT("Facture client"),
        PAIEMENT_FOURNISSEUR("Paiement fournisseur"),
        ENCAISSEMENT_CLIENT("Encaissement client"),
        DEPENSE("Dépense"),
        RECETTE("Recette"),
        REGULARISATION("Régularisation"),
        AUTRE("Autre");
        
        private final String libelle;
        
        TypeEcritureProposition(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
    
    public enum StatutProposition {
        PROPOSEE("Proposée"),
        EN_ATTENTE_VALIDATION("En attente de validation"),
        VALIDEE("Validée"),
        REJETEE("Rejetée"),
        MODIFIEE("Modifiée"),
        GENEREES("Écriture générée");
        
        private final String libelle;
        
        StatutProposition(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
}
