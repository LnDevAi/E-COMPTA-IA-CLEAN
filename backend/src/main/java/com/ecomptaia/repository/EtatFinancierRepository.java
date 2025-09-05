package com.ecomptaia.repository;

import com.ecomptaia.entity.EtatFinancier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EtatFinancierRepository extends JpaRepository<EtatFinancier, Long> {

    // Recherche par exercice
    List<EtatFinancier> findByExerciceIdOrderByDateGenerationDesc(Long exerciceId);

    // Recherche par type d'état
    List<EtatFinancier> findByTypeEtatAndExerciceIdOrderByDateGenerationDesc(String typeEtat, Long exerciceId);

    // Recherche par standard comptable
    List<EtatFinancier> findByStandardComptableAndExerciceIdOrderByDateGenerationDesc(String standardComptable, Long exerciceId);

    // Recherche par statut
    List<EtatFinancier> findByStatutAndExerciceIdOrderByDateGenerationDesc(String statut, Long exerciceId);

    // Recherche par devise
    List<EtatFinancier> findByDeviseAndExerciceIdOrderByDateGenerationDesc(String devise, Long exerciceId);

    // Recherche par date de génération
    List<EtatFinancier> findByDateGenerationAndExerciceIdOrderByDateGenerationDesc(LocalDate dateGeneration, Long exerciceId);

    // Recherche par période de génération
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.dateGeneration BETWEEN :dateDebut AND :dateFin AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findByPeriodeGeneration(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin, @Param("exerciceId") Long exerciceId);

    // Recherche états financiers créés récemment
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.dateCreation >= :since ORDER BY ef.dateCreation DESC")
    List<EtatFinancier> findRecentlyCreated(@Param("since") java.time.LocalDateTime since);

    // Recherche états financiers modifiés récemment
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.dateModification >= :since ORDER BY ef.dateModification DESC")
    List<EtatFinancier> findRecentlyModified(@Param("since") java.time.LocalDateTime since);

    // Statistiques par type d'état
    @Query("SELECT ef.typeEtat, COUNT(ef) FROM EtatFinancier ef WHERE ef.exercice.id = :exerciceId GROUP BY ef.typeEtat")
    List<Object[]> getStatisticsByTypeEtat(@Param("exerciceId") Long exerciceId);

    // Statistiques par standard comptable
    @Query("SELECT ef.standardComptable, COUNT(ef) FROM EtatFinancier ef WHERE ef.exercice.id = :exerciceId GROUP BY ef.standardComptable")
    List<Object[]> getStatisticsByStandardComptable(@Param("exerciceId") Long exerciceId);

    // Statistiques par statut
    @Query("SELECT ef.statut, COUNT(ef) FROM EtatFinancier ef WHERE ef.exercice.id = :exerciceId GROUP BY ef.statut")
    List<Object[]> getStatisticsByStatut(@Param("exerciceId") Long exerciceId);

    // Statistiques par devise
    @Query("SELECT ef.devise, COUNT(ef) FROM EtatFinancier ef WHERE ef.exercice.id = :exerciceId GROUP BY ef.devise")
    List<Object[]> getStatisticsByDevise(@Param("exerciceId") Long exerciceId);

    // Compter les états financiers
    @Query("SELECT COUNT(ef) FROM EtatFinancier ef WHERE ef.exercice.id = :exerciceId")
    Long countEtatsFinanciers(@Param("exerciceId") Long exerciceId);

    // Compter les états financiers par type
    @Query("SELECT COUNT(ef) FROM EtatFinancier ef WHERE ef.typeEtat = :typeEtat AND ef.exercice.id = :exerciceId")
    Long countByTypeEtat(@Param("typeEtat") String typeEtat, @Param("exerciceId") Long exerciceId);

    // Compter les états financiers par standard comptable
    @Query("SELECT COUNT(ef) FROM EtatFinancier ef WHERE ef.standardComptable = :standardComptable AND ef.exercice.id = :exerciceId")
    Long countByStandardComptable(@Param("standardComptable") String standardComptable, @Param("exerciceId") Long exerciceId);

    // Recherche bilans
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.typeEtat = 'BILAN' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findBilans(@Param("exerciceId") Long exerciceId);

    // Recherche comptes de résultat
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.typeEtat = 'COMPTE_RESULTAT' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findComptesResultat(@Param("exerciceId") Long exerciceId);

    // Recherche flux de trésorerie
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.typeEtat = 'FLUX_TRESORERIE' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findFluxTresorerie(@Param("exerciceId") Long exerciceId);

    // Recherche annexes
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.typeEtat = 'ANNEXES' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findAnnexes(@Param("exerciceId") Long exerciceId);

    // Recherche états SYSCOHADA
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.standardComptable = 'SYSCOHADA' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findEtatsSYSCOHADA(@Param("exerciceId") Long exerciceId);

    // Recherche états IFRS
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.standardComptable = 'IFRS' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findEtatsIFRS(@Param("exerciceId") Long exerciceId);

    // Recherche états US GAAP
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.standardComptable = 'US_GAAP' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findEtatsUSGAAP(@Param("exerciceId") Long exerciceId);

    // Recherche états PCG
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.standardComptable = 'PCG' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findEtatsPCG(@Param("exerciceId") Long exerciceId);

    // Recherche états générés
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.statut = 'GENERATED' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findEtatsGeneres(@Param("exerciceId") Long exerciceId);

    // Recherche états validés
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.statut = 'VALIDATED' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findEtatsValides(@Param("exerciceId") Long exerciceId);

    // Recherche états publiés
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.statut = 'PUBLISHED' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findEtatsPublies(@Param("exerciceId") Long exerciceId);

    // Recherche états avec observations
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.observations IS NOT NULL AND ef.observations != '' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findWithObservations(@Param("exerciceId") Long exerciceId);

    // Recherche états avec données JSON
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.donneesJson IS NOT NULL AND ef.donneesJson != '' AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findWithDonneesJson(@Param("exerciceId") Long exerciceId);

    // Recherche le dernier état financier par type
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.typeEtat = :typeEtat AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findDernierEtatByType(@Param("typeEtat") String typeEtat, @Param("exerciceId") Long exerciceId);

    // Recherche le dernier état financier par standard comptable
    @Query("SELECT ef FROM EtatFinancier ef WHERE ef.standardComptable = :standardComptable AND ef.exercice.id = :exerciceId ORDER BY ef.dateGeneration DESC")
    List<EtatFinancier> findDernierEtatByStandard(@Param("standardComptable") String standardComptable, @Param("exerciceId") Long exerciceId);
}







