package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.entity.LeadSourceEntity;
import com.crmportal.entity.LeadSubSourceEntity;
import com.crmportal.entity.PipelineCloseStageEntity;
import com.crmportal.entity.PipelineEntity;
import com.crmportal.entity.PipelineOpenStageEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface LeadMasterRepository extends JpaRepository<LeadMasterEntity, Long> {

	Optional<LeadMasterEntity> findByIdAndIsDeleteFalse(Long id);

	boolean existsByIdAndIsDeleteFalse(Long id);

	@Query(value = "SELECT lead_code FROM lead_master WHERE user_id = :userId AND is_delete = FALSE AND lead_code IS NOT NULL  AND lead_code LIKE CONCAT('%', :yearDigit, '%') ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
	String findLastInsertedLeadCode(Long userId, String yearDigit);

	List<LeadMasterEntity> findByLeadTypeAndIsDeleteFalseAndUser(String leadType,UserMasterEntity user);

	List<LeadMasterEntity> findAllByIsDeleteFalse();

	@Query(value = "SELECT lead_type, COUNT(*) FROM lead_master WHERE is_Delete = FALSE AND user_id = :userId GROUP BY lead_type ", nativeQuery = true)
	List<Object[]> countByLeadType(Long userId);
	
	@Query(value = 
			"SELECT lead_type, COUNT(*) "
			+ " FROM lead_master "
			+ " WHERE is_Delete = FALSE AND lead_assign_id = :memberId AND user_id = :userId"
			+ " GROUP BY lead_type "
			+ "", nativeQuery =  true)
	List<Object[]> countByLeadTypeAndLeadAssignId(Long memberId, Long userId);

	@Query(value = "SELECT  COUNT(*) FROM lead_master WHERE is_Delete = FALSE AND user_id = :userId ", nativeQuery = true)
	Long countByIsDeleteFalse(Long userId);

	List<LeadMasterEntity> findByLeadStatusAndIsDeleteFalse(String leadStatus);

	@Query(value =
	        "SELECT  COUNT(*) " +
	        "FROM lead_master l " +
	        "INNER JOIN lead_status_master ls ON l.lead_status_id = ls.lead_status_id " +
	        "INNER JOIN lead_status_type lst ON ls.lead_status_type_id = lst.lead_status_type_id " +
	        "WHERE l.is_delete = false AND l.user_id = :userId and lst.status_type = :typeName  ",
	        nativeQuery = true)
	Long countByStatusTypeName(Long userId,String typeName);
	
	@Query(value = 
			"SELECT lead_status, COUNT(*) "
			+ " FROM lead_master"
			+ " WHERE is_Delete = FALSE AND lead_assign_id = :memberId AND lead_master.user_id = :userId "
			+ " GROUP BY lead_status ", nativeQuery = true)
	List<Object[]> countByLeadStatusAndLeadAssignId(Long memberId, Long userId);

	@Query(value = "SELECT 'Lead Assigned', COUNT(lead_assign_id) FROM lead_master WHERE is_Delete = FALSE ", nativeQuery = true)
	List<Object[]> countByLeadAssigned();

	List<LeadMasterEntity> findByleadAssignIdAndIsDeleteFalse(Long leadAssignedId);
	
	@Query(value = 
			" SELECT "
			+ "	u.user_id, "
			+ "	u.first_name, "
			+ "	u.last_name, "
			+ "	u.email, "
			+ "	u.contact_no, "
			+ "	fdm.follow_up_id, "
			+ "	fdm.follow_up_type, "
			+ "	fdm.follow_up_status, "
			+ "	fdm.follow_up_date, "
			+ "	fdm.client_remarks, "
			+ "	fdm.employee_remarks, "
			+ "	fdm.is_delete, "
			+ "	fdm.created_at "
			+ " FROM follow_up_details_master fdm "
			+ " JOIN lead_master l ON l.lead_id = fdm.lead_id "
			+ " LEFT JOIN users u ON l.lead_assign_id = u.user_id "
			+ " WHERE (:memberId = -1 OR l.lead_assign_id = :memberId) AND fdm.is_Delete = FALSE AND l.user_id = :userId ", nativeQuery = true)
	List<Object[]> getAllFollowUpsByMember(Long memberId,Long userId);

	List<LeadMasterEntity> findAllByIsDeleteFalseAndLeadAssign_id(Long assignId);

	List<LeadMasterEntity> findAllByIsDeleteFalseAndUser(UserMasterEntity user);

	List<LeadMasterEntity> findAllByIsDeleteFalseAndLeadAssign_idAndUser(Long assignId, UserMasterEntity user);

	List<LeadMasterEntity> findByLeadStatusAndIsDeleteFalseAndUser(String leadStatus, UserMasterEntity user);

	List<LeadMasterEntity> findByleadAssignIdAndIsDeleteFalseAndUser(Long leadAssignedId, UserMasterEntity user);

	//boolean existsByLeadSubSourceAndIsDeleteFalse(LeadSubSourceEntity entity);

	boolean existsByLeadSourceAndIsDeleteFalse(LeadSourceEntity entity);

	//boolean existsByPipelineAndIsDeleteFalse(PipelineEntity pipelineEntity);

	//boolean  existsByOpenStageAndIsDeleteFalse(PipelineOpenStageEntity stage);

	//boolean  existsByCloseStageAndIsDeleteFalse(PipelineCloseStageEntity stage);
	
	@Query(value = "SELECT client_name,contact_number,CONCAT(u.first_name, ' ', u.last_name) AS lead_assign,lst.status_name,ls.source_name,et.name_english, "
			+ "CAST(CONCAT(min_pax, '-', max_pax) AS CHAR) AS person,DATE_FORMAT(lm.inquiry_date, '%d-%m-%Y') AS inquiry_date,lm.lead_remark,lm.lead_priority "
			+ "FROM lead_master lm LEFT JOIN users u ON u.user_id = lm.lead_assign_id "
			+ "LEFT JOIN lead_source_master ls ON ls.lead_source_id = lm.lead_source_id "
			+ "LEFT JOIN lead_status_master lst ON lst.lead_status_id = lm.lead_status_id "
			+ "LEFT JOIN eventtype et ON et.event_type_id = lm.event_type_id "
			+ "	WHERE lm.user_id = :userid "
			+ "	AND lm.is_delete = FALSE "
			+ "	AND (:firstDate IS NULL OR :lastDate IS NULL OR DATE(lm.created_at) BETWEEN :firstDate AND :lastDate) "
			+ "	AND (:flag = TRUE OR lm.lead_assign_id IN (:managerIds))"
			+ " AND (:statusId = 0 OR lm.lead_status_id IN (:statusId)) AND (:sourceId = 0 OR lm.lead_source_id IN (:sourceId))  AND (:priority = 'All' OR lm.lead_priority = :priority)  ", nativeQuery = true)
	List<Object[]> findDatewiseLeadSummary(LocalDate firstDate, LocalDate
			  lastDate, Long statusId, Long sourceId, String priority, List<Long> managerIds, Long
			  userid, Boolean flag);
	
	  @Query("SELECT l FROM LeadMasterEntity l " +
	            "WHERE l.isDelete = false " +
	            "AND (:statusId IS NULL OR l.leadStatus.leadStatusId = :statusId) " +
	            "AND (:priority IS NULL OR :priority = '' OR l.leadPriority = :priority) " +
	            "AND (:sourceId IS NULL OR l.leadSource.leadSourceId = :sourceId) " +
	            "AND (:userId IS NULL OR l.leadAssign.id = :userId)")
	    List<LeadMasterEntity> searchLeads(Long statusId,String priority,Long sourceId,Long userId);

	  @Query(value = "SELECT "
		        + "lm.client_name, "
		        + "lm.contact_number, "
		        + "lm.company_name, "
		        + "ls.source_name, "
		        + "DATE_FORMAT(lm.created_at, '%d-%m-%Y'), "
		        + "DATE_FORMAT(fu.follow_up_date, '%d-%m-%Y'), "
		        + "fu.follow_up_type, "
		        + "CONCAT(COALESCE(fu.client_remarks, ''), "
		        + "CASE WHEN fu.employee_remarks IS NOT NULL AND fu.employee_remarks != '' "
		        + "THEN CONCAT(' / ', fu.employee_remarks) ELSE '' END), "
		        + "lst.status_name, "
		        + "lm.lead_priority, "
		        + "lm.deal_value, "
		        + "CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, '')), "
		        + "lm.lead_assign_id "
		        + "FROM lead_master lm "
		        + "LEFT JOIN users u ON u.user_id = lm.lead_assign_id "
		        + "LEFT JOIN lead_source_master ls ON ls.lead_source_id = lm.lead_source_id "
		        + "LEFT JOIN lead_status_master lst ON lst.lead_status_id = lm.lead_status_id "
		        + "LEFT JOIN follow_up_details_master fu "
		        + "ON fu.lead_id = lm.lead_id "
		        + "AND fu.is_delete = FALSE "
		        + "AND fu.follow_up_id = ( "
		        + "    SELECT MAX(fu2.follow_up_id) "
		        + "    FROM follow_up_details_master fu2 "
		        + "    WHERE fu2.lead_id = lm.lead_id "
		        + "    AND fu2.is_delete = FALSE "
		        + ") "
		        + "WHERE lm.user_id = :userid "
		        + "AND lm.is_delete = FALSE "
		        + "AND (:firstDate IS NULL OR :lastDate IS NULL "
		        + "     OR DATE(fu.follow_up_date) BETWEEN :firstDate AND :lastDate) "
		        + "AND (:flag = TRUE OR lm.lead_assign_id IN (:managerIds)) "
		        + "AND (:statusId = 0 OR lm.lead_status_id = :statusId) "
		        + "AND (:sourceId = 0 OR lm.lead_source_id = :sourceId) "
		        + "AND (:priority = 'All' OR lm.lead_priority = :priority)",
		        nativeQuery = true)
		List<Object[]> findDatewiseFollowupSummary(
		        LocalDate firstDate,
		        LocalDate lastDate,
		        Long statusId,
		        Long sourceId,
		        String priority,
		        List<Long> managerIds,
		        Long userid,
		        Boolean flag);

}
