package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionStaffAssignmentEntity;
import com.crmportal.enums.AllocationType;

@Repository
public interface EventFunctionStaffAssignmentRepository
		extends JpaRepository<EventFunctionStaffAssignmentEntity, Long> {

	List<EventFunctionStaffAssignmentEntity> findByEventIdAndEventFunctionIdAndResourceType(Long eventId,
			Long eventFunctionId, AllocationType allocationType);

}
