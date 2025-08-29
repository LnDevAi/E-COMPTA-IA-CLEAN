package com.ecomptaia.repository;

import com.ecomptaia.entity.EntrepriseSMT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepriseSMTRepository extends JpaRepository<EntrepriseSMT, Long> {

    // Recherche par numéro contribuable
    Optional<EntrepriseSMT> findByNumeroContribuableAndEstActifTrue(String numeroContribuable);

    // Recherche par pays
    List<EntrepriseSMT> findByPaysCodeAndEstActifTrue(String paysCode);

    // Recherche par régime fiscal
    List<EntrepriseSMT> findByRegimeFiscalAndEstActifTrue(String regimeFiscal);

    // Recherche par statut
    List<EntrepriseSMT> findByStatutAndEstActifTrue(String statut);

    // Recherche par devise
    List<EntrepriseSMT> findByDeviseAndEstActifTrue(String devise);

    // Recherche entreprises actives
    List<EntrepriseSMT> findByEstActifTrue();

    // Recherche par activité principale
    List<EntrepriseSMT> findByActivitePrincipaleContainingIgnoreCaseAndEstActifTrue(String activite);

    // Recherche par nom d'entreprise (contient)
    List<EntrepriseSMT> findByNomEntrepriseContainingIgnoreCaseAndEstActifTrue(String nom);

    // Recherche par représentant légal
    List<EntrepriseSMT> findByRepresentantLegalContainingIgnoreCaseAndEstActifTrue(String representant);

    // Recherche entreprises créées récemment
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.dateCreation >= :since AND e.estActif = true ORDER BY e.dateCreation DESC")
    List<EntrepriseSMT> findRecentlyCreated(@Param("since") java.time.LocalDateTime since);

    // Recherche entreprises modifiées récemment
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.dateModification >= :since AND e.estActif = true ORDER BY e.dateModification DESC")
    List<EntrepriseSMT> findRecentlyModified(@Param("since") java.time.LocalDateTime since);

    // Statistiques par régime fiscal
    @Query("SELECT e.regimeFiscal, COUNT(e) FROM EntrepriseSMT e WHERE e.estActif = true GROUP BY e.regimeFiscal")
    List<Object[]> getStatisticsByRegimeFiscal();

    // Statistiques par pays
    @Query("SELECT e.paysCode, COUNT(e) FROM EntrepriseSMT e WHERE e.estActif = true GROUP BY e.paysCode")
    List<Object[]> getStatisticsByPays();

    // Statistiques par devise
    @Query("SELECT e.devise, COUNT(e) FROM EntrepriseSMT e WHERE e.estActif = true GROUP BY e.devise")
    List<Object[]> getStatisticsByDevise();

    // Statistiques par statut
    @Query("SELECT e.statut, COUNT(e) FROM EntrepriseSMT e WHERE e.estActif = true GROUP BY e.statut")
    List<Object[]> getStatisticsByStatut();

    // Compter les entreprises actives
    @Query("SELECT COUNT(e) FROM EntrepriseSMT e WHERE e.estActif = true")
    Long countActiveEntreprises();

    // Compter les entreprises par pays
    @Query("SELECT COUNT(e) FROM EntrepriseSMT e WHERE e.paysCode = :paysCode AND e.estActif = true")
    Long countByPaysCode(@Param("paysCode") String paysCode);

    // Compter les entreprises par régime fiscal
    @Query("SELECT COUNT(e) FROM EntrepriseSMT e WHERE e.regimeFiscal = :regimeFiscal AND e.estActif = true")
    Long countByRegimeFiscal(@Param("regimeFiscal") String regimeFiscal);

    // Recherche entreprises avec seuil de chiffre d'affaires supérieur
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.seuilChiffreAffaires >= :seuil AND e.estActif = true ORDER BY e.seuilChiffreAffaires DESC")
    List<EntrepriseSMT> findBySeuilChiffreAffairesGreaterThan(@Param("seuil") Double seuil);

    // Recherche entreprises avec seuil de chiffre d'affaires inférieur
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.seuilChiffreAffaires <= :seuil AND e.estActif = true ORDER BY e.seuilChiffreAffaires ASC")
    List<EntrepriseSMT> findBySeuilChiffreAffairesLessThan(@Param("seuil") Double seuil);

    // Recherche entreprises par plage de seuil
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.seuilChiffreAffaires BETWEEN :minSeuil AND :maxSeuil AND e.estActif = true ORDER BY e.seuilChiffreAffaires")
    List<EntrepriseSMT> findBySeuilChiffreAffairesBetween(@Param("minSeuil") Double minSeuil, @Param("maxSeuil") Double maxSeuil);

    // Recherche entreprises avec numéro de registre de commerce
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.numeroRegistreCommerce IS NOT NULL AND e.numeroRegistreCommerce != '' AND e.estActif = true")
    List<EntrepriseSMT> findWithRegistreCommerce();

    // Recherche entreprises avec numéro CNSS
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.numeroCNSS IS NOT NULL AND e.numeroCNSS != '' AND e.estActif = true")
    List<EntrepriseSMT> findWithCNSS();

    // Recherche entreprises avec numéro CNAS
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.numeroCNAS IS NOT NULL AND e.numeroCNAS != '' AND e.estActif = true")
    List<EntrepriseSMT> findWithCNAS();

    // Recherche entreprises avec email
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.email IS NOT NULL AND e.email != '' AND e.estActif = true")
    List<EntrepriseSMT> findWithEmail();

    // Recherche entreprises avec téléphone
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.telephone IS NOT NULL AND e.telephone != '' AND e.estActif = true")
    List<EntrepriseSMT> findWithTelephone();

    // Recherche entreprises avec adresse complète
    @Query("SELECT e FROM EntrepriseSMT e WHERE e.adresse IS NOT NULL AND e.adresse != '' AND e.estActif = true")
    List<EntrepriseSMT> findWithAdresse();
}
