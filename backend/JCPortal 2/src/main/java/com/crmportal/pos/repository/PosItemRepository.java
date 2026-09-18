package com.crmportal.pos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crmportal.pos.entity.PosItemEntity;

@Repository
public interface PosItemRepository extends JpaRepository<PosItemEntity, Long> {
    List<PosItemEntity> findByActiveTrue();
    List<PosItemEntity> findByCategoryIdAndActiveTrue(Long categoryId);
}
