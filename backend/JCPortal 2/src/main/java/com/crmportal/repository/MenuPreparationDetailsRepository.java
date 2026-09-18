package com.crmportal.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MenuAllocationOrdersEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuPreparationDetailsEntity;
import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.response.dto.EventAndFunctionWisePartyResponseDto;
import com.crmportal.response.dto.MenuCategoryForPreparationResponseDto;
import com.crmportal.response.dto.MenuCategoryMasterResponseDto;
import com.crmportal.response.dto.RemaingDataResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;

import lombok.val;

@Repository
public interface MenuPreparationDetailsRepository extends JpaRepository<MenuPreparationDetailsEntity, Long> {

	void deleteAllByMenuPreparation(MenuPreparationEntity menuPreparationEntity);

	@Query("SELECT new com.crmportal.response.dto.MenuCategoryForPreparationResponseDto(" +
		       "mpd.menuCategory.id, " +
		       "mpd.menuCategoryName, " +
		       "mpd.menuSortOrder, " +
		       "mpd.menuSlogan, " +
		       "mpd.menuNotes, " +
		       "mpd.menuCategoryNameHindi, " +
		       "mpd.menuCategoryNameGujarati, " +
		       "mpd.menuNotesHindi, " +
		       "mpd.menuNotesGujarati, " +
		       "mpd.startTime, " +
		       "mpd.isMenuCatAddons, " +
		       "mpd.catImgId, " +
		       "mpd.bgImgId, " +
		       "mpd.catSpace, " +
		       "mpd.anyItem, " +     
		       "mpd.subCat," +
		       "mpd.subCatHindi," +
		       "mpd.subCatGujarati,"
		       + " mpd.catNickNameEnglish, "
		       + " mpd.catNickNameHindi, "
		       + " mpd.catNickNameGujarati, "
		       + " mpd.categoryStatus, "
		       + " mpd.changedAfterCompletion, "
		       + " mpd.catHeadingEnglish, "
		       + " mpd.catHeadingHindi, "
		       + " mpd.catHeadingGujarati ) " +     
		       "FROM MenuPreparationDetailsEntity mpd " +
		       "WHERE mpd.menuPreparation.eventFunction.id = :eventFunctionId " +
		       "AND mpd.menuPreparation.isDelete = false " +
		       "GROUP BY mpd.menuCategory.id, mpd.menuCategoryName, mpd.menuSortOrder, " +
		       "mpd.menuSlogan, mpd.menuNotes, mpd.menuCategoryNameHindi, mpd.menuCategoryNameGujarati, " +
		       "mpd.menuNotesHindi, mpd.menuNotesGujarati, mpd.startTime, mpd.isMenuCatAddons, " +
		       "mpd.catImgId, mpd.bgImgId, mpd.catSpace, mpd.anyItem, mpd.subCat, mpd.subCatHindi, mpd.subCatGujarati, "
		       + " mpd.catNickNameEnglish, mpd.catNickNameHindi, mpd.catNickNameGujarati,mpd.categoryStatus, "
		       + "mpd.changedAfterCompletion, " 
		       + " mpd.catHeadingEnglish, "
		       + " mpd.catHeadingHindi, "
		       + " mpd.catHeadingGujarati "+
		       "ORDER BY mpd.menuSortOrder ASC")
		List<MenuCategoryForPreparationResponseDto> findAllByEventFunctionIdAndIsDeleteFalse(
		        @Param("eventFunctionId") Long eventFunctionId);

	List<MenuPreparationDetailsEntity> findAllByMenuCategory(MenuCategoryMasterEntity menuCategoryMasterEntity);

	List<MenuPreparationDetailsEntity> findAllByMenuCategoryIdIn(List<Long> categoryIds);

	List<MenuPreparationDetailsEntity> findAllByMenuCategoryAndMenuPreparationOrderByMenuSortOrderAscItemSortOrderAsc(
			MenuCategoryMasterEntity category, MenuPreparationEntity menuPreparationEntity);

	Optional<MenuPreparationDetailsEntity> findByMenuPreparationAndMenuItemAndMenuCategory(
			MenuPreparationEntity menuPreparation, MenuItemMasterEntity item, MenuCategoryMasterEntity category);

	@Query(value = "SELECT " + " mpd.menu_item_id, " + " mpd.menuitem_name, " + " mpd.menu_category_id, "
			+ " mpd.menu_category_name, " + " miac.select_outside_agency, " + " miac.allocation_type, "
			+ " COALESCE(miac.base_price, 0) AS base_price, " + " miac.counter_no, " + " miac.godown_location, "
			+ " COALESCE(miac.price_per_helper, 0) AS price_per_helper, "
			+ " COALESCE(miac.price_per_labour, 0) AS price_per_labour, "
			+ " COALESCE(miac.quantity_per_100_person, 0) AS quantity_per_100_person, " + " p.party_id AS party_id, "
			+ " p.name_english AS party_name, " + " u.unit_id AS unit_id, " + " u.name_english AS unit_name, "
			+ " mp.is_update AS is_update, " + " miac.select_chef_labour_agency, " + " mpd.item_notes, "
			+ " miac.number, miac.remarks, mpd.menu_category_name_gujarati, mpd.menu_category_name_hindi,  "
			+ " mpd.menuitem_name_gujarati, mpd.menuitem_name_hindi, " + " mpd.menu_sortorder , mpd.item_sortorder, "
		    + " p.name_gujarati AS party_name_gujarati, p.name_hindi AS party_name_hindi, mpd.item_notes_gujarati, mpd.item_notes_hindi  "
			+ " FROM menupreparationdetails mpd " + "JOIN memuitems mi ON mpd.menu_item_id = mi.menu_item_id "
			+ "JOIN menupreparation mp ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ "LEFT JOIN menu_item_allocation_config miac ON mi.menu_item_id = miac.menu_item_id "
			+ "LEFT JOIN partymaster p ON miac.party_id = p.party_id "
			+ "LEFT JOIN units u ON miac.unit_id = u.unit_id "
			+ "WHERE mp.event_function_id = :eventFunctionId ORDER BY mpd.menu_sortorder , mpd.item_sortorder", nativeQuery = true)
	List<Object[]> getAllMenuPreparationDetails(@Param("eventFunctionId") Long eventFunctionId);

