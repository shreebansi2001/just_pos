package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventRemarkMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface EventRemarkMasterRepository extends JpaRepository<EventRemarkMasterEntity, Long>{

	Optional<EventRemarkMasterEntity> findByIdAndIsDeleteFalse(long id);
	List<EventRemarkMasterEntity> findAllByIsDeleteFalse();
	List<EventRemarkMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);
	List<EventRemarkMasterEntity> findByUserIdAndIsDeleteFalseAndIsActiveTrue(Long userId);
	Optional<EventRemarkMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);
	List<EventRemarkMasterEntity> findByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse(String eventRemarkName,
			UserMasterEntity user);
	Optional<EventRemarkMasterEntity> findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse(String string,
			UserMasterEntity user);

}