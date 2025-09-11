package com.ecomptaia.repository;

import com.ecomptaia.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des projets
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    /**
     * Trouve les projets par entreprise
     */
    List<Project> findByCompanyId(Long companyId);
    
    /**
     * Trouve un projet par code et entreprise
     */
    Optional<Project> findByCodeAndCompanyId(String code, Long companyId);
    
    /**
     * Trouve les projets actifs
     */
    List<Project> findByIsActiveTrue();
    
    /**
     * Trouve les projets actifs par entreprise
     */
    List<Project> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouve les projets par statut
     */
    List<Project> findByStatus(Project.ProjectStatus status);
    
    /**
     * Trouve les projets par entreprise et statut
     */
    List<Project> findByCompanyIdAndStatus(Long companyId, Project.ProjectStatus status);
    
    /**
     * Trouve les projets par nom
     */
    List<Project> findByNameContainingIgnoreCase(String name);
    
    /**
     * Trouve les projets par entreprise et nom
     */
    List<Project> findByCompanyIdAndNameContainingIgnoreCase(Long companyId, String name);
    
    /**
     * Trouve les projets par chef de projet
     */
    List<Project> findByProjectManagerId(Long projectManagerId);
    
    /**
     * Trouve les projets par entreprise et chef de projet
     */
    List<Project> findByCompanyIdAndProjectManagerId(Long companyId, Long projectManagerId);
    
    /**
     * Trouve les projets par client
     */
    List<Project> findByClientId(Long clientId);
    
    /**
     * Trouve les projets par entreprise et client
     */
    List<Project> findByCompanyIdAndClientId(Long companyId, Long clientId);
    
    /**
     * Trouve les projets par date de début
     */
    List<Project> findByStartDate(LocalDate startDate);
    
    /**
     * Trouve les projets par date de fin
     */
    List<Project> findByEndDate(LocalDate endDate);
    
    /**
     * Trouve les projets par période de début
     */
    List<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Trouve les projets par période de fin
     */
    List<Project> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Trouve les projets en cours
     */
    @Query("SELECT p FROM Project p WHERE p.startDate <= :currentDate AND (p.endDate IS NULL OR p.endDate >= :currentDate)")
    List<Project> findActiveProjects(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Trouve les projets en cours par entreprise
     */
    @Query("SELECT p FROM Project p WHERE p.company.id = :companyId AND p.startDate <= :currentDate AND (p.endDate IS NULL OR p.endDate >= :currentDate)")
    List<Project> findActiveProjectsByCompany(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);
    
    /**
     * Trouve les projets terminés
     */
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate")
    List<Project> findCompletedProjects(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Trouve les projets terminés par entreprise
     */
    @Query("SELECT p FROM Project p WHERE p.company.id = :companyId AND p.endDate < :currentDate")
    List<Project> findCompletedProjectsByCompany(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);
    
    /**
     * Trouve les projets en retard
     */
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate AND p.status <> 'COMPLETED'")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Trouve les projets en retard par entreprise
     */
    @Query("SELECT p FROM Project p WHERE p.company.id = :companyId AND p.endDate < :currentDate AND p.status <> 'COMPLETED'")
    List<Project> findOverdueProjectsByCompany(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);
    
    /**
     * Vérifie si un projet existe par code et entreprise
     */
    boolean existsByCodeAndCompanyId(String code, Long companyId);
    
    /**
     * Trouve les projets créés après une date
     */
    List<Project> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Trouve les projets mis à jour après une date
     */
    List<Project> findByUpdatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Compte les projets par entreprise
     */
    @Query("SELECT p.company.id, COUNT(p) FROM Project p GROUP BY p.company.id")
    List<Object[]> countByCompany();
    
    /**
     * Compte les projets par statut
     */
    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countByStatus();
    
    /**
     * Compte les projets par chef de projet
     */
    @Query("SELECT p.projectManagerId, COUNT(p) FROM Project p GROUP BY p.projectManagerId")
    List<Object[]> countByProjectManager();
    
    /**
     * Trouve les projets par budget minimum
     */
    @Query("SELECT p FROM Project p WHERE p.budget >= :minBudget")
    List<Project> findByBudgetGreaterThanEqual(@Param("minBudget") java.math.BigDecimal minBudget);
    
    /**
     * Trouve les projets par budget maximum
     */
    @Query("SELECT p FROM Project p WHERE p.budget <= :maxBudget")
    List<Project> findByBudgetLessThanEqual(@Param("maxBudget") java.math.BigDecimal maxBudget);
    
    /**
     * Trouve les projets par plage de budget
     */
    @Query("SELECT p FROM Project p WHERE p.budget BETWEEN :minBudget AND :maxBudget")
    List<Project> findByBudgetBetween(@Param("minBudget") java.math.BigDecimal minBudget, @Param("maxBudget") java.math.BigDecimal maxBudget);
}



