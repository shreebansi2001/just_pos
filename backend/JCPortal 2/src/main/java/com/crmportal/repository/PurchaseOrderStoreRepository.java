package com.crmportal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseOrderStoreEntity;
import com.crmportal.response.dto.PocodeWithStatusResponseDto;

@Repository
public interface PurchaseOrderStoreRepository extends JpaRepository<PurchaseOrderStoreEntity, Long> {
    
	@Query("SELECT COUNT(p) FROM PurchaseOrderStoreEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.createdAt >= :start " +
		       "AND p.createdAt < :end")
		long countByUserAndCreatedAtBetween(
		        @Param("userId") Long userId,
		        @Param("start") LocalDateTime start,
		        @Param("end") LocalDateTime end);
    
    List<PurchaseOrderStoreEntity> findByUserIdAndIsDeleteFalse(Long userId);
    
    @Query("SELECT p FROM PurchaseOrderStoreEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.isDelete = false " +
		       "ORDER BY p.createdAt DESC")
		List<PurchaseOrderStoreEntity> getByUserDesc(@Param("userId") Long userId);
    
    // Fix 1: UserId in method name instead of extra Long parameter
    Optional<PurchaseOrderStoreEntity> findByPocodeAndUserIdAndIsDeleteFalse(String pocode, Long userId);
    
    // Fix 2: Add @Param and use p.user.id in query
    @Query("SELECT new com.crmportal.response.dto.PocodeWithStatusResponseDto(" +
           "p.id, p.pocode, false) " +
           "FROM PurchaseOrderStoreEntity p " +
           "WHERE p.isDelete = false AND p.user.id = :userId AND p.status != 'PENDING' " +
           "ORDER BY p.createdAt DESC")
    List<PocodeWithStatusResponseDto> findAllPocodes(@Param("userId") Long userId);
    