	@Query(value = "SELECT " + " mpd.menu_item_id, " + " mpd.menuitem_name, " + " mpd.menu_category_id, "
			+ " mpd.menu_category_name, " + " miac.select_outside_agency, " + " miac.allocation_type, "
			+ " COALESCE(miac.base_price, 0) AS base_price, " + " miac.counter_no, " + " miac.godown_location, "
			+ " COALESCE(miac.price_per_helper, 0) AS price_per_helper, "
			+ " COALESCE(miac.price_per_labour, 0) AS price_per_labour, "
			+ " COALESCE(miac.quantity_per_100_person, 0) AS quantity_per_100_person, " + " p.party_id AS party_id, "
			+ " p.name_english AS party_name, " + " u.unit_id AS unit_id, " + " u.name_english AS unit_name, "
			+ " mp.is_update AS is_update, " + " miac.select_chef_labour_agency, " + " mpd.item_notes, "
			+ " miac.number, miac.remarks, mpd.menu_category_name_gujarati, mpd.menu_category_name_hindi, mpd.menuitem_name_gujarati, mpd.menuitem_name_hindi"
			+ " FROM menupreparationdetails mpd " + "JOIN memuitems mi ON mpd.menu_item_id = mi.menu_item_id "
			+ "JOIN menupreparation mp ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ "LEFT JOIN menu_item_allocation_config miac ON mi.menu_item_id = miac.menu_item_id "
			+ "LEFT JOIN partymaster p ON miac.party_id = p.party_id "
			+ "LEFT JOIN units u ON miac.unit_id = u.unit_id " + "WHERE mp.event_function_id = :eventFunctionId AND ( "
			+ " (:type = 'OUTSIDE' AND miac.select_outside_agency = 1) "
			+ " OR (:type = 'CHEF' AND miac.select_chef_labour_agency = 1) "
			+ " OR (:type = 'INSIDE' AND (miac.select_inside_agency = 1 OR (miac.select_outside_agency = 0 OR miac.select_outside_agency IS NULL) AND (miac.select_chef_labour_agency = 0 OR miac.select_chef_labour_agency IS NULL) AND (miac.select_inside_agency = 0 OR miac.select_inside_agency IS NULL)))) ORDER BY mpd.menu_sortorder , mpd.item_sortorder", nativeQuery = true)
	List<Object[]> getAllMenuPreparationDetails(@Param("eventFunctionId") Long eventFunctionId,
			@Param("type") String type);

	// ─── Replace your existing @Query with this ───────────────────────────────────
	// Changes from original:
	//   1. Added vendorName column at the END of SELECT (index 47)
	//   2. Added LEFT JOIN eventfunction_menuallocation ema
	//   3. Added LEFT JOIN eventfunction_menuallocation_order emao
	//   4. Added LEFT JOIN partymaster vpm (vendor/chef)
	//
	// Join logic:
	//   mpd.menu_item_id + mpd's event_function_id  → ema (eventfunction_menuallocation)
	//   ema.menu_allocation_id                       → emao (menuallocation_order)
	//   emao.party_id                                → vpm (partymaster = chef/vendor)

