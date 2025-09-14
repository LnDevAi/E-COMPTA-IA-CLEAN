package com.ecomptaia.repository;

import com.ecomptaia.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des articles d'inventaire
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    /**
     * Trouver les articles actifs d'un inventaire
     */
    List<InventoryItem> findByInventoryIdAndIsActiveTrue(Long inventoryId);
    
    /**
     * Trouver les articles par nom
     */
    List<InventoryItem> findByItemNameContainingIgnoreCaseAndIsActiveTrue(String itemName);
    
    /**
     * Compter les articles d'un inventaire
     */
    long countByInventoryIdAndIsActiveTrue(Long inventoryId);
}
