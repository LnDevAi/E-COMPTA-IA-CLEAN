package com.ecomptaia.sycebnl.entity;

import com.ecomptaia.accounting.entity.Account;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Entité pour les lignes de proposition d'écriture comptable SYCEBNL
 */
@Entity
@Table(name = "lignes_propositions_ecritures_sycebnl")
public class LignePropositionEcritureSycebnl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposition_id", nullable = false)
    private PropositionEcritureSycebnl proposition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id", nullable = false)
    private Account compte;
    
    @Column(name = "numero_compte", nullable = false, length = 10)
    private String numeroCompte;
    
    @Column(name = "libelle_compte", nullable = false, length = 255)
    private String libelleCompte;
    
    @Column(name = "libelle_ligne", nullable = false, length = 255)
    private String libelleLigne;
    
    @Column(name = "debit", precision = 15, scale = 2)
    private BigDecimal debit;
    
    @Column(name = "credit", precision = 15, scale = 2)
    private BigDecimal credit;
    
    @Column(name = "ordre", nullable = false)
    private Integer ordre;
    
    @Column(name = "sens_normal", length = 10)
    private String sensNormal; // DEBITEUR ou CREDITEUR
    
    @Column(name = "confiance_ligne", precision = 3, scale = 2)
    private BigDecimal confianceLigne;
    
    @Column(name = "justification_ligne", columnDefinition = "TEXT")
    private String justificationLigne;
    
    @Column(name = "regle_appliquee", length = 100)
    private String regleAppliquee;
    
    @Column(name = "parametres_regle", columnDefinition = "TEXT")
    private String parametresRegle; // JSON des paramètres de la règle
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON des métadonnées
    
    // Constructeurs
    public LignePropositionEcritureSycebnl() {
    }
    
    public LignePropositionEcritureSycebnl(PropositionEcritureSycebnl proposition, Account compte, 
                                         String libelleLigne, BigDecimal debit, BigDecimal credit, 
                                         Integer ordre) {
        this.proposition = proposition;
        this.compte = compte;
        this.numeroCompte = compte.getAccountNumber();
        this.libelleCompte = compte.getName();
        this.libelleLigne = libelleLigne;
        this.debit = debit;
        this.credit = credit;
        this.ordre = ordre;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public PropositionEcritureSycebnl getProposition() { return proposition; }
    public void setProposition(PropositionEcritureSycebnl proposition) { this.proposition = proposition; }
    
    public Account getCompte() { return compte; }
    public void setCompte(Account compte) { this.compte = compte; }
    
    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }
    
    public String getLibelleCompte() { return libelleCompte; }
    public void setLibelleCompte(String libelleCompte) { this.libelleCompte = libelleCompte; }
    
    public String getLibelleLigne() { return libelleLigne; }
    public void setLibelleLigne(String libelleLigne) { this.libelleLigne = libelleLigne; }
    
    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }
    
    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }
    
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    
    public String getSensNormal() { return sensNormal; }
    public void setSensNormal(String sensNormal) { this.sensNormal = sensNormal; }
    
    public BigDecimal getConfianceLigne() { return confianceLigne; }
    public void setConfianceLigne(BigDecimal confianceLigne) { this.confianceLigne = confianceLigne; }
    
    public String getJustificationLigne() { return justificationLigne; }
    public void setJustificationLigne(String justificationLigne) { this.justificationLigne = justificationLigne; }
    
    public String getRegleAppliquee() { return regleAppliquee; }
    public void setRegleAppliquee(String regleAppliquee) { this.regleAppliquee = regleAppliquee; }
    
    public String getParametresRegle() { return parametresRegle; }
    public void setParametresRegle(String parametresRegle) { this.parametresRegle = parametresRegle; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
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
}
