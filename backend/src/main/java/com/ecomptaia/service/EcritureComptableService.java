package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import com.ecomptaia.accounting.entity.Account;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class EcritureComptableService {
    
    @Autowired
    private EcritureComptableRepository ecritureRepository;
    
    @Autowired
    private LigneEcritureRepository ligneRepository;
    
    @Autowired
    private TemplateEcritureRepository templateRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private FinancialPeriodRepository periodRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // ==================== CRUD BASIQUE ====================
    
    /**
     * Créer une nouvelle écriture comptable
     */
    public EcritureComptable createEcriture(EcritureComptable ecriture) {
        // Générer le numéro de pièce automatiquement
        if (ecriture.getNumeroPiece() == null || ecriture.getNumeroPiece().isEmpty()) {
            ecriture.setNumeroPiece(genererNumeroPiece(ecriture.getEntreprise()));
        }
        
        // Calculer les totaux
        ecriture.calculerTotaux();
        
        // Validation de base
        validerEcriture(ecriture);
        
        // Sauvegarder l'écriture
        EcritureComptable ecritureSauvegardee = ecritureRepository.save(ecriture);
        
        // Sauvegarder les lignes
        if (ecriture.getLignes() != null) {
            for (LigneEcriture ligne : ecriture.getLignes()) {
                ligne.setEcriture(ecritureSauvegardee);
                ligneRepository.save(ligne);
            }
        }
        
        return ecritureSauvegardee;
    }
    
    /**
     * Mettre à jour une écriture comptable
     */
    public EcritureComptable updateEcriture(Long id, EcritureComptable ecriture) {
        EcritureComptable ecritureExistante = ecritureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Écriture non trouvée"));
        
        // Vérifier que l'écriture n'est pas clôturée
        if (ecritureExistante.getStatut() == EcritureComptable.StatutEcriture.CLOTUREE) {
            throw new RuntimeException("Impossible de modifier une écriture clôturée");
        }
        
        // Mettre à jour les champs
        ecritureExistante.setDateEcriture(ecriture.getDateEcriture());
        ecritureExistante.setDatePiece(ecriture.getDatePiece());
        ecritureExistante.setReference(ecriture.getReference());
        ecritureExistante.setLibelle(ecriture.getLibelle());
        ecritureExistante.setTypeEcriture(ecriture.getTypeEcriture());
        ecritureExistante.setDevise(ecriture.getDevise());
        ecritureExistante.setTauxChange(ecriture.getTauxChange());
        ecritureExistante.setTemplateId(ecriture.getTemplateId());
        ecritureExistante.setMetadata(ecriture.getMetadata());
        
        // Mettre à jour les lignes
        if (ecriture.getLignes() != null) {
            // Supprimer les anciennes lignes
            ligneRepository.deleteByEcriture(ecritureExistante);
            
            // Ajouter les nouvelles lignes
            for (LigneEcriture ligne : ecriture.getLignes()) {
                ligne.setEcriture(ecritureExistante);
                ligneRepository.save(ligne);
            }
        }
        
        // Recalculer les totaux
        ecritureExistante.calculerTotaux();
        
        // Validation
        validerEcriture(ecritureExistante);
        
        return ecritureRepository.save(ecritureExistante);
    }
    
    /**
     * Supprimer une écriture comptable
     */
    public void deleteEcriture(Long id) {
        EcritureComptable ecriture = ecritureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Écriture non trouvée"));
        
        // Vérifier que l'écriture peut être supprimée
        if (ecriture.getStatut() != EcritureComptable.StatutEcriture.BROUILLON) {
            throw new RuntimeException("Seules les écritures en brouillon peuvent être supprimées");
        }
        
        // Supprimer les lignes
        ligneRepository.deleteByEcriture(ecriture);
        
        // Supprimer l'écriture
        ecritureRepository.delete(ecriture);
    }
    
    /**
     * Obtenir une écriture par ID
     */
    public EcritureComptable getEcritureById(Long id) {
        return ecritureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Écriture non trouvée"));
    }
    
    /**
     * Obtenir toutes les écritures d'une entreprise
     */
    public List<EcritureComptable> getEcrituresByEntreprise(Company entreprise) {
        return ecritureRepository.findByEntrepriseOrderByDateEcritureDesc(entreprise);
    }
    
    /**
     * Obtenir les écritures d'un exercice
     */
    public List<EcritureComptable> getEcrituresByExercice(FinancialPeriod exercice) {
        return ecritureRepository.findByExerciceOrderByDateEcritureDesc(exercice);
    }
    
    // ==================== VALIDATION ====================
    
    /**
     * Valider une écriture comptable
     */
    public EcritureComptable validerEcriture(Long id) {
        EcritureComptable ecriture = getEcritureById(id);
        
        // Validation complète
        validerEcriture(ecriture);
        
        // Changer le statut
        ecriture.setStatut(EcritureComptable.StatutEcriture.VALIDEE);
        
        return ecritureRepository.save(ecriture);
    }
    
    /**
     * Annuler la validation d'une écriture
     */
    public EcritureComptable annulerValidation(Long id) {
        EcritureComptable ecriture = getEcritureById(id);
        
        // Vérifier que l'écriture est validée
        if (ecriture.getStatut() != EcritureComptable.StatutEcriture.VALIDEE) {
            throw new RuntimeException("Seules les écritures validées peuvent être annulées");
        }
        
        // Changer le statut
        ecriture.setStatut(EcritureComptable.StatutEcriture.BROUILLON);
        
        return ecritureRepository.save(ecriture);
    }
    
    /**
     * Clôturer une écriture
     */
    public EcritureComptable cloturerEcriture(Long id) {
        EcritureComptable ecriture = getEcritureById(id);
        
        // Vérifier que l'écriture est validée
        if (ecriture.getStatut() != EcritureComptable.StatutEcriture.VALIDEE) {
            throw new RuntimeException("Seules les écritures validées peuvent être clôturées");
        }
        
        // Changer le statut
        ecriture.setStatut(EcritureComptable.StatutEcriture.CLOTUREE);
        
        return ecritureRepository.save(ecriture);
    }
    
    // ==================== TEMPLATES ====================
    
    /**
     * Créer une écriture à partir d'un template
     */
    public EcritureComptable createEcritureFromTemplate(String templateCode, Map<String, Object> variables) {
        TemplateEcriture template = templateRepository.findByCode(templateCode)
            .orElseThrow(() -> new RuntimeException("Template non trouvé"));
        
        // Créer l'écriture de base
        EcritureComptable ecriture = new EcritureComptable();
        ecriture.setLibelle(template.getNom());
        ecriture.setTemplateId(templateCode);
        ecriture.setSource(EcritureComptable.SourceEcriture.TEMPLATE);
        ecriture.setDevise(template.getDeviseDefaut());
        
        // Appliquer les variables
        appliquerVariablesTemplate(ecriture, template, variables);
        
        // Créer les lignes selon le pattern
        List<LigneEcriture> lignes = genererLignesFromTemplate(template, variables);
        ecriture.setLignes(lignes);
        
        // Calculer les totaux
        ecriture.calculerTotaux();
        
        return createEcriture(ecriture);
    }
    
    /**
     * Obtenir les templates recommandés pour une opération
     */
    public List<TemplateEcriture> getTemplatesRecommandes(String standardComptable, String operation) {
        return templateRepository.findTemplatesRecommandes(standardComptable, operation);
    }
    
    /**
     * Obtenir tous les templates actifs
     */
    public List<TemplateEcriture> getAllTemplatesActifs() {
        return templateRepository.findByIsActifTrueOrderByOrdreAffichageAsc();
    }
    
    // ==================== RECHERCHE AVANCÉE ====================
    
    /**
     * Recherche avancée d'écritures
     */
    public List<EcritureComptable> searchEcritures(Map<String, Object> criteres) {
        Company entreprise = criteres.get("entreprise") != null ? 
            companyRepository.findById((Long) criteres.get("entreprise")).orElse(null) : null;
        
        FinancialPeriod exercice = criteres.get("exercice") != null ? 
            periodRepository.findById((Long) criteres.get("exercice")).orElse(null) : null;
        
        EcritureComptable.StatutEcriture statut = criteres.get("statut") != null ? 
            EcritureComptable.StatutEcriture.valueOf((String) criteres.get("statut")) : null;
        
        EcritureComptable.TypeEcriture typeEcriture = criteres.get("typeEcriture") != null ? 
            EcritureComptable.TypeEcriture.valueOf((String) criteres.get("typeEcriture")) : null;
        
        EcritureComptable.SourceEcriture source = criteres.get("source") != null ? 
            EcritureComptable.SourceEcriture.valueOf((String) criteres.get("source")) : null;
        
        LocalDate dateDebut = criteres.get("dateDebut") != null ? 
            LocalDate.parse((String) criteres.get("dateDebut")) : null;
        
        LocalDate dateFin = criteres.get("dateFin") != null ? 
            LocalDate.parse((String) criteres.get("dateFin")) : null;
        
        String devise = (String) criteres.get("devise");
        Long utilisateurId = (Long) criteres.get("utilisateurId");
        
        return ecritureRepository.findEcrituresWithCriteria(
            entreprise, exercice, statut, typeEcriture, source, 
            dateDebut, dateFin, devise, utilisateurId
        );
    }
    
    /**
     * Recherche par texte
     */
    public List<EcritureComptable> searchByTexte(Company entreprise, String texte) {
        return ecritureRepository.findByTexte(entreprise, texte);
    }
    
    // ==================== STATISTIQUES ====================
    
    /**
     * Obtenir les statistiques des écritures
     */
    public Map<String, Object> getStatistiques(Company entreprise) {
        Map<String, Object> statistiques = new HashMap<>();
        
        // Comptage par statut
        List<Object[]> countByStatut = ecritureRepository.countByStatut(entreprise);
        Map<String, Long> statuts = new HashMap<>();
        for (Object[] result : countByStatut) {
            statuts.put(result[0].toString(), (Long) result[1]);
        }
        statistiques.put("par_statut", statuts);
        
        // Comptage par type d'écriture
        List<Object[]> countByType = ecritureRepository.countByTypeEcriture(entreprise);
        Map<String, Long> types = new HashMap<>();
        for (Object[] result : countByType) {
            types.put(result[0].toString(), (Long) result[1]);
        }
        statistiques.put("par_type", types);
        
        // Comptage par source
        List<Object[]> countBySource = ecritureRepository.countBySource(entreprise);
        Map<String, Long> sources = new HashMap<>();
        for (Object[] result : countBySource) {
            sources.put(result[0].toString(), (Long) result[1]);
        }
        statistiques.put("par_source", sources);
        
        // Comptage par mois (derniers 12 mois)
        LocalDate dateFin = LocalDate.now();
        LocalDate dateDebut = dateFin.minusMonths(12);
        List<Object[]> countByMonth = ecritureRepository.countByMonth(entreprise, dateDebut, dateFin);
        Map<String, Long> mois = new HashMap<>();
        for (Object[] result : countByMonth) {
            String moisKey = result[0] + "-" + String.format("%02d", result[1]);
            mois.put(moisKey, (Long) result[2]);
        }
        statistiques.put("par_mois", mois);
        
        return statistiques;
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    /**
     * Générer un numéro de pièce automatique
     */
    private String genererNumeroPiece(Company entreprise) {
        LocalDate aujourdhui = LocalDate.now();
        String prefixe = "ECR-" + aujourdhui.getYear() + String.format("%02d", aujourdhui.getMonthValue());
        
        // Compter les écritures du mois
        LocalDate debutMois = aujourdhui.withDayOfMonth(1);
        LocalDate finMois = aujourdhui.withDayOfMonth(aujourdhui.lengthOfMonth());
        
        List<EcritureComptable> ecrituresMois = ecritureRepository.findByDateEcritureBetweenOrderByDateEcritureDesc(debutMois, finMois);
        
        int numero = ecrituresMois.size() + 1;
        return prefixe + "-" + String.format("%04d", numero);
    }
    
    /**
     * Valider une écriture comptable
     */
    private void validerEcriture(EcritureComptable ecriture) {
        List<String> erreurs = new ArrayList<>();
        
        // Vérifier l'équilibre
        if (!ecriture.isEquilibree()) {
            erreurs.add("L'écriture doit être équilibrée (débit = crédit)");
        }
        
        // Vérifier qu'il y a au moins 2 lignes
        if (ecriture.getLignes() == null || ecriture.getLignes().size() < 2) {
            erreurs.add("Une écriture doit avoir au moins 2 lignes");
        }
        
        // Vérifier les comptes
        if (ecriture.getLignes() != null) {
            for (LigneEcriture ligne : ecriture.getLignes()) {
                if (ligne.getCompte() == null) {
                    erreurs.add("Toutes les lignes doivent avoir un compte");
                }
                if (ligne.getDebit() == null && ligne.getCredit() == null) {
                    erreurs.add("Chaque ligne doit avoir un débit ou un crédit");
                }
                if (ligne.getDebit() != null && ligne.getCredit() != null && 
                    ligne.getDebit().compareTo(BigDecimal.ZERO) > 0 && 
                    ligne.getCredit().compareTo(BigDecimal.ZERO) > 0) {
                    erreurs.add("Une ligne ne peut pas avoir un débit et un crédit simultanément");
                }
            }
        }
        
        if (!erreurs.isEmpty()) {
            throw new RuntimeException("Erreurs de validation : " + String.join(", ", erreurs));
        }
    }
    
    /**
     * Appliquer les variables d'un template
     */
    private void appliquerVariablesTemplate(EcritureComptable ecriture, TemplateEcriture template, Map<String, Object> variables) {
        // Appliquer les variables de base
        if (variables.containsKey("dateEcriture")) {
            ecriture.setDateEcriture(LocalDate.parse((String) variables.get("dateEcriture")));
        } else {
            ecriture.setDateEcriture(LocalDate.now());
        }
        
        if (variables.containsKey("datePiece")) {
            ecriture.setDatePiece(LocalDate.parse((String) variables.get("datePiece")));
        } else {
            ecriture.setDatePiece(LocalDate.now());
        }
        
        if (variables.containsKey("reference")) {
            ecriture.setReference((String) variables.get("reference"));
        }
        
        if (variables.containsKey("libelle")) {
            ecriture.setLibelle((String) variables.get("libelle"));
        }
        
        if (variables.containsKey("devise")) {
            ecriture.setDevise((String) variables.get("devise"));
        }
    }
    
    /**
     * Générer les lignes d'écriture à partir d'un template
     */
    private List<LigneEcriture> genererLignesFromTemplate(TemplateEcriture template, Map<String, Object> variables) {
        List<LigneEcriture> lignes = new ArrayList<>();
        
        try {
            // Parser le pattern des comptes
            List<Map<String, Object>> comptesPattern = objectMapper.readValue(
                template.getComptesPattern(), 
                new TypeReference<List<Map<String, Object>>>() {}
            );
            

            
            int ordre = 1;
            for (Map<String, Object> comptePattern : comptesPattern) {
                LigneEcriture ligne = new LigneEcriture();
                
                // Position (débit/crédit)
                String position = (String) comptePattern.get("position");
                
                // Pattern du compte
                String comptePatternStr = (String) comptePattern.get("compte_pattern");
                
                // Trouver le compte correspondant
                Account compte = trouverCompteByPattern(comptePatternStr);
                ligne.setCompte(compte);
                ligne.setCompteNumero(compte.getAccountNumber());
                ligne.setCompteLibelle(compte.getName());
                
                // Libellé de la ligne
                ligne.setLibelleLigne((String) comptePattern.get("libelle"));
                
                // Calculer le montant selon la formule
                String formule = (String) comptePattern.get("formule");
                BigDecimal montant = calculerMontantFromFormule(formule, variables);
                
                if ("debit".equals(position)) {
                    ligne.setDebit(montant);
                    ligne.setCredit(BigDecimal.ZERO);
                } else {
                    ligne.setCredit(montant);
                    ligne.setDebit(BigDecimal.ZERO);
                }
                
                ligne.setOrdre(ordre++);
                lignes.add(ligne);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération des lignes : " + e.getMessage());
        }
        
        return lignes;
    }
    
    /**
     * Trouver un compte par pattern
     */
    private Account trouverCompteByPattern(String pattern) {
        // Implémentation simplifiée - à améliorer selon les besoins
        List<Account> comptes = accountRepository.findAll();
        
        for (Account compte : comptes) {
            if (compte.getAccountNumber().startsWith(pattern.replace("%", ""))) {
                return compte;
            }
        }
        
        throw new RuntimeException("Aucun compte trouvé pour le pattern : " + pattern);
    }
    
    /**
     * Calculer un montant à partir d'une formule
     */
    private BigDecimal calculerMontantFromFormule(String formule, Map<String, Object> variables) {
        // Implémentation simplifiée - à améliorer avec un moteur d'expression
        if (formule == null || formule.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Remplacer les variables dans la formule
        String formuleCalculee = formule;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            formuleCalculee = formuleCalculee.replace("${" + entry.getKey() + "}", entry.getValue().toString());
        }
        
        // Calcul simple - à améliorer
        try {
            return new BigDecimal(formuleCalculee);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erreur dans le calcul de la formule : " + formule);
        }
    }
}
