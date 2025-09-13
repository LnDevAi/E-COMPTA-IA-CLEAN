package com.ecomptaia.sycebnl.service;

import com.ecomptaia.sycebnl.entity.PlanComptableSycebnl;
import com.ecomptaia.sycebnl.repository.PlanComptableSycebnlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de gestion du plan comptable SYCEBNL
 * Gère la création, modification et consultation du plan comptable spécialisé ONG
 */
@Service
@Transactional(readOnly = true)
public class PlanComptableSycebnlService {
    
    private final PlanComptableSycebnlRepository planComptableRepository;
    
    @Autowired
    public PlanComptableSycebnlService(PlanComptableSycebnlRepository planComptableRepository) {
        this.planComptableRepository = planComptableRepository;
    }
    
    /**
     * Récupère tous les comptes du plan comptable
     */
    public List<PlanComptableSycebnl> getAllComptes() {
        return planComptableRepository.findAll().stream()
                .filter(PlanComptableSycebnl::getActif)
                .sorted(Comparator.comparing(PlanComptableSycebnl::getNumeroCompte))
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un compte par son numéro
     */
    public Optional<PlanComptableSycebnl> getCompteByNumero(String numeroCompte) {
        return planComptableRepository.findByNumeroCompteAndActifTrue(numeroCompte);
    }
    
    /**
     * Récupère tous les comptes d'une classe
     */
    public List<PlanComptableSycebnl> getComptesByClasse(PlanComptableSycebnl.ClasseCompte classeCompte) {
        return planComptableRepository.findByClasseCompteAndActifTrueOrderByNumeroCompte(classeCompte);
    }
    
    /**
     * Récupère tous les comptes d'un niveau
     */
    public List<PlanComptableSycebnl> getComptesByNiveau(Integer niveau) {
        return planComptableRepository.findByNiveauAndActifTrueOrderByNumeroCompte(niveau);
    }
    
    /**
     * Récupère les comptes enfants d'un compte parent
     */
    public List<PlanComptableSycebnl> getComptesEnfants(String compteParent) {
        return planComptableRepository.findByCompteParentAndActifTrueOrderByNumeroCompte(compteParent);
    }
    
    /**
     * Récupère les comptes utilisables pour le système normal
     */
    public List<PlanComptableSycebnl> getComptesSystemeNormal() {
        return planComptableRepository.findByUtiliseSystemeNormalTrueAndActifTrueOrderByNumeroCompte();
    }
    
    /**
     * Récupère les comptes utilisables pour le SMT
     */
    public List<PlanComptableSycebnl> getComptesSMT() {
        return planComptableRepository.findByUtiliseSMTTrueAndActifTrueOrderByNumeroCompte();
    }
    
    /**
     * Récupère les comptes obligatoires pour les ONG
     */
    public List<PlanComptableSycebnl> getComptesObligatoiresONG() {
        return planComptableRepository.findByObligatoireONGTrueAndActifTrueOrderByNumeroCompte();
    }
    
    /**
     * Récupère les comptes de trésorerie
     */
    public List<PlanComptableSycebnl> getComptesTresorerie() {
        return planComptableRepository.findComptesTresorerie();
    }
    
    /**
     * Récupère les comptes de charges
     */
    public List<PlanComptableSycebnl> getComptesCharges() {
        return planComptableRepository.findComptesCharges();
    }
    
    /**
     * Récupère les comptes de produits
     */
    public List<PlanComptableSycebnl> getComptesProduits() {
        return planComptableRepository.findComptesProduits();
    }
    
    /**
     * Récupère les comptes spécifiques aux ONG
     */
    public List<PlanComptableSycebnl> getComptesSpecifiquesONG() {
        return planComptableRepository.findComptesSpecifiquesONG();
    }
    
    /**
     * Recherche de comptes par libellé
     */
    public List<PlanComptableSycebnl> rechercherComptesParLibelle(String libelle) {
        return planComptableRepository.findByIntituleCompteContainingAndActifTrue(libelle);
    }
    
    /**
     * Recherche de comptes par pattern
     */
    public List<PlanComptableSycebnl> rechercherComptesParPattern(String pattern) {
        return planComptableRepository.findByNumeroCompteLikeAndActifTrue(pattern);
    }
    
    /**
     * Récupère la hiérarchie complète d'un compte
     */
    public List<PlanComptableSycebnl> getHierarchieCompte(String numeroCompte) {
        return planComptableRepository.findHierarchieCompte(numeroCompte);
    }
    
    /**
     * Crée un nouveau compte
     */
    @Transactional
    public PlanComptableSycebnl creerCompte(PlanComptableSycebnl compte) {
        // Validation des données
        validerCompte(compte);
        
        // Vérification de l'unicité du numéro
        if (planComptableRepository.findByNumeroCompteAndActifTrue(compte.getNumeroCompte()).isPresent()) {
            throw new RuntimeException("Un compte avec ce numéro existe déjà : " + compte.getNumeroCompte());
        }
        
        return planComptableRepository.save(compte);
    }
    
    /**
     * Met à jour un compte existant
     */
    @Transactional
    public PlanComptableSycebnl mettreAJourCompte(Long id, PlanComptableSycebnl compteModifie) {
        PlanComptableSycebnl compteExistant = planComptableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID : " + id));
        
        // Validation des données
        validerCompte(compteModifie);
        
        // Mise à jour des champs
        compteExistant.setIntituleCompte(compteModifie.getIntituleCompte());
        compteExistant.setClasseCompte(compteModifie.getClasseCompte());
        compteExistant.setTypeCompte(compteModifie.getTypeCompte());
        compteExistant.setNiveau(compteModifie.getNiveau());
        compteExistant.setCompteParent(compteModifie.getCompteParent());
        compteExistant.setSensNormal(compteModifie.getSensNormal());
        compteExistant.setUtiliseSystemeNormal(compteModifie.getUtiliseSystemeNormal());
        compteExistant.setUtiliseSMT(compteModifie.getUtiliseSMT());
        compteExistant.setObligatoireONG(compteModifie.getObligatoireONG());
        compteExistant.setDescriptionUtilisation(compteModifie.getDescriptionUtilisation());
        compteExistant.setActif(compteModifie.getActif());
        
        return planComptableRepository.save(compteExistant);
    }
    
    /**
     * Désactive un compte
     */
    @Transactional
    public void desactiverCompte(Long id) {
        PlanComptableSycebnl compte = planComptableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID : " + id));
        
        compte.setActif(false);
        planComptableRepository.save(compte);
    }
    
    /**
     * Supprime un compte (soft delete)
     */
    @Transactional
    public void supprimerCompte(Long id) {
        desactiverCompte(id);
    }
    
    /**
     * Valide un compte avant sauvegarde
     */
    private void validerCompte(PlanComptableSycebnl compte) {
        if (compte.getNumeroCompte() == null || compte.getNumeroCompte().trim().isEmpty()) {
            throw new RuntimeException("Le numéro de compte est obligatoire");
        }
        
        if (compte.getIntituleCompte() == null || compte.getIntituleCompte().trim().isEmpty()) {
            throw new RuntimeException("L'intitulé du compte est obligatoire");
        }
        
        if (compte.getClasseCompte() == null) {
            throw new RuntimeException("La classe de compte est obligatoire");
        }
        
        if (compte.getTypeCompte() == null) {
            throw new RuntimeException("Le type de compte est obligatoire");
        }
        
        if (compte.getSensNormal() == null) {
            throw new RuntimeException("Le sens normal du compte est obligatoire");
        }
        
        if (compte.getNiveau() == null || compte.getNiveau() < 1 || compte.getNiveau() > 3) {
            throw new RuntimeException("Le niveau doit être entre 1 et 3");
        }
        
        // Validation de la cohérence du numéro de compte avec la classe
        if (!estNumeroCompteValide(compte.getNumeroCompte(), compte.getClasseCompte())) {
            throw new RuntimeException("Le numéro de compte ne correspond pas à la classe sélectionnée");
        }
    }
    
    /**
     * Vérifie si un numéro de compte est valide pour une classe donnée
     */
    private boolean estNumeroCompteValide(String numeroCompte, PlanComptableSycebnl.ClasseCompte classeCompte) {
        if (numeroCompte == null || numeroCompte.length() < 1) {
            return false;
        }
        
        char premierChiffre = numeroCompte.charAt(0);
        
        switch (classeCompte) {
            case CLASSE_1:
                return premierChiffre == '1';
            case CLASSE_2:
                return premierChiffre == '2';
            case CLASSE_3:
                return premierChiffre == '3';
            case CLASSE_4:
                return premierChiffre == '4';
            case CLASSE_5:
                return premierChiffre == '5';
            case CLASSE_6:
                return premierChiffre == '6';
            case CLASSE_7:
                return premierChiffre == '7';
            case CLASSE_8:
                return premierChiffre == '8';
            default:
                return false;
        }
    }
    
    /**
     * Génère les statistiques du plan comptable
     */
    public Map<String, Object> getStatistiquesPlanComptable() {
        Map<String, Object> statistiques = new HashMap<>();
        
        // Nombre total de comptes
        long nombreTotalComptes = planComptableRepository.findAll().stream()
                .filter(PlanComptableSycebnl::getActif)
                .count();
        statistiques.put("nombreTotalComptes", nombreTotalComptes);
        
        // Nombre de comptes par classe
        List<Object[]> comptesParClasse = planComptableRepository.countByClasseCompte();
        Map<String, Long> repartitionParClasse = new HashMap<>();
        for (Object[] result : comptesParClasse) {
            PlanComptableSycebnl.ClasseCompte classe = (PlanComptableSycebnl.ClasseCompte) result[0];
            Long count = (Long) result[1];
            repartitionParClasse.put(classe.getLibelle(), count);
        }
        statistiques.put("repartitionParClasse", repartitionParClasse);
        
        // Nombre de comptes par niveau
        Map<Integer, Long> repartitionParNiveau = new HashMap<>();
        for (int niveau = 1; niveau <= 3; niveau++) {
            long count = planComptableRepository.findByNiveauAndActifTrueOrderByNumeroCompte(niveau).size();
            repartitionParNiveau.put(niveau, count);
        }
        statistiques.put("repartitionParNiveau", repartitionParNiveau);
        
        // Nombre de comptes par système
        long comptesSystemeNormal = planComptableRepository.findByUtiliseSystemeNormalTrueAndActifTrueOrderByNumeroCompte().size();
        long comptesSMT = planComptableRepository.findByUtiliseSMTTrueAndActifTrueOrderByNumeroCompte().size();
        long comptesObligatoiresONG = planComptableRepository.findByObligatoireONGTrueAndActifTrueOrderByNumeroCompte().size();
        
        statistiques.put("comptesSystemeNormal", comptesSystemeNormal);
        statistiques.put("comptesSMT", comptesSMT);
        statistiques.put("comptesObligatoiresONG", comptesObligatoiresONG);
        
        // Comptes spécifiques
        statistiques.put("comptesTresorerie", planComptableRepository.findComptesTresorerie().size());
        statistiques.put("comptesCharges", planComptableRepository.findComptesCharges().size());
        statistiques.put("comptesProduits", planComptableRepository.findComptesProduits().size());
        statistiques.put("comptesSpecifiquesONG", planComptableRepository.findComptesSpecifiquesONG().size());
        
        return statistiques;
    }
    
    /**
     * Génère le plan comptable par défaut pour les ONG
     */
    @Transactional
    public void initialiserPlanComptableDefaut() {
        // Vérifier si le plan comptable est déjà initialisé
        if (!planComptableRepository.findAll().isEmpty()) {
            return; // Déjà initialisé
        }
        
        List<PlanComptableSycebnl> comptes = new ArrayList<>();
        
        // Classes principales
        comptes.add(PlanComptableSycebnl.builder()
                .numeroCompte("1")
                .intituleCompte("FINANCEMENT PERMANENT")
                .classeCompte(PlanComptableSycebnl.ClasseCompte.CLASSE_1)
                .typeCompte(PlanComptableSycebnl.TypeCompte.CLASSE)
                .niveau(1)
                .sensNormal(PlanComptableSycebnl.SensNormalCompte.CREDITEUR)
                .utiliseSystemeNormal(true)
                .utiliseSMT(true)
                .obligatoireONG(false)
                .build());
        
        // Comptes spécifiques ONG
        comptes.add(PlanComptableSycebnl.builder()
                .numeroCompte("101")
                .intituleCompte("Fonds associatifs")
                .classeCompte(PlanComptableSycebnl.ClasseCompte.CLASSE_1)
                .typeCompte(PlanComptableSycebnl.TypeCompte.COMPTE)
                .niveau(2)
                .compteParent("1")
                .sensNormal(PlanComptableSycebnl.SensNormalCompte.CREDITEUR)
                .utiliseSystemeNormal(true)
                .utiliseSMT(true)
                .obligatoireONG(true)
                .descriptionUtilisation("Capital social des associations")
                .build());
        
        // Sauvegarde de tous les comptes
        planComptableRepository.saveAll(comptes);
    }
}
