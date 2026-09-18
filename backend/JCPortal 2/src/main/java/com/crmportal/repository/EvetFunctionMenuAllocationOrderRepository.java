package com.crmportal.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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
import com.crmportal.entity.MenuItemRawMaterialEntity;

@Repository
public interface EvetFunctionMenuAllocationOrderRepository extends JpaRepository<MenuAllocationOrdersEntity, Long> {

	List<MenuAllocationOrdersEntity> findAllByMenuAllocation(EventFunctionMenuAllocationEntity allocationEntitie);

	boolean existsByIdAndIsDeleteFalse(Long id);

	MenuAllocationOrdersEntity findByIdAndIsDeleteFalse(Long id);

	@Query(value = "SELECT mo.* FROM eventfunction_menuallocation_order mo "
			+ "JOIN eventfunction_menuallocation ma ON mo.menu_allocation_id = ma.menu_allocation_id "
			+ "WHERE ma.menu_item_id = :menuItemId AND mo.is_delete = false AND mo.menu_allocation_id = :menuAllocationId ", nativeQuery = true)
	List<MenuAllocationOrdersEntity> findByMenuItemId(@Param("menuItemId") Long menuItemId, Long menuAllocationId);

	@Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM MenuAllocationOrdersEntity o WHERE o.menuAllocation.event.id = :eventId AND o.menuAllocation.eventFunction.id = :eventFunctionId AND o.menuAllocation.chefLabour = true AND o.isDelete = false AND o.isActive = true")
	BigDecimal getChefLabourTotalForEventAndFunction(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT "
			+ " COALESCE(SUM(o.totalPrice), 0) "
			+ " FROM MenuAllocationOrdersEntity o "
			+ " WHERE o.menuAllocation.event.id = :eventId "
			+ " AND o.menuAllocation.chefLabour = true "
			+ " AND o.isDelete = false "
			+ " AND o.isActive = true "
			+ " AND o.menuAllocation.isDelete = false"
			+ " AND o.menuAllocation.isActive = true ")
	BigDecimal getChefLabourTotalForEvent(@Param("eventId") Long eventId);

