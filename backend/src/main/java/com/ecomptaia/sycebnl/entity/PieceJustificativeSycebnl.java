package com.ecomptaia.sycebnl.entity;

import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.entity.EcritureComptable;
import com.ecomptaia.entity.Company;
import com.ecomptaia.security.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EntitÃ© pour les piÃ¨ces justificatives comptables SYCEBNL
 * GÃ¨re le workflow complet : tÃ©lÃ©chargement, analyse OCR, IA, proposition, validation, gÃ©nÃ©ration
 */
@Entity
@Table(name = "pieces_justificatives_sycebnl")
public class PieceJustificativeSycebnl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_pj", nullable = false, unique = true, length = 50)
    private String numeroPJ;
    
    @Column(name = "libelle_pj", nullable = false, length = 255)
    private String libellePJ;
    
    @Column(name = "date_piece", nullable = false)
    private LocalDate datePiece;
    
    @Column(name = "montant_total", precision = 15, scale = 2)
    private BigDecimal montantTotal;
    
    @Column(name = "devise", length = 3, nullable = false)
    private String devise = "XOF";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_pj", nullable = false, length = 50)
    private TypePieceJustificative typePJ;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_traitement", nullable = false, length = 30)
    private StatutTraitement statutTraitement = StatutTraitement.TELECHARGEE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private GedDocument document;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Company entreprise;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;
    
    // RÃ©sultats de l'analyse OCR
    @Column(name = "texte_ocr", columnDefinition = "TEXT")
    private String texteOCR;
    
    @Column(name = "confiance_ocr", precision = 3, scale = 2)
    private BigDecimal confianceOCR;
    
    @Column(name = "date_analyse_ocr")
    private LocalDateTime dateAnalyseOCR;
    
    // RÃ©sultats de l'analyse IA
    @Column(name = "analyse_ia", columnDefinition = "TEXT")
    private String analyseIA;
    
    @Column(name = "confiance_ia", precision = 3, scale = 2)
    private BigDecimal confianceIA;
    
    @Column(name = "date_analyse_ia")
    private LocalDateTime dateAnalyseIA;
    
    // Propositions d'Ã©critures
    @Column(name = "propositions_ecritures", columnDefinition = "TEXT")
    private String propositionsEcritures; // JSON des propositions
    
    @Column(name = "proposition_selectionnee")
    private Long propositionSelectionnee;
    
    @Column(name = "date_proposition")
    private LocalDateTime dateProposition;
    
    // Validation
    @Column(name = "valide_par")
    private Long validePar;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @Column(name = "commentaires_validation", columnDefinition = "TEXT")
    private String commentairesValidation;
    
    // GÃ©nÃ©ration d'Ã©criture
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecriture_generee_id")
    private EcritureComptable ecritureGeneree;
    
    @Column(name = "date_generation_ecriture")
    private LocalDateTime dateGenerationEcriture;
    
    @Column(name = "numero_ecriture_generee", length = 50)
    private String numeroEcritureGeneree;
    
    // MÃ©tadonnÃ©es
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des mÃ©tadonnÃ©es
    
    @Column(name = "tags", length = 500)
    private String tags; // Tags sÃ©parÃ©s par des virgules
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // Relations avec les analyses
    @OneToMany(mappedBy = "pieceJustificative", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalyseOCRSycebnl> analysesOCR;
    
    @OneToMany(mappedBy = "pieceJustificative", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalyseIASycebnl> analysesIA;
    
    @OneToMany(mappedBy = "pieceJustificative", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PropositionEcritureSycebnl> propositions;
    
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
    public PieceJustificativeSycebnl() {
    }
    
    public PieceJustificativeSycebnl(String numeroPJ, String libellePJ, LocalDate datePiece, 
                                   TypePieceJustificative typePJ, GedDocument document, 
                                   Company entreprise, User utilisateur) {
        this.numeroPJ = numeroPJ;
        this.libellePJ = libellePJ;
        this.datePiece = datePiece;
        this.typePJ = typePJ;
        this.document = document;
        this.entreprise = entreprise;
        this.utilisateur = utilisateur;
        this.statutTraitement = StatutTraitement.TELECHARGEE;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroPJ() { return numeroPJ; }
    public void setNumeroPJ(String numeroPJ) { this.numeroPJ = numeroPJ; }
    
    public String getLibellePJ() { return libellePJ; }
    public void setLibellePJ(String libellePJ) { this.libellePJ = libellePJ; }
    
    public LocalDate getDatePiece() { return datePiece; }
    public void setDatePiece(LocalDate datePiece) { this.datePiece = datePiece; }
    
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    
    public TypePieceJustificative getTypePJ() { return typePJ; }
    public void setTypePJ(TypePieceJustificative typePJ) { this.typePJ = typePJ; }
    
    public StatutTraitement getStatutTraitement() { return statutTraitement; }
    public void setStatutTraitement(StatutTraitement statutTraitement) { this.statutTraitement = statutTraitement; }
    
    public GedDocument getDocument() { return document; }
    public void setDocument(GedDocument document) { this.document = document; }
    
    public Company getEntreprise() { return entreprise; }
    public void setEntreprise(Company entreprise) { this.entreprise = entreprise; }
    
    public User getUtilisateur() { return utilisateur; }
    public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }
    
    public String getTexteOCR() { return texteOCR; }
    public void setTexteOCR(String texteOCR) { this.texteOCR = texteOCR; }
    
    public BigDecimal getConfianceOCR() { return confianceOCR; }
    public void setConfianceOCR(BigDecimal confianceOCR) { this.confianceOCR = confianceOCR; }
    
    public LocalDateTime getDateAnalyseOCR() { return dateAnalyseOCR; }
    public void setDateAnalyseOCR(LocalDateTime dateAnalyseOCR) { this.dateAnalyseOCR = dateAnalyseOCR; }
    
    public String getAnalyseIA() { return analyseIA; }
    public void setAnalyseIA(String analyseIA) { this.analyseIA = analyseIA; }
    
    public BigDecimal getConfianceIA() { return confianceIA; }
    public void setConfianceIA(BigDecimal confianceIA) { this.confianceIA = confianceIA; }
    
    public LocalDateTime getDateAnalyseIA() { return dateAnalyseIA; }
    public void setDateAnalyseIA(LocalDateTime dateAnalyseIA) { this.dateAnalyseIA = dateAnalyseIA; }
    
    public String getPropositionsEcritures() { return propositionsEcritures; }
    public void setPropositionsEcritures(String propositionsEcritures) { this.propositionsEcritures = propositionsEcritures; }
    
    public Long getPropositionSelectionnee() { return propositionSelectionnee; }
    public void setPropositionSelectionnee(Long propositionSelectionnee) { this.propositionSelectionnee = propositionSelectionnee; }
    
    public LocalDateTime getDateProposition() { return dateProposition; }
    public void setDateProposition(LocalDateTime dateProposition) { this.dateProposition = dateProposition; }
    
    public Long getValidePar() { return validePar; }
    public void setValidePar(Long validePar) { this.validePar = validePar; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    
    public String getCommentairesValidation() { return commentairesValidation; }
    public void setCommentairesValidation(String commentairesValidation) { this.commentairesValidation = commentairesValidation; }
    
    public EcritureComptable getEcritureGeneree() { return ecritureGeneree; }
    public void setEcritureGeneree(EcritureComptable ecritureGeneree) { this.ecritureGeneree = ecritureGeneree; }
    
    public LocalDateTime getDateGenerationEcriture() { return dateGenerationEcriture; }
    public void setDateGenerationEcriture(LocalDateTime dateGenerationEcriture) { this.dateGenerationEcriture = dateGenerationEcriture; }
    
    public String getNumeroEcritureGeneree() { return numeroEcritureGeneree; }
    public void setNumeroEcritureGeneree(String numeroEcritureGeneree) { this.numeroEcritureGeneree = numeroEcritureGeneree; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    
    public List<AnalyseOCRSycebnl> getAnalysesOCR() { return analysesOCR; }
    public void setAnalysesOCR(List<AnalyseOCRSycebnl> analysesOCR) { this.analysesOCR = analysesOCR; }
    
    public List<AnalyseIASycebnl> getAnalysesIA() { return analysesIA; }
    public void setAnalysesIA(List<AnalyseIASycebnl> analysesIA) { this.analysesIA = analysesIA; }
    
    public List<PropositionEcritureSycebnl> getPropositions() { return propositions; }
    public void setPropositions(List<PropositionEcritureSycebnl> propositions) { this.propositions = propositions; }
    
    // Enums
    public enum TypePieceJustificative {
        FACTURE_FOURNISSEUR("Facture fournisseur"),
        FACTURE_CLIENT("Facture client"),
        BON_COMMANDE("Bon de commande"),
        BON_LIVRAISON("Bon de livraison"),
        RECU("ReÃ§u"),
        BORDEREAU_BANCAIRE("Bordereau bancaire"),
        RELEVE_BANCAIRE("RelevÃ© bancaire"),
        BULLETIN_PAIE("Bulletin de paie"),
        DECLARATION_FISCALE("DÃ©claration fiscale"),
        CONTRAT("Contrat"),
        AUTRE("Autre");
        
        private final String libelle;
        
        TypePieceJustificative(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
    
    public enum StatutTraitement {
        TELECHARGEE("TÃ©lÃ©chargÃ©e"),
        ANALYSE_OCR_EN_COURS("Analyse OCR en cours"),
        ANALYSE_OCR_TERMINEE("Analyse OCR terminÃ©e"),
        ANALYSE_IA_EN_COURS("Analyse IA en cours"),
        ANALYSE_IA_TERMINEE("Analyse IA terminÃ©e"),
        PROPOSITIONS_GENEREES("Propositions gÃ©nÃ©rÃ©es"),
        EN_ATTENTE_VALIDATION("En attente de validation"),
        VALIDEE("ValidÃ©e"),
        ECRITURE_GENEREES("Ã‰criture gÃ©nÃ©rÃ©e"),
        REJETEE("RejetÃ©e"),
        ARCHIVEE("ArchivÃ©e");
        
        private final String libelle;
        
        StatutTraitement(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
}

