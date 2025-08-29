package com.ecomptaia.repository;

import com.ecomptaia.entity.LivreRecette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LivreRecetteRepository extends JpaRepository<LivreRecette, Long> {

    // Recherche par exercice
    List<LivreRecette> findByExerciceIdOrderByDateRecetteDesc(Long exerciceId);

    // Recherche par compte de trésorerie
    List<LivreRecette> findByCompteTresorerieIdOrderByDateRecetteDesc(Long compteTresorerieId);

    // Recherche par type de recette
    List<LivreRecette> findByTypeRecetteAndExerciceIdOrderByDateRecetteDesc(String typeRecette, Long exerciceId);

    // Recherche par mode de paiement
    List<LivreRecette> findByModePaiementAndExerciceIdOrderByDateRecetteDesc(String modePaiement, Long exerciceId);

    // Recherche par devise
    List<LivreRecette> findByDeviseAndExerciceIdOrderByDateRecetteDesc(String devise, Long exerciceId);

    // Recherche par statut
    List<LivreRecette> findByStatutAndExerciceIdOrderByDateRecetteDesc(String statut, Long exerciceId);

    // Recherche par date de recette
    List<LivreRecette> findByDateRecetteAndExerciceIdOrderByDateRecetteDesc(LocalDate dateRecette, Long exerciceId);

    // Recherche par période
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.dateRecette BETWEEN :dateDebut AND :dateFin AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin, @Param("exerciceId") Long exerciceId);

    // Recherche par libellé (contient)
    List<LivreRecette> findByLibelleContainingIgnoreCaseAndExerciceIdOrderByDateRecetteDesc(String libelle, Long exerciceId);

    // Recherche par tiers (contient)
    List<LivreRecette> findByTiersContainingIgnoreCaseAndExerciceIdOrderByDateRecetteDesc(String tiers, Long exerciceId);

    // Recherche par numéro de pièce
    List<LivreRecette> findByNumeroPieceAndExerciceIdOrderByDateRecetteDesc(String numeroPiece, Long exerciceId);

    // Recherche recettes créées récemment
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.dateCreation >= :since AND lr.exercice.id = :exerciceId ORDER BY lr.dateCreation DESC")
    List<LivreRecette> findRecentlyCreated(@Param("since") java.time.LocalDateTime since, @Param("exerciceId") Long exerciceId);

    // Recherche recettes modifiées récemment
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.dateModification >= :since AND lr.exercice.id = :exerciceId ORDER BY lr.dateModification DESC")
    List<LivreRecette> findRecentlyModified(@Param("since") java.time.LocalDateTime since, @Param("exerciceId") Long exerciceId);

    // Statistiques par type de recette
    @Query("SELECT lr.typeRecette, COUNT(lr), SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId GROUP BY lr.typeRecette")
    List<Object[]> getStatisticsByTypeRecette(@Param("exerciceId") Long exerciceId);

    // Statistiques par mode de paiement
    @Query("SELECT lr.modePaiement, COUNT(lr), SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId GROUP BY lr.modePaiement")
    List<Object[]> getStatisticsByModePaiement(@Param("exerciceId") Long exerciceId);

    // Statistiques par devise
    @Query("SELECT lr.devise, COUNT(lr), SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId GROUP BY lr.devise")
    List<Object[]> getStatisticsByDevise(@Param("exerciceId") Long exerciceId);

    // Statistiques par statut
    @Query("SELECT lr.statut, COUNT(lr), SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId GROUP BY lr.statut")
    List<Object[]> getStatisticsByStatut(@Param("exerciceId") Long exerciceId);

    // Statistiques par mois
    @Query("SELECT MONTH(lr.dateRecette), COUNT(lr), SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId GROUP BY MONTH(lr.dateRecette) ORDER BY MONTH(lr.dateRecette)")
    List<Object[]> getStatisticsByMonth(@Param("exerciceId") Long exerciceId);

    // Compter les recettes
    @Query("SELECT COUNT(lr) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId")
    Long countRecettes(@Param("exerciceId") Long exerciceId);

    // Compter les recettes par type
    @Query("SELECT COUNT(lr) FROM LivreRecette lr WHERE lr.typeRecette = :typeRecette AND lr.exercice.id = :exerciceId")
    Long countByTypeRecette(@Param("typeRecette") String typeRecette, @Param("exerciceId") Long exerciceId);

    // Compter les recettes par mode de paiement
    @Query("SELECT COUNT(lr) FROM LivreRecette lr WHERE lr.modePaiement = :modePaiement AND lr.exercice.id = :exerciceId")
    Long countByModePaiement(@Param("modePaiement") String modePaiement, @Param("exerciceId") Long exerciceId);

    // Calculer le montant total
    @Query("SELECT SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId")
    BigDecimal getMontantTotal(@Param("exerciceId") Long exerciceId);

    // Calculer le montant total par type de recette
    @Query("SELECT lr.typeRecette, SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId GROUP BY lr.typeRecette")
    List<Object[]> getMontantTotalByTypeRecette(@Param("exerciceId") Long exerciceId);

    // Calculer le montant total par mode de paiement
    @Query("SELECT lr.modePaiement, SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId GROUP BY lr.modePaiement")
    List<Object[]> getMontantTotalByModePaiement(@Param("exerciceId") Long exerciceId);

    // Calculer le montant total par devise
    @Query("SELECT lr.devise, SUM(lr.montant) FROM LivreRecette lr WHERE lr.exercice.id = :exerciceId GROUP BY lr.devise")
    List<Object[]> getMontantTotalByDevise(@Param("exerciceId") Long exerciceId);

    // Recherche recettes avec montant supérieur à un seuil
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.montant >= :seuil AND lr.exercice.id = :exerciceId ORDER BY lr.montant DESC")
    List<LivreRecette> findByMontantGreaterThanEqual(@Param("seuil") BigDecimal seuil, @Param("exerciceId") Long exerciceId);

    // Recherche recettes avec montant inférieur à un seuil
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.montant <= :seuil AND lr.exercice.id = :exerciceId ORDER BY lr.montant ASC")
    List<LivreRecette> findByMontantLessThanEqual(@Param("seuil") BigDecimal seuil, @Param("exerciceId") Long exerciceId);

    // Recherche recettes par plage de montant
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.montant BETWEEN :minMontant AND :maxMontant AND lr.exercice.id = :exerciceId ORDER BY lr.montant DESC")
    List<LivreRecette> findByMontantBetween(@Param("minMontant") BigDecimal minMontant, @Param("maxMontant") BigDecimal maxMontant, @Param("exerciceId") Long exerciceId);

    // Recherche recettes avec observations
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.observations IS NOT NULL AND lr.observations != '' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findWithObservations(@Param("exerciceId") Long exerciceId);

    // Recherche recettes avec numéro de pièce
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.numeroPiece IS NOT NULL AND lr.numeroPiece != '' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findWithNumeroPiece(@Param("exerciceId") Long exerciceId);

    // Recherche recettes avec tiers
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.tiers IS NOT NULL AND lr.tiers != '' AND lr.exercice.id = :exerciceId ORDER BY lr.tiers")
    List<LivreRecette> findWithTiers(@Param("exerciceId") Long exerciceId);

    // Recherche recettes de ventes
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.typeRecette = 'VENTES' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findRecettesVentes(@Param("exerciceId") Long exerciceId);

    // Recherche recettes de services
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.typeRecette = 'SERVICES' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findRecettesServices(@Param("exerciceId") Long exerciceId);

    // Recherche autres recettes
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.typeRecette = 'AUTRES_RECETTES' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findAutresRecettes(@Param("exerciceId") Long exerciceId);

    // Recherche paiements en espèces
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.modePaiement = 'ESPECES' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findPaiementsEspeces(@Param("exerciceId") Long exerciceId);

    // Recherche paiements par chèque
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.modePaiement = 'CHEQUE' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findPaiementsCheque(@Param("exerciceId") Long exerciceId);

    // Recherche paiements par virement
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.modePaiement = 'VIREMENT' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findPaiementsVirement(@Param("exerciceId") Long exerciceId);

    // Recherche paiements par mobile money
    @Query("SELECT lr FROM LivreRecette lr WHERE lr.modePaiement = 'MOBILE_MONEY' AND lr.exercice.id = :exerciceId ORDER BY lr.dateRecette DESC")
    List<LivreRecette> findPaiementsMobileMoney(@Param("exerciceId") Long exerciceId);
}
