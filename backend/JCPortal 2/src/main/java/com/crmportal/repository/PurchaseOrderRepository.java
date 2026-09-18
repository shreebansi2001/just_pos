package com.crmportal.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseOrderEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.PocodeWithStatusResponseDto;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Long> {

	List<PurchaseOrderEntity> findByUserIdAndIsDeleteFalseOrderByCreatedAtDesc(Long userId);
	
	@Query("SELECT p FROM PurchaseOrderEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.isDelete = false " +
		       "ORDER BY p.createdAt DESC")
		List<PurchaseOrderEntity> getByUserDesc(@Param("userId") Long userId);

	List<PurchaseOrderEntity> findByUserIdAndPotypeAndIsDeleteFalse(Long userId, int potype);

	List<PurchaseOrderEntity> findByIdAndIsDeleteFalse(Long id);

	List<PurchaseOrderEntity> findByPotypeAndIsDeleteFalse(int potype);

	Optional<PurchaseOrderEntity> findByPocodeAndUserIdAndIsDeleteFalse(String pocode, Long userId);

	@Query("SELECT COUNT(p) FROM PurchaseOrderEntity p " +
		       "WHERE p.user.id = :userId " +
		       "AND p.createdAt >= :start " +
		       "AND p.createdAt < :end")
		long countByUserAndCreatedAtBetween(
		        @Param("userId") Long userId,
		        @Param("start") LocalDateTime start,
		        @Param("end") LocalDateTime end);

	@Query("SELECT new com.crmportal.response.dto.PocodeWithStatusResponseDto(" + "p.id, p.pocode, false) "
			+ "FROM PurchaseOrderEntity p " + "WHERE p.isDelete = false AND p.user.id = :userId "
			+ "ORDER BY p.createdAt DESC")
	List<PocodeWithStatusResponseDto> findAllPocodes(@Param("userId") Long userId);

	@Query("SELECT MAX(p.pocode) FROM PurchaseOrderEntity p WHERE p.pocode LIKE CONCAT(:prefix, '%')")
	String findMaxPocodeByPrefix(@Param("prefix") String prefix);

	@Query(value = "SELECT DISTINCT " +
	        "p.party_id AS partyId, " +
	        "p.name_english AS partyName, " +
	        " '' AS eventTypeName, " +
	        "CAST(NULL AS DATETIME) AS startDateTime, " +
	        "CAST(NULL AS DATETIME) AS endDateTime, " +
	        "p.opb_date as opbDate, "+
	        "p.opb as opb, "+
	        "po.created_at AS createdAt " + 
	        "FROM purchaseorder po "+
	        "JOIN partymaster p ON po.supplier_id = p.party_id " +
	        "JOIN contact_category cc ON p.contact_category_id = cc.contact_category_id " +
	        "JOIN contacttype ct ON cc.contact_type_id = ct.contact_type_id " +
	        "WHERE po.user_id = :userid AND p.user_id = :userid " +
	        "AND ct.contact_type_id IN (2,3,6) " +
	        "AND p.is_delete = false " +
	        "ORDER BY po.created_at ASC", 
	        nativeQuery = true)
	List<Object[]> findAllPurchasePartyByUser(Long userid);
	
	@Query(value = " "
			+ "	SELECT "
			+ "		pod.price "
			+ "	FROM purchaseorder po "
			+ "	INNER JOIN purchaseorderdetails pod "
			+ "	ON po.po_id = pod.po_id "
			+ "	WHERE po.supplier_id = :supplierId "
			+ "	AND po.user_id = :userId "
			+ "	AND pod.raw_material_id = :rawMaterialId "
			+ "	ORDER BY po.po_id DESC, pod.podetail_id DESC"
			+ "	LIMIT 1 ", nativeQuery = true)
	BigDecimal getLatestRawMaterialPriceSupplierWise(
			@Param("userId") Long userId, 
			@Param("supplierId") Long supplierId,
			@Param("rawMaterialId") Long rawMaterialId);
	
	Optional<PurchaseOrderEntity> findByGrnNumberAndUserAndIsDeleteFalse(String grnNumber, UserMasterEntity user);
}