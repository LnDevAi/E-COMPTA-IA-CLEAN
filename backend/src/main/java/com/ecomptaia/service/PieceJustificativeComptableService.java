ackage com.ecomptaia.service;

import com.ecomptaia.entity.EcritureComptable;
import com.ecomptaia.entity.LigneEcriture;
import com.ecomptaia.entity.PieceJustificativeComptable;
import com.ecomptaia.security.entity.Company;
import com.ecomptaia.security.entity.User;
import com.ecomptaia.entity.Account;
import com.ecomptaia.entity.FinancialPeriod;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des Pièces Justificatives Comptables
 * Inclut l'analyse OCR, la lecture IA et la génération d'écritures
 */
@Service
@Transactional
public class PieceJustificativeComptableService {
    
    @Autowired
    private PieceJustificativeComptableRepository pjRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private FinancialPeriodRepository periodRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private EcritureComptableRepository ecritureRepository;
    
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // ==================== GESTION DES FICHIERS ====================
    
    /**
     * Télécharger et enregistrer une PJ comptable
     */
    public PieceJustificativeComptable uploadPieceJustificative(
            MultipartFile file, Long companyId, Long exerciceId, Long userId,
            String libelle, String description, PieceJustificativeComptable.TypeDocument typeDocument) {
        
        try {
            // Vérifier les entités
            Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
            FinancialPeriod exercice = periodRepository.findById(exerciceId)
                .orElseThrow(() -> new RuntimeException("Exercice non trouvé"));
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            // Générer le numéro PJ
            String numeroPJ = genererNumeroPJ(companyId, exerciceId);
            
            // Créer le répertoire de stockage
            String uploadDir = "uploads/pj-comptables/" + companyId + "/" + exerciceId;
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Sauvegarder le fichier
            String fileName = numeroPJ + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // Créer l'entité PJ
            PieceJustificativeComptable pj = new PieceJustificativeComptable();
            pj.setNumeroPJ(numeroPJ);
            pj.setNomFichier(file.getOriginalFilename());
            pj.setCheminFichier(filePath.toString());
            pj.setTypeFichier(file.getContentType());
            pj.setTailleFichier(file.getSize());
            pj.setLibelle(libelle != null ? libelle : "PJ - " + file.getOriginalFilename());
            pj.setDescription(description);
            pj.setTypeDocument(typeDocument);
            pj.setCompany(company);
            pj.setExercice(exercice);
            pj.setUploadedBy(user);
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.UPLOADED);
            
            // Sauvegarder en base
            pj = pjRepository.save(pj);
            
            // Démarrer le traitement automatique
            traiterPieceJustificativeAutomatiquement(pj.getId());
            
            return pj;
            
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du téléchargement du fichier", e);
        }
    }
    
    /**
     * Traitement automatique d'une PJ (OCR + IA)
     */
    public void traiterPieceJustificativeAutomatiquement(Long pjId) {
        PieceJustificativeComptable pj = pjRepository.findById(pjId)
            .orElseThrow(() -> new RuntimeException("PJ non trouvée"));
        
        // 1. Traitement OCR
        traiterOCR(pj);
        
        // 2. Traitement IA (après OCR)
        if (pj.getOcrStatut() == PieceJustificativeComptable.StatutOCR.COMPLETED) {
            traiterIA(pj);
        }
    }
    
    // ==================== TRAITEMENT OCR ====================
    
    /**
     * Traitement OCR d'une PJ
     */
    public void traiterOCR(PieceJustificativeComptable pj) {
        try {
            pj.setOcrStatut(PieceJustificativeComptable.StatutOCR.PROCESSING);
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.OCR_EN_COURS);
            pjRepository.save(pj);
            
            // Simulation du traitement OCR (à remplacer par un vrai service OCR)
            String ocrText = simulerOCR(pj.getCheminFichier(), pj.getTypeFichier());
            BigDecimal ocrConfidence = new BigDecimal("0.85"); // Simulation
            
            pj.setOcrText(ocrText);
            pj.setOcrConfidence(ocrConfidence);
            pj.setOcrDateTraitement(LocalDateTime.now());
            pj.setOcrStatut(PieceJustificativeComptable.StatutOCR.COMPLETED);
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.OCR_TERMINE);
            
            pjRepository.save(pj);
            
        } catch (Exception e) {
            pj.setOcrStatut(PieceJustificativeComptable.StatutOCR.FAILED);
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.ERROR);
            pjRepository.save(pj);
            throw new RuntimeException("Erreur lors du traitement OCR", e);
        }
    }
    
    /**
     * Simulation du traitement OCR (à remplacer par un vrai service)
     */
    private String simulerOCR(String cheminFichier, String typeFichier) {
        // Simulation basée sur le type de fichier
        if (typeFichier.contains("pdf") || typeFichier.contains("image")) {
            return "FACTURE N° F2024-001\n" +
                   "Date: 15/12/2024\n" +
                   "Fournisseur: ACME SARL\n" +
                   "Montant HT: 100,000.00 XOF\n" +
                   "TVA 18%: 18,000.00 XOF\n" +
                   "Montant TTC: 118,000.00 XOF\n" +
                   "Compte: 401 - Fournisseurs\n" +
                   "Libellé: Achat de matériel informatique";
        }
        return "Texte extrait par OCR";
    }
    
    // ==================== TRAITEMENT IA ====================
    
    /**
     * Traitement IA d'une PJ
     */
    public void traiterIA(PieceJustificativeComptable pj) {
        try {
            pj.setIaStatut(PieceJustificativeComptable.StatutIA.PROCESSING);
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.IA_EN_COURS);
            pjRepository.save(pj);
            
            // Analyse IA du texte OCR
            Map<String, Object> analyseIA = analyserAvecIA(pj.getOcrText(), pj.getTypeDocument());
            
            pj.setIaAnalyse(analyseIA.get("analyse").toString());
            pj.setIaConfidence((BigDecimal) analyseIA.get("confidence"));
            pj.setIaDateTraitement(LocalDateTime.now());
            pj.setIaStatut(PieceJustificativeComptable.StatutIA.COMPLETED);
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.IA_TERMINE);
            
            // Extraire les informations détectées
            if (analyseIA.containsKey("montant")) {
                pj.setMontantDetecte((BigDecimal) analyseIA.get("montant"));
            }
            if (analyseIA.containsKey("devise")) {
                pj.setDeviseDetectee(analyseIA.get("devise").toString());
            }
            if (analyseIA.containsKey("date")) {
                pj.setDateDocument((LocalDate) analyseIA.get("date"));
            }
            
            // Générer les propositions d'écritures
            String propositions = genererPropositionsEcritures(pj, analyseIA);
            pj.setPropositionsEcritures(propositions);
            pj.setComptesSuggerees(genererComptesSuggerees(pj, analyseIA));
            pj.setIaSuggestions(analyseIA.get("suggestions").toString());
            
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.PROPOSITIONS_READY);
            pjRepository.save(pj);
            
        } catch (Exception e) {
            pj.setIaStatut(PieceJustificativeComptable.StatutIA.FAILED);
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.ERROR);
            pjRepository.save(pj);
            throw new RuntimeException("Erreur lors du traitement IA", e);
        }
    }
    
    /**
     * Analyse IA du texte OCR
     */
    private Map<String, Object> analyserAvecIA(String ocrText, PieceJustificativeComptable.TypeDocument typeDocument) {
        Map<String, Object> resultat = new HashMap<>();
        
        // Simulation de l'analyse IA basée sur le type de document
        switch (typeDocument) {
            case FACTURE_FOURNISSEUR:
                resultat.put("analyse", "Facture fournisseur détectée avec achat de matériel");
                resultat.put("montant", new BigDecimal("118000.00"));
                resultat.put("devise", "XOF");
                resultat.put("date", LocalDate.of(2024, 12, 15));
                resultat.put("confidence", new BigDecimal("0.92"));
                resultat.put("suggestions", "Comptes suggérés: 401 (Fournisseurs), 601 (Achats), 44566 (TVA déductible)");
                break;
            case FACTURE_CLIENT:
                resultat.put("analyse", "Facture client détectée avec vente de services");
                resultat.put("montant", new BigDecimal("150000.00"));
                resultat.put("devise", "XOF");
                resultat.put("date", LocalDate.now());
                resultat.put("confidence", new BigDecimal("0.88"));
                resultat.put("suggestions", "Comptes suggérés: 411 (Clients), 701 (Ventes), 44571 (TVA collectée)");
                break;
            default:
                resultat.put("analyse", "Document comptable générique détecté");
                resultat.put("confidence", new BigDecimal("0.75"));
                resultat.put("suggestions", "Vérification manuelle recommandée");
        }
        
        return resultat;
    }
    
    /**
     * Générer les propositions d'écritures
     */
    private String genererPropositionsEcritures(PieceJustificativeComptable pj, Map<String, Object> analyseIA) {
        try {
            Map<String, Object> propositions = new HashMap<>();
            
            if (pj.getTypeDocument() == PieceJustificativeComptable.TypeDocument.FACTURE_FOURNISSEUR) {
                propositions.put("type", "ACHAT");
                propositions.put("journal", "ACH");
                
                List<Map<String, Object>> lignes = new ArrayList<>();
                
                // Ligne 1: Débit - Compte d'achat
                Map<String, Object> ligne1 = new HashMap<>();
                ligne1.put("compte", "601");
                ligne1.put("libelle", "Achat de matériel");
                ligne1.put("debit", new BigDecimal("100000.00"));
                ligne1.put("credit", BigDecimal.ZERO);
                lignes.add(ligne1);
                
                // Ligne 2: Débit - TVA déductible
                Map<String, Object> ligne2 = new HashMap<>();
                ligne2.put("compte", "44566");
                ligne2.put("libelle", "TVA déductible");
                ligne2.put("debit", new BigDecimal("18000.00"));
                ligne2.put("credit", BigDecimal.ZERO);
                lignes.add(ligne2);
                
                // Ligne 3: Crédit - Fournisseur
                Map<String, Object> ligne3 = new HashMap<>();
                ligne3.put("compte", "401");
                ligne3.put("libelle", "Fournisseur ACME SARL");
                ligne3.put("debit", BigDecimal.ZERO);
                ligne3.put("credit", new BigDecimal("118000.00"));
                lignes.add(ligne3);
                
                propositions.put("lignes", lignes);
                propositions.put("totalDebit", new BigDecimal("118000.00"));
                propositions.put("totalCredit", new BigDecimal("118000.00"));
            }
            
            return objectMapper.writeValueAsString(propositions);
            
        } catch (Exception e) {
            return "{}";
        }
    }
    
    /**
     * Générer les comptes suggérés
     */
    private String genererComptesSuggerees(PieceJustificativeComptable pj, Map<String, Object> analyseIA) {
        try {
            List<Map<String, Object>> comptes = new ArrayList<>();
            
            if (pj.getTypeDocument() == PieceJustificativeComptable.TypeDocument.FACTURE_FOURNISSEUR) {
                comptes.add(createCompteSuggestion("401", "Fournisseurs", "PASSIF", 0.95));
                comptes.add(createCompteSuggestion("601", "Achats de matières premières", "CHARGES", 0.90));
                comptes.add(createCompteSuggestion("44566", "TVA déductible", "ACTIF", 0.85));
            }
            
            return objectMapper.writeValueAsString(comptes);
            
        } catch (Exception e) {
            return "[]";
        }
    }
    
    private Map<String, Object> createCompteSuggestion(String numero, String libelle, String type, double confidence) {
        Map<String, Object> compte = new HashMap<>();
        compte.put("numero", numero);
        compte.put("libelle", libelle);
        compte.put("type", type);
        compte.put("confidence", new BigDecimal(confidence));
        return compte;
    }
    
    // ==================== VALIDATION ET GÉNÉRATION D'ÉCRITURES ====================
    
    /**
     * Valider les propositions et générer l'écriture
     */
    public EcritureComptable validerEtGenererEcriture(Long pjId, String propositionsValidees, Long userId) {
        PieceJustificativeComptable pj = pjRepository.findById(pjId)
            .orElseThrow(() -> new RuntimeException("PJ non trouvée"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        try {
            // Créer l'écriture comptable
            EcritureComptable ecriture = new EcritureComptable();
            ecriture.setNumeroPiece(genererNumeroEcriture(pj.getCompany().getId(), pj.getExercice().getId()));
            ecriture.setDateEcriture(LocalDate.now());
            ecriture.setDatePiece(pj.getDateDocument() != null ? pj.getDateDocument() : LocalDate.now());
            ecriture.setLibelle("Écriture générée depuis PJ: " + pj.getNumeroPJ());
            ecriture.setTypeEcriture(EcritureComptable.TypeEcriture.NORMALE);
            ecriture.setStatut(EcritureComptable.StatutEcriture.VALIDEE);
            ecriture.setEntreprise(pj.getCompany());
            ecriture.setExercice(pj.getExercice());
            ecriture.setUtilisateur(user);
            ecriture.setDevise(pj.getDeviseDetectee() != null ? pj.getDeviseDetectee() : "XOF");
            ecriture.setSource(EcritureComptable.SourceEcriture.IA);
            ecriture.setValidationAiConfiance(pj.getIaConfidence());
            ecriture.setValidationAiSuggestions(pj.getIaSuggestions());
            
            // Parser les propositions validées
            @SuppressWarnings("unchecked")
            Map<String, Object> propositions = objectMapper.readValue(propositionsValidees, Map.class);
            
            // Créer les lignes d'écriture
            List<LigneEcriture> lignes = new ArrayList<>();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lignesData = (List<Map<String, Object>>) propositions.get("lignes");
            
            for (Map<String, Object> ligneData : lignesData) {
                LigneEcriture ligne = new LigneEcriture();
                ligne.setEcriture(ecriture);
                
                // Trouver le compte
                String numeroCompte = ligneData.get("compte").toString();
                Account compte = accountRepository.findByAccountNumber(numeroCompte)
                    .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + numeroCompte));
                
                ligne.setCompte(compte);
                ligne.setCompteNumero(compte.getAccountNumber());
                ligne.setCompteLibelle(compte.getName());
                ligne.setLibelleLigne(ligneData.get("libelle").toString());
                ligne.setDebit((BigDecimal) ligneData.get("debit"));
                ligne.setCredit((BigDecimal) ligneData.get("credit"));
                
                lignes.add(ligne);
            }
            
            ecriture.setLignes(lignes);
            ecriture.calculerTotaux();
            
            // Sauvegarder l'écriture
            ecriture = ecritureRepository.save(ecriture);
            
            // Mettre à jour la PJ
            pj.setValidatedBy(user);
            pj.setDateValidation(LocalDateTime.now());
            pj.setEcritureGenereeId(ecriture.getId());
            pj.setNumeroEcriture(ecriture.getNumeroPiece());
            pj.setDateGenerationEcriture(LocalDateTime.now());
            pj.setStatutTraitement(PieceJustificativeComptable.StatutTraitement.ECRITURE_GENERE);
            pj.setJournalComptable(propositions.get("journal").toString());
            
            // Créer la fiche d'imputation
            pj.setFicheImputation(creerFicheImputation(pj, ecriture));
            
            pjRepository.save(pj);
            
            return ecriture;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de l'écriture", e);
        }
    }
    
    /**
     * Créer la fiche d'imputation
     */
    private String creerFicheImputation(PieceJustificativeComptable pj, EcritureComptable ecriture) {
        try {
            Map<String, Object> fiche = new HashMap<>();
            fiche.put("numeroPJ", pj.getNumeroPJ());
            fiche.put("numeroEcriture", ecriture.getNumeroPiece());
            fiche.put("dateDocument", pj.getDateDocument());
            fiche.put("dateEcriture", ecriture.getDateEcriture());
            fiche.put("libelle", ecriture.getLibelle());
            fiche.put("montant", pj.getMontantDetecte());
            fiche.put("devise", pj.getDeviseDetectee());
            fiche.put("journal", pj.getJournalComptable());
            fiche.put("typeDocument", pj.getTypeDocument());
            fiche.put("lignes", ecriture.getLignes().stream().map(ligne -> {
                Map<String, Object> ligneData = new HashMap<>();
                ligneData.put("compte", ligne.getCompte().getAccountNumber());
                ligneData.put("libelle", ligne.getLibelleLigne());
                ligneData.put("debit", ligne.getDebit());
                ligneData.put("credit", ligne.getCredit());
                return ligneData;
            }).collect(Collectors.toList()));
            
            return objectMapper.writeValueAsString(fiche);
            
        } catch (Exception e) {
            return "{}";
        }
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    /**
     * Générer un numéro PJ unique
     */
    private String genererNumeroPJ(Long companyId, Long exerciceId) {
        String prefix = "PJ" + companyId + exerciceId;
        long count = pjRepository.count() + 1;
        return prefix + String.format("%06d", count);
    }
    
    /**
     * Générer un numéro d'écriture unique
     */
    private String genererNumeroEcriture(Long companyId, Long exerciceId) {
        String prefix = "EC" + companyId + exerciceId;
        long count = ecritureRepository.count() + 1;
        return prefix + String.format("%06d", count);
    }
    
    // ==================== MÉTHODES DE CONSULTATION ====================
    
    /**
     * Obtenir une PJ par ID
     */
    public Optional<PieceJustificativeComptable> getPJById(Long id) {
        return pjRepository.findById(id);
    }
    
    /**
     * Obtenir toutes les PJ d'une entreprise
     */
    public List<PieceJustificativeComptable> getPJByCompany(Long companyId) {
        return pjRepository.findByCompanyIdOrderByDateUploadDesc(companyId);
    }
    
    /**
     * Obtenir toutes les PJ d'un exercice
     */
    public List<PieceJustificativeComptable> getPJByExercice(Long exerciceId) {
        return pjRepository.findByExerciceIdOrderByDateUploadDesc(exerciceId);
    }
    
    /**
     * Obtenir les PJ par statut
     */
    public List<PieceJustificativeComptable> getPJByStatut(PieceJustificativeComptable.StatutTraitement statut) {
        return pjRepository.findByStatutTraitementOrderByDateUploadDesc(statut);
    }
    
    /**
     * Recherche avancée
     */
    public List<PieceJustificativeComptable> rechercheAvancee(Long companyId, Long exerciceId,
            PieceJustificativeComptable.StatutTraitement statut,
            PieceJustificativeComptable.TypeDocument typeDocument,
            LocalDateTime dateDebut, LocalDateTime dateFin,
            BigDecimal montantMin, BigDecimal montantMax) {
        return pjRepository.rechercheAvancee(companyId, exerciceId, statut, typeDocument,
            dateDebut, dateFin, montantMin, montantMax);
    }
    
    /**
     * Obtenir les statistiques
     */
    public Map<String, Object> getStatistiques(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques par statut
        List<Object[]> statutStats = pjRepository.getStatistiquesParStatut(companyId);
        Map<String, Long> statutMap = new HashMap<>();
        for (Object[] stat : statutStats) {
            statutMap.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("parStatut", statutMap);
        
        // Statistiques par type
        List<Object[]> typeStats = pjRepository.getStatistiquesParTypeDocument(companyId);
        Map<String, Long> typeMap = new HashMap<>();
        for (Object[] stat : typeStats) {
            typeMap.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("parType", typeMap);
        
        // Montant total
        BigDecimal montantTotal = pjRepository.getMontantTotalParEntreprise(companyId);
        stats.put("montantTotal", montantTotal != null ? montantTotal : BigDecimal.ZERO);
        
        return stats;
    }
}




