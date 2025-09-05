package com.ecomptaia.service;

import com.ecomptaia.entity.ComplianceRequirement;
import com.ecomptaia.entity.Audit;
import com.ecomptaia.repository.ComplianceRequirementRepository;
import com.ecomptaia.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ComplianceAuditService {

    @Autowired
    private ComplianceRequirementRepository complianceRequirementRepository;

    @Autowired
    private AuditRepository auditRepository;

    // ==================== GESTION DES EXIGENCES DE CONFORMITÉ ====================

    /**
     * Créer une nouvelle exigence de conformité
     */
    public ComplianceRequirement createComplianceRequirement(String requirementCode, String requirementName,
                                                           ComplianceRequirement.RequirementCategory category,
                                                           ComplianceRequirement.RequirementType type,
                                                           ComplianceRequirement.PriorityLevel priority,
                                                           Long entrepriseId) {
        ComplianceRequirement requirement = new ComplianceRequirement(requirementCode, requirementName, 
                                                                    category, type, priority, entrepriseId);
        requirement.setUpdatedAt(LocalDateTime.now());
        return complianceRequirementRepository.save(requirement);
    }

    /**
     * Mettre à jour une exigence de conformité
     */
    public ComplianceRequirement updateComplianceRequirement(Long requirementId, ComplianceRequirement requirementDetails) {
        ComplianceRequirement requirement = complianceRequirementRepository.findById(requirementId)
                .orElseThrow(() -> new RuntimeException("Exigence de conformité non trouvée"));

        if (requirementDetails.getRequirementName() != null) {
            requirement.setRequirementName(requirementDetails.getRequirementName());
        }
        if (requirementDetails.getRequirementDescription() != null) {
            requirement.setRequirementDescription(requirementDetails.getRequirementDescription());
        }
        if (requirementDetails.getRequirementCategory() != null) {
            requirement.setRequirementCategory(requirementDetails.getRequirementCategory());
        }
        if (requirementDetails.getRequirementType() != null) {
            requirement.setRequirementType(requirementDetails.getRequirementType());
        }
        if (requirementDetails.getPriorityLevel() != null) {
            requirement.setPriorityLevel(requirementDetails.getPriorityLevel());
        }
        if (requirementDetails.getComplianceFrequency() != null) {
            requirement.setComplianceFrequency(requirementDetails.getComplianceFrequency());
        }
        if (requirementDetails.getComplianceStatus() != null) {
            requirement.setComplianceStatus(requirementDetails.getComplianceStatus());
        }
        if (requirementDetails.getImplementationNotes() != null) {
            requirement.setImplementationNotes(requirementDetails.getImplementationNotes());
        }
        if (requirementDetails.getControlProcedures() != null) {
            requirement.setControlProcedures(requirementDetails.getControlProcedures());
        }
        if (requirementDetails.getResponsiblePerson() != null) {
            requirement.setResponsiblePerson(requirementDetails.getResponsiblePerson());
        }
        if (requirementDetails.getReviewFrequency() != null) {
            requirement.setReviewFrequency(requirementDetails.getReviewFrequency());
        }

        requirement.setUpdatedAt(LocalDateTime.now());
        return complianceRequirementRepository.save(requirement);
    }

    /**
     * Mettre à jour le statut de conformité
     */
    public ComplianceRequirement updateComplianceStatus(Long requirementId, 
                                                      ComplianceRequirement.ComplianceStatus status,
                                                      String notes) {
        ComplianceRequirement requirement = complianceRequirementRepository.findById(requirementId)
                .orElseThrow(() -> new RuntimeException("Exigence de conformité non trouvée"));

        requirement.setComplianceStatus(status);
        requirement.setLastComplianceDate(LocalDateTime.now());
        requirement.setImplementationNotes(notes);
        requirement.setUpdatedAt(LocalDateTime.now());

        // Calculer la prochaine date de conformité
        if (requirement.getComplianceFrequency() != null) {
            LocalDateTime nextDate = calculateNextComplianceDate(requirement.getComplianceFrequency());
            requirement.setNextComplianceDate(nextDate);
        }

        return complianceRequirementRepository.save(requirement);
    }

    /**
     * Obtenir les exigences de conformité par catégorie
     */
    public List<ComplianceRequirement> getRequirementsByCategory(Long entrepriseId, 
                                                               ComplianceRequirement.RequirementCategory category) {
        return complianceRequirementRepository.findByEntrepriseIdAndRequirementCategoryAndIsActiveTrue(entrepriseId, category);
    }

    /**
     * Obtenir les exigences de conformité par statut
     */
    public List<ComplianceRequirement> getRequirementsByStatus(Long entrepriseId, 
                                                             ComplianceRequirement.ComplianceStatus status) {
        return complianceRequirementRepository.findByEntrepriseIdAndComplianceStatusAndIsActiveTrue(entrepriseId, status);
    }

    /**
     * Rechercher des exigences de conformité
     */
    public List<ComplianceRequirement> searchRequirements(Long entrepriseId, String keyword) {
        return complianceRequirementRepository.searchActiveRequirements(entrepriseId, keyword);
    }

    // ==================== GESTION DES AUDITS ====================

    /**
     * Créer un nouvel audit
     */
    public Audit createAudit(String auditCode, String auditName, Audit.AuditType auditType,
                           Audit.AuditScope auditScope, Audit.AuditPriority auditPriority,
                           Long auditorId, Long entrepriseId) {
        Audit audit = new Audit(auditCode, auditName, auditType, auditScope, auditPriority, auditorId, entrepriseId);
        audit.setUpdatedAt(LocalDateTime.now());
        return auditRepository.save(audit);
    }

    /**
     * Mettre à jour un audit
     */
    public Audit updateAudit(Long auditId, Audit auditDetails) {
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));

        if (auditDetails.getAuditName() != null) {
            audit.setAuditName(auditDetails.getAuditName());
        }
        if (auditDetails.getAuditDescription() != null) {
            audit.setAuditDescription(auditDetails.getAuditDescription());
        }
        if (auditDetails.getAuditType() != null) {
            audit.setAuditType(auditDetails.getAuditType());
        }
        if (auditDetails.getAuditScope() != null) {
            audit.setAuditScope(auditDetails.getAuditScope());
        }
        if (auditDetails.getAuditPriority() != null) {
            audit.setAuditPriority(auditDetails.getAuditPriority());
        }
        if (auditDetails.getPlannedStartDate() != null) {
            audit.setPlannedStartDate(auditDetails.getPlannedStartDate());
        }
        if (auditDetails.getPlannedEndDate() != null) {
            audit.setPlannedEndDate(auditDetails.getPlannedEndDate());
        }
        if (auditDetails.getAuditObjectives() != null) {
            audit.setAuditObjectives(auditDetails.getAuditObjectives());
        }
        if (auditDetails.getAuditCriteria() != null) {
            audit.setAuditCriteria(auditDetails.getAuditCriteria());
        }
        if (auditDetails.getAuditMethodology() != null) {
            audit.setAuditMethodology(auditDetails.getAuditMethodology());
        }

        audit.setUpdatedAt(LocalDateTime.now());
        return auditRepository.save(audit);
    }

    /**
     * Démarrer un audit
     */
    public Audit startAudit(Long auditId) {
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));

        audit.setAuditStatus(Audit.AuditStatus.IN_PROGRESS);
        audit.setActualStartDate(LocalDateTime.now());
        audit.setUpdatedAt(LocalDateTime.now());

        return auditRepository.save(audit);
    }

    /**
     * Terminer un audit
     */
    public Audit completeAudit(Long auditId, String findings, String conclusions, 
                             String recommendations, Integer auditScore, 
                             Integer complianceScore, Integer riskScore) {
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));

        audit.setAuditStatus(Audit.AuditStatus.COMPLETED);
        audit.setActualEndDate(LocalDateTime.now());
        audit.setAuditFindings(findings);
        audit.setAuditConclusions(conclusions);
        audit.setAuditRecommendations(recommendations);
        audit.setAuditScore(auditScore);
        audit.setComplianceScore(complianceScore);
        audit.setRiskScore(riskScore);
        audit.setUpdatedAt(LocalDateTime.now());

        return auditRepository.save(audit);
    }

    /**
     * Approuver un audit
     */
    public Audit approveAudit(Long auditId, Long approvedBy, String approvalNotes) {
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));

        audit.setApprovalStatus(Audit.ApprovalStatus.APPROVED);
        audit.setApprovedBy(approvedBy);
        audit.setApprovalDate(LocalDateTime.now());
        audit.setApprovalNotes(approvalNotes);
        audit.setUpdatedAt(LocalDateTime.now());

        return auditRepository.save(audit);
    }

    /**
     * Obtenir les audits par type
     */
    public List<Audit> getAuditsByType(Long entrepriseId, Audit.AuditType auditType) {
        return auditRepository.findByEntrepriseIdAndAuditTypeOrderByCreatedAtDesc(entrepriseId, auditType);
    }

    /**
     * Obtenir les audits par statut
     */
    public List<Audit> getAuditsByStatus(Long entrepriseId, Audit.AuditStatus auditStatus) {
        return auditRepository.findByEntrepriseIdAndAuditStatusOrderByCreatedAtDesc(entrepriseId, auditStatus);
    }

    /**
     * Rechercher des audits
     */
    public List<Audit> searchAudits(Long entrepriseId, String keyword) {
        return auditRepository.searchAuditsOrderByCreatedAtDesc(entrepriseId, keyword);
    }

    // ==================== STATISTIQUES ====================

    /**
     * Obtenir les statistiques de conformité
     */
    public Map<String, Object> getComplianceStatistics(Long entrepriseId) {
        Map<String, Object> statistics = new HashMap<>();

        // Statistiques générales
        statistics.put("totalRequirements", complianceRequirementRepository.countByEntrepriseId(entrepriseId));
        statistics.put("activeRequirements", complianceRequirementRepository.countActiveByEntrepriseId(entrepriseId));

        // Statistiques par statut
        statistics.put("compliantRequirements", 
            complianceRequirementRepository.countByEntrepriseIdAndComplianceStatus(entrepriseId, 
                ComplianceRequirement.ComplianceStatus.COMPLIANT));
        statistics.put("nonCompliantRequirements", 
            complianceRequirementRepository.countByEntrepriseIdAndComplianceStatus(entrepriseId, 
                ComplianceRequirement.ComplianceStatus.NON_COMPLIANT));
        statistics.put("partiallyCompliantRequirements", 
            complianceRequirementRepository.countByEntrepriseIdAndComplianceStatus(entrepriseId, 
                ComplianceRequirement.ComplianceStatus.PARTIALLY_COMPLIANT));

        // Statistiques par catégorie
        statistics.put("requirementsByCategory", 
            complianceRequirementRepository.getRequirementsByCategory(entrepriseId));

        // Statistiques par priorité
        statistics.put("requirementsByPriority", 
            complianceRequirementRepository.getRequirementsByPriority(entrepriseId));

        // Score moyen de conformité
        Double averageScore = complianceRequirementRepository.getAverageComplianceScore(entrepriseId);
        statistics.put("averageComplianceScore", averageScore != null ? averageScore : 0.0);

        return statistics;
    }

    /**
     * Obtenir les statistiques d'audit
     */
    public Map<String, Object> getAuditStatistics(Long entrepriseId) {
        Map<String, Object> statistics = new HashMap<>();

        // Statistiques générales
        statistics.put("totalAudits", auditRepository.countByEntrepriseId(entrepriseId));

        // Statistiques par statut
        statistics.put("plannedAudits", 
            auditRepository.countByEntrepriseIdAndAuditStatus(entrepriseId, Audit.AuditStatus.PLANNED));
        statistics.put("inProgressAudits", 
            auditRepository.countByEntrepriseIdAndAuditStatus(entrepriseId, Audit.AuditStatus.IN_PROGRESS));
        statistics.put("completedAudits", 
            auditRepository.countByEntrepriseIdAndAuditStatus(entrepriseId, Audit.AuditStatus.COMPLETED));

        // Statistiques par type
        statistics.put("auditsByType", auditRepository.getAuditsByType(entrepriseId));

        // Statistiques par priorité
        statistics.put("auditsByPriority", auditRepository.getAuditsByPriority(entrepriseId));

        // Statistiques par évaluation globale
        statistics.put("auditsByOverallRating", auditRepository.getAuditsByOverallRating(entrepriseId));

        // Scores moyens
        Double averageAuditScore = auditRepository.getAverageAuditScore(entrepriseId);
        Double averageComplianceScore = auditRepository.getAverageComplianceScore(entrepriseId);
        Double averageRiskScore = auditRepository.getAverageRiskScore(entrepriseId);

        statistics.put("averageAuditScore", averageAuditScore != null ? averageAuditScore : 0.0);
        statistics.put("averageComplianceScore", averageComplianceScore != null ? averageComplianceScore : 0.0);
        statistics.put("averageRiskScore", averageRiskScore != null ? averageRiskScore : 0.0);

        // Statistiques de coûts
        Double totalActualCost = auditRepository.getTotalActualCost(entrepriseId);
        Double totalBudgetAmount = auditRepository.getTotalBudgetAmount(entrepriseId);

        statistics.put("totalActualCost", totalActualCost != null ? totalActualCost : 0.0);
        statistics.put("totalBudgetAmount", totalBudgetAmount != null ? totalBudgetAmount : 0.0);

        return statistics;
    }

    // ==================== SURVEILLANCE ====================

    /**
     * Obtenir les exigences en retard
     */
    public List<ComplianceRequirement> getOverdueRequirements(Long entrepriseId) {
        return complianceRequirementRepository.findOverdueRequirements(entrepriseId, LocalDateTime.now());
    }

    /**
     * Obtenir les audits en retard
     */
    public List<Audit> getOverdueAudits(Long entrepriseId) {
        return auditRepository.findOverdueAudits(entrepriseId, LocalDateTime.now());
    }

    /**
     * Obtenir les exigences critiques non conformes
     */
    public List<ComplianceRequirement> getCriticalNonCompliantRequirements(Long entrepriseId) {
        return complianceRequirementRepository.findCriticalNonCompliantRequirements(entrepriseId);
    }

    /**
     * Obtenir les audits nécessitant un suivi
     */
    public List<Audit> getAuditsRequiringFollowUp(Long entrepriseId) {
        return auditRepository.findAuditsRequiringFollowUp(entrepriseId);
    }

    // ==================== TÂCHES PLANIFIÉES ====================

    /**
     * Surveillance quotidienne de la conformité
     */
    // @Scheduled(cron = "0 0 9 * * ?") // Tous les jours à 9h00 - Désactivé temporairement
    // @Async
    public void dailyComplianceMonitoring() {
        try {
            // Logique de surveillance quotidienne
            List<ComplianceRequirement> overdueRequirements = complianceRequirementRepository
                .findOverdueRequirements(1L, LocalDateTime.now());

            if (!overdueRequirements.isEmpty()) {
                // Envoyer des alertes pour les exigences en retard
                System.out.println("Alertes de conformité: " + overdueRequirements.size() + " exigences en retard");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la surveillance quotidienne: " + e.getMessage());
        }
    }

    /**
     * Surveillance hebdomadaire des audits
     */
    // @Scheduled(cron = "0 0 10 * * MON") // Tous les lundis à 10h00 - Désactivé temporairement
    // @Async
    public void weeklyAuditMonitoring() {
        try {
            // Logique de surveillance hebdomadaire
            List<Audit> overdueAudits = auditRepository.findOverdueAudits(1L, LocalDateTime.now());

            if (!overdueAudits.isEmpty()) {
                // Envoyer des alertes pour les audits en retard
                System.out.println("Alertes d'audit: " + overdueAudits.size() + " audits en retard");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la surveillance hebdomadaire: " + e.getMessage());
        }
    }

    /**
     * Génération de rapports mensuels
     */
    // @Scheduled(cron = "0 0 8 1 * ?") // Le 1er de chaque mois à 8h00 - Désactivé temporairement
    // @Async
    public void monthlyReportGeneration() {
        try {
            // Logique de génération de rapports mensuels
            Map<String, Object> complianceStats = getComplianceStatistics(1L);
            Map<String, Object> auditStats = getAuditStatistics(1L);

            System.out.println("Rapport mensuel généré - Conformité: " + complianceStats.size() + " stats, Audits: " + auditStats.size() + " stats");
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du rapport mensuel: " + e.getMessage());
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Calculer la prochaine date de conformité
     */
    private LocalDateTime calculateNextComplianceDate(ComplianceRequirement.ComplianceFrequency frequency) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (frequency) {
            case DAILY:
                return now.plusDays(1);
            case WEEKLY:
                return now.plusWeeks(1);
            case MONTHLY:
                return now.plusMonths(1);
            case QUARTERLY:
                return now.plusMonths(3);
            case SEMI_ANNUAL:
                return now.plusMonths(6);
            case ANNUAL:
                return now.plusYears(1);
            case BIENNIAL:
                return now.plusYears(2);
            default:
                return now.plusMonths(1); // Par défaut mensuel
        }
    }
}






