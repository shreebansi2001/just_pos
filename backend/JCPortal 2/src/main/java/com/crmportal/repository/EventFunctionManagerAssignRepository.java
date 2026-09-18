package com.crmportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionManagerAssignEntity;

@Repository
public interface EventFunctionManagerAssignRepository extends JpaRepository<EventFunctionManagerAssignEntity, Long> {

	void deleteAllByEventIdAndUserId(Long eventId, Long userId);

}
