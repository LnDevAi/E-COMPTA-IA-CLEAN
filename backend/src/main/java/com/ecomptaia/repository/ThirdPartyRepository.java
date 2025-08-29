package com.ecomptaia.repository;

import com.ecomptaia.entity.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Long> {

    List<ThirdParty> findByCompanyIdAndIsActiveTrue(Long companyId);

    List<ThirdParty> findByCompanyIdAndTypeAndIsActiveTrue(Long companyId, String type);

    List<ThirdParty> findByCompanyIdAndCountryCodeAndIsActiveTrue(Long companyId, String countryCode);

    Optional<ThirdParty> findByCodeAndCompanyIdAndIsActiveTrue(String code, Long companyId);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.type = 'CLIENT' AND tp.isActive = true")
    List<ThirdParty> findClientsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.type = 'FOURNISSEUR' AND tp.isActive = true")
    List<ThirdParty> findSuppliersByCompany(@Param("companyId") Long companyId);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.type = 'BOTH' AND tp.isActive = true")
    List<ThirdParty> findBothTypesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.currentBalance > 0 AND tp.isActive = true ORDER BY tp.currentBalance DESC")
    List<ThirdParty> findDebtorsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.currentBalance < 0 AND tp.isActive = true ORDER BY tp.currentBalance ASC")
    List<ThirdParty> findCreditorsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.name LIKE %:searchTerm% AND tp.isActive = true")
    List<ThirdParty> searchByName(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.code LIKE %:searchTerm% AND tp.isActive = true")
    List<ThirdParty> searchByCode(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.taxIdentificationNumber = :taxId AND tp.isActive = true")
    Optional<ThirdParty> findByTaxId(@Param("companyId") Long companyId, @Param("taxId") String taxId);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.vatNumber = :vatNumber AND tp.isActive = true")
    Optional<ThirdParty> findByVatNumber(@Param("companyId") Long companyId, @Param("vatNumber") String vatNumber);

    @Query("SELECT COUNT(tp) FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.type = 'CLIENT' AND tp.isActive = true")
    Long countClientsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(tp) FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.type = 'FOURNISSEUR' AND tp.isActive = true")
    Long countSuppliersByCompany(@Param("companyId") Long companyId);

    @Query("SELECT DISTINCT tp.category FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.isActive = true")
    List<String> findDistinctCategoriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.category = :category AND tp.isActive = true")
    List<ThirdParty> findByCategory(@Param("companyId") Long companyId, @Param("category") String category);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.accountNumber = :accountNumber AND tp.isActive = true")
    Optional<ThirdParty> findByAccountNumberAndCompanyId(@Param("companyId") Long companyId, @Param("accountNumber") String accountNumber);

    @Query("SELECT tp.accountNumber FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.accountNumber LIKE :baseAccount% AND tp.isActive = true ORDER BY tp.accountNumber DESC")
    List<String> findAccountNumbersByBaseAccount(@Param("companyId") Long companyId, @Param("baseAccount") String baseAccount);

    @Query("SELECT tp.accountNumber FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.accountNumber LIKE :baseAccount% AND tp.isActive = true ORDER BY tp.accountNumber DESC LIMIT 1")
    String findLastAccountNumberByBaseAccount(@Param("companyId") Long companyId, @Param("baseAccount") String baseAccount);

    @Query("SELECT tp FROM ThirdParty tp WHERE tp.companyId = :companyId AND tp.currency = :currency AND tp.isActive = true")
    List<ThirdParty> findByCurrency(@Param("companyId") Long companyId, @Param("currency") String currency);
}

