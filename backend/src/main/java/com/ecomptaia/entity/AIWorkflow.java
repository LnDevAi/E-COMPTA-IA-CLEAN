package com.ecomptaia.entity;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.security.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}




