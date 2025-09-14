package com.ecomptaia.repository;

import com.ecomptaia.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des inventaires
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    /**
     * Trouver les inventaires actifs d'une entreprise
     */
    List<Inventory> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouver les inventaires par nom
     */
    List<Inventory> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    /**
     * Compter les inventaires d'une entreprise
     */
    long countByCompanyIdAndIsActiveTrue(Long companyId);
}