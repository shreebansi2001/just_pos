package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.VenueMasterEntity;

@Repository
public interface VenueMasterRepository extends JpaRepository<VenueMasterEntity, Long>{

	VenueMasterEntity findByIdAndIsDeleteFalseAndUser(Long id, UserMasterEntity user);

	Optional<VenueMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	List<VenueMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<VenueMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String venueName,
			UserMasterEntity user);

	List<VenueMasterEntity> findAllByUserAndIsActiveAndIsDeleteFalse(UserMasterEntity user, Boolean isActive);

	List<VenueMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsActiveAndIsDeleteFalse(String venueName,
			UserMasterEntity user, Boolean isActive);

	VenueMasterEntity findByIdAndIsDeleteFalse(Long id);

	boolean existsByIdAndIsDeleteFalse(Long id);

	
}
