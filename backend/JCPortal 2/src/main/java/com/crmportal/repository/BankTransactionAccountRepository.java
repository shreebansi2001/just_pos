package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.crmportal.entity.AccountEntryEntity;

public interface BankTransactionAccountRepository
        extends JpaRepository<AccountEntryEntity, Long> {

    @Query("SELECT a FROM AccountEntryEntity a " +
           "WHERE a.bankDetails.id = :bankId " +
           "AND a.isDelete = false " +
           "AND (:fromDate IS NULL OR a.date >= :fromDate) " +
           "AND (:toDate   IS NULL OR a.date <= :toDate) " +
           "ORDER BY a.date ASC")
    List<AccountEntryEntity> findByBankAndDateRange(
            @Param("bankId")   Long      bankId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate")   LocalDate toDate);
}