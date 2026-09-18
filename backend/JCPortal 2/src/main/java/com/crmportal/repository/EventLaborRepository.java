package com.crmportal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.crmportal.entity.EventLaborEntity;
import com.crmportal.response.dto.EventLaborDetailsResponseDto;

@Repository
public interface EventLaborRepository extends JpaRepository<EventLaborEntity, Long> {

	@Modifying
	@Transactional
	@Query("DELETE FROM EventLaborEntity erf WHERE erf.event.id = :eventId AND erf.eventFunction.id = :eventFunctionId")
	void deleteByEventIdAndEventFunctionId(Long eventId, Long eventFunctionId);

	@Modifying
	@Transactional
	@Query("DELETE FROM EventLaborEntity erf WHERE erf.event.id = :eventId")
	void deleteByEventId(Long eventId);

	List<EventLaborEntity> findByEvent_IdAndEventFunction_Id(Long eventId, Long eventFunctionId);

	List<EventLaborEntity> findByEvent_IdAndEventFunction_IdAndContact_Id(Long eventId, Long eventFunctionId,
			Long partyId);

	@Query("SELECT COALESCE(SUM(e.totalprice), 0) FROM EventLaborEntity e WHERE e.event.id = :eventId AND e.eventFunction.id = :eventFunctionId")
	Double getTotalLaborPrice(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT COALESCE(SUM(e.totalprice), 0) FROM EventLaborEntity e WHERE e.event.id = :eventId")
	Double getTotalLaborPrice(@Param("eventId") Long eventId);

	@Query("SELECT COALESCE(SUM(e.totalprice), 0) FROM EventLaborEntity e WHERE e.event.user.id = :userId")
	Double getTotalLaborPriceByUser(@Param("userId") Long userId);

	@Query("SELECT COALESCE(SUM(e.totalprice), 0) FROM EventLaborEntity e WHERE e.event.user.id = :userId AND e.event.eventStartDateTime BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	Double getTotalLaborPriceByUserAndDate(@Param("userId") Long userId,
			@Param("givenDateTime") LocalDateTime givenDateTime);

	Optional<EventLaborEntity> findById(Long id);

	boolean existsById(Long id);

	void deleteById(Long id);

//	@Query(value = "SELECT DISTINCT el.party_id, el.event_id, el.event_function_id, "
//			+ " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS labour_name, "
//			+ " pm.mobileno, " + " el.labordatetime AS labor_date, " + " el.place AS venue, " + " ud.company_name, "
//			+ " ud.country_code, " + " ud.office_no, " + " ud.company_email," + " u.logo, "
//			+ " CASE WHEN UPPER(el.place) LIKE 'AT VENUE' THEN ef.function_venue WHEN :lang = 0 THEN ug.address_english WHEN :lang = 1 THEN ug.address_hindi WHEN :lang = 2 THEN ug.address_gujarati END AS address, "
//			+ " ef.function_start_date_time, el.qty AS qty, cc.contact_category_id, cc.name_english AS contact_category_name_english,"
//			+ " cc.name_gujarati AS contact_category_name_gujarati, cc.name_hindi AS contact_category_name_hindi, el.laborshift AS labor_shift, "
//			+ " FROM event_labor el " + " LEFT JOIN event_function ef ON ef.event_function_id = el.event_function_id "
//			+ " el.laborshift AS labor_shift, el.price, el.totalprice, e.event_start_date_time "
//			+ " FROM event_labor el " 
//			+ " LEFT JOIN event_function ef ON ef.event_function_id = el.event_function_id "
//			+ " LEFT JOIN partymaster pm ON pm.party_id = el.party_id "
//			+ " LEFT JOIN events e ON e.event_id = el.event_id " + " LEFT JOIN users u ON u.user_id = e.user_id "
//			+ " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
//			+ " LEFT JOIN user_godown ug ON ug.id = el.place "
//			+ " LEFT JOIN contact_category cc ON cc.contact_category_id = el.contact_category_id "
//			+ " WHERE el.event_id = :eventId AND (:eventFunctionId = -1 OR el.event_function_id = :eventFunctionId) "
//			+ " AND el.party_id IN (:agencyId) ", nativeQuery = true)
//	List<Object[]> getLaborEventwise(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId,
//			@Param("lang") int lang, @Param("agencyId") List<Long> agencyId);

	@Query(value = "SELECT DISTINCT el.party_id, el.event_id, el.event_function_id, "
			+ " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS labour_name, "
			+ " pm.mobileno, " + " el.labordatetime AS labor_date, " + " el.place AS venue, " + " ud.company_name, "
			+ " ud.country_code, " + " ud.office_no, " + " ud.company_email," + " u.logo, "
			+ " CASE "
			+ " WHEN UPPER(el.place) IN ('0', 'AT VENUE', 'VENUE') THEN "
			+ "   CASE "
			+ "     WHEN :lang = 0 THEN ef.function_venue "
			+ "     WHEN :lang = 1 THEN ef.function_venue_hindi "
			+ "     WHEN :lang = 2 THEN ef.function_venue_gujarati "
			+ "   END "
			+ " ELSE "
			+ "   CASE "
			+ "     WHEN :lang = 0 THEN ug.address_english "
			+ "     WHEN :lang = 1 THEN ug.address_hindi "
			+ "     WHEN :lang = 2 THEN ug.address_gujarati "
			+ "   END "
			+ " END AS address, "
			+ " ef.function_start_date_time, el.qty AS qty, cc.contact_category_id, cc.name_english AS contact_category_name_english,"
			+ " cc.name_gujarati AS contact_category_name_gujarati, cc.name_hindi AS contact_category_name_hindi,  "
			+ " el.laborshift AS labor_shift, el.price, el.totalprice, e.event_start_date_time, "
			+ " f.name_english AS function_name_english, f.name_hindi AS function_name_hindi, f.name_gujarati AS function_name_gujarati, "
			+ " et.name_english AS event_name_english, et.name_hindi AS event_name_hindi, et.name_gujarati AS event_name_gujarati, el.shift_trans_price	 "
			+ " FROM event_labor el " + " LEFT JOIN event_function ef ON ef.event_function_id = el.event_function_id "
			+ " LEFT JOIN partymaster pm ON pm.party_id = el.party_id "
			+ " LEFT JOIN `events` e ON e.event_id = el.event_id " + " LEFT JOIN users u ON u.user_id = e.user_id "
			+ " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
			+ " LEFT JOIN user_godown ug ON ug.id = CAST(el.place AS UNSIGNED) "
			+ " LEFT JOIN contact_category cc ON cc.contact_category_id = el.contact_category_id "
			+ " LEFT JOIN functions f on ef.function_master_id = f.function_id "
			+ " LEFT JOIN eventtype et on e.event_type_id = et.event_type_id "
			+ " WHERE el.event_id = :eventId AND (:eventFunctionId = -1 OR el.event_function_id = :eventFunctionId) "
			+ " AND el.party_id IN (:agencyId) AND ef.is_delete = false AND e.is_delete = false ", nativeQuery = true)
	List<Object[]> getLaborEventwise(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId,
			@Param("lang") int lang, @Param("agencyId") List<Long> agencyId);

	@Query(value = "SELECT DISTINCT el.party_id, el.event_id, el.event_function_id, "
			+ " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS labour_name, "
			+ " pm.mobileno, " + " el.labordatetime, "
//			+ " el.place AS venue, " 
			+ " CASE WHEN :lang = 0 THEN ug.address_english WHEN :lang = 1 THEN ug.address_hindi WHEN :lang = 2 THEN ug.address_gujarati END AS address, "
			+ " el.contact_category_id, "
			+ " CASE WHEN :lang = 0 THEN cc.name_english WHEN :lang = 1 THEN cc.name_hindi WHEN :lang = 2 THEN cc.name_gujarati END AS contact_category_name, "
			+ " el.price, " + " el.qty, " + " el.totalprice, " + " pm.user_id, " + " ud.company_name, "
			+ " ud.country_code, " + " ud.office_no, " + " ud.company_email, " + " ef.function_venue, "
			+ " ef.function_start_date_time, el.laborshift AS labour_shift, el.shift_trans_price "
			+ " FROM event_labor el " + " LEFT JOIN event_function ef ON ef.event_function_id = el.event_function_id "
			+ " LEFT JOIN partymaster pm ON pm.party_id = el.party_id "
			+ " LEFT JOIN events e ON e.event_id = el.event_id "
			+ " LEFT JOIN contact_category cc ON cc.contact_category_id = el.contact_category_id "
			+ " LEFT JOIN users u ON u.user_id = e.user_id "
			+ " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
			+ " LEFT JOIN user_godown ug ON ug.id = el.place "
			+ " WHERE el.event_id = :eventId AND el.event_function_id = :eventFunctionId AND el.party_id = :partyId ", nativeQuery = true)
	List<Object[]> getLaborDetailEventwise(Long eventId, Long eventFunctionId, int lang, Long partyId);

	@Query(value = "SELECT DISTINCT el.event_id, " + " el.party_id, "
			+ " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS party_name, "
			+ " el.event_function_id, "
			+ " CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS function_name, "
			+ " ef.function_venue, " + " e.event_no , el.shift_trans_price,ef.function_venue_hindi, ef.function_venue_gujarati " + " FROM event_labor el "
			+ " LEFT JOIN events e ON el.event_id = e.event_id "
			+ " LEFT JOIN partymaster pm ON pm.party_id = e.party_id "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = el.event_function_id "
			+ " LEFT JOIN functions f ON f.function_id = ef.function_master_id "
			+ " WHERE (:eventId = -1 OR el.event_id = :eventId) AND (:eventFunctionId = -1 OR el.event_function_id = :eventFunctionId) "
			+ " AND ((:startDate IS NULL AND :endDate IS NULL) OR (e.event_start_date_time BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y'))) "
			+ " AND el.party_id IN (:agencyId) ", nativeQuery = true)
	List<Object[]> getEventData(Long eventId, Long eventFunctionId, String startDate, String endDate, int lang,
			List<Long> agencyId);

	@Query(value = "SELECT DISTINCT el.event_id, " + " el.event_function_id, " + " el.party_id, "
			+ " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS party_name, "
			+ " pm.mobileno, el.shift_trans_price " + " FROM event_labor el "
			+ " LEFT JOIN events e on e.event_id = el.event_id "
			+ " LEFT JOIN partymaster pm ON pm.party_id = el.party_id "
			+ " WHERE (:eventId = -1 OR el.event_id = :eventId) AND (:functionId = -1 OR el.event_function_id = :functionId) AND el.party_id = :partyId  "
			+ " AND ((:startDate IS NULL AND :endDate IS NULL) OR (e.event_start_date_time BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y'))) ", nativeQuery = true)
	List<Object[]> getLaborData(Long eventId, Long functionId, String startDate, String endDate, int lang,
			Long partyId);

	@Query(value = "SELECT DISTINCT e.event_id, e.event_no, u.user_id, ud.company_name, ud.company_email, ud.country_code, ud.office_no, ud.address, u.logo, e.event_start_date_time "
			+ " FROM `events` e " + " LEFT JOIN users u ON u.user_id = e.user_id "
			+ " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
			+ " WHERE e.event_id = :eventId ", nativeQuery = true)
	Object getCmpAndEventData(Long eventId);

	@Query(value = "SELECT DISTINCT u.user_id, ud.company_name, ud.company_email, ud.country_code, ud.office_no, ud.address, u.logo "
			+ " FROM users u " + " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
			+ " WHERE u.user_id = :userid ", nativeQuery = true)
	Object getCmpData(Long userid);

	@Query(value = "SELECT DISTINCT el.event_id, " + " el.event_function_id, " + " el.labor_id, " + " el.party_id, "
			+ " el.contact_category_id, " + " CASE WHEN :lang = 0 THEN cc.name_english "
			+ "      WHEN :lang = 1 THEN cc.name_hindi "
			+ "      WHEN :lang = 2 THEN cc.name_gujarati END AS contact_category_name, "
			+ " DATE_FORMAT(el.labordatetime, '%d/%m/%Y %h:%i %p') AS labor_date_time, "
			+ " CASE WHEN :lang = 0 THEN s.name_english " + "      WHEN :lang = 1 THEN s.name_hindi "
			+ "      WHEN :lang = 2 THEN s.name_gujarati END AS laborshift, " + " el.qty, "
			+ " CASE WHEN :lang = 0 THEN el.notes_english " + "      WHEN :lang = 1 THEN el.notes_hindi "
			+ "      WHEN :lang = 2 THEN el.notes_gujarati END AS notes, "
			+ " el.price, el.totalprice, el.shift_trans_price " + " FROM event_labor el "
			+ " LEFT JOIN events e on e.event_id = el.event_id "
			+ " LEFT JOIN contact_category cc ON cc.contact_category_id = el.contact_category_id "
			+ " LEFT JOIN shift s ON ( " + "   s.name_english  = CONVERT(el.laborshift USING utf8) "
			+ "   OR s.name_hindi = CONVERT(el.laborshift USING utf8) "
			+ "   OR s.name_gujarati = CONVERT(el.laborshift USING utf8) " + " )"
			+ " AND s.user_id = :userId "
			+ " WHERE (:eventId = -1 OR el.event_id = :eventId) "
			+ " AND (:functionId = -1 OR el.event_function_id = :functionId) "
			+ " AND ((:startDate IS NULL AND :endDate IS NULL) OR (e.event_start_date_time BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y'))) "
			+ " AND el.party_id = :pId", nativeQuery = true)
	List<Object[]> getLaborShiftData(Long eventId, Long functionId, String startDate, String endDate, Long pId,
			int lang, Long userId);

	@Query(value =
		    "SELECT " +
		    " t.party_id, " +
		    " t.agency_name, " +
		    " t.labordatetime, " +
		    " t.address, " +
		    " t.contact_category_id, " +
		    " t.contact_category_name, " +
		    " t.labor_date, " +
		    " t.labor_time, " +
		    " t.laborshift, " +
		    " t.qty, " +
		    " t.price, " +
		    " t.totalprice, " +
		    " t.notes, " +
		    " t.mobileno, " +
		    " t.shift_trans_price, " +
		    " t.event_function_id, " +
		    " t.event_id " +
		    "FROM ( " +
		    "   SELECT DISTINCT " +
		    "       el.party_id, " +
		    "       CASE WHEN :lang = 0 THEN pm.name_english " +
		    "            WHEN :lang = 1 THEN pm.name_hindi " +
		    "            WHEN :lang = 2 THEN pm.name_gujarati END AS agency_name, " +
		    "       el.labordatetime, " +
		    "       CASE " +
		    "            WHEN UPPER(el.place) LIKE 'AT VENUE' THEN " +
		    "                CASE " +
		    " 				 	 WHEN :lang = 0 THEN ef.function_venue " +
		    "                    WHEN :lang = 1 THEN ef.function_venue_hindi " +
		    "                    WHEN :lang = 2 THEN ef.function_venue_gujarati " +
		    "                END " +
		    "            WHEN :lang = 0 THEN ug.address_english " +
		    "            WHEN :lang = 1 THEN ug.address_hindi " +
		    "            WHEN :lang = 2 THEN ug.address_gujarati " +
		    "       END AS address, " + 
		    "       el.contact_category_id, " +
		    "       CASE WHEN :lang = 0 THEN cc.name_english " +
		    "            WHEN :lang = 1 THEN cc.name_hindi " +
		    "            WHEN :lang = 2 THEN cc.name_gujarati END AS contact_category_name, " +
		    "       DATE_FORMAT(el.labordatetime,'%d/%m/%Y') AS labor_date, " +
		    "       DATE_FORMAT(el.labordatetime,'%h:%i %p') AS labor_time, " +
		    "       CASE WHEN :lang = 0 THEN s.name_english " +
		    "            WHEN :lang = 1 THEN s.name_hindi " +
		    "            WHEN :lang = 2 THEN s.name_gujarati END AS laborshift, " +
		    "       el.qty, " +
		    "       el.price, " +
		    "       el.totalprice, " +
		    "       CASE WHEN :lang = 0 THEN el.notes_english " +
		    "            WHEN :lang = 1 THEN el.notes_hindi " +
		    "            WHEN :lang = 2 THEN el.notes_gujarati END AS notes, " +
		    "       pm.mobileno, " +
		    "       el.shift_trans_price, " +
		    "       el.sort_order, " +
		    "       el.labor_id, " +
		    "       ef.event_function_id, "+
		    "       e.event_id " +
		    "   FROM event_labor el " +
		    "   JOIN events e ON e.event_id = el.event_id " +
		    "   LEFT JOIN partymaster pm ON pm.party_id = el.party_id " +
		    "   LEFT JOIN event_function ef ON ef.event_function_id = el.event_function_id " +
		    "   LEFT JOIN contact_category cc ON cc.contact_category_id = el.contact_category_id " +
		    "   LEFT JOIN user_godown ug ON ug.id = el.place " +
		    "   LEFT JOIN shift s ON ( " +
		    "       s.name_english = CONVERT(el.laborshift USING utf8) " +
		    "       OR s.name_hindi = CONVERT(el.laborshift USING utf8) " +
		    "       OR s.name_gujarati = CONVERT(el.laborshift USING utf8) ) " +
		    "   WHERE " +
		    " 	 	e.event_start_date_time >= STR_TO_DATE(:startDate, '%d/%m/%Y') " +
		    "		AND e.event_start_date_time < DATE_ADD( " +
		    "    		STR_TO_DATE(:endDate, '%d/%m/%Y'), " +
		    "    		INTERVAL 1 DAY " +
		    "		) " +
		    "     AND (:flag = 0 OR el.party_id IN (:agencyId)) " +
		    "     AND e.is_delete = FALSE " +
		    "     AND ef.is_delete = FALSE " +
		    "     AND cc.is_delete = FALSE " +
		    "     AND (:partyId = -1 OR e.party_id = :partyId) " +
		    "     AND e.user_id = :userId " +
		    ") t " +
		    "ORDER BY t.sort_order, t.labor_id, t.party_id, t.labordatetime",
		    nativeQuery = true)
		List<Object[]> getLaborDatawise(String startDate,
		                                String endDate,
		                                int lang,
		                                Integer flag,
		                                List<Long> agencyId,
		                                Long partyId,
		                                Long userId);

	@Query(value = "SELECT DISTINCT "
	        + "p.party_id AS partyId, "
	        + "p.name_english AS partyName, "
	        + "et.name_english AS eventTypeName, "
	        + "e.event_start_date_time AS startDateTime, "
	        + "e.event_end_date_time AS endDateTime, "
	        + "p.opb_date AS opbDate, "
	        + "p.opb AS opb "
	        + "FROM partymaster p "
	        + "JOIN contact_category cc ON p.contact_category_id = cc.contact_category_id "
	        + "JOIN contacttype ct ON cc.contact_type_id = ct.contact_type_id "
	        + "LEFT JOIN event_labor el "
	        + "       ON el.party_id = p.party_id "
	        + "LEFT JOIN events e "
	        + "       ON el.event_id = e.event_id "
	        + "       AND e.user_id = :userid "
	        + "       AND (:isAllStatus = true OR e.status = 1) "
	        + "       AND e.event_start_date_time = ( "
	        + "           SELECT MAX(e2.event_start_date_time) "
	        + "           FROM event_labor el2 "
	        + "           JOIN events e2 ON el2.event_id = e2.event_id "
	        + "           WHERE el2.party_id = p.party_id "
	        + "           AND e2.user_id = :userid "
	        + "           AND (:isAllStatus = true OR e2.status = 1) "
	        + "       ) "
	        + "LEFT JOIN eventtype et "
	        + "       ON e.event_type_id = et.event_type_id "
	        + "WHERE ct.contact_type_id IN (2) "
	        + "AND p.is_delete = false AND p.user_id = :userid",
	        nativeQuery = true)
	List<Object[]> findAllLabourPartyByUser(Long userid, Boolean isAllStatus);

	@Query(value = "SELECT COUNT(DISTINCT e.event_id) " + "FROM event_labor el "
			+ "JOIN events e ON el.event_id = e.event_id " + "WHERE e.is_delete = false "
			+ "AND (:userId = -1 OR e.user_id = :userId)", nativeQuery = true)
	Integer getLabourCount(Long userId);

	@Query("SELECT e "+
			"FROM EventLaborEntity e "+
			"WHERE e.event.id = :eventId "+
			"AND e.eventFunction.id = :eventFunctionId ")
		List<EventLaborEntity> findByEventIdAndFunctionId(
				@Param("eventId") Long eventId,
				@Param("eventFunctionId") Long eventFunctionId);

	List<EventLaborEntity> findByEvent_IdAndEventFunction_IdOrderBySortOrderAsc(Long eventId, Long eventFunctionId);

}
