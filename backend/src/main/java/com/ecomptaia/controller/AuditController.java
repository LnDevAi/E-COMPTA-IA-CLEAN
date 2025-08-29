package com.ecomptaia.controller;

import com.ecomptaia.entity.AuditLog;
import com.ecomptaia.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour l'audit trail - Traçabilité de toutes les actions importantes
 */
@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditController {

    @Autowired
    private AuditService auditService;

    // === ENDPOINTS DE CONSULTATION ===

    /**
     * Obtenir tous les logs d'audit
     */
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(auditService.getAuditLogsByPeriod(
            LocalDateTime.now().minusDays(30), LocalDateTime.now()));
    }

    /**
     * Obtenir les logs d'audit par utilisateur
     */
    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditService.getAuditLogsByUser(userId));
    }

    /**
     * Obtenir les logs d'audit par entreprise
     */
    @GetMapping("/logs/company/{companyId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(auditService.getAuditLogsByCompany(companyId));
    }

    /**
     * Obtenir les logs d'audit par type d'action
     */
    @GetMapping("/logs/action/{actionType}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByActionType(@PathVariable AuditLog.ActionType actionType) {
        return ResponseEntity.ok(auditService.getAuditLogsByActionType(actionType));
    }

    /**
     * Obtenir les logs d'audit par période
     */
    @GetMapping("/logs/period")
    public ResponseEntity<List<AuditLog>> getAuditLogsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(auditService.getAuditLogsByPeriod(startDate, endDate));
    }

    /**
     * Obtenir les logs d'audit par entité
     */
    @GetMapping("/logs/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByEntity(
            @PathVariable String entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.getAuditLogsByEntity(entityType, entityId));
    }

    // === ENDPOINTS DE RAPPORTS ===

    /**
     * Générer un rapport d'activité utilisateur
     */
    @GetMapping("/reports/user/{userId}")
    public ResponseEntity<Map<String, Object>> generateUserActivityReport(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.generateUserActivityReport(userId, since));
    }

    /**
     * Générer un rapport d'activité entreprise
     */
    @GetMapping("/reports/company/{companyId}")
    public ResponseEntity<Map<String, Object>> generateCompanyActivityReport(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.generateCompanyActivityReport(companyId, since));
    }

    /**
     * Générer un rapport de sécurité
     */
    @GetMapping("/reports/security")
    public ResponseEntity<Map<String, Object>> generateSecurityReport(
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.generateSecurityReport(since));
    }

    /**
     * Générer un rapport d'audit complet
     */
    @GetMapping("/reports/complete")
    public ResponseEntity<Map<String, Object>> generateCompleteAuditReport(
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.generateCompleteAuditReport(since));
    }

    /**
     * Obtenir les statistiques d'audit
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAuditStatistics() {
        return ResponseEntity.ok(auditService.getAuditStatistics());
    }

    // === ENDPOINTS SPÉCIALISÉS ===

    /**
     * Obtenir les événements de sécurité
     */
    @GetMapping("/security/events")
    public ResponseEntity<List<AuditLog>> getSecurityEvents(
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.getSecurityEvents(since));
    }

    /**
     * Obtenir les modifications d'entités
     */
    @GetMapping("/entity/modifications/{entityType}")
    public ResponseEntity<List<AuditLog>> getEntityModifications(
            @PathVariable String entityType,
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.getEntityModifications(entityType, since));
    }

    /**
     * Obtenir les actions d'abonnement
     */
    @GetMapping("/subscription/actions")
    public ResponseEntity<List<AuditLog>> getSubscriptionActions(
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.getSubscriptionActions(since));
    }

    /**
     * Obtenir les actions de paiement
     */
    @GetMapping("/payment/actions")
    public ResponseEntity<List<AuditLog>> getPaymentActions(
            @RequestParam(defaultValue = "30") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.getPaymentActions(since));
    }

    /**
     * Obtenir les actions de document
     */
    @GetMapping("/document/actions")
    public ResponseEntity<List<AuditLog>> getDocumentActions(
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return ResponseEntity.ok(auditService.getDocumentActions(since));
    }

    // === ENDPOINTS DE TEST ===

    /**
     * Tester l'enregistrement d'une action d'audit
     */
    @PostMapping("/test/log-action")
    public ResponseEntity<Map<String, Object>> testLogAction(
            @RequestParam AuditLog.ActionType actionType,
            @RequestParam String entityType,
            @RequestParam String description,
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "test-user") String username) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            AuditLog auditLog = auditService.logAction(actionType, entityType, description, userId, username);
            
            result.put("status", "SUCCESS");
            result.put("message", "Action d'audit enregistrée avec succès");
            result.put("auditLog", auditLog);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Tester l'enregistrement d'une action d'audit avec entreprise
     */
    @PostMapping("/test/log-action-company")
    public ResponseEntity<Map<String, Object>> testLogActionWithCompany(
            @RequestParam AuditLog.ActionType actionType,
            @RequestParam String entityType,
            @RequestParam String description,
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "test-user") String username,
            @RequestParam(defaultValue = "1") Long companyId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            AuditLog auditLog = auditService.logAction(actionType, entityType, null, description, userId, username, companyId);
            
            result.put("status", "SUCCESS");
            result.put("message", "Action d'audit avec entreprise enregistrée avec succès");
            result.put("auditLog", auditLog);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Tester l'enregistrement d'une action d'audit échouée
     */
    @PostMapping("/test/log-failed-action")
    public ResponseEntity<Map<String, Object>> testLogFailedAction(
            @RequestParam AuditLog.ActionType actionType,
            @RequestParam String entityType,
            @RequestParam String description,
            @RequestParam String errorMessage,
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "test-user") String username) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            AuditLog auditLog = auditService.logFailedAction(actionType, entityType, description, errorMessage, userId, username);
            
            result.put("status", "SUCCESS");
            result.put("message", "Action d'audit échouée enregistrée avec succès");
            result.put("auditLog", auditLog);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Générer des données de test pour l'audit
     */
    @PostMapping("/test/generate-test-data")
    public ResponseEntity<Map<String, Object>> generateTestData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Générer des actions de test variées
            auditService.logAction(AuditLog.ActionType.LOGIN, "User", "Connexion utilisateur test", 1L, "test-user");
            auditService.logAction(AuditLog.ActionType.CREATE, "JournalEntry", 1L, "Création d'une écriture comptable", 1L, "test-user", 1L);
            auditService.logAction(AuditLog.ActionType.UPDATE, "Company", 1L, "Modification des informations entreprise", 1L, "test-user", 1L);
            auditService.logAction(AuditLog.ActionType.EXPORT, "FinancialStatement", 1L, "Export du bilan", 1L, "test-user", 1L);
            auditService.logAction(AuditLog.ActionType.SUBSCRIPTION_CREATE, "Subscription", 1L, "Création d'un abonnement", 1L, "test-user", 1L);
            auditService.logAction(AuditLog.ActionType.PAYMENT_SUCCESS, "Payment", 1L, "Paiement réussi", 1L, "test-user", 1L);
            auditService.logAction(AuditLog.ActionType.DOCUMENT_UPLOAD, "Document", 1L, "Téléchargement d'un document", 1L, "test-user", 1L);
            auditService.logAction(AuditLog.ActionType.LOGOUT, "User", "Déconnexion utilisateur test", 1L, "test-user");
            
            result.put("status", "SUCCESS");
            result.put("message", "Données de test générées avec succès");
            result.put("actionsCreated", 8);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de la génération: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Informations sur le système d'audit
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAuditInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("systeme", "Système d'Audit Trail E-COMPTA-IA");
        info.put("version", "1.0");
        info.put("description", "Traçabilité complète de toutes les actions importantes");
        
        Map<String, String> typesActions = new HashMap<>();
        typesActions.put("CREATE", "Création d'entité");
        typesActions.put("UPDATE", "Modification d'entité");
        typesActions.put("DELETE", "Suppression d'entité");
        typesActions.put("LOGIN", "Connexion utilisateur");
        typesActions.put("LOGOUT", "Déconnexion utilisateur");
        typesActions.put("EXPORT", "Export de données");
        typesActions.put("IMPORT", "Import de données");
        typesActions.put("VALIDATE", "Validation");
        typesActions.put("REJECT", "Rejet");
        typesActions.put("APPROVE", "Approbation");
        typesActions.put("SUBSCRIPTION_CREATE", "Création d'abonnement");
        typesActions.put("SUBSCRIPTION_UPDATE", "Modification d'abonnement");
        typesActions.put("SUBSCRIPTION_CANCEL", "Annulation d'abonnement");
        typesActions.put("PAYMENT_SUCCESS", "Paiement réussi");
        typesActions.put("PAYMENT_FAILED", "Paiement échoué");
        typesActions.put("DOCUMENT_UPLOAD", "Téléchargement de document");
        typesActions.put("DOCUMENT_DELETE", "Suppression de document");
        typesActions.put("SECURITY_EVENT", "Événement de sécurité");
        info.put("typesActions", typesActions);
        
        Map<String, String> fonctionnalites = new HashMap<>();
        fonctionnalites.put("traçabilité", "Enregistrement automatique de toutes les actions");
        fonctionnalites.put("recherche", "Recherche avancée par critères multiples");
        fonctionnalites.put("rapports", "Génération de rapports d'activité");
        fonctionnalites.put("securite", "Surveillance des événements de sécurité");
        fonctionnalites.put("conformite", "Conformité réglementaire et légale");
        info.put("fonctionnalites", fonctionnalites);
        
        return ResponseEntity.ok(info);
    }
}
