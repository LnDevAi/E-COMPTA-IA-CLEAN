package com.ecomptaia.controller;

import com.ecomptaia.entity.*;
import com.ecomptaia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/asset-inventory-advanced")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssetInventoryAdvancedController {

    @Autowired
    private AssetInventoryAdvancedService advancedService;

    @Autowired
    private InventoryAnalysisService analysisService;

    @Autowired
    private AssetInventoryDocumentService documentService;

    @Autowired
    private AssetInventoryService assetInventoryService;

    // === GÉNÉRATION D'ÉCRITURES COMPTABLES ===

    /**
     * Générer une écriture comptable pour l'acquisition d'un actif
     */
    @PostMapping("/generate-asset-acquisition-entry")
    public ResponseEntity<?> generateAssetAcquisitionEntry(@RequestBody Map<String, Object> request) {
        try {
            Long assetId = Long.valueOf(request.get("assetId").toString());
            String supplierCode = (String) request.get("supplierCode");
            String supplierName = (String) request.get("supplierName");

            Asset asset = assetInventoryService.getAssetById(assetId);
            if (asset == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Actif non trouvé"));
            }

            JournalEntry entry = advancedService.generateAssetAcquisitionEntry(asset, supplierCode, supplierName);
            return ResponseEntity.ok(Map.of("message", "Écriture générée avec succès", "entry", entry));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Générer une écriture comptable pour la dépréciation d'un actif
     */
    @PostMapping("/generate-asset-depreciation-entry")
    public ResponseEntity<?> generateAssetDepreciationEntry(@RequestBody Map<String, Object> request) {
        try {
            Long assetId = Long.valueOf(request.get("assetId").toString());
            Double depreciationAmount = Double.valueOf(request.get("depreciationAmount").toString());

            Asset asset = assetInventoryService.getAssetById(assetId);
            if (asset == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Actif non trouvé"));
            }

            JournalEntry entry = advancedService.generateAssetDepreciationEntry(asset, new java.math.BigDecimal(depreciationAmount.toString()));
            return ResponseEntity.ok(Map.of("message", "Écriture de dépréciation générée avec succès", "entry", entry));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Générer une écriture comptable pour un mouvement d'inventaire
     */
    @PostMapping("/generate-inventory-movement-entry")
    public ResponseEntity<?> generateInventoryMovementEntry(@RequestBody Map<String, Object> request) {
        try {
            Long movementId = Long.valueOf(request.get("movementId").toString());

            InventoryMovement movement = assetInventoryService.getMovementById(movementId);
            if (movement == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mouvement non trouvé"));
            }

            Inventory inventory = assetInventoryService.getInventoryById(movement.getProductId());
            if (inventory == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Inventaire non trouvé"));
            }

            JournalEntry entry = advancedService.generateInventoryMovementEntry(movement, inventory);
            return ResponseEntity.ok(Map.of("message", "Écriture de mouvement générée avec succès", "entry", entry));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === ANALYSE D'INVENTAIRE ===

    /**
     * Créer une nouvelle analyse d'inventaire
     */
    @PostMapping("/create-inventory-analysis")
    public ResponseEntity<?> createInventoryAnalysis(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String analysisType = (String) request.get("analysisType");
            String countryCode = (String) request.get("countryCode");
            String accountingStandard = (String) request.get("accountingStandard");

            InventoryAnalysis analysis = analysisService.createInventoryAnalysis(companyId, analysisType, countryCode, accountingStandard);
            return ResponseEntity.ok(Map.of("message", "Analyse créée avec succès", "analysis", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Effectuer une analyse d'inventaire
     */
    @PostMapping("/perform-inventory-analysis/{analysisId}")
    public ResponseEntity<?> performInventoryAnalysis(@PathVariable Long analysisId) {
        try {
            Map<String, Object> results = analysisService.performInventoryAnalysis(analysisId);
            return ResponseEntity.ok(Map.of("message", "Analyse effectuée avec succès", "results", results));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Générer des écritures comptables de correction
     */
    @PostMapping("/generate-correction-entries/{analysisId}")
    public ResponseEntity<?> generateCorrectionEntries(@PathVariable Long analysisId) {
        try {
            List<JournalEntry> correctionEntries = analysisService.generateCorrectionEntries(analysisId);
            return ResponseEntity.ok(Map.of("message", "Écritures de correction générées avec succès", "entries", correctionEntries));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Générer un rapport d'analyse
     */
    @PostMapping("/generate-analysis-report/{analysisId}")
    public ResponseEntity<?> generateAnalysisReport(@PathVariable Long analysisId) {
        try {
            Map<String, Object> report = analysisService.generateAnalysisReport(analysisId);
            return ResponseEntity.ok(Map.of("message", "Rapport généré avec succès", "report", report));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupérer toutes les analyses d'une entreprise
     */
    @GetMapping("/analyses/{companyId}")
    public ResponseEntity<?> getAnalysesByCompany(@PathVariable Long companyId) {
        try {
            List<InventoryAnalysis> analyses = analysisService.getAnalysesByCompany(companyId);
            return ResponseEntity.ok(analyses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupérer une analyse par ID
     */
    @GetMapping("/analysis/{analysisId}")
    public ResponseEntity<?> getAnalysisById(@PathVariable Long analysisId) {
        try {
            InventoryAnalysis analysis = analysisService.getAnalysisById(analysisId);
            if (analysis == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupérer les détails d'une analyse
     */
    @GetMapping("/analysis-details/{analysisId}")
    public ResponseEntity<?> getAnalysisDetails(@PathVariable Long analysisId) {
        try {
            List<InventoryAnalysisDetail> details = analysisService.getAnalysisDetails(analysisId);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === GESTION DES DOCUMENTS ===

    /**
     * Créer un document
     */
    @PostMapping("/create-document")
    public ResponseEntity<?> createDocument(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String documentType = (String) request.get("documentType");
            String title = (String) request.get("title");
            String relatedEntityType = (String) request.get("relatedEntityType");
            Long relatedEntityId = Long.valueOf(request.get("relatedEntityId").toString());
            String relatedEntityCode = (String) request.get("relatedEntityCode");
            String countryCode = (String) request.get("countryCode");
            String accountingStandard = (String) request.get("accountingStandard");

            AssetInventoryDocument document = documentService.createDocument(
                companyId, documentType, title, relatedEntityType, relatedEntityId, 
                relatedEntityCode, countryCode, accountingStandard
            );
            return ResponseEntity.ok(Map.of("message", "Document créé avec succès", "document", document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Attacher un fichier à un document
     */
    @PostMapping("/attach-file/{documentId}")
    public ResponseEntity<?> attachFileToDocument(@PathVariable Long documentId, @RequestBody Map<String, Object> request) {
        try {
            String filePath = (String) request.get("filePath");
            String fileType = (String) request.get("fileType");
            Long fileSize = Long.valueOf(request.get("fileSize").toString());
            String originalFileName = (String) request.get("originalFileName");

            AssetInventoryDocument document = documentService.attachFileToDocument(documentId, filePath, fileType, fileSize, originalFileName);
            return ResponseEntity.ok(Map.of("message", "Fichier attaché avec succès", "document", document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Valider un document
     */
    @PostMapping("/validate-document/{documentId}")
    public ResponseEntity<?> validateDocument(@PathVariable Long documentId, @RequestBody Map<String, Object> request) {
        try {
            Long validatedBy = Long.valueOf(request.get("validatedBy").toString());

            AssetInventoryDocument document = documentService.validateDocument(documentId, validatedBy);
            return ResponseEntity.ok(Map.of("message", "Document validé avec succès", "document", document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Archiver un document
     */
    @PostMapping("/archive-document/{documentId}")
    public ResponseEntity<?> archiveDocument(@PathVariable Long documentId, @RequestBody Map<String, Object> request) {
        try {
            Long archivedBy = Long.valueOf(request.get("archivedBy").toString());

            AssetInventoryDocument document = documentService.archiveDocument(documentId, archivedBy);
            return ResponseEntity.ok(Map.of("message", "Document archivé avec succès", "document", document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupérer tous les documents d'une entreprise
     */
    @GetMapping("/documents/{companyId}")
    public ResponseEntity<?> getDocumentsByCompany(@PathVariable Long companyId) {
        try {
            List<AssetInventoryDocument> documents = documentService.getDocumentsByCompany(companyId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupérer les documents par type
     */
    @GetMapping("/documents/{companyId}/type/{documentType}")
    public ResponseEntity<?> getDocumentsByType(@PathVariable Long companyId, @PathVariable String documentType) {
        try {
            List<AssetInventoryDocument> documents = documentService.getDocumentsByType(companyId, documentType);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupérer les documents par entité liée
     */
    @GetMapping("/documents/{companyId}/entity/{entityId}/{entityType}")
    public ResponseEntity<?> getDocumentsByRelatedEntity(@PathVariable Long companyId, @PathVariable Long entityId, @PathVariable String entityType) {
        try {
            List<AssetInventoryDocument> documents = documentService.getDocumentsByRelatedEntity(companyId, entityId, entityType);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupérer les documents non réconciliés
     */
    @GetMapping("/documents/{companyId}/unreconciled")
    public ResponseEntity<?> getUnreconciledDocuments(@PathVariable Long companyId) {
        try {
            List<AssetInventoryDocument> documents = documentService.getUnreconciledDocuments(companyId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupérer les documents avec écritures comptables
     */
    @GetMapping("/documents/{companyId}/with-accounting-entries")
    public ResponseEntity<?> getDocumentsWithAccountingEntries(@PathVariable Long companyId) {
        try {
            List<AssetInventoryDocument> documents = documentService.getDocumentsWithAccountingEntries(companyId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === STATISTIQUES ===

    /**
     * Obtenir les statistiques des documents
     */
    @GetMapping("/document-statistics/{companyId}")
    public ResponseEntity<?> getDocumentStatistics(@PathVariable Long companyId) {
        try {
            Map<String, Object> stats = documentService.getDocumentStatistics(companyId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === ENDPOINTS DE TEST ===

    /**
     * Test de génération d'écriture d'acquisition
     */
    @PostMapping("/test-asset-acquisition-entry")
    public ResponseEntity<?> testAssetAcquisitionEntry() {
        try {
            // Créer un actif de test
            Asset testAsset = new Asset();
            testAsset.setAssetCode("TEST_ASSET_001");
            testAsset.setAssetName("Ordinateur portable de test");
            testAsset.setAssetType(Asset.AssetType.MACHINERY);
            testAsset.setPurchasePrice(new java.math.BigDecimal("500000"));
            testAsset.setCurrentValue(new java.math.BigDecimal("500000"));
            testAsset.setCompanyId(1L);
            testAsset.setCountryCode("CI");
            testAsset.setAccountingStandard("SYSCOHADA");

            JournalEntry entry = advancedService.generateAssetAcquisitionEntry(testAsset, "SUPPLIER_001", "Fournisseur Test");
            return ResponseEntity.ok(Map.of("message", "Test réussi", "entry", entry));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Test de création d'analyse d'inventaire
     */
    @PostMapping("/test-inventory-analysis")
    public ResponseEntity<?> testInventoryAnalysis() {
        try {
            InventoryAnalysis analysis = analysisService.createInventoryAnalysis(1L, "COMPREHENSIVE", "CI", "SYSCOHADA");
            return ResponseEntity.ok(Map.of("message", "Test d'analyse créé avec succès", "analysis", analysis));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Test de création de document
     */
    @PostMapping("/test-document-creation")
    public ResponseEntity<?> testDocumentCreation() {
        try {
            AssetInventoryDocument document = documentService.createDocument(
                1L, "ASSET_PURCHASE", "Facture d'acquisition d'immobilisation", 
                "ASSET", 1L, "ASSET_001", "CI", "SYSCOHADA"
            );
            return ResponseEntity.ok(Map.of("message", "Test de document créé avec succès", "document", document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
