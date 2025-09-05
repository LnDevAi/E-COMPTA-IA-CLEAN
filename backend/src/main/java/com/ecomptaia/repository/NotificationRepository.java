package com.ecomptaia.repository;

import com.ecomptaia.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Notifications par utilisateur
    List<Notification> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);

    // Notifications par entreprise
    List<Notification> findByCompanyIdAndIsActiveTrueOrderByCreatedAtDesc(Long companyId);

    // Notifications non lues par utilisateur
    List<Notification> findByUserIdAndIsReadFalseAndIsActiveTrueOrderByCreatedAtDesc(Long userId);

    // Notifications non lues par entreprise
    List<Notification> findByCompanyIdAndIsReadFalseAndIsActiveTrueOrderByCreatedAtDesc(Long companyId);

    // Notifications par type
    List<Notification> findByUserIdAndTypeAndIsActiveTrueOrderByCreatedAtDesc(Long userId, String type);

    // Notifications par catégorie
    List<Notification> findByUserIdAndCategoryAndIsActiveTrueOrderByCreatedAtDesc(Long userId, String category);

    // Notifications par priorité
    List<Notification> findByUserIdAndPriorityAndIsActiveTrueOrderByCreatedAtDesc(Long userId, String priority);

    // Notifications par module source
    List<Notification> findByUserIdAndSourceModuleAndIsActiveTrueOrderByCreatedAtDesc(Long userId, String sourceModule);

    // Notifications récentes (dernières 24h)
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt >= :since AND n.isActive = true ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    // Notifications expirées
    @Query("SELECT n FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < :now AND n.isActive = true")
    List<Notification> findExpiredNotifications(@Param("now") LocalDateTime now);

    // Compter les notifications non lues
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false AND n.isActive = true")
    Long countUnreadNotifications(@Param("userId") Long userId);

    // Compter les notifications par type
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.type = :type AND n.isActive = true")
    Long countNotificationsByType(@Param("userId") Long userId, @Param("type") String type);

    // Compter les notifications par catégorie
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.category = :category AND n.isActive = true")
    Long countNotificationsByCategory(@Param("userId") Long userId, @Param("category") String category);

    // Notifications par période
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt BETWEEN :startDate AND :endDate AND n.isActive = true ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByDateRange(@Param("userId") Long userId, 
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);

    // Notifications urgentes
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.priority = 'URGENT' AND n.isActive = true ORDER BY n.createdAt DESC")
    List<Notification> findUrgentNotifications(@Param("userId") Long userId);

    // Notifications par source
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.sourceModule = :sourceModule AND n.sourceId = :sourceId AND n.isActive = true ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsBySource(@Param("userId") Long userId, 
                                                @Param("sourceModule") String sourceModule, 
                                                @Param("sourceId") String sourceId);
}







