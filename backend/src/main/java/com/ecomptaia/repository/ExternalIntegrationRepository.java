package com.ecomptaia.repository;

import com.ecomptaia.entity.ExternalIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExternalIntegrationRepository extends JpaRepository<ExternalIntegration, Long> {

    // Trouver par entreprise
    List<ExternalIntegration> findByEntrepriseId(Long entrepriseId);

    // Trouver par type d'intégration
    List<ExternalIntegration> findByIntegrationType(ExternalIntegration.IntegrationType integrationType);

    // Trouver par entreprise et type
    List<ExternalIntegration> findByEntrepriseIdAndIntegrationType(Long entrepriseId, 
                                                                 ExternalIntegration.IntegrationType integrationType);

    // Trouver les intégrations actives
    List<ExternalIntegration> findByIsActiveTrue();

    // Trouver les intégrations actives par entreprise
    List<ExternalIntegration> findByEntrepriseIdAndIsActiveTrue(Long entrepriseId);

    // Trouver par statut de synchronisation
    List<ExternalIntegration> findBySyncStatus(ExternalIntegration.SyncStatus syncStatus);

    // Trouver les intégrations qui doivent être synchronisées
    @Query("SELECT ei FROM ExternalIntegration ei WHERE ei.isActive = true AND ei.nextSync <= :now")
    List<ExternalIntegration> findIntegrationsToSync(@Param("now") LocalDateTime now);

    // Compter par entreprise
    Long countByEntrepriseId(Long entrepriseId);

    // Compter les intégrations actives par entreprise
    Long countByEntrepriseIdAndIsActiveTrue(Long entrepriseId);

    // Compter par type d'intégration
    Long countByIntegrationType(ExternalIntegration.IntegrationType integrationType);

    // Trouver par fournisseur
    List<ExternalIntegration> findByProviderName(String providerName);

    // Trouver les intégrations avec des erreurs
    @Query("SELECT ei FROM ExternalIntegration ei WHERE ei.errorCount > 0 ORDER BY ei.errorCount DESC")
    List<ExternalIntegration> findIntegrationsWithErrors();

    // Trouver les intégrations récentes
    @Query("SELECT ei FROM ExternalIntegration ei WHERE ei.entrepriseId = :entrepriseId ORDER BY ei.createdAt DESC")
    List<ExternalIntegration> findRecentIntegrations(@Param("entrepriseId") Long entrepriseId);

    // Trouver par nom d'intégration
    List<ExternalIntegration> findByIntegrationNameContainingIgnoreCase(String integrationName);

    // Trouver par entreprise et nom
    List<ExternalIntegration> findByEntrepriseIdAndIntegrationNameContainingIgnoreCase(Long entrepriseId, 
                                                                                      String integrationName);
}




