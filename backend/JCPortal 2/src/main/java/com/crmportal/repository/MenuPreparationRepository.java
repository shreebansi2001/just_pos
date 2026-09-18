package com.crmportal.repository;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.MenuCategoryMasterResponseDto;
import com.crmportal.response.dto.MenuItemForReportResponseDto;
import com.crmportal.response.dto.MenuItemMasterResponseDto;
import com.crmportal.response.dto.MenuPreparationItemResponseDto;

@Repository
public interface MenuPreparationRepository extends JpaRepository<MenuPreparationEntity, Long> {

	Optional<MenuPreparationEntity> findByIdAndIsDeleteFalse(long id);

	@Query(value = "SELECT " + " i.name_english AS itemName, "
			+ " COALESCE(pc.name_english, c.name_english) AS categoryName, " + " i.price as itemPrice, " + " i.slogan, "
			+ " COALESCE(pc.menuslogan, c.menuslogan) AS categorySlogan, "
			+ " COALESCE(mpd.menu_category_id, c.menu_category_id) AS categoryId, " + " i.menu_item_id AS itemId, "
			+ " i.image_path AS imagePath, "
			+ " CASE WHEN mpd.menu_preparation_details_id IS NOT NULL THEN 1 ELSE 0 END AS selected, "
			+ " mpd.item_sortorder, " + " mpd.menu_sortorder, "
			+ " COALESCE(pc.name_hindi, c.name_hindi) AS categoryNameHindi, "
			+ " COALESCE(pc.name_gujarati, c.name_gujarati) AS categoryNameGujarati, "
			+ " i.name_hindi AS itemNameHindi, " + " i.name_gujarati AS itemNameGujarati, i.instruction_english AS instructionEnglish, i.instruction_gujarati AS instructionGujarati , i.instruction_hindi AS instructionHindi, c.price as catPrice, c.report_name_english, c.report_name_hindi, c.report_name_gujarati,"
			+ " i.url AS imageUrl " 
			+ "FROM memuitems i "
			+ "JOIN menucategory c ON i.menu_category_id = c.menu_category_id "
			+ "LEFT JOIN menupreparationdetails mpd " + "       ON mpd.menu_item_id = i.menu_item_id "
			+ "      AND mpd.menu_preparation_id IN ( " + "            SELECT mp.menu_preparation_id "
			+ "            FROM menupreparation mp " + "            WHERE mp.event_function_id = :eventFunctionId "
			+ "      ) " + "LEFT JOIN menucategory pc ON mpd.menu_category_id = pc.menu_category_id "
			+ "WHERE i.is_delete = false " + " AND i.is_active = true " + " AND c.is_delete = false "
			+ " AND c.is_active = true " + " AND i.user_id = :userId "
			+ " AND (:itemName IS NULL OR TRIM(:itemName) = '' OR LOWER(i.name_english) LIKE LOWER(CONCAT('%', :itemName, '%'))) "
			+ " AND (:menuCategoryId = 0 OR COALESCE(mpd.menu_category_id, c.menu_category_id) = :menuCategoryId) "
			+ "ORDER BY selected DESC, i.name_english ASC", nativeQuery = true)
	List<Object[]> getAllMenuPreparationItemsNative(@Param("eventFunctionId") Long eventFunctionId,
			@Param("userId") Long userId, @Param("itemName") String itemName,
			@Param("menuCategoryId") Long menuCategoryId);

	MenuPreparationEntity findByEventFunctionAndIsDeleteFalse(EventFunctionMasterEntity eventFunction);

	@Query("SELECT COALESCE(MAX(m.sortorder), 0) FROM MenuPreparationEntity m WHERE m.eventFunction.id = :eventFunctionId")
	Integer findMaxSortOrder(Long eventFunctionId);

	void deleteByEventFunction(EventFunctionMasterEntity fnEntity);

	MenuPreparationEntity findByEventFunction_IdAndIsDeleteFalse(Long id);