	@Query(value = "SELECT e.event_id, e.event_no, e.event_end_date_time AS event_end_timestamp, "
	        + " e.event_start_date_time AS event_start_timestamp, "
	        + " CASE WHEN :lang = 0 THEN v.name_english WHEN :lang = 1 THEN v.name_hindi WHEN :lang = 2 THEN v.name_gujarati END AS venue, "
	        + " et.event_type_id AS event_type_id, "
	        + " CASE WHEN :lang = 0 THEN et.name_english WHEN :lang = 1 THEN et.name_hindi WHEN :lang = 2 THEN et.name_gujarati END AS event_name, "
	        + " CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS fn_name, "
	        + " CASE WHEN :lang = 0 THEN mc.name_english WHEN :lang = 1 THEN mc.name_hindi WHEN :lang = 2 THEN mc.name_gujarati END AS cat_name, "
	        + " CASE WHEN :lang = 0 THEN mi.name_english WHEN :lang = 1 THEN mi.name_hindi WHEN :lang = 2 THEN mi.name_gujarati END AS item_name, "
	        + " ef.event_function_id AS fn_id, ef.function_start_date_time AS function_start_timestamp, "
	        + " ef.function_end_date_time AS function_end_timestamp, ef.function_venue AS func_venue, ef.pax AS pax, ef.rate AS rate, "
	        + " mp.default_price AS default_price, mpd.menu_category_id AS menu_cat_id, "
	        + " CASE WHEN :lang = 0 THEN mpd.menu_notes WHEN :lang = 1 THEN mpd.menu_notes_hindi WHEN :lang = 2 THEN mpd.menu_notes_gujarati END AS cat_notes, "
	        + " mpd.menu_slogan AS cat_slogan, "
	        + " mpd.menu_item_id AS item_id, mpd.item_slogan AS item_slogan, mpd.item_price AS item_price, "
	        + " mc.image_path AS cat_image_path, "
	        + " CAST(mi.image_path AS CHAR) AS item_image_path, "
	        + " CAST(mpd.starttime AS CHAR) AS catStartTime, "
	        + " CASE WHEN :lang = 0 THEN e.remark WHEN :lang = 1 THEN e.remarks_hindi WHEN :lang = 2 THEN e.remarks_gujarati END AS event_remark, "
	        + " CASE WHEN :lang = 0 THEN mpd.item_notes WHEN :lang = 1 THEN mpd.item_notes_hindi WHEN :lang = 2 THEN mpd.item_notes_gujarati END AS item_notes, "
	        + " e.meal_notes AS food_notes, "
	        + " CASE WHEN :lang = 0 THEN mt.name_english WHEN :lang = 1 THEN mt.name_hindi WHEN :lang = 2 THEN mt.name_gujarati END AS food_type, "
	        + " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS partyName, "
	        + " pm.mobileno, ud.company_name, ud.office_no, ud.address, "
	        + " (SELECT MAX(ef2.pax) FROM event_function ef2 WHERE ef2.event_id = e.event_id) AS max_pax, "
	        + " u.first_name, u.last_name, ud.country_code, u.email, u.logo, "
	        + " CASE WHEN :defaultLanguage = 'en' THEN mc.name_english WHEN :defaultLanguage = 'hi' THEN mc.name_hindi WHEN :defaultLanguage = 'gu' THEN mc.name_gujarati END AS cat_name_def, "
	        + " CASE WHEN :preferedLanguage = 'en' THEN mc.name_english WHEN :preferedLanguage = 'hi' THEN mc.name_hindi WHEN :preferedLanguage = 'gu' THEN mc.name_gujarati END AS cat_name_pref, "
	        + " CASE WHEN :defaultLanguage = 'en' THEN mi.name_english WHEN :defaultLanguage = 'hi' THEN mi.name_hindi WHEN :defaultLanguage = 'gu' THEN mi.name_gujarati END AS item_name_def, "
	        + " CASE WHEN :preferedLanguage = 'en' THEN mi.name_english WHEN :preferedLanguage = 'hi' THEN mi.name_hindi WHEN :preferedLanguage = 'gu' THEN mi.name_gujarati END AS item_name_pref, "
	        + " mp.is_package AS is_package, mp.package_price AS package_price, "
	        // ── NEW: chef/vendor name from partymaster via menuallocation_order (index 47)
	        + " CAST(vpm.name_english AS CHAR CHARACTER SET utf8) AS vendorName,"
	        + " e.billing_name_english AS billing_name_english, "
	        + " e.billing_name_hindi AS billing_name_hindi,"
	        + " e.billing_name_gujarati AS billing_name_gujarati, "
	        + " e.meal_notes_hindi AS meal_notes_hindi, "
	        + " e.meal_notes_gujarati AS meal_notes_gujarati, "
	        + " ef.function_venue_hindi AS function_venue_hindi, "
	        + "	ef.function_venue_gujarati AS function_venue_gujarati, "
	        + " e.service_hindi AS service_hindi,"
	        + " e.service_gujarati AS service_gujarati,"
	        + " e.theme_hindi AS theme_hindi, "
	        + " e.theme_gujarati AS theme_gujarati,"
	        + " e.service AS service_english,"
	        + " e.theme AS theme_english, "
	        + " ef.notes_english AS function_notes_english, "
	        + " ef.notes_hindi AS function_notes_hindi, "
	        + " ef.notes_gujarati AS function_notes_gujarati,"
	        + " mpd.sub_cat AS sub_cat_english,"
	        + " mpd.sub_cat_hindi AS sub_cat_hindi,"
	        + " mpd.sub_cat_gujarati AS sub_cat_gujarati,"
	        + " mpd.sub_item AS sub_item,"
	        + " mpd.sub_item_hindi AS sub_item_hindi,"
	        + " mpd.sub_item_gujarati AS sub_item_gujarati,"
	        + " ef.food_type AS function_food_type, "
	        + " ef.main_function AS main_function, "
	        + " ef.rate_post_fix AS rate_post_fix,"
	        + " e.banquet_hall_id AS banquet_hall_id, "
	        + " bhm.hall_name AS hall_name, "
	        + " mpd.cat_space, "
	        + " mpd.item_space, "
	        + " mpd.is_cat_image AS is_cat_image,"
	        + " CASE WHEN :lang = 0 THEN mpd.cat_nick_name_english WHEN :lang = 1 THEN mpd.cat_nick_name_hindi WHEN :lang = 2 THEN mpd.cat_nick_name_gujarati END AS cat_nick_name, "
	        + " CASE WHEN :lang = 0 THEN mpd.item_nick_name_english WHEN :lang = 1 THEN mpd.item_nick_name_hindi WHEN :lang = 2 THEN mpd.item_nick_name_gujarati END AS item_nick_name, "
	        + " 0 AS item_qty, "
	        + " em.user_id AS manager_id,"
	        + " CONCAT(em.first_name, ' ', em.last_name) AS manager_name, "
	        + " CASE WHEN :lang = 0 THEN cp.name_english WHEN :lang = 1 THEN cp.name_hindi WHEN :lang = 2 THEN cp.name_gujarati END AS package_name,"
	        + " cp.price AS pack_price,"
	        + " CASE WHEN :lang = 0 THEN e.cordinator_person_name_english WHEN :lang = 1 THEN e.cordinator_person_name_hindi WHEN :lang = 2 THEN e.cordinator_person_name_gujarati END AS cordination_person_name, "
	        + " e.cordinator_person_contact_no AS cordination_person_contact_no,"
	        + " mpd.category_status AS category_status, "
	        + " mpd.item_status AS item_status,"
	        + " CASE WHEN :lang = 0 THEN pm.address_english WHEN :lang = 1 THEN pm.address_hindi WHEN :lang = 2 THEN pm.address_gujarati END AS party_address, "
	        + " e.permissable_item,e.not_permissable_item,e.reference, "
	        + " v.img_path AS venue_img, "
	        + " CASE WHEN :lang = 0 THEN mpd.item_heading WHEN :lang = 1 THEN mpd.item_heading_hindi WHEN :lang = 2 THEN mpd.item_heading_gujarati END AS itemHeading, "
	        + " e.prefix,"
	        + " mpd.is_menu_cat_addons AS cat_addons, "
	        + " mpd.is_item_addons AS item_addons,"
	        + " mpd.cat_heading_english AS cat_heading_english,"
	        + " mpd.cat_heading_hindi AS cat_heading_hindi,"
	        + " mpd.cat_heading_gujarati AS cat_heading_gujarati,"
	        + " pm.alt_mobileno AS alternate_mobile "
	        + " FROM `events` e "
	        + " LEFT JOIN eventtype et ON et.event_type_id = e.event_type_id "
	        + " LEFT JOIN venuemaster v ON v.venue_id = e.venue_id "
	        + " LEFT JOIN mealtype mt ON e.meal_type_id = mt.meal_type_id "
	        + " LEFT JOIN partymaster pm ON e.party_id = pm.party_id "
	        + " LEFT JOIN event_function ef ON ef.event_id = e.event_id "
	        + " LEFT JOIN functions f ON f.function_id = ef.function_master_id "
	        + " LEFT JOIN menupreparation mp ON ef.event_function_id = mp.event_function_id "
	        + " LEFT JOIN menupreparationdetails mpd ON mp.menu_preparation_id = mpd.menu_preparation_id "
	        + " LEFT JOIN menucategory mc ON mc.menu_category_id = mpd.menu_category_id "
	        + " LEFT JOIN memuitems mi ON mi.menu_item_id = mpd.menu_item_id "
	        // ── NEW JOINs to get chef/vendor ─────────────────────────────────────
	        // Step 1: match allocation by menu_item_id + event_function_id
	        + " LEFT JOIN eventfunction_menuallocation ema "
	        + "     ON ema.menu_item_id = mpd.menu_item_id "
	        + "     AND ema.menu_category_id = mpd.menu_category_id "
	        + "     AND ema.eventfunction_id = ef.event_function_id "
	        // Step 2: join on key only — MySQL does not support non-key column filters in ON clause
	        + " LEFT JOIN eventfunction_menuallocation_order emao "
	        + "     ON emao.menu_allocation_id = ema.menu_allocation_id "
	        // Step 3: get vendor/chef name from partymaster
	        + " LEFT JOIN partymaster vpm ON vpm.party_id = emao.party_id "
	        // ─────────────────────────────────────────────────────────────────────
	        + " LEFT JOIN users u ON e.user_id = u.user_id "
	        + " LEFT JOIN user_basic_details ud ON e.user_id = ud.user_id "
	        + " LEFT JOIN banquet_hall_master bhm ON bhm.id = e.banquet_hall_id "
	        + " LEFT JOIN users em ON em.user_id = e.manager_id "
	        + " LEFT JOIN custompackage cp ON cp.custom_package_id = ef.custom_package_id  "
	        + " WHERE e.event_id = :eventId AND e.user_id = :user "
	        + " AND mpd.menu_category_id IS NOT NULL "
	        + " AND mpd.menu_item_id IS NOT NULL "
	        + " AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) "
	        + " AND ef.is_delete = FALSE "
	        + " ORDER BY ef.sortorder ASC, mpd.menu_sortorder ASC, mpd.item_sortorder ASC",
	        nativeQuery = true)
	List<Object[]> getEventMenuReportRaw(@Param("eventId") Long eventId,
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("lang") int lang,
	        @Param("user") Long user,
	        @Param("defaultLanguage") String defaultLanguage,
	        @Param("preferedLanguage") String preferedLanguage);
	
