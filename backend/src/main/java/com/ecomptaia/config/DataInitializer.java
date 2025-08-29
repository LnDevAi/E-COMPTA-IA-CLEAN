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
            User adminUser = new User(
                "Admin",
                "User",
                "admin@ecomptaia.com",
                passwordEncoder.encode("admin123")
            );
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
            new Country("Bénin", "BJ", "XOF"),
            new Country("Burkina Faso", "BF", "XOF"),
            new Country("Côte d'Ivoire", "CI", "XOF"),
            new Country("Guinée-Bissau", "GW", "XOF"),
            new Country("Mali", "ML", "XOF"),
            new Country("Niger", "NE", "XOF"),
            new Country("Sénégal", "SN", "XOF"),
            new Country("Togo", "TG", "XOF"),
            
            // Pays OHADA - Zone XAF
            new Country("Cameroun", "CM", "XAF"),
            new Country("République Centrafricaine", "CF", "XAF"),
            new Country("Congo", "CG", "XAF"),
            new Country("Gabon", "GA", "XAF"),
            new Country("Guinée Équatoriale", "GQ", "XAF"),
            new Country("Tchad", "TD", "XAF"),
            
            // Pays OHADA - Zone CDF
            new Country("République Démocratique du Congo", "CD", "CDF"),
            
            // Pays Europe
            new Country("France", "FR", "EUR"),
            new Country("Allemagne", "DE", "EUR"),
            new Country("Royaume-Uni", "GB", "GBP"),
            
            // Pays Amérique
            new Country("États-Unis", "US", "USD"),
            new Country("Canada", "CA", "CAD"),
            new Country("Brésil", "BR", "BRL"),
            
            // Pays Asie
            new Country("Inde", "IN", "INR"),
            new Country("Chine", "CN", "CNY"),
            new Country("Japon", "JP", "JPY"),
            
            // Pays Océanie
            new Country("Australie", "AU", "AUD"),
            
            // Pays Afrique
            new Country("Afrique du Sud", "ZA", "ZAR")
        );

        countryRepository.saveAll(countries);
        System.out.println("✅ " + countries.size() + " pays initialisés dans la base de données");
    }
}


