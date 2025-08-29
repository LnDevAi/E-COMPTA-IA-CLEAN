package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * Entité pour gérer les abonnements et modules
 */
@Entity
@Table(name = "subscriptions")
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    
    @Column(name = "currency", nullable = false)
    private String currency = "EUR";
    
    @Column(name = "billing_cycle", nullable = false)
    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;
    
    @Column(name = "max_users")
    private Integer maxUsers;
    
    @Column(name = "max_companies")
    private Integer maxCompanies;
    
    @Column(name = "storage_limit_gb")
    private Integer storageLimitGB;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ElementCollection(targetClass = ModuleType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "subscription_modules", 
                     joinColumns = @JoinColumn(name = "subscription_id"))
    @Column(name = "module_type")
    private Set<ModuleType> includedModules = new HashSet<>();
    
    @ElementCollection(targetClass = FeatureType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "subscription_features", 
                     joinColumns = @JoinColumn(name = "subscription_id"))
    @Column(name = "feature_type")
    private Set<FeatureType> includedFeatures = new HashSet<>();
    
    // Constructeurs
    public Subscription() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Subscription(String name, String description, BigDecimal price, BillingCycle billingCycle) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.billingCycle = billingCycle;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BillingCycle getBillingCycle() {
        return billingCycle;
    }
    
    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }
    
    public Integer getMaxUsers() {
        return maxUsers;
    }
    
    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }
    
    public Integer getMaxCompanies() {
        return maxCompanies;
    }
    
    public void setMaxCompanies(Integer maxCompanies) {
        this.maxCompanies = maxCompanies;
    }
    
    public Integer getStorageLimitGB() {
        return storageLimitGB;
    }
    
    public void setStorageLimitGB(Integer storageLimitGB) {
        this.storageLimitGB = storageLimitGB;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    public Set<ModuleType> getIncludedModules() {
        return includedModules;
    }
    
    public void setIncludedModules(Set<ModuleType> includedModules) {
        this.includedModules = includedModules;
    }
    
    public Set<FeatureType> getIncludedFeatures() {
        return includedFeatures;
    }
    
    public void setIncludedFeatures(Set<FeatureType> includedFeatures) {
        this.includedFeatures = includedFeatures;
    }
    
    // Méthodes utilitaires
    public void addModule(ModuleType module) {
        this.includedModules.add(module);
    }
    
    public void removeModule(ModuleType module) {
        this.includedModules.remove(module);
    }
    
    public boolean hasModule(ModuleType module) {
        return this.includedModules.contains(module);
    }
    
    public void addFeature(FeatureType feature) {
        this.includedFeatures.add(feature);
    }
    
    public void removeFeature(FeatureType feature) {
        this.includedFeatures.remove(feature);
    }
    
    public boolean hasFeature(FeatureType feature) {
        return this.includedFeatures.contains(feature);
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Énumérations
    public enum BillingCycle {
        MONTHLY("Mensuel"),
        QUARTERLY("Trimestriel"),
        YEARLY("Annuel");
        
        private final String displayName;
        
        BillingCycle(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ModuleType {
        ACCOUNTING("Comptabilité"),
        TAXATION("Fiscalité"),
        SOCIAL("Social"),
        FINANCIAL_STATEMENTS("États Financiers"),
        AI("Intelligence Artificielle"),
        DOCUMENT_MANAGEMENT("Gestion de Documents"),
        USER_MANAGEMENT("Gestion des Utilisateurs"),
        LEGAL_INFORMATION("Informations Légales"),
        EXPORT("Export"),
        REPORTING("Reporting"),
        DASHBOARD("Tableau de Bord");
        
        private final String displayName;
        
        ModuleType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum FeatureType {
        BASIC_ACCOUNTING("Comptabilité de base"),
        ADVANCED_ACCOUNTING("Comptabilité avancée"),
        MULTI_COMPANY("Multi-entreprises"),
        MULTI_CURRENCY("Multi-devises"),
        OHADA_COMPLIANCE("Conformité OHADA"),
        INTERNATIONAL_STANDARDS("Standards internationaux"),
        PDF_EXPORT("Export PDF"),
        EXCEL_EXPORT("Export Excel"),
        AI_DOCUMENT_ANALYSIS("Analyse IA des documents"),
        AI_PREDICTIONS("Prédictions IA"),
        TAX_CALCULATIONS("Calculs fiscaux"),
        SOCIAL_CALCULATIONS("Calculs sociaux"),
        FINANCIAL_FORECASTING("Prévisions financières"),
        CUSTOM_REPORTS("Rapports personnalisés"),
        API_ACCESS("Accès API"),
        WHITE_LABEL("Marque blanche"),
        PRIORITY_SUPPORT("Support prioritaire"),
        TRAINING("Formation"),
        DATA_BACKUP("Sauvegarde des données"),
        ADVANCED_SECURITY("Sécurité avancée");
        
        private final String displayName;
        
        FeatureType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

