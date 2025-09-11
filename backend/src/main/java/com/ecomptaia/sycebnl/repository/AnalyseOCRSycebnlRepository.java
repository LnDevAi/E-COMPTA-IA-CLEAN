ackage com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.AnalyseOCRSycebnl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour les analyses OCR SYCEBNL
 */
@Repository
public interface AnalyseOCRSycebnlRepository extends JpaRepository<AnalyseOCRSycebnl, Long> {
    
    /**
     * Trouve les analyses par pièce justificative
     */
    List<AnalyseOCRSycebnl> findByPieceJustificativeIdOrderByDateAnalyseDesc(Long pieceJustificativeId);
    
    /**
     * Trouve les analyses par statut
     */
    List<AnalyseOCRSycebnl> findByStatutAnalyseOrderByDateAnalyseDesc(AnalyseOCRSycebnl.StatutAnalyse statutAnalyse);
    
    /**
     * Trouve les analyses par moteur OCR
     */
    List<AnalyseOCRSycebnl> findByMoteurOCRUtiliseOrderByDateAnalyseDesc(String moteurOCRUtilise);
    
    /**
     * Trouve les analyses avec faible confiance
     */
    @Query("SELECT a FROM AnalyseOCRSycebnl a WHERE a.confianceGlobale < :seuilConfiance ORDER BY a.confianceGlobale ASC")
    List<AnalyseOCRSycebnl> findByConfianceFaible(@Param("seuilConfiance") BigDecimal seuilConfiance);
    
    /**
     * Trouve les analyses par période
     */
    @Query("SELECT a FROM AnalyseOCRSycebnl a WHERE a.dateAnalyse BETWEEN :dateDebut AND :dateFin ORDER BY a.dateAnalyse DESC")
    List<AnalyseOCRSycebnl> findByPeriode(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    
    /**
     * Trouve les analyses par langue détectée
     */
    List<AnalyseOCRSycebnl> findByLangueDetecteeOrderByDateAnalyseDesc(String langueDetectee);
    
    /**
     * Trouve les analyses avec erreurs
     */
    @Query("SELECT a FROM AnalyseOCRSycebnl a WHERE a.statutAnalyse = 'ERREUR' ORDER BY a.dateAnalyse DESC")
    List<AnalyseOCRSycebnl> findAnalysesAvecErreurs();
    
    /**
     * Trouve la dernière analyse pour une PJ
     */
    @Query("SELECT a FROM AnalyseOCRSycebnl a WHERE a.pieceJustificative.id = :pieceJustificativeId ORDER BY a.dateAnalyse DESC")
    List<AnalyseOCRSycebnl> findDerniereAnalyseParPJ(@Param("pieceJustificativeId") Long pieceJustificativeId);
    
    /**
     * Compte les analyses par statut
     */
    @Query("SELECT a.statutAnalyse, COUNT(a) FROM AnalyseOCRSycebnl a GROUP BY a.statutAnalyse")
    List<Object[]> countByStatutAnalyse();
    
    /**
     * Compte les analyses par moteur OCR
     */
    @Query("SELECT a.moteurOCRUtilise, COUNT(a) FROM AnalyseOCRSycebnl a GROUP BY a.moteurOCRUtilise")
    List<Object[]> countByMoteurOCR();
    
    /**
     * Trouve les analyses récentes
     */
    @Query("SELECT a FROM AnalyseOCRSycebnl a ORDER BY a.dateAnalyse DESC")
    List<AnalyseOCRSycebnl> findAnalysesRecentes();
    
    /**
     * Trouve les analyses par temps de traitement
     */
    @Query("SELECT a FROM AnalyseOCRSycebnl a WHERE a.tempsTraitementMs > :tempsMin ORDER BY a.tempsTraitementMs DESC")
    List<AnalyseOCRSycebnl> findByTempsTraitementEleve(@Param("tempsMin") Long tempsMin);
}
