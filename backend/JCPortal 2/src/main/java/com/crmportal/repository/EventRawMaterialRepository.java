package com.crmportal.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventRawMaterialEntity;
import com.crmportal.response.dto.EventRawMaterialPartyDTO;
import com.crmportal.response.dto.EventRawMaterialResponse;
import com.crmportal.response.dto.EventWiseGeneralFixResponseDto;

@Repository
public interface EventRawMaterialRepository extends JpaRepository<EventRawMaterialEntity, Long> {

	@Query(value = "SELECT ef.event_id AS eventId,IFNULL(rms.party_id,0) AS supplierId, IFNULL(pt.name_english,'') AS supplierName,rm.raw_material_id AS rawMaterialId "
			+ " ,rm.name_english AS rawMaterialNameEng,rm.name_gujarati AS rawMaterialNameGuj, rm.name_hindi AS rawMaterialNameHin,  "
			+ " mrw.unit_id AS unitId,un.name_english AS unitName,((ef.pax*mrw.weight)/100) AS qty, ((ef.pax*mrw.weight)/100) AS finalQty, '' AS place, "
			+ " ef.function_master_id AS functionId, ef.event_function_id AS eventFunctionId,fun.name_english AS functionName,mpd.menuitem_name as itemName,ef.function_start_date_time, ((((ef.pax*mrw.weight)/100)*mrw.rate)/mrw.weight) AS totalPrice,mrw.rate as rate, mpd.menu_item_id "
			+ " ,rmc.raw_matrial_cat_id, rm.is_apply_cal FROM menupreparationdetails mpd "
			+ " INNER JOIN menu_item_raw_material mrw ON mpd.menu_item_id = mrw.menu_item_id " 
			+ " AND (mrw.is_visible IS NULL OR mrw.is_visible = TRUE) " 
			+ " AND mrw.is_delete = FALSE " 
			+ " INNER JOIN units un ON mrw.unit_id = un.unit_id "
			+ " INNER JOIN rawmaterial rm ON mrw.raw_material_id = rm.raw_material_id "
			+ " LEFT JOIN raw_material_supplier rms ON rm.raw_material_id = rms.raw_material_id "
			+ " LEFT JOIN partymaster pt ON pt.party_id = rms.party_id "
			+ " INNER JOIN raw_material_category rmc ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id AND rmc.raw_matrial_cat_id =:rawMateriaCatlId "
			+ " LEFT JOIN menupreparation mp ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ " LEFT JOIN event_function ef ON mp.event_function_id = ef.event_function_id "
			+ " LEFT JOIN functions fun ON ef.function_master_id = fun.function_id WHERE ef.event_id =:eventId "
		      + " AND NOT EXISTS ( "
		        + "    SELECT 1 "
		        + "    FROM eventfunction_rawmaterial_permission p "
		        + "    WHERE p.raw_material_id = rm.raw_material_id "
		        + "      AND p.event_id = :eventId "
		        + "      AND p.eventfunction_id IN (:eventFunctionIds) "
		        + "      AND p.type = 'NotPermissable' "
		        + ")", nativeQuery = true)
	List<Object[]> getEventRawMaterialIfDoesNotExist(@Param("eventId") Long eventId,
			@Param("rawMateriaCatlId") Long rawMateriaCatlId, @Param("eventFunctionIds") List<Long> eventFunctionIds);

//	@Query(value = " SELECT  " + " 	ef.event_id AS eventId, " + " 	IFNULL(rms.party_id,0) AS supplierId, "
//			+ " 	IFNULL(pt.name_english,'') AS supplierName, " + " 	rm.raw_material_id AS rawMaterialId, "
//			+ " 	rm.name_english AS rawMaterialNameEng, " + " 	rm.name_gujarati AS rawMaterialNameGuj, "
//			+ " 	rm.name_hindi AS rawMaterialNameHin, " + " 	u.unit_id AS unit_id, "
//			+ " 	u.name_english AS unit_name, "
//			// + " ((ef.pax * crr.qty) / 100) AS qty, "
//			// + " ((ef.pax * crr.qty) / 100) AS finalQty, "
//			+ " 	((((crr.qty * micr.weight) / crm.weight) * ef.pax) / 100) AS qty, "
//			+ " 	((((crr.qty * micr.weight) / crm.weight) * ef.pax) / 100) AS finalQty, " + " 	'' AS place, "
//			+ " 	ef.function_master_id AS functionId, " + " 	ef.event_function_id AS eventFunctionId, "
//			+ " 	fun.name_english AS functionName, "
//			+ " 	CONCAT(mpd.menuitem_name, ' (', crm.name ,')') AS itemName, " + " 	ef.function_start_date_time, "
//			+ " 	((((((crr.qty * micr.weight) / crm.weight) * ef.pax) / 100)*crr.rate)/crr.qty) AS totalPrice, "
//			+ " 	crr.rate AS rate, " + " 	mpd.menu_item_id, " + " 	rmc.raw_matrial_cat_id, "
//			+ " 	rm.is_apply_cal " + " FROM menupreparation mp " + " INNER JOIN menupreparationdetails mpd "
//			+ " 	ON mp.menu_preparation_id = mpd.menu_preparation_id " + " INNER JOIN memuitems mi "
//			+ " 	ON mi.menu_item_id = mpd.menu_item_id " + " INNER JOIN menu_item_captain_receipe micp "
//			+ " 	ON micp.menu_item_id = mi.menu_item_id" + " 	AND micp.is_delete = FALSE "
//			+ " INNER JOIN captain_receipe_master crm " + " 	ON crm.id = micp.captain_receipe_id "
//			+ " INNER JOIN captain_receipe_raw_material crr " + " 	ON crm.id = crr.captain_receipe_id "
//			+ " INNER JOIN rawmaterial rm " + " 	ON rm.raw_material_id = crr.raw_item_id "
//			+ " LEFT JOIN raw_material_supplier rms  " + " 	ON rm.raw_material_id = rms.raw_material_id "
//			+ " LEFT JOIN partymaster pt  " + " 	ON pt.party_id = rms.party_id " + " INNER JOIN units u "
//			+ " 	ON u.unit_id = crr.unit_id " + " INNER JOIN raw_material_category rmc  "
//			+ " 	ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id  "
//			+ " 	AND rmc.raw_matrial_cat_id = :rawMateriaCatlId " + " LEFT JOIN event_function ef "
//			+ " 	ON mp.event_function_id = ef.event_function_id " + " LEFT JOIN functions fun "
//			+ " 	ON ef.function_master_id = fun.function_id " + " WHERE  " + " 	ef.event_id = :eventId  "
//			+ " 	AND crr.is_delete = FALSE ", nativeQuery = true)
//	List<Object[]> getEventCaptainReceipeRawMaterailIfDoesNotExist(@Param("eventId") Long eventId,
//			@Param("rawMateriaCatlId") Long rawMateriaCatlId);
	
	@Query(value = ""
	        + " SELECT "
	        + " ef.event_id AS eventId, "
	        + " IFNULL(rms.party_id,0) AS supplierId, "
	        + " IFNULL(pt.name_english,'') AS supplierName, "
	        + " rm.raw_material_id AS rawMaterialId, "
	        + " rm.name_english AS rawMaterialNameEng, "
	        + " rm.name_gujarati AS rawMaterialNameGuj, "
	        + " rm.name_hindi AS rawMaterialNameHin, "

	        + " t.unitId AS unit_id, "
	        + " t.unitName AS unit_name, "

	        + " ((((t.convertedQty * t.convertedWeight) / t.recipeWeight) * ef.pax) / 100) AS qty, "
	        + " ((((t.convertedQty * t.convertedWeight) / t.recipeWeight) * ef.pax) / 100) AS finalQty, "

	        + " '' AS place, "
	        + " ef.function_master_id AS functionId, "
	        + " ef.event_function_id AS eventFunctionId, "
	        + " fun.name_english AS functionName, "

	        + " CONCAT(mpd.menuitem_name,' (',crm.name,')') AS itemName, "

	        + " ef.function_start_date_time, "

	        + " ((((((t.convertedQty * t.convertedWeight) / t.recipeWeight) * ef.pax) / 100) * t.rate) / t.convertedQty) AS totalPrice, "

	        + " t.rate AS rate, "
	        + " mpd.menu_item_id, "
	        + " rmc.raw_matrial_cat_id, "
	        + " rm.is_apply_cal "

	        + " FROM ( "

	        + " SELECT "
	        + " ef.event_id AS event_id, "
	        + " mi.menu_item_id AS menuItemId, "                 
	        + " rm.raw_material_id AS rawMaterialId, "
	        + " crr.qty, "
	        + " crr.rate, "
	        + " crm.weight AS recipeWeight, "
	        + " crm.id AS captainRecipeId, "
	        + " ru.unit_id AS unitId, "
	        + " ru.english_name AS unitName, "
	        + " CASE "
	        + " WHEN micp.unit_id = crm.unit_id "
	        + " THEN IFNULL(micp.weight,0) "
	        + " WHEN mu.is_parent_unit = 1 AND cu.is_parent_unit = 0 "
	        + " THEN IFNULL(micp.weight,0) * 1000 "
	        + " WHEN mu.is_parent_unit = 0 AND cu.is_parent_unit = 1 "
	        + " THEN ROUND(IFNULL(micp.weight,0)/1000,4) "
	        + " ELSE IFNULL(micp.weight,0) "
	        + " END AS convertedWeight, "

	        + " CASE "
	        + " WHEN crr.unit_id = crm.unit_id "
	        + " THEN IFNULL(crr.qty,0) "
	        + " WHEN ru.is_parent_unit = 1 AND cu.is_parent_unit = 0 "
	        + " THEN IFNULL(crr.qty,0) * 1000 "
	        + " WHEN ru.is_parent_unit = 0 AND cu.is_parent_unit = 1 "
	        + " THEN ROUND(IFNULL(crr.qty,0)/1000,4) "
	        + " ELSE IFNULL(crr.qty,0) "
	        + " END AS convertedQty "
	        + " FROM menupreparation mp "

	        + " INNER JOIN menupreparationdetails mpd "
	        + " ON mp.menu_preparation_id = mpd.menu_preparation_id "

	        + " INNER JOIN memuitems mi "
	        + " ON mi.menu_item_id = mpd.menu_item_id "

	        + " INNER JOIN menu_item_captain_receipe micp "
	        + " ON micp.menu_item_id = mi.menu_item_id "
	        + " AND micp.is_delete = FALSE "

	        + " INNER JOIN captain_receipe_master crm "
	        + " ON crm.id = micp.captain_receipe_id "
	        + " AND crm.is_delete = FALSE "

	        + " INNER JOIN captain_receipe_raw_material crr "
	        + " ON crr.captain_receipe_id = crm.id "
	        + " AND crr.is_delete = FALSE "

	        + " INNER JOIN rawmaterial rm "
	        + " ON rm.raw_material_id = crr.raw_item_id "

	        + " INNER JOIN raw_material_category rmc "
	        + " ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "

	        + " LEFT JOIN event_function ef "
	        + " ON ef.event_function_id = mp.event_function_id "

	        + " LEFT JOIN units mu "
	        + " ON mu.unit_id = micp.unit_id "

	        + " LEFT JOIN units cu "
	        + " ON cu.unit_id = crm.unit_id "

	        + " LEFT JOIN units ru "
	        + " ON ru.unit_id = crr.unit_id "

	        + " WHERE ef.event_id = :eventId "
	        + " AND rmc.raw_matrial_cat_id = :rawMateriaCatlId "
	        + " AND NOT EXISTS ( "
	        + "     SELECT 1 "
	        + "     FROM eventfunction_rawmaterial_permission p "
	        + "     WHERE p.raw_material_id = rm.raw_material_id "
	        + "       AND p.event_id = :eventId "
	        + "       AND p.eventfunction_id IN (:eventFunctionIds) "
	        + "       AND p.type = 'NotPermissable' "
	        + "          ) "
	        + " ) t "

	        + " INNER JOIN menupreparationdetails mpd "
	        + " ON mpd.menu_item_id = t.menuItemId "

	        + " INNER JOIN rawmaterial rm "
	        + " ON rm.raw_material_id = t.rawMaterialId "

	        + " LEFT JOIN raw_material_supplier rms "
	        + " ON rms.raw_material_id = rm.raw_material_id "

	        + " LEFT JOIN partymaster pt "
	        + " ON pt.party_id = rms.party_id "

	        + " INNER JOIN captain_receipe_master crm "
	        + " ON crm.id = t.captainRecipeId "

	        + " INNER JOIN units u "
	        + " ON u.unit_id = crm.unit_id "

	        + " LEFT JOIN event_function ef "
	        + " ON ef.event_id = t.event_id "

	        + " LEFT JOIN functions fun "
	        + " ON fun.function_id = ef.function_master_id "

	        + " INNER JOIN raw_material_category rmc "
	        + " ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "  

	        , nativeQuery = true)
	List<Object[]> getEventCaptainReceipeRawMaterailIfDoesNotExist(
	        @Param("eventId") Long eventId,
	        @Param("rawMateriaCatlId") Long rawMateriaCatlId,
	        @Param("eventFunctionIds") List<Long> eventFunctionIds);

