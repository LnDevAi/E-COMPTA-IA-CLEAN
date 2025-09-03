package com.ecomptaia.service;

import com.ecomptaia.entity.Risk;
import com.ecomptaia.entity.RiskAssessment;
import com.ecomptaia.repository.RiskRepository;
import com.ecomptaia.repository.RiskAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
@SuppressWarnings("unused")
import java.math.BigDecimal;

@Service
@Transactional
public class RiskManagementService {

    @Autowired
    private RiskRepository riskRepository;

    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    // ==================== GESTION DES RISQUES ====================

    /**
     * Créer un nouveau risque
     */
    public Risk createRisk(String riskName, Risk.RiskCategory riskCategory, Risk.RiskType riskType,
                          Long entrepriseId, String description) {
        Risk risk = new Risk(riskName, riskCategory, riskType, entrepriseId);
        risk.setRiskDescription(description);
        return riskRepository.save(risk);
    }

    /**
     * Mettre à jour un risque
     */
    public Risk updateRisk(Long riskId, Risk riskDetails) {
        Risk risk = riskRepository.findById(riskId)
                .orElseThrow(() -> new RuntimeException("Risque non trouvé"));

        if (riskDetails.getRiskName() != null) {
            risk.setRiskName(riskDetails.getRiskName());
        }
        if (riskDetails.getRiskDescription() != null) {
            risk.setRiskDescription(riskDetails.getRiskDescription());
        }
        if (riskDetails.getRiskCategory() != null) {
            risk.setRiskCategory(riskDetails.getRiskCategory());
        }
        if (riskDetails.getRiskType() != null) {
            risk.setRiskType(riskDetails.getRiskType());
        }
        if (riskDetails.getProbabilityScore() != null) {
            risk.setProbabilityScore(riskDetails.getProbabilityScore());
        }
        if (riskDetails.getImpactScore() != null) {
            risk.setImpactScore(riskDetails.getImpactScore());
        }
        if (riskDetails.getEstimatedFinancialImpact() != null) {
            risk.setEstimatedFinancialImpact(riskDetails.getEstimatedFinancialImpact());
        }
        if (riskDetails.getCurrency() != null) {
            risk.setCurrency(riskDetails.getCurrency());
        }
        if (riskDetails.getMitigationPlan() != null) {
            risk.setMitigationPlan(riskDetails.getMitigationPlan());
        }
        if (riskDetails.getContingencyPlan() != null) {
            risk.setContingencyPlan(riskDetails.getContingencyPlan());
        }
        if (riskDetails.getResponsiblePerson() != null) {
            risk.setResponsiblePerson(riskDetails.getResponsiblePerson());
        }
        if (riskDetails.getDueDate() != null) {
            risk.setDueDate(riskDetails.getDueDate());
        }

        risk.setUpdatedAt(LocalDateTime.now());
        return riskRepository.save(risk);
    }

    /**
     * Activer un risque
     */
    public Risk activateRisk(Long riskId) {
        Risk risk = riskRepository.findById(riskId)
                .orElseThrow(() -> new RuntimeException("Risque non trouvé"));
        risk.setIsActive(true);
        risk.setUpdatedAt(LocalDateTime.now());
        return riskRepository.save(risk);
    }

    /**
     * Désactiver un risque
     */
    public Risk deactivateRisk(Long riskId) {
        Risk risk = riskRepository.findById(riskId)
                .orElseThrow(() -> new RuntimeException("Risque non trouvé"));
        risk.setIsActive(false);
        risk.setUpdatedAt(LocalDateTime.now());
        return riskRepository.save(risk);
    }

    /**
     * Marquer un risque comme nécessitant une action immédiate
     */
    public Risk markAsUrgent(Long riskId) {
        Risk risk = riskRepository.findById(riskId)
                .orElseThrow(() -> new RuntimeException("Risque non trouvé"));
        risk.setRequiresImmediateAction(true);
        risk.setUpdatedAt(LocalDateTime.now());
        return riskRepository.save(risk);
    }

    // ==================== GESTION DES ÉVALUATIONS ====================

