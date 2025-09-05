package com.ecomptaia.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // Bean CORS géré par CorsConfig
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(new CorsConfig().corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Endpoints sans contexte /api
                .requestMatchers("/health").permitAll()
                .requestMatchers("/test/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/dashboard/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/accounting/**").permitAll()
                .requestMatchers("/hr/**").permitAll()
                .requestMatchers("/third-parties/**").permitAll()
                .requestMatchers("/assets/**").permitAll()
                .requestMatchers("/documents/**").permitAll()
                .requestMatchers("/ai/**").permitAll()
                .requestMatchers("/international/**").permitAll()
                .requestMatchers("/system/**").permitAll()
                .requestMatchers("/workflow/**").permitAll()
                .requestMatchers("/subscription/**").permitAll()
                .requestMatchers("/government-platform/**").permitAll()
                .requestMatchers("/smt/**").permitAll()
                // Endpoints avec contexte /api
                .requestMatchers("/api/security/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/api/actuator/**").permitAll()
                .requestMatchers("/api/dashboard/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/accounting/**").permitAll()
                .requestMatchers("/api/hr/**").permitAll()
                .requestMatchers("/api/third-parties/**").permitAll()
                .requestMatchers("/api/assets/**").permitAll()
                .requestMatchers("/api/documents/**").permitAll()
                .requestMatchers("/api/ai/**").permitAll()
                .requestMatchers("/api/international/**").permitAll()
                .requestMatchers("/api/system/**").permitAll()
                .requestMatchers("/api/workflow/**").permitAll()
                .requestMatchers("/api/subscription/**").permitAll()
                .requestMatchers("/api/government-platform/**").permitAll()
                .requestMatchers("/api/smt/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .anyRequest().permitAll() // Temporaire pour les tests
            );
        return http.build();
    }
}
