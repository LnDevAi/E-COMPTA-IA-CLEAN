package com.ecomptaia.repository;

import com.ecomptaia.entity.Backup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

    // ==================== RECHERCHE PAR ENTREPRISE ====================

    List<Backup> findByEntrepriseIdOrderByBackupDateDesc(Long entrepriseId);

    List<Backup> findByEntrepriseIdAndStatusOrderByBackupDateDesc(Long entrepriseId, Backup.BackupStatus status);

    List<Backup> findByEntrepriseIdAndBackupTypeOrderByBackupDateDesc(Long entrepriseId, Backup.BackupType backupType);

    // ==================== RECHERCHE PAR UTILISATEUR ====================

    List<Backup> findByUtilisateurIdOrderByBackupDateDesc(Long utilisateurId);

    List<Backup> findByUtilisateurIdAndStatusOrderByBackupDateDesc(Long utilisateurId, Backup.BackupStatus status);

    // ==================== RECHERCHE PAR STATUT ====================

    List<Backup> findByStatusOrderByBackupDateDesc(Backup.BackupStatus status);

    List<Backup> findByStatusAndEntrepriseIdOrderByBackupDateDesc(Backup.BackupStatus status, Long entrepriseId);

    // ==================== RECHERCHE PAR TYPE ====================

    List<Backup> findByBackupTypeOrderByBackupDateDesc(Backup.BackupType backupType);

    List<Backup> findByBackupTypeAndEntrepriseIdOrderByBackupDateDesc(Backup.BackupType backupType, Long entrepriseId);

    // ==================== RECHERCHE PAR STOCKAGE ====================

    List<Backup> findByStorageTypeOrderByBackupDateDesc(Backup.StorageType storageType);

    List<Backup> findByStorageTypeAndEntrepriseIdOrderByBackupDateDesc(Backup.StorageType storageType, Long entrepriseId);

    // ==================== RECHERCHE PAR DATE ====================

    @Query("SELECT b FROM Backup b WHERE b.backupDate BETWEEN :startDate AND :endDate ORDER BY b.backupDate DESC")
    List<Backup> findByBackupDateBetweenOrderByBackupDateDesc(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Backup b WHERE b.backupDate BETWEEN :startDate AND :endDate AND b.entrepriseId = :entrepriseId ORDER BY b.backupDate DESC")
    List<Backup> findByBackupDateBetweenAndEntrepriseIdOrderByBackupDateDesc(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("entrepriseId") Long entrepriseId);

    // ==================== RECHERCHE PAR TAILLE ====================

    @Query("SELECT b FROM Backup b WHERE b.fileSize > :minSize ORDER BY b.fileSize DESC")
    List<Backup> findByFileSizeGreaterThanOrderByFileSizeDesc(@Param("minSize") Long minSize);

    @Query("SELECT b FROM Backup b WHERE b.fileSize BETWEEN :minSize AND :maxSize ORDER BY b.fileSize DESC")
    List<Backup> findByFileSizeBetweenOrderByFileSizeDesc(
            @Param("minSize") Long minSize,
            @Param("maxSize") Long maxSize);

    // ==================== RECHERCHE PAR COMPRESSION ====================

    @Query("SELECT b FROM Backup b WHERE b.compressionRatio > :minRatio ORDER BY b.compressionRatio DESC")
    List<Backup> findByCompressionRatioGreaterThanOrderByCompressionRatioDesc(@Param("minRatio") Double minRatio);

    // ==================== RECHERCHE PAR CHIFFREMENT ====================

    List<Backup> findByEncryptionEnabledOrderByBackupDateDesc(Boolean encryptionEnabled);

    List<Backup> findByEncryptionEnabledAndEntrepriseIdOrderByBackupDateDesc(Boolean encryptionEnabled, Long entrepriseId);

    // ==================== RECHERCHE PAR DURÉE ====================

    @Query("SELECT b FROM Backup b WHERE b.durationSeconds > :minDuration ORDER BY b.durationSeconds DESC")
    List<Backup> findByDurationGreaterThanOrderByDurationDesc(@Param("minDuration") Long minDuration);

    // ==================== RECHERCHE PAR VERSION ====================

    List<Backup> findByVersionOrderByBackupDateDesc(String version);

    List<Backup> findByVersionAndEntrepriseIdOrderByBackupDateDesc(String version, Long entrepriseId);

    // ==================== RECHERCHE AVANCÉE ====================

    @Query("SELECT b FROM Backup b WHERE " +
           "(:entrepriseId IS NULL OR b.entrepriseId = :entrepriseId) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:backupType IS NULL OR b.backupType = :backupType) AND " +
           "(:storageType IS NULL OR b.storageType = :storageType) AND " +
           "(:encryptionEnabled IS NULL OR b.encryptionEnabled = :encryptionEnabled) AND " +
           "(:startDate IS NULL OR b.backupDate >= :startDate) AND " +
           "(:endDate IS NULL OR b.backupDate <= :endDate) " +
           "ORDER BY b.backupDate DESC")
    List<Backup> findBackupsWithCriteria(
            @Param("entrepriseId") Long entrepriseId,
            @Param("status") Backup.BackupStatus status,
            @Param("backupType") Backup.BackupType backupType,
            @Param("storageType") Backup.StorageType storageType,
            @Param("encryptionEnabled") Boolean encryptionEnabled,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ==================== COMPTAGE ====================

    @Query("SELECT COUNT(b) FROM Backup b WHERE b.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(b) FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.status = :status")
    Long countByEntrepriseIdAndStatus(@Param("entrepriseId") Long entrepriseId, @Param("status") Backup.BackupStatus status);

    @Query("SELECT COUNT(b) FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.backupType = :backupType")
    Long countByEntrepriseIdAndBackupType(@Param("entrepriseId") Long entrepriseId, @Param("backupType") Backup.BackupType backupType);

    // ==================== STATISTIQUES ====================

    @Query("SELECT AVG(b.fileSize) FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.status = 'COMPLETED'")
    Double getAverageFileSizeByEntreprise(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(b.durationSeconds) FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.status = 'COMPLETED'")
    Double getAverageDurationByEntreprise(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT AVG(b.compressionRatio) FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.status = 'COMPLETED' AND b.compressionRatio IS NOT NULL")
    Double getAverageCompressionRatioByEntreprise(@Param("entrepriseId") Long entrepriseId);

    // ==================== DERNIÈRES SAUVEGARDES ====================

    @Query("SELECT b FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.status = 'COMPLETED' ORDER BY b.backupDate DESC")
    List<Backup> findLastCompletedBackupsByEntreprise(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT b FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.status = 'COMPLETED' ORDER BY b.backupDate DESC LIMIT 1")
    Backup findLastCompletedBackupByEntreprise(@Param("entrepriseId") Long entrepriseId);

    // ==================== SAUVEGARDES EN ÉCHEC ====================

    @Query("SELECT b FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.status = 'FAILED' ORDER BY b.backupDate DESC")
    List<Backup> findFailedBackupsByEntreprise(@Param("entrepriseId") Long entrepriseId);

    // ==================== SAUVEGARDES EN COURS ====================

    @Query("SELECT b FROM Backup b WHERE b.status = 'IN_PROGRESS' ORDER BY b.backupDate ASC")
    List<Backup> findInProgressBackups();

    @Query("SELECT b FROM Backup b WHERE b.entrepriseId = :entrepriseId AND b.status = 'IN_PROGRESS' ORDER BY b.backupDate ASC")
    List<Backup> findInProgressBackupsByEntreprise(@Param("entrepriseId") Long entrepriseId);
}





