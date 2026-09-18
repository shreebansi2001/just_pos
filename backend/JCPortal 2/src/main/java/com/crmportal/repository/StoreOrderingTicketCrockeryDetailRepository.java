package com.crmportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.StoreOrderingTicketCrockeryDetailEntity;
import com.crmportal.entity.StoreOrderingTicketDetailEntity;

@Repository
public interface StoreOrderingTicketCrockeryDetailRepository
        extends JpaRepository<StoreOrderingTicketCrockeryDetailEntity, Long> {

    List<StoreOrderingTicketCrockeryDetailEntity> findBySotId(Long sotId);

    @Modifying
    @Query("DELETE FROM StoreOrderingTicketCrockeryDetailEntity d WHERE d.sot.id = :sotId")
    void deleteBySotId(Long sotId);

    // Get details by category for screen 3
    List<StoreOrderingTicketCrockeryDetailEntity> findBySotIdAndRawMaterialCatId(
            Long sotId, Long catId);
}