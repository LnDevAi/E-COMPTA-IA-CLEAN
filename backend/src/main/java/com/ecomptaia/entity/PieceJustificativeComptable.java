package com.ecomptaia.entity;

import com.ecomptaia.security.entity.User;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EntitÃ© pour les PiÃ¨ces Justificatives Comptables
 * GÃ¨re le tÃ©lÃ©chargement, l'analyse OCR, la lecture IA et la gÃ©nÃ©ration d'Ã©critures
 */
@Entity
@Table(name = "pieces_justificatives_comptables")
public class PieceJustificativeComptable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_pj", nullable = false, unique = true)
    private String numeroPJ;
    
    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;
    
    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;
    
    @Column(name = "type_fichier", nullable = false)
    private String typeFichier; // PDF, JPG, PNG, etc.
    
    @Column(name = "taille_fichier")
    private Long tailleFichier;
    
    @Column(name = "date_document")
    private LocalDate dateDocument;
    
    @Column(name = "date_upload", nullable = false)
    private LocalDateTime dateUpload;
    
    @Column(name = "libelle", nullable = false)
    private String libelle;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_document", nullable = false)
    private TypeDocument typeDocument;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_traitement", nullable = false)
    private StatutTraitement statutTraitement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_id", nullable = false)
    private FinancialPeriod exercice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    // ==================== ANALYSE OCR ====================
    
    @Column(name = "ocr_text", columnDefinition = "TEXT")
    private String ocrText;
    
    @Column(name = "ocr_confidence")
    private BigDecimal ocrConfidence;
    
    @Column(name = "ocr_date_traitement")
    private LocalDateTime ocrDateTraitement;
    
    @Column(name = "ocr_statut")
    @Enumerated(EnumType.STRING)
    private StatutOCR ocrStatut;
    
    // ==================== ANALYSE IA ====================
    
    @Column(name = "ia_analyse", columnDefinition = "TEXT")
    private String iaAnalyse;
    
    @Column(name = "ia_confidence")
    private BigDecimal iaConfidence;
    
    @Column(name = "ia_date_traitement")
    private LocalDateTime iaDateTraitement;
    
    @Column(name = "ia_statut")
    @Enumerated(EnumType.STRING)
    private StatutIA iaStatut;
    
    @Column(name = "ia_suggestions", columnDefinition = "TEXT")
    private String iaSuggestions;
    
    // ==================== PROPOSITIONS D'Ã‰CRITURES ====================
    
    @Column(name = "propositions_ecritures", columnDefinition = "TEXT")
    private String propositionsEcritures; // JSON des propositions
    
    @Column(name = "montant_detecte", precision = 15, scale = 2)
    private BigDecimal montantDetecte;
    
    @Column(name = "devise_detectee", length = 3)
    private String deviseDetectee;
    
    @Column(name = "comptes_suggerees", columnDefinition = "TEXT")
    private String comptesSuggerees; // JSON des comptes suggÃ©rÃ©s
    
    // ==================== VALIDATION ET GÃ‰NÃ‰RATION ====================
    
    @Column(name = "ecriture_generee_id")
    private Long ecritureGenereeId;
    
    @Column(name = "fiche_imputation", columnDefinition = "TEXT")
    private String ficheImputation; // JSON de la fiche d'imputation
    
    @Column(name = "journal_comptable", length = 50)
    private String journalComptable;
    
    @Column(name = "numero_ecriture")
    private String numeroEcriture;
    
    @Column(name = "date_generation_ecriture")
    private LocalDateTime dateGenerationEcriture;
    
    // ==================== MÃ‰TADONNÃ‰ES ====================
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des mÃ©tadonnÃ©es
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // Tags sÃ©parÃ©s par virgules
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // ==================== Ã‰NUMÃ‰RATIONS ====================
    
    public enum TypeDocument {
        FACTURE_FOURNISSEUR,
        FACTURE_CLIENT,
        BON_COMMANDE,
        BON_RECEPTION,
        BON_LIVRAISON,
        BULLETIN_PAIE,
        RELEVE_BANCAIRE,
        AVOIR,
        NOTE_CREDIT,
        NOTE_DEBIT,
        CHEQUE,
        VIREMENT,
        REMISE_CHEQUE,
        AUTRE
    }
    
    public enum StatutTraitement {
        UPLOADED,           // TÃ©lÃ©chargÃ©
        OCR_EN_COURS,       // OCR en cours
        OCR_TERMINE,        // OCR terminÃ©
        IA_EN_COURS,        // IA en cours
        IA_TERMINE,         // IA terminÃ©
        PROPOSITIONS_READY, // Propositions prÃªtes
        VALIDATED,          // ValidÃ© par l'utilisateur
        ECRITURE_GENERE,    // Ã‰criture gÃ©nÃ©rÃ©e
        ARCHIVED,           // ArchivÃ©
        ERROR               // Erreur
    }
    
    public enum StatutOCR {
        PENDING,    // En attente
        PROCESSING, // En cours
        COMPLETED,  // TerminÃ©
        FAILED      // Ã‰chec
    }
    
    public enum StatutIA {
        PENDING,    // En attente
        PROCESSING, // En cours
        COMPLETED,  // TerminÃ©
        FAILED      // Ã‰chec
    }
    
    // ==================== CONSTRUCTEURS ====================
    
    public PieceJustificativeComptable() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.statutTraitement = StatutTraitement.UPLOADED;
        this.ocrStatut = StatutOCR.PENDING;
        this.iaStatut = StatutIA.PENDING;
        this.dateUpload = LocalDateTime.now();
    }
    
    public PieceJustificativeComptable(String nomFichier, String cheminFichier, 
                                     String typeFichier, Company company, 
                                     FinancialPeriod exercice, User uploadedBy) {
        this();
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
        this.typeFichier = typeFichier;
        this.company = company;
        this.exercice = exercice;
        this.uploadedBy = uploadedBy;
        this.libelle = "PJ - " + nomFichier;
    }
    
    // ==================== GETTERS ET SETTERS ====================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroPJ() { return numeroPJ; }
    public void setNumeroPJ(String numeroPJ) { this.numeroPJ = numeroPJ; }
    
    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    
    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }
    
    public String getTypeFichier() { return typeFichier; }
    public void setTypeFichier(String typeFichier) { this.typeFichier = typeFichier; }
    
    public Long getTailleFichier() { return tailleFichier; }
    public void setTailleFichier(Long tailleFichier) { this.tailleFichier = tailleFichier; }
    
    public LocalDate getDateDocument() { return dateDocument; }
    public void setDateDocument(LocalDate dateDocument) { this.dateDocument = dateDocument; }
    
    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TypeDocument getTypeDocument() { return typeDocument; }
    public void setTypeDocument(TypeDocument typeDocument) { this.typeDocument = typeDocument; }
    
    public StatutTraitement getStatutTraitement() { return statutTraitement; }
    public void setStatutTraitement(StatutTraitement statutTraitement) { this.statutTraitement = statutTraitement; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public FinancialPeriod getExercice() { return exercice; }
    public void setExercice(FinancialPeriod exercice) { this.exercice = exercice; }
    
    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public User getValidatedBy() { return validatedBy; }
    public void setValidatedBy(User validatedBy) { this.validatedBy = validatedBy; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    
    // OCR
    public String getOcrText() { return ocrText; }
    public void setOcrText(String ocrText) { this.ocrText = ocrText; }
    
    public BigDecimal getOcrConfidence() { return ocrConfidence; }
    public void setOcrConfidence(BigDecimal ocrConfidence) { this.ocrConfidence = ocrConfidence; }
    
    public LocalDateTime getOcrDateTraitement() { return ocrDateTraitement; }
    public void setOcrDateTraitement(LocalDateTime ocrDateTraitement) { this.ocrDateTraitement = ocrDateTraitement; }
    
    public StatutOCR getOcrStatut() { return ocrStatut; }
    public void setOcrStatut(StatutOCR ocrStatut) { this.ocrStatut = ocrStatut; }
    
    // IA
    public String getIaAnalyse() { return iaAnalyse; }
    public void setIaAnalyse(String iaAnalyse) { this.iaAnalyse = iaAnalyse; }
    
    public BigDecimal getIaConfidence() { return iaConfidence; }
    public void setIaConfidence(BigDecimal iaConfidence) { this.iaConfidence = iaConfidence; }
    
    public LocalDateTime getIaDateTraitement() { return iaDateTraitement; }
    public void setIaDateTraitement(LocalDateTime iaDateTraitement) { this.iaDateTraitement = iaDateTraitement; }
    
    public StatutIA getIaStatut() { return iaStatut; }
    public void setIaStatut(StatutIA iaStatut) { this.iaStatut = iaStatut; }
    
    public String getIaSuggestions() { return iaSuggestions; }
    public void setIaSuggestions(String iaSuggestions) { this.iaSuggestions = iaSuggestions; }
    
    // Propositions
    public String getPropositionsEcritures() { return propositionsEcritures; }
    public void setPropositionsEcritures(String propositionsEcritures) { this.propositionsEcritures = propositionsEcritures; }
    
    public BigDecimal getMontantDetecte() { return montantDetecte; }
    public void setMontantDetecte(BigDecimal montantDetecte) { this.montantDetecte = montantDetecte; }
    
    public String getDeviseDetectee() { return deviseDetectee; }
    public void setDeviseDetectee(String deviseDetectee) { this.deviseDetectee = deviseDetectee; }
    
    public String getComptesSuggerees() { return comptesSuggerees; }
    public void setComptesSuggerees(String comptesSuggerees) { this.comptesSuggerees = comptesSuggerees; }
    
    // Validation et gÃ©nÃ©ration
    public Long getEcritureGenereeId() { return ecritureGenereeId; }
    public void setEcritureGenereeId(Long ecritureGenereeId) { this.ecritureGenereeId = ecritureGenereeId; }
    
    public String getFicheImputation() { return ficheImputation; }
    public void setFicheImputation(String ficheImputation) { this.ficheImputation = ficheImputation; }
    
    public String getJournalComptable() { return journalComptable; }
    public void setJournalComptable(String journalComptable) { this.journalComptable = journalComptable; }
    
    public String getNumeroEcriture() { return numeroEcriture; }
    public void setNumeroEcriture(String numeroEcriture) { this.numeroEcriture = numeroEcriture; }
    
    public LocalDateTime getDateGenerationEcriture() { return dateGenerationEcriture; }
    public void setDateGenerationEcriture(LocalDateTime dateGenerationEcriture) { this.dateGenerationEcriture = dateGenerationEcriture; }
    
    // MÃ©tadonnÃ©es
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // ==================== MÃ‰THODES UTILITAIRES ====================
    
    public boolean isOcrTermine() {
        return ocrStatut == StatutOCR.COMPLETED;
    }
    
    public boolean isIaTermine() {
        return iaStatut == StatutIA.COMPLETED;
    }
    
    public boolean isPretPourValidation() {
        return statutTraitement == StatutTraitement.PROPOSITIONS_READY;
    }
    
    public boolean isEcritureGeneree() {
        return ecritureGenereeId != null && statutTraitement == StatutTraitement.ECRITURE_GENERE;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "PieceJustificativeComptable{" +
                "id=" + id +
                ", numeroPJ='" + numeroPJ + '\'' +
                ", nomFichier='" + nomFichier + '\'' +
                ", typeDocument=" + typeDocument +
                ", statutTraitement=" + statutTraitement +
                ", company=" + (company != null ? company.getName() : "null") +
                '}';
    }
}





