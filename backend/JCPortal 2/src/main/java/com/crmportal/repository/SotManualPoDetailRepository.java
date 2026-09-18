package com.crmportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.SotManualPoDetailEntity;

@Repository
public interface SotManualPoDetailRepository
        extends JpaRepository<SotManualPoDetailEntity, Long> {

    List<SotManualPoDetailEntity> findBySotManualPoId(Long sotPoId);

    List<SotManualPoDetailEntity> findBySotManualPoIdAndRawMaterialCatId(
            Long sotPoId, Long catId);
}