package com.ecomptaia.sycebnl.service;

import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.EcritureComptable;
import com.ecomptaia.entity.FinancialPeriod;
import com.ecomptaia.entity.LigneEcriture;
import com.ecomptaia.sycebnl.entity.LignePropositionEcritureSycebnl;
import com.ecomptaia.sycebnl.entity.PropositionEcritureSycebnl;
import com.ecomptaia.repository.EcritureComptableRepository;
import com.ecomptaia.repository.LigneEcritureRepository;
import com.ecomptaia.repository.FinancialPeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service pour la gÃ©nÃ©ration d'Ã©critures comptables Ã  partir des propositions SYCEBNL
 */
@Service
@Transactional
public class GenerationEcritureService {
    
    private final EcritureComptableRepository ecritureRepository;
    private final LigneEcritureRepository ligneRepository;
    private final FinancialPeriodRepository periodRepository;
    
    @Autowired
    public GenerationEcritureService(
            EcritureComptableRepository ecritureRepository,
            LigneEcritureRepository ligneRepository,
            FinancialPeriodRepository periodRepository) {
        this.ecritureRepository = ecritureRepository;
        this.ligneRepository = ligneRepository;
        this.periodRepository = periodRepository;
    }
    
    /**
     * GÃ©nÃ¨re une Ã©criture comptable Ã  partir d'une proposition validÃ©e
     */
    public EcritureComptable genererEcritureComptable(PropositionEcritureSycebnl proposition) {
        try {
            // CrÃ©er l'Ã©criture comptable
            EcritureComptable ecriture = creerEcritureComptable(proposition);
            
            // Sauvegarder l'Ã©criture
            ecriture = ecritureRepository.save(ecriture);
            
            // CrÃ©er les lignes d'Ã©criture
            List<LigneEcriture> lignes = creerLignesEcriture(ecriture, proposition);
            
            // Sauvegarder les lignes
            for (LigneEcriture ligne : lignes) {
                ligneRepository.save(ligne);
            }
            
            // Calculer et mettre Ã  jour les totaux
            ecriture.calculerTotaux();
            ecritureRepository.save(ecriture);
            
            return ecriture;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la gÃ©nÃ©ration de l'Ã©criture : " + e.getMessage(), e);
        }
    }
    
    /**
     * CrÃ©e l'Ã©criture comptable de base
     */
    private EcritureComptable creerEcritureComptable(PropositionEcritureSycebnl proposition) {
        EcritureComptable ecriture = new EcritureComptable();
        
        // Informations de base
        ecriture.setNumeroPiece(genererNumeroPiece(proposition));
        ecriture.setDateEcriture(LocalDate.now());
        ecriture.setDatePiece(proposition.getDateProposition());
        ecriture.setReference(proposition.getNumeroProposition());
        ecriture.setLibelle(proposition.getLibelleProposition());
        
        // Type et statut
        ecriture.setTypeEcriture(EcritureComptable.TypeEcriture.NORMALE);
        ecriture.setStatut(EcritureComptable.StatutEcriture.VALIDEE);
        ecriture.setSource(EcritureComptable.SourceEcriture.IA);
        
        // Entreprise et exercice
        ecriture.setEntreprise(proposition.getPieceJustificative().getEntreprise());
        ecriture.setExercice(getExerciceCourant(proposition.getPieceJustificative().getEntreprise()));
        ecriture.setUtilisateur(proposition.getPieceJustificative().getUtilisateur());
        
        // Devise et montants
        ecriture.setDevise(proposition.getDevise());
        ecriture.setTotalDebit(BigDecimal.ZERO);
        ecriture.setTotalCredit(BigDecimal.ZERO);
        
        // MÃ©tadonnÃ©es
        ecriture.setTemplateId(proposition.getId().toString());
        ecriture.setValidationAiConfiance(proposition.getConfianceProposition());
        ecriture.setValidationAiSuggestions("Ã‰criture gÃ©nÃ©rÃ©e automatiquement Ã  partir de la proposition " + proposition.getNumeroProposition());
        ecriture.setMetadata(creerMetadata(proposition));
        
        return ecriture;
    }
    
