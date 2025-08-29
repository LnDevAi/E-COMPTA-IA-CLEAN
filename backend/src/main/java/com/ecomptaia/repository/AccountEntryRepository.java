package com.ecomptaia.repository;

import com.ecomptaia.entity.AccountEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountEntryRepository extends JpaRepository<AccountEntry, Long> {

    List<AccountEntry> findByJournalEntryId(Long journalEntryId);

    List<AccountEntry> findByCompanyId(Long companyId);

    List<AccountEntry> findByAccountNumberAndCompanyId(String accountNumber, Long companyId);

    @Query("SELECT ae FROM AccountEntry ae WHERE ae.companyId = :companyId AND ae.accountType = 'DEBIT'")
    List<AccountEntry> findDebitEntriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT ae FROM AccountEntry ae WHERE ae.companyId = :companyId AND ae.accountType = 'CREDIT'")
    List<AccountEntry> findCreditEntriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT ae FROM AccountEntry ae WHERE ae.companyId = :companyId AND ae.thirdPartyCode = :thirdPartyCode")
    List<AccountEntry> findByThirdPartyCode(@Param("companyId") Long companyId, @Param("thirdPartyCode") String thirdPartyCode);

    @Query("SELECT ae FROM AccountEntry ae WHERE ae.companyId = :companyId AND ae.isReconciled = false")
    List<AccountEntry> findUnreconciledEntriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT ae FROM AccountEntry ae WHERE ae.companyId = :companyId AND ae.isReconciled = true")
    List<AccountEntry> findReconciledEntriesByCompany(@Param("companyId") Long companyId);
}
