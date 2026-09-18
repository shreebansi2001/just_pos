package com.crmportal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.StoreOrderingTicketDetailEntity;

@Repository
public interface StoreOrderingTicketDetailRepository
        extends JpaRepository<StoreOrderingTicketDetailEntity, Long> {

    List<StoreOrderingTicketDetailEntity> findBySotId(Long sotId);

    @Modifying
    @Query("DELETE FROM StoreOrderingTicketDetailEntity d WHERE d.sot.id = :sotId")
    void deleteBySotId(Long sotId);

    // Get details by category for screen 3
    List<StoreOrderingTicketDetailEntity> findBySotIdAndRawMaterialCatId(
            Long sotId, Long catId);
    
    @Query("SELECT d FROM StoreOrderingTicketDetailEntity d "
            + "JOIN FETCH d.sot s "
            + "JOIN FETCH s.event e "
            + "LEFT JOIN FETCH d.rawMaterial rm "
            + "LEFT JOIN FETCH d.rawMaterialCat rc "
            + "LEFT JOIN FETCH d.unit u "
            + "LEFT JOIN FETCH d.party p "
            + "WHERE s.id IN (:sotIds) "
            + "AND s.user.id = :userId "
            + "AND s.isDelete = false")
   List<StoreOrderingTicketDetailEntity> findAllBySotIds(
           @Param("sotIds") List<Long> sotIds,
           @Param("userId") Long userId);
}