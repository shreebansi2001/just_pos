package com.crmportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFoodTestingLinkEntity;
import com.crmportal.entity.EventMasterEntity;

@Repository
public interface EventFoodTestingLinkRepository extends JpaRepository<EventFoodTestingLinkEntity, Long> {

	Optional<EventFoodTestingLinkEntity> findByEventFunctionIdAndTesterIdAndUserIdAndEventIdAndIsDeleteFalse(
			Long eventFunctionId, Long testerId, Long userId, Long eventId);

	Optional<EventFoodTestingLinkEntity> findByTokenAndIsDeleteFalse(String token);

}
