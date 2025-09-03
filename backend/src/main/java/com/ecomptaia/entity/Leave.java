package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "leaves")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leave_code", nullable = false, unique = true)
    private String leaveCode;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "leave_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(name = "leave_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveStatus leaveStatus;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_days")
    private Integer totalDays;

    @Column(name = "half_day")
    private Boolean halfDay;

    @Column(name = "half_day_type")
    @Enumerated(EnumType.STRING)
    private HalfDayType halfDayType;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "medical_certificate")
    private String medicalCertificate;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    @Column(name = "destination")
    private String destination;

    @Column(name = "travel_insurance")
    private Boolean travelInsurance;

    @Column(name = "insurance_policy_number")
    private String insurancePolicyNumber;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approval_notes")
    private String approvalNotes;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "cancelled_by")
    private Long cancelledBy;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "paid_leave")
    private Boolean paidLeave;

    @Column(name = "salary_during_leave")
    private BigDecimal salaryDuringLeave;

    @Column(name = "salary_percentage")
    private BigDecimal salaryPercentage;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeur
    public Leave() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.leaveStatus = LeaveStatus.PENDING;
        this.paidLeave = true;
        this.salaryPercentage = BigDecimal.valueOf(100);
        this.halfDay = false;
    }

    // Enums
    public enum LeaveType {
        ANNUAL("Congé annuel"),
        SICK("Congé maladie"),
        MATERNITY("Congé maternité"),
        PATERNITY("Congé paternité"),
        BEREAVEMENT("Congé de deuil"),
        STUDY("Congé d'études"),
        TRAINING("Congé de formation"),
        PERSONAL("Congé personnel"),
        UNPAID("Congé sans solde"),
        COMPENSATORY("Congé compensatoire"),
        PUBLIC_HOLIDAY("Jour férié"),
        OTHER("Autre");

        private final String description;

        LeaveType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum LeaveStatus {
        PENDING("En attente"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté"),
        CANCELLED("Annulé"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminé");

        private final String description;

        LeaveStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum HalfDayType {
        MORNING("Matin"),
        AFTERNOON("Après-midi");

        private final String description;

        HalfDayType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Méthodes métier
    public void calculateTotalDays() {
        if (startDate != null && endDate != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
            this.totalDays = (int) days;
            
            if (halfDay != null && halfDay) {
                this.totalDays = (int) Math.ceil(this.totalDays * 0.5);
            }
        }
    }

    public boolean isApproved() {
        return leaveStatus == LeaveStatus.APPROVED;
    }

    public boolean isPending() {
        return leaveStatus == LeaveStatus.PENDING;
    }

    public boolean isRejected() {
        return leaveStatus == LeaveStatus.REJECTED;
    }

    public boolean isCancelled() {
        return leaveStatus == LeaveStatus.CANCELLED;
    }

    public boolean isInProgress() {
        return leaveStatus == LeaveStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return leaveStatus == LeaveStatus.COMPLETED;
    }

    public boolean isCurrentlyActive() {
        LocalDate today = LocalDate.now();
        return startDate != null && endDate != null && 
               !today.isBefore(startDate) && !today.isAfter(endDate) &&
               (leaveStatus == LeaveStatus.APPROVED || leaveStatus == LeaveStatus.IN_PROGRESS);
    }

    public boolean isFuture() {
        return startDate != null && startDate.isAfter(LocalDate.now());
    }

    public boolean isPast() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    public boolean requiresMedicalCertificate() {
        return leaveType == LeaveType.SICK || leaveType == LeaveType.MATERNITY;
    }

    public boolean isEmergencyLeave() {
        return leaveType == LeaveType.BEREAVEMENT || leaveType == LeaveType.SICK;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLeaveCode() { return leaveCode; }
    public void setLeaveCode(String leaveCode) { this.leaveCode = leaveCode; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LeaveStatus getLeaveStatus() { return leaveStatus; }
    public void setLeaveStatus(LeaveStatus leaveStatus) { this.leaveStatus = leaveStatus; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }

    public Boolean getHalfDay() { return halfDay; }
    public void setHalfDay(Boolean halfDay) { this.halfDay = halfDay; }

    public HalfDayType getHalfDayType() { return halfDayType; }
    public void setHalfDayType(HalfDayType halfDayType) { this.halfDayType = halfDayType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getMedicalCertificate() { return medicalCertificate; }
    public void setMedicalCertificate(String medicalCertificate) { this.medicalCertificate = medicalCertificate; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Boolean getTravelInsurance() { return travelInsurance; }
    public void setTravelInsurance(Boolean travelInsurance) { this.travelInsurance = travelInsurance; }

    public String getInsurancePolicyNumber() { return insurancePolicyNumber; }
    public void setInsurancePolicyNumber(String insurancePolicyNumber) { this.insurancePolicyNumber = insurancePolicyNumber; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getApprovalNotes() { return approvalNotes; }
    public void setApprovalNotes(String approvalNotes) { this.approvalNotes = approvalNotes; }

    public Long getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(Long rejectedBy) { this.rejectedBy = rejectedBy; }

    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Long getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(Long cancelledBy) { this.cancelledBy = cancelledBy; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public Boolean getPaidLeave() { return paidLeave; }
    public void setPaidLeave(Boolean paidLeave) { this.paidLeave = paidLeave; }

    public BigDecimal getSalaryDuringLeave() { return salaryDuringLeave; }
    public void setSalaryDuringLeave(BigDecimal salaryDuringLeave) { this.salaryDuringLeave = salaryDuringLeave; }

    public BigDecimal getSalaryPercentage() { return salaryPercentage; }
    public void setSalaryPercentage(BigDecimal salaryPercentage) { this.salaryPercentage = salaryPercentage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

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
}





