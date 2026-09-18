package com.crmportal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.StoreOrderingTicketEntity;

@Repository
public interface StoreOrderingTicketRepository
        extends JpaRepository<StoreOrderingTicketEntity, Long> {

	List<StoreOrderingTicketEntity> findByUserIdAndIsDeleteFalseOrderByCreatedAtDesc(Long userId);

    List<StoreOrderingTicketEntity> findByEventIdAndIsDeleteFalse(Long eventId);

    Optional<StoreOrderingTicketEntity> findByEventIdAndIsDeleteFalseAndStatusNot(
            Long eventId, String status);

    @Query("SELECT MAX(s.sotNo) FROM StoreOrderingTicketEntity s " +
           "WHERE s.sotNo LIKE CONCAT(:prefix, '%')")
    String findMaxSotNo(@Param("prefix") String prefix);
    
    boolean existsByEventIdAndUserIdAndIsDeleteFalse(Long eventId, Long userId);

	StoreOrderingTicketEntity findTopByUserIdOrderByIdDesc(Long userId);
}