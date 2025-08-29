package com.ecomptaia.service;

import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.repository.AccountEntryRepository;
import com.ecomptaia.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour les tableaux de bord financiers et analyses
 */
@Service
public class FinancialDashboardService {

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    /**
     * Tableau de bord principal avec tous les KPIs
     */
    public Map<String, Object> getMainDashboard(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            // 1. KPIs principaux
            Map<String, Object> kpis = calculateMainKPIs(companyId, startDate, endDate);
            dashboard.put("kpis", kpis);
            
            // 2. Analyses de tendances
            Map<String, Object> trends = analyzeTrends(companyId, startDate, endDate);
            dashboard.put("trends", trends);
            
            // 3. Répartition par classe de compte
            Map<String, Object> accountDistribution = getAccountDistribution(companyId, startDate, endDate);
            dashboard.put("accountDistribution", accountDistribution);
            
            // 4. Activité mensuelle
            Map<String, Object> monthlyActivity = getMonthlyActivity(companyId, startDate, endDate);
            dashboard.put("monthlyActivity", monthlyActivity);
            
            // 5. Alertes et notifications
            List<String> alerts = generateAlerts(companyId, startDate, endDate);
            dashboard.put("alerts", alerts);
            
            dashboard.put("generatedAt", LocalDate.now());
            dashboard.put("period", Map.of("startDate", startDate, "endDate", endDate));
            
        } catch (Exception e) {
            dashboard.put("error", "Erreur lors de la génération du tableau de bord: " + e.getMessage());
        }
        
