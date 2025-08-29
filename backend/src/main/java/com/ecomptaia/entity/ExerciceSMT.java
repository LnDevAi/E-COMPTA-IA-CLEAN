package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercices_smt")
public class ExerciceSMT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_smt_id", nullable = false)
    private EntrepriseSMT entreprise;

    @Column(nullable = false)
    private Integer anneeExercice;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false)
    private Boolean estCloture = false;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column
    private LocalDateTime dateCloture;

    @Column(length = 20)
    private String statut = "OUVERT"; // OUVERT, EN_COURS, CLOTURE

    @Column(precision = 15, scale = 2)
    private BigDecimal chiffreAffairesTotal;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalRecettes;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalDepenses;

    @Column(precision = 15, scale = 2)
    private BigDecimal soldeTresorerie;

    @Column(length = 20)
    private String devise = "XOF";

    @Column(length = 500)
    private String observations;

    @Column(length = 10)
    private String version = "1.0";

    // Constructeurs
    public ExerciceSMT() {}

    public ExerciceSMT(EntrepriseSMT entreprise, Integer anneeExercice, LocalDate dateDebut, LocalDate dateFin) {
        this.entreprise = entreprise;
        this.anneeExercice = anneeExercice;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.estCloture = false;
        this.dateCreation = LocalDateTime.now();
        this.statut = "OUVERT";
        this.devise = entreprise.getDevise() != null ? entreprise.getDevise() : "XOF";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EntrepriseSMT getEntreprise() { return entreprise; }
    public void setEntreprise(EntrepriseSMT entreprise) { this.entreprise = entreprise; }

    public Integer getAnneeExercice() { return anneeExercice; }
    public void setAnneeExercice(Integer anneeExercice) { this.anneeExercice = anneeExercice; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Boolean getEstCloture() { return estCloture; }
    public void setEstCloture(Boolean estCloture) { this.estCloture = estCloture; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDateTime dateCloture) { this.dateCloture = dateCloture; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public BigDecimal getChiffreAffairesTotal() { return chiffreAffairesTotal; }
    public void setChiffreAffairesTotal(BigDecimal chiffreAffairesTotal) { this.chiffreAffairesTotal = chiffreAffairesTotal; }

    public BigDecimal getTotalRecettes() { return totalRecettes; }
    public void setTotalRecettes(BigDecimal totalRecettes) { this.totalRecettes = totalRecettes; }

    public BigDecimal getTotalDepenses() { return totalDepenses; }
    public void setTotalDepenses(BigDecimal totalDepenses) { this.totalDepenses = totalDepenses; }

    public BigDecimal getSoldeTresorerie() { return soldeTresorerie; }
    public void setSoldeTresorerie(BigDecimal soldeTresorerie) { this.soldeTresorerie = soldeTresorerie; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    // Méthodes utilitaires
    public boolean isEnCours() {
        LocalDate aujourdhui = LocalDate.now();
        return !estCloture && dateDebut.isBefore(aujourdhui) && dateFin.isAfter(aujourdhui);
    }

    public boolean peutEtreCloture() {
        return !estCloture && LocalDate.now().isAfter(dateFin);
    }

    public BigDecimal getResultatNet() {
        if (totalRecettes != null && totalDepenses != null) {
            return totalRecettes.subtract(totalDepenses);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "ExerciceSMT{" +
                "id=" + id +
                ", entreprise=" + (entreprise != null ? entreprise.getNomEntreprise() : "null") +
                ", anneeExercice=" + anneeExercice +
                ", statut='" + statut + '\'' +
                ", estCloture=" + estCloture +
                '}';
    }
}
