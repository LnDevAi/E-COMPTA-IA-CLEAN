package com.ecomptaia.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Service d'analyse intelligente des documents comptables
 */
@Service
public class AIDocumentAnalysisService {

    // Types de documents supportés
    private static final List<String> SUPPORTED_DOCUMENT_TYPES = Arrays.asList(
        "FACTURE", "RECU", "BON_LIVRAISON", "DEVIS", "BORDEREAU", "TICKET_CAISSE"
    );

    // Règles d'extraction par type de document
    private static final Map<String, Map<String, Object>> DOCUMENT_RULES = Map.of(
        "FACTURE", Map.of(
            "requiredFields", Arrays.asList("numero", "date", "montant_ht", "tva", "montant_ttc", "fournisseur"),
            "optionalFields", Arrays.asList("devise", "conditions_paiement", "reference"),
            "confidenceThreshold", 0.85
        ),
        "RECU", Map.of(
            "requiredFields", Arrays.asList("numero", "date", "montant", "commercant"),
            "optionalFields", Arrays.asList("devise", "methode_paiement"),
            "confidenceThreshold", 0.80
        ),
        "BON_LIVRAISON", Map.of(
            "requiredFields", Arrays.asList("numero", "date", "fournisseur", "articles"),
            "optionalFields", Arrays.asList("transporteur", "conditions"),
            "confidenceThreshold", 0.75
        )
    );

    /**
     * Analyser un document et extraire les informations comptables
     */
    public Map<String, Object> analyzeDocument(String documentContent, String documentType, String countryCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Validation du type de document
            if (!SUPPORTED_DOCUMENT_TYPES.contains(documentType.toUpperCase())) {
                result.put("error", "Type de document non supporté: " + documentType);
                result.put("supportedTypes", SUPPORTED_DOCUMENT_TYPES);
                return result;
            }

            // Simulation de l'analyse IA
            Map<String, Object> extractedData = simulateAIAnalysis(documentContent, documentType);
            
            // Validation des données extraites
            Map<String, Object> validationResult = validateExtractedData(extractedData, documentType);
            
            // Génération des suggestions comptables
            Map<String, Object> suggestionsResult = generateAccountingSuggestions(extractedData, countryCode);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> suggestions = (List<Map<String, Object>>) suggestionsResult.get("suggestions");
            
            // Calcul du score de confiance
            double confidenceScore = calculateConfidenceScore(extractedData, validationResult);
            
            result.put("documentType", documentType.toUpperCase());
            result.put("countryCode", countryCode);
            result.put("extractedData", extractedData);
            result.put("validation", validationResult);
            result.put("suggestions", suggestions);
            result.put("confidenceScore", confidenceScore);
            result.put("isValid", confidenceScore >= 0.75);
            result.put("analysisDate", LocalDate.now().toString());
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de l'analyse du document: " + e.getMessage());
        }
        
