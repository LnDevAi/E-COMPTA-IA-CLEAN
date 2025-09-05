package com.ecomptaia.repository;

import com.ecomptaia.entity.SecurityAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SecurityAuditRepository extends JpaRepository<SecurityAudit, Long> {

    // ==================== RECHERCHE PAR UTILISATEUR ====================

    List<SecurityAudit> findByUserIdOrderByTimestampDesc(Long userId);

    List<SecurityAudit> findByUsernameOrderByTimestampDesc(String username);

    List<SecurityAudit> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== RECHERCHE PAR STATUT ====================

    List<SecurityAudit> findByStatusOrderByTimestampDesc(SecurityAudit.AuditStatus status);

    List<SecurityAudit> findByStatusAndTimestampBetweenOrderByTimestampDesc(
            SecurityAudit.AuditStatus status, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== RECHERCHE PAR NIVEAU DE RISQUE ====================

    List<SecurityAudit> findByRiskLevelOrderByTimestampDesc(SecurityAudit.RiskLevel riskLevel);

    List<SecurityAudit> findByRiskLevelAndTimestampBetweenOrderByTimestampDesc(
            SecurityAudit.RiskLevel riskLevel, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== RECHERCHE PAR ACTION ====================

    List<SecurityAudit> findByActionOrderByTimestampDesc(String action);

    List<SecurityAudit> findByActionAndTimestampBetweenOrderByTimestampDesc(
            String action, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== RECHERCHE PAR RESSOURCE ====================

    List<SecurityAudit> findByResourceOrderByTimestampDesc(String resource);

    List<SecurityAudit> findByResourceAndTimestampBetweenOrderByTimestampDesc(
            String resource, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== RECHERCHE PAR IP ====================

    List<SecurityAudit> findByIpAddressOrderByTimestampDesc(String ipAddress);

    List<SecurityAudit> findByIpAddressAndTimestampBetweenOrderByTimestampDesc(
            String ipAddress, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== RECHERCHE PAR ENTREPRISE ====================

    List<SecurityAudit> findByEntrepriseIdOrderByTimestampDesc(Long entrepriseId);

    List<SecurityAudit> findByEntrepriseIdAndTimestampBetweenOrderByTimestampDesc(
            Long entrepriseId, LocalDateTime startDate, LocalDateTime endDate);

    // ==================== RECHERCHE PAR PÉRIODE ====================

    List<SecurityAudit> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate);

    // ==================== STATISTIQUES ====================

    @Query("SELECT COUNT(sa) FROM SecurityAudit sa WHERE sa.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(sa) FROM SecurityAudit sa WHERE sa.entrepriseId = :entrepriseId AND sa.status = :status")
    Long countByEntrepriseIdAndStatus(@Param("entrepriseId") Long entrepriseId, @Param("status") SecurityAudit.AuditStatus status);

    @Query("SELECT COUNT(sa) FROM SecurityAudit sa WHERE sa.entrepriseId = :entrepriseId AND sa.riskLevel = :riskLevel")
    Long countByEntrepriseIdAndRiskLevel(@Param("entrepriseId") Long entrepriseId, @Param("riskLevel") SecurityAudit.RiskLevel riskLevel);

    @Query("SELECT COUNT(sa) FROM SecurityAudit sa WHERE sa.entrepriseId = :entrepriseId AND sa.timestamp BETWEEN :startDate AND :endDate")
    Long countByEntrepriseIdAndTimestampBetween(@Param("entrepriseId") Long entrepriseId, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    // ==================== RECHERCHE AVANCÉE ====================

    @Query("SELECT sa FROM SecurityAudit sa WHERE sa.entrepriseId = :entrepriseId " +
           "AND (:userId IS NULL OR sa.userId = :userId) " +
           "AND (:status IS NULL OR sa.status = :status) " +
           "AND (:riskLevel IS NULL OR sa.riskLevel = :riskLevel) " +
           "AND (:action IS NULL OR sa.action LIKE %:action%) " +
           "AND (:resource IS NULL OR sa.resource LIKE %:resource%) " +
           "AND (:ipAddress IS NULL OR sa.ipAddress = :ipAddress) " +
           "AND sa.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY sa.timestamp DESC")
    List<SecurityAudit> findAuditsWithCriteria(@Param("entrepriseId") Long entrepriseId,
                                              @Param("userId") Long userId,
                                              @Param("status") SecurityAudit.AuditStatus status,
                                              @Param("riskLevel") SecurityAudit.RiskLevel riskLevel,
                                              @Param("action") String action,
                                              @Param("resource") String resource,
                                              @Param("ipAddress") String ipAddress,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    // ==================== DÉTECTION D'INTRUSION ====================

    @Query("SELECT sa.ipAddress, COUNT(sa) as count FROM SecurityAudit sa " +
           "WHERE sa.entrepriseId = :entrepriseId AND sa.status = 'FAILURE' " +
           "AND sa.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY sa.ipAddress HAVING COUNT(sa) > :threshold")
    List<Object[]> findSuspiciousIPs(@Param("entrepriseId") Long entrepriseId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("threshold") Long threshold);

    @Query("SELECT sa FROM SecurityAudit sa WHERE sa.entrepriseId = :entrepriseId " +
           "AND sa.userId = :userId AND sa.status = 'FAILURE' " +
           "AND sa.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY sa.timestamp DESC")
    List<SecurityAudit> findFailedLoginAttempts(@Param("entrepriseId") Long entrepriseId,
                                               @Param("userId") Long userId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}







