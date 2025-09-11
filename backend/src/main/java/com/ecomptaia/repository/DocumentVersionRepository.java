package com.ecomptaia.repository;

import com.ecomptaia.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des versions de documents
 */
@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    
    // === RECHERCHE PAR DOCUMENT ===
    
    /**
     * Trouve toutes les versions d'un document
     */
    List<DocumentVersion> findByDocumentIdOrderByVersionNumberDesc(Long documentId);
    
    /**
     * Trouve toutes les versions d'un document triées par date de création
     */
    List<DocumentVersion> findByDocumentIdOrderByCreatedAtDesc(Long documentId);
    
    /**
     * Trouve la version actuelle d'un document
     */
    Optional<DocumentVersion> findByDocumentIdAndIsCurrentVersionTrue(Long documentId);
    
    /**
     * Trouve une version spécifique d'un document
     */
    Optional<DocumentVersion> findByDocumentIdAndVersionNumber(Long documentId, Integer versionNumber);
    
    /**
     * Trouve la dernière version d'un document
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.documentId = :documentId ORDER BY dv.versionNumber DESC")
    Optional<DocumentVersion> findLatestVersionByDocumentId(@Param("documentId") Long documentId);
    
    /**
     * Trouve la première version d'un document
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.documentId = :documentId ORDER BY dv.versionNumber ASC")
    Optional<DocumentVersion> findFirstVersionByDocumentId(@Param("documentId") Long documentId);
    
    // === RECHERCHE PAR TYPE DE VERSION ===
    
    /**
     * Trouve les versions par type
     */
    List<DocumentVersion> findByVersionType(DocumentVersion.VersionType versionType);
    
    /**
     * Trouve les versions d'un document par type
     */
    List<DocumentVersion> findByDocumentIdAndVersionType(Long documentId, DocumentVersion.VersionType versionType);
    
    /**
     * Trouve les versions majeures
     */
    List<DocumentVersion> findByVersionTypeOrderByVersionNumberDesc(DocumentVersion.VersionType versionType);
    
    /**
     * Trouve les versions mineures
     */
    List<DocumentVersion> findByVersionTypeOrderByCreatedAtDesc(DocumentVersion.VersionType versionType);
    
    // === RECHERCHE PAR STATUT ===
    
    /**
     * Trouve les versions par statut d'approbation
     */
    List<DocumentVersion> findByApprovalStatus(DocumentVersion.ApprovalStatus approvalStatus);
    
    /**
     * Trouve les versions en attente d'approbation
     */
    List<DocumentVersion> findByApprovalStatusOrderByCreatedAtAsc(DocumentVersion.ApprovalStatus approvalStatus);
    
    /**
     * Trouve les versions approuvées
     */
    List<DocumentVersion> findByApprovalStatusOrderByApprovedAtDesc(DocumentVersion.ApprovalStatus approvalStatus);
    
    /**
     * Trouve les versions archivées
     */
    List<DocumentVersion> findByIsArchivedTrue();
    
    /**
     * Trouve les versions non archivées
     */
    List<DocumentVersion> findByIsArchivedFalse();
    
    // === RECHERCHE PAR UTILISATEUR ===
    
    /**
     * Trouve les versions créées par un utilisateur
     */
    List<DocumentVersion> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
    
    /**
     * Trouve les versions approuvées par un utilisateur
     */
    List<DocumentVersion> findByApprovedByOrderByApprovedAtDesc(Long approvedBy);
    
    /**
     * Trouve les versions signées par un utilisateur
     */
    List<DocumentVersion> findBySignedByOrderBySignedAtDesc(Long signedBy);
    
    // === RECHERCHE PAR PÉRIODE ===
    
    /**
     * Trouve les versions créées après une date
     */
    List<DocumentVersion> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Trouve les versions créées entre deux dates
     */
    List<DocumentVersion> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve les versions approuvées après une date
     */
    List<DocumentVersion> findByApprovedAtAfter(LocalDateTime date);
    
    /**
     * Trouve les versions approuvées entre deux dates
     */
    List<DocumentVersion> findByApprovedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve les versions expirées
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.retentionUntil IS NOT NULL AND dv.retentionUntil < :currentDate")
    List<DocumentVersion> findExpiredVersions(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Trouve les versions expirant bientôt
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.retentionUntil IS NOT NULL AND dv.retentionUntil BETWEEN :startDate AND :endDate")
    List<DocumentVersion> findVersionsExpiringSoon(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // === RECHERCHE PAR HASH ET INTÉGRITÉ ===
    
    /**
     * Trouve les versions par hash de fichier
     */
    List<DocumentVersion> findByFileHash(String fileHash);
    
    /**
     * Trouve les versions avec le même hash (doublons potentiels)
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.fileHash = :fileHash AND dv.documentId != :documentId")
    List<DocumentVersion> findDuplicateVersions(@Param("fileHash") String fileHash, @Param("documentId") Long documentId);
    
    /**
     * Trouve les versions par nom de fichier
     */
    List<DocumentVersion> findByFileName(String fileName);
    
    /**
     * Trouve les versions par nom de fichier contenant
     */
    List<DocumentVersion> findByFileNameContainingIgnoreCase(String fileName);
    
    // === RECHERCHE PAR MÉTADONNÉES ===
    
    /**
     * Trouve les versions par label
     */
    List<DocumentVersion> findByVersionLabel(String versionLabel);
    
    /**
     * Trouve les versions par label contenant
     */
    List<DocumentVersion> findByVersionLabelContainingIgnoreCase(String versionLabel);
    
    /**
     * Trouve les versions par tags
     */
    List<DocumentVersion> findByVersionTagsContainingIgnoreCase(String tag);
    
    /**
     * Trouve les versions par branche
     */
    List<DocumentVersion> findByBranchName(String branchName);
    
    /**
     * Trouve les versions d'un document par branche
     */
    List<DocumentVersion> findByDocumentIdAndBranchName(Long documentId, String branchName);
    
    /**
     * Trouve les versions par branche contenant
     */
    List<DocumentVersion> findByBranchNameContainingIgnoreCase(String branchName);
    
    // === RECHERCHE PAR SÉCURITÉ ===
    
    /**
     * Trouve les versions chiffrées
     */
    List<DocumentVersion> findByIsEncryptedTrue();
    
    /**
     * Trouve les versions non chiffrées
     */
    List<DocumentVersion> findByIsEncryptedFalse();
    
    /**
     * Trouve les versions avec filigrane
     */
    List<DocumentVersion> findByWatermarkAppliedTrue();
    
    /**
     * Trouve les versions signées numériquement
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.digitalSignature IS NOT NULL")
    List<DocumentVersion> findDigitallySignedVersions();
    
    // === RECHERCHE PAR PERFORMANCE ===
    
    /**
     * Trouve les versions les plus accédées
     */
    @Query("SELECT dv FROM DocumentVersion dv ORDER BY dv.accessCount DESC")
    List<DocumentVersion> findMostAccessedVersions();
    
    /**
     * Trouve les versions les plus téléchargées
     */
    @Query("SELECT dv FROM DocumentVersion dv ORDER BY dv.downloadCount DESC")
    List<DocumentVersion> findMostDownloadedVersions();
    
    /**
     * Trouve les versions récemment accédées
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.lastAccessedAt IS NOT NULL ORDER BY dv.lastAccessedAt DESC")
    List<DocumentVersion> findRecentlyAccessedVersions();
    
    /**
     * Trouve les versions récemment téléchargées
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.lastDownloadedAt IS NOT NULL ORDER BY dv.lastDownloadedAt DESC")
    List<DocumentVersion> findRecentlyDownloadedVersions();
    
    // === RECHERCHE PAR COMPRESSION ===
    
    /**
     * Trouve les versions compressées
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.compressionAlgorithm IS NOT NULL")
    List<DocumentVersion> findCompressedVersions();
    
    /**
     * Trouve les versions avec le meilleur ratio de compression
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.compressionRatio IS NOT NULL ORDER BY dv.compressionRatio ASC")
    List<DocumentVersion> findBestCompressedVersions();
    
    // === RECHERCHE PAR AUTO-SAVE ===
    
    /**
     * Trouve les versions auto-sauvegardées
     */
    List<DocumentVersion> findByIsAutoSaveTrue();
    
    /**
     * Trouve les versions auto-sauvegardées d'un document
     */
    List<DocumentVersion> findByDocumentIdAndIsAutoSaveTrue(Long documentId);
    
    /**
     * Trouve les versions nécessitant une auto-sauvegarde
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.nextAutoSaveAt IS NOT NULL AND dv.nextAutoSaveAt <= :currentDate")
    List<DocumentVersion> findVersionsNeedingAutoSave(@Param("currentDate") LocalDateTime currentDate);
    
    // === RECHERCHE PAR HIÉRARCHIE ===
    
    /**
     * Trouve les versions enfants d'une version parente
     */
    List<DocumentVersion> findByParentVersionId(Long parentVersionId);
    
    /**
     * Trouve les versions racines (sans parent)
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.parentVersionId IS NULL")
    List<DocumentVersion> findRootVersions();
    
    /**
     * Trouve les versions de merge
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.mergeSourceVersionId IS NOT NULL")
    List<DocumentVersion> findMergedVersions();
    
    // === STATISTIQUES ===
    
    /**
     * Compte le nombre de versions d'un document
     */
    @Query("SELECT COUNT(dv) FROM DocumentVersion dv WHERE dv.documentId = :documentId")
    Long countVersionsByDocumentId(@Param("documentId") Long documentId);
    
    /**
     * Compte les versions par type
     */
    @Query("SELECT COUNT(dv) FROM DocumentVersion dv WHERE dv.versionType = :versionType")
    Long countVersionsByType(@Param("versionType") DocumentVersion.VersionType versionType);
    
    /**
     * Compte les versions par statut d'approbation
     */
    @Query("SELECT COUNT(dv) FROM DocumentVersion dv WHERE dv.approvalStatus = :approvalStatus")
    Long countVersionsByApprovalStatus(@Param("approvalStatus") DocumentVersion.ApprovalStatus approvalStatus);
    
    /**
     * Trouve la version avec le numéro le plus élevé d'un document
     */
    @Query("SELECT MAX(dv.versionNumber) FROM DocumentVersion dv WHERE dv.documentId = :documentId")
    Optional<Integer> findMaxVersionNumberByDocumentId(@Param("documentId") Long documentId);
    
    /**
     * Trouve la version avec le numéro le plus bas d'un document
     */
    @Query("SELECT MIN(dv.versionNumber) FROM DocumentVersion dv WHERE dv.documentId = :documentId")
    Optional<Integer> findMinVersionNumberByDocumentId(@Param("documentId") Long documentId);
    
    // === RECHERCHE COMPLEXE ===
    
    /**
     * Trouve les versions avec plusieurs critères
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.documentId = :documentId AND dv.versionType = :versionType AND dv.approvalStatus = :approvalStatus")
    List<DocumentVersion> findVersionsByMultipleCriteria(@Param("documentId") Long documentId, 
                                                         @Param("versionType") DocumentVersion.VersionType versionType, 
                                                         @Param("approvalStatus") DocumentVersion.ApprovalStatus approvalStatus);
    
    /**
     * Trouve les versions créées par un utilisateur pour un document
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.documentId = :documentId AND dv.createdBy = :createdBy ORDER BY dv.createdAt DESC")
    List<DocumentVersion> findVersionsByDocumentAndCreator(@Param("documentId") Long documentId, @Param("createdBy") Long createdBy);
    
    /**
     * Trouve les versions approuvées par un utilisateur pour un document
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.documentId = :documentId AND dv.approvedBy = :approvedBy ORDER BY dv.approvedAt DESC")
    List<DocumentVersion> findVersionsByDocumentAndApprover(@Param("documentId") Long documentId, @Param("approvedBy") Long approvedBy);
}
