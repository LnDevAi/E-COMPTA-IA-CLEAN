package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // ID de l'utilisateur

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false, length = 50)
    private String category; // CATEGORY: ACCOUNTING, THIRD_PARTY, FINANCIAL, SECURITY, SYSTEM

    @Column(nullable = false, length = 50)
    private String type; // TYPE: SYSTEM, ALERT, REMINDER, INFO, WARNING, ERROR

    @Column(nullable = false)
    private Boolean enabled = true; // Activer/désactiver ce type de notification

    @Column(nullable = false)
    private Boolean emailEnabled = true; // Notifications par email

    @Column(nullable = false)
    private Boolean pushEnabled = true; // Notifications push

    @Column(nullable = false)
    private Boolean inAppEnabled = true; // Notifications dans l'application

    @Column(length = 20)
    private String frequency; // FREQUENCY: IMMEDIATE, DAILY, WEEKLY, NEVER

    @Column(length = 20)
    private String priority; // PRIORITY: LOW, MEDIUM, HIGH, URGENT

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Constructeurs
    public NotificationPreference() {}

    public NotificationPreference(Long userId, Long companyId, String category, String type) {
        this.userId = userId;
        this.companyId = companyId;
        this.category = category;
        this.type = type;
        this.enabled = true;
        this.emailEnabled = true;
        this.pushEnabled = true;
        this.inAppEnabled = true;
        this.frequency = "IMMEDIATE";
        this.priority = "MEDIUM";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Boolean getEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(Boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public Boolean getPushEnabled() { return pushEnabled; }
    public void setPushEnabled(Boolean pushEnabled) { this.pushEnabled = pushEnabled; }

    public Boolean getInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(Boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "NotificationPreference{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}




