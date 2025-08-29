package com.ecomptaia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Autoriser les origines du frontend
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost",
            "http://localhost:80",
            "http://localhost:4200",
            "http://frontend",
            "http://frontend:80",
            "http://127.0.0.1:4200"
        ));
        
        // Autoriser toutes les méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Autoriser tous les headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permettre les credentials
        configuration.setAllowCredentials(true);
        
        // Exposer les headers de réponse
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Durée de cache pour les requêtes preflight
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
