ackage com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * EntitÃ© pour les rÃ©sultats de synchronisation
 */
@Entity
@Table(name = "sync_results")
public class SyncResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id")
    private AdvancedIntegration integration;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private Boolean success;
    
    @Column(nullable = false)
    private Integer recordsProcessed;
    
    @Column(nullable = false)
    private Integer recordsFailed;
    
    @Column(nullable = false)
    private Long duration; // en millisecondes
    
    @ElementCollection
    @CollectionTable(name = "sync_errors", joinColumns = @JoinColumn(name = "sync_result_id"))
    @Column(name = "error", columnDefinition = "TEXT")
    private List<String> errors = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "sync_warnings", joinColumns = @JoinColumn(name = "sync_result_id"))
    @Column(name = "warning", columnDefinition = "TEXT")
    private List<String> warnings = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public AdvancedIntegration getIntegration() { return integration; }
    public void setIntegration(AdvancedIntegration integration) { this.integration = integration; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    
    public Integer getRecordsProcessed() { return recordsProcessed; }
    public void setRecordsProcessed(Integer recordsProcessed) { this.recordsProcessed = recordsProcessed; }
    
    public Integer getRecordsFailed() { return recordsFailed; }
    public void setRecordsFailed(Integer recordsFailed) { this.recordsFailed = recordsFailed; }
    
    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}




