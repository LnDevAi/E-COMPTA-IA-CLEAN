package com.ecomptaia.repository;

import com.ecomptaia.entity.LivreDepense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LivreDepenseRepository extends JpaRepository<LivreDepense, Long> {

    // Recherche par exercice
    List<LivreDepense> findByExerciceIdOrderByDateDepenseDesc(Long exerciceId);

    // Recherche par compte de trésorerie
    List<LivreDepense> findByCompteTresorerieIdOrderByDateDepenseDesc(Long compteTresorerieId);

    // Recherche par type de dépense
    List<LivreDepense> findByTypeDepenseAndExerciceIdOrderByDateDepenseDesc(String typeDepense, Long exerciceId);

    // Recherche par mode de paiement
    List<LivreDepense> findByModePaiementAndExerciceIdOrderByDateDepenseDesc(String modePaiement, Long exerciceId);

    // Recherche par devise
    List<LivreDepense> findByDeviseAndExerciceIdOrderByDateDepenseDesc(String devise, Long exerciceId);

    // Recherche par statut
    List<LivreDepense> findByStatutAndExerciceIdOrderByDateDepenseDesc(String statut, Long exerciceId);

    // Recherche par date de dépense
    List<LivreDepense> findByDateDepenseAndExerciceIdOrderByDateDepenseDesc(LocalDate dateDepense, Long exerciceId);

    // Recherche par période
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.dateDepense BETWEEN :dateDebut AND :dateFin AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin, @Param("exerciceId") Long exerciceId);

    // Recherche par libellé (contient)
    List<LivreDepense> findByLibelleContainingIgnoreCaseAndExerciceIdOrderByDateDepenseDesc(String libelle, Long exerciceId);

    // Recherche par tiers (contient)
    List<LivreDepense> findByTiersContainingIgnoreCaseAndExerciceIdOrderByDateDepenseDesc(String tiers, Long exerciceId);

    // Recherche par numéro de pièce
    List<LivreDepense> findByNumeroPieceAndExerciceIdOrderByDateDepenseDesc(String numeroPiece, Long exerciceId);

    // Recherche dépenses créées récemment
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.dateCreation >= :since AND ld.exercice.id = :exerciceId ORDER BY ld.dateCreation DESC")
    List<LivreDepense> findRecentlyCreated(@Param("since") java.time.LocalDateTime since, @Param("exerciceId") Long exerciceId);

    // Recherche dépenses modifiées récemment
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.dateModification >= :since AND ld.exercice.id = :exerciceId ORDER BY ld.dateModification DESC")
    List<LivreDepense> findRecentlyModified(@Param("since") java.time.LocalDateTime since, @Param("exerciceId") Long exerciceId);

    // Statistiques par type de dépense
    @Query("SELECT ld.typeDepense, COUNT(ld), SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId GROUP BY ld.typeDepense")
    List<Object[]> getStatisticsByTypeDepense(@Param("exerciceId") Long exerciceId);

    // Statistiques par mode de paiement
    @Query("SELECT ld.modePaiement, COUNT(ld), SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId GROUP BY ld.modePaiement")
    List<Object[]> getStatisticsByModePaiement(@Param("exerciceId") Long exerciceId);

    // Statistiques par devise
    @Query("SELECT ld.devise, COUNT(ld), SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId GROUP BY ld.devise")
    List<Object[]> getStatisticsByDevise(@Param("exerciceId") Long exerciceId);

    // Statistiques par statut
    @Query("SELECT ld.statut, COUNT(ld), SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId GROUP BY ld.statut")
    List<Object[]> getStatisticsByStatut(@Param("exerciceId") Long exerciceId);

    // Statistiques par mois
    @Query("SELECT MONTH(ld.dateDepense), COUNT(ld), SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId GROUP BY MONTH(ld.dateDepense) ORDER BY MONTH(ld.dateDepense)")
    List<Object[]> getStatisticsByMonth(@Param("exerciceId") Long exerciceId);

    // Compter les dépenses
    @Query("SELECT COUNT(ld) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId")
    Long countDepenses(@Param("exerciceId") Long exerciceId);

    // Compter les dépenses par type
    @Query("SELECT COUNT(ld) FROM LivreDepense ld WHERE ld.typeDepense = :typeDepense AND ld.exercice.id = :exerciceId")
    Long countByTypeDepense(@Param("typeDepense") String typeDepense, @Param("exerciceId") Long exerciceId);

    // Compter les dépenses par mode de paiement
    @Query("SELECT COUNT(ld) FROM LivreDepense ld WHERE ld.modePaiement = :modePaiement AND ld.exercice.id = :exerciceId")
    Long countByModePaiement(@Param("modePaiement") String modePaiement, @Param("exerciceId") Long exerciceId);

    // Calculer le montant total
    @Query("SELECT SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId")
    BigDecimal getMontantTotal(@Param("exerciceId") Long exerciceId);

    // Calculer le montant total par type de dépense
    @Query("SELECT ld.typeDepense, SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId GROUP BY ld.typeDepense")
    List<Object[]> getMontantTotalByTypeDepense(@Param("exerciceId") Long exerciceId);

    // Calculer le montant total par mode de paiement
    @Query("SELECT ld.modePaiement, SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId GROUP BY ld.modePaiement")
    List<Object[]> getMontantTotalByModePaiement(@Param("exerciceId") Long exerciceId);

    // Calculer le montant total par devise
    @Query("SELECT ld.devise, SUM(ld.montant) FROM LivreDepense ld WHERE ld.exercice.id = :exerciceId GROUP BY ld.devise")
    List<Object[]> getMontantTotalByDevise(@Param("exerciceId") Long exerciceId);

    // Recherche dépenses avec montant supérieur à un seuil
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.montant >= :seuil AND ld.exercice.id = :exerciceId ORDER BY ld.montant DESC")
    List<LivreDepense> findByMontantGreaterThanEqual(@Param("seuil") BigDecimal seuil, @Param("exerciceId") Long exerciceId);

    // Recherche dépenses avec montant inférieur à un seuil
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.montant <= :seuil AND ld.exercice.id = :exerciceId ORDER BY ld.montant ASC")
    List<LivreDepense> findByMontantLessThanEqual(@Param("seuil") BigDecimal seuil, @Param("exerciceId") Long exerciceId);

    // Recherche dépenses par plage de montant
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.montant BETWEEN :minMontant AND :maxMontant AND ld.exercice.id = :exerciceId ORDER BY ld.montant DESC")
    List<LivreDepense> findByMontantBetween(@Param("minMontant") BigDecimal minMontant, @Param("maxMontant") BigDecimal maxMontant, @Param("exerciceId") Long exerciceId);

    // Recherche dépenses avec observations
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.observations IS NOT NULL AND ld.observations != '' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findWithObservations(@Param("exerciceId") Long exerciceId);

    // Recherche dépenses avec numéro de pièce
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.numeroPiece IS NOT NULL AND ld.numeroPiece != '' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findWithNumeroPiece(@Param("exerciceId") Long exerciceId);

    // Recherche dépenses avec tiers
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.tiers IS NOT NULL AND ld.tiers != '' AND ld.exercice.id = :exerciceId ORDER BY ld.tiers")
    List<LivreDepense> findWithTiers(@Param("exerciceId") Long exerciceId);

    // Recherche dépenses d'achats
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.typeDepense = 'ACHATS' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findDepensesAchats(@Param("exerciceId") Long exerciceId);

    // Recherche dépenses de frais généraux
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.typeDepense = 'FRAIS_GENERAUX' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findDepensesFraisGeneraux(@Param("exerciceId") Long exerciceId);

    // Recherche dépenses de salaires
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.typeDepense = 'SALAIRES' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findDepensesSalaires(@Param("exerciceId") Long exerciceId);

    // Recherche dépenses d'impôts
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.typeDepense = 'IMPOTS' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findDepensesImpots(@Param("exerciceId") Long exerciceId);

    // Recherche autres dépenses
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.typeDepense = 'AUTRES_DEPENSES' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findAutresDepenses(@Param("exerciceId") Long exerciceId);

    // Recherche paiements en espèces
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.modePaiement = 'ESPECES' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findPaiementsEspeces(@Param("exerciceId") Long exerciceId);

    // Recherche paiements par chèque
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.modePaiement = 'CHEQUE' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findPaiementsCheque(@Param("exerciceId") Long exerciceId);

    // Recherche paiements par virement
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.modePaiement = 'VIREMENT' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findPaiementsVirement(@Param("exerciceId") Long exerciceId);

    // Recherche paiements par mobile money
    @Query("SELECT ld FROM LivreDepense ld WHERE ld.modePaiement = 'MOBILE_MONEY' AND ld.exercice.id = :exerciceId ORDER BY ld.dateDepense DESC")
    List<LivreDepense> findPaiementsMobileMoney(@Param("exerciceId") Long exerciceId);
}
