package com.ecomptaia.controller;

import com.ecomptaia.entity.Notification;
import com.ecomptaia.entity.NotificationPreference;
import com.ecomptaia.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Contrôleur pour la gestion des notifications en temps réel
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Créer une nouvelle notification
     * POST /api/notifications
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotification(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String title = (String) request.get("title");
            String message = (String) request.get("message");
            String type = (String) request.get("type");
            String category = (String) request.get("category");
            String priority = (String) request.get("priority");
            String sourceModule = (String) request.get("sourceModule");
            String sourceId = (String) request.get("sourceId");
            String actionUrl = (String) request.get("actionUrl");
            String actionType = (String) request.get("actionType");

            Notification notification = notificationService.createNotification(
                userId, companyId, title, message, type, category, priority, 
                sourceModule, sourceId, actionUrl, actionType);

            Map<String, Object> response = new HashMap<>();
            response.put("notification", notification);
            response.put("message", "Notification créée avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la création de la notification");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Récupérer les notifications d'un utilisateur
     * GET /api/notifications/user/{userId}?limit=10
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("notifications", notifications);
            response.put("count", notifications.size());
            response.put("userId", userId);
            response.put("message", "Notifications récupérées avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des notifications");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     * GET /api/notifications/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            Long count = notificationService.countUnreadNotifications(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("notifications", notifications);
            response.put("count", count);
            response.put("userId", userId);
            response.put("message", "Notifications non lues récupérées avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des notifications non lues");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Marquer une notification comme lue
     * PUT /api/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("notification", notification);
            response.put("message", "Notification marquée comme lue");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors du marquage de la notification");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     * PUT /api/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("message", "Toutes les notifications marquées comme lues");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors du marquage des notifications");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Supprimer une notification
     * DELETE /api/notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long notificationId) {
        try {
            Notification notification = notificationService.deleteNotification(notificationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("notification", notification);
            response.put("message", "Notification supprimée avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la suppression de la notification");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Statistiques des notifications
     * GET /api/notifications/user/{userId}/statistics
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getNotificationStatistics(@PathVariable Long userId) {
        try {
            Map<String, Object> statistics = notificationService.getNotificationStatistics(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statistics", statistics);
            response.put("userId", userId);
            response.put("message", "Statistiques récupérées avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des statistiques");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Récupérer les préférences d'un utilisateur
     * GET /api/notifications/user/{userId}/preferences?companyId=1
     */
    @GetMapping("/user/{userId}/preferences")
    public ResponseEntity<Map<String, Object>> getUserPreferences(
            @PathVariable Long userId,
            @RequestParam Long companyId) {
        try {
            List<NotificationPreference> preferences = notificationService.getUserPreferences(userId, companyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("preferences", preferences);
            response.put("count", preferences.size());
            response.put("userId", userId);
            response.put("companyId", companyId);
            response.put("message", "Préférences récupérées avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des préférences");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Mettre à jour les préférences de notification
     * PUT /api/notifications/user/{userId}/preferences
     */
    @PutMapping("/user/{userId}/preferences")
    public ResponseEntity<Map<String, Object>> updatePreference(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String category = (String) request.get("category");
            String type = (String) request.get("type");
            Boolean enabled = request.get("enabled") != null ? Boolean.valueOf(request.get("enabled").toString()) : null;
            Boolean emailEnabled = request.get("emailEnabled") != null ? Boolean.valueOf(request.get("emailEnabled").toString()) : null;
            Boolean pushEnabled = request.get("pushEnabled") != null ? Boolean.valueOf(request.get("pushEnabled").toString()) : null;
            Boolean inAppEnabled = request.get("inAppEnabled") != null ? Boolean.valueOf(request.get("inAppEnabled").toString()) : null;
            String frequency = (String) request.get("frequency");
            String priority = (String) request.get("priority");

            NotificationPreference preference = notificationService.updatePreference(
                userId, companyId, category, type, enabled, emailEnabled, pushEnabled, 
                inAppEnabled, frequency, priority);

            Map<String, Object> response = new HashMap<>();
            response.put("preference", preference);
            response.put("message", "Préférence mise à jour avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la mise à jour de la préférence");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer des préférences par défaut pour un utilisateur
     * POST /api/notifications/user/{userId}/preferences/default
     */
    @PostMapping("/user/{userId}/preferences/default")
    public ResponseEntity<Map<String, Object>> createDefaultPreferences(
            @PathVariable Long userId,
            @RequestParam Long companyId) {
        try {
            notificationService.createDefaultPreferences(userId, companyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("companyId", companyId);
            response.put("message", "Préférences par défaut créées avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la création des préférences par défaut");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Endpoints pour les alertes automatiques

    /**
     * Créer une alerte d'échéance de paiement
     * POST /api/notifications/alerts/payment-due
     */
    @PostMapping("/alerts/payment-due")
    public ResponseEntity<Map<String, Object>> createPaymentDueAlert(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String thirdPartyName = (String) request.get("thirdPartyName");
            String dueDate = (String) request.get("dueDate");
            String amount = (String) request.get("amount");

            notificationService.createPaymentDueAlert(userId, companyId, thirdPartyName, dueDate, amount);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Alerte d'échéance créée avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la création de l'alerte");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer une alerte de solde négatif
     * POST /api/notifications/alerts/negative-balance
     */
    @PostMapping("/alerts/negative-balance")
    public ResponseEntity<Map<String, Object>> createNegativeBalanceAlert(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String accountName = (String) request.get("accountName");
            String balance = (String) request.get("balance");

            notificationService.createNegativeBalanceAlert(userId, companyId, accountName, balance);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Alerte de solde négatif créée avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la création de l'alerte");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer une alerte de sécurité
     * POST /api/notifications/alerts/security
     */
    @PostMapping("/alerts/security")
    public ResponseEntity<Map<String, Object>> createSecurityAlert(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String event = (String) request.get("event");
            String details = (String) request.get("details");

            notificationService.createSecurityAlert(userId, companyId, event, details);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Alerte de sécurité créée avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la création de l'alerte");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Informations sur le système de notifications
     * GET /api/notifications/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getNotificationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("system", "Système de Notifications en Temps Réel");
        info.put("version", "1.0");
        info.put("features", new String[]{
            "Notifications en temps réel",
            "Gestion des préférences utilisateur",
            "Alertes automatiques",
            "Notifications par email",
            "Notifications push",
            "Notifications in-app",
            "Statistiques et rapports"
        });
        info.put("types", new String[]{"SYSTEM", "ALERT", "REMINDER", "INFO", "WARNING", "ERROR"});
        info.put("categories", new String[]{"ACCOUNTING", "THIRD_PARTY", "FINANCIAL", "SECURITY", "SYSTEM"});
        info.put("priorities", new String[]{"LOW", "MEDIUM", "HIGH", "URGENT"});
        info.put("frequencies", new String[]{"IMMEDIATE", "DAILY", "WEEKLY", "NEVER"});
        
        Map<String, Object> response = new HashMap<>();
        response.put("info", info);
        response.put("message", "Informations sur le système de notifications");
        response.put("status", "SUCCESS");

        return ResponseEntity.ok(response);
    }
}
