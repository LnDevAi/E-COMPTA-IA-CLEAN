package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "livre_depenses")
public class LivreDepense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_smt_id", nullable = false)
    private ExerciceSMT exercice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_tresorerie_id", nullable = false)
    private CompteTresorerie compteTresorerie;

    @Column(nullable = false)
    private LocalDate dateDepense;

    @Column(nullable = false, length = 100)
    private String libelle;

    @Column(nullable = false, length = 20)
    private String typeDepense; // ACHATS, FRAIS_GENERAUX, SALAIRES, IMPOTS, AUTRES_DEPENSES

    @Column(length = 50)
    private String numeroPiece; // Numéro de facture, reçu, etc.

    @Column(length = 100)
    private String tiers; // Nom du fournisseur ou tiers

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(length = 20)
    private String modePaiement; // ESPECES, CHEQUE, VIREMENT, MOBILE_MONEY

    @Column(length = 20)
    private String devise = "XOF";

    @Column(length = 500)
    private String observations;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column
    private LocalDateTime dateModification;

    @Column(length = 20)
    private String statut = "VALIDE"; // VALIDE, ANNULE, EN_ATTENTE

    @Column(length = 10)
    private String version = "1.0";

    // Constructeurs
    public LivreDepense() {}

    public LivreDepense(ExerciceSMT exercice, CompteTresorerie compteTresorerie, LocalDate dateDepense, 
                       String libelle, String typeDepense, BigDecimal montant) {
        this.exercice = exercice;
        this.compteTresorerie = compteTresorerie;
        this.dateDepense = dateDepense;
        this.libelle = libelle;
        this.typeDepense = typeDepense;
        this.montant = montant;
        this.dateCreation = LocalDateTime.now();
        this.statut = "VALIDE";
        this.devise = exercice.getDevise();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExerciceSMT getExercice() { return exercice; }
    public void setExercice(ExerciceSMT exercice) { this.exercice = exercice; }

    public CompteTresorerie getCompteTresorerie() { return compteTresorerie; }
    public void setCompteTresorerie(CompteTresorerie compteTresorerie) { this.compteTresorerie = compteTresorerie; }

    public LocalDate getDateDepense() { return dateDepense; }
    public void setDateDepense(LocalDate dateDepense) { this.dateDepense = dateDepense; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getTypeDepense() { return typeDepense; }
    public void setTypeDepense(String typeDepense) { this.typeDepense = typeDepense; }

    public String getNumeroPiece() { return numeroPiece; }
    public void setNumeroPiece(String numeroPiece) { this.numeroPiece = numeroPiece; }

    public String getTiers() { return tiers; }
    public void setTiers(String tiers) { this.tiers = tiers; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    // Méthodes utilitaires
    public boolean isDepenseAchats() {
        return "ACHATS".equals(typeDepense);
    }

    public boolean isDepenseFraisGeneraux() {
        return "FRAIS_GENERAUX".equals(typeDepense);
    }

    public boolean isDepenseSalaires() {
        return "SALAIRES".equals(typeDepense);
    }

    public boolean isDepenseImpots() {
        return "IMPOTS".equals(typeDepense);
    }

    public boolean isDepenseAutres() {
        return "AUTRES_DEPENSES".equals(typeDepense);
    }

    public boolean isPaiementEspeces() {
        return "ESPECES".equals(modePaiement);
    }

    public boolean isPaiementCheque() {
        return "CHEQUE".equals(modePaiement);
    }

    public boolean isPaiementVirement() {
        return "VIREMENT".equals(modePaiement);
    }

    public boolean isPaiementMobileMoney() {
        return "MOBILE_MONEY".equals(modePaiement);
    }

    public boolean isValide() {
        return "VALIDE".equals(statut);
    }

    public boolean isAnnule() {
        return "ANNULE".equals(statut);
    }

    @Override
    public String toString() {
        return "LivreDepense{" +
                "id=" + id +
                ", dateDepense=" + dateDepense +
                ", libelle='" + libelle + '\'' +
                ", typeDepense='" + typeDepense + '\'' +
                ", montant=" + montant +
                ", devise='" + devise + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
