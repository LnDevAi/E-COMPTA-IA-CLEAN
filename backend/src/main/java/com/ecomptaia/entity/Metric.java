package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metrics")
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Column(name = "metric_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    @Column(name = "metric_value", nullable = false)
    private Double value;

    @Column(name = "unit")
    private String unit;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private MetricCategory category;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "description")
    private String description;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Column(name = "threshold_warning")
    private Double thresholdWarning;

    @Column(name = "threshold_critical")
    private Double thresholdCritical;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MetricStatus status;

    @Column(name = "entreprise_id")
    private Long entrepriseId;

    @Column(name = "utilisateur_id")
    private Long utilisateurId;

    // ==================== ENUMS ====================

    public enum MetricType {
        COUNTER("Compteur"),
        GAUGE("Jauge"),
        HISTOGRAM("Histogramme"),
        TIMER("Minuteur");

        private final String description;

        MetricType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum MetricCategory {
        SYSTEM("Système"),
        APPLICATION("Application"),
        DATABASE("Base de données"),
        NETWORK("Réseau"),
        MEMORY("Mémoire"),
        CPU("Processeur"),
        DISK("Disque"),
        BUSINESS("Métier");

        private final String description;

        MetricCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum MetricStatus {
        NORMAL("Normal"),
        WARNING("Avertissement"),
        CRITICAL("Critique"),
        UNKNOWN("Inconnu");

        private final String description;

        MetricStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public Metric() {
        this.timestamp = LocalDateTime.now();
        this.status = MetricStatus.NORMAL;
    }

    public Metric(String metricName, MetricType metricType, Double value, MetricCategory category, String source) {
        this();
        this.metricName = metricName;
        this.metricType = metricType;
        this.value = value;
        this.category = category;
        this.source = source;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public MetricCategory getCategory() {
        return category;
    }

    public void setCategory(MetricCategory category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Double getThresholdWarning() {
        return thresholdWarning;
    }

    public void setThresholdWarning(Double thresholdWarning) {
        this.thresholdWarning = thresholdWarning;
    }

    public Double getThresholdCritical() {
        return thresholdCritical;
    }

    public void setThresholdCritical(Double thresholdCritical) {
        this.thresholdCritical = thresholdCritical;
    }

    public MetricStatus getStatus() {
        return status;
    }

    public void setStatus(MetricStatus status) {
        this.status = status;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
}
