package com.crmportal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventDishCostingEntity;
import com.crmportal.entity.EventLaborEntity;

@Repository
public interface EventDishCostingRepository extends JpaRepository<EventDishCostingEntity, Long>{

	@Modifying
    @Transactional
    @Query("DELETE FROM EventDishCostingEntity erf WHERE erf.event.id = :eventId AND erf.eventFunction.id = :eventFunctionId")
    void deleteByEventIdAndEventFunctionId(Long eventId, Long eventFunctionId);
	
	@Modifying
    @Transactional
    @Query("DELETE FROM EventDishCostingEntity erf WHERE erf.event.id = :eventId")
    void deleteByEventId(Long eventId);
	
	List<EventDishCostingEntity> findByEvent_IdAndEventFunction_Id(Long eventId, Long eventFunctionId);
	
	List<EventDishCostingEntity> findByEvent_IdAndEventFunctionIsNull(Long eventId);
	
}
