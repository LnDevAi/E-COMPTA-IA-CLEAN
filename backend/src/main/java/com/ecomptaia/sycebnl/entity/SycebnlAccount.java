ackage com.ecomptaia.sycebnl.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité Compte SYCEBNL - Plan comptable conforme aux normes SYCEBNL-OHADA
 * Gestion des comptes pour organisations à but non lucratif
 */
@Entity
@Table(name = "sycebnl_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SycebnlAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private SycebnlOrganization organization;
    
    // === INFORMATIONS DU COMPTE ===
    @Column(name = "account_code", nullable = false)
    private String accountCode;
    
    @Column(name = "account_name", nullable = false)
    private String accountName;
    
    @Column(name = "account_description", columnDefinition = "TEXT")
    private String accountDescription;
    
    // === CLASSIFICATION SYCEBNL ===
    @Column(name = "account_class", nullable = false)
    private Integer accountClass; // 1-7 selon SYCEBNL
    
    @Column(name = "account_category")
    @Enumerated(EnumType.STRING)
    private AccountCategory accountCategory;
    
    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    
    // === HIÉRARCHIE ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private SycebnlAccount parentAccount;
    
    @Column(name = "account_level")
    private Integer accountLevel; // 1 = Classe, 2 = Sous-classe, 3 = Compte, 4 = Sous-compte
    
    @Column(name = "is_group_account")
    @Builder.Default
    private Boolean isGroupAccount = false;
    
    // === SOLDE ET MOUVEMENTS ===
    @Column(name = "opening_debit_balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal openingDebitBalance = BigDecimal.ZERO;
    
    @Column(name = "opening_credit_balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal openingCreditBalance = BigDecimal.ZERO;
    
    @Column(name = "current_debit_balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal currentDebitBalance = BigDecimal.ZERO;
    
    @Column(name = "current_credit_balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal currentCreditBalance = BigDecimal.ZERO;
    
    @Column(name = "closing_debit_balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal closingDebitBalance = BigDecimal.ZERO;
    
    @Column(name = "closing_credit_balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal closingCreditBalance = BigDecimal.ZERO;
    
    // === RESTRICTIONS ET AFFECTATIONS ===
    @Column(name = "is_restricted")
    @Builder.Default
    private Boolean isRestricted = false;
    
    @Column(name = "restriction_type")
    @Enumerated(EnumType.STRING)
    private RestrictionType restrictionType;
    
    @Column(name = "restriction_description", columnDefinition = "TEXT")
    private String restrictionDescription;
    
    @Column(name = "donor_restriction")
    private String donorRestriction;
    
    @Column(name = "time_restriction")
    private String timeRestriction;
    
    @Column(name = "purpose_restriction")
    private String purposeRestriction;
    
    // === AMORTISSEMENTS ===
    @Column(name = "is_depreciable")
    @Builder.Default
    private Boolean isDepreciable = false;
    
    @Column(name = "depreciation_method")
    @Enumerated(EnumType.STRING)
    private DepreciationMethod depreciationMethod;
    
    @Column(name = "useful_life_years")
    private Integer usefulLifeYears;
    
    @Column(name = "depreciation_rate", precision = 5, scale = 2)
    private BigDecimal depreciationRate;
    
    @Column(name = "accumulated_depreciation", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal accumulatedDepreciation = BigDecimal.ZERO;
    
    // === CONFIGURATION ===
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "is_system_account")
    @Builder.Default
    private Boolean isSystemAccount = false;
    
    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = false;
    
    @Column(name = "is_cash_account")
    @Builder.Default
    private Boolean isCashAccount = false;
    
    @Column(name = "is_bank_account")
    @Builder.Default
    private Boolean isBankAccount = false;
    
    // === MÉTADONNÉES ===
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "approved_by")
    private String approvedBy;
    
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // === ENUMS ===
    
    /**
     * Catégories de comptes SYCEBNL
     */
    public enum AccountCategory {
        ASSETS("Actifs"),
        LIABILITIES("Passifs"),
        EQUITY("Capitaux propres"),
        REVENUE("Produits"),
        EXPENSES("Charges"),
        OFF_BALANCE("Hors bilan");
        
        private final String description;
        
        AccountCategory(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Types de comptes
     */
    public enum AccountType {
        ASSET("Actif"),
        LIABILITY("Passif"),
        EQUITY("Capitaux propres"),
        REVENUE("Produit"),
        EXPENSE("Charge"),
        CONTRA_ASSET("Contre-actif"),
        CONTRA_REVENUE("Contre-produit");
        
        private final String description;
        
        AccountType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Types de restrictions
     */
    public enum RestrictionType {
        UNRESTRICTED("Sans restriction"),
        TEMPORARILY_RESTRICTED("Restriction temporaire"),
        PERMANENTLY_RESTRICTED("Restriction permanente"),
        DONOR_RESTRICTED("Restriction du donateur"),
        TIME_RESTRICTED("Restriction temporelle"),
        PURPOSE_RESTRICTED("Restriction d'usage");
        
        private final String description;
        
        RestrictionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Méthodes d'amortissement
     */
    public enum DepreciationMethod {
        STRAIGHT_LINE("Linéaire"),
        DECLINING_BALANCE("Dégressif"),
        SUM_OF_YEARS("Somme des années"),
        UNITS_OF_PRODUCTION("Unités de production"),
        NONE("Aucun amortissement");
        
        private final String description;
        
        DepreciationMethod(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}