	@Query("SELECT new com.crmportal.response.dto.MenuItemForReportResponseDto("
			+ "mp.menuItem.id, mp.menuItem.nameEnglish, mp.menuItem.nameHindi, mp.menuItem.nameGujarati, mp.itemSlogan, mp.menuItem.imagePath ,mp.itemNotes ) "
			+ "FROM com.crmportal.entity.MenuPreparationDetailsEntity mp "
			+ "WHERE mp.menuCategory.id = :menuCategoryId AND mp.menuPreparation.id = :menuPreparationId")
	List<MenuItemForReportResponseDto> findByMenuCategoryIdAndMenuPreparationId(Long menuCategoryId,
			Long menuPreparationId);

	boolean existsByEventFunction_id(Long eventFunctionId);

	boolean existsByEventFunction_idAndIsDeleteFalse(Long eventFunctionId);

	@Query(value = "SELECT e.event_id, DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y') AS event_start_date_time, "
			+ "  CASE WHEN :lang = 0 THEN et.name_english WHEN :lang = 1 THEN et.name_hindi WHEN :lang = 2 THEN et.name_gujarati END AS event_name,  "
			+ " CASE  "
			+ " WHEN :lang = 0 THEN CONCAT(f.name_english, ' (', DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y'), ')')  "
			+ " WHEN :lang = 1 THEN CONCAT(f.name_hindi, ' (', DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y'), ')')  "
			+ " WHEN :lang = 2 THEN CONCAT(f.name_gujarati, ' (', DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y'), ')')  "
			+ " END AS function_name,  " + " CAST(ef.pax AS CHAR) AS pax, " 
			+ " CASE WHEN :lang = 0 THEN ef.function_venue WHEN :lang = 1 THEN ef.function_venue_hindi WHEN :lang = 2 THEN ef.function_venue_gujarati END AS venue, "
			+ " CASE WHEN :lang = 0 THEN pm1.name_english WHEN :lang = 1 THEN pm1.name_hindi WHEN :lang = 2 THEN pm1.name_gujarati END AS party_name,  "
			+ " CONCAT(pm2.first_name, ' ', pm2.last_name) AS manager_name, " 
			+ " CASE "
			+ " WHEN status = 0 THEN CASE WHEN :lang = 0 THEN 'Inquiry' WHEN :lang = 1 THEN 'इन्क्वायरी' WHEN :lang = 2 THEN 'ઇન્ક્વાયરી' ELSE 'Inquiry' END "
			+ " WHEN status = 1 THEN CASE WHEN :lang = 0 THEN 'Confirm' WHEN :lang = 1 THEN 'कन्फर्म' WHEN :lang = 2 THEN 'કન્ફર્મ' ELSE 'Confirm' END "
			+ " WHEN status = 2 THEN CASE WHEN :lang = 0 THEN 'Cancel' WHEN :lang = 1 THEN 'कैंसल' WHEN :lang = 2 THEN 'કેન્સલ' ELSE 'Cancel' END "
			+ " WHEN status = 3 THEN CASE WHEN :lang = 0 THEN 'Tentative' WHEN :lang = 1 THEN 'टेंटेटिव' WHEN :lang = 2 THEN 'ટેન્ટેટિવ' ELSE 'Tentative' END "
			+ " ELSE '' "
			+ " END AS status, "
			+	" ef.event_function_id as eventFunctionId, "
			+ " e.event_no, "
			+ " DATE_FORMAT(e.inquiry_date, '%d/%m/%Y') AS inquiry_date, "
			+ " e.mobileno, q.sub_total,q.transportation,q.grand_total "
			+ " FROM events e " + " LEFT JOIN `eventtype` et ON et.event_type_id = e.event_type_id "
			+ " LEFT JOIN `event_function` ef ON ef.event_id = e.event_id "
			+ " LEFT JOIN `functions` f ON f.function_id = ef.function_master_id "
			+ " LEFT JOIN `venuemaster` v ON v.venue_id = e.venue_id "
			+ " LEFT JOIN partymaster pm1 ON pm1.party_id = e.party_id "
			+ " LEFT JOIN quotations q ON q.event_id = e.event_id and q.is_decore = FALSE "
			+ " LEFT JOIN users pm2 ON pm2.user_id = e.manager_id " + " WHERE "
			+ " DATE(e.event_start_date_time) BETWEEN :firstDate AND :lastDate " + " AND e.user_id = :userid "
			+ " AND e.is_delete = FALSE " + " AND et.is_delete = FALSE " + " AND ef.is_delete = FALSE "
			+ " AND f.is_delete = FALSE " + " AND  pm1.is_delete = FALSE "
			+ " AND pm2.is_delete = FALSE " + " AND e.status IN (:eventStatus) "
			+ " AND (:partyId = -1 OR pm1.party_id = :partyId)"
			+ " AND (:flag = true OR e.manager_id IN (:managerIds)) "
			+ " ORDER BY ef.function_start_date_time ", nativeQuery = true)
	List<Object[]> findDatewiseOrderSummary(LocalDate firstDate, LocalDate lastDate, Long userid, int lang,
			List<Integer> eventStatus, List<Long> managerIds, Boolean flag, Long partyId);

