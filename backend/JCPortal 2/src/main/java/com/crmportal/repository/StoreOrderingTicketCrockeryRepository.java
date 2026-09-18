package com.crmportal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.StoreOrderingTicketCrockeryEntity;
import com.crmportal.entity.StoreOrderingTicketEntity;

@Repository
public interface StoreOrderingTicketCrockeryRepository
        extends JpaRepository<StoreOrderingTicketCrockeryEntity, Long> {

	List<StoreOrderingTicketCrockeryEntity> findByUserIdAndIsDeleteFalseOrderByCreatedAtDesc(Long userId);

    List<StoreOrderingTicketCrockeryEntity> findByEventIdAndIsDeleteFalse(Long eventId);

    Optional<StoreOrderingTicketCrockeryEntity> findByEventIdAndIsDeleteFalseAndStatusNot(
            Long eventId, String status);

    @Query("SELECT MAX(s.sotNo) FROM StoreOrderingTicketCrockeryEntity s " +
           "WHERE s.sotNo LIKE CONCAT(:prefix, '%')")
    String findMaxSotNo(@Param("prefix") String prefix);
    
    StoreOrderingTicketCrockeryEntity findTopByUserIdOrderByIdDesc(Long userId);
    
    boolean existsByEventIdAndUserIdAndIsDeleteFalse(Long eventId, Long userId);
}