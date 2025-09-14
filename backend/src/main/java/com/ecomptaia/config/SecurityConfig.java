package com.ecomptaia.config;
import com.ecomptaia.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private com.ecomptaia.security.JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // Bean CORS géré par CorsConfig
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Réactiver CSRF pour la production
            .cors(cors -> cors.configurationSource(new CorsConfig().corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics (sans authentification)
                .requestMatchers("/health").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Endpoints d'authentification
                .requestMatchers("/api/auth/**").permitAll()
                
                // Endpoints sensibles nécessitant une authentification
                .requestMatchers("/api/accounting/**").authenticated()
                .requestMatchers("/api/hr/**").authenticated()
                .requestMatchers("/api/third-parties/**").authenticated()
                .requestMatchers("/api/assets/**").authenticated()
                .requestMatchers("/api/documents/**").authenticated()
                .requestMatchers("/api/ai/**").authenticated()
                .requestMatchers("/api/international/**").authenticated()
                .requestMatchers("/api/system/**").hasRole("ADMIN")
                .requestMatchers("/api/workflow/**").authenticated()
                .requestMatchers("/api/subscription/**").authenticated()
                .requestMatchers("/api/government-platform/**").authenticated()
                .requestMatchers("/api/smt/**").authenticated()
                .requestMatchers("/api/dashboard/**").authenticated()
                
                // Endpoints d'administration
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
