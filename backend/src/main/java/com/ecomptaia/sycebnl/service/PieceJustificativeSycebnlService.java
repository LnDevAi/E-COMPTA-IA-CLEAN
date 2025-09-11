package com.ecomptaia.sycebnl.service;

import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.entity.User;
import com.ecomptaia.sycebnl.entity.PieceJustificativeSycebnl;
import com.ecomptaia.sycebnl.entity.AnalyseOCRSycebnl;
import com.ecomptaia.sycebnl.entity.AnalyseIASycebnl;
import com.ecomptaia.sycebnl.entity.PropositionEcritureSycebnl;
import com.ecomptaia.sycebnl.repository.PieceJustificativeSycebnlRepository;
import com.ecomptaia.sycebnl.repository.PropositionEcritureSycebnlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service principal pour la gestion des piÃ¨ces justificatives comptables SYCEBNL
 * GÃ¨re le workflow complet : tÃ©lÃ©chargement, analyse OCR, IA, proposition, validation, gÃ©nÃ©ration
 */
@Service
@Transactional
public class PieceJustificativeSycebnlService {
    
    private final PieceJustificativeSycebnlRepository pjRepository;
    private final PropositionEcritureSycebnlRepository propositionRepository;
    private final AnalyseOCRService analyseOCRService;
    private final AnalyseIAService analyseIAService;
    private final PropositionEcritureService propositionService;
    private final GenerationEcritureService generationService;
    
    @Autowired
    public PieceJustificativeSycebnlService(
            PieceJustificativeSycebnlRepository pjRepository,
            PropositionEcritureSycebnlRepository propositionRepository,
            AnalyseOCRService analyseOCRService,
            AnalyseIAService analyseIAService,
            PropositionEcritureService propositionService,
            GenerationEcritureService generationService) {
        this.pjRepository = pjRepository;
        this.propositionRepository = propositionRepository;
        this.analyseOCRService = analyseOCRService;
        this.analyseIAService = analyseIAService;
        this.propositionService = propositionService;
        this.generationService = generationService;
    }
    
    /**
     * TÃ©lÃ©charge et crÃ©e une nouvelle piÃ¨ce justificative
     */
    public PieceJustificativeSycebnl telechargerPieceJustificative(
            MultipartFile fichier,
            String libellePJ,
            LocalDate datePiece,
            PieceJustificativeSycebnl.TypePieceJustificative typePJ,
            Company entreprise,
            User utilisateur) throws IOException {
        
        // GÃ©nÃ©rer un numÃ©ro unique pour la PJ
        String numeroPJ = genererNumeroPJ(entreprise.getId());
        
        // CrÃ©er le document GED
        GedDocument document = creerDocumentGED(fichier, numeroPJ, entreprise);
        
        // CrÃ©er la piÃ¨ce justificative
        PieceJustificativeSycebnl pj = new PieceJustificativeSycebnl(
            numeroPJ, libellePJ, datePiece, typePJ, document, entreprise, utilisateur);
        
        // DÃ©terminer le montant total si possible
        pj.setMontantTotal(extraireMontantDuFichier(fichier));
        
        // Sauvegarder
        pj = pjRepository.save(pj);
        
        // DÃ©marrer l'analyse OCR automatiquement
        demarrerAnalyseOCR(pj.getId());
        
        return pj;
    }
    
    /**
     * DÃ©marre l'analyse OCR d'une piÃ¨ce justificative
     */
    public void demarrerAnalyseOCR(Long pjId) {
        Optional<PieceJustificativeSycebnl> pjOpt = pjRepository.findById(pjId);
        if (pjOpt.isEmpty()) {
            throw new RuntimeException("PiÃ¨ce justificative non trouvÃ©e");
        }
        
        PieceJustificativeSycebnl pj = pjOpt.get();
        
        // Mettre Ã  jour le statut
        pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.ANALYSE_OCR_EN_COURS);
        pjRepository.save(pj);
        
        // Lancer l'analyse OCR de maniÃ¨re asynchrone
        try {
            AnalyseOCRSycebnl analyseOCR = analyseOCRService.analyserDocument(pj);
            
            // Mettre Ã  jour la PJ avec les rÃ©sultats
            pj.setTexteOCR(analyseOCR.getTexteExtrait());
            pj.setConfianceOCR(analyseOCR.getConfianceGlobale());
            pj.setDateAnalyseOCR(analyseOCR.getDateAnalyse());
            pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.ANALYSE_OCR_TERMINEE);
            pjRepository.save(pj);
            
