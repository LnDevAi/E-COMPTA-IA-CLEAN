package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.security.entity.User;

@Entity
@Table(name = "ecritures_comptables")
@SuppressWarnings("unused")
public class EcritureComptable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_piece", nullable = false, unique = true)
    private String numeroPiece;
    
    @Column(name = "date_ecriture", nullable = false)
    private LocalDate dateEcriture;
    
    @Column(name = "date_piece", nullable = false)
    private LocalDate datePiece;
    
    @Column(name = "reference")
    private String reference;
    
    @Column(name = "libelle", nullable = false)
    private String libelle;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_ecriture", nullable = false)
    private TypeEcriture typeEcriture;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutEcriture statut;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Company entreprise;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_id", nullable = false)
    private FinancialPeriod exercice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;
    
    @Column(name = "devise", nullable = false)
    private String devise;
    
    @Column(name = "taux_change", precision = 10, scale = 4)
    private BigDecimal tauxChange;
    
    @Column(name = "total_debit", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalDebit;
    
    @Column(name = "total_credit", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalCredit;
    
    @OneToMany(mappedBy = "ecriture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneEcriture> lignes;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ecriture_id")
    private List<GedDocument> piecesJointes;
    
    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.STRING)
    private SourceEcriture source;
    
    @Column(name = "template_id")
    private String templateId;
    
    @Column(name = "validation_ai_confiance")
    private BigDecimal validationAiConfiance;
    
    @Column(name = "validation_ai_suggestions", columnDefinition = "TEXT")
    private String validationAiSuggestions;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Enums
    public enum TypeEcriture {
        NORMALE, OUVERTURE, CLOTURE, A_NOUVEAU
    }
    
    public enum StatutEcriture {
        BROUILLON, VALIDEE, CLOTUREE, ANNULEE
    }
    
    public enum SourceEcriture {
        MANUELLE, IMPORTEE, AUTOMATIQUE, IA, TEMPLATE
    }
    
    // Constructeurs
    public EcritureComptable() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.statut = StatutEcriture.BROUILLON;
        this.source = SourceEcriture.MANUELLE;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroPiece() { return numeroPiece; }
    public void setNumeroPiece(String numeroPiece) { this.numeroPiece = numeroPiece; }
    
    public LocalDate getDateEcriture() { return dateEcriture; }
    public void setDateEcriture(LocalDate dateEcriture) { this.dateEcriture = dateEcriture; }
    
    public LocalDate getDatePiece() { return datePiece; }
    public void setDatePiece(LocalDate datePiece) { this.datePiece = datePiece; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    public TypeEcriture getTypeEcriture() { return typeEcriture; }
    public void setTypeEcriture(TypeEcriture typeEcriture) { this.typeEcriture = typeEcriture; }
    
    public StatutEcriture getStatut() { return statut; }
    public void setStatut(StatutEcriture statut) { this.statut = statut; }
    
    public Company getEntreprise() { return entreprise; }
    public void setEntreprise(Company entreprise) { this.entreprise = entreprise; }
    
    public FinancialPeriod getExercice() { return exercice; }
    public void setExercice(FinancialPeriod exercice) { this.exercice = exercice; }
    
    public User getUtilisateur() { return utilisateur; }
    public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }
    
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    
    public BigDecimal getTauxChange() { return tauxChange; }
    public void setTauxChange(BigDecimal tauxChange) { this.tauxChange = tauxChange; }
    
    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }
    
    public BigDecimal getTotalCredit() { return totalCredit; }
    public void setTotalCredit(BigDecimal totalCredit) { this.totalCredit = totalCredit; }
    
        public List<LigneEcriture> getLignes() { return lignes; }
    public void setLignes(List<LigneEcriture> lignes) { this.lignes = lignes; }
    
    public List<GedDocument> getPiecesJointes() { return piecesJointes; }
    public void setPiecesJointes(List<GedDocument> piecesJointes) { this.piecesJointes = piecesJointes; }
    
    public SourceEcriture getSource() { return source; }
    public void setSource(SourceEcriture source) { this.source = source; }
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    
    public BigDecimal getValidationAiConfiance() { return validationAiConfiance; }
    public void setValidationAiConfiance(BigDecimal validationAiConfiance) { this.validationAiConfiance = validationAiConfiance; }
    
    public String getValidationAiSuggestions() { return validationAiSuggestions; }
    public void setValidationAiSuggestions(String validationAiSuggestions) { this.validationAiSuggestions = validationAiSuggestions; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Méthodes utilitaires
    public boolean isEquilibree() {
        return totalDebit != null && totalCredit != null && 
               totalDebit.compareTo(totalCredit) == 0;
    }
    
    public void calculerTotaux() {
        if (lignes != null) {
            this.totalDebit = lignes.stream()
                .map(LigneEcriture::getDebit)
                .filter(d -> d != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            this.totalCredit = lignes.stream()
                .map(LigneEcriture::getCredit)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
