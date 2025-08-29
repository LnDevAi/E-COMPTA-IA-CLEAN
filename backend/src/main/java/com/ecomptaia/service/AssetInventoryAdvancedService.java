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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetInventoryAdvancedService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMovementRepository movementRepository;

    @Autowired
    private InventoryAnalysisRepository analysisRepository;

    @Autowired
    private InventoryAnalysisDetailRepository analysisDetailRepository;

    @Autowired
    private AssetInventoryDocumentRepository documentRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    // === GÉNÉRATION D'ÉCRITURES COMPTABLES ===

    /**
     * Générer une écriture comptable pour l'acquisition d'un actif
     */
    public JournalEntry generateAssetAcquisitionEntry(Asset asset, String supplierCode, String supplierName) {
        try {
            // Créer l'écriture comptable
            JournalEntry entry = new JournalEntry();
            entry.setEntryNumber("ECR_" + System.currentTimeMillis() + "_" + asset.getAssetCode());
            entry.setEntryDate(LocalDate.now());
            entry.setDescription("Acquisition d'immobilisation - " + asset.getAssetName() + " (" + asset.getAssetCode() + ")");
            entry.setJournalType("ACHATS");
            entry.setCurrency("XOF");
            entry.setCompanyId(asset.getCompanyId());
            entry.setCountryCode(asset.getCountryCode());
            entry.setAccountingStandard(asset.getAccountingStandard());
            entry.setReference("ASSET_" + asset.getAssetCode());
            entry.setDocumentNumber("FACT_" + asset.getAssetCode());
            entry.setStatus("BROUILLON");
            entry.setCreatedAt(LocalDateTime.now());
            entry.setUpdatedAt(LocalDateTime.now());

            // Calculer les montants
            BigDecimal purchaseAmount = asset.getPurchasePrice();
            BigDecimal vatAmount = purchaseAmount.multiply(new BigDecimal("0.18")); // TVA 18%
            BigDecimal totalAmount = purchaseAmount.add(vatAmount);

            entry.setTotalDebit(totalAmount);
            entry.setTotalCredit(totalAmount);

            // Sauvegarder l'écriture
            JournalEntry savedEntry = journalEntryRepository.save(entry);

            // Créer les lignes d'écriture
            List<AccountEntry> accountEntries = new ArrayList<>();

            // Débit - Immobilisation
            AccountEntry assetEntry = new AccountEntry();
            assetEntry.setJournalEntryId(savedEntry.getId());
            assetEntry.setAccountNumber("211"); // Immobilisations corporelles
            assetEntry.setAccountName("Immobilisations corporelles");
            assetEntry.setAccountType("DEBIT");
            assetEntry.setAmount(purchaseAmount);
            assetEntry.setDescription("Acquisition " + asset.getAssetName());
            assetEntry.setCompanyId(asset.getCompanyId());
            assetEntry.setCountryCode(asset.getCountryCode());
            assetEntry.setAccountingStandard(asset.getAccountingStandard());
            assetEntry.setThirdPartyCode(supplierCode);
            accountEntries.add(assetEntry);

            // Débit - TVA déductible
            AccountEntry vatEntry = new AccountEntry();
            vatEntry.setJournalEntryId(savedEntry.getId());
            vatEntry.setAccountNumber("4456"); // TVA déductible
            vatEntry.setAccountName("TVA déductible");
            vatEntry.setAccountType("DEBIT");
            vatEntry.setAmount(vatAmount);
            vatEntry.setDescription("TVA acquisition " + asset.getAssetName());
            vatEntry.setCompanyId(asset.getCompanyId());
            vatEntry.setCountryCode(asset.getCountryCode());
            vatEntry.setAccountingStandard(asset.getAccountingStandard());
            vatEntry.setThirdPartyCode(supplierCode);
            accountEntries.add(vatEntry);

            // Crédit - Fournisseur
            AccountEntry supplierEntry = new AccountEntry();
            supplierEntry.setJournalEntryId(savedEntry.getId());
            supplierEntry.setAccountNumber("401"); // Fournisseurs
            supplierEntry.setAccountName("Fournisseurs");
            supplierEntry.setAccountType("CREDIT");
            supplierEntry.setAmount(totalAmount);
            supplierEntry.setDescription("Dette fournisseur " + supplierName);
            supplierEntry.setCompanyId(asset.getCompanyId());
            supplierEntry.setCountryCode(asset.getCountryCode());
            supplierEntry.setAccountingStandard(asset.getAccountingStandard());
            supplierEntry.setThirdPartyCode(supplierCode);
            accountEntries.add(supplierEntry);

            // Sauvegarder les lignes
            accountEntryRepository.saveAll(accountEntries);

            return savedEntry;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'écriture d'acquisition: " + e.getMessage());
        }
    }

    /**
     * Générer une écriture comptable pour la dépréciation d'un actif
     */
    public JournalEntry generateAssetDepreciationEntry(Asset asset, BigDecimal depreciationAmount) {
        try {
            // Créer l'écriture comptable
            JournalEntry entry = new JournalEntry();
            entry.setEntryNumber("ECR_" + System.currentTimeMillis() + "_DEP_" + asset.getAssetCode());
            entry.setEntryDate(LocalDate.now());
            entry.setDescription("Dotation aux amortissements - " + asset.getAssetName() + " (" + asset.getAssetCode() + ")");
            entry.setJournalType("OD");
            entry.setCurrency("XOF");
            entry.setCompanyId(asset.getCompanyId());
            entry.setCountryCode(asset.getCountryCode());
            entry.setAccountingStandard(asset.getAccountingStandard());
            entry.setReference("DEP_" + asset.getAssetCode());
            entry.setStatus("BROUILLON");
            entry.setCreatedAt(LocalDateTime.now());
            entry.setUpdatedAt(LocalDateTime.now());

            entry.setTotalDebit(depreciationAmount);
            entry.setTotalCredit(depreciationAmount);

            // Sauvegarder l'écriture
            JournalEntry savedEntry = journalEntryRepository.save(entry);

            // Créer les lignes d'écriture
            List<AccountEntry> accountEntries = new ArrayList<>();

            // Débit - Dotations aux amortissements
            AccountEntry depreciationEntry = new AccountEntry();
            depreciationEntry.setJournalEntryId(savedEntry.getId());
            depreciationEntry.setAccountNumber("6811"); // Dotations aux amortissements
            depreciationEntry.setAccountName("Dotations aux amortissements");
            depreciationEntry.setAccountType("DEBIT");
            depreciationEntry.setAmount(depreciationAmount);
            depreciationEntry.setDescription("Dotation " + asset.getAssetName());
            depreciationEntry.setCompanyId(asset.getCompanyId());
            depreciationEntry.setCountryCode(asset.getCountryCode());
            depreciationEntry.setAccountingStandard(asset.getAccountingStandard());
            accountEntries.add(depreciationEntry);

            // Crédit - Amortissements cumulés
            AccountEntry accumulatedEntry = new AccountEntry();
            accumulatedEntry.setJournalEntryId(savedEntry.getId());
            accumulatedEntry.setAccountNumber("2811"); // Amortissements cumulés
            accumulatedEntry.setAccountName("Amortissements cumulés");
            accumulatedEntry.setAccountType("CREDIT");
            accumulatedEntry.setAmount(depreciationAmount);
            accumulatedEntry.setDescription("Amortissement " + asset.getAssetName());
            accumulatedEntry.setCompanyId(asset.getCompanyId());
            accumulatedEntry.setCountryCode(asset.getCountryCode());
            accumulatedEntry.setAccountingStandard(asset.getAccountingStandard());
            accountEntries.add(accumulatedEntry);

            // Sauvegarder les lignes
            accountEntryRepository.saveAll(accountEntries);

            return savedEntry;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'écriture de dépréciation: " + e.getMessage());
        }
    }

    /**
     * Générer une écriture comptable pour un mouvement d'inventaire
     */
    public JournalEntry generateInventoryMovementEntry(InventoryMovement movement, Inventory inventory) {
        try {
            // Créer l'écriture comptable
            JournalEntry entry = new JournalEntry();
            entry.setEntryNumber("ECR_" + System.currentTimeMillis() + "_MOV_" + movement.getMovementCode());
            entry.setEntryDate(LocalDate.now());
            entry.setDescription("Mouvement de stock - " + inventory.getProductName() + " (" + inventory.getProductCode() + ") - " + movement.getMovementType());
            entry.setJournalType("STOCK");
            entry.setCurrency("XOF");
            entry.setCompanyId(movement.getCompanyId());
            entry.setCountryCode(movement.getCountryCode());
            entry.setAccountingStandard(movement.getAccountingStandard());
            entry.setReference("MOV_" + movement.getMovementCode());
            entry.setStatus("BROUILLON");
            entry.setCreatedAt(LocalDateTime.now());
            entry.setUpdatedAt(LocalDateTime.now());

            // Calculer le montant
            BigDecimal unitCost = inventory.getUnitPrice();
            BigDecimal totalAmount = unitCost.multiply(movement.getQuantity());

            entry.setTotalDebit(totalAmount);
            entry.setTotalCredit(totalAmount);

            // Sauvegarder l'écriture
            JournalEntry savedEntry = journalEntryRepository.save(entry);

            // Créer les lignes d'écriture selon le type de mouvement
            List<AccountEntry> accountEntries = new ArrayList<>();

            if ("IN".equals(movement.getMovementType())) {
                // Entrée en stock
                AccountEntry stockEntry = new AccountEntry();
                stockEntry.setJournalEntryId(savedEntry.getId());
                stockEntry.setAccountNumber("31"); // Stocks
                stockEntry.setAccountName("Stocks de marchandises");
                stockEntry.setAccountType("DEBIT");
                stockEntry.setAmount(totalAmount);
                stockEntry.setDescription("Entrée stock " + inventory.getProductName());
                stockEntry.setCompanyId(movement.getCompanyId());
                stockEntry.setCountryCode(movement.getCountryCode());
                stockEntry.setAccountingStandard(movement.getAccountingStandard());
                accountEntries.add(stockEntry);

                AccountEntry supplierEntry = new AccountEntry();
                supplierEntry.setJournalEntryId(savedEntry.getId());
                supplierEntry.setAccountNumber("401"); // Fournisseurs
                supplierEntry.setAccountName("Fournisseurs");
                supplierEntry.setAccountType("CREDIT");
                supplierEntry.setAmount(totalAmount);
                supplierEntry.setDescription("Achat stock " + inventory.getProductName());
                supplierEntry.setCompanyId(movement.getCompanyId());
                supplierEntry.setCountryCode(movement.getCountryCode());
                supplierEntry.setAccountingStandard(movement.getAccountingStandard());
                accountEntries.add(supplierEntry);

            } else if ("OUT".equals(movement.getMovementType())) {
                // Sortie de stock
                AccountEntry costEntry = new AccountEntry();
                costEntry.setJournalEntryId(savedEntry.getId());
                costEntry.setAccountNumber("607"); // Achats de marchandises
                costEntry.setAccountName("Coût des marchandises vendues");
                costEntry.setAccountType("DEBIT");
                costEntry.setAmount(totalAmount);
                costEntry.setDescription("Sortie stock " + inventory.getProductName());
                costEntry.setCompanyId(movement.getCompanyId());
                costEntry.setCountryCode(movement.getCountryCode());
                costEntry.setAccountingStandard(movement.getAccountingStandard());
                accountEntries.add(costEntry);

                AccountEntry stockEntry = new AccountEntry();
                stockEntry.setJournalEntryId(savedEntry.getId());
                stockEntry.setAccountNumber("31"); // Stocks
                stockEntry.setAccountName("Stocks de marchandises");
                stockEntry.setAccountType("CREDIT");
                stockEntry.setAmount(totalAmount);
                stockEntry.setDescription("Sortie stock " + inventory.getProductName());
                stockEntry.setCompanyId(movement.getCompanyId());
                stockEntry.setCountryCode(movement.getCountryCode());
                stockEntry.setAccountingStandard(movement.getAccountingStandard());
                accountEntries.add(stockEntry);
            }

            // Sauvegarder les lignes
            accountEntryRepository.saveAll(accountEntries);

            return savedEntry;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'écriture de mouvement: " + e.getMessage());
        }
    }
}
