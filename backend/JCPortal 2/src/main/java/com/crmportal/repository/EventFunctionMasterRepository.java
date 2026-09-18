package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;

@Repository
public interface EventFunctionMasterRepository extends JpaRepository<EventFunctionMasterEntity, Long> {

	Optional<EventFunctionMasterEntity> findByIdAndIsDeleteFalse(Long eventFuncId);

	List<EventFunctionMasterEntity> findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(Long id);
	
	List<EventFunctionMasterEntity> findByEventId(Long eventId);

	List<EventFunctionMasterEntity> findAllByEventIdAndIsDeleteFalseOrderByFunctionStartDateTimeAscSortorderAsc(
			Long id);

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<EventFunctionMasterEntity> findAllByEventAndIsDeleteFalse(EventMasterEntity eventEntity);

	@Query(value = "SELECT " + " ef.event_function_id, " + " f.name_english  AS function_name_english, "
			+ " f.name_hindi    AS function_name_hindi, " + " f.name_gujarati AS function_name_gujarati, "
			+ " DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y') AS function_start_date_time, "
			+ " DATE_FORMAT(ef.function_end_date_time, '%d/%m/%Y') AS function_end_date_time, " + " e.event_id, "
			+ " et.name_english  AS eventtype_name_english, " + " et.name_hindi    AS eventtype_name_hindi, "
			+ " et.name_gujarati AS eventtype_name_gujarati, " + " e.event_start_date_time, e.event_end_date_time, "
			+ " p.party_id, " + " p.name_english  AS party_name_english, " + " p.name_hindi    AS party_name_hindi, "
			+ " p.name_gujarati AS party_name_gujarati, " + " e.event_no " + "FROM event_function ef "
			+ "JOIN functions f ON ef.function_master_id = f.function_id "
			+ "JOIN events e ON ef.event_id = e.event_id " + "JOIN eventtype et ON e.event_type_id = et.event_type_id "
			+ "LEFT JOIN partymaster p ON e.party_id = p.party_id "
			+ "WHERE ef.is_delete = false AND e.user_id = :userId " + "AND ( :search IS NULL OR ( "
			+ "     et.name_english LIKE %:search% " + "  OR f.name_english LIKE %:search% "
			+ "  OR p.name_english LIKE %:search% "
			+ "  OR DATE_FORMAT(e.event_start_date_time,'%Y-%m-%d') LIKE CONCAT('%', :search, '%') " + ") ) "
			+ "ORDER BY "
			+ "CASE "
			+ "  WHEN DATE(e.event_start_date_time) = CURDATE() THEN 0 "
			+ "  WHEN DATE(e.event_start_date_time) > CURDATE() THEN 1 "
			+ "  ELSE 2 "
			+ "END, "
			+ "DATE(e.event_start_date_time), "
			+ "ef.function_start_date_time, "
			+ "ef.sortorder "
			+ "LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<Object[]> findAllEventFunctionsNative(@Param("search") String search, @Param("limit") int limit,
			@Param("offset") int offset, @Param("userId") Long userId);

	@Query(value = "SELECT COUNT(*) " + "FROM event_function ef "
			+ "JOIN functions f ON ef.function_master_id = f.function_id "
			+ "JOIN events e ON ef.event_id = e.event_id " + "JOIN eventtype et ON e.event_type_id = et.event_type_id "
			+ "LEFT JOIN partymaster p ON e.party_id = p.party_id "
			+ "WHERE ef.is_delete = false AND e.user_id = :userId " + "AND ( :search IS NULL OR ( "
			+ "     et.name_english LIKE %:search% " + "  OR f.name_english LIKE %:search% "
			+ "  OR p.name_english LIKE %:search% " + "  OR DATE(e.event_start_date_time) LIKE %:search% "
			+ ") ) ", nativeQuery = true)
	long countAllEventFunctions(@Param("search") String search, @Param("userId") Long userId);

	List<EventFunctionMasterEntity> findAllByEventIdAndIsDeleteFalse(Long id);

