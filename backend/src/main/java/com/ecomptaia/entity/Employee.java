package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "social_security_number")
    private String socialSecurityNumber;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "contract_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Column(name = "employment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    @Column(name = "department")
    private String department;

    @Column(name = "position")
    private String position;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "base_salary")
    private BigDecimal baseSalary;

    @Column(name = "salary_currency")
    private String salaryCurrency;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills; // JSON des compétences

    @Column(name = "certifications", columnDefinition = "TEXT")
    private String certifications; // JSON des certifications

    @Column(name = "performance_rating")
    private Integer performanceRating; // 1-5

    @Column(name = "last_evaluation_date")
    private LocalDate lastEvaluationDate;

    @Column(name = "next_evaluation_date")
    private LocalDate nextEvaluationDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "termination_reason")
    private String terminationReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeur
    public Employee() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.employmentStatus = EmploymentStatus.ACTIVE;
        this.performanceRating = 3;
    }

    // Enums
    public enum Gender {
        MALE("Masculin"),
        FEMALE("Féminin"),
        OTHER("Autre");

        private final String description;

        Gender(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ContractType {
        CDI("Contrat à durée indéterminée"),
        PERMANENT("Contrat permanent"),
        FIXED_TERM("Contrat à durée déterminée"),
        TEMPORARY("Contrat temporaire"),
        INTERNSHIP("Stage"),
        FREELANCE("Freelance");

        private final String description;

        ContractType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum EmploymentStatus {
        ACTIVE("Actif"),
        ON_LEAVE("En congé"),
        SUSPENDED("Suspendu"),
        TERMINATED("Terminé"),
        RETIRED("Retraité");

        private final String description;

        EmploymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Méthodes métier
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return employmentStatus == EmploymentStatus.ACTIVE;
    }

    public boolean isOnLeave() {
        return employmentStatus == EmploymentStatus.ON_LEAVE;
    }

    public boolean isTerminated() {
        return employmentStatus == EmploymentStatus.TERMINATED;
    }

    public Long getYearsOfService() {
        if (hireDate == null) return 0L;
        return java.time.temporal.ChronoUnit.YEARS.between(hireDate, LocalDate.now());
    }

    public boolean needsEvaluation() {
        return nextEvaluationDate != null && nextEvaluationDate.isBefore(LocalDate.now());
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getSocialSecurityNumber() { return socialSecurityNumber; }
    public void setSocialSecurityNumber(String socialSecurityNumber) { this.socialSecurityNumber = socialSecurityNumber; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }

    public EmploymentStatus getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public String getSalaryCurrency() { return salaryCurrency; }
    public void setSalaryCurrency(String salaryCurrency) { this.salaryCurrency = salaryCurrency; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }

    public Integer getPerformanceRating() { return performanceRating; }
    public void setPerformanceRating(Integer performanceRating) { this.performanceRating = performanceRating; }

    public LocalDate getLastEvaluationDate() { return lastEvaluationDate; }
    public void setLastEvaluationDate(LocalDate lastEvaluationDate) { this.lastEvaluationDate = lastEvaluationDate; }

    public LocalDate getNextEvaluationDate() { return nextEvaluationDate; }
    public void setNextEvaluationDate(LocalDate nextEvaluationDate) { this.nextEvaluationDate = nextEvaluationDate; }

    public LocalDate getTerminationDate() { return terminationDate; }
    public void setTerminationDate(LocalDate terminationDate) { this.terminationDate = terminationDate; }

    public String getTerminationReason() { return terminationReason; }
    public void setTerminationReason(String terminationReason) { this.terminationReason = terminationReason; }

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





