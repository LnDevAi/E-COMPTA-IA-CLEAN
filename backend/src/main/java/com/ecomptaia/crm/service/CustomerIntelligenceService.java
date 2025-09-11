package com.ecomptaia.crm.service;

import com.ecomptaia.crm.dto.CustomerIntelligence;
import com.ecomptaia.crm.entity.CrmCustomer;
import com.ecomptaia.crm.entity.CrmCustomer.CustomerSegment;
import com.ecomptaia.crm.entity.CrmCustomer.PaymentBehavior;
import com.ecomptaia.crm.repository.CrmCustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Service d'intelligence artificielle pour l'analyse des clients
 * Calcule les scores, segments et prédictions basés sur le comportement
 */
@Service
@Slf4j
public class CustomerIntelligenceService {
    
    private final CrmCustomerRepository crmCustomerRepository;
    
    public CustomerIntelligenceService(CrmCustomerRepository crmCustomerRepository) {
        this.crmCustomerRepository = crmCustomerRepository;
    }

    /**
     * Calculer l'intelligence client complète
     */
    public CustomerIntelligence calculateCustomerIntelligence(Long customerId) {
        // log.info("Calcul de l'intelligence pour le client ID: {}", customerId);
        
        CrmCustomer customer = crmCustomerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Calculer le score IA global
        int aiScore = calculateAIScore(customer);
        
        // Déterminer le segment client
        CustomerSegment segment = determineCustomerSegment(customer);
        
        // Calculer la probabilité de churn
        BigDecimal churnProbability = calculateChurnProbability(customer);
        
        // Prédire la valeur vie client
        BigDecimal predictedLTV = calculatePredictedLTV(customer);
        
        // Analyser le comportement de paiement
        PaymentBehavior paymentBehavior = analyzePaymentBehavior(customer);
        
        // Calculer le score de satisfaction
        int satisfactionScore = calculateSatisfactionScore(customer);

        CustomerIntelligence intelligence = CustomerIntelligence.builder()
            .aiScore(aiScore)
            .segment(segment)
            .churnProbability(churnProbability)
            .predictedLTV(predictedLTV)
            .paymentBehavior(paymentBehavior)
            .satisfactionScore(satisfactionScore)
            .build();

        // log.info("Intelligence calculée - Score: {}, Segment: {}, Churn: {}", 
        //         aiScore, segment, churnProbability);
        
        return intelligence;
    }

    /**
     * Calculer le score IA global (0-100)
     */
    private int calculateAIScore(CrmCustomer customer) {
        int score = 0;
        
        // Score basé sur le revenu généré (40 points max)
        BigDecimal totalRevenue = customer.getTotalRevenueGenerated();
        if (totalRevenue.compareTo(BigDecimal.valueOf(10000)) > 0) {
            score += 40;
        } else if (totalRevenue.compareTo(BigDecimal.valueOf(5000)) > 0) {
            score += 30;
        } else if (totalRevenue.compareTo(BigDecimal.valueOf(1000)) > 0) {
            score += 20;
        } else if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            score += 10;
        }
        
        // Score basé sur la fréquence d'achat (30 points max)
        int purchaseFrequency = customer.getPurchaseFrequency().intValue();
        if (purchaseFrequency > 12) {
            score += 30;
        } else if (purchaseFrequency > 6) {
            score += 20;
        } else if (purchaseFrequency > 3) {
            score += 15;
        } else if (purchaseFrequency > 0) {
            score += 10;
        }
        
        // Score basé sur la récence (20 points max)
        LocalDateTime lastPurchase = customer.getLastPurchaseDate();
        if (lastPurchase != null) {
            long daysSinceLastPurchase = ChronoUnit.DAYS.between(lastPurchase, LocalDateTime.now());
            if (daysSinceLastPurchase <= 30) {
                score += 20;
            } else if (daysSinceLastPurchase <= 90) {
                score += 15;
            } else if (daysSinceLastPurchase <= 180) {
                score += 10;
            } else if (daysSinceLastPurchase <= 365) {
                score += 5;
            }
        }
        
        // Score basé sur le comportement de paiement (10 points max)
        if (customer.getAvgPaymentDelay() <= 0) {
            score += 10;
        } else if (customer.getAvgPaymentDelay() <= 7) {
            score += 8;
        } else if (customer.getAvgPaymentDelay() <= 15) {
            score += 5;
        } else if (customer.getAvgPaymentDelay() <= 30) {
            score += 2;
        }
        
