package com.crmportal.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CaptainReceipeMasterEntity;
import com.crmportal.entity.MenuItemCaptainReceipeEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UserMasterEntity;

import lombok.val;

@Repository
public interface MenuItemRawMaterialRepository extends JpaRepository<MenuItemRawMaterialEntity, Long> {

	Optional<MenuItemRawMaterialEntity> findByIdAndIsDeleteFalse(Long id);

	List<MenuItemRawMaterialEntity> findAllByMenuItem(MenuItemMasterEntity entity);

	@Modifying
	@Transactional
	@Query("DELETE FROM MenuItemRawMaterialEntity e WHERE e.menuItem = :menuItem AND e.user = :user")
	void deleteByMenuItemAndUser(@Param("menuItem") MenuItemMasterEntity menuItem,
			@Param("user") UserMasterEntity user);

	List<MenuItemRawMaterialEntity> findAllByMenuItem_IdAndIsDeleteFalse(Long menuItemId);

	List<MenuItemRawMaterialEntity> findByUuid(String uuid);

	List<MenuItemRawMaterialEntity> findAllByMenuItemAndUserAndIsDeleteFalse(MenuItemMasterEntity item,
			UserMasterEntity user);

//	@Query(value = "SELECT DISTINCT mir.event_id, " + " mir.eventfunction_id, "
//			+ " CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS function_name, "
//			+ " CONCAT(DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y %h:%i %p'), ' to ', DATE_FORMAT(ef.function_end_date_time, '%h:%i %p')) AS function_time, "
//			+ " ef.pax AS person, "
//			+ " CASE WHEN :lang = 0 THEN ef.function_venue WHEN :lang = 1 THEN ef.function_venue_hindi WHEN :lang = 2 THEN ef.function_venue_gujarati END AS function_venue "
//			+ " FROM menuallocation_item_rawmaterial mir "
//			+ " LEFT JOIN `events` e ON e.event_id = mir.event_id "
//			+ " LEFT JOIN eventtype et ON et.event_type_id = e.event_type_id "
//			+ " LEFT JOIN event_function ef ON ef.event_function_id = mir.eventfunction_id "
//			+ " LEFT JOIN functions f ON f.function_id = ef.function_master_id "
//			+ " WHERE mir.event_id = :eventId AND (:eventFunctionId = -1 OR eventfunction_id = :eventFunctionId) "
//			+ " AND mir.is_delete = FALSE AND ef.is_delete = FALSE AND e.is_delete = FALSE ", nativeQuery = true)
//	List<Object[]> getEventFunctions(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId,
//			@Param("lang") int lang);
	
	@Query(value = " "
			+ " SELECT DISTINCT "
			+ "		e.event_id, "
	        + "		ef.event_function_id, "
	        + "		CASE "
	        + "    		WHEN :lang = 0 THEN f.name_english "
	        + "    		WHEN :lang = 1 THEN f.name_hindi "
	        + "    		WHEN :lang = 2 THEN f.name_gujarati "
	        + "		END AS function_name, "
	        + "		CONCAT( "
	        + "    		DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y %h:%i %p'), "
	        + "    		' to ', "
	        + "    		DATE_FORMAT(ef.function_end_date_time, '%h:%i %p') "
	        + "		) AS function_time, "
	        + "		ef.pax AS person, "
	        + "		CASE "
	        + "    		WHEN :lang = 0 THEN ef.function_venue "
	        + "    		WHEN :lang = 1 THEN ef.function_venue_hindi "
	        + "    		WHEN :lang = 2 THEN ef.function_venue_gujarati "
	        + "		END AS function_venue "
	        + "	FROM `events` e "
	        + "	LEFT JOIN eventtype et "
	        + "    	ON et.event_type_id = e.event_type_id "
	        + "	LEFT JOIN event_function ef "
	        + "    	ON ef.event_id = e.event_id "
	        + "	LEFT JOIN functions f "
	        + "    	ON f.function_id = ef.function_master_id "
	        + "	WHERE e.event_id = :eventId "
	        + " AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) "
	        + " AND ef.is_delete = FALSE "
	        + " AND e.is_delete = FALSE",
	        nativeQuery = true)
	List<Object[]> getEventFunctions(
	        @Param("eventId") Long eventId,
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("lang") int lang);

	@Query(value = "SELECT DISTINCT mir.event_id, " + " mir.eventfunction_id, "
			+ " CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS function_name, "
			+ " CONCAT(DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y %h:%i %p'), ' to ', DATE_FORMAT(ef.function_end_date_time, '%h:%i %p')) AS function_time, "
			+ " ef.pax AS person " + " FROM menuallocation_item_rawmaterial mir "
			+ " LEFT JOIN `events` e ON e.event_id = mir.event_id "
			+ " LEFT JOIN eventtype et ON et.event_type_id = e.event_type_id "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = mir.eventfunction_id "
			+ " LEFT JOIN functions f ON f.function_id = ef.function_master_id "
			+ " WHERE mir.event_id = :eventId AND (eventfunction_id IN (:eventFunctionIds)) "
			+ " AND mir.is_delete = FALSE AND ef.is_delete = FALSE AND e.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getEventMultiFunctions(@Param("eventId") Long eventId,
			@Param("eventFunctionIds") List<Long> eventFunctionIds, @Param("lang") int lang);

//	SELECT DISTINCT
//	ef.event_id,
//	ef.event_function_id, 
//	mpd.menu_item_id,
//	mpd.menuitem_name AS item_name,
//	em.inside,
//	em.outside,
//	em.chef_labour
//FROM event_function ef
//LEFT JOIN menupreparation mp ON ef.event_function_id = mp.event_function_id
//LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id
//LEFT JOIN eventfunction_menuallocation em ON em.menu_item_id = mpd.menu_item_id AND em.eventfunction_id = ef.event_function_id AND em.event_id = ef.event_id
//WHERE ef.event_id = 197
//  AND ef.event_function_id = 379;

	@Query(value = "SELECT em.event_id, em.eventfunction_id, em.menu_item_id, "
			+ " CASE WHEN :lang = 0 THEN mpd.menuitem_name " + "      WHEN :lang = 1 THEN mpd.menuitem_name_hindi "
			+ "      WHEN :lang = 2 THEN mpd.menuitem_name_gujarati END AS item_name, "
			+ " MAX(em.inside) AS inside, MAX(em.outside) AS outside, MAX(em.chef_labour) AS chef_labour, "
			+ " MAX(mpd.menu_sortorder) AS menu_sort, MAX(mpd.item_sortorder) AS item_sort, "
			+ " CASE WHEN :lang = 0 THEN em.instructions " + "      WHEN :lang = 1 THEN em.instructions_hindi "
			+ "      WHEN :lang = 2 THEN em.instructions_gujarati END AS instruction "
			+ " FROM eventfunction_menuallocation em "
			+ " LEFT JOIN menupreparation mp ON mp.event_function_id = em.eventfunction_id "
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ "      AND mpd.menu_item_id = em.menu_item_id " + " WHERE em.event_id = :eventId "
			+ "   AND em.eventfunction_id = :functionId " + "   AND em.is_delete = FALSE "
			+ " GROUP BY em.event_id, em.eventfunction_id, em.menu_item_id, item_name,instruction "
			+ " ORDER BY menu_sort, item_sort", nativeQuery = true)
	List<Object[]> getEventFunctionItems(@Param("eventId") Long eventId, @Param("functionId") Long functionId,
			@Param("lang") int lang);

