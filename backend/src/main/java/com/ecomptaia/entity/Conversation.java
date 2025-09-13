package com.ecomptaia.entity;

import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * EntitÃ© pour les conversations avec l'assistant IA
 */
@Entity
@Table(name = "ai_conversations")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConversationStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime lastMessageAt;
    
    @Column
    private LocalDateTime closedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AIAssistant> messages = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String context; // Contexte de la conversation
    
    @Column(columnDefinition = "TEXT")
    private String summary; // RÃ©sumÃ© de la conversation
    
    // Ã‰numÃ©rations
    public enum ConversationStatus {
        ACTIVE, CLOSED, ARCHIVED, SUSPENDED
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ConversationStatus getStatus() { return status; }
    public void setStatus(ConversationStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public List<AIAssistant> getMessages() { return messages; }
    public void setMessages(List<AIAssistant> messages) { this.messages = messages; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}





