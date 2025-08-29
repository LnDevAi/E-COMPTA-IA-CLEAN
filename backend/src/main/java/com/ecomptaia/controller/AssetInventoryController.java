package com.ecomptaia.controller;

import com.ecomptaia.entity.Asset;
import com.ecomptaia.entity.Inventory;
import com.ecomptaia.service.AssetInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/asset-inventory")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssetInventoryController {

    @Autowired
    private AssetInventoryService assetInventoryService;

    // === GESTION DES ACTIFS ===

    /**
     * Récupérer tous les actifs (pour les tests)
     */
    @GetMapping("/assets")
    public ResponseEntity<?> getAllAssets() {
        try {
            List<Asset> assets = assetInventoryService.getAllAssets();

            Map<String, Object> response = new HashMap<>();
            response.put("assets", assets);
            response.put("total", assets.size());
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la récupération: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer un nouvel actif
     */
    @PostMapping("/assets")
    public ResponseEntity<?> createAsset(@RequestBody Asset asset) {
        try {
            Asset createdAsset = assetInventoryService.createAsset(asset);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Actif créé avec succès");
            response.put("asset", createdAsset);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la création: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    // === GESTION DE L'INVENTAIRE ===

    /**
     * Récupérer tout l'inventaire (pour les tests)
     */
    @GetMapping("/inventory")
    public ResponseEntity<?> getAllInventory() {
        try {
            List<Inventory> inventory = assetInventoryService.getAllInventory();

            Map<String, Object> response = new HashMap<>();
            response.put("inventory", inventory);
            response.put("total", inventory.size());
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la récupération: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer un nouvel inventaire
     */
    @PostMapping("/inventory")
    public ResponseEntity<?> createInventory(@RequestBody Inventory inventory) {
        try {
            Inventory createdInventory = assetInventoryService.createInventory(inventory);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Inventaire créé avec succès");
            response.put("inventory", createdInventory);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la création: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