        return result;
    }



    /**
     * Détecter les anomalies et fraudes potentielles
     */
    public Map<String, Object> detectAnomalies(Map<String, Object> extractedData, String countryCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> anomalies = new ArrayList<>();
            
            // Vérification des montants suspects
            Map<String, Object> amountAnomaly = checkAmountAnomalies(extractedData);
            if (amountAnomaly != null) {
                anomalies.add(amountAnomaly);
            }
            
            // Vérification des dates suspectes
            Map<String, Object> dateAnomaly = checkDateAnomalies(extractedData);
            if (dateAnomaly != null) {
                anomalies.add(dateAnomaly);
            }
            
            // Vérification des fournisseurs suspects
            Map<String, Object> supplierAnomaly = checkSupplierAnomalies(extractedData);
            if (supplierAnomaly != null) {
                anomalies.add(supplierAnomaly);
            }
            
            // Vérification des doublons
            Map<String, Object> duplicateAnomaly = checkDuplicateAnomalies(extractedData);
            if (duplicateAnomaly != null) {
                anomalies.add(duplicateAnomaly);
            }
            
            result.put("anomalies", anomalies);
            result.put("totalAnomalies", anomalies.size());
            result.put("riskLevel", calculateRiskLevel(anomalies));
            result.put("requiresReview", anomalies.size() > 0);
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la détection d'anomalies: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Automatiser la création d'écritures comptables
     */
    public Map<String, Object> automateJournalEntry(Map<String, Object> extractedData, String countryCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Génération automatique de l'écriture
            Map<String, Object> journalEntry = generateJournalEntry(extractedData, countryCode);
            
            // Validation de l'écriture
            Map<String, Object> validation = validateJournalEntry(journalEntry);
            
            // Suggestions d'amélioration
            List<String> improvements = suggestImprovements(journalEntry);
            
            result.put("journalEntry", journalEntry);
            result.put("validation", validation);
            result.put("improvements", improvements);
            result.put("isValid", (Boolean) validation.get("isValid"));
            result.put("confidence", calculateEntryConfidence(journalEntry));
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de l'automatisation: " + e.getMessage());
        }
        
        return result;
    }

    // Méthodes privées pour la simulation de l'IA
    private Map<String, Object> simulateAIAnalysis(String content, String documentType) {
        Map<String, Object> extractedData = new HashMap<>();
        
        // Simulation d'extraction de données
        extractedData.put("numero", "FAC-" + System.currentTimeMillis());
        extractedData.put("date", LocalDate.now().toString());
        extractedData.put("fournisseur", "Fournisseur Simulé");
        extractedData.put("montant_ht", new BigDecimal("1000.00"));
        extractedData.put("tva", new BigDecimal("200.00"));
        extractedData.put("montant_ttc", new BigDecimal("1200.00"));
        extractedData.put("devise", "EUR");
        extractedData.put("conditions_paiement", "30 jours");
        extractedData.put("reference", "REF-" + System.currentTimeMillis());
        
        return extractedData;
    }

    private Map<String, Object> validateExtractedData(Map<String, Object> data, String documentType) {
        Map<String, Object> validation = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Validation des champs requis
        Map<String, Object> rules = DOCUMENT_RULES.get(documentType);
        if (rules != null) {
            @SuppressWarnings("unchecked")
            List<String> requiredFields = (List<String>) rules.get("requiredFields");
            for (String field : requiredFields) {
                if (!data.containsKey(field) || data.get(field) == null) {
                    errors.add("Champ requis manquant: " + field);
                }
            }
        }
        
        validation.put("errors", errors);
        validation.put("warnings", warnings);
        validation.put("isValid", errors.isEmpty());
        validation.put("score", calculateValidationScore(data, errors, warnings));
        
        return validation;
    }

    private Map<String, Object> generateAccountingSuggestions(Map<String, Object> data, String countryCode) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        // Suggestion de compte
        Map<String, Object> accountSuggestion = new HashMap<>();
        accountSuggestion.put("type", "ACCOUNT");
        accountSuggestion.put("suggestion", "401 - Fournisseurs");
        accountSuggestion.put("confidence", 0.95);
        accountSuggestion.put("reason", "Document de fournisseur détecté");
        suggestions.add(accountSuggestion);
        
        // Suggestion de TVA
        Map<String, Object> taxSuggestion = new HashMap<>();
        taxSuggestion.put("type", "TAX");
        taxSuggestion.put("suggestion", "4456 - TVA déductible");
        taxSuggestion.put("confidence", 0.90);
        taxSuggestion.put("reason", "TVA détectée sur le document");
        suggestions.add(taxSuggestion);
        
        result.put("suggestions", suggestions);
        result.put("totalSuggestions", suggestions.size());
        result.put("priority", determinePriority(data));
        
        return result;
    }

    private double calculateConfidenceScore(Map<String, Object> data, Map<String, Object> validation) {
        double baseScore = 0.8;
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) validation.get("errors");
        @SuppressWarnings("unchecked")
        List<String> warnings = (List<String>) validation.get("warnings");
        
        // Réduction du score selon les erreurs
        baseScore -= errors.size() * 0.1;
        baseScore -= warnings.size() * 0.05;
        
        return Math.max(0.0, Math.min(1.0, baseScore));
    }









    private String determinePriority(Map<String, Object> data) {
        BigDecimal amount = (BigDecimal) data.get("montant_ttc");
        if (amount != null && amount.compareTo(new BigDecimal("10000")) > 0) {
            return "HIGH";
        } else if (amount != null && amount.compareTo(new BigDecimal("1000")) > 0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private Map<String, Object> checkAmountAnomalies(Map<String, Object> data) {
        BigDecimal amount = (BigDecimal) data.get("montant_ttc");
        if (amount != null && amount.compareTo(new BigDecimal("50000")) > 0) {
            Map<String, Object> anomaly = new HashMap<>();
            anomaly.put("type", "AMOUNT_ANOMALY");
            anomaly.put("severity", "HIGH");
            anomaly.put("description", "Montant très élevé détecté");
            anomaly.put("value", amount);
            return anomaly;
        }
        return null;
    }

    private Map<String, Object> checkDateAnomalies(Map<String, Object> data) {
        String dateStr = (String) data.get("date");
        if (dateStr != null) {
            LocalDate date = LocalDate.parse(dateStr);
            LocalDate today = LocalDate.now();
            if (date.isAfter(today)) {
                Map<String, Object> anomaly = new HashMap<>();
                anomaly.put("type", "DATE_ANOMALY");
                anomaly.put("severity", "MEDIUM");
                anomaly.put("description", "Date future détectée");
                anomaly.put("value", dateStr);
                return anomaly;
            }
        }
        return null;
    }

    private Map<String, Object> checkSupplierAnomalies(Map<String, Object> data) {
        String supplier = (String) data.get("fournisseur");
        if (supplier != null && supplier.toLowerCase().contains("test")) {
            Map<String, Object> anomaly = new HashMap<>();
            anomaly.put("type", "SUPPLIER_ANOMALY");
            anomaly.put("severity", "LOW");
            anomaly.put("description", "Fournisseur suspect détecté");
            anomaly.put("value", supplier);
            return anomaly;
        }
        return null;
    }

    private Map<String, Object> checkDuplicateAnomalies(Map<String, Object> data) {
        // Simulation de détection de doublons
        return null;
    }

    private String calculateRiskLevel(List<Map<String, Object>> anomalies) {
        long highSeverity = anomalies.stream()
            .filter(a -> "HIGH".equals(a.get("severity")))
            .count();
        
        if (highSeverity > 0) {
            return "HIGH";
        } else if (anomalies.size() > 2) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private Map<String, Object> generateJournalEntry(Map<String, Object> data, String countryCode) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("date", data.get("date"));
        entry.put("reference", data.get("numero"));
        entry.put("description", "Achat fournisseur - " + data.get("fournisseur"));
        
        List<Map<String, Object>> lines = new ArrayList<>();
        
        // Ligne débit
        Map<String, Object> debitLine = new HashMap<>();
        debitLine.put("account", "601");
        debitLine.put("description", "Achats de marchandises");
        debitLine.put("debit", data.get("montant_ht"));
        debitLine.put("credit", BigDecimal.ZERO);
        lines.add(debitLine);
        
        // Ligne TVA
        Map<String, Object> taxLine = new HashMap<>();
        taxLine.put("account", "4456");
        taxLine.put("description", "TVA déductible");
        taxLine.put("debit", data.get("tva"));
        taxLine.put("credit", BigDecimal.ZERO);
        lines.add(taxLine);
        
        // Ligne crédit
        Map<String, Object> creditLine = new HashMap<>();
        creditLine.put("account", "401");
        creditLine.put("description", "Fournisseurs");
        creditLine.put("debit", BigDecimal.ZERO);
        creditLine.put("credit", data.get("montant_ttc"));
        lines.add(creditLine);
        
        entry.put("lines", lines);
        return entry;
    }

    private Map<String, Object> validateJournalEntry(Map<String, Object> entry) {
        Map<String, Object> validation = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lines = (List<Map<String, Object>>) entry.get("lines");
        
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        
        for (Map<String, Object> line : lines) {
            totalDebit = totalDebit.add((BigDecimal) line.get("debit"));
            totalCredit = totalCredit.add((BigDecimal) line.get("credit"));
        }
        
        boolean isBalanced = totalDebit.compareTo(totalCredit) == 0;
        
        validation.put("isValid", isBalanced);
        validation.put("totalDebit", totalDebit);
        validation.put("totalCredit", totalCredit);
        validation.put("balance", totalDebit.subtract(totalCredit));
        
        return validation;
    }

    private List<String> suggestImprovements(Map<String, Object> entry) {
        List<String> improvements = new ArrayList<>();
        improvements.add("Vérifier la classification des comptes");
        improvements.add("Ajouter des références analytiques");
        improvements.add("Vérifier les taux de TVA");
        return improvements;
    }

    private double calculateEntryConfidence(Map<String, Object> entry) {
        return 0.85; // Score de confiance simulé
    }

    private double calculateValidationScore(Map<String, Object> data, List<String> errors, List<String> warnings) {
        double score = 1.0;
        score -= errors.size() * 0.2;
        score -= warnings.size() * 0.1;
        return Math.max(0.0, score);
    }
}
