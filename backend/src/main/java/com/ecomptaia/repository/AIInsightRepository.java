package com.ecomptaia.repository;

import com.ecomptaia.entity.AIInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour les insights IA
 * Révolutionnaire vs TOMPRO - IA native intégrée
 */
@Repository
public interface AIInsightRepository extends JpaRepository<AIInsight, Long> {
}


