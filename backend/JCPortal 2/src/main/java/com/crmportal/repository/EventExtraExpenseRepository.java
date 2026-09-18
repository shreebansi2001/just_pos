package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventExtraExpenseEntity;

@Repository
public interface EventExtraExpenseRepository extends JpaRepository<EventExtraExpenseEntity, Long>{

	Optional<EventExtraExpenseEntity> findById(Long id);

	boolean existsById(Long id);

	
	@Modifying
    @Transactional
    @Query("DELETE FROM EventExtraExpenseEntity erf WHERE erf.id = :id")
    void deleteById(Long id);
	
	List<EventExtraExpenseEntity> findByEvent_IdAndEventFunction_Id(Long eventId, Long eventFunctionId);
	
	@Query("SELECT COALESCE(SUM(e.totalprice), 0) FROM EventExtraExpenseEntity e WHERE e.event.id = :eventId AND e.eventFunction.id = :eventFunctionId")
	Double getTotalExtraExpense(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId);
	
	@Query("SELECT COALESCE(SUM(e.totalprice), 0) FROM EventExtraExpenseEntity e WHERE e.event.id = :eventId")
	Double getTotalExtraExpense(@Param("eventId") Long eventId);
	
	@Query(value = "SELECT COALESCE(SUM(totalprice), 0) FROM event_extra_expense WHERE event_id = :eventId AND event_function_id = :eventFunctionId", nativeQuery = true)
	Double getTotalExtraExpenseNative(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT COALESCE(SUM(e.totalprice), 0) FROM EventExtraExpenseEntity e WHERE e.event.user.id = :userId")
	Double getTotalExtraExpenseByUser(@Param("userId") Long userId);
	
	@Query("SELECT COALESCE(SUM(e.totalprice), 0) FROM EventExtraExpenseEntity e WHERE e.event.user.id = :userId AND e.event.eventStartDateTime BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	Double getTotalExtraExpenseByUserAndDate(@Param("userId") Long userId, @Param("givenDateTime") LocalDateTime givenDateTime);


}