	@Query(" SELECT "
			+ " 	SUM(efm.pax) "
			+ " FROM EventFunctionMasterEntity efm "
			+ " WHERE efm.event.id = :eventId ")
	Integer getEventTotalPerson(@Param("eventId") Long eventId);
	
	List<EventFunctionMasterEntity> findByIdIn(List<Long> ids);
	
	@Query(value =
	        "SELECT " +
	        "ef.event_function_id AS event_function_id, " +
	        "e.event_id AS event_id, " +
	        "e.user_id AS user_id, " +
	        "p.party_id AS party_id, " +
	        "f.function_id AS function_id, ef.rate AS price, " +

	        "p.name_english AS party_name_english, " +
	        "p.name_hindi AS party_name_hindi, " +
	        "p.name_gujarati AS party_name_gujarati, " +

	        "f.name_english AS function_name_english, " +
	        "f.name_hindi AS function_name_hindi, " +
	        "f.name_gujarati AS function_name_gujarati, " +

	        "e.event_start_date_time AS event_start_date_time, " +
	        "ef.function_start_date_time AS function_start_date_time, " +
	        "ef.function_end_date_time AS function_end_date_time " +

	        "FROM event_function ef " +
	        "INNER JOIN events e ON ef.event_id = e.event_id " +
	        "INNER JOIN functions f ON ef.function_master_id = f.function_id " +
	        "LEFT JOIN partymaster p ON e.party_id = p.party_id " +

	        "WHERE ef.is_delete = false " +
	        "AND e.is_delete = false " +
	        "AND e.user_id = :userId " +
	        "AND f.function_id = :functionId " +

	        "ORDER BY e.event_start_date_time DESC, " +
	        "ef.function_start_date_time DESC " +
	        "LIMIT :maxFunctionsCount",
	        nativeQuery = true)
	List<Object[]> findFilteredEventFunctions(
	        @Param("userId") Long userId,
	        @Param("functionId") Long functionId,
	        @Param("maxFunctionsCount") Integer maxFunctionsCount);
	
	@Query(value =
	        "SELECT " +
	        "ef.event_function_id AS event_function_id, " +
	        "e.event_id AS event_id, " +
	        "e.user_id AS user_id, " +
	        "p.party_id AS party_id, " +
	        "f.function_id AS function_id, ef.rate AS price, " +

	        "p.name_english AS party_name_english, " +
	        "p.name_hindi AS party_name_hindi, " +
	        "p.name_gujarati AS party_name_gujarati, " +

	        "f.name_english AS function_name_english, " +
	        "f.name_hindi AS function_name_hindi, " +
	        "f.name_gujarati AS function_name_gujarati, " +

	        "e.event_start_date_time AS event_start_date_time, " +
	        "ef.function_start_date_time AS function_start_date_time, " +
	        "ef.function_end_date_time AS function_end_date_time " +

	        "FROM event_function ef " +
	        "INNER JOIN events e ON ef.event_id = e.event_id " +
	        "INNER JOIN functions f ON ef.function_master_id = f.function_id " +
	        "LEFT JOIN partymaster p ON e.party_id = p.party_id " +

	        "WHERE ef.is_delete = false " +
	        "AND e.is_delete = false " +
	        "AND e.user_id = :userId " +
	        "AND f.function_id = :functionId " +
	        "AND ef.rate BETWEEN :fromPrice AND :toPrice " +

	        "ORDER BY e.event_start_date_time DESC, " +
	        "ef.function_start_date_time DESC " +
	        "LIMIT :maxFunctionsCount",
	        nativeQuery = true)
	List<Object[]> findFilteredEventFunctionsByPrice(
	        @Param("userId") Long userId,
	        @Param("functionId") Long functionId,
	        @Param("fromPrice") Integer fromPrice,
	        @Param("toPrice") Integer toPrice,
	        @Param("maxFunctionsCount") Integer maxFunctionsCount);

	List<EventFunctionMasterEntity> findAllByEventAndIsDeleteFalseOrderBySortorderAsc(EventMasterEntity event);

	List<EventFunctionMasterEntity> findByEventAndIdInAndIsDeleteFalseOrderBySortorderAsc(EventMasterEntity event,
			List<Long> eventFunctionIds);
}