			@Query(value = "SELECT ef.event_id AS eventId,IFNULL(rms.party_id,0) AS supplierId, IFNULL(pt.name_english,'') AS supplierName,"
			        + "rm.raw_material_id AS rawMaterialId, "
			        + "rm.name_english AS rawMaterialNameEng,rm.name_gujarati AS rawMaterialNameGuj, rm.name_hindi AS rawMaterialNameHin,"
			        + "mrw.unit_id AS unitId,un.name_english AS unitName,((ef.pax*mrw.weight)/100) AS qty,"
			        + "((ef.pax*mrw.weight)/100) AS finalQty, '' AS place,"
			        + "ef.function_master_id AS functionId, ef.event_function_id AS eventFunctionId,"
			        + "fun.name_english AS functionName,mpd.menuitem_name as itemName,ef.function_start_date_time,"
			        + "((((ef.pax*mrw.weight)/100)*mrw.rate)/mrw.weight) AS totalPrice,mrw.rate as rate,"
			        + "mpd.menu_item_id,rmc.raw_matrial_cat_id,rm.is_apply_cal "
			        + "FROM menupreparationdetails mpd "
			        + "INNER JOIN menu_item_raw_material mrw ON mpd.menu_item_id = mrw.menu_item_id "
			        + "INNER JOIN units un ON mrw.unit_id = un.unit_id "
			        + "INNER JOIN rawmaterial rm ON mrw.raw_material_id = rm.raw_material_id "
			        + "LEFT JOIN raw_material_supplier rms ON rm.raw_material_id = rms.raw_material_id "
			        + "LEFT JOIN partymaster pt ON pt.party_id = rms.party_id "
			        + "INNER JOIN raw_material_category rmc ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
			        + "LEFT JOIN menupreparation mp ON mpd.menu_preparation_id = mp.menu_preparation_id "
			        + "LEFT JOIN event_function ef ON mp.event_function_id = ef.event_function_id "
			        + "LEFT JOIN functions fun ON ef.function_master_id = fun.function_id "
			        + "WHERE ef.event_id = :eventId "
			        + "AND ef.event_function_id = :eventFunctionId "
			        + "AND mpd.menu_item_id IN (:menuItemIds) "
			        + "AND mrw.is_delete = false "
			        + "AND (mrw.is_visible IS NULL OR mrw.is_visible = TRUE) "
			        + "AND NOT EXISTS ( "
			        + "    SELECT 1 "
			        + "    FROM eventfunction_rawmaterial_permission p "
			        + "    WHERE p.raw_material_id = mrw.raw_material_id "
			        + "      AND p.event_id = ef.event_id "
			        + "      AND p.eventfunction_id = ef.event_function_id "
			        + "      AND p.type = 'NotPermissable'"
			        + ")",
			        nativeQuery = true)
			List<Object[]> getEventRawMaterialByMenuPreparation(
			        @Param("eventId") Long eventId,
			        @Param("eventFunctionId") Long eventFunctionId,
			        @Param("menuItemIds") List<Long> menuItemIds);

//	@Query(value = "" + " select " + " 	ef.event_id AS eventId, " + " 	IFNULL(rms.party_id,0) AS supplierId, "
//			+ " 	IFNULL(pt.name_english,'') AS supplierName, " + " 	rm.raw_material_id AS rawMaterialId, "
//			+ " 	rm.name_english AS rawMaterialNameEng, " + " 	rm.name_gujarati AS rawMaterialNameGuj,  "
//			+ " 	rm.name_hindi AS rawMaterialNameHin, " + " 	crrm.unit_id AS unitId, "
//			+ " 	un.name_english AS unitName, "
//			+ " 	((((crrm.qty * micr.weight) / crm.weight) * ef.pax) / 100) AS qty, "
//			+ " 	((((crrm.qty * micr.weight) / crm.weight) * ef.pax) / 100) AS finalQty, " + " 	'' AS place, "
//			+ " 	ef.function_master_id AS functionId,  " + " 	ef.event_function_id AS eventFunctionId, "
//			+ " 	fun.name_english AS functionName, "
//			+ " 	CONCAT(mpd.menuitem_name, ' (', crm.name ,')') as itemName, " + " 	ef.function_start_date_time,  "
//			+ " 	((((((crrm.qty * micr.weight) / crm.weight) * ef.pax) / 100) * crrm.rate) / crrm.qty) AS totalPrice, "
//			+ " 	crrm.rate as rate,  " + " 	mpd.menu_item_id, " + " 	rmc.raw_matrial_cat_id,  "
//			+ " 	rm.is_apply_cal " + " from menupreparationdetails mpd " + " INNER JOIN memuitems mi "
//			+ " 	ON mi.menu_item_id = mpd.menu_item_id " + " INNER JOIN menu_item_captain_receipe micr "
//			+ " 	ON micr.menu_item_id = mi.menu_item_id " + " INNER JOIN captain_receipe_master crm "
//			+ " 	ON crm.id = micr.captain_receipe_id " + " INNER JOIN captain_receipe_raw_material crrm "
//			+ " 	ON crrm.captain_receipe_id = crm.id " + " INNER JOIN units un  "
//			+ " 	ON crrm.unit_id = un.unit_id " + " INNER JOIN rawmaterial rm  "
//			+ " 	ON crrm.raw_item_id = rm.raw_material_id " + " LEFT JOIN raw_material_supplier rms  "
//			+ " 	ON rm.raw_material_id = rms.raw_material_id " + " LEFT JOIN partymaster pt  "
//			+ " 	ON pt.party_id = rms.party_id  " + " INNER JOIN raw_material_category rmc  "
//			+ " 	ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id " + " LEFT JOIN menupreparation mp  "
//			+ " 	ON mpd.menu_preparation_id = mp.menu_preparation_id " + " LEFT JOIN event_function ef  "
//			+ " 	ON mp.event_function_id = ef.event_function_id " + " LEFT JOIN functions fun  "
//			+ " 	ON ef.function_master_id = fun.function_id " + " WHERE  " + " 	ef.event_id = :eventId  "
//			+ " 	AND ef.event_function_id = :eventFunctionId " + " 	AND mpd.menu_item_id IN (:menuItemIds) "
//			+ " 	AND crrm.is_delete = false AND micr.is_delete = false AND crm.is_delete = false ", nativeQuery = true)
//	List<Object[]> getEventCaptainReceipeRawMaterialByMenuPreparation(@Param("eventId") Long eventId,
//			@Param("eventFunctionId") Long eventFunctionId, @Param("menuItemIds") List<Long> menuItemIds);
	
