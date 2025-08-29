package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "social_declarations")
public class SocialDeclaration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "declaration_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeclarationType declarationType;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "employer_number")
    private String employerNumber;

    @Column(name = "total_employees")
    private Integer totalEmployees;

    @Column(name = "total_gross_salary")
    private BigDecimal totalGrossSalary;

    @Column(name = "total_net_salary")
    private BigDecimal totalNetSalary;

    @Column(name = "total_contributions")
    private BigDecimal totalContributions;

    @Column(name = "employer_contribution")
    private BigDecimal employerContribution;

    @Column(name = "employee_contribution")
    private BigDecimal employeeContribution;

    @Column(name = "platform_url")
    private String platformUrl;

    @Column(name = "api_endpoint")
    private String apiEndpoint;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "submission_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubmissionStatus submissionStatus;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "declaration_data", columnDefinition = "TEXT")
    private String declarationData; // JSON des données de déclaration

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments; // JSON des pièces jointes

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum DeclarationType {
        CNSS("Déclaration CNSS"),
        IPRES("Déclaration IPRES"),
        FDFP("Déclaration FDFP"),
        CNAMGS("Déclaration CNAMGS"),
        CNAV("Déclaration CNAV"),
        CAISSE_MALADIE("Déclaration Caisse Maladie"),
        CAISSE_FAMILLE("Déclaration Caisse Famille"),
        CAISSE_ACCIDENTS("Déclaration Caisse Accidents"),
        CAISSE_RETRAITE("Déclaration Caisse Retraite"),
        AUTRE("Autre déclaration sociale");

        private final String description;

        DeclarationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum SubmissionStatus {
        DRAFT("Brouillon"),
        READY("Prêt à soumettre"),
        SUBMITTED("Soumis"),
        VALIDATED("Validé"),
        REJECTED("Rejeté"),
        CORRECTED("Corrigé"),
        APPROVED("Approuvé");

        private final String description;

        SubmissionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PaymentStatus {
        PENDING("En attente"),
        PAID("Payé"),
        PARTIAL("Partiellement payé"),
        OVERDUE("En retard"),
        CANCELLED("Annulé");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public SocialDeclaration() {
        this.createdAt = LocalDateTime.now();
        this.submissionStatus = SubmissionStatus.DRAFT;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public SocialDeclaration(DeclarationType declarationType, Integer periodMonth, 
                           Integer periodYear, Long entrepriseId) {
        this();
        this.declarationType = declarationType;
        this.periodMonth = periodMonth;
        this.periodYear = periodYear;
        this.entrepriseId = entrepriseId;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeclarationType getDeclarationType() {
        return declarationType;
    }

    public void setDeclarationType(DeclarationType declarationType) {
        this.declarationType = declarationType;
    }

    public Integer getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(Integer periodMonth) {
        this.periodMonth = periodMonth;
    }

    public Integer getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(Integer periodYear) {
        this.periodYear = periodYear;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getEmployerNumber() {
        return employerNumber;
    }

    public void setEmployerNumber(String employerNumber) {
        this.employerNumber = employerNumber;
    }

    public Integer getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(Integer totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public BigDecimal getTotalGrossSalary() {
        return totalGrossSalary;
    }

    public void setTotalGrossSalary(BigDecimal totalGrossSalary) {
        this.totalGrossSalary = totalGrossSalary;
    }

    public BigDecimal getTotalNetSalary() {
        return totalNetSalary;
    }

    public void setTotalNetSalary(BigDecimal totalNetSalary) {
        this.totalNetSalary = totalNetSalary;
    }

    public BigDecimal getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(BigDecimal totalContributions) {
        this.totalContributions = totalContributions;
    }

    public BigDecimal getEmployerContribution() {
        return employerContribution;
    }

    public void setEmployerContribution(BigDecimal employerContribution) {
        this.employerContribution = employerContribution;
    }

    public BigDecimal getEmployeeContribution() {
        return employeeContribution;
    }

    public void setEmployeeContribution(BigDecimal employeeContribution) {
        this.employeeContribution = employeeContribution;
    }

    public String getPlatformUrl() {
        return platformUrl;
    }

    public void setPlatformUrl(String platformUrl) {
        this.platformUrl = platformUrl;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(LocalDateTime validationDate) {
        this.validationDate = validationDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getDeclarationData() {
        return declarationData;
    }

    public void setDeclarationData(String declarationData) {
        this.declarationData = declarationData;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}




