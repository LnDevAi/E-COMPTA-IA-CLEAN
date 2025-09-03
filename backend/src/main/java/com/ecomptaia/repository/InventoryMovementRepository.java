package com.ecomptaia.repository;

import com.ecomptaia.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    
    // Recherche par entreprise
    List<InventoryMovement> findByCompanyIdOrderByMovementDateDesc(Long companyId);
    
    // Recherche par code de mouvement
    Optional<InventoryMovement> findByMovementCodeAndCompanyId(String movementCode, Long companyId);
    
    // Recherche par produit
    List<InventoryMovement> findByProductIdAndCompanyIdOrderByMovementDateDesc(Long productId, Long companyId);
    
    // Recherche par type de mouvement
    List<InventoryMovement> findByMovementTypeAndCompanyId(InventoryMovement.MovementType movementType, Long companyId);
    
    // Recherche par statut
    List<InventoryMovement> findByStatusAndCompanyId(InventoryMovement.MovementStatus status, Long companyId);
    
    // Recherche par date de mouvement
    List<InventoryMovement> findByMovementDateBetweenAndCompanyId(LocalDate startDate, LocalDate endDate, Long companyId);
    
    // Recherche par entrepôt source
    List<InventoryMovement> findByWarehouseFromContainingIgnoreCaseAndCompanyId(String warehouseFrom, Long companyId);
    
    // Recherche par entrepôt destination
    List<InventoryMovement> findByWarehouseToContainingIgnoreCaseAndCompanyId(String warehouseTo, Long companyId);
    
    // Recherche par numéro de référence
    List<InventoryMovement> findByReferenceNumberContainingIgnoreCaseAndCompanyId(String referenceNumber, Long companyId);
    
    // Recherche par type de référence
    List<InventoryMovement> findByReferenceTypeContainingIgnoreCaseAndCompanyId(String referenceType, Long companyId);
    
    // Recherche par numéro de lot
    List<InventoryMovement> findByBatchNumberContainingIgnoreCaseAndCompanyId(String batchNumber, Long companyId);
    
    // Recherche par montant total
    List<InventoryMovement> findByTotalAmountBetweenAndCompanyId(BigDecimal minAmount, BigDecimal maxAmount, Long companyId);
    
    // Recherche par quantité
    List<InventoryMovement> findByQuantityBetweenAndCompanyId(BigDecimal minQuantity, BigDecimal maxQuantity, Long companyId);
    
    // Recherche par approuvé par
    List<InventoryMovement> findByApprovedByAndCompanyId(Long approvedBy, Long companyId);
    
    // Recherche combinée
    @Query("SELECT im FROM InventoryMovement im WHERE im.companyId = :companyId " +
           "AND (:productId IS NULL OR im.productId = :productId) " +
           "AND (:movementType IS NULL OR im.movementType = :movementType) " +
           "AND (:status IS NULL OR im.status = :status) " +
           "AND (:startDate IS NULL OR im.movementDate >= :startDate) " +
           "AND (:endDate IS NULL OR im.movementDate <= :endDate) " +
           "ORDER BY im.movementDate DESC")
    List<InventoryMovement> findMovementsByFilters(@Param("companyId") Long companyId,
                                                  @Param("productId") Long productId,
                                                  @Param("movementType") InventoryMovement.MovementType movementType,
                                                  @Param("status") InventoryMovement.MovementStatus status,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
    
    // Statistiques par type de mouvement
    @Query("SELECT im.movementType, COUNT(im), SUM(im.quantity), SUM(im.totalAmount) FROM InventoryMovement im " +
           "WHERE im.companyId = :companyId AND im.status = 'COMPLETED' GROUP BY im.movementType")
    List<Object[]> getMovementStatisticsByType(@Param("companyId") Long companyId);
    
    // Statistiques par statut
    @Query("SELECT im.status, COUNT(im), SUM(im.quantity), SUM(im.totalAmount) FROM InventoryMovement im " +
           "WHERE im.companyId = :companyId GROUP BY im.status")
    List<Object[]> getMovementStatisticsByStatus(@Param("companyId") Long companyId);
    
    // Statistiques par entrepôt
    @Query("SELECT im.warehouseFrom, COUNT(im), SUM(im.quantity), SUM(im.totalAmount) FROM InventoryMovement im " +
           "WHERE im.companyId = :companyId AND im.status = 'COMPLETED' GROUP BY im.warehouseFrom")
    List<Object[]> getMovementStatisticsByWarehouseFrom(@Param("companyId") Long companyId);
    
    // Mouvements par période
    @Query("SELECT DATE(im.movementDate), COUNT(im), SUM(im.quantity), SUM(im.totalAmount) FROM InventoryMovement im " +
           "WHERE im.companyId = :companyId AND im.movementDate BETWEEN :startDate AND :endDate " +
           "AND im.status = 'COMPLETED' GROUP BY DATE(im.movementDate) ORDER BY DATE(im.movementDate)")
    List<Object[]> getMovementsByPeriod(@Param("companyId") Long companyId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
    
    // Mouvements récents (derniers 30 jours)
    @Query("SELECT im FROM InventoryMovement im WHERE im.companyId = :companyId " +
           "AND im.movementDate >= :date ORDER BY im.movementDate DESC")
    List<InventoryMovement> getRecentMovements(@Param("companyId") Long companyId, @Param("date") LocalDate date);
    
    // Mouvements en attente d'approbation
    @Query("SELECT im FROM InventoryMovement im WHERE im.companyId = :companyId " +
           "AND im.status = 'PENDING' ORDER BY im.createdAt DESC")
    List<InventoryMovement> getPendingMovements(@Param("companyId") Long companyId);
    
    // Mouvements par produit et période
    @Query("SELECT im FROM InventoryMovement im WHERE im.companyId = :companyId " +
           "AND im.productId = :productId " +
           "AND im.movementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY im.movementDate DESC")
    List<InventoryMovement> getMovementsByProductAndPeriod(@Param("companyId") Long companyId,
                                                          @Param("productId") Long productId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
    
    // Mouvements de transfert
    @Query("SELECT im FROM InventoryMovement im WHERE im.companyId = :companyId " +
           "AND im.movementType = 'TRANSFER' " +
           "AND im.status = 'COMPLETED' " +
           "ORDER BY im.movementDate DESC")
    List<InventoryMovement> getTransferMovements(@Param("companyId") Long companyId);
    
    // Mouvements d'ajustement
    @Query("SELECT im FROM InventoryMovement im WHERE im.companyId = :companyId " +
           "AND im.movementType = 'ADJUSTMENT' " +
           "ORDER BY im.movementDate DESC")
    List<InventoryMovement> getAdjustmentMovements(@Param("companyId") Long companyId);
    
    // Mouvements d'expiration
    @Query("SELECT im FROM InventoryMovement im WHERE im.companyId = :companyId " +
           "AND im.movementType = 'EXPIRY' " +
           "AND im.movementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY im.movementDate DESC")
    List<InventoryMovement> getExpiryMovements(@Param("companyId") Long companyId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    // Vérification d'existence par code de mouvement
    boolean existsByMovementCodeAndCompanyId(String movementCode, Long companyId);
    
    // Nombre total de mouvements
    @Query("SELECT COUNT(im) FROM InventoryMovement im WHERE im.companyId = :companyId")
    Long getTotalMovementCount(@Param("companyId") Long companyId);
    
    // Valeur totale des mouvements
    @Query("SELECT SUM(im.totalAmount) FROM InventoryMovement im WHERE im.companyId = :companyId AND im.status = 'COMPLETED'")
    BigDecimal getTotalMovementValue(@Param("companyId") Long companyId);
    
    // Quantité totale des mouvements
    @Query("SELECT SUM(im.quantity) FROM InventoryMovement im WHERE im.companyId = :companyId AND im.status = 'COMPLETED'")
    BigDecimal getTotalMovementQuantity(@Param("companyId") Long companyId);
}



