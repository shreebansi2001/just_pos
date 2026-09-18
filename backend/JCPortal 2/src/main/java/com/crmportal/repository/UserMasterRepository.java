package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.controller.EventFollowUpEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.UserLogsResponseDto;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMasterEntity, Long> {

	Optional<UserMasterEntity> findByEmailAndIsDeleteFalse(String email);

	Optional<UserMasterEntity> findByIdAndIsDeleteFalse(Long id);

	@Query("SELECT ubd.user FROM UserBasicDetailsMasterEntity ubd " + "WHERE (ubd.user.clientId = :clientUserId "
			+ "OR ubd.user.id = :clientUserId) " + "AND ubd.role.name NOT IN :roleNames "
			+ "AND ubd.user.isDelete = false")
	List<UserMasterEntity> findManagersAndAdminsByClientId(@Param("clientUserId") Long clientUserId,
			@Param("roleNames") List<String> roleNames);

	List<UserMasterEntity> findAllByClientIdAndIsDeleteFalse(Long id);

	Optional<UserMasterEntity> findByEmailAndIsDeleteFalseAndIsApproveTrue(String emailId);

	@Query("SELECT u.userCode FROM UserMasterEntity u WHERE u.isDelete = false ORDER BY u.userCode DESC")
	Optional<String> findLatestUserCode();

	Optional<UserMasterEntity> findTopByIsDeleteFalseOrderByUserCodeDesc();

	Optional<UserMasterEntity> findByContactNoAndIsDeleteFalse(String mobileNo);

	Optional<UserMasterEntity> findByEmailAndIsDeleteFalseAndIsActiveTrueAndIsApproveTrue(String emailId);

	@Query(value = "SELECT * FROM users " + "WHERE is_delete = false "
			+ "AND usercode IS NOT NULL AND TRIM(usercode) <> '' " + "AND usercode LIKE CONCAT('JC', :typeCode, '%') "
			+ "ORDER BY CAST(RIGHT(usercode, 4) AS UNSIGNED) DESC " + "LIMIT 1", nativeQuery = true)
	Optional<UserMasterEntity> findLastUserCode(@Param("typeCode") String typeCode);

	// Query 1: Find users with subscriptions expiring in a date range
	@Query("SELECT DISTINCT u FROM UserPlansHistoryEntity up " + "JOIN up.user u " + "WHERE up.isActive = true "
			+ "AND u.isApprove = true " + "AND u.isActive = true "
			+ "AND up.endDate BETWEEN :reminderStart AND :reminderEnd")
	List<UserMasterEntity> findByEndDateBetweenAndIsActiveTrueAndIsApproveTrue(
			@Param("reminderStart") LocalDateTime reminderStart, @Param("reminderEnd") LocalDateTime reminderEnd);

	// Query 2: Find users with expired subscriptions (FIXED - Added @Query
	// annotation)
	@Query("SELECT DISTINCT u FROM UserPlansHistoryEntity up " + "JOIN up.user u " + "WHERE up.isActive = true "
			+ "AND u.isApprove = true " + "AND u.isActive = true " + "AND up.endDate < :reminderStart")
	List<UserMasterEntity> findByEndDateBeforeAndIsActiveTrueAndIsApproveTrue(
			@Param("reminderStart") LocalDateTime reminderStart);

	// Query 3: Find users with subscriptions expiring in range by clientId
	@Query("SELECT DISTINCT u FROM UserPlansHistoryEntity up " + "JOIN up.user u " + "WHERE up.isActive = true "
			+ "AND u.isApprove = true " + "AND u.isActive = true " + "AND u.clientId = :clientId "
			+ "AND up.endDate BETWEEN :reminderStart AND :reminderEnd")
	List<UserMasterEntity> findByEndDateBetweenAndIsActiveTrueAndIsApproveTrueAndClientId(
			@Param("reminderStart") LocalDateTime reminderStart, @Param("reminderEnd") LocalDateTime reminderEnd,
			@Param("clientId") long clientId);

	@Query("SELECT COUNT(u) FROM UserMasterEntity u WHERE u.clientId = 0 AND u.isActive = true AND u.isDelete = false")
	Long countActiveUsersWithClientIdZero();

	@Query("SELECT COUNT(u) FROM UserMasterEntity u WHERE u.clientId = 0")
	Long countActiveUsersWithClientIdZeroAll();

	@Query(value = "SELECT u.user_id AS userId, u.first_name AS firstName, u.last_name AS lastName, u.email AS email, u.contact_no AS contactNo, u.is_active AS isActive, u.is_approve AS isApproved, u.usercode AS userCode, u.pre_fix AS preFix, u.created_at AS createdAt, bd.company_name AS companyName, bd.company_email AS companyEmail, bd.member_type AS memberType, p.plan_id AS planId, p.name AS planName, uph.start_date AS planStartDate, uph.end_date AS planEndDate FROM users u LEFT JOIN user_basic_details bd ON bd.user_id = u.user_id AND bd.isDelete = false LEFT JOIN user_plan_histories uph ON uph.user_id = u.user_id AND uph.is_active = true LEFT JOIN plans p ON p.plan_id = uph.plan_id AND p.isDelete = false WHERE u.client_id = 0 AND u.isDelete = false AND u.created_at BETWEEN :startDate AND :endDate ORDER BY u.created_at ASC", nativeQuery = true)
	List<Object[]> getUsersWithPlanNative(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query(value = " SELECT " + " 	u.user_id, " + " 	u.first_name, " + " 	u.last_name, " + " 	u.email, "
			+ " 	u.contact_no, " + " 	u.pre_fix, " + " 	r.role_id, " + " 	r.name " + " FROM users u "
			+ " JOIN user_basic_details ubd ON ubd.user_id = u.user_id " + " JOIN `roles` r ON ubd.role_id = r.role_id "
			+ " WHERE u.client_id = 1 AND r.role_id=3 AND u.is_delete = FALSE", nativeQuery = true)
	List<Object[]> getAllManagers();

	@Query(value = " SELECT " + " 	u.user_id, " + " 	u.first_name, " + " 	u.last_name, " + " 	u.email, "
			+ " 	u.contact_no, " + " 	u.pre_fix, " + " 	r.role_id, " + " 	r.name " + " FROM users u "
			+ " JOIN user_basic_details ubd ON ubd.user_id = u.user_id " + " JOIN `roles` r ON ubd.role_id = r.role_id "
			+ " WHERE u.client_id = 1 AND r.role_id NOT IN (1, 2, 3) AND u.is_delete = FALSE", nativeQuery = true)
	List<Object[]> getAllRolesExcludesAdminManager();

	@Query(value = "SELECT " + "    u.user_id, " + "    u.first_name AS employee_first_name, "
			+ "    u.last_name AS employee_last_name, " + "    u.email AS employee_email, "
			+ "    u.contact_no AS employee_contact_no, " + "    u.pre_fix AS employee_pre_fix, "
			+ "    m.user_id AS manager_id, " + "    m.first_name AS manager_first_name, "
			+ "    m.last_name AS manager_last_name, " + "    m.email AS manager_email, "
			+ "    m.contact_no AS manager_contact_no, " + "    m.pre_fix AS manager_pre_fix, "
			+ "    u.created_at as createdAt, " + "    u.is_approve as isApprove, "
			+ "    ubd.company_name as company_name " + " FROM user_basic_details ubd "
			+ " JOIN users u ON u.user_id = ubd.user_id "
			+ " LEFT JOIN users m ON m.user_id = ubd.reporting_manager_id " + " WHERE u.client_id = 0 "
			+ " AND u.is_delete = FALSE "
			+ " AND (:reportingManagerId = 1 OR ubd.reporting_manager_id = :reportingManagerId) ", nativeQuery = true)
	List<Object[]> getAllClientsByReportingManagerId(Long reportingManagerId);

	Optional<UserMasterEntity> findByUserCodeAndIsDeleteFalse(String code);

	@Query(value = "SELECT d.dt AS created_date, COUNT(u.created_at) AS total_count " + "FROM ( "
			+ "   SELECT STR_TO_DATE(:startDate, '%d/%m/%Y') + INTERVAL n DAY AS dt " + "   FROM ( "
			+ "       SELECT a.n + b.n * 10 + c.n * 100 AS n " + "       FROM "
			+ "           (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 "
			+ "            UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a, "
			+ "           (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 "
			+ "            UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b, "
			+ "           (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 "
			+ "            UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c "
			+ "   ) numbers "
			+ "   WHERE STR_TO_DATE(:startDate, '%d/%m/%Y') + INTERVAL n DAY <= STR_TO_DATE(:endDate, '%d/%m/%Y') "
			+ ") d " + "   LEFT JOIN users u ON u.created_at >= d.dt "
			+ " 	 	AND u.created_at < d.dt + INTERVAL 1 DAY " + "	  	AND u.is_delete = FALSE "
			+ "   LEFT JOIN user_basic_details ubd ON u.user_id = ubd.user_id " + " 	 	AND ubd.role_id = 2 "
			+ "   LEFT JOIN user_plan_histories uph ON u.user_id = uph.user_id " + " 	 	AND uph.is_active = TRUE"
			+ " WHERE " + "  (:planId = -1 OR uph.plan_id = :planId)" + "  AND ubd.is_delete = FALSE  "
			+ " GROUP BY d.dt " + " ORDER BY d.dt", nativeQuery = true)
	List<Object[]> getUserCountBetweenDates(@Param("startDate") String startDate, @Param("endDate") String endDate,
			@Param("planId") Long planId);

	@Query("SELECT new com.crmportal.response.dto.UserLogsResponseDto(" + "u.id, "
			+ "CONCAT(u.firstName, ' ', u.lastName), " + "ubd.companyName, " + "u.email, " + "CASE "
			+ "    WHEN EXISTS ( " + "        SELECT 1 FROM UserLogsEntity l " + "        WHERE l.user = u.email "
			+ "        AND l.createAt >= :last7Days " + "    ) THEN 'ACTIVE' " + "    ELSE 'DEACTIVE' " + "END " + ") "
			+ "FROM UserMasterEntity u " + "JOIN UserBasicDetailsMasterEntity ubd ON u.id = ubd.user.id "
			+ "WHERE u.isDelete = FALSE " + "AND u.clientId = 0 " + "AND ( :isActive IS NULL OR "
			+ "      ( :isActive = true AND EXISTS ( " + "            SELECT 1 FROM UserLogsEntity l "
			+ "            WHERE l.user = u.email " + "            AND l.createAt >= :last7Days " + "      )) OR "
			+ "      ( :isActive = false AND NOT EXISTS ( " + "            SELECT 1 FROM UserLogsEntity l "
			+ "            WHERE l.user = u.email  " + "            AND l.createAt >= :last7Days " + "      )) " + ")")
	List<UserLogsResponseDto> getUserStatus(@Param("last7Days") LocalDateTime last7Days,
			@Param("isActive") Boolean isActive);

	@Query(value = "SELECT u.user_id AS userId, " + "CONCAT(u.first_name, ' ', u.last_name) AS userName, "
			+ "ub.company_name AS companyName, " + "u.created_at AS createdAt, " + "ub.type AS status, " +

			"COALESCE(e.eventCount,0) + " + "COALESCE(m.menuCount,0) + " + "COALESCE(q.quotationCount,0) + "
			+ "COALESCE(ma.menuAllocationCount,0) + " + "COALESCE(r.rawMatCount,0) + " + "COALESCE(i.invoiceCount,0) + "
			+ "COALESCE(l.labourCount,0) AS totalScore " +

			"FROM users u " +

			"LEFT JOIN user_basic_details ub ON u.user_id = ub.user_id " +

			"LEFT JOIN (SELECT user_id, COUNT(*) eventCount FROM events WHERE is_delete = FALSE GROUP BY user_id) e ON u.user_id = e.user_id "
			+

			"LEFT JOIN (SELECT e.user_id, COUNT(DISTINCT e.event_id) menuCount " + "           FROM menupreparation mp "
			+ "           JOIN event_function ef ON mp.event_function_id = ef.event_function_id "
			+ "           JOIN events e ON ef.event_id = e.event_id "
			+ "           WHERE mp.is_delete = FALSE AND ef.is_delete = FALSE AND e.is_delete = FALSE "
			+ "           GROUP BY e.user_id) m ON u.user_id = m.user_id " +

			"LEFT JOIN (SELECT user_id, COUNT(DISTINCT event_id) quotationCount "
			+ "           FROM quotations WHERE is_delete = FALSE GROUP BY user_id) q ON u.user_id = q.user_id " +

			"LEFT JOIN (SELECT user_id, COUNT(DISTINCT event_id) menuAllocationCount "
			+ "           FROM eventfunction_menuallocation WHERE is_delete = FALSE GROUP BY user_id) ma ON u.user_id = ma.user_id "
			+

			"LEFT JOIN (SELECT e.user_id, COUNT(DISTINCT e.event_id) rawMatCount "
			+ "           FROM event_raw_material rm " + "           JOIN events e ON rm.event_id = e.event_id "
			+ "           WHERE rm.is_delete = FALSE AND e.is_delete = FALSE "
			+ "           GROUP BY e.user_id) r ON u.user_id = r.user_id " +

			"LEFT JOIN (SELECT user_id, COUNT(DISTINCT event_id) invoiceCount "
			+ "           FROM event_invoice WHERE is_delete = FALSE GROUP BY user_id) i ON u.user_id = i.user_id " +

			"LEFT JOIN (SELECT e.user_id, COUNT(DISTINCT e.event_id) labourCount " + "           FROM event_labor el "
			+ "           JOIN events e ON el.event_id = e.event_id " + "           WHERE e.is_delete = FALSE "
			+ "           GROUP BY e.user_id) l ON u.user_id = l.user_id " +

			"WHERE u.is_delete = FALSE " + "ORDER BY totalScore DESC " + "LIMIT 50", nativeQuery = true)
	List<Object[]> getTop50Users();

	@Query(value = "SELECT DISTINCT a.account_contact_id , a.name, '' AS eventTypeName, "
			+ "CAST(NULL AS DATETIME) AS startDateTime, CAST(NULL AS DATETIME) AS endDateTime, "
			+ " a.opening_date as opbDate, a.current_balance as opb,a.created_at AS createdAt  FROM "
			+ "account_contact a WHERE user_id = :userid AND a.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> findAllByUser(Long userid);

	@Query("select u.isVisible from UserMasterEntity u where u.id = :userId")
	Boolean getIsVisibleByUserId(Long userId);

	@Modifying
	@Transactional
	@Query(value = "update users set is_visible = :isVisible WHERE user_id = :userId  OR client_id = :userId", nativeQuery = true)
	int updateIsVisibleByUserId(Long userId, Boolean isVisible);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " + "FROM UserMasterEntity u "
			+ "WHERE u.clientId = :userId " + "AND u.ischilduser = true")
	boolean isChildUserExist(@Param("userId") Long userId);

	boolean existsByUniqueCode(String code);

	@Query(value = "SELECT CONCAT(u.firstName, ' ', u.lastName) FROM UserMasterEntity u WHERE u.id = :entryBy ")
	String getUserNameById(Long entryBy);

	@Modifying
	@Transactional
	@Query(value = "UPDATE users SET is_active = TRUE , is_approve = TRUE , is_block = FALSE WHERE client_id = :userId", nativeQuery = true)
	void updateIsApproveAndIsActive(Long userId);

	@Query("SELECT u FROM UserMasterEntity u WHERE u.clientId NOT IN (0,-1)")
	List<UserMasterEntity> findAllManagers();

	@Query("SELECT u FROM UserMasterEntity u WHERE u.clientId IN (0,-1)")
	List<UserMasterEntity> findAllAdmins();

	List<UserMasterEntity> findByIdInAndIsDeleteFalse(List<Long> managerIds);

	@Query("SELECT email FROM UserMasterEntity u WHERE u.clientId = :userId")
	List<String> findUserEmailsByAdminId(Long userId);
}
