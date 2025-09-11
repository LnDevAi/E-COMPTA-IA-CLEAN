package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "balance_comptable")
public class BalanceComptable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_id")
    private FinancialPeriod exercice;
    
    @Column(name = "date_balance")
    private LocalDate dateBalance;
    
    @Column(name = "standard_comptable")
    private String standardComptable;
    
    @Column(name = "total_debit", precision = 15, scale = 2)
    private BigDecimal totalDebit;
    
    @Column(name = "total_credit", precision = 15, scale = 2)
    private BigDecimal totalCredit;
    
    @Column(name = "solde_debit", precision = 15, scale = 2)
    private BigDecimal soldeDebit;
    
    @Column(name = "solde_credit", precision = 15, scale = 2)
    private BigDecimal soldeCredit;
    
    @Column(name = "equilibre")
    private Boolean equilibre;
    
    @Column(name = "statut")
    private String statut;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "validated_by")
    private Long validatedBy;
    
    @Column(name = "date_debut")
    private LocalDate dateDebut;
    
    @Column(name = "date_fin")
    private LocalDate dateFin;
    
    @Column(name = "devise")
    private String devise;
    
    public BalanceComptable() { }
    
    public BalanceComptable(Company company, FinancialPeriod exercice, LocalDate dateBalance, String standardComptable) {
        this.company = company;
        this.exercice = exercice;
        this.dateBalance = dateBalance;
        this.standardComptable = standardComptable;
        this.equilibre = false;
        this.totalDebit = BigDecimal.ZERO;
        this.totalCredit = BigDecimal.ZERO;
        this.soldeDebit = BigDecimal.ZERO;
        this.soldeCredit = BigDecimal.ZERO;
    }
    
    public void verifierEquilibre() {
        this.equilibre = this.totalDebit != null && this.totalCredit != null && this.totalDebit.compareTo(this.totalCredit) == 0;
    }
    
    public void valider(Long userId) {
        this.statut = "VALIDATED";
        this.validatedBy = userId;
        this.dateValidation = LocalDateTime.now();
    }
    
    public void publier() {
        this.statut = "PUBLISHED";
        this.dateModification = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public FinancialPeriod getExercice() { return exercice; }
    public void setExercice(FinancialPeriod exercice) { this.exercice = exercice; }
    public LocalDate getDateBalance() { return dateBalance; }
    public void setDateBalance(LocalDate dateBalance) { this.dateBalance = dateBalance; }
    public String getStandardComptable() { return standardComptable; }
    public void setStandardComptable(String standardComptable) { this.standardComptable = standardComptable; }
    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }
    public BigDecimal getTotalCredit() { return totalCredit; }
    public void setTotalCredit(BigDecimal totalCredit) { this.totalCredit = totalCredit; }
    public BigDecimal getSoldeDebit() { return soldeDebit; }
    public void setSoldeDebit(BigDecimal soldeDebit) { this.soldeDebit = soldeDebit; }
    public BigDecimal getSoldeCredit() { return soldeCredit; }
    public void setSoldeCredit(BigDecimal soldeCredit) { this.soldeCredit = soldeCredit; }
    public Boolean getEquilibre() { return equilibre; }
    public void setEquilibre(Boolean equilibre) { this.equilibre = equilibre; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Long getValidatedBy() { return validatedBy; }
    public void setValidatedBy(Long validatedBy) { this.validatedBy = validatedBy; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
}