    @Query(value = " "
             + " SELECT " 
             + " 	e.event_id, " 
             + " 	e.event_no, " 
             + " 	CASE WHEN :lang = 0 THEN et.name_english " 
             + "     	WHEN :lang = 1 THEN et.name_hindi " 
             + "      	WHEN :lang = 2 THEN et.name_gujarati END AS event_name, "
             + "	e.event_start_date_time, "
             + " 	pm.party_id, " 
             + " 	CASE WHEN :lang = 0 THEN pm.name_english " 
             + "		WHEN :lang = 1 THEN pm.name_hindi " 
             + "      	WHEN :lang = 2 THEN pm.name_gujarati END AS party_name, " 
             + " 	pm.mobileno, " 
             + " 	pm.email, " 
             + " 	CASE WHEN :lang = 0 THEN pm.address_english " 
             + "      	WHEN :lang = 1 THEN pm.address_hindi " 
             + "      	WHEN :lang = 2 THEN pm.address_gujarati END AS party_address, " 
             + " 	COALESCE(e.venue, bhm.hall_name) AS venue, " 
             + " 	u.user_id, " 
             + " 	ubd.company_name, " 
             + " 	u.logo, " 
             + " 	ubd.office_no, " 
             + " 	ubd.company_email, " 
             + " 	ubd.address, " 
             + " 	pos.storepo_id, " 
             + " 	pos.pocode, " 
             + " 	pos.podate, " 
             + " 	pos.remarks, " 
             + " 	pos.voucher, " 
             + " 	pos.stock_type_id, " 
             + " 	CASE WHEN :lang = 0 THEN st.name_english " 
             + "      	WHEN :lang = 1 THEN st.name_hindi " 
             + "      	WHEN :lang = 2 THEN st.name_gujarati END AS stock_name, " 
             + " 	pos.status, " 
             + " 	pos.cr_id, " 
             + " 	cr.crcode, " 
             + " 	pos.kitchen_type_id, " 
             + " 	CASE WHEN :lang = 0 THEN ka.name_english " 
             + "      	WHEN :lang = 1 THEN ka.name_hindi " 
             + "      	WHEN :lang = 2 THEN ka.name_gujarati END AS kitchen_name, " 
             + " 	posd.raw_material_cat_id, " 
             + " 	CASE WHEN :lang = 0 THEN rmc.name_english " 
             + "      	WHEN :lang = 1 THEN rmc.name_hindi " 
             + "      	WHEN :lang = 2 THEN rmc.name_gujarati END AS raw_material_cat, " 
             + " 	posd.storepodetail_id, " 
             + " 	posd.raw_material_id, " 
             + " 	CASE WHEN :lang = 0 THEN rm.name_english " 
             + "      	WHEN :lang = 1 THEN rm.name_hindi " 
             + "      	WHEN :lang = 2 THEN rm.name_gujarati END AS raw_material, " 
             + " 	COALESCE(posd.qty, 0) - COALESCE(sird.qty, 0) AS qty, " 
             + " 	posd.unit_id, " 
             + " 	CASE WHEN :lang = 0 THEN un.name_english " 
             + "      	WHEN :lang = 1 THEN un.name_hindi " 
             + "      	WHEN :lang = 2 THEN un.name_gujarati END AS unit, " 
             + " 	CAST(( " 
             + "     	SELECT CASE " 
             + "         	WHEN AVG(pod1.price) IS NULL THEN IFNULL(rm.supplier_rate,0) " 
             + "         	ELSE AVG(pod1.price) " 
             + "     	END " 
             + "     	FROM purchaseorderdetails pod1 " 
             + "     	WHERE pod1.raw_material_id = posd.raw_material_id " 
             + " 	) AS DECIMAL(10,2)) AS rate, " 
             + " 	CAST(( " 
             + "     	(COALESCE(posd.qty, 0) - COALESCE(sird.qty, 0)) * " 
             + "     	CAST(( " 
             + "         	SELECT CASE " 
             + "             	WHEN AVG(pod1.price) IS NULL THEN IFNULL(rm.supplier_rate,0) " 
             + "             	ELSE AVG(pod1.price) " 
             + "         	END " 
             + "         	FROM purchaseorderdetails pod1 " 
             + "         	WHERE pod1.raw_material_id = posd.raw_material_id " 
             + "     	) AS DECIMAL(10,2)) " 
             + " 	) AS DECIMAL(10,2)) AS total_amount " 
             + " FROM purchaseorderstore pos " 
             + " LEFT JOIN purchaseorderstoredetails posd ON pos.storepo_id = posd.storepo_id " 
             + " LEFT JOIN storeissuereturn sir ON sir.storepo_id = pos.storepo_id "
             + " LEFT JOIN storeissuereturndetails sird ON sird.sir_id = sir.sir_id AND sird.raw_material_id = posd.raw_material_id "
             + " LEFT JOIN events e ON e.event_id = pos.event_id " 
             + " LEFT JOIN eventtype et ON et.event_type_id = e.event_type_id " 
             + " LEFT JOIN users u ON u.user_id = pos.user_id " 
             + " LEFT JOIN user_basic_details ubd ON ubd.user_id = u.user_id " 
             + " LEFT JOIN partymaster pm ON pm.party_id = pos.party_id " 
             + " LEFT JOIN stock_type st ON st.stock_type_id = pos.stock_type_id " 
             + " LEFT JOIN chefrequisition cr ON cr.cr_id = pos.cr_id " 
             + " LEFT JOIN kitchenarea ka ON ka.kitchen_area_id = pos.kitchen_type_id " 
             + " LEFT JOIN raw_material_category rmc ON rmc.raw_matrial_cat_id = posd.raw_material_cat_id " 
             + " LEFT JOIN rawmaterial rm ON rm.raw_material_id = posd.raw_material_id " 
             + " LEFT JOIN units un ON un.unit_id = posd.unit_id " 
             + " LEFT JOIN banquet_hall_master bhm ON bhm.id = e.banquet_hall_id " 
             + " WHERE pos.event_id = :eventId" ,
    nativeQuery = true)
    List<Object[]> getStoreIssueDataForCostingReport(
    		@Param("eventId") Long eventId,
			@Param("lang") Integer lang);
    
