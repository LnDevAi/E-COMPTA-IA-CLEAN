package com.ecomptaia.repository;

import com.ecomptaia.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des mouvements d'inventaire
 */
@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    
    /**
     * Trouver les mouvements d'un inventaire
     */
    List<InventoryMovement> findByInventoryIdOrderByCreatedAtDesc(Long inventoryId);
    
    /**
     * Trouver les mouvements d'un article
     */
    List<InventoryMovement> findByItemIdOrderByCreatedAtAsc(Long itemId);
    
    /**
     * Trouver les mouvements par type
     */
    List<InventoryMovement> findByMovementTypeOrderByCreatedAtDesc(String movementType);
}