package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.NamePlateTableMenuWithBgEntity;

@Repository
public interface NamePlateTableMenuWithBgRepository extends JpaRepository<NamePlateTableMenuWithBgEntity, Long>{

	
	Optional<NamePlateTableMenuWithBgEntity> findByIdAndIsDeleteFalse(Long id);
	
	
//	@Query(value = 
//			" SELECT " +
//	        " nptmbg.name_plate_id, " +
//	        " nptmbg.menu_item_id, " +
//	        " nptmbg.item_name_english, " +
//	        " nptmbg.item_name_gujarati, " +
//	        " nptmbg.item_name_hindi, " +
//	        " nptmbg.item_count, " +
//	        " nptmbg.sequence, " +
//	        " nptmbg.event_id, " +
//	        " nptmbg.event_function_id,"+
//	        " e.event_no " +
//	        " FROM menupreparationdetails mpd " +
//	        " LEFT JOIN menupreparation mp ON mpd.menu_preparation_id = mp.menu_preparation_id " +
//	        " LEFT JOIN name_plate_table_menu_with_bg nptmbg ON nptmbg.menu_item_id = mpd.menu_item_id " +
//	        " LEFT JOIN event_function ef ON ef.event_function_id = nptmbg.event_function_id " +
//	        " LEFT JOIN events e ON e.event_id = nptmbg.event_id " +
//	        " WHERE nptmbg.user_id = :userId " +
//	        " AND nptmbg.event_id = :eventId " +
//	        " AND nptmbg.event_function_id = :eventFunctionId " +
//	        " AND nptmbg.is_delete = FALSE " +
//	        " ORDER BY nptmbg.sequence",
//	        nativeQuery = true)
//	List<Object[]> findAllNamePlateTableMenuItemsWithBg(
//	        @Param("userId") Long userId,
//	        @Param("eventId") Long eventId,
//	        @Param("eventFunctionId") Long eventFunctionId
//	);
	
	@Query(value = "SELECT " +
	        " nptmbg.name_plate_id, " +
	        " CASE WHEN nptmbg.menu_item_id IS NULL THEN mpd.menu_item_id ELSE nptmbg.menu_item_id END AS menu_item_id, " +
	        " CASE WHEN nptmbg.menu_item_id IS NULL THEN mpd.menuitem_name ELSE " +
	        "      CASE WHEN nptmbg.item_name_english IS NULL OR LENGTH(TRIM(nptmbg.item_name_english)) = 0 " +
	        "           THEN mpd.menuitem_name ELSE nptmbg.item_name_english END END AS item_name_english, " +
	        " CASE WHEN nptmbg.menu_item_id IS NULL THEN mpd.menuitem_name_hindi ELSE " +
	        "      CASE WHEN nptmbg.item_name_hindi IS NULL OR LENGTH(TRIM(nptmbg.item_name_hindi)) = 0 " +
	        "           THEN mpd.menuitem_name_hindi ELSE nptmbg.item_name_hindi END END AS item_name_hindi, " +
	        " CASE WHEN nptmbg.menu_item_id IS NULL THEN mpd.menuitem_name_gujarati ELSE " +
	        "      CASE WHEN nptmbg.item_name_gujarati IS NULL OR LENGTH(TRIM(nptmbg.item_name_gujarati)) = 0 " +
	        "           THEN mpd.menuitem_name_gujarati ELSE nptmbg.item_name_gujarati END END AS item_name_gujarati, " +
	        " CASE WHEN nptmbg.menu_item_id IS NULL THEN 1 ELSE nptmbg.item_count END AS item_count, " +
	        " nptmbg.sequence, " +
	        " nptmbg.is_checked, " +
	        " e.event_id, " +
	        " nptmbg.event_function_id, " +
	        " e.event_no, " +
	        " mpd.menu_category_id, " +
	        " mpd.menu_category_name  AS cat_name_eng, " +
	        " mpd.menu_category_name_gujarati AS cat_name_guj, " +
	        " mpd.menu_category_name_hindi AS cat_name_hindi, " +
	        " nptmbg.cat_font_size AS cat_font_size, " +
	        " nptmbg.item_font_size AS item_font_size, " +
	        " nptmbg.header_notes_english," +
	        " nptmbg.header_notes_hindi," +
	        " nptmbg.header_notes_gujarati," +
	        " nptmbg.footer_notes_english," +
	        " nptmbg.footer_notes_hindi," +
	        " nptmbg.footer_notes_gujarati " +
	        " FROM menupreparationdetails mpd " +
	        " LEFT JOIN ( " +
	        "     SELECT * FROM name_plate_table_menu_with_bg x " +
	        "     WHERE x.event_id = :eventId " +
	        "       AND x.event_function_id = :eventFunctionId " +
	        "       AND EXISTS ( " +
	        "            SELECT 1 FROM name_plate_table_menu_with_bg y " +
	        "            WHERE y.event_id = :eventId " +
	        "              AND y.event_function_id = :eventFunctionId ) " +
	        " ) nptmbg ON mpd.menu_item_id = nptmbg.menu_item_id " +
	        " LEFT JOIN menupreparation mp ON mp.menu_preparation_id = mpd.menu_preparation_id " +
	        " LEFT JOIN event_function ef ON ef.event_function_id = mp.event_function_id " +
	        " INNER JOIN events e ON e.event_id = ef.event_id " +
	        " WHERE e.user_id = :userId " +
	        "   AND e.event_id = :eventId " +
	        "   AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) " +
	        " ORDER BY " +
	        "    CASE " +
			"        WHEN nptmbg.sequence IS NULL THEN ef.sortorder" +
			"        ELSE nptmbg.sequence" +
			"    END," +
	        "    CASE " +
	        "        WHEN nptmbg.sequence IS NULL THEN mpd.menu_sortorder" +
	        "        ELSE nptmbg.sequence" +
	        "    END," +
	        "    CASE " +
	        "        WHEN nptmbg.sequence IS NULL THEN mpd.item_sortorder" +
	        "        ELSE 0" +
	        "    END ",
	        nativeQuery = true)
	List<Object[]> findAllNamePlateTableMenuWithBg(
	        @Param("eventId") Long eventId,
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("userId") Long userId);
}
