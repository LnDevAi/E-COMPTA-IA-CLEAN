package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consent_records")
public class ConsentRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private Long companyId;
    
    @Column
    private Long userId;
    
    @Column(length = 200)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ConsentType consentType;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ConsentMethod consentMethod;
    
    @Column
    private Boolean consentGiven;
    
    @Column
    private Boolean isActive = true;
    
    @Column
    private String ipAddress;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column
    private LocalDateTime consentDate;
    
    @Column
    private LocalDateTime withdrawalDate;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_protection_id")
    private DataProtection dataProtection;
    
    public enum ConsentType {
        MARKETING_EMAIL("Marketing Email"),
        MARKETING_SMS("Marketing SMS"),
        DATA_PROCESSING("Traitement des données"),
        TERMS_ACCEPTANCE("Conditions d'utilisation");
        
        private final String description;
        ConsentType(String description) { this.description = description; }
        public String getDescription() { return description; }
    }
    
    public enum ConsentMethod {
        WEB_FORM, EMAIL, SMS, API, OTHER
    }
    
    public void withdrawConsent(ConsentMethod method, String ip) {
        this.consentGiven = false;
        this.isActive = false;
        this.consentMethod = method;
        this.ipAddress = ip;
        this.withdrawalDate = LocalDateTime.now();
    }
    
    public boolean isConsentActive() {
        return Boolean.TRUE.equals(isActive) && Boolean.TRUE.equals(consentGiven) && withdrawalDate == null;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public ConsentType getConsentType() { return consentType; }
    public void setConsentType(ConsentType consentType) { this.consentType = consentType; }
    public ConsentMethod getConsentMethod() { return consentMethod; }
    public void setConsentMethod(ConsentMethod consentMethod) { this.consentMethod = consentMethod; }
    public Boolean getConsentGiven() { return consentGiven; }
    public void setConsentGiven(Boolean consentGiven) { this.consentGiven = consentGiven; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getConsentDate() { return consentDate; }
    public void setConsentDate(LocalDateTime consentDate) { this.consentDate = consentDate; }
    public LocalDateTime getWithdrawalDate() { return withdrawalDate; }
    public void setWithdrawalDate(LocalDateTime withdrawalDate) { this.withdrawalDate = withdrawalDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public DataProtection getDataProtection() { return dataProtection; }
    public void setDataProtection(DataProtection dataProtection) { this.dataProtection = dataProtection; }
}