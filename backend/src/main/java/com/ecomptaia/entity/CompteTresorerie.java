package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comptes_tresorerie")
public class CompteTresorerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_smt_id", nullable = false)
    private EntrepriseSMT entreprise;

    @Column(nullable = false, length = 50)
    private String nomCompte; // Ex: "Caisse principale", "Banque BICIA", "Mobile Money"

    @Column(nullable = false, length = 20)
    private String typeCompte; // CAISSE, BANQUE, MOBILE_MONEY, AUTRE

    @Column(length = 50)
    private String numeroCompte; // Numéro de compte bancaire ou mobile

    @Column(length = 100)
    private String nomBanque; // Nom de la banque si applicable

    @Column(length = 20)
    private String devise = "XOF";

    @Column(precision = 15, scale = 2)
    private BigDecimal soldeInitial = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal soldeActuel = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean estActif = true;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column
    private LocalDateTime dateModification;

    @Column(length = 500)
    private String description;

    @Column(length = 20)
    private String statut = "ACTIVE"; // ACTIVE, SUSPENDU, FERME

    @Column(length = 10)
    private String version = "1.0";

    // Constructeurs
    public CompteTresorerie() {}

    public CompteTresorerie(EntrepriseSMT entreprise, String nomCompte, String typeCompte) {
        this.entreprise = entreprise;
        this.nomCompte = nomCompte;
        this.typeCompte = typeCompte;
        this.estActif = true;
        this.dateCreation = LocalDateTime.now();
        this.statut = "ACTIVE";
        this.devise = entreprise.getDevise() != null ? entreprise.getDevise() : "XOF";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EntrepriseSMT getEntreprise() { return entreprise; }
    public void setEntreprise(EntrepriseSMT entreprise) { this.entreprise = entreprise; }

    public String getNomCompte() { return nomCompte; }
    public void setNomCompte(String nomCompte) { this.nomCompte = nomCompte; }

    public String getTypeCompte() { return typeCompte; }
    public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }

    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }

    public String getNomBanque() { return nomBanque; }
    public void setNomBanque(String nomBanque) { this.nomBanque = nomBanque; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public BigDecimal getSoldeInitial() { return soldeInitial; }
    public void setSoldeInitial(BigDecimal soldeInitial) { this.soldeInitial = soldeInitial; }

    public BigDecimal getSoldeActuel() { return soldeActuel; }
    public void setSoldeActuel(BigDecimal soldeActuel) { this.soldeActuel = soldeActuel; }

    public Boolean getEstActif() { return estActif; }
    public void setEstActif(Boolean estActif) { this.estActif = estActif; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    // Méthodes utilitaires
    public boolean isCompteCaisse() {
        return "CAISSE".equals(typeCompte);
    }

    public boolean isCompteBanque() {
        return "BANQUE".equals(typeCompte);
    }

    public boolean isCompteMobileMoney() {
        return "MOBILE_MONEY".equals(typeCompte);
    }

    public BigDecimal getSoldeDisponible() {
        return soldeActuel != null ? soldeActuel : BigDecimal.ZERO;
    }

    public void debiter(BigDecimal montant) {
        if (soldeActuel == null) {
            soldeActuel = BigDecimal.ZERO;
        }
        soldeActuel = soldeActuel.subtract(montant);
        dateModification = LocalDateTime.now();
    }

    public void crediter(BigDecimal montant) {
        if (soldeActuel == null) {
            soldeActuel = BigDecimal.ZERO;
        }
        soldeActuel = soldeActuel.add(montant);
        dateModification = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "CompteTresorerie{" +
                "id=" + id +
                ", nomCompte='" + nomCompte + '\'' +
                ", typeCompte='" + typeCompte + '\'' +
                ", soldeActuel=" + soldeActuel +
                ", devise='" + devise + '\'' +
                ", estActif=" + estActif +
                '}';
    }
}
