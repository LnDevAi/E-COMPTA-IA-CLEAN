package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class SMTDataInitializer implements CommandLineRunner {

    @Autowired
    private EntrepriseSMTRepository entrepriseSMTRepository;

    @Autowired
    private ExerciceSMTRepository exerciceSMTRepository;

    @Autowired
    private CompteTresorerieRepository compteTresorerieRepository;

    @Autowired
    private LivreRecetteRepository livreRecetteRepository;

    @Autowired
    private LivreDepenseRepository livreDepenseRepository;

    @Autowired
    private EtatFinancierRepository etatFinancierRepository;

    @Override
    public void run(String... args) throws Exception {
        if (entrepriseSMTRepository.count() == 0) {
            initializeSMTData();
            System.out.println("✅ Données SMT initialisées avec succès !");
        }
    }

    private void initializeSMTData() {
        // Créer des entreprises SMT de test
        EntrepriseSMT entreprise1 = new EntrepriseSMT("SARL Tech Solutions", "BF123456789", "BF", "PETITE", 50000000.0);
        entreprise1.setAdresse("Ouagadougou, Secteur 15");
        entreprise1.setTelephone("+226 70 12 34 56");
        entreprise1.setEmail("contact@techsolutions.bf");
        entreprise1.setRepresentantLegal("Moussa Ouédraogo");
        entreprise1.setNumeroRegistreCommerce("RC-BF-2024-001");
        entreprise1.setNumeroCNSS("CNSS-BF-001");
        entreprise1.setActivitePrincipale("Services informatiques");
        entreprise1.setDevise("XOF");
        entreprise1.setDateCreation(LocalDateTime.now());
        entreprise1.setStatut("ACTIVE");
        entreprise1.setVersion("1.0");

        EntrepriseSMT entreprise2 = new EntrepriseSMT("EURL Commerce Plus", "CI987654321", "CI", "MICRO", 15000000.0);
        entreprise2.setAdresse("Abidjan, Cocody");
        entreprise2.setTelephone("+225 27 22 33 44");
        entreprise2.setEmail("info@commerceplus.ci");
        entreprise2.setRepresentantLegal("Fatou Koné");
        entreprise2.setNumeroRegistreCommerce("RC-CI-2024-002");
        entreprise2.setNumeroCNSS("CNSS-CI-002");
        entreprise2.setActivitePrincipale("Commerce de détail");
        entreprise2.setDevise("XOF");
        entreprise2.setDateCreation(LocalDateTime.now());
        entreprise2.setStatut("ACTIVE");
        entreprise2.setVersion("1.0");

        List<EntrepriseSMT> entreprises = Arrays.asList(entreprise1, entreprise2);
        entrepriseSMTRepository.saveAll(entreprises);

        // Créer des exercices pour chaque entreprise
        for (EntrepriseSMT entreprise : entreprises) {
            // Exercice 2024
            ExerciceSMT exercice2024 = new ExerciceSMT(entreprise, 2024, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
            exercice2024.setDateCreation(LocalDateTime.now());
            exercice2024.setStatut("OUVERT");
            exercice2024.setDevise(entreprise.getDevise());
            exerciceSMTRepository.save(exercice2024);

            // Exercice 2023 (clôturé)
            ExerciceSMT exercice2023 = new ExerciceSMT(entreprise, 2023, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
            exercice2023.setDateCreation(LocalDateTime.now().minusDays(365));
            exercice2023.setEstCloture(true);
            exercice2023.setDateCloture(LocalDateTime.now().minusDays(1));
            exercice2023.setStatut("CLOTURE");
            exercice2023.setDevise(entreprise.getDevise());
            exerciceSMTRepository.save(exercice2023);

            // Créer des comptes de trésorerie pour chaque entreprise
            CompteTresorerie caisse = new CompteTresorerie(entreprise, "Caisse principale", "CAISSE");
            caisse.setSoldeInitial(BigDecimal.valueOf(500000));
            caisse.setSoldeActuel(BigDecimal.valueOf(750000));
            caisse.setDateCreation(LocalDateTime.now());
            caisse.setStatut("ACTIVE");
            caisse.setDevise(entreprise.getDevise());
            compteTresorerieRepository.save(caisse);

            CompteTresorerie banque = new CompteTresorerie(entreprise, "Compte bancaire BICIA", "BANQUE");
            banque.setNumeroCompte("BF1234567890");
            banque.setNomBanque("BICIA");
            banque.setSoldeInitial(BigDecimal.valueOf(2000000));
            banque.setSoldeActuel(BigDecimal.valueOf(3500000));
            banque.setDateCreation(LocalDateTime.now());
            banque.setStatut("ACTIVE");
            banque.setDevise(entreprise.getDevise());
            compteTresorerieRepository.save(banque);

            CompteTresorerie mobileMoney = new CompteTresorerie(entreprise, "Orange Money", "MOBILE_MONEY");
            mobileMoney.setNumeroCompte("22670123456");
            mobileMoney.setSoldeInitial(BigDecimal.valueOf(100000));
            mobileMoney.setSoldeActuel(BigDecimal.valueOf(250000));
            mobileMoney.setDateCreation(LocalDateTime.now());
            mobileMoney.setStatut("ACTIVE");
            mobileMoney.setDevise(entreprise.getDevise());
            compteTresorerieRepository.save(mobileMoney);

            // Créer des recettes pour l'exercice 2024
            List<LivreRecette> recettes = Arrays.asList(
                new LivreRecette(exercice2024, caisse, LocalDate.of(2024, 1, 15), "Vente services informatiques", "SERVICES", BigDecimal.valueOf(500000)),
                new LivreRecette(exercice2024, banque, LocalDate.of(2024, 2, 10), "Paiement client ABC", "VENTES", BigDecimal.valueOf(750000)),
                new LivreRecette(exercice2024, mobileMoney, LocalDate.of(2024, 3, 5), "Prestation de service", "SERVICES", BigDecimal.valueOf(300000)),
                new LivreRecette(exercice2024, caisse, LocalDate.of(2024, 4, 20), "Vente produits", "VENTES", BigDecimal.valueOf(400000))
            );

            for (LivreRecette recette : recettes) {
                recette.setNumeroPiece("FACT-" + System.currentTimeMillis());
                recette.setTiers("Client " + (recettes.indexOf(recette) + 1));
                recette.setModePaiement(recettes.indexOf(recette) % 2 == 0 ? "ESPECES" : "VIREMENT");
                recette.setDateCreation(LocalDateTime.now());
                recette.setStatut("VALIDE");
                recette.setDevise(entreprise.getDevise());
                livreRecetteRepository.save(recette);
            }

            // Créer des dépenses pour l'exercice 2024
            List<LivreDepense> depenses = Arrays.asList(
                new LivreDepense(exercice2024, caisse, LocalDate.of(2024, 1, 10), "Achat fournitures bureau", "ACHATS", BigDecimal.valueOf(150000)),
                new LivreDepense(exercice2024, banque, LocalDate.of(2024, 2, 15), "Paiement loyer", "FRAIS_GENERAUX", BigDecimal.valueOf(300000)),
                new LivreDepense(exercice2024, caisse, LocalDate.of(2024, 3, 8), "Achat matériel informatique", "ACHATS", BigDecimal.valueOf(500000)),
                new LivreDepense(exercice2024, banque, LocalDate.of(2024, 4, 12), "Paiement salaires", "SALAIRES", BigDecimal.valueOf(800000)),
                new LivreDepense(exercice2024, caisse, LocalDate.of(2024, 5, 20), "Paiement impôts", "IMPOTS", BigDecimal.valueOf(200000))
            );

            for (LivreDepense depense : depenses) {
                depense.setNumeroPiece("FACT-FOURN-" + System.currentTimeMillis());
                depense.setTiers("Fournisseur " + (depenses.indexOf(depense) + 1));
                depense.setModePaiement(depenses.indexOf(depense) % 2 == 0 ? "ESPECES" : "CHEQUE");
                depense.setDateCreation(LocalDateTime.now());
                depense.setStatut("VALIDE");
                depense.setDevise(entreprise.getDevise());
                livreDepenseRepository.save(depense);
            }

            // Créer des états financiers pour l'exercice 2023 (clôturé)
            EtatFinancier bilan2023 = new EtatFinancier(exercice2023, "BILAN", "SYSCOHADA");
            bilan2023.setDateGeneration(LocalDate.of(2023, 12, 31));
            bilan2023.setDateCreation(LocalDateTime.now().minusDays(1));
            bilan2023.setStatut("PUBLISHED");
            bilan2023.setDevise(entreprise.getDevise());
            bilan2023.setTotalActifs(BigDecimal.valueOf(5000000));
            bilan2023.setTotalPassifs(BigDecimal.valueOf(2000000));
            bilan2023.setObservations("Bilan conforme aux normes SYSCOHADA");
            etatFinancierRepository.save(bilan2023);

            EtatFinancier compteResultat2023 = new EtatFinancier(exercice2023, "COMPTE_RESULTAT", "SYSCOHADA");
            compteResultat2023.setDateGeneration(LocalDate.of(2023, 12, 31));
            compteResultat2023.setDateCreation(LocalDateTime.now().minusDays(1));
            compteResultat2023.setStatut("PUBLISHED");
            compteResultat2023.setDevise(entreprise.getDevise());
            compteResultat2023.setTotalProduits(BigDecimal.valueOf(8000000));
            compteResultat2023.setTotalCharges(BigDecimal.valueOf(6000000));
            compteResultat2023.setResultatNet(BigDecimal.valueOf(2000000));
            compteResultat2023.setObservations("Compte de résultat positif");
            etatFinancierRepository.save(compteResultat2023);
        }
    }
}
