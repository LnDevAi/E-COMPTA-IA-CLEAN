package com.ecomptaia.repository;

import com.ecomptaia.entity.CompteTresorerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CompteTresorerieRepository extends JpaRepository<CompteTresorerie, Long> {

    // Recherche par entreprise
    List<CompteTresorerie> findByEntrepriseIdOrderByNomCompte(Long entrepriseId);

    // Recherche comptes actifs par entreprise
    List<CompteTresorerie> findByEntrepriseIdAndEstActifTrueOrderByNomCompte(Long entrepriseId);

    // Recherche par type de compte
    List<CompteTresorerie> findByTypeCompteAndEntrepriseIdOrderByNomCompte(String typeCompte, Long entrepriseId);

    // Recherche par devise
    List<CompteTresorerie> findByDeviseAndEntrepriseIdOrderByNomCompte(String devise, Long entrepriseId);

    // Recherche par statut
    List<CompteTresorerie> findByStatutAndEntrepriseIdOrderByNomCompte(String statut, Long entrepriseId);

    // Recherche par nom de compte (contient)
    List<CompteTresorerie> findByNomCompteContainingIgnoreCaseAndEntrepriseIdOrderByNomCompte(String nomCompte, Long entrepriseId);

    // Recherche par nom de banque (contient)
    List<CompteTresorerie> findByNomBanqueContainingIgnoreCaseAndEntrepriseIdOrderByNomCompte(String nomBanque, Long entrepriseId);

    // Recherche comptes avec solde positif
    @Query("SELECT c FROM CompteTresorerie c WHERE c.soldeActuel > 0 AND c.entreprise.id = :entrepriseId ORDER BY c.soldeActuel DESC")
    List<CompteTresorerie> findWithSoldePositif(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes avec solde négatif
    @Query("SELECT c FROM CompteTresorerie c WHERE c.soldeActuel < 0 AND c.entreprise.id = :entrepriseId ORDER BY c.soldeActuel ASC")
    List<CompteTresorerie> findWithSoldeNegatif(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes avec solde nul
    @Query("SELECT c FROM CompteTresorerie c WHERE c.soldeActuel = 0 AND c.entreprise.id = :entrepriseId ORDER BY c.nomCompte")
    List<CompteTresorerie> findWithSoldeNul(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes avec solde supérieur à un seuil
    @Query("SELECT c FROM CompteTresorerie c WHERE c.soldeActuel >= :seuil AND c.entreprise.id = :entrepriseId ORDER BY c.soldeActuel DESC")
    List<CompteTresorerie> findBySoldeGreaterThanEqual(@Param("seuil") BigDecimal seuil, @Param("entrepriseId") Long entrepriseId);

    // Recherche comptes avec solde inférieur à un seuil
    @Query("SELECT c FROM CompteTresorerie c WHERE c.soldeActuel <= :seuil AND c.entreprise.id = :entrepriseId ORDER BY c.soldeActuel ASC")
    List<CompteTresorerie> findBySoldeLessThanEqual(@Param("seuil") BigDecimal seuil, @Param("entrepriseId") Long entrepriseId);

    // Recherche comptes par plage de solde
    @Query("SELECT c FROM CompteTresorerie c WHERE c.soldeActuel BETWEEN :minSolde AND :maxSolde AND c.entreprise.id = :entrepriseId ORDER BY c.soldeActuel DESC")
    List<CompteTresorerie> findBySoldeBetween(@Param("minSolde") BigDecimal minSolde, @Param("maxSolde") BigDecimal maxSolde, @Param("entrepriseId") Long entrepriseId);

    // Recherche comptes créés récemment
    @Query("SELECT c FROM CompteTresorerie c WHERE c.dateCreation >= :since AND c.entreprise.id = :entrepriseId ORDER BY c.dateCreation DESC")
    List<CompteTresorerie> findRecentlyCreated(@Param("since") java.time.LocalDateTime since, @Param("entrepriseId") Long entrepriseId);

    // Recherche comptes modifiés récemment
    @Query("SELECT c FROM CompteTresorerie c WHERE c.dateModification >= :since AND c.entreprise.id = :entrepriseId ORDER BY c.dateModification DESC")
    List<CompteTresorerie> findRecentlyModified(@Param("since") java.time.LocalDateTime since, @Param("entrepriseId") Long entrepriseId);

    // Statistiques par type de compte
    @Query("SELECT c.typeCompte, COUNT(c) FROM CompteTresorerie c WHERE c.entreprise.id = :entrepriseId GROUP BY c.typeCompte")
    List<Object[]> getStatisticsByTypeCompte(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par devise
    @Query("SELECT c.devise, COUNT(c) FROM CompteTresorerie c WHERE c.entreprise.id = :entrepriseId GROUP BY c.devise")
    List<Object[]> getStatisticsByDevise(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par statut
    @Query("SELECT c.statut, COUNT(c) FROM CompteTresorerie c WHERE c.entreprise.id = :entrepriseId GROUP BY c.statut")
    List<Object[]> getStatisticsByStatut(@Param("entrepriseId") Long entrepriseId);

    // Compter les comptes actifs
    @Query("SELECT COUNT(c) FROM CompteTresorerie c WHERE c.estActif = true AND c.entreprise.id = :entrepriseId")
    Long countComptesActifs(@Param("entrepriseId") Long entrepriseId);

    // Compter les comptes par type
    @Query("SELECT COUNT(c) FROM CompteTresorerie c WHERE c.typeCompte = :typeCompte AND c.entreprise.id = :entrepriseId")
    Long countByTypeCompte(@Param("typeCompte") String typeCompte, @Param("entrepriseId") Long entrepriseId);

    // Compter les comptes par devise
    @Query("SELECT COUNT(c) FROM CompteTresorerie c WHERE c.devise = :devise AND c.entreprise.id = :entrepriseId")
    Long countByDevise(@Param("devise") String devise, @Param("entrepriseId") Long entrepriseId);

    // Calculer le solde total par type de compte
    @Query("SELECT c.typeCompte, SUM(c.soldeActuel) FROM CompteTresorerie c WHERE c.entreprise.id = :entrepriseId GROUP BY c.typeCompte")
    List<Object[]> getSoldeTotalByTypeCompte(@Param("entrepriseId") Long entrepriseId);

    // Calculer le solde total par devise
    @Query("SELECT c.devise, SUM(c.soldeActuel) FROM CompteTresorerie c WHERE c.entreprise.id = :entrepriseId GROUP BY c.devise")
    List<Object[]> getSoldeTotalByDevise(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes avec numéro de compte
    @Query("SELECT c FROM CompteTresorerie c WHERE c.numeroCompte IS NOT NULL AND c.numeroCompte != '' AND c.entreprise.id = :entrepriseId ORDER BY c.nomCompte")
    List<CompteTresorerie> findWithNumeroCompte(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes avec nom de banque
    @Query("SELECT c FROM CompteTresorerie c WHERE c.nomBanque IS NOT NULL AND c.nomBanque != '' AND c.entreprise.id = :entrepriseId ORDER BY c.nomBanque")
    List<CompteTresorerie> findWithNomBanque(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes avec description
    @Query("SELECT c FROM CompteTresorerie c WHERE c.description IS NOT NULL AND c.description != '' AND c.entreprise.id = :entrepriseId ORDER BY c.nomCompte")
    List<CompteTresorerie> findWithDescription(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes caisse
    @Query("SELECT c FROM CompteTresorerie c WHERE c.typeCompte = 'CAISSE' AND c.entreprise.id = :entrepriseId ORDER BY c.nomCompte")
    List<CompteTresorerie> findComptesCaisse(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes banque
    @Query("SELECT c FROM CompteTresorerie c WHERE c.typeCompte = 'BANQUE' AND c.entreprise.id = :entrepriseId ORDER BY c.nomCompte")
    List<CompteTresorerie> findComptesBanque(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes mobile money
    @Query("SELECT c FROM CompteTresorerie c WHERE c.typeCompte = 'MOBILE_MONEY' AND c.entreprise.id = :entrepriseId ORDER BY c.nomCompte")
    List<CompteTresorerie> findComptesMobileMoney(@Param("entrepriseId") Long entrepriseId);

    // Recherche comptes autres
    @Query("SELECT c FROM CompteTresorerie c WHERE c.typeCompte = 'AUTRE' AND c.entreprise.id = :entrepriseId ORDER BY c.nomCompte")
    List<CompteTresorerie> findComptesAutres(@Param("entrepriseId") Long entrepriseId);
}
