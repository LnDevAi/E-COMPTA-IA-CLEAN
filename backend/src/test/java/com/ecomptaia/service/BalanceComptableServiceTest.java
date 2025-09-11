package com.ecomptaia.service;

import com.ecomptaia.entity.*;
import com.ecomptaia.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le service BalanceComptableService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BalanceComptableServiceTest {

    @Autowired
    private BalanceComptableService balanceComptableService;

    @Autowired
    private BalanceComptableRepository balanceRepository;

    @Autowired
    private SoldeCompteRepository soldeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private FinancialPeriodRepository periodRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Company testCompany;
    private FinancialPeriod testExercice;
    private Account testAccount;

    @BeforeEach
    public void setUp() {
        // Créer une entreprise de test
        testCompany = new Company();
        testCompany.setName("Entreprise Test");
        testCompany.setCurrency("XOF");
        testCompany = companyRepository.save(testCompany);

        // Créer un exercice de test
        testExercice = new FinancialPeriod();
        testExercice.setCompanyId(testCompany.getId());
        testExercice.setPeriodName("Exercice 2024");
        testExercice.setStartDate(LocalDate.of(2024, 1, 1));
        testExercice.setEndDate(LocalDate.of(2024, 12, 31));
        testExercice.setStatus("OPEN");
        testExercice.setIsCurrent(true);
        testExercice = periodRepository.save(testExercice);

        // Créer un compte de test
        testAccount = new Account();
        testAccount.setAccountNumber("411");
        testAccount.setName("Clients");
        testAccount.setType(Account.AccountType.ASSET);
        testAccount.setAccountClass(Account.AccountClass.CURRENT_ASSETS);
        testAccount.setCompany(testCompany);
        testAccount.setIsActive(true);
        testAccount = accountRepository.save(testAccount);
    }

    @Test
    public void testCreateBalance() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");

        // When
        BalanceComptable savedBalance = balanceComptableService.createBalance(balance);

        // Then
        assertNotNull(savedBalance.getId());
        assertEquals("DRAFT", savedBalance.getStatut());
        assertEquals(testCompany, savedBalance.getCompany());
        assertEquals(testExercice, savedBalance.getExercice());
        assertEquals("SYSCOHADA", savedBalance.getStandardComptable());
    }

    @Test
    public void testGetBalanceById() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balance = balanceRepository.save(balance);

        // When
        Optional<BalanceComptable> foundBalance = balanceComptableService.getBalanceById(balance.getId());

        // Then
        assertTrue(foundBalance.isPresent());
        assertEquals(balance.getId(), foundBalance.get().getId());
        assertEquals("SYSCOHADA", foundBalance.get().getStandardComptable());
    }

    @Test
    public void testGetBalancesByCompany() {
        // Given
        BalanceComptable balance1 = new BalanceComptable();
        balance1.setCompany(testCompany);
        balance1.setExercice(testExercice);
        balance1.setDateBalance(LocalDate.of(2024, 12, 31));
        balance1.setStandardComptable("SYSCOHADA");
        balanceRepository.save(balance1);

        BalanceComptable balance2 = new BalanceComptable();
        balance2.setCompany(testCompany);
        balance2.setExercice(testExercice);
        balance2.setDateBalance(LocalDate.of(2024, 6, 30));
        balance2.setStandardComptable("SYSCOHADA");
        balanceRepository.save(balance2);

        // When
        List<BalanceComptable> balances = balanceComptableService.getBalancesByCompany(testCompany.getId());

        // Then
        assertNotNull(balances);
        assertTrue(balances.size() >= 2);
        // Vérifier que les balances sont triées par date décroissante
        assertTrue(balances.get(0).getDateBalance().isAfter(balances.get(1).getDateBalance()));
    }

    @Test
    public void testGetBalancesByExercice() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balanceRepository.save(balance);

        // When
        List<BalanceComptable> balances = balanceComptableService.getBalancesByExercice(testExercice.getId());

        // Then
        assertNotNull(balances);
        assertTrue(balances.size() >= 1);
        assertEquals(testExercice.getId(), balances.get(0).getExercice().getId());
    }

    @Test
    public void testGenererBalanceAutomatique() {
        // Given
        LocalDate dateBalance = LocalDate.of(2024, 12, 31);
        String standardComptable = "SYSCOHADA";

        // When
        BalanceComptable balance = balanceComptableService.genererBalanceAutomatique(
            testCompany.getId(), testExercice.getId(), dateBalance, standardComptable);

        // Then
        assertNotNull(balance);
        assertNotNull(balance.getId());
        assertEquals("GENERATED", balance.getStatut());
        assertEquals(testCompany, balance.getCompany());
        assertEquals(testExercice, balance.getExercice());
        assertEquals(dateBalance, balance.getDateBalance());
        assertEquals(standardComptable, balance.getStandardComptable());
        assertTrue(balance.getEquilibre()); // La balance doit être équilibrée
    }

    @Test
    public void testValiderBalance() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balance.setStatut("GENERATED");
        balance = balanceRepository.save(balance);

        Long userId = 1L;

        // When
        BalanceComptable validatedBalance = balanceComptableService.validerBalance(balance.getId(), userId);

        // Then
        assertEquals("VALIDATED", validatedBalance.getStatut());
        assertEquals(userId, validatedBalance.getValidatedBy());
        assertNotNull(validatedBalance.getDateValidation());
    }

    @Test
    public void testPublierBalance() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balance.setStatut("VALIDATED");
        balance = balanceRepository.save(balance);

        // When
        BalanceComptable publishedBalance = balanceComptableService.publierBalance(balance.getId());

        // Then
        assertEquals("PUBLISHED", publishedBalance.getStatut());
        assertNotNull(publishedBalance.getDateModification());
    }

    @Test
    public void testGetSoldesByBalance() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balance = balanceRepository.save(balance);

        SoldeCompte solde = new SoldeCompte();
        solde.setBalance(balance);
        solde.setCompte(testAccount);
        solde.setNumeroCompte("411");
        solde.setLibelleCompte("Clients");
        solde.setSoldeFinal(new BigDecimal("100000.00"));
        solde.setSensSolde("DEBIT");
        soldeRepository.save(solde);

        // When
        List<SoldeCompte> soldes = balanceComptableService.getSoldesByBalance(balance.getId());

        // Then
        assertNotNull(soldes);
        assertTrue(soldes.size() >= 1);
        assertEquals("411", soldes.get(0).getNumeroCompte());
        assertEquals("Clients", soldes.get(0).getLibelleCompte());
    }

    @Test
    public void testGetSoldesByClasse() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balance = balanceRepository.save(balance);

        SoldeCompte solde = new SoldeCompte();
        solde.setBalance(balance);
        solde.setCompte(testAccount);
        solde.setNumeroCompte("411");
        solde.setLibelleCompte("Clients");
        solde.setClasseCompte(4);
        solde.setSoldeFinal(new BigDecimal("100000.00"));
        solde.setSensSolde("DEBIT");
        soldeRepository.save(solde);

        // When
        List<SoldeCompte> soldes = balanceComptableService.getSoldesByClasse(balance.getId(), 4);

        // Then
        assertNotNull(soldes);
        assertTrue(soldes.size() >= 1);
        assertEquals(4, soldes.get(0).getClasseCompte());
    }

    @Test
    public void testGetSoldesByNature() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balance = balanceRepository.save(balance);

        SoldeCompte solde = new SoldeCompte();
        solde.setBalance(balance);
        solde.setCompte(testAccount);
        solde.setNumeroCompte("411");
        solde.setLibelleCompte("Clients");
        solde.setNatureCompte("ACTIF");
        solde.setSoldeFinal(new BigDecimal("100000.00"));
        solde.setSensSolde("DEBIT");
        soldeRepository.save(solde);

        // When
        List<SoldeCompte> soldes = balanceComptableService.getSoldesByNature(balance.getId(), "ACTIF");

        // Then
        assertNotNull(soldes);
        assertTrue(soldes.size() >= 1);
        assertEquals("ACTIF", soldes.get(0).getNatureCompte());
    }

    @Test
    public void testGetStatistiquesBalance() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balance.setTotalDebit(new BigDecimal("1000000.00"));
        balance.setTotalCredit(new BigDecimal("1000000.00"));
        balance.setSoldeDebit(new BigDecimal("500000.00"));
        balance.setSoldeCredit(new BigDecimal("500000.00"));
        balance.setNombreComptes(10);
        balance.setNombreMouvements(50);
        balance.setEquilibre(true);
        balance = balanceRepository.save(balance);

        // When
        var stats = balanceComptableService.getStatistiquesBalance(balance.getId());

        // Then
        assertNotNull(stats);
        assertTrue(stats.containsKey("balance"));
        assertTrue(stats.containsKey("totalDebit"));
        assertTrue(stats.containsKey("totalCredit"));
        assertTrue(stats.containsKey("equilibre"));
        assertTrue(stats.containsKey("nombreComptes"));
        assertTrue(stats.containsKey("nombreMouvements"));
        assertTrue(stats.containsKey("statsParClasse"));
        assertTrue(stats.containsKey("statsParNature"));
    }

    @Test
    public void testDeleteBalance() {
        // Given
        BalanceComptable balance = new BalanceComptable();
        balance.setCompany(testCompany);
        balance.setExercice(testExercice);
        balance.setDateBalance(LocalDate.of(2024, 12, 31));
        balance.setStandardComptable("SYSCOHADA");
        balance = balanceRepository.save(balance);

        SoldeCompte solde = new SoldeCompte();
        solde.setBalance(balance);
        solde.setCompte(testAccount);
        solde.setNumeroCompte("411");
        solde.setLibelleCompte("Clients");
        soldeRepository.save(solde);

        Long balanceId = balance.getId();

        // When
        balanceComptableService.deleteBalance(balanceId);

        // Then
        Optional<BalanceComptable> deletedBalance = balanceRepository.findById(balanceId);
        assertFalse(deletedBalance.isPresent());
        
        // Vérifier que les soldes associés sont aussi supprimés
        List<SoldeCompte> soldes = soldeRepository.findByBalanceOrderByNumeroCompte(balance);
        assertTrue(soldes.isEmpty());
    }
}
