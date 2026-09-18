package com.crmportal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionMenuAllocationEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.MenuAllocationOrdersEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.response.dto.AgencyWiseItemResponseDto;
import com.crmportal.response.dto.CompanyDetailsResponseDto;
import com.crmportal.response.dto.MenuAllocationItemsResponseDto;

@Repository
public interface EventFunctionMenuAllocationRepository extends JpaRepository<EventFunctionMenuAllocationEntity, Long> {

	Optional<EventFunctionMenuAllocationEntity> findByIdAndIsDeleteFalse(Long id);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndEventFunctionAndIsDeleteFalse(
			EventMasterEntity eventEntity, EventFunctionMasterEntity eventFunctionEntity);

	EventFunctionMenuAllocationEntity findByMenuItem_IdAndEventFunctionAndIsDeleteFalse(Long menuItemId,
			EventFunctionMasterEntity eventFunctionEntity);

	EventFunctionMenuAllocationEntity findByMenuItemAndEventFunction(MenuItemMasterEntity menuItem,
			EventFunctionMasterEntity eventFunction);

	@Query("SELECT DISTINCT mao" + " FROM MenuAllocationOrdersEntity mao" + " JOIN FETCH mao.party p"
			+ " JOIN FETCH mao.menuAllocation ma " + " JOIN FETCH ma.event e" + " JOIN FETCH ma.menuItem mi"
			+ " JOIN FETCH ma.menuCategory mc" + " WHERE ( :eventId = -1L OR e.id = :eventId) "
			+ " AND ( :eventFunctionId = -1L OR ma.eventFunction.id = :eventFunctionId )" + " AND ("
			+ "        (:type = 'outside' AND ma.outside = true)" + "     OR (:type = 'chef' AND ma.chefLabour = true)"
			+ "     OR (:type = 'inside' AND ma.inside = true)" + "  ) "
			+ " AND ((:startDate IS NULL AND :endDate IS NULL) OR (e.eventStartDateTime BETWEEN :startDate AND :endDate)) "
			+ " AND (:isAgencyWise = FALSE OR p.id IN :agencyId) " + " AND (:isItemWise = FALSE OR mi.id IN :itemId) "
			+ "  AND ma.isDelete = false" + "  AND mao.isDelete = false")
	List<MenuAllocationOrdersEntity> fetchAllAllocationData(@Param("type") String type, @Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("agencyId") List<Long> agencyId,
			@Param("itemId") List<Long> itemId, @Param("isAgencyWise") Boolean isAgencyWise,
			@Param("isItemWise") Boolean isItemWise);

