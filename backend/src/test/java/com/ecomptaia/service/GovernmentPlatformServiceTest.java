package com.ecomptaia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GovernmentPlatformServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GovernmentPlatformService governmentPlatformService;

    private Map<String, Object> testDeclarationData;
    
    // Type reference pour les tests
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE_REF = 
        new ParameterizedTypeReference<Map<String, Object>>() {};

    @BeforeEach
    void setUp() {
        testDeclarationData = new HashMap<>();
        testDeclarationData.put("montant", 1000000.0);
        testDeclarationData.put("devise", "XOF");
        testDeclarationData.put("description", "Test déclaration fiscale");
        testDeclarationData.put("dateDeclaration", LocalDateTime.now());
    }

    // ==================== TESTS DE CONFIGURATION ====================

    @Test
    void testGetAvailablePlatforms_Success() {
        Map<String, Object> platforms = governmentPlatformService.getAvailablePlatforms("SN");
        
        assertNotNull(platforms);
        assertTrue(platforms.containsKey("taxPlatform"));
        assertTrue(platforms.containsKey("socialPlatform"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> taxPlatform = (Map<String, Object>) platforms.get("taxPlatform");
        assertEquals("DGI Sénégal", taxPlatform.get("name"));
        assertEquals("https://api.dgi.sn", taxPlatform.get("baseUrl"));
    }

    @Test
    void testGetAvailablePlatforms_UnsupportedCountry() {
        assertThrows(IllegalArgumentException.class, () -> {
            governmentPlatformService.getAvailablePlatforms("XX");
        });
    }

    @Test
    void testGetAvailablePlatforms_Cameroon() {
        Map<String, Object> platforms = governmentPlatformService.getAvailablePlatforms("CMR");
        
        assertNotNull(platforms);
        assertTrue(platforms.containsKey("taxPlatform"));
        assertTrue(platforms.containsKey("socialPlatform"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> taxPlatform = (Map<String, Object>) platforms.get("taxPlatform");
        assertEquals("DGI Cameroun", taxPlatform.get("name"));
        assertEquals("https://api.dgi.cm", taxPlatform.get("baseUrl"));
    }

    @Test
    void testGetAvailablePlatforms_IvoryCoast() {
        Map<String, Object> platforms = governmentPlatformService.getAvailablePlatforms("CIV");
        
        assertNotNull(platforms);
        assertTrue(platforms.containsKey("taxPlatform"));
        assertTrue(platforms.containsKey("socialPlatform"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> socialPlatform = (Map<String, Object>) platforms.get("socialPlatform");
        assertEquals("CNPS Côte d'Ivoire", socialPlatform.get("name"));
        assertEquals("https://api.cnps.ci", socialPlatform.get("baseUrl"));
    }

    @Test
    void testGetAvailablePlatforms_Mali() {
        Map<String, Object> platforms = governmentPlatformService.getAvailablePlatforms("MLI");
        
        assertNotNull(platforms);
        assertTrue(platforms.containsKey("taxPlatform"));
        assertFalse(platforms.containsKey("socialPlatform")); // Mali n'a que la plateforme fiscale
        
        @SuppressWarnings("unchecked")
        Map<String, Object> taxPlatform = (Map<String, Object>) platforms.get("taxPlatform");
        assertEquals("DGI Mali", taxPlatform.get("name"));
        assertEquals("https://api.dgi.ml", taxPlatform.get("baseUrl"));
    }

    @Test
    void testGetAvailablePlatforms_BurkinaFaso() {
        Map<String, Object> platforms = governmentPlatformService.getAvailablePlatforms("BFA");
        
        assertNotNull(platforms);
        assertTrue(platforms.containsKey("taxPlatform"));
        assertFalse(platforms.containsKey("socialPlatform")); // Burkina Faso n'a que la plateforme fiscale
        
        @SuppressWarnings("unchecked")
        Map<String, Object> taxPlatform = (Map<String, Object>) platforms.get("taxPlatform");
        assertEquals("DGI Burkina Faso", taxPlatform.get("name"));
        assertEquals("https://api.dgi.bf", taxPlatform.get("baseUrl"));
    }

    // ==================== TESTS DE CONNEXION ====================

    @Test
    void testTestConnection_Success() {
        // Mock de la réponse HTTP
        ResponseEntity<String> mockResponse = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = governmentPlatformService.testConnection("SN", "taxPlatform");
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("CONNECTED", result.get("status"));
        assertEquals(200, result.get("responseCode"));
        assertEquals("DGI Sénégal", result.get("softwareName"));
    }

    @Test
    void testTestConnection_HttpError() {
        // Mock d'une erreur HTTP
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Map<String, Object> result = governmentPlatformService.testConnection("SN", "taxPlatform");
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("CONNECTION_FAILED", result.get("status"));
        assertEquals(401, result.get("errorCode"));
        assertNotNull(result.get("errorMessage"));
    }

    // ==================== TESTS DE SOUMISSION FISCALE ====================

    @Test
    void testSubmitTaxDeclaration_Success() {
        // Mock de la réponse HTTP
        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.CREATED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(MAP_TYPE_REF)))
                .thenReturn(mockResponse);

        Map<String, Object> result = governmentPlatformService.submitTaxDeclaration(
            "SN", "TVA", "2024-01", testDeclarationData, 1L
        );
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("SUBMITTED", result.get("status"));
        assertEquals(201, result.get("responseCode"));
        assertEquals("DGI Sénégal", result.get("platformName"));
        assertEquals("TVA", result.get("declarationType"));
        assertEquals("2024-01", result.get("period"));
        assertTrue(result.get("submissionId").toString().startsWith("SUB_"));
        assertTrue(result.get("submissionId").toString().endsWith("_SN"));
    }

    @Test
    void testSubmitTaxDeclaration_SubmissionFailed() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(MAP_TYPE_REF)))
                .thenThrow(new RuntimeException("Submission failed"));

        Map<String, Object> result = governmentPlatformService.submitTaxDeclaration(
            "SN", "TVA", "2024-01", testDeclarationData, 1L
        );
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("SUBMISSION_FAILED", result.get("status"));
        assertNotNull(result.get("errorMessage"));
    }

    @Test
    void testSubmitTaxDeclaration_Cameroon() {
        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.CREATED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(MAP_TYPE_REF)))
                .thenReturn(mockResponse);

        Map<String, Object> result = governmentPlatformService.submitTaxDeclaration(
            "CMR", "TVA", "2024-01", testDeclarationData, 1L
        );
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("DGI Cameroun", result.get("platformName"));
        assertTrue(result.get("submissionId").toString().endsWith("_CMR"));
    }

    // ==================== TESTS DE SOUMISSION SOCIALE ====================

    @Test
    void testSubmitSocialDeclaration_Success() {
        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.CREATED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(MAP_TYPE_REF)))
                .thenReturn(mockResponse);

        Map<String, Object> socialData = Map.of(
            "nombreEmployes", 50,
            "salaireTotal", 25000000.0,
            "cotisations", 3750000.0
        );

        Map<String, Object> result = governmentPlatformService.submitSocialDeclaration(
            "SN", "Déclaration mensuelle", "2024-01", socialData, 1L
        );
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("SUBMITTED", result.get("status"));
        assertEquals(201, result.get("responseCode"));
        assertEquals("IPRES Sénégal", result.get("platformName"));
        assertEquals("Déclaration mensuelle", result.get("declarationType"));
        assertEquals("2024-01", result.get("period"));
        assertTrue(result.get("submissionId").toString().startsWith("SOC_"));
        assertTrue(result.get("submissionId").toString().endsWith("_SN"));
    }

    @Test
    void testSubmitSocialDeclaration_SubmissionFailed() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(MAP_TYPE_REF)))
                .thenThrow(new RuntimeException("Social submission failed"));

        Map<String, Object> socialData = Map.of(
            "nombreEmployes", 50,
            "salaireTotal", 25000000.0
        );

        Map<String, Object> result = governmentPlatformService.submitSocialDeclaration(
            "SN", "Déclaration mensuelle", "2024-01", socialData, 1L
        );
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("SUBMISSION_FAILED", result.get("status"));
        assertNotNull(result.get("errorMessage"));
    }

    @Test
    void testSubmitSocialDeclaration_Cameroon() {
        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.CREATED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(MAP_TYPE_REF)))
                .thenReturn(mockResponse);

        Map<String, Object> socialData = Map.of(
            "nombreEmployes", 100,
            "salaireTotal", 50000000.0
        );

        Map<String, Object> result = governmentPlatformService.submitSocialDeclaration(
            "CMR", "Déclaration trimestrielle", "2024-Q1", socialData, 1L
        );
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("CNPS Cameroun", result.get("platformName"));
        assertTrue(result.get("submissionId").toString().endsWith("_CMR"));
    }

    // ==================== TESTS DE STATUT DE DÉCLARATION ====================

    @Test
    void testGetDeclarationStatus_Success() {
        String submissionId = "SUB_1234567890_CMR";
        
        Map<String, Object> result = governmentPlatformService.getDeclarationStatus(submissionId);
        
        assertNotNull(result);
        assertTrue(result.containsKey("submissionId"));
        assertTrue(result.containsKey("status"));
        assertTrue(result.containsKey("countryCode"));
        assertTrue(result.containsKey("lastUpdate"));
        assertTrue(result.containsKey("estimatedCompletion"));
        
        assertEquals(submissionId, result.get("submissionId"));
        assertEquals("CMR", result.get("countryCode"));
    }

    @Test
    void testGetDeclarationStatus_InvalidFormat() {
        String invalidSubmissionId = "INVALID_ID";
        
        Map<String, Object> result = governmentPlatformService.getDeclarationStatus(invalidSubmissionId);
        
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertTrue(result.containsKey("message"));
    }

    // ==================== TESTS DE NOTIFICATIONS ====================

    @Test
    void testGetNotifications_Success() {
        List<Map<String, Object>> notifications = governmentPlatformService.getNotifications("CMR");
        
        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        
        Map<String, Object> firstNotification = notifications.get(0);
        assertTrue(firstNotification.containsKey("id"));
        assertTrue(firstNotification.containsKey("type"));
        assertTrue(firstNotification.containsKey("priority"));
        assertTrue(firstNotification.containsKey("title"));
        assertTrue(firstNotification.containsKey("message"));
        assertTrue(firstNotification.containsKey("date"));
        assertTrue(firstNotification.containsKey("read"));
        assertEquals("CMR", firstNotification.get("countryCode"));
    }

    @Test
    void testGetNotifications_UnsupportedCountry() {
        List<Map<String, Object>> notifications = governmentPlatformService.getNotifications("XX");
        
        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        
        Map<String, Object> errorNotification = notifications.get(0);
        assertTrue(errorNotification.containsKey("error"));
        assertEquals("Erreur lors de la récupération des notifications", errorNotification.get("error"));
    }

    // ==================== TESTS DE STATISTIQUES ====================

    @Test
    void testGetIntegrationStatistics() {
        Map<String, Object> stats = governmentPlatformService.getIntegrationStatistics();
        
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalCountries"));
        assertTrue(stats.containsKey("totalPlatforms"));
        assertTrue(stats.containsKey("supportedCountries"));
        assertTrue(stats.containsKey("lastUpdate"));
        assertTrue(stats.containsKey("platformTypeStats"));
        
        assertTrue((Integer) stats.get("totalCountries") > 0);
        assertTrue((Integer) stats.get("totalPlatforms") > 0);
        
        @SuppressWarnings("unchecked")
        List<String> supportedCountries = (List<String>) stats.get("supportedCountries");
        assertFalse(supportedCountries.isEmpty());
        assertTrue(supportedCountries.contains("CMR"));
        assertTrue(supportedCountries.contains("SN"));
        assertTrue(supportedCountries.contains("CIV"));
    }

    // ==================== TESTS DE TEST COMPLET ====================

    @Test
    void testRunCompleteTest() {
        Map<String, Object> testResults = governmentPlatformService.runCompleteTest();
        
        assertNotNull(testResults);
        assertTrue(testResults.containsKey("testDate"));
        assertTrue(testResults.containsKey("overallStatus"));
        assertTrue(testResults.containsKey("platformRetrieval"));
        assertTrue(testResults.containsKey("connectionTest"));
        assertTrue(testResults.containsKey("statistics"));
        
        assertEquals("OPERATIONAL", testResults.get("overallStatus"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> platformRetrieval = (Map<String, Object>) testResults.get("platformRetrieval");
        assertFalse(platformRetrieval.isEmpty());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> statistics = (Map<String, Object>) testResults.get("statistics");
        assertTrue((Integer) statistics.get("testedCountries") > 0);
        assertTrue((Integer) statistics.get("successfulTests") > 0);
        assertEquals(0, statistics.get("failedTests"));
    }

    // ==================== TESTS DE GESTION D'ERREURS ====================

    @Test
    void testTestConnection_GenericException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        Map<String, Object> result = governmentPlatformService.testConnection("CMR", "taxPlatform");
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("ERROR", result.get("status"));
        assertNotNull(result.get("errorMessage"));
        assertEquals("Network error", result.get("errorMessage"));
    }

    @Test
    void testSubmitTaxDeclaration_GenericException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(MAP_TYPE_REF)))
                .thenThrow(new RuntimeException("Service unavailable"));

        Map<String, Object> result = governmentPlatformService.submitTaxDeclaration(
            "CMR", "TVA", "2024-01", testDeclarationData, 1L
        );
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("SUBMISSION_FAILED", result.get("status"));
        assertEquals("Service unavailable", result.get("errorMessage"));
    }

    @Test
    void testSubmitSocialDeclaration_GenericException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(MAP_TYPE_REF)))
                .thenThrow(new RuntimeException("Database connection failed"));

        Map<String, Object> socialData = Map.of(
            "nombreEmployes", 25,
            "salaireTotal", 12500000.0
        );

        Map<String, Object> result = governmentPlatformService.submitSocialDeclaration(
            "CIV", "Déclaration mensuelle", "2024-01", socialData, 1L
        );
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("SUBMISSION_FAILED", result.get("status"));
        assertEquals("Database connection failed", result.get("errorMessage"));
    }
}
