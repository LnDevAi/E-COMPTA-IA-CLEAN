package com.ecomptaia.repository;

import com.ecomptaia.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<BankAccount> findByCompanyId(Long companyId);

    Optional<BankAccount> findByAccountNumberAndCompanyId(String accountNumber, Long companyId);

    Optional<BankAccount> findByIbanAndCompanyId(String iban, Long companyId);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.currency = :currency ORDER BY ba.createdAt DESC")
    List<BankAccount> findByCurrency(@Param("companyId") Long companyId, @Param("currency") String currency);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.isReconciled = false ORDER BY ba.createdAt DESC")
    List<BankAccount> findUnreconciledAccountsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.isReconciled = true ORDER BY ba.lastReconciliationDate DESC")
    List<BankAccount> findReconciledAccountsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.accountType = :accountType ORDER BY ba.createdAt DESC")
    List<BankAccount> findByAccountType(@Param("companyId") Long companyId, @Param("accountType") String accountType);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.bankName LIKE %:searchTerm% ORDER BY ba.createdAt DESC")
    List<BankAccount> searchByBankName(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.accountNumber LIKE %:searchTerm% ORDER BY ba.createdAt DESC")
    List<BankAccount> searchByAccountNumber(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.lastReconciliationDate <= :date ORDER BY ba.lastReconciliationDate ASC")
    List<BankAccount> findAccountsNeedingReconciliation(@Param("companyId") Long companyId, @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(ba) FROM BankAccount ba WHERE ba.companyId = :companyId")
    Long countActiveAccountsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(ba) FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.isReconciled = false")
    Long countUnreconciledAccountsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(ba) FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.currency = :currency")
    Long countByCurrency(@Param("companyId") Long companyId, @Param("currency") String currency);

    @Query("SELECT DISTINCT ba.currency FROM BankAccount ba WHERE ba.companyId = :companyId")
    List<String> findDistinctCurrenciesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT DISTINCT ba.accountType FROM BankAccount ba WHERE ba.companyId = :companyId")
    List<String> findDistinctAccountTypesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.currentBalance < 0 ORDER BY ba.currentBalance ASC")
    List<BankAccount> findOverdrawnAccounts(@Param("companyId") Long companyId);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.companyId = :companyId AND ba.currentBalance > :minBalance ORDER BY ba.currentBalance DESC")
    List<BankAccount> findAccountsWithMinimumBalance(@Param("companyId") Long companyId, @Param("minBalance") java.math.BigDecimal minBalance);
}
