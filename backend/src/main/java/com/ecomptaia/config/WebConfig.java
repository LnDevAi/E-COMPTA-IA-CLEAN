package com.ecomptaia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration pour les services web
 */
@Configuration
public class WebConfig {

    /**
     * Bean RestTemplate pour les appels API externes
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