        return Math.min(score, 100);
    }

    /**
     * Déterminer le segment client
     */
    private CustomerSegment determineCustomerSegment(CrmCustomer customer) {
        BigDecimal totalRevenue = customer.getTotalRevenueGenerated();
        int purchaseFrequency = customer.getPurchaseFrequency().intValue();
        
        // Segment VIP - Clients à haute valeur
        if (totalRevenue.compareTo(BigDecimal.valueOf(50000)) > 0 && purchaseFrequency > 10) {
            return CustomerSegment.VIP_HIGH_VALUE;
        }
        
        // Segment Premium - Clients réguliers avec bon revenu
        if (totalRevenue.compareTo(BigDecimal.valueOf(20000)) > 0 && purchaseFrequency > 5) {
            return CustomerSegment.STRATEGIC_ACCOUNT;
        }
        
        // Segment Regular - Clients actifs
        if (totalRevenue.compareTo(BigDecimal.valueOf(5000)) > 0 && purchaseFrequency > 2) {
            return CustomerSegment.STABLE_REGULAR;
        }
        
        // Segment At Risk - Clients avec risque de churn
        LocalDateTime lastPurchase = customer.getLastPurchaseDate();
        if (lastPurchase != null) {
            long daysSinceLastPurchase = ChronoUnit.DAYS.between(lastPurchase, LocalDateTime.now());
            if (daysSinceLastPurchase > 180) {
                return CustomerSegment.AT_RISK_CHURN;
            }
        }
        
        // Segment New - Nouveaux clients
        if (purchaseFrequency <= 1) {
            return CustomerSegment.OCCASIONAL_BUYER;
        }
        
        return CustomerSegment.STABLE_REGULAR;
    }

    /**
     * Calculer la probabilité de churn (0.0 - 1.0)
     */
    private BigDecimal calculateChurnProbability(CrmCustomer customer) {
        BigDecimal probability = BigDecimal.ZERO;
        
        // Facteur de récence
        LocalDateTime lastPurchase = customer.getLastPurchaseDate();
        if (lastPurchase != null) {
            long daysSinceLastPurchase = ChronoUnit.DAYS.between(lastPurchase, LocalDateTime.now());
            if (daysSinceLastPurchase > 365) {
                probability = probability.add(BigDecimal.valueOf(0.4));
            } else if (daysSinceLastPurchase > 180) {
                probability = probability.add(BigDecimal.valueOf(0.3));
            } else if (daysSinceLastPurchase > 90) {
                probability = probability.add(BigDecimal.valueOf(0.2));
            } else if (daysSinceLastPurchase > 30) {
                probability = probability.add(BigDecimal.valueOf(0.1));
            }
        } else {
            probability = probability.add(BigDecimal.valueOf(0.5));
        }
        
        // Facteur de fréquence d'achat
        int purchaseFrequency = customer.getPurchaseFrequency().intValue();
        if (purchaseFrequency == 0) {
            probability = probability.add(BigDecimal.valueOf(0.3));
        } else if (purchaseFrequency < 3) {
            probability = probability.add(BigDecimal.valueOf(0.2));
        }
        
        // Facteur de retard de paiement
        int avgPaymentDelay = customer.getAvgPaymentDelay();
        if (avgPaymentDelay > 30) {
            probability = probability.add(BigDecimal.valueOf(0.2));
        } else if (avgPaymentDelay > 15) {
            probability = probability.add(BigDecimal.valueOf(0.1));
        }
        
        // Facteur de revenu
        BigDecimal totalRevenue = customer.getTotalRevenueGenerated();
        if (totalRevenue.compareTo(BigDecimal.valueOf(1000)) < 0) {
            probability = probability.add(BigDecimal.valueOf(0.1));
        }
        
        return probability.min(BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Calculer la valeur vie client prédite
     */
    private BigDecimal calculatePredictedLTV(CrmCustomer customer) {
        BigDecimal totalRevenue = customer.getTotalRevenueGenerated();
        int purchaseFrequency = customer.getPurchaseFrequency().intValue();
        
        if (purchaseFrequency == 0) {
            return BigDecimal.ZERO;
        }
        
        // Calculer la valeur moyenne par transaction
        BigDecimal avgTransactionValue = totalRevenue.divide(
            BigDecimal.valueOf(purchaseFrequency), 2, RoundingMode.HALF_UP);
        
        // Prédire la fréquence annuelle future
        int predictedAnnualFrequency = Math.max(1, purchaseFrequency);
        
        // Prédire la durée de vie client (en années)
        int predictedLifespan = 3; // Par défaut 3 ans
        
        // Ajuster selon le segment
        CustomerSegment segment = customer.getCustomerSegment();
        switch (segment) {
            case VIP_HIGH_VALUE:
                predictedLifespan = 5;
                predictedAnnualFrequency = (int) (predictedAnnualFrequency * 1.2);
                break;
            case STRATEGIC_ACCOUNT:
                predictedLifespan = 4;
                predictedAnnualFrequency = (int) (predictedAnnualFrequency * 1.1);
                break;
            case AT_RISK_CHURN:
                predictedLifespan = 1;
                predictedAnnualFrequency = (int) (predictedAnnualFrequency * 0.5);
                break;
            case OCCASIONAL_BUYER:
                predictedLifespan = 2;
                predictedAnnualFrequency = (int) (predictedAnnualFrequency * 0.8);
                break;
            case PRICE_SENSITIVE:
                predictedLifespan = 2;
                predictedAnnualFrequency = (int) (predictedAnnualFrequency * 0.7);
                break;
            case GROWING_BUSINESS:
                predictedLifespan = 3;
                predictedAnnualFrequency = (int) (predictedAnnualFrequency * 1.0);
                break;
            case STABLE_REGULAR:
                predictedLifespan = 3;
                predictedAnnualFrequency = (int) (predictedAnnualFrequency * 1.0);
                break;
            case PAYMENT_DELAYED:
                predictedLifespan = 1;
                predictedAnnualFrequency = (int) (predictedAnnualFrequency * 0.6);
                break;
        }
        
        // Calculer la LTV prédite
        BigDecimal predictedLTV = avgTransactionValue
            .multiply(BigDecimal.valueOf(predictedAnnualFrequency))
            .multiply(BigDecimal.valueOf(predictedLifespan));
        
        return predictedLTV.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Analyser le comportement de paiement
     */
    private PaymentBehavior analyzePaymentBehavior(CrmCustomer customer) {
        int avgPaymentDelay = customer.getAvgPaymentDelay();
        
        if (avgPaymentDelay <= 0) {
            return PaymentBehavior.EARLY_PAYER;
        } else if (avgPaymentDelay <= 7) {
            return PaymentBehavior.PROMPT_PAYER;
        } else if (avgPaymentDelay <= 15) {
            return PaymentBehavior.REGULAR_DELAY;
        } else if (avgPaymentDelay <= 30) {
            return PaymentBehavior.NEGOTIATOR;
        } else {
            return PaymentBehavior.PROBLEMATIC_PAYER;
        }
    }

    /**
     * Calculer le score de satisfaction (0-100)
     */
    private int calculateSatisfactionScore(CrmCustomer customer) {
        int score = 50; // Score de base
        
        // Facteur de revenu
        BigDecimal totalRevenue = customer.getTotalRevenueGenerated();
        if (totalRevenue.compareTo(BigDecimal.valueOf(10000)) > 0) {
            score += 20;
        } else if (totalRevenue.compareTo(BigDecimal.valueOf(5000)) > 0) {
            score += 15;
        } else if (totalRevenue.compareTo(BigDecimal.valueOf(1000)) > 0) {
            score += 10;
        }
        
        // Facteur de fréquence
        int purchaseFrequency = customer.getPurchaseFrequency().intValue();
        if (purchaseFrequency > 10) {
            score += 15;
        } else if (purchaseFrequency > 5) {
            score += 10;
        } else if (purchaseFrequency > 2) {
            score += 5;
        }
        
        // Facteur de récence
        LocalDateTime lastPurchase = customer.getLastPurchaseDate();
        if (lastPurchase != null) {
            long daysSinceLastPurchase = ChronoUnit.DAYS.between(lastPurchase, LocalDateTime.now());
            if (daysSinceLastPurchase <= 30) {
                score += 15;
            } else if (daysSinceLastPurchase <= 90) {
                score += 10;
            } else if (daysSinceLastPurchase <= 180) {
                score += 5;
            } else {
                score -= 10;
            }
        }
        
        // Facteur de paiement
        int avgPaymentDelay = customer.getAvgPaymentDelay();
        if (avgPaymentDelay <= 0) {
            score += 10;
        } else if (avgPaymentDelay <= 7) {
            score += 5;
        } else if (avgPaymentDelay > 30) {
            score -= 15;
        }
        
        return Math.max(0, Math.min(100, score));
    }
}
