package com.ecomptaia.repository;

import com.ecomptaia.entity.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des centres de coûts
 */
@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {
    
    /**
     * Trouve les centres de coûts par entreprise
     */
    List<CostCenter> findByCompanyId(Long companyId);
    
    /**
     * Trouve un centre de coût par code et entreprise
     */
    Optional<CostCenter> findByCodeAndCompanyId(String code, Long companyId);
    
    /**
     * Trouve les centres de coûts actifs
     */
    List<CostCenter> findByIsActiveTrue();
    
    /**
     * Trouve les centres de coûts actifs par entreprise
     */
    List<CostCenter> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouve les centres de coûts par type
     */
    List<CostCenter> findByType(String type);
    
    /**
     * Trouve les centres de coûts par entreprise et type
     */
    List<CostCenter> findByCompanyIdAndType(Long companyId, String type);
    
    /**
     * Trouve les centres de coûts par nom
     */
    List<CostCenter> findByNameContainingIgnoreCase(String name);
    
    /**
     * Trouve les centres de coûts par entreprise et nom
     */
    List<CostCenter> findByCompanyIdAndNameContainingIgnoreCase(Long companyId, String name);
    
    /**
     * Vérifie si un centre de coût existe par code et entreprise
     */
    boolean existsByCodeAndCompanyId(String code, Long companyId);
    
    /**
     * Trouve les centres de coûts créés après une date
     */
    List<CostCenter> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Trouve les centres de coûts mis à jour après une date
     */
    List<CostCenter> findByUpdatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Compte les centres de coûts par entreprise
     */
    @Query("SELECT cc.companyId, COUNT(cc) FROM CostCenter cc WHERE cc.isActive = true GROUP BY cc.companyId")
    List<Object[]> countByCompany();
    
    /**
     * Compte les centres de coûts par type
     */
    @Query("SELECT cc.type, COUNT(cc) FROM CostCenter cc WHERE cc.isActive = true GROUP BY cc.type")
    List<Object[]> countByType();
    
    /**
     * Trouve les centres de coûts par niveau hiérarchique
     */
    List<CostCenter> findByLevel(Integer level);
    
    /**
     * Trouve les centres de coûts par entreprise et niveau
     */
    List<CostCenter> findByCompanyIdAndLevel(Long companyId, Integer level);
    
    /**
     * Trouve les centres de coûts parents (sans parent)
     */
    @Query("SELECT cc FROM CostCenter cc WHERE cc.parentId IS NULL AND cc.isActive = true")
    List<CostCenter> findParentCostCenters();
    
    /**
     * Trouve les centres de coûts enfants d'un parent
     */
    List<CostCenter> findByParentId(Long parentId);
    
    /**
     * Trouve les centres de coûts enfants actifs d'un parent
     */
    List<CostCenter> findByParentIdAndIsActiveTrue(Long parentId);
    
    /**
     * Trouve les centres de coûts par entreprise et parent
     */
    List<CostCenter> findByCompanyIdAndParentId(Long companyId, Long parentId);
    
    /**
     * Trouve les centres de coûts par responsable
     */
    List<CostCenter> findByResponsibleUserId(Long responsibleUserId);
    
    /**
     * Trouve les centres de coûts par entreprise et responsable
     */
    List<CostCenter> findByCompanyIdAndResponsibleUserId(Long companyId, Long responsibleUserId);
}
