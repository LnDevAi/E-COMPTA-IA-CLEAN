package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_inventory_documents")
public class AssetInventoryDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long companyId; // ID de l'entreprise

    @Column(nullable = false)
    private String documentNumber; // Numéro du document

    @Column(nullable = false, length = 20)
    private String documentType; // ASSET_PURCHASE, ASSET_MAINTENANCE, INVENTORY_RECEIPT, INVENTORY_ISSUE, INVENTORY_TRANSFER, INVENTORY_ADJUSTMENT

    @Column(nullable = false)
    private String title; // Titre du document

    @Column(length = 1000)
    private String description; // Description du document

    @Column(nullable = false, length = 20)
    private String relatedEntityType; // ASSET, INVENTORY, MOVEMENT, ANALYSIS

    @Column
    private Long relatedEntityId; // ID de l'entité liée

    @Column
    private String relatedEntityCode; // Code de l'entité liée

    @Column(precision = 15, scale = 2)
    private BigDecimal documentAmount; // Montant du document

    @Column(length = 3)
    private String currency; // Devise du document

    @Column(nullable = false, length = 20)
    private String status; // DRAFT, VALIDATED, CANCELLED, ARCHIVED

    @Column
    private LocalDateTime documentDate; // Date du document

    @Column
    private String supplierCode; // Code du fournisseur

    @Column
    private String supplierName; // Nom du fournisseur

    @Column
    private String reference; // Référence externe

    @Column
    private String filePath; // Chemin vers le fichier

    @Column
    private String fileType; // Type de fichier (PDF, JPG, etc.)

    @Column
    private Long fileSize; // Taille du fichier en bytes

    @Column
    private String originalFileName; // Nom original du fichier

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false, length = 3)
    private String countryCode; // Code pays

    @Column(nullable = false, length = 20)
    private String accountingStandard; // Standard comptable

    @Column
    private Long createdBy; // ID de l'utilisateur créateur

    @Column
    private Long validatedBy; // ID de l'utilisateur validateur

    @Column
    private LocalDateTime validatedAt; // Date de validation

    @Column
    private Boolean isReconciled = false; // Si le document est rapproché

    @Column
    private String reconciliationReference; // Référence de rapprochement

    @Column
    private LocalDateTime reconciledAt; // Date de rapprochement

    @Column
    private Long reconciledBy; // ID de l'utilisateur qui a rapproché

    @Column(length = 500)
    private String notes; // Notes additionnelles

    @Column
    private Boolean isArchived = false; // Si le document est archivé

    @Column
    private LocalDateTime archivedAt; // Date d'archivage

    @Column
    private Long archivedBy; // ID de l'utilisateur qui a archivé

    @Column
    private String accountingEntryReference; // Référence de l'écriture comptable liée

    // Constructeurs
    public AssetInventoryDocument() {}

    public AssetInventoryDocument(Long companyId, String documentNumber, String documentType, 
                                String title, String relatedEntityType, String countryCode, 
                                String accountingStandard) {
        this.companyId = companyId;
        this.documentNumber = documentNumber;
        this.documentType = documentType;
        this.title = title;
        this.relatedEntityType = relatedEntityType;
        this.countryCode = countryCode;
        this.accountingStandard = accountingStandard;
        this.status = "DRAFT";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }

    public Long getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public String getRelatedEntityCode() { return relatedEntityCode; }
    public void setRelatedEntityCode(String relatedEntityCode) { this.relatedEntityCode = relatedEntityCode; }

    public BigDecimal getDocumentAmount() { return documentAmount; }
    public void setDocumentAmount(BigDecimal documentAmount) { this.documentAmount = documentAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDocumentDate() { return documentDate; }
    public void setDocumentDate(LocalDateTime documentDate) { this.documentDate = documentDate; }

    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getValidatedBy() { return validatedBy; }
    public void setValidatedBy(Long validatedBy) { this.validatedBy = validatedBy; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }

    public Boolean getIsReconciled() { return isReconciled; }
    public void setIsReconciled(Boolean isReconciled) { this.isReconciled = isReconciled; }

    public String getReconciliationReference() { return reconciliationReference; }
    public void setReconciliationReference(String reconciliationReference) { this.reconciliationReference = reconciliationReference; }

    public LocalDateTime getReconciledAt() { return reconciledAt; }
    public void setReconciledAt(LocalDateTime reconciledAt) { this.reconciledAt = reconciledAt; }

    public Long getReconciledBy() { return reconciledBy; }
    public void setReconciledBy(Long reconciledBy) { this.reconciledBy = reconciledBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }

    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }

    public Long getArchivedBy() { return archivedBy; }
    public void setArchivedBy(Long archivedBy) { this.archivedBy = archivedBy; }

    public String getAccountingEntryReference() { return accountingEntryReference; }
    public void setAccountingEntryReference(String accountingEntryReference) { this.accountingEntryReference = accountingEntryReference; }
}


