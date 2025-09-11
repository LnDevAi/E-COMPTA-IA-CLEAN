ackage com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * EntitÃ© pour les modÃ¨les ML utilisÃ©s par l'assistant
 */
@Entity
@Table(name = "ai_models")
public class AIModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String version;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModelType type;
    
    @Column(nullable = false, length = 200)
    private String description;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal accuracy;
    
    @Column(nullable = false)
    private LocalDateTime lastTrained;
    
    @ElementCollection
    @CollectionTable(name = "model_features", joinColumns = @JoinColumn(name = "model_id"))
    @Column(name = "feature", length = 100)
    private List<String> features = new ArrayList<>();
    
    @Column(nullable = false)
    private Integer predictions = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModelStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String configuration; // Configuration du modÃ¨le en JSON
    
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    // Ã‰numÃ©rations
    public enum ModelType {
        REGRESSION, CLASSIFICATION, CLUSTERING, TIME_SERIES, NLP, RECOMMENDATION
    }
    
    public enum ModelStatus {
        ACTIVE, TRAINING, ERROR, DEPRECATED, MAINTENANCE
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public ModelType getType() { return type; }
    public void setType(ModelType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getAccuracy() { return accuracy; }
    public void setAccuracy(BigDecimal accuracy) { this.accuracy = accuracy; }
    
    public LocalDateTime getLastTrained() { return lastTrained; }
    public void setLastTrained(LocalDateTime lastTrained) { this.lastTrained = lastTrained; }
    
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    
    public Integer getPredictions() { return predictions; }
    public void setPredictions(Integer predictions) { this.predictions = predictions; }
    
    public ModelStatus getStatus() { return status; }
    public void setStatus(ModelStatus status) { this.status = status; }
    
    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}




