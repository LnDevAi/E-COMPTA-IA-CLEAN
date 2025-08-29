package com.ecomptaia.repository;

import com.ecomptaia.entity.ExerciceSMT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciceSMTRepository extends JpaRepository<ExerciceSMT, Long> {

    // Recherche par entreprise
    List<ExerciceSMT> findByEntrepriseIdOrderByAnneeExerciceDesc(Long entrepriseId);

    // Recherche par année d'exercice
    List<ExerciceSMT> findByAnneeExerciceAndEntrepriseId(Integer anneeExercice, Long entrepriseId);

    // Recherche exercice en cours
    @Query("SELECT e FROM ExerciceSMT e WHERE e.entreprise.id = :entrepriseId AND e.estCloture = false AND e.dateDebut <= CURRENT_DATE AND e.dateFin >= CURRENT_DATE")
    Optional<ExerciceSMT> findExerciceEnCours(@Param("entrepriseId") Long entrepriseId);

    // Recherche exercices ouverts
    List<ExerciceSMT> findByEstClotureFalseAndEntrepriseIdOrderByAnneeExerciceDesc(Long entrepriseId);

    // Recherche exercices clôturés
    List<ExerciceSMT> findByEstClotureTrueAndEntrepriseIdOrderByAnneeExerciceDesc(Long entrepriseId);

    // Recherche par statut
    List<ExerciceSMT> findByStatutAndEntrepriseIdOrderByAnneeExerciceDesc(String statut, Long entrepriseId);

    // Recherche par devise
    List<ExerciceSMT> findByDeviseAndEntrepriseIdOrderByAnneeExerciceDesc(String devise, Long entrepriseId);

    // Recherche exercices créés récemment
    @Query("SELECT e FROM ExerciceSMT e WHERE e.dateCreation >= :since ORDER BY e.dateCreation DESC")
    List<ExerciceSMT> findRecentlyCreated(@Param("since") java.time.LocalDateTime since);

    // Recherche exercices par période
    @Query("SELECT e FROM ExerciceSMT e WHERE e.dateDebut >= :dateDebut AND e.dateFin <= :dateFin AND e.entreprise.id = :entrepriseId ORDER BY e.anneeExercice DESC")
    List<ExerciceSMT> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin, @Param("entrepriseId") Long entrepriseId);

    // Recherche exercices qui peuvent être clôturés
    @Query("SELECT e FROM ExerciceSMT e WHERE e.estCloture = false AND e.dateFin < CURRENT_DATE AND e.entreprise.id = :entrepriseId ORDER BY e.dateFin DESC")
    List<ExerciceSMT> findExercicesCloturables(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par statut
    @Query("SELECT e.statut, COUNT(e) FROM ExerciceSMT e WHERE e.entreprise.id = :entrepriseId GROUP BY e.statut")
    List<Object[]> getStatisticsByStatut(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par devise
    @Query("SELECT e.devise, COUNT(e) FROM ExerciceSMT e WHERE e.entreprise.id = :entrepriseId GROUP BY e.devise")
    List<Object[]> getStatisticsByDevise(@Param("entrepriseId") Long entrepriseId);

    // Compter les exercices actifs
    @Query("SELECT COUNT(e) FROM ExerciceSMT e WHERE e.estCloture = false AND e.entreprise.id = :entrepriseId")
    Long countExercicesActifs(@Param("entrepriseId") Long entrepriseId);

    // Compter les exercices clôturés
    @Query("SELECT COUNT(e) FROM ExerciceSMT e WHERE e.estCloture = true AND e.entreprise.id = :entrepriseId")
    Long countExercicesClotures(@Param("entrepriseId") Long entrepriseId);

    // Recherche exercices avec chiffre d'affaires supérieur
    @Query("SELECT e FROM ExerciceSMT e WHERE e.chiffreAffairesTotal >= :seuil AND e.entreprise.id = :entrepriseId ORDER BY e.chiffreAffairesTotal DESC")
    List<ExerciceSMT> findByChiffreAffairesGreaterThan(@Param("seuil") java.math.BigDecimal seuil, @Param("entrepriseId") Long entrepriseId);

    // Recherche exercices avec résultat net positif
    @Query("SELECT e FROM ExerciceSMT e WHERE (e.totalRecettes - e.totalDepenses) > 0 AND e.entreprise.id = :entrepriseId ORDER BY (e.totalRecettes - e.totalDepenses) DESC")
    List<ExerciceSMT> findWithResultatPositif(@Param("entrepriseId") Long entrepriseId);

    // Recherche exercices avec résultat net négatif
    @Query("SELECT e FROM ExerciceSMT e WHERE (e.totalRecettes - e.totalDepenses) < 0 AND e.entreprise.id = :entrepriseId ORDER BY (e.totalRecettes - e.totalDepenses) ASC")
    List<ExerciceSMT> findWithResultatNegatif(@Param("entrepriseId") Long entrepriseId);

    // Recherche exercices par plage de dates
    @Query("SELECT e FROM ExerciceSMT e WHERE e.dateDebut BETWEEN :dateDebut AND :dateFin AND e.entreprise.id = :entrepriseId ORDER BY e.dateDebut")
    List<ExerciceSMT> findByDateDebutBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin, @Param("entrepriseId") Long entrepriseId);

    // Recherche exercices par plage de dates de fin
    @Query("SELECT e FROM ExerciceSMT e WHERE e.dateFin BETWEEN :dateDebut AND :dateFin AND e.entreprise.id = :entrepriseId ORDER BY e.dateFin")
    List<ExerciceSMT> findByDateFinBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin, @Param("entrepriseId") Long entrepriseId);

    // Recherche exercices avec observations
    @Query("SELECT e FROM ExerciceSMT e WHERE e.observations IS NOT NULL AND e.observations != '' AND e.entreprise.id = :entrepriseId ORDER BY e.dateCreation DESC")
    List<ExerciceSMT> findWithObservations(@Param("entrepriseId") Long entrepriseId);

    // Recherche exercices par année
    @Query("SELECT e FROM ExerciceSMT e WHERE e.anneeExercice = :annee AND e.entreprise.id = :entrepriseId ORDER BY e.dateDebut")
    List<ExerciceSMT> findByAnnee(@Param("annee") Integer annee, @Param("entrepriseId") Long entrepriseId);

    // Recherche exercices par plage d'années
    @Query("SELECT e FROM ExerciceSMT e WHERE e.anneeExercice BETWEEN :anneeDebut AND :anneeFin AND e.entreprise.id = :entrepriseId ORDER BY e.anneeExercice DESC")
    List<ExerciceSMT> findByAnneeBetween(@Param("anneeDebut") Integer anneeDebut, @Param("anneeFin") Integer anneeFin, @Param("entrepriseId") Long entrepriseId);
}
