ackage com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EntitÃ© pour l'historique du statut des intÃ©grations
 */
@Entity
@Table(name = "integration_status_history")
public class IntegrationStatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id")
    private AdvancedIntegration integration;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdvancedIntegration.IntegrationStatus status;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public AdvancedIntegration getIntegration() { return integration; }
    public void setIntegration(AdvancedIntegration integration) { this.integration = integration; }
    
    public AdvancedIntegration.IntegrationStatus getStatus() { return status; }
    public void setStatus(AdvancedIntegration.IntegrationStatus status) { this.status = status; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}