	@Query(value = "SELECT mir.menu_item_id, " + " mir.raw_material_id, "
			+ " CASE WHEN :lang = 0 THEN rm.name_english WHEN :lang = 1 THEN rm.name_hindi WHEN :lang = 2 THEN rm.name_gujarati END AS raw_material_name, "
			+ " mir.weight, " + " mir.unit_id, "
			+ " CASE WHEN :lang = 0 THEN u.name_english WHEN :lang = 1 THEN u.name_hindi WHEN :lang = 2 THEN u.name_gujarati END AS unit_symbol "
			+ " FROM menuallocation_item_rawmaterial mir "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = mir.raw_material_id "
			+ " LEFT JOIN units u ON u.unit_id = mir.unit_id " + " WHERE mir.event_id = :eventId "
			+ "	  AND mir.is_delete = FALSE " + "   AND mir.eventfunction_id = :functionId "
			+ "   AND menu_item_id = :itemId AND rm.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getEventFunctionItemsRawMaterials(@Param("eventId") Long eventId,
			@Param("functionId") Long functionId, @Param("itemId") Long itemId, @Param("lang") int lang);

	@Query(value = "SELECT DISTINCT e.event_id, ud.company_name, ud.country_code, ud.office_no, ud.company_email, u.logo,"
			+ " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS party_name, "
			+ " pm.mobileno, "
			+ " CASE WHEN :lang = 0 THEN et.name_english WHEN :lang = 1 THEN et.name_hindi WHEN :lang = 2 THEN et.name_gujarati END AS event_name, "
			+ " DATE_FORMAT(e.event_start_date_time, '%d/%m/%Y') AS event_date, " + " e.venue_id, "
			+ " CASE WHEN :lang = 0 THEN v.name_english WHEN :lang = 1 THEN v.name_hindi WHEN :lang = 2 THEN v.name_gujarati END AS venue_name, "
			+ " e.event_no, ud.address, "
			+ " DATE_FORMAT(e.event_start_date_time, '%d/%m/%Y') as event_start_date_time, e.remark "
			+ " FROM `events` e " + " LEFT JOIN partymaster pm ON pm.party_id = e.party_id "
			+ " LEFT JOIN eventtype et ON et.event_type_id = e.event_type_id "
			+ " LEFT JOIN venuemaster v ON v.venue_id = e.venue_id " + " LEFT JOIN users u ON u.user_id = e.user_id "
			+ " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
			+ " WHERE e.event_id = :eventId AND e.is_delete = FALSE ", nativeQuery = true)
	Object getEventData(@Param("eventId") Long eventId, @Param("lang") int lang);

	@Query(value = "SELECT ud.company_name, ud.country_code, ud.office_no, ud.company_email, u.logo," + " ud.address "
			+ " FROM users u " + " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
			+ " WHERE u.user_id = :userId ", nativeQuery = true)
	Object getCmpData(@Param("userId") Long userId);

	@Query(value = "SELECT DISTINCT rmc.raw_matrial_cat_id, "
			+ " CASE WHEN :lang = 0 THEN rmc.name_english WHEN :lang = 1 THEN rmc.name_hindi WHEN :lang = 2 THEN rmc.name_gujarati END AS raw_material_cat_name,mir.raw_material_id "
			+ " FROM menuallocation_item_rawmaterial mir "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = mir.raw_material_id "
			+ " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ " WHERE mir.event_id = :eventId AND mir.eventfunction_id = :functionId AND mir.is_delete = FALSE "
			+ " ORDER BY rmc.raw_matrial_cat_id, mir.raw_material_id ", nativeQuery = true)
	List<Object[]> getEventFunctionRawMaterialCat(@Param("eventId") Long eventId, @Param("functionId") Long functionId,
			@Param("lang") int lang);

	@Query(value = "SELECT DISTINCT rmc.raw_matrial_cat_id, mir.raw_material_id, "
			+ " CASE WHEN :lang = 0 THEN rmc.name_english WHEN :lang = 1 THEN rmc.name_hindi WHEN :lang = 2 THEN rmc.name_gujarati END AS raw_material_cat_name, "
			+ " CASE WHEN :lang = 0 THEN rm.name_english WHEN :lang = 1 THEN rm.name_hindi WHEN :lang = 2 THEN rm.name_gujarati END AS raw_material_name "
			+ " FROM menuallocation_item_rawmaterial mir "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = mir.raw_material_id "
			+ " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ " WHERE mir.event_id = :eventId AND mir.eventfunction_id = :functionId AND rmc.raw_matrial_cat_id = :cat AND mir.is_delete = FALSE "
			+ " ORDER BY rmc.raw_matrial_cat_id, mir.raw_material_id ", nativeQuery = true)
	List<Object[]> getEventFunctionRawMaterial(@Param("eventId") Long eventId, @Param("functionId") Long functionId,
			@Param("lang") int lang, @Param("cat") Long cat);

	@Query(value = "SELECT rmc.raw_matrial_cat_id, mir.raw_material_id, " + " mir.menu_item_id, "
			+ " CASE WHEN :lang = 0 THEN m.name_english WHEN :lang = 1 THEN m.name_hindi WHEN :lang = 2 THEN m.name_gujarati END AS item_name, "
			+ " mir.weight, " + " mir.unit_id,"
			+ " CASE WHEN :lang = 0 THEN u.name_english WHEN :lang = 1 THEN u.name_hindi WHEN :lang = 2 THEN u.name_gujarati END AS symbol_name, "
			+ " mir.rate " + " FROM menuallocation_item_rawmaterial mir "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = mir.raw_material_id "
			+ " LEFT JOIN memuitems m ON m.menu_item_id = mir.menu_item_id "
			+ " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ " LEFT JOIN units u ON u.unit_id = mir.unit_id "
			+ " WHERE mir.event_id = :eventId  AND mir.eventfunction_id = :functionId AND rmc.raw_matrial_cat_id = :cat  "
			+ " AND mir.raw_material_id = :rawMaterialId AND mir.is_delete = FALSE "
			+ " ORDER BY rmc.raw_matrial_cat_id, mir.raw_material_id ", nativeQuery = true)
	List<Object[]> getEventFunctionRawMaterialWiseItem(@Param("eventId") Long eventId,
			@Param("functionId") Long functionId, @Param("lang") int lang, @Param("cat") Long catId,
			@Param("rawMaterialId") Long rawMaterialId);

