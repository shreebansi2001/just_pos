package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.ExtraChargesHeadingEntity;

@Repository
public interface ExtraChargesHeadingRepository extends JpaRepository<ExtraChargesHeadingEntity, Long> {

    Optional<ExtraChargesHeadingEntity> findByIdAndIsDeleteFalse(Long id);

    /**
     * Fetch all headings for a specific event + specific function.
     */
    @Query("SELECT h FROM ExtraChargesHeadingEntity h " +
           "WHERE h.event.id = :eventId " +
           "AND h.eventFunction.id = :eventFunctionId " +
           "AND h.isDelete = false " +
           "ORDER BY h.id ASC")
    List<ExtraChargesHeadingEntity> findByEventIdAndEventFunctionId(
            @Param("eventId") Long eventId,
            @Param("eventFunctionId") Long eventFunctionId);

    /**
     * Fetch all headings for an event across ALL functions
     * (eventFunction is null → "All Functions" headings,
     *  or any function → aggregate view when eventFunctionId = -1).
     */
    @Query("SELECT h FROM ExtraChargesHeadingEntity h " +
           "WHERE h.event.id = :eventId " +
           "AND h.isDelete = false " +
           "ORDER BY h.eventFunction.id ASC NULLS FIRST, h.id ASC")
    List<ExtraChargesHeadingEntity> findAllByEventId(@Param("eventId") Long eventId);

    /**
     * Fetch headings where eventFunction IS NULL
     * (records saved with eventFunctionId = -1 → stored as NULL).
     */
    @Query("SELECT h FROM ExtraChargesHeadingEntity h " +
           "WHERE h.event.id = :eventId " +
           "AND h.eventFunction IS NULL " +
           "AND h.isDelete = false " +
           "ORDER BY h.id ASC")
    List<ExtraChargesHeadingEntity> findByEventIdAndAllFunctions(@Param("eventId") Long eventId);

	List<ExtraChargesHeadingEntity> findAllByEventFunctionInAndIsDeleteFalse(
			List<EventFunctionMasterEntity> eventFunctions);
}