package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.entity.UserLeadEventEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface UserLeadEventRepository extends JpaRepository<UserLeadEventEntity, Long>{

	void deleteAllByLead(LeadMasterEntity entity);

	List<UserLeadEventEntity> findAllByLeadAndUser(LeadMasterEntity entity, UserMasterEntity user);

}
