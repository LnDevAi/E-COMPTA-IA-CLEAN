package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.repository.JournalEntryRepository;
import com.ecomptaia.repository.AccountEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryAnalysisService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryAnalysisRepository analysisRepository;

    @Autowired
    private InventoryAnalysisDetailRepository analysisDetailRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    /**
     * Créer une nouvelle analyse d'inventaire
     */
    public InventoryAnalysis createInventoryAnalysis(Long companyId, String analysisType, String countryCode, String accountingStandard) {
        try {
            String analysisNumber = "ANAL_" + System.currentTimeMillis();
            InventoryAnalysis analysis = new InventoryAnalysis(companyId, analysisNumber, analysisType, countryCode, accountingStandard);
            analysis.setAnalysisMethod("COMPREHENSIVE");
            return analysisRepository.save(analysis);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de l'analyse: " + e.getMessage());
        }
    }

    /**
     * Analyser les écarts entre la situation comptable et physique
     */
    public Map<String, Object> performInventoryAnalysis(Long analysisId) {
        try {
            InventoryAnalysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analyse non trouvée"));

            analysis.setStatus("IN_PROGRESS");
            analysisRepository.save(analysis);

            List<InventoryAnalysisDetail> details = new ArrayList<>();
            BigDecimal totalAccountingValue = BigDecimal.ZERO;
            BigDecimal totalPhysicalValue = BigDecimal.ZERO;
            int totalItems = 0;
            int itemsWithVariance = 0;

            // Analyser les actifs
            if ("ASSET_ANALYSIS".equals(analysis.getAnalysisType()) || "COMPREHENSIVE".equals(analysis.getAnalysisType())) {
                List<Asset> assets = assetRepository.findByCompanyIdOrderByAssetNameAsc(analysis.getCompanyId());
                
                for (Asset asset : assets) {
                    // Simuler un inventaire physique (en réalité, ceci viendrait d'un formulaire)
                    BigDecimal physicalValue = asset.getCurrentValue().multiply(new BigDecimal("0.95")); // Simulation d'écart
                    
                    BigDecimal variance = physicalValue.subtract(asset.getCurrentValue());
                    String varianceType = variance.compareTo(BigDecimal.ZERO) > 0 ? "SURPLUS" : 
                                        variance.compareTo(BigDecimal.ZERO) < 0 ? "SHORTAGE" : "NONE";

                    InventoryAnalysisDetail detail = new InventoryAnalysisDetail(
                        analysisId, asset.getAssetCode(), asset.getAssetName(), "ASSET", 
                        analysis.getCompanyId(), analysis.getCountryCode(), analysis.getAccountingStandard()
                    );
                    
                    detail.setAccountingQuantity(BigDecimal.ONE);
                    detail.setPhysicalQuantity(BigDecimal.ONE);
                    detail.setQuantityVariance(BigDecimal.ZERO);
                    detail.setAccountingValue(asset.getCurrentValue());
                    detail.setPhysicalValue(physicalValue);
                    detail.setValueVariance(variance);
                    detail.setUnitPrice(asset.getCurrentValue());
                    detail.setVarianceType(varianceType);
                    detail.setVariancePercentage(variance.divide(asset.getCurrentValue(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
                    detail.setVarianceReason("Écart d'inventaire physique");
                    detail.setProposedAction("NONE".equals(varianceType) ? "NONE" : "ADJUSTMENT");

                    details.add(detail);
                    totalAccountingValue = totalAccountingValue.add(asset.getCurrentValue());
                    totalPhysicalValue = totalPhysicalValue.add(physicalValue);
                    totalItems++;
                    if (!"NONE".equals(varianceType)) itemsWithVariance++;
                }
            }

            // Analyser les stocks
            if ("INVENTORY_ANALYSIS".equals(analysis.getAnalysisType()) || "COMPREHENSIVE".equals(analysis.getAnalysisType())) {
                List<Inventory> inventories = inventoryRepository.findAll().stream()
                    .filter(inv -> analysis.getCompanyId().equals(inv.getCompanyId()))
                    .sorted((inv1, inv2) -> inv1.getName().compareTo(inv2.getName()))
                    .collect(java.util.stream.Collectors.toList());
                
                for (Inventory inventory : inventories) {
                    // Simuler un inventaire physique
                    BigDecimal physicalQuantity = inventory.getQuantityOnHand().multiply(new BigDecimal("0.98")); // Simulation d'écart
                    BigDecimal variance = physicalQuantity.subtract(inventory.getQuantityOnHand());
                    String varianceType = variance.compareTo(BigDecimal.ZERO) > 0 ? "SURPLUS" : 
                                        variance.compareTo(BigDecimal.ZERO) < 0 ? "SHORTAGE" : "NONE";

                    InventoryAnalysisDetail detail = new InventoryAnalysisDetail(
                        analysisId, inventory.getProductCode(), inventory.getProductName(), "INVENTORY", 
                        analysis.getCompanyId(), analysis.getCountryCode(), analysis.getAccountingStandard()
                    );
                    
                    detail.setAccountingQuantity(inventory.getQuantityOnHand());
                    detail.setPhysicalQuantity(physicalQuantity);
                    detail.setQuantityVariance(variance);
                    detail.setAccountingValue(inventory.getQuantityOnHand().multiply(inventory.getUnitPrice()));
                    detail.setPhysicalValue(physicalQuantity.multiply(inventory.getUnitPrice()));
                    detail.setValueVariance(variance.multiply(inventory.getUnitPrice()));
                    detail.setUnitPrice(inventory.getUnitPrice());
                    detail.setVarianceType(varianceType);
                    detail.setVariancePercentage(variance.divide(inventory.getQuantityOnHand(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
                    detail.setVarianceReason("Écart d'inventaire physique");
                    detail.setProposedAction("NONE".equals(varianceType) ? "NONE" : "ADJUSTMENT");

                    details.add(detail);
                    totalAccountingValue = totalAccountingValue.add(inventory.getQuantityOnHand().multiply(inventory.getUnitPrice()));
                    totalPhysicalValue = totalPhysicalValue.add(physicalQuantity.multiply(inventory.getUnitPrice()));
                    totalItems++;
                    if (!"NONE".equals(varianceType)) itemsWithVariance++;
                }
            }

            // Sauvegarder les détails
            analysisDetailRepository.saveAll(details);

            // Mettre à jour l'analyse
            analysis.setTotalAccountingValue(totalAccountingValue);
            analysis.setTotalPhysicalValue(totalPhysicalValue);
            analysis.setTotalVariance(totalPhysicalValue.subtract(totalAccountingValue));
            analysis.setVariancePercentage(totalAccountingValue.compareTo(BigDecimal.ZERO) > 0 ? 
                totalPhysicalValue.subtract(totalAccountingValue).divide(totalAccountingValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : 
                BigDecimal.ZERO);
            analysis.setTotalItemsAnalyzed(totalItems);
            analysis.setItemsWithVariance(itemsWithVariance);
            analysis.setStatus("COMPLETED");
            analysis.setUpdatedAt(LocalDateTime.now());

            analysisRepository.save(analysis);

            // Retourner les résultats
            Map<String, Object> results = new HashMap<>();
            results.put("analysis", analysis);
            results.put("details", details);
            results.put("summary", Map.of(
                "totalItems", totalItems,
                "itemsWithVariance", itemsWithVariance,
                "totalAccountingValue", totalAccountingValue,
                "totalPhysicalValue", totalPhysicalValue,
                "totalVariance", totalPhysicalValue.subtract(totalAccountingValue)
            ));

            return results;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'analyse d'inventaire: " + e.getMessage());
        }
    }

    /**
     * Générer des écritures comptables de correction pour les écarts
     */
    public List<JournalEntry> generateCorrectionEntries(Long analysisId) {
        try {
            InventoryAnalysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analyse non trouvée"));

            List<InventoryAnalysisDetail> details = analysisDetailRepository.findByAnalysisId(analysisId);
            List<JournalEntry> correctionEntries = new ArrayList<>();

            for (InventoryAnalysisDetail detail : details) {
                if (!"NONE".equals(detail.getVarianceType()) && !detail.getIsReconciled()) {
                    JournalEntry correctionEntry = generateCorrectionEntry(detail, analysis);
                    correctionEntries.add(correctionEntry);
                    
                    // Marquer comme réconcilié
                    detail.setIsReconciled(true);
                    detail.setReconciledAt(LocalDateTime.now());
                    detail.setAccountingEntryReference(correctionEntry.getEntryNumber());
                    analysisDetailRepository.save(detail);
                }
            }

            // Mettre à jour l'analyse
            analysis.setIsAccountingEntriesGenerated(true);
            analysis.setAccountingEntriesReference("CORR_" + analysis.getAnalysisNumber());
            analysisRepository.save(analysis);

            return correctionEntries;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération des écritures de correction: " + e.getMessage());
        }
    }

    private JournalEntry generateCorrectionEntry(InventoryAnalysisDetail detail, InventoryAnalysis analysis) {
        try {
            JournalEntry entry = new JournalEntry();
            entry.setEntryNumber("ECR_" + System.currentTimeMillis() + "_CORR_" + detail.getItemCode());
            entry.setEntryDate(java.time.LocalDate.now());
            entry.setDescription("Correction d'écart d'inventaire - " + detail.getItemName() + " (" + detail.getItemCode() + ")");
            entry.setJournalType("OD");
            entry.setCurrency("XOF");
            entry.setCompanyId(analysis.getCompanyId());
            entry.setCountryCode(analysis.getCountryCode());
            entry.setAccountingStandard(analysis.getAccountingStandard());
            entry.setReference("CORR_" + detail.getItemCode());
            entry.setStatus("BROUILLON");
            entry.setCreatedAt(LocalDateTime.now());
            entry.setUpdatedAt(LocalDateTime.now());

            BigDecimal correctionAmount = detail.getValueVariance().abs();
            entry.setTotalDebit(correctionAmount);
            entry.setTotalCredit(correctionAmount);

            JournalEntry savedEntry = journalEntryRepository.save(entry);

            List<AccountEntry> accountEntries = new ArrayList<>();

            if ("ASSET".equals(detail.getItemType())) {
                // Correction d'immobilisation
                if ("SHORTAGE".equals(detail.getVarianceType())) {
                    // Débit - Perte sur immobilisation
                    AccountEntry lossEntry = new AccountEntry();
                    lossEntry.setJournalEntryId(savedEntry.getId());
                    lossEntry.setAccountNumber("675"); // Charges exceptionnelles
                    lossEntry.setAccountName("Perte sur immobilisation");
                    lossEntry.setAccountType("DEBIT");
                    lossEntry.setAmount(correctionAmount);
                    lossEntry.setDescription("Perte " + detail.getItemName());
                    lossEntry.setCompanyId(analysis.getCompanyId());
                    lossEntry.setCountryCode(analysis.getCountryCode());
                    lossEntry.setAccountingStandard(analysis.getAccountingStandard());
                    accountEntries.add(lossEntry);

                    // Crédit - Immobilisation
                    AccountEntry assetEntry = new AccountEntry();
                    assetEntry.setJournalEntryId(savedEntry.getId());
                    assetEntry.setAccountNumber("211"); // Immobilisations
                    assetEntry.setAccountName("Immobilisations corporelles");
                    assetEntry.setAccountType("CREDIT");
                    assetEntry.setAmount(correctionAmount);
                    assetEntry.setDescription("Correction " + detail.getItemName());
                    assetEntry.setCompanyId(analysis.getCompanyId());
                    assetEntry.setCountryCode(analysis.getCountryCode());
                    assetEntry.setAccountingStandard(analysis.getAccountingStandard());
                    accountEntries.add(assetEntry);
                }
            } else if ("INVENTORY".equals(detail.getItemType())) {
                // Correction de stock
                if ("SHORTAGE".equals(detail.getVarianceType())) {
                    // Débit - Perte sur stock
                    AccountEntry lossEntry = new AccountEntry();
                    lossEntry.setJournalEntryId(savedEntry.getId());
                    lossEntry.setAccountNumber("607"); // Achats de marchandises
                    lossEntry.setAccountName("Perte sur stock");
                    lossEntry.setAccountType("DEBIT");
                    lossEntry.setAmount(correctionAmount);
                    lossEntry.setDescription("Perte stock " + detail.getItemName());
                    lossEntry.setCompanyId(analysis.getCompanyId());
                    lossEntry.setCountryCode(analysis.getCountryCode());
                    lossEntry.setAccountingStandard(analysis.getAccountingStandard());
                    accountEntries.add(lossEntry);

                    // Crédit - Stock
                    AccountEntry stockEntry = new AccountEntry();
                    stockEntry.setJournalEntryId(savedEntry.getId());
                    stockEntry.setAccountNumber("31"); // Stocks
                    stockEntry.setAccountName("Stocks de marchandises");
                    stockEntry.setAccountType("CREDIT");
                    stockEntry.setAmount(correctionAmount);
                    stockEntry.setDescription("Correction stock " + detail.getItemName());
                    stockEntry.setCompanyId(analysis.getCompanyId());
                    stockEntry.setCountryCode(analysis.getCountryCode());
                    stockEntry.setAccountingStandard(analysis.getAccountingStandard());
                    accountEntries.add(stockEntry);
                }
            }

            accountEntryRepository.saveAll(accountEntries);
            return savedEntry;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'écriture de correction: " + e.getMessage());
        }
    }

    /**
     * Générer un rapport d'analyse d'inventaire
     */
    public Map<String, Object> generateAnalysisReport(Long analysisId) {
        try {
            InventoryAnalysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException("Analyse non trouvée"));

            List<InventoryAnalysisDetail> details = analysisDetailRepository.findByAnalysisId(analysisId);

            Map<String, Object> report = new HashMap<>();
            report.put("analysis", analysis);
            report.put("details", details);
            report.put("summary", Map.of(
                "totalItems", analysis.getTotalItemsAnalyzed(),
                "itemsWithVariance", analysis.getItemsWithVariance(),
                "totalAccountingValue", analysis.getTotalAccountingValue(),
                "totalPhysicalValue", analysis.getTotalPhysicalValue(),
                "totalVariance", analysis.getTotalVariance(),
                "variancePercentage", analysis.getVariancePercentage()
            ));

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

            // Marquer comme rapport généré
            analysis.setIsReportGenerated(true);
            analysis.setReportFilePath("reports/analysis_" + analysis.getAnalysisNumber() + ".pdf");
            analysisRepository.save(analysis);

            return report;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport: " + e.getMessage());
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Obtenir les analyses par entreprise
     */
    public List<InventoryAnalysis> getAnalysesByCompany(Long companyId) {
        return analysisRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    /**
     * Obtenir une analyse par ID
     */
    public InventoryAnalysis getAnalysisById(Long analysisId) {
        return analysisRepository.findById(analysisId).orElse(null);
    }

    /**
     * Obtenir les détails d'une analyse
     */
    public List<InventoryAnalysisDetail> getAnalysisDetails(Long analysisId) {
        return analysisDetailRepository.findByAnalysisId(analysisId);
    }
}
