package com.ecomptaia.repository;

import com.ecomptaia.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'audit trail
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Recherche par utilisateur
    List<AuditLog> findByUserIdOrderByTimestampDesc(Long userId);
    
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);

    // Recherche par entreprise
    List<AuditLog> findByCompanyIdOrderByTimestampDesc(Long companyId);

    // Recherche par type d'action
    List<AuditLog> findByActionTypeOrderByTimestampDesc(AuditLog.ActionType actionType);

    // Recherche par type d'entité
    List<AuditLog> findByEntityTypeOrderByTimestampDesc(String entityType);

    // Recherche par statut
    List<AuditLog> findByStatusOrderByTimestampDesc(AuditLog.AuditStatus status);

    // Recherche par période
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate);

    // Recherche par utilisateur et période
    List<AuditLog> findByUserIdAndTimestampBetweenOrderByTimestampDesc(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    // Recherche par entreprise et période
    List<AuditLog> findByCompanyIdAndTimestampBetweenOrderByTimestampDesc(Long companyId, LocalDateTime startDate, LocalDateTime endDate);

    // Recherche par type d'action et période
    List<AuditLog> findByActionTypeAndTimestampBetweenOrderByTimestampDesc(AuditLog.ActionType actionType, LocalDateTime startDate, LocalDateTime endDate);

    // Recherche par entité spécifique
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);

    // Recherche par adresse IP
    List<AuditLog> findByIpAddressOrderByTimestampDesc(String ipAddress);

    // Recherche par session
    List<AuditLog> findBySessionIdOrderByTimestampDesc(String sessionId);

    // Requêtes personnalisées pour les rapports

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId AND a.timestamp >= :startDate")
    Long countUserActionsSince(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.companyId = :companyId AND a.actionType = :actionType AND a.timestamp >= :startDate")
    Long countCompanyActionsByTypeSince(@Param("companyId") Long companyId, @Param("actionType") AuditLog.ActionType actionType, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.actionType, COUNT(a) FROM AuditLog a WHERE a.timestamp >= :startDate GROUP BY a.actionType ORDER BY COUNT(a) DESC")
    List<Object[]> getActionTypeStatsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.entityType, COUNT(a) FROM AuditLog a WHERE a.timestamp >= :startDate GROUP BY a.entityType ORDER BY COUNT(a) DESC")
    List<Object[]> getEntityTypeStatsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.username, COUNT(a) FROM AuditLog a WHERE a.timestamp >= :startDate GROUP BY a.username ORDER BY COUNT(a) DESC")
    List<Object[]> getUserActivityStatsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.ipAddress, COUNT(a) FROM AuditLog a WHERE a.timestamp >= :startDate GROUP BY a.ipAddress ORDER BY COUNT(a) DESC")
    List<Object[]> getIpActivityStatsSince(@Param("startDate") LocalDateTime startDate);

    // Recherche des actions de sécurité
    @Query("SELECT a FROM AuditLog a WHERE a.actionType IN ('LOGIN', 'LOGOUT', 'SECURITY_EVENT') AND a.timestamp >= :startDate ORDER BY a.timestamp DESC")
    List<AuditLog> findSecurityEventsSince(@Param("startDate") LocalDateTime startDate);

    // Recherche des modifications d'entités
    @Query("SELECT a FROM AuditLog a WHERE a.actionType IN ('CREATE', 'UPDATE', 'DELETE') AND a.entityType = :entityType AND a.timestamp >= :startDate ORDER BY a.timestamp DESC")
    List<AuditLog> findEntityModificationsSince(@Param("entityType") String entityType, @Param("startDate") LocalDateTime startDate);

    // Recherche des actions d'abonnement
    @Query("SELECT a FROM AuditLog a WHERE a.actionType LIKE 'SUBSCRIPTION_%' AND a.timestamp >= :startDate ORDER BY a.timestamp DESC")
    List<AuditLog> findSubscriptionActionsSince(@Param("startDate") LocalDateTime startDate);

    // Recherche des actions de paiement
    @Query("SELECT a FROM AuditLog a WHERE a.actionType LIKE 'PAYMENT_%' AND a.timestamp >= :startDate ORDER BY a.timestamp DESC")
    List<AuditLog> findPaymentActionsSince(@Param("startDate") LocalDateTime startDate);

    // Recherche des actions de document
    @Query("SELECT a FROM AuditLog a WHERE a.actionType LIKE 'DOCUMENT_%' AND a.timestamp >= :startDate ORDER BY a.timestamp DESC")
    List<AuditLog> findDocumentActionsSince(@Param("startDate") LocalDateTime startDate);

    // Statistiques par jour
    @Query("SELECT CAST(a.timestamp AS DATE), COUNT(a) FROM AuditLog a WHERE a.timestamp >= :startDate GROUP BY CAST(a.timestamp AS DATE) ORDER BY CAST(a.timestamp AS DATE) DESC")
    List<Object[]> getDailyStatsSince(@Param("startDate") LocalDateTime startDate);

    // Statistiques par heure
    @Query("SELECT EXTRACT(HOUR FROM a.timestamp), COUNT(a) FROM AuditLog a WHERE a.timestamp >= :startDate GROUP BY EXTRACT(HOUR FROM a.timestamp) ORDER BY EXTRACT(HOUR FROM a.timestamp)")
    List<Object[]> getHourlyStatsSince(@Param("startDate") LocalDateTime startDate);

    // Recherche des actions suspectes (plusieurs tentatives de connexion échouées)
    @Query("SELECT a.username, COUNT(a) FROM AuditLog a WHERE a.actionType = 'LOGIN' AND a.status = 'FAILED' AND a.timestamp >= :startDate GROUP BY a.username HAVING COUNT(a) > :threshold")
    List<Object[]> findSuspiciousLoginAttempts(@Param("startDate") LocalDateTime startDate, @Param("threshold") Long threshold);

    // Recherche des actions par utilisateur et type
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.actionType = :actionType ORDER BY a.timestamp DESC")
    List<AuditLog> findByUserIdAndActionType(@Param("userId") Long userId, @Param("actionType") AuditLog.ActionType actionType);

    // Recherche des actions par entreprise et type
    @Query("SELECT a FROM AuditLog a WHERE a.companyId = :companyId AND a.actionType = :actionType ORDER BY a.timestamp DESC")
    List<AuditLog> findByCompanyIdAndActionType(@Param("companyId") Long companyId, @Param("actionType") AuditLog.ActionType actionType);
}
