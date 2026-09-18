package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface EventTypeMasterRepository extends JpaRepository<EventTypeMasterEntity, Long>{

	Optional<EventTypeMasterEntity> findByIdAndIsDeleteFalse(long id);
	List<EventTypeMasterEntity> findAllByIsDeleteFalse();
	Optional<EventTypeMasterEntity> findByIdAndIsDeleteFalse(Long id);
	List<EventTypeMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);
	Optional<EventTypeMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);
	List<EventTypeMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String eventTypeName,
			UserMasterEntity user);


}