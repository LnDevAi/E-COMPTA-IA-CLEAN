package com.ecomptaia.sycebnl.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour le contrôleur des pièces justificatives SYCEBNL
 * Utilise les vraies données de test insérées via data.sql
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class PieceJustificativeSycebnlControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;


    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetPieceJustificativeById() throws Exception {
        // Test avec les données de test insérées
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroPJ").value("PJ-1-20240115120000-ABC12345"))
                .andExpect(jsonPath("$.libellePJ").value("Facture fournisseur SARL EXEMPLE"))
                .andExpect(jsonPath("$.typePJ").value("FACTURE_FOURNISSEUR"))
                .andExpect(jsonPath("$.statutTraitement").value("ANALYSE_IA_TERMINEE"))
                .andExpect(jsonPath("$.montantTotal").value(236000.00));
    }

    @Test
    public void testGetPiecesJustificativesByEntreprise() throws Exception {
        // Test avec les données de test insérées
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/entreprise/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$[0].entrepriseId").value(1));
    }

    @Test
    public void testGetPiecesJustificativesByStatut() throws Exception {
        // Test avec les données de test insérées
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/statut/ANALYSE_IA_TERMINEE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[0].statutTraitement").value("ANALYSE_IA_TERMINEE"));
    }

    @Test
    public void testGetPropositions() throws Exception {
        // Test avec les données de test insérées
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/1/propositions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetTypesPiecesJustificatives() throws Exception {
        // Test des types disponibles
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/types")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(11)) // 11 types définis
                .andExpect(jsonPath("$[0].name()").value("FACTURE_FOURNISSEUR"));
    }

    @Test
    public void testGetStatutsTraitement() throws Exception {
        // Test des statuts disponibles
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/statuts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(11)) // 11 statuts définis
                .andExpect(jsonPath("$[0].name()").value("TELECHARGEE"));
    }

    @Test
    public void testDemarrerAnalyseOCR() throws Exception {
        // Test avec une PJ téléchargée (ID 3)
        mockMvc.perform(post("/api/sycebnl/pieces-justificatives/3/analyse-ocr")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Analyse OCR démarrée avec succès"));
    }

    @Test
    public void testDemarrerAnalyseIA() throws Exception {
        // Test avec une PJ ayant l'OCR terminé
        mockMvc.perform(post("/api/sycebnl/pieces-justificatives/1/analyse-ia")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Analyse IA démarrée avec succès"));
    }

    @Test
    public void testGenererPropositions() throws Exception {
        // Test avec une PJ ayant l'IA terminée
        mockMvc.perform(post("/api/sycebnl/pieces-justificatives/1/generer-propositions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Propositions générées avec succès"));
    }

    @Test
    public void testValiderProposition() throws Exception {
        // Test de validation d'une proposition
        mockMvc.perform(post("/api/sycebnl/pieces-justificatives/propositions/1/valider")
                .param("validateurId", "1")
                .param("commentaires", "Proposition validée par test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Proposition validée avec succès"));
    }

    @Test
    public void testGenererEcritureComptable() throws Exception {
        // Test de génération d'écriture à partir d'une proposition validée
        mockMvc.perform(post("/api/sycebnl/pieces-justificatives/propositions/2/generer-ecriture")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Écriture comptable générée avec succès"));
    }

    @Test
    public void testGetPieceJustificativeNotFound() throws Exception {
        // Test avec un ID inexistant
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testWorkflowCompletAPI() throws Exception {
        // Test du workflow complet via l'API
        
        // 1. Vérifier qu'on a des PJ
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/entreprise/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
        
        // 2. Vérifier les différents statuts
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/statut/TELECHARGEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/statut/ANALYSE_IA_TERMINEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        mockMvc.perform(get("/api/sycebnl/pieces-justificatives/statut/PROPOSITIONS_GENEREES"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
