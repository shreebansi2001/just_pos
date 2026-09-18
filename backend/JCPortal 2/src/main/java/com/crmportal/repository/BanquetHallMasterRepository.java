package com.crmportal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.crmportal.entity.BanquetHallMasterEntity;

public interface BanquetHallMasterRepository
        extends JpaRepository<BanquetHallMasterEntity, Long> {

    List<BanquetHallMasterEntity> findByUserIdAndIsDeleteFalse(Long userId);

    Optional<BanquetHallMasterEntity> findByIdAndIsDeleteFalse(Long id);
    
    Optional<BanquetHallMasterEntity> findByHallNameAndIsDeleteFalse(String hallName);
    
    Optional<BanquetHallMasterEntity> findByIdAndUserIdAndIsDeleteFalse(
            Long id,
            Long userId);
    
 // Get only active halls
    List<BanquetHallMasterEntity> findByUserIdAndIsDeleteFalseAndIsActiveTrue(Long userId);
}