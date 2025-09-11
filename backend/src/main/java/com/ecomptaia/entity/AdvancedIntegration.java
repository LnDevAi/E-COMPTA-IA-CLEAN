package com.ecomptaia.entity;

import com.ecomptaia.entity.Company;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * EntitÃ© pour les intÃ©grations avancÃ©es
 * RÃ©volutionnaire vs TOMPRO - Ã‰cosystÃ¨me connectÃ© complet
 */
@Entity
@Table(name = "advanced_integrations")
public class AdvancedIntegration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IntegrationType type;
    
    @Column(nullable = false, length = 100)
    private String provider;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IntegrationStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 50)
    private String icon;
    
    @ElementCollection
    @CollectionTable(name = "integration_features", joinColumns = @JoinColumn(name = "integration_id"))
    @Column(name = "feature", length = 200)
    private List<String> features = new ArrayList<>();
    
    @Column(nullable = false, length = 200)
    private String endpoint;
    
    @Column(length = 200)
    private String apiKey;
    
    @Column(length = 200)
    private String secretKey;
    
    @Column(length = 200)
    private String webhookUrl;
    
    @Column(nullable = false)
    private Integer timeout; // en millisecondes
    
    @Column(nullable = false)
    private Integer retryAttempts;
    
    @Column(nullable = false)
    private Integer rateLimit; // requÃªtes par minute
    
    @Column(columnDefinition = "JSON")
    private String customHeaders; // Headers personnalisÃ©s en JSON
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthenticationType authentication;
    
    @Column(nullable = false)
    private Boolean encryption = true;
    
    @Column(nullable = false)
    private Boolean compression = false;
    
    @Column(nullable = false)
    private Boolean logging = true;
    
    @Column
    private LocalDateTime lastSync;
    
    @Column
    private LocalDateTime nextSync;
    
    @Column(nullable = false)
    private Integer syncFrequency; // en minutes
    
    @Column(nullable = false)
    private Boolean isRealTime = false;
    
    @Column(nullable = false, length = 20)
    private String apiVersion;
    
    @Column(length = 200)
    private String documentation;
    
    @Column(length = 200)
    private String supportContact;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ElementCollection(targetClass = IntegrationStatus.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "integration_status_history", joinColumns = @JoinColumn(name = "integration_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private List<IntegrationStatus> statusHistory = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "integration_sync_results", joinColumns = @JoinColumn(name = "integration_id"))
    @Column(name = "result_json", columnDefinition = "TEXT")
    private List<String> syncResults = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String configuration; // Configuration complÃ¨te en JSON
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // MÃ©tadonnÃ©es additionnelles
    
    // Constructeurs
    public AdvancedIntegration() {
        this.lastSync = LocalDateTime.now();
        this.nextSync = LocalDateTime.now().plusMinutes(15);
    }
    
    public AdvancedIntegration(String name, IntegrationType type, String provider) {
        this();
        this.name = name;
        this.type = type;
        this.provider = provider;
    }
    
    // Ã‰numÃ©rations
    public enum IntegrationType {
        BANKING, MOBILE_MONEY, GOVERNMENT, ERP, CRM, PAYMENT, COMMUNICATION, ANALYTICS
    }
    
    public enum IntegrationStatus {
        ACTIVE, INACTIVE, PENDING, ERROR, CONFIGURING, MAINTENANCE
    }
    
    public enum AuthenticationType {
        API_KEY, OAUTH2, BASIC, JWT, CERTIFICATE
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public IntegrationType getType() { return type; }
    public void setType(IntegrationType type) { this.type = type; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public IntegrationStatus getStatus() { return status; }
    public void setStatus(IntegrationStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    
    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    
    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }
    
    public Integer getRetryAttempts() { return retryAttempts; }
    public void setRetryAttempts(Integer retryAttempts) { this.retryAttempts = retryAttempts; }
    
    public Integer getRateLimit() { return rateLimit; }
    public void setRateLimit(Integer rateLimit) { this.rateLimit = rateLimit; }
    
    public String getCustomHeaders() { return customHeaders; }
    public void setCustomHeaders(String customHeaders) { this.customHeaders = customHeaders; }
    
    public AuthenticationType getAuthentication() { return authentication; }
    public void setAuthentication(AuthenticationType authentication) { this.authentication = authentication; }
    
    public Boolean getEncryption() { return encryption; }
    public void setEncryption(Boolean encryption) { this.encryption = encryption; }
    
    public Boolean getCompression() { return compression; }
    public void setCompression(Boolean compression) { this.compression = compression; }
    
    public Boolean getLogging() { return logging; }
    public void setLogging(Boolean logging) { this.logging = logging; }
    
    public LocalDateTime getLastSync() { return lastSync; }
    public void setLastSync(LocalDateTime lastSync) { this.lastSync = lastSync; }
    
    public LocalDateTime getNextSync() { return nextSync; }
    public void setNextSync(LocalDateTime nextSync) { this.nextSync = nextSync; }
    
    public Integer getSyncFrequency() { return syncFrequency; }
    public void setSyncFrequency(Integer syncFrequency) { this.syncFrequency = syncFrequency; }
    
    public Boolean getIsRealTime() { return isRealTime; }
    public void setIsRealTime(Boolean isRealTime) { this.isRealTime = isRealTime; }
    
    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
    
    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }
    
    public String getSupportContact() { return supportContact; }
    public void setSupportContact(String supportContact) { this.supportContact = supportContact; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public List<IntegrationStatus> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<IntegrationStatus> statusHistory) { this.statusHistory = statusHistory; }
    
    public List<String> getSyncResults() { return syncResults; }
    public void setSyncResults(List<String> syncResults) { this.syncResults = syncResults; }
    
    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    @PreUpdate
    public void preUpdate() {
        if (status == IntegrationStatus.ACTIVE && nextSync == null) {
            nextSync = LocalDateTime.now().plusMinutes(syncFrequency);
        }
    }
    
    @Override
    public String toString() {
        return "AdvancedIntegration{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", provider='" + provider + '\'' +
                ", status=" + status +
                ", isRealTime=" + isRealTime +
                '}';
    }
}