	@Query(value = ""
			+ " SELECT "
			+ " t.eventId AS eventId, "
			+ " t.supplierId AS supplierId, "
			+ " t.supplierName AS supplierName, "
			+ " t.rawMaterialId AS rawMaterialId, "
			+ " t.rawMaterialNameEng AS rawMaterialNameEng, "
			+ " t.rawMaterialNameGuj AS rawMaterialNameGuj, "
			+ " t.rawMaterialNameHin AS rawMaterialNameHin, "
			+ " t.unitId AS unitId, "
			+ " t.unitName AS unitName, "
			+ " (((t.convertedWeight * t.convertedQty) / t.recipeWeight) * t.pax) / 100 AS qty, "
			+ " (((t.convertedWeight * t.convertedQty) / t.recipeWeight) * t.pax) / 100 AS finalQty, "
			+ " '' AS place, "
			+ " t.functionId AS functionId, "
			+ " t.eventFunctionId AS eventFunctionId, "
			+ " t.functionName AS functionName, "
			+ " t.itemName AS itemName, "
			+ " t.function_start_date_time, "
			+ " (((((t.convertedWeight * t.convertedQty) / t.recipeWeight) * t.pax) / 100) * t.rate) / t.convertedQty AS totalPrice, "
			+ " t.rate, "
			+ " t.menu_item_id, "
			+ " t.raw_matrial_cat_id, "
			+ " t.is_apply_cal "
			+ " FROM ( "
			+ " SELECT DISTINCT "
			+ " ef.event_id AS eventId, "
			+ " IFNULL(rms.party_id,0) AS supplierId, "
			+ " IFNULL(pt.name_english,'') AS supplierName, "
			+ " rm.raw_material_id AS rawMaterialId, "
			+ " rm.name_english AS rawMaterialNameEng, "
			+ " rm.name_gujarati AS rawMaterialNameGuj, "
			+ " rm.name_hindi AS rawMaterialNameHin, "
			+ " ru.unit_id AS unitId, "
			+ " ru.name_english AS unitName, "
			+ " ef.function_master_id AS functionId, "
			+ " ef.event_function_id AS eventFunctionId, "
			+ " fun.name_english AS functionName, "
			+ " CONCAT(mpd.menuitem_name,' (',crm.name,')') AS itemName, "
			+ " ef.function_start_date_time, "
			+ " crm.weight AS recipeWeight, "
			+ " ef.pax AS pax, "
			+ " crrm.rate AS rate, "
			+ " mpd.menu_item_id, "
			+ " rmc.raw_matrial_cat_id, "
			+ " rm.is_apply_cal, "

			+ " CASE "
			+ " WHEN micr.unit_id = crm.unit_id THEN IFNULL(micr.weight,0) "
			+ " WHEN mu.is_parent_unit = 1 AND cu.is_parent_unit = 0 THEN IFNULL(micr.weight,0) * 1000 "
			+ " WHEN mu.is_parent_unit = 0 AND cu.is_parent_unit = 1 THEN ROUND(IFNULL(micr.weight,0)/1000,4) "
			+ " ELSE IFNULL(micr.weight,0) "
			+ " END AS convertedWeight, "

			+ " CASE "
			+ " WHEN crrm.unit_id = crm.unit_id THEN IFNULL(crrm.qty,0) "
			+ " WHEN ru.is_parent_unit = 1 AND cu.is_parent_unit = 0 THEN IFNULL(crrm.qty,0) * 1000 "
			+ " WHEN ru.is_parent_unit = 0 AND cu.is_parent_unit = 1 THEN ROUND(IFNULL(crrm.qty,0)/1000,4) "
			+ " ELSE IFNULL(crrm.qty,0) "
			+ " END AS convertedQty "
			+ " FROM menupreparationdetails mpd "

			+ " INNER JOIN memuitems mi "
			+ " ON mi.menu_item_id = mpd.menu_item_id "

			+ " INNER JOIN menu_item_captain_receipe micr "
			+ " ON micr.menu_item_id = mi.menu_item_id "
			+ " AND micr.is_delete = FALSE "

			+ " INNER JOIN captain_receipe_master crm "
			+ " ON crm.id = micr.captain_receipe_id "
			+ " AND crm.is_delete = FALSE "

			+ " INNER JOIN captain_receipe_raw_material crrm "
			+ " ON crrm.captain_receipe_id = crm.id "
			+ " AND crrm.is_delete = FALSE "

			+ " INNER JOIN rawmaterial rm "
			+ " ON rm.raw_material_id = crrm.raw_item_id "

			+ " INNER JOIN raw_material_category rmc "
			+ " ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "

			+ " LEFT JOIN raw_material_supplier rms "
			+ " ON rm.raw_material_id = rms.raw_material_id "

			+ " LEFT JOIN partymaster pt "
			+ " ON pt.party_id = rms.party_id "

			+ " LEFT JOIN menupreparation mp "
			+ " ON mp.menu_preparation_id = mpd.menu_preparation_id "

			+ " LEFT JOIN event_function ef "
			+ " ON ef.event_function_id = mp.event_function_id "

			+ " LEFT JOIN functions fun "
			+ " ON fun.function_id = ef.function_master_id "

			+ " LEFT JOIN units mu "
			+ " ON mu.unit_id = micr.unit_id "

			+ " LEFT JOIN units cu "
			+ " ON cu.unit_id = crm.unit_id "

			+ " LEFT JOIN units ru "
			+ " ON ru.unit_id = crrm.unit_id "

			+ " INNER JOIN units un "
			+ " ON un.unit_id = crm.unit_id "
			+ " WHERE ef.event_id = :eventId "
			+ " AND ef.event_function_id = :eventFunctionId "
			+ " AND mpd.menu_item_id IN (:menuItemIds) "
			+ " AND crrm.is_delete = false "
			+ " AND micr.is_delete = false "
			+ " AND crm.is_delete = false "
			+ " AND NOT EXISTS ( "
	        + "     SELECT 1 "
	        + "     FROM eventfunction_rawmaterial_permission p "
	        + "     WHERE p.raw_material_id = rm.raw_material_id "
	        + "       AND p.event_id = ef.event_id "
	        + "       AND p.eventfunction_id = ef.event_function_id "
	        + "       AND p.type = 'NotPermissable' "
	        + " ) "
	        + " ) t ", nativeQuery = true)
			List<Object[]> getEventCaptainReceipeRawMaterialByMenuPreparation(
			        @Param("eventId") Long eventId,
			        @Param("eventFunctionId") Long eventFunctionId,
			        @Param("menuItemIds") List<Long> menuItemIds);

	@Query(value = " SELECT " + " ef.event_id AS eventId, " + " IFNULL(p.party_id, 0) AS supplierId, "
			+ " IFNULL(p.name_english, '') AS supplierName, " + " rm.raw_material_id AS rawMaterialId, "
			+ " rm.name_english AS rawMaterialNameEng, " + " rm.name_gujarati AS rawMaterialNameGuj, "
			+ " rm.name_hindi AS rawMaterialNameHin, " + " u.unit_id AS unitId, " + " u.name_english AS unitName, "
			+ " mairm.weight AS qty, " + " mairm.weight AS finalQty, " + " IFNULL(mairm.place, '') AS place, "
			+ " ef.function_master_id AS functionId, " + " ef.event_function_id AS eventFunctionId, "
			+ " f.name_english AS functionName, " + " mi.name_english AS itemName, " + " ef.function_start_date_time, "
			+ " mairm.rate AS totalPrice, "
			+ " mairm.rawmaterial_rate, mi.menu_item_id, rmc.raw_matrial_cat_id, rm.is_apply_cal "
			+ " FROM menuallocation_item_rawmaterial mairm " + " INNER JOIN rawmaterial rm "
			+ "     ON mairm.raw_material_id = rm.raw_material_id " + " INNER JOIN raw_material_category rmc "
			+ "     ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
			+ "    AND rmc.raw_matrial_cat_id = :rawMaterialCatId " + " LEFT JOIN partymaster p "
			+ "     ON mairm.party_id = p.party_id " + " LEFT JOIN units u " + "     ON mairm.unit_id = u.unit_id "
			+ " INNER JOIN memuitems mi " + "     ON mairm.menu_item_id = mi.menu_item_id "
			+ " INNER JOIN event_function ef " + "     ON mairm.eventfunction_id = ef.event_function_id "
			+ " INNER JOIN functions f " + "     ON ef.function_master_id = f.function_id "
			+ " INNER JOIN eventfunction_menuallocation ema " + "    ON ema.eventfunction_id = ef.event_function_id "
			+ "    AND ema.menu_item_id = mi.menu_item_id" + " WHERE mairm.event_id = :eventId "
			+ "    AND mairm.is_delete = false AND ema.outside = false ", nativeQuery = true)
	List<Object[]> getMenuAllocationRawMaterialData(@Param("eventId") Long eventId,
			@Param("rawMaterialCatId") Long rawMaterialCatId);

	@Query(value = "SELECT ef.event_id AS eventId,IFNULL(rms.party_id,0) AS supplierId, IFNULL(pt.name_english,'') AS supplierName,rm.raw_material_id AS rawMaterialId "
	        + " ,rm.name_english AS rawMaterialNameEng,rm.name_gujarati AS rawMaterialNameGuj, rm.name_hindi AS rawMaterialNameHin,  "
	        + " mrw.unit_id AS unitId,un.name_english AS unitName,((ef.pax*mrw.weight)/100) AS qty, ((ef.pax*mrw.weight)/100) AS finalQty, '' AS place, "
	        + " ef.function_master_id AS functionId, ef.event_function_id AS eventFunctionId,fun.name_english AS functionName,mpd.menuitem_name as itemName,ef.function_start_date_time, ((((ef.pax*mrw.weight)/100)*mrw.rate)/mrw.weight) AS totalPrice, mrw.rate as rate, mpd.menu_item_id "
	        + ",rmc.raw_matrial_cat_id, rm.is_apply_cal FROM menupreparationdetails mpd "
	        + " INNER JOIN menu_item_raw_material mrw ON mpd.menu_item_id = mrw.menu_item_id "
	        + " AND (mrw.is_visible IS NULL OR mrw.is_visible = TRUE) AND mrw.is_delete = false "
	        + " INNER JOIN units un ON mrw.unit_id = un.unit_id "
	        + " INNER JOIN rawmaterial rm ON mrw.raw_material_id = rm.raw_material_id AND mrw.raw_material_id =:rawMateriaId"
	        + " LEFT JOIN raw_material_supplier rms ON rm.raw_material_id = rms.raw_material_id "
	        + " LEFT JOIN partymaster pt ON pt.party_id = rms.party_id "
	        + " INNER JOIN raw_material_category rmc ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
	        + " LEFT JOIN menupreparation mp ON mpd.menu_preparation_id = mp.menu_preparation_id "
	        + " LEFT JOIN event_function ef ON mp.event_function_id = ef.event_function_id "
	        + " LEFT JOIN functions fun ON ef.function_master_id = fun.function_id "
	        + " WHERE ef.event_id =:eventId  "
	        + " AND NOT EXISTS ( "
	        + "     SELECT 1 "
	        + "     FROM eventfunction_rawmaterial_permission p "
	        + "     WHERE p.raw_material_id = rm.raw_material_id "
	        + "       AND p.event_id = :eventId "
	        + "       AND p.eventfunction_id IN (:eventFunctionIds) "
	        + "       AND p.type = 'NotPermissable' "
	        + "          ) ",
	        nativeQuery = true)
	List<Object[]> getEventRawMaterialIfDoesNotExistByRawMaterialId(
	        @Param("eventId") Long eventId,
	        @Param("rawMateriaId") Long rawMateriaId,
	        @Param("eventFunctionIds") List<Long> eventFunctionIds);

//	@Query(value = " SELECT " + "  	ef.event_id AS eventId, " + "  	IFNULL(rms.party_id,0) AS supplierId, "
//			+ "  	IFNULL(pt.name_english,'') AS supplierName, " + "  	rm.raw_material_id AS rawMaterialId, "
//			+ "  	rm.name_english AS rawMaterialNameEng, " + "  	rm.name_gujarati AS rawMaterialNameGuj, "
//			+ "  	rm.name_hindi AS rawMaterialNameHin, " + "  	u.unit_id AS unit_id, "
//			+ "  	u.name_english AS unit_name, "
//			+ "  	((((crr.qty * micr.weight) / crm.weight) * ef.pax) / 100) AS qty, "
//			+ "  	((((crr.qty * micr.weight) / crm.weight) * ef.pax) / 100) AS finalQty, " + "  	'' AS place, "
//			+ "  	ef.function_master_id AS functionId, " + "  	ef.event_function_id AS eventFunctionId, "
//			+ "  	fun.name_english AS functionName, "
//			+ "  	CONCAT(mpd.menuitem_name, ' (', crm.name ,')') AS itemName, " + "  	ef.function_start_date_time, "
//			+ "  	((((((crr.qty * micr.weight) / crm.weight) * ef.pax) / 100) * crr.rate) / crr.qty) AS totalPrice, "
//			+ "  	crr.rate AS rate, " + "  	mpd.menu_item_id, " + "  	rmc.raw_matrial_cat_id, "
//			+ "  	rm.is_apply_cal " + "  FROM menupreparation mp " + "  INNER JOIN menupreparationdetails mpd "
//			+ "  	ON mp.menu_preparation_id = mpd.menu_preparation_id " + "  INNER JOIN memuitems mi "
//			+ "  	ON mi.menu_item_id = mpd.menu_item_id " + "  INNER JOIN menu_item_captain_receipe micp "
//			+ "  	ON micp.menu_item_id = mi.menu_item_id " + "  INNER JOIN captain_receipe_master crm "
//			+ "  	ON crm.id = micp.captain_receipe_id " + "  INNER JOIN captain_receipe_raw_material crr "
//			+ "  	ON crm.id = crr.captain_receipe_id " + "  INNER JOIN rawmaterial rm "
//			+ "  	ON rm.raw_material_id = crr.raw_item_id " + "  	AND crr.raw_item_id = :rawMateriaId "
//			+ "  LEFT JOIN raw_material_supplier rms  " + "  	ON rm.raw_material_id = rms.raw_material_id "
//			+ "  LEFT JOIN partymaster pt  " + "  	ON pt.party_id = rms.party_id " + "  INNER JOIN units u "
//			+ "  	ON u.unit_id = crr.unit_id " + "  INNER JOIN raw_material_category rmc  "
//			+ "  	ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id  " + "  LEFT JOIN event_function ef "
//			+ "  	ON mp.event_function_id = ef.event_function_id " + "  LEFT JOIN functions fun "
//			+ "  	ON ef.function_master_id = fun.function_id " + "  WHERE " + "  	ef.event_id = :eventId  "
//			+ "  	AND crr.is_delete = FALSE  ", nativeQuery = true)
//	List<Object[]> getEventCaptainReceipeRawMaterialIfDoesNotExistByRawMaterialId(@Param("eventId") Long eventId,
//			@Param("rawMateriaId") Long rawMateriaId);

