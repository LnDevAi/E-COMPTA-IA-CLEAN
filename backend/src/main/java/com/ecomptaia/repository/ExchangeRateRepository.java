package com.ecomptaia.repository;

import com.ecomptaia.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    
    @Query("SELECT er FROM ExchangeRate er WHERE er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency AND er.date = :date AND er.isActive = true")
    Optional<ExchangeRate> findByFromCurrencyAndToCurrencyAndDate(@Param("fromCurrency") String fromCurrency, 
                                                                  @Param("toCurrency") String toCurrency, 
                                                                  @Param("date") LocalDate date);
    
    @Query("SELECT er FROM ExchangeRate er WHERE er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency AND er.isActive = true ORDER BY er.date DESC")
    List<ExchangeRate> findByFromCurrencyAndToCurrencyOrderByDateDesc(@Param("fromCurrency") String fromCurrency, 
                                                                      @Param("toCurrency") String toCurrency);
    
    @Query("SELECT er FROM ExchangeRate er WHERE er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency AND er.isActive = true ORDER BY er.date DESC LIMIT 1")
    Optional<ExchangeRate> findLatestByFromCurrencyAndToCurrency(@Param("fromCurrency") String fromCurrency, 
                                                                 @Param("toCurrency") String toCurrency);
    
    List<ExchangeRate> findByDateAndIsActiveTrue(LocalDate date);
    
    List<ExchangeRate> findByFromCurrencyAndIsActiveTrue(String fromCurrency);
    
    List<ExchangeRate> findByToCurrencyAndIsActiveTrue(String toCurrency);
    
    @Query("SELECT DISTINCT er.fromCurrency FROM ExchangeRate er WHERE er.isActive = true")
    List<String> findAllDistinctFromCurrencies();
    
    @Query("SELECT DISTINCT er.toCurrency FROM ExchangeRate er WHERE er.isActive = true")
    List<String> findAllDistinctToCurrencies();
}





