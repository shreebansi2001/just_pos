package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.NameplateEntity;

@Repository
public interface NameplateRepository extends JpaRepository<NameplateEntity, Long> {

	Optional<NameplateEntity> findByIdAndIsDeleteFalse(Long id);

	@Query(value = "SELECT DISTINCT efnp.id, CASE WHEN efnp.menu_item_id IS NULL THEN mpd.menu_item_id ELSE efnp.menu_item_id END AS menu_item_id, "
			+ " CASE WHEN efnp.menu_item_id IS NULL THEN mpd.menuitem_name ELSE CASE WHEN efnp.item_name_english IS NULL OR LENGTH(TRIM(efnp.item_name_english)) = 0 THEN mpd.menuitem_name ELSE efnp.item_name_english END END AS item_name_english, "
			+ " CASE WHEN efnp.menu_item_id IS NULL THEN mpd.menuitem_name_hindi  ELSE CASE WHEN efnp.item_name_hindi IS NULL OR LENGTH(TRIM(efnp.item_name_hindi)) = 0 THEN mpd.menuitem_name_hindi ELSE efnp.item_name_hindi END END AS item_name_hindi, "
			+ " CASE WHEN efnp.menu_item_id IS NULL THEN mpd.menuitem_name_gujarati ELSE CASE WHEN efnp.item_name_gujarati IS NULL OR LENGTH(TRIM(efnp.item_name_gujarati)) = 0 THEN mpd.menuitem_name_gujarati ELSE efnp.item_name_gujarati END END AS item_name_gujarati, "
			+ " CASE WHEN efnp.menu_item_id IS NULL THEN 1 ELSE efnp.item_count END AS item_count,"
			+ " efnp.item_font_size, "
			+ " efnp.category_font_size,"
			+ " e.event_id, "
			+ " efnp.event_function_id, "
			+ " efnp.sequence,"
			+ " e.event_no,"
			+ " IFNULL(efnp.is_standy_checked, 1) AS is_standy_checked, "
			+ " IFNULL(efnp.is_table_menu_checked, 1) AS is_table_menu_checked, "
			+ " FROM menupreparationdetails mpd "
			+ " LEFT JOIN event_function_name_plate efnp ON mpd.menu_item_id = efnp.menu_item_id "
			+ " LEFT JOIN menupreparation mp ON mp.menu_preparation_id = mpd.menu_preparation_id "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
			+ " LEFT JOIN events e ON e.event_id = ef.event_id "
			+ " WHERE e.user_id = :userId AND e.event_id = :eventId AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) "
			+ " ORDER BY efnp.sequence ",
			nativeQuery = true)
	List<Object[]> findAllNamePlateItems(Long eventId, Long eventFunctionId, Long userId);
	
