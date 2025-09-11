ackage com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.PieceJustificativeSycebnl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les pièces justificatives SYCEBNL
 */
@Repository
public interface PieceJustificativeSycebnlRepository extends JpaRepository<PieceJustificativeSycebnl, Long> {
    
    /**
     * Trouve une PJ par son numéro
     */
    Optional<PieceJustificativeSycebnl> findByNumeroPJ(String numeroPJ);
    
    /**
     * Trouve les PJ par entreprise
     */
    List<PieceJustificativeSycebnl> findByEntrepriseIdOrderByDateCreationDesc(Long entrepriseId);
    
    /**
     * Trouve les PJ par statut de traitement
     */
    List<PieceJustificativeSycebnl> findByStatutTraitementOrderByDateCreationDesc(
        PieceJustificativeSycebnl.StatutTraitement statutTraitement);
    
    /**
     * Trouve les PJ par type
     */
    List<PieceJustificativeSycebnl> findByTypePJOrderByDateCreationDesc(
        PieceJustificativeSycebnl.TypePieceJustificative typePJ);
    
    /**
     * Trouve les PJ par utilisateur
     */
    List<PieceJustificativeSycebnl> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId);
    
    /**
     * Trouve les PJ par période
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.datePiece BETWEEN :dateDebut AND :dateFin ORDER BY p.datePiece DESC")
    List<PieceJustificativeSycebnl> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    /**
     * Trouve les PJ en attente d'analyse OCR
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.statutTraitement = 'TELECHARGEE' ORDER BY p.dateCreation ASC")
    List<PieceJustificativeSycebnl> findPJEnAttenteAnalyseOCR();
    
    /**
     * Trouve les PJ en attente d'analyse IA
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.statutTraitement = 'ANALYSE_OCR_TERMINEE' ORDER BY p.dateAnalyseOCR ASC")
    List<PieceJustificativeSycebnl> findPJEnAttenteAnalyseIA();
    
    /**
     * Trouve les PJ en attente de validation
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.statutTraitement = 'PROPOSITIONS_GENEREES' ORDER BY p.dateProposition ASC")
    List<PieceJustificativeSycebnl> findPJEnAttenteValidation();
    
    /**
     * Trouve les PJ par montant
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.montantTotal BETWEEN :montantMin AND :montantMax ORDER BY p.montantTotal DESC")
    List<PieceJustificativeSycebnl> findByMontant(@Param("montantMin") java.math.BigDecimal montantMin, 
                                                  @Param("montantMax") java.math.BigDecimal montantMax);
    
    /**
     * Trouve les PJ par devise
     */
    List<PieceJustificativeSycebnl> findByDeviseOrderByDateCreationDesc(String devise);
    
    /**
     * Trouve les PJ avec faible confiance OCR
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.confianceOCR < :seuilConfiance ORDER BY p.confianceOCR ASC")
    List<PieceJustificativeSycebnl> findPJAvecFaibleConfianceOCR(@Param("seuilConfiance") java.math.BigDecimal seuilConfiance);
    
    /**
     * Trouve les PJ avec faible confiance IA
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.confianceIA < :seuilConfiance ORDER BY p.confianceIA ASC")
    List<PieceJustificativeSycebnl> findPJAvecFaibleConfianceIA(@Param("seuilConfiance") java.math.BigDecimal seuilConfiance);
    
    /**
     * Compte les PJ par statut
     */
    @Query("SELECT p.statutTraitement, COUNT(p) FROM PieceJustificativeSycebnl p GROUP BY p.statutTraitement")
    List<Object[]> countByStatutTraitement();
    
    /**
     * Compte les PJ par type
     */
    @Query("SELECT p.typePJ, COUNT(p) FROM PieceJustificativeSycebnl p GROUP BY p.typePJ")
    List<Object[]> countByTypePJ();
    
    /**
     * Trouve les PJ récentes
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.entreprise.id = :entrepriseId ORDER BY p.dateCreation DESC")
    List<PieceJustificativeSycebnl> findPJRecentes(@Param("entrepriseId") Long entrepriseId);
    
    /**
     * Trouve les PJ par texte (recherche dans libellé, tags, etc.)
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE " +
           "p.entreprise.id = :entrepriseId AND " +
           "(LOWER(p.libellePJ) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(p.numeroPJ) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY p.dateCreation DESC")
    List<PieceJustificativeSycebnl> findByTexte(@Param("entrepriseId") Long entrepriseId, @Param("texte") String texte);
    
    /**
     * Trouve les PJ avec écritures générées
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.ecritureGeneree IS NOT NULL ORDER BY p.dateGenerationEcriture DESC")
    List<PieceJustificativeSycebnl> findPJAvecEcrituresGenerees();
    
    /**
     * Trouve les PJ sans écritures générées
     */
    @Query("SELECT p FROM PieceJustificativeSycebnl p WHERE p.ecritureGeneree IS NULL AND p.statutTraitement = 'VALIDEE' ORDER BY p.dateValidation ASC")
    List<PieceJustificativeSycebnl> findPJSansEcrituresGenerees();
}
