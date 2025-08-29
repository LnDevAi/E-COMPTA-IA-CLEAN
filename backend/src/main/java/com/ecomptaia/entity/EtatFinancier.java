package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "etats_financiers")
public class EtatFinancier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_smt_id", nullable = false)
    private ExerciceSMT exercice;

    @Column(nullable = false, length = 50)
    private String typeEtat; // BILAN, COMPTE_RESULTAT, FLUX_TRESORERIE, ANNEXES

    @Column(nullable = false, length = 20)
    private String standardComptable; // SYSCOHADA, IFRS, US_GAAP, PCG

    @Column(nullable = false)
    private LocalDate dateGeneration;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column
    private LocalDateTime dateModification;

    @Column(length = 20)
    private String statut = "GENERATED"; // DRAFT, GENERATED, VALIDATED, PUBLISHED

    @Column(length = 20)
    private String devise = "XOF";

    @Column(precision = 15, scale = 2)
    private BigDecimal totalActifs;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalPassifs;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalProduits;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalCharges;

    @Column(precision = 15, scale = 2)
    private BigDecimal resultatNet;

    @Column(length = 500)
    private String observations;

    @Column(length = 1000)
    private String donneesJson; // Données détaillées de l'état financier en JSON

    @Column(length = 10)
    private String version = "1.0";

    // Constructeurs
    public EtatFinancier() {}

    public EtatFinancier(ExerciceSMT exercice, String typeEtat, String standardComptable) {
        this.exercice = exercice;
        this.typeEtat = typeEtat;
        this.standardComptable = standardComptable;
        this.dateGeneration = LocalDate.now();
        this.dateCreation = LocalDateTime.now();
        this.statut = "GENERATED";
        this.devise = exercice.getDevise();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExerciceSMT getExercice() { return exercice; }
    public void setExercice(ExerciceSMT exercice) { this.exercice = exercice; }

    public String getTypeEtat() { return typeEtat; }
    public void setTypeEtat(String typeEtat) { this.typeEtat = typeEtat; }

    public String getStandardComptable() { return standardComptable; }
    public void setStandardComptable(String standardComptable) { this.standardComptable = standardComptable; }

    public LocalDate getDateGeneration() { return dateGeneration; }
    public void setDateGeneration(LocalDate dateGeneration) { this.dateGeneration = dateGeneration; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public BigDecimal getTotalActifs() { return totalActifs; }
    public void setTotalActifs(BigDecimal totalActifs) { this.totalActifs = totalActifs; }

    public BigDecimal getTotalPassifs() { return totalPassifs; }
    public void setTotalPassifs(BigDecimal totalPassifs) { this.totalPassifs = totalPassifs; }

    public BigDecimal getTotalProduits() { return totalProduits; }
    public void setTotalProduits(BigDecimal totalProduits) { this.totalProduits = totalProduits; }

    public BigDecimal getTotalCharges() { return totalCharges; }
    public void setTotalCharges(BigDecimal totalCharges) { this.totalCharges = totalCharges; }

    public BigDecimal getResultatNet() { return resultatNet; }
    public void setResultatNet(BigDecimal resultatNet) { this.resultatNet = resultatNet; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public String getDonneesJson() { return donneesJson; }
    public void setDonneesJson(String donneesJson) { this.donneesJson = donneesJson; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    // Méthodes utilitaires
    public boolean isBilan() {
        return "BILAN".equals(typeEtat);
    }

    public boolean isCompteResultat() {
        return "COMPTE_RESULTAT".equals(typeEtat);
    }

    public boolean isFluxTresorerie() {
        return "FLUX_TRESORERIE".equals(typeEtat);
    }

    public boolean isAnnexes() {
        return "ANNEXES".equals(typeEtat);
    }

    public boolean isSYSCOHADA() {
        return "SYSCOHADA".equals(standardComptable);
    }

    public boolean isIFRS() {
        return "IFRS".equals(standardComptable);
    }

    public boolean isUSGAAP() {
        return "US_GAAP".equals(standardComptable);
    }

    public boolean isPCG() {
        return "PCG".equals(standardComptable);
    }

    public boolean isGenerated() {
        return "GENERATED".equals(statut);
    }

    public boolean isValidated() {
        return "VALIDATED".equals(statut);
    }

    public boolean isPublished() {
        return "PUBLISHED".equals(statut);
    }

    public BigDecimal getSoldeNet() {
        if (totalActifs != null && totalPassifs != null) {
            return totalActifs.subtract(totalPassifs);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getResultatAvantImpot() {
        if (totalProduits != null && totalCharges != null) {
            return totalProduits.subtract(totalCharges);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "EtatFinancier{" +
                "id=" + id +
                ", typeEtat='" + typeEtat + '\'' +
                ", standardComptable='" + standardComptable + '\'' +
                ", dateGeneration=" + dateGeneration +
                ", statut='" + statut + '\'' +
                ", devise='" + devise + '\'' +
                '}';
    }
}




