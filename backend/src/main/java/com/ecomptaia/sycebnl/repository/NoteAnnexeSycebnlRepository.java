package com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.NoteAnnexeSycebnl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les notes annexes des états financiers SYCEBNL
 */
@Repository
public interface NoteAnnexeSycebnlRepository extends JpaRepository<NoteAnnexeSycebnl, Long> {
    
    /**
     * Trouve toutes les notes annexes pour un état financier donné
     */
    List<NoteAnnexeSycebnl> findByEtatFinancierIdOrderByOrdreAffichage(Long etatFinancierId);
    
    /**
     * Trouve toutes les notes annexes pour un état financier et un type donné
     */
    List<NoteAnnexeSycebnl> findByEtatFinancierIdAndTypeNoteOrderByOrdreAffichage(
            Long etatFinancierId, NoteAnnexeSycebnl.TypeNote typeNote);
    
    /**
     * Trouve une note annexe spécifique par numéro
     */
    NoteAnnexeSycebnl findByEtatFinancierIdAndNumeroNote(Long etatFinancierId, String numeroNote);
    
    /**
     * Trouve toutes les notes annexes par type
     */
    List<NoteAnnexeSycebnl> findByTypeNoteOrderByOrdreAffichage(NoteAnnexeSycebnl.TypeNote typeNote);
    
    /**
     * Trouve toutes les notes annexes pour un exercice donné
     */
    @Query("SELECT n FROM NoteAnnexeSycebnl n WHERE n.etatFinancier.exercice.id = :exerciceId " +
           "ORDER BY n.etatFinancier.typeSysteme, n.etatFinancier.typeEtat, n.ordreAffichage")
    List<NoteAnnexeSycebnl> findByExerciceId(@Param("exerciceId") Long exerciceId);
    
    /**
     * Trouve toutes les notes annexes pour un exercice et un type de système donnés
     */
    @Query("SELECT n FROM NoteAnnexeSycebnl n WHERE n.etatFinancier.exercice.id = :exerciceId " +
           "AND n.etatFinancier.typeSysteme = :typeSysteme " +
           "ORDER BY n.ordreAffichage")
    List<NoteAnnexeSycebnl> findByExerciceIdAndTypeSysteme(
            @Param("exerciceId") Long exerciceId, 
            @Param("typeSysteme") String typeSysteme);
    
    /**
     * Supprime toutes les notes annexes pour un état financier donné
     */
    void deleteByEtatFinancierId(Long etatFinancierId);
}
