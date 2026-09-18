package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.crmportal.entity.VendorPaymentEntity;

public interface BankTransactionVendorRepository
        extends JpaRepository<VendorPaymentEntity, Long> {

    @Query("SELECT v FROM VendorPaymentEntity v " +
           "WHERE v.bankId = :bankId " +
           "AND v.isDelete = false " +
           "AND (:fromDate IS NULL OR v.paymentDate >= :fromDate) " +
           "AND (:toDate   IS NULL OR v.paymentDate <= :toDate) " +
           "ORDER BY v.paymentDate ASC")
    List<VendorPaymentEntity> findByBankAndDateRange(
            @Param("bankId")   Long      bankId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate")   LocalDate toDate);
}