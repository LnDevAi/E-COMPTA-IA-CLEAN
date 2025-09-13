package com.ecomptaia.entity;

import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * EntitÃ© pour l'assistant IA intÃ©grÃ©
 * RÃ©volutionnaire vs TOMPRO - Assistant IA 24/7
 */
@Entity
@Table(name = "ai_assistants")
public class AIAssistant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    @Column
    private String name;
    
    @Column
    private String model;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    // Champs de message
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MessageType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column
    private LocalDateTime timestamp;

    @Column
    private Boolean isFromUser;

    @Column
    private Integer confidence;

    @ElementCollection
    @CollectionTable(name = "ai_assistant_related_insights", joinColumns = @JoinColumn(name = "assistant_id"))
    @Column(name = "insight_id")
    private List<Long> relatedInsights = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    // Constructeurs
    public AIAssistant() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AIAssistant(MessageType type, String content, Boolean isFromUser) {
        this();
        this.type = type;
        this.content = content;
        this.isFromUser = isFromUser;
    }
    
    // Ã‰numÃ©rations
    public enum MessageType {
        QUESTION, ANSWER, SUGGESTION, ALERT, NOTIFICATION, COMMAND, RESPONSE
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Boolean getIsFromUser() { return isFromUser; }
    public void setIsFromUser(Boolean isFromUser) { this.isFromUser = isFromUser; }
    
    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }
    
    public List<Long> getRelatedInsights() { return relatedInsights; }
    public void setRelatedInsights(List<Long> relatedInsights) { this.relatedInsights = relatedInsights; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    @Override
    public String toString() {
        return "AIAssistant{" +
                "id=" + id +
                ", type=" + type +
                ", isFromUser=" + isFromUser +
                ", timestamp=" + timestamp +
                ", confidence=" + confidence +
                '}';
    }
}




