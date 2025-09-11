package com.ecomptaia.sycebnl.entity;

import com.ecomptaia.security.entity.Company;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * EntitÃ© Organisation SYCEBNL - ReprÃ©sente une organisation Ã  but non lucratif
 * Conforme aux normes SYCEBNL-OHADA avec gestion des systÃ¨mes Normal et Minimal
 */
@Entity
@Table(name = "sycebnl_organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SycebnlOrganization {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    // === INFORMATIONS GÃ‰NÃ‰RALES ===
    @Column(name = "organization_name", nullable = false)
    private String organizationName;
    
    @Column(name = "legal_form")
    private String legalForm; // Association, Fondation, ONG, etc.
    
    @Column(name = "registration_number")
    private String registrationNumber;
    
    @Column(name = "tax_identification_number")
    private String taxIdentificationNumber;
    
    @Column(name = "organization_type")
    @Enumerated(EnumType.STRING)
    private OrganizationType organizationType;
    
    // === SYSTÃˆME COMPTABLE SYCEBNL ===
    @Column(name = "accounting_system", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountingSystem accountingSystem;
    
    @Column(name = "fiscal_year_start")
    private String fiscalYearStart; // Format: "MM-DD"
    
    @Column(name = "fiscal_year_end")
    private String fiscalYearEnd; // Format: "MM-DD"
    
    @Column(name = "base_currency", nullable = false)
    private String baseCurrency; // XOF, EUR, USD, etc.
    
    @Column(name = "reporting_currency")
    private String reportingCurrency;
    
    // === SEUILS SYCEBNL ===
    @Column(name = "annual_revenue", precision = 15, scale = 2)
    private BigDecimal annualRevenue;
    
    @Column(name = "employee_count")
    private Integer employeeCount;
    
    @Column(name = "total_assets", precision = 15, scale = 2)
    private BigDecimal totalAssets;
    
    @Column(name = "meets_normal_system_criteria")
    private Boolean meetsNormalSystemCriteria;
    
    // === INFORMATIONS LÃ‰GALES ===
    @Column(name = "legal_address", columnDefinition = "TEXT")
    private String legalAddress;
    
    @Column(name = "headquarters_address", columnDefinition = "TEXT")
    private String headquartersAddress;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "website")
    private String website;
    
    // === GESTION DES FONDS ===
    @Column(name = "fund_restriction_policy", columnDefinition = "TEXT")
    private String fundRestrictionPolicy;
    
    @Column(name = "donor_restriction_tracking")
    @Builder.Default
    private Boolean donorRestrictionTracking = true;
    
    @Column(name = "temporarily_restricted_funds", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal temporarilyRestrictedFunds = BigDecimal.ZERO;
    
    @Column(name = "permanently_restricted_funds", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal permanentlyRestrictedFunds = BigDecimal.ZERO;
    
    @Column(name = "unrestricted_funds", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal unrestrictedFunds = BigDecimal.ZERO;
    
    // === CONFORMITÃ‰ OHADA ===
    @Column(name = "ohada_compliance_status")
    @Enumerated(EnumType.STRING)
    private ComplianceStatus ohadaComplianceStatus;
    
    @Column(name = "last_compliance_audit")
    private LocalDate lastComplianceAudit;
    
    @Column(name = "next_compliance_audit")
    private LocalDate nextComplianceAudit;
    
    @Column(name = "auditor_name")
    private String auditorName;
    
    @Column(name = "auditor_license_number")
    private String auditorLicenseNumber;
    
    // === CONFIGURATION SPÃ‰CIFIQUE ONG ===
    @Column(name = "mission_statement", columnDefinition = "TEXT")
    private String missionStatement;
    
    @Column(name = "program_areas")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> programAreas = Map.of();
    
    @Column(name = "geographic_scope")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> geographicScope = Map.of();
    
    @Column(name = "beneficiary_count")
    private Integer beneficiaryCount;
    
    @Column(name = "volunteer_count")
    private Integer volunteerCount;
    
    // === MÃ‰TADONNÃ‰ES ===
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "approved_by")
    private String approvedBy;
    
    @Column(name = "approval_date")
    private LocalDate approvalDate;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // === ENUMS ===
    
    /**
     * Types d'organisations Ã  but non lucratif
     */
    public enum OrganizationType {
        ASSOCIATION("Association"),
        FOUNDATION("Fondation"),
        NGO("Organisation Non Gouvernementale"),
        COOPERATIVE("CoopÃ©rative"),
        MUTUAL("Mutuelle"),
        RELIGIOUS_ORGANIZATION("Organisation religieuse"),
        EDUCATIONAL_INSTITUTION("Institution Ã©ducative"),
        HEALTH_INSTITUTION("Institution de santÃ©"),
        CULTURAL_ORGANIZATION("Organisation culturelle"),
        SPORTS_ORGANIZATION("Organisation sportive"),
        PROFESSIONAL_ASSOCIATION("Association professionnelle"),
        OTHER("Autre");
        
        private final String description;
        
        OrganizationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * SystÃ¨mes comptables SYCEBNL
     */
    public enum AccountingSystem {
        NORMAL("SystÃ¨me Normal (SN)"),
        MINIMAL("SystÃ¨me Minimal de TrÃ©sorerie (SMT)"),
        TRANSITIONAL("SystÃ¨me transitoire");
        
        private final String description;
        
        AccountingSystem(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Statut de conformitÃ© OHADA
     */
    public enum ComplianceStatus {
        COMPLIANT("Conforme"),
        NON_COMPLIANT("Non conforme"),
        UNDER_REVIEW("En cours de rÃ©vision"),
        PENDING_AUDIT("Audit en attente"),
        NEEDS_IMPROVEMENT("AmÃ©lioration nÃ©cessaire");
        
        private final String description;
        
        ComplianceStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

