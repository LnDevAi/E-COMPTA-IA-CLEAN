package com.ecomptaia.repository;

import com.ecomptaia.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    // Recherche par clé
    Optional<Configuration> findByConfigKey(String configKey);

    // Recherche par clé et statut actif
    Optional<Configuration> findByConfigKeyAndIsActiveTrue(String configKey);

    // Recherche par catégorie
    List<Configuration> findByCategory(Configuration.ConfigCategory category);

    // Recherche par catégorie et statut actif
    List<Configuration> findByCategoryAndIsActiveTrue(Configuration.ConfigCategory category);

    // Recherche par type
    List<Configuration> findByConfigType(Configuration.ConfigType configType);

    // Recherche par entreprise
    List<Configuration> findByCompanyId(Long companyId);

    // Recherche par entreprise et statut actif
    List<Configuration> findByCompanyIdAndIsActiveTrue(Long companyId);

    // Recherche par utilisateur
    List<Configuration> findByUserId(Long userId);

    // Recherche par utilisateur et statut actif
    List<Configuration> findByUserIdAndIsActiveTrue(Long userId);

    // Recherche par entreprise et catégorie
    List<Configuration> findByCompanyIdAndCategory(Long companyId, Configuration.ConfigCategory category);

    // Recherche par entreprise, catégorie et statut actif
    List<Configuration> findByCompanyIdAndCategoryAndIsActiveTrue(Long companyId, Configuration.ConfigCategory category);

    // Recherche par utilisateur et catégorie
    List<Configuration> findByUserIdAndCategory(Long userId, Configuration.ConfigCategory category);

    // Recherche par utilisateur, catégorie et statut actif
    List<Configuration> findByUserIdAndCategoryAndIsActiveTrue(Long userId, Configuration.ConfigCategory category);

    // Recherche globale par clé (système + entreprise + utilisateur)
    @Query("SELECT c FROM Configuration c WHERE c.configKey = :configKey AND " +
           "(c.companyId = :companyId OR c.companyId IS NULL) AND " +
           "(c.userId = :userId OR c.userId IS NULL) AND " +
           "c.isActive = true ORDER BY c.companyId DESC NULLS LAST, c.userId DESC NULLS LAST")
    List<Configuration> findGlobalConfigByKey(@Param("configKey") String configKey, 
                                             @Param("companyId") Long companyId, 
                                             @Param("userId") Long userId);

    // Recherche de toutes les configurations actives
    List<Configuration> findByIsActiveTrue();

    // Recherche par clé contenant
    List<Configuration> findByConfigKeyContainingIgnoreCase(String configKey);

    // Recherche par description contenant
    List<Configuration> findByDescriptionContainingIgnoreCase(String description);

    // Recherche par clé et entreprise
    Optional<Configuration> findByConfigKeyAndCompanyId(String configKey, Long companyId);

    // Recherche par clé et utilisateur
    Optional<Configuration> findByConfigKeyAndUserId(String configKey, Long userId);

    // Recherche des configurations système (sans entreprise ni utilisateur)
    List<Configuration> findByCompanyIdIsNullAndUserIdIsNullAndIsActiveTrue();

    // Recherche des configurations d'entreprise (avec entreprise, sans utilisateur)
    List<Configuration> findByCompanyIdIsNotNullAndUserIdIsNullAndIsActiveTrue();

    // Recherche des configurations utilisateur (avec utilisateur)
    List<Configuration> findByUserIdIsNotNullAndIsActiveTrue();

    // Recherche par version
    List<Configuration> findByVersionGreaterThan(Integer version);

    // Recherche des configurations modifiées récemment
    @Query("SELECT c FROM Configuration c WHERE c.updatedAt >= :since AND c.isActive = true")
    List<Configuration> findRecentlyModified(@Param("since") java.time.LocalDateTime since);

    // Recherche des configurations par priorité (système > entreprise > utilisateur)
    @Query("SELECT c FROM Configuration c WHERE c.configKey = :configKey AND c.isActive = true " +
           "ORDER BY CASE WHEN c.companyId IS NULL AND c.userId IS NULL THEN 1 " +
           "WHEN c.companyId IS NOT NULL AND c.userId IS NULL THEN 2 " +
           "WHEN c.userId IS NOT NULL THEN 3 END")
    List<Configuration> findConfigurationsByPriority(@Param("configKey") String configKey);

    // Recherche des configurations avec pagination
    @Query("SELECT c FROM Configuration c WHERE c.isActive = true " +
           "AND (:category IS NULL OR c.category = :category) " +
           "AND (:configType IS NULL OR c.configType = :configType) " +
           "AND (:companyId IS NULL OR c.companyId = :companyId) " +
           "AND (:userId IS NULL OR c.userId = :userId)")
    List<Configuration> findConfigurationsWithFilters(@Param("category") Configuration.ConfigCategory category,
                                                     @Param("configType") Configuration.ConfigType configType,
                                                     @Param("companyId") Long companyId,
                                                     @Param("userId") Long userId);
}




