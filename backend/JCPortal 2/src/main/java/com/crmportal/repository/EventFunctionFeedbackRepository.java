package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionFeedbackEntity;

@Repository
public interface EventFunctionFeedbackRepository
        extends JpaRepository<EventFunctionFeedbackEntity, Long> {

    @Query("SELECT e FROM EventFunctionFeedbackEntity e " +
           "WHERE (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:mobileno IS NULL OR e.mobileno LIKE CONCAT('%', :mobileno, '%')) " +
           "AND (:eventId IS NULL OR e.eventId = :eventId) " +
           "AND (:eventFunctionId IS NULL OR e.eventFunctionId = :eventFunctionId) " +
           "AND (:userId IS NULL OR e.userId = :userId) " +
           "AND (:memberId IS NULL OR e.memberId = :memberId)")
    List<EventFunctionFeedbackEntity> getAllWithFilters(
            @Param("name") String name,
            @Param("mobileno") String mobileno,
            @Param("eventId") Long eventId,
            @Param("eventFunctionId") Long eventFunctionId,
            @Param("userId") Long userId,
            @Param("memberId") Long memberId);
}