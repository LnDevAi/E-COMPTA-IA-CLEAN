ackage com.ecomptaia.sycebnl.integration;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.security.entity.User;
import com.ecomptaia.entity.Account;
import com.ecomptaia.entity.FinancialPeriod;
import com.ecomptaia.sycebnl.entity.PieceJustificativeSycebnl;
import com.ecomptaia.sycebnl.entity.AnalyseOCRSycebnl;
import com.ecomptaia.sycebnl.entity.AnalyseIASycebnl;
import com.ecomptaia.sycebnl.entity.PropositionEcritureSycebnl;
import com.ecomptaia.sycebnl.entity.LignePropositionEcritureSycebnl;
import com.ecomptaia.repository.CompanyRepository;
import com.ecomptaia.repository.UserRepository;
import com.ecomptaia.repository.AccountRepository;
import com.ecomptaia.repository.FinancialPeriodRepository;
import com.ecomptaia.sycebnl.repository.PieceJustificativeSycebnlRepository;
import com.ecomptaia.sycebnl.repository.AnalyseOCRSycebnlRepository;
import com.ecomptaia.sycebnl.repository.AnalyseIASycebnlRepository;
import com.ecomptaia.sycebnl.repository.PropositionEcritureSycebnlRepository;
import com.ecomptaia.sycebnl.repository.LignePropositionEcritureSycebnlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intÃ©gration pour vÃ©rifier que les donnÃ©es de test sont bien insÃ©rÃ©es
 * et que toutes les relations fonctionnent correctement
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DataIntegrationTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FinancialPeriodRepository periodRepository;

    @Autowired
    private PieceJustificativeSycebnlRepository pjRepository;

    @Autowired
    private AnalyseOCRSycebnlRepository analyseOCRRepository;

    @Autowired
    private AnalyseIASycebnlRepository analyseIARepository;

    @Autowired
    private PropositionEcritureSycebnlRepository propositionRepository;

    @Autowired
    private LignePropositionEcritureSycebnlRepository ligneRepository;

    @Test
    public void testDonneesEntreprises() {
        // VÃ©rifier les entreprises
        List<Company> companies = companyRepository.findAll();
        assertTrue(companies.size() >= 2);
        
        Optional<Company> entreprise1 = companyRepository.findById(1L);
        assertTrue(entreprise1.isPresent());
        assertEquals("ENTREPRISE TEST SYCEBNL", entreprise1.get().getName());
        assertEquals("SN", entreprise1.get().getCountryCode());
        assertEquals("SYCEBNL", entreprise1.get().getAccountingStandard());
    }

    @Test
    public void testDonneesUtilisateurs() {
        // VÃ©rifier les utilisateurs
        List<User> users = userRepository.findAll();
        assertTrue(users.size() >= 3);
        
        Optional<User> admin = userRepository.findById(1L);
        assertTrue(admin.isPresent());
        assertEquals("admin_sycebnl", admin.get().getUsername());
        assertEquals("admin@sycebnl.sn", admin.get().getEmail());
        assertTrue(admin.get().isAdmin());
    }

    @Test
    public void testDonneesExercices() {
        // VÃ©rifier les exercices
        List<FinancialPeriod> periods = periodRepository.findAll();
        assertTrue(periods.size() >= 2);
        
        Optional<FinancialPeriod> period1 = periodRepository.findById(1L);
        assertTrue(period1.isPresent());
        assertEquals("Exercice 2024", period1.get().getPeriodName());
        assertTrue(period1.get().getIsCurrent());
    }

    @Test
    public void testDonneesPlanComptable() {
        // VÃ©rifier le plan comptable
        List<Account> accounts = accountRepository.findAll();
        assertTrue(accounts.size() >= 24);
        
        // VÃ©rifier les comptes de charges
        List<Account> charges = accountRepository.findByCompanyAndAccountNumberStartingWithOrderByAccountNumberAsc(
            companyRepository.findById(1L).get(), "6");
        assertTrue(charges.size() >= 10);
        
        // VÃ©rifier les comptes de fournisseurs
        List<Account> fournisseurs = accountRepository.findByCompanyAndAccountNumberStartingWithOrderByAccountNumberAsc(
            companyRepository.findById(1L).get(), "401");
        assertTrue(fournisseurs.size() >= 3);
        
        // VÃ©rifier les comptes de clients
        List<Account> clients = accountRepository.findByCompanyAndAccountNumberStartingWithOrderByAccountNumberAsc(
            companyRepository.findById(1L).get(), "411");
        assertTrue(clients.size() >= 3);
    }

    @Test
    public void testDonneesPiecesJustificatives() {
        // VÃ©rifier les piÃ¨ces justificatives
        List<PieceJustificativeSycebnl> pjs = pjRepository.findAll();
        assertTrue(pjs.size() >= 3);
        
        Optional<PieceJustificativeSycebnl> pj1 = pjRepository.findById(1L);
        assertTrue(pj1.isPresent());
        assertEquals("PJ-1-20240115120000-ABC12345", pj1.get().getNumeroPJ());
        assertEquals("Facture fournisseur SARL EXEMPLE", pj1.get().getLibellePJ());
        assertEquals(PieceJustificativeSycebnl.TypePieceJustificative.FACTURE_FOURNISSEUR, pj1.get().getTypePJ());
        assertEquals(236000.00, pj1.get().getMontantTotal().doubleValue(), 0.01);
    }

    @Test
    public void testDonneesAnalysesOCR() {
        // VÃ©rifier les analyses OCR
        List<AnalyseOCRSycebnl> analysesOCR = analyseOCRRepository.findAll();
        assertTrue(analysesOCR.size() >= 3);
        
        Optional<AnalyseOCRSycebnl> analyse1 = analyseOCRRepository.findById(1L);
        assertTrue(analyse1.isPresent());
        assertNotNull(analyse1.get().getTexteExtrait());
        assertTrue(analyse1.get().getTexteExtrait().contains("FACTURE"));
        assertTrue(analyse1.get().getConfianceGlobale().doubleValue() > 0.9);
        assertEquals("TESSERACT", analyse1.get().getMoteurOCRUtilise());
    }

    @Test
    public void testDonneesAnalysesIA() {
        // VÃ©rifier les analyses IA
        List<AnalyseIASycebnl> analysesIA = analyseIARepository.findAll();
        assertTrue(analysesIA.size() >= 3);
        
        Optional<AnalyseIASycebnl> analyse1 = analyseIARepository.findById(1L);
        assertTrue(analyse1.isPresent());
        assertEquals("FACTURE", analyse1.get().getTypeDocumentDetecte());
        assertEquals(236000.00, analyse1.get().getMontantDetecte().doubleValue(), 0.01);
        assertEquals("SARL EXEMPLE", analyse1.get().getFournisseurDetecte());
        assertEquals(18.00, analyse1.get().getTvaDetectee().doubleValue(), 0.01);
        assertEquals("GPT-4", analyse1.get().getModeleIAUtilise());
    }

    @Test
    public void testDonneesPropositions() {
        // VÃ©rifier les propositions
        List<PropositionEcritureSycebnl> propositions = propositionRepository.findAll();
        assertTrue(propositions.size() >= 3);
        
        Optional<PropositionEcritureSycebnl> prop1 = propositionRepository.findById(1L);
        assertTrue(prop1.isPresent());
        assertEquals("PROP-1-1705320000000", prop1.get().getNumeroProposition());
        assertEquals("Facture fournisseur SARL EXEMPLE", prop1.get().getLibelleProposition());
        assertEquals(PropositionEcritureSycebnl.TypeEcritureProposition.FACTURE_FOURNISSEUR, prop1.get().getTypeEcriture());
        assertEquals(236000.00, prop1.get().getMontantTotal().doubleValue(), 0.01);
    }

    @Test
    public void testDonneesLignesPropositions() {
        // VÃ©rifier les lignes de propositions
        List<LignePropositionEcritureSycebnl> lignes = ligneRepository.findAll();
        assertTrue(lignes.size() >= 7);
        
        // VÃ©rifier les lignes de la premiÃ¨re proposition
        List<LignePropositionEcritureSycebnl> lignesProp1 = ligneRepository.findByPropositionIdOrderByOrdreAsc(1L);
        assertTrue(lignesProp1.size() >= 3);
        
        // VÃ©rifier la premiÃ¨re ligne (dÃ©bit)
        LignePropositionEcritureSycebnl ligne1 = lignesProp1.get(0);
        assertEquals("606000", ligne1.getNumeroCompte());
        assertEquals("Services extÃ©rieurs", ligne1.getLibelleCompte());
        assertEquals(200000.00, ligne1.getDebit().doubleValue(), 0.01);
        assertNull(ligne1.getCredit());
        
        // VÃ©rifier la deuxiÃ¨me ligne (TVA)
        LignePropositionEcritureSycebnl ligne2 = lignesProp1.get(1);
        assertEquals("445001", ligne2.getNumeroCompte());
        assertEquals("TVA dÃ©ductible", ligne2.getLibelleCompte());
        assertEquals(36000.00, ligne2.getDebit().doubleValue(), 0.01);
        assertNull(ligne2.getCredit());
        
        // VÃ©rifier la troisiÃ¨me ligne (crÃ©dit)
        LignePropositionEcritureSycebnl ligne3 = lignesProp1.get(2);
        assertEquals("401001", ligne3.getNumeroCompte());
        assertEquals("Fournisseur SARL EXEMPLE", ligne3.getLibelleCompte());
        assertNull(ligne3.getDebit());
        assertEquals(236000.00, ligne3.getCredit().doubleValue(), 0.01);
    }

    @Test
    public void testRelationsEntreEntites() {
        // VÃ©rifier les relations entre les entitÃ©s
        
        // PJ -> Analyse OCR
        Optional<PieceJustificativeSycebnl> pj = pjRepository.findById(1L);
        assertTrue(pj.isPresent());
        assertNotNull(pj.get().getTexteOCR());
        assertNotNull(pj.get().getConfianceOCR());
        
        // PJ -> Analyse IA
        assertNotNull(pj.get().getAnalyseIA());
        assertNotNull(pj.get().getConfianceIA());
        
        // PJ -> Propositions
        List<PropositionEcritureSycebnl> propositions = propositionRepository.findByPieceJustificativeIdOrderByDateCreationDesc(1L);
        assertFalse(propositions.isEmpty());
        
        // Proposition -> Lignes
        List<LignePropositionEcritureSycebnl> lignes = ligneRepository.findByPropositionIdOrderByOrdreAsc(propositions.get(0).getId());
        assertFalse(lignes.isEmpty());
        
        // VÃ©rifier l'Ã©quilibre des propositions
        for (PropositionEcritureSycebnl proposition : propositions) {
            List<LignePropositionEcritureSycebnl> lignesProp = ligneRepository.findByPropositionIdOrderByOrdreAsc(proposition.getId());
            
            double totalDebit = lignesProp.stream()
                .filter(l -> l.getDebit() != null)
                .mapToDouble(l -> l.getDebit().doubleValue())
                .sum();
            
            double totalCredit = lignesProp.stream()
                .filter(l -> l.getCredit() != null)
                .mapToDouble(l -> l.getCredit().doubleValue())
                .sum();
            
            assertEquals(totalDebit, totalCredit, 0.01, 
                "La proposition " + proposition.getNumeroProposition() + " doit Ãªtre Ã©quilibrÃ©e");
        }
    }

    @Test
    public void testWorkflowCompletDonnees() {
        // VÃ©rifier que le workflow complet est reprÃ©sentÃ© dans les donnÃ©es
        
        // 1. PJ tÃ©lÃ©chargÃ©e
        List<PieceJustificativeSycebnl> pjsTelechargees = pjRepository.findByStatutTraitementOrderByDateCreationDesc(
            PieceJustificativeSycebnl.StatutTraitement.TELECHARGEE);
        assertFalse(pjsTelechargees.isEmpty());
        
        // 2. PJ avec analyse IA terminÃ©e
        List<PieceJustificativeSycebnl> pjsAnalyseIA = pjRepository.findByStatutTraitementOrderByDateCreationDesc(
            PieceJustificativeSycebnl.StatutTraitement.ANALYSE_IA_TERMINEE);
        assertFalse(pjsAnalyseIA.isEmpty());
        
        // 3. PJ avec propositions gÃ©nÃ©rÃ©es
        List<PieceJustificativeSycebnl> pjsPropositions = pjRepository.findByStatutTraitementOrderByDateCreationDesc(
            PieceJustificativeSycebnl.StatutTraitement.PROPOSITIONS_GENEREES);
        assertFalse(pjsPropositions.isEmpty());
        
        // 4. Propositions validÃ©es
        List<PropositionEcritureSycebnl> propositionsValidees = propositionRepository.findByStatutPropositionOrderByDateCreationDesc(
            PropositionEcritureSycebnl.StatutProposition.VALIDEE);
        assertFalse(propositionsValidees.isEmpty());
        
        // 5. Propositions gÃ©nÃ©rÃ©es en Ã©critures
        List<PropositionEcritureSycebnl> propositionsGenerees = propositionRepository.findByStatutPropositionOrderByDateCreationDesc(
            PropositionEcritureSycebnl.StatutProposition.GENEREES);
        assertFalse(propositionsGenerees.isEmpty());
    }
}