	@Query("SELECT COALESCE(SUM(m.packagePrice), 0) FROM MenuPreparationEntity m "
			+ "WHERE m.eventFunction.id = :eventFunctionId " + "AND m.isPackage = true " + "AND m.isDelete = false")
	BigDecimal getPackageRate(Long eventFunctionId);

	List<MenuPreparationEntity> findByEventFunctionInAndIsDeleteFalse(List<EventFunctionMasterEntity> eventFunctions);

	@Query(value = "SELECT COUNT(DISTINCT e.event_id) " + "FROM menupreparation mp "
			+ "JOIN event_function ef ON mp.event_function_id = ef.event_function_id "
			+ "JOIN events e ON ef.event_id = e.event_id " + "WHERE e.is_delete = FALSE " + "AND ef.is_delete = FALSE "
			+ "AND mp.is_delete = FALSE " + " AND (:userId = -1 OR e.user_id = :userId) ", nativeQuery = true)
	Integer getMenuCountByUserId(@Param("userId") Long userId);

	@Query(value =
	        "SELECT " +
	        " i.menu_item_id AS itemId, " +
	        " i.name_english AS itemName, " +
	        " c.menu_category_id AS categoryId, " +
	        " c.name_gujarati AS categoryNameGujarati " +
	        "FROM memuitems i " +
	        "JOIN menucategory c ON i.menu_category_id = c.menu_category_id " +
	        "WHERE i.is_delete = false " +
	        "AND i.is_active = true " +
	        "AND c.is_delete = false " +
	        "AND c.is_active = true " +
	        "AND i.user_id = :userId"
	        + " AND c.menu_category_id = 83",
	        nativeQuery = true)
	List<Object[]> getAllMenuPreparationItems(Long userId);
	
	List<MenuPreparationEntity> findByEventFunctionId(Long eventFunctionId);