	@Query(value = ""
			+ "  SELECT  " 
	        + " 	e.event_id, " 
	        + " 	e.event_no, " 
	        + " 	e.event_end_date_time, " 
	        + " 	e.event_start_date_time, " 
	        + " 	CASE WHEN :lang = 0 THEN v.name_english WHEN :lang = 1 THEN v.name_hindi WHEN :lang = 2 THEN v.name_gujarati END AS venue, " 
	        + " 	et.event_type_id AS event_type_id, " 
	        + " 	CASE WHEN :lang = 0 THEN et.name_english WHEN :lang = 1 THEN et.name_hindi WHEN :lang = 2 THEN et.name_gujarati END AS event_name, " 
	        + " 	CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS fn_name, " 
	        + " 	CASE WHEN :lang = 0 THEN dmc.name_english WHEN :lang = 1 THEN dmc.name_hindi WHEN :lang = 2 THEN dmc.name_gujarati END AS cat_name, " 
	        + " 	CASE WHEN :lang = 0 THEN dmci.name_english WHEN :lang = 1 THEN dmci.name_hindi WHEN :lang = 2 THEN dmci.name_gujarati END AS item_name, " 
	        + " 	ef.event_function_id AS fn_id,  " 
	        + " 	ef.function_start_date_time AS function_start_timestamp, " 
	        + " 	ef.function_end_date_time AS function_end_timestamp,  " 
	        + " 	ef.function_venue AS func_venue,  " 
	        + " 	ef.pax AS pax,  " 
	        + " 	ef.rate AS rate, " 
	        + " 	dp.default_price AS default_price,  " 
	        + " 	dpd.decore_main_category_id AS decore_cat_id, " 
	        + " 	CASE WHEN :lang = 0 THEN dpd.decore_cat_notes WHEN :lang = 1 THEN dpd.decore_cat_notes_hindi WHEN :lang = 2 THEN dpd.decore_cat_notes_gujarati END AS cat_notes, " 
	        + " 	dpd.decore_cat_slogan AS cat_slogan, " 
	        + " 	dpd.decore_item_id AS item_id,  " 
	        + " 	dpd.decore_item_slogan AS item_slogan,  " 
	        + " 	dpd.decore_item_price AS item_price, " 
	        + " 	dmc.image_path AS cat_image_path,  " 
	        + " 	CAST(NULL AS CHAR CHARACTER SET utf8) AS item_image_path,  " 
	        + " 	CAST(NULL AS CHAR CHARACTER SET utf8) AS catStartTime, " 
	        + " 	CASE WHEN :lang = 0 THEN e.remark WHEN :lang = 1 THEN e.remarks_hindi WHEN :lang = 2 THEN e.remarks_gujarati END AS event_remark, " 
	        + " 	CASE WHEN :lang = 0 THEN dpd.decore_item_notes WHEN :lang = 1 THEN dpd.decore_item_notes_hindi WHEN :lang = 2 THEN dpd.decore_item_notes_gujarati END AS item_notes, " 
	        + " 	e.meal_notes AS food_notes, " 
	        + " 	CASE WHEN :lang = 0 THEN mt.name_english WHEN :lang = 1 THEN mt.name_hindi WHEN :lang = 2 THEN mt.name_gujarati END AS food_type, " 
	        + " 	CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS partyName, " 
	        + " 	pm.mobileno,  " 
	        + " 	ud.company_name,  " 
	        + " 	ud.office_no,  " 
	        + " 	ud.address, " 
	        + " 	(SELECT MAX(ef2.pax) FROM event_function ef2 WHERE ef2.event_id = e.event_id) AS max_pax, " 
	        + " 	u.first_name,  " 
	        + " 	u.last_name,  " 
	        + " 	ud.country_code,  " 
	        + " 	u.email,  " 
	        + " 	u.logo, " 
	        + " 	CASE WHEN :defaultLanguage = 'en' THEN dmc.name_english WHEN :defaultLanguage = 'hi' THEN dmc.name_hindi WHEN :defaultLanguage = 'gu' THEN dmc.name_gujarati END AS cat_name_def, " 
	        + " 	CASE WHEN :preferedLanguage = 'en' THEN dmc.name_english WHEN :preferedLanguage = 'hi' THEN dmc.name_hindi WHEN :preferedLanguage = 'gu' THEN dmc.name_gujarati END AS cat_name_pref, " 
	        + " 	CASE WHEN :defaultLanguage = 'en' THEN dmci.name_english WHEN :defaultLanguage = 'hi' THEN dmci.name_hindi WHEN :defaultLanguage = 'gu' THEN dmci.name_gujarati END AS item_name_def, " 
	        + " 	CASE WHEN :preferedLanguage = 'en' THEN dmci.name_english WHEN :preferedLanguage = 'hi' THEN dmci.name_hindi WHEN :preferedLanguage = 'gu' THEN dmci.name_gujarati END AS item_name_pref, " 
	        + " 	dp.is_package AS is_package,  " 
	        + " 	dp.package_price AS package_price, " 
	        + " 	CAST(NULL AS CHAR CHARACTER SET utf8) AS vendorName, " 
	        + " 	e.billing_name_english AS billing_name_english, " 
	        + " 	e.billing_name_hindi AS billing_name_hindi, " 
	        + " 	e.billing_name_gujarati AS billing_name_gujarati, " 
	        + " 	e.meal_notes_hindi AS meal_notes_hindi, " 
	        + " 	e.meal_notes_gujarati AS meal_notes_gujarati, " 
	        + " 	ef.function_venue_hindi AS function_venue_hindi, " 
	        + " 	ef.function_venue_gujarati AS function_venue_gujarati, " 
	        + " 	e.service_hindi AS service_hindi, " 
	        + " 	e.service_gujarati AS service_gujarati, " 
	        + " 	e.theme_hindi AS theme_hindi, " 
	        + " 	e.theme_gujarati AS theme_gujarati, " 
	        + " 	e.service AS service_english, " 
	        + " 	e.theme AS theme_english, " 
	        + " 	ef.notes_english AS function_notes_english, " 
	        + " 	ef.notes_hindi AS function_notes_hindi, " 
	        + " 	ef.notes_gujarati AS function_notes_gujarati, " 
	        + " 	dpd.sub_cat AS sub_cat_english, " 
	        + " 	dpd.sub_cat_hindi AS sub_cat_hindi, " 
	        + " 	dpd.sub_cat_gujarati AS sub_cat_gujarati, " 
	        + " 	dpd.sub_item AS sub_item, " 
	        + " 	dpd.sub_item_hindi AS sub_item_hindi, " 
	        + " 	dpd.sub_item_gujarati AS sub_item_gujarati, " 
	        + " 	ef.food_type AS function_food_type, " 
	        + " 	ef.main_function AS main_function, " 
	        + " 	ef.rate_post_fix AS rate_post_fix, " 
	        + " 	e.banquet_hall_id AS banquet_hall_id, " 
	        + " 	bhm.hall_name AS hall_name,"
	        + "		0 AS cat_space,"
	        + " 	dpd.item_space AS item_space,"
	        + " 	FALSE AS is_cat_image,"
	        + " 	CASE WHEN :lang = 0 THEN dmc.name_english WHEN :lang = 1 THEN dmc.name_hindi WHEN :lang = 2 THEN dmc.name_gujarati END AS cat_nick_name, " 
	        + " 	CASE WHEN :lang = 0 THEN dmci.name_english WHEN :lang = 1 THEN dmci.name_hindi WHEN :lang = 2 THEN dmci.name_gujarati END AS item_nick_name,"
	        + "     dpd.item_qty AS item_qty, "
	        + "     dpd.decore_item_price AS decore_item_price, "
	        + "     em.user_id AS manager_id, "
	        + "     CONCAT(em.first_name, ' ', em.last_name) AS manager_name, "
	        + "     CASE WHEN :lang = 0 THEN cp.name_english "
	        + "          WHEN :lang = 1 THEN cp.name_hindi "
	        + "          WHEN :lang = 2 THEN cp.name_gujarati "
	        + "     END AS package_name, "
	        + "     cp.price AS pack_price, "
	        + "     CASE WHEN :lang = 0 THEN e.cordinator_person_name_english "
	        + "          WHEN :lang = 1 THEN e.cordinator_person_name_hindi "
	        + "          WHEN :lang = 2 THEN e.cordinator_person_name_gujarati "
	        + "     END AS cordination_person_name, "
	        + "     e.cordinator_person_contact_no AS cordination_person_contact_no, "
	        + "     CAST(NULL AS CHAR CHARACTER SET utf8) AS category_status, "
	        + "     CAST(NULL AS CHAR CHARACTER SET utf8) AS item_status, "
	        + "     CASE WHEN :lang = 0 THEN pm.address_english "
	        + "          WHEN :lang = 1 THEN pm.address_hindi "
	        + "          WHEN :lang = 2 THEN pm.address_gujarati "
	        + "     END AS party_address, "
	        + "     e.permissable_item, "
	        + "     e.not_permissable_item, "
	        + "     e.reference, "
	        + "     v.img_path AS venue_img, "
	        + "     CAST(NULL AS CHAR CHARACTER SET utf8) AS itemHeading, "
	        + "     e.prefix, "
	        + " 	CASE WHEN :lang = 0 THEN vp.name_english WHEN :lang = 1 THEN vp.name_hindi WHEN :lang = 2 THEN vp.name_gujarati END AS decorVendorName " 
	        + " FROM decore_preparation dp " 
	        + " INNER JOIN decore_preparation_details dpd " 
	        + " 	 ON dp.decore_preparation_id = dpd.decore_preparation_id " 
	        + " LEFT JOIN decore_main_category dmc " 
	        + " 	ON dmc.decore_main_category_id = dpd.decore_main_category_id " 
	        + " LEFT JOIN decore_main_category_item dmci " 
	        + " 	ON dmci.decore_item_id = dpd.decore_item_id " 
	        + " INNER JOIN event_function ef " 
	        + " 	ON ef.event_function_id = dp.event_function_id"
	        + " LEFT JOIN functions f "
	        + " 	ON f.function_id = ef.function_master_id " 
	        + " INNER JOIN `events` e " 
	        + " 	ON e.event_id = ef.event_id " 
	        + " INNER JOIN users u " 
	        + " 	ON u.user_id = e.user_id " 
	        + " INNER JOIN partymaster pm " 
	        + " 	ON pm.party_id = e.party_id " 
	        + " LEFT JOIN partymaster vp " 
	        + " 	ON vp.party_id = dpd.vendor_id " 
	        + " INNER JOIN eventtype et " 
	        + " 	ON et.event_type_id = e.event_type_id " 
	        + " LEFT JOIN mealtype mt " 
	        + " 	ON e.meal_type_id = mt.meal_type_id " 
	        + " LEFT JOIN venuemaster v " 
	        + " 	ON v.venue_id = e.venue_id " 
	        + " LEFT JOIN user_basic_details ud  " 
	        + " 	ON e.user_id = ud.user_id " 
	        + " LEFT JOIN banquet_hall_master bhm  " 
	        + " 	ON bhm.id = e.banquet_hall_id " 
	        + " LEFT JOIN users em "
	        + "     ON em.user_id = e.manager_id "
	        + " LEFT JOIN custompackage cp "
	        + "     ON cp.custom_package_id = ef.custom_package_id "
	        + " WHERE  " 
	        + " 	dp.is_delete = FALSE " 
	        + "		AND e.is_delete = FALSE " 
	        + " 	AND ef.is_delete = FALSE " 
	        + " 	AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) " 
	        + " 	AND e.user_id = :user " 
	        + " 	AND dpd.decore_main_category_id IS NOT NULL " 
	        + " 	AND dpd.decore_item_id IS NOT NULL "
	        + "		AND e.event_id = :eventId "
	        + " ORDER BY "
	        + " 	ef.sortorder ASC, "
	        + "		dpd.decore_cat_sortorder ASC, "
	        + "		dpd.decore_item_sortorder ASC " , nativeQuery = true)
	List<Object[]> getEventDecorReportRaw(@Param("eventId") Long eventId,
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("lang") int lang,
	        @Param("user") Long user,
	        @Param("defaultLanguage") String defaultLanguage,
	        @Param("preferedLanguage") String preferedLanguage);

