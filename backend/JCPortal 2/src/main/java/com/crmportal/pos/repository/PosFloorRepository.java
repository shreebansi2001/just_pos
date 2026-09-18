package com.crmportal.pos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.pos.entity.PosFloorEntity;

@Repository
public interface PosFloorRepository extends JpaRepository<PosFloorEntity, Long> {
    List<PosFloorEntity> findByActiveTrueOrderBySortOrderAsc();
    List<PosFloorEntity> findAllByOrderBySortOrderAsc();
}