	@Query(value =
		    "SELECT " +
		    " MAX(efnp.id) AS id, " +

		    " COALESCE(efnp.menu_item_id, mpd.menu_item_id) AS menu_item_id, " +

		    " MAX(COALESCE(NULLIF(TRIM(efnp.item_name_english), ''), mpd.menuitem_name)) AS item_name_english, " +
		    " MAX(COALESCE(NULLIF(TRIM(efnp.item_name_hindi), ''), mpd.menuitem_name_hindi)) AS item_name_hindi, " +
		    " MAX(COALESCE(NULLIF(TRIM(efnp.item_name_gujarati), ''), mpd.menuitem_name_gujarati)) AS item_name_gujarati, " +

		    " MAX(COALESCE(efnp.item_count, 1)) AS item_count, " +

		    " MAX(efnp.item_font_size) AS item_font_size, " +
		    " MAX(efnp.category_font_size) AS category_font_size, " +

		    " MAX(e.event_id) AS event_id, " +
		    " MAX(efnp.event_function_id) AS event_function_id, " +
		    " MIN(efnp.sequence) AS sequence, " +
		    " MAX(e.event_no) AS event_no, " +

		    " 0 AS is_standy_checked, " +
		    " 0 AS is_table_menu_checked, " +

		    " COALESCE(MAX(efnp.is_counter_item), :isCounterItem) AS counter_item, " +
		    " COALESCE(MAX(efnp.is_standy_item), :isStandyItem) AS standy_item, " +
		    " COALESCE(MAX(efnp.is_table_menu_item), :isTableMenuItem) AS table_menu_item, " +

		    " MAX(efnp.header_notes_english) AS header_notes_english, " +
		    " MAX(efnp.header_notes_hindi) AS header_notes_hindi, " +
		    " MAX(efnp.header_notes_gujarati) AS header_notes_gujarati, " +
		    " MAX(efnp.footer_notes_english) AS footer_notes_english, " +
		    " MAX(efnp.footer_notes_hindi) AS footer_notes_hindi, " +
		    " MAX(efnp.footer_notes_gujarati) AS footer_notes_gujarati " +

		    "FROM menupreparationdetails mpd " +

		    "LEFT JOIN event_function_name_plate efnp " +
		    " ON mpd.menu_item_id = efnp.menu_item_id " +
		    " AND efnp.is_counter_item = :isCounterItem " +
		    " AND efnp.is_standy_item = :isStandyItem " +
		    " AND efnp.is_table_menu_item = :isTableMenuItem " +
		    " AND efnp.event_id = :eventId " +

		    "LEFT JOIN menupreparation mp " +
		    " ON mp.menu_preparation_id = mpd.menu_preparation_id " +

		    "LEFT JOIN event_function ef " +
		    " ON ef.event_function_id = mp.event_function_id " +

		    "INNER JOIN events e " +
		    " ON e.event_id = ef.event_id " +

		    "WHERE e.user_id = :userId " +
		    " AND e.event_id = :eventId " +
		    " AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) " +

		    "GROUP BY COALESCE(efnp.menu_item_id, mpd.menu_item_id) " +

		    "ORDER BY " +
		    " MIN(CASE WHEN efnp.sequence IS NULL THEN ef.sortorder ELSE efnp.sequence END), " +
		    " MIN(CASE WHEN efnp.sequence IS NULL THEN mpd.menu_sortorder ELSE efnp.sequence END), " +
		    " MIN(CASE WHEN efnp.sequence IS NULL THEN mpd.item_sortorder ELSE 0 END)",

		    nativeQuery = true)
		List<Object[]> findAllNamePlateItems(
		    Long eventId,
		    Long eventFunctionId,
		    Long userId,
		    Integer isCounterItem,
		    Integer isStandyItem,
		    Integer isTableMenuItem
		);

	@Query(value = " SELECT DISTINCT efnp.id, mpd.menu_category_id, "
			+ " CASE WHEN :lang = 0 THEN mpd.menu_category_name WHEN :lang = 1 THEN mpd.menu_category_name_hindi WHEN :lang = 2 THEN mpd.menu_category_name_gujarati END AS category_name, "
			+ " efnp.menu_item_id, "
			+ " CASE WHEN :lang = 0 THEN efnp.item_name_english WHEN :lang = 1 THEN efnp.item_name_hindi WHEN :lang = 2 THEN efnp.item_name_gujarati END AS item_name, "
			+ " efnp.item_count, "
			+ " efnp.item_font_size, "
			+ " efnp.category_font_size, "
			+ " e.event_id, "
			+ " efnp.event_function_id, "
			+ " efnp.sequence, "
			+ " e.event_no "
			+ " FROM event_function_name_plate efnp"
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_item_id = efnp.menu_item_id AND efnp.event_id = :eventId "
			+ " LEFT JOIN menupreparation mp ON mp.menu_preparation_id = mpd.menu_preparation_id "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
			+ " INNER JOIN events e ON e.event_id = ef.event_id AND e.event_id = efnp.event_id "
			+ " WHERE e.user_id = :userId AND e.event_id = :eventId "
			+ " AND efnp.event_id = :eventId "
			+ " AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) "
			+ " AND (:eventFunctionId = -1 OR efnp.event_function_id = :eventFunctionId) "
			+ " AND (:catId = -1 OR mpd.menu_category_id = :catId) "
			+ " AND efnp.is_counter_item = :isCounterNamePlate "
			+ " AND efnp.is_standy_item = :isStandyNamePlate "
			+ " AND efnp.is_table_menu_item = :isTableMenuNamePlate "
			+ " AND efnp.is_standy_checked = :isStandyNamePlate "
			+ " AND efnp.is_table_menu_checked = :isTableMenuNamePlate "
			+ " AND efnp.is_delete = FALSE "
			+ " ORDER BY efnp.sequence ",
			nativeQuery = true)
	List<Object[]> findAllNamePlateItemsWithCategory(Long eventId, Long eventFunctionId, Long catId, Integer lang, 
			Long userId, Integer isCounterNamePlate, Integer isStandyNamePlate, Integer isTableMenuNamePlate);

