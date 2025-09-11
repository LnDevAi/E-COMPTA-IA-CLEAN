package com.ecomptaia.sycebnl.entity;

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
 * Entité Membre SYCEBNL - Gestion des membres d'organisations à but non lucratif
 * Intègre CRM et analytics pour la gestion intelligente des membres
 */
@Entity
@Table(name = "sycebnl_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SycebnlMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private SycebnlOrganization organization;
    
    // === INFORMATIONS PERSONNELLES ===
    @Column(name = "member_number", unique = true)
    private String memberNumber;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "nationality")
    private String nationality;
    
    @Column(name = "id_number")
    private String idNumber;
    
    @Column(name = "id_type")
    @Enumerated(EnumType.STRING)
    private IdType idType;
    
    // === ADRESSE ===
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "region")
    private String region;
    
    // === STATUT MEMBRE ===
    @Column(name = "membership_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;
    
    @Column(name = "member_type")
    @Enumerated(EnumType.STRING)
    private MemberType memberType;
    
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;
    
    @Column(name = "membership_expiry_date")
    private LocalDate membershipExpiryDate;
    
    @Column(name = "last_renewal_date")
    private LocalDate lastRenewalDate;
    
    // === COTISATIONS ===
    @Column(name = "annual_contribution", precision = 10, scale = 2)
    private BigDecimal annualContribution;
    
    @Column(name = "contribution_frequency")
    @Enumerated(EnumType.STRING)
    private ContributionFrequency contributionFrequency;
    
    @Column(name = "total_contributions_paid", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalContributionsPaid = BigDecimal.ZERO;
    
    @Column(name = "outstanding_contributions", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal outstandingContributions = BigDecimal.ZERO;
    
    @Column(name = "last_contribution_date")
    private LocalDate lastContributionDate;
    
    @Column(name = "next_contribution_due")
    private LocalDate nextContributionDue;
    
    // === FONCTIONS ET RESPONSABILITÉS ===
    @Column(name = "position_title")
    private String positionTitle;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "is_board_member")
    @Builder.Default
    private Boolean isBoardMember = false;
    
    @Column(name = "is_volunteer")
    @Builder.Default
    private Boolean isVolunteer = false;
    
    @Column(name = "volunteer_hours_per_month")
    private Integer volunteerHoursPerMonth;
    
    @Column(name = "skills")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> skills = Map.of();
    
    @Column(name = "interests")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> interests = Map.of();
    
    // === ANALYTICS ET CRM ===
    @Column(name = "engagement_score")
    private Integer engagementScore; // 0-100
    
    @Column(name = "lifetime_value", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal lifetimeValue = BigDecimal.ZERO;
    
    @Column(name = "churn_probability", precision = 5, scale = 4)
    private BigDecimal churnProbability;
    
    @Column(name = "member_segment")
    @Enumerated(EnumType.STRING)
    private MemberSegment memberSegment;
    
    @Column(name = "communication_preferences")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> communicationPreferences = Map.of();
    
    @Column(name = "email_opt_in")
    @Builder.Default
    private Boolean emailOptIn = true;
    
    @Column(name = "sms_opt_in")
    @Builder.Default
    private Boolean smsOptIn = true;
    
    @Column(name = "language_preference")
    @Builder.Default
    private String languagePreference = "fr";
    
    // === MÉTADONNÉES ===
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // === ENUMS ===
    
    /**
     * Genre
     */
    public enum Gender {
        MALE("Masculin"),
        FEMALE("Féminin"),
        OTHER("Autre"),
        PREFER_NOT_TO_SAY("Préfère ne pas dire");
        
        private final String description;
        
        Gender(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Types de pièces d'identité
     */
    public enum IdType {
        NATIONAL_ID("Carte d'identité nationale"),
        PASSPORT("Passeport"),
        DRIVER_LICENSE("Permis de conduire"),
        BIRTH_CERTIFICATE("Acte de naissance"),
        OTHER("Autre");
        
        private final String description;
        
        IdType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Statut d'adhésion
     */
    public enum MembershipStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        SUSPENDED("Suspendu"),
        EXPIRED("Expiré"),
        PENDING("En attente"),
        CANCELLED("Annulé"),
        HONORARY("Honoraire");
        
        private final String description;
        
        MembershipStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Types de membres
     */
    public enum MemberType {
        INDIVIDUAL("Membre individuel"),
        CORPORATE("Membre corporatif"),
        STUDENT("Membre étudiant"),
        SENIOR("Membre senior"),
        LIFETIME("Membre à vie"),
        FOUNDING("Membre fondateur"),
        BENEFACTOR("Bienfaiteur"),
        VOLUNTEER("Bénévole");
        
        private final String description;
        
        MemberType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Fréquence de cotisation
     */
    public enum ContributionFrequency {
        ANNUAL("Annuelle"),
        SEMI_ANNUAL("Semestrielle"),
        QUARTERLY("Trimestrielle"),
        MONTHLY("Mensuelle"),
        ONE_TIME("Unique"),
        CUSTOM("Personnalisée");
        
        private final String description;
        
        ContributionFrequency(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Segments de membres basés sur l'engagement
     */
    public enum MemberSegment {
        CHAMPION("Champion"),
        LOYAL_SUPPORTER("Soutien fidèle"),
        POTENTIAL_LOYALIST("Potentiel fidèle"),
        NEW_CUSTOMER("Nouveau membre"),
        AT_RISK("À risque"),
        CANT_LOSE_THEM("Ne peut pas les perdre"),
        HIBERNATING("En hibernation"),
        LOST("Perdu");
        
        private final String description;
        
        MemberSegment(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
