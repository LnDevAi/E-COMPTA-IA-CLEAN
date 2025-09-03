package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SMTService {

    @Autowired
    private EntrepriseSMTRepository entrepriseSMTRepository;

    @Autowired
    private ExerciceSMTRepository exerciceSMTRepository;

    @Autowired
    private CompteTresorerieRepository compteTresorerieRepository;

    @Autowired
    private LivreRecetteRepository livreRecetteRepository;

    @Autowired
    private LivreDepenseRepository livreDepenseRepository;

    @Autowired
    private EtatFinancierRepository etatFinancierRepository;

    // ==================== GESTION DES ENTREPRISES SMT ====================

    /**
     * Créer une nouvelle entreprise SMT
     */
    public EntrepriseSMT creerEntreprise(EntrepriseSMT entreprise) {
        entreprise.setDateCreation(LocalDateTime.now());
        entreprise.setEstActif(true);
        entreprise.setStatut("ACTIVE");
        return entrepriseSMTRepository.save(entreprise);
    }

    /**
     * Obtenir toutes les entreprises actives
     */
    public List<EntrepriseSMT> getAllEntreprisesActives() {
        return entrepriseSMTRepository.findByEstActifTrue();
    }

    /**
     * Obtenir une entreprise par ID
     */
    public Optional<EntrepriseSMT> getEntrepriseById(Long id) {
        return entrepriseSMTRepository.findById(id);
    }

    /**
     * Obtenir les statistiques des entreprises
     */
    public Map<String, Object> getStatistiquesEntreprises() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEntreprises", entrepriseSMTRepository.countActiveEntreprises());
        stats.put("parPays", entrepriseSMTRepository.getStatisticsByPays());
        stats.put("parRegimeFiscal", entrepriseSMTRepository.getStatisticsByRegimeFiscal());
        stats.put("parDevise", entrepriseSMTRepository.getStatisticsByDevise());
        return stats;
    }

    // ==================== GESTION DES EXERCICES SMT ====================

    /**
     * Créer un nouvel exercice SMT
     */
    public ExerciceSMT creerExercice(ExerciceSMT exercice) {
        exercice.setDateCreation(LocalDateTime.now());
        exercice.setEstCloture(false);
        exercice.setStatut("OUVERT");
        return exerciceSMTRepository.save(exercice);
    }

    /**
     * Obtenir l'exercice en cours d'une entreprise
     */
    public Optional<ExerciceSMT> getExerciceEnCours(Long entrepriseId) {
        return exerciceSMTRepository.findExerciceEnCours(entrepriseId);
    }

    /**
     * Clôturer un exercice
     */
    public ExerciceSMT cloturerExercice(Long exerciceId) {
        Optional<ExerciceSMT> optExercice = exerciceSMTRepository.findById(exerciceId);
        if (optExercice.isPresent()) {
            ExerciceSMT exercice = optExercice.get();
            exercice.setEstCloture(true);
            exercice.setDateCloture(LocalDateTime.now());
            exercice.setStatut("CLOTURE");
            
            // Calculer les totaux
            calculerTotauxExercice(exercice);
            
            return exerciceSMTRepository.save(exercice);
        }
        return null;
    }

    /**
     * Calculer les totaux d'un exercice
     */
    private void calculerTotauxExercice(ExerciceSMT exercice) {
        // Calculer le total des recettes
        BigDecimal totalRecettes = livreRecetteRepository.getMontantTotal(exercice.getId());
        exercice.setTotalRecettes(totalRecettes != null ? totalRecettes : BigDecimal.ZERO);

        // Calculer le total des dépenses
        BigDecimal totalDepenses = livreDepenseRepository.getMontantTotal(exercice.getId());
        exercice.setTotalDepenses(totalDepenses != null ? totalDepenses : BigDecimal.ZERO);

        // Calculer le solde de trésorerie
        BigDecimal soldeTresorerie = BigDecimal.ZERO;
        List<CompteTresorerie> comptes = compteTresorerieRepository.findByEntrepriseIdAndEstActifTrueOrderByNomCompte(exercice.getEntreprise().getId());
        for (CompteTresorerie compte : comptes) {
            soldeTresorerie = soldeTresorerie.add(compte.getSoldeActuel());
        }
        exercice.setSoldeTresorerie(soldeTresorerie);
    }

    // ==================== GESTION DES COMPTES DE TRÉSORERIE ====================

    /**
     * Créer un nouveau compte de trésorerie
     */
    public CompteTresorerie creerCompteTresorerie(CompteTresorerie compte) {
        compte.setDateCreation(LocalDateTime.now());
        compte.setEstActif(true);
        compte.setStatut("ACTIVE");
        return compteTresorerieRepository.save(compte);
    }

    /**
     * Obtenir tous les comptes d'une entreprise
     */
    public List<CompteTresorerie> getComptesTresorerie(Long entrepriseId) {
        return compteTresorerieRepository.findByEntrepriseIdAndEstActifTrueOrderByNomCompte(entrepriseId);
    }

    /**
     * Effectuer un mouvement de trésorerie
     */
    public void effectuerMouvementTresorerie(Long compteId, BigDecimal montant, boolean isDebit) {
        Optional<CompteTresorerie> optCompte = compteTresorerieRepository.findById(compteId);
        if (optCompte.isPresent()) {
            CompteTresorerie compte = optCompte.get();
            if (isDebit) {
                compte.debiter(montant);
            } else {
                compte.crediter(montant);
            }
            compteTresorerieRepository.save(compte);
        }
    }

    // ==================== GESTION DES RECETTES ====================

    /**
     * Enregistrer une nouvelle recette
     */
    public LivreRecette enregistrerRecette(LivreRecette recette) {
        recette.setDateCreation(LocalDateTime.now());
        recette.setStatut("VALIDE");
        
        // Créditer le compte de trésorerie
        effectuerMouvementTresorerie(recette.getCompteTresorerie().getId(), recette.getMontant(), false);
        
        return livreRecetteRepository.save(recette);
    }

    /**
     * Obtenir les recettes d'un exercice
     */
    public List<LivreRecette> getRecettesExercice(Long exerciceId) {
        return livreRecetteRepository.findByExerciceIdOrderByDateRecetteDesc(exerciceId);
    }

    /**
     * Obtenir les statistiques des recettes
     */
    public Map<String, Object> getStatistiquesRecettes(Long exerciceId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecettes", livreRecetteRepository.getMontantTotal(exerciceId));
        stats.put("parType", livreRecetteRepository.getStatisticsByTypeRecette(exerciceId));
        stats.put("parModePaiement", livreRecetteRepository.getStatisticsByModePaiement(exerciceId));
        stats.put("parMois", livreRecetteRepository.getStatisticsByMonth(exerciceId));
        return stats;
    }

    // ==================== GESTION DES DÉPENSES ====================

    /**
     * Enregistrer une nouvelle dépense
     */
    public LivreDepense enregistrerDepense(LivreDepense depense) {
        depense.setDateCreation(LocalDateTime.now());
        depense.setStatut("VALIDE");
        
        // Débiter le compte de trésorerie
        effectuerMouvementTresorerie(depense.getCompteTresorerie().getId(), depense.getMontant(), true);
        
        return livreDepenseRepository.save(depense);
    }

    /**
     * Obtenir les dépenses d'un exercice
     */
    public List<LivreDepense> getDepensesExercice(Long exerciceId) {
        return livreDepenseRepository.findByExerciceIdOrderByDateDepenseDesc(exerciceId);
    }

    /**
     * Obtenir les statistiques des dépenses
     */
    public Map<String, Object> getStatistiquesDepenses(Long exerciceId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDepenses", livreDepenseRepository.getMontantTotal(exerciceId));
        stats.put("parType", livreDepenseRepository.getStatisticsByTypeDepense(exerciceId));
        stats.put("parModePaiement", livreDepenseRepository.getStatisticsByModePaiement(exerciceId));
        stats.put("parMois", livreDepenseRepository.getStatisticsByMonth(exerciceId));
        return stats;
    }

    // ==================== GÉNÉRATION D'ÉTATS FINANCIERS ====================

    /**
     * Générer un état financier
     */
    public EtatFinancier genererEtatFinancier(Long exerciceId, String typeEtat, String standardComptable) {
        Optional<ExerciceSMT> optExercice = exerciceSMTRepository.findById(exerciceId);
        if (optExercice.isPresent()) {
            ExerciceSMT exercice = optExercice.get();
            
            EtatFinancier etat = new EtatFinancier(exercice, typeEtat, standardComptable);
            
            // Calculer les données selon le type d'état
            switch (typeEtat) {
                case "BILAN":
                    genererDonneesBilan(etat, exercice);
                    break;
                case "COMPTE_RESULTAT":
                    genererDonneesCompteResultat(etat, exercice);
                    break;
                case "FLUX_TRESORERIE":
                    genererDonneesFluxTresorerie(etat, exercice);
                    break;
                case "ANNEXES":
                    genererDonneesAnnexes(etat, exercice);
                    break;
            }
            
            return etatFinancierRepository.save(etat);
        }
        return null;
    }

    /**
     * Générer les données du bilan
     */
    private void genererDonneesBilan(EtatFinancier etat, ExerciceSMT exercice) {
        // Calculer les actifs (comptes de trésorerie)
        BigDecimal totalActifs = BigDecimal.ZERO;
        List<CompteTresorerie> comptes = compteTresorerieRepository.findByEntrepriseIdAndEstActifTrueOrderByNomCompte(exercice.getEntreprise().getId());
        for (CompteTresorerie compte : comptes) {
            if (compte.getSoldeActuel().compareTo(BigDecimal.ZERO) > 0) {
                totalActifs = totalActifs.add(compte.getSoldeActuel());
            }
        }
        etat.setTotalActifs(totalActifs);
        
        // Pour SMT, les passifs sont généralement les dettes (dépenses non payées)
        BigDecimal totalPassifs = BigDecimal.ZERO;
        // Logique simplifiée pour les passifs SMT
        etat.setTotalPassifs(totalPassifs);
        
        // Créer les données JSON détaillées
        Map<String, Object> donnees = new HashMap<>();
        donnees.put("actifs", comptes.stream()
                .filter(c -> c.getSoldeActuel().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toMap(CompteTresorerie::getNomCompte, CompteTresorerie::getSoldeActuel)));
        donnees.put("totalActifs", totalActifs);
        donnees.put("totalPassifs", totalPassifs);
        donnees.put("soldeNet", etat.getSoldeNet());
        
        etat.setDonneesJson(convertToJson(donnees));
    }

    /**
     * Générer les données du compte de résultat
     */
    private void genererDonneesCompteResultat(EtatFinancier etat, ExerciceSMT exercice) {
        // Calculer les produits (recettes)
        BigDecimal totalProduits = livreRecetteRepository.getMontantTotal(exercice.getId());
        etat.setTotalProduits(totalProduits != null ? totalProduits : BigDecimal.ZERO);
        
        // Calculer les charges (dépenses)
        BigDecimal totalCharges = livreDepenseRepository.getMontantTotal(exercice.getId());
        etat.setTotalCharges(totalCharges != null ? totalCharges : BigDecimal.ZERO);
        
        // Calculer le résultat net
        etat.setResultatNet(etat.getResultatAvantImpot());
        
        // Créer les données JSON détaillées
        Map<String, Object> donnees = new HashMap<>();
        donnees.put("produits", livreRecetteRepository.getMontantTotalByTypeRecette(exercice.getId()));
        donnees.put("charges", livreDepenseRepository.getMontantTotalByTypeDepense(exercice.getId()));
        donnees.put("totalProduits", etat.getTotalProduits());
        donnees.put("totalCharges", etat.getTotalCharges());
        donnees.put("resultatNet", etat.getResultatNet());
        
        etat.setDonneesJson(convertToJson(donnees));
    }

    /**
     * Générer les données du flux de trésorerie
     */
    private void genererDonneesFluxTresorerie(EtatFinancier etat, ExerciceSMT exercice) {
        // Flux de trésorerie d'exploitation (recettes - dépenses)
        BigDecimal fluxExploitation = etat.getResultatAvantImpot();
        
        // Créer les données JSON détaillées
        Map<String, Object> donnees = new HashMap<>();
        donnees.put("fluxExploitation", fluxExploitation);
        donnees.put("recettes", livreRecetteRepository.getMontantTotalByModePaiement(exercice.getId()));
        donnees.put("depenses", livreDepenseRepository.getMontantTotalByModePaiement(exercice.getId()));
        
        etat.setDonneesJson(convertToJson(donnees));
    }

    /**
     * Générer les données des annexes
     */
    private void genererDonneesAnnexes(EtatFinancier etat, ExerciceSMT exercice) {
        // Données complémentaires pour les annexes
        Map<String, Object> donnees = new HashMap<>();
        donnees.put("statistiquesRecettes", livreRecetteRepository.getStatisticsByTypeRecette(exercice.getId()));
        donnees.put("statistiquesDepenses", livreDepenseRepository.getStatisticsByTypeDepense(exercice.getId()));
        donnees.put("comptesTresorerie", compteTresorerieRepository.getStatisticsByTypeCompte(exercice.getEntreprise().getId()));
        
        etat.setDonneesJson(convertToJson(donnees));
    }

    /**
     * Convertir un objet en JSON (simplifié)
     */
    private String convertToJson(Object obj) {
        // Implémentation simplifiée - en production, utiliser Jackson ou Gson
        return obj.toString();
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Obtenir le tableau de bord SMT d'une entreprise
     */
    public Map<String, Object> getTableauBord(Long entrepriseId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Informations de l'entreprise
        Optional<EntrepriseSMT> entreprise = entrepriseSMTRepository.findById(entrepriseId);
        if (entreprise.isPresent()) {
            dashboard.put("entreprise", entreprise.get());
            
            // Exercice en cours
            Optional<ExerciceSMT> exerciceEnCours = exerciceSMTRepository.findExerciceEnCours(entrepriseId);
            dashboard.put("exerciceEnCours", exerciceEnCours.orElse(null));
            
            // Comptes de trésorerie
            List<CompteTresorerie> comptes = compteTresorerieRepository.findByEntrepriseIdAndEstActifTrueOrderByNomCompte(entrepriseId);
            dashboard.put("comptesTresorerie", comptes);
            
            // Solde total de trésorerie
            BigDecimal soldeTotal = comptes.stream()
                    .map(CompteTresorerie::getSoldeActuel)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dashboard.put("soldeTotalTresorerie", soldeTotal);
            
            // Statistiques de l'exercice en cours
            if (exerciceEnCours.isPresent()) {
                Long exerciceId = exerciceEnCours.get().getId();
                dashboard.put("statistiquesRecettes", getStatistiquesRecettes(exerciceId));
                dashboard.put("statistiquesDepenses", getStatistiquesDepenses(exerciceId));
            }
        }
        
        return dashboard;
    }

    /**
     * Valider un état financier
     */
    public EtatFinancier validerEtatFinancier(Long etatId) {
        Optional<EtatFinancier> optEtat = etatFinancierRepository.findById(etatId);
        if (optEtat.isPresent()) {
            EtatFinancier etat = optEtat.get();
            etat.setStatut("VALIDATED");
            etat.setDateModification(LocalDateTime.now());
            return etatFinancierRepository.save(etat);
        }
        return null;
    }

    /**
     * Publier un état financier
     */
    public EtatFinancier publierEtatFinancier(Long etatId) {
        Optional<EtatFinancier> optEtat = etatFinancierRepository.findById(etatId);
        if (optEtat.isPresent()) {
            EtatFinancier etat = optEtat.get();
            etat.setStatut("PUBLISHED");
            etat.setDateModification(LocalDateTime.now());
            return etatFinancierRepository.save(etat);
        }
        return null;
    }
}
