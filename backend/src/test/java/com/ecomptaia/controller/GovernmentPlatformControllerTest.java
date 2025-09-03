package com.ecomptaia.controller;

import com.ecomptaia.service.GovernmentPlatformService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GovernmentPlatformControllerTest {

    @Mock
    private GovernmentPlatformService governmentPlatformService;

    @InjectMocks
    private GovernmentPlatformController governmentPlatformController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // Données de test
    private Map<String, Object> mockPlatformConfig;
    private Map<String, Object> mockConnectionStatus;
    private Map<String, Object> mockSubmissionResult;
    private Map<String, Object> mockDeclarationStatus;
    private List<Map<String, Object>> mockNotifications;
    private Map<String, Object> mockIntegrationStats;
    private Map<String, Object> mockTestResults;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(governmentPlatformController).build();
        objectMapper = new ObjectMapper();

        // Configuration des données de test
        setupMockData();
    }

    private void setupMockData() {
        // Configuration des plateformes
        mockPlatformConfig = new HashMap<>();
        mockPlatformConfig.put("taxPlatform", Map.of(
            "name", "DGI Sénégal",
            "baseUrl", "https://api.dgi.sn",
            "apiVersion", "v1",
            "supportedDeclarations", List.of("TVA", "IS", "IRPP")
        ));
        mockPlatformConfig.put("socialPlatform", Map.of(
            "name", "IPRES Sénégal",
            "baseUrl", "https://api.ipres.sn",
            "apiVersion", "v1",
            "supportedDeclarations", List.of("IPRES", "CNSS")
        ));

        // Statut de connexion
        mockConnectionStatus = Map.of(
            "success", true,
            "status", "CONNECTED",
            "responseCode", 200,
            "softwareName", "DGI Sénégal",
            "softwareType", "taxPlatform",
            "testUrl", "https://api.dgi.sn/v1/health",
            "timestamp", LocalDateTime.now()
        );

        // Résultat de soumission
        mockSubmissionResult = Map.of(
            "success", true,
            "submissionId", "SUB_1234567890_SN",
            "status", "SUBMITTED",
            "responseCode", 201,
            "platformName", "DGI Sénégal",
            "declarationType", "TVA",
            "period", "2024-01",
            "submissionDate", LocalDateTime.now()
        );

        // Statut de déclaration
        mockDeclarationStatus = Map.of(
            "submissionId", "SUB_1234567890_SN",
            "status", "APPROVED",
            "countryCode", "SN",
            "lastUpdate", LocalDateTime.now(),
            "estimatedCompletion", LocalDateTime.now().plusDays(3)
        );

        // Notifications
        mockNotifications = List.of(
            Map.of(
                "id", "NOTIF_1234567890_0",
                "type", "DECLARATION_APPROVED",
                "priority", "HIGH",
                "title", "Notification 1 pour SN",
                "message", "Message de notification 1",
                "date", LocalDateTime.now().minusDays(1),
                "read", false,
                "countryCode", "SN"
            ),
            Map.of(
                "id", "NOTIF_1234567890_1",
                "type", "PAYMENT_RECEIVED",
                "priority", "MEDIUM",
                "title", "Notification 2 pour SN",
                "message", "Message de notification 2",
                "date", LocalDateTime.now().minusDays(2),
                "read", true,
                "countryCode", "SN"
            )
        );

        // Statistiques d'intégration
        mockIntegrationStats = Map.of(
            "totalCountries", 5,
            "totalPlatforms", 8,
            "supportedCountries", List.of("CMR", "CIV", "SN", "MLI", "BFA"),
            "lastUpdate", LocalDateTime.now(),
            "platformTypeStats", Map.of(
                "taxPlatform", 5,
                "socialPlatform", 3,
                "customsPlatform", 0
            )
        );

        // Résultats de test
        mockTestResults = Map.of(
            "testDate", LocalDateTime.now(),
            "overallStatus", "OPERATIONAL",
            "platformRetrieval", Map.of(
                "CMR", "SUCCESS",
                "SN", "SUCCESS",
                "CIV", "SUCCESS"
            ),
            "connectionTest", Map.of(
                "CMR", "SIMULATED_SUCCESS",
                "SN", "SIMULATED_SUCCESS",
                "CIV", "SIMULATED_SUCCESS"
            ),
            "statistics", Map.of(
                "testedCountries", 3,
                "successfulTests", 3,
                "failedTests", 0,
                "testDuration", "Simulated"
            )
        );
    }

    // ==================== TESTS DES PLATEFORMES GOUVERNEMENTALES ====================

    @Test
    void testGetGovernmentPlatforms_Success() throws Exception {
        when(governmentPlatformService.getAvailablePlatforms("SN")).thenReturn(mockPlatformConfig);

        mockMvc.perform(get("/api/government-platforms/platforms/SN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.countryCode").value("SN"))
                .andExpect(jsonPath("$.platforms.taxPlatform.name").value("DGI Sénégal"))
                .andExpect(jsonPath("$.platforms.socialPlatform.name").value("IPRES Sénégal"));
    }

    @Test
    void testGetGovernmentPlatforms_CountryNotFound() throws Exception {
        when(governmentPlatformService.getAvailablePlatforms("XX"))
                .thenThrow(new IllegalArgumentException("Pays non supporté: XX"));

        mockMvc.perform(get("/api/government-platforms/platforms/XX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors de la récupération des plateformes"));
    }

    @Test
    void testTestPlatformConnection_Success() throws Exception {
        when(governmentPlatformService.testConnection(eq("SN"), eq("taxPlatform")))
                .thenReturn(mockConnectionStatus);

        Map<String, Object> request = Map.of(
            "platformType", "taxPlatform",
            "apiKey", "test_key",
            "apiSecret", "test_secret"
        );

        mockMvc.perform(post("/api/government-platforms/test-connection/SN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.connectionStatus.success").value(true))
                .andExpect(jsonPath("$.connectionStatus.status").value("CONNECTED"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubmitTaxDeclaration_Success() throws Exception {
        when(governmentPlatformService.submitTaxDeclaration(
                eq("SN"), eq("TVA"), eq("2024-01"), any(Map.class), eq(1L)))
                .thenReturn(mockSubmissionResult);

        Map<String, Object> declarationData = Map.of(
            "tvaCollectee", 1000000.0,
            "tvaDeductible", 800000.0,
            "tvaDue", 200000.0
        );

        Map<String, Object> request = Map.of(
            "declarationType", "TVA",
            "period", "2024-01",
            "data", declarationData,
            "companyId", "1"
        );

        mockMvc.perform(post("/api/government-platforms/submit-tax-declaration/SN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.submissionResult.success").value(true))
                .andExpect(jsonPath("$.submissionResult.declarationType").value("TVA"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubmitSocialDeclaration_Success() throws Exception {
        when(governmentPlatformService.submitSocialDeclaration(
                eq("SN"), eq("Déclaration mensuelle"), eq("2024-01"), any(Map.class), eq(1L)))
                .thenReturn(mockSubmissionResult);

        Map<String, Object> declarationData = Map.of(
            "nombreEmployes", 50,
            "salaireTotal", 25000000.0,
            "cotisations", 3750000.0
        );

        Map<String, Object> request = Map.of(
            "declarationType", "Déclaration mensuelle",
            "period", "2024-01",
            "data", declarationData,
            "companyId", "1"
        );

        mockMvc.perform(post("/api/government-platforms/submit-social-declaration/SN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.submissionResult.success").value(true))
                .andExpect(jsonPath("$.submissionResult.declarationType").value("Déclaration mensuelle"));
    }

    @Test
    void testGetDeclarationStatus_Success() throws Exception {
        when(governmentPlatformService.getDeclarationStatus("SUB_1234567890_SN"))
                .thenReturn(mockDeclarationStatus);

        mockMvc.perform(get("/api/government-platforms/declaration-status/SUB_1234567890_SN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.submissionId").value("SUB_1234567890_SN"))
                .andExpect(jsonPath("$.status.status").value("APPROVED"));
    }

    @Test
    void testGetGovernmentNotifications_Success() throws Exception {
        when(governmentPlatformService.getNotifications("SN")).thenReturn(mockNotifications);

        mockMvc.perform(get("/api/government-platforms/notifications/SN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.countryCode").value("SN"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.notifications[0].type").value("DECLARATION_APPROVED"))
                .andExpect(jsonPath("$.notifications[1].type").value("PAYMENT_RECEIVED"));
    }

    // ==================== TESTS DES LOGICIELS TIERS ====================

    @Test
    void testTestThirdPartyConnection_Success() throws Exception {
        Map<String, Object> request = Map.of(
            "softwareType", "accounting",
            "apiKey", "test_key",
            "apiSecret", "test_secret"
        );

        mockMvc.perform(post("/api/government-platforms/test-third-party-connection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.connectionStatus.success").value(true))
                .andExpect(jsonPath("$.connectionStatus.status").value("CONNECTED"));
    }

    @Test
    void testGetIntegrationStatistics_Success() throws Exception {
        when(governmentPlatformService.getIntegrationStatistics()).thenReturn(mockIntegrationStats);

        mockMvc.perform(get("/api/government-platforms/integration-statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statistics.totalCountries").value(5))
                .andExpect(jsonPath("$.statistics.totalPlatforms").value(8))
                .andExpect(jsonPath("$.statistics.supportedCountries").isArray())
                .andExpect(jsonPath("$.statistics.platformTypeStats.taxPlatform").value(5));
    }

    @Test
    void testRunCompletePlatformTest_Success() throws Exception {
        when(governmentPlatformService.runCompleteTest()).thenReturn(mockTestResults);

        mockMvc.perform(post("/api/government-platforms/run-complete-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.testResults.overallStatus").value("OPERATIONAL"))
                .andExpect(jsonPath("$.testResults.statistics.testedCountries").value(3))
                .andExpect(jsonPath("$.testResults.statistics.successfulTests").value(3));
    }

    // ==================== TESTS DES ENDPOINTS DE TEST ====================

    @Test
    void testTestGetPlatforms_Success() throws Exception {
        when(governmentPlatformService.getAvailablePlatforms("CMR")).thenReturn(mockPlatformConfig);

        mockMvc.perform(post("/api/government-platforms/test/platforms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.testType").value("getAvailablePlatforms"))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void testTestConnection_Success() throws Exception {
        when(governmentPlatformService.testConnection("CMR", "taxPlatform")).thenReturn(mockConnectionStatus);

        mockMvc.perform(post("/api/government-platforms/test/connection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.testType").value("testConnection"))
                .andExpect(jsonPath("$.result.success").value(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTestTaxDeclaration_Success() throws Exception {
        when(governmentPlatformService.submitTaxDeclaration(
                eq("CMR"), eq("TVA"), eq("2024-Q1"), any(Map.class), eq(1L)))
                .thenReturn(mockSubmissionResult);

        mockMvc.perform(post("/api/government-platforms/test/tax-declaration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.testType").value("submitTaxDeclaration"))
                .andExpect(jsonPath("$.result.success").value(true));
    }

    @Test
    void testTestGetNotifications_Success() throws Exception {
        when(governmentPlatformService.getNotifications("CMR")).thenReturn(mockNotifications);

        mockMvc.perform(post("/api/government-platforms/test/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.testType").value("getNotifications"))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void testTestGetDeclarationStatus_Success() throws Exception {
        when(governmentPlatformService.getDeclarationStatus(anyString())).thenReturn(mockDeclarationStatus);

        mockMvc.perform(post("/api/government-platforms/test/declaration-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.testType").value("getDeclarationStatus"))
                .andExpect(jsonPath("$.submissionId").exists());
    }

    @Test
    void testTestCompleteFunctionality_Success() throws Exception {
        when(governmentPlatformService.getAvailablePlatforms("CMR")).thenReturn(mockPlatformConfig);
        when(governmentPlatformService.testConnection("CMR", "taxPlatform")).thenReturn(mockConnectionStatus);
        when(governmentPlatformService.getNotifications("CMR")).thenReturn(mockNotifications);
        when(governmentPlatformService.getIntegrationStatistics()).thenReturn(mockIntegrationStats);

        mockMvc.perform(post("/api/government-platforms/test/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.testType").value("completeFunctionality"))
                .andExpect(jsonPath("$.results").exists());
    }

    // ==================== TESTS DE GESTION D'ERREURS ====================

    @Test
    void testTestPlatformConnection_Exception() throws Exception {
        when(governmentPlatformService.testConnection(eq("SN"), eq("taxPlatform")))
                .thenThrow(new RuntimeException("Erreur de connexion"));

        Map<String, Object> request = Map.of(
            "platformType", "taxPlatform",
            "apiKey", "invalid_key",
            "apiSecret", "invalid_secret"
        );

        mockMvc.perform(post("/api/government-platforms/test-connection/SN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors du test de connexion"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubmitTaxDeclaration_Exception() throws Exception {
        when(governmentPlatformService.submitTaxDeclaration(
                eq("SN"), eq("TVA"), eq("2024-01"), any(Map.class), eq(1L)))
                .thenThrow(new RuntimeException("Erreur de soumission"));

        Map<String, Object> request = Map.of(
            "declarationType", "TVA",
            "period", "2024-01",
            "data", Map.of("test", "data"),
            "companyId", "1"
        );

        mockMvc.perform(post("/api/government-platforms/submit-tax-declaration/SN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors de la soumission"));
    }

    @Test
    void testGetIntegrationStatistics_Exception() throws Exception {
        when(governmentPlatformService.getIntegrationStatistics())
                .thenThrow(new RuntimeException("Erreur de récupération des statistiques"));

        mockMvc.perform(get("/api/government-platforms/integration-statistics"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors de la récupération des statistiques"));
    }

    @Test
    void testRunCompletePlatformTest_Exception() throws Exception {
        when(governmentPlatformService.runCompleteTest())
                .thenThrow(new RuntimeException("Erreur d'exécution du test"));

        mockMvc.perform(post("/api/government-platforms/run-complete-test"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors de l'exécution du test"));
    }
}
