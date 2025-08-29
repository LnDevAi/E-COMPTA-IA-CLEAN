package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_code", unique = true, nullable = false)
    private String productCode;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "unit", nullable = false)
    private String unit; // pièce, kg, litre, mètre, etc.
    
    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice;
    
    @Column(name = "quantity_on_hand", precision = 15, scale = 3, nullable = false)
    private BigDecimal quantityOnHand;
    
    @Column(name = "minimum_stock", precision = 15, scale = 3)
    private BigDecimal minimumStock;
    
    @Column(name = "maximum_stock", precision = 15, scale = 3)
    private BigDecimal maximumStock;
    
    @Column(name = "reorder_point", precision = 15, scale = 3)
    private BigDecimal reorderPoint;
    
    @Column(name = "supplier")
    private String supplier;
    
    @Column(name = "supplier_code")
    private String supplierCode;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "warehouse")
    private String warehouse;
    
    @Column(name = "shelf")
    private String shelf;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InventoryStatus status;
    
    @Column(name = "valuation_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private ValuationMethod valuationMethod;
    
    @Column(name = "last_purchase_price", precision = 15, scale = 2)
    private BigDecimal lastPurchasePrice;
    
    @Column(name = "average_cost", precision = 15, scale = 2)
    private BigDecimal averageCost;
    
    @Column(name = "total_value", precision = 15, scale = 2)
    private BigDecimal totalValue;
    
    @Column(name = "last_movement_date")
    private LocalDate lastMovementDate;
    
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
    public enum InventoryStatus {
        ACTIVE,             // Actif
        INACTIVE,           // Inactif
        DISCONTINUED,       // Arrêté
        OUT_OF_STOCK,       // Rupture de stock
        EXPIRED,            // Expiré
        DAMAGED,            // Endommagé
        RESERVED            // Réservé
    }
    
    public enum ValuationMethod {
        FIFO,               // Premier entré, premier sorti
        LIFO,               // Dernier entré, premier sorti
        AVERAGE_COST,       // Coût moyen
        STANDARD_COST,      // Coût standard
        SPECIFIC_IDENTIFICATION // Identification spécifique
    }
    
    // Constructeurs
    public Inventory() {
        this.createdAt = LocalDateTime.now();
        this.status = InventoryStatus.ACTIVE;
        this.valuationMethod = ValuationMethod.FIFO;
        this.quantityOnHand = BigDecimal.ZERO;
        this.totalValue = BigDecimal.ZERO;
    }
    
    public Inventory(String productCode, String productName, String unit, BigDecimal unitPrice, 
                     Long companyId, String countryCode, String accountingStandard) {
        this();
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(BigDecimal quantityOnHand) { this.quantityOnHand = quantityOnHand; }
    
    public BigDecimal getMinimumStock() { return minimumStock; }
    public void setMinimumStock(BigDecimal minimumStock) { this.minimumStock = minimumStock; }
    
    public BigDecimal getMaximumStock() { return maximumStock; }
    public void setMaximumStock(BigDecimal maximumStock) { this.maximumStock = maximumStock; }
    
    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }
    
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }
    
    public String getShelf() { return shelf; }
    public void setShelf(String shelf) { this.shelf = shelf; }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public InventoryStatus getStatus() { return status; }
    public void setStatus(InventoryStatus status) { this.status = status; }
    
    public ValuationMethod getValuationMethod() { return valuationMethod; }
    public void setValuationMethod(ValuationMethod valuationMethod) { this.valuationMethod = valuationMethod; }
    
    public BigDecimal getLastPurchasePrice() { return lastPurchasePrice; }
    public void setLastPurchasePrice(BigDecimal lastPurchasePrice) { this.lastPurchasePrice = lastPurchasePrice; }
    
    public BigDecimal getAverageCost() { return averageCost; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }
    
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    
    public LocalDate getLastMovementDate() { return lastMovementDate; }
    public void setLastMovementDate(LocalDate lastMovementDate) { this.lastMovementDate = lastMovementDate; }
    
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


