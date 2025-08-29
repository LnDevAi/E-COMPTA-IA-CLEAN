package com.ecomptaia.repository;

import com.ecomptaia.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    // Préférences par utilisateur
    List<NotificationPreference> findByUserIdAndCompanyId(Long userId, Long companyId);

    // Préférence spécifique par utilisateur, catégorie et type
    Optional<NotificationPreference> findByUserIdAndCompanyIdAndCategoryAndType(
        Long userId, Long companyId, String category, String type);

    // Préférences par catégorie
    List<NotificationPreference> findByUserIdAndCompanyIdAndCategory(Long userId, Long companyId, String category);

    // Préférences par type
    List<NotificationPreference> findByUserIdAndCompanyIdAndType(Long userId, Long companyId, String type);

    // Préférences activées
    List<NotificationPreference> findByUserIdAndCompanyIdAndEnabledTrue(Long userId, Long companyId);

    // Préférences avec notifications email activées
    List<NotificationPreference> findByUserIdAndCompanyIdAndEmailEnabledTrue(Long userId, Long companyId);

    // Préférences avec notifications push activées
    List<NotificationPreference> findByUserIdAndCompanyIdAndPushEnabledTrue(Long userId, Long companyId);

    // Préférences avec notifications in-app activées
    List<NotificationPreference> findByUserIdAndCompanyIdAndInAppEnabledTrue(Long userId, Long companyId);

    // Préférences par priorité
    List<NotificationPreference> findByUserIdAndCompanyIdAndPriority(Long userId, Long companyId, String priority);

    // Préférences par fréquence
    List<NotificationPreference> findByUserIdAndCompanyIdAndFrequency(Long userId, Long companyId, String frequency);

    // Vérifier si une préférence existe
    @Query("SELECT COUNT(np) > 0 FROM NotificationPreference np WHERE np.userId = :userId AND np.companyId = :companyId AND np.category = :category AND np.type = :type")
    boolean existsByUserAndCategoryAndType(@Param("userId") Long userId, 
                                         @Param("companyId") Long companyId, 
                                         @Param("category") String category, 
                                         @Param("type") String type);

    // Compter les préférences par utilisateur
    @Query("SELECT COUNT(np) FROM NotificationPreference np WHERE np.userId = :userId AND np.companyId = :companyId")
    Long countByUser(@Param("userId") Long userId, @Param("companyId") Long companyId);

    // Compter les préférences activées par utilisateur
    @Query("SELECT COUNT(np) FROM NotificationPreference np WHERE np.userId = :userId AND np.companyId = :companyId AND np.enabled = true")
    Long countEnabledByUser(@Param("userId") Long userId, @Param("companyId") Long companyId);
}




