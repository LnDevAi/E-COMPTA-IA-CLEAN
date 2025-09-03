package com.ecomptaia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application principale E-COMPTA-IA INTERNATIONAL
 * 
 * @author E-COMPTA-IA Team
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
@EntityScan("com.ecomptaia.entity")
@EnableJpaRepositories("com.ecomptaia.repository")
@EnableAsync
@EnableScheduling
public class EcomptaiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomptaiaApplication.class, args);
        System.out.println("🚀 E-COMPTA-IA INTERNATIONAL démarré avec succès !");
        System.out.println("📊 Plateforme de comptabilité internationale opérationnelle");
        System.out.println("🌍 Support multi-pays et multi-devises activé");
    }
}


