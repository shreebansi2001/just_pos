package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PosOrderEntity;

@Repository
public interface PosOrderRepository extends JpaRepository<PosOrderEntity, Long> {
    Optional<PosOrderEntity> findByOrderCode(String orderCode);
    List<PosOrderEntity> findByStatus(String status);
    List<PosOrderEntity> findByTableIdAndStatus(Long tableId, String status);

    // User-wise queries
    List<PosOrderEntity> findByUserId(Long userId);
    List<PosOrderEntity> findByUserIdAndStatus(Long userId, String status);
    List<PosOrderEntity> findByUserIdAndTableIdAndStatus(Long userId, Long tableId, String status);
    Optional<PosOrderEntity> findByUserIdAndOrderCode(Long userId, String orderCode);
}
