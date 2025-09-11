package com.ecomptaia.crm.entity;

import com.ecomptaia.security.entity.Company;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * EntitÃ© Marketing Campaign - Campagnes marketing multi-canaux
 * Supporte Email, SMS, Social Media avec analytics intÃ©grÃ©es
 */
@Entity
@Table(name = "marketing_campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MarketingCampaign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(name = "campaign_name", nullable = false)
    private String campaignName;
    
    @Column(name = "campaign_code", unique = true)
    private String campaignCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_type", nullable = false)
    private CampaignType campaignType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CampaignStatus status;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // === CIBLAGE ===
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_segments", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> targetSegments = new ArrayList<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_criteria", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> targetCriteria = new HashMap<>();
    
    @Column(name = "estimated_reach")
    private Integer estimatedReach;
    
    // === CONTENU ===
    @Column(name = "subject_line")
    private String subjectLine;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_variants", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> contentVariants = new HashMap<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attachments", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> attachments = new ArrayList<>();
    
    // === PLANNING ===
    @Column(name = "scheduled_start")
    private LocalDateTime scheduledStart;
    
    @Column(name = "scheduled_end")
    private LocalDateTime scheduledEnd;
    
    @Column(name = "actual_start")
    private LocalDateTime actualStart;
    
    @Column(name = "actual_end")
    private LocalDateTime actualEnd;
    
    // === BUDGET ===
    @Column(name = "budget_allocated", precision = 15, scale = 2)
    private BigDecimal budgetAllocated;
    
    @Column(name = "cost_per_message", precision = 10, scale = 4)
    private BigDecimal costPerMessage;
    
    @Column(name = "total_cost", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalCost = BigDecimal.ZERO;
    
    // === MÃ‰TRIQUES ===
    @Column(name = "messages_sent")
    @Builder.Default
    private Integer messagesSent = 0;
    
    @Column(name = "messages_delivered")
    @Builder.Default
    private Integer messagesDelivered = 0;
    
    @Column(name = "messages_opened")
    @Builder.Default
    private Integer messagesOpened = 0;
    
    @Column(name = "clicks_count")
    @Builder.Default
    private Integer clicksCount = 0;
    
    @Column(name = "conversions_count")
    @Builder.Default
    private Integer conversionsCount = 0;
    
    @Column(name = "revenue_generated", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal revenueGenerated = BigDecimal.ZERO;
    
    @Column(name = "roi_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal roiPercentage = BigDecimal.ZERO;
    
    // === CONFIGURATION ===
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "channel_config", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> channelConfig = new HashMap<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "automation_rules", columnDefinition = "jsonb")
    @Builder.Default
    private List<Map<String, Object>> automationRules = new ArrayList<>();
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // === ENUMS ===
    
    /**
     * Types de campagnes marketing
     */
    public enum CampaignType {
        EMAIL("Email marketing"),
        SMS("SMS marketing"),
        SOCIAL("RÃ©seaux sociaux"),
        MULTI_CHANNEL("Multi-canal"),
        NURTURE("Campagne de nurturing"),
        RETENTION("RÃ©tention client"),
        ACQUISITION("Acquisition"),
        REACTIVATION("RÃ©activation");
        
        private final String description;
        
        CampaignType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Statuts des campagnes
     */
    public enum CampaignStatus {
        DRAFT("Brouillon"),
        SCHEDULED("ProgrammÃ©e"),
        RUNNING("En cours"),
        PAUSED("En pause"),
        COMPLETED("TerminÃ©e"),
        CANCELLED("AnnulÃ©e"),
        FAILED("Ã‰chouÃ©e");
        
        private final String description;
        
        CampaignStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

