package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_periods")
public class FinancialPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false)
    private String periodName; // Nom de l'exercice (ex: "Exercice 2024")

    @Column(nullable = false)
    private LocalDate startDate; // Date de début d'exercice

    @Column(nullable = false)
    private LocalDate endDate; // Date de fin d'exercice

    @Column(nullable = false, length = 20)
    private String status; // OPEN, CLOSED, LOCKED

    @Column
    private LocalDateTime closedAt; // Date de clôture

    @Column
    private Long closedBy; // ID de l'utilisateur qui a clôturé

    @Column
    private Boolean isCurrent = false; // Si c'est l'exercice courant

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays

    @Column(nullable = false, length = 20)
    private String accountingStandard; // Standard comptable

    @Column(length = 500)
    private String notes; // Notes additionnelles

    @Column
    private Boolean isLocked = false; // Si l'exercice est verrouillé

    @Column
    private LocalDateTime lockedAt; // Date de verrouillage

    @Column
    private Long lockedBy; // ID de l'utilisateur qui a verrouillé

    @Column
    private String lockReason; // Raison du verrouillage

    // Constructeurs
    public FinancialPeriod() {}

    public FinancialPeriod(Long companyId, String periodName, LocalDate startDate, 
                          LocalDate endDate, String countryCode, String accountingStandard) {
        this.companyId = companyId;
        this.periodName = periodName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.status = "OPEN";
        this.isCurrent = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getPeriodName() { return periodName; }
    public void setPeriodName(String periodName) { this.periodName = periodName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public Long getClosedBy() { return closedBy; }
    public void setClosedBy(Long closedBy) { this.closedBy = closedBy; }

    public Boolean getIsCurrent() { return isCurrent; }
    public void setIsCurrent(Boolean isCurrent) { this.isCurrent = isCurrent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }

    public LocalDateTime getLockedAt() { return lockedAt; }
    public void setLockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; }

    public Long getLockedBy() { return lockedBy; }
    public void setLockedBy(Long lockedBy) { this.lockedBy = lockedBy; }

    public String getLockReason() { return lockReason; }
    public void setLockReason(String lockReason) { this.lockReason = lockReason; }

    @Override
    public String toString() {
        return periodName + " (" + startDate + " - " + endDate + ") - " + status;
    }
}





