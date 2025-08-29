package com.ecomptaia.config;

import com.ecomptaia.entity.Currency;
import com.ecomptaia.entity.ExchangeRate;
import com.ecomptaia.repository.CurrencyRepository;
import com.ecomptaia.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class CurrencyDataInitializer implements CommandLineRunner {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Override
    public void run(String... args) throws Exception {
        if (currencyRepository.count() == 0) {
            initializeCurrencies();
        }
        
        if (exchangeRateRepository.count() == 0) {
            initializeExchangeRates();
        }
    }

    private void initializeCurrencies() {
        List<Currency> currencies = Arrays.asList(
            new Currency("EUR", "Euro", "€", new BigDecimal("1.0000"), "Union Européenne"),
            new Currency("USD", "Dollar US", "$", new BigDecimal("1.0850"), "États-Unis"),
            new Currency("XOF", "Franc CFA", "FCFA", new BigDecimal("655.9570"), "Afrique de l'Ouest"),
            new Currency("XAF", "Franc CFA", "FCFA", new BigDecimal("655.9570"), "Afrique Centrale"),
            new Currency("CDF", "Franc Congolais", "FC", new BigDecimal("2700.0000"), "RDC"),
            new Currency("GBP", "Livre Sterling", "£", new BigDecimal("0.8600"), "Royaume-Uni"),
            new Currency("JPY", "Yen Japonais", "¥", new BigDecimal("160.0000"), "Japon"),
            new Currency("CNY", "Yuan Chinois", "¥", new BigDecimal("7.8000"), "Chine"),
            new Currency("INR", "Roupie Indienne", "₹", new BigDecimal("90.0000"), "Inde"),
            new Currency("BRL", "Real Brésilien", "R$", new BigDecimal("5.4000"), "Brésil"),
            new Currency("CAD", "Dollar Canadien", "C$", new BigDecimal("1.4700"), "Canada"),
            new Currency("AUD", "Dollar Australien", "A$", new BigDecimal("1.6500"), "Australie"),
            new Currency("CHF", "Franc Suisse", "CHF", new BigDecimal("0.9500"), "Suisse"),
            new Currency("SEK", "Couronne Suédoise", "kr", new BigDecimal("11.2000"), "Suède"),
            new Currency("NOK", "Couronne Norvégienne", "kr", new BigDecimal("11.5000"), "Norvège"),
            new Currency("DKK", "Couronne Danoise", "kr", new BigDecimal("7.4500"), "Danemark"),
            new Currency("PLN", "Zloty Polonais", "zł", new BigDecimal("4.3000"), "Pologne"),
            new Currency("CZK", "Couronne Tchèque", "Kč", new BigDecimal("25.0000"), "République Tchèque"),
            new Currency("HUF", "Forint Hongrois", "Ft", new BigDecimal("380.0000"), "Hongrie"),
            new Currency("RON", "Leu Roumain", "lei", new BigDecimal("4.9500"), "Roumanie"),
            new Currency("BGN", "Lev Bulgare", "лв", new BigDecimal("1.9558"), "Bulgarie"),
            new Currency("HRK", "Kuna Croate", "kn", new BigDecimal("7.5345"), "Croatie"),
            new Currency("RUB", "Rouble Russe", "₽", new BigDecimal("100.0000"), "Russie"),
            new Currency("TRY", "Livre Turque", "₺", new BigDecimal("32.0000"), "Turquie"),
            new Currency("ZAR", "Rand Sud-Africain", "R", new BigDecimal("20.0000"), "Afrique du Sud"),
            new Currency("EGP", "Livre Égyptienne", "E£", new BigDecimal("33.0000"), "Égypte"),
            new Currency("NGN", "Naira Nigérian", "₦", new BigDecimal("1600.0000"), "Nigeria"),
            new Currency("KES", "Shilling Kenyan", "KSh", new BigDecimal("160.0000"), "Kenya"),
            new Currency("GHS", "Cedi Ghanéen", "GH₵", new BigDecimal("13.0000"), "Ghana"),
            new Currency("MAD", "Dirham Marocain", "MAD", new BigDecimal("10.8000"), "Maroc"),
            new Currency("TND", "Dinar Tunisien", "TND", new BigDecimal("3.2000"), "Tunisie"),
            new Currency("DZD", "Dinar Algérien", "DZD", new BigDecimal("145.0000"), "Algérie")
        );
        
        currencyRepository.saveAll(currencies);
        System.out.println("✅ " + currencies.size() + " devises initialisées dans la base de données");
    }

    private void initializeExchangeRates() {
        LocalDate today = LocalDate.now();
        
        List<ExchangeRate> exchangeRates = Arrays.asList(
            // EUR vers autres devises
            new ExchangeRate("EUR", "USD", new BigDecimal("1.0850"), today, "Système"),
            new ExchangeRate("EUR", "XOF", new BigDecimal("655.9570"), today, "Système"),
            new ExchangeRate("EUR", "XAF", new BigDecimal("655.9570"), today, "Système"),
            new ExchangeRate("EUR", "CDF", new BigDecimal("2700.0000"), today, "Système"),
            new ExchangeRate("EUR", "GBP", new BigDecimal("0.8600"), today, "Système"),
            new ExchangeRate("EUR", "JPY", new BigDecimal("160.0000"), today, "Système"),
            
            // USD vers autres devises
            new ExchangeRate("USD", "EUR", new BigDecimal("0.9217"), today, "Système"),
            new ExchangeRate("USD", "XOF", new BigDecimal("604.5677"), today, "Système"),
            new ExchangeRate("USD", "XAF", new BigDecimal("604.5677"), today, "Système"),
            new ExchangeRate("USD", "CDF", new BigDecimal("2488.4793"), today, "Système"),
            new ExchangeRate("USD", "GBP", new BigDecimal("0.7926"), today, "Système"),
            new ExchangeRate("USD", "JPY", new BigDecimal("147.4654"), today, "Système"),
            
            // XOF vers autres devises
            new ExchangeRate("XOF", "EUR", new BigDecimal("0.0015"), today, "Système"),
            new ExchangeRate("XOF", "USD", new BigDecimal("0.0017"), today, "Système"),
            new ExchangeRate("XOF", "XAF", new BigDecimal("1.0000"), today, "Système"),
            new ExchangeRate("XOF", "CDF", new BigDecimal("4.1167"), today, "Système"),
            
            // XAF vers autres devises
            new ExchangeRate("XAF", "EUR", new BigDecimal("0.0015"), today, "Système"),
            new ExchangeRate("XAF", "USD", new BigDecimal("0.0017"), today, "Système"),
            new ExchangeRate("XAF", "XOF", new BigDecimal("1.0000"), today, "Système"),
            new ExchangeRate("XAF", "CDF", new BigDecimal("4.1167"), today, "Système"),
            
            // CDF vers autres devises
            new ExchangeRate("CDF", "EUR", new BigDecimal("0.0004"), today, "Système"),
            new ExchangeRate("CDF", "USD", new BigDecimal("0.0004"), today, "Système"),
            new ExchangeRate("CDF", "XOF", new BigDecimal("0.2429"), today, "Système"),
            new ExchangeRate("CDF", "XAF", new BigDecimal("0.2429"), today, "Système")
        );
        
        exchangeRateRepository.saveAll(exchangeRates);
        System.out.println("✅ " + exchangeRates.size() + " taux de change initialisés dans la base de données");
    }
}