	@Query(value = ""
	        + " SELECT "
	        + "   ef.event_id AS eventId, "
	        + "   IFNULL(rms.party_id,0) AS supplierId, "
	        + "   IFNULL(pt.name_english,'') AS supplierName, "
	        + "   rm.raw_material_id AS rawMaterialId, "
	        + "   rm.name_english AS rawMaterialNameEng, "
	        + "   rm.name_gujarati AS rawMaterialNameGuj, "
	        + "   rm.name_hindi AS rawMaterialNameHin, "
	        + "   t.unitId AS unit_id, "
	        + "   t.unitName AS unit_name, "

	        + "   (((t.convertedWeight * t.convertedQty) / t.recipeWeight) * ef.pax) / 100 AS qty, "
	        + "   (((t.convertedWeight * t.convertedQty) / t.recipeWeight) * ef.pax) / 100 AS finalQty, "

	        + "   '' AS place, "
	        + "   ef.function_master_id AS functionId, "
	        + "   ef.event_function_id AS eventFunctionId, "
	        + "   fun.name_english AS functionName, "

	        + "   CONCAT(mpd.menuitem_name,' (',crm.name,')') AS itemName, "
	        + "   ef.function_start_date_time, "

	        + "   (((((t.convertedWeight * t.convertedQty) / t.recipeWeight) * ef.pax) / 100) * t.rate) / t.convertedQty AS totalPrice, "

	        + "   t.rate AS rate, "
	        + "   mpd.menu_item_id, "
	        + "   rmc.raw_matrial_cat_id, "
	        + "   rm.is_apply_cal "

	        + " FROM ( "

	        + " SELECT "
	        + "   ef.event_id AS event_id, "                         
	        + "   mi.menu_item_id AS menuItemId, "                   
	        + "   rm.raw_material_id AS rawMaterialId, "             
	        + "   crm.id AS captainRecipeId, "                        
	        + "   crr.qty, "
	        + "   crr.rate, "
	        + "   crm.weight AS recipeWeight, "
	        + "   micr.weight AS menuWeight, "
	        + "   micr.unit_id AS menuUnitId, "
	        + "   crm.unit_id AS recipeUnitId, "
	        + "   crr.unit_id AS rawUnitId, "
	        + "   ru.unit_id AS unitId, "
	        + "   ru.english_name AS unitName, "
	        + " CASE "
	        + "   WHEN micr.unit_id = crm.unit_id "
	        + "       THEN IFNULL(micr.weight,0) "

	        + "   WHEN mu.is_parent_unit = 1 AND cu.is_parent_unit = 0 "
	        + "       THEN IFNULL(micr.weight,0) * 1000 "

	        + "   WHEN mu.is_parent_unit = 0 AND cu.is_parent_unit = 1 "
	        + "       THEN ROUND(IFNULL(micr.weight,0)/1000,4) "

	        + "   ELSE IFNULL(micr.weight,0) "
	        + " END AS convertedWeight, "

	        + " CASE "
	        + "   WHEN crr.unit_id = crm.unit_id "
	        + "       THEN IFNULL(crr.qty,0) "

	        + "   WHEN ru.is_parent_unit = 1 AND cu.is_parent_unit = 0 "
	        + "       THEN IFNULL(crr.qty,0) * 1000 "

	        + "   WHEN ru.is_parent_unit = 0 AND cu.is_parent_unit = 1 "
	        + "       THEN ROUND(IFNULL(crr.qty,0)/1000,4) "

	        + "   ELSE IFNULL(crr.qty,0) "
	        + " END AS convertedQty "

	        + " FROM menupreparation mp "

	        + " INNER JOIN menupreparationdetails mpd "
	        + " ON mp.menu_preparation_id = mpd.menu_preparation_id "

	        + " INNER JOIN memuitems mi "
	        + " ON mi.menu_item_id = mpd.menu_item_id "

	        + " INNER JOIN menu_item_captain_receipe micr "
	        + " ON micr.menu_item_id = mi.menu_item_id "
	        + " AND micr.is_delete = FALSE "

	        + " INNER JOIN captain_receipe_master crm "
	        + " ON crm.id = micr.captain_receipe_id "
	        + " AND crm.is_delete = FALSE "

	        + " INNER JOIN captain_receipe_raw_material crr "
	        + " ON crm.id = crr.captain_receipe_id "
	        + " AND crr.is_delete = FALSE "

	        + " INNER JOIN rawmaterial rm "
	        + " ON rm.raw_material_id = crr.raw_item_id "
	        + " AND crr.raw_item_id = :rawMateriaId "

	        + " INNER JOIN raw_material_category rmc "
	        + " ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "

	        + " LEFT JOIN event_function ef "
	        + " ON mp.event_function_id = ef.event_function_id "

	        + " LEFT JOIN units mu "
	        + " ON mu.unit_id = micr.unit_id "

	        + " LEFT JOIN units cu "
	        + " ON cu.unit_id = crm.unit_id "

	        + " LEFT JOIN units ru "
	        + " ON ru.unit_id = crr.unit_id "

	        + " WHERE ef.event_id = :eventId "
	        + " AND NOT EXISTS ( "
	        + "     SELECT 1 "
	        + "     FROM eventfunction_rawmaterial_permission p "
	        + "     WHERE p.raw_material_id = rm.raw_material_id "
	        + "       AND p.event_id = :eventId "
	        + "       AND p.eventfunction_id IN (:eventFunctionIds) "
	        + "       AND p.type = 'NotPermissable' "
	        + "          ) "

	        + " ) t "

	        + " INNER JOIN menupreparationdetails mpd "                
	        + " ON mpd.menu_item_id = t.menuItemId "

	        + " INNER JOIN rawmaterial rm "                            
	        + " ON rm.raw_material_id = t.rawMaterialId "

	        + " LEFT JOIN raw_material_supplier rms "
	        + " ON rms.raw_material_id = rm.raw_material_id "

	        + " LEFT JOIN partymaster pt "
	        + " ON pt.party_id = rms.party_id "

	        + " INNER JOIN captain_receipe_master crm "                
	        + " ON crm.id = t.captainRecipeId "

	        + " INNER JOIN units u "
	        + " ON u.unit_id = crm.unit_id "

	        + " LEFT JOIN event_function ef "
	        + " ON ef.event_id = t.event_id "

	        + " LEFT JOIN functions fun "
	        + " ON fun.function_id = ef.function_master_id "

	        + " INNER JOIN raw_material_category rmc "                 
	        + " ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "

	        , nativeQuery = true)
	List<Object[]> getEventCaptainReceipeRawMaterialIfDoesNotExistByRawMaterialId(
	        @Param("eventId") Long eventId,
	        @Param("rawMateriaId") Long rawMateriaId,
	        @Param("eventFunctionIds") List<Long> eventFunctionIds);  
	
	@Query(value = " SELECT " + " ef.event_id AS eventId, " + " IFNULL(p.party_id, 0) AS supplierId, "
			+ " IFNULL(p.name_english, '') AS supplierName, " + " rm.raw_material_id AS rawMaterialId, "
			+ " rm.name_english AS rawMaterialNameEng, " + " rm.name_gujarati AS rawMaterialNameGuj, "
			+ " rm.name_hindi AS rawMaterialNameHin, " + " u.unit_id AS unitId, " + " u.name_english AS unitName, "
			+ " mairm.weight AS qty, " + " mairm.weight AS finalQty, " + " IFNULL(mairm.place, '') AS place, "
			+ " ef.function_master_id AS functionId, " + " ef.event_function_id AS eventFunctionId, "
			+ " f.name_english AS functionName, " + " mi.name_english AS itemName, " + " ef.function_start_date_time, "
			+ " mairm.rate AS totalPrice, "
			+ " mairm.rawmaterial_rate, mi.menu_item_id, rmc.raw_matrial_cat_id, rm.is_apply_cal "
			+ " FROM menuallocation_item_rawmaterial mairm " + " INNER JOIN rawmaterial rm "
			+ "     ON mairm.raw_material_id = rm.raw_material_id AND mairm.raw_material_id = :rawMaterialId "
			+ " INNER JOIN raw_material_category rmc " + "     ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
			+ " LEFT JOIN partymaster p " + "     ON mairm.party_id = p.party_id " + " LEFT JOIN units u "
			+ "     ON mairm.unit_id = u.unit_id " + " INNER JOIN memuitems mi "
			+ "     ON mairm.menu_item_id = mi.menu_item_id " + " INNER JOIN event_function ef "
			+ "     ON mairm.eventfunction_id = ef.event_function_id " + " INNER JOIN functions f "
			+ "     ON ef.function_master_id = f.function_id " + " INNER JOIN eventfunction_menuallocation ema "
			+ "    ON ema.eventfunction_id = ef.event_function_id " + "    AND ema.menu_item_id = mi.menu_item_id"
			+ " WHERE mairm.event_id = :eventId "
			+ "   AND mairm.is_delete = false AND ema.outside = false ", nativeQuery = true)
	List<Object[]> getMenuAllocationRawMaterialDataByRawMaterialId(@Param("eventId") Long eventId,
			@Param("rawMaterialId") Long rawMaterialId);

