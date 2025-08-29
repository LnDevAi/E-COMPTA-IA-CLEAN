package com.ecomptaia.config;

import com.ecomptaia.entity.Tax;
import com.ecomptaia.repository.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class TaxDataInitializer implements CommandLineRunner {

    @Autowired
    private TaxRepository taxRepository;

    @Override
    public void run(String... args) throws Exception {
        if (taxRepository.count() == 0) {
            initializeTaxes();
        }
    }

    private void initializeTaxes() {
        List<Tax> taxes = Arrays.asList(
            // === SÉNÉGAL (SN) ===
            new Tax("SN", "Sénégal", "VAT", "TVA", new BigDecimal("18.00"), "XOF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("SN", "Sénégal", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("30.00"), "XOF", 
                   null, null, "Impôt sur les bénéfices des entreprises", "annually"),
            new Tax("SN", "Sénégal", "WITHHOLDING", "Retenue à la source", new BigDecimal("20.00"), "XOF", 
                   null, null, "Retenue à la source sur les paiements", "monthly"),
            new Tax("SN", "Sénégal", "SOCIAL_CONTRIBUTION", "Cotisations sociales", new BigDecimal("7.50"), "XOF", 
                   null, null, "Cotisations patronales et salariales", "monthly"),
            
            // === FRANCE (FR) ===
            new Tax("FR", "France", "VAT", "TVA", new BigDecimal("20.00"), "EUR", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("FR", "France", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("25.00"), "EUR", 
                   null, null, "Impôt sur les bénéfices des entreprises", "annually"),
            new Tax("FR", "France", "WITHHOLDING", "Retenue à la source", new BigDecimal("30.00"), "EUR", 
                   null, null, "Retenue à la source sur les dividendes", "annually"),
            new Tax("FR", "France", "SOCIAL_CONTRIBUTION", "Cotisations sociales", new BigDecimal("45.00"), "EUR", 
                   null, null, "Cotisations patronales et salariales", "monthly"),
            
            // === ÉTATS-UNIS (US) ===
            new Tax("US", "États-Unis", "CORPORATE_INCOME", "Corporate Income Tax", new BigDecimal("21.00"), "USD", 
                   null, null, "Federal corporate income tax", "annually"),
            new Tax("US", "États-Unis", "SALES_TAX", "Sales Tax", new BigDecimal("7.25"), "USD", 
                   null, null, "State sales tax (average)", "monthly"),
            new Tax("US", "États-Unis", "WITHHOLDING", "Withholding Tax", new BigDecimal("30.00"), "USD", 
                   null, null, "Federal withholding tax", "monthly"),
            new Tax("US", "États-Unis", "SOCIAL_CONTRIBUTION", "Social Security", new BigDecimal("15.30"), "USD", 
                   null, null, "Social Security and Medicare", "monthly"),
            
            // === ALLEMAGNE (DE) ===
            new Tax("DE", "Allemagne", "VAT", "Mehrwertsteuer", new BigDecimal("19.00"), "EUR", 
                   null, null, "Taxe sur la valeur ajoutée", "monthly"),
            new Tax("DE", "Allemagne", "CORPORATE_INCOME", "Körperschaftsteuer", new BigDecimal("15.00"), "EUR", 
                   null, null, "Impôt sur les sociétés", "annually"),
            new Tax("DE", "Allemagne", "TRADE_TAX", "Gewerbesteuer", new BigDecimal("14.00"), "EUR", 
                   null, null, "Taxe commerciale", "quarterly"),
            new Tax("DE", "Allemagne", "SOCIAL_CONTRIBUTION", "Sozialversicherung", new BigDecimal("40.00"), "EUR", 
                   null, null, "Assurance sociale", "monthly"),
            
            // === CÔTE D'IVOIRE (CI) ===
            new Tax("CI", "Côte d'Ivoire", "VAT", "TVA", new BigDecimal("18.00"), "XOF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("CI", "Côte d'Ivoire", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("25.00"), "XOF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("CI", "Côte d'Ivoire", "WITHHOLDING", "Retenue à la source", new BigDecimal("15.00"), "XOF", 
                   null, null, "Retenue à la source", "monthly"),
            new Tax("CI", "Côte d'Ivoire", "SOCIAL_CONTRIBUTION", "Cotisations sociales", new BigDecimal("8.00"), "XOF", 
                   null, null, "Cotisations patronales", "monthly"),
            
            // === BÉNIN (BJ) ===
            new Tax("BJ", "Bénin", "VAT", "TVA", new BigDecimal("18.00"), "XOF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("BJ", "Bénin", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("30.00"), "XOF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("BJ", "Bénin", "WITHHOLDING", "Retenue à la source", new BigDecimal("20.00"), "XOF", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === BURKINA FASO (BF) ===
            new Tax("BF", "Burkina Faso", "VAT", "TVA", new BigDecimal("18.00"), "XOF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("BF", "Burkina Faso", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("27.50"), "XOF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("BF", "Burkina Faso", "WITHHOLDING", "Retenue à la source", new BigDecimal("20.00"), "XOF", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === MALI (ML) ===
            new Tax("ML", "Mali", "VAT", "TVA", new BigDecimal("18.00"), "XOF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("ML", "Mali", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("30.00"), "XOF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("ML", "Mali", "WITHHOLDING", "Retenue à la source", new BigDecimal("20.00"), "XOF", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === NIGER (NE) ===
            new Tax("NE", "Niger", "VAT", "TVA", new BigDecimal("19.00"), "XOF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("NE", "Niger", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("30.00"), "XOF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("NE", "Niger", "WITHHOLDING", "Retenue à la source", new BigDecimal("20.00"), "XOF", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === TOGO (TG) ===
            new Tax("TG", "Togo", "VAT", "TVA", new BigDecimal("18.00"), "XOF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("TG", "Togo", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("27.00"), "XOF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("TG", "Togo", "WITHHOLDING", "Retenue à la source", new BigDecimal("20.00"), "XOF", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === CAMEROUN (CM) ===
            new Tax("CM", "Cameroun", "VAT", "TVA", new BigDecimal("19.25"), "XAF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("CM", "Cameroun", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("33.00"), "XAF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("CM", "Cameroun", "WITHHOLDING", "Retenue à la source", new BigDecimal("15.00"), "XAF", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === GABON (GA) ===
            new Tax("GA", "Gabon", "VAT", "TVA", new BigDecimal("18.00"), "XAF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("GA", "Gabon", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("30.00"), "XAF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("GA", "Gabon", "WITHHOLDING", "Retenue à la source", new BigDecimal("20.00"), "XAF", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === RDC (CD) ===
            new Tax("CD", "RDC", "VAT", "TVA", new BigDecimal("16.00"), "CDF", 
                   null, null, "Taxe sur la Valeur Ajoutée", "monthly"),
            new Tax("CD", "RDC", "CORPORATE_INCOME", "Impôt sur les Sociétés", new BigDecimal("30.00"), "CDF", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("CD", "RDC", "WITHHOLDING", "Retenue à la source", new BigDecimal("20.00"), "CDF", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === ROYAUME-UNI (GB) ===
            new Tax("GB", "Royaume-Uni", "VAT", "Value Added Tax", new BigDecimal("20.00"), "GBP", 
                   null, null, "Value Added Tax", "quarterly"),
            new Tax("GB", "Royaume-Uni", "CORPORATE_INCOME", "Corporation Tax", new BigDecimal("19.00"), "GBP", 
                   null, null, "Corporation Tax", "annually"),
            new Tax("GB", "Royaume-Uni", "WITHHOLDING", "Withholding Tax", new BigDecimal("20.00"), "GBP", 
                   null, null, "Withholding Tax", "monthly"),
            
            // === JAPON (JP) ===
            new Tax("JP", "Japon", "CONSUMPTION_TAX", "Consumption Tax", new BigDecimal("10.00"), "JPY", 
                   null, null, "Taxe de consommation", "monthly"),
            new Tax("JP", "Japon", "CORPORATE_INCOME", "Corporate Tax", new BigDecimal("23.20"), "JPY", 
                   null, null, "Impôt sur les sociétés", "annually"),
            new Tax("JP", "Japon", "WITHHOLDING", "Withholding Tax", new BigDecimal("20.42"), "JPY", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === CHINE (CN) ===
            new Tax("CN", "Chine", "VAT", "VAT", new BigDecimal("13.00"), "CNY", 
                   null, null, "Taxe sur la valeur ajoutée", "monthly"),
            new Tax("CN", "Chine", "CORPORATE_INCOME", "Corporate Income Tax", new BigDecimal("25.00"), "CNY", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("CN", "Chine", "WITHHOLDING", "Withholding Tax", new BigDecimal("10.00"), "CNY", 
                   null, null, "Retenue à la source", "monthly"),
            
            // === INDE (IN) ===
            new Tax("IN", "Inde", "GST", "Goods and Services Tax", new BigDecimal("18.00"), "INR", 
                   null, null, "Taxe sur les biens et services", "monthly"),
            new Tax("IN", "Inde", "CORPORATE_INCOME", "Corporate Tax", new BigDecimal("30.00"), "INR", 
                   null, null, "Impôt sur les sociétés", "annually"),
            new Tax("IN", "Inde", "WITHHOLDING", "TDS", new BigDecimal("10.00"), "INR", 
                   null, null, "Tax Deducted at Source", "monthly"),
            
            // === BRÉSIL (BR) ===
            new Tax("BR", "Brésil", "ICMS", "ICMS", new BigDecimal("18.00"), "BRL", 
                   null, null, "Taxe sur la circulation des marchandises", "monthly"),
            new Tax("BR", "Brésil", "CORPORATE_INCOME", "Corporate Income Tax", new BigDecimal("34.00"), "BRL", 
                   null, null, "Impôt sur les bénéfices", "annually"),
            new Tax("BR", "Brésil", "WITHHOLDING", "Withholding Tax", new BigDecimal("15.00"), "BRL", 
                   null, null, "Retenue à la source", "monthly")
        );
        
        taxRepository.saveAll(taxes);
        System.out.println("✅ " + taxes.size() + " taxes internationales initialisées dans la base de données");
    }
}





