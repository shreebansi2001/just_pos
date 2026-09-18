package com.crmportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.crmportal.entity.JournalVoucherDetailEntity;

public interface JournalVoucherDetailRepository
        extends JpaRepository<JournalVoucherDetailEntity, Long> {

    List<JournalVoucherDetailEntity> findByVoucherIdAndIsDeleteFalse(Long voucherId);

    @Modifying
    @Query("UPDATE JournalVoucherDetailEntity d SET d.isDelete = true " +
           "WHERE d.voucher.id = :voucherId")
    void softDeleteByVoucherId(@Param("voucherId") Long voucherId);
}