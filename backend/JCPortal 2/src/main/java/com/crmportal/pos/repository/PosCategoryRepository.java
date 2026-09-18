package com.crmportal.pos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.pos.entity.PosCategoryEntity;

@Repository
public interface PosCategoryRepository extends JpaRepository<PosCategoryEntity, Long> {
    List<PosCategoryEntity> findByActiveTrueOrderBySortOrderAsc();
    List<PosCategoryEntity> findAllByOrderBySortOrderAsc();
}
