package com.ecomptaia.controller;

import com.ecomptaia.entity.Backup;
import com.ecomptaia.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backup")
@CrossOrigin(origins = "*")
public class BackupController {

    @Autowired
    private BackupService backupService;

    // ==================== CRÉATION DE SAUVEGARDE ====================

    /**
     * Créer une nouvelle sauvegarde
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createBackup(
            @RequestParam String backupName,
            @RequestParam String backupType,
            @RequestParam String storageType,
            @RequestParam Long entrepriseId,
            @RequestParam Long utilisateurId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Backup.BackupType type = Backup.BackupType.valueOf(backupType.toUpperCase());
            Backup.StorageType storage = Backup.StorageType.valueOf(storageType.toUpperCase());
            
            Backup backup = backupService.createBackup(backupName, type, storage, entrepriseId, utilisateurId);
            
            response.put("success", true);
            response.put("message", "Sauvegarde créée avec succès");
            response.put("backup", backup);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Démarrer une sauvegarde automatique
     */
    @PostMapping("/start-automatic")
    public ResponseEntity<Map<String, Object>> startAutomaticBackup(
            @RequestParam Long entrepriseId,
            @RequestParam Long utilisateurId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            backupService.startAutomaticBackup(entrepriseId, utilisateurId)
                    .thenAccept(backup -> {
                        System.out.println("Sauvegarde automatique terminée: " + backup.getBackupName());
                    });
            
            response.put("success", true);
            response.put("message", "Sauvegarde automatique démarrée");
            response.put("entrepriseId", entrepriseId);
            response.put("utilisateurId", utilisateurId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Finaliser une sauvegarde
     */
    @PostMapping("/finalize")
    public ResponseEntity<Map<String, Object>> finalizeBackup(@RequestParam Long backupId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Backup backup = backupService.finalizeBackup(backupId);
            
            response.put("success", true);
            response.put("message", "Sauvegarde finalisée avec succès");
            response.put("backup", backup);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== RECHERCHE ET RÉCUPÉRATION ====================

    /**
     * Obtenir toutes les sauvegardes d'une entreprise
     */
    @GetMapping("/by-entreprise")
    public ResponseEntity<Map<String, Object>> getBackupsByEntreprise(@RequestParam Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Backup> backups = backupService.getBackupsByEntreprise(entrepriseId);
            
            response.put("success", true);
            response.put("backups", backups);
            response.put("count", backups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les sauvegardes par statut
     */
    @GetMapping("/by-status")
    public ResponseEntity<Map<String, Object>> getBackupsByStatus(@RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            Backup.BackupStatus backupStatus = Backup.BackupStatus.valueOf(status.toUpperCase());
            List<Backup> backups = backupService.getBackupsByStatus(backupStatus);
            
            response.put("success", true);
            response.put("backups", backups);
            response.put("count", backups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les sauvegardes par type
     */
    @GetMapping("/by-type")
    public ResponseEntity<Map<String, Object>> getBackupsByType(@RequestParam String backupType) {
        Map<String, Object> response = new HashMap<>();
        try {
            Backup.BackupType type = Backup.BackupType.valueOf(backupType.toUpperCase());
            List<Backup> backups = backupService.getBackupsByType(type);
            
            response.put("success", true);
            response.put("backups", backups);
            response.put("count", backups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir la dernière sauvegarde complétée
     */
    @GetMapping("/last-completed")
    public ResponseEntity<Map<String, Object>> getLastCompletedBackup(@RequestParam Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Backup backup = backupService.getLastCompletedBackup(entrepriseId);
            
            response.put("success", true);
            response.put("backup", backup);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Recherche avancée de sauvegardes
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBackups(
            @RequestParam(required = false) Long entrepriseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String backupType,
            @RequestParam(required = false) String storageType,
            @RequestParam(required = false) Boolean encryptionEnabled,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Backup.BackupStatus backupStatus = status != null ? Backup.BackupStatus.valueOf(status.toUpperCase()) : null;
            Backup.BackupType type = backupType != null ? Backup.BackupType.valueOf(backupType.toUpperCase()) : null;
            Backup.StorageType storage = storageType != null ? Backup.StorageType.valueOf(storageType.toUpperCase()) : null;
            
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
            
            List<Backup> backups = backupService.searchBackups(entrepriseId, backupStatus, type, storage, encryptionEnabled, start, end);
            
            response.put("success", true);
            response.put("backups", backups);
            response.put("count", backups.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== RESTAURATION ====================

    /**
     * Démarrer une restauration
     */
    @PostMapping("/restore")
    public ResponseEntity<Map<String, Object>> startRestoration(@RequestParam Long backupId) {
        Map<String, Object> response = new HashMap<>();
        try {
            backupService.startRestoration(backupId)
                    .thenAccept(backup -> {
                        System.out.println("Restauration terminée: " + backup.getBackupName());
                    });
            
            response.put("success", true);
            response.put("message", "Restauration démarrée");
            response.put("backupId", backupId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== SUPPRESSION ====================

    /**
     * Supprimer une sauvegarde
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteBackup(@RequestParam Long backupId) {
        Map<String, Object> response = new HashMap<>();
        try {
            backupService.deleteBackup(backupId);
            
            response.put("success", true);
            response.put("message", "Sauvegarde supprimée avec succès");
            response.put("backupId", backupId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Nettoyer les anciennes sauvegardes
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldBackups(
            @RequestParam Long entrepriseId,
            @RequestParam(defaultValue = "30") int daysToKeep) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            backupService.cleanupOldBackups(entrepriseId, daysToKeep);
            
            response.put("success", true);
            response.put("message", "Nettoyage des anciennes sauvegardes terminé");
            response.put("entrepriseId", entrepriseId);
            response.put("daysToKeep", daysToKeep);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== STATISTIQUES ====================

    /**
     * Obtenir les statistiques de sauvegarde
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getBackupStatistics(@RequestParam Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = backupService.getBackupStatistics(entrepriseId);
            
            response.put("success", true);
            response.put("statistics", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les métriques de performance
     */
    @GetMapping("/performance-metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics(@RequestParam Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> metrics = backupService.getPerformanceMetrics(entrepriseId);
            
            response.put("success", true);
            response.put("metrics", metrics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== UTILITAIRES ====================

    /**
     * Vérifier l'intégrité d'une sauvegarde
     */
    @GetMapping("/verify-integrity")
    public ResponseEntity<Map<String, Object>> verifyBackupIntegrity(@RequestParam Long backupId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isValid = backupService.verifyBackupIntegrity(backupId);
            
            response.put("success", true);
            response.put("isValid", isValid);
            response.put("message", isValid ? "Sauvegarde intègre" : "Sauvegarde corrompue");
            response.put("backupId", backupId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les types de sauvegarde disponibles
     */
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getBackupTypes() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> types = new HashMap<>();
            for (Backup.BackupType type : Backup.BackupType.values()) {
                types.put(type.name(), type.getDescription());
            }
            
            response.put("success", true);
            response.put("types", types);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les types de stockage disponibles
     */
    @GetMapping("/storage-types")
    public ResponseEntity<Map<String, Object>> getStorageTypes() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> types = new HashMap<>();
            for (Backup.StorageType type : Backup.StorageType.values()) {
                types.put(type.name(), type.getDescription());
            }
            
            response.put("success", true);
            response.put("storageTypes", types);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statuts disponibles
     */
    @GetMapping("/statuses")
    public ResponseEntity<Map<String, Object>> getBackupStatuses() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> statuses = new HashMap<>();
            for (Backup.BackupStatus status : Backup.BackupStatus.values()) {
                statuses.put(status.name(), status.getDescription());
            }
            
            response.put("success", true);
            response.put("statuses", statuses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== TEST ====================

    /**
     * Endpoint de test pour créer des données de test
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> createTestData() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> testData = backupService.getTestData();
            
            response.put("success", true);
            response.put("message", "Données de test créées avec succès");
            response.put("data", testData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint de test simple
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Module de sauvegarde opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("features", new String[]{
            "Création de sauvegardes",
            "Sauvegarde automatique",
            "Restauration",
            "Statistiques",
            "Nettoyage automatique",
            "Vérification d'intégrité"
        });
        return ResponseEntity.ok(response);
    }
}







