ackage com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EntitÃ© pour l'exÃ©cution des workflows
 */
@Entity
@Table(name = "workflow_executions")
public class WorkflowExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private AIWorkflow workflow;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExecutionStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String inputData; // DonnÃ©es d'entrÃ©e en JSON
    
    @Column(columnDefinition = "TEXT")
    private String outputData; // DonnÃ©es de sortie en JSON
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column
    private Long duration; // en millisecondes
    
    @Column(columnDefinition = "TEXT")
    private String executionLog; // Log d'exÃ©cution
    
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    // Ã‰numÃ©rations
    public enum ExecutionStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED, TIMEOUT
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public AIWorkflow getWorkflow() { return workflow; }
    public void setWorkflow(AIWorkflow workflow) { this.workflow = workflow; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }
    
    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }
    
    public String getOutputData() { return outputData; }
    public void setOutputData(String outputData) { this.outputData = outputData; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }
    
    public String getExecutionLog() { return executionLog; }
    public void setExecutionLog(String executionLog) { this.executionLog = executionLog; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}




