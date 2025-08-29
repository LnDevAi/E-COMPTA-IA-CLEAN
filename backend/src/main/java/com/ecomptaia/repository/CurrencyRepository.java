package com.ecomptaia.repository;

import com.ecomptaia.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    
    Optional<Currency> findByCode(String code);
    
    Boolean existsByCode(String code);
    
    List<Currency> findByIsActiveTrue();
    
    List<Currency> findByCountry(String country);
    
    Optional<Currency> findByCodeAndIsActiveTrue(String code);
}





