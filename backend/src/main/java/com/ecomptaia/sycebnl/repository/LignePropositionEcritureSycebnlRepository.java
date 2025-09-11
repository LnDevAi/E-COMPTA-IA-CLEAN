ackage com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.LignePropositionEcritureSycebnl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository pour les lignes de propositions d'écritures SYCEBNL
 */
@Repository
public interface LignePropositionEcritureSycebnlRepository extends JpaRepository<LignePropositionEcritureSycebnl, Long> {
    
    /**
     * Trouve les lignes par proposition
     */
    List<LignePropositionEcritureSycebnl> findByPropositionIdOrderByOrdreAsc(Long propositionId);
    
    /**
     * Trouve les lignes par compte
     */
    List<LignePropositionEcritureSycebnl> findByCompteIdOrderByOrdreAsc(Long compteId);
    
    /**
     * Trouve les lignes par numéro de compte
     */
    List<LignePropositionEcritureSycebnl> findByNumeroCompteOrderByOrdreAsc(String numeroCompte);
    
    /**
     * Trouve les lignes en débit
     */
    @Query("SELECT l FROM LignePropositionEcritureSycebnl l WHERE l.debit IS NOT NULL AND l.debit > 0 ORDER BY l.debit DESC")
    List<LignePropositionEcritureSycebnl> findLignesEnDebit();
    
    /**
     * Trouve les lignes en crédit
     */
    @Query("SELECT l FROM LignePropositionEcritureSycebnl l WHERE l.credit IS NOT NULL AND l.credit > 0 ORDER BY l.credit DESC")
    List<LignePropositionEcritureSycebnl> findLignesEnCredit();
    
    /**
     * Trouve les lignes par montant
     */
    @Query("SELECT l FROM LignePropositionEcritureSycebnl l WHERE " +
           "(l.debit BETWEEN :montantMin AND :montantMax) OR " +
           "(l.credit BETWEEN :montantMin AND :montantMax) " +
           "ORDER BY l.ordre ASC")
    List<LignePropositionEcritureSycebnl> findByMontant(@Param("montantMin") BigDecimal montantMin, 
                                                        @Param("montantMax") BigDecimal montantMax);
    
    /**
     * Trouve les lignes avec faible confiance
     */
    @Query("SELECT l FROM LignePropositionEcritureSycebnl l WHERE l.confianceLigne < :seuilConfiance ORDER BY l.confianceLigne ASC")
    List<LignePropositionEcritureSycebnl> findByConfianceFaible(@Param("seuilConfiance") BigDecimal seuilConfiance);
    
    /**
     * Trouve les lignes par règle appliquée
     */
    List<LignePropositionEcritureSycebnl> findByRegleAppliqueeOrderByOrdreAsc(String regleAppliquee);
    
    /**
     * Trouve les lignes par sens normal
     */
    List<LignePropositionEcritureSycebnl> findBySensNormalOrderByOrdreAsc(String sensNormal);
    
    /**
     * Compte les lignes par proposition
     */
    @Query("SELECT COUNT(l) FROM LignePropositionEcritureSycebnl l WHERE l.proposition.id = :propositionId")
    Long countByPropositionId(@Param("propositionId") Long propositionId);
    
    /**
     * Somme des débits par proposition
     */
    @Query("SELECT SUM(l.debit) FROM LignePropositionEcritureSycebnl l WHERE l.proposition.id = :propositionId")
    BigDecimal sumDebitByPropositionId(@Param("propositionId") Long propositionId);
    
    /**
     * Somme des crédits par proposition
     */
    @Query("SELECT SUM(l.credit) FROM LignePropositionEcritureSycebnl l WHERE l.proposition.id = :propositionId")
    BigDecimal sumCreditByPropositionId(@Param("propositionId") Long propositionId);
    
    /**
     * Trouve les lignes par texte (recherche dans libellé, justification)
     */
    @Query("SELECT l FROM LignePropositionEcritureSycebnl l WHERE " +
           "(LOWER(l.libelleLigne) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(l.justificationLigne) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(l.libelleCompte) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY l.ordre ASC")
    List<LignePropositionEcritureSycebnl> findByTexte(@Param("texte") String texte);
    
    /**
     * Trouve les lignes par compte et proposition
     */
    @Query("SELECT l FROM LignePropositionEcritureSycebnl l WHERE l.proposition.id = :propositionId AND l.compte.id = :compteId ORDER BY l.ordre ASC")
    List<LignePropositionEcritureSycebnl> findByPropositionAndCompte(@Param("propositionId") Long propositionId, 
                                                                     @Param("compteId") Long compteId);
    
    /**
     * Trouve les lignes avec montants élevés
     */
    @Query("SELECT l FROM LignePropositionEcritureSycebnl l WHERE " +
           "(l.debit > :seuil) OR (l.credit > :seuil) " +
           "ORDER BY l.ordre ASC")
    List<LignePropositionEcritureSycebnl> findLignesMontantsEleves(@Param("seuil") BigDecimal seuil);
}