	@Query(value = "SELECT ef.event_id, ef.event_function_id, "
			+ " 	IF(:lang = 0, f.name_english, IF(:lang = 1, f.name_hindi, IF(:lang = 2, f.name_gujarati, ''))) AS function_name, "
			+ " 	ef.pax, "
			+ " 	DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y %h:%i %p') AS function_start_time, "
			+ "	DATE_FORMAT(ef.function_end_date_time, '%d/%m/%Y %h:%i %p') AS function_end_time, "
			+ " pm.name_english, " + " pm.mobileno, "
			+ " IF(:lang = 0, ef.function_venue, IF(:lang = 1, ef.function_venue_hindi, IF(:lang = 2, function_venue_gujarati, ''))) AS function_venue FROM event_function ef "
			+ " LEFT JOIN functions f ON f.function_id = ef.function_master_id "
			+ " LEFT JOIN events e ON e.event_id = ef.event_id "
			+ " LEFT JOIN partymaster pm ON pm.party_id = e.party_id "
			+ " WHERE ef.event_id = :eventId ", nativeQuery = true)
	List<Object[]> getEventFunction(Long eventId, int lang);

//	@Query(value = "SELECT DISTINCT mpd.menu_category_id, "
//			+ " IF(:lang = 0, mpd.menu_category_name, IF(:lang = 1, mpd.menu_category_name_hindi, IF(:lang = 2, mpd.menu_category_name_gujarati, ''))) AS category_name, "
//			+ " mpd.menu_sortorder " + " FROM menupreparation mp "
//			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
//			+ " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
//			+ " WHERE mp.event_function_id = :eventFunctionId AND ef.event_id = :eventId "
//			+ " ORDER BY mpd.menu_sortorder ", nativeQuery = true)
//	List<Object[]> getEventFunctionCategory(Long eventId, Long eventFunctionId, int lang);

	@Query(value = "" + " SELECT efma.menu_category_id, " + " IF(:lang = 0, mc.name_english, "
			+ "    IF(:lang = 1, mc.name_hindi, " + "       IF(:lang = 2, mc.name_gujarati, ''))) AS category_name "
			+ " FROM eventfunction_menuallocation efma " + " INNER JOIN menucategory mc "
			+ "    ON efma.menu_category_id = mc.menu_category_id " + "    AND mc.user_id = :userId "
			+ " WHERE efma.eventfunction_id = :eventFunctionId " + " AND efma.event_id = :eventId "
			+ " GROUP BY efma.menu_category_id ", nativeQuery = true)
	List<Object[]> getEventFunctionCategory(Long eventId, Long eventFunctionId, int lang, Long userId);

	@Query(value = "SELECT DISTINCT mpd.menu_item_id, "
			+ " IF(:lang = 0, mpd.menuitem_name, IF(:lang = 1, mpd.menuitem_name_hindi, IF(:lang = 2, mpd.menuitem_name_gujarati, ''))) AS item_name, "
			+ " SUM(ROUND((ef.pax * eirm.rate) / 100, 2)) AS totalprice, "
			+ " ROUND(SUM(ROUND((ef.pax * eirm.rate) / 100, 2)) / ef.pax, 2) AS perplateprice, "
			+ " COUNT(eirm.raw_material_id) AS totalrawmaterialitems" + " FROM event_function ef "
			+ " LEFT JOIN menupreparation mp ON mp.event_function_id = ef.event_function_id "
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ " LEFT JOIN menu_item_raw_material eirm ON eirm.menu_item_id = mpd.menu_item_id "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = eirm.raw_material_id  "
			+ " WHERE mp.event_function_id = :eventFunctionId " + "   AND mpd.menu_category_id = :catId "
			+ "   AND ef.event_id = :eventId "
			+ " GROUP BY mpd.menu_item_id, mpd.menuitem_name, mpd.menuitem_name_hindi, mpd.menuitem_name_gujarati "
			+ " HAVING COUNT(eirm.raw_material_id) > 0 ", nativeQuery = true)
	List<Object[]> getEventFunctionCategoryItem(Long eventId, Long eventFunctionId, Long catId, int lang);

//	@Query(value = "SELECT DISTINCT "
//			+ " 	mpd.menu_item_id, "
//			+ "  	IF(:lang = 0, mpd.menuitem_name, IF(:lang = 1, mpd.menuitem_name_hindi, IF(:lang = 2, mpd.menuitem_name_gujarati, ''))) AS item_name, "
//			+ "  	SUM(eirm.rate) AS totalprice, "
//			+ "  	(SUM(eirm.rate) / 100) AS perplateprice, "
//			+ "  	COUNT(eirm.raw_material_id) AS totalrawmaterialitems" + " FROM event_function ef "
//			+ " LEFT JOIN menupreparation mp ON mp.event_function_id = ef.event_function_id "
//			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
//			+ " LEFT JOIN menuallocation_item_rawmaterial eirm ON eirm.menu_item_id = mpd.menu_item_id "
//			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = eirm.raw_material_id  "
//			+ " WHERE mp.event_function_id = :eventFunctionId " 
//			+ "  	 AND mpd.menu_category_id = :catId "
//			+ " 	 AND ef.event_id = :eventId "
//			+ " GROUP BY mpd.menu_item_id, mpd.menuitem_name, mpd.menuitem_name_hindi, mpd.menuitem_name_gujarati "
//			+ " HAVING COUNT(eirm.raw_material_id) > 0 ", nativeQuery = true)
//	List<Object[]> getEventFunctionCategoryItem2(Long eventId, Long eventFunctionId, Long catId, int lang);

	@Query(value = " " + " SELECT  " + "    em.menu_item_id, " + "    IF( " + "        :lang = 0, "
			+ "        mi.name_english, " + "        IF( " + "            :lang = 1, " + "            mi.name_hindi, "
			+ "            IF(:lang = 2, mi.name_gujarati, '') " + "        ) " + "    ) AS item_name, "
			+ "    SUM(eirm.rate) AS totalprice, " + "    (SUM(eirm.rate) / em.person_count) AS perplateprice, "
			+ "    COUNT(eirm.raw_material_id) AS totalrawmaterialitems, " + "    em.menu_category_id,"
			+ "	   em.person_count " + "	FROM menuallocation_item_rawmaterial eirm  "
			+ "	LEFT JOIN memuitems mi  " + "		ON eirm.menu_item_id = mi.menu_item_id "
			+ "	LEFT JOIN eventfunction_menuallocation em " + "    ON em.event_id = eirm.event_id "
			+ "    AND em.eventfunction_id = eirm.eventfunction_id " + "    AND em.menu_item_id = eirm.menu_item_id  "
			+ "WHERE  " + "     eirm.eventfunction_id = :eventFunctionId " + "    AND eirm.event_id = :eventId"
			+ "    AND em.menu_category_id = :catId  " + "GROUP BY eirm.menu_item_id, em.person_count "
			+ "HAVING COUNT(eirm.raw_material_id) > 0; ", nativeQuery = true)
	List<Object[]> getEventFunctionCategoryItem2(Long eventId, Long eventFunctionId, Long catId, int lang);

//	@Query(value = "SELECT DISTINCT mpd.menu_item_id, "
//			+ " IF(:lang = 0, mpd.menuitem_name, IF(:lang = 1, mpd.menuitem_name_hindi, IF(:lang = 2, mpd.menuitem_name_gujarati, ''))) AS item_name, "
//			+ " SUM(ROUND((ef.pax * eirm.price) / 100, 2)) AS totalprice, "
//			+ " ROUND(SUM((ROUND(ef.pax * eirm.price) / 100, 2)) / ef.pax, 2) AS perplateprice, "
//			+ " COUNT(eirm.event_raw_material_id) AS totalrawmaterialitems" + " FROM event_function ef "
//			+ " LEFT JOIN menupreparation mp ON mp.event_function_id = ef.event_function_id "
//			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
//			+ " LEFT JOIN event_raw_material_functions eirm ON eirm.item_name = mpd.menuitem_name "
//			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = eirm.event_raw_material_id  "
//			+ " WHERE mp.event_function_id = :eventFunctionId " + "   AND mpd.menu_category_id = :catId "
//			+ "   AND ef.event_id = :eventId "
//			+ " GROUP BY mpd.menu_item_id, mpd.menuitem_name, mpd.menuitem_name_hindi, mpd.menuitem_name_gujarati "
//			+ " HAVING COUNT(eirm.event_raw_material_id) > 0 ", nativeQuery = true)
//	List<Object[]> getEventFunctionCategoryItem3(Long eventId, Long eventFunctionId, Long catId, int lang);

