package com.ecomptaia.controller;

import com.ecomptaia.service.GovernmentPlatformService;
import com.ecomptaia.service.ThirdPartySoftwareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GovernmentPlatformController.class)
public class GovernmentPlatformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GovernmentPlatformService governmentPlatformService;

    @MockBean
    private ThirdPartySoftwareService thirdPartySoftwareService;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> mockPlatformConfig;
    private Map<String, Object> mockConnectionStatus;
    private Map<String, Object> mockSubmissionResult;
    private List<Map<String, Object>> mockNotifications;
    private List<Map<String, Object>> mockSoftwareList;

    @BeforeEach
    void setUp() {
        // Configuration des mocks pour les plateformes gouvernementales
        mockPlatformConfig = new HashMap<>();
        mockPlatformConfig.put("taxPlatform", Map.of(
            "name", "Direction Générale des Impôts et Domaines (DGID)",
            "baseUrl", "https://api.dgid.sn",
            "apiVersion", "v1",
            "supportedDeclarations", Arrays.asList("TVA", "IS", "IR", "TSS")
        ));
        mockPlatformConfig.put("socialPlatform", Map.of(
            "name", "Caisse Nationale de Sécurité Sociale (CNSS)",
            "baseUrl", "https://api.cnss.sn",
            "apiVersion", "v1",
            "supportedDeclarations", Arrays.asList("Déclaration mensuelle", "Déclaration annuelle")
        ));

        mockConnectionStatus = Map.of(
            "success", true,
            "status", "CONNECTED",
            "responseCode", 200,
            "platformName", "Direction Générale des Impôts et Domaines (DGID)",
            "testUrl", "https://api.dgid.sn/v1/health",
            "timestamp", "2024-01-01T10:00:00"
        );

        mockSubmissionResult = Map.of(
            "success", true,
            "submissionId", "SUB_1704110400000_SN",
            "status", "SUBMITTED",
            "responseCode", 201,
            "platformName", "Direction Générale des Impôts et Domaines (DGID)",
            "declarationType", "TVA",
            "period", "2024-01",
            "submissionDate", "2024-01-01T10:00:00"
        );

        mockNotifications = Arrays.asList(
            Map.of(
                "id", "NOTIF_1704110400000_1",
                "type", "TAX_DEADLINE",
                "priority", "HIGH",
                "title", "Échéance TVA",
                "message", "Déclaration TVA à soumettre avant le 20/01/2024",
                "date", "2024-01-01T10:00:00",
                "read", false,
                "countryCode", "SN"
            ),
            Map.of(
                "id", "NOTIF_1704110400000_2",
                "type", "SOCIAL_DEADLINE",
                "priority", "MEDIUM",
                "title", "Échéance CNSS",
                "message", "Déclaration CNSS à soumettre avant le 15/01/2024",
                "date", "2024-01-01T10:00:00",
                "read", true,
                "countryCode", "SN"
            )
        );

        // Configuration des mocks pour les logiciels tiers
        mockSoftwareList = Arrays.asList(
            Map.of(
                "code", "SAGE",
                "name", "Sage",
                "type", "ACCOUNTING",
                "version", "2024",
                "apiUrl", "https://api.sage.com",
                "apiVersion", "v3",
                "supportedDataTypes", Arrays.asList("ACCOUNTS", "JOURNAL_ENTRIES", "CUSTOMERS", "SUPPLIERS", "INVOICES"),
                "syncModes", Arrays.asList("REAL_TIME", "SCHEDULED", "MANUAL")
            ),
            Map.of(
                "code", "SALESFORCE",
                "name", "Salesforce",
                "type", "CRM",
                "version", "2024",
                "apiUrl", "https://api.salesforce.com",
                "apiVersion", "v58",
                "supportedDataTypes", Arrays.asList("CUSTOMERS", "OPPORTUNITIES", "INVOICES", "PAYMENTS"),
                "syncModes", Arrays.asList("REAL_TIME", "SCHEDULED", "MANUAL")
            )
        );
    }

    // ==================== TESTS DES APIS GOUVERNEMENTALES ====================

    @Test
    void testGetGovernmentPlatforms_Success() throws Exception {
        when(governmentPlatformService.getAvailablePlatforms("SN")).thenReturn(mockPlatformConfig);

        mockMvc.perform(get("/api/government-platform/platforms/SN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.countryCode").value("SN"))
                .andExpect(jsonPath("$.platforms.taxPlatform.name").value("Direction Générale des Impôts et Domaines (DGID)"))
                .andExpect(jsonPath("$.platforms.socialPlatform.name").value("Caisse Nationale de Sécurité Sociale (CNSS)"));
    }

    @Test
    void testGetGovernmentPlatforms_CountryNotFound() throws Exception {
        when(governmentPlatformService.getAvailablePlatforms("XX"))
                .thenThrow(new IllegalArgumentException("Pays non supporté: XX"));

        mockMvc.perform(get("/api/government-platform/platforms/XX"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors de la récupération des plateformes"));
    }

    @Test
    void testTestPlatformConnection_Success() throws Exception {
        when(governmentPlatformService.testConnection(eq("SN"), eq("taxPlatform"), eq("test_key"), eq("test_secret")))
                .thenReturn(mockConnectionStatus);

        Map<String, Object> request = Map.of(
            "countryCode", "SN",
            "platformType", "taxPlatform",
            "apiKey", "test_key",
            "apiSecret", "test_secret"
        );

        mockMvc.perform(post("/api/government-platform/test-connection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.connectionStatus.success").value(true))
                .andExpect(jsonPath("$.connectionStatus.status").value("CONNECTED"));
    }

    @Test
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
            "countryCode", "SN",
            "declarationType", "TVA",
            "period", "2024-01",
            "declarationData", declarationData,
            "companyId", 1L
        );

        mockMvc.perform(post("/api/government-platform/submit-tax-declaration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.submissionResult.success").value(true))
                .andExpect(jsonPath("$.submissionResult.declarationType").value("TVA"));
    }

    @Test
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
            "countryCode", "SN",
            "declarationType", "Déclaration mensuelle",
            "period", "2024-01",
            "declarationData", declarationData,
            "companyId", 1L
        );

        mockMvc.perform(post("/api/government-platform/submit-social-declaration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.submissionResult.success").value(true));
    }

    @Test
    void testGetDeclarationStatus_Success() throws Exception {
        Map<String, Object> mockStatus = Map.of(
            "submissionId", "SUB_1704110400000_SN",
            "status", "APPROVED",
            "lastUpdate", "2024-01-01T10:00:00",
            "countryCode", "SN",
            "message", "Statut récupéré avec succès"
        );

        when(governmentPlatformService.getDeclarationStatus("SUB_1704110400000_SN"))
                .thenReturn(mockStatus);

        mockMvc.perform(get("/api/government-platform/declaration-status/SUB_1704110400000_SN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.submissionId").value("SUB_1704110400000_SN"))
                .andExpect(jsonPath("$.status.status").value("APPROVED"));
    }

    @Test
    void testGetGovernmentNotifications_Success() throws Exception {
        when(governmentPlatformService.getNotifications("SN")).thenReturn(mockNotifications);

        mockMvc.perform(get("/api/government-platform/notifications/SN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.countryCode").value("SN"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.notifications[0].type").value("TAX_DEADLINE"))
                .andExpect(jsonPath("$.notifications[1].type").value("SOCIAL_DEADLINE"));
    }

    // ==================== TESTS DES LOGICIELS TIERS ====================

    @Test
    void testGetThirdPartySoftware_Success() throws Exception {
        when(thirdPartySoftwareService.getAvailableSoftware()).thenReturn(mockSoftwareList);

        mockMvc.perform(get("/api/government-platform/third-party-software"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.softwareList[0].name").value("Sage"))
                .andExpect(jsonPath("$.softwareList[1].name").value("Salesforce"));
    }

    @Test
    void testTestThirdPartyConnection_Success() throws Exception {
        Map<String, Object> mockConnectionStatus = Map.of(
            "success", true,
            "status", "CONNECTED",
            "responseCode", 200,
            "softwareName", "Sage",
            "softwareType", "ACCOUNTING",
            "testUrl", "https://api.sage.com/v3/health",
            "timestamp", "2024-01-01T10:00:00"
        );

        when(thirdPartySoftwareService.testConnection(eq("SAGE"), eq("https://api.sage.com"), eq("test_key"), eq("test_secret")))
                .thenReturn(mockConnectionStatus);

        Map<String, Object> request = Map.of(
            "softwareName", "SAGE",
            "apiUrl", "https://api.sage.com",
            "apiKey", "test_key",
            "apiSecret", "test_secret"
        );

        mockMvc.perform(post("/api/government-platform/test-third-party-connection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.softwareName").value("SAGE"))
                .andExpect(jsonPath("$.connectionStatus.success").value(true));
    }

    @Test
    void testSyncThirdPartyData_Success() throws Exception {
        Map<String, Object> mockSyncResult = Map.of(
            "success", true,
            "syncId", "SYNC_1704110400000_SAGE",
            "status", "SYNCED",
            "responseCode", 200,
            "softwareName", "Sage",
            "dataType", "ACCOUNTS",
            "syncDirection", "BOTH",
            "recordsProcessed", 500,
            "syncDate", "2024-01-01T10:00:00"
        );

        when(thirdPartySoftwareService.syncData(eq("SAGE"), eq("ACCOUNTS"), eq("BOTH"), eq(1L)))
                .thenReturn(mockSyncResult);

        Map<String, Object> request = Map.of(
            "softwareName", "SAGE",
            "dataType", "ACCOUNTS",
            "syncDirection", "BOTH",
            "companyId", 1L
        );

        mockMvc.perform(post("/api/government-platform/sync-third-party-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.syncResult.success").value(true))
                .andExpect(jsonPath("$.syncResult.dataType").value("ACCOUNTS"));
    }

    @Test
    void testGetSyncLogs_Success() throws Exception {
        List<Map<String, Object>> mockLogs = Arrays.asList(
            Map.of(
                "id", "LOG_1704110400000_1",
                "softwareName", "SAGE",
                "dataType", "ACCOUNTS",
                "syncDirection", "BOTH",
                "status", "SUCCESS",
                "recordsProcessed", 500,
                "startTime", "2024-01-01T10:00:00",
                "endTime", "2024-01-01T10:05:00",
                "duration", 300
            ),
            Map.of(
                "id", "LOG_1704110400000_2",
                "softwareName", "SAGE",
                "dataType", "JOURNAL_ENTRIES",
                "syncDirection", "INBOUND",
                "status", "SUCCESS",
                "recordsProcessed", 200,
                "startTime", "2024-01-01T09:00:00",
                "endTime", "2024-01-01T09:02:00",
                "duration", 120
            )
        );

        when(thirdPartySoftwareService.getSyncLogs("SAGE")).thenReturn(mockLogs);

        mockMvc.perform(get("/api/government-platform/sync-logs/SAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.softwareName").value("SAGE"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.logs[0].dataType").value("ACCOUNTS"))
                .andExpect(jsonPath("$.logs[1].dataType").value("JOURNAL_ENTRIES"));
    }

    @Test
    void testConfigureThirdPartyIntegration_Success() throws Exception {
        Map<String, Object> mockConfigResult = Map.of(
            "success", true,
            "integrationId", "INT_1704110400000_SAGE",
            "status", "CONFIGURED",
            "responseCode", 200,
            "softwareName", "Sage",
            "integrationType", "REAL_TIME",
            "configDate", "2024-01-01T10:00:00"
        );

        Map<String, Object> configuration = Map.of(
            "apiKey", "test_api_key",
            "apiSecret", "test_api_secret",
            "syncInterval", "5 minutes"
        );

        when(thirdPartySoftwareService.configureIntegration(eq("SAGE"), eq("REAL_TIME"), eq(configuration), eq(1L)))
                .thenReturn(mockConfigResult);

        Map<String, Object> request = Map.of(
            "softwareName", "SAGE",
            "integrationType", "REAL_TIME",
            "configuration", configuration,
            "companyId", 1L
        );

        mockMvc.perform(post("/api/government-platform/configure-third-party-integration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.configResult.success").value(true))
                .andExpect(jsonPath("$.configResult.integrationType").value("REAL_TIME"));
    }

    // ==================== TESTS DES STATISTIQUES ET TESTS COMPLETS ====================

    @Test
    void testGetIntegrationStatistics_Success() throws Exception {
        Map<String, Object> mockGovStats = Map.of(
            "totalCountries", 5,
            "totalPlatforms", 15,
            "supportedCountries", Arrays.asList("SN", "FR", "US", "BF", "CI"),
            "lastUpdate", "2024-01-01T10:00:00"
        );

        Map<String, Object> mockThirdPartyStats = Map.of(
            "totalSoftware", 12,
            "lastUpdate", "2024-01-01T10:00:00",
            "softwareTypeStats", Map.of(
                "ACCOUNTING", 4,
                "CRM", 2,
                "HR", 2,
                "ERP", 2,
                "PAYMENT", 2
            )
        );

        when(governmentPlatformService.getIntegrationStatistics()).thenReturn(mockGovStats);
        when(thirdPartySoftwareService.getIntegrationStatistics()).thenReturn(mockThirdPartyStats);

        mockMvc.perform(get("/api/government-platform/integration-statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.governmentPlatforms.totalCountries").value(5))
                .andExpect(jsonPath("$.thirdPartySoftware.totalSoftware").value(12));
    }

    @Test
    void testTestCompleteModule_Success() throws Exception {
        Map<String, Object> mockGovTestResults = Map.of(
            "overallStatus", "OPERATIONAL",
            "testDate", "2024-01-01T10:00:00",
            "testedCountries", 5
        );

        Map<String, Object> mockThirdPartyTestResults = Map.of(
            "overallStatus", "OPERATIONAL",
            "testDate", "2024-01-01T10:00:00",
            "testedSoftware", 4
        );

        when(governmentPlatformService.runCompleteTest()).thenReturn(mockGovTestResults);
        when(thirdPartySoftwareService.runCompleteTest()).thenReturn(mockThirdPartyTestResults);

        mockMvc.perform(post("/api/government-platform/test-complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.governmentPlatforms.overallStatus").value("OPERATIONAL"))
                .andExpect(jsonPath("$.thirdPartySoftware.overallStatus").value("OPERATIONAL"))
                .andExpect(jsonPath("$.overallStatus").value("OPERATIONAL"));
    }

    @Test
    void testTestCompleteModule_Error() throws Exception {
        when(governmentPlatformService.runCompleteTest())
                .thenThrow(new RuntimeException("Erreur de test"));

        mockMvc.perform(post("/api/government-platform/test-complete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors du test complet"));
    }

    // ==================== TESTS DE VALIDATION ====================

    @Test
    void testSubmitTaxDeclaration_MissingRequiredFields() throws Exception {
        Map<String, Object> request = Map.of(
            "countryCode", "SN",
            "declarationType", "TVA"
            // Manque period, declarationData, companyId
        );

        mockMvc.perform(post("/api/government-platform/submit-tax-declaration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTestThirdPartyConnection_InvalidSoftware() throws Exception {
        when(thirdPartySoftwareService.testConnection(eq("INVALID"), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Logiciel non supporté: INVALID"));

        Map<String, Object> request = Map.of(
            "softwareName", "INVALID",
            "apiUrl", "https://api.invalid.com",
            "apiKey", "test_key",
            "apiSecret", "test_secret"
        );

        mockMvc.perform(post("/api/government-platform/test-third-party-connection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors du test de connexion"));
    }

    @Test
    void testSyncThirdPartyData_UnsupportedDataType() throws Exception {
        when(thirdPartySoftwareService.syncData(eq("SAGE"), eq("UNSUPPORTED"), eq("BOTH"), eq(1L)))
                .thenThrow(new IllegalArgumentException("Type de données non supporté: UNSUPPORTED"));

        Map<String, Object> request = Map.of(
            "softwareName", "SAGE",
            "dataType", "UNSUPPORTED",
            "syncDirection", "BOTH",
            "companyId", 1L
        );

        mockMvc.perform(post("/api/government-platform/sync-third-party-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreur lors de la synchronisation"));
    }
}


