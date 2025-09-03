package com.ecomptaia.controller;

import com.ecomptaia.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur d'authentification pour E-COMPTA-IA INTERNATIONAL
 * Adapté à l'architecture Map existante
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Endpoint de connexion
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = authenticationService.authenticateUser(
            loginRequest.getUsername(), 
            loginRequest.getPassword()
        );

        if ("SUCCESS".equals(result.get("status"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Endpoint d'inscription
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        Map<String, Object> result = authenticationService.registerUser(
            registerRequest.getUsername(),
            registerRequest.getEmail(),
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getRole(),
            registerRequest.getPassword()
        );

        if ("SUCCESS".equals(result.get("status"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Endpoint de déconnexion
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, Object> result = authenticationService.logoutUser();
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test d'authentification
     */
    @GetMapping("/test")
    public ResponseEntity<?> testAuth() {
        Map<String, Object> response = Map.of(
            "message", "✅ Endpoint d'authentification accessible",
            "timestamp", new java.util.Date(),
            "status", "SUCCESS"
        );
        return ResponseEntity.ok(response);
    }

    // Classes internes pour les requêtes
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters et setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String role = "USER";
        private String password;

        // Getters et setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