	@Query(value = "SELECT mpd.menu_item_id AS itemId, mi.nameEnglish AS nameEnglish, mi.name_hindi AS nameHindi, mi.nameGujarati AS nameGujarati, mi.slogan AS slogan, COUNT(mpd.menu_item_id) AS totalCount FROM menupreparationdetails mpd INNER JOIN memuitems mi ON mi.menu_item_id = mpd.menu_item_id GROUP BY mpd.menu_item_id, mi.nameEnglish, mi.name_hindi, mi.nameGujarati, mi.slogan ORDER BY totalCount DESC LIMIT 10", nativeQuery = true)
	List<Object[]> getTopSellingMenuItems();

	@Query(value = "SELECT e.event_id, e.event_no, mp.event_function_id, mp.menu_preparation_id, "
			+ " mpd.menu_item_id, "
			+ " CASE WHEN :lang = 0 THEN mpd.menuitem_name WHEN :lang = 1 THEN mpd.menuitem_name_hindi WHEN :lang = 2 THEN mpd.menuitem_name_gujarati END AS menu_item_name "
			+ " FROM menupreparation mp "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
			+ " LEFT JOIN events e ON e.event_id = ef.event_id "
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ " WHERE e.event_id = :eventId AND e.user_id = :userId ", nativeQuery = true)
	List<Object[]> getEventItems(Long eventId, int lang, Long userId);