    @Query(value = ""
            + " SELECT "
            + "    pos.podate AS issueDate, "
            + "    posd.storepodetail_id AS storePoDetailId, "
            + "    rmc.raw_matrial_cat_id AS rawMaterialCatId, "
            + "    rmc.name_english AS rawMaterialCatName, "
            + "    rm.raw_material_id AS rawMaterialId, "
            + "    rm.name_english AS rawMaterialName, "
            + "    u.unit_id AS unitId, "
            + "    u.name_english AS unitName, "
            + "    posd.qty AS qty, "
            + "    CASE "
            + "        WHEN :type = 'Master' THEN IFNULL(rm.supplier_rate,0) "
            + "        WHEN :type = 'Average' THEN ( "
            + "            SELECT IFNULL(AVG(pod.price),0) "
            + "            FROM purchaseorderdetails pod "
            + "            INNER JOIN purchaseorder po "
            + "                    ON po.po_id = pod.po_id "
            + "            WHERE pod.raw_material_id = rm.raw_material_id "
            + "              AND po.is_delete = FALSE "
            + "        ) "
            + "        WHEN :type = 'Latest' THEN ( "
            + "            SELECT pod.price "
            + "            FROM purchaseorderdetails pod "
            + "            INNER JOIN purchaseorder po "
            + "                    ON po.po_id = pod.po_id "
            + "            WHERE pod.raw_material_id = rm.raw_material_id "
            + "              AND po.is_delete = FALSE "
            + "            ORDER BY po.podate DESC, pod.podetail_id DESC "
            + "            LIMIT 1 "
            + "        ) "
            + "        ELSE 0 "
            + "    END AS price "
            + " FROM purchaseorderstore pos "
            + " INNER JOIN purchaseorderstoredetails posd "
            + "        ON pos.storepo_id = posd.storepo_id "
            + " INNER JOIN raw_material_category rmc "
            + "        ON rmc.raw_matrial_cat_id = posd.raw_material_cat_id "
            + " INNER JOIN rawmaterial rm "
            + "        ON rm.raw_material_id = posd.raw_material_id "
            + " INNER JOIN units u "
            + "        ON u.unit_id = posd.unit_id "
            + " WHERE pos.user_id = :userId "
            + "  AND pos.is_delete = FALSE "
            + "  AND pos.podate BETWEEN :startDate AND :endDate"
            + "  AND (:kitchenTypeId IS NULL OR :kitchenTypeId = -1 OR pos.kitchen_type_id = :kitchenTypeId) "
            + " ORDER BY rmc.sequence, pos.podate, rm.sequence ",
            nativeQuery = true)
    List<Object[]> getDatewiseStoreIssueReportData(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") String type,
            @Param("kitchenTypeId") Long kitchenTypeId);
    
    @Query(value = "SELECT "
            + " pos.event_id AS eventId, "
            + " ROUND(IFNULL(issue.totalIssuedPrice, 0), 2) AS totalIssuedPrice, "
            + " ROUND(IFNULL(ret.totalReturnPrice, 0), 2) AS totalReturnPrice, "
            + " ROUND( "
            + "     IFNULL(issue.totalIssuedPrice, 0) "
            + "     - IFNULL(ret.totalReturnPrice, 0), "
            + "     2 "
            + " ) AS totalPrice "
            + "FROM purchaseorderstore pos "
            + "LEFT JOIN ( "
            + "    SELECT "
            + "        pos.event_id, "
            + "        SUM(posd.qty * pod.price) AS totalIssuedPrice "
            + "    FROM purchaseorderstore pos "
            + "    JOIN purchaseorderstoredetails posd "
            + "        ON posd.storepo_id = pos.storepo_id "
            + "    JOIN purchaseorderdetails pod "
            + "        ON pod.raw_material_id = posd.raw_material_id "
            + "    GROUP BY pos.event_id "
            + ") issue "
            + "    ON issue.event_id = pos.event_id "
            + "LEFT JOIN ( "
            + "    SELECT "
            + "        sir.event_id, "
            + "        SUM(sird.qty * pod.price) AS totalReturnPrice "
            + "    FROM storeissuereturn sir "
            + "    JOIN storeissuereturndetails sird "
            + "        ON sird.sir_id = sir.sir_id "
            + "    JOIN purchaseorderdetails pod "
            + "        ON pod.raw_material_id = sird.raw_material_id "
            + "    GROUP BY sir.event_id "
            + ") ret "
            + "    ON ret.event_id = pos.event_id "
            + "WHERE pos.event_id = :eventId "
            + "LIMIT 1",
            nativeQuery = true)
    Object getStoreIssuePriceByEventId(@Param("eventId") Long eventId);
}