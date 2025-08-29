package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entreprises_smt")
public class EntrepriseSMT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomEntreprise;

    @Column(nullable = false, length = 20)
    private String numeroContribuable;

    @Column(nullable = false, length = 3)
    private String paysCode; // BF, CI, SN, etc.

    @Column(nullable = false, length = 20)
    private String regimeFiscal; // MICRO, PETITE, MOYENNE

    @Column(nullable = false)
    private Double seuilChiffreAffaires; // Seuil pour déterminer le régime

    @Column(nullable = false)
    private Boolean estActif = true;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column
    private LocalDateTime dateModification;

    @Column(length = 500)
    private String adresse;

    @Column(length = 20)
    private String telephone;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String representantLegal;

    @Column(length = 20)
    private String numeroRegistreCommerce;

    @Column(length = 20)
    private String numeroCNSS;

    @Column(length = 20)
    private String numeroCNAS;

    @Column(length = 100)
    private String activitePrincipale;

    @Column(length = 20)
    private String devise; // XOF, EUR, USD, etc.

    @Column(length = 20)
    private String statut = "ACTIVE"; // ACTIVE, SUSPENDU, RADIE

    @Column(length = 10)
    private String version = "1.0";

    // Constructeurs
    public EntrepriseSMT() {}

    public EntrepriseSMT(String nomEntreprise, String numeroContribuable, String paysCode, 
                        String regimeFiscal, Double seuilChiffreAffaires) {
        this.nomEntreprise = nomEntreprise;
        this.numeroContribuable = numeroContribuable;
        this.paysCode = paysCode;
        this.regimeFiscal = regimeFiscal;
        this.seuilChiffreAffaires = seuilChiffreAffaires;
        this.estActif = true;
        this.dateCreation = LocalDateTime.now();
        this.statut = "ACTIVE";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomEntreprise() { return nomEntreprise; }
    public void setNomEntreprise(String nomEntreprise) { this.nomEntreprise = nomEntreprise; }

    public String getNumeroContribuable() { return numeroContribuable; }
    public void setNumeroContribuable(String numeroContribuable) { this.numeroContribuable = numeroContribuable; }

    public String getPaysCode() { return paysCode; }
    public void setPaysCode(String paysCode) { this.paysCode = paysCode; }

    public String getRegimeFiscal() { return regimeFiscal; }
    public void setRegimeFiscal(String regimeFiscal) { this.regimeFiscal = regimeFiscal; }

    public Double getSeuilChiffreAffaires() { return seuilChiffreAffaires; }
    public void setSeuilChiffreAffaires(Double seuilChiffreAffaires) { this.seuilChiffreAffaires = seuilChiffreAffaires; }

    public Boolean getEstActif() { return estActif; }
    public void setEstActif(Boolean estActif) { this.estActif = estActif; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRepresentantLegal() { return representantLegal; }
    public void setRepresentantLegal(String representantLegal) { this.representantLegal = representantLegal; }

    public String getNumeroRegistreCommerce() { return numeroRegistreCommerce; }
    public void setNumeroRegistreCommerce(String numeroRegistreCommerce) { this.numeroRegistreCommerce = numeroRegistreCommerce; }

    public String getNumeroCNSS() { return numeroCNSS; }
    public void setNumeroCNSS(String numeroCNSS) { this.numeroCNSS = numeroCNSS; }

    public String getNumeroCNAS() { return numeroCNAS; }
    public void setNumeroCNAS(String numeroCNAS) { this.numeroCNAS = numeroCNAS; }

    public String getActivitePrincipale() { return activitePrincipale; }
    public void setActivitePrincipale(String activitePrincipale) { this.activitePrincipale = activitePrincipale; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    @Override
    public String toString() {
        return "EntrepriseSMT{" +
                "id=" + id +
                ", nomEntreprise='" + nomEntreprise + '\'' +
                ", numeroContribuable='" + numeroContribuable + '\'' +
                ", paysCode='" + paysCode + '\'' +
                ", regimeFiscal='" + regimeFiscal + '\'' +
                ", estActif=" + estActif +
                '}';
    }
}
