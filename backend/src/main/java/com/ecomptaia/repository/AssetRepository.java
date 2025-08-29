package com.ecomptaia.repository;

import com.ecomptaia.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    
    // Recherche par entreprise
    List<Asset> findByCompanyIdOrderByAssetNameAsc(Long companyId);
    List<Asset> findByCompanyId(Long companyId);
    
    // Recherche par code d'immobilisation
    Optional<Asset> findByAssetCodeAndCompanyId(String assetCode, Long companyId);
    
    // Recherche par type d'immobilisation
    List<Asset> findByAssetTypeAndCompanyId(Asset.AssetType assetType, Long companyId);
    
    // Recherche par statut
    List<Asset> findByStatusAndCompanyId(Asset.AssetStatus status, Long companyId);
    
    // Recherche par localisation
    List<Asset> findByLocationContainingIgnoreCaseAndCompanyId(String location, Long companyId);
    
    // Recherche par fournisseur
    List<Asset> findBySupplierContainingIgnoreCaseAndCompanyId(String supplier, Long companyId);
    
    // Recherche par nom d'immobilisation
    List<Asset> findByAssetNameContainingIgnoreCaseAndCompanyId(String assetName, Long companyId);
    
    // Recherche par catégorie
    List<Asset> findByCategoryContainingIgnoreCaseAndCompanyId(String category, Long companyId);
    
    // Recherche par date d'achat
    List<Asset> findByPurchaseDateBetweenAndCompanyId(LocalDate startDate, LocalDate endDate, Long companyId);
    
    // Recherche par valeur d'achat
    List<Asset> findByPurchasePriceBetweenAndCompanyId(BigDecimal minPrice, BigDecimal maxPrice, Long companyId);
    
    // Recherche par date de maintenance
    List<Asset> findByNextMaintenanceDateBeforeAndCompanyId(LocalDate date, Long companyId);
    
    // Recherche par date d'expiration de garantie
    List<Asset> findByWarrantyExpiryBeforeAndCompanyId(LocalDate date, Long companyId);
    
    // Recherche par date d'expiration d'assurance
    List<Asset> findByInsuranceExpiryBeforeAndCompanyId(LocalDate date, Long companyId);
    
    // Recherche combinée
    @Query("SELECT a FROM Asset a WHERE a.companyId = :companyId " +
           "AND (:assetType IS NULL OR a.assetType = :assetType) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:location IS NULL OR a.location LIKE %:location%) " +
           "ORDER BY a.assetName")
    List<Asset> findAssetsByFilters(@Param("companyId") Long companyId,
                                   @Param("assetType") Asset.AssetType assetType,
                                   @Param("status") Asset.AssetStatus status,
                                   @Param("location") String location);
    
    // Statistiques par type
    @Query("SELECT a.assetType, COUNT(a), SUM(a.currentValue) FROM Asset a " +
           "WHERE a.companyId = :companyId GROUP BY a.assetType")
    List<Object[]> getAssetStatisticsByType(@Param("companyId") Long companyId);
    
    // Statistiques par statut
    @Query("SELECT a.status, COUNT(a), SUM(a.currentValue) FROM Asset a " +
           "WHERE a.companyId = :companyId GROUP BY a.status")
    List<Object[]> getAssetStatisticsByStatus(@Param("companyId") Long companyId);
    
    // Valeur totale des immobilisations
    @Query("SELECT SUM(a.currentValue) FROM Asset a WHERE a.companyId = :companyId AND a.status = 'ACTIVE'")
    BigDecimal getTotalAssetValue(@Param("companyId") Long companyId);
    
    // Nombre total d'immobilisations
    @Query("SELECT COUNT(a) FROM Asset a WHERE a.companyId = :companyId")
    Long getTotalAssetCount(@Param("companyId") Long companyId);
    
    // Immobilisations nécessitant une maintenance
    @Query("SELECT a FROM Asset a WHERE a.companyId = :companyId " +
           "AND a.nextMaintenanceDate <= :date AND a.status = 'ACTIVE'")
    List<Asset> getAssetsNeedingMaintenance(@Param("companyId") Long companyId, @Param("date") LocalDate date);
    
    // Immobilisations avec garantie expirée
    @Query("SELECT a FROM Asset a WHERE a.companyId = :companyId " +
           "AND a.warrantyExpiry <= :date AND a.status = 'ACTIVE'")
    List<Asset> getAssetsWithExpiredWarranty(@Param("companyId") Long companyId, @Param("date") LocalDate date);
    
    // Immobilisations avec assurance expirée
    @Query("SELECT a FROM Asset a WHERE a.companyId = :companyId " +
           "AND a.insuranceExpiry <= :date AND a.status = 'ACTIVE'")
    List<Asset> getAssetsWithExpiredInsurance(@Param("companyId") Long companyId, @Param("date") LocalDate date);
    
    // Vérification d'existence par code
    boolean existsByAssetCodeAndCompanyId(String assetCode, Long companyId);
    
    // Vérification d'existence par numéro de série
    boolean existsBySerialNumberAndCompanyId(String serialNumber, Long companyId);
}