	@Query(value = "SELECT DISTINCT e.event_id, e.event_no, " + " mpd.menu_category_id, "
			+ " CASE WHEN :lang = 0 THEN mpd.menu_category_name WHEN :lang = 1 THEN mpd.menu_category_name_hindi WHEN :lang = 2 THEN mpd.menu_category_name_gujarati END AS menu_category_name "
			+ " FROM menupreparation mp "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
			+ " LEFT JOIN events e ON e.event_id = ef.event_id "
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ " WHERE e.event_id = :eventId AND e.user_id = :userId ", nativeQuery = true)
	List<Object[]> getEventCategories(Long eventId, int lang, Long userId);

	@Query(value = "SELECT DISTINCT e.event_id, e.event_no, " + " mpd.menu_category_id, mpd.menu_item_id, "
			+ " CASE WHEN :lang = 0 THEN mpd.menuitem_name WHEN :lang = 1 THEN mpd.menuitem_name_hindi WHEN :lang = 2 THEN mpd.menuitem_name_gujarati END AS menu_item_name "
			+ " FROM menupreparation mp "
			+ " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id "
			+ " LEFT JOIN events e ON e.event_id = ef.event_id "
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ " WHERE mpd.menu_category_id = :catId AND e.event_id = :eventId AND e.user_id = :userId ", nativeQuery = true)
	List<Object[]> getEventCategoryWiseItem(@Param("eventId") Long eventId, @Param("catId") Long catId,
			@Param("lang") int lang, @Param("userId") Long userId);

