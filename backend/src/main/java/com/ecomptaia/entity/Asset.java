package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "asset_code", unique = true, nullable = false)
    private String assetCode;
    
    @Column(name = "asset_name", nullable = false)
    private String assetName;
    
    @Column(name = "asset_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssetType assetType;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "purchase_price", precision = 15, scale = 2)
    private BigDecimal purchasePrice;
    
    @Column(name = "current_value", precision = 15, scale = 2)
    private BigDecimal currentValue;
    
    @Column(name = "depreciation_rate", precision = 5, scale = 2)
    private BigDecimal depreciationRate;
    
    @Column(name = "useful_life_years")
    private Integer usefulLifeYears;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "supplier")
    private String supplier;
    
    @Column(name = "serial_number")
    private String serialNumber;
    
    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssetStatus status;
    
    @Column(name = "maintenance_frequency")
    private String maintenanceFrequency;
    
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;
    
    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;
    
    @Column(name = "insurance_policy")
    private String insurancePolicy;
    
    @Column(name = "insurance_expiry")
    private LocalDate insuranceExpiry;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(name = "country_code", nullable = false)
    private String countryCode;
    
    @Column(name = "accounting_standard", nullable = false)
    private String accountingStandard;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Enums
    public enum AssetType {
        BUILDING,           // Bâtiment
        MACHINERY,          // Machine
        VEHICLE,            // Véhicule
        EQUIPMENT,          // Équipement
        FURNITURE,          // Mobilier
        COMPUTER,           // Informatique
        SOFTWARE,           // Logiciel
        LAND,               // Terrain
        INTANGIBLE,         // Immobilisation incorporelle
        OTHER               // Autre
    }
    
    public enum AssetStatus {
        ACTIVE,             // En service
        MAINTENANCE,        // En maintenance
        RETIRED,            // Retiré
        SOLD,               // Vendu
        LOST,               // Perdu/Volé
        DAMAGED,            // Endommagé
        UNDER_CONSTRUCTION  // En construction
    }
    
    // Constructeurs
    public Asset() {
        this.createdAt = LocalDateTime.now();
        this.status = AssetStatus.ACTIVE;
    }
    
    public Asset(String assetCode, String assetName, AssetType assetType, BigDecimal purchasePrice, 
                 Long companyId, String countryCode, String accountingStandard) {
        this();
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.assetType = assetType;
        this.purchasePrice = purchasePrice;
        this.currentValue = purchasePrice;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAssetCode() { return assetCode; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }
    
    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }
    
    public AssetType getAssetType() { return assetType; }
    public void setAssetType(AssetType assetType) { this.assetType = assetType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }
    
    public BigDecimal getDepreciationRate() { return depreciationRate; }
    public void setDepreciationRate(BigDecimal depreciationRate) { this.depreciationRate = depreciationRate; }
    
    public Integer getUsefulLifeYears() { return usefulLifeYears; }
    public void setUsefulLifeYears(Integer usefulLifeYears) { this.usefulLifeYears = usefulLifeYears; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    
    public LocalDate getWarrantyExpiry() { return warrantyExpiry; }
    public void setWarrantyExpiry(LocalDate warrantyExpiry) { this.warrantyExpiry = warrantyExpiry; }
    
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
    
    public String getMaintenanceFrequency() { return maintenanceFrequency; }
    public void setMaintenanceFrequency(String maintenanceFrequency) { this.maintenanceFrequency = maintenanceFrequency; }
    
    public LocalDate getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }
    
    public LocalDate getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }
    
    public String getInsurancePolicy() { return insurancePolicy; }
    public void setInsurancePolicy(String insurancePolicy) { this.insurancePolicy = insurancePolicy; }
    
    public LocalDate getInsuranceExpiry() { return insuranceExpiry; }
    public void setInsuranceExpiry(LocalDate insuranceExpiry) { this.insuranceExpiry = insuranceExpiry; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


