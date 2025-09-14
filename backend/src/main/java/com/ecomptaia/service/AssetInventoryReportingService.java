package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetInventoryReportingService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMovementRepository movementRepository;



    // ==================== RAPPORTS D'IMMOBILISATIONS ====================

    /**
     * Rapport d'état des immobilisations
     */
    public Map<String, Object> generateAssetStatusReport(Long companyId, String countryCode, String accountingStandard) {
        try {
            List<Asset> assets = assetRepository.findByCompanyIdOrderByAssetNameAsc(companyId);
            
            Map<String, Object> report = new HashMap<>();
            report.put("reportType", "ASSET_STATUS_REPORT");
            report.put("generatedAt", LocalDateTime.now());
            report.put("companyId", companyId);
            report.put("countryCode", countryCode);
            report.put("accountingStandard", accountingStandard);

            // Statistiques générales
            BigDecimal totalPurchaseValue = BigDecimal.ZERO;
            BigDecimal totalCurrentValue = BigDecimal.ZERO;
            BigDecimal totalDepreciation = BigDecimal.ZERO;
            Map<String, Long> statusCount = new HashMap<>();
            Map<String, Long> typeCount = new HashMap<>();

            for (Asset asset : assets) {
                totalPurchaseValue = totalPurchaseValue.add(asset.getPurchasePrice());
                totalCurrentValue = totalCurrentValue.add(asset.getCurrentValue());
                // Calculer la dépréciation basée sur le taux et la valeur actuelle
                BigDecimal depreciation = asset.getCurrentValue().multiply(asset.getDepreciationRate()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                totalDepreciation = totalDepreciation.add(depreciation);
                
                statusCount.merge(asset.getStatus().toString(), 1L, Long::sum);
                typeCount.merge(asset.getAssetType().toString(), 1L, Long::sum);
            }

            report.put("totalAssets", assets.size());
            report.put("totalPurchaseValue", totalPurchaseValue);
            report.put("totalCurrentValue", totalCurrentValue);
            report.put("totalDepreciation", totalDepreciation);
            report.put("statusDistribution", statusCount);
            report.put("typeDistribution", typeCount);

            // Top 10 des immobilisations par valeur
            List<Asset> topAssets = assets.stream()
                .sorted((a1, a2) -> a2.getCurrentValue().compareTo(a1.getCurrentValue()))
                .limit(10)
                .collect(Collectors.toList());
            report.put("topAssetsByValue", topAssets);

            // Immobilisations nécessitant attention (dépréciation > 80%)
            List<Asset> criticalAssets = assets.stream()
                .filter(asset -> {
                    BigDecimal depreciationRate = asset.getDepreciationRate();
                    return depreciationRate != null && depreciationRate.compareTo(new BigDecimal("80")) > 0;
                })
                .collect(Collectors.toList());
            report.put("criticalAssets", criticalAssets);

            return report;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport d'état: " + e.getMessage());
        }
    }

    /**
     * Rapport de dépréciation des immobilisations
     */
    public Map<String, Object> generateDepreciationReport(Long companyId, String countryCode, String accountingStandard) {
        try {
            List<Asset> assets = assetRepository.findByCompanyIdOrderByAssetNameAsc(companyId);
            
            Map<String, Object> report = new HashMap<>();
            report.put("reportType", "DEPRECIATION_REPORT");
            report.put("generatedAt", LocalDateTime.now());
            report.put("companyId", companyId);

            BigDecimal totalDepreciation = BigDecimal.ZERO;
            BigDecimal totalAnnualDepreciation = BigDecimal.ZERO;
            Map<String, BigDecimal> depreciationByType = new HashMap<>();

            for (Asset asset : assets) {
                // Calculer la dépréciation annuelle basée sur le taux
                BigDecimal annualDepreciation = asset.getCurrentValue().multiply(asset.getDepreciationRate()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                totalDepreciation = totalDepreciation.add(annualDepreciation);
                totalAnnualDepreciation = totalAnnualDepreciation.add(annualDepreciation);
                
                depreciationByType.merge(asset.getAssetType().toString(), 
                    annualDepreciation, BigDecimal::add);
            }

            report.put("totalDepreciation", totalDepreciation);
            report.put("totalAnnualDepreciation", totalAnnualDepreciation);
            report.put("depreciationByType", depreciationByType);

            // Plan d'amortissement pour l'année en cours
            List<Map<String, Object>> depreciationSchedule = new ArrayList<>();
            for (Asset asset : assets) {
                Map<String, Object> schedule = new HashMap<>();
                schedule.put("assetCode", asset.getAssetCode());
                schedule.put("assetName", asset.getAssetName());
                schedule.put("purchaseDate", asset.getPurchaseDate());
                schedule.put("purchasePrice", asset.getPurchasePrice());
                // Calculer la dépréciation annuelle
                BigDecimal annualDepreciation = asset.getCurrentValue().multiply(asset.getDepreciationRate()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                schedule.put("annualDepreciation", annualDepreciation);
                schedule.put("accumulatedDepreciation", annualDepreciation); // Simplifié pour l'exemple
                schedule.put("remainingValue", asset.getCurrentValue());
                schedule.put("depreciationMethod", "LINEAR"); // Méthode par défaut
                depreciationSchedule.add(schedule);
            }
            report.put("depreciationSchedule", depreciationSchedule);

            return report;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport de dépréciation: " + e.getMessage());
        }
    }

    // ==================== RAPPORTS D'INVENTAIRE ====================

    /**
     * Rapport d'état des stocks
     */
    public Map<String, Object> generateInventoryStatusReport(Long companyId, String countryCode, String accountingStandard) {
        try {
            List<Inventory> inventories = inventoryRepository.findAll().stream()
                .filter(inv -> companyId.equals(inv.getCompanyId()))
                .sorted((inv1, inv2) -> inv1.getName().compareTo(inv2.getName()))
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> report = new HashMap<>();
            report.put("reportType", "INVENTORY_STATUS_REPORT");
            report.put("generatedAt", LocalDateTime.now());
            report.put("companyId", companyId);

            BigDecimal totalValue = BigDecimal.ZERO;
            int totalItems = 0;
            Map<String, Long> statusCount = new HashMap<>();
            Map<String, Long> categoryCount = new HashMap<>();

            for (Inventory inventory : inventories) {
                BigDecimal itemValue = inventory.getQuantityOnHand().multiply(inventory.getUnitPrice());
                totalValue = totalValue.add(itemValue);
                totalItems += inventory.getQuantityOnHand().intValue();
                
                statusCount.merge(inventory.getStatus().toString(), 1L, Long::sum);
                categoryCount.merge(inventory.getCategory(), 1L, Long::sum);
            }

            report.put("totalProducts", inventories.size());
            report.put("totalQuantity", totalItems);
            report.put("totalValue", totalValue);
            report.put("statusDistribution", statusCount);
            report.put("categoryDistribution", categoryCount);

            // Produits en rupture de stock
            List<Inventory> outOfStock = inventories.stream()
                .filter(inv -> inv.getQuantityOnHand().compareTo(BigDecimal.ZERO) == 0)
                .collect(Collectors.toList());
            report.put("outOfStockProducts", outOfStock);

            // Produits avec stock faible (< seuil minimum)
            List<Inventory> lowStock = inventories.stream()
                .filter(inv -> inv.getQuantityOnHand().compareTo(inv.getMinimumStock()) < 0)
                .collect(Collectors.toList());
            report.put("lowStockProducts", lowStock);

            // Top 10 des produits par valeur
            List<Inventory> topProducts = inventories.stream()
                .sorted((i1, i2) -> i2.getQuantityOnHand().multiply(i2.getUnitPrice())
                    .compareTo(i1.getQuantityOnHand().multiply(i1.getUnitPrice())))
                .limit(10)
                .collect(Collectors.toList());
            report.put("topProductsByValue", topProducts);

            return report;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport d'état des stocks: " + e.getMessage());
        }
    }

    /**
     * Rapport de mouvements de stock
     */
    public Map<String, Object> generateInventoryMovementReport(Long companyId, String countryCode, String accountingStandard, LocalDate startDate, LocalDate endDate) {
        try {
            List<InventoryMovement> movements = movementRepository.findAll().stream()
                .filter(movement -> movement.getCompanyId().equals(companyId))
                .filter(movement -> !movement.getMovementDate().isBefore(startDate) && !movement.getMovementDate().isAfter(endDate))
                .sorted((m1, m2) -> m2.getMovementDate().compareTo(m1.getMovementDate()))
                .collect(Collectors.toList());
            
            Map<String, Object> report = new HashMap<>();
            report.put("reportType", "INVENTORY_MOVEMENT_REPORT");
            report.put("generatedAt", LocalDateTime.now());
            report.put("companyId", companyId);
            report.put("startDate", startDate);
            report.put("endDate", endDate);

            // Statistiques des mouvements
            Map<String, Long> movementTypeCount = new HashMap<>();
            Map<String, BigDecimal> movementTypeValue = new HashMap<>();
            BigDecimal totalInValue = BigDecimal.ZERO;
            BigDecimal totalOutValue = BigDecimal.ZERO;

            for (InventoryMovement movement : movements) {
                String movementType = movement.getMovementType().name();
                BigDecimal movementValue = movement.getQuantity().multiply(movement.getUnitPrice());
                
                movementTypeCount.merge(movementType, 1L, Long::sum);
                movementTypeValue.merge(movementType, movementValue, BigDecimal::add);
                
                if ("IN".equals(movementType)) {
                    totalInValue = totalInValue.add(movementValue);
                } else if ("OUT".equals(movementType)) {
                    totalOutValue = totalOutValue.add(movementValue);
                }
            }

            report.put("totalMovements", movements.size());
            report.put("movementTypeCount", movementTypeCount);
            report.put("movementTypeValue", movementTypeValue);
            report.put("totalInValue", totalInValue);
            report.put("totalOutValue", totalOutValue);
            report.put("netMovementValue", totalInValue.subtract(totalOutValue));

            // Mouvements par jour
            Map<LocalDate, List<InventoryMovement>> movementsByDate = movements.stream()
                .collect(Collectors.groupingBy(InventoryMovement::getMovementDate));
            report.put("movementsByDate", movementsByDate);

            // Top produits par mouvement (simplifié par ID produit)
            Map<String, Long> productMovementCount = new HashMap<>();
            for (InventoryMovement movement : movements) {
                String productKey = "Product_" + movement.getProductId();
                productMovementCount.merge(productKey, 1L, Long::sum);
            }
            
            List<Map.Entry<String, Long>> topProducts = productMovementCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
            report.put("topProductsByMovement", topProducts);

            return report;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport de mouvements: " + e.getMessage());
        }
    }

    // ==================== RAPPORTS D'ANALYSE ====================

    /**
     * Rapport d'analyse des immobilisations
     */
    public Map<String, Object> generateAssetAnalysisReport(Long companyId, String countryCode, String accountingStandard) {
        try {
            List<Asset> assets = assetRepository.findByCompanyIdOrderByAssetNameAsc(companyId);
            
            Map<String, Object> report = new HashMap<>();
            report.put("reportType", "ASSET_ANALYSIS_REPORT");
            report.put("generatedAt", LocalDateTime.now());
            report.put("companyId", companyId);

            // Analyse par type d'immobilisation
            Map<String, List<Asset>> assetsByType = assets.stream()
                .collect(Collectors.groupingBy(asset -> asset.getAssetType().toString()));
            
            Map<String, Object> analysisByType = new HashMap<>();
            for (Map.Entry<String, List<Asset>> entry : assetsByType.entrySet()) {
                String type = entry.getKey();
                List<Asset> typeAssets = entry.getValue();
                
                BigDecimal totalValue = typeAssets.stream()
                    .map(Asset::getCurrentValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal totalDepreciation = typeAssets.stream()
                    .map(asset -> asset.getCurrentValue().multiply(asset.getDepreciationRate()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                Map<String, Object> typeAnalysis = new HashMap<>();
                typeAnalysis.put("count", typeAssets.size());
                typeAnalysis.put("totalValue", totalValue);
                typeAnalysis.put("totalDepreciation", totalDepreciation);
                typeAnalysis.put("averageValue", totalValue.divide(BigDecimal.valueOf(typeAssets.size()), 2, RoundingMode.HALF_UP));
                
                analysisByType.put(type, typeAnalysis);
            }
            
            report.put("analysisByType", analysisByType);

            // Analyse de la dépréciation
            List<Asset> assetsWithHighDepreciation = assets.stream()
                .filter(asset -> asset.getDepreciationRate().compareTo(new BigDecimal("50")) > 0)
                .collect(Collectors.toList());
            report.put("assetsWithHighDepreciation", assetsWithHighDepreciation);

            // Analyse de la valeur
            BigDecimal averageAssetValue = assets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(assets.size()), 2, RoundingMode.HALF_UP);
            report.put("averageAssetValue", averageAssetValue);

            return report;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport d'analyse: " + e.getMessage());
        }
    }

    // ==================== RAPPORTS COMBINÉS ====================

    /**
     * Rapport complet immobilisations et stocks
     */
    public Map<String, Object> generateComprehensiveReport(Long companyId, String countryCode, String accountingStandard) {
        try {
            Map<String, Object> comprehensiveReport = new HashMap<>();
            comprehensiveReport.put("reportType", "COMPREHENSIVE_ASSET_INVENTORY_REPORT");
            comprehensiveReport.put("generatedAt", LocalDateTime.now());
            comprehensiveReport.put("companyId", companyId);
            comprehensiveReport.put("countryCode", countryCode);
            comprehensiveReport.put("accountingStandard", accountingStandard);

            // Sous-rapports
            comprehensiveReport.put("assetStatus", generateAssetStatusReport(companyId, countryCode, accountingStandard));
            comprehensiveReport.put("depreciation", generateDepreciationReport(companyId, countryCode, accountingStandard));
            comprehensiveReport.put("inventoryStatus", generateInventoryStatusReport(companyId, countryCode, accountingStandard));
            comprehensiveReport.put("assetAnalysis", generateAssetAnalysisReport(companyId, countryCode, accountingStandard));

            // Résumé exécutif
            Map<String, Object> executiveSummary = new HashMap<>();
            @SuppressWarnings("unchecked")
            Map<String, Object> assetStatus = (Map<String, Object>) comprehensiveReport.get("assetStatus");
            @SuppressWarnings("unchecked")
            Map<String, Object> inventoryStatus = (Map<String, Object>) comprehensiveReport.get("inventoryStatus");
            
            executiveSummary.put("totalAssets", assetStatus.get("totalAssets"));
            executiveSummary.put("totalInventoryProducts", inventoryStatus.get("totalProducts"));
            executiveSummary.put("totalAssetValue", assetStatus.get("totalCurrentValue"));
            executiveSummary.put("totalInventoryValue", inventoryStatus.get("totalValue"));
            
            comprehensiveReport.put("executiveSummary", executiveSummary);

            return comprehensiveReport;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport complet: " + e.getMessage());
        }
    }
}
