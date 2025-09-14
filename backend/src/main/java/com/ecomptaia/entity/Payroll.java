package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "payrolls")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payroll_code", nullable = false, unique = true)
    private String payrollCode;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "month", nullable = false)
    private Integer month;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "pay_period", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayPeriod payPeriod;

    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "base_salary")
    private BigDecimal baseSalary;

    @Column(name = "hours_worked")
    private BigDecimal hoursWorked;

    @Column(name = "overtime_hours")
    private BigDecimal overtimeHours;

    @Column(name = "overtime_rate")
    private BigDecimal overtimeRate;

    @Column(name = "overtime_pay")
    private BigDecimal overtimePay;

    @Column(name = "bonus_amount")
    private BigDecimal bonusAmount;

    @Column(name = "bonus_description")
    private String bonusDescription;

    @Column(name = "allowance_amount")
    private BigDecimal allowanceAmount;

    @Column(name = "allowance_description")
    private String allowanceDescription;

    @Column(name = "commission_amount")
    private BigDecimal commissionAmount;

    @Column(name = "commission_rate")
    private BigDecimal commissionRate;

    @Column(name = "gross_salary")
    private BigDecimal grossSalary;

    @Column(name = "income_tax", precision = 15, scale = 2)
    private BigDecimal incomeTax;

    @Column(name = "social_security_tax")
    private BigDecimal socialSecurityTax;

    @Column(name = "health_insurance")
    private BigDecimal healthInsurance;

    @Column(name = "pension_contribution")
    private BigDecimal pensionContribution;

    @Column(name = "other_deductions")
    private BigDecimal otherDeductions;

    @Column(name = "deduction_description")
    private String deductionDescription;

    @Column(name = "social_charges", precision = 15, scale = 2)
    private BigDecimal socialCharges;
    
    @Column(name = "net_salary", precision = 15, scale = 2)
    private BigDecimal netSalary;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "payroll_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayrollStatus payrollStatus;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "bank_transfer_reference")
    private String bankTransferReference;

    @Column(name = "check_number")
    private String checkNumber;

    @Column(name = "cash_amount")
    private BigDecimal cashAmount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // Enums
    public enum PayPeriod {
        WEEKLY("Hebdomadaire"),
        BIWEEKLY("Bimensuel"),
        MONTHLY("Mensuel"),
        QUARTERLY("Trimestriel"),
        ANNUAL("Annuel");

        private final String description;

        PayPeriod(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PayrollStatus {
        DRAFT("Brouillon"),
        PENDING_APPROVAL("En attente d'approbation"),
        APPROVED("Approuvé"),
        PAID("Payé"),
        CANCELLED("Annulé"),
        REJECTED("Rejeté");

        private final String description;

        PayrollStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PaymentMethod {
        BANK_TRANSFER("Virement bancaire"),
        CHECK("Chèque"),
        CASH("Espèces"),
        MOBILE_MONEY("Mobile Money"),
        OTHER("Autre");

        private final String description;

        PaymentMethod(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Méthodes métier
    public void calculateGrossSalary() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (baseSalary != null) {
            total = total.add(baseSalary);
        }
        
        if (overtimePay != null) {
            total = total.add(overtimePay);
        }
        
        if (bonusAmount != null) {
            total = total.add(bonusAmount);
        }
        
        if (allowanceAmount != null) {
            total = total.add(allowanceAmount);
        }
        
        if (commissionAmount != null) {
            total = total.add(commissionAmount);
        }
        
        this.grossSalary = total;
    }

    public void calculateNetSalary() {
        if (grossSalary == null) {
            calculateGrossSalary();
        }
        
        BigDecimal totalDeductions = BigDecimal.ZERO;
        
        if (incomeTax != null) {
            totalDeductions = totalDeductions.add(incomeTax);
        }
        
        if (socialSecurityTax != null) {
            totalDeductions = totalDeductions.add(socialSecurityTax);
        }
        
        if (healthInsurance != null) {
            totalDeductions = totalDeductions.add(healthInsurance);
        }
        
        if (pensionContribution != null) {
            totalDeductions = totalDeductions.add(pensionContribution);
        }
        
        if (otherDeductions != null) {
            totalDeductions = totalDeductions.add(otherDeductions);
        }
        
        this.netSalary = grossSalary.subtract(totalDeductions);
    }

    public void calculateOvertimePay() {
        if (overtimeHours != null && overtimeRate != null) {
            this.overtimePay = overtimeHours.multiply(overtimeRate);
        }
    }

    public void calculateCommission() {
        if (baseSalary != null && commissionRate != null) {
            this.commissionAmount = baseSalary.multiply(commissionRate).divide(BigDecimal.valueOf(100));
        }
    }

    public boolean isPaid() {
        return payrollStatus == PayrollStatus.PAID;
    }

    public boolean isApproved() {
        return payrollStatus == PayrollStatus.APPROVED || payrollStatus == PayrollStatus.PAID;
    }

    public boolean isPending() {
        return payrollStatus == PayrollStatus.PENDING_APPROVAL;
    }

    public boolean isDraft() {
        return payrollStatus == PayrollStatus.DRAFT;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPayrollCode() { return payrollCode; }
    public void setPayrollCode(String payrollCode) { this.payrollCode = payrollCode; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public PayPeriod getPayPeriod() { return payPeriod; }
    public void setPayPeriod(PayPeriod payPeriod) { this.payPeriod = payPeriod; }

    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }

    public LocalDate getPeriodStartDate() { return periodStartDate; }
    public void setPeriodStartDate(LocalDate periodStartDate) { this.periodStartDate = periodStartDate; }

    public LocalDate getPeriodEndDate() { return periodEndDate; }
    public void setPeriodEndDate(LocalDate periodEndDate) { this.periodEndDate = periodEndDate; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public BigDecimal getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(BigDecimal hoursWorked) { this.hoursWorked = hoursWorked; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public BigDecimal getOvertimeRate() { return overtimeRate; }
    public void setOvertimeRate(BigDecimal overtimeRate) { this.overtimeRate = overtimeRate; }

    public BigDecimal getOvertimePay() { return overtimePay; }
    public void setOvertimePay(BigDecimal overtimePay) { this.overtimePay = overtimePay; }

    public BigDecimal getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }

    public String getBonusDescription() { return bonusDescription; }
    public void setBonusDescription(String bonusDescription) { this.bonusDescription = bonusDescription; }

    public BigDecimal getAllowanceAmount() { return allowanceAmount; }
    public void setAllowanceAmount(BigDecimal allowanceAmount) { this.allowanceAmount = allowanceAmount; }

    public String getAllowanceDescription() { return allowanceDescription; }
    public void setAllowanceDescription(String allowanceDescription) { this.allowanceDescription = allowanceDescription; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public BigDecimal getIncomeTax() { return incomeTax; }
    public void setIncomeTax(BigDecimal incomeTax) { this.incomeTax = incomeTax; }

    public BigDecimal getSocialSecurityTax() { return socialSecurityTax; }
    public void setSocialSecurityTax(BigDecimal socialSecurityTax) { this.socialSecurityTax = socialSecurityTax; }

    public BigDecimal getHealthInsurance() { return healthInsurance; }
    public void setHealthInsurance(BigDecimal healthInsurance) { this.healthInsurance = healthInsurance; }

    public BigDecimal getPensionContribution() { return pensionContribution; }
    public void setPensionContribution(BigDecimal pensionContribution) { this.pensionContribution = pensionContribution; }

    public BigDecimal getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(BigDecimal otherDeductions) { this.otherDeductions = otherDeductions; }

    public String getDeductionDescription() { return deductionDescription; }
    public void setDeductionDescription(String deductionDescription) { this.deductionDescription = deductionDescription; }

    public BigDecimal getSocialCharges() { return socialCharges; }
    public void setSocialCharges(BigDecimal socialCharges) { this.socialCharges = socialCharges; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    
    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public PayrollStatus getPayrollStatus() { return payrollStatus; }
    public void setPayrollStatus(PayrollStatus payrollStatus) { this.payrollStatus = payrollStatus; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBankTransferReference() { return bankTransferReference; }
    public void setBankTransferReference(String bankTransferReference) { this.bankTransferReference = bankTransferReference; }

    public String getCheckNumber() { return checkNumber; }
    public void setCheckNumber(String checkNumber) { this.checkNumber = checkNumber; }

    public BigDecimal getCashAmount() { return cashAmount; }
    public void setCashAmount(BigDecimal cashAmount) { this.cashAmount = cashAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public Long getEntrepriseId() { return entrepriseId; }
    public void setEntrepriseId(Long entrepriseId) { this.entrepriseId = entrepriseId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getter et Setter pour companyId
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
}







