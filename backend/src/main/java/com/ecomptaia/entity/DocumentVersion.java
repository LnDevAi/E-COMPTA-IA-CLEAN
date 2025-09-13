package com.ecomptaia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EntitÃ© pour gÃ©rer l'historique des versions de documents
 * Permet un versioning avancÃ© avec suivi des changements
 */
@Entity
@Table(name = "document_versions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "document_id", nullable = false)
    private Long documentId;
    
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;
    
    @Column(name = "version_label")
    private String versionLabel; // Ex: "1.0", "2.1", "Draft", "Final"
    
    @Column(name = "version_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VersionType versionType;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_hash", nullable = false)
    private String fileHash; // SHA-256 pour dÃ©tecter les changements
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "change_summary", columnDefinition = "TEXT")
    private String changeSummary; // RÃ©sumÃ© des changements
    
    @Column(name = "change_details", columnDefinition = "TEXT")
    private String changeDetails; // DÃ©tails des changements
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_current_version", nullable = false)
    @lombok.Builder.Default
    private Boolean isCurrentVersion = false;
    
    @Column(name = "is_archived", nullable = false)
    @lombok.Builder.Default
    private Boolean isArchived = false;
    
    @Column(name = "archive_date")
    private LocalDateTime archiveDate;
    
    @Column(name = "retention_until")
    private LocalDateTime retentionUntil;
    
    @Column(name = "access_count")
    @lombok.Builder.Default
    private Long accessCount = 0L;
    
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;
    
    @Column(name = "download_count")
    @lombok.Builder.Default
    private Long downloadCount = 0L;
    
    @Column(name = "last_downloaded_at")
    private LocalDateTime lastDownloadedAt;
    
    @Column(name = "version_metadata", columnDefinition = "TEXT")
    private String versionMetadata; // JSON pour mÃ©tadonnÃ©es supplÃ©mentaires
    
    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "approval_notes", columnDefinition = "TEXT")
    private String approvalNotes;
    
    @Column(name = "is_encrypted", nullable = false)
    @lombok.Builder.Default
    private Boolean isEncrypted = false;
    
    @Column(name = "encryption_key_id")
    private String encryptionKeyId;
    
    @Column(name = "compression_algorithm")
    private String compressionAlgorithm;
    
    @Column(name = "original_file_size")
    private Long originalFileSize;
    
    @Column(name = "compression_ratio")
    private Double compressionRatio;
    
    @Column(name = "watermark_applied")
    @lombok.Builder.Default
    private Boolean watermarkApplied = false;
    
    @Column(name = "watermark_text")
    private String watermarkText;
    
    @Column(name = "digital_signature")
    private String digitalSignature;
    
    @Column(name = "signature_algorithm")
    private String signatureAlgorithm;
    
    @Column(name = "signed_by")
    private Long signedBy;
    
    @Column(name = "signed_at")
    private LocalDateTime signedAt;
    
    @Column(name = "certificate_chain", columnDefinition = "TEXT")
    private String certificateChain; // ChaÃ®ne de certificats pour la signature
    
    @Column(name = "version_tags")
    private String versionTags; // Tags spÃ©cifiques Ã  cette version
    
    @Column(name = "parent_version_id")
    private Long parentVersionId; // RÃ©fÃ©rence vers la version parente
    
    @Column(name = "branch_name")
    private String branchName; // Pour le versioning en branches
    
    @Column(name = "merge_source_version_id")
    private Long mergeSourceVersionId; // Version source en cas de merge
    
    @Column(name = "is_auto_save", nullable = false)
    @lombok.Builder.Default
    private Boolean isAutoSave = false;
    
    @Column(name = "auto_save_interval_minutes")
    private Integer autoSaveIntervalMinutes;
    
    @Column(name = "next_auto_save_at")
    private LocalDateTime nextAutoSaveAt;
    
    // === ENUMS ===
    
    public enum VersionType {
        MAJOR,      // Version majeure (1.0 -> 2.0)
        MINOR,      // Version mineure (1.0 -> 1.1)
        PATCH,      // Correctif (1.0.0 -> 1.0.1)
        DRAFT,      // Brouillon
        FINAL,      // Version finale
        ARCHIVED,   // Version archivÃ©e
        DELETED     // Version supprimÃ©e
    }
    
    public enum ApprovalStatus {
        PENDING,    // En attente d'approbation
        APPROVED,   // ApprouvÃ©
        REJECTED,   // RejetÃ©
        CANCELLED,  // AnnulÃ©
        EXPIRED     // ExpirÃ©
    }
    
    // === MÃ‰THODES UTILITAIRES ===
    
    /**
     * VÃ©rifie si cette version est plus rÃ©cente qu'une autre
     */
    public boolean isNewerThan(DocumentVersion other) {
        return this.versionNumber > other.versionNumber;
    }
    
    /**
     * VÃ©rifie si cette version est une version majeure
     */
    public boolean isMajorVersion() {
        return this.versionType == VersionType.MAJOR;
    }
    
    /**
     * VÃ©rifie si cette version est approuvÃ©e
     */
    public boolean isApproved() {
        return this.approvalStatus == ApprovalStatus.APPROVED;
    }
    
    /**
     * VÃ©rifie si cette version est expirÃ©e
     */
    public boolean isExpired() {
        return this.retentionUntil != null && 
               this.retentionUntil.isBefore(LocalDateTime.now());
    }
    
    /**
     * IncrÃ©mente le compteur d'accÃ¨s
     */
    public void incrementAccessCount() {
        this.accessCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }
    
    /**
     * IncrÃ©mente le compteur de tÃ©lÃ©chargement
     */
    public void incrementDownloadCount() {
        this.downloadCount++;
        this.lastDownloadedAt = LocalDateTime.now();
    }
    
    /**
     * Calcule le ratio de compression
     */
    public void calculateCompressionRatio() {
        if (this.originalFileSize != null && this.fileSize != null && this.originalFileSize > 0) {
            this.compressionRatio = (double) this.fileSize / this.originalFileSize;
        }
    }
    
    /**
     * GÃ©nÃ¨re un label de version automatique
     */
    public String generateVersionLabel() {
        switch (this.versionType) {
            case MAJOR:
                return this.versionNumber + ".0";
            case MINOR:
                return this.versionNumber + ".1";
            case PATCH:
                return this.versionNumber + ".0.1";
            case DRAFT:
                return this.versionNumber + "-DRAFT";
            case FINAL:
                return this.versionNumber + "-FINAL";
            case ARCHIVED:
                return this.versionNumber + "-ARCHIVED";
            default:
                return this.versionNumber.toString();
        }
    }
}


