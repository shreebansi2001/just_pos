// JournalVoucherRepository.java
package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.crmportal.entity.JournalVoucherEntity;

public interface JournalVoucherRepository
        extends JpaRepository<JournalVoucherEntity, Long> {

    List<JournalVoucherEntity> findByUserIdAndIsDeleteFalseOrderByCreatedAtDesc(Long userId);

    Optional<JournalVoucherEntity> findByIdAndIsDeleteFalse(Long id);

    // For auto-generating voucher number per user
    @Query("SELECT MAX(v.voucherNo) FROM JournalVoucherEntity v " +
           "WHERE v.user.id = :userId " +
           "AND v.isDelete = false")
    String findMaxVoucherNoByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(v) FROM JournalVoucherEntity v " +
           "WHERE v.user.id = :userId " +
           "AND v.isDelete = false")
    Long countByUserId(@Param("userId") Long userId);
}