            // DÃ©marrer automatiquement l'analyse IA
            demarrerAnalyseIA(pjId);
            
        } catch (Exception e) {
            // En cas d'erreur, marquer comme erreur
            pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.REJETEE);
            pjRepository.save(pj);
            throw new RuntimeException("Erreur lors de l'analyse OCR : " + e.getMessage(), e);
        }
    }
    
    /**
     * DÃ©marre l'analyse IA d'une piÃ¨ce justificative
     */
    public void demarrerAnalyseIA(Long pjId) {
        Optional<PieceJustificativeSycebnl> pjOpt = pjRepository.findById(pjId);
        if (pjOpt.isEmpty()) {
            throw new RuntimeException("PiÃ¨ce justificative non trouvÃ©e");
        }
        
        PieceJustificativeSycebnl pj = pjOpt.get();
        
        // VÃ©rifier que l'analyse OCR est terminÃ©e
        if (pj.getStatutTraitement() != PieceJustificativeSycebnl.StatutTraitement.ANALYSE_OCR_TERMINEE) {
            throw new RuntimeException("L'analyse OCR doit Ãªtre terminÃ©e avant l'analyse IA");
        }
        
        // Mettre Ã  jour le statut
        pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.ANALYSE_IA_EN_COURS);
        pjRepository.save(pj);
        
        // Lancer l'analyse IA de maniÃ¨re asynchrone
        try {
            AnalyseIASycebnl analyseIA = analyseIAService.analyserDocument(pj);
            
            // Mettre Ã  jour la PJ avec les rÃ©sultats
            pj.setAnalyseIA(analyseIA.getTypeDocumentDetecte());
            pj.setConfianceIA(analyseIA.getConfianceGlobale());
            pj.setDateAnalyseIA(analyseIA.getDateAnalyse());
            pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.ANALYSE_IA_TERMINEE);
            pjRepository.save(pj);
            
            // GÃ©nÃ©rer automatiquement les propositions d'Ã©critures
            genererPropositionsEcritures(pjId);
            
        } catch (Exception e) {
            // En cas d'erreur, marquer comme erreur
            pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.REJETEE);
            pjRepository.save(pj);
            throw new RuntimeException("Erreur lors de l'analyse IA : " + e.getMessage(), e);
        }
    }
    
    /**
     * GÃ©nÃ¨re les propositions d'Ã©critures pour une piÃ¨ce justificative
     */
    public void genererPropositionsEcritures(Long pjId) {
        Optional<PieceJustificativeSycebnl> pjOpt = pjRepository.findById(pjId);
        if (pjOpt.isEmpty()) {
            throw new RuntimeException("PiÃ¨ce justificative non trouvÃ©e");
        }
        
        PieceJustificativeSycebnl pj = pjOpt.get();
        
        // VÃ©rifier que l'analyse IA est terminÃ©e
        if (pj.getStatutTraitement() != PieceJustificativeSycebnl.StatutTraitement.ANALYSE_IA_TERMINEE) {
            throw new RuntimeException("L'analyse IA doit Ãªtre terminÃ©e avant la gÃ©nÃ©ration des propositions");
        }
        
        try {
            // GÃ©nÃ©rer les propositions
            propositionService.genererPropositions(pj);
            
            // Mettre Ã  jour la PJ
            pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.PROPOSITIONS_GENEREES);
            pj.setDateProposition(LocalDateTime.now());
            pjRepository.save(pj);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la gÃ©nÃ©ration des propositions : " + e.getMessage(), e);
        }
    }
    
    /**
     * Valide une proposition d'Ã©criture
     */
    public void validerProposition(Long propositionId, Long validateurId, String commentaires) {
        Optional<PropositionEcritureSycebnl> propositionOpt = propositionRepository.findById(propositionId);
        if (propositionOpt.isEmpty()) {
            throw new RuntimeException("Proposition non trouvÃ©e");
        }
        
        PropositionEcritureSycebnl proposition = propositionOpt.get();
        
        // Valider la proposition
        proposition.setStatutProposition(PropositionEcritureSycebnl.StatutProposition.VALIDEE);
        proposition.setValidePar(validateurId);
        proposition.setDateValidation(LocalDateTime.now());
        proposition.setCommentairesValidation(commentaires);
        propositionRepository.save(proposition);
        
        // Mettre Ã  jour la PJ
        PieceJustificativeSycebnl pj = proposition.getPieceJustificative();
        pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.EN_ATTENTE_VALIDATION);
        pj.setValidePar(validateurId);
        pj.setDateValidation(LocalDateTime.now());
        pj.setCommentairesValidation(commentaires);
        pjRepository.save(pj);
    }
    
    /**
     * GÃ©nÃ¨re l'Ã©criture comptable Ã  partir d'une proposition validÃ©e
     */
    public void genererEcritureComptable(Long propositionId) {
        Optional<PropositionEcritureSycebnl> propositionOpt = propositionRepository.findById(propositionId);
        if (propositionOpt.isEmpty()) {
            throw new RuntimeException("Proposition non trouvÃ©e");
        }
        
        PropositionEcritureSycebnl proposition = propositionOpt.get();
        
        // VÃ©rifier que la proposition est validÃ©e
        if (proposition.getStatutProposition() != PropositionEcritureSycebnl.StatutProposition.VALIDEE) {
            throw new RuntimeException("La proposition doit Ãªtre validÃ©e avant la gÃ©nÃ©ration de l'Ã©criture");
        }
        
        try {
            // GÃ©nÃ©rer l'Ã©criture comptable
            var ecritureGeneree = generationService.genererEcritureComptable(proposition);
            
            // Mettre Ã  jour la proposition
            proposition.setStatutProposition(PropositionEcritureSycebnl.StatutProposition.GENEREES);
            propositionRepository.save(proposition);
            
            // Mettre Ã  jour la PJ
            PieceJustificativeSycebnl pj = proposition.getPieceJustificative();
            pj.setEcritureGeneree(ecritureGeneree);
            pj.setNumeroEcritureGeneree(ecritureGeneree.getNumeroPiece());
            pj.setDateGenerationEcriture(LocalDateTime.now());
            pj.setStatutTraitement(PieceJustificativeSycebnl.StatutTraitement.ECRITURE_GENEREES);
            pjRepository.save(pj);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la gÃ©nÃ©ration de l'Ã©criture : " + e.getMessage(), e);
        }
    }
    
    /**
     * RÃ©cupÃ¨re une piÃ¨ce justificative par son ID
     */
    @Transactional(readOnly = true)
    public Optional<PieceJustificativeSycebnl> getPieceJustificativeById(Long id) {
        return pjRepository.findById(id);
    }
    
    /**
     * RÃ©cupÃ¨re toutes les piÃ¨ces justificatives d'une entreprise
     */
    @Transactional(readOnly = true)
    public List<PieceJustificativeSycebnl> getPiecesJustificativesByEntreprise(Long entrepriseId) {
        return pjRepository.findByEntrepriseIdOrderByDateCreationDesc(entrepriseId);
    }
    
    /**
     * RÃ©cupÃ¨re les piÃ¨ces justificatives par statut
     */
    @Transactional(readOnly = true)
    public List<PieceJustificativeSycebnl> getPiecesJustificativesByStatut(
            PieceJustificativeSycebnl.StatutTraitement statut) {
        return pjRepository.findByStatutTraitementOrderByDateCreationDesc(statut);
    }
    
    /**
     * RÃ©cupÃ¨re les propositions d'une piÃ¨ce justificative
     */
    @Transactional(readOnly = true)
    public List<PropositionEcritureSycebnl> getPropositionsByPJ(Long pjId) {
        return propositionRepository.findByPieceJustificativeIdOrderByDateCreationDesc(pjId);
    }
    
    // MÃ©thodes utilitaires privÃ©es
    
    private String genererNumeroPJ(Long entrepriseId) {
        String prefixe = "PJ" + entrepriseId + "-";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefixe + timestamp + "-" + uuid;
    }
    
    private GedDocument creerDocumentGED(MultipartFile fichier, String numeroPJ, Company entreprise) throws IOException {
        // Cette mÃ©thode devrait crÃ©er le document GED et le sauvegarder
        // Pour l'instant, on simule la crÃ©ation
        GedDocument document = new GedDocument();
        document.setDocumentCode(numeroPJ);
        document.setTitle("PiÃ¨ce justificative " + numeroPJ);
        document.setFileName(fichier.getOriginalFilename());
        document.setFilePath("/uploads/pj/" + numeroPJ + "/" + fichier.getOriginalFilename());
        document.setFileSize(fichier.getSize());
        document.setFileType(fichier.getContentType());
        document.setDocumentType(GedDocument.DocumentType.INVOICE);
        document.setCompanyId(entreprise.getId());
        document.setCountryCode(entreprise.getCountryCode());
        document.setAccountingStandard("SYCEBNL");
        
        return document;
    }
    
    private BigDecimal extraireMontantDuFichier(MultipartFile fichier) {
        // Cette mÃ©thode devrait extraire le montant du fichier
        // Pour l'instant, on retourne null
        return null;
    }
}

