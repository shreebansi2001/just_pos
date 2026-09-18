package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.controller.EventFollowUpEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.EventPartiesResponseDto;

@Repository
public interface EventMasterRepository
		extends JpaRepository<EventMasterEntity, Long>, JpaSpecificationExecutor<EventMasterEntity> {

	Optional<EventMasterEntity> findTopByEventNoStartingWithOrderByEventNoDesc(String prefix);

	Optional<EventMasterEntity> findByIdAndIsDeleteFalse(long id);

	@Query("SELECT e FROM EventMasterEntity e WHERE e.user = :user AND e.isDelete = false " + "ORDER BY "
			+ "CASE WHEN DATE(e.eventStartDateTime) >= CURRENT_DATE THEN 0 ELSE 1 END, "
			+ "CASE WHEN DATE(e.eventStartDateTime) >= CURRENT_DATE THEN e.eventStartDateTime END ASC, "
			+ "CASE WHEN DATE(e.eventStartDateTime) < CURRENT_DATE THEN e.eventStartDateTime END DESC ")
	List<EventMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<EventMasterEntity> findByParty_NameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String partyName,
			UserMasterEntity user);

	List<EventMasterEntity> findAllByPartyAndIsDeleteFalse(PartyMasterEntity party);

	List<EventMasterEntity> findByParty_NameEnglishContainingIgnoreCaseAndPartyAndIsDeleteFalse(String partyName,
			PartyMasterEntity party);

	/*
	 * List<EventMasterEntity>
	 * findAllByUserAndIsDeleteFalseAndEventDateGreaterThanEqualOrderByEventDateAsc(
	 * UserMasterEntity user, LocalDateTime now);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findAllByUserAndStatusAndIsDeleteFalseAndEventDateGreaterThanEqualOrderByEventDateAsc(
	 * UserMasterEntity user, Integer eventStatus, LocalDateTime now);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findByEventStartDateTimeAndIsDeleteFalseAndStatus(LocalDateTime dateTime,
	 * Integer eventStatus);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findByEventStartDateTimeAndIsDeleteFalse(LocalDateTime dateTime);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findAllByUserAndIsDeleteFalseAndEventStartDateTimeGreaterThanEqualAndEventEndDateTimeLessThanEqualOrderByEventStartDateTimeAsc(
	 * UserMasterEntity user, LocalDateTime start, LocalDateTime end);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findAllByUserAndStatusAndIsDeleteFalseAndEventStartDateTimeGreaterThanEqualAndEventEndDateTimeLessThanEqualOrderByEventStartDateTimeAsc(
	 * UserMasterEntity user, Integer eventStatus, LocalDateTime start,
	 * LocalDateTime end);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findByParty_NameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndEventDateGreaterThanEqualOrderByEventDateAsc(
	 * String partyName, UserMasterEntity user, LocalDateTime now);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findByParty_NameEnglishContainingIgnoreCaseAndUserAndStatusAndIsDeleteFalseAndEventDateGreaterThanEqualOrderByEventDateAsc(
	 * String partyName, UserMasterEntity user, Integer eventStatus, LocalDateTime
	 * now);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findByParty_NameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndEventStartDateTimeGreaterThanEqualAndEventEndDateTimeLessThanEqualOrderByEventStartDateTimeAsc(
	 * String partyName, UserMasterEntity user, LocalDateTime start, LocalDateTime
	 * end);
	 * 
	 * 
	 * List<EventMasterEntity>
	 * findByParty_NameEnglishContainingIgnoreCaseAndUserAndStatusAndIsDeleteFalseAndEventStartDateTimeGreaterThanEqualAndEventEndDateTimeLessThanEqualOrderByEventStartDateTimeAsc(
	 * String partyName, UserMasterEntity user, Integer eventStatus, LocalDateTime
	 * start, LocalDateTime end);
	 */

	long countByUser_IdAndIsDeleteFalse(Long userId);

	@Query("SELECT e FROM EventMasterEntity e " + "WHERE e.user.id = :userId AND e.isDelete = false "
			+ "AND e.eventStartDateTime >= :startDateTime " + "AND e.eventStartDateTime <= :endDateTime "
			+ "ORDER BY e.eventStartDateTime ASC")
	List<EventMasterEntity> getEventsByUserAndDateRange(@Param("userId") Long userId,
			@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

	@Query(value = "SELECT DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y') AS function_date, et.name_english AS event_name, "
			+ " CONCAT(f.name_english, ' ', DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y %h:%i %p')) AS session_date_time, ef.pax, "
			+ " ef.function_venue AS venue_name, p.name_english AS guest_name, pm.name_english AS mgr_name, "
			+ " CASE WHEN e.is_r_menu = TRUE THEN 'PENDING' ELSE 'COMPLETE' END AS menu_releasing_status, e.childuserid "
			+ " FROM event_function ef " + " JOIN functions f ON ef.function_master_id = f.function_id "
			+ " JOIN events e ON ef.event_id = e.event_id "
			+ " JOIN eventtype et ON  e.event_type_id = et.event_type_id "
			+ " JOIN partymaster p ON e.party_id = p.party_id " + " JOIN partymaster pm ON e.manager_id = pm.party_id "
			+ " WHERE e.user_id = :userId "
			+ " AND (:endDate IS NOT NULL AND DATE(ef.function_start_date_time) BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')) "
			+ " OR (:endDate IS NULL AND DATE(ef.function_start_date_time) >= STR_TO_DATE(:formatedDate, '%d/%m/%Y')) "
			+ " ORDER BY ef.function_start_date_time, e.event_id ", nativeQuery = true)
	List<Object[]> getEventByDate(@Param("startDate") String startDate, @Param("endDate") String endDate,
			@Param("userId") Long userId);

	@Query("SELECT id FROM EventMasterEntity WHERE isDelete = FALSE AND eventStartDateTime BETWEEN :startDate AND :endDate ORDER BY eventStartDateTime ASC")
	List<Long> findByEventStartDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

	Optional<EventMasterEntity> findTopByEventNoStartingWithAndUserOrderByEventNoDesc(String prefix,
			UserMasterEntity userMasterEntity);

	@Query(value = "SELECT pm.name_english AS nameEnglish FROM `events` e LEFT JOIN partymaster pm ON e.party_id = pm.party_id WHERE e.event_id = :eventId", nativeQuery = true)
	Optional<String> getPartyByEventId(@Param("eventId") Long eventId);

	@Query(value = "SELECT e.* " + "FROM events e " + "LEFT JOIN partymaster p ON e.party_id = p.party_id "
			+ "WHERE e.user_id = :userId " + "AND e.is_delete = false "
			+ "AND (:partyName IS NULL OR LOWER(p.name_english) LIKE LOWER(CONCAT('%', :partyName, '%'))) "
			+ "AND (:status = -1 OR e.status = :status) " + "AND ( " + "    (:month = '-1' OR :year = '-1') "
			+ "    OR ( " + "        e.event_start_date_time < DATE_ADD( "
			+ "            STR_TO_DATE(CONCAT(:year, '-', :month, '-01'), '%Y-%m-%d'), "
			+ "            INTERVAL 1 MONTH " + "        ) " + "        AND "
			+ "        e.event_end_date_time >= STR_TO_DATE( "
			+ "            CONCAT(:year, '-', :month, '-01'), '%Y-%m-%d' " + "        ) " + "    ) " + ") "
			+ "ORDER BY " + "CASE " + "    WHEN DATE(e.event_start_date_time) = CURDATE() THEN 0 "
			+ "    WHEN DATE(e.event_start_date_time) > CURDATE() THEN 1 " + "    ELSE 2 " + "END, "
			+ "e.event_start_date_time", nativeQuery = true)
	List<EventMasterEntity> findEventsSorted(@Param("userId") Long userId, @Param("partyName") String partyName,
			@Param("month") String month, @Param("year") String year, @Param("status") Integer status);

	@Query(value = "SELECT e.* " + "FROM events e " + "LEFT JOIN partymaster p ON e.party_id = p.party_id "
			+ "WHERE e.user_id = :userId " + "AND e.is_delete = false "
			+ "AND (:partyName IS NULL OR LOWER(p.name_english) LIKE LOWER(CONCAT('%', :partyName, '%'))) "
			+ "AND (:status = -1 OR status = :status) " + "AND ( (:month = '-1' OR :year = '-1') "
			+ "      OR (MONTH(e.event_start_date_time) = CAST(:month AS UNSIGNED) "
			+ "          AND YEAR(e.event_start_date_time) = CAST(:year AS UNSIGNED)) ) " + "ORDER BY " + "CASE "
			+ "WHEN DATE(e.event_start_date_time) = CURDATE() THEN 0 "
			+ "WHEN DATE(e.event_start_date_time) > CURDATE() THEN 1 " + "ELSE 2 " + "END, "
			+ "e.event_start_date_time DESC " + "LIMIT 10", nativeQuery = true)
	List<EventMasterEntity> findTop10Events(@Param("userId") Long userId, @Param("partyName") String partyName,
			@Param("month") String month, @Param("year") String year, Integer status);

	@Query("SELECT DISTINCT " + "p.id AS partyId, " + "p.nameEnglish AS partyName, "
			+ "et.nameEnglish AS eventTypeName, " + "e.eventStartDateTime AS startDateTime, "
			+ "e.eventEndDateTime AS endDateTime, " + "p.opbDate AS opbDate, " + "p.opb AS opb "
			+ "FROM PartyMasterEntity p " + "JOIN p.contact cc " + "JOIN cc.contactType ct "
			+ "LEFT JOIN EventMasterEntity e ON e.party.id = p.id " + "AND e.user.id = :userid "
			+ "AND e.eventStartDateTime = ( " + "    SELECT MAX(e2.eventStartDateTime) "
			+ "    FROM EventMasterEntity e2 " + "    WHERE e2.party.id = p.id " + "    AND e2.user.id = :userid "
			+ "    AND (:isAllStatus = true OR e2.status = 1) " + ") " + "LEFT JOIN e.eventType et "
			+ "WHERE ct.id = 1 " + "AND p.isDelete = false AND p.user.id = :userid")
	List<Object[]> findAllConfirmPartyByUser(Long userid, Boolean isAllStatus);

	@Query(value = "SELECT et.name_english FROM eventtype et JOIN events e ON et.event_type_id = e.event_type_id WHERE e.event_id = :eventId GROUP BY 1", nativeQuery = true)
	String getEventNameByEventId(Long eventId);

	@Query(value = "SELECT COUNT(*) FROM events where is_delete = FALSE AND  (:userId = -1 OR user_id = :userId)", nativeQuery = true)
	Integer getEventCountByUserId(Long userId);

	@Query("SELECT e, ef, et, f, p, v " + "FROM EventFunctionManagerAssignEntity efma "
			+ "JOIN EventMasterEntity e ON e.id = efma.eventId "
			+ "JOIN EventFunctionMasterEntity ef ON ef.id = efma.eventFunctionId " + "JOIN e.eventType et "
			+ "JOIN ef.function f " + "JOIN e.party p " + "LEFT JOIN e.venue v " + "WHERE efma.managerId = :managerId "
			+ "AND e.isDelete = FALSE")
	List<Object[]> getAllManagerEvents(@Param("managerId") Long managerId);

	@Query(value = "SELECT " + "e.event_id, " + "et.name_english AS event_name_english, "
			+ "et.name_hindi AS event_name_hindi, " + "et.name_gujarati AS event_name_gujarati, " +

			"p.name_english AS party_name_english, " + "p.name_hindi AS party_name_hindi, "
			+ "p.name_gujarati AS party_name_gujarati, " +

			"e.event_start_date_time, " + "e.event_end_date_time, " +

			"v.name_english AS venue_english, " + "v.name_hindi AS venue_hindi, "
			+ "v.name_gujarati AS venue_gujarati, " +

			"e.status, " +

			"ef.event_function_id, " + "f.name_english AS function_name_english, "
			+ "f.name_hindi AS function_name_hindi, " + "f.name_gujarati AS function_name_gujarati, " +

			"ef.function_venue, " + "ef.function_start_date_time, " + "ef.function_end_date_time, " + "ef.pax, " +

			"u.user_id, " + "CONCAT(u.first_name, ' ', u.last_name) AS manager_name " +

			"FROM events e " +

			"LEFT JOIN eventtype et " + "ON et.event_type_id = e.event_type_id " +

			"LEFT JOIN partymaster p " + "ON p.party_id = e.party_id " +

			"LEFT JOIN venuemaster v " + "ON v.venue_id = e.venue_id " +

			"LEFT JOIN event_function ef " + "ON ef.event_id = e.event_id " + "AND ef.is_delete = false " +

			"LEFT JOIN functions f " + "ON f.function_id = ef.function_master_id " +

			"LEFT JOIN eventfunction_manager_assign efma " + "ON efma.eventfunction_id = ef.event_function_id " +

			"LEFT JOIN users u " + "ON u.user_id = efma.manager_id " +

			"WHERE e.event_id = :eventId " + "ORDER BY ef.sortorder ASC", nativeQuery = true)
	List<Object[]> getAllAssignFunctionByEvent(@Param("eventId") Long eventId);

	List<EventMasterEntity> findByBanquetHallIdAndIsDeleteFalse(Long banquetHallId);

	boolean existsByPartyAndIsDeleteFalse(PartyMasterEntity entity);

	@Modifying
	@Transactional
	@Query("UPDATE EventMasterEntity e SET e.childuserid = :childUserId WHERE e.id IN :eventIds")
	int assignEventsToChildUser(@Param("childUserId") Long childUserId, @Param("eventIds") List<Long> eventIds);

	@Query("SELECT new com.crmportal.response.dto.EventPartiesResponseDto(" + "	e.id, " + "	et.nameEnglish, "
			+ "	FUNCTION('DATE_FORMAT', e.eventStartDateTime, '%d/%m/%Y'), " + "	e.eventNo, " + "	p.id, "
			+ "	p.nameEnglish" + ") " + " FROM EventMasterEntity e " + " LEFT JOIN e.eventType et "
			+ " LEFT JOIN e.party p " + " WHERE e.user.id = :userId " + " AND e.isDelete = false "
			+ " ORDER BY e.id DESC")
	List<EventPartiesResponseDto> getAllPartiesWithEvent(@Param("userId") Long userId);

	List<EventMasterEntity> findByIdInAndIsDeleteFalse(List<Long> eventIds);

	@Query(value = " "
			+ "  SELECT "
			+ " 	e.inquiry_date, "
			+ " 	pm.name_english AS party_name, "
			+ " 	pm.mobileno, "
			+ " 	pm.address_english AS party_address, "
			+ " 	e.address, "
			+ " 	e.event_start_date_time, "
			+ " 	e.event_end_date_time, "
			+ " 	et.name_english AS event_name, "
			+ "		e.event_no "
			+ " FROM `events` e "
			+ " INNER JOIN partymaster pm "
			+ " 	ON e.party_id = pm.party_id "
			+ " INNER JOIN eventtype et "
			+ " 	ON et.event_type_id = e.event_type_id "
			+ " WHERE e.event_id = :eventId "
			+ " AND e.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getEventData(@Param("eventId") Long eventId);
}
