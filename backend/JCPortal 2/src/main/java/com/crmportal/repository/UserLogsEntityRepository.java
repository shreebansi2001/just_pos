package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserLogsEntity;
import com.crmportal.response.dto.InActiveUserResponseDto;

@Repository
public interface UserLogsEntityRepository extends JpaRepository<UserLogsEntity, Long> {
	List<UserLogsEntity> findByUserAndIsDeleteFalse(String user);

	List<UserLogsEntity> findByUser(String user);

	List<UserLogsEntity> findAll();

	@Query("SELECT u FROM UserLogsEntity u " +
	       "WHERE u.user IN :users " +
	       "AND u.isDelete = false " +
	       "AND (:startDate IS NULL OR u.createAt >= :startDate) " +
	       "AND (:endDate IS NULL OR u.createAt <= :endDate) " +
	       "AND (:eventType IS NULL OR u.eventType = :eventType) " +
	       "AND (:eventId IS NULL OR u.eventId = :eventId)")
	List<UserLogsEntity> findLogsWithFilters(
	        @Param("users") List<String> users,
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate,
	        @Param("eventType") String eventType,
	        @Param("eventId") Long eventId
	);


	@Query(value = "SELECT " + "CONCAT(u.first_name, ' ', u.last_name) AS userName, "
			+ "ub.company_name AS companyName, " + "u.contact_no AS mobileNo, " + "u.email AS emailid, "
			+ "MAX(l.created_at) AS lastActivityDate " + "FROM user_logs l " + "JOIN users u ON u.email = l.user_id "
			+ "LEFT JOIN user_basic_details ub ON ub.user_id = u.user_id "
			+ "WHERE l.is_delete = false and u.is_delete = false and u.is_approve = true and  u.is_active = true "
			+ "GROUP BY u.user_id, u.first_name, u.last_name, ub.company_name, u.contact_no, u.email "
			+ "HAVING MAX(l.created_at) < :sevendays", nativeQuery = true)
	List<Object[]> findInactiveUsers(LocalDateTime sevendays);

	@Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name) AS userName " +
	        "FROM user_logs l " +
	        "JOIN users u ON u.email = l.user_id " +
	        "WHERE l.is_delete = false " +
	        "AND u.is_delete = false " +
	        "AND u.is_approve = true " +
	        "AND u.is_active = true " +
	        "AND l.event_id = :eventId AND event_type IN ('Menu Planning Update' , 'Menu Planning Save','Event Create','Event Update','Decor Planning Update' , 'Decor Planning Save')" +
	        "ORDER BY l.created_at DESC " +
	        "LIMIT 1",
	        nativeQuery = true)
	String getLastChangedName(@Param("eventId") Long eventId);
}
