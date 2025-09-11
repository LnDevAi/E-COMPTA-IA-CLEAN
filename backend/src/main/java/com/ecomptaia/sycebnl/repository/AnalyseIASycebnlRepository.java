ackage com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.AnalyseIASycebnl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour les analyses IA SYCEBNL
 */
@Repository
public interface AnalyseIASycebnlRepository extends JpaRepository<AnalyseIASycebnl, Long> {
    
    /**
     * Trouve les analyses par pièce justificative
     */
    List<AnalyseIASycebnl> findByPieceJustificativeIdOrderByDateAnalyseDesc(Long pieceJustificativeId);
    
    /**
     * Trouve les analyses par statut
     */
    List<AnalyseIASycebnl> findByStatutAnalyseOrderByDateAnalyseDesc(AnalyseIASycebnl.StatutAnalyse statutAnalyse);
    
    /**
     * Trouve les analyses par modèle IA
     */
    List<AnalyseIASycebnl> findByModeleIAUtiliseOrderByDateAnalyseDesc(String modeleIAUtilise);
    
    /**
     * Trouve les analyses par type de document détecté
     */
    List<AnalyseIASycebnl> findByTypeDocumentDetecteOrderByDateAnalyseDesc(String typeDocumentDetecte);
    
    /**
     * Trouve les analyses avec faible confiance
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE a.confianceGlobale < :seuilConfiance ORDER BY a.confianceGlobale ASC")
    List<AnalyseIASycebnl> findByConfianceFaible(@Param("seuilConfiance") BigDecimal seuilConfiance);
    
    /**
     * Trouve les analyses par période
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE a.dateAnalyse BETWEEN :dateDebut AND :dateFin ORDER BY a.dateAnalyse DESC")
    List<AnalyseIASycebnl> findByPeriode(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    
    /**
     * Trouve les analyses par fournisseur détecté
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE LOWER(a.fournisseurDetecte) LIKE LOWER(CONCAT('%', :fournisseur, '%')) ORDER BY a.dateAnalyse DESC")
    List<AnalyseIASycebnl> findByFournisseurDetecte(@Param("fournisseur") String fournisseur);
    
    /**
     * Trouve les analyses par client détecté
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE LOWER(a.clientDetecte) LIKE LOWER(CONCAT('%', :client, '%')) ORDER BY a.dateAnalyse DESC")
    List<AnalyseIASycebnl> findByClientDetecte(@Param("client") String client);
    
    /**
     * Trouve les analyses par montant détecté
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE a.montantDetecte BETWEEN :montantMin AND :montantMax ORDER BY a.montantDetecte DESC")
    List<AnalyseIASycebnl> findByMontantDetecte(@Param("montantMin") BigDecimal montantMin, 
                                               @Param("montantMax") BigDecimal montantMax);
    
    /**
     * Trouve les analyses par devise détectée
     */
    List<AnalyseIASycebnl> findByDeviseDetecteeOrderByDateAnalyseDesc(String deviseDetectee);
    
    /**
     * Trouve les analyses avec erreurs
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE a.statutAnalyse = 'ERREUR' ORDER BY a.dateAnalyse DESC")
    List<AnalyseIASycebnl> findAnalysesAvecErreurs();
    
    /**
     * Trouve la dernière analyse pour une PJ
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE a.pieceJustificative.id = :pieceJustificativeId ORDER BY a.dateAnalyse DESC")
    List<AnalyseIASycebnl> findDerniereAnalyseParPJ(@Param("pieceJustificativeId") Long pieceJustificativeId);
    
    /**
     * Compte les analyses par statut
     */
    @Query("SELECT a.statutAnalyse, COUNT(a) FROM AnalyseIASycebnl a GROUP BY a.statutAnalyse")
    List<Object[]> countByStatutAnalyse();
    
    /**
     * Compte les analyses par modèle IA
     */
    @Query("SELECT a.modeleIAUtilise, COUNT(a) FROM AnalyseIASycebnl a GROUP BY a.modeleIAUtilise")
    List<Object[]> countByModeleIA();
    
    /**
     * Compte les analyses par type de document
     */
    @Query("SELECT a.typeDocumentDetecte, COUNT(a) FROM AnalyseIASycebnl a GROUP BY a.typeDocumentDetecte")
    List<Object[]> countByTypeDocument();
    
    /**
     * Trouve les analyses récentes
     */
    @Query("SELECT a FROM AnalyseIASycebnl a ORDER BY a.dateAnalyse DESC")
    List<AnalyseIASycebnl> findAnalysesRecentes();
    
    /**
     * Trouve les analyses par temps de traitement
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE a.tempsTraitementMs > :tempsMin ORDER BY a.tempsTraitementMs DESC")
    List<AnalyseIASycebnl> findByTempsTraitementEleve(@Param("tempsMin") Long tempsMin);
    
    /**
     * Trouve les analyses avec TVA détectée
     */
    @Query("SELECT a FROM AnalyseIASycebnl a WHERE a.tvaDetectee IS NOT NULL AND a.tvaDetectee > 0 ORDER BY a.tvaDetectee DESC")
    List<AnalyseIASycebnl> findAnalysesAvecTVA();
}