	@Query(value =
	        " SELECT " +
	        "     mi.name_english AS itemName, " +
	        "     COALESCE(pc.name_english, mc.name_english) AS categoryName, " +
	        "     cpd.item_price AS price, " +
	        "     mi.slogan AS itemSlogan, " +
	        "     COALESCE(pc.menuslogan, mc.menuslogan) AS categorySlogan, " +

	        "     COALESCE(mpd.menu_category_id, mc.menu_category_id) AS categoryId, " +
	        "     mi.menu_item_id AS itemId, " +
	        "     mi.image_path AS imagePath, " +

	        "     CASE " +
	        "         WHEN mpd.menu_preparation_details_id IS NOT NULL THEN 1 " +
	        "         ELSE 0 " +
	        "     END AS selected, " +

	        "     COALESCE(mpd.item_sortorder, cpd.item_sortorder) AS itemSortOrder, " +
	        "     COALESCE(mpd.menu_sortorder, cpd.menu_sortorder) AS menuSortOrder, " +

	        "     COALESCE(pc.name_hindi, mc.name_hindi) AS categoryNameHindi, " +
	        "     COALESCE(pc.name_gujarati, mc.name_gujarati) AS categoryNameGujarati, " +

	        "     mi.name_hindi AS itemNameHindi, " +
	        "     mi.name_gujarati AS itemNameGujarati, " +
	        " 	   mi.instruction_english AS instructionEnglish, " +
	        "     mi.instruction_gujarati AS instructionGujarati, "+
	        "     mi.instruction_hindi AS instructionHindi" +
	        " FROM custom_package_details cpd " +

	        " INNER JOIN custompackage cp " +
	        "     ON cp.custom_package_id = cpd.custom_package_id " +

	        " INNER JOIN menucategory mc " +
	        "     ON mc.menu_category_id = cpd.menu_category_id " +

	        " LEFT JOIN memuitems mi " +
	        "     ON mi.menu_item_id = cpd.menu_item_id " +

	        " LEFT JOIN menupreparation mp " +
	        "     ON mp.package_id = cp.custom_package_id " +
	        "     AND mp.event_function_id = :eventFunctionId " +
	        "     AND mp.is_delete = false " +

	        " LEFT JOIN menupreparationdetails mpd " +
	        "     ON mpd.menu_preparation_id = mp.menu_preparation_id " +
	        "     AND mpd.menu_item_id = cpd.menu_item_id " +
	        "     AND mpd.menu_category_id = cpd.menu_category_id " +

	        " LEFT JOIN menucategory pc " +
	        "     ON mpd.menu_category_id = pc.menu_category_id " +

	        " WHERE cp.custom_package_id = :packageId " +
	        "     AND cp.user_id = :userId " +
	        "     AND cp.is_delete = false " +
	        "     AND cp.is_active = true " +
	        "     AND cpd.is_delete = false " +
	        "     AND cpd.is_active = true " +
	        "     AND cpd.menu_item_id IS NOT NULL " +
	        " ORDER BY selected DESC, menuSortOrder ASC, itemSortOrder ASC ",
	        nativeQuery = true)
	List<Object[]> getAllMenuPreparationSharItems(
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("userId") Long userId,
	        @Param("packageId") Long packageId);
	
	List<MenuPreparationEntity> findByEventFunction_IdIn(List<Long> eventFunctionIds);

