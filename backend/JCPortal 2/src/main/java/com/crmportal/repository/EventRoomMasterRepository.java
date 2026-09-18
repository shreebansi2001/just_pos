package com.crmportal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventRoomMasterEntity;

@Repository
public interface EventRoomMasterRepository extends JpaRepository<EventRoomMasterEntity, Long> {

	List<EventRoomMasterEntity> findByEvent_Id(Long eventId);
	
	@Modifying
	@Transactional
	@Query("delete from EventRoomMasterEntity e where e.event.id = :eventId")
	int deleteByEventId(@Param("eventId") Long eventId);

}
