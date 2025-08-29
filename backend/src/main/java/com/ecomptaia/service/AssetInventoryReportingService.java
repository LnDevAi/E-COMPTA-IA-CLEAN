package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private InventoryAnalysisRepository analysisRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private InventoryAnalysisDetailRepository analysisDetailRepository;

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
                BigDecimal depreciation = asset.getCurrentValue().multiply(asset.getDepreciationRate()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
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
                BigDecimal annualDepreciation = asset.getCurrentValue().multiply(asset.getDepreciationRate()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
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
                BigDecimal annualDepreciation = asset.getCurrentValue().multiply(asset.getDepreciationRate()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
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
            List<Inventory> inventories = inventoryRepository.findByCompanyIdOrderByProductNameAsc(companyId);
            
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
            throw new RuntimeException("Erreur lors de la génération du rapport d'inventaire: " + e.getMessage());
        }
    }

    /**
     * Rapport de mouvements de stock
     */
    public Map<String, Object> generateInventoryMovementReport(Long companyId, LocalDate startDate, LocalDate endDate) {
        try {
            List<InventoryMovement> movements = movementRepository.findMovementsByFilters(
                companyId, null, null, null, startDate, endDate);
            
            Map<String, Object> report = new HashMap<>();
            report.put("reportType", "INVENTORY_MOVEMENT_REPORT");
            report.put("generatedAt", LocalDateTime.now());
            report.put("period", startDate + " à " + endDate);
            report.put("companyId", companyId);

            BigDecimal totalInValue = BigDecimal.ZERO;
            BigDecimal totalOutValue = BigDecimal.ZERO;
            Map<String, Long> movementTypeCount = new HashMap<>();
            Map<String, BigDecimal> movementTypeValue = new HashMap<>();

            for (InventoryMovement movement : movements) {
                BigDecimal movementValue = movement.getQuantity().multiply(movement.getUnitPrice());
                
                if ("IN".equals(movement.getMovementType())) {
                    totalInValue = totalInValue.add(movementValue);
                } else if ("OUT".equals(movement.getMovementType())) {
                    totalOutValue = totalOutValue.add(movementValue);
                }
                
                movementTypeCount.merge(movement.getMovementType().toString(), 1L, Long::sum);
                movementTypeValue.merge(movement.getMovementType().toString(), movementValue, BigDecimal::add);
            }

            report.put("totalMovements", movements.size());
            report.put("totalInValue", totalInValue);
            report.put("totalOutValue", totalOutValue);
            report.put("netMovement", totalInValue.subtract(totalOutValue));
            report.put("movementTypeCount", movementTypeCount);
            report.put("movementTypeValue", movementTypeValue);

            // Mouvements par jour
            Map<String, List<InventoryMovement>> movementsByDate = movements.stream()
                .collect(Collectors.groupingBy(m -> m.getMovementDate().toString()));
            report.put("movementsByDate", movementsByDate);

            return report;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport de mouvements: " + e.getMessage());
        }
    }

    // ==================== RAPPORTS D'ANALYSE D'INVENTAIRE ====================

    /**
     * Rapport d'analyse d'inventaire complet
     */
    public Map<String, Object> generateInventoryAnalysisReport(Long analysisId) {
        try {
            InventoryAnalysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analyse non trouvée"));

            Map<String, Object> report = new HashMap<>();
            report.put("reportType", "INVENTORY_ANALYSIS_REPORT");
            report.put("generatedAt", LocalDateTime.now());
            report.put("analysisId", analysisId);
            report.put("analysisNumber", analysis.getAnalysisNumber());

            // Résumé de l'analyse
            report.put("analysis", analysis);
            report.put("summary", Map.of(
                "totalItems", analysis.getTotalItemsAnalyzed(),
                "itemsWithVariance", analysis.getItemsWithVariance(),
                "totalAccountingValue", analysis.getTotalAccountingValue(),
                "totalPhysicalValue", analysis.getTotalPhysicalValue(),
                "totalVariance", analysis.getTotalVariance(),
                "variancePercentage", analysis.getVariancePercentage()
            ));

            // Détails des écarts
            List<InventoryAnalysisDetail> details = analysisDetailRepository.findByAnalysisId(analysisId);
            report.put("details", details);

            // Statistiques par type d'écart
            Map<String, Long> varianceStats = details.stream()
                .collect(Collectors.groupingBy(
                    InventoryAnalysisDetail::getVarianceType,
                    Collectors.counting()
                ));
            report.put("varianceStats", varianceStats);

            // Top 10 des plus gros écarts
            List<InventoryAnalysisDetail> topVariances = details.stream()
                .filter(d -> d.getValueVariance().compareTo(BigDecimal.ZERO) != 0)
                .sorted((d1, d2) -> d2.getValueVariance().abs().compareTo(d1.getValueVariance().abs()))
                .limit(10)
                .collect(Collectors.toList());
            report.put("topVariances", topVariances);

            return report;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport d'analyse: " + e.getMessage());
        }
    }

    // ==================== TABLEAUX DE BORD ====================

    /**
     * Tableau de bord complet des immobilisations et stocks
     */
    public Map<String, Object> generateDashboard(Long companyId, String countryCode, String accountingStandard) {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("dashboardType", "ASSET_INVENTORY_DASHBOARD");
            dashboard.put("generatedAt", LocalDateTime.now());
            dashboard.put("companyId", companyId);

            // KPIs des immobilisations
            List<Asset> assets = assetRepository.findByCompanyIdOrderByAssetNameAsc(companyId);
            BigDecimal totalAssetValue = assets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalAssetDepreciation = assets.stream()
                .map(asset -> asset.getCurrentValue().multiply(asset.getDepreciationRate()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            dashboard.put("assetKPIs", Map.of(
                "totalAssets", assets.size(),
                "totalAssetValue", totalAssetValue,
                "totalDepreciation", totalAssetDepreciation,
                "averageAssetValue", assets.isEmpty() ? BigDecimal.ZERO : 
                    totalAssetValue.divide(new BigDecimal(assets.size()), 2, RoundingMode.HALF_UP)
            ));

            // KPIs des stocks
            List<Inventory> inventories = inventoryRepository.findByCompanyIdOrderByProductNameAsc(companyId);
            BigDecimal totalInventoryValue = inventories.stream()
                .map(inv -> inv.getQuantityOnHand().multiply(inv.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            int totalInventoryItems = inventories.stream()
                .mapToInt(inv -> inv.getQuantityOnHand().intValue())
                .sum();

            dashboard.put("inventoryKPIs", Map.of(
                "totalProducts", inventories.size(),
                "totalInventoryValue", totalInventoryValue,
                "totalInventoryItems", totalInventoryItems,
                "averageProductValue", inventories.isEmpty() ? BigDecimal.ZERO :
                    totalInventoryValue.divide(new BigDecimal(inventories.size()), 2, RoundingMode.HALF_UP)
            ));

            // Alertes
            List<Asset> criticalAssets = assets.stream()
                .filter(asset -> {
                    BigDecimal depreciationRate = asset.getDepreciationRate();
                    return depreciationRate != null && depreciationRate.compareTo(new BigDecimal("80")) > 0;
                })
                .collect(Collectors.toList());

            List<Inventory> lowStockItems = inventories.stream()
                .filter(inv -> inv.getQuantityOnHand().compareTo(inv.getMinimumStock()) < 0)
                .collect(Collectors.toList());

            dashboard.put("alerts", Map.of(
                "criticalAssets", criticalAssets.size(),
                "lowStockItems", lowStockItems.size(),
                "criticalAssetsList", criticalAssets.stream().limit(5).collect(Collectors.toList()),
                "lowStockItemsList", lowStockItems.stream().limit(5).collect(Collectors.toList())
            ));

            // Graphiques (données pour frontend)
            Map<String, Long> assetTypeDistribution = assets.stream()
                .collect(Collectors.groupingBy(
                    asset -> asset.getAssetType().toString(),
                    Collectors.counting()
                ));

            Map<String, Long> inventoryStatusDistribution = inventories.stream()
                .collect(Collectors.groupingBy(
                    inv -> inv.getStatus().toString(),
                    Collectors.counting()
                ));

            dashboard.put("charts", Map.of(
                "assetTypeDistribution", assetTypeDistribution,
                "inventoryStatusDistribution", inventoryStatusDistribution
            ));

            return dashboard;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du tableau de bord: " + e.getMessage());
        }
    }
}
