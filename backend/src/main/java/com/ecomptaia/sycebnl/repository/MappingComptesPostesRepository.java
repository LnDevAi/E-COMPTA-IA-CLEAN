ackage com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.MappingComptesPostes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour le mapping des comptes vers les postes des états financiers
 */
@Repository
public interface MappingComptesPostesRepository extends JpaRepository<MappingComptesPostes, Long> {
    
    /**
     * Trouve tous les mappings pour un pays, standard et système donnés
     */
    List<MappingComptesPostes> findByPaysCodeAndStandardComptableAndTypeSystemeAndActifTrue(
            String paysCode, String standardComptable, MappingComptesPostes.TypeSysteme typeSysteme);
    
    /**
     * Trouve tous les mappings pour un type d'état financier donné
     */
    List<MappingComptesPostes> findByPaysCodeAndStandardComptableAndTypeSystemeAndTypeEtatAndActifTrueOrderByOrdreAffichage(
            String paysCode, String standardComptable, MappingComptesPostes.TypeSysteme typeSysteme, 
            MappingComptesPostes.TypeEtat typeEtat);
    
    /**
     * Trouve un mapping spécifique par code de poste
     */
    MappingComptesPostes findByPaysCodeAndStandardComptableAndTypeSystemeAndTypeEtatAndPosteCodeAndActifTrue(
            String paysCode, String standardComptable, MappingComptesPostes.TypeSysteme typeSysteme,
            MappingComptesPostes.TypeEtat typeEtat, String posteCode);
    
    /**
     * Trouve tous les postes totaux pour un état financier
     */
    @Query("SELECT m FROM MappingComptesPostes m WHERE m.paysCode = :paysCode " +
           "AND m.standardComptable = :standardComptable " +
           "AND m.typeSysteme = :typeSysteme " +
           "AND m.typeEtat = :typeEtat " +
           "AND m.estTotal = true " +
           "AND m.actif = true " +
           "ORDER BY m.ordreAffichage")
    List<MappingComptesPostes> findPostesTotaux(
            @Param("paysCode") String paysCode,
            @Param("standardComptable") String standardComptable,
            @Param("typeSysteme") MappingComptesPostes.TypeSysteme typeSysteme,
            @Param("typeEtat") MappingComptesPostes.TypeEtat typeEtat);
    
    /**
     * Trouve tous les postes de détail (non totaux) pour un état financier
     */
    @Query("SELECT m FROM MappingComptesPostes m WHERE m.paysCode = :paysCode " +
           "AND m.standardComptable = :standardComptable " +
           "AND m.typeSysteme = :typeSysteme " +
           "AND m.typeEtat = :typeEtat " +
           "AND m.estTotal = false " +
           "AND m.actif = true " +
           "ORDER BY m.ordreAffichage")
    List<MappingComptesPostes> findPostesDetail(
            @Param("paysCode") String paysCode,
            @Param("standardComptable") String standardComptable,
            @Param("typeSysteme") MappingComptesPostes.TypeSysteme typeSysteme,
            @Param("typeEtat") MappingComptesPostes.TypeEtat typeEtat);
}
