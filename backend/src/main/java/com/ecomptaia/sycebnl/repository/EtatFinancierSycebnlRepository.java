package com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.EtatFinancierSycebnl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les états financiers SYCEBNL
 */
@Repository
public interface EtatFinancierSycebnlRepository extends JpaRepository<EtatFinancierSycebnl, Long> {
    
    /**
     * Trouve tous les états financiers pour un exercice donné
     */
    List<EtatFinancierSycebnl> findByExerciceIdOrderByDateArreteDesc(Long exerciceId);
    
    /**
     * Trouve tous les états financiers pour une entité donnée
     */
    List<EtatFinancierSycebnl> findByEntiteIdOrderByDateArreteDesc(Long entiteId);
    
    /**
     * Trouve un état financier spécifique par exercice, type système et type d'état
     */
    Optional<EtatFinancierSycebnl> findByExerciceIdAndTypeSystemeAndTypeEtat(
            Long exerciceId, EtatFinancierSycebnl.TypeSysteme typeSysteme, 
            EtatFinancierSycebnl.TypeEtat typeEtat);
    
    /**
     * Trouve tous les états financiers pour une période donnée
     */
    @Query("SELECT e FROM EtatFinancierSycebnl e WHERE e.dateArrete BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY e.dateArrete DESC")
    List<EtatFinancierSycebnl> findByDateArreteBetween(
            @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve tous les états financiers par statut
     */
    List<EtatFinancierSycebnl> findByStatutOrderByDateArreteDesc(EtatFinancierSycebnl.StatutEtat statut);
    
    /**
     * Trouve tous les états financiers validés pour un exercice
     */
    @Query("SELECT e FROM EtatFinancierSycebnl e WHERE e.exercice.id = :exerciceId " +
           "AND e.statut = 'VALIDE' ORDER BY e.dateArrete DESC")
    List<EtatFinancierSycebnl> findEtatsValidesByExercice(@Param("exerciceId") Long exerciceId);
    
    /**
     * Trouve le dernier état financier généré pour un exercice et type donné
     */
    @Query("SELECT e FROM EtatFinancierSycebnl e WHERE e.exercice.id = :exerciceId " +
           "AND e.typeSysteme = :typeSysteme AND e.typeEtat = :typeEtat " +
           "ORDER BY e.dateCreation DESC LIMIT 1")
    Optional<EtatFinancierSycebnl> findDernierEtatParType(
            @Param("exerciceId") Long exerciceId,
            @Param("typeSysteme") EtatFinancierSycebnl.TypeSysteme typeSysteme,
            @Param("typeEtat") EtatFinancierSycebnl.TypeEtat typeEtat);
    
    /**
     * Compte le nombre d'états financiers par statut pour un exercice
     */
    @Query("SELECT COUNT(e) FROM EtatFinancierSycebnl e WHERE e.exercice.id = :exerciceId " +
           "AND e.statut = :statut")
    Long countByExerciceIdAndStatut(@Param("exerciceId") Long exerciceId, 
                                   @Param("statut") EtatFinancierSycebnl.StatutEtat statut);
}
