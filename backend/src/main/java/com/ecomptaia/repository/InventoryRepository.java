package com.ecomptaia.repository;

import com.ecomptaia.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    // Recherche par entreprise
    List<Inventory> findByCompanyIdOrderByProductNameAsc(Long companyId);
    List<Inventory> findByCompanyId(Long companyId);
    
    // Recherche par code produit
    Optional<Inventory> findByProductCodeAndCompanyId(String productCode, Long companyId);
    
    // Recherche par catégorie
    List<Inventory> findByCategoryContainingIgnoreCaseAndCompanyId(String category, Long companyId);
    
    // Recherche par statut
    List<Inventory> findByStatusAndCompanyId(Inventory.InventoryStatus status, Long companyId);
    
    // Recherche par fournisseur
    List<Inventory> findBySupplierContainingIgnoreCaseAndCompanyId(String supplier, Long companyId);
    
    // Recherche par entrepôt
    List<Inventory> findByWarehouseContainingIgnoreCaseAndCompanyId(String warehouse, Long companyId);
    
    // Recherche par nom de produit
    List<Inventory> findByProductNameContainingIgnoreCaseAndCompanyId(String productName, Long companyId);
    
    // Recherche par méthode de valorisation
    List<Inventory> findByValuationMethodAndCompanyId(Inventory.ValuationMethod valuationMethod, Long companyId);
    
    // Recherche par date d'expiration
    List<Inventory> findByExpiryDateBeforeAndCompanyId(LocalDate date, Long companyId);
    
    // Recherche par numéro de lot
    List<Inventory> findByBatchNumberContainingIgnoreCaseAndCompanyId(String batchNumber, Long companyId);
    
    // Recherche par quantité en stock
    List<Inventory> findByQuantityOnHandLessThanAndCompanyId(BigDecimal quantity, Long companyId);
    
    // Recherche par point de réapprovisionnement
    List<Inventory> findByQuantityOnHandLessThanEqualAndReorderPointGreaterThanEqualAndCompanyId(
        BigDecimal quantityOnHand, BigDecimal reorderPoint, Long companyId);
    
    // Recherche par prix unitaire
    List<Inventory> findByUnitPriceBetweenAndCompanyId(BigDecimal minPrice, BigDecimal maxPrice, Long companyId);
    
    // Recherche par date de dernier mouvement
    List<Inventory> findByLastMovementDateBeforeAndCompanyId(LocalDate date, Long companyId);
    
    // Recherche combinée
    @Query("SELECT i FROM Inventory i WHERE i.companyId = :companyId " +
           "AND (:category IS NULL OR i.category LIKE %:category%) " +
           "AND (:status IS NULL OR i.status = :status) " +
           "AND (:warehouse IS NULL OR i.warehouse LIKE %:warehouse%) " +
           "AND (:supplier IS NULL OR i.supplier LIKE %:supplier%) " +
           "ORDER BY i.productName")
    List<Inventory> findInventoryByFilters(@Param("companyId") Long companyId,
                                         @Param("category") String category,
                                         @Param("status") Inventory.InventoryStatus status,
                                         @Param("warehouse") String warehouse,
                                         @Param("supplier") String supplier);
    
    // Statistiques par catégorie
    @Query("SELECT i.category, COUNT(i), SUM(i.quantityOnHand), SUM(i.totalValue) FROM Inventory i " +
           "WHERE i.companyId = :companyId GROUP BY i.category")
    List<Object[]> getInventoryStatisticsByCategory(@Param("companyId") Long companyId);
    
    // Statistiques par statut
    @Query("SELECT i.status, COUNT(i), SUM(i.quantityOnHand), SUM(i.totalValue) FROM Inventory i " +
           "WHERE i.companyId = :companyId GROUP BY i.status")
    List<Object[]> getInventoryStatisticsByStatus(@Param("companyId") Long companyId);
    
    // Statistiques par entrepôt
    @Query("SELECT i.warehouse, COUNT(i), SUM(i.quantityOnHand), SUM(i.totalValue) FROM Inventory i " +
           "WHERE i.companyId = :companyId GROUP BY i.warehouse")
    List<Object[]> getInventoryStatisticsByWarehouse(@Param("companyId") Long companyId);
    
    // Valeur totale des stocks
    @Query("SELECT SUM(i.totalValue) FROM Inventory i WHERE i.companyId = :companyId AND i.status = 'ACTIVE'")
    BigDecimal getTotalInventoryValue(@Param("companyId") Long companyId);
    
    // Quantité totale en stock
    @Query("SELECT SUM(i.quantityOnHand) FROM Inventory i WHERE i.companyId = :companyId AND i.status = 'ACTIVE'")
    BigDecimal getTotalInventoryQuantity(@Param("companyId") Long companyId);
    
    // Nombre total de produits
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.companyId = :companyId")
    Long getTotalInventoryCount(@Param("companyId") Long companyId);
    
    // Produits en rupture de stock
    @Query("SELECT i FROM Inventory i WHERE i.companyId = :companyId " +
           "AND i.quantityOnHand <= 0 AND i.status = 'ACTIVE'")
    List<Inventory> getOutOfStockProducts(@Param("companyId") Long companyId);
    
    // Produits à réapprovisionner
    @Query("SELECT i FROM Inventory i WHERE i.companyId = :companyId " +
           "AND i.quantityOnHand <= i.reorderPoint AND i.status = 'ACTIVE'")
    List<Inventory> getProductsToReorder(@Param("companyId") Long companyId);
    
    // Produits expirés
    @Query("SELECT i FROM Inventory i WHERE i.companyId = :companyId " +
           "AND i.expiryDate <= :date AND i.status = 'ACTIVE'")
    List<Inventory> getExpiredProducts(@Param("companyId") Long companyId, @Param("date") LocalDate date);
    
    // Produits expirant bientôt (dans les 30 jours)
    @Query("SELECT i FROM Inventory i WHERE i.companyId = :companyId " +
           "AND i.expiryDate BETWEEN :startDate AND :endDate AND i.status = 'ACTIVE'")
    List<Inventory> getProductsExpiringSoon(@Param("companyId") Long companyId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    // Produits avec mouvement récent
    @Query("SELECT i FROM Inventory i WHERE i.companyId = :companyId " +
           "AND i.lastMovementDate >= :date ORDER BY i.lastMovementDate DESC")
    List<Inventory> getRecentlyMovedProducts(@Param("companyId") Long companyId, @Param("date") LocalDate date);
    
    // Vérification d'existence par code produit
    boolean existsByProductCodeAndCompanyId(String productCode, Long companyId);
    
    // Vérification d'existence par numéro de lot
    boolean existsByBatchNumberAndCompanyId(String batchNumber, Long companyId);
    
    // Recherche par critères de valorisation
    @Query("SELECT i FROM Inventory i WHERE i.companyId = :companyId " +
           "AND i.valuationMethod = :valuationMethod " +
           "AND i.averageCost IS NOT NULL " +
           "ORDER BY i.averageCost DESC")
    List<Inventory> findByValuationMethodOrderByAverageCost(@Param("companyId") Long companyId,
                                                           @Param("valuationMethod") Inventory.ValuationMethod valuationMethod);
}
