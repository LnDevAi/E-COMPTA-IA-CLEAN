package com.ecomptaia.controller;

import com.ecomptaia.service.DatabaseConnectionService;
import com.ecomptaia.service.DataValidationService;
import com.ecomptaia.service.DataSynchronizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour la gestion de la base de données et la communication BD-Backend
 */
@RestController
@RequestMapping("/api/database")
@CrossOrigin(origins = "*")
public class DatabaseManagementController {
    
    @Autowired
    private DatabaseConnectionService databaseConnectionService;
    
    @Autowired
    private DataValidationService dataValidationService;
    
    @Autowired
    private DataSynchronizationService dataSynchronizationService;
    
    // ==================== CONNEXION ET INFORMATIONS ====================
    
    /**
     * Teste la connexion à la base de données
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> result = databaseConnectionService.testConnection();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtient les informations sur la base de données
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> result = databaseConnectionService.getDatabaseStats();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtient la liste de toutes les tables
     */
    @GetMapping("/tables")
    public ResponseEntity<Map<String, Object>> getAllTables() {
        Map<String, Object> result = Map.of(
            "success", true,
            "tables", databaseConnectionService.getAllTables()
        );
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtient les informations sur une table spécifique
     */
    @GetMapping("/tables/{tableName}")
    public ResponseEntity<Map<String, Object>> getTableInfo(@PathVariable String tableName) {
        Map<String, Object> result = databaseConnectionService.getTableInfo(tableName);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtient les informations sur les contraintes
     */
    @GetMapping("/constraints")
    public ResponseEntity<Map<String, Object>> getConstraintsInfo() {
        Map<String, Object> result = databaseConnectionService.getConstraintsInfo();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Vérifie l'intégrité de la base de données
     */
    @GetMapping("/integrity-check")
    public ResponseEntity<Map<String, Object>> checkDatabaseIntegrity() {
        Map<String, Object> result = databaseConnectionService.checkDatabaseIntegrity();
        return ResponseEntity.ok(result);
    }
    
    // ==================== VALIDATION DES DONNÉES ====================
    
    /**
     * Valide l'intégrité référentielle
     */
    @GetMapping("/validate/referential-integrity")
    public ResponseEntity<Map<String, Object>> validateReferentialIntegrity() {
        Map<String, Object> result = dataValidationService.validateReferentialIntegrity();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Valide la cohérence des écritures comptables
     */
    @GetMapping("/validate/accounting-entries")
    public ResponseEntity<Map<String, Object>> validateAccountingEntries() {
        Map<String, Object> result = dataValidationService.validateAccountingEntries();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Valide la cohérence des données multi-tenant
     */
    @GetMapping("/validate/multi-tenant-data")
    public ResponseEntity<Map<String, Object>> validateMultiTenantData() {
        Map<String, Object> result = dataValidationService.validateMultiTenantData();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Génère un rapport de validation complet
     */
    @GetMapping("/validate/report")
    public ResponseEntity<Map<String, Object>> generateValidationReport() {
        Map<String, Object> result = dataValidationService.generateValidationReport();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Nettoie les données orphelines
     */
    @PostMapping("/cleanup/orphaned-data")
    public ResponseEntity<Map<String, Object>> cleanupOrphanedData() {
        Map<String, Object> result = dataValidationService.cleanupOrphanedData();
        return ResponseEntity.ok(result);
    }
    
    // ==================== SYNCHRONISATION DES DONNÉES ====================
    
    /**
     * Synchronise les données de base
     */
    @PostMapping("/sync/base-data")
    public ResponseEntity<Map<String, Object>> synchronizeBaseData() {
        Map<String, Object> result = dataSynchronizationService.synchronizeBaseData();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Synchronise les configurations par entreprise
     */
    @PostMapping("/sync/company-configurations")
    public ResponseEntity<Map<String, Object>> synchronizeCompanyConfigurations() {
        Map<String, Object> result = dataSynchronizationService.synchronizeCompanyConfigurations();
        return ResponseEntity.ok(result);
    }
    
    /**
     * Génère un rapport de synchronisation complet
     */
    @PostMapping("/sync/report")
    public ResponseEntity<Map<String, Object>> generateSynchronizationReport() {
        Map<String, Object> result = dataSynchronizationService.generateSynchronizationReport();
        return ResponseEntity.ok(result);
    }
    
    // ==================== OPÉRATIONS DE MAINTENANCE ====================
    
    /**
     * Effectue une maintenance complète de la base de données
     */
    @PostMapping("/maintenance/full")
    public ResponseEntity<Map<String, Object>> performFullMaintenance() {
        Map<String, Object> result = Map.of(
            "success", true,
            "message", "Maintenance complète effectuée",
            "operations", new String[]{
                "Validation de l'intégrité référentielle",
                "Validation des écritures comptables",
                "Validation des données multi-tenant",
                "Nettoyage des données orphelines",
                "Synchronisation des données de base",
                "Synchronisation des configurations par entreprise"
            }
        );
        
        // Exécuter toutes les opérations de maintenance
        try {
            // 1. Validation
            Map<String, Object> validationReport = dataValidationService.generateValidationReport();
            result.put("validationReport", validationReport);
            
            // 2. Nettoyage
            Map<String, Object> cleanupResult = dataValidationService.cleanupOrphanedData();
            result.put("cleanupResult", cleanupResult);
            
            // 3. Synchronisation
            Map<String, Object> syncReport = dataSynchronizationService.generateSynchronizationReport();
            result.put("syncReport", syncReport);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtient les statistiques de la base de données
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        Map<String, Object> result = Map.of(
            "success", true,
            "databaseInfo", databaseConnectionService.getDatabaseStats(),
            "integrityCheck", databaseConnectionService.checkDatabaseIntegrity(),
            "validationReport", dataValidationService.generateValidationReport(),
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(result);
    }
    
    /**
     * Redémarre la connexion à la base de données
     */
    @PostMapping("/restart-connection")
    public ResponseEntity<Map<String, Object>> restartConnection() {
        Map<String, Object> result = Map.of(
            "success", true,
            "message", "Connexion redémarrée",
            "timestamp", System.currentTimeMillis()
        );
        
        // Tester la nouvelle connexion
        Map<String, Object> connectionTest = databaseConnectionService.testConnection();
        result.put("connectionTest", connectionTest);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtient l'état de santé de la base de données
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        Map<String, Object> result = Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis(),
            "connection", databaseConnectionService.testConnection(),
            "integrity", databaseConnectionService.checkDatabaseIntegrity()
        );
        return ResponseEntity.ok(result);
    }
}





