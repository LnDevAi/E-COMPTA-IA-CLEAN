package com.ecomptaia.elasticsearch;

import com.ecomptaia.accounting.entity.AccountingStandard;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité Elasticsearch pour l'indexation et la recherche de documents
 */
@Document(indexName = "ged_documents")
public class DocumentSearchEntity {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Long)
    private Long documentId;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String documentCode;
    
    @Field(type = FieldType.Keyword)
    private String fileName;
    
    @Field(type = FieldType.Keyword)
    private String fileType;
    
    @Field(type = FieldType.Keyword)
    private String mimeType;
    
    @Field(type = FieldType.Keyword)
    private String documentType;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String tags;
    
    @Field(type = FieldType.Integer)
    private Integer version;
    
    @Field(type = FieldType.Boolean)
    private Boolean isCurrentVersion;
    
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Keyword)
    private String securityLevel;
    
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiryDate;
    
    @Field(type = FieldType.Boolean)
    private Boolean isArchived;
    
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime archiveDate;
    
    @Field(type = FieldType.Integer)
    private Integer retentionPeriodYears;
    
    @Field(type = FieldType.Keyword)
    private String moduleReference;
    
    @Field(type = FieldType.Long)
    private Long entityReferenceId;
    
    @Field(type = FieldType.Keyword)
    private String entityReferenceType;
    
    @Field(type = FieldType.Long)
    private Long ecritureId;
    
    @Field(type = FieldType.Long)
    private Long companyId;
    
    @Field(type = FieldType.Keyword)
    private String countryCode;
    
    @Field(type = FieldType.Keyword)
    private String accountingStandard;
    
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @Field(type = FieldType.Long)
    private Long createdBy;
    
    @Field(type = FieldType.Long)
    private Long updatedBy;
    
    @Field(type = FieldType.Long)
    private Long approvedBy;
    
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime approvedAt;
    
    @Field(type = FieldType.Keyword)
    private String checksum;
    
    @Field(type = FieldType.Boolean)
    private Boolean isEncrypted;
    
    @Field(type = FieldType.Long)
    private Long fileSize;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String filePath;
    
    // Métadonnées pour la recherche
    @Field(type = FieldType.Nested)
    private List<DocumentMetadata> metadata;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchableContent;
    
    // Constructeurs
    public DocumentSearchEntity() {}
    
    public DocumentSearchEntity(Long documentId, String title, String description, String content) {
        this.documentId = documentId;
        this.title = title;
        this.description = description;
        this.content = content;
        this.id = documentId.toString();
    }
    
    // Classe interne pour les métadonnées
    public static class DocumentMetadata {
        @Field(type = FieldType.Keyword)
        private String key;
        
        @Field(type = FieldType.Text, analyzer = "standard")
        private String value;
        
        @Field(type = FieldType.Keyword)
        private String type;
        
        @Field(type = FieldType.Boolean)
        private Boolean isSearchable;
        
        // Constructeurs
        public DocumentMetadata() {}
        
        public DocumentMetadata(String key, String value, String type, Boolean isSearchable) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.isSearchable = isSearchable;
        }
        
        // Getters et Setters
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Boolean getIsSearchable() { return isSearchable; }
        public void setIsSearchable(Boolean isSearchable) { this.isSearchable = isSearchable; }
    }
    
    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getDocumentCode() { return documentCode; }
    public void setDocumentCode(String documentCode) { this.documentCode = documentCode; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    public Boolean getIsCurrentVersion() { return isCurrentVersion; }
    public void setIsCurrentVersion(Boolean isCurrentVersion) { this.isCurrentVersion = isCurrentVersion; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }
    
    public LocalDateTime getArchiveDate() { return archiveDate; }
    public void setArchiveDate(LocalDateTime archiveDate) { this.archiveDate = archiveDate; }
    
    public Integer getRetentionPeriodYears() { return retentionPeriodYears; }
    public void setRetentionPeriodYears(Integer retentionPeriodYears) { this.retentionPeriodYears = retentionPeriodYears; }
    
    public String getModuleReference() { return moduleReference; }
    public void setModuleReference(String moduleReference) { this.moduleReference = moduleReference; }
    
    public Long getEntityReferenceId() { return entityReferenceId; }
    public void setEntityReferenceId(Long entityReferenceId) { this.entityReferenceId = entityReferenceId; }
    
    public String getEntityReferenceType() { return entityReferenceType; }
    public void setEntityReferenceType(String entityReferenceType) { this.entityReferenceType = entityReferenceType; }
    
    public Long getEcritureId() { return ecritureId; }
    public void setEcritureId(Long ecritureId) { this.ecritureId = ecritureId; }
    
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
    
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public Boolean getIsEncrypted() { return isEncrypted; }
    public void setIsEncrypted(Boolean isEncrypted) { this.isEncrypted = isEncrypted; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public List<DocumentMetadata> getMetadata() { return metadata; }
    public void setMetadata(List<DocumentMetadata> metadata) { this.metadata = metadata; }
    
    public String getSearchableContent() { return searchableContent; }
    public void setSearchableContent(String searchableContent) { this.searchableContent = searchableContent; }
}


