package com.ecomptaia.entity;

import com.ecomptaia.security.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * EntitÃ© pour les workflows intelligents automatisÃ©s par l'IA
 * RÃ©volutionnaire vs TOMPRO - Automatisation intelligente
 */
@Entity
@Table(name = "ai_workflows")
public class AIWorkflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column
    private String workflowName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type")
    private WorkflowTrigger trigger;
    
    @Column
    private Boolean isActive = true;
    
    @Column
    private Integer successRate = 0;
    
    @Column
    private String version = "v1";
    
    @Column
    private Integer executionCount = 0;
    
    @Column
    private Integer failureCount = 0;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime nextExecution;
    
    @Column
    private LocalDateTime lastExecuted;
    
    public enum WorkflowTrigger {
        SCHEDULED, EVENT, MANUAL
    }
}




