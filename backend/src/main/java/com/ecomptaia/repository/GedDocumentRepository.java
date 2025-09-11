ackage com.ecomptaia.repository;

import com.ecomptaia.entity.GedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des documents GED
 */
@Repository
public interface GedDocumentRepository extends JpaRepository<GedDocument, Long> {
    
    /**
     * Trouve les documents GED par entreprise
     */
    List<GedDocument> findByCompanyId(Long companyId);
    
    /**
     * Trouve un document GED par titre et entreprise
     */
    Optional<GedDocument> findByTitleAndCompanyId(String title, Long companyId);
    
    /**
     * Trouve les documents GED par type
     */
    List<GedDocument> findByDocumentType(GedDocument.DocumentType documentType);
    
    /**
     * Trouve les documents GED par entreprise et type
     */
    List<GedDocument> findByCompanyIdAndDocumentType(Long companyId, GedDocument.DocumentType documentType);
    
    /**
     * Trouve les documents GED par statut
     */
    List<GedDocument> findByStatus(GedDocument.DocumentStatus status);
    
    /**
     * Trouve les documents GED par entreprise et statut
     */
    List<GedDocument> findByCompanyIdAndStatus(Long companyId, GedDocument.DocumentStatus status);
    
    /**
     * Trouve les documents GED par titre contenant
     */
    List<GedDocument> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Trouve les documents GED par entreprise et titre contenant
     */
    List<GedDocument> findByCompanyIdAndTitleContainingIgnoreCase(Long companyId, String title);
    
    /**
     * Trouve les documents GED par créateur
     */
    List<GedDocument> findByCreatedBy(Long createdBy);
    
    /**
     * Trouve les documents GED par entreprise et créateur
     */
    List<GedDocument> findByCompanyIdAndCreatedBy(Long companyId, Long createdBy);
    
    /**
     * Trouve les documents GED par entité liée
     */
    List<GedDocument> findByEntityReferenceTypeAndEntityReferenceId(String entityReferenceType, Long entityReferenceId);
    
    /**
     * Trouve les documents GED par entreprise et entité liée
     */
    List<GedDocument> findByCompanyIdAndEntityReferenceTypeAndEntityReferenceId(Long companyId, String entityReferenceType, Long entityReferenceId);
    
    /**
     * Trouve les documents GED par catégorie
     */
    List<GedDocument> findByCategory(String category);
    
    /**
     * Trouve les documents GED par entreprise et catégorie
     */
    List<GedDocument> findByCompanyIdAndCategory(Long companyId, String category);
    
    /**
     * Trouve les documents GED par version
     */
    List<GedDocument> findByVersion(Integer version);
    
    /**
     * Trouve les documents GED par entreprise et version
     */
    List<GedDocument> findByCompanyIdAndVersion(Long companyId, Integer version);
    
    /**
     * Trouve les documents GED par tags
     */
    List<GedDocument> findByTagsContainingIgnoreCase(String tag);
    
    /**
     * Trouve les documents GED par entreprise et tags
     */
    List<GedDocument> findByCompanyIdAndTagsContainingIgnoreCase(Long companyId, String tag);
    
    /**
     * Trouve les documents GED créés après une date
     */
    List<GedDocument> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Trouve les documents GED créés entre deux dates
     */
    List<GedDocument> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve les documents GED mis à jour après une date
     */
    List<GedDocument> findByUpdatedAtAfter(LocalDateTime date);
    
    /**
     * Trouve les documents GED par taille
     */
    List<GedDocument> findByFileSize(Long fileSize);
    
    /**
     * Trouve les documents GED par taille minimum
     */
    @Query("SELECT gd FROM GedDocument gd WHERE gd.fileSize >= :minSize")
    List<GedDocument> findByFileSizeGreaterThanEqual(@Param("minSize") Long minSize);
    
    /**
     * Trouve les documents GED par taille maximum
     */
    @Query("SELECT gd FROM GedDocument gd WHERE gd.fileSize <= :maxSize")
    List<GedDocument> findByFileSizeLessThanEqual(@Param("maxSize") Long maxSize);
    
