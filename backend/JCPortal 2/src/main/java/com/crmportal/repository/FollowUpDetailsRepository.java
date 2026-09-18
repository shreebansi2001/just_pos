package com.crmportal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.crmportal.entity.FollowUpDetailsEntity;
import com.crmportal.entity.LeadMasterEntity;

public interface FollowUpDetailsRepository extends JpaRepository<FollowUpDetailsEntity, Long> {

	Optional<FollowUpDetailsEntity> findByIdAndIsDeleteFalse(Long id);

	boolean existsByLeadAndIsDeleteFalse(LeadMasterEntity leadMasterEntity);

	List<FollowUpDetailsEntity> findByLeadAndIsDeleteFalse(LeadMasterEntity leadMasterEntity);

	boolean existsByLead(LeadMasterEntity leadMasterEntity);

	List<FollowUpDetailsEntity> findByLeadIdAndIsDeleteFalseAndCreatedAtBetween(Long leadId, LocalDateTime startDate,
			LocalDateTime endDate);

	List<FollowUpDetailsEntity> findByLeadIdAndIsDeleteFalseAndFollowUpDateBetween(Long leadId, LocalDateTime startDate,
			LocalDateTime endDate);

	List<FollowUpDetailsEntity> findByFollowUpDateAndIsDeleteFalse(LocalDateTime twoDaysFromNow);

	List<FollowUpDetailsEntity> findByLeadIdAndIsDeleteFalse(Long leadId);

	List<FollowUpDetailsEntity> findByLeadIdAndIsDeleteFalseAndCreatedAtGreaterThanEqual(Long leadId,
			LocalDateTime formatedStartDate);

	List<FollowUpDetailsEntity> findByLeadIdAndIsDeleteFalseAndFollowUpDateGreaterThanEqual(Long leadId,
			LocalDateTime formatedStartDate);

	void deleteAllByLead(LeadMasterEntity entity);
	
	
	  @Query(value = "SELECT COUNT(*) " +
	  " FROM follow_up_details_master fd left join lead_master lm on lm.lead_id = fd.lead_id "
	  + " WHERE fd.is_delete = false AND lm.user_id = :userId " +
	  " AND DATE(fd.follow_up_date) = CURDATE()", nativeQuery = true) Long
	  countTodayFollowUp(Long userId);
	 

}
