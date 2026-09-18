package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFoodTestingEntity;
import com.crmportal.response.dto.EventFunctionTesterResponseDto;

@Repository
public interface EventFoodTestingRepository extends JpaRepository<EventFoodTestingEntity, Long> {

	Optional<EventFoodTestingEntity> findByEventIdAndEventFunctionIdAndTesterId(Long eventId, Long eventFunctionId,
			Long testerId);

	@Query(value =  " " 
			 + "  SELECT  " 
			 + " 	e.event_id, " 
			 + " 	e.event_no, " 
			 + " 	e.event_end_date_time AS event_end_timestamp, " 
			 + " 	e.event_start_date_time AS event_start_timestamp, " 
			 + " 	CASE WHEN :lang = 0 THEN v.name_english WHEN :lang = 1 THEN v.name_hindi WHEN :lang = 2 THEN v.name_gujarati END AS venue, " 
			 + " 	et.event_type_id AS event_type_id, " 
			 + " 	CASE WHEN :lang = 0 THEN et.name_english WHEN :lang = 1 THEN et.name_hindi WHEN :lang = 2 THEN et.name_gujarati END AS event_name, " 
			 + " 	CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS fn_name, " 
			 + " 	CASE WHEN :lang = 0 THEN eftd.menu_cat_name_english WHEN :lang = 1 THEN eftd.menu_cat_name_hindi WHEN :lang = 2 THEN eftd.menu_cat_name_gujarati END AS cat_name, " 
			 + " 	CASE WHEN :lang = 0 THEN eftd.menu_item_name_english WHEN :lang = 1 THEN eftd.menu_item_name_hindi WHEN :lang = 2 THEN eftd.menu_item_name_gujarati END AS item_name, " 
			 + " 	ef.event_function_id AS fn_id, ef.function_start_date_time AS function_start_timestamp, " 
			 + " 	ef.function_end_date_time AS function_end_timestamp, ef.function_venue AS func_venue, ef.pax AS pax, ef.rate AS rate, " 
			 + " 	0 AS default_price, eftd.menu_cat_id AS menu_cat_id, " 
			 + " 	CASE WHEN :lang = 0 THEN eftd.cat_notes_english WHEN :lang = 1 THEN eftd.cat_notes_hindi WHEN :lang = 2 THEN eftd.cat_notes_gujarati END AS cat_notes, " 
			 + " 	'' AS cat_slogan, " 
			 + " 	eftd.menu_item_id AS item_id,  " 
			 + " 	eftd.review AS item_slogan,  " 
			 + " 	0 AS item_price, " 
			 + " 	mc.image_path AS cat_image_path,  " 
			 + " 	mi.image_path AS item_image_path,  " 
			 + " 	CAST(NULL AS DATETIME) AS catStartTime, " 
			 + " 	CASE WHEN :lang = 0 THEN e.remark WHEN :lang = 1 THEN e.remarks_hindi WHEN :lang = 2 THEN e.remarks_gujarati END AS event_remark, " 
			 + " 	CASE WHEN :lang = 0 THEN eftd.client_notes WHEN :lang = 1 THEN eftd.client_notes_hindi WHEN :lang = 2 THEN eftd.client_notes_gujarati END AS item_notes, " 
			 + " 	e.meal_notes AS food_notes, " 
			 + " 	CASE WHEN :lang = 0 THEN mt.name_english WHEN :lang = 1 THEN mt.name_hindi WHEN :lang = 2 THEN mt.name_gujarati END AS food_type, " 
			 + " 	CASE WHEN :lang = 0 THEN tm.name_english WHEN :lang = 1 THEN tm.name_hindi WHEN :lang = 2 THEN tm.name_gujarati END AS partyName, " 
			 + " 	tm.contact_no,  " 
			 + " 	ud.company_name,  " 
			 + " 	ud.office_no,  " 
			 + " 	ud.address, " 
			 + " 	(SELECT MAX(ef2.pax) FROM event_function ef2 WHERE ef2.event_id = e.event_id) AS max_pax, " 
			 + " 	u.first_name,  " 
			 + " 	u.last_name,  " 
			 + " 	ud.country_code,  " 
			 + " 	u.email,  " 
			 + " 	u.logo, " 
			 + " 	CASE WHEN :defaultLanguage = 'en' THEN mc.name_english WHEN :defaultLanguage = 'hi' THEN mc.name_hindi WHEN :defaultLanguage = 'gu' THEN mc.name_gujarati END AS cat_name_def, " 
			 + " 	CASE WHEN :preferedLanguage = 'en' THEN mc.name_english WHEN :preferedLanguage = 'hi' THEN mc.name_hindi WHEN :preferedLanguage = 'gu' THEN mc.name_gujarati END AS cat_name_pref, " 
			 + " 	CASE WHEN :defaultLanguage = 'en' THEN mi.name_english WHEN :defaultLanguage = 'hi' THEN mi.name_hindi WHEN :defaultLanguage = 'gu' THEN mi.name_gujarati END AS item_name_def, " 
			 + " 	CASE WHEN :preferedLanguage = 'en' THEN mi.name_english WHEN :preferedLanguage = 'hi' THEN mi.name_hindi WHEN :preferedLanguage = 'gu' THEN mi.name_gujarati END AS item_name_pref, " 
			 + " 	0 AS is_package,  " 
			 + " 	0 AS package_price, " 
			 + " 	0 AS vendorName, " 
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
			 + " 	CAST(NULL AS CHAR) AS sub_cat_english, " 
			 + " 	CAST(NULL AS CHAR) AS sub_cat_hindi, " 
			 + " 	CAST(NULL AS CHAR) AS sub_cat_gujarati, " 
			 + " 	CAST(NULL AS CHAR) AS sub_item, " 
			 + " 	CAST(NULL AS CHAR) AS sub_item_hindi, " 
			 + " 	CAST(NULL AS CHAR) AS sub_item_gujarati,"
			 + "	CAST(NULL AS CHAR) AS function_food_type,"
			 + "	CAST(NULL AS CHAR) AS main_function, "
			 + "    CAST(NULL AS CHAR) AS rate_post_fix,"
			 + "	0 AS banquet_hall_id,"
			 + "	CAST(NULL AS CHAR) AS hall_name "
			 + " FROM event_food_testing eft " 
			 + " INNER JOIN event_food_testing_detail eftd  " 
			 + " 	ON eft.event_food_testing_id = eftd.event_food_testing_id " 
			 + " LEFT JOIN events e  " 
			 + " 	ON e.event_id = eft.event_id  " 
			 + " LEFT JOIN event_function ef  " 
			 + " 	ON eft.event_function_id = ef.event_function_id "
			 + " LEFT JOIN tester_master tm " 
			 + " 	ON tm.tester_id = eft.tester_id " 
			 + " LEFT JOIN eventtype et  " 
			 + " 	ON et.event_type_id = e.event_type_id " 
			 + " LEFT JOIN menucategory mc  " 
			 + " 	ON mc.menu_category_id = eftd.menu_cat_id " 
			 + " LEFT JOIN memuitems mi  " 
			 + " 	ON mi.menu_item_id = eftd.menu_item_id " 
			 + " LEFT JOIN mealtype mt " 
			 + " 	ON e.meal_type_id = mt.meal_type_id " 
			 + " LEFT JOIN users u " 
			 + " 	ON e.user_id = u.user_id " 
			 + " LEFT JOIN user_basic_details ud  " 
			 + " 	ON e.user_id = ud.user_id "
			 + " LEFT JOIN venuemaster v "
			 + "	ON v.venue_id = e.venue_id "
			 + " LEFT JOIN functions f "
			 + "	ON f.function_id = ef.function_master_id  " 
			 + " WHERE eft.event_id = :eventId " 
			 + " AND eft.event_function_id = :eventFunctionId " 
			 + " AND eft.tester_id = :testerId " , nativeQuery = true)
	List<Object[]> getAllTestingPreparationData(
			@Param("eventId") Long eventId, 
			@Param("eventFunctionId") Long eventFunctionId, 
			@Param("testerId") Long testerId,
			@Param("lang") Integer lang, 
			@Param("defaultLanguage") String defaultLanguage,
			@Param("preferedLanguage") String preferedLanguage
	);
	
	@Query(" SELECT new com.crmportal.response.dto.EventFunctionTesterResponseDto(" +
	        "	t.id, " +
	        "	t.nameEnglish, " +
	        "	eftl.eventId, " +
	        "	eftl.eventFunctionId, " +
	        "	eftl.members) " +
	        " FROM EventFoodTestingLinkEntity eftl " +
	        " JOIN TesterMasterEntity t ON t.id = eftl.testerId AND t.isDelete = false " +
	        " WHERE eftl.eventId = :eventId " +
	        " AND eftl.eventFunctionId = :eventFunctionId " +
	        " AND eftl.isDelete = false " +
	        " AND eftl.isActive = true")
	List<EventFunctionTesterResponseDto> findTestersByEventAndFunction(
	        @Param("eventId") Long eventId,
	        @Param("eventFunctionId") Long eventFunctionId);

	List<EventFoodTestingEntity> findByEventIdAndEventFunctionId(Long eventId, Long eventFunctionId);
	
}
