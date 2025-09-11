ackage com.ecomptaia.repository;

import com.ecomptaia.entity.ConsentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des enregistrements de consentement
 */
@Repository
public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, Long> {
    
    /**
     * Trouve les consentements par entreprise
     */
    List<ConsentRecord> findByCompanyId(Long companyId);
    
    /**
     * Trouve les consentements par utilisateur
     */
    List<ConsentRecord> findByUserId(Long userId);
    
    /**
     * Trouve les consentements par email
     */
    List<ConsentRecord> findByEmail(String email);
    
    /**
     * Trouve les consentements par type
     */
    List<ConsentRecord> findByConsentType(ConsentRecord.ConsentType consentType);
    
    /**
     * Trouve les consentements actifs
     */
    List<ConsentRecord> findByIsActiveTrue();
    
    /**
     * Trouve les consentements donnés
     */
    List<ConsentRecord> findByConsentGivenTrue();
    
    /**
     * Trouve les consentements retirés
     */
    List<ConsentRecord> findByWithdrawalDateIsNotNull();
    
    /**
     * Trouve les consentements par entreprise et email
     */
    List<ConsentRecord> findByCompanyIdAndEmail(Long companyId, String email);
    
    /**
     * Trouve les consentements par entreprise et type
     */
    List<ConsentRecord> findByCompanyIdAndConsentType(Long companyId, ConsentRecord.ConsentType consentType);
    
    /**
     * Trouve les consentements actifs par entreprise et email
     */
    @Query("SELECT cr FROM ConsentRecord cr WHERE cr.companyId = :companyId AND cr.email = :email " +
           "AND cr.isActive = true AND cr.consentGiven = true AND cr.withdrawalDate IS NULL")
    List<ConsentRecord> findActiveConsentsByCompanyAndEmail(@Param("companyId") Long companyId, @Param("email") String email);
    
    /**
     * Trouve les consentements actifs par entreprise et type
     */
    @Query("SELECT cr FROM ConsentRecord cr WHERE cr.companyId = :companyId AND cr.consentType = :consentType " +
           "AND cr.isActive = true AND cr.consentGiven = true AND cr.withdrawalDate IS NULL")
    List<ConsentRecord> findActiveConsentsByCompanyAndType(@Param("companyId") Long companyId, @Param("consentType") ConsentRecord.ConsentType consentType);
    
    /**
     * Vérifie si un consentement actif existe
     */
    @Query("SELECT COUNT(cr) > 0 FROM ConsentRecord cr WHERE cr.companyId = :companyId AND cr.email = :email " +
           "AND cr.consentType = :consentType AND cr.isActive = true AND cr.consentGiven = true AND cr.withdrawalDate IS NULL")
    boolean existsActiveConsent(@Param("companyId") Long companyId, @Param("email") String email, @Param("consentType") ConsentRecord.ConsentType consentType);
    
    /**
     * Trouve les consentements par méthode
     */
    List<ConsentRecord> findByConsentMethod(ConsentRecord.ConsentMethod consentMethod);
    
    /**
     * Trouve les consentements par adresse IP
     */
    List<ConsentRecord> findByIpAddress(String ipAddress);
    
    /**
     * Trouve les consentements créés après une date
     */
    List<ConsentRecord> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Trouve les consentements créés entre deux dates
     */
    List<ConsentRecord> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve les consentements retirés après une date
     */
    List<ConsentRecord> findByWithdrawalDateAfter(LocalDateTime date);
    
    /**
     * Compte les consentements par type et entreprise
     */
    @Query("SELECT cr.consentType, COUNT(cr) FROM ConsentRecord cr WHERE cr.companyId = :companyId " +
           "AND cr.isActive = true AND cr.consentGiven = true AND cr.withdrawalDate IS NULL GROUP BY cr.consentType")
    List<Object[]> countActiveConsentsByTypeAndCompany(@Param("companyId") Long companyId);
    
    /**
     * Compte les consentements par méthode
     */
    @Query("SELECT cr.consentMethod, COUNT(cr) FROM ConsentRecord cr GROUP BY cr.consentMethod")
    List<Object[]> countByConsentMethod();
    
    /**
     * Trouve les consentements expirés (plus de X jours)
     */
    @Query("SELECT cr FROM ConsentRecord cr WHERE cr.consentDate < :expiryDate AND cr.isActive = true")
    List<ConsentRecord> findExpiredConsents(@Param("expiryDate") LocalDateTime expiryDate);
    
    /**
     * Trouve les consentements par période de consentement
     */
    List<ConsentRecord> findByConsentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve les consentements par période de retrait
     */
    List<ConsentRecord> findByWithdrawalDateBetweenAndIsActiveTrue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve le dernier consentement par email et type
     */
    @Query("SELECT cr FROM ConsentRecord cr WHERE cr.email = :email AND cr.consentType = :consentType " +
           "ORDER BY cr.consentDate DESC")
    List<ConsentRecord> findLatestConsentByEmailAndType(@Param("email") String email, @Param("consentType") ConsentRecord.ConsentType consentType);
}
