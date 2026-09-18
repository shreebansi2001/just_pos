package com.crmportal.pos.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.pos.entity.PosOrderEntity;

@Repository
public interface PosOrderRepository extends JpaRepository<PosOrderEntity, Long> {
    Optional<PosOrderEntity> findByOrderCode(String orderCode);
    List<PosOrderEntity> findByStatus(String status);
    List<PosOrderEntity> findByTableIdAndStatus(Long tableId, String status);
}
