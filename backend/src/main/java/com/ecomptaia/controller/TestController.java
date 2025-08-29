package com.ecomptaia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<?> testPublic() {
        return ResponseEntity.ok(Map.of(
            "message", "✅ Endpoint public accessible sans authentification",
            "timestamp", new java.util.Date(),
            "status", "SUCCESS"
        ));
    }

    @GetMapping("/protected")
    public ResponseEntity<?> testProtected() {
        return ResponseEntity.ok(Map.of(
            "message", "✅ Endpoint protégé accessible avec authentification JWT",
            "timestamp", new java.util.Date(),
            "status", "SUCCESS"
        ));
    }
}
