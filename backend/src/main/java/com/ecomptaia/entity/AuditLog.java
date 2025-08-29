package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité pour l'audit trail - Traçabilité de toutes les actions importantes
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "action_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    // Enum pour les types d'actions
    public enum ActionType {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT, IMPORT, 
        VALIDATE, REJECT, APPROVE, SUSPEND, ACTIVATE,
        SUBSCRIPTION_CREATE, SUBSCRIPTION_UPDATE, SUBSCRIPTION_CANCEL,
        PAYMENT_SUCCESS, PAYMENT_FAILED, INVOICE_GENERATE,
        DOCUMENT_UPLOAD, DOCUMENT_DELETE, BACKUP_CREATE,
        CONFIGURATION_CHANGE, SECURITY_EVENT, SYSTEM_MAINTENANCE
    }

    // Enum pour le statut de l'audit
    public enum AuditStatus {
        SUCCESS, FAILED, PENDING, CANCELLED
    }

    // Constructeurs
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
        this.status = AuditStatus.SUCCESS;
    }

    public AuditLog(ActionType actionType, String entityType, String description) {
        this();
        this.actionType = actionType;
        this.entityType = entityType;
        this.description = description;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", actionType=" + actionType +
                ", entityType='" + entityType + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}