	@Query(value = " " + "SELECT  " + "    mi.menu_item_id, "
			+ "    IF(:lang = 0, mi.name_english, IF(:lang = 1, mi.name_hindi, IF(:lang = 2, mi.name_gujarati, ''))) AS item_name, "
			+ "    SUM(ROUND(eirmf.price, 2)) AS total_price, " + "    ROUND( "
			+ "        SUM(ROUND(eirmf.price, 2)) / ef.pax, " + "        2 " + "    ) AS per_plate_price, "
			+ "    COUNT(eirmf.event_raw_material_function_id) AS total_raw_materials "
			+ "FROM event_raw_material_functions eirmf " + "INNER JOIN event_function ef "
			+ "    ON ef.event_function_id = eirmf.event_function_id " + "INNER JOIN memuitems mi "
			+ "    ON mi.menu_item_id = eirmf.menuitemid " + "INNER JOIN menupreparation mp "
			+ "    ON mp.event_function_id = ef.event_function_id " + "INNER JOIN menupreparationdetails mpd "
			+ "    ON mpd.menu_preparation_id = mp.menu_preparation_id " + "    AND mpd.menu_item_id = mi.menu_item_id "
			+ "INNER JOIN menucategory mc " + "    ON mc.menu_category_id = mpd.menu_category_id "
			+ "INNER JOIN event_raw_material erm " + "    ON erm.event_raw_material_id = eirmf.event_raw_material_id "
			+ "INNER JOIN rawmaterial rm " + "    ON rm.raw_material_id = erm.raw_material_id "
			+ "WHERE ef.event_function_id = :eventFunctionId " + "AND mpd.menu_category_id = :catId "
			+ "    AND eirmf.event_id = :eventId " + "GROUP BY " + "    mi.menu_item_id, " + "    item_name "
			+ "HAVING COUNT(eirmf.event_raw_material_function_id) > 0 ", nativeQuery = true)
	List<Object[]> getEventFunctionCategoryItem3(Long eventId, Long eventFunctionId, Long catId, int lang);

	@Query(value = "SELECT DISTINCT rmc.raw_matrial_cat_id, "
			+ " IF(:lang = 0, rmc.name_english, IF(:lang = 1, rmc.name_hindi, IF(:lang = 2, rmc.name_gujarati, ''))) AS raw_materila_category_name,"
			+ " menu_sortorder " + " FROM event_function ef "
			+ " LEFT JOIN menupreparation mp ON mp.event_function_id = ef.event_function_id "
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ " LEFT JOIN menu_item_raw_material eirm ON eirm.menu_item_id = mpd.menu_item_id "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = eirm.raw_material_id "
			+ " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ " WHERE mp.event_function_id = :eventFunctionId " + "   AND mpd.menu_category_id = :catId "
			+ "   AND mpd.menu_item_id = :itemId " + "   AND ef.event_id = :eventId "
			+ "   AND rmc.raw_matrial_cat_id IS NOT NULL " + " ORDER BY menu_sortorder ", nativeQuery = true)
	List<Object[]> getRawMaterialCategory(Long eventId, Long eventFunctionId, Long catId, Long itemId, int lang);

	@Query(value = "SELECT DISTINCT eirm.raw_material_id, "
			+ " IF(:lang = 0, rm.name_english, IF(:lang = 1, rm.name_hindi, IF(:lang = 2, rm.name_gujarati, ''))) AS raw_materila_item_name, "
			+ " ROUND((ef.pax * eirm.weight) / 100, 2) AS quantity, "
			+ " IF(:lang = 0, u.name_english, IF(:lang = 1, u.name_hindi, IF(:lang = 2, u.name_gujarati, ''))) AS unit_name, "
			+ " ROUND(((ef.pax * eirm.rate) / 100) / ((ef.pax * eirm.weight) / 100), 2) AS rate, "
			+ " ROUND((ef.pax * eirm.rate) / 100, 2) AS totalprice " + " FROM event_function ef "
			+ " LEFT JOIN menupreparation mp ON mp.event_function_id = ef.event_function_id "
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ " LEFT JOIN menu_item_raw_material eirm ON eirm.menu_item_id = mpd.menu_item_id "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = eirm.raw_material_id "
			+ " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ " LEFT JOIN units u ON u.unit_id = eirm.unit_id " + " WHERE mp.event_function_id = :eventFunctionId "
			+ "   AND mpd.menu_category_id = :catId " + "   AND mpd.menu_item_id = :itemId "
			+ "   AND rmc.raw_matrial_cat_id = :rawMaterialCatId "
			+ "   AND ef.event_id = :eventId ", nativeQuery = true)
	List<Object[]> getRawMaterialItem(Long eventId, Long eventFunctionId, Long catId, Long itemId,
			Long rawMaterialCatId, int lang);

	@Query(value = "SELECT DISTINCT rm.raw_material_id, "
			+ " IF(:lang = 0, rm.name_english, IF(:lang = 1, rm.name_hindi, IF(:lang = 2, rm.name_gujarati, ''))) AS raw_materila_item_name, "
			+ " mir.weight, "
			+ " IF(:lang = 0, u.name_english, IF(:lang = 1, u.name_hindi, IF(:lang = 2, u.name_gujarati, ''))) AS unit_name, "
			+ " ROUND(rm.supplier_rate, 2), " + " ROUND(mir.rate, 2) AS totalprice "
			+ " FROM menuallocation_item_rawmaterial mir "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = mir.raw_material_id "
			+ " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ " LEFT JOIN units u ON u.unit_id = mir.unit_id " + " WHERE mir.eventfunction_id = :eventFunctionId "
			+ "   AND mir.menu_item_id = :itemId " + "   AND rmc.raw_matrial_cat_id = :rawMaterialCatId "
			+ "   AND mir.event_id = :eventId ", nativeQuery = true)
	List<Object[]> getRawMaterialItem2(Long eventId, Long eventFunctionId, Long itemId, Long rawMaterialCatId,
			int lang);

//	@Query(value = "SELECT DISTINCT ermf.event_raw_material_id, "
//			+ " IF(:lang = 0, rm.name_english, IF(:lang = 1, rm.name_hindi, IF(:lang = 2, rm.name_gujarati, ''))) AS raw_materila_item_name, "
//			+ " ermf.qty, "
//			+ " IF(:lang = 0, u.name_english, IF(:lang = 1, u.name_hindi, IF(:lang = 2, u.name_gujarati, ''))) AS unit_name, "
//			+ " ermf.price, " + " ROUND(ermf.qty * ermf.price, 2) AS totalprice "
//			+ " FROM event_raw_material_functions ermf "
//			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = ermf.event_raw_material_id "
//			+ " LEFT JOIN units u ON u.unit_id = ermf.unit_id " + " WHERE ermf.event_id = :eventId "
//			+ "   AND ermf.event_function_id = :eventFunctionId " + "   AND ermf.item_name = :itemName "
//			+ "   AND ermf.raw_material_cat_id = :rawMaterialCatId ", nativeQuery = true)
//	List<Object[]> getRawMaterialItem3(Long eventId, Long eventFunctionId, String itemName, Long rawMaterialCatId,
//			int lang);

