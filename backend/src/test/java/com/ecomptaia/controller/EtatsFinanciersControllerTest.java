ackage com.ecomptaia.controller;

import com.ecomptaia.entity.BalanceComptable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour le contrôleur EtatsFinanciersController
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class EtatsFinanciersControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGenererBalance() throws Exception {
        // Given
        BalanceComptable mockBalance = new BalanceComptable();
        mockBalance.setId(1L);
        mockBalance.setStatut("GENERATED");
        mockBalance.setStandardComptable("SYSCOHADA");

        // When & Then
        mockMvc.perform(post("/api/etats-financiers/balance/generer")
                .param("companyId", "1")
                .param("exerciceId", "1")
                .param("dateBalance", "2024-12-31")
                .param("standardComptable", "SYSCOHADA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBalancesByCompany() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/balance/entreprise/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBalancesByExercice() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/balance/exercice/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBalanceById() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/balance/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSoldesByBalance() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/balance/1/soldes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSoldesByClasse() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/balance/1/soldes/classe/4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSoldesByNature() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/balance/1/soldes/nature/ACTIF")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testValiderBalance() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/etats-financiers/balance/1/valider")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testPublierBalance() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/etats-financiers/balance/1/publier")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStatistiquesBalance() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/balance/1/statistiques")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenererTousLesEtatsFinanciers() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/etats-financiers/generer-tous")
                .param("companyId", "1")
                .param("exerciceId", "1")
                .param("standardComptable", "SYSCOHADA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenererBilan() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/etats-financiers/bilan/generer")
                .param("companyId", "1")
                .param("exerciceId", "1")
                .param("standardComptable", "SYSCOHADA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenererCompteResultat() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/etats-financiers/compte-resultat/generer")
                .param("companyId", "1")
                .param("exerciceId", "1")
                .param("standardComptable", "SYSCOHADA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenererTableauFluxTresorerie() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/etats-financiers/flux-tresorerie/generer")
                .param("companyId", "1")
                .param("exerciceId", "1")
                .param("standardComptable", "SYSCOHADA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenererAnnexes() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/etats-financiers/annexes/generer")
                .param("companyId", "1")
                .param("exerciceId", "1")
                .param("standardComptable", "SYSCOHADA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEtatsFinanciersByExercice() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/exercice/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEtatFinancierById() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEtatsFinanciersByType() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/type/BILAN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEtatsFinanciersByStandard() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/standard/SYSCOHADA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testRechercherBalances() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/etats-financiers/balance/rechercher")
                .param("companyId", "1")
                .param("standardComptable", "SYSCOHADA")
                .param("statut", "GENERATED")
                .param("dateDebut", "2024-01-01")
                .param("dateFin", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
