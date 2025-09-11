package com.ecomptaia.entity;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.entity.FinancialPeriod;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EntitÃ© Balance Comptable - GÃ©nÃ©ration automatique de la balance
 * Conforme aux standards SYSCOHADA, IFRS, US GAAP, PCG
 */
@Entity
@Table(name = "balance_comptable")
public class BalanceComptable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_id", nullable = false)
    private FinancialPeriod exercice;
    
    @Column(name = "date_balance", nullable = false)
    private LocalDate dateBalance;
    
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;
    
    @Column(name = "standard_comptable", nullable = false, length = 20)
    private String standardComptable; // SYSCOHADA, IFRS, US_GAAP, PCG
    
    @Column(name = "devise", nullable = false, length = 3)
    private String devise = "XOF";
    
    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "GENERATED"; // DRAFT, GENERATED, VALIDATED, PUBLISHED
    
    @Column(name = "total_debit", precision = 15, scale = 2)
    private BigDecimal totalDebit = BigDecimal.ZERO;
    
    @Column(name = "total_credit", precision = 15, scale = 2)
    private BigDecimal totalCredit = BigDecimal.ZERO;
    
    @Column(name = "solde_debit", precision = 15, scale = 2)
    private BigDecimal soldeDebit = BigDecimal.ZERO;
    
    @Column(name = "solde_credit", precision = 15, scale = 2)
    private BigDecimal soldeCredit = BigDecimal.ZERO;
    
    @Column(name = "nombre_comptes")
    private Integer nombreComptes = 0;
    
    @Column(name = "nombre_mouvements")
    private Integer nombreMouvements = 0;
    
    @Column(name = "equilibre", nullable = false)
    private Boolean equilibre = false;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "validated_by")
    private Long validatedBy;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @Column(name = "observations", length = 1000)
    private String observations;
    
    @Column(name = "donnees_json", columnDefinition = "TEXT")
    private String donneesJson; // DonnÃ©es dÃ©taillÃ©es de la balance en JSON
    
    @Column(name = "version", length = 10)
    private String version = "1.0";
    
    // Constructeurs
    public BalanceComptable() {
        this.dateCreation = LocalDateTime.now();
        this.equilibre = false;
        this.statut = "GENERATED";
        this.devise = "XOF";
        this.version = "1.0";
    }
    
    public BalanceComptable(Company company, FinancialPeriod exercice, LocalDate dateBalance, String standardComptable) {
        this();
        this.company = company;
        this.exercice = exercice;
        this.dateBalance = dateBalance;
        this.standardComptable = standardComptable;
        this.devise = company.getCurrency();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public FinancialPeriod getExercice() { return exercice; }
    public void setExercice(FinancialPeriod exercice) { this.exercice = exercice; }
    
    public LocalDate getDateBalance() { return dateBalance; }
    public void setDateBalance(LocalDate dateBalance) { this.dateBalance = dateBalance; }
    
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    
    public String getStandardComptable() { return standardComptable; }
    public void setStandardComptable(String standardComptable) { this.standardComptable = standardComptable; }
    
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }
    
    public BigDecimal getTotalCredit() { return totalCredit; }
    public void setTotalCredit(BigDecimal totalCredit) { this.totalCredit = totalCredit; }
    
    public BigDecimal getSoldeDebit() { return soldeDebit; }
    public void setSoldeDebit(BigDecimal soldeDebit) { this.soldeDebit = soldeDebit; }
    
    public BigDecimal getSoldeCredit() { return soldeCredit; }
    public void setSoldeCredit(BigDecimal soldeCredit) { this.soldeCredit = soldeCredit; }
    
    public Integer getNombreComptes() { return nombreComptes; }
    public void setNombreComptes(Integer nombreComptes) { this.nombreComptes = nombreComptes; }
    
    public Integer getNombreMouvements() { return nombreMouvements; }
    public void setNombreMouvements(Integer nombreMouvements) { this.nombreMouvements = nombreMouvements; }
    
    public Boolean getEquilibre() { return equilibre; }
    public void setEquilibre(Boolean equilibre) { this.equilibre = equilibre; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Long getValidatedBy() { return validatedBy; }
    public void setValidatedBy(Long validatedBy) { this.validatedBy = validatedBy; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    public String getDonneesJson() { return donneesJson; }
    public void setDonneesJson(String donneesJson) { this.donneesJson = donneesJson; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    // MÃ©thodes utilitaires
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
    
    public boolean isDraft() {
        return "DRAFT".equals(statut);
    }
    
    /**
     * VÃ©rifier l'Ã©quilibre de la balance
     */
    public void verifierEquilibre() {
        if (totalDebit != null && totalCredit != null) {
            this.equilibre = totalDebit.compareTo(totalCredit) == 0;
        }
    }
    
    /**
     * Calculer le solde net
     */
    public BigDecimal getSoldeNet() {
        if (soldeDebit != null && soldeCredit != null) {
            return soldeDebit.subtract(soldeCredit);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Marquer comme validÃ©e
     */
    public void valider(Long userId) {
        this.statut = "VALIDATED";
        this.validatedBy = userId;
        this.dateValidation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }
    
    /**
     * Marquer comme publiÃ©e
     */
    public void publier() {
        this.statut = "PUBLISHED";
        this.dateModification = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "BalanceComptable{" +
                "id=" + id +
                ", dateBalance=" + dateBalance +
                ", standardComptable='" + standardComptable + '\'' +
                ", statut='" + statut + '\'' +
                ", equilibre=" + equilibre +
                ", totalDebit=" + totalDebit +
                ", totalCredit=" + totalCredit +
                '}';
    }
}



