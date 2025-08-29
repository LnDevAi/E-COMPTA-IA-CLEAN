package com.ecomptaia.repository;

import com.ecomptaia.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    
    List<Tax> findByCountryCodeAndIsActiveTrue(String countryCode);
    
    List<Tax> findByCountryCodeAndTaxTypeAndIsActiveTrue(String countryCode, String taxType);
    
    List<Tax> findByTaxTypeAndIsActiveTrue(String taxType);
    
    @Query("SELECT DISTINCT t.countryCode FROM Tax t WHERE t.isActive = true")
    List<String> findAllDistinctCountryCodes();
    
    @Query("SELECT DISTINCT t.taxType FROM Tax t WHERE t.isActive = true")
    List<String> findAllDistinctTaxTypes();
    
    @Query("SELECT DISTINCT t.taxType FROM Tax t WHERE t.countryCode = :countryCode AND t.isActive = true")
    List<String> findDistinctTaxTypesByCountry(@Param("countryCode") String countryCode);
    
    Optional<Tax> findByCountryCodeAndTaxTypeAndTaxNameAndIsActiveTrue(String countryCode, String taxType, String taxName);
    
    List<Tax> findByCountryCodeAndIsMandatoryTrueAndIsActiveTrue(String countryCode);
    
    @Query("SELECT t FROM Tax t WHERE t.countryCode = :countryCode AND t.rate >= :minRate AND t.rate <= :maxRate AND t.isActive = true")
    List<Tax> findByCountryCodeAndRateRange(@Param("countryCode") String countryCode, 
                                           @Param("minRate") Double minRate, 
                                           @Param("maxRate") Double maxRate);
    
    @Query("SELECT t FROM Tax t WHERE t.countryCode = :countryCode AND t.currency = :currency AND t.isActive = true")
    List<Tax> findByCountryCodeAndCurrency(@Param("countryCode") String countryCode, 
                                          @Param("currency") String currency);
    
    @Query("SELECT t FROM Tax t WHERE t.taxType = :taxType AND t.rate >= :minRate AND t.isActive = true ORDER BY t.rate DESC")
    List<Tax> findTopTaxesByTypeAndMinRate(@Param("taxType") String taxType, 
                                          @Param("minRate") Double minRate);
}





