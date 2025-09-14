package com.ecomptaia.service;

import com.ecomptaia.entity.Asset;
import com.ecomptaia.entity.Inventory;
import com.ecomptaia.entity.InventoryMovement;
import com.ecomptaia.repository.AssetRepository;
import com.ecomptaia.repository.InventoryRepository;
import com.ecomptaia.repository.InventoryMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Transactional
public class AssetInventoryService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMovementRepository movementRepository;

    // ==================== GESTION DES IMMOBILISATIONS ====================

    /**
     * Créer une nouvelle immobilisation
     */
    public Asset createAsset(String assetCode, String assetName, Asset.AssetType assetType, 
                            BigDecimal purchasePrice, Long companyId, String countryCode, String accountingStandard) {
        
        if (assetRepository.existsByAssetCodeAndCompanyId(assetCode, companyId)) {
            throw new RuntimeException("Le code d'immobilisation existe déjà");
        }

        Asset asset = new Asset(assetCode, assetName, assetType, purchasePrice, companyId, countryCode, accountingStandard);
        asset.setCurrentValue(purchasePrice);
        
        // Calcul automatique de la dépréciation si la durée de vie est définie
        if (asset.getUsefulLifeYears() != null && asset.getUsefulLifeYears() > 0) {
            BigDecimal depreciationRate = BigDecimal.valueOf(100)
                .divide(BigDecimal.valueOf(asset.getUsefulLifeYears()), 2, RoundingMode.HALF_UP);
            asset.setDepreciationRate(depreciationRate);
        }

        return assetRepository.save(asset);
    }

    /**
     * Mettre à jour une immobilisation
     */
    public Asset updateAsset(Long assetId, Map<String, Object> updates) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Immobilisation non trouvée"));

        if (updates.containsKey("assetName")) {
            asset.setAssetName((String) updates.get("assetName"));
        }
        if (updates.containsKey("category")) {
            asset.setCategory((String) updates.get("category"));
        }
        if (updates.containsKey("description")) {
            asset.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("location")) {
            asset.setLocation((String) updates.get("location"));
        }
        if (updates.containsKey("supplier")) {
            asset.setSupplier((String) updates.get("supplier"));
        }
        if (updates.containsKey("status")) {
            asset.setStatus(Asset.AssetStatus.valueOf((String) updates.get("status")));
        }
        if (updates.containsKey("currentValue")) {
            asset.setCurrentValue(new BigDecimal(updates.get("currentValue").toString()));
        }

        return assetRepository.save(asset);
    }

    /**
     * Calculer la dépréciation d'une immobilisation
     */
    public Asset calculateDepreciation(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Immobilisation non trouvée"));

        if (asset.getDepreciationRate() == null || asset.getUsefulLifeYears() == null) {
            throw new RuntimeException("Taux de dépréciation ou durée de vie non définis");
        }

        // Calcul de la dépréciation linéaire
        BigDecimal annualDepreciation = asset.getPurchasePrice()
            .multiply(asset.getDepreciationRate())
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Calcul de la dépréciation cumulée
        LocalDate purchaseDate = asset.getPurchaseDate() != null ? asset.getPurchaseDate() : LocalDate.now();
        int yearsOfService = LocalDate.now().getYear() - purchaseDate.getYear();
        
        BigDecimal accumulatedDepreciation = annualDepreciation.multiply(BigDecimal.valueOf(yearsOfService));
        BigDecimal newCurrentValue = asset.getPurchasePrice().subtract(accumulatedDepreciation);

        if (newCurrentValue.compareTo(BigDecimal.ZERO) < 0) {
            newCurrentValue = BigDecimal.ZERO;
        }

        asset.setCurrentValue(newCurrentValue);
        return assetRepository.save(asset);
    }

    /**
     * Obtenir les statistiques des immobilisations
     */
    public Map<String, Object> getAssetStatistics(Long companyId) {
        Map<String, Object> statistics = new HashMap<>();

        // Statistiques par type
        List<Object[]> typeStats = assetRepository.getAssetStatisticsByType(companyId);
        Map<String, Object> typeStatistics = new HashMap<>();
        for (Object[] stat : typeStats) {
            Map<String, Object> typeStat = new HashMap<>();
            typeStat.put("count", stat[1]);
            typeStat.put("totalValue", stat[2]);
            typeStatistics.put(stat[0].toString(), typeStat);
        }
        statistics.put("byType", typeStatistics);

        // Statistiques par statut
        List<Object[]> statusStats = assetRepository.getAssetStatisticsByStatus(companyId);
        Map<String, Object> statusStatistics = new HashMap<>();
        for (Object[] stat : statusStats) {
            Map<String, Object> statusStat = new HashMap<>();
            statusStat.put("count", stat[1]);
            statusStat.put("totalValue", stat[2]);
            statusStatistics.put(stat[0].toString(), statusStat);
        }
        statistics.put("byStatus", statusStatistics);

        // Totaux
        statistics.put("totalAssets", assetRepository.getTotalAssetCount(companyId));
        statistics.put("totalValue", assetRepository.getTotalAssetValue(companyId));

        // Alertes
        LocalDate today = LocalDate.now();
        statistics.put("needsMaintenance", assetRepository.getAssetsNeedingMaintenance(companyId, today).size());
        statistics.put("expiredWarranty", assetRepository.getAssetsWithExpiredWarranty(companyId, today).size());
        statistics.put("expiredInsurance", assetRepository.getAssetsWithExpiredInsurance(companyId, today).size());

        return statistics;
    }

    // ==================== GESTION DES STOCKS ====================

    /**
     * Créer un nouveau produit en stock
     */
    public Inventory createInventory(String productCode, String productName, String unit, 
                                   BigDecimal unitPrice, Long companyId, String countryCode, String accountingStandard) {
        
        // Vérifier si le code produit existe déjà
        List<Inventory> existingProducts = inventoryRepository.findAll();
        boolean productExists = existingProducts.stream()
            .anyMatch(p -> productCode.equals(p.getProductCode()) && companyId.equals(p.getCompanyId()));
        if (productExists) {
            throw new RuntimeException("Le code produit existe déjà");
        }

        Inventory inventory = new Inventory(productCode, productName, unit, unitPrice, companyId, countryCode, accountingStandard);
        inventory.setLastPurchasePrice(unitPrice);
        inventory.setAverageCost(unitPrice);
        inventory.setTotalValue(BigDecimal.ZERO);

        return inventoryRepository.save(inventory);
    }

    /**
     * Mettre à jour un produit en stock
     */
    public Inventory updateInventory(Long inventoryId, Map<String, Object> updates) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        if (updates.containsKey("productName")) {
            inventory.setProductName((String) updates.get("productName"));
        }
        if (updates.containsKey("category")) {
            inventory.setCategory((String) updates.get("category"));
        }
        if (updates.containsKey("description")) {
            inventory.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("unitPrice")) {
            inventory.setUnitPrice(new BigDecimal(updates.get("unitPrice").toString()));
        }
        if (updates.containsKey("minimumStock")) {
            inventory.setMinimumStock(new BigDecimal(updates.get("minimumStock").toString()));
        }
        if (updates.containsKey("maximumStock")) {
            inventory.setMaximumStock(new BigDecimal(updates.get("maximumStock").toString()));
        }
        if (updates.containsKey("reorderPoint")) {
            inventory.setReorderPoint(new BigDecimal(updates.get("reorderPoint").toString()));
        }
        if (updates.containsKey("warehouse")) {
            inventory.setWarehouse((String) updates.get("warehouse"));
        }
        if (updates.containsKey("status")) {
            inventory.setStatus(Inventory.InventoryStatus.valueOf((String) updates.get("status")));
        }

        return inventoryRepository.save(inventory);
    }

    /**
     * Créer un mouvement de stock
     */
    public InventoryMovement createMovement(String movementCode, Long productId, 
                                          InventoryMovement.MovementType movementType, BigDecimal quantity,
                                          BigDecimal unitPrice, Long companyId, String countryCode, String accountingStandard) {
        
        // Vérifier si le code de mouvement existe déjà
        List<InventoryMovement> existingMovements = movementRepository.findAll();
        boolean movementExists = existingMovements.stream()
            .anyMatch(m -> movementCode.equals(m.getMovementCode()) && companyId.equals(m.getCompanyId()));
        if (movementExists) {
            throw new RuntimeException("Le code de mouvement existe déjà");
        }

        InventoryMovement movement = new InventoryMovement(movementCode, productId, movementType, 
                                                         quantity, companyId, countryCode, accountingStandard);
        movement.setUnitPrice(unitPrice);
        movement.setTotalAmount(quantity.multiply(unitPrice));
        movement.setMovementDate(LocalDate.now());

        return movementRepository.save(movement);
    }

    /**
     * Approuver un mouvement de stock
     */
    public InventoryMovement approveMovement(Long movementId, Long approvedBy) {
        InventoryMovement movement = movementRepository.findById(movementId)
            .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));

        if (movement.getStatus() != InventoryMovement.MovementStatus.PENDING) {
            throw new RuntimeException("Le mouvement ne peut pas être approuvé");
        }

        movement.setStatus(InventoryMovement.MovementStatus.APPROVED);
        movement.setApprovedBy(approvedBy);
        movement.setApprovedAt(LocalDateTime.now());

        // Mettre à jour le stock
        updateInventoryStock(movement);

        return movementRepository.save(movement);
    }

    /**
     * Mettre à jour le stock après un mouvement
     */
    private void updateInventoryStock(InventoryMovement movement) {
        Inventory inventory = inventoryRepository.findById(movement.getProductId())
            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        BigDecimal newQuantity = inventory.getQuantityOnHand();
        BigDecimal newTotalValue = inventory.getTotalValue();

        switch (movement.getMovementType()) {
            case IN:
                newQuantity = newQuantity.add(movement.getQuantity());
                newTotalValue = newTotalValue.add(movement.getTotalAmount());
                break;
            case OUT:
                if (newQuantity.compareTo(movement.getQuantity()) < 0) {
                    throw new RuntimeException("Stock insuffisant");
                }
                newQuantity = newQuantity.subtract(movement.getQuantity());
                // Calculer la valeur sortie selon la méthode de valorisation
                BigDecimal costOfGoodsSold = calculateCostOfGoodsSold(inventory, movement.getQuantity());
                newTotalValue = newTotalValue.subtract(costOfGoodsSold);
                break;
            case TRANSFER:
                // Pour les transferts, on ne change que la localisation
                break;
            case ADJUSTMENT:
                newQuantity = movement.getQuantity();
                newTotalValue = newQuantity.multiply(inventory.getAverageCost());
                break;
            case RETURN:
                // Pour les retours, traiter comme un mouvement IN
                newQuantity = newQuantity.add(movement.getQuantity());
                newTotalValue = newTotalValue.add(movement.getTotalAmount());
                break;
            case EXPIRY:
                // Pour les expirations, traiter comme un mouvement OUT
                if (newQuantity.compareTo(movement.getQuantity()) < 0) {
                    throw new RuntimeException("Stock insuffisant pour expiration");
                }
                newQuantity = newQuantity.subtract(movement.getQuantity());
                BigDecimal costOfExpired = calculateCostOfGoodsSold(inventory, movement.getQuantity());
                newTotalValue = newTotalValue.subtract(costOfExpired);
                break;
            case DAMAGE:
                // Pour les dommages, traiter comme un mouvement OUT
                if (newQuantity.compareTo(movement.getQuantity()) < 0) {
                    throw new RuntimeException("Stock insuffisant pour dommages");
                }
                newQuantity = newQuantity.subtract(movement.getQuantity());
                BigDecimal costOfDamaged = calculateCostOfGoodsSold(inventory, movement.getQuantity());
                newTotalValue = newTotalValue.subtract(costOfDamaged);
                break;
            case LOSS:
                // Pour les pertes, traiter comme un mouvement OUT
                if (newQuantity.compareTo(movement.getQuantity()) < 0) {
                    throw new RuntimeException("Stock insuffisant pour pertes");
                }
                newQuantity = newQuantity.subtract(movement.getQuantity());
                BigDecimal costOfLost = calculateCostOfGoodsSold(inventory, movement.getQuantity());
                newTotalValue = newTotalValue.subtract(costOfLost);
                break;
        }

        inventory.setQuantityOnHand(newQuantity);
        inventory.setTotalValue(newTotalValue);
        inventory.setLastMovementDate(LocalDate.now());

        // Mettre à jour le coût moyen si nécessaire
        if (movement.getMovementType() == InventoryMovement.MovementType.IN) {
            updateAverageCost(inventory, movement);
        }

        inventoryRepository.save(inventory);
    }

    /**
     * Calculer le coût des marchandises vendues
     */
    private BigDecimal calculateCostOfGoodsSold(Inventory inventory, BigDecimal quantity) {
        switch (inventory.getValuationMethod()) {
            case FIFO:
                return calculateFIFOCost(inventory, quantity);
            case LIFO:
                return calculateLIFOCost(inventory, quantity);
            case AVERAGE_COST:
                return inventory.getAverageCost().multiply(quantity);
            case STANDARD_COST:
                return inventory.getUnitPrice().multiply(quantity);
            default:
                return inventory.getAverageCost().multiply(quantity);
        }
    }

    /**
     * Calculer le coût FIFO
     */
    private BigDecimal calculateFIFOCost(Inventory inventory, BigDecimal quantity) {
        // Simplification : utiliser le coût moyen
        return inventory.getAverageCost().multiply(quantity);
    }

    /**
     * Calculer le coût LIFO
     */
    private BigDecimal calculateLIFOCost(Inventory inventory, BigDecimal quantity) {
        // Simplification : utiliser le coût moyen
        return inventory.getAverageCost().multiply(quantity);
    }

    /**
     * Mettre à jour le coût moyen
     */
    private void updateAverageCost(Inventory inventory, InventoryMovement movement) {
        BigDecimal totalQuantity = inventory.getQuantityOnHand().add(movement.getQuantity());
        BigDecimal totalValue = inventory.getTotalValue().add(movement.getTotalAmount());
        
        if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newAverageCost = totalValue.divide(totalQuantity, 2, RoundingMode.HALF_UP);
            inventory.setAverageCost(newAverageCost);
        }
    }

    /**
     * Obtenir les statistiques des stocks
     */
    public Map<String, Object> getInventoryStatistics(Long companyId) {
        Map<String, Object> statistics = new HashMap<>();

        // Statistiques par catégorie
        List<Inventory> inventories = inventoryRepository.findAll();
        Map<String, Object> categoryStatistics = new HashMap<>();
        
        // Calculer les statistiques par catégorie
        Map<String, Integer> categoryCounts = new HashMap<>();
        Map<String, BigDecimal> categoryQuantities = new HashMap<>();
        Map<String, BigDecimal> categoryValues = new HashMap<>();
        
        for (Inventory inventory : inventories) {
            String category = inventory.getCategory() != null ? inventory.getCategory() : "Sans catégorie";
            
            categoryCounts.merge(category, 1, Integer::sum);
            categoryQuantities.merge(category, inventory.getQuantityOnHand(), BigDecimal::add);
            categoryValues.merge(category, inventory.getTotalValue(), BigDecimal::add);
        }
        
        for (String category : categoryCounts.keySet()) {
            Map<String, Object> catStat = new HashMap<>();
            catStat.put("count", categoryCounts.get(category));
            catStat.put("quantity", categoryQuantities.get(category));
            catStat.put("value", categoryValues.get(category));
            categoryStatistics.put(category, catStat);
        }
        statistics.put("byCategory", categoryStatistics);

        // Statistiques par statut
        Map<String, Object> statusStatistics = new HashMap<>();
        
        // Calculer les statistiques par statut
        Map<String, Integer> statusCounts = new HashMap<>();
        Map<String, BigDecimal> statusQuantities = new HashMap<>();
        Map<String, BigDecimal> statusValues = new HashMap<>();
        
        for (Inventory inventory : inventories) {
            String status = inventory.getStatus() != null ? inventory.getStatus().toString() : "ACTIF";
            
            statusCounts.merge(status, 1, Integer::sum);
            statusQuantities.merge(status, inventory.getQuantityOnHand(), BigDecimal::add);
            statusValues.merge(status, inventory.getTotalValue(), BigDecimal::add);
        }
        
        for (String status : statusCounts.keySet()) {
            Map<String, Object> statusStat = new HashMap<>();
            statusStat.put("count", statusCounts.get(status));
            statusStat.put("quantity", statusQuantities.get(status));
            statusStat.put("value", statusValues.get(status));
            statusStatistics.put(status, statusStat);
        }
        statistics.put("byStatus", statusStatistics);

        // Statistiques par entrepôt
        Map<String, Object> warehouseStatistics = new HashMap<>();
        
        // Calculer les statistiques par entrepôt
        Map<String, Integer> warehouseCounts = new HashMap<>();
        Map<String, BigDecimal> warehouseQuantities = new HashMap<>();
        Map<String, BigDecimal> warehouseValues = new HashMap<>();
        
        for (Inventory inventory : inventories) {
            String warehouse = inventory.getWarehouse() != null ? inventory.getWarehouse() : "Entrepôt principal";
            
            warehouseCounts.merge(warehouse, 1, Integer::sum);
            warehouseQuantities.merge(warehouse, inventory.getQuantityOnHand(), BigDecimal::add);
            warehouseValues.merge(warehouse, inventory.getTotalValue(), BigDecimal::add);
        }
        
        for (String warehouse : warehouseCounts.keySet()) {
            Map<String, Object> warehouseStat = new HashMap<>();
            warehouseStat.put("count", warehouseCounts.get(warehouse));
            warehouseStat.put("quantity", warehouseQuantities.get(warehouse));
            warehouseStat.put("value", warehouseValues.get(warehouse));
            warehouseStatistics.put(warehouse, warehouseStat);
        }
        statistics.put("byWarehouse", warehouseStatistics);

        // Totaux
        statistics.put("totalProducts", inventoryRepository.count());
        
        // Calculer les totaux réels
        BigDecimal totalQuantity = inventories.stream()
            .map(Inventory::getQuantityOnHand)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalValue = inventories.stream()
            .map(Inventory::getTotalValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        statistics.put("totalQuantity", totalQuantity);
        statistics.put("totalValue", totalValue);

        // Alertes
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        
        // Calculer les alertes réelles
        long outOfStock = inventories.stream()
            .filter(inv -> inv.getQuantityOnHand().compareTo(BigDecimal.ZERO) <= 0)
            .count();
        long toReorder = inventories.stream()
            .filter(inv -> inv.getQuantityOnHand().compareTo(BigDecimal.valueOf(10)) <= 0) // Seuil fixe de 10
            .count();
        long expired = inventories.stream()
            .filter(inv -> inv.getExpiryDate() != null && inv.getExpiryDate().isBefore(today))
            .count();
        long expiringSoon = inventories.stream()
            .filter(inv -> inv.getExpiryDate() != null && inv.getExpiryDate().isAfter(today) && inv.getExpiryDate().isBefore(thirtyDaysFromNow))
            .count();
            
        statistics.put("outOfStock", outOfStock);
        statistics.put("toReorder", toReorder);
        statistics.put("expired", expired);
        statistics.put("expiringSoon", expiringSoon);
        statistics.put("alertDate", thirtyDaysFromNow.toString()); // Utilise la variable thirtyDaysFromNow

        return statistics;
    }

    /**
     * Obtenir les statistiques des mouvements
     */
    public Map<String, Object> getMovementStatistics(Long companyId) {
        Map<String, Object> statistics = new HashMap<>();

        // Statistiques par type de mouvement
        List<InventoryMovement> movements = movementRepository.findAll();
        Map<String, Object> typeStatistics = new HashMap<>();
        
        // Calculer les statistiques par type
        Map<String, Integer> typeCounts = new HashMap<>();
        Map<String, BigDecimal> typeQuantities = new HashMap<>();
        Map<String, BigDecimal> typeValues = new HashMap<>();
        
        for (InventoryMovement movement : movements) {
            String type = movement.getMovementType() != null ? movement.getMovementType().toString() : "IN";
            
            typeCounts.merge(type, 1, Integer::sum);
            typeQuantities.merge(type, movement.getQuantity(), BigDecimal::add);
            typeValues.merge(type, movement.getTotalAmount(), BigDecimal::add);
        }
        
        for (String type : typeCounts.keySet()) {
            Map<String, Object> typeStat = new HashMap<>();
            typeStat.put("count", typeCounts.get(type));
            typeStat.put("quantity", typeQuantities.get(type));
            typeStat.put("value", typeValues.get(type));
            typeStatistics.put(type, typeStat);
        }
        statistics.put("byType", typeStatistics);

        // Statistiques par statut
        Map<String, Object> statusStatistics = new HashMap<>();
        
        // Calculer les statistiques par statut
        Map<String, Integer> statusCounts = new HashMap<>();
        Map<String, BigDecimal> statusQuantities = new HashMap<>();
        Map<String, BigDecimal> statusValues = new HashMap<>();
        
        for (InventoryMovement movement : movements) {
            String status = movement.getStatus() != null ? movement.getStatus().toString() : "PENDING";
            
            statusCounts.merge(status, 1, Integer::sum);
            statusQuantities.merge(status, movement.getQuantity(), BigDecimal::add);
            statusValues.merge(status, movement.getTotalAmount(), BigDecimal::add);
        }
        
        for (String status : statusCounts.keySet()) {
            Map<String, Object> statusStat = new HashMap<>();
            statusStat.put("count", statusCounts.get(status));
            statusStat.put("quantity", statusQuantities.get(status));
            statusStat.put("value", statusValues.get(status));
            statusStatistics.put(status, statusStat);
        }
        statistics.put("byStatus", statusStatistics);

        // Totaux
        statistics.put("totalMovements", movementRepository.count());
        
        // Calculer les totaux réels
        BigDecimal totalQuantity = movements.stream()
            .map(InventoryMovement::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalValue = movements.stream()
            .map(InventoryMovement::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        statistics.put("totalQuantity", totalQuantity);
        statistics.put("totalValue", totalValue);

        // Mouvements en attente
        long pendingMovements = movements.stream()
            .filter(m -> m.getStatus() != null && "PENDING".equals(m.getStatus().toString()))
            .count();
        statistics.put("pendingMovements", pendingMovements);

        return statistics;
    }

    // ==================== RECHERCHE ET FILTRAGE ====================

    /**
     * Rechercher des immobilisations
     */
    public List<Asset> searchAssets(Long companyId, Asset.AssetType assetType, 
                                   Asset.AssetStatus status, String location) {
        return assetRepository.findAssetsByFilters(companyId, assetType, status, location);
    }

    /**
     * Rechercher des produits en stock
     */
    public List<Inventory> searchInventory(Long companyId, String category, 
                                         Inventory.InventoryStatus status, String warehouse, String supplier) {
        return inventoryRepository.findAll().stream()
            .filter(inv -> companyId == null || companyId.equals(inv.getCompanyId()))
            .filter(inv -> category == null || category.equals(inv.getCategory()))
            .filter(inv -> status == null || status.equals(inv.getStatus()))
            .filter(inv -> warehouse == null || warehouse.equals(inv.getWarehouse()))
            .filter(inv -> supplier == null || supplier.equals(inv.getSupplier()))
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Rechercher des mouvements
     */
    public List<InventoryMovement> searchMovements(Long companyId, Long productId, 
                                                  InventoryMovement.MovementType movementType,
                                                  InventoryMovement.MovementStatus status,
                                                  LocalDate startDate, LocalDate endDate) {
        return movementRepository.findAll().stream()
            .filter(mov -> companyId == null || companyId.equals(mov.getCompanyId()))
            .filter(mov -> productId == null || productId.equals(mov.getItemId()))
            .filter(mov -> movementType == null || movementType.equals(mov.getMovementType()))
            .filter(mov -> status == null || status.equals(mov.getStatus()))
            .filter(mov -> startDate == null || mov.getMovementDate().isAfter(startDate) || mov.getMovementDate().isEqual(startDate))
            .filter(mov -> endDate == null || mov.getMovementDate().isBefore(endDate) || mov.getMovementDate().isEqual(endDate))
            .collect(java.util.stream.Collectors.toList());
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Obtenir une immobilisation par ID
     */
    public Asset getAssetById(Long assetId) {
        return assetRepository.findById(assetId)
            .orElseThrow(() -> new RuntimeException("Immobilisation non trouvée"));
    }

    /**
     * Obtenir un inventaire par ID
     */
    public Inventory getInventoryById(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new RuntimeException("Inventaire non trouvé"));
    }

    /**
     * Obtenir un mouvement par ID
     */
    public InventoryMovement getMovementById(Long movementId) {
        return movementRepository.findById(movementId)
            .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));
    }



    // ==================== MÉTHODES SIMPLIFIÉES POUR LES TESTS ====================

    /**
     * Récupérer tous les actifs (pour les tests)
     */
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    /**
     * Créer un actif (version simplifiée pour les tests)
     */
    public Asset createAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    /**
     * Récupérer tout l'inventaire (pour les tests)
     */
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    /**
     * Créer un inventaire (version simplifiée pour les tests)
     */
    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
}