    /**
     * CrÃ©e les lignes d'Ã©criture Ã  partir des lignes de proposition
     */
    private List<LigneEcriture> creerLignesEcriture(EcritureComptable ecriture, PropositionEcritureSycebnl proposition) {
        List<LigneEcriture> lignes = new ArrayList<>();
        
        for (LignePropositionEcritureSycebnl ligneProposition : proposition.getLignesProposition()) {
            LigneEcriture ligne = new LigneEcriture();
            
            // Informations de base
            ligne.setEcriture(ecriture);
            ligne.setCompte(ligneProposition.getCompte());
            ligne.setCompteNumero(ligneProposition.getNumeroCompte());
            ligne.setCompteLibelle(ligneProposition.getLibelleCompte());
            ligne.setLibelleLigne(ligneProposition.getLibelleLigne());
            ligne.setOrdre(ligneProposition.getOrdre());
            
            // Montants
            ligne.setDebit(ligneProposition.getDebit());
            ligne.setCredit(ligneProposition.getCredit());
            
            // MÃ©tadonnÃ©es dans analytique
            String analytique = "Justification: " + ligneProposition.getJustificationLigne() + 
                              "; RÃ¨gle: " + ligneProposition.getRegleAppliquee() + 
                              "; Confiance: " + ligneProposition.getConfianceLigne();
            ligne.setAnalytique(analytique);
            
            lignes.add(ligne);
        }
        
        return lignes;
    }
    
    /**
     * GÃ©nÃ¨re un numÃ©ro de piÃ¨ce unique
     */
    private String genererNumeroPiece(PropositionEcritureSycebnl proposition) {
        String prefixe = "ECR";
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefixe + "-" + timestamp + "-" + uuid;
    }
    
    /**
     * RÃ©cupÃ¨re l'exercice courant pour une entreprise
     */
    private FinancialPeriod getExerciceCourant(Company entreprise) {
        // Cette mÃ©thode devrait rÃ©cupÃ©rer l'exercice courant
        // Pour l'instant, on simule
        List<FinancialPeriod> periods = periodRepository.findByCompanyIdOrderByStartDateDesc(entreprise.getId());
        return periods.isEmpty() ? null : periods.get(0);
    }
    
    /**
     * CrÃ©e les mÃ©tadonnÃ©es de l'Ã©criture
     */
    private String creerMetadata(PropositionEcritureSycebnl proposition) {
        StringBuilder metadata = new StringBuilder();
        metadata.append("{");
        metadata.append("\"proposition_id\":").append(proposition.getId()).append(",");
        metadata.append("\"piece_justificative_id\":").append(proposition.getPieceJustificative().getId()).append(",");
        metadata.append("\"type_proposition\":\"").append(proposition.getTypeEcriture()).append("\",");
        metadata.append("\"confiance_proposition\":").append(proposition.getConfianceProposition()).append(",");
        metadata.append("\"date_generation\":\"").append(LocalDateTime.now()).append("\",");
        metadata.append("\"source\":\"SYCEBNL_IA\"");
        metadata.append("}");
        return metadata.toString();
    }
    
    /**
     * Valide une Ã©criture gÃ©nÃ©rÃ©e
     */
    public void validerEcriture(EcritureComptable ecriture) {
        // VÃ©rifier l'Ã©quilibre
        if (!ecriture.isEquilibree()) {
            throw new RuntimeException("L'Ã©criture n'est pas Ã©quilibrÃ©e");
        }
        
        // VÃ©rifier les totaux
        if (ecriture.getTotalDebit().compareTo(BigDecimal.ZERO) <= 0 || 
            ecriture.getTotalCredit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Les totaux doivent Ãªtre positifs");
        }
        
        // Marquer comme validÃ©e
        ecriture.setStatut(EcritureComptable.StatutEcriture.VALIDEE);
        ecritureRepository.save(ecriture);
    }
    
    /**
     * Annule une Ã©criture gÃ©nÃ©rÃ©e
     */
    public void annulerEcriture(EcritureComptable ecriture) {
        ecriture.setStatut(EcritureComptable.StatutEcriture.ANNULEE);
        ecritureRepository.save(ecriture);
    }
    
    /**
     * RÃ©cupÃ¨re les Ã©critures gÃ©nÃ©rÃ©es Ã  partir de propositions
     */
    @Transactional(readOnly = true)
    public List<EcritureComptable> getEcrituresGenereesParPropositions() {
        // RÃ©cupÃ¨re toutes les Ã©critures et filtre celles gÃ©nÃ©rÃ©es par IA
        return ecritureRepository.findAll().stream()
            .filter(ecriture -> ecriture.getSource() == EcritureComptable.SourceEcriture.IA)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * RÃ©cupÃ¨re les Ã©critures gÃ©nÃ©rÃ©es pour une piÃ¨ce justificative
     */
    @Transactional(readOnly = true)
    public List<EcritureComptable> getEcrituresParPieceJustificative(Long pjId) {
        // RÃ©cupÃ¨re toutes les Ã©critures et filtre celles liÃ©es Ã  la PJ
        return ecritureRepository.findAll().stream()
            .filter(ecriture -> ecriture.getTemplateId() != null && 
                               ecriture.getTemplateId().contains("proposition_id\":" + pjId))
            .collect(java.util.stream.Collectors.toList());
    }
}

