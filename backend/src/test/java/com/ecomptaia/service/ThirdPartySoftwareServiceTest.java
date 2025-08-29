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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThirdPartySoftwareServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ThirdPartySoftwareService thirdPartySoftwareService;

    @BeforeEach
    void setUp() {
        // Configuration de base pour les tests
    }

    @Test
    void testGetAvailableSoftware_Success() {
        List<Map<String, Object>> result = thirdPartySoftwareService.getAvailableSoftware();
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() > 0);
        
        // Vérifier qu'au moins un logiciel Sage est présent
        Optional<Map<String, Object>> sageSoftware = result.stream()
                .filter(software -> "SAGE".equals(software.get("code")))
                .findFirst();
        
        assertTrue(sageSoftware.isPresent());
        assertEquals("Sage", sageSoftware.get().get("name"));
        assertEquals("ACCOUNTING", sageSoftware.get().get("type"));
    }

    @Test
    void testTestConnection_Success() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        Map<String, Object> result = thirdPartySoftwareService.testConnection("SAGE", "https://api.sage.com", "test-api-key", "test-secret");
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("CONNECTED", result.get("status"));
    }

    @Test
    void testTestConnection_Failure() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Map<String, Object> result = thirdPartySoftwareService.testConnection("SAGE", "https://api.sage.com", "invalid-key", "invalid-secret");
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("CONNECTION_FAILED", result.get("status"));
    }

    @Test
    void testSyncData_Success() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(new HashMap<>(), HttpStatus.OK));

        Map<String, Object> result = thirdPartySoftwareService.syncData("SAGE", "ACCOUNTS", "BOTH", 1L);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("SYNCED", result.get("status"));
    }

    @Test
    void testSyncData_Failure() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid data type"));

        Map<String, Object> result = thirdPartySoftwareService.syncData("SAGE", "INVALID_TYPE", "BOTH", 1L);
        
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("SYNC_FAILED", result.get("status"));
    }

    @Test
    void testGetSyncLogs_Success() {
        List<Map<String, Object>> result = thirdPartySoftwareService.getSyncLogs("SAGE");
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 5 && result.size() <= 15);
    }

    @Test
    void testConfigureIntegration_Success() {
        Map<String, Object> config = new HashMap<>();
        config.put("apiKey", "new-api-key");
        config.put("apiSecret", "new-api-secret");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(new HashMap<>(), HttpStatus.OK));

        Map<String, Object> result = thirdPartySoftwareService.configureIntegration("SAGE", "REAL_TIME", config, 1L);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("CONFIGURED", result.get("status"));
    }

    @Test
    void testGetIntegrationStatistics_Success() {
        Map<String, Object> result = thirdPartySoftwareService.getIntegrationStatistics();
        
        assertNotNull(result);
        assertTrue((Integer) result.get("totalSoftware") > 0);
        assertNotNull(result.get("lastUpdate"));
        assertNotNull(result.get("softwareTypeStats"));
    }

    @Test
    void testRunCompleteTest_Success() {
        Map<String, Object> result = thirdPartySoftwareService.runCompleteTest();
        
        assertNotNull(result);
        assertEquals("OPERATIONAL", result.get("overallStatus"));
        assertNotNull(result.get("testDate"));
        assertTrue((Integer) result.get("testedSoftware") > 0);
    }

    @Test
    void testRunCompleteTest_Failure() {
        // Test avec une exception simulée
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"));

        Map<String, Object> result = thirdPartySoftwareService.runCompleteTest();
        
        assertNotNull(result);
        assertEquals("ERROR", result.get("overallStatus"));
        assertNotNull(result.get("error"));
    }

    @Test
    void testGetAvailableSoftware_ContainsAllTypes() {
        List<Map<String, Object>> result = thirdPartySoftwareService.getAvailableSoftware();
        
        // Vérifier qu'on a des logiciels de différents types
        Set<String> types = new HashSet<>();
        for (Map<String, Object> software : result) {
            types.add((String) software.get("type"));
        }
        
        assertTrue(types.contains("ACCOUNTING"));
        assertTrue(types.contains("CRM"));
        assertTrue(types.contains("HR"));
        assertTrue(types.contains("ERP"));
        assertTrue(types.contains("PAYMENT"));
        assertTrue(types.contains("INVOICING"));
    }

    @Test
    void testTestConnection_InvalidSoftware() {
        assertThrows(IllegalArgumentException.class, () -> {
            thirdPartySoftwareService.testConnection("INVALID_SOFTWARE", "https://api.invalid.com", "key", "secret");
        });
    }

    @Test
    void testSyncData_InvalidSoftware() {
        assertThrows(IllegalArgumentException.class, () -> {
            thirdPartySoftwareService.syncData("INVALID_SOFTWARE", "ACCOUNTS", "BOTH", 1L);
        });
    }
}
