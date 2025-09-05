package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.ecomptaia.entity.Account;
import com.ecomptaia.entity.CostCenter;
import com.ecomptaia.entity.Project;
import com.ecomptaia.entity.ThirdParty;

@Entity
@Table(name = "lignes_ecritures")
@SuppressWarnings("unused")
public class LigneEcriture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecriture_id", nullable = false)
    private EcritureComptable ecriture;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id", nullable = false)
    private Account compte;
    
    @Column(name = "compte_numero", nullable = false)
    private String compteNumero;
    
    @Column(name = "compte_libelle", nullable = false)
    private String compteLibelle;
    
    @Column(name = "libelle_ligne", nullable = false)
    private String libelleLigne;
    
    @Column(name = "debit", precision = 15, scale = 2)
    private BigDecimal debit;
    
    @Column(name = "credit", precision = 15, scale = 2)
    private BigDecimal credit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiers_id")
    private ThirdParty tiers;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centre_cout_id")
    private CostCenter centreCout;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id")
    private Project projet;
    
    @Column(name = "analytique", columnDefinition = "TEXT")
    private String analytique;
    
    @Column(name = "ordre", nullable = false)
    private Integer ordre;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public LigneEcriture() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.debit = BigDecimal.ZERO;
        this.credit = BigDecimal.ZERO;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public EcritureComptable getEcriture() { return ecriture; }
    public void setEcriture(EcritureComptable ecriture) { this.ecriture = ecriture; }
    
    public Account getCompte() { return compte; }
    public void setCompte(Account compte) { this.compte = compte; }
    
    public String getCompteNumero() { return compteNumero; }
    public void setCompteNumero(String compteNumero) { this.compteNumero = compteNumero; }
    
    public String getCompteLibelle() { return compteLibelle; }
    public void setCompteLibelle(String compteLibelle) { this.compteLibelle = compteLibelle; }
    
    public String getLibelleLigne() { return libelleLigne; }
    public void setLibelleLigne(String libelleLigne) { this.libelleLigne = libelleLigne; }
    
    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }
    
    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }
    
    public ThirdParty getTiers() { return tiers; }
    public void setTiers(ThirdParty tiers) { this.tiers = tiers; }
    
    public CostCenter getCentreCout() { return centreCout; }
    public void setCentreCout(CostCenter centreCout) { this.centreCout = centreCout; }
    
    public Project getProjet() { return projet; }
    public void setProjet(Project projet) { this.projet = projet; }
    
    public String getAnalytique() { return analytique; }
    public void setAnalytique(String analytique) { this.analytique = analytique; }
    
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Méthodes utilitaires
    public boolean isDebit() {
        return debit != null && debit.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isCredit() {
        return credit != null && credit.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public BigDecimal getMontant() {
        if (isDebit()) return debit;
        if (isCredit()) return credit;
        return BigDecimal.ZERO;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
