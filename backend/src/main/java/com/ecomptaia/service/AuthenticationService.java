package com.ecomptaia.service;

import com.ecomptaia.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service d'authentification pour E-COMPTA-IA INTERNATIONAL
 * Adapté à l'architecture Map existante
 */
@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Authentification d'un utilisateur
     */
    public Map<String, Object> authenticateUser(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Vérifier si l'utilisateur existe
            List<Map<String, Object>> users = userManagementService.getCompanyUsers(1L);
            Map<String, Object> user = users.stream()
                    .filter(u -> username.equals(u.get("username")) || username.equals(u.get("email")))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                result.put("error", "Utilisateur non trouvé");
                result.put("status", "ERROR");
                return result;
            }

            // Pour la démo, on accepte n'importe quel mot de passe
            // En production, il faudrait vérifier le hash du mot de passe
            if (password == null || password.trim().isEmpty()) {
                result.put("error", "Mot de passe requis");
                result.put("status", "ERROR");
                return result;
            }

            // Créer l'authentification
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, password
            );

            // Authentifier
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Générer le token JWT
            String jwt = tokenProvider.generateToken(authentication);

            // Préparer la réponse
            result.put("token", jwt);
            result.put("type", "Bearer");
            result.put("user", user);
            result.put("message", "Connexion réussie");
            result.put("status", "SUCCESS");

        } catch (Exception e) {
            result.put("error", "Erreur d'authentification: " + e.getMessage());
            result.put("status", "ERROR");
        }

        return result;
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    public Map<String, Object> registerUser(String username, String email, String firstName, 
                                          String lastName, String role, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Vérifier si l'utilisateur existe déjà
            List<Map<String, Object>> users = userManagementService.getCompanyUsers(1L);
            boolean userExists = users.stream()
                    .anyMatch(u -> username.equals(u.get("username")) || email.equals(u.get("email")));

            if (userExists) {
                result.put("error", "Un utilisateur avec ce nom ou email existe déjà");
                result.put("status", "ERROR");
                return result;
            }

            // Créer l'utilisateur via le service existant
            Map<String, Object> newUser = userManagementService.createUser(
                1L, username, email, firstName, lastName, role
            );

            if ("SUCCESS".equals(newUser.get("status"))) {
                result.put("user", newUser.get("user"));
                result.put("message", "Utilisateur enregistré avec succès");
                result.put("status", "SUCCESS");
            } else {
                result.put("error", newUser.get("error"));
                result.put("status", "ERROR");
            }

        } catch (Exception e) {
            result.put("error", "Erreur lors de l'enregistrement: " + e.getMessage());
            result.put("status", "ERROR");
        }

        return result;
    }

    /**
     * Déconnexion d'un utilisateur
     */
    public Map<String, Object> logoutUser() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            SecurityContextHolder.clearContext();
            result.put("message", "Déconnexion réussie");
            result.put("status", "SUCCESS");
        } catch (Exception e) {
            result.put("error", "Erreur lors de la déconnexion: " + e.getMessage());
            result.put("status", "ERROR");
        }

        return result;
    }
}
