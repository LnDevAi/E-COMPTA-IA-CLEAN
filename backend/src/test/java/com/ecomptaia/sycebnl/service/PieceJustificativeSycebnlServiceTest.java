ackage com.ecomptaia.sycebnl.service;

import com.ecomptaia.sycebnl.entity.PieceJustificativeSycebnl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour le service des pièces justificatives SYCEBNL
 * Utilise les vraies données de test insérées via data.sql
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PieceJustificativeSycebnlServiceTest {

    @Autowired
    private PieceJustificativeSycebnlService pjService;


    @Test
    public void testGetPieceJustificativeById() {
        // Test avec les données de test insérées
        Optional<PieceJustificativeSycebnl> pj = pjService.getPieceJustificativeById(1L);
        
        assertTrue(pj.isPresent());
        assertEquals("PJ-1-20240115120000-ABC12345", pj.get().getNumeroPJ());
        assertEquals("Facture fournisseur SARL EXEMPLE", pj.get().getLibellePJ());
        assertEquals(PieceJustificativeSycebnl.TypePieceJustificative.FACTURE_FOURNISSEUR, pj.get().getTypePJ());
        assertEquals(PieceJustificativeSycebnl.StatutTraitement.ANALYSE_IA_TERMINEE, pj.get().getStatutTraitement());
    }

    @Test
    public void testGetPiecesJustificativesByEntreprise() {
        // Test avec les données de test insérées
        List<PieceJustificativeSycebnl> pjs = pjService.getPiecesJustificativesByEntreprise(1L);
        
        assertNotNull(pjs);
        assertTrue(pjs.size() >= 3); // Au moins 3 PJ de test
        assertTrue(pjs.stream().anyMatch(pj -> pj.getNumeroPJ().equals("PJ-1-20240115120000-ABC12345")));
        assertTrue(pjs.stream().anyMatch(pj -> pj.getNumeroPJ().equals("PJ-2-20240115130000-DEF67890")));
        assertTrue(pjs.stream().anyMatch(pj -> pj.getNumeroPJ().equals("PJ-3-20240115140000-GHI11111")));
    }

    @Test
    public void testGetPiecesJustificativesByStatut() {
        // Test avec les données de test insérées
        List<PieceJustificativeSycebnl> pjsAnalyseIA = pjService.getPiecesJustificativesByStatut(
            PieceJustificativeSycebnl.StatutTraitement.ANALYSE_IA_TERMINEE);
        
        assertNotNull(pjsAnalyseIA);
        assertTrue(pjsAnalyseIA.size() >= 1);
        assertTrue(pjsAnalyseIA.stream().anyMatch(pj -> pj.getNumeroPJ().equals("PJ-1-20240115120000-ABC12345")));

        List<PieceJustificativeSycebnl> pjsPropositions = pjService.getPiecesJustificativesByStatut(
            PieceJustificativeSycebnl.StatutTraitement.PROPOSITIONS_GENEREES);
        
        assertNotNull(pjsPropositions);
        assertTrue(pjsPropositions.size() >= 1);
        assertTrue(pjsPropositions.stream().anyMatch(pj -> pj.getNumeroPJ().equals("PJ-2-20240115130000-DEF67890")));
    }

    @Test
    public void testGetPropositionsByPJ() {
        // Test avec les données de test insérées
        List<PieceJustificativeSycebnl> pjs = pjService.getPiecesJustificativesByEntreprise(1L);
        assertFalse(pjs.isEmpty());
        
        Long pjId = pjs.get(0).getId();
        var propositions = pjService.getPropositionsByPJ(pjId);
        
        assertNotNull(propositions);
        // Les propositions peuvent être vides si elles n'ont pas encore été générées
    }

    @Test
    public void testWorkflowComplet() {
        // Test du workflow complet avec les données de test
        
        // 1. Vérifier qu'on a des PJ avec différents statuts
        List<PieceJustificativeSycebnl> pjs = pjService.getPiecesJustificativesByEntreprise(1L);
        assertTrue(pjs.size() >= 3);
        
        // 2. Vérifier les statuts
        boolean hasAnalyseIA = pjs.stream().anyMatch(pj -> 
            pj.getStatutTraitement() == PieceJustificativeSycebnl.StatutTraitement.ANALYSE_IA_TERMINEE);
        assertTrue(hasAnalyseIA, "Au moins une PJ doit avoir l'analyse IA terminée");
        
        boolean hasPropositions = pjs.stream().anyMatch(pj -> 
            pj.getStatutTraitement() == PieceJustificativeSycebnl.StatutTraitement.PROPOSITIONS_GENEREES);
        assertTrue(hasPropositions, "Au moins une PJ doit avoir des propositions générées");
        
        boolean hasTelechargee = pjs.stream().anyMatch(pj -> 
            pj.getStatutTraitement() == PieceJustificativeSycebnl.StatutTraitement.TELECHARGEE);
        assertTrue(hasTelechargee, "Au moins une PJ doit être téléchargée");
    }

    @Test
    public void testDonneesOCR() {
        // Test des données OCR
        Optional<PieceJustificativeSycebnl> pj = pjService.getPieceJustificativeById(1L);
        assertTrue(pj.isPresent());
        
        PieceJustificativeSycebnl piece = pj.get();
        assertNotNull(piece.getTexteOCR());
        assertTrue(piece.getTexteOCR().contains("FACTURE"));
        assertTrue(piece.getTexteOCR().contains("SARL EXEMPLE"));
        assertTrue(piece.getTexteOCR().contains("236,000"));
        
        assertNotNull(piece.getConfianceOCR());
        assertTrue(piece.getConfianceOCR().doubleValue() > 0.9);
    }

    @Test
    public void testDonneesIA() {
        // Test des données IA
        Optional<PieceJustificativeSycebnl> pj = pjService.getPieceJustificativeById(1L);
        assertTrue(pj.isPresent());
        
        PieceJustificativeSycebnl piece = pj.get();
        assertNotNull(piece.getAnalyseIA());
        assertEquals("FACTURE", piece.getAnalyseIA());
        
        assertNotNull(piece.getConfianceIA());
        assertTrue(piece.getConfianceIA().doubleValue() > 0.8);
        
        assertNotNull(piece.getMontantTotal());
        assertEquals(236000.00, piece.getMontantTotal().doubleValue(), 0.01);
    }

    @Test
    public void testTypesPiecesJustificatives() {
        // Test des types de PJ
        List<PieceJustificativeSycebnl> pjs = pjService.getPiecesJustificativesByEntreprise(1L);
        
        boolean hasFacture = pjs.stream().anyMatch(pj -> 
            pj.getTypePJ() == PieceJustificativeSycebnl.TypePieceJustificative.FACTURE_FOURNISSEUR);
        assertTrue(hasFacture, "Au moins une facture fournisseur doit être présente");
        
        boolean hasRecu = pjs.stream().anyMatch(pj -> 
            pj.getTypePJ() == PieceJustificativeSycebnl.TypePieceJustificative.RECU);
        assertTrue(hasRecu, "Au moins un reçu doit être présent");
        
        boolean hasBonCommande = pjs.stream().anyMatch(pj -> 
            pj.getTypePJ() == PieceJustificativeSycebnl.TypePieceJustificative.BON_COMMANDE);
        assertTrue(hasBonCommande, "Au moins un bon de commande doit être présent");
    }
}
