package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.crmportal.entity.StoreManageDetailEntity;

public interface StoreManageDetailRepository
        extends JpaRepository<StoreManageDetailEntity, Long> {

    List<StoreManageDetailEntity> findByStoreManageId(Long storeManageId);

    @Modifying
    @Query("DELETE FROM StoreManageDetailEntity d " +
           "WHERE d.storeManage.id = :storeManageId")
    void deleteByStoreManageId(@Param("storeManageId") Long storeManageId);

    @Query(value = "SELECT smd.remarks " +
            "FROM store_manage_detail smd " +
            "INNER JOIN store_manage sm ON sm.store_manage_id = smd.store_manage_id " +
            "WHERE smd.raw_material_id = :rmId " +
            "AND sm.manage_date BETWEEN :from AND :to " +
            "ORDER BY sm.manage_date DESC, smd.store_manage_detail_id DESC " +
            "LIMIT 1",
    nativeQuery = true)
String getRemarks(@Param("rmId") Long rmId,
               @Param("from") LocalDate from,
               @Param("to") LocalDate to);
}