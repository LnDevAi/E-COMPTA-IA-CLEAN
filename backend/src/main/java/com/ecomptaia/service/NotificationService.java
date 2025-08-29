package com.ecomptaia.service;

import com.ecomptaia.entity.Notification;
import com.ecomptaia.entity.NotificationPreference;
import com.ecomptaia.repository.NotificationRepository;
import com.ecomptaia.repository.NotificationPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service de gestion des notifications en temps réel
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Créer une nouvelle notification
     */
    public Notification createNotification(Long userId, Long companyId, String title, String message, 
                                         String type, String category) {
        // Vérifier les préférences de l'utilisateur
        if (!shouldSendNotification(userId, companyId, category, type)) {
            return null;
        }

        Notification notification = new Notification(userId, companyId, title, message, type, category);
        notification.setPriority("MEDIUM");
        notification.setCreatedAt(LocalDateTime.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Envoyer les notifications selon les préférences
        sendNotificationAsync(savedNotification);
        
        return savedNotification;
    }

    /**
     * Créer une notification avec plus d'options
     */
    public Notification createNotification(Long userId, Long companyId, String title, String message, 
                                         String type, String category, String priority, String sourceModule, 
                                         String sourceId, String actionUrl, String actionType) {
        // Vérifier les préférences de l'utilisateur
        if (!shouldSendNotification(userId, companyId, category, type)) {
            return null;
        }

        Notification notification = new Notification(userId, companyId, title, message, type, category);
        notification.setPriority(priority != null ? priority : "MEDIUM");
        notification.setSourceModule(sourceModule);
        notification.setSourceId(sourceId);
        notification.setActionUrl(actionUrl);
        notification.setActionType(actionType);
        notification.setCreatedAt(LocalDateTime.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Envoyer les notifications selon les préférences
        sendNotificationAsync(savedNotification);
        
        return savedNotification;
    }

    /**
     * Marquer une notification comme lue
     */
    public Notification markAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        }
        throw new RuntimeException("Notification non trouvée");
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseAndIsActiveTrueOrderByCreatedAtDesc(userId);
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    /**
     * Supprimer une notification (désactivation)
     */
    public Notification deleteNotification(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsActive(false);
            return notificationRepository.save(notification);
        }
        throw new RuntimeException("Notification non trouvée");
    }

    /**
     * Récupérer les notifications d'un utilisateur
     */
    public List<Notification> getUserNotifications(Long userId, int limit) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
        return notifications.stream().limit(limit).toList();
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseAndIsActiveTrueOrderByCreatedAtDesc(userId);
    }

    /**
     * Récupérer les notifications par type
     */
    public List<Notification> getNotificationsByType(Long userId, String type) {
        return notificationRepository.findByUserIdAndTypeAndIsActiveTrueOrderByCreatedAtDesc(userId, type);
    }

    /**
     * Récupérer les notifications par catégorie
     */
    public List<Notification> getNotificationsByCategory(Long userId, String category) {
        return notificationRepository.findByUserIdAndCategoryAndIsActiveTrueOrderByCreatedAtDesc(userId, category);
    }

    /**
     * Récupérer les notifications urgentes
     */
    public List<Notification> getUrgentNotifications(Long userId) {
        return notificationRepository.findUrgentNotifications(userId);
    }

    /**
     * Compter les notifications non lues
     */
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }

    /**
     * Statistiques des notifications
     */
    public Map<String, Object> getNotificationStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalNotifications", notificationRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId).size());
        stats.put("unreadNotifications", notificationRepository.countUnreadNotifications(userId));
        stats.put("urgentNotifications", notificationRepository.findUrgentNotifications(userId).size());
        
        // Statistiques par type
        stats.put("systemNotifications", notificationRepository.countNotificationsByType(userId, "SYSTEM"));
        stats.put("alertNotifications", notificationRepository.countNotificationsByType(userId, "ALERT"));
        stats.put("reminderNotifications", notificationRepository.countNotificationsByType(userId, "REMINDER"));
        stats.put("infoNotifications", notificationRepository.countNotificationsByType(userId, "INFO"));
        
        // Statistiques par catégorie
        stats.put("accountingNotifications", notificationRepository.countNotificationsByCategory(userId, "ACCOUNTING"));
        stats.put("thirdPartyNotifications", notificationRepository.countNotificationsByCategory(userId, "THIRD_PARTY"));
        stats.put("financialNotifications", notificationRepository.countNotificationsByCategory(userId, "FINANCIAL"));
        stats.put("securityNotifications", notificationRepository.countNotificationsByCategory(userId, "SECURITY"));
        
        return stats;
    }

    /**
     * Créer des préférences par défaut pour un utilisateur
     */
    public void createDefaultPreferences(Long userId, Long companyId) {
        String[] categories = {"ACCOUNTING", "THIRD_PARTY", "FINANCIAL", "SECURITY", "SYSTEM"};
        String[] types = {"SYSTEM", "ALERT", "REMINDER", "INFO", "WARNING", "ERROR"};
        
        for (String category : categories) {
            for (String type : types) {
                if (!preferenceRepository.existsByUserAndCategoryAndType(userId, companyId, category, type)) {
                    NotificationPreference preference = new NotificationPreference(userId, companyId, category, type);
                    preferenceRepository.save(preference);
                }
            }
        }
    }

    /**
     * Mettre à jour les préférences de notification
     */
    public NotificationPreference updatePreference(Long userId, Long companyId, String category, String type, 
                                                  Boolean enabled, Boolean emailEnabled, Boolean pushEnabled, 
                                                  Boolean inAppEnabled, String frequency, String priority) {
        Optional<NotificationPreference> preferenceOpt = preferenceRepository.findByUserIdAndCompanyIdAndCategoryAndType(
            userId, companyId, category, type);
        
        if (preferenceOpt.isPresent()) {
            NotificationPreference preference = preferenceOpt.get();
            if (enabled != null) preference.setEnabled(enabled);
            if (emailEnabled != null) preference.setEmailEnabled(emailEnabled);
            if (pushEnabled != null) preference.setPushEnabled(pushEnabled);
            if (inAppEnabled != null) preference.setInAppEnabled(inAppEnabled);
            if (frequency != null) preference.setFrequency(frequency);
            if (priority != null) preference.setPriority(priority);
            preference.setUpdatedAt(LocalDateTime.now());
            return preferenceRepository.save(preference);
        } else {
            NotificationPreference preference = new NotificationPreference(userId, companyId, category, type);
            if (enabled != null) preference.setEnabled(enabled);
            if (emailEnabled != null) preference.setEmailEnabled(emailEnabled);
            if (pushEnabled != null) preference.setPushEnabled(pushEnabled);
            if (inAppEnabled != null) preference.setInAppEnabled(inAppEnabled);
            if (frequency != null) preference.setFrequency(frequency);
            if (priority != null) preference.setPriority(priority);
            return preferenceRepository.save(preference);
        }
    }

    /**
     * Récupérer les préférences d'un utilisateur
     */
    public List<NotificationPreference> getUserPreferences(Long userId, Long companyId) {
        return preferenceRepository.findByUserIdAndCompanyId(userId, companyId);
    }

    /**
     * Nettoyer les notifications expirées
     */
    @Async
    public CompletableFuture<Void> cleanupExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository.findExpiredNotifications(LocalDateTime.now());
        for (Notification notification : expiredNotifications) {
            notification.setIsActive(false);
            notificationRepository.save(notification);
        }
        return CompletableFuture.completedFuture(null);
    }

    // Méthodes privées utilitaires

    /**
     * Vérifier si une notification doit être envoyée selon les préférences
     */
    private boolean shouldSendNotification(Long userId, Long companyId, String category, String type) {
        Optional<NotificationPreference> preferenceOpt = preferenceRepository.findByUserIdAndCompanyIdAndCategoryAndType(
            userId, companyId, category, type);
        
        if (preferenceOpt.isPresent()) {
            return preferenceOpt.get().getEnabled();
        }
        
        // Si aucune préférence n'existe, créer les préférences par défaut et accepter
        createDefaultPreferences(userId, companyId);
        return true;
    }

    /**
     * Envoyer les notifications selon les préférences (email, push, in-app)
     */
    @Async
    private CompletableFuture<Void> sendNotificationAsync(Notification notification) {
        Optional<NotificationPreference> preferenceOpt = preferenceRepository.findByUserIdAndCompanyIdAndCategoryAndType(
            notification.getUserId(), notification.getCompanyId(), notification.getCategory(), notification.getType());
        
        if (preferenceOpt.isPresent()) {
            NotificationPreference preference = preferenceOpt.get();
            
            // Envoyer par email si activé
            if (preference.getEmailEnabled()) {
                sendEmailNotification(notification);
            }
            
            // Envoyer push notification si activé
            if (preference.getPushEnabled()) {
                sendPushNotification(notification);
            }
            
            // Notification in-app toujours disponible
            // (déjà créée en base de données)
        }
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Envoyer une notification par email
     */
    private void sendEmailNotification(Notification notification) {
        try {
            String subject = "Notification: " + notification.getTitle();
            String body = notification.getMessage();
            
            // Ici on utiliserait le service email existant
            // emailService.sendNotificationEmail(notification.getUserId(), subject, body);
            
        } catch (Exception e) {
            // Log l'erreur mais ne pas faire échouer le processus
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }

    /**
     * Envoyer une notification push
     */
    private void sendPushNotification(Notification notification) {
        try {
            // Ici on implémenterait l'envoi de push notifications
            // (Firebase, WebPush, etc.)
            
        } catch (Exception e) {
            // Log l'erreur mais ne pas faire échouer le processus
            System.err.println("Erreur lors de l'envoi de la push notification: " + e.getMessage());
        }
    }

    // Méthodes pour les alertes automatiques

    /**
     * Créer une alerte pour échéance de paiement
     */
    public void createPaymentDueAlert(Long userId, Long companyId, String thirdPartyName, String dueDate, String amount) {
        String title = "Échéance de paiement";
        String message = String.format("Le paiement de %s pour %s est dû le %s", amount, thirdPartyName, dueDate);
        
        createNotification(userId, companyId, title, message, "ALERT", "THIRD_PARTY", 
                          "HIGH", "THIRD_PARTY", null, "/third-parties", "VIEW");
    }

    /**
     * Créer une alerte pour solde négatif
     */
    public void createNegativeBalanceAlert(Long userId, Long companyId, String accountName, String balance) {
        String title = "Solde négatif détecté";
        String message = String.format("Le compte %s présente un solde négatif de %s", accountName, balance);
        
        createNotification(userId, companyId, title, message, "WARNING", "FINANCIAL", 
                          "HIGH", "ACCOUNTING", null, "/accounts", "VIEW");
    }

    /**
     * Créer une alerte de sécurité
     */
    public void createSecurityAlert(Long userId, Long companyId, String event, String details) {
        String title = "Alerte de sécurité";
        String message = String.format("Événement de sécurité: %s - %s", event, details);
        
        createNotification(userId, companyId, title, message, "ERROR", "SECURITY", 
                          "URGENT", "SECURITY", null, "/security", "VIEW");
    }

    /**
     * Créer une notification système
     */
    public void createSystemNotification(Long userId, Long companyId, String title, String message) {
        createNotification(userId, companyId, title, message, "SYSTEM", "SYSTEM", 
                          "MEDIUM", "SYSTEM", null, null, null);
    }
}
