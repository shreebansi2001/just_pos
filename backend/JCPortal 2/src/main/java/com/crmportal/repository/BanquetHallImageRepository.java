package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.crmportal.entity.BanquetHallImageEntity;

public interface BanquetHallImageRepository
        extends JpaRepository<BanquetHallImageEntity, Long> {

    List<BanquetHallImageEntity> findByHallIdAndIsDeleteFalse(Long hallId);

    @Modifying
    @Query("UPDATE BanquetHallImageEntity i SET i.isDelete = true WHERE i.hall.id = :hallId")
    void softDeleteAllByHallId(Long hallId);

	Optional<BanquetHallImageEntity> findByIdAndIsDeleteFalse(Long moduleRecordId);
}