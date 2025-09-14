package com.ecomptaia.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Test security configuration: disables JWT and custom filters for test profile.
 */
@TestConfiguration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests((authz) -> authz.anyRequest().permitAll())
            .addFilterBefore(new NoOpFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * No-op filter to replace JwtAuthenticationFilter and SecurityFilter in tests.
     */
    public static class NoOpFilter extends UsernamePasswordAuthenticationFilter {
        // No-op: does nothing, allows all requests
    }
}
