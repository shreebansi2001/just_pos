// BanquetShiftMasterRepository.java
package com.crmportal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.crmportal.entity.BanquetShiftMasterEntity;

public interface BanquetShiftMasterRepository
        extends JpaRepository<BanquetShiftMasterEntity, Long> {

    List<BanquetShiftMasterEntity> findByUserIdAndIsDeleteFalse(Long userId);

    List<BanquetShiftMasterEntity> findByUserIdAndIsDeleteFalseAndIsActiveTrue(Long userId);

    Optional<BanquetShiftMasterEntity> findByIdAndIsDeleteFalse(Long id);
}