	@Query(value = "" + " SELECT DISTINCT " + "    rm.raw_material_id, " + "    IF( " + "        :lang = 0, "
			+ "        rm.name_english, " + "        IF(:lang = 1, rm.name_hindi, "
			+ "            IF(:lang = 2, rm.name_gujarati, '') " + "        ) " + "    ) AS raw_material_name, "
			+ "    IFNULL(ermf.qty, 0) AS qty, " + "    IFNULL(ermf.price, 0) AS price, " + "    IF( "
			+ "        :lang = 0, " + "        u.name_english, " + "        IF(:lang = 1, u.name_hindi, "
			+ "            IF(:lang = 2, u.name_gujarati, '') " + "        ) " + "    ) AS unit_name "
			+ " FROM event_raw_material_functions ermf " + " INNER JOIN event_raw_material erm "
			+ "    ON erm.event_raw_material_id = ermf.event_raw_material_id " + " INNER JOIN rawmaterial rm "
			+ "    ON rm.raw_material_id = erm.raw_material_id " + " LEFT JOIN units u "
			+ "    ON u.unit_id = ermf.unit_id " + " WHERE ermf.event_id = :eventId "
			+ "    AND ermf.event_function_id = :eventFunctionId " + "    AND ermf.menuitemid = :menuItemId "
			+ "    AND ermf.raw_material_cat_id = :rawMaterialCatId ", nativeQuery = true)
	List<Object[]> getRawMaterialItem3(@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId,
			@Param("menuItemId") Long menuItemId, @Param("rawMaterialCatId") Long rawMaterialCatId,
			@Param("lang") int lang);

	@Query(value = "" + " SELECT " + "     eerm.rawmaterial_cat_id AS cat_id, " + "     rmc.name_english AS cat_name, "
			+ "     eerm.extra_rawmaterial AS raw_material_name, " + "     IFNULL(eerm.finalqty, 0) AS qty, "
			+ "     IFNULL(eerm.totalprice, 0) AS price, " + "     IF(:lang = 0, u.name_english, "
			+ "        IF(:lang = 1, u.name_hindi, " + "           IF(:lang = 2, u.name_gujarati, '')" + "        )"
			+ "     ) AS unit_name " + " FROM event_extra_rawmaterial eerm "
			+ " LEFT JOIN units u ON u.unit_id = eerm.unit_id "
			+ " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = eerm.rawmaterial_cat_id "
			+ " WHERE eerm.event_id = :eventId " + "   AND eerm.event_function_id = :eventFunctionId "
			+ " ORDER BY eerm.rawmaterial_cat_id", nativeQuery = true)
	List<Object[]> getExtraRawMaterialItemCategoryWise(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId, @Param("lang") int lang);

