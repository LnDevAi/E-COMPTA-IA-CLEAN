package com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.PropositionEcritureSycebnl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les propositions d'écritures SYCEBNL
 */
@Repository
public interface PropositionEcritureSycebnlRepository extends JpaRepository<PropositionEcritureSycebnl, Long> {
    
    /**
     * Trouve une proposition par son numéro
     */
    Optional<PropositionEcritureSycebnl> findByNumeroProposition(String numeroProposition);
    
    /**
     * Trouve les propositions par pièce justificative
     */
    List<PropositionEcritureSycebnl> findByPieceJustificativeIdOrderByDateCreationDesc(Long pieceJustificativeId);
    
    /**
     * Trouve les propositions par statut
     */
    List<PropositionEcritureSycebnl> findByStatutPropositionOrderByDateCreationDesc(
        PropositionEcritureSycebnl.StatutProposition statutProposition);
    
    /**
     * Trouve les propositions par type d'écriture
     */
    List<PropositionEcritureSycebnl> findByTypeEcritureOrderByDateCreationDesc(
        PropositionEcritureSycebnl.TypeEcritureProposition typeEcriture);
    
    /**
     * Trouve les propositions par créateur
     */
    List<PropositionEcritureSycebnl> findByCreeParOrderByDateCreationDesc(Long creePar);
    
    /**
     * Trouve les propositions par validateur
     */
    List<PropositionEcritureSycebnl> findByValideParOrderByDateValidationDesc(Long validePar);
    
    /**
     * Trouve les propositions par période
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE p.dateProposition BETWEEN :dateDebut AND :dateFin ORDER BY p.dateProposition DESC")
    List<PropositionEcritureSycebnl> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve les propositions en attente de validation
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE p.statutProposition = 'PROPOSEE' ORDER BY p.dateCreation ASC")
    List<PropositionEcritureSycebnl> findPropositionsEnAttenteValidation();
    
    /**
     * Trouve les propositions validées
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE p.statutProposition = 'VALIDEE' ORDER BY p.dateValidation DESC")
    List<PropositionEcritureSycebnl> findPropositionsValidees();
    
    /**
     * Trouve les propositions rejetées
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE p.statutProposition = 'REJETEE' ORDER BY p.dateValidation DESC")
    List<PropositionEcritureSycebnl> findPropositionsRejetees();
    
    /**
     * Trouve les propositions par montant
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE p.montantTotal BETWEEN :montantMin AND :montantMax ORDER BY p.montantTotal DESC")
    List<PropositionEcritureSycebnl> findByMontant(@Param("montantMin") BigDecimal montantMin, 
                                                   @Param("montantMax") BigDecimal montantMax);
    
    /**
     * Trouve les propositions par devise
     */
    List<PropositionEcritureSycebnl> findByDeviseOrderByDateCreationDesc(String devise);
    
    /**
     * Trouve les propositions avec faible confiance
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE p.confianceProposition < :seuilConfiance ORDER BY p.confianceProposition ASC")
    List<PropositionEcritureSycebnl> findByConfianceFaible(@Param("seuilConfiance") BigDecimal seuilConfiance);
    
    /**
     * Trouve les propositions récentes
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p ORDER BY p.dateCreation DESC")
    List<PropositionEcritureSycebnl> findPropositionsRecentes();
    
    /**
     * Compte les propositions par statut
     */
    @Query("SELECT p.statutProposition, COUNT(p) FROM PropositionEcritureSycebnl p GROUP BY p.statutProposition")
    List<Object[]> countByStatutProposition();
    
    /**
     * Compte les propositions par type d'écriture
     */
    @Query("SELECT p.typeEcriture, COUNT(p) FROM PropositionEcritureSycebnl p GROUP BY p.typeEcriture")
    List<Object[]> countByTypeEcriture();
    
    /**
     * Trouve les propositions par texte (recherche dans libellé, commentaires)
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE " +
           "(LOWER(p.libelleProposition) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(p.commentairesProposition) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(p.numeroProposition) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY p.dateCreation DESC")
    List<PropositionEcritureSycebnl> findByTexte(@Param("texte") String texte);
    
    /**
     * Trouve les propositions non générées en écritures
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE p.statutProposition = 'VALIDEE' AND p.statutProposition != 'GENEREES' ORDER BY p.dateValidation ASC")
    List<PropositionEcritureSycebnl> findPropositionsNonGenerees();
    
    /**
     * Trouve les propositions générées en écritures
     */
    @Query("SELECT p FROM PropositionEcritureSycebnl p WHERE p.statutProposition = 'GENEREES' ORDER BY p.dateValidation DESC")
    List<PropositionEcritureSycebnl> findPropositionsGenerees();
}