	@Query("SELECT mao" + " FROM MenuAllocationOrdersEntity mao" + " JOIN FETCH mao.party p"
			+ " JOIN FETCH mao.menuAllocation ma" + " JOIN FETCH ma.menuItem mi" + " JOIN FETCH ma.menuCategory mc"
			+ " WHERE ma.event.id = :eventId "
			+ " AND ( :eventFunctionId = -1L OR ma.eventFunction.id = :eventFunctionId )" + " AND ("
			+ "        (:type = 'outside' AND ma.outside = true)" + "     OR (:type = 'chef' AND ma.chefLabour = true)"
			+ "     OR (:type = 'inside' AND ma.inside = true)" + "  ) " + "  AND ma.isDelete = false"
			+ "  AND mao.isDelete = false")
	List<MenuAllocationOrdersEntity> fetchAllAllocationData(@Param("type") String type, @Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT new com.crmportal.response.dto.AgencyWiseItemResponseDto(" + "   mi.nameEnglish, "
			+ "   mi.nameHindi, " + "   mi.nameGujarati, " + "   mao.pax" + ") "
			+ "FROM MenuAllocationOrdersEntity mao " + "JOIN mao.party p " + "JOIN mao.menuAllocation ma "
			+ "JOIN ma.menuItem mi " + "WHERE ma.event.id = :eventId "
			+ "AND (:eventFunctionId = -1L OR ma.eventFunction.id = :eventFunctionId) " + "AND mao.id = :partyId "
			+ "AND ( " + "      (:type = 'outside' AND ma.outside = true) "
			+ "   OR (:type = 'chef' AND ma.chefLabour = true) " + "   OR (:type = 'inside' AND ma.inside = true) "
			+ ") " + "AND ma.isDelete = false " + "AND mao.isDelete = false")
	List<AgencyWiseItemResponseDto> fetchAllAllocationDataPartyWise(@Param("type") String type,
			@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId,
			@Param("partyId") Long partyId);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndEventFunctionAndIsDeleteFalseAndInsideTrue(
			EventMasterEntity eventEntity, EventFunctionMasterEntity eventFunctionEntity);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndEventFunctionAndIsDeleteFalseAndOutsideTrue(
			EventMasterEntity eventEntity, EventFunctionMasterEntity eventFunctionEntity);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndEventFunctionAndIsDeleteFalseAndChefLabourTrue(
			EventMasterEntity eventEntity, EventFunctionMasterEntity eventFunctionEntity);

	@Query(value = "SELECT " + "COALESCE(ud.company_name, '') AS companyName, "
			+ "COALESCE(ud.country_code, '') AS countryCode, " + "COALESCE(ud.company_email, '') AS companyEmail, "
			+ "COALESCE(ud.office_no, '') AS officeNo, " + "COALESCE(ud.address, '') AS address, "
			+ "COALESCE(u.logo, '') AS logo " + "FROM users u "
			+ "LEFT JOIN user_basic_details ud ON u.user_id = ud.user_id "
			+ "WHERE u.user_id = :userId", nativeQuery = true)
	Optional<Object[]> getCompanyDetailsByUserId(@Param("userId") Long userId);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndIsDeleteFalse(EventMasterEntity eventEntity);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndIsDeleteFalseAndInsideTrue(EventMasterEntity eventEntity);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndIsDeleteFalseAndOutsideTrue(EventMasterEntity eventEntity);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndIsDeleteFalseAndChefLabourTrue(
			EventMasterEntity eventEntity);

	@Modifying
	@Query(value = "DELETE FROM eventfunction_menuallocation " + "WHERE menu_item_id = :menuItemId "
			+ "AND event_id = :eventId " + "AND eventfunction_id = :eventFunctionId", nativeQuery = true)
	void deleteMenuAllocations(@Param("menuItemId") Long menuItemId, @Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Modifying
	@Query(value = "DELETE FROM eventfunction_menuallocation " + "WHERE " + " event_id = :eventId "
			+ "AND eventfunction_id = :eventFunctionId", nativeQuery = true)
	void deleteMenuAllocationsByEventAndEventFunction(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query(value = "SELECT * FROM eventfunction_menuallocation " + "WHERE menu_item_id = :menuItemId "
			+ "AND eventfunction_id = :eventFunctionId", nativeQuery = true)
	EventFunctionMenuAllocationEntity getData(Long menuItemId, Long eventFunctionId);

	boolean existsByEvent_Id(Long eventId);

	@Query(value = "SELECT pm.party_id,pm.name_english AS agency_name_english, "
			+ "  pm.name_hindi AS agency_name_hindi," + "  pm.name_gujarati AS agency_name_gujarati, "
			+ "  ef.function_start_date_time, " + "  ef.function_venue, " + "  efmao.counter_quantity, "
			+ "  efmao.helper_quantity, " + "  efmao.counter_price, " + "  efmao.helper_price, "
			+ "  efmao.service_type, " + "  efmao.quantity, " + "  efmao.price, "
			+ "  mi.name_english AS item_name_english, " + "  mi.name_hindi AS item_name_hindi, "
			+ "  mi.name_gujarati AS item_name_gujarati, " + "  efmao.total_price, "
			+ "  pm.mobileno, u.unit_id, u.name_english AS unit_name_english, u.name_hindi AS unit_name_hindi, u.name_gujarati AS unit_name_gujarati,"
			+ "  pm1.name_english AS party_name_english," + "  pm1.name_hindi AS party_name_hindi,"
			+ "  pm1.name_gujarati AS party_name_gujarati, efma.person_count AS person_count, ef.event_function_id, e.event_id " + " FROM eventfunction_menuallocation efma "
			+ " LEFT JOIN eventfunction_menuallocation_order efmao ON efmao.menu_allocation_id = efma.menu_allocation_id "
			+ " LEFT JOIN `events` e ON e.event_id = efma.event_id "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = efma.eventfunction_id "
			+ " JOIN partymaster pm ON pm.party_id = efmao.party_id "
			+ " LEFT JOIN partymaster pm1 ON pm1.party_id = e.party_id"
			+ " LEFT JOIN memuitems mi ON mi.menu_item_id = efma.menu_item_id "
			+ " LEFT JOIN units u ON u.unit_id = efmao.unit_id "
			+ "WHERE ef.function_start_date_time BETWEEN :startDate AND :endDate " + "AND e.is_delete = FALSE "
			+ "AND ef.is_delete = FALSE " + "AND efma.is_delete = FALSE " + "AND efmao.is_delete = FALSE "
			+ "AND efma.user_id = :userId " + "AND ( " + "     (:type = 'chef' AND efma.chef_labour = TRUE) OR "
			+ "     (:type = 'outside' AND efma.outside = TRUE) OR " + "     (:type = 'inside' AND efma.inside = TRUE) "
			+ ") " + "AND ( :flag = 0 OR pm.party_id IN (:agencyIds) ) "
			+ "AND (:partyId = -1 OR pm1.party_id = :partyId) "
			+ "ORDER BY pm.party_id ASC, ef.function_start_date_time ASC, ef.function_venue", nativeQuery = true)
	List<Object[]> getDataForDatewiseReport(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("type") String type,
			@Param("agencyIds") List<Long> agencyIds, @Param("flag") Integer flag, @Param("partyId") Long partyId);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndEventFunctionAndIsDeleteFalseOrderByMenuCategorySortOrderAscMenuitemSortOrderAsc(
			EventMasterEntity eventEntity, EventFunctionMasterEntity function);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndEventFunctionAndIsDeleteFalseAndInsideTrueOrderByMenuCategorySortOrderAscMenuitemSortOrderAsc(
			EventMasterEntity eventEntity, EventFunctionMasterEntity function);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndEventFunctionAndIsDeleteFalseAndOutsideTrueOrderByMenuCategorySortOrderAscMenuitemSortOrderAsc(
			EventMasterEntity eventEntity, EventFunctionMasterEntity function);

	List<EventFunctionMenuAllocationEntity> findAllByEventAndEventFunctionAndIsDeleteFalseAndChefLabourTrueOrderByMenuCategorySortOrderAscMenuitemSortOrderAsc(
			EventMasterEntity eventEntity, EventFunctionMasterEntity function);

	EventFunctionMenuAllocationEntity findByMenuItem_IdAndMenuCategory_IdAndEventFunctionAndIsDeleteFalse(
			Long menuItemId, Long menuCategoryId, EventFunctionMasterEntity eventFunctionEntity);

	EventFunctionMenuAllocationEntity findByMenuItem_IdAndEventFunction_IdAndIsDeleteFalse(Long menuItemId, Long id);

	List<EventFunctionMenuAllocationEntity> findAllByIdInAndIsDeleteFalse(Set<Long> allocationIds);

	@Query("SELECT e FROM EventFunctionMenuAllocationEntity e " + "WHERE e.event = :event "
			+ "AND e.eventFunction = :function " + "AND e.isDelete = false " + "AND ( " + "      :type IS NULL OR "
			+ "      (:type = 'inside' AND e.inside = true) OR " + "      (:type = 'outside' AND e.outside = true) OR "
			+ "      (:type = 'chef' AND e.chefLabour = true) " + ") "
			+ "ORDER BY e.menuCategorySortOrder ASC, e.menuitemSortOrder ASC")
	List<EventFunctionMenuAllocationEntity> findAllocations(@Param("event") EventMasterEntity event,
			@Param("function") EventFunctionMasterEntity function, @Param("type") String type);

	@Query(value = "SELECT COUNT(DISTINCT e.event_id) " + "FROM eventfunction_menuallocation efma "
			+ "JOIN events e ON efma.event_id = e.event_id " + "WHERE efma.is_delete = false "
			+ "AND e.is_delete = false " + "AND (:userId = -1 OR efma.user_id = :userId)", nativeQuery = true)
	Integer getMenuAllocationCount(@Param("userId") Long userId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM EventFunctionMenuAllocationEntity e where e.event.id = :id")
	void deleteAllByEvent(Long id);

	@Query(value = " "
			+ "  SELECT " 
	        + "  e.event_id AS event_id, "
	        + "  e.event_start_date_time AS event_start_time, "
	        + "  pm.name_english AS party_name, "
	        + "  et.name_english AS event_name, "
	        + "  SUM(efma.person_count) AS pax, "
	        + "  COALESCE(e.venue, bhm.hall_name) AS venue "
	        + " FROM eventfunction_menuallocation efma "
	        + " LEFT JOIN memuitems mi "
	        + " 	ON mi.menu_item_id = efma.menu_item_id "
	        + " 	AND efma.is_delete = FALSE "
	        + " LEFT JOIN event_function ef "
	        + " 	ON ef.event_function_id = efma.eventfunction_id "
	        + " 	AND ef.is_delete = FALSE "
	        + " LEFT JOIN functions f "
	        + " 	ON f.function_id = ef.function_master_id "
	        + " LEFT JOIN `events` e "
	        + " 	ON e.event_id = ef.event_id "
	        + " 	AND e.is_delete = FALSE "
	        + " LEFT JOIN eventtype et "
	        + " 	ON et.event_type_id = e.event_type_id "
	        + " LEFT JOIN partymaster pm "
	        + " 	ON pm.party_id = e.party_id "
	        + " LEFT JOIN banquet_hall_master bhm "
	        + " 	ON bhm.id = e.banquet_hall_id "
	        + " WHERE efma.user_id = :userId "
	        + " 	AND efma.menu_item_id IN (:itemIds) "
	        + " 	AND efma.is_delete = FALSE "
	        + " 	AND DATE(e.event_start_date_time) BETWEEN :startDate AND :endDate "
	        + " GROUP BY e.event_id, e.event_start_date_time, pm.name_english, "
	        + " et.name_english, e.venue, bhm.hall_name",
	        nativeQuery = true)
	List<Object[]> getItemOrderHistoryReport(
	        @Param("userId") Long userId,
	        @Param("itemIds") List<Long> itemIds,
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate);
}
