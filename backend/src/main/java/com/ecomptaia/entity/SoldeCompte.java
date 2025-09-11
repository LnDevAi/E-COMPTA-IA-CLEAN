package com.ecomptaia.entity;

import com.ecomptaia.entity.Account;
import com.ecomptaia.entity.BalanceComptable;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entité Solde Compte - Détail des soldes par compte dans la balance
 */
@Entity
@Table(name = "solde_compte")
public class SoldeCompte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id", nullable = false)
    private BalanceComptable balance;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account compte;
    
    @Column(name = "numero_compte", nullable = false, length = 20)
    private String numeroCompte;
    
    @Column(name = "libelle_compte", nullable = false, length = 200)
    private String libelleCompte;
    
    @Column(name = "solde_debut_debit", precision = 15, scale = 2)
    private BigDecimal soldeDebutDebit = BigDecimal.ZERO;
    
    @Column(name = "solde_debut_credit", precision = 15, scale = 2)
    private BigDecimal soldeDebutCredit = BigDecimal.ZERO;
    
    @Column(name = "mouvement_debit", precision = 15, scale = 2)
    private BigDecimal mouvementDebit = BigDecimal.ZERO;
    
    @Column(name = "mouvement_credit", precision = 15, scale = 2)
    private BigDecimal mouvementCredit = BigDecimal.ZERO;
    
    @Column(name = "solde_fin_debit", precision = 15, scale = 2)
    private BigDecimal soldeFinDebit = BigDecimal.ZERO;
    
    @Column(name = "solde_fin_credit", precision = 15, scale = 2)
    private BigDecimal soldeFinCredit = BigDecimal.ZERO;
    
    @Column(name = "solde_final", precision = 15, scale = 2)
    private BigDecimal soldeFinal = BigDecimal.ZERO;
    
    @Column(name = "sens_solde", length = 10)
    private String sensSolde; // DEBIT, CREDIT, NUL
    
    @Column(name = "nombre_mouvements")
    private Integer nombreMouvements = 0;
    
    @Column(name = "date_dernier_mouvement")
    private LocalDate dateDernierMouvement;
    
    @Column(name = "classe_compte")
    private Integer classeCompte;
    
    @Column(name = "type_compte", length = 50)
    private String typeCompte;
    
    @Column(name = "nature_compte", length = 20)
    private String natureCompte; // ACTIF, PASSIF, CHARGES, PRODUITS
    
    @Column(name = "ordre_affichage")
    private Integer ordreAffichage;
    
    // Constructeurs
    public SoldeCompte() {}
    
    public SoldeCompte(BalanceComptable balance, Account compte) {
        this.balance = balance;
        this.compte = compte;
        this.numeroCompte = compte.getAccountNumber();
        this.libelleCompte = compte.getName();
        this.classeCompte = getClasseFromAccountNumber(compte.getAccountNumber());
        this.typeCompte = compte.getType().toString();
        this.natureCompte = getNatureFromAccountNumber(compte.getAccountNumber());
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BalanceComptable getBalance() { return balance; }
    public void setBalance(BalanceComptable balance) { this.balance = balance; }
    
    public Account getCompte() { return compte; }
    public void setCompte(Account compte) { this.compte = compte; }
    
    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }
    
    public String getLibelleCompte() { return libelleCompte; }
    public void setLibelleCompte(String libelleCompte) { this.libelleCompte = libelleCompte; }
    
    public BigDecimal getSoldeDebutDebit() { return soldeDebutDebit; }
    public void setSoldeDebutDebit(BigDecimal soldeDebutDebit) { this.soldeDebutDebit = soldeDebutDebit; }
    
    public BigDecimal getSoldeDebutCredit() { return soldeDebutCredit; }
    public void setSoldeDebutCredit(BigDecimal soldeDebutCredit) { this.soldeDebutCredit = soldeDebutCredit; }
    
    public BigDecimal getMouvementDebit() { return mouvementDebit; }
    public void setMouvementDebit(BigDecimal mouvementDebit) { this.mouvementDebit = mouvementDebit; }
    
    public BigDecimal getMouvementCredit() { return mouvementCredit; }
    public void setMouvementCredit(BigDecimal mouvementCredit) { this.mouvementCredit = mouvementCredit; }
    
    public BigDecimal getSoldeFinDebit() { return soldeFinDebit; }
    public void setSoldeFinDebit(BigDecimal soldeFinDebit) { this.soldeFinDebit = soldeFinDebit; }
    
    public BigDecimal getSoldeFinCredit() { return soldeFinCredit; }
    public void setSoldeFinCredit(BigDecimal soldeFinCredit) { this.soldeFinCredit = soldeFinCredit; }
    
    public BigDecimal getSoldeFinal() { return soldeFinal; }
    public void setSoldeFinal(BigDecimal soldeFinal) { this.soldeFinal = soldeFinal; }
    
    public String getSensSolde() { return sensSolde; }
    public void setSensSolde(String sensSolde) { this.sensSolde = sensSolde; }
    
    public Integer getNombreMouvements() { return nombreMouvements; }
    public void setNombreMouvements(Integer nombreMouvements) { this.nombreMouvements = nombreMouvements; }
    
    public LocalDate getDateDernierMouvement() { return dateDernierMouvement; }
    public void setDateDernierMouvement(LocalDate dateDernierMouvement) { this.dateDernierMouvement = dateDernierMouvement; }
    
    public Integer getClasseCompte() { return classeCompte; }
    public void setClasseCompte(Integer classeCompte) { this.classeCompte = classeCompte; }
    
    public String getTypeCompte() { return typeCompte; }
    public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }
    
    public String getNatureCompte() { return natureCompte; }
    public void setNatureCompte(String natureCompte) { this.natureCompte = natureCompte; }
    
    public Integer getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(Integer ordreAffichage) { this.ordreAffichage = ordreAffichage; }
    
    // Méthodes utilitaires
    /**
     * Calculer le solde final
     */
    public void calculerSoldeFinal() {
        // Solde début
        BigDecimal soldeDebut = soldeDebutDebit.subtract(soldeDebutCredit);
        
        // Mouvements
        BigDecimal mouvementNet = mouvementDebit.subtract(mouvementCredit);
        
        // Solde final
        this.soldeFinal = soldeDebut.add(mouvementNet);
        
        // Déterminer le sens du solde
        if (soldeFinal.compareTo(BigDecimal.ZERO) > 0) {
            this.sensSolde = "DEBIT";
            this.soldeFinDebit = soldeFinal;
            this.soldeFinCredit = BigDecimal.ZERO;
        } else if (soldeFinal.compareTo(BigDecimal.ZERO) < 0) {
            this.sensSolde = "CREDIT";
            this.soldeFinDebit = BigDecimal.ZERO;
            this.soldeFinCredit = soldeFinal.abs();
        } else {
            this.sensSolde = "NUL";
            this.soldeFinDebit = BigDecimal.ZERO;
            this.soldeFinCredit = BigDecimal.ZERO;
        }
    }
    
    /**
     * Déterminer la classe du compte à partir du numéro
     */
    private Integer getClasseFromAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 1) {
            return 0;
        }
        try {
            return Integer.parseInt(accountNumber.substring(0, 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Déterminer la nature du compte à partir du numéro
     */
    private String getNatureFromAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 1) {
            return "INCONNU";
        }
        
        char firstChar = accountNumber.charAt(0);
        switch (firstChar) {
            case '1': return "PASSIF";
            case '2': return "ACTIF";
            case '3': return "ACTIF";
            case '4': return "PASSIF";
            case '5': return "PASSIF";
            case '6': return "CHARGES";
            case '7': return "PRODUITS";
            default: return "INCONNU";
        }
    }
    
    /**
     * Vérifier si le compte a des mouvements
     */
    public boolean hasMouvements() {
        return nombreMouvements != null && nombreMouvements > 0;
    }
    
    /**
     * Vérifier si le compte a un solde
     */
    public boolean hasSolde() {
        return soldeFinal != null && soldeFinal.compareTo(BigDecimal.ZERO) != 0;
    }
    
    @Override
    public String toString() {
        return "SoldeCompte{" +
                "id=" + id +
                ", numeroCompte='" + numeroCompte + '\'' +
                ", libelleCompte='" + libelleCompte + '\'' +
                ", soldeFinal=" + soldeFinal +
                ", sensSolde='" + sensSolde + '\'' +
                '}';
    }
}



