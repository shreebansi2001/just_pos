package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.crmportal.entity.StoreManageEntity;

public interface StoreManageRepository
        extends JpaRepository<StoreManageEntity, Long> {

    @Query("SELECT s FROM StoreManageEntity s " +
           "WHERE s.user.id = :userId " +
           "AND s.isDelete = false " +
           "ORDER BY s.createdAt DESC")
    List<StoreManageEntity> findByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId);

    @Query("SELECT s FROM StoreManageEntity s " +
           "WHERE s.user.id = :userId " +
           "AND s.manageDate = :date " +
           "AND s.isDelete = false")
    Optional<StoreManageEntity> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date")   LocalDate date);

    @Query("SELECT COUNT(s) FROM StoreManageEntity s " +
           "WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    Optional<StoreManageEntity> findByUser_IdAndManageDateAndStockType_Id(
            Long userId,
            LocalDate manageDate,
            Long stockTypeId
    );

    Optional<StoreManageEntity> findByUser_IdAndManageDateAndStockTypeIsNull(
            Long userId,
            LocalDate manageDate
    );
}