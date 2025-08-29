package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "expansion_analyses")
public class ExpansionAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3)
    private String countryCode; // Code du pays analysé

    @Column(nullable = false, length = 100)
    private String countryName; // Nom du pays

    @Column(nullable = false)
    private Integer digitalMaturityScore; // Score de maturité digitale (0-100)

    @Column(nullable = false)
    private Integer economicScore; // Score économique (0-100)

    @Column(nullable = false)
    private Integer apiAvailabilityScore; // Score disponibilité APIs (0-100)

    @Column(nullable = false)
    private Integer integrationComplexityScore; // Score complexité intégration (0-100)

    @Column(nullable = false)
    private Integer totalScore; // Score total pondéré

    @Column(length = 20)
    private String recommendation; // RECOMMENDED, CONSIDER, NOT_RECOMMENDED

    @Column(length = 1000)
    private String analysisDetails; // Détails de l'analyse (JSON)

    @Column(length = 500)
    private String marketOpportunities; // Opportunités de marché

    @Column(length = 500)
    private String challenges; // Défis et obstacles

    @Column(length = 500)
    private String nextSteps; // Prochaines étapes recommandées

    @Column(nullable = false)
    private LocalDateTime analysisDate;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private LocalDateTime updatedAt;

    // Constructeurs
    public ExpansionAnalysis() {}

    public ExpansionAnalysis(String countryCode, String countryName, 
                           Integer digitalMaturityScore, Integer economicScore,
                           Integer apiAvailabilityScore, Integer integrationComplexityScore) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.digitalMaturityScore = digitalMaturityScore;
        this.economicScore = economicScore;
        this.apiAvailabilityScore = apiAvailabilityScore;
        this.integrationComplexityScore = integrationComplexityScore;
        this.totalScore = calculateTotalScore();
        this.recommendation = generateRecommendation();
        this.analysisDate = LocalDateTime.now();
        this.isActive = true;
    }

    // Méthodes privées
    private Integer calculateTotalScore() {
        // Pondération : Digital 30%, Economic 25%, API 25%, Integration 20%
        return (int) Math.round(
            (digitalMaturityScore * 0.30) +
            (economicScore * 0.25) +
            (apiAvailabilityScore * 0.25) +
            (integrationComplexityScore * 0.20)
        );
    }

    private String generateRecommendation() {
        if (totalScore >= 80) return "RECOMMENDED";
        if (totalScore >= 60) return "CONSIDER";
        return "NOT_RECOMMENDED";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public Integer getDigitalMaturityScore() { return digitalMaturityScore; }
    public void setDigitalMaturityScore(Integer digitalMaturityScore) { 
        this.digitalMaturityScore = digitalMaturityScore;
        this.totalScore = calculateTotalScore();
        this.recommendation = generateRecommendation();
    }

    public Integer getEconomicScore() { return economicScore; }
    public void setEconomicScore(Integer economicScore) { 
        this.economicScore = economicScore;
        this.totalScore = calculateTotalScore();
        this.recommendation = generateRecommendation();
    }

    public Integer getApiAvailabilityScore() { return apiAvailabilityScore; }
    public void setApiAvailabilityScore(Integer apiAvailabilityScore) { 
        this.apiAvailabilityScore = apiAvailabilityScore;
        this.totalScore = calculateTotalScore();
        this.recommendation = generateRecommendation();
    }

    public Integer getIntegrationComplexityScore() { return integrationComplexityScore; }
    public void setIntegrationComplexityScore(Integer integrationComplexityScore) { 
        this.integrationComplexityScore = integrationComplexityScore;
        this.totalScore = calculateTotalScore();
        this.recommendation = generateRecommendation();
    }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getAnalysisDetails() { return analysisDetails; }
    public void setAnalysisDetails(String analysisDetails) { this.analysisDetails = analysisDetails; }

    public String getMarketOpportunities() { return marketOpportunities; }
    public void setMarketOpportunities(String marketOpportunities) { this.marketOpportunities = marketOpportunities; }

    public String getChallenges() { return challenges; }
    public void setChallenges(String challenges) { this.challenges = challenges; }

    public String getNextSteps() { return nextSteps; }
    public void setNextSteps(String nextSteps) { this.nextSteps = nextSteps; }

    public LocalDateTime getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ExpansionAnalysis{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", totalScore=" + totalScore +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}




