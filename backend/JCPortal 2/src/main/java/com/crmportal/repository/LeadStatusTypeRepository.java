package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.LeadStatusEntity;
import com.crmportal.entity.LeadStatusTypeEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface LeadStatusTypeRepository extends JpaRepository<LeadStatusTypeEntity, Long>{

	Optional<LeadStatusTypeEntity> findByLeadStatusTypeId(Long leadStatusTypeId);
	
	
}