    /**
     * Créer une nouvelle évaluation de risque
     */
    public RiskAssessment createAssessment(Long riskId, Long assessorId, RiskAssessment.AssessmentType assessmentType,
                                         Long entrepriseId) {
        RiskAssessment assessment = new RiskAssessment(riskId, assessorId, assessmentType, entrepriseId);
        return riskAssessmentRepository.save(assessment);
    }

    /**
     * Compléter une évaluation de risque
     */
    public RiskAssessment completeAssessment(Long assessmentId, Integer probabilityScore, Integer impactScore,
                                           String assessmentNotes, String recommendations) {
        RiskAssessment assessment = riskAssessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));

        assessment.setProbabilityScore(probabilityScore);
        assessment.setImpactScore(impactScore);
        assessment.setAssessmentNotes(assessmentNotes);
        assessment.setRecommendations(recommendations);
        assessment.setAssessmentStatus(RiskAssessment.AssessmentStatus.COMPLETED);
        assessment.setUpdatedAt(LocalDateTime.now());

        // Calculer le score de risque
        assessment.calculateRiskScore();

        // Mettre à jour le risque avec les nouvelles données
        updateRiskFromAssessment(assessment);

        return riskAssessmentRepository.save(assessment);
    }

    /**
     * Approuver une évaluation
     */
    public RiskAssessment approveAssessment(Long assessmentId, Long approvedBy, String approvalNotes) {
        RiskAssessment assessment = riskAssessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));

        assessment.setApprovalStatus(RiskAssessment.ApprovalStatus.APPROVED);
        assessment.setApprovedBy(approvedBy);
        assessment.setApprovalDate(LocalDateTime.now());
        assessment.setApprovalNotes(approvalNotes);
        assessment.setUpdatedAt(LocalDateTime.now());

        return riskAssessmentRepository.save(assessment);
    }

    /**
     * Rejeter une évaluation
     */
    public RiskAssessment rejectAssessment(Long assessmentId, Long rejectedBy, String rejectionNotes) {
        RiskAssessment assessment = riskAssessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));

        assessment.setApprovalStatus(RiskAssessment.ApprovalStatus.REJECTED);
        assessment.setApprovedBy(rejectedBy);
        assessment.setApprovalDate(LocalDateTime.now());
        assessment.setApprovalNotes(rejectionNotes);
        assessment.setUpdatedAt(LocalDateTime.now());

        return riskAssessmentRepository.save(assessment);
    }

    /**
     * Mettre à jour un risque basé sur une évaluation
     */
    private void updateRiskFromAssessment(RiskAssessment assessment) {
        Risk risk = riskRepository.findById(assessment.getRiskId())
                .orElseThrow(() -> new RuntimeException("Risque non trouvé"));

        risk.setProbabilityScore(assessment.getProbabilityScore());
        risk.setImpactScore(assessment.getImpactScore());
        risk.setRiskScore(assessment.getRiskScore());
        risk.setRiskLevel(assessment.getRiskLevel());
        risk.setLastAssessmentDate(assessment.getAssessmentDate());
        risk.setNextAssessmentDate(assessment.getNextAssessmentDate());
        risk.setUpdatedAt(LocalDateTime.now());

        riskRepository.save(risk);
    }

    // ==================== ANALYSE ET SURVEILLANCE ====================

    /**
     * Obtenir les statistiques des risques
     */
    public Map<String, Object> getRiskStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalRisks", riskRepository.countActiveRisksByEntreprise(entrepriseId));
        stats.put("highRiskRisks", riskRepository.countRisksByLevel(entrepriseId, Risk.RiskLevel.HIGH));
        stats.put("veryHighRiskRisks", riskRepository.countRisksByLevel(entrepriseId, Risk.RiskLevel.VERY_HIGH));
        stats.put("criticalRisks", riskRepository.countRisksByLevel(entrepriseId, Risk.RiskLevel.CRITICAL));
        stats.put("urgentRisks", riskRepository.findUrgentRisks(entrepriseId).size());
        stats.put("unmitigatedRisks", riskRepository.countRisksByMitigationStatus(entrepriseId, Risk.MitigationStatus.NOT_STARTED));
        stats.put("averageRiskScore", riskRepository.getAverageRiskScore(entrepriseId));
        stats.put("maxRiskScore", riskRepository.getMaxRiskScore(entrepriseId));

        return stats;
    }

    /**
     * Obtenir les statistiques des évaluations
     */
    public Map<String, Object> getAssessmentStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalAssessments", riskAssessmentRepository.countCompletedAssessments(entrepriseId));
        stats.put("pendingApprovals", riskAssessmentRepository.findPendingApprovalAssessments(entrepriseId).size());
        stats.put("nonCompliantAssessments", riskAssessmentRepository.findNonCompliantAssessments(entrepriseId).size());
        stats.put("averageRiskScore", riskAssessmentRepository.getAverageRiskScore(entrepriseId));
        stats.put("averageResidualRiskScore", riskAssessmentRepository.getAverageResidualRiskScore(entrepriseId));
        stats.put("averageComplianceScore", riskAssessmentRepository.getAverageComplianceScore(entrepriseId));
        stats.put("averageMitigationEffectiveness", riskAssessmentRepository.getAverageMitigationEffectiveness(entrepriseId));

        return stats;
    }

    /**
     * Obtenir les risques critiques nécessitant une attention immédiate
     */
    public List<Risk> getCriticalRisks(Long entrepriseId) {
        return riskRepository.findCriticalUnmitigatedRisks(entrepriseId);
    }

    /**
     * Obtenir les risques en retard
     */
    public List<Risk> getOverdueRisks(Long entrepriseId) {
        return riskRepository.findOverdueRisks(entrepriseId, LocalDateTime.now());
    }

    /**
     * Obtenir les risques nécessitant une évaluation
     */
    public List<Risk> getRisksNeedingAssessment(Long entrepriseId) {
        return riskRepository.findRisksNeedingAssessment(entrepriseId, LocalDateTime.now());
    }

    /**
     * Obtenir les évaluations en attente d'approbation
     */
    public List<RiskAssessment> getPendingApprovalAssessments(Long entrepriseId) {
        return riskAssessmentRepository.findPendingApprovalAssessments(entrepriseId);
    }

    // ==================== TÂCHES AUTOMATISÉES ====================

    /**
     * Vérifier les risques en retard et envoyer des alertes
     */
    @Scheduled(fixedRate = 3600000) // Toutes les heures
    public void checkOverdueRisks() {
        LocalDateTime now = LocalDateTime.now();
        List<Risk> overdueRisks = riskRepository.findOverdueRisks(1L, now);

        for (Risk risk : overdueRisks) {
            sendOverdueRiskAlert(risk);
        }
    }

    /**
     * Vérifier les évaluations nécessitant une révision
     */
    @Scheduled(fixedRate = 7200000) // Toutes les 2 heures
    public void checkAssessmentsNeedingReview() {
        LocalDateTime now = LocalDateTime.now();
        List<RiskAssessment> assessmentsNeedingReview = riskAssessmentRepository.findAssessmentsNeedingReview(1L, now);

        for (RiskAssessment assessment : assessmentsNeedingReview) {
            sendAssessmentReviewReminder(assessment);
        }
    }

    /**
     * Analyser les tendances des risques
     */
    @Scheduled(cron = "0 0 8 * * ?") // Tous les jours à 8h
    public void analyzeRiskTrends() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<RiskAssessment> recentAssessments = riskAssessmentRepository.findAssessmentsByDateRange(1L, oneMonthAgo, LocalDateTime.now());

        Map<String, Object> trendAnalysis = analyzeTrends(recentAssessments);
        generateTrendReport(trendAnalysis);
    }

    /**
     * Vérifier la conformité des risques
     */
    @Scheduled(cron = "0 0 9 * * MON") // Tous les lundis à 9h
    public void checkComplianceStatus() {
        List<RiskAssessment> lowComplianceAssessments = riskAssessmentRepository.findLowComplianceAssessments(1L);

        for (RiskAssessment assessment : lowComplianceAssessments) {
            sendComplianceAlert(assessment);
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Envoyer une alerte pour un risque en retard
     */
    @Async
    private CompletableFuture<Void> sendOverdueRiskAlert(Risk risk) {
        // Simulation d'envoi d'alerte
        System.out.println("ALERTE: Risque en retard - " + risk.getRiskName() + " (ID: " + risk.getId() + ")");
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Envoyer un rappel pour une évaluation nécessitant une révision
     */
    @Async
    private CompletableFuture<Void> sendAssessmentReviewReminder(RiskAssessment assessment) {
        // Simulation d'envoi de rappel
        System.out.println("RAPPEL: Évaluation nécessitant une révision - ID: " + assessment.getId());
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Analyser les tendances des risques
     */
    private Map<String, Object> analyzeTrends(List<RiskAssessment> assessments) {
        Map<String, Object> trends = new HashMap<>();

        if (assessments.isEmpty()) {
            return trends;
        }

        // Calculer les moyennes
        double avgRiskScore = assessments.stream()
                .mapToInt(RiskAssessment::getRiskScore)
                .average()
                .orElse(0.0);

        double avgResidualRiskScore = assessments.stream()
                .mapToInt(RiskAssessment::getResidualRiskScore)
                .average()
                .orElse(0.0);

        // Analyser les tendances
        String riskTrend = avgRiskScore > 12 ? "AUGMENTANT" : avgRiskScore < 8 ? "DIMINUANT" : "STABLE";
        String residualTrend = avgResidualRiskScore > 8 ? "AUGMENTANT" : avgResidualRiskScore < 4 ? "DIMINUANT" : "STABLE";

        trends.put("averageRiskScore", avgRiskScore);
        trends.put("averageResidualRiskScore", avgResidualRiskScore);
        trends.put("riskTrend", riskTrend);
        trends.put("residualRiskTrend", residualTrend);
        trends.put("assessmentCount", assessments.size());

        return trends;
    }

    /**
     * Générer un rapport de tendances
     */
    @Async
    private CompletableFuture<Void> generateTrendReport(Map<String, Object> trendAnalysis) {
        // Simulation de génération de rapport
        System.out.println("RAPPORT DE TENDANCES: " + trendAnalysis);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Envoyer une alerte de conformité
     */
    @Async
    private CompletableFuture<Void> sendComplianceAlert(RiskAssessment assessment) {
        // Simulation d'envoi d'alerte de conformité
        System.out.println("ALERTE CONFORMITÉ: Évaluation ID " + assessment.getId() + " - Score: " + assessment.getComplianceScore());
        return CompletableFuture.completedFuture(null);
    }

    // ==================== MÉTHODES DE RECHERCHE ====================

    /**
     * Rechercher des risques par mot-clé
     */
    public List<Risk> searchRisks(Long entrepriseId, String keyword) {
        return riskRepository.searchRisksByKeyword(entrepriseId, keyword);
    }

    /**
     * Rechercher des évaluations par mot-clé
     */
    public List<RiskAssessment> searchAssessments(Long entrepriseId, String keyword) {
        return riskAssessmentRepository.searchAssessmentsByKeyword(entrepriseId, keyword);
    }

    /**
     * Obtenir les risques par catégorie
     */
    public List<Risk> getRisksByCategory(Long entrepriseId, Risk.RiskCategory category) {
        return riskRepository.findByEntrepriseIdAndRiskCategory(entrepriseId, category);
    }

    /**
     * Obtenir les risques par niveau
     */
    public List<Risk> getRisksByLevel(Long entrepriseId, Risk.RiskLevel level) {
        return riskRepository.findByEntrepriseIdAndRiskLevel(entrepriseId, level);
    }

    /**
     * Obtenir les évaluations d'un risque
     */
    public List<RiskAssessment> getRiskAssessments(Long riskId) {
        return riskAssessmentRepository.findByRiskIdOrderByAssessmentDateDesc(riskId);
    }

    /**
     * Obtenir les évaluations par type
     */
    public List<RiskAssessment> getAssessmentsByType(Long entrepriseId, RiskAssessment.AssessmentType type) {
        return riskAssessmentRepository.findByEntrepriseIdAndAssessmentType(entrepriseId, type);
    }
}
