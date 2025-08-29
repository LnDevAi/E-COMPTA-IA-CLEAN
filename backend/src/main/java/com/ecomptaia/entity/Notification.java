package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // ID de l'utilisateur destinataire

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false, length = 100)
    private String title; // Titre de la notification

    @Column(nullable = false, length = 500)
    private String message; // Message de la notification

    @Column(nullable = false, length = 50)
    private String type; // TYPE: SYSTEM, ALERT, REMINDER, INFO, WARNING, ERROR

    @Column(nullable = false, length = 50)
    private String category; // CATEGORY: ACCOUNTING, THIRD_PARTY, FINANCIAL, SECURITY, SYSTEM

    @Column(length = 100)
    private String actionUrl; // URL d'action (optionnel)

    @Column(length = 50)
    private String actionType; // ACTION_TYPE: VIEW, EDIT, APPROVE, REJECT

    @Column(length = 20)
    private String priority; // PRIORITY: LOW, MEDIUM, HIGH, URGENT

    @Column(nullable = false)
    private Boolean isRead = false; // Statut de lecture

    @Column(nullable = false)
    private Boolean isActive = true; // Statut actif

    @Column
    private LocalDateTime readAt; // Date de lecture

    @Column(nullable = false)
    private LocalDateTime createdAt; // Date de création

    @Column
    private LocalDateTime expiresAt; // Date d'expiration (optionnel)

    @Column(length = 50)
    private String sourceModule; // Module source: THIRD_PARTY, ACCOUNTING, SECURITY, etc.

    @Column(length = 20)
    private String sourceId; // ID de l'élément source

    @Column(length = 500)
    private String metadata; // Données JSON supplémentaires

    // Constructeurs
    public Notification() {}

    public Notification(Long userId, Long companyId, String title, String message, 
                       String type, String category) {
        this.userId = userId;
        this.companyId = companyId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.category = category;
        this.isRead = false;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getSourceModule() { return sourceModule; }
    public void setSourceModule(String sourceModule) { this.sourceModule = sourceModule; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}




