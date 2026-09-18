package com.crmportal.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.crmportal.entity.MenuAllocationItemRawMaterialEntity;
import com.crmportal.response.dto.RawMaterialCategoryRateResponseDto;

@Repository
public interface MenuAllocationItemRawMaterialRepository
		extends JpaRepository<MenuAllocationItemRawMaterialEntity, Long> {

	List<MenuAllocationItemRawMaterialEntity> findAllByMenuItem_IdAndEventFunctionAndIsDeleteFalse(Long menuItemId,
			EventFunctionMasterEntity eventFunctionEntity);

	@Query("SELECT "
			+ " COALESCE(SUM(m.rate), 0) FROM MenuAllocationItemRawMaterialEntity m"
			+ " WHERE m.event.id = :eventId"
			+ " AND m.eventFunction.id = :eventFunctionId"
			+ " AND m.isDelete = false")
	BigDecimal getTotalRateByEventAndFunction(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT "
			+ " COALESCE(SUM(m.rate), 0) "
			+ " FROM MenuAllocationItemRawMaterialEntity m"
			+ " WHERE m.event.id = :eventId"
			+ " AND m.isDelete = false")
	BigDecimal getTotalRateByEvent(@Param("eventId") Long eventId);

	@Query("SELECT new com.crmportal.response.dto.RawMaterialCategoryRateResponseDto("
			+ " 	c.id, "
			+ " 	c.nameEnglish, "
			+ " 	c.nameHindi, c.nameGujarati, "
			+ " 	COALESCE(SUM(m.rate), 0)) "
			+ " FROM "
			+ " MenuAllocationItemRawMaterialEntity m "
			+ " JOIN m.rawMaterial r "
			+ " JOIN r.rawMaterialCat c "
			+ " WHERE m.event.id = :eventId "
			+ " AND (:eventFunctionId = -1l OR m.eventFunction.id = :eventFunctionId) "
			+ " AND m.isDelete = false "
			+ " GROUP BY c.id, c.nameEnglish, c.nameHindi, c.nameGujarati "
			+ " ORDER BY c.sequence")
	List<RawMaterialCategoryRateResponseDto> getCategoryWiseTotalRate(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query(value = "SELECT COALESCE(SUM(rate), 0) FROM menuallocation_item_rawmaterial WHERE event_id = :eventId AND eventfunction_id = :eventFunctionId AND is_delete = 0", nativeQuery = true)
	BigDecimal getTotalRateByEventAndFunctionNative(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT COALESCE(SUM(m.rate), 0) FROM MenuAllocationItemRawMaterialEntity m WHERE m.event.user.id = :userId AND m.isDelete = false")
	BigDecimal getTotalRateByUser(@Param("userId") Long userId);

	@Query("SELECT COALESCE(SUM(m.rate), 0) FROM MenuAllocationItemRawMaterialEntity m WHERE m.event.user.id = :userId AND m.event.eventStartDateTime BETWEEN :givenDateTime AND CURRENT_TIMESTAMP")
	BigDecimal getTotalRateByUserAndDate(@Param("userId") Long userId,
			@Param("givenDateTime") LocalDateTime givenDateTime);

	List<MenuAllocationItemRawMaterialEntity> findByEventFunctionAndIsDeleteFalse(EventFunctionMasterEntity entity);

	@Modifying
	@Transactional
	@Query(value = "UPDATE eventfunction_menuallocation_order emo JOIN eventfunction_menuallocation em ON em.menu_allocation_id = emo.menu_allocation_id SET emo.is_delete = true WHERE em.eventfunction_id = :eventFunctionId AND em.user_id = :userId", nativeQuery = true)
	Integer removeAgencyByEventFunctionIdAndUserId(@Param("eventFunctionId") Long eventFunctionId,
			@Param("userId") Long userId);

	List<MenuAllocationItemRawMaterialEntity> findAllByMenuItem_IdAndEventAndIsDeleteFalse(Long menuItemId,
			EventMasterEntity eventEntity);

	void deleteAllByEventFunction(EventFunctionMasterEntity eventFunctionMasterEntity);


	@Modifying
	@Query(value = "DELETE FROM menuallocation_item_rawmaterial " + "WHERE menu_item_id = :menuItemId "
			+ "AND event_id = :eventId " + "AND eventfunction_id = :eventFunctionId", nativeQuery = true)
	void deleteRawMaterials(@Param("menuItemId") Long menuItemId,
			@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId);
	
	@Modifying
	@Query(value = ""
			+ " DELETE FROM menu_allocation_item_captain_receipe "
			+ " WHERE "
			+ " menu_item_id = :menuItemId "
			+ " AND event_id = :eventId"
			+ " AND eventfunction_id = :eventFunctionId ", nativeQuery = true)
	void deleteCaptainReceipeRawMaterial(@Param("menuItemId") Long menuItemId,
			@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId);
	
	@Modifying
	@Query(value = "DELETE FROM menuallocation_item_rawmaterial " + "WHERE "
			+ " event_id = :eventId " + "AND eventfunction_id = :eventFunctionId", nativeQuery = true)
	void deleteRawMaterialsByEventAndEventFunction(
			@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId);

	void deleteAllByEvent(EventMasterEntity eventEntity);

	@Query("SELECT "
			+ " COALESCE(SUM(m.price), 0) FROM EventRawMaterialFunctions m"
			+ " WHERE m.event.id = :eventId"
			+ " AND m.eventFunction.id = :eventFunctionId ")
	BigDecimal getTotalRateByEventAndFunctionFromRowMaterial(Long eventId, Long eventFunctionId);

	@Query("SELECT "
			+ " COALESCE(SUM(m.totalprice), 0) FROM EventRawMaterialEntity m"
			+ " WHERE m.event.id = :eventId ")
	BigDecimal getTotalRateByEventFromRowMaterial(Long eventId);

	@Query(value = " "
			+ " SELECT "
			+ "		rm.raw_material_id AS raw_material_id, "
			+ "     CASE "
			+ "         WHEN :lang = 1 THEN CONCAT(rm.name_hindi, ' - ', mi.name_hindi) "
			+ "         WHEN :lang = 2 THEN CONCAT(rm.name_gujarati, ' - ', mi.name_gujarati) "
			+ "         ELSE CONCAT(rm.name_english, ' - ', mi.name_english) "
			+ "     END AS raw_material_name, "
			+ "		mairm.weight AS raw_material_weight, "
			+ " 	mairm.unit_id AS unit_id, "
			+ "     CASE "
			+ "         WHEN :lang = 1 THEN u.name_hindi "
			+ "         WHEN :lang = 2 THEN u.name_gujarati "
			+ "         ELSE u.name_english "
			+ "     END AS unit, "
			+ "		rmc.raw_matrial_cat_id AS raw_material_cat_id, "
			+ "     CASE "
			+ "         WHEN :lang = 1 THEN rmc.name_hindi "
			+ "         WHEN :lang = 2 THEN rmc.name_gujarati "
			+ "         ELSE rmc.name_english "
			+ "     END AS raw_material_cat_name, "
			+ " 	mi.menu_item_id AS menu_item_id "
			+ "	FROM menuallocation_item_rawmaterial mairm "
			+ "	INNER JOIN rawmaterial rm "
			+ "		ON rm.raw_material_id = mairm.raw_material_id "
			+ "		AND rm.is_delete = FALSE "
			+ "	INNER JOIN memuitems mi "
			+ "		ON mi.menu_item_id = mairm.menu_item_id "
			+ "		AND mi.is_delete = FALSE "
			+ "	LEFT JOIN units u "
			+ "		ON u.unit_id = mairm.unit_id "
			+ " 	AND u.is_delete = FALSE "
			+ " LEFT JOIN raw_material_category rmc "
			+ "		ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ "		AND rmc.is_delete = FALSE "
			+ "	WHERE mairm.is_delete = FALSE "
			+ " AND ( "
	        + "     ( "
	        + "         :eventFunctionId IS NOT NULL "
	        + "         AND :eventFunctionId != -1 "
	        + "         AND mairm.eventfunction_id = :eventFunctionId "
	        + "     ) "
	        + "     OR "
	        + "     ( "
	        + "         (:eventFunctionId IS NULL OR :eventFunctionId = -1) "
	        + "         AND mairm.event_id = :eventId "
	        + "     ) "
			+ "	) ", nativeQuery = true)
	List<Object[]> getAllRawMaterial(
			@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId,
			@Param("lang") Integer lang
	);
}