	@Query(value = "SELECT COALESCE(SUM(mo.total_price), 0) FROM eventfunction_menuallocation_order mo JOIN eventfunction_menuallocation ma ON mo.menu_allocation_id = ma.menu_allocation_id WHERE ma.event_id = :eventId AND ma.eventfunction_id = :eventFunctionId AND ma.chef_labour = TRUE AND mo.is_delete = FALSE AND mo.is_active = TRUE", nativeQuery = true)
	BigDecimal getChefLabourTotalForEventAndFunctionNative(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM MenuAllocationOrdersEntity o WHERE o.menuAllocation.event.id = :eventId AND o.menuAllocation.eventFunction.id = :eventFunctionId AND o.menuAllocation.outside = true AND o.isDelete = false AND o.isActive = true")
	BigDecimal getOutsideTotalForEventAndFunction(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM MenuAllocationOrdersEntity o WHERE o.menuAllocation.event.id = :eventId AND o.menuAllocation.outside = true AND o.isDelete = false AND o.isActive = true")
	BigDecimal getOutsideTotalForEvent(@Param("eventId") Long eventId);

	@Query(value = "SELECT COALESCE(SUM(mo.total_price), 0) FROM eventfunction_menuallocation_order mo JOIN eventfunction_menuallocation ma ON mo.menu_allocation_id = ma.menu_allocation_id WHERE ma.event_id = :eventId AND ma.eventfunction_id = :eventFunctionId AND ma.outside = TRUE AND mo.is_delete = FALSE AND mo.is_active = TRUE", nativeQuery = true)
	BigDecimal getOutsideTotalForEventAndFunctionNative(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM MenuAllocationOrdersEntity o WHERE o.menuAllocation.event.user.id = :userId AND o.menuAllocation.chefLabour = true AND o.isDelete = false AND o.isActive = true")
	BigDecimal getChefLabourTotalByUser(@Param("userId") Long userId);

	@Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM MenuAllocationOrdersEntity o WHERE o.menuAllocation.event.user.id = :userId AND o.menuAllocation.chefLabour = true AND o.isDelete = false AND o.isActive = true AND o.menuAllocation.event.eventStartDateTime BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	BigDecimal getChefLabourTotalByUserAndDate(@Param("userId") Long userId,
			@Param("givenDateTime") LocalDateTime givenDateTime);

	@Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM MenuAllocationOrdersEntity o WHERE o.menuAllocation.event.user.id = :userId AND o.menuAllocation.outside = true AND o.isDelete = false AND o.isActive = true")
	BigDecimal getOutsideTotalByUser(@Param("userId") Long userId);

	@Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM MenuAllocationOrdersEntity o WHERE o.menuAllocation.event.user.id = :userId AND o.menuAllocation.outside = true AND o.isDelete = false AND o.isActive = true AND o.menuAllocation.event.eventStartDateTime BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	BigDecimal getOutsideTotalByUserAndDate(@Param("userId") Long userId,
			@Param("givenDateTime") LocalDateTime givenDateTime);

	List<MenuAllocationOrdersEntity> findAllByMenuAllocationAndIsDeleteFalse(EventFunctionMenuAllocationEntity entity);

	void deleteAllByMenuAllocation(EventFunctionMenuAllocationEntity savedAllocation);

	void deleteAllByMenuAllocation_id(Long long1);

	@Modifying
	@Transactional
	@Query(value = "DELETE emo " + "FROM eventfunction_menuallocation_order emo "
			+ "JOIN eventfunction_menuallocation em " + "  ON emo.menu_allocation_id = em.menu_allocation_id "
			+ "WHERE em.event_id = :eventId " + "  AND em.eventfunction_id = :eventFunctionId "
			+ "  AND em.menu_item_id = :menuItemId", nativeQuery = true)
	void deleteOrders(@Param("menuItemId") Long menuItemId, @Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Modifying
	@Transactional
	@Query(value = "DELETE emo " + "FROM eventfunction_menuallocation_order emo "
			+ "JOIN eventfunction_menuallocation em " + "  ON emo.menu_allocation_id = em.menu_allocation_id "
			+ "WHERE em.event_id = :eventId " + "  AND em.eventfunction_id = :eventFunctionId ", nativeQuery = true)
	void deleteOrdersByEventAndEventFunction(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

//	@Query(value = "SELECT " +
//	        "p.party_id AS partyId, " +
//	        "p.name_english AS partyName, " +
//	        "et.name_english AS eventTypeName, " +
//	        "e.event_start_date_time AS startDateTime, " +
//	        "e.event_end_date_time AS endDateTime " +
//	        "FROM partymaster p " +
//	        "JOIN contact_category cc ON p.contact_category_id = cc.contact_category_id " +
//	        "JOIN contacttype ct ON cc.contact_type_id = ct.contact_type_id " +
//	        "JOIN eventfunction_menuallocation_order emo ON emo.party_id = p.party_id " +
//	        "JOIN eventfunction_menuallocation em ON emo.menu_allocation_id = em.menu_allocation_id " +
//	        "JOIN events e ON em.event_id = e.event_id " +
//	        "JOIN eventtype et ON e.event_type_id = et.event_type_id " +
//	        "WHERE ct.contact_type_id IN (5,6) " +
//	        "AND (:isAllStatus = true OR e.status = 1) " +
//	        "AND e.user_id = :userid " +
//	        "AND e.event_start_date_time = ( " +
//	        "   SELECT MAX(e2.event_start_date_time) " +
//	        "   FROM eventfunction_menuallocation_order emo2 " +
//	        "   JOIN eventfunction_menuallocation em2 ON emo2.menu_allocation_id = em2.menu_allocation_id " +
//	        "   JOIN events e2 ON em2.event_id = e2.event_id " +
//	        "   WHERE emo2.party_id = p.party_id " +
//	        "   AND (:isAllStatus = true OR e2.status = 1) " +
//	        "   AND e2.user_id = :userid " +
//	        ")",
//	        nativeQuery = true)
//	List<Object[]> findAllChefOutsidePartyByUser(Long userid, Boolean isAllStatus);

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
	        + "LEFT JOIN eventfunction_menuallocation_order emo "
	        + "       ON emo.party_id = p.party_id "
	        + "LEFT JOIN eventfunction_menuallocation em "
	        + "       ON emo.menu_allocation_id = em.menu_allocation_id "
	        + "       AND em.chef_labour = 1 "
	        + "LEFT JOIN events e "
	        + "       ON em.event_id = e.event_id "
	        + "       AND e.user_id = :userid "
	        + "       AND (:isAllStatus = true OR e.status = 1) "
	        + "       AND e.event_start_date_time = ( "
	        + "           SELECT MAX(e2.event_start_date_time) "
	        + "           FROM eventfunction_menuallocation_order emo2 "
	        + "           JOIN eventfunction_menuallocation em2 "
	        + "                ON emo2.menu_allocation_id = em2.menu_allocation_id "
	        + "           JOIN events e2 "
	        + "                ON em2.event_id = e2.event_id "
	        + "           WHERE emo2.party_id = p.party_id "
	        + "           AND em2.chef_labour = 1 "
	        + "           AND e2.user_id = :userid "
	        + "           AND (:isAllStatus = true OR e2.status = 1) "
	        + "       ) "
	        + "LEFT JOIN eventtype et ON e.event_type_id = et.event_type_id "
	        + "WHERE ct.contact_type_id IN (5) "
	        + "AND p.is_delete = false AND p.user_id = :userid",
	        nativeQuery = true)
	List<Object[]> findAllChefPartyByUser(Long userid, Boolean isAllStatus);

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
	        + "LEFT JOIN eventfunction_menuallocation_order emo "
	        + "       ON emo.party_id = p.party_id "
	        + "LEFT JOIN eventfunction_menuallocation em "
	        + "       ON emo.menu_allocation_id = em.menu_allocation_id "
	        + "       AND em.outside = 1 "
	        + "LEFT JOIN events e "
	        + "       ON em.event_id = e.event_id "
	        + "       AND e.user_id = :userid "
	        + "       AND (:isAllStatus = true OR e.status = 1) "
	        + "       AND e.event_start_date_time = ( "
	        + "           SELECT MAX(e2.event_start_date_time) "
	        + "           FROM eventfunction_menuallocation_order emo2 "
	        + "           JOIN eventfunction_menuallocation em2 "
	        + "                ON emo2.menu_allocation_id = em2.menu_allocation_id "
	        + "           JOIN events e2 "
	        + "                ON em2.event_id = e2.event_id "
	        + "           WHERE emo2.party_id = p.party_id "
	        + "           AND em2.outside = 1 "
	        + "           AND (:isAllStatus = true OR e2.status = 1) "
	        + "           AND e2.user_id = :userid "
	        + "       ) "
	        + "LEFT JOIN eventtype et ON e.event_type_id = et.event_type_id "
	        + "WHERE ct.contact_type_id IN (3,6) "
	        + "AND p.is_delete = false AND p.user_id = :userid",
	        nativeQuery = true)
	List<Object[]> findAllOutsidePartyByUser(Long userid, Boolean isAllStatus);

	@Modifying
	@Transactional
	@Query("DELETE FROM MenuAllocationOrdersEntity m WHERE m.menuAllocation.id IN :ids")
	void deleteByAllocationIds(@Param("ids") List<Long> ids);

	List<MenuAllocationOrdersEntity> findAllByMenuAllocationInAndIsDeleteFalse(
			List<EventFunctionMenuAllocationEntity> allocationEntities);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM eventfunction_menuallocation_order " + "WHERE menu_allocation_id IN ("
			+ "SELECT menu_allocation_id FROM eventfunction_menuallocation WHERE event_id = :id"
			+ ")", nativeQuery = true)
	void deleteAllByEvent(@Param("id") Long id);

	@Query("SELECT m "+
			"FROM MenuAllocationOrdersEntity m "+
			"WHERE m.menuAllocation.event.id = :eventId "+
			"AND m.menuAllocation.eventFunction.id = :eventFunctionId "+
			"AND ( "+
			"	(:type = 'CHEF' AND m.menuAllocation.chefLabour = true) "+
			"	OR "+
			"	(:type = 'OUTSIDE' AND m.menuAllocation.outside = true) "+
			") "+
			"AND m.isDelete = false AND m.party IS NOT NULL")
		List<MenuAllocationOrdersEntity> findByEventAndFunctionAndType(
				@Param("eventId") Long eventId,
				@Param("eventFunctionId") Long eventFunctionId,
				@Param("type") String type);

}
