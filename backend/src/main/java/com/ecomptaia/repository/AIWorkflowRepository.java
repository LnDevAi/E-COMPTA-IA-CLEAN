ackage com.ecomptaia.repository;

import com.ecomptaia.security.entity.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecomptaia.entity.AIWorkflow;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour les workflows IA
 * Révolutionnaire vs TOMPRO - Automatisation intelligente
 */
@Repository
public interface AIWorkflowRepository extends JpaRepository<AIWorkflow, Long> {
    
    /**
     * Trouver les workflows par entreprise
     */
    List<AIWorkflow> findByCompanyId(Long companyId);
    
    /**
     * Trouver les workflows actifs
     */
    List<AIWorkflow> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouver les workflows par type de déclenchement
     */
    List<AIWorkflow> findByCompanyIdAndTrigger(Long companyId, AIWorkflow.WorkflowTrigger trigger);
    
    /**
     * Trouver les workflows nécessitant une exécution
     */
    @Query("SELECT w FROM AIWorkflow w WHERE w.isActive = true AND w.nextExecution <= :now")
    List<AIWorkflow> findWorkflowsNeedingExecution(@Param("now") LocalDateTime now);
    
    /**
     * Trouver les workflows par taux de succès
     */
    List<AIWorkflow> findByCompanyIdAndSuccessRateGreaterThanEqual(Long companyId, Integer minSuccessRate);
    
    /**
     * Trouver les workflows créés récemment
     */
    List<AIWorkflow> findByCompanyIdAndCreatedAtAfter(Long companyId, LocalDateTime since);
    
    /**
     * Trouver les workflows par version
     */
    List<AIWorkflow> findByCompanyIdAndVersion(Long companyId, String version);
    
    /**
     * Compter les workflows par statut
     */
    @Query("SELECT w.isActive, COUNT(w) FROM AIWorkflow w WHERE w.company.id = :companyId GROUP BY w.isActive")
    List<Object[]> countWorkflowsByStatus(@Param("companyId") Long companyId);
    
    /**
     * Calculer le taux de succès moyen
     */
    @Query("SELECT AVG(w.successRate) FROM AIWorkflow w WHERE w.company.id = :companyId AND w.isActive = true")
    Double getAverageSuccessRate(@Param("companyId") Long companyId);
    
    /**
     * Trouver les workflows avec le plus d'exécutions
     */
    @Query("SELECT w FROM AIWorkflow w WHERE w.company.id = :companyId ORDER BY w.executionCount DESC")
    List<AIWorkflow> findMostExecutedWorkflows(@Param("companyId") Long companyId);
    
    /**
     * Trouver les workflows avec des échecs récents
     */
    @Query("SELECT w FROM AIWorkflow w WHERE w.company.id = :companyId AND w.failureCount > 0 ORDER BY w.lastExecuted DESC")
    List<AIWorkflow> findWorkflowsWithRecentFailures(@Param("companyId") Long companyId);
}