	@Query(value = " SELECT SUM(totalprice) " + " FROM " + " event_extra_rawmaterial " + " WHERE event_id = :eventId "
			+ " AND (:eventFunctionId = -1 OR event_function_id = :eventFunctionId) ", nativeQuery = true)
	BigDecimal getTotalExtraRawMaterialAmount(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query(value = "SELECT mirm.menu_item_id, " + "SUM(mirm.rate) AS total_rate, "
			+ "SUM(mirm.rate) / 100 AS dish_costing " + "FROM menu_item_raw_material mirm "
			+ "WHERE mirm.user_id = :id " + "AND mirm.uuid = :uuid " + "GROUP BY mirm.menu_item_id", nativeQuery = true)
	List<Object[]> getRateDishCosting(@Param("id") Long id, @Param("uuid") String uuid);

	@Query(value = "SELECT IF(:lang = 0, f.name_english, IF(:lang = 1, f.name_hindi, IF(:lang = 2, f.name_gujarati, ''))) AS function_name, "
			+ "IF(:lang = 0, mpd.menuitem_name, IF(:lang = 1, mpd.menuitem_name_hindi, IF(:lang = 2, mpd.menuitem_name_gujarati, ''))) AS item_name, "
			+ "miac.party_id, "
			+ "IF(:lang = 0, pm.name_english, IF(:lang = 1, pm.name_hindi, IF(:lang = 2, pm.name_gujarati, ''))) AS agency_name, "
			+ "miac.price_per_labour, " + "miac.base_price, " + "miac.select_chef_labour_agency, "
			+ "miac.select_outside_agency, " + "ROUND((ef.pax * miac.quantity_per_100_person) / 100, 2) AS quantity, "
			+ "IF(:lang = 0, u.name_english, IF(:lang = 1, u.name_hindi, IF(:lang = 2, u.name_gujarati, ''))) AS unit_name, "
			+ "ROUND(miac.price_per_labour * ef.pax * miac.quantity_per_100_person / 100, 2) AS total_chef_labour_price, "
			+ "ROUND(miac.base_price * ef.pax * miac.quantity_per_100_person / 100, 2) AS total_outsource_price "
			+ "FROM menu_item_allocation_config miac "
			+ "LEFT JOIN menupreparationdetails mpd ON miac.menu_item_id = mpd.menu_item_id "
			+ "LEFT JOIN menupreparation mp ON mp.menu_preparation_id = mpd.menu_preparation_id "
			+ "LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
			+ "LEFT JOIN functions f ON f.function_id = ef.function_master_id "
			+ "LEFT JOIN partymaster pm ON pm.party_id = miac.party_id "
			+ "LEFT JOIN units u ON u.unit_id = miac.unit_id " + "WHERE miac.menu_item_id IN (:itemIds) "
			+ " AND miac.select_chef_labour_agency = :chefLabour " + " AND miac.select_outside_agency = :outsource "
			+ "AND ef.event_id = :eventId ", nativeQuery = true)
	List<Object[]> getAgencyData(Long eventId, List<Long> itemIds, int lang, boolean chefLabour, boolean outsource);

	@Query(value = "SELECT DISTINCT IF(:lang = 0, f.name_english, IF(:lang = 1, f.name_hindi, IF(:lang = 2, f.name_gujarati, ''))) AS function_name, "
			+ "IF(:lang = 0, mi.name_english, IF(:lang = 1, mi.name_hindi, IF(:lang = 2, mi.name_gujarati, ''))) AS item_name, "
			+ "emo.party_id, "
			+ "IF(:lang = 0, pm.name_english, IF(:lang = 1, pm.name_hindi, IF(:lang = 2, pm.name_gujarati, ''))) AS agency_name, "
			+ " CASE " + "     WHEN LOWER(REPLACE(emo.service_type, '_', ' ')) = 'counter wise' "
			+ "         THEN CONCAT(FORMAT(emo.helper_price, 2), ' + ', FORMAT(emo.counter_price, 2)) "
			+ "     WHEN LOWER(REPLACE(emo.service_type, '_', ' ')) = 'plate wise' "
			+ "         THEN FORMAT(emo.price, 2) " + "     ELSE FORMAT(emo.price, 2) " + " END AS chef_labour_price, "
			+ "emo.price AS outside_price, " + "em.chef_labour, " + "em.outside, " + " CAST( " + "     CASE "
			+ "         WHEN :chefLabour = TRUE THEN " + "             CASE "
			+ "                 WHEN LOWER(REPLACE(emo.service_type, '_', ' ')) = 'counter wise' "
			+ "                     THEN CONCAT(CAST(emo.helper_quantity AS CHAR), ' + ', CAST(emo.counter_quantity AS CHAR)) "
			+ "                 WHEN LOWER(REPLACE(emo.service_type, '_', ' ')) = 'plate wise' "
			+ "                     THEN CAST(emo.quantity AS CHAR) "
			+ "                 ELSE CAST(emo.quantity AS CHAR) " + "             END "
			+ "         ELSE CAST(emo.quantity AS CHAR) " + "     END AS CHAR " + " ) AS quantity, "
			+ "u.name_english, " + " CASE " + "     WHEN LOWER(REPLACE(emo.service_type, '_', ' ')) = 'counter wise' "
			+ "         THEN (((emo.helper_price + emo.counter_price) * (emo.helper_quantity + emo.counter_quantity)) + IFNULL(emo.shift_trans_price, 0)) "
			+ "     WHEN LOWER(REPLACE(emo.service_type, '_', ' ')) = 'plate wise' "
			+ "         THEN ((emo.price * emo.quantity) + IFNULL(emo.shift_trans_price, 0)) "
			+ "     ELSE ((emo.price * emo.quantity) + IFNULL(emo.shift_trans_price, 0)) "
			+ " END AS total_chef_labour_price, "
			+ " ((emo.price * emo.quantity) + IFNULL(emo.shift_trans_price, 0)) AS total_outside_price, emo.shift_trans_price "
			+ "FROM eventfunction_menuallocation em " + " JOIN memuitems mi ON em.menu_item_id = mi.menu_item_id "
			+ "LEFT JOIN eventfunction_menuallocation_order emo ON emo.menu_allocation_id = em.menu_allocation_id "
			+ "LEFT JOIN event_function ef ON ef.event_function_id = em.eventfunction_id "
			+ "LEFT JOIN functions f ON f.function_id = ef.function_master_id "
			+ "LEFT JOIN menupreparationdetails mpd ON mpd.menu_item_id = em.menu_item_id "
			+ "LEFT JOIN partymaster pm ON pm.party_id = emo.party_id "
			+ "LEFT JOIN units u ON u.unit_id = emo.unit_id " + "WHERE em.event_id = :eventId "
			+ "AND em.chef_labour = :chefLabour " + "AND em.outside = :outsource", nativeQuery = true)
	List<Object[]> getAgencyData2(Long eventId, int lang, boolean chefLabour, boolean outsource);

	MenuItemRawMaterialEntity findByMenuItemAndRawMaterialAndIsDeleteFalse(MenuItemMasterEntity menuItem,
			RawMaterialMasterEntity rawMaterialEntity);

	@Query(value = "SELECT DISTINCT rm.raw_material_cat_id FROM menuallocation_item_rawmaterial mir "
			+ " INNER JOIN rawmaterial rm ON mir.raw_material_id = rm.raw_material_id "
			+ " WHERE mir.event_id = :eventId AND mir.eventfunction_id = :eventFunctionId  "
			+ " AND mir.menu_item_id = :itemId ", nativeQuery = true)
	List<Long> getRawMaterialIds(Long eventId, Long eventFunctionId, Long itemId);

	@Query(value = "SELECT DISTINCT rmc.raw_matrial_cat_id, "
			+ " IF(:lang = 0, rmc.name_english, IF(:lang = 1, rmc.name_hindi, IF(:lang = 2, rmc.name_gujarati, ''))) AS raw_materila_category_name "
			+ "   FROM menuallocation_item_rawmaterial mir "
			+ "   LEFT JOIN rawmaterial rm ON rm.raw_material_id = mir.raw_material_id "
			+ "   LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ "   WHERE mir.event_id = :eventId " + "   AND mir.eventfunction_id = :eventFunctionId "
			+ "   AND mir.menu_item_id = :itemId "
			+ "   AND rmc.raw_matrial_cat_id IN (:rawMaterialIds) ", nativeQuery = true)
	List<Object[]> getRawMaterialCategory(Long eventId, Long eventFunctionId, Long itemId, List<Long> rawMaterialIds,
			int lang);

//	@Query(value = "SELECT DISTINCT ermf.raw_material_cat_id, "
//			+ " IF(:lang = 0, rmc.name_english, IF(:lang = 1, rmc.name_hindi, IF(:lang = 2, rmc.name_gujarati, ''))) AS raw_materila_category_name "
//			+ "   FROM event_raw_material_functions ermf "
//			+ "   LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = ermf.raw_material_cat_id "
//			+ "   WHERE ermf.event_id = :eventId " + "   AND ermf.event_function_id = :eventFunctionId "
//			+ "   AND ermf.item_name = :itemName ", nativeQuery = true)
//	List<Object[]> getRawMaterialCategory(Long eventId, Long eventFunctionId, String itemName, int lang);

//	@Query(value = " "
//			+ " SELECT DISTINCT  "
//			+ "    rm.raw_material_id, "
//			+ "    IF( "
//			+ "        :lang = 0, "
//			+ "        rm.name_english, "
//			+ "        IF( "
//			+ "            :lang = 1, "
//			+ "            rm.name_hindi, "
//			+ "            IF( "
//			+ "                :lang = 2, "
//			+ "                rm.name_gujarati, "
//			+ "                '' "
//			+ "            ) "
//			+ "        ) "
//			+ "    ) AS raw_material_name, "
//			+ "    ermf.qty, "
//			+ "    ermf.price, "
//			+ "    IF( "
//			+ "        :lang = 0, "
//			+ "        u.name_english, "
//			+ "        IF( "
//			+ "            :lang = 1, "
//			+ "            u.name_hindi, "
//			+ "            IF( "
//			+ "                :lang = 2, "
//			+ "                u.name_gujarati, "
//			+ "                '' "
//			+ "            ) "
//			+ "        ) "
//			+ "    ) AS unit_name "
//			+ "FROM event_raw_material_functions ermf "
//			+ "INNER JOIN event_raw_material erm "
//			+ "    ON erm.event_raw_material_id = ermf.event_raw_material_id "
//			+ "INNER JOIN rawmaterial rm "
//			+ "    ON rm.raw_material_id = erm.raw_material_id "
//			+ "LEFT JOIN units u "
//			+ "    ON u.unit_id = ermf.unit_id "
//			+ "WHERE ermf.event_id = :eventId "
//			+ "    AND ermf.event_function_id = :eventFunctionId "
//			+ "    AND ermf.menuitemid = :menuItemId ", nativeQuery = true)
//	List<Object[]> getRawMaterialCategory(Long eventId, Long eventFunctionId, Long menuItemId, int lang);

	@Query(value = "" + " SELECT DISTINCT " + "     rmc.raw_matrial_cat_id AS raw_material_cat_id, " + "     CASE "
			+ "         WHEN :lang = 0 THEN rmc.name_english " + "         WHEN :lang = 1 THEN rmc.name_hindi "
			+ "         WHEN :lang = 2 THEN rmc.name_gujarati " + "         ELSE rmc.name_english "
			+ "     END AS raw_material_category_name " + " FROM event_raw_material_functions ermf "
			+ " INNER JOIN raw_material_category rmc " + "     ON rmc.raw_matrial_cat_id = ermf.raw_material_cat_id "
			+ " WHERE ermf.event_id = :eventId " + "     AND ermf.event_function_id = :eventFunctionId "
			+ "     AND ermf.menuitemid = :menuItemId " + " ORDER BY raw_material_category_name ", nativeQuery = true)
	List<Object[]> getRawMaterialCategory(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId, @Param("menuItemId") Long menuItemId,
			@Param("lang") int lang);

	@Query(value = " SELECT DISTINCT "
			+ " CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS function_name, "
			+ " CASE WHEN :lang = 0 THEN cc.name_english WHEN :lang = 1 THEN cc.name_hindi WHEN :lang = 2 THEN cc.name_gujarati END AS contact_category_name, "
			+ " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS agency_name, "
			+ " el.qty, el.price, el.totalprice, el.laborshift " + " FROM event_labor el "
			+ " LEFT JOIN functions f ON f.function_id = el.event_function_id "
			+ " LEFT JOIN contact_category cc ON cc.contact_category_id = el.contact_category_id "
			+ " LEFT JOIN partymaster pm ON pm.party_id = el.party_id "
			+ " WHERE el.event_id = :eventId ", nativeQuery = true)
	List<Object[]> getLabourData(Long eventId, int lang);

	@Query(value = " SELECT DISTINCT event_id FROM event_raw_material_functions WHERE event_id = :eventId ", nativeQuery = true)
	Long checkEventId(Long eventId);

	@Query(value = " SELECT DISTINCT event_id FROM eventfunction_menuallocation WHERE event_id = :eventId ", nativeQuery = true)
	Long checkEventId2(Long eventId);

	@Query(value = " SELECT DISTINCT event_id " + " FROM event_raw_material_functions " + " WHERE event_id = :eventId "
			+ " AND (:eventFunctionId = -1 OR event_function_id = :eventFunctionId) ", nativeQuery = true)
	Long checkEventIdAndFunctionId(Long eventId, Long eventFunctionId);

	@Query(value = " SELECT DISTINCT event_id " + " FROM eventfunction_menuallocation " + " WHERE event_id = :eventId "
			+ " AND (:eventFunctionId = -1 OR eventfunction_id = :eventFunctionId) ", nativeQuery = true)
	Long checkEventIdAndFunctionId2(Long eventId, Long eventFunctionId);

	@Query("SELECT rm " +
		       "FROM MenuItemRawMaterialEntity rm " +
		       "JOIN FETCH rm.unit " +
		       "JOIN FETCH rm.rawMaterial r " +
		       "JOIN FETCH rm.menuItem " +
		       "WHERE rm.menuItem.id = :menuItemId " +
		       "AND rm.isDelete = false " +
		       "AND (rm.isVisible IS NULL OR rm.isVisible = TRUE) " +
		       "AND NOT EXISTS ( " +
		       "   SELECT 1 " +
		       "   FROM EventFunctionRawmaterialPermissionEntity p " +
		       "   WHERE p.rawMaterialId = r.id " +
		       "   AND p.eventId = :eventId " +
		       "   AND p.eventFunctionId = :eventFunctionId " +
		       "   AND p.type = 'NotPermissable' " +
		       "   AND p.userId = :userId " +
		       ")")
		List<MenuItemRawMaterialEntity> findAllByMenuItemWithRelations(
		        @Param("menuItemId") Long menuItemId,
		        @Param("eventId") Long eventId,
		        @Param("eventFunctionId") Long eventFunctionId,
		        @Param("userId") Long userId);

	@Query(value = "SELECT " + "mirm.menu_item_raw_material_id AS menuItemRawMaterialId, "
			+ "mirm_unit.unit_id AS menuItemRawMaterialUnitId, "
			+ "mirm_unit.name_english AS menuItemRawMaterialUnitName, " + "mirm.weight AS weight, "
			+ "rm.raw_material_id AS rawMaterialId, " + "rm.name_english AS rawMaterialName, "
			+ "rm_unit.unit_id AS rawMaterialUnitId, " + "rm_unit.name_english AS rawMaterialUnitName, "
			+ "mi.menu_item_id AS menuItemId, " + "mi.name_english AS menuItemName, rm.supplier_rate as rate "
			+ "FROM menu_item_raw_material mirm "
			+ "INNER JOIN rawmaterial rm ON mirm.raw_material_id = rm.raw_material_id "
			+ "INNER JOIN units rm_unit ON rm.unit_id = rm_unit.unit_id "
			+ "INNER JOIN units mirm_unit ON mirm.unit_id = mirm_unit.unit_id "
			+ "INNER JOIN memuitems mi ON mirm.menu_item_id = mi.menu_item_id "
			+ "INNER JOIN users u ON mi.user_id = u.user_Id " + "WHERE mirm.is_delete = 0 " + "AND rm.is_delete = 0 "
			+ "AND mi.is_delete = 0 " + "AND u.is_delete = 0 " + "AND u.user_id = :userId "
			+ "AND rm_unit.unit_id != mirm_unit.unit_id "
			+ "AND (mirm_unit.parent_unit_id IS NULL OR mirm_unit.parent_unit_id != rm_unit.unit_id) "
			+ "AND (rm_unit.parent_unit_id IS NULL OR rm_unit.parent_unit_id != mirm_unit.unit_id) "
			+ "ORDER BY menuItemRawMaterialId ASC", nativeQuery = true)
	List<Object[]> findMismatchedUnitsByUserId(Long userId);

	List<MenuItemRawMaterialEntity> findByIdIn(List<Long> ids);

	@Modifying
	@Query(value = "UPDATE menu_item_raw_material " + "SET weight = :weight, " + "unit_id = :unitId, "
			+ "updated_at = :updatedAt " + "WHERE menu_item_raw_material_id = :id", nativeQuery = true)
	int updateMisMatchedUnit(@Param("id") Long id, @Param("weight") BigDecimal weight, @Param("unitId") Long unitId,
			@Param("updatedAt") LocalDateTime updatedAt);

	List<MenuItemRawMaterialEntity> findByUuidAndIsDeleteFalse(String oldUuid);

	List<MenuItemRawMaterialEntity> findAllByRawMaterialAndIsDeleteFalse(RawMaterialMasterEntity entity);

	@Modifying
	@Query(value = "UPDATE menu_item_raw_material mirm "
			+ "JOIN rawmaterial rm ON rm.raw_material_id = mirm.raw_material_id "
			+ "LEFT JOIN units u  ON u.unit_id = mirm.unit_id "
			+ "LEFT JOIN units rmu  ON rmu.unit_id = IFNULL(rm.unit_id , mirm.unit_id) " + "SET mirm.rate = ROUND( "
			+ "rm.supplier_rate * " + "( " + "CASE " + "WHEN u.unit_id = rmu.unit_id " + "THEN IFNULL(mirm.weight,0) "
			+ "WHEN u.unit_id <> rmu.unit_id AND u.is_parent_unit = 1 " + "THEN IFNULL(mirm.weight,0) * 1000 "
			+ "ELSE ROUND(IFNULL(mirm.weight,0) / 1000 , 4) " + "END " + ") " + ",4) "
			+ "WHERE mirm.user_id = :userId", nativeQuery = true)
	void updateLatestMenuItemRawMatRateByUser(@Param("userId") Long userId);

	@Modifying
	@Query(value = " UPDATE menu_item_captain_receipe micr " + " INNER JOIN captain_receipe_master crm "
			+ " 	ON micr.captain_receipe_id = crm.id " + " LEFT JOIN units u " + " 	ON u.unit_id = micr.unit_id "
			+ " LEFT JOIN units rmu " + "     ON rmu.unit_id = IFNULL(crm.unit_id, micr.unit_id) "
			+ " SET micr.rate = ROUND( " + " 	( " + " 		CASE " + " 			WHEN u.unit_id = rmu.unit_id "
			+ " 				THEN (micr.weight * crm.rate)/crm.weight "
			+ " 			WHEN u.unit_id <> rmu.unit_id AND u.is_parent_unit = TRUE "
			+ " 				THEN ((IFNULL(micr.weight, 0) * 1000) * crm.rate) / crm.weight "
			+ " 			ELSE ROUND(((IFNULL(micr.weight, 0) * crm.rate) / crm.weight) / 1000, 4) " + " 		END "
			+ " 	), " + " 	4 " + " ) " + " WHERE  " + " 	micr.user_id = :userId "
			+ " 	AND micr.is_delete = FALSE ", nativeQuery = true)
	void updateLatestMenuItemCaptainReceipeRateByUser(@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query(value = " INSERT INTO menuitem_rawmaterial_rate_dishcosting ( " + "     menu_item_id, " + "     user_id, "
			+ "     total_rate, " + "     dish_costing, " + "     is_active, " + "     is_delete, " + "     UUID, "
			+ "     is_published, " + "     created_at, " + "     updated_at " + " ) " + " SELECT "
			+ "     m.menu_item_id, " + "     m.user_id, " + "     ROUND( "
			+ "         IFNULL(rm.raw_material_rate, 0) " + "         +IFNULL(cr.captain_recipe_rate, 0), "
			+ "         2 " + "     ) AS total_rate, " + "     ROUND( " + "         ( "
			+ "             IFNULL(rm.raw_material_rate, 0) " + "             +IFNULL(cr.captain_recipe_rate, 0) "
			+ "         ) / 100, " + "         2 " + "     ) AS dish_costing, " + "     TRUE, " + "     FALSE, "
			+ "     m.uuid, " + "     FALSE, " + "     NOW(), " + "     NOW() " + " FROM memuitems m " + " LEFT JOIN ( "
			+ "     SELECT " + "         menu_item_id, " + "         user_id, "
			+ "         SUM(IFNULL(rate, 0)) AS raw_material_rate " + "     FROM menu_item_raw_material "
			+ "     WHERE is_delete = FALSE " + "       AND is_active = TRUE " + "       AND user_id = :userId "
			+ "     GROUP BY menu_item_id, user_id " + " ) rm " + "     ON rm.menu_item_id = m.menu_item_id "
			+ "    AND rm.user_id = m.user_id " + " LEFT JOIN ( " + "     SELECT " + "         menu_item_id, "
			+ "         user_id, " + "         SUM(IFNULL(rate, 0)) AS captain_recipe_rate "
			+ "     FROM menu_item_captain_receipe " + "     WHERE is_delete = FALSE " + "       AND is_active = TRUE "
			+ "       AND user_id = :userId " + "     GROUP BY menu_item_id, user_id " + " ) cr "
			+ "     ON cr.menu_item_id = m.menu_item_id " + "    AND cr.user_id = m.user_id "
			+ " WHERE m.user_id = :userId " + " ON DUPLICATE KEY UPDATE " + "     total_rate = VALUES(total_rate), "
			+ "     dish_costing = VALUES(dish_costing), " + "     updated_at = NOW(); ", nativeQuery = true)
	void upsertDishCostingRate(@Param("userId") Long userId);

	@Query(value = "select mirm.menu_item_raw_material_id, mirm.weight, mirm.unit_id, u.name_english as unitName, mirm.menu_item_id as menuItemId, "
			+ " mi.name_english as itemName, mirm.raw_material_id, rm.name_english as rawMaterialName,rm.unit_id as rawMatUnitId, ur.name_english,rm.supplier_rate,mi.menu_item_id as itemId, "
			+ " mirm.is_visible AS is_visible  " + "FROM  menu_item_raw_material mirm "
			+ "JOIN memuitems mi ON mirm.menu_item_id = mi.menu_item_id "
			+ "JOIN rawmaterial rm ON mirm.raw_material_id = rm.raw_material_id "
			+ "JOIN units u ON mirm.unit_id = u.unit_id " + "JOIN  units ur ON rm.unit_id = ur.unit_id "
			+ "WHERE mirm.raw_material_id = :rawMaterialId AND mirm.user_id = :userId AND mirm.is_delete = FALSE "
			+ " AND mi.is_delete = FALSE AND  rm.is_delete = FALSE AND u.is_delete = FALSE  AND  ur.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getAllByRawMaterial(Long rawMaterialId, Long userId);

	@Query(value = "" + "	SELECT  " + "		mi.name_english AS item_name_english, "
			+ "		rm.name_english AS raw_material_name_english, " + "		mirm.weight AS weight, "
			+ "		u.name_english AS unit_name_english " + "	FROM menu_item_raw_material mirm "
			+ "	INNER JOIN memuitems mi " + "		ON mirm.menu_item_id = mi.menu_item_id "
			+ "		AND mi.is_delete = FALSE " + "		AND mi.user_id = :userId " + "	INNER JOIN rawmaterial rm "
			+ "		ON rm.raw_material_id = mirm.raw_material_id " + "		AND rm.is_delete = FALSE "
			+ "		AND rm.user_id = :userId " + "	LEFT JOIN units u " + "		ON u.unit_id = mirm.unit_id "
			+ "		AND u.is_delete = FALSE " + "		AND u.user_id = :userId " + "	WHERE mirm.is_delete = FALSE "
			+ "		AND mi.user_id = :userId " + "	ORDER BY mi.sequence ASC", nativeQuery = true)
	List<Object[]> getMenuItemRawMaterialExportData(@Param("userId") Long userId);

	@Query(value = "" + " SELECT  " + "		efma.menu_item_id " + "	FROM eventfunction_menuallocation efma "
			+ "	INNER JOIN eventfunction_menuallocation_order efmao ON efma.menu_allocation_id = efmao.menu_allocation_id "
			+ "	WHERE event_id = :eventId "
			// + " and eventfunction_id = 2873 "
			+ "	AND efma.is_delete = FALSE " + "	AND efmao.is_delete = FALSE "
			+ "	AND efma.outside = TRUE ", nativeQuery = true)
	List<Long> getOutsourceItemAllocatedId(@Param("eventId") Long eventId);

}