    /**
     * Trouve les documents GED par plage de taille
     */
    @Query("SELECT gd FROM GedDocument gd WHERE gd.fileSize BETWEEN :minSize AND :maxSize")
    List<GedDocument> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);
    
    /**
     * Trouve les documents GED par type de fichier
     */
    List<GedDocument> findByFileType(String fileType);
    
    /**
     * Trouve les documents GED par entreprise et type de fichier
     */
    List<GedDocument> findByCompanyIdAndFileType(Long companyId, String fileType);
    
    /**
     * Trouve les documents GED par MIME type
     */
    List<GedDocument> findByMimeType(String mimeType);
    
    /**
     * Trouve les documents GED par entreprise et MIME type
     */
    List<GedDocument> findByCompanyIdAndMimeType(Long companyId, String mimeType);
    
    /**
     * Vérifie si un document GED existe par titre et entreprise
     */
    boolean existsByTitleAndCompanyId(String title, Long companyId);
    
    /**
     * Compte les documents GED par entreprise
     */
    @Query("SELECT gd.companyId, COUNT(gd) FROM GedDocument gd GROUP BY gd.companyId")
    List<Object[]> countByCompany();
    
    /**
     * Compte les documents GED par type
     */
    @Query("SELECT gd.documentType, COUNT(gd) FROM GedDocument gd GROUP BY gd.documentType")
    List<Object[]> countByDocumentType();
    
    /**
     * Compte les documents GED par statut
     */
    @Query("SELECT gd.status, COUNT(gd) FROM GedDocument gd GROUP BY gd.status")
    List<Object[]> countByStatus();
    
    /**
     * Compte les documents GED par type de fichier
     */
    @Query("SELECT gd.fileType, COUNT(gd) FROM GedDocument gd GROUP BY gd.fileType")
    List<Object[]> countByFileType();
    
    /**
     * Calcule la taille totale des documents GED par entreprise
     */
    @Query("SELECT gd.companyId, SUM(gd.fileSize) FROM GedDocument gd GROUP BY gd.companyId")
    List<Object[]> sumFileSizeByCompany();
    
    /**
     * Trouve les documents GED les plus récents
     */
    @Query("SELECT gd FROM GedDocument gd ORDER BY gd.createdAt DESC")
    List<GedDocument> findRecentDocuments();
    
    /**
     * Trouve les documents GED les plus récents par entreprise
     */
    @Query("SELECT gd FROM GedDocument gd WHERE gd.companyId = :companyId ORDER BY gd.createdAt DESC")
    List<GedDocument> findRecentDocumentsByCompany(@Param("companyId") Long companyId);
    
    /**
     * Trouve les documents GED par chemin
     */
    List<GedDocument> findByFilePath(String filePath);
    
    /**
     * Trouve les documents GED par entreprise et chemin
     */
    List<GedDocument> findByCompanyIdAndFilePath(Long companyId, String filePath);
    
    /**
     * Trouve les documents GED par chemin contenant
     */
    List<GedDocument> findByFilePathContaining(String path);
    
    /**
     * Trouve les documents GED par entreprise et chemin contenant
     */
    List<GedDocument> findByCompanyIdAndFilePathContaining(Long companyId, String path);
    
    /**
     * Trouve les documents GED par niveau de sécurité
     */
    List<GedDocument> findBySecurityLevel(GedDocument.SecurityLevel securityLevel);
    
    /**
     * Trouve les documents GED par entreprise et niveau de sécurité
     */
    List<GedDocument> findByCompanyIdAndSecurityLevel(Long companyId, GedDocument.SecurityLevel securityLevel);
    
    /**
     * Trouve les documents GED par date d'expiration
     */
    List<GedDocument> findByExpiryDate(java.time.LocalDate expiryDate);
    
    /**
     * Trouve les documents GED expirés
     */
    @Query("SELECT gd FROM GedDocument gd WHERE gd.expiryDate < :currentDate")
    List<GedDocument> findExpiredDocuments(@Param("currentDate") java.time.LocalDate currentDate);
    
    /**
     * Trouve les documents GED expirés par entreprise
     */
    @Query("SELECT gd FROM GedDocument gd WHERE gd.companyId = :companyId AND gd.expiryDate < :currentDate")
    List<GedDocument> findExpiredDocumentsByCompany(@Param("companyId") Long companyId, @Param("currentDate") java.time.LocalDate currentDate);
    
    /**
     * Trouve les documents GED par date d'expiration entre deux dates
     */
    List<GedDocument> findByExpiryDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);
}



