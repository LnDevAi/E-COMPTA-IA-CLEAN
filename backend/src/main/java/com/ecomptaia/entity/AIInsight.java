ackage com.ecomptaia.entity;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.security.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * EntitÃ© pour les insights gÃ©nÃ©rÃ©s par l'IA
 * RÃ©volutionnaire vs TOMPRO - Intelligence artificielle native
 */
@Entity
@Table(name = "ai_insights")
public class AIInsight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InsightType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InsightCategory category;
    
    @Column(nullable = false)
    private Integer confidence; // 0-100
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImpactLevel impact;
    
    @Column(nullable = false)
    private Integer priority; // 1-10
    
    @Column(nullable = false)
    private Boolean actionable = false;
    
    @ElementCollection
    @CollectionTable(name = "ai_insight_actions", joinColumns = @JoinColumn(name = "insight_id"))
    @Column(name = "action", columnDefinition = "TEXT")
    private List<String> suggestedActions = new ArrayList<>();
    
    @Column(columnDefinition = "JSON")
    private String data; // DonnÃ©es JSON associÃ©es
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime expiresAt;
    
    @Column
    private LocalDateTime resolvedAt;
    
    @Column
    private Boolean isResolved = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;
    
    @Column(length = 50)
    private String source; // Source de l'insight (ML_MODEL, RULE_ENGINE, etc.)
    
    @Column(length = 20)
    private String version; // Version du modÃ¨le IA
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // MÃ©tadonnÃ©es additionnelles
    
    // Constructeurs
    public AIInsight() {
        this.createdAt = LocalDateTime.now();
    }
    
    public AIInsight(String title, String description, InsightType type, InsightCategory category) {
        this();
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
    }
    
    // Ã‰numÃ©rations
    public enum InsightType {
        PREDICTION, ANOMALY, RECOMMENDATION, OPTIMIZATION, RISK, TREND, OPPORTUNITY
    }
    
    public enum InsightCategory {
        FINANCIAL, OPERATIONAL, COMPLIANCE, STRATEGIC, TECHNICAL, MARKETING
    }
    
    public enum ImpactLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public InsightType getType() { return type; }
    public void setType(InsightType type) { this.type = type; }
    
    public InsightCategory getCategory() { return category; }
    public void setCategory(InsightCategory category) { this.category = category; }
    
    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }
    
    public ImpactLevel getImpact() { return impact; }
    public void setImpact(ImpactLevel impact) { this.impact = impact; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Boolean getActionable() { return actionable; }
    public void setActionable(Boolean actionable) { this.actionable = actionable; }
    
    public List<String> getSuggestedActions() { return suggestedActions; }
    public void setSuggestedActions(List<String> suggestedActions) { this.suggestedActions = suggestedActions; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public Boolean getIsResolved() { return isResolved; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    @PreUpdate
    public void preUpdate() {
        if (isResolved && resolvedAt == null) {
            resolvedAt = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "AIInsight{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", category=" + category +
                ", confidence=" + confidence +
                ", impact=" + impact +
                ", priority=" + priority +
                ", isResolved=" + isResolved +
                '}';
    }
}



