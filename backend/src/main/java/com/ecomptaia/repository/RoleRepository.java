package com.ecomptaia.repository;

import com.ecomptaia.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des rôles
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Trouve un rôle par son nom
     */
    Optional<Role> findByName(String name);
    
    /**
     * Trouve tous les rôles actifs
     */
    List<Role> findByIsActiveTrue();
    
    /**
     * Trouve tous les rôles système
     */
    List<Role> findByIsSystemRoleTrue();
    
    /**
     * Trouve tous les rôles non-système
     */
    List<Role> findByIsSystemRoleFalse();
    
    /**
     * Vérifie si un rôle existe par nom
     */
    boolean existsByName(String name);
    
    /**
     * Trouve les rôles par utilisateur
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findByUserId(@Param("userId") Long userId);
    
    /**
     * Trouve les rôles avec une permission spécifique
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p = :permission")
    List<Role> findByPermission(@Param("permission") Role.Permission permission);
    
    /**
     * Compte le nombre d'utilisateurs par rôle
     */
    @Query("SELECT r.name, COUNT(u) FROM Role r LEFT JOIN r.users u GROUP BY r.id, r.name")
    List<Object[]> countUsersByRole();
    
    /**
     * Trouve les rôles créés après une date
     */
    List<Role> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Trouve les rôles mis à jour après une date
     */
    List<Role> findByUpdatedAtAfter(java.time.LocalDateTime date);
}



