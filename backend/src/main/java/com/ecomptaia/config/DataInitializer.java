package com.ecomptaia.config;

import com.ecomptaia.entity.Country;
import com.ecomptaia.entity.User;
import com.ecomptaia.repository.CountryRepository;
import com.ecomptaia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si les données existent déjà
        if (countryRepository.count() == 0) {
            initializeCountries();
        }

        // Créer un utilisateur admin par défaut s'il n'existe pas
        if (!userRepository.existsByEmail("admin@ecomptaia.com")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@ecomptaia.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setAdmin(true);
            adminUser.setActive(true);
            userRepository.save(adminUser);
            System.out.println("✅ Utilisateur admin créé: admin@ecomptaia.com / admin123");
        } else {
            System.out.println("ℹ️ Utilisateur admin existe déjà");
        }

        // Afficher le nombre d'utilisateurs
        long userCount = userRepository.count();
        System.out.println("📊 Nombre d'utilisateurs en base: " + userCount);
    }

    private void initializeCountries() {
        List<Country> countries = Arrays.asList(
            // Pays OHADA - Zone XOF
            new Country("BJ", "Bénin"),
            new Country("BF", "Burkina Faso"),
            new Country("CI", "Côte d'Ivoire"),
            new Country("GW", "Guinée-Bissau"),
            new Country("ML", "Mali"),
            new Country("NE", "Niger"),
            new Country("SN", "Sénégal"),
            new Country("TG", "Togo"),
            
            // Pays OHADA - Zone XAF
            new Country("CM", "Cameroun"),
            new Country("CF", "République Centrafricaine"),
            new Country("CG", "Congo"),
            new Country("GA", "Gabon"),
            new Country("GQ", "Guinée Équatoriale"),
            new Country("TD", "Tchad"),
            
            // Pays OHADA - Zone CDF
            new Country("CD", "République Démocratique du Congo"),
            
            // Pays Europe
            new Country("FR", "France"),
            new Country("DE", "Allemagne"),
            new Country("GB", "Royaume-Uni"),
            
            // Pays Amérique
            new Country("US", "États-Unis"),
            new Country("CA", "Canada"),
            new Country("BR", "Brésil"),
            
            // Pays Asie
            new Country("IN", "Inde"),
            new Country("CN", "Chine"),
            new Country("JP", "Japon"),
            
            // Pays Océanie
            new Country("AU", "Australie"),
            
            // Pays Afrique
            new Country("ZA", "Afrique du Sud")
        );

        countryRepository.saveAll(countries);
        System.out.println("✅ " + countries.size() + " pays initialisés dans la base de données");
    }
}