        return dashboard;
    }

    /**
     * Calcul des KPIs principaux
     */
    public Map<String, Object> calculateMainKPIs(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> kpis = new HashMap<>();
        
        try {
            List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
            List<JournalEntry> journalEntries = journalEntryRepository.findByCompanyIdAndEntryDateBetween(companyId, startDate, endDate);
            
            // 1. Chiffre d'affaires (Classe 7 - Produits)
            BigDecimal revenue = accountEntries.stream()
                .filter(entry -> entry.getAccountNumber().startsWith("7"))
                .map(AccountEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 2. Charges totales (Classe 6 - Charges)
            BigDecimal expenses = accountEntries.stream()
                .filter(entry -> entry.getAccountNumber().startsWith("6"))
                .map(AccountEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 3. Résultat net
            BigDecimal netResult = revenue.subtract(expenses);
            
            // 4. Marge brute (approximative)
            BigDecimal grossMargin = revenue.multiply(new BigDecimal("0.3")); // 30% de marge estimée
            
            // 5. Nombre d'écritures
            int totalEntries = journalEntries.size();
            
            // 6. Nombre de comptes utilisés
            long uniqueAccounts = accountEntries.stream()
                .map(AccountEntry::getAccountNumber)
                .distinct()
                .count();
            
            // 7. Ratio de rentabilité
            BigDecimal profitabilityRatio = revenue.compareTo(BigDecimal.ZERO) > 0 ? 
                netResult.divide(revenue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : 
                BigDecimal.ZERO;
            
            kpis.put("revenue", revenue);
            kpis.put("expenses", expenses);
            kpis.put("netResult", netResult);
            kpis.put("grossMargin", grossMargin);
            kpis.put("totalEntries", totalEntries);
            kpis.put("uniqueAccounts", uniqueAccounts);
            kpis.put("profitabilityRatio", profitabilityRatio);
            
        } catch (Exception e) {
            kpis.put("error", "Erreur lors du calcul des KPIs: " + e.getMessage());
        }
        
        return kpis;
    }

    /**
     * Analyse des tendances
     */
    private Map<String, Object> analyzeTrends(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> trends = new HashMap<>();
        
        try {
            List<JournalEntry> journalEntries = journalEntryRepository.findByCompanyIdAndEntryDateBetween(companyId, startDate, endDate);
            
            // Tendances par mois
            Map<String, BigDecimal> monthlyTrends = journalEntries.stream()
                .collect(Collectors.groupingBy(
                    entry -> entry.getEntryDate().getMonth().toString(),
                    Collectors.mapping(
                        entry -> entry.getTotalDebit() != null ? entry.getTotalDebit() : BigDecimal.ZERO,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
            
            // Calcul de la croissance
            List<BigDecimal> monthlyValues = new ArrayList<>(monthlyTrends.values());
            BigDecimal growthRate = BigDecimal.ZERO;
            
            if (monthlyValues.size() >= 2) {
                BigDecimal currentMonth = monthlyValues.get(monthlyValues.size() - 1);
                BigDecimal previousMonth = monthlyValues.get(monthlyValues.size() - 2);
                
                if (previousMonth.compareTo(BigDecimal.ZERO) > 0) {
                    growthRate = currentMonth.subtract(previousMonth)
                        .divide(previousMonth, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                }
            }
            
            trends.put("monthlyTrends", monthlyTrends);
            trends.put("growthRate", growthRate);
            trends.put("trendDirection", growthRate.compareTo(BigDecimal.ZERO) > 0 ? "POSITIVE" : "NEGATIVE");
            
        } catch (Exception e) {
            trends.put("error", "Erreur lors de l'analyse des tendances: " + e.getMessage());
        }
        
        return trends;
    }

    /**
     * Répartition par classe de compte
     */
    private Map<String, Object> getAccountDistribution(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> distribution = new HashMap<>();
        
        try {
            List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
            
            // Répartition par classe OHADA
            Map<String, BigDecimal> classDistribution = new HashMap<>();
            Map<String, String> classNames = Map.of(
                "1", "Capitaux",
                "2", "Immobilisations",
                "3", "Stocks",
                "4", "Tiers",
                "5", "Financier",
                "6", "Charges",
                "7", "Produits"
            );
            
            for (int i = 1; i <= 7; i++) {
                String classCode = String.valueOf(i);
                BigDecimal total = accountEntries.stream()
                    .filter(entry -> entry.getAccountNumber().startsWith(classCode))
                    .map(AccountEntry::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                classDistribution.put(classNames.get(classCode), total);
            }
            
            distribution.put("byClass", classDistribution);
            distribution.put("totalAmount", classDistribution.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
            
        } catch (Exception e) {
            distribution.put("error", "Erreur lors du calcul de la répartition: " + e.getMessage());
        }
        
        return distribution;
    }

    /**
     * Activité mensuelle
     */
    private Map<String, Object> getMonthlyActivity(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> activity = new HashMap<>();
        
        try {
            List<JournalEntry> journalEntries = journalEntryRepository.findByCompanyIdAndEntryDateBetween(companyId, startDate, endDate);
            
            // Activité par mois
            Map<String, Integer> entriesByMonth = journalEntries.stream()
                .collect(Collectors.groupingBy(
                    entry -> entry.getEntryDate().getMonth().toString(),
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
            
            // Montants par mois
            Map<String, BigDecimal> amountsByMonth = journalEntries.stream()
                .collect(Collectors.groupingBy(
                    entry -> entry.getEntryDate().getMonth().toString(),
                    Collectors.mapping(
                        entry -> entry.getTotalDebit() != null ? entry.getTotalDebit() : BigDecimal.ZERO,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
            
            activity.put("entriesByMonth", entriesByMonth);
            activity.put("amountsByMonth", amountsByMonth);
            activity.put("averageEntriesPerMonth", journalEntries.size() / Math.max(1, (int) ChronoUnit.MONTHS.between(startDate, endDate)));
            
        } catch (Exception e) {
            activity.put("error", "Erreur lors du calcul de l'activité: " + e.getMessage());
        }
        
        return activity;
    }

    /**
     * Génération d'alertes
     */
    private List<String> generateAlerts(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<String> alerts = new ArrayList<>();
        
        try {
            List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
            List<JournalEntry> journalEntries = journalEntryRepository.findByCompanyIdAndEntryDateBetween(companyId, startDate, endDate);
            
            // Alerte si pas d'écritures récentes
            if (journalEntries.isEmpty()) {
                alerts.add("⚠️ Aucune écriture comptable pour la période sélectionnée");
            }
            
            // Alerte si montants élevés
            BigDecimal maxAmount = accountEntries.stream()
                .map(AccountEntry::getAmount)
                .filter(Objects::nonNull)
                .map(BigDecimal::abs)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            if (maxAmount.compareTo(new BigDecimal("1000000")) > 0) {
                alerts.add("💰 Montant élevé détecté: " + maxAmount + " - Vérification recommandée");
            }
            
            // Alerte si déséquilibre
            BigDecimal totalDebit = journalEntries.stream()
                .map(JournalEntry::getTotalDebit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalCredit = journalEntries.stream()
                .map(JournalEntry::getTotalCredit)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (totalDebit.compareTo(totalCredit) != 0) {
                alerts.add("⚖️ Déséquilibre débit/crédit détecté - Réconciliation nécessaire");
            }
            
            // Alerte si peu d'activité
            if (journalEntries.size() < 10) {
                alerts.add("📉 Activité comptable faible - Vérification des saisies");
            }
            
        } catch (Exception e) {
            alerts.add("❌ Erreur lors de la génération des alertes: " + e.getMessage());
        }
        
        return alerts;
    }

    /**
     * KPI spécifique : Rentabilité
     */
    public Map<String, Object> getProfitabilityKPI(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> profitability = new HashMap<>();
        
        try {
            List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
            
            BigDecimal revenue = accountEntries.stream()
                .filter(entry -> entry.getAccountNumber().startsWith("7"))
                .map(AccountEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal expenses = accountEntries.stream()
                .filter(entry -> entry.getAccountNumber().startsWith("6"))
                .map(AccountEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal netResult = revenue.subtract(expenses);
            BigDecimal margin = revenue.compareTo(BigDecimal.ZERO) > 0 ? 
                netResult.divide(revenue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : 
                BigDecimal.ZERO;
            
            profitability.put("revenue", revenue);
            profitability.put("expenses", expenses);
            profitability.put("netResult", netResult);
            profitability.put("margin", margin);
            profitability.put("isProfitable", netResult.compareTo(BigDecimal.ZERO) > 0);
            
        } catch (Exception e) {
            profitability.put("error", "Erreur lors du calcul de la rentabilité: " + e.getMessage());
        }
        
        return profitability;
    }

    /**
     * KPI spécifique : Liquidité
     */
    public Map<String, Object> getLiquidityKPI(Long companyId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> liquidity = new HashMap<>();
        
        try {
            List<AccountEntry> accountEntries = accountEntryRepository.findByCompanyId(companyId);
            
            // Actifs circulants (classes 3, 4, 5)
            BigDecimal currentAssets = accountEntries.stream()
                .filter(entry -> {
                    String accountClass = entry.getAccountNumber().substring(0, 1);
                    return "345".contains(accountClass);
                })
                .map(AccountEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Passifs circulants (classe 4 créditeurs)
            BigDecimal currentLiabilities = accountEntries.stream()
                .filter(entry -> entry.getAccountNumber().startsWith("4") && entry.getAccountType().equals("CREDIT"))
                .map(AccountEntry::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal liquidityRatio = currentLiabilities.compareTo(BigDecimal.ZERO) > 0 ? 
                currentAssets.divide(currentLiabilities, 4, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
            
            liquidity.put("currentAssets", currentAssets);
            liquidity.put("currentLiabilities", currentLiabilities);
            liquidity.put("liquidityRatio", liquidityRatio);
            liquidity.put("isLiquid", liquidityRatio.compareTo(new BigDecimal("1.5")) > 0);
            
        } catch (Exception e) {
            liquidity.put("error", "Erreur lors du calcul de la liquidité: " + e.getMessage());
        }
        
        return liquidity;
    }
}
