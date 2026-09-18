package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.MenuShareLinkEntity;

@Repository
public interface MenuShareLinkRepository extends JpaRepository<MenuShareLinkEntity, Long> {

    Optional<MenuShareLinkEntity> findByTokenAndIsDeleteFalse(String token);

	Optional<MenuShareLinkEntity> findByEventFunctionIdAndUserIdAndEventIdAndIsDeleteFalse(Long eventFunctionId,
			Long userId, Long eventId);
}