	@Query(value = " SELECT " + " ef.event_id AS eventId, " + " IFNULL(p.party_id, 0) AS supplierId, "
			+ " IFNULL(p.name_english, '') AS supplierName, " + " rm.raw_material_id AS rawMaterialId, "
			+ " rm.name_english AS rawMaterialNameEng, " + " rm.name_gujarati AS rawMaterialNameGuj, "
			+ " rm.name_hindi AS rawMaterialNameHin, " + " u.unit_id AS unitId, " + " u.name_english AS unitName, "
			+ " mairm.weight AS qty, " + " mairm.weight AS finalQty, " + " IFNULL(mairm.place, '') AS place, "
			+ " ef.function_master_id AS functionId, " + " ef.event_function_id AS eventFunctionId, "
			+ " f.name_english AS functionName, " + " mi.name_english AS itemName, " + " ef.function_start_date_time, "
			+ " mairm.rate AS totalPrice, "
			+ " mairm.rawmaterial_rate, mi.menu_item_id, rmc.raw_matrial_cat_id, rm.is_apply_cal "
			+ " FROM menuallocation_item_rawmaterial mairm " + " INNER JOIN rawmaterial rm "
			+ "     ON mairm.raw_material_id = rm.raw_material_id " + " INNER JOIN raw_material_category rmc "
			+ "     ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id " + " LEFT JOIN partymaster p "
			+ "     ON mairm.party_id = p.party_id " + " LEFT JOIN units u " + "     ON mairm.unit_id = u.unit_id "
			+ " INNER JOIN memuitems mi " + "     ON mairm.menu_item_id = mi.menu_item_id "
			+ " INNER JOIN event_function ef " + "     ON mairm.eventfunction_id = ef.event_function_id "
			+ " INNER JOIN functions f " + "     ON ef.function_master_id = f.function_id "
			+ " INNER JOIN eventfunction_menuallocation ema " + "    ON ema.eventfunction_id = ef.event_function_id "
			+ "    AND ema.menu_item_id = mi.menu_item_id " + " WHERE mairm.event_id = :eventId "
			+ "   AND mairm.eventfunction_id = :eventFunctionId AND ema.outside = false "
			+ "   AND mairm.menu_item_id IN (:menuItemIds) " + "   AND mairm.is_delete = false ", nativeQuery = true)
	List<Object[]> getMenuAllocationRawMaterialDataByMenuItemIds(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId, @Param("menuItemIds") List<Long> menuItemIds);

	@Query(value = " SELECT DISTINCT "
	        + " ef.event_id AS eventId, "
	        + " IFNULL(p.party_id,0) AS supplierId, "
	        + " IFNULL(p.name_english,'') AS supplierName, "
	        + " rm.raw_material_id AS rawMaterialId, "
	        + " rm.name_english AS rawMaterialNameEng, "
	        + " rm.name_gujarati AS rawMaterialNameGuj, "
	        + " rm.name_hindi AS rawMaterialNameHin, "
	        + " ru.unit_id AS unitId, "
	        + " ru.name_english AS unitName, "

	        + " ( "
	        + " (CASE "
	        + "     WHEN maicr.unit_id = crm.unit_id THEN IFNULL(maicr.weight,0) "
	        + "     WHEN mu.is_parent_unit = 1 AND cu.is_parent_unit = 0 THEN IFNULL(maicr.weight,0) * 1000 "
	        + "     WHEN mu.is_parent_unit = 0 AND cu.is_parent_unit = 1 THEN ROUND(IFNULL(maicr.weight,0)/1000,4) "
	        + "     ELSE IFNULL(maicr.weight,0) "
	        + " END) "
	        + " * "
	        + " (CASE "
	        + "     WHEN crrm.unit_id = crm.unit_id THEN IFNULL(crrm.qty,0) "
	        + "     WHEN ru.is_parent_unit = 1 AND cu.is_parent_unit = 0 THEN IFNULL(crrm.qty,0) * 1000 "
	        + "     WHEN ru.is_parent_unit = 0 AND cu.is_parent_unit = 1 THEN ROUND(IFNULL(crrm.qty,0)/1000,4) "
	        + "     ELSE IFNULL(crrm.qty,0) "
	        + " END) "
	        + " ) / crm.weight AS qty, "

	        + " ( "
	        + " (CASE "
	        + "     WHEN maicr.unit_id = crm.unit_id THEN IFNULL(maicr.weight,0) "
	        + "     WHEN mu.is_parent_unit = 1 AND cu.is_parent_unit = 0 THEN IFNULL(maicr.weight,0) * 1000 "
	        + "     WHEN mu.is_parent_unit = 0 AND cu.is_parent_unit = 1 THEN ROUND(IFNULL(maicr.weight,0)/1000,4) "
	        + "     ELSE IFNULL(maicr.weight,0) "
	        + " END) "
	        + " * "
	        + " (CASE "
	        + "     WHEN crrm.unit_id = crm.unit_id THEN IFNULL(crrm.qty,0) "
	        + "     WHEN ru.is_parent_unit = 1 AND cu.is_parent_unit = 0 THEN IFNULL(crrm.qty,0) * 1000 "
	        + "     WHEN ru.is_parent_unit = 0 AND cu.is_parent_unit = 1 THEN ROUND(IFNULL(crrm.qty,0)/1000,4) "
	        + "     ELSE IFNULL(crrm.qty,0) "
	        + " END) "
	        + " ) / crm.weight AS finalQty, "

	        + " '' AS place, "
	        + " ef.function_master_id AS functionId, "
	        + " ef.event_function_id AS eventFunctionId, "
	        + " f.name_english AS functionName, "
	        + " CONCAT(mi.name_english,' (',crm.name,')') AS itemName, "
	        + " ef.function_start_date_time, "

	        + " ( "
	        + " (CASE "
	        + "     WHEN maicr.unit_id = crm.unit_id THEN IFNULL(maicr.weight,0) "
	        + "     WHEN mu.is_parent_unit = 1 AND cu.is_parent_unit = 0 THEN IFNULL(maicr.weight,0) * 1000 "
	        + "     WHEN mu.is_parent_unit = 0 AND cu.is_parent_unit = 1 THEN ROUND(IFNULL(maicr.weight,0)/1000,4) "
	        + "     ELSE IFNULL(maicr.weight,0) "
	        + " END) "
	        + " * crrm.rate "
	        + " ) / crm.weight AS totalPrice, "

	        + " crrm.rate, "
	        + " mi.menu_item_id, "
	        + " rmc.raw_matrial_cat_id, "
	        + " rm.is_apply_cal "

	        + " FROM menu_allocation_item_captain_receipe maicr "

	        + " INNER JOIN memuitems mi "
	        + " ON mi.menu_item_id = maicr.menu_item_id "

	        + " INNER JOIN captain_receipe_master crm "
	        + " ON crm.id = maicr.captain_receipe_id "

	        + " INNER JOIN captain_receipe_raw_material crrm "
	        + " ON crrm.captain_receipe_id = crm.id "
	        + " AND crrm.is_delete = FALSE "

	        + " INNER JOIN rawmaterial rm "
	        + " ON rm.raw_material_id = crrm.raw_item_id "

	        + " INNER JOIN raw_material_category rmc "
	        + " ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "

	        + " LEFT JOIN partymaster p "
	        + " ON maicr.party_id = p.party_id "

	        + " LEFT JOIN units mu "
	        + " ON mu.unit_id = maicr.unit_id "

	        + " LEFT JOIN units cu "
	        + " ON cu.unit_id = crm.unit_id "

	        + " LEFT JOIN units ru "
	        + " ON ru.unit_id = crrm.unit_id "

	        + " INNER JOIN units u "
	        + " ON u.unit_id = crm.unit_id "

	        + " INNER JOIN event_function ef "
	        + " ON maicr.eventfunction_id = ef.event_function_id "

	        + " INNER JOIN functions f "
	        + " ON ef.function_master_id = f.function_id "

	        + " INNER JOIN eventfunction_menuallocation ema "
	        + " ON ema.eventfunction_id = maicr.eventfunction_id "
	        + " AND ema.menu_item_id = maicr.menu_item_id "
	        + " AND ema.outside = FALSE "

	        + " WHERE maicr.event_id = :eventId "
	        + " AND maicr.eventfunction_id = :eventFunctionId "
	        + " AND maicr.menu_item_id IN (:menuItemIds) "
	        + " AND maicr.is_delete = FALSE "
	        + " AND crm.is_delete = FALSE "
	        + " AND NOT EXISTS ( "
	        + "     SELECT 1 "
	        + "     FROM eventfunction_rawmaterial_permission p "
	        + "     WHERE p.raw_material_id = rm.raw_material_id "
	        + "       AND p.event_id = :eventId "
	        + "       AND p.eventfunction_id = :eventFunctionId "
	        + "       AND p.type = 'NotPermissable' "
	        + " ) ",
	        nativeQuery = true)
	List<Object[]> getMenuAllocationCaptainReceipeRawMaterialDataByMenuItemIds(
	        @Param("eventId") Long eventId,
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("menuItemIds") List<Long> menuItemIds);

	@Query("SELECT new com.crmportal.response.dto.EventRawMaterialResponse(i.id, i.event.id, COALESCE(s.id,0),COALESCE(s.nameEnglish,''),i.rawMaterial.id, "
			+ " i.rawMaterial.nameEnglish, i.rawMaterial.nameGujarati, i.rawMaterial.nameHindi, i.unit.id,"
			+ "i.qty, i. finalqty, i.place, i.totalprice, i.rawMaterialCat.id, i.rawMaterial.isApplyCal, i.delieveryDateTime, i.remarksEnglish, i.remarksHindi, i.remarksGujarati)   from #{#entityName} i LEFT JOIN i.supplier s where i.event.id=:eventId and i.rawMaterial.id =:rawMateriaId "
			+ "  ORDER BY i.rawMaterial.sequence ASC ")
	List<EventRawMaterialResponse> getEventRawMaterialDetailsByRawMaterialId(Long eventId, Long rawMateriaId);

