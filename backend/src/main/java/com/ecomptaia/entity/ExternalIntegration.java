package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "external_integrations")
public class ExternalIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "integration_name", nullable = false)
    private String integrationName;

    @Column(name = "integration_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IntegrationType integrationType;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "api_endpoint")
    private String apiEndpoint;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "api_secret")
    private String apiSecret;

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "sync_frequency")
    private Integer syncFrequency; // en minutes

    @Column(name = "last_sync")
    private LocalDateTime lastSync;

    @Column(name = "next_sync")
    private LocalDateTime nextSync;

    @Column(name = "sync_status")
    @Enumerated(EnumType.STRING)
    private SyncStatus syncStatus;

    @Column(name = "error_count")
    private Integer errorCount;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "config_data", columnDefinition = "TEXT")
    private String configData; // JSON configuration

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum IntegrationType {
        BANKING("Intégration bancaire"),
        TAX("Intégration fiscale"),
        SOCIAL("Intégration sociale"),
        ERP("Intégration ERP"),
        CRM("Intégration CRM"),
        PAYMENT("Intégration paiement"),
        ACCOUNTING("Intégration comptable"),
        INVENTORY("Intégration inventaire"),
        HR("Intégration RH"),
        CUSTOM("Intégration personnalisée");

        private final String description;

        IntegrationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum SyncStatus {
        PENDING("En attente"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminé"),
        FAILED("Échoué"),
        DISABLED("Désactivé");

        private final String description;

        SyncStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public ExternalIntegration() {
        this.createdAt = LocalDateTime.now();
        this.isActive = false;
        this.errorCount = 0;
        this.syncStatus = SyncStatus.PENDING;
    }

    public ExternalIntegration(String integrationName, IntegrationType integrationType, 
                             String providerName, Long entrepriseId) {
        this();
        this.integrationName = integrationName;
        this.integrationType = integrationType;
        this.providerName = providerName;
        this.entrepriseId = entrepriseId;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntegrationName() {
        return integrationName;
    }

    public void setIntegrationName(String integrationName) {
        this.integrationName = integrationName;
    }

    public IntegrationType getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(IntegrationType integrationType) {
        this.integrationType = integrationType;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSyncFrequency() {
        return syncFrequency;
    }

    public void setSyncFrequency(Integer syncFrequency) {
        this.syncFrequency = syncFrequency;
    }

    public LocalDateTime getLastSync() {
        return lastSync;
    }

    public void setLastSync(LocalDateTime lastSync) {
        this.lastSync = lastSync;
    }

    public LocalDateTime getNextSync() {
        return nextSync;
    }

    public void setNextSync(LocalDateTime nextSync) {
        this.nextSync = nextSync;
    }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
