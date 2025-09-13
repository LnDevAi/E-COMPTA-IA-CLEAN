package com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.PlanComptableSycebnl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour le plan comptable SYCEBNL
 */
@Repository
public interface PlanComptableSycebnlRepository extends JpaRepository<PlanComptableSycebnl, Long> {
    
    /**
     * Trouve un compte par son numéro
     */
    Optional<PlanComptableSycebnl> findByNumeroCompteAndActifTrue(String numeroCompte);
    
    /**
     * Trouve tous les comptes d'une classe
     */
    List<PlanComptableSycebnl> findByClasseCompteAndActifTrueOrderByNumeroCompte(PlanComptableSycebnl.ClasseCompte classeCompte);
    
    /**
     * Trouve tous les comptes d'un niveau
     */
    List<PlanComptableSycebnl> findByNiveauAndActifTrueOrderByNumeroCompte(Integer niveau);
    
    /**
     * Trouve tous les comptes enfants d'un compte parent
     */
    List<PlanComptableSycebnl> findByCompteParentAndActifTrueOrderByNumeroCompte(String compteParent);
    
    /**
     * Trouve tous les comptes utilisables pour le système normal
     */
    List<PlanComptableSycebnl> findByUtiliseSystemeNormalTrueAndActifTrueOrderByNumeroCompte();
    
    /**
     * Trouve tous les comptes utilisables pour le SMT
     */
    List<PlanComptableSycebnl> findByUtiliseSMTTrueAndActifTrueOrderByNumeroCompte();
    
    /**
     * Trouve tous les comptes obligatoires pour les ONG
     */
    List<PlanComptableSycebnl> findByObligatoireONGTrueAndActifTrueOrderByNumeroCompte();
    
    /**
     * Trouve les comptes par type
     */
    List<PlanComptableSycebnl> findByTypeCompteAndActifTrueOrderByNumeroCompte(PlanComptableSycebnl.TypeCompte typeCompte);
    
    /**
     * Trouve les comptes par sens normal
     */
    List<PlanComptableSycebnl> findBySensNormalAndActifTrueOrderByNumeroCompte(PlanComptableSycebnl.SensNormalCompte sensNormal);
    
    /**
     * Recherche de comptes par libellé (recherche partielle)
     */
    @Query("SELECT p FROM PlanComptableSycebnl p WHERE p.intituleCompte LIKE %:libelle% AND p.actif = true ORDER BY p.numeroCompte")
    List<PlanComptableSycebnl> findByIntituleCompteContainingAndActifTrue(@Param("libelle") String libelle);
    
    /**
     * Trouve les comptes correspondant à un pattern
     */
    @Query("SELECT p FROM PlanComptableSycebnl p WHERE p.numeroCompte LIKE :pattern AND p.actif = true ORDER BY p.numeroCompte")
    List<PlanComptableSycebnl> findByNumeroCompteLikeAndActifTrue(@Param("pattern") String pattern);
    
    /**
     * Trouve les comptes de trésorerie (classe 5)
     */
    @Query("SELECT p FROM PlanComptableSycebnl p WHERE p.numeroCompte LIKE '5%' AND p.actif = true ORDER BY p.numeroCompte")
    List<PlanComptableSycebnl> findComptesTresorerie();
    
    /**
     * Trouve les comptes de charges (classe 6)
     */
    @Query("SELECT p FROM PlanComptableSycebnl p WHERE p.numeroCompte LIKE '6%' AND p.actif = true ORDER BY p.numeroCompte")
    List<PlanComptableSycebnl> findComptesCharges();
    
    /**
     * Trouve les comptes de produits (classe 7)
     */
    @Query("SELECT p FROM PlanComptableSycebnl p WHERE p.numeroCompte LIKE '7%' AND p.actif = true ORDER BY p.numeroCompte")
    List<PlanComptableSycebnl> findComptesProduits();
    
    /**
     * Trouve les comptes spécifiques aux ONG
     */
    @Query("SELECT p FROM PlanComptableSycebnl p WHERE (p.numeroCompte LIKE '101%' OR p.numeroCompte LIKE '104%' OR p.numeroCompte LIKE '105%' OR " +
           "p.numeroCompte LIKE '215%' OR p.numeroCompte LIKE '218%' OR p.numeroCompte LIKE '740%' OR " +
           "p.numeroCompte LIKE '750%' OR p.numeroCompte LIKE '755%') AND p.actif = true ORDER BY p.numeroCompte")
    List<PlanComptableSycebnl> findComptesSpecifiquesONG();
    
    /**
     * Compte le nombre de comptes par classe
     */
    @Query("SELECT p.classeCompte, COUNT(p) FROM PlanComptableSycebnl p WHERE p.actif = true GROUP BY p.classeCompte")
    List<Object[]> countByClasseCompte();
    
    /**
     * Trouve la hiérarchie complète d'un compte
     */
    @Query("SELECT p FROM PlanComptableSycebnl p WHERE p.numeroCompte = :numeroCompte OR p.numeroCompte LIKE CONCAT(:numeroCompte, '%') AND p.actif = true ORDER BY p.numeroCompte")
    List<PlanComptableSycebnl> findHierarchieCompte(@Param("numeroCompte") String numeroCompte);
}