	@Query("SELECT new com.crmportal.response.dto.EventRawMaterialResponse(i.id, i.event.id, COALESCE(s.id,0),COALESCE(s.nameEnglish,''),"
			+ " i.rawMaterial.id, i.rawMaterial.nameEnglish, i.rawMaterial.nameGujarati, i.rawMaterial.nameHindi, i.unit.id,"
			+ " i.qty, i. finalqty, i.place, i.totalprice, i.rawMaterialCat.id, i.rawMaterial.isApplyCal,"
			+ " i.delieveryDateTime, i.remarksEnglish, i.remarksHindi, i.remarksGujarati) " + " from #{#entityName} i "
			+ " LEFT JOIN i.supplier s " + " where i.event.id=:eventId "
			+ " and i.rawMaterialCat.id =:rawMateriaCatlId " + " ORDER BY i.rawMaterial.sequence ASC ")
	List<EventRawMaterialResponse> getEventRawMaterialDetails(Long eventId, Long rawMateriaCatlId);

//	@Modifying
//	@Transactional
//	@Query("DELETE FROM EventRawMaterialEntity erf WHERE erf.event.id = :eventId AND erf.rawMaterialCat.id = :rawMaterialCatId")
//	void deleteByEventIdAndRawMaterialCatId(Long eventId, Long rawMaterialCatId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM event_raw_material " + "WHERE event_id = :eventId "
			+ "AND raw_material_cat_id = :rawMaterialCatId", nativeQuery = true)
	void deleteByEventIdAndRawMaterialCatId(@Param("eventId") Long eventId,
			@Param("rawMaterialCatId") Long rawMaterialCatId);

	@Query("SELECT erm FROM EventRawMaterialEntity erm  " + " JOIN FETCH erm.rawMaterial rm  "
			+ " JOIN FETCH rm.rawMaterialCat cat  " + " LEFT JOIN FETCH erm.supplier s  "
			+ " LEFT JOIN FETCH erm.unit u " + " WHERE erm.event.id = :eventId " + " AND erm.isDelete = false "
			+ " AND cat.id IN (:rawMaterialCatIds) "
			+ " ORDER BY COALESCE(cat.sequence, 999999), COALESCE(rm.sequence, 999999) ")
	List<EventRawMaterialEntity> findAllByEventId(@Param("eventId") Long eventId,
			@Param("rawMaterialCatIds") List<Long> rawMaterialCatIds);

	@Query("SELECT erm FROM EventRawMaterialEntity erm  " + " JOIN FETCH erm.rawMaterial rm  "
			+ " JOIN FETCH rm.rawMaterialCat cat " + " JOIN FETCH cat.rawMaterialCatType cattype "
			+ " LEFT JOIN FETCH erm.supplier s  " + " LEFT JOIN FETCH erm.unit u "
			+ " WHERE erm.event.id = :eventId AND cat.rawMaterialCatType.id = 2 " + " AND erm.isDelete = false")
	List<EventRawMaterialEntity> findAllCrockeryByEventId(@Param("eventId") Long eventId);

	@Query("SELECT new com.crmportal.response.dto.EventRawMaterialPartyDTO(p.id, p.nameEnglish, cc.nameEnglish, p.mobileno) FROM EventRawMaterialFunctions ermf JOIN ermf.supplier p LEFT JOIN p.contact cc WHERE ermf.event.id = :eventId AND ermf.eventFunction.id = :eventFunctionId GROUP BY p.id, p.nameEnglish, cc.nameEnglish, p.mobileno")
	List<EventRawMaterialPartyDTO> findGroupedPartyByEventAndFunction(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query("SELECT new com.crmportal.response.dto.EventRawMaterialPartyDTO(p.id, p.nameEnglish, cc.nameEnglish, p.mobileno) FROM EventRawMaterialEntity ermf JOIN ermf.supplier p LEFT JOIN p.contact cc WHERE ermf.event.id = :eventId GROUP BY p.id, p.nameEnglish, cc.nameEnglish, p.mobileno")
	List<EventRawMaterialPartyDTO> findGroupedPartyByEvent(@Param("eventId") Long eventId);

	boolean existsByEvent_IdAndRawMaterialCat_IdAndIsDeleteFalse(Long eventId, Long rawMaterialCatId);

//	@Query(value = "SELECT " + " pm.party_id AS partyId, " + " pm.name_english AS partyName, "
//			+ " mi.menu_item_id AS menuItemId, " + " mi.name_english AS itemNameEnglish, "
//			+ " mi.name_hindi AS itemNameHindi, " + " mi.name_gujarati AS itemNameGujarati, "
//			+ " mc.menu_category_id AS menuCategoryId, " + " mc.name_english AS menuCatNameEnglish, "
//			+ " mc.name_hindi AS menuCatNameHindi, " + " mc.name_gujarati AS menuCatNameGujarati, "
//			+ " rmc.raw_matrial_cat_id AS rawMaterialCategoryId, " + " rmc.name_english AS rawCatNameEnglish, "
//			+ " rmc.name_hindi AS rawCatNameHindi, " + " rmc.name_gujarati AS rawCatNameGujarati, "
//			+ " rm.raw_material_id AS rawMaterialId, " + " rm.name_english AS rawItemNameEnglish, "
//			+ " rm.name_hindi AS rawItemNameHindi, " + " rm.name_gujarati AS rawItemNameGujarati, "
//			+ " mairm.rawmaterial_weight AS rawMaterialWeight, " + " mairm.rawmaterial_rate AS rawMaterialRate, "
//			+ " u.name_english AS unitName " + " FROM eventfunction_menuallocation_order efmao "
//			+ " JOIN eventfunction_menuallocation efma ON efmao.menu_allocation_id = efma.menu_allocation_id "
//			+ " JOIN memuitems mi ON efma.menu_item_id = mi.menu_item_id "
//			+ " JOIN menucategory mc ON efma.menu_category_id = mc.menu_category_id "
//			+ " LEFT JOIN menuallocation_item_rawmaterial mairm ON mairm.menu_item_id = mi.menu_item_id AND mairm.party_id = efmao.party_id AND mairm.event_id = efma.event_id "
//			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = mairm.raw_material_id "
//			+ " LEFT JOIN raw_material_category rmc ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
//			+ " LEFT JOIN units u ON u.unit_id = mairm.unit_id "
//			+ " JOIN partymaster pm ON pm.party_id = efmao.party_id " + " WHERE efma.event_id = :eventId "
//			+ "   AND efma.chef_labour = TRUE " + "   AND efma.is_delete = FALSE "
//			+ "   AND efmao.is_delete = FALSE " + " ORDER BY pm.party_id, mi.menu_item_id ", nativeQuery = true)
//	List<Object[]> findRawMaterialDetailsByEventAndParty(@Param("eventId") Long eventId);

	@Query(value = " SELECT DISTINCT " + "    efma.menu_allocation_id, " + "    pm.party_id,"
			+ "    CASE WHEN :lang = 1 THEN pm.name_hindi" + "    WHEN :lang = 2 THEN pm.name_gujarati "
			+ "    ELSE pm.name_english END AS party_name, " + "    mi.menu_item_id, "
			+ "    mi.name_english        AS item_name_english, " + "    mi.name_hindi          AS item_name_hindi, "
			+ "    mi.name_gujarati       AS item_name_gujarati, " + "    mc.menu_category_id, "
			+ "    mc.name_english        AS menu_cat_name_english, "
			+ "    mc.name_hindi          AS menu_cat_name_hindi, "
			+ "    mc.name_gujarati       AS menu_cat_name_gujarati, " + "    rmc.raw_matrial_cat_id, "
			+ "    rmc.name_english       AS raw_cat_english, " + "    rmc.name_hindi         AS raw_cat_hindi, "
			+ "    rmc.name_gujarati      AS raw_cat_gujarati, " + "    rm.raw_material_id, "
			+ "    rm.name_english        AS raw_item_name_english, "
			+ "    rm.name_hindi          AS raw_item_name_hindi, "
			+ "    rm.name_gujarati       AS raw_item_name_gujarati, " + "    mairm.weight, " + "    mairm.rate, "
			+ "    CASE WHEN :lang = 1 THEN COALESCE(NULLIF(u.name_hindi, ''), u.name_english) "
			+ "    WHEN :lang = 2 THEN COALESCE(NULLIF(u.name_gujarati, ''), u.name_english) "
			+ "    ELSE u.name_english END AS unit_name, " + "    efma.person_count      AS pax,"
			+ "    u.unit_id AS unit_id, rm.is_apply_cal " + "FROM eventfunction_menuallocation efma "
			+ "JOIN eventfunction_menuallocation_order efmao ON efmao.menu_allocation_id = efma.menu_allocation_id AND efmao.is_delete = FALSE "
			+ "JOIN partymaster pm ON pm.party_id = efmao.party_id "
			+ "JOIN memuitems mi ON mi.menu_item_id = efma.menu_item_id "
			+ "JOIN menucategory mc ON mc.menu_category_id = efma.menu_category_id "
			+ "JOIN menuallocation_item_rawmaterial mairm ON mairm.menu_item_id = mi.menu_item_id AND mairm.event_id = efma.event_id "
			+ "JOIN rawmaterial rm ON rm.raw_material_id = mairm.raw_material_id "
			+ "LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ "LEFT JOIN units u ON u.unit_id = mairm.unit_id " + "WHERE efma.event_id = :eventId "
			+ "  AND efma.chef_labour = TRUE " + "  AND efma.is_delete = FALSE "
			+ " AND (:isAgency = FALSE OR efmao.party_id IN (:agencyId)) "
			+ " AND (:isItem = FALSE OR efma.menu_item_id IN (:itemId)) "
			+ "ORDER BY  "
			+ "    pm.party_id,     " + "    mi.menu_item_id, " + "    rm.raw_material_id ", nativeQuery = true)
	List<Object[]> findRawMaterialDetailsByEventAndParty(
			@Param("eventId") Long eventId, 
			@Param("lang") Integer lang,
			@Param("agencyId") List<Long> agencyId,
			@Param("itemId") List<Long> itemId,
			@Param("isAgency") Boolean isAgencyWise,
			@Param("isItem") Boolean isItem);

	@Query(value = "SELECT " + "   efma.menu_allocation_id, " + "   pm.party_id, " + "   CASE "
			+ "       WHEN :lang = 1 THEN pm.name_hindi " + "       WHEN :lang = 2 THEN pm.name_gujarati "
			+ "       ELSE pm.name_english " + "   END AS party_name, " + "   mi.menu_item_id, "
			+ "   mi.name_english        AS item_name_english, " + "   mi.name_hindi          AS item_name_hindi, "
			+ "   mi.name_gujarati       AS item_name_gujarati, " + "   mc.menu_category_id, "
			+ "   mc.name_english        AS menu_cat_name_english, "
			+ "   mc.name_hindi          AS menu_cat_name_hindi, "
			+ "   mc.name_gujarati       AS menu_cat_name_gujarati, " + "   rc.raw_matrial_cat_id, "
			+ "   rc.name_english        AS raw_cat_english, " + "   rc.name_hindi          AS raw_cat_hindi, "
			+ "   rc.name_gujarati       AS raw_cat_gujarati, " + "   rm.raw_material_id, "
			+ "   rm.name_english        AS raw_item_name_english, "
			+ "   rm.name_hindi          AS raw_item_name_hindi, "
			+ "   rm.name_gujarati       AS raw_item_name_gujarati, " + "   mirm.weight, " + "	mirm.rate, "
			+ "   CASE " + "       WHEN :lang = 1 THEN COALESCE(NULLIF(un.name_hindi, ''), un.name_english) "
			+ "       WHEN :lang = 2 THEN COALESCE(NULLIF(un.name_gujarati, ''), un.name_english) "
			+ "       ELSE un.name_english " + "   END AS unit_name, " + "   efma.person_count      AS pax, "
			+ "   un.unit_id             AS unit_id, rm.is_apply_cal " + "FROM menupreparation mp "
			+ "LEFT JOIN menupreparationdetails mpd " + "       ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ "LEFT JOIN memuitems mi " + "       ON mi.menu_item_id = mpd.menu_item_id " + "LEFT JOIN menucategory mc "
			+ "       ON mpd.menu_category_id = mc.menu_category_id " + "LEFT JOIN menu_item_raw_material mirm "
			+ "       ON mirm.menu_item_id = mi.menu_item_id " + "LEFT JOIN rawmaterial rm "
			+ "       ON rm.raw_material_id = mirm.raw_material_id " + "LEFT JOIN raw_material_category rc "
			+ "       ON rc.raw_matrial_cat_id = rm.raw_material_cat_id " + "LEFT JOIN units un "
			+ "       ON un.unit_id = mirm.unit_id " + "LEFT JOIN event_function ef "
			+ "       ON ef.event_function_id = mp.event_function_id " + "LEFT JOIN events e "
			+ "       ON e.event_id = ef.event_id " + "LEFT JOIN partymaster pm "
			+ "       ON pm.party_id = e.party_id " + "LEFT JOIN eventfunction_menuallocation efma "
			+ "       ON ef.event_id = efma.event_id " + "LEFT JOIN eventfunction_menuallocation_order efmao "
			+ "       ON efmao.menu_allocation_id = efma.menu_allocation_id " + "WHERE e.event_id = :eventId "
			+ "  AND efma.is_delete = FALSE " + "  AND efma.chef_labour = TRUE "
			+ "ORDER BY pm.party_id, mi.menu_item_id, rm.raw_material_id ", nativeQuery = true)
	List<Object[]> findSynchrnonizedRawMaterialDetailsByEventAndParty(@Param("eventId") Long eventId,
			@Param("lang") Integer lang);

	Optional<EventRawMaterialEntity> findByEvent_IdAndRawMaterial_IdAndIsDeleteFalse(Long eventId, Long rawMaterialId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM event_raw_material " + "WHERE event_id = :eventId ", nativeQuery = true)
	void deleteByEventId(Long eventId);

	@Query(value = "SELECT ef.event_id, ef.event_function_id, mp.menu_preparation_id, mpd.menu_item_id, mirm.raw_material_id, rm.raw_material_cat_id, "
			+ " CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS function_name, "
			+ " CASE WHEN :lang = 0 THEN rmc.name_english WHEN :lang = 1 THEN rmc.name_hindi WHEN :lang = 2 THEN rmc.name_gujarati END AS raw_material_cat_name, "
			+ " CASE WHEN :lang = 0 THEN rm.name_english WHEN :lang = 1 THEN rm.name_hindi WHEN :lang = 2 THEN rm.name_gujarati END AS raw_material_name, "
			+ " mirm.weight, mirm.unit_id, "
			+ " CASE WHEN :lang = 0 THEN u.name_english WHEN :lang = 1 THEN u.name_hindi WHEN :lang = 2 THEN u.name_gujarati END AS unit_name "
			+ " FROM event_function ef "
			+ " LEFT JOIN menupreparation mp ON mp.event_function_id = ef.event_function_id "
			+ " LEFT JOIN functions f ON f.function_id = ef.function_master_id "
			+ " LEFT JOIN menupreparationdetails mpd ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ " LEFT JOIN menu_item_raw_material mirm ON mirm.menu_item_id = mpd.menu_item_id "
			+ " LEFT JOIN rawmaterial rm ON rm.raw_material_id = mirm.raw_material_id "
			+ " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ " LEFT JOIN units u ON u.unit_id = mirm.unit_id "
			+ " WHERE event_id = :eventId AND mirm.raw_material_id IS NOT NULL AND is_general_fix = TRUE "
			+ " AND ef.event_function_id = :eventFunctionId "
			+ " AND (:applyFilter = 0 OR rm.raw_material_cat_id IN (:rawMaterialCatIds)) "
			+ " ORDER BY ef.event_function_id, rm.raw_material_cat_id ", nativeQuery = true)
	List<Object[]> getGeneralFixItems(Long eventId, Long eventFunctionId, List<Long> rawMaterialCatIds, int lang,
			int applyFilter);

	@Query(value = "SELECT ef.event_function_id, "
			+ " CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS function_name, "
			+ " e.event_no, " + " ef.function_venue, " + " ef.pax " + " FROM event_function ef "
			+ " LEFT JOIN functions f ON f.function_id = ef.function_master_id "
			+ " LEFT JOIN events e ON e.event_id = ef.event_id "
			+ " WHERE ef.event_id = :eventId AND (:eventFunctionId = -1 OR ef.event_function_id = :eventFunctionId) "
			+ " ORDER BY sortorder ", nativeQuery = true)
	List<Object[]> getFunctions(Long eventId, Long eventFunctionId, int lang);

	@Query(value = "SELECT ef.event_function_id, "
			+ " CASE WHEN :lang = 0 THEN f.name_english WHEN :lang = 1 THEN f.name_hindi WHEN :lang = 2 THEN f.name_gujarati END AS function_name, "
			+ " e.event_no, " + " ef.function_venue, " + " ef.pax " + " FROM event_function ef "
			+ " LEFT JOIN functions f ON f.function_id = ef.function_master_id "
			+ " LEFT JOIN events e ON e.event_id = ef.event_id "
			+ " WHERE ef.event_id = :eventId AND (:flag = -1 OR ef.event_function_id IN (:eventFunctionId)) "
			+ " ORDER BY sortorder ", nativeQuery = true)
	List<Object[]> getFunctions(Long eventId, List<Long> eventFunctionId, int lang, int flag);

	@Query(value = " SELECT cat.raw_matrial_cat_id, "
			+ " CASE WHEN :lang = 0 THEN cat.name_english WHEN :lang = 1 THEN cat.name_hindi WHEN :lang = 2 THEN cat.name_gujarati END AS raw_material_cat_name "
			+ " FROM raw_material_category cat " + " WHERE cat.raw_material_cat_type_id = 2 "
			+ "   AND cat.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getCrockerCategories(int lang);

	@Query(value = " SELECT CASE WHEN :lang = 0 THEN cat.name_english WHEN :lang = 1 THEN cat.name_hindi WHEN :lang = 2 THEN cat.name_gujarati END AS raw_material_cat_name, "
			+ " CASE WHEN :lang = 0 THEN raw_material_name_english WHEN :lang = 1 THEN raw_material_name_hindi WHEN :lang = 2 THEN raw_material_name_gujarati END AS item_name "
			+ " FROM raw_material_category rmc "
			+ " INNER JOIN crockery_cutlery cc ON cc.raw_material_category_id = rmc.raw_matrial_cat_id "
			+ " WHERE raw_material_cat_type_id = 2 ", nativeQuery = true)
	List<Object[]> getAllCrockerCutleryData(int lang);

	@Query(value = "SELECT cc.raw_material_id, "
			+ " CASE WHEN :lang = 0 THEN raw_material_name_english WHEN :lang = 1 THEN raw_material_name_hindi WHEN :lang = 2 THEN raw_material_name_gujarati END AS item_name, "
			+ "    CASE " + "        WHEN :person BETWEEN 0 AND 100 THEN range_0_to_100 "
			+ "        WHEN :person BETWEEN 101 AND 200 THEN range_101_to_200 "
			+ "        WHEN :person BETWEEN 201 AND 300 THEN range_201_to_300 "
			+ "        WHEN :person BETWEEN 301 AND 400 THEN range_301_to_400 "
			+ "        WHEN :person BETWEEN 401 AND 500 THEN range_401_to_500 "
			+ "        WHEN :person BETWEEN 501 AND 600 THEN range_501_to_600 "
			+ "        WHEN :person BETWEEN 601 AND 700 THEN range_601_to_700 "
			+ "        WHEN :person BETWEEN 701 AND 800 THEN range_701_to_800 "
			+ "        WHEN :person BETWEEN 801 AND 900 THEN range_801_to_900 "
			+ "        WHEN :person BETWEEN 901 AND 1000 THEN range_901_to_1000 "
			+ "        WHEN :person BETWEEN 1001 AND 1100 THEN range_1001_to_1100 "
			+ "        WHEN :person BETWEEN 1101 AND 1200 THEN range_1101_to_1200 "
			+ "        WHEN :person BETWEEN 1201 AND 1300 THEN range_1201_to_1300 "
			+ "        WHEN :person BETWEEN 1301 AND 1400 THEN range_1301_to_1400 "
			+ "        WHEN :person BETWEEN 1401 AND 1500 THEN range_1401_to_1500 "
			+ "        WHEN :person BETWEEN 1501 AND 1600 THEN range_1501_to_1600 "
			+ "        WHEN :person BETWEEN 1601 AND 1700 THEN range_1601_to_1700 "
			+ "        WHEN :person BETWEEN 1701 AND 1800 THEN range_1701_to_1800 "
			+ "        WHEN :person BETWEEN 1801 AND 1900 THEN range_1801_to_1900 "
			+ "        WHEN :person BETWEEN 1901 AND 2000 THEN range_1901_to_2000 " + "        ELSE 0 "
			+ "    END AS quantity " + " FROM raw_material_category rmc "
			+ " INNER JOIN crockery_cutlery cc ON cc.raw_material_category_id = rmc.raw_matrial_cat_id "
			+ " WHERE raw_material_cat_type_id = 2 AND cc.raw_material_category_id = :rawMaterialCatId ", nativeQuery = true)
	List<Object[]> getCrockerItems(Long rawMaterialCatId, int lang, Integer person);

	@Query(value = "SELECT CASE WHEN :lang = 0 THEN rmc.name_english WHEN :lang = 1 THEN rmc.name_hindi WHEN :lang = 2 THEN rmc.name_gujarati END AS raw_material_cat_name, "
			+ " CASE WHEN :lang = 0 THEN raw_material_name_english WHEN :lang = 1 THEN raw_material_name_hindi WHEN :lang = 2 THEN raw_material_name_gujarati END AS item_name, "
			+ "    CASE " + "        WHEN :person BETWEEN 0 AND 100 THEN range_0_to_100 "
			+ "        WHEN :person BETWEEN 101 AND 200 THEN range_101_to_200 "
			+ "        WHEN :person BETWEEN 201 AND 300 THEN range_201_to_300 "
			+ "        WHEN :person BETWEEN 301 AND 400 THEN range_301_to_400 "
			+ "        WHEN :person BETWEEN 401 AND 500 THEN range_401_to_500 "
			+ "        WHEN :person BETWEEN 501 AND 600 THEN range_501_to_600 "
			+ "        WHEN :person BETWEEN 601 AND 700 THEN range_601_to_700 "
			+ "        WHEN :person BETWEEN 701 AND 800 THEN range_701_to_800 "
			+ "        WHEN :person BETWEEN 801 AND 900 THEN range_801_to_900 "
			+ "        WHEN :person BETWEEN 901 AND 1000 THEN range_901_to_1000 "
			+ "        WHEN :person BETWEEN 1001 AND 1100 THEN range_1001_to_1100 "
			+ "        WHEN :person BETWEEN 1101 AND 1200 THEN range_1101_to_1200 "
			+ "        WHEN :person BETWEEN 1201 AND 1300 THEN range_1201_to_1300 "
			+ "        WHEN :person BETWEEN 1301 AND 1400 THEN range_1301_to_1400 "
			+ "        WHEN :person BETWEEN 1401 AND 1500 THEN range_1401_to_1500 "
			+ "        WHEN :person BETWEEN 1501 AND 1600 THEN range_1501_to_1600 "
			+ "        WHEN :person BETWEEN 1601 AND 1700 THEN range_1601_to_1700 "
			+ "        WHEN :person BETWEEN 1701 AND 1800 THEN range_1701_to_1800 "
			+ "        WHEN :person BETWEEN 1801 AND 1900 THEN range_1801_to_1900 "
			+ "        WHEN :person BETWEEN 1901 AND 2000 THEN range_1901_to_2000 " + "        ELSE 0 "
			+ "    END AS quantity " + " FROM raw_material_category rmc "
			+ " INNER JOIN crockery_cutlery cc ON cc.raw_material_category_id = rmc.raw_matrial_cat_id "
			+ " WHERE raw_material_cat_type_id = 2 AND rmc.user_id = :userId AND cc.is_delete = false ", nativeQuery = true)
	List<Object[]> getAllCrockerCutleryData(int lang, Integer person, Long userId);

	List<EventRawMaterialEntity> findByEvent_Id(Long eventId);

	List<EventRawMaterialEntity> findByIdIn(Set<Long> ids);

	@Query(value = "SELECT erm.party_id, " + "   CASE "
			+ "       WHEN :lang = 1 THEN CONCAT(pm.name_hindi, ' (' , pm.mobileno, ')') "
			+ "       WHEN :lang = 2 THEN CONCAT(pm.name_gujarati, ' (' , pm.mobileno, ')') "
			+ "       ELSE CONCAT(pm.name_english, ' (' , pm.mobileno, ')') " + "   END AS party_name, "
			+ " erm.raw_material_cat_id, " + "   CASE " + "       WHEN :lang = 1 THEN rmc.name_hindi "
			+ "       WHEN :lang = 2 THEN rmc.name_gujarati " + "       ELSE rmc.name_english "
			+ "   END AS raw_material_cat_name, " + " erm.raw_material_id,  " + "   CASE "
			+ "       WHEN :lang = 1 THEN rm.name_hindi " + "       WHEN :lang = 2 THEN rm.name_gujarati "
			+ "       ELSE rm.name_english " + "   END AS raw_material_name, " + " finalqty, " + "   CASE "
			+ "       WHEN :lang = 1 THEN u.name_hindi " + "       WHEN :lang = 2 THEN u.name_gujarati "
			+ "       ELSE u.name_english " + "   END AS unit, " + " CASE "
			+ "		WHEN (UPPER(erm.place) = 'AT VENUE' OR UPPER(erm.place) = 'VENUE') " + " THEN " + "		CASE"
			+ "			WHEN :lang = 1 THEN vm.name_hindi" + "			WHEN :lang = 2 THEN vm.name_gujarati"
			+ "     ELSE " + "			vm.name_english" + "		END" + " ELSE " + "		erm.place "
			+ " END AS place, " + " erm.delievery_date_time " + " FROM `event_raw_material` erm "
			+ " LEFT JOIN `raw_material_category` rmc ON rmc.raw_matrial_cat_id = erm.raw_material_cat_id "
			+ " LEFT JOIN `rawmaterial` rm ON rm.raw_material_id = erm.raw_material_id "
			+ " LEFT JOIN `partymaster` pm ON pm.party_id = erm.party_id "
			+ " LEFT JOIN units u ON u.unit_id = erm.unit_id " + " LEFT JOIN `events` e ON e.event_id = erm.event_id "
			+ " LEFT JOIN venuemaster vm ON e.venue_id = vm.venue_id " + " WHERE erm.event_id = :eventId "
			+ "   AND erm.is_delete = FALSE " + "   AND erm.party_id IN (:agencyId) "
			+ " ORDER BY rmc.sequence, erm.party_id ", nativeQuery = true)
	List<Object[]> getSupplierwiseRawMaterial(Long eventId, int lang, List<Long> agencyId);

	List<EventRawMaterialEntity> findAllByEventAndIsDeleteFalse(EventMasterEntity eventEntity);

	@Query(value = "SELECT COUNT(DISTINCT e.event_id) " + "FROM event_raw_material rm "
			+ "JOIN events e ON rm.event_id = e.event_id " + "WHERE rm.is_delete = false " + "AND e.is_delete = false "
			+ "AND (:userId = -1 OR e.user_id = :userId)", nativeQuery = true)
	Integer getRawMatCount(Long userId);

	void deleteAllByEvent(EventMasterEntity eventEntity);

	@Query(value = "SELECT rm.raw_material_id, rm.raw_material_cat_id, "
			+ " CASE WHEN :lang = 0 THEN rmc.name_english WHEN :lang = 1 THEN rmc.name_hindi WHEN :lang = 2 THEN rmc.name_gujarati END AS raw_material_cat_name, "
			+ " CASE WHEN :lang = 0 THEN rm.name_english WHEN :lang = 1 THEN rm.name_hindi WHEN :lang = 2 THEN rm.name_gujarati END AS raw_material_name, "
			+ " rm.weight_per_100_pax, rm.unit_id, "
			+ " CASE WHEN :lang = 0 THEN u.name_english WHEN :lang = 1 THEN u.name_hindi WHEN :lang = 2 THEN u.name_gujarati END AS unit_name "
			+ " FROM rawmaterial rm "
			+ " JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
			+ " JOIN units u ON rm.unit_id = u.unit_id " + " WHERE rm.is_general_fix = TRUE "
			+ " AND (:applyFilter = 0 OR rm.raw_material_cat_id IN (:rawMaterialCatIds)) AND rm.user_id = :userId AND rm.is_delete = FALSE AND rmc.is_delete = FALSE AND u.is_delete = FALSE"
			+ " ORDER BY rm.raw_material_cat_id,rm.raw_material_id ", nativeQuery = true)
	List<Object[]> getGeneralFixRawItems(int applyFilter, int lang, List<Long> rawMaterialCatIds, Long userId);

	@Query("SELECT DISTINCT p.eventFunctionId " +
		       "FROM EventFunctionRawmaterialPermissionEntity p " +
		       "WHERE p.eventId = :eventId")
		List<Long> findDistinctEventFunctionIdsByEventId(@Param("eventId") Long eventId);
	
	@Query(value = ""
	        + "SELECT "
	        + "    e.event_id, "
	        + "    et.name_english AS event_name, "
	        + "    p.total_pax, "
	        + "    rmc.raw_matrial_cat_id, "
	        + "    rmc.name_english AS raw_material_cat_name, "
	        + "    rm.raw_material_id, "
	        + "    rm.name_english AS raw_material, "
	        + "    rm.weight_per_100_pax, "
	        + "    rm.unit_id, "
	        + "    u.name_english AS unit_name, "
	        + "    ROUND((p.total_pax * rm.weight_per_100_pax) / 100, 3) AS required_qty "
	        + "FROM events e "
	        + "LEFT JOIN eventtype et "
	        + "    ON e.event_type_id = et.event_type_id "
	        + "INNER JOIN ( "
	        + "    SELECT event_id, SUM(pax) AS total_pax "
	        + "    FROM event_function "
	        + "    WHERE is_delete = FALSE "
	        + "    GROUP BY event_id "
	        + ") p ON p.event_id = e.event_id "
	        + "INNER JOIN rawmaterial rm "
	        + "    ON rm.user_id = e.user_id "
	        + "   AND rm.is_general_fix = TRUE "
	        + "   AND rm.is_delete = FALSE "
	        + "LEFT JOIN raw_material_category rmc "
	        + "    ON rmc.raw_matrial_cat_id = rm.raw_material_cat_id "
	        + "LEFT JOIN units u "
	        + "    ON u.unit_id = rm.unit_id "
	        + "WHERE e.event_id = :eventId "
	        + "ORDER BY rm.name_english",
	        nativeQuery = true)
	List<Object[]> getEventWiseGeneralFix(@Param("eventId") Long eventId);

	@Query(value = " "
 	 	 	+ "	SELECT "
 	 	 	+ "		CASE WHEN :lang = 0 THEN rmc.name_english WHEN :lang = 1 THEN rmc.name_hindi WHEN :lang = 2 THEN rmc.name_gujarati END AS name "
 	 	 	+ "	FROM raw_material_category rmc "
 	 	 	+ "	WHERE rmc.user_id = :userId "
 	 	 	+ "	AND rmc.is_delete = FALSE "
 	 	 	+ " AND rmc.sequence < 28 "
			+ " ORDER BY rmc.sequence ASC ", nativeQuery = true)
	List<String> getAgencyBookingData(@Param("userId") Long userId, @Param("lang") Integer lang);
	
}
