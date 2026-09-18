package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.LeadSourceEntity;
import com.crmportal.entity.LeadSubSourceEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface LeadSubSourceRepository extends JpaRepository<LeadSubSourceEntity, Long> {
	
	Optional<LeadSubSourceEntity> findByLeadSubSourceIdAndIsDeleteFalse(Long subSourceId);
	
	List<LeadSubSourceEntity> findAllByIsDeleteFalse();
	
	List<LeadSubSourceEntity> findAllByLeadSourceAndIsDeleteFalse(LeadSourceEntity leadSource);

	Optional<LeadSubSourceEntity> findByNameContainingIgnoreCaseAndIsDeleteFalse(String name);

	boolean existsByLeadSourceAndIsDeleteFalse(LeadSourceEntity entity);

	List<LeadSubSourceEntity> findAllByIsDeleteFalseAndUser(UserMasterEntity userMasterEntity);
	
}
