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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GovernmentPlatformServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GovernmentPlatformService governmentPlatformService;

    private Map<String, Object> testPlatformConfig;
    private Map<String, Object> testDeclarationData;

    @BeforeEach
    void setUp() {
        testPlatformConfig = new HashMap<>();
        testPlatformConfig.put("taxPlatform", Map.of(
            "name", "Direction Générale des Impôts et Domaines (DGID)",
            "baseUrl", "https://api.dgid.sn",
            "apiVersion", "v1",
            "supportedDeclarations", Arrays.asList("TVA", "IS", "IR", "TSS")
        ));
        testPlatformConfig.put("socialPlatform", Map.of(
            "name", "Caisse Nationale de Sécurité Sociale (CNSS)",
            "baseUrl", "https://api.cnss.sn",
            "apiVersion", "v1",
            "supportedDeclarations", Arrays.asList("Déclaration mensuelle", "Déclaration annuelle")
        ));

        testDeclarationData = Map.of(
            "tvaCollectee", 1000000.0,
            "tvaDeductible", 800000.0,
            "tvaDue", 200000.0
        );
    }

    // ==================== TESTS DE RÉCUPÉRATION DES PLATEFORMES ====================

    @Test
    void testGetAvailablePlatforms_Success() {
        // Test pour le Sénégal
        Map<String, Object> result = governmentPlatformService.getAvailablePlatforms("SN");
        
        assertNotNull(result);
        assertTrue(result.containsKey("taxPlatform"));
        assertTrue(result.containsKey("socialPlatform"));
        
        Map<String, Object> taxPlatform = (Map<String, Object>) result.get("taxPlatform");
        assertEquals("Direction Générale des Impôts et Domaines (DGID)", taxPlatform.get("name"));
        assertEquals("https://api.dgid.sn", taxPlatform.get("baseUrl"));
        
        Map<String, Object> socialPlatform = (Map<String, Object>) result.get("socialPlatform");
        assertEquals("Caisse Nationale de Sécurité Sociale (CNSS)", socialPlatform.get("name"));
        assertEquals("https://api.cnss.sn", socialPlatform.get("baseUrl"));
    }

    @Test
    void testGetAvailablePlatforms_France() {
        Map<String, Object> result = governmentPlatformService.getAvailablePlatforms("FR");
        
        assertNotNull(result);
        assertTrue(result.containsKey("taxPlatform"));
        assertTrue(result.containsKey("socialPlatform"));
        assertTrue(result.containsKey("customsPlatform"));
        
        Map<String, Object> taxPlatform = (Map<String, Object>) result.get("taxPlatform");
        assertEquals("Direction Générale des Finances Publiques (DGFiP)", taxPlatform.get("name"));
        assertEquals("https://api.impots.gouv.fr", taxPlatform.get("baseUrl"));
    }

    @Test
    void testGetAvailablePlatforms_UnitedStates() {
        Map<String, Object> result = governmentPlatformService.getAvailablePlatforms("US");
        
        assertNotNull(result);
        assertTrue(result.containsKey("taxPlatform"));
        assertTrue(result.containsKey("socialPlatform"));
        
        Map<String, Object> taxPlatform = (Map<String, Object>) result.get("taxPlatform");
        assertEquals("Internal Revenue Service (IRS)", taxPlatform.get("name"));
        assertEquals("https://api.irs.gov", taxPlatform.get("baseUrl"));
    }

    @Test
    void testGetAvailablePlatforms_CountryNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            governmentPlatformService.getAvailablePlatforms("XX");
        });
    }

    @Test
    void testGetAvailablePlatforms_CaseInsensitive() {
        // Test avec code pays en minuscules
        Map<String, Object> result = governmentPlatformService.getAvailablePlatforms("sn");
        
        assertNotNull(result);
        assertTrue(result.containsKey("taxPlatform"));
        assertTrue(result.containsKey("socialPlatform"));
    }

    // ==================== TESTS DE CONNEXION ====================

    @Test
    void testTestConnection_Success() {
        // Mock de la réponse HTTP
        ResponseEntity<String> mockResponse = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = governmentPlatformService.testConnection("SN", "taxPlatform", "test_key", "test_secret");
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("CONNECTED", result.get("status"));
        assertEquals(200, result.get("responseCode"));
        assertEquals("Direction Générale des Impôts et Domaines (DGID)", result.get("platformName"));
    }

    @Test
    void testTestConnection_HttpError() {
        // Mock d'une erreur HTTP
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Map<String, Object> result = governmentPlatformService.testConnection("SN", "taxPlatform", "invalid_key", "invalid_secret");
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("CONNECTION_FAILED", result.get("status"));
        assertEquals(401, result.get("errorCode"));
    }

    @Test
    void testTestConnection_PlatformTypeNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            governmentPlatformService.testConnection("SN", "invalidPlatform", "test_key", "test_secret");
        });
    }

    @Test
    void testTestConnection_GeneralException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        Map<String, Object> result = governmentPlatformService.testConnection("SN", "taxPlatform", "test_key", "test_secret");
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("ERROR", result.get("status"));
        assertNotNull(result.get("errorMessage"));
    }

    // ==================== TESTS DE SOUMISSION FISCALE ====================

    @Test
    void testSubmitTaxDeclaration_Success() {
        // Mock de la réponse HTTP
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.CREATED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = governmentPlatformService.submitTaxDeclaration(
            "SN", "TVA", "2024-01", testDeclarationData, 1L
        );
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("SUBMITTED", result.get("status"));
        assertEquals(201, result.get("responseCode"));
        assertEquals("Direction Générale des Impôts et Domaines (DGID)", result.get("platformName"));
        assertEquals("TVA", result.get("declarationType"));
        assertEquals("2024-01", result.get("period"));
        assertTrue(result.get("submissionId").toString().startsWith("SUB_"));
        assertTrue(result.get("submissionId").toString().endsWith("_SN"));
    }

    @Test
    void testSubmitTaxDeclaration_SubmissionFailed() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
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
    void testSubmitTaxDeclaration_France() {
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.CREATED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = governmentPlatformService.submitTaxDeclaration(
            "FR", "TVA", "2024-01", testDeclarationData, 1L
        );
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("Direction Générale des Finances Publiques (DGFiP)", result.get("platformName"));
        assertTrue(result.get("submissionId").toString().endsWith("_FR"));
    }

    // ==================== TESTS DE SOUMISSION SOCIALE ====================

    @Test
    void testSubmitSocialDeclaration_Success() {
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.CREATED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
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
        assertEquals("Caisse Nationale de Sécurité Sociale (CNSS)", result.get("platformName"));
        assertEquals("Déclaration mensuelle", result.get("declarationType"));
        assertTrue(result.get("submissionId").toString().startsWith("SOC_"));
    }

    @Test
    void testSubmitSocialDeclaration_SubmissionFailed() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Social submission failed"));

        Map<String, Object> socialData = Map.of("test", "data");

        Map<String, Object> result = governmentPlatformService.submitSocialDeclaration(
            "SN", "Déclaration mensuelle", "2024-01", socialData, 1L
        );
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("SUBMISSION_FAILED", result.get("status"));
    }

    // ==================== TESTS DE STATUT DE DÉCLARATION ====================

    @Test
    void testGetDeclarationStatus_Success() {
        String submissionId = "SUB_1704110400000_SN";
        
        Map<String, Object> result = governmentPlatformService.getDeclarationStatus(submissionId);
        
        assertNotNull(result);
        assertEquals(submissionId, result.get("submissionId"));
        assertEquals("SN", result.get("countryCode"));
        assertNotNull(result.get("status"));
        assertNotNull(result.get("lastUpdate"));
        assertEquals("Statut récupéré avec succès", result.get("message"));
        
        // Vérifier que le statut est l'un des statuts possibles
        String status = (String) result.get("status");
        assertTrue(Arrays.asList("PENDING", "PROCESSING", "APPROVED", "REJECTED", "COMPLETED").contains(status));
    }

    @Test
    void testGetDeclarationStatus_InvalidSubmissionId() {
        String submissionId = "INVALID_ID";
        
        Map<String, Object> result = governmentPlatformService.getDeclarationStatus(submissionId);
        
        assertNotNull(result);
        assertEquals(submissionId, result.get("submissionId"));
        assertEquals("ERROR", result.get("status"));
        assertNotNull(result.get("errorMessage"));
    }

    // ==================== TESTS DE NOTIFICATIONS ====================

    @Test
    void testGetNotifications_Success() {
        List<Map<String, Object>> result = governmentPlatformService.getNotifications("SN");
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1 && result.size() <= 5); // Entre 1 et 5 notifications
        
        for (Map<String, Object> notification : result) {
            assertNotNull(notification.get("id"));
            assertNotNull(notification.get("type"));
            assertNotNull(notification.get("priority"));
            assertNotNull(notification.get("title"));
            assertNotNull(notification.get("message"));
            assertNotNull(notification.get("date"));
            assertNotNull(notification.get("read"));
            assertEquals("SN", notification.get("countryCode"));
            
            // Vérifier les types de notification
            String type = (String) notification.get("type");
            assertTrue(Arrays.asList("TAX_DEADLINE", "SOCIAL_DEADLINE", "REGULATORY_UPDATE", "SYSTEM_MAINTENANCE").contains(type));
            
            // Vérifier les priorités
            String priority = (String) notification.get("priority");
            assertTrue(Arrays.asList("LOW", "MEDIUM", "HIGH", "URGENT").contains(priority));
        }
    }

    @Test
    void testGetNotifications_DifferentCountries() {
        List<Map<String, Object>> senegalNotifications = governmentPlatformService.getNotifications("SN");
        List<Map<String, Object>> franceNotifications = governmentPlatformService.getNotifications("FR");
        
        assertNotNull(senegalNotifications);
        assertNotNull(franceNotifications);
        
        for (Map<String, Object> notification : senegalNotifications) {
            assertEquals("SN", notification.get("countryCode"));
        }
        
        for (Map<String, Object> notification : franceNotifications) {
            assertEquals("FR", notification.get("countryCode"));
        }
    }

    @Test
    void testGetNotifications_Error() {
        // Simuler une erreur en passant un pays non supporté
        List<Map<String, Object>> result = governmentPlatformService.getNotifications("XX");
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).containsKey("error"));
        assertEquals("Erreur lors de la récupération des notifications", result.get(0).get("error"));
    }

    // ==================== TESTS DE STATISTIQUES ====================

    @Test
    void testGetIntegrationStatistics_Success() {
        Map<String, Object> result = governmentPlatformService.getIntegrationStatistics();
        
        assertNotNull(result);
        assertTrue((Integer) result.get("totalCountries") > 0);
        assertTrue((Integer) result.get("totalPlatforms") > 0);
        assertNotNull(result.get("supportedCountries"));
        assertNotNull(result.get("lastUpdate"));
        
        List<String> supportedCountries = (List<String>) result.get("supportedCountries");
        assertTrue(supportedCountries.contains("SN"));
        assertTrue(supportedCountries.contains("FR"));
        assertTrue(supportedCountries.contains("US"));
        assertTrue(supportedCountries.contains("BF"));
        assertTrue(supportedCountries.contains("CI"));
        
        // Vérifier les statistiques par type de plateforme
        Map<String, Integer> platformTypeStats = (Map<String, Integer>) result.get("platformTypeStats");
        assertNotNull(platformTypeStats);
        assertTrue(platformTypeStats.get("taxPlatform") > 0);
        assertTrue(platformTypeStats.get("socialPlatform") > 0);
    }

    // ==================== TESTS COMPLETS ====================

    @Test
    void testRunCompleteTest_Success() {
        Map<String, Object> result = governmentPlatformService.runCompleteTest();
        
        assertNotNull(result);
        assertEquals("OPERATIONAL", result.get("overallStatus"));
        assertNotNull(result.get("testDate"));
        assertTrue((Integer) result.get("testedCountries") > 0);
        
        // Vérifier les résultats des tests
        Map<String, Object> platformTest = (Map<String, Object>) result.get("platformRetrieval");
        assertNotNull(platformTest);
        assertTrue(platformTest.containsKey("SN"));
        assertTrue(platformTest.containsKey("FR"));
        assertTrue(platformTest.containsKey("US"));
        assertTrue(platformTest.containsKey("BF"));
        assertTrue(platformTest.containsKey("CI"));
        
        Map<String, Object> connectionTest = (Map<String, Object>) result.get("connectionTest");
        assertNotNull(connectionTest);
        
        Map<String, Object> stats = (Map<String, Object>) result.get("statistics");
        assertNotNull(stats);
    }

    @Test
    void testRunCompleteTest_WithErrors() {
        // Ce test vérifie que le système gère correctement les erreurs
        // Les erreurs sont simulées dans le service pour certains pays
        Map<String, Object> result = governmentPlatformService.runCompleteTest();
        
        assertNotNull(result);
        assertEquals("OPERATIONAL", result.get("overallStatus"));
        
        // Même avec des erreurs simulées, le test global doit passer
        assertNotNull(result.get("platformRetrieval"));
        assertNotNull(result.get("connectionTest"));
        assertNotNull(result.get("statistics"));
    }

    // ==================== TESTS DE VALIDATION ====================

    @Test
    void testPlatformConfigs_Completeness() {
        // Vérifier que toutes les configurations sont complètes
        String[] testCountries = {"SN", "FR", "US", "BF", "CI"};
        
        for (String country : testCountries) {
            Map<String, Object> config = governmentPlatformService.getAvailablePlatforms(country);
            
            assertNotNull(config);
            assertFalse(config.isEmpty());
            
            // Vérifier que chaque pays a au moins une plateforme fiscale et sociale
            assertTrue(config.containsKey("taxPlatform"));
            assertTrue(config.containsKey("socialPlatform"));
            
            Map<String, Object> taxPlatform = (Map<String, Object>) config.get("taxPlatform");
            assertNotNull(taxPlatform.get("name"));
            assertNotNull(taxPlatform.get("baseUrl"));
            assertNotNull(taxPlatform.get("apiVersion"));
            assertNotNull(taxPlatform.get("supportedDeclarations"));
            
            Map<String, Object> socialPlatform = (Map<String, Object>) config.get("socialPlatform");
            assertNotNull(socialPlatform.get("name"));
            assertNotNull(socialPlatform.get("baseUrl"));
            assertNotNull(socialPlatform.get("apiVersion"));
            assertNotNull(socialPlatform.get("supportedDeclarations"));
        }
    }

    @Test
    void testPlatformConfigs_UniqueUrls() {
        // Vérifier que les URLs sont uniques par plateforme
        Set<String> urls = new HashSet<>();
        
        String[] testCountries = {"SN", "FR", "US", "BF", "CI"};
        for (String country : testCountries) {
            Map<String, Object> config = governmentPlatformService.getAvailablePlatforms(country);
            
            for (Object platformObj : config.values()) {
                Map<String, Object> platform = (Map<String, Object>) platformObj;
                String url = (String) platform.get("baseUrl");
                assertFalse(urls.contains(url), "URL dupliquée trouvée: " + url);
                urls.add(url);
            }
        }
    }

    @Test
    void testPlatformConfigs_SupportedDeclarations() {
        // Vérifier que les déclarations supportées ne sont pas vides
        String[] testCountries = {"SN", "FR", "US", "BF", "CI"};
        
        for (String country : testCountries) {
            Map<String, Object> config = governmentPlatformService.getAvailablePlatforms(country);
            
            for (Object platformObj : config.values()) {
                Map<String, Object> platform = (Map<String, Object>) platformObj;
                List<String> supportedDeclarations = (List<String>) platform.get("supportedDeclarations");
                
                assertNotNull(supportedDeclarations);
                assertFalse(supportedDeclarations.isEmpty());
                
                for (String declaration : supportedDeclarations) {
                    assertNotNull(declaration);
                    assertFalse(declaration.trim().isEmpty());
                }
            }
        }
    }
}


