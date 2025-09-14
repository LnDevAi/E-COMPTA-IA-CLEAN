package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "movement_code", unique = true, nullable = false)
    private String movementCode;
    
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;
    
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "reason", length = 500)
    private String reason;
    
    @Column(name = "movement_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType movementType;
    
    @Column(name = "quantity", precision = 15, scale = 3, nullable = false)
    private BigDecimal quantity;
    
    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate;
    
    @Column(name = "reference_number")
    private String referenceNumber; // Numéro de facture, bon de livraison, etc.
    
    @Column(name = "reference_type")
    private String referenceType; // PURCHASE, SALE, TRANSFER, ADJUSTMENT, etc.
    
    @Column(name = "warehouse_from")
    private String warehouseFrom;
    
    @Column(name = "warehouse_to")
    private String warehouseTo;
    
    @Column(name = "location_from")
    private String locationFrom;
    
    @Column(name = "location_to")
    private String locationTo;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementStatus status;
    
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
    public enum MovementType {
        IN,                 // Entrée
        OUT,                // Sortie
        TRANSFER,           // Transfert
        ADJUSTMENT,         // Ajustement
        RETURN,             // Retour
        DAMAGE,             // Dégâts
        LOSS,               // Perte
        EXPIRY              // Expiration
    }
    
    public enum MovementStatus {
        PENDING,            // En attente
        APPROVED,           // Approuvé
        REJECTED,           // Rejeté
        CANCELLED,          // Annulé
        COMPLETED           // Terminé
    }
    
    // Constructeurs
    public InventoryMovement() {
        this.createdAt = LocalDateTime.now();
        this.status = MovementStatus.PENDING;
        this.movementDate = LocalDate.now();
    }
    
    public InventoryMovement(String movementCode, Long productId, MovementType movementType, 
                            BigDecimal quantity, Long companyId, String countryCode, String accountingStandard) {
        this();
        this.movementCode = movementCode;
        this.itemId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.companyId = companyId;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMovementCode() { return movementCode; }
    public void setMovementCode(String movementCode) { this.movementCode = movementCode; }
    
    public Long getInventoryId() { return inventoryId; }
    public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }
    
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public Long getProductId() { return itemId; }
    public void setProductId(Long productId) { this.itemId = productId; }
    
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public LocalDate getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDate movementDate) { this.movementDate = movementDate; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    
    public String getWarehouseFrom() { return warehouseFrom; }
    public void setWarehouseFrom(String warehouseFrom) { this.warehouseFrom = warehouseFrom; }
    
    public String getWarehouseTo() { return warehouseTo; }
    public void setWarehouseTo(String warehouseTo) { this.warehouseTo = warehouseTo; }
    
    public String getLocationFrom() { return locationFrom; }
    public void setLocationFrom(String locationFrom) { this.locationFrom = locationFrom; }
    
    public String getLocationTo() { return locationTo; }
    public void setLocationTo(String locationTo) { this.locationTo = locationTo; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public MovementStatus getStatus() { return status; }
    public void setStatus(MovementStatus status) { this.status = status; }
    
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