	@Query(value = " SELECT DISTINCT efnp.id, mpd.menu_category_id, "
			+ " CASE WHEN :defaultLanguage = 'en' THEN mpd.menu_category_name WHEN :defaultLanguage = 'hi' THEN mpd.menu_category_name_hindi WHEN :defaultLanguage = 'gu' THEN mpd.menu_category_name_gujarati END AS category_name_def, "
			+ " CASE WHEN :preferedLanguage = 'en' THEN mpd.menu_category_name WHEN :preferedLanguage = 'hi' THEN mpd.menu_category_name_hindi WHEN :preferedLanguage = 'gu' THEN mpd.menu_category_name_gujarati END AS category_name_pref, "
			+ " efnp.menu_item_id, "
			+ " CASE WHEN :defaultLanguage = 'en' THEN efnp.item_name_english WHEN :defaultLanguage = 'hi' THEN efnp.item_name_hindi WHEN :defaultLanguage = 'gu' THEN efnp.item_name_gujarati END AS item_name_def, "
			+ " CASE WHEN :preferedLanguage = 'en' THEN efnp.item_name_english WHEN :preferedLanguage = 'hi' THEN efnp.item_name_hindi WHEN :preferedLanguage = 'gu' THEN efnp.item_name_gujarati END AS item_name_pref, "
			+ " efnp.item_count, "
			+ " efnp.item_font_size, "
			+ " efnp.category_font_size, "
			+ " e.event_id, "
			+ " efnp.event_function_id, "
			+ " efnp.sequence, "
			+ " e.event_no "
			+ " FROM event_function_name_plate efnp"
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_item_id = efnp.menu_item_id AND efnp.event_id = :eventId "
			+ " LEFT JOIN menupreparation mp ON mp.menu_preparation_id = mpd.menu_preparation_id "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
			+ " INNER JOIN events e ON e.event_id = ef.event_id AND e.event_id = efnp.event_id  "
			+ " WHERE e.user_id = :userId AND e.event_id = :eventId "
			+ " AND efnp.event_id = :eventId "
			+ " AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) "
			+ " AND (:eventFunctionId = -1 OR efnp.event_function_id = :eventFunctionId) "
			+ " AND (:catId = -1 OR mpd.menu_category_id = :catId) "
			+ " AND efnp.is_counter_item = :isCounterNamePlate "
			+ " AND efnp.is_standy_item = :isStandyNamePlate "
			+ " AND efnp.is_table_menu_item = :isTableMenuNamePlate "
			+ " AND efnp.is_delete = FALSE "
			+ " ORDER BY efnp.sequence ",
			nativeQuery = true)
	List<Object[]> findAllNamePlateItemsWithCategory(Long eventId, Long eventFunctionId, Long catId, 
			String defaultLanguage, String preferedLanguage, Long userId, Integer isCounterNamePlate, Integer isStandyNamePlate, Integer isTableMenuNamePlate);

	
	@Query(value = " SELECT DISTINCT mpd.menu_category_id, "
			+ " CASE WHEN :lang = 0 THEN mpd.menu_category_name WHEN :lang = 1 THEN mpd.menu_category_name_hindi WHEN :lang = 2 THEN mpd.menu_category_name_gujarati END AS category_name, "
			+ " efnp.category_font_size, "
			+ " e.event_id, "
			+ " efnp.event_function_id, "
			+ " e.event_no, "
			+ " efnp.sequence, "
			+ " efnp.header_notes_english,"
			+ " efnp.header_notes_hindi,"
			+ " efnp.header_notes_gujarati,"
			+ " efnp.footer_notes_english,"
			+ " efnp.footer_notes_hindi,"
			+ " efnp.footer_notes_gujarati "
			+ " FROM menupreparationdetails mpd "
			+ " LEFT JOIN event_function_name_plate efnp ON mpd.menu_item_id = efnp.menu_item_id AND efnp.event_id = :eventId "
			+ " LEFT JOIN menupreparation mp ON mp.menu_preparation_id = mpd.menu_preparation_id "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
			+ " INNER JOIN events e ON e.event_id = ef.event_id AND e.event_id = efnp.event_id "
			+ " WHERE e.user_id = :userId "
			+ " AND e.event_id = :eventId  "
			+ " AND efnp.is_counter_item = :isCounterNamePlate "
			+ " AND efnp.is_standy_item = :isStandyNamePlate "
			+ " AND efnp.is_table_menu_item = :isTableMenuNamePlate "
			+ " AND (:eventFunctionId = 0 OR efnp.event_function_id = :eventFunctionId)  "
			+ " ORDER BY efnp.sequence ",
			nativeQuery = true)
	List<Object[]> findNamePlateCategory(Long eventId, Long eventFunctionId, Integer lang, Long userId,
			Integer isCounterNamePlate, Integer isStandyNamePlate, Integer isTableMenuNamePlate);

	void deleteAllByEventAndEventFunctionId(EventMasterEntity eventMasterEntity, Long eventFunctionId);


}