	@Query(value = "SELECT  ud.company_name, ud.country_code, ud.office_no, ud.company_email, ud.address, u.logo,"
			+ " CASE WHEN :lang = 0 THEN pm.name_english WHEN :lang = 1 THEN pm.name_hindi WHEN :lang = 2 THEN pm.name_gujarati END AS client_name, "
			+ " CASE WHEN :lang = 0 THEN pm.address_english WHEN :lang = 1 THEN pm.address_hindi WHEN :lang = 2 THEN pm.address_gujarati END AS client_address, "
			+ " pm.mobileno, " + " u.first_name, u.last_name, u.contact_no " + " FROM events e "
			+ " LEFT JOIN users u ON u.user_id = e.user_id "
			+ " LEFT JOIN user_basic_details ud ON ud.user_id = u.user_id "
			+ " LEFT JOIN partymaster pm ON pm.party_id = e.party_id "
			+ " WHERE e.event_id = :eventId AND e.user_id = :userid", nativeQuery = true)
	Object getCmpData(Long eventId, Long userid, int lang);

	@Query(value = " SELECT two_language_default, two_language_preferred FROM user_utility WHERE user = :userid ", nativeQuery = true)
	Object getLanguageSpecification(Long userid);

	@Query(value = "SELECT mi.menu_item_id, " + " mi.name_english,  mi.name_gujarati, mi.name_hindi"
			+ " FROM memuitems mi JOIN eventfunction_menuallocation em ON mi.menu_item_id = em.menu_item_id "
			+ "JOIN eventfunction_menuallocation_order emo ON em.menu_allocation_id = emo.menu_allocation_id "
			+ "WHERE em.eventfunction_id = :eventFunctionId AND emo.party_id IN (:partyIds)", nativeQuery = true)
	List<Object[]> getAllMenuPreparationDetailsByEventFunctionIdAndPartyIds(
			@Param("eventFunctionId") Long eventFunctionId, @Param("partyIds") List<Long> partyIds);

	@Query(value = "SELECT mi.menu_item_id, " + " mi.name_english, mi.name_gujarati, mi.name_hindi"
			+ " FROM memuitems mi JOIN eventfunction_menuallocation em ON mi.menu_item_id = em.menu_item_id "
			+ "JOIN eventfunction_menuallocation_order emo ON em.menu_allocation_id = emo.menu_allocation_id "
			+ "WHERE em.event_id = :eventId AND emo.party_id IN (:partyIds) ", nativeQuery = true)
	List<Object[]> getAllMenuPreparationDetailsByEventIdAndPartyIds(Long eventId, List<Long> partyIds);

	@Query("SELECT new com.crmportal.response.dto.EventAndFunctionWisePartyResponseDto(" + " p.id, "
			+ " p.nameEnglish, " + " p.nameHindi, " + " p.nameGujarati," + "p.mobileno" + ") " + "FROM MenuAllocationOrdersEntity mao "
			+ "JOIN mao.party p " + "JOIN mao.menuAllocation ma " + "WHERE ( :eventId = -1L OR ma.event.id = :eventId )"
			+ "AND ( :isFunctionWise = FALSE OR ma.eventFunction.id IN (:eventFunctionIds) ) " + "AND ma.isDelete = false AND p.user.id = :userId "
			+ "AND mao.isDelete = false  AND ( :type IS NULL " + "    OR ( :type = 'chef' AND ma.chefLabour = true ) "
			+ "    OR ( :type = 'outside' AND ma.outside = true ) "
			+ "    OR ( :type = 'inside' AND ma.inside = true ) " + ") " + "GROUP BY "
			+ "p.id, p.nameEnglish, p.nameHindi, p.nameGujarati")
	List<EventAndFunctionWisePartyResponseDto> getAgencyByEventFunctionId(@Param("eventId") Long eventId,
			@Param("eventFunctionIds") List<Long> eventFunctionIds, @Param("type") String type,@Param("userId") Long userId, 
			@Param("isFunctionWise") Boolean isFunctionWise);

	@Query("SELECT new com.crmportal.response.dto.EventAndFunctionWisePartyResponseDto(" + " p.id, "
			+ " p.nameEnglish, " + " p.nameHindi, " + " p.nameGujarati," + "p.mobileno" + ") " + "FROM EventRawMaterialEntity mao "
			+ "JOIN mao.supplier p " + "WHERE mao.event.id = :eventId " + "AND mao.isDelete = false AND p.user.id = :userId " + "GROUP BY "
			+ "p.id, p.nameEnglish, p.nameHindi, p.nameGujarati")
	List<EventAndFunctionWisePartyResponseDto> getSupplierByEvent(@Param("eventId") Long eventId,@Param("userId") Long userId);

