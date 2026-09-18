package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.LeadSourceEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface LeadSourceRepository extends JpaRepository<LeadSourceEntity, Long>{

	Optional<LeadSourceEntity> findByLeadSourceIdAndIsDeletedFalse(Long leadSourceId);
	
	List<LeadSourceEntity> findAllByIsDeletedFalse();

	List<LeadSourceEntity> findAllByIsDeletedFalseAndUser(UserMasterEntity user);

	Optional<LeadSourceEntity> findBySourceNameContainingIgnoreCaseAndIsDeletedFalseAndUser(String sourceName,
			UserMasterEntity user);
}
