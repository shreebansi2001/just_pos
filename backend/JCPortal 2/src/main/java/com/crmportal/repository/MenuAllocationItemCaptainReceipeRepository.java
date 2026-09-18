package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.MenuAllocationItemCaptainReceipeEntity;

@Repository
public interface MenuAllocationItemCaptainReceipeRepository extends JpaRepository<MenuAllocationItemCaptainReceipeEntity, Long> {

	List<MenuAllocationItemCaptainReceipeEntity> findAllByMenuItem_IdAndEventFunctionAndIsDeleteFalse(Long menuItemId, EventFunctionMasterEntity eventFunction);

	@Modifying
	@Query(value = "DELETE FROM menu_allocation_item_captain_receipe " + "WHERE menu_item_id = :menuItemId "
			+ "AND event_id = :eventId " + "AND eventfunction_id = :eventFunctionId", nativeQuery = true)
	void deleteAllocatedCaptainReceipe(@Param("menuItemId") Long menuItemId,
			@Param("eventId") Long eventId, @Param("eventFunctionId") Long eventFunctionId);

	long deleteAllByEventFunction(EventFunctionMasterEntity eventFunctionMasterEntity);
	
	@Query(value = " "
			+ " SELECT DISTINCT "
			+ "    rm.raw_material_id AS rawMaterialId, "
			+ "    CASE "
			+ "        WHEN :lang = 1 THEN CONCAT(rm.name_hindi, ' - ', crm.name) "
			+ "        WHEN :lang = 2 THEN CONCAT(rm.name_gujarati, ' - ', crm.name) "
			+ "        ELSE CONCAT(rm.name_english, ' - ', crm.name) "
			+ "    END AS raw_material_name, "
			+ "    ( "
			+ "        ( "
			+ "            CASE "
			+ "                WHEN maicr.unit_id = crm.unit_id "
			+ "                    THEN IFNULL(maicr.weight, 0) "
			+ "                WHEN mu.is_parent_unit = 1 "
			+ "                     AND cu.is_parent_unit = 0 "
			+ "                    THEN IFNULL(maicr.weight, 0) * 1000 "
			+ "                WHEN mu.is_parent_unit = 0 "
			+ "                     AND cu.is_parent_unit = 1 "
			+ "                    THEN ROUND(IFNULL(maicr.weight, 0) / 1000, 4) "
			+ "                ELSE IFNULL(maicr.weight, 0) "
			+ "            END "
			+ "        ) "
			+ "        * "
			+ "        ( "
			+ "            CASE "
			+ "                WHEN crrm.unit_id = crm.unit_id "
			+ "                    THEN IFNULL(crrm.qty, 0) "
			+ "                WHEN ru.is_parent_unit = 1 "
			+ "                     AND cu.is_parent_unit = 0 "
			+ "                    THEN IFNULL(crrm.qty, 0) * 1000 "
			+ ""
			+ "                WHEN ru.is_parent_unit = 0 "
			+ "                     AND cu.is_parent_unit = 1 "
			+ "                    THEN ROUND(IFNULL(crrm.qty, 0) / 1000, 4) "
			+ "                ELSE IFNULL(crrm.qty, 0) "
			+ "            END "
			+ "        ) "
			+ "    ) / crm.weight AS finalQty, "
			+ "    ru.unit_id AS unitId, "
			+ "    CASE "
			+ "        WHEN :lang = 1 THEN ru.name_hindi "
			+ "        WHEN :lang = 2 THEN ru.name_gujarati "
			+ "        ELSE ru.name_english "
			+ "    END AS unitName, "
			+ "    rmc.raw_matrial_cat_id, "
			+ "    CASE "
			+ "        WHEN :lang = 1 THEN rmc.name_hindi "
			+ "        WHEN :lang = 2 THEN rmc.name_gujarati "
			+ "        ELSE rmc.name_english "
			+ "    END AS raw_material_cat_name, "
			+ "    mi.menu_item_id AS menu_item_id "
			+ "	FROM menu_allocation_item_captain_receipe maicr "
			+ "	INNER JOIN memuitems mi "
			+ "    ON mi.menu_item_id = maicr.menu_item_id "
			+ "	INNER JOIN captain_receipe_master crm "
			+ "    ON crm.id = maicr.captain_receipe_id "
			+ "	INNER JOIN captain_receipe_raw_material crrm "
			+ "    ON crrm.captain_receipe_id = crm.id "
			+ "    AND crrm.is_delete = FALSE "
			+ "	INNER JOIN rawmaterial rm "
			+ "    ON rm.raw_material_id = crrm.raw_item_id "
			+ "	INNER JOIN raw_material_category rmc "
			+ "    ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
			+ "	LEFT JOIN partymaster p "
			+ "    ON maicr.party_id = p.party_id "
			+ "	LEFT JOIN units mu "
			+ "    ON mu.unit_id = maicr.unit_id "
			+ "	LEFT JOIN units cu "
			+ "    ON cu.unit_id = crm.unit_id "
			+ "	LEFT JOIN units ru "
			+ "    ON ru.unit_id = crrm.unit_id "
			+ "	INNER JOIN units u "
			+ "    ON u.unit_id = crm.unit_id "
			+ "	INNER JOIN event_function ef "
			+ "    ON maicr.eventfunction_id = ef.event_function_id "
			+ "	INNER JOIN functions f "
			+ "    ON ef.function_master_id = f.function_id "
			+ "	INNER JOIN eventfunction_menuallocation ema "
			+ "    ON ema.eventfunction_id = maicr.eventfunction_id "
			+ "    AND ema.menu_item_id = maicr.menu_item_id "
			+ "    AND ema.outside = FALSE"
			+ "	WHERE maicr.event_id = :eventId "
			+ "    AND ("
			+ "        :eventFunctionId IS NULL"
			+ "        OR :eventFunctionId = -1 "
			+ "        OR maicr.eventfunction_id = :eventFunctionId "
			+ "    )"
			+ "    AND maicr.is_delete = FALSE"
			+ "    AND crm.is_delete = FALSE"
			+ "    AND NOT EXISTS ("
			+ "        SELECT 1"
			+ "        FROM eventfunction_rawmaterial_permission p "
			+ "        WHERE p.raw_material_id = rm.raw_material_id "
			+ "            AND p.event_id = :eventId "
			+ "            AND ("
			+ "					:eventFunctionId IS NULL"
			+ "					OR :eventFunctionId = -1 "
			+ "					OR maicr.eventfunction_id = :eventFunctionId "
			+ "	    		)"
			+ "            AND p.type = 'NotPermissable'"
			+ "    ) ", nativeQuery = true)
	List<Object[]> getAllCaptainReceipeRawMaterial(
			@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId,
			@Param("lang") Integer lang);
}
