package com.ecomptaia.controller;

import com.ecomptaia.service.AssetInventoryReportingService;
import com.ecomptaia.service.AssetInventoryImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/asset-inventory-reports")
@CrossOrigin(origins = "*")
public class AssetInventoryReportingController {

    @Autowired
    private AssetInventoryReportingService reportingService;

    @Autowired
    private AssetInventoryImportService importService;

    // ==================== RAPPORTS D'IMMOBILISATIONS ====================

    /**
     * Générer un rapport d'état des immobilisations
     */
    @GetMapping("/assets/status-report")
    public ResponseEntity<Map<String, Object>> generateAssetStatusReport(
            @RequestParam Long companyId,
            @RequestParam String countryCode,
            @RequestParam String accountingStandard) {
        try {
            Map<String, Object> report = reportingService.generateAssetStatusReport(companyId, countryCode, accountingStandard);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Générer un rapport de dépréciation des immobilisations
     */
    @GetMapping("/assets/depreciation-report")
    public ResponseEntity<Map<String, Object>> generateDepreciationReport(
            @RequestParam Long companyId,
            @RequestParam String countryCode,
            @RequestParam String accountingStandard) {
        try {
            Map<String, Object> report = reportingService.generateDepreciationReport(companyId, countryCode, accountingStandard);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== RAPPORTS D'INVENTAIRE ====================

    /**
     * Générer un rapport d'état des stocks
     */
    @GetMapping("/inventory/status-report")
    public ResponseEntity<Map<String, Object>> generateInventoryStatusReport(
            @RequestParam Long companyId,
            @RequestParam String countryCode,
            @RequestParam String accountingStandard) {
        try {
            Map<String, Object> report = reportingService.generateInventoryStatusReport(companyId, countryCode, accountingStandard);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Générer un rapport de mouvements de stock
     */
    @GetMapping("/inventory/movement-report")
    public ResponseEntity<Map<String, Object>> generateInventoryMovementReport(
            @RequestParam Long companyId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            Map<String, Object> report = reportingService.generateInventoryMovementReport(companyId, start, end);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== RAPPORTS D'ANALYSE D'INVENTAIRE ====================

    /**
     * Générer un rapport d'analyse d'inventaire complet
     */
    @GetMapping("/analysis/report/{analysisId}")
    public ResponseEntity<Map<String, Object>> generateInventoryAnalysisReport(@PathVariable Long analysisId) {
        try {
            Map<String, Object> report = reportingService.generateInventoryAnalysisReport(analysisId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== TABLEAUX DE BORD ====================

    /**
     * Générer le tableau de bord complet des immobilisations et stocks
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> generateDashboard(
            @RequestParam Long companyId,
            @RequestParam String countryCode,
            @RequestParam String accountingStandard) {
        try {
            Map<String, Object> dashboard = reportingService.generateDashboard(companyId, countryCode, accountingStandard);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== IMPORT D'IMMOBILISATIONS ====================

    /**
     * Importer des immobilisations depuis un fichier CSV
     */
    @PostMapping("/import/assets/csv")
    public ResponseEntity<Map<String, Object>> importAssetsFromCSV(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long companyId,
            @RequestParam String countryCode,
            @RequestParam String accountingStandard) {
        try {
            Map<String, Object> result = importService.importAssetsFromCSV(file, companyId, countryCode, accountingStandard);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Importer des immobilisations depuis un fichier JSON
     */
    @PostMapping("/import/assets/json")
    public ResponseEntity<Map<String, Object>> importAssetsFromJSON(
            @RequestBody String jsonData,
            @RequestParam Long companyId,
            @RequestParam String countryCode,
            @RequestParam String accountingStandard) {
        try {
            Map<String, Object> result = importService.importAssetsFromJSON(jsonData, companyId, countryCode, accountingStandard);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== IMPORT D'INVENTAIRES ====================

    /**
     * Importer des produits d'inventaire depuis un fichier CSV
     */
    @PostMapping("/import/inventory/csv")
    public ResponseEntity<Map<String, Object>> importInventoryFromCSV(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long companyId,
            @RequestParam String countryCode,
            @RequestParam String accountingStandard) {
        try {
            Map<String, Object> result = importService.importInventoryFromCSV(file, companyId, countryCode, accountingStandard);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== IMPORT DE MOUVEMENTS ====================

    /**
     * Importer des mouvements d'inventaire depuis un fichier CSV
     */
    @PostMapping("/import/movements/csv")
    public ResponseEntity<Map<String, Object>> importMovementsFromCSV(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long companyId,
            @RequestParam String countryCode,
            @RequestParam String accountingStandard) {
        try {
            Map<String, Object> result = importService.importMovementsFromCSV(file, companyId, countryCode, accountingStandard);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== TEMPLATES ET VALIDATION ====================

    /**
     * Télécharger le template CSV pour l'import d'immobilisations
     */
    @GetMapping("/templates/assets")
    public ResponseEntity<String> getAssetImportTemplate() {
        try {
            String template = importService.generateAssetImportTemplate();
            return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=assets_import_template.csv")
                .body(template);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la génération du template: " + e.getMessage());
        }
    }

    /**
     * Télécharger le template CSV pour l'import d'inventaires
     */
    @GetMapping("/templates/inventory")
    public ResponseEntity<String> getInventoryImportTemplate() {
        try {
            String template = importService.generateInventoryImportTemplate();
            return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=inventory_import_template.csv")
                .body(template);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la génération du template: " + e.getMessage());
        }
    }

    /**
     * Télécharger le template CSV pour l'import de mouvements
     */
    @GetMapping("/templates/movements")
    public ResponseEntity<String> getMovementImportTemplate() {
        try {
            String template = importService.generateMovementImportTemplate();
            return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=movements_import_template.csv")
                .body(template);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la génération du template: " + e.getMessage());
        }
    }

    /**
     * Valider un fichier d'import avant traitement
     */
    @PostMapping("/validate-import")
    public ResponseEntity<Map<String, Object>> validateImportFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String importType) {
        try {
            Map<String, Object> result = importService.validateImportFile(file, importType);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test de génération de rapport d'immobilisations
     */
    @PostMapping("/test/asset-report")
    public ResponseEntity<Map<String, Object>> testAssetReport() {
        try {
            Map<String, Object> report = reportingService.generateAssetStatusReport(1L, "CMR", "SYSCOHADA");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Test de génération de rapport d'inventaire
     */
    @PostMapping("/test/inventory-report")
    public ResponseEntity<Map<String, Object>> testInventoryReport() {
        try {
            Map<String, Object> report = reportingService.generateInventoryStatusReport(1L, "CMR", "SYSCOHADA");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Test de génération de tableau de bord
     */
    @PostMapping("/test/dashboard")
    public ResponseEntity<Map<String, Object>> testDashboard() {
        try {
            Map<String, Object> dashboard = reportingService.generateDashboard(1L, "CMR", "SYSCOHADA");
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}



