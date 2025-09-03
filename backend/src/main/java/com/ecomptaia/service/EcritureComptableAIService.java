package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class EcritureComptableAIService {
    
    @Autowired
    private TemplateEcritureRepository templateRepository;
    
    @Autowired
    private EcritureComptableRepository ecritureRepository;
    
    // ==================== ANALYSE DE DOCUMENTS ====================
    
    /**
     * Analyser un document avec l'IA
     */
    public Map<String, Object> analyserDocument(String documentContent, String documentType, Long entrepriseId) {
        Map<String, Object> resultat = new HashMap<>();
        
        try {
            // Normaliser le contenu du document
            String contenuNormalise = normaliserDescription(documentContent);
            
            // Identifier le type de document
            String typeIdentifie = identifierTypeDocument(contenuNormalise, documentType);
            resultat.put("type_document", typeIdentifie);
            
            // Extraire les informations clés
            Map<String, Object> informations = extraireInformationsDocument(contenuNormalise, typeIdentifie);
            resultat.put("informations_extraites", informations);
            
            // Suggérer des templates
            List<TemplateEcriture> templates = suggererTemplatesDocument(informations, entrepriseId);
            resultat.put("templates_suggerees", templates);
            
            // Générer une écriture préliminaire
            EcritureComptable ecriturePreliminaire = genererEcriturePreliminaire(informations, templates);
            resultat.put("ecriture_preliminaire", ecriturePreliminaire);
            
            // Calculer le score de confiance
            double confiance = calculerConfianceDocument(contenuNormalise, informations, templates);
            resultat.put("confiance", confiance);
            
            // Générer des recommandations
            List<String> recommandations = genererRecommandationsDocument(informations, templates);
            resultat.put("recommandations", recommandations);
            
        } catch (Exception e) {
            resultat.put("erreur", e.getMessage());
            resultat.put("confiance", 0.0);
        }
        
        return resultat;
    }
    
    /**
     * Obtenir des recommandations IA
     */
    public List<Map<String, Object>> getRecommendations(Long entrepriseId, String type) {
        List<Map<String, Object>> recommandations = new ArrayList<>();
        
        try {
            // Recommandations basées sur l'historique
            List<Map<String, Object>> recommandationsHistorique = getRecommandationsHistorique(entrepriseId);
            recommandations.addAll(recommandationsHistorique);
            
            // Recommandations basées sur le type
            if (type != null && !type.isEmpty()) {
                List<Map<String, Object>> recommandationsType = getRecommandationsParType(entrepriseId, type);
                recommandations.addAll(recommandationsType);
            }
            
            // Recommandations d'optimisation
            List<Map<String, Object>> recommandationsOptimisation = getRecommandationsOptimisation(entrepriseId);
            recommandations.addAll(recommandationsOptimisation);
            
        } catch (Exception e) {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("type", "erreur");
            erreur.put("message", e.getMessage());
            recommandations.add(erreur);
        }
        
        return recommandations;
    }
    
    /**
     * Optimiser une écriture avec l'IA
     */
    public EcritureComptable optimiserEcriture(EcritureComptable ecriture) {
        try {
            // Analyser l'écriture actuelle
            Map<String, Object> analyse = analyserEcriturePourOptimisation(ecriture);
            
            // Identifier les améliorations possibles
            List<String> ameliorations = identifierAmeliorations(ecriture, analyse);
            
            // Appliquer les optimisations
            EcritureComptable ecritureOptimisee = appliquerOptimisations(ecriture, ameliorations);
            
            // Recalculer les totaux
            ecritureOptimisee.calculerTotaux();
            
            // Valider l'écriture optimisée
            validerEcritureOptimisee(ecritureOptimisee);
            
            return ecritureOptimisee;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'optimisation : " + e.getMessage());
        }
    }
    
    // ==================== RECONNAISSANCE D'OPÉRATIONS ====================
    
    /**
     * Analyser une description d'opération et suggérer un template
     */
    public Map<String, Object> analyserOperation(String description, String standardComptable, String pays) {
        Map<String, Object> resultat = new HashMap<>();
        
        // Normaliser la description
        String descriptionNormalisee = normaliserDescription(description);
        
        // Identifier le type d'opération
        String typeOperation = identifierTypeOperation(descriptionNormalisee);
        resultat.put("type_operation", typeOperation);
        
        // Trouver les templates recommandés
        List<TemplateEcriture> templates = templateRepository.findTemplatesRecommandes(standardComptable, typeOperation);
        resultat.put("templates_recommandes", templates);
        
        // Extraire les variables probables
        Map<String, Object> variables = extraireVariables(descriptionNormalisee, typeOperation);
        resultat.put("variables_extraites", variables);
        
        // Calculer le score de confiance
        double confiance = calculerConfiance(descriptionNormalisee, typeOperation, templates);
        resultat.put("confiance", confiance);
        
        // Générer des suggestions
        List<String> suggestions = genererSuggestions(descriptionNormalisee, typeOperation, variables);
        resultat.put("suggestions", suggestions);
        
        return resultat;
    }
    
    /**
     * Générer une écriture automatique à partir d'une description
     */
    public EcritureComptable genererEcritureAutomatique(String description, Map<String, Object> parametres) {
        // Analyser l'opération
        Map<String, Object> analyse = analyserOperation(
            description, 
            (String) parametres.get("standardComptable"), 
            (String) parametres.get("pays")
        );
        
        // Sélectionner le meilleur template
        @SuppressWarnings("unchecked")
        List<TemplateEcriture> templates = (List<TemplateEcriture>) analyse.get("templates_recommandes");
        if (templates.isEmpty()) {
            throw new RuntimeException("Aucun template trouvé pour cette opération");
        }
        
        TemplateEcriture meilleurTemplate = templates.get(0);
        
        // Fusionner les variables extraites avec les paramètres
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) analyse.get("variables_extraites");
        variables.putAll(parametres);
        
        // Créer l'écriture
        EcritureComptable ecriture = new EcritureComptable();
        ecriture.setLibelle(description);
        ecriture.setTemplateId(meilleurTemplate.getCode());
        ecriture.setSource(EcritureComptable.SourceEcriture.IA);
        ecriture.setValidationAiConfiance(BigDecimal.valueOf((Double) analyse.get("confiance")));
        // ecriture.setValidationAiSuggestions(objectMapper.writeValueAsString(analyse.get("suggestions")));
        
        // Appliquer les paramètres de base
        if (parametres.containsKey("dateEcriture")) {
            ecriture.setDateEcriture(LocalDate.parse((String) parametres.get("dateEcriture")));
        } else {
            ecriture.setDateEcriture(LocalDate.now());
        }
        
        if (parametres.containsKey("datePiece")) {
            ecriture.setDatePiece(LocalDate.parse((String) parametres.get("datePiece")));
        } else {
            ecriture.setDatePiece(LocalDate.now());
        }
        
        if (parametres.containsKey("devise")) {
            ecriture.setDevise((String) parametres.get("devise"));
        } else {
            ecriture.setDevise(meilleurTemplate.getDeviseDefaut());
        }
        
        return ecriture;
    }
    
    // ==================== VALIDATION IA ====================
    
    /**
     * Valider une écriture avec l'IA
     */
    public Map<String, Object> validerEcritureIA(EcritureComptable ecriture) {
        Map<String, Object> resultat = new HashMap<>();
        List<String> erreurs = new ArrayList<>();
        List<String> avertissements = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        // Validation de l'équilibre
        if (!ecriture.isEquilibree()) {
            erreurs.add("L'écriture n'est pas équilibrée");
        }
        
        // Validation des comptes
        validerComptes(ecriture, erreurs, avertissements, suggestions);
        
        // Validation des montants
        validerMontants(ecriture, erreurs, avertissements, suggestions);
        
        // Validation de la cohérence
        validerCohérence(ecriture, erreurs, avertissements, suggestions);
        
        // Validation des patterns
        validerPatterns(ecriture, erreurs, avertissements, suggestions);
        
        // Calculer le score de confiance
        double confiance = calculerScoreConfiance(erreurs, avertissements, suggestions);
        
        resultat.put("confiance", confiance);
        resultat.put("erreurs", erreurs);
        resultat.put("avertissements", avertissements);
        resultat.put("suggestions", suggestions);
        resultat.put("is_valide", erreurs.isEmpty());
        
        return resultat;
    }
    
    /**
     * Suggérer des améliorations pour une écriture
     */
    public List<String> suggererAmeliorations(EcritureComptable ecriture) {
        List<String> suggestions = new ArrayList<>();
        
        // Analyser les patterns historiques
        List<EcritureComptable> ecrituresSimilaires = trouverEcrituresSimilaires(ecriture);
        if (!ecrituresSimilaires.isEmpty()) {
            suggestions.add("Écritures similaires trouvées dans l'historique");
        }
        
        // Vérifier les comptes utilisés
        suggestions.addAll(suggererComptesAlternatifs(ecriture));
        
        // Vérifier les montants
        suggestions.addAll(suggererMontants(ecriture));
        
        // Vérifier la structure
        suggestions.addAll(suggererStructure(ecriture));
        
        return suggestions;
    }
    
    // ==================== APPRENTISSAGE ====================
    
    /**
     * Apprendre à partir d'écritures validées
     */
    public void apprendreFromEcritures(List<EcritureComptable> ecritures) {
        for (EcritureComptable ecriture : ecritures) {
            if (ecriture.getStatut() == EcritureComptable.StatutEcriture.VALIDEE) {
                apprendrePattern(ecriture);
            }
        }
    }
    
    /**
     * Mettre à jour les templates basés sur l'usage
     */
    public void mettreAJourTemplatesUsage() {
        List<TemplateEcriture> templates = templateRepository.findAll();
        
        for (TemplateEcriture template : templates) {
            // Compter l'usage du template
            List<EcritureComptable> ecritures = ecritureRepository.findByTemplateIdOrderByDateEcritureDesc(template.getCode());
            
            // Mettre à jour l'ordre d'affichage basé sur l'usage
            if (template.getOrdreAffichage() == null) {
                template.setOrdreAffichage(ecritures.size());
            }
        }
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    /**
     * Normaliser une description
     */
    private String normaliserDescription(String description) {
        return description.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", " ")
            .replaceAll("\\s+", " ")
            .trim();
    }
    
    /**
     * Identifier le type d'opération
     */
    private String identifierTypeOperation(String description) {
        // Patterns de reconnaissance
        Map<String, List<String>> patterns = new HashMap<>();
        
        patterns.put("vente", Arrays.asList("vente", "facturation", "client", "ca", "chiffre d'affaires"));
        patterns.put("achat", Arrays.asList("achat", "fournisseur", "approvisionnement", "commande"));
        patterns.put("paie", Arrays.asList("salaire", "paie", "rémunération", "personnel", "employé"));
        patterns.put("tresorerie", Arrays.asList("banque", "caisse", "virement", "encaissement", "paiement"));
        patterns.put("immobilisation", Arrays.asList("immobilisation", "amortissement", "cession", "acquisition"));
        patterns.put("stock", Arrays.asList("stock", "inventaire", "entrée", "sortie", "marchandise"));
        patterns.put("fiscal", Arrays.asList("tva", "impôt", "taxe", "déclaration", "fiscal"));
        patterns.put("financement", Arrays.asList("emprunt", "prêt", "capital", "dividende", "financement"));
        
        // Rechercher le pattern le plus probable
        String meilleurType = "autre";
        int meilleurScore = 0;
        
        for (Map.Entry<String, List<String>> entry : patterns.entrySet()) {
            int score = 0;
            for (String mot : entry.getValue()) {
                if (description.contains(mot)) {
                    score++;
                }
            }
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurType = entry.getKey();
            }
        }
        
        return meilleurType;
    }
    
    /**
     * Extraire les variables d'une description
     */
    private Map<String, Object> extraireVariables(String description, String typeOperation) {
        Map<String, Object> variables = new HashMap<>();
        
        // Extraire les montants
        Pattern montantPattern = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*(?:f|fcfa|xof|euro|eur|dollar|usd)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = montantPattern.matcher(description);
        if (matcher.find()) {
            String montantStr = matcher.group(1).replace(",", ".");
            variables.put("montant", new BigDecimal(montantStr));
        }
        
        // Extraire les taux de TVA
        Pattern tvaPattern = Pattern.compile("(\\d+(?:[.,]\\d+)?)%\\s*tva", Pattern.CASE_INSENSITIVE);
        matcher = tvaPattern.matcher(description);
        if (matcher.find()) {
            String tauxStr = matcher.group(1).replace(",", ".");
            variables.put("tauxTva", new BigDecimal(tauxStr).divide(BigDecimal.valueOf(100)));
        }
        
        // Extraire les dates
        Pattern datePattern = Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})");
        matcher = datePattern.matcher(description);
        if (matcher.find()) {
            String dateStr = matcher.group(3) + "-" + matcher.group(2) + "-" + matcher.group(1);
            variables.put("date", dateStr);
        }
        
        return variables;
    }
    
    /**
     * Calculer le score de confiance
     */
    private double calculerConfiance(String description, String typeOperation, List<TemplateEcriture> templates) {
        double confiance = 0.5; // Confiance de base
        
        // Augmenter si des templates sont trouvés
        if (!templates.isEmpty()) {
            confiance += 0.3;
        }
        
        // Augmenter si le type d'opération est clair
        if (!"autre".equals(typeOperation)) {
            confiance += 0.2;
        }
        
        // Augmenter si des variables sont extraites
        Map<String, Object> variables = extraireVariables(description, typeOperation);
        if (!variables.isEmpty()) {
            confiance += 0.1;
        }
        
        return Math.min(confiance, 1.0);
    }
    
    /**
     * Générer des suggestions
     */
    private List<String> genererSuggestions(String description, String typeOperation, Map<String, Object> variables) {
        List<String> suggestions = new ArrayList<>();
        
        if (variables.containsKey("montant")) {
            suggestions.add("Montant détecté : " + variables.get("montant"));
        }
        
        if (variables.containsKey("tauxTva")) {
            suggestions.add("TVA détectée : " + variables.get("tauxTva") + "%");
        }
        
        if ("vente".equals(typeOperation)) {
            suggestions.add("Suggestion : Vérifiez le taux de TVA applicable");
        }
        
        if ("achat".equals(typeOperation)) {
            suggestions.add("Suggestion : Vérifiez la TVA déductible");
        }
        
        return suggestions;
    }
    
    /**
     * Valider les comptes d'une écriture
     */
    private void validerComptes(EcritureComptable ecriture, List<String> erreurs, List<String> avertissements, List<String> suggestions) {
        if (ecriture.getLignes() != null) {
            for (LigneEcriture ligne : ecriture.getLignes()) {
                if (ligne.getCompte() != null) {
                    // Vérifier si le compte est actif
                    if (!ligne.getCompte().getIsActive()) {
                        avertissements.add("Compte " + ligne.getCompteNumero() + " inactif");
                    }
                    
                    // Vérifier la cohérence du compte avec le type d'opération
                    verifierCohérenceCompte(ligne, ecriture, avertissements);
                }
            }
        }
    }
    
    /**
     * Valider les montants d'une écriture
     */
    private void validerMontants(EcritureComptable ecriture, List<String> erreurs, List<String> avertissements, List<String> suggestions) {
        if (ecriture.getLignes() != null) {
            BigDecimal totalDebit = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            
            for (LigneEcriture ligne : ecriture.getLignes()) {
                if (ligne.getDebit() != null) {
                    totalDebit = totalDebit.add(ligne.getDebit());
                }
                if (ligne.getCredit() != null) {
                    totalCredit = totalCredit.add(ligne.getCredit());
                }
            }
            
            // Vérifier l'équilibre
            if (totalDebit.compareTo(totalCredit) != 0) {
                erreurs.add("Écriture non équilibrée : Débit=" + totalDebit + ", Crédit=" + totalCredit);
            }
            
            // Vérifier les montants anormaux
            if (totalDebit.compareTo(BigDecimal.valueOf(1000000)) > 0) {
                avertissements.add("Montant élevé détecté : " + totalDebit);
            }
        }
    }
    
    /**
     * Valider la cohérence d'une écriture
     */
    private void validerCohérence(EcritureComptable ecriture, List<String> erreurs, List<String> avertissements, List<String> suggestions) {
        // Vérifier la cohérence avec le template
        if (ecriture.getTemplateId() != null) {
            Optional<TemplateEcriture> template = templateRepository.findByCode(ecriture.getTemplateId());
            if (template.isPresent()) {
                verifierCohérenceTemplate(ecriture, template.get(), avertissements);
            }
        }
        
        // Vérifier la cohérence temporelle
        if (ecriture.getDateEcriture() != null && ecriture.getDatePiece() != null) {
            if (ecriture.getDateEcriture().isAfter(LocalDate.now())) {
                avertissements.add("Date d'écriture dans le futur");
            }
            if (ecriture.getDatePiece().isAfter(LocalDate.now())) {
                avertissements.add("Date de pièce dans le futur");
            }
        }
    }
    
    /**
     * Valider les patterns d'une écriture
     */
    private void validerPatterns(EcritureComptable ecriture, List<String> erreurs, List<String> avertissements, List<String> suggestions) {
        // Vérifier les patterns de comptes
        if (ecriture.getLignes() != null) {
            Set<String> classesComptes = new HashSet<>();
            for (LigneEcriture ligne : ecriture.getLignes()) {
                if (ligne.getCompteNumero() != null && ligne.getCompteNumero().length() >= 1) {
                    classesComptes.add(ligne.getCompteNumero().substring(0, 1));
                }
            }
            
            // Vérifier la diversité des classes de comptes
            if (classesComptes.size() < 2) {
                avertissements.add("Peu de diversité dans les classes de comptes");
            }
        }
    }
    
    /**
     * Calculer le score de confiance
     */
    private double calculerScoreConfiance(List<String> erreurs, List<String> avertissements, List<String> suggestions) {
        double confiance = 1.0;
        
        // Réduire pour chaque erreur
        confiance -= erreurs.size() * 0.3;
        
        // Réduire pour chaque avertissement
        confiance -= avertissements.size() * 0.1;
        
        // Augmenter pour chaque suggestion
        confiance += suggestions.size() * 0.05;
        
        return Math.max(0.0, Math.min(confiance, 1.0));
    }
    
    /**
     * Trouver des écritures similaires
     */
    private List<EcritureComptable> trouverEcrituresSimilaires(EcritureComptable ecriture) {
        // Implémentation simplifiée - à améliorer
        return ecritureRepository.findByLibelleContainingIgnoreCaseOrderByDateEcritureDesc(
            ecriture.getLibelle().substring(0, Math.min(10, ecriture.getLibelle().length()))
        );
    }
    
    /**
     * Suggérer des comptes alternatifs
     */
    private List<String> suggererComptesAlternatifs(EcritureComptable ecriture) {
        List<String> suggestions = new ArrayList<>();
        
        // Implémentation simplifiée
        suggestions.add("Vérifiez les comptes utilisés");
        
        return suggestions;
    }
    
    /**
     * Suggérer des montants
     */
    private List<String> suggererMontants(EcritureComptable ecriture) {
        List<String> suggestions = new ArrayList<>();
        
        if (ecriture.getTotalDebit() != null && ecriture.getTotalDebit().compareTo(BigDecimal.ZERO) > 0) {
            suggestions.add("Vérifiez le montant total : " + ecriture.getTotalDebit());
        }
        
        return suggestions;
    }
    
    /**
     * Suggérer des améliorations de structure
     */
    private List<String> suggererStructure(EcritureComptable ecriture) {
        List<String> suggestions = new ArrayList<>();
        
        if (ecriture.getLignes() != null && ecriture.getLignes().size() < 2) {
            suggestions.add("Ajoutez plus de lignes pour une meilleure traçabilité");
        }
        
        return suggestions;
    }
    
    /**
     * Apprendre un pattern d'écriture
     */
    private void apprendrePattern(EcritureComptable ecriture) {
        // Implémentation simplifiée - à améliorer avec un système d'apprentissage
        // Ici on pourrait stocker les patterns dans une base de données
        // ou mettre à jour des statistiques d'usage
    }
    
    /**
     * Vérifier la cohérence d'un compte
     */
    private void verifierCohérenceCompte(LigneEcriture ligne, EcritureComptable ecriture, List<String> avertissements) {
        // Implémentation simplifiée
        // Ici on pourrait vérifier si le compte est approprié pour le type d'opération
    }
    
    /**
     * Vérifier la cohérence avec un template
     */
    private void verifierCohérenceTemplate(EcritureComptable ecriture, TemplateEcriture template, List<String> avertissements) {
        // Implémentation simplifiée
        // Ici on pourrait vérifier si l'écriture respecte le pattern du template
    }
    
    // ==================== MÉTHODES UTILITAIRES POUR ANALYSE DE DOCUMENTS ====================
    
    private String identifierTypeDocument(String contenu, String typeSuggere) {
        if (typeSuggere != null && !typeSuggere.isEmpty()) {
            return typeSuggere.toUpperCase();
        }
        
        String contenuLower = contenu.toLowerCase();
        if (contenuLower.contains("facture") || contenuLower.contains("invoice")) {
            return "FACTURE";
        } else if (contenuLower.contains("bon de commande") || contenuLower.contains("purchase order")) {
            return "BON_COMMANDE";
        } else if (contenuLower.contains("bon de livraison") || contenuLower.contains("delivery note")) {
            return "BON_LIVRAISON";
        } else if (contenuLower.contains("reçu") || contenuLower.contains("receipt")) {
            return "RECU";
        } else {
            return "DOCUMENT_GENERIQUE";
        }
    }
    
    private Map<String, Object> extraireInformationsDocument(String contenu, String typeDocument) {
        Map<String, Object> informations = new HashMap<>();
        
        // Extraire le montant
        Pattern montantPattern = Pattern.compile("(\\d+[\\s,]*\\d*)\\s*(FCFA|XOF|€|\\$)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher montantMatcher = montantPattern.matcher(contenu);
        if (montantMatcher.find()) {
            String montantStr = montantMatcher.group(1).replaceAll("[\\s,]", "");
            informations.put("montant", new BigDecimal(montantStr));
            informations.put("devise", montantMatcher.group(2));
        }
        
        // Extraire le numéro de document
        Pattern numeroPattern = Pattern.compile("(?:N°|Numéro|No|#)\\s*([A-Z0-9-]+)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher numeroMatcher = numeroPattern.matcher(contenu);
        if (numeroMatcher.find()) {
            informations.put("numero_document", numeroMatcher.group(1));
        }
        
        // Extraire le fournisseur/client
        Pattern tiersPattern = Pattern.compile("(?:Fournisseur|Client|Supplier|Customer)\\s*:?\\s*([A-Za-z\\s]+)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher tiersMatcher = tiersPattern.matcher(contenu);
        if (tiersMatcher.find()) {
            informations.put("tiers", tiersMatcher.group(1).trim());
        }
        
        // Extraire la date
        Pattern datePattern = Pattern.compile("(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher dateMatcher = datePattern.matcher(contenu);
        if (dateMatcher.find()) {
            informations.put("date", dateMatcher.group(1));
        }
        
        informations.put("type_document", typeDocument);
        return informations;
    }
    
    private List<TemplateEcriture> suggererTemplatesDocument(Map<String, Object> informations, Long entrepriseId) {
        List<TemplateEcriture> templates = new ArrayList<>();
        
        String typeDocument = (String) informations.get("type_document");
        BigDecimal montant = (BigDecimal) informations.get("montant");
        
        if ("FACTURE".equals(typeDocument)) {
            if (montant != null && montant.compareTo(BigDecimal.ZERO) > 0) {
                templates = templateRepository.findByCategorieAndIsActifTrueOrderByOrdreAffichageAsc(TemplateEcriture.CategorieTemplate.VENTE);
            }
        } else if ("BON_COMMANDE".equals(typeDocument)) {
            templates = templateRepository.findByCategorieAndIsActifTrueOrderByOrdreAffichageAsc(TemplateEcriture.CategorieTemplate.ACHAT);
        }
        
        return templates;
    }
    
    private EcritureComptable genererEcriturePreliminaire(Map<String, Object> informations, List<TemplateEcriture> templates) {
        EcritureComptable ecriture = new EcritureComptable();
        
        // Appliquer les informations extraites
        if (informations.containsKey("montant")) {
            ecriture.setTotalDebit((BigDecimal) informations.get("montant"));
            ecriture.setTotalCredit((BigDecimal) informations.get("montant"));
        }
        
        if (informations.containsKey("numero_document")) {
            ecriture.setReference((String) informations.get("numero_document"));
        }
        
        if (informations.containsKey("date")) {
            try {
                String dateStr = (String) informations.get("date");
                LocalDate date = LocalDate.parse(dateStr.replace("/", "-"));
                ecriture.setDateEcriture(date);
                ecriture.setDatePiece(date);
            } catch (Exception e) {
                ecriture.setDateEcriture(LocalDate.now());
                ecriture.setDatePiece(LocalDate.now());
            }
        }
        
        ecriture.setSource(EcritureComptable.SourceEcriture.IA);
        ecriture.setStatut(EcritureComptable.StatutEcriture.BROUILLON);
        
        return ecriture;
    }
    
    private double calculerConfianceDocument(String contenu, Map<String, Object> informations, List<TemplateEcriture> templates) {
        double confiance = 0.0;
        
        // Score basé sur la présence d'informations clés
        if (informations.containsKey("montant")) confiance += 0.3;
        if (informations.containsKey("numero_document")) confiance += 0.2;
        if (informations.containsKey("tiers")) confiance += 0.2;
        if (informations.containsKey("date")) confiance += 0.1;
        
        // Score basé sur la qualité des templates trouvés
        if (!templates.isEmpty()) confiance += 0.2;
        
        return Math.min(confiance, 1.0);
    }
    
    private List<String> genererRecommandationsDocument(Map<String, Object> informations, List<TemplateEcriture> templates) {
        List<String> recommandations = new ArrayList<>();
        
        if (!informations.containsKey("montant")) {
            recommandations.add("Montant non détecté - vérifiez le format");
        }
        
        if (!informations.containsKey("tiers")) {
            recommandations.add("Tiers non identifié - ajoutez manuellement");
        }
        
        if (templates.isEmpty()) {
            recommandations.add("Aucun template automatique trouvé - sélectionnez manuellement");
        }
        
        return recommandations;
    }
    
    // ==================== MÉTHODES UTILITAIRES POUR RECOMMANDATIONS ====================
    
    private List<Map<String, Object>> getRecommandationsHistorique(Long entrepriseId) {
        List<Map<String, Object>> recommandations = new ArrayList<>();
        
        // Analyser les écritures récentes
        Company entreprise = new Company();
        entreprise.setId(entrepriseId);
        List<EcritureComptable> ecrituresRecentes = ecritureRepository.findByEntrepriseOrderByDateEcritureDesc(entreprise);
        
        if (!ecrituresRecentes.isEmpty()) {
            Map<String, Object> recommandation = new HashMap<>();
            recommandation.put("type", "historique");
            recommandation.put("titre", "Écritures récentes");
            recommandation.put("message", "Vous avez " + ecrituresRecentes.size() + " écritures récentes");
            recommandation.put("donnees", ecrituresRecentes.subList(0, Math.min(5, ecrituresRecentes.size())));
            recommandations.add(recommandation);
        }
        
        return recommandations;
    }
    
    private List<Map<String, Object>> getRecommandationsParType(Long entrepriseId, String type) {
        List<Map<String, Object>> recommandations = new ArrayList<>();
        
        // Trouver les templates pour ce type
        List<TemplateEcriture> templates = templateRepository.findByCategorieAndIsActifTrueOrderByOrdreAffichageAsc(
            TemplateEcriture.CategorieTemplate.valueOf(type.toUpperCase())
        );
        
        if (!templates.isEmpty()) {
            Map<String, Object> recommandation = new HashMap<>();
            recommandation.put("type", "template");
            recommandation.put("titre", "Templates recommandés");
            recommandation.put("message", type + " - " + templates.size() + " templates disponibles");
            recommandation.put("donnees", templates);
            recommandations.add(recommandation);
        }
        
        return recommandations;
    }
    
    private List<Map<String, Object>> getRecommandationsOptimisation(Long entrepriseId) {
        List<Map<String, Object>> recommandations = new ArrayList<>();
        
        // Vérifier les écritures non validées
        List<EcritureComptable> ecrituresNonValidees = ecritureRepository.findByStatutOrderByDateEcritureDesc(EcritureComptable.StatutEcriture.BROUILLON);
        
        if (!ecrituresNonValidees.isEmpty()) {
            Map<String, Object> recommandation = new HashMap<>();
            recommandation.put("type", "optimisation");
            recommandation.put("titre", "Écritures à valider");
            recommandation.put("message", ecrituresNonValidees.size() + " écritures en attente de validation");
            recommandation.put("donnees", ecrituresNonValidees);
            recommandations.add(recommandation);
        }
        
        return recommandations;
    }
    
    // ==================== MÉTHODES UTILITAIRES POUR OPTIMISATION ====================
    
    private Map<String, Object> analyserEcriturePourOptimisation(EcritureComptable ecriture) {
        Map<String, Object> analyse = new HashMap<>();
        
        // Analyser l'équilibre débit/crédit
        BigDecimal totalDebit = ecriture.getTotalDebit();
        BigDecimal totalCredit = ecriture.getTotalCredit();
        
        if (totalDebit != null && totalCredit != null) {
            BigDecimal difference = totalDebit.subtract(totalCredit).abs();
            analyse.put("equilibre", difference.compareTo(BigDecimal.ZERO) == 0);
            analyse.put("difference", difference);
        }
        
        // Analyser les lignes
        if (ecriture.getLignes() != null) {
            analyse.put("nombre_lignes", ecriture.getLignes().size());
            analyse.put("lignes_vides", ecriture.getLignes().stream().anyMatch(l -> l.getLibelleLigne() == null || l.getLibelleLigne().isEmpty()));
        }
        
        return analyse;
    }
    
    private List<String> identifierAmeliorations(EcritureComptable ecriture, Map<String, Object> analyse) {
        List<String> ameliorations = new ArrayList<>();
        
        Boolean equilibre = (Boolean) analyse.get("equilibre");
        if (equilibre != null && !equilibre) {
            ameliorations.add("Équilibre débit/crédit non respecté");
        }
        
        Boolean lignesVides = (Boolean) analyse.get("lignes_vides");
        if (lignesVides != null && lignesVides) {
            ameliorations.add("Certaines lignes ont des libellés vides");
        }
        
        return ameliorations;
    }
    
    private EcritureComptable appliquerOptimisations(EcritureComptable ecriture, List<String> ameliorations) {
        // Appliquer les optimisations identifiées
        if (ameliorations.contains("Équilibre débit/crédit non respecté")) {
            // Recalculer les totaux
            ecriture.calculerTotaux();
        }
        
        if (ameliorations.contains("Certaines lignes ont des libellés vides")) {
            // Compléter les libellés vides
            if (ecriture.getLignes() != null) {
                for (LigneEcriture ligne : ecriture.getLignes()) {
                    if (ligne.getLibelleLigne() == null || ligne.getLibelleLigne().isEmpty()) {
                        ligne.setLibelleLigne("Libellé automatique");
                    }
                }
            }
        }
        
        return ecriture;
    }
    
    private void validerEcritureOptimisee(EcritureComptable ecriture) {
        // Validation basique de l'écriture optimisée
        if (ecriture.getTotalDebit() == null || ecriture.getTotalCredit() == null) {
            throw new RuntimeException("Totaux manquants après optimisation");
        }
        
        if (ecriture.getTotalDebit().compareTo(ecriture.getTotalCredit()) != 0) {
            throw new RuntimeException("Écriture non équilibrée après optimisation");
        }
    }
}
