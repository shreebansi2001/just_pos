package com.crmportal.pos.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.pos.entity.PosTableEntity;

@Repository
public interface PosTableRepository extends JpaRepository<PosTableEntity, Long> {
    List<PosTableEntity> findByActiveTrue();
    List<PosTableEntity> findByFloorIdAndActiveTrue(Long floorId);
    Optional<PosTableEntity> findByCode(String code);

    // User-wise queries
    List<PosTableEntity> findByUserId(Long userId);
    List<PosTableEntity> findByUserIdAndActiveTrue(Long userId);
    List<PosTableEntity> findByUserIdAndFloorIdAndActiveTrue(Long userId, Long floorId);
}
