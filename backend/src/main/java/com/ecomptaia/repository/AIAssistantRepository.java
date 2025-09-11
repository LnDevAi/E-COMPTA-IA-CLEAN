ackage com.ecomptaia.repository;

import com.ecomptaia.security.entity.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecomptaia.entity.AIAssistant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'assistant IA
 * Révolutionnaire vs TOMPRO - Assistant IA 24/7
 */
@Repository
public interface AIAssistantRepository extends JpaRepository<AIAssistant, Long> {
    
    /**
     * Trouver les messages par entreprise
     */
    List<AIAssistant> findByCompanyId(Long companyId);
    
    /**
     * Trouver les messages par utilisateur
     */
    List<AIAssistant> findByUserId(Long userId);
    
    /**
     * Trouver les messages d'une conversation
     */
    List<AIAssistant> findByConversationIdOrderByTimestampAsc(Long conversationId);
    
    /**
     * Trouver les messages par type
     */
    List<AIAssistant> findByCompanyIdAndType(Long companyId, AIAssistant.MessageType type);
    
    /**
     * Trouver les messages récents
     */
    List<AIAssistant> findByCompanyIdAndTimestampAfter(Long companyId, LocalDateTime since);
    
    /**
     * Trouver les messages par confiance minimale
     */
    List<AIAssistant> findByCompanyIdAndConfidenceGreaterThanEqual(Long companyId, Integer minConfidence);
    
    /**
     * Trouver les messages de l'utilisateur
     */
    List<AIAssistant> findByCompanyIdAndIsFromUserTrue(Long companyId);
    
    /**
     * Trouver les messages de l'IA
     */
    List<AIAssistant> findByCompanyIdAndIsFromUserFalse(Long companyId);
    
    /**
     * Compter les messages par type
     */
    @Query("SELECT a.type, COUNT(a) FROM AIAssistant a WHERE a.company.id = :companyId GROUP BY a.type")
    List<Object[]> countMessagesByType(@Param("companyId") Long companyId);
    
    /**
     * Trouver les messages avec des insights liés
     */
    @Query("SELECT a FROM AIAssistant a WHERE a.company.id = :companyId AND SIZE(a.relatedInsights) > 0")
    List<AIAssistant> findMessagesWithRelatedInsights(@Param("companyId") Long companyId);
    
    /**
     * Calculer la confiance moyenne des réponses IA
     */
    @Query("SELECT AVG(a.confidence) FROM AIAssistant a WHERE a.company.id = :companyId AND a.isFromUser = false AND a.confidence IS NOT NULL")
    Double getAverageConfidence(@Param("companyId") Long companyId);
}

