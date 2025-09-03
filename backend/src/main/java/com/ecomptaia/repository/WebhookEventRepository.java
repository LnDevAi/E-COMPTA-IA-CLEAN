package com.ecomptaia.repository;

import com.ecomptaia.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {

    // Trouver par entreprise
    List<WebhookEvent> findByEntrepriseId(Long entrepriseId);

    // Trouver par statut
    List<WebhookEvent> findByStatus(WebhookEvent.EventStatus status);

    // Trouver par entreprise et statut
    List<WebhookEvent> findByEntrepriseIdAndStatus(Long entrepriseId, WebhookEvent.EventStatus status);

    // Trouver par type d'événement
    List<WebhookEvent> findByEventType(String eventType);

    // Trouver par source d'événement
    List<WebhookEvent> findByEventSource(String eventSource);

    // Trouver les événements en attente de traitement
    List<WebhookEvent> findByStatusAndRetryCountLessThan(WebhookEvent.EventStatus status, Integer maxRetries);

    // Trouver les événements qui doivent être retentés
    @Query("SELECT we FROM WebhookEvent we WHERE we.status = 'RETRY' AND we.nextRetry <= :now")
    List<WebhookEvent> findEventsToRetry(@Param("now") LocalDateTime now);

    // Trouver les événements récents
    @Query("SELECT we FROM WebhookEvent we WHERE we.entrepriseId = :entrepriseId ORDER BY we.createdAt DESC")
    List<WebhookEvent> findRecentEvents(@Param("entrepriseId") Long entrepriseId);

    // Compter par entreprise
    Long countByEntrepriseId(Long entrepriseId);

    // Compter par statut
    Long countByStatus(WebhookEvent.EventStatus status);

    // Compter par entreprise et statut
    Long countByEntrepriseIdAndStatus(Long entrepriseId, WebhookEvent.EventStatus status);

    // Trouver les événements échoués
    @Query("SELECT we FROM WebhookEvent we WHERE we.status = 'FAILED' ORDER BY we.createdAt DESC")
    List<WebhookEvent> findFailedEvents();

    // Trouver par URL webhook
    List<WebhookEvent> findByWebhookUrl(String webhookUrl);

    // Trouver les événements par période
    @Query("SELECT we FROM WebhookEvent we WHERE we.entrepriseId = :entrepriseId AND we.createdAt BETWEEN :startDate AND :endDate")
    List<WebhookEvent> findEventsByPeriod(@Param("entrepriseId") Long entrepriseId, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
}





