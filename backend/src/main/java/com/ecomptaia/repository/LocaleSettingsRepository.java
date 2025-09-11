package com.ecomptaia.repository;

import com.ecomptaia.entity.LocaleSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des paramètres de localisation
 */
@Repository
public interface LocaleSettingsRepository extends JpaRepository<LocaleSettings, Long> {
    
    /**
     * Trouve les paramètres par entreprise
     */
    List<LocaleSettings> findByCompanyId(Long companyId);
    
    /**
     * Trouve les paramètres par défaut d'une entreprise
     */
    Optional<LocaleSettings> findByCompanyIdAndIsDefaultTrue(Long companyId);
    
    /**
     * Trouve les paramètres par langue
     */
    List<LocaleSettings> findByLanguageCode(String languageCode);
    
    /**
     * Trouve les paramètres par pays
     */
    List<LocaleSettings> findByCountryCode(String countryCode);
    
    /**
     * Trouve les paramètres par devise
     */
    List<LocaleSettings> findByCurrencyCode(String currencyCode);
    
    /**
     * Trouve les paramètres par entreprise et langue
     */
    List<LocaleSettings> findByCompanyIdAndLanguageCode(Long companyId, String languageCode);
    
    /**
     * Trouve les paramètres par entreprise et pays
     */
    List<LocaleSettings> findByCompanyIdAndCountryCode(Long companyId, String countryCode);
    
    /**
     * Trouve les paramètres par entreprise et devise
     */
    List<LocaleSettings> findByCompanyIdAndCurrencyCode(Long companyId, String currencyCode);
    
    /**
     * Trouve les paramètres par entreprise, langue et pays
     */
    Optional<LocaleSettings> findByCompanyIdAndLanguageCodeAndCountryCode(Long companyId, String languageCode, String countryCode);
    
    /**
     * Vérifie si une entreprise a des paramètres de localisation
     */
    boolean existsByCompanyId(Long companyId);
    
    /**
     * Vérifie si une entreprise a des paramètres par défaut
     */
    boolean existsByCompanyIdAndIsDefaultTrue(Long companyId);
    
    /**
     * Trouve les paramètres créés après une date
     */
    List<LocaleSettings> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Trouve les paramètres mis à jour après une date
     */
    List<LocaleSettings> findByUpdatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Compte les paramètres par langue
     */
    @Query("SELECT ls.languageCode, COUNT(ls) FROM LocaleSettings ls GROUP BY ls.languageCode")
    List<Object[]> countByLanguage();
    
    /**
     * Compte les paramètres par pays
     */
    @Query("SELECT ls.countryCode, COUNT(ls) FROM LocaleSettings ls GROUP BY ls.countryCode")
    List<Object[]> countByCountry();
    
    /**
     * Compte les paramètres par devise
     */
    @Query("SELECT ls.currencyCode, COUNT(ls) FROM LocaleSettings ls GROUP BY ls.currencyCode")
    List<Object[]> countByCurrency();
    
    /**
     * Trouve les paramètres par fuseau horaire
     */
    List<LocaleSettings> findByTimezone(String timezone);
    
    /**
     * Trouve les paramètres par format de date
     */
    List<LocaleSettings> findByDateFormat(String dateFormat);
    
    /**
     * Trouve les paramètres par format de nombre
     */
    List<LocaleSettings> findByNumberFormat(String numberFormat);
    
    /**
     * Trouve les paramètres par entreprise et fuseau horaire
     */
    List<LocaleSettings> findByCompanyIdAndTimezone(Long companyId, String timezone);
}