	@Query(value = "SELECT e.event_id, DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y') AS event_start_date_time, "
			+ "  CASE WHEN :lang = 0 THEN et.name_english WHEN :lang = 1 THEN et.name_hindi WHEN :lang = 2 THEN et.name_gujarati END AS event_name,  "
			+ "  DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y') "
			+ " AS function_date,  " + " CAST(ef.pax AS CHAR) AS pax, " 
			+ " CASE WHEN :lang = 0 THEN ef.function_venue WHEN :lang = 1 THEN ef.function_venue_hindi WHEN :lang = 2 THEN ef.function_venue_gujarati END AS venue, "
			+ " CASE WHEN :lang = 0 THEN pm1.name_english WHEN :lang = 1 THEN pm1.name_hindi WHEN :lang = 2 THEN pm1.name_gujarati END AS party_name,  "
			+ " CONCAT(pm2.first_name, ' ', pm2.last_name) AS manager_name, " 
			+ " CASE "
			+ " WHEN status = 0 THEN CASE WHEN :lang = 0 THEN 'Inquiry' WHEN :lang = 1 THEN 'इन्क्वायरी' WHEN :lang = 2 THEN 'ઇન્ક્વાયરી' ELSE 'Inquiry' END "
			+ " WHEN status = 1 THEN CASE WHEN :lang = 0 THEN 'Confirm' WHEN :lang = 1 THEN 'कन्फर्म' WHEN :lang = 2 THEN 'કન્ફર્મ' ELSE 'Confirm' END "
			+ " WHEN status = 2 THEN CASE WHEN :lang = 0 THEN 'Cancel' WHEN :lang = 1 THEN 'कैंसल' WHEN :lang = 2 THEN 'કેન્સલ' ELSE 'Cancel' END "
			+ " WHEN status = 3 THEN CASE WHEN :lang = 0 THEN 'Tentative' WHEN :lang = 1 THEN 'टेंटेटिव' WHEN :lang = 2 THEN 'ટેન્ટેટિવ' ELSE 'Tentative' END "
			+ " ELSE '' "
			+ " END AS status, "
			+	" ef.event_function_id as eventFunctionId, "
			+ " e.event_no, "
			+ " DATE_FORMAT(e.inquiry_date, '%d/%m/%Y') AS inquiry_date, "
			+ " e.mobileno, q.sub_total,q.transportation,q.grand_total "
			+ " FROM events e " + " LEFT JOIN `eventtype` et ON et.event_type_id = e.event_type_id "
			+ " LEFT JOIN `event_function` ef ON ef.event_id = e.event_id "
			+ " LEFT JOIN `functions` f ON f.function_id = ef.function_master_id "
			+ " LEFT JOIN `venuemaster` v ON v.venue_id = e.venue_id "
			+ " LEFT JOIN partymaster pm1 ON pm1.party_id = e.party_id "
			+ " LEFT JOIN quotations q ON q.event_id = e.event_id and q.is_decore = FALSE "
			+ " LEFT JOIN users pm2 ON pm2.user_id = e.manager_id " + " WHERE "
			+ " DATE(e.event_start_date_time) BETWEEN :firstDate AND :lastDate " + " AND e.user_id = :userid "
			+ " AND e.is_delete = FALSE " + " AND et.is_delete = FALSE " + " AND ef.is_delete = FALSE "
			+ " AND f.is_delete = FALSE " + " AND  pm1.is_delete = FALSE "
			+ " AND pm2.is_delete = FALSE " + " AND e.status IN (:eventStatus) "
			+ " AND (:partyId = -1 OR pm1.party_id = :partyId)"
			+ " AND (:flag = true OR e.manager_id IN (:managerIds)) "
			+ " ORDER BY e.event_start_date_time, ef.function_start_date_time ", nativeQuery = true)
	List<Object[]> findDatewiseOrderSummary2(LocalDate firstDate, LocalDate lastDate, Long userid, int lang,
			List<Integer> eventStatus, List<Long> managerIds, Boolean flag, Long partyId);
	
