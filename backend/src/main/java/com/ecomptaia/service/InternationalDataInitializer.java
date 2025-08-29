package com.ecomptaia.service;

import com.ecomptaia.entity.CountryConfig;
import com.ecomptaia.repository.CountryConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class InternationalDataInitializer implements CommandLineRunner {

    @Autowired
    private CountryConfigRepository countryConfigRepository;

    @Override
    public void run(String... args) throws Exception {
        if (countryConfigRepository.count() == 0) {
            initializeCountryConfigs();
            System.out.println("✅ Configurations pays initialisées avec succès !");
        }
    }

    private void initializeCountryConfigs() {
        // Burkina Faso
        CountryConfig bf = new CountryConfig("BF", "Burkina Faso", "XOF", "SYSCOHADA", "BOTH");
        bf.setBusinessCreationPlatform("Guichet Unique");
        bf.setBusinessCreationWebsite("https://guichet-unique.bf");
        bf.setBusinessCreationApiAvailable(true);
        bf.setBusinessCreationApiUrl("https://api.guichet-unique.bf");
        bf.setTaxAdministrationWebsite("https://dgid.bf");
        bf.setTaxAdministrationApiUrl("https://api.dgid.bf");
        bf.setSocialSecurityWebsite("https://cnss.bf");
        bf.setSocialSecurityApiUrl("https://api.cnss.bf");
        bf.setCentralBankWebsite("https://bceao.int");
        bf.setCentralBankApiUrl("https://api.bceao.int");
        bf.setPaymentSystems("[\"Orange Money\", \"Moov Money\", \"MobiCash\"]");
        bf.setOfficialApis("[\"API Guichet Unique\", \"API DGID\", \"API CNSS\"]");
        bf.setRegulations("{\"businessCreation\": \"Loi 2018-XXX\", \"taxation\": \"Code général des impôts\"}");
        bf.setEconomicIndicators("{\"gdp\": 15000000000, \"population\": 20000000, \"internetPenetration\": 25}");
        bf.setCreatedAt(LocalDateTime.now());
        bf.setStatus("ACTIVE");
        bf.setVersion("1.0");

        // Côte d'Ivoire
        CountryConfig ci = new CountryConfig("CI", "Côte d'Ivoire", "XOF", "SYSCOHADA", "BOTH");
        ci.setBusinessCreationPlatform("Guichet Unique");
        ci.setBusinessCreationWebsite("https://guichet-unique.ci");
        ci.setBusinessCreationApiAvailable(true);
        ci.setBusinessCreationApiUrl("https://api.guichet-unique.ci");
        ci.setTaxAdministrationWebsite("https://dgid.ci");
        ci.setTaxAdministrationApiUrl("https://api.dgid.ci");
        ci.setSocialSecurityWebsite("https://cnss.ci");
        ci.setSocialSecurityApiUrl("https://api.cnss.ci");
        ci.setCentralBankWebsite("https://bceao.int");
        ci.setCentralBankApiUrl("https://api.bceao.int");
        ci.setPaymentSystems("[\"Orange Money\", \"Moov Money\", \"MTN Money\"]");
        ci.setOfficialApis("[\"API Guichet Unique\", \"API DGID\", \"API CNSS\"]");
        ci.setRegulations("{\"businessCreation\": \"Loi 2019-XXX\", \"taxation\": \"Code général des impôts\"}");
        ci.setEconomicIndicators("{\"gdp\": 70000000000, \"population\": 25000000, \"internetPenetration\": 35}");
        ci.setCreatedAt(LocalDateTime.now());
        ci.setStatus("ACTIVE");
        ci.setVersion("1.0");

        // France
        CountryConfig fr = new CountryConfig("FR", "France", "EUR", "PCG", "NORMAL");
        fr.setBusinessCreationPlatform("Infogreffe");
        fr.setBusinessCreationWebsite("https://www.infogreffe.fr");
        fr.setBusinessCreationApiAvailable(true);
        fr.setBusinessCreationApiUrl("https://api.infogreffe.fr");
        fr.setTaxAdministrationWebsite("https://www.impots.gouv.fr");
        fr.setTaxAdministrationApiUrl("https://api.impots.gouv.fr");
        fr.setSocialSecurityWebsite("https://www.urssaf.fr");
        fr.setSocialSecurityApiUrl("https://api.urssaf.fr");
        fr.setCentralBankWebsite("https://www.banque-france.fr");
        fr.setCentralBankApiUrl("https://api.banque-france.fr");
        fr.setPaymentSystems("[\"Carte Bancaire\", \"PayPal\", \"Stripe\"]");
        fr.setOfficialApis("[\"API Infogreffe\", \"API Impôts\", \"API URSSAF\"]");
        fr.setRegulations("{\"businessCreation\": \"Code de commerce\", \"taxation\": \"Code général des impôts\"}");
        fr.setEconomicIndicators("{\"gdp\": 2800000000000, \"population\": 67000000, \"internetPenetration\": 85}");
        fr.setCreatedAt(LocalDateTime.now());
        fr.setStatus("ACTIVE");
        fr.setVersion("1.0");

        // États-Unis
        CountryConfig us = new CountryConfig("US", "United States", "USD", "US_GAAP", "NORMAL");
        us.setBusinessCreationPlatform("Secretary of State");
        us.setBusinessCreationWebsite("https://www.sos.state.tx.us");
        us.setBusinessCreationApiAvailable(true);
        us.setBusinessCreationApiUrl("https://api.sos.state.tx.us");
        us.setTaxAdministrationWebsite("https://www.irs.gov");
        us.setTaxAdministrationApiUrl("https://api.irs.gov");
        us.setSocialSecurityWebsite("https://www.ssa.gov");
        us.setSocialSecurityApiUrl("https://api.ssa.gov");
        us.setCentralBankWebsite("https://www.federalreserve.gov");
        us.setCentralBankApiUrl("https://api.federalreserve.gov");
        us.setPaymentSystems("[\"Credit Cards\", \"PayPal\", \"Stripe\", \"Square\"]");
        us.setOfficialApis("[\"API Secretary of State\", \"API IRS\", \"API SSA\"]");
        us.setRegulations("{\"businessCreation\": \"State Law\", \"taxation\": \"Internal Revenue Code\"}");
        us.setEconomicIndicators("{\"gdp\": 21000000000000, \"population\": 330000000, \"internetPenetration\": 90}");
        us.setCreatedAt(LocalDateTime.now());
        us.setStatus("ACTIVE");
        us.setVersion("1.0");

        List<CountryConfig> configs = Arrays.asList(bf, ci, fr, us);
        countryConfigRepository.saveAll(configs);
    }
}