	@Query(value = "SELECT mi.menu_item_id, " + " mi.name_english,  mi.name_gujarati, mi.name_hindi"
			+ " FROM memuitems mi JOIN eventfunction_menuallocation em ON mi.menu_item_id = em.menu_item_id "
			+ "JOIN eventfunction_menuallocation_order emo ON em.menu_allocation_id = emo.menu_allocation_id "
			+ "WHERE em.eventfunction_id = :eventFunctionId ", nativeQuery = true)
	List<Object[]> findByEventFunctionId(@Param("eventFunctionId") Long eventFunctionId);

	@Query(value = "SELECT mi.menu_item_id, " + " mi.name_english, mi.name_gujarati, mi.name_hindi"
			+ " FROM memuitems mi JOIN eventfunction_menuallocation em ON mi.menu_item_id = em.menu_item_id "
			+ "JOIN eventfunction_menuallocation_order emo ON em.menu_allocation_id = emo.menu_allocation_id "
			+ "WHERE em.event_id = :eventId", nativeQuery = true)
	List<Object[]> findByEventId(@Param("eventId") Long eventId);

	@Query("SELECT new com.crmportal.response.dto.EventAndFunctionWisePartyResponseDto(" + " p.id, "
			+ " p.nameEnglish, " + " p.nameHindi, " + " p.nameGujarati," + "p.mobileno" + ") FROM EventLaborEntity mao "
			+ "JOIN mao.contact p WHERE ( :eventId = -1L OR mao.event.id = :eventId ) "
			+ "AND ( :isFunctionWise = FALSE OR mao.eventFunction.id IN (:eventFunctionIds) ) AND p.user.id = :userId GROUP BY "
			+ "p.id, p.nameEnglish, p.nameHindi, p.nameGujarati")
	List<EventAndFunctionWisePartyResponseDto> getLabourAgencyByEventFunctionId(Long eventId, List<Long> eventFunctionIds,Long userId, Boolean isFunctionWise);

	@Query(value = "SELECT m.menu_item_id, "
			+ " 	CASE WHEN :lang = 0 THEN m.name_english WHEN :lang = 1 THEN m.name_hindi WHEN :lang = 2 THEN m.name_gujarati END AS item_name, "
			+ " 	COUNT(mirm.raw_material_id) AS total_raw_material " + " FROM `memuitems` m "
			+ " LEFT JOIN `menu_item_raw_material` mirm ON mirm.menu_item_id = m.menu_item_id AND mirm.is_delete = FALSE "
			+ " WHERE m.user_id = :userid AND m.is_delete = FALSE " + " GROUP BY m.menu_item_id, m.name_english "
			+ " HAVING COUNT(mirm.raw_material_id) = 0 ", nativeQuery = true)
	List<Object[]> getItemOfRemaingRawMaterial(Long userid, int lang);

	@Query(value = "SELECT menu_item_id, "
			+ " 	CASE WHEN :lang = 0 THEN name_english WHEN :lang = 1 THEN name_hindi WHEN :lang = 2 THEN name_gujarati END AS item_name, "
			+ " 	slogan " + " FROM `memuitems` " + " WHERE user_id = :userid AND is_delete = FALSE "
			+ "   AND LENGTH(TRIM(slogan)) = 0", nativeQuery = true)
	List<Object[]> getItemOfRemaingSlogan(Long userid, int lang);

	@Query(value = "SELECT m.menu_item_id, "
			+ " 	CASE WHEN :lang = 0 THEN m.name_english WHEN :lang = 1 THEN m.name_hindi WHEN :lang = 2 THEN m.name_gujarati END AS item_name, "
			+ " 	mirm.raw_material_id, "
			+ " 	CASE WHEN :lang = 0 THEN rm.name_english WHEN :lang = 1 THEN rm.name_hindi WHEN :lang = 2 THEN m.name_gujarati END AS raw_material_name,"
			+ "		mirm.rate " + " FROM `memuitems` m "
			+ " LEFT JOIN `menu_item_raw_material` mirm ON mirm.menu_item_id = m.menu_item_id "
			+ " LEFT JOIN `rawmaterial` rm ON rm.raw_material_id = mirm.raw_material_id "
			+ " WHERE m.user_id = :userid AND m.is_delete = FALSE AND mirm.is_delete = FALSE AND rm.is_delete = FALSE "
			+ " AND mirm.rate = 0 ", nativeQuery = true)
	List<Object[]> getItemOfRemaingRecipyRate(Long userid, int lang);

	@Query(value = "SELECT menu_item_id, "
			+ " 	CASE WHEN :lang = 0 THEN name_english WHEN :lang = 1 THEN name_hindi WHEN :lang = 2 THEN name_gujarati END AS item_name "
			+ " FROM `memuitems` " + " WHERE user_id = :userid AND is_delete = FALSE "
			+ "   AND (image_path IS NULL OR LENGTH(TRIM(image_path)) = 0) ", nativeQuery = true)
	List<Object[]> getItemOfRemaingImage(Long userid, int lang);

	boolean existsByMenuPreparation(MenuPreparationEntity menuPreparationEntity);

	List<MenuPreparationDetailsEntity> findAllByMenuPreparation(MenuPreparationEntity oldMenuPreparation);

	@Query("SELECT DISTINCT mpd.menuItem.id " + "FROM MenuPreparationDetailsEntity mpd "
			+ "WHERE mpd.menuPreparation.eventFunction.event.id = :eventId "
			+ "AND mpd.menuPreparation.eventFunction.id = :eventFunctionId")
	List<Long> findDistinctMenuItemIdsByEventAndEventFunction(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	List<MenuPreparationDetailsEntity> findByMenuPreparationIn(List<MenuPreparationEntity> preparations);

	boolean existsByMenuItem(MenuItemMasterEntity entity);
	
	List<MenuPreparationDetailsEntity> findByMenuPreparationId(Long menuPreparationId);
	
	List<MenuPreparationDetailsEntity> findByMenuPreparation_IdIn(List<Long> menuPreparationIds);

}
