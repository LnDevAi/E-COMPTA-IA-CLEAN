package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AssetInventoryImportService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMovementRepository movementRepository;

    // ==================== IMPORT D'IMMOBILISATIONS ====================

    /**
     * Importer des immobilisations depuis un fichier CSV
     */
    public Map<String, Object> importAssetsFromCSV(MultipartFile file, Long companyId, String countryCode, String accountingStandard) {
        try {
            List<Asset> importedAssets = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            int lineNumber = 0;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Ignorer l'en-tête
                    }

                    try {
                        Asset asset = parseAssetFromCSV(line, companyId, countryCode, accountingStandard);
                        if (asset != null) {
                            importedAssets.add(asset);
                        }
                    } catch (Exception e) {
                        errors.add("Ligne " + lineNumber + ": " + e.getMessage());
                    }
                }
            }

            // Sauvegarder les immobilisations valides
            List<Asset> savedAssets = new ArrayList<>();
            for (Asset asset : importedAssets) {
                try {
                    Asset savedAsset = assetRepository.save(asset);
                    savedAssets.add(savedAsset);
                } catch (Exception e) {
                    errors.add("Erreur sauvegarde " + asset.getAssetCode() + ": " + e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("totalProcessed", lineNumber - 1);
            result.put("successfullyImported", savedAssets.size());
            result.put("errors", errors);
            result.put("importedAssets", savedAssets);

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier: " + e.getMessage());
        }
    }

    private Asset parseAssetFromCSV(String line, Long companyId, String countryCode, String accountingStandard) {
        String[] fields = line.split(",");
        
        if (fields.length < 8) {
            throw new RuntimeException("Format invalide: nombre de colonnes insuffisant");
        }

        Asset asset = new Asset();
        asset.setAssetCode(fields[0].trim());
        asset.setAssetName(fields[1].trim());
        asset.setAssetType(Asset.AssetType.valueOf(fields[2].trim().toUpperCase()));
        asset.setPurchasePrice(new BigDecimal(fields[3].trim()));
        asset.setPurchaseDate(LocalDate.parse(fields[4].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // Utiliser le taux de dépréciation au lieu de la méthode
        asset.setDepreciationRate(new BigDecimal(fields[5].trim()));
        asset.setUsefulLifeYears(Integer.parseInt(fields[6].trim()));
        asset.setStatus(Asset.AssetStatus.valueOf(fields[7].trim().toUpperCase()));
        
        // Champs optionnels
        if (fields.length > 8 && !fields[8].trim().isEmpty()) {
            asset.setLocation(fields[8].trim());
        }
        if (fields.length > 9 && !fields[9].trim().isEmpty()) {
            asset.setSupplier(fields[9].trim());
        }
        if (fields.length > 10 && !fields[10].trim().isEmpty()) {
            // Utiliser le champ supplier pour le nom du fournisseur
            asset.setSupplier(asset.getSupplier() + " - " + fields[10].trim());
        }

        asset.setCompanyId(companyId);
        asset.setCountryCode(countryCode);
        asset.setAccountingStandard(accountingStandard);
        asset.setCurrentValue(asset.getPurchasePrice());
        // La dépréciation sera calculée automatiquement basée sur le taux

        return asset;
    }

    /**
     * Importer des immobilisations depuis un fichier Excel (format JSON)
     */
    public Map<String, Object> importAssetsFromJSON(String jsonData, Long companyId, String countryCode, String accountingStandard) {
        try {
            // Simuler le parsing JSON (en réalité, utiliser Jackson ou Gson)
            List<Asset> importedAssets = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            // Exemple de structure JSON attendue
            // [{"assetCode":"ASSET001","assetName":"Ordinateur","assetType":"MACHINERY",...}]

            Map<String, Object> result = new HashMap<>();
            result.put("totalProcessed", 0);
            result.put("successfullyImported", importedAssets.size());
            result.put("errors", errors);
            result.put("importedAssets", importedAssets);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'import JSON: " + e.getMessage());
        }
    }

    // ==================== IMPORT D'INVENTAIRES ====================

    /**
     * Importer des produits d'inventaire depuis un fichier CSV
     */
    public Map<String, Object> importInventoryFromCSV(MultipartFile file, Long companyId, String countryCode, String accountingStandard) {
        try {
            List<Inventory> importedInventories = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            int lineNumber = 0;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Ignorer l'en-tête
                    }

                    try {
                        Inventory inventory = parseInventoryFromCSV(line, companyId, countryCode, accountingStandard);
                        if (inventory != null) {
                            importedInventories.add(inventory);
                        }
                    } catch (Exception e) {
                        errors.add("Ligne " + lineNumber + ": " + e.getMessage());
                    }
                }
            }

            // Sauvegarder les inventaires valides
            List<Inventory> savedInventories = new ArrayList<>();
            for (Inventory inventory : importedInventories) {
                try {
                    Inventory savedInventory = inventoryRepository.save(inventory);
                    savedInventories.add(savedInventory);
                } catch (Exception e) {
                    errors.add("Erreur sauvegarde " + inventory.getProductCode() + ": " + e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("totalProcessed", lineNumber - 1);
            result.put("successfullyImported", savedInventories.size());
            result.put("errors", errors);
            result.put("importedInventories", savedInventories);

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier: " + e.getMessage());
        }
    }

    private Inventory parseInventoryFromCSV(String line, Long companyId, String countryCode, String accountingStandard) {
        String[] fields = line.split(",");
        
        if (fields.length < 7) {
            throw new RuntimeException("Format invalide: nombre de colonnes insuffisant");
        }

        Inventory inventory = new Inventory();
        inventory.setProductCode(fields[0].trim());
        inventory.setProductName(fields[1].trim());
        inventory.setCategory(fields[2].trim());
        inventory.setUnitPrice(new BigDecimal(fields[3].trim()));
        inventory.setQuantityOnHand(new BigDecimal(fields[4].trim()));
        inventory.setMinimumStock(new BigDecimal(fields[5].trim()));
        inventory.setStatus(Inventory.InventoryStatus.valueOf(fields[6].trim().toUpperCase()));
        
        // Champs optionnels
        if (fields.length > 7 && !fields[7].trim().isEmpty()) {
            inventory.setValuationMethod(Inventory.ValuationMethod.valueOf(fields[7].trim().toUpperCase()));
        }
        if (fields.length > 8 && !fields[8].trim().isEmpty()) {
            inventory.setUnit(fields[8].trim());
        }
        if (fields.length > 9 && !fields[9].trim().isEmpty()) {
            inventory.setSupplierCode(fields[9].trim());
        }

        inventory.setCompanyId(companyId);
        inventory.setCountryCode(countryCode);
        inventory.setAccountingStandard(accountingStandard);

        return inventory;
    }

    // ==================== IMPORT DE MOUVEMENTS ====================

    /**
     * Importer des mouvements d'inventaire depuis un fichier CSV
     */
    public Map<String, Object> importMovementsFromCSV(MultipartFile file, Long companyId, String countryCode, String accountingStandard) {
        try {
            List<InventoryMovement> importedMovements = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            int lineNumber = 0;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Ignorer l'en-tête
                    }

                    try {
                        InventoryMovement movement = parseMovementFromCSV(line, companyId, countryCode, accountingStandard);
                        if (movement != null) {
                            importedMovements.add(movement);
                        }
                    } catch (Exception e) {
                        errors.add("Ligne " + lineNumber + ": " + e.getMessage());
                    }
                }
            }

            // Sauvegarder les mouvements valides
            List<InventoryMovement> savedMovements = new ArrayList<>();
            for (InventoryMovement movement : importedMovements) {
                try {
                    InventoryMovement savedMovement = movementRepository.save(movement);
                    savedMovements.add(savedMovement);
                } catch (Exception e) {
                    errors.add("Erreur sauvegarde " + movement.getMovementCode() + ": " + e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("totalProcessed", lineNumber - 1);
            result.put("successfullyImported", savedMovements.size());
            result.put("errors", errors);
            result.put("importedMovements", savedMovements);

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier: " + e.getMessage());
        }
    }

    private InventoryMovement parseMovementFromCSV(String line, Long companyId, String countryCode, String accountingStandard) {
        String[] fields = line.split(",");
        
        if (fields.length < 6) {
            throw new RuntimeException("Format invalide: nombre de colonnes insuffisant");
        }

        InventoryMovement movement = new InventoryMovement();
        movement.setMovementCode(fields[0].trim());
        // Utiliser productId au lieu de productCode
        movement.setProductId(1L); // ID par défaut, à adapter selon le contexte
        movement.setMovementType(InventoryMovement.MovementType.valueOf(fields[2].trim().toUpperCase()));
        movement.setQuantity(new BigDecimal(fields[3].trim()));
        movement.setUnitPrice(new BigDecimal(fields[4].trim()));
        movement.setMovementDate(LocalDate.parse(fields[5].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // Champs optionnels
        if (fields.length > 6 && !fields[6].trim().isEmpty()) {
            movement.setReferenceNumber(fields[6].trim());
        }
        if (fields.length > 7 && !fields[7].trim().isEmpty()) {
            movement.setNotes(fields[7].trim());
        }

        movement.setCompanyId(companyId);
        movement.setCountryCode(countryCode);
        movement.setAccountingStandard(accountingStandard);
        movement.setStatus(InventoryMovement.MovementStatus.PENDING);

        return movement;
    }

    // ==================== VALIDATION ET TEMPLATES ====================

    /**
     * Générer un template CSV pour l'import d'immobilisations
     */
    public String generateAssetImportTemplate() {
        return "assetCode,assetName,assetType,purchasePrice,purchaseDate,depreciationRate,usefulLifeYears,status,location,supplier\n" +
               "ASSET001,Ordinateur portable,MACHINERY,500000,2024-01-15,20,5,ACTIVE,Bureau principal,Fournisseur Informatique\n" +
               "ASSET002,Voiture de service,VEHICLE,8000000,2024-02-01,12.5,8,ACTIVE,Parking,Garage Auto";
    }

    /**
     * Générer un template CSV pour l'import d'inventaires
     */
    public String generateInventoryImportTemplate() {
        return "productCode,productName,category,unitPrice,quantityOnHand,minimumStock,status,valuationMethod,unit,supplierCode\n" +
               "PROD001,Stylos Bic,MATERIEL,500,100,20,ACTIVE,FIFO,Unité,SUP003\n" +
               "PROD002,Papier A4,MATERIEL,2500,50,10,ACTIVE,AVERAGE_COST,Rame,SUP003";
    }

    /**
     * Générer un template CSV pour l'import de mouvements
     */
    public String generateMovementImportTemplate() {
        return "movementCode,productId,movementType,quantity,unitPrice,movementDate,referenceNumber,notes\n" +
               "MOV001,1,IN,50,500,2024-01-15,ACH001,Achat initial\n" +
               "MOV002,1,OUT,10,500,2024-01-20,SORT001,Sortie bureau";
    }

    /**
     * Valider un fichier d'import
     */
    public Map<String, Object> validateImportFile(MultipartFile file, String importType) {
        try {
            List<String> errors = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            int lineCount = 0;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    
                    if (isFirstLine) {
                        isFirstLine = false;
                        // Valider l'en-tête selon le type
                        validateHeader(line, importType, errors);
                        continue;
                    }

                    // Valider la ligne selon le type
                    validateDataLine(line, importType, lineCount, errors, warnings);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("isValid", errors.isEmpty());
            result.put("totalLines", lineCount - 1);
            result.put("errors", errors);
            result.put("warnings", warnings);

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la validation du fichier: " + e.getMessage());
        }
    }

    private void validateHeader(String header, String importType, List<String> errors) {
        String[] expectedHeaders = getExpectedHeaders(importType);
        String[] actualHeaders = header.split(",");

        if (actualHeaders.length < expectedHeaders.length) {
            errors.add("En-tête incomplet: " + actualHeaders.length + " colonnes trouvées, " + expectedHeaders.length + " attendues");
        }

        for (int i = 0; i < Math.min(expectedHeaders.length, actualHeaders.length); i++) {
            if (!actualHeaders[i].trim().equalsIgnoreCase(expectedHeaders[i])) {
                errors.add("Colonne " + (i + 1) + ": '" + actualHeaders[i].trim() + "' attendu, '" + expectedHeaders[i] + "' trouvé");
            }
        }
    }

    private void validateDataLine(String line, String importType, int lineNumber, List<String> errors, List<String> warnings) {
        String[] fields = line.split(",");
        String[] expectedHeaders = getExpectedHeaders(importType);

        if (fields.length < expectedHeaders.length) {
            errors.add("Ligne " + lineNumber + ": nombre de colonnes insuffisant");
            return;
        }

        // Validation spécifique selon le type
        switch (importType) {
            case "ASSETS":
                validateAssetLine(fields, lineNumber, errors, warnings);
                break;
            case "INVENTORY":
                validateInventoryLine(fields, lineNumber, errors, warnings);
                break;
            case "MOVEMENTS":
                validateMovementLine(fields, lineNumber, errors, warnings);
                break;
        }
    }

    private String[] getExpectedHeaders(String importType) {
        switch (importType) {
            case "ASSETS":
                return new String[]{"assetCode", "assetName", "assetType", "purchasePrice", "purchaseDate", "depreciationRate", "usefulLifeYears", "status"};
            case "INVENTORY":
                return new String[]{"productCode", "productName", "category", "unitPrice", "quantityOnHand", "minimumStock", "status"};
            case "MOVEMENTS":
                return new String[]{"movementCode", "productId", "movementType", "quantity", "unitPrice", "movementDate"};
            default:
                return new String[0];
        }
    }

    private void validateAssetLine(String[] fields, int lineNumber, List<String> errors, List<String> warnings) {
        // Valider le code d'actif
        if (fields[0].trim().isEmpty()) {
            errors.add("Ligne " + lineNumber + ": code d'actif requis");
        }

        // Valider le prix d'achat
        try {
            new BigDecimal(fields[3].trim());
        } catch (NumberFormatException e) {
            errors.add("Ligne " + lineNumber + ": prix d'achat invalide");
        }

        // Valider la date d'achat
        try {
            LocalDate.parse(fields[4].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            errors.add("Ligne " + lineNumber + ": date d'achat invalide (format: yyyy-MM-dd)");
        }

        // Valider le type d'actif
        try {
            Asset.AssetType.valueOf(fields[2].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add("Ligne " + lineNumber + ": type d'actif invalide");
        }

        // Valider le taux de dépréciation
        try {
            new BigDecimal(fields[5].trim());
        } catch (NumberFormatException e) {
            errors.add("Ligne " + lineNumber + ": taux de dépréciation invalide");
        }
    }

    private void validateInventoryLine(String[] fields, int lineNumber, List<String> errors, List<String> warnings) {
        // Valider le code produit
        if (fields[0].trim().isEmpty()) {
            errors.add("Ligne " + lineNumber + ": code produit requis");
        }

        // Valider le prix unitaire
        try {
            new BigDecimal(fields[3].trim());
        } catch (NumberFormatException e) {
            errors.add("Ligne " + lineNumber + ": prix unitaire invalide");
        }

        // Valider la quantité
        try {
            new BigDecimal(fields[4].trim());
        } catch (NumberFormatException e) {
            errors.add("Ligne " + lineNumber + ": quantité invalide");
        }

        // Valider le statut
        try {
            Inventory.InventoryStatus.valueOf(fields[6].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add("Ligne " + lineNumber + ": statut invalide");
        }
    }

    private void validateMovementLine(String[] fields, int lineNumber, List<String> errors, List<String> warnings) {
        // Valider le code mouvement
        if (fields[0].trim().isEmpty()) {
            errors.add("Ligne " + lineNumber + ": code mouvement requis");
        }

        // Valider la quantité
        try {
            new BigDecimal(fields[3].trim());
        } catch (NumberFormatException e) {
            errors.add("Ligne " + lineNumber + ": quantité invalide");
        }

        // Valider le prix unitaire
        try {
            new BigDecimal(fields[4].trim());
        } catch (NumberFormatException e) {
            errors.add("Ligne " + lineNumber + ": prix unitaire invalide");
        }

        // Valider le type de mouvement
        try {
            InventoryMovement.MovementType.valueOf(fields[2].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add("Ligne " + lineNumber + ": type de mouvement invalide");
        }

        // Valider la date
        try {
            LocalDate.parse(fields[5].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            errors.add("Ligne " + lineNumber + ": date invalide (format: yyyy-MM-dd)");
        }
    }
}