	@Query(value = "SELECT e.event_id, DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y') AS event_start_date_time, "
			+ "  CASE WHEN :lang = 0 THEN et.name_english WHEN :lang = 1 THEN et.name_hindi WHEN :lang = 2 THEN et.name_gujarati END AS event_name,  "
			+ " CASE  "
			+ " WHEN :lang = 0 THEN CONCAT(f.name_english, ' (', DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y'), ')')  "
			+ " WHEN :lang = 1 THEN CONCAT(f.name_hindi, ' (', DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y'), ')')  "
			+ " WHEN :lang = 2 THEN CONCAT(f.name_gujarati, ' (', DATE_FORMAT(ef.function_start_date_time, '%d/%m/%Y'), ')')  "
			+ " END AS function_name,  " + " CAST(ef.pax AS CHAR) AS pax, " 
			+ " CASE WHEN :lang = 0 THEN ef.function_venue WHEN :lang = 1 THEN ef.function_venue_hindi WHEN :lang = 2 THEN ef.function_venue_gujarati END AS venue, "
			+ " CASE WHEN :lang = 0 THEN pm1.name_english WHEN :lang = 1 THEN pm1.name_hindi WHEN :lang = 2 THEN pm1.name_gujarati END AS party_name,  "
			+ " CONCAT(pm2.first_name, ' ', pm2.last_name) AS manager_name, " 
			+ " CASE "
			+ " WHEN status = 0 THEN CASE WHEN :lang = 0 THEN 'Inquiry' WHEN :lang = 1 THEN 'इन्क्वायरी' WHEN :lang = 2 THEN 'ઇન્ક્વાયરી' ELSE 'Inquiry' END "
			+ " WHEN status = 1 THEN CASE WHEN :lang = 0 THEN 'Confirm' WHEN :lang = 1 THEN 'कन्फर्म' WHEN :lang = 2 THEN 'કન્ફર્મ' ELSE 'Confirm' END "
			+ " WHEN status = 2 THEN CASE WHEN :lang = 0 THEN 'Cancel' WHEN :lang = 1 THEN 'कैंसल' WHEN :lang = 2 THEN 'કેન્સલ' ELSE 'Cancel' END "
			+ " WHEN status = 3 THEN CASE WHEN :lang = 0 THEN 'Tentative' WHEN :lang = 1 THEN 'टेंटेटिव' WHEN :lang = 2 THEN 'ટેન્ટેટિવ' ELSE 'Tentative' END "
			+ " ELSE '' "
			+ " END AS status, "
			+	" ef.event_function_id as eventFunctionId, "
			+ " e.event_no, "
			+ " DATE_FORMAT(e.inquiry_date, '%d/%m/%Y') AS inquiry_date, "
			+ " e.mobileno, q.sub_total,q.transportation,q.grand_total "
			+ " FROM events e " + " LEFT JOIN `eventtype` et ON et.event_type_id = e.event_type_id "
			+ " LEFT JOIN `event_function` ef ON ef.event_id = e.event_id "
			+ " LEFT JOIN `functions` f ON f.function_id = ef.function_master_id "
			+ " LEFT JOIN `venuemaster` v ON v.venue_id = e.venue_id "
			+ " LEFT JOIN partymaster pm1 ON pm1.party_id = e.party_id "
			+ " LEFT JOIN quotations q ON q.event_id = e.event_id and q.is_decore = FALSE "
			+ " LEFT JOIN users pm2 ON pm2.user_id = e.manager_id " + " WHERE "
			+ " DATE(e.event_start_date_time) BETWEEN :firstDate AND :lastDate " + " AND e.user_id = :userid "
			+ " AND e.is_delete = FALSE " + " AND et.is_delete = FALSE " + " AND ef.is_delete = FALSE "
			+ " AND f.is_delete = FALSE " + " AND  pm1.is_delete = FALSE "
			+ " AND pm2.is_delete = FALSE " + " AND e.status IN (:eventStatus) "
			+ " AND (:partyId = -1 OR pm1.party_id = :partyId)"
			+ " AND (:flag = true OR e.manager_id IN (:managerIds)) "
			+ " ORDER BY e.event_start_date_time, ef.function_start_date_time ", nativeQuery = true)
	List<Object[]> findDatewiseOrderSummary3(LocalDate firstDate, LocalDate lastDate, Long userid, int lang,
			List<Integer> eventStatus, List<Long> managerIds, Boolean flag, Long partyId);
	
	@Query(value = " "
			+ " SELECT "
	        + "    mpd.menu_preparation_details_id, "
	        + "    mpd.menu_item_id, "
	        + "    mi.name_english, "
	        + "    mi.name_hindi, "
	        + "    mi.name_gujarati "
	        + " FROM menupreparationdetails mpd "
	        + " LEFT JOIN menupreparation mp "
	        + "    ON mp.menu_preparation_id = mpd.menu_preparation_id "
	        + "    AND mp.is_delete = FALSE "
	        + " LEFT JOIN memuitems mi "
	        + "    ON mi.menu_item_id = mpd.menu_item_id "
	        + " LEFT JOIN event_function ef "
	        + "    ON ef.event_function_id = mp.event_function_id "
	        + "    AND ef.is_delete = FALSE "
	        + " WHERE ( "
	        + "    (:event_function_id = -1 AND ef.event_function_id IN ( "
	        + "        SELECT event_function_id "
	        + "        FROM event_function "
	        + "        WHERE event_id = :event_id "
	        + "        AND is_delete = FALSE "
	        + "    )) "
	        + "    OR "
	        + "    (:event_function_id != -1 "
	        + "     AND ef.event_function_id = :event_function_id) "
	        + " )",
	        nativeQuery = true)
	List<Object[]> getMenuPreparationDetails(
	        @Param("event_function_id") Long eventFunctionId,
	        @Param("event_id") Long eventId);
}
