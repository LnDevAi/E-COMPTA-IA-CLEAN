package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "backups")
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "backup_name", nullable = false)
    private String backupName;

    @Column(name = "backup_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BackupType backupType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BackupStatus status;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "compression_ratio")
    private Double compressionRatio;

    @Column(name = "encryption_enabled")
    private Boolean encryptionEnabled = false;

    @Column(name = "storage_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    @Column(name = "backup_date", nullable = false)
    private LocalDateTime backupDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "version")
    private String version;

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    // ==================== ENUMS ====================

    public enum BackupType {
        COMPLETE("Complète"),
        INCREMENTAL("Incrémentale"),
        DIFFERENTIAL("Différentielle"),
        LOG("Log");

        private final String description;

        BackupType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum BackupStatus {
        PENDING("En attente"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminée"),
        FAILED("Échouée"),
        CANCELLED("Annulée"),
        RESTORING("Restauration en cours");

        private final String description;

        BackupStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum StorageType {
        LOCAL("Local"),
        CLOUD("Cloud"),
        HYBRID("Hybride");

        private final String description;

        StorageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public Backup() {
        this.backupDate = LocalDateTime.now();
        this.status = BackupStatus.PENDING;
        this.encryptionEnabled = false;
    }

    public Backup(String backupName, BackupType backupType, StorageType storageType, Long entrepriseId, Long utilisateurId) {
        this();
        this.backupName = backupName;
        this.backupType = backupType;
        this.storageType = storageType;
        this.entrepriseId = entrepriseId;
        this.utilisateurId = utilisateurId;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBackupName() {
        return backupName;
    }

    public void setBackupName(String backupName) {
        this.backupName = backupName;
    }

    public BackupType getBackupType() {
        return backupType;
    }

    public void setBackupType(BackupType backupType) {
        this.backupType = backupType;
    }

    public BackupStatus getStatus() {
        return status;
    }

    public void setStatus(BackupStatus status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Double getCompressionRatio() {
        return compressionRatio;
    }

    public void setCompressionRatio(Double compressionRatio) {
        this.compressionRatio = compressionRatio;
    }

    public Boolean getEncryptionEnabled() {
        return encryptionEnabled;
    }

    public void setEncryptionEnabled(Boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public LocalDateTime getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(LocalDateTime backupDate) {
        this.backupDate = backupDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
}




