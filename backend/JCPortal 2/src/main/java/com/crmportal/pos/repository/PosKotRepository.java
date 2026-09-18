package com.crmportal.pos.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.pos.entity.PosKotEntity;

@Repository
public interface PosKotRepository extends JpaRepository<PosKotEntity, Long> {
    Optional<PosKotEntity> findByKotCode(String kotCode);
    List<PosKotEntity> findByOrderId(Long orderId);
    List<PosKotEntity> findByStatusNot(String status);
}
