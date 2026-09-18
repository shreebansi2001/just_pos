package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventLaborHelperEntity;

@Repository
public interface EventLaborHelperRepository extends JpaRepository<EventLaborHelperEntity, Long> {

    @Query("SELECT e FROM EventLaborHelperEntity e WHERE e.event_id = :eventId")
    List<EventLaborHelperEntity> findByEvent_id(@Param("eventId") Long eventId);

    @Query("SELECT e FROM EventLaborHelperEntity e WHERE e.event_id = :eventId AND e.event_function_id = :eventFunctionId AND e.contact.id = :partyId")
    List<EventLaborHelperEntity> findByEvent_idAndEvent_function_idAndContact_Id(
            @Param("eventId") Long eventId, 
            @Param("eventFunctionId") Long eventFunctionId, 
            @Param("partyId") Long partyId);

    @Modifying
    @Query("DELETE FROM EventLaborHelperEntity e WHERE e.event_id = :eventId AND e.event_function_id = :eventFunctionId AND e.contact.id = :partyId")
    void deleteByEventIdAndEventFunctionIdAndPartyId(
            @Param("eventId") Long eventId, 
            @Param("eventFunctionId") Long eventFunctionId, 
            @Param("partyId") Long partyId);

    @Modifying
    @Query("DELETE FROM EventLaborHelperEntity e WHERE e.event_id = :eventId")
    void deleteByEventId(@Param("eventId") Long eventId);
}