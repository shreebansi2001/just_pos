package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.SotManualPoEntity;

@Repository
public interface SotManualPoRepository
        extends JpaRepository<SotManualPoEntity, Long> {

    List<SotManualPoEntity> findBySotIdAndIsDeleteFalse(Long sotId);
    
    List<SotManualPoEntity> findByUserIdAndIsDeleteFalse(Long userId);
    
    @Query("SELECT p FROM SotManualPoEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.isDelete = false " +
		       "ORDER BY p.createdAt DESC")
		List<SotManualPoEntity> getByUserDesc(@Param("userId") Long userId);

    List<SotManualPoEntity> findByEventIdAndIsDeleteFalse(Long eventId);

    @Query("SELECT MAX(s.voucherNo) FROM SotManualPoEntity s " +
           "WHERE s.voucherNo LIKE CONCAT(:prefix, '%')")
    String findMaxVoucherNo(@Param("prefix") String prefix);
    
    Optional<SotManualPoEntity> findByIdAndIsDeleteFalse(Long id);
}