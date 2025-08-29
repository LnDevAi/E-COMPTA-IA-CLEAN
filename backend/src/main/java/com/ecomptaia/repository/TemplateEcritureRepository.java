package com.ecomptaia.repository;

import com.ecomptaia.entity.TemplateEcriture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

@Repository
public interface TemplateEcritureRepository extends JpaRepository<TemplateEcriture, UUID> {
    
    // Recherche par code
    Optional<TemplateEcriture> findByCode(String code);
    
    // Recherche par standard comptable
    List<TemplateEcriture> findByStandardComptableOrderByOrdreAffichageAsc(String standardComptable);
    
    // Recherche par catégorie
    List<TemplateEcriture> findByCategorieOrderByOrdreAffichageAsc(TemplateEcriture.CategorieTemplate categorie);
    
    // Recherche par pays applicable
    List<TemplateEcriture> findByPaysApplicableOrderByOrdreAffichageAsc(String paysApplicable);
    
    // Recherche par devise
    List<TemplateEcriture> findByDeviseDefautOrderByOrdreAffichageAsc(String deviseDefaut);
    
    // Recherche des templates actifs
    List<TemplateEcriture> findByIsActifTrueOrderByOrdreAffichageAsc();
    
    // Recherche par standard comptable et catégorie
    List<TemplateEcriture> findByStandardComptableAndCategorieOrderByOrdreAffichageAsc(
        String standardComptable, 
        TemplateEcriture.CategorieTemplate categorie
    );
    
    // Recherche par standard comptable et pays
    List<TemplateEcriture> findByStandardComptableAndPaysApplicableOrderByOrdreAffichageAsc(
        String standardComptable, 
        String paysApplicable
    );
    
    // Recherche par mots-clés
    @Query("SELECT t FROM TemplateEcriture t WHERE " +
           "LOWER(t.motsCles) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(t.nom) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
           "ORDER BY t.ordreAffichage ASC")
    List<TemplateEcriture> findByMotCle(@Param("motCle") String motCle);
    
    // Recherche avancée avec critères multiples
    @Query("SELECT t FROM TemplateEcriture t WHERE " +
           "(:standardComptable IS NULL OR t.standardComptable = :standardComptable) AND " +
           "(:categorie IS NULL OR t.categorie = :categorie) AND " +
           "(:paysApplicable IS NULL OR t.paysApplicable = :paysApplicable) AND " +
           "(:deviseDefaut IS NULL OR t.deviseDefaut = :deviseDefaut) AND " +
           "(:isActif IS NULL OR t.isActif = :isActif) AND " +
           "(:motCle IS NULL OR " +
           "LOWER(t.motsCles) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(t.nom) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :motCle, '%'))) " +
           "ORDER BY t.ordreAffichage ASC")
    List<TemplateEcriture> findTemplatesWithCriteria(
        @Param("standardComptable") String standardComptable,
        @Param("categorie") TemplateEcriture.CategorieTemplate categorie,
        @Param("paysApplicable") String paysApplicable,
        @Param("deviseDefaut") String deviseDefaut,
        @Param("isActif") Boolean isActif,
        @Param("motCle") String motCle
    );
    
    // Recherche des templates recommandés pour une opération
    @Query("SELECT t FROM TemplateEcriture t WHERE " +
           "t.standardComptable = :standardComptable AND " +
           "t.isActif = true AND " +
           "(" +
           "LOWER(t.motsCles) LIKE LOWER(CONCAT('%', :operation, '%')) OR " +
           "LOWER(t.nom) LIKE LOWER(CONCAT('%', :operation, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :operation, '%'))" +
           ") " +
           "ORDER BY t.ordreAffichage ASC")
    List<TemplateEcriture> findTemplatesRecommandes(
        @Param("standardComptable") String standardComptable,
        @Param("operation") String operation
    );
    
    // Comptage par catégorie
    @Query("SELECT t.categorie, COUNT(t) FROM TemplateEcriture t WHERE t.isActif = true GROUP BY t.categorie")
    List<Object[]> countByCategorie();
    
    // Comptage par standard comptable
    @Query("SELECT t.standardComptable, COUNT(t) FROM TemplateEcriture t WHERE t.isActif = true GROUP BY t.standardComptable")
    List<Object[]> countByStandardComptable();
    
    // Comptage par pays
    @Query("SELECT t.paysApplicable, COUNT(t) FROM TemplateEcriture t WHERE t.isActif = true GROUP BY t.paysApplicable")
    List<Object[]> countByPays();
    
    // Recherche des templates populaires (utilisés le plus souvent)
    @Query("SELECT t FROM TemplateEcriture t WHERE t.isActif = true ORDER BY t.ordreAffichage ASC")
    List<TemplateEcriture> findTemplatesPopulaires();
    
    // Recherche par taux de TVA
    @Query("SELECT t FROM TemplateEcriture t WHERE t.tauxTvaDefaut = :tauxTva ORDER BY t.ordreAffichage ASC")
    List<TemplateEcriture> findByTauxTva(@Param("tauxTva") BigDecimal tauxTva);
    
    // Recherche des templates sans TVA
    @Query("SELECT t FROM TemplateEcriture t WHERE t.tauxTvaDefaut IS NULL OR t.tauxTvaDefaut = 0 ORDER BY t.ordreAffichage ASC")
    List<TemplateEcriture> findTemplatesSansTva();
    
    // Recherche des templates avec TVA
    @Query("SELECT t FROM TemplateEcriture t WHERE t.tauxTvaDefaut IS NOT NULL AND t.tauxTvaDefaut > 0 ORDER BY t.ordreAffichage ASC")
    List<TemplateEcriture> findTemplatesAvecTva();
    
    // Recherche par plage de taux de TVA
    @Query("SELECT t FROM TemplateEcriture t WHERE " +
           "t.tauxTvaDefaut BETWEEN :tauxMin AND :tauxMax " +
           "ORDER BY t.ordreAffichage ASC")
    List<TemplateEcriture> findByPlageTauxTva(
        @Param("tauxMin") BigDecimal tauxMin,
        @Param("tauxMax") BigDecimal tauxMax
    );
    
    // Recherche des templates récents
    @Query("SELECT t FROM TemplateEcriture t WHERE t.isActif = true ORDER BY t.createdAt DESC")
    List<TemplateEcriture> findTemplatesRecents();
    
    // Recherche des templates mis à jour récemment
    @Query("SELECT t FROM TemplateEcriture t WHERE t.isActif = true ORDER BY t.updatedAt DESC")
    List<TemplateEcriture> findTemplatesMisAJourRecemment();
    
    // Recherche par nom contenant
    List<TemplateEcriture> findByNomContainingIgnoreCaseOrderByOrdreAffichageAsc(String nom);
    
    // Recherche par description contenant
    List<TemplateEcriture> findByDescriptionContainingIgnoreCaseOrderByOrdreAffichageAsc(String description);
    
    // Recherche des templates par standard comptable et actifs
    List<TemplateEcriture> findByStandardComptableAndIsActifTrueOrderByOrdreAffichageAsc(String standardComptable);
    
    // Recherche des templates par catégorie et actifs
    List<TemplateEcriture> findByCategorieAndIsActifTrueOrderByOrdreAffichageAsc(TemplateEcriture.CategorieTemplate categorie);
}
