package com.ecomptaia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test-simple")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestSimpleController {

    @GetMapping("/accounting")
    public ResponseEntity<?> testAccounting() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test accounting endpoint OK");
        response.put("status", "SUCCESS");
        response.put("data", "Données de test comptabilité");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/third-parties")
    public ResponseEntity<?> testThirdParties() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test third-parties endpoint OK");
        response.put("status", "SUCCESS");
        response.put("data", "Données de test tiers");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hr")
    public ResponseEntity<?> testHR() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test HR endpoint OK");
        response.put("status", "SUCCESS");
        response.put("data", "Données de test RH");
        return ResponseEntity.ok(response);
    }
}
