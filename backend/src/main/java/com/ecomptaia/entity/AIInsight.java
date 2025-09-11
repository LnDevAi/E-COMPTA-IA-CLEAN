package com.ecomptaia.entity;

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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "insight_type")
    private String insightType;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
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



