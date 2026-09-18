package com.crmportal.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventRawMaterialDisposableEntity;
import com.crmportal.entity.EventRawMaterialEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface EventRawMaterialDisposableRepository
        extends JpaRepository<EventRawMaterialDisposableEntity, Long> {

//	@Query(value = "SELECT " +
//	        "ermd.id, " +
//
//	        "rmc.name_english AS category_name_english, " +
//	        "rmc.name_hindi AS category_name_hindi, " +
//	        "rmc.name_gujarati AS category_name_gujarati, " +
//
//	        "r.name_english AS raw_material_name_english, " +
//	        "r.name_hindi AS raw_material_name_hindi, " +
//	        "r.name_gujarati AS raw_material_name_gujarati, " +
//
//	        "u.name_english AS unit_name_english, " +
//	        "u.name_hindi AS unit_name_hindi, " +
//	        "u.name_gujarati AS unit_name_gujarati, " +
//
//	        "r.supplier_rate AS supp_rate, " +
//	        "IFNULL(ermd.qty, 0) AS qty, " +
//	        "IFNULL(ermd.total_rate, 0) AS totalRate, " +
//
//	        " u.unit_id, " +
//	        "r.raw_material_id, "+
//	        "rmc.raw_matrial_cat_id "+
//	        "FROM rawmaterial r " +
//
//	        "JOIN raw_material_category rmc " +
//	        "ON r.raw_material_cat_id = rmc.raw_matrial_cat_id " +
//
//	        "JOIN units u ON u.unit_id = r.unit_id " +
//
//	        "LEFT JOIN event_raw_material_disposable ermd " +
//	        "ON r.raw_material_id = ermd.raw_material_id " +
//	        "AND ermd.user_id = :userId " +
//	        "AND ermd.event_id = :eventId " +
//
//	        "WHERE rmc.raw_material_cat_type_id IN (2,4) " +
//	        "AND r.user_id = :userId " +
//	        "AND r.is_delete = FALSE " +
//	        "AND rmc.user_id = :userId " +
//	        "AND rmc.is_delete = FALSE " +
//	        "AND u.is_delete = FALSE "+
//	        "AND rmc.raw_matrial_cat_id = :rawCategoryId ",
//
//	        nativeQuery = true)
//	List<Object[]> findRawMaterialDisposable(
//	        @Param("eventId") Long eventId,
//	        @Param("userId") Long userId, Long rawCategoryId);
	
	@Query(value = "SELECT " +
	        "ermd.id, " +

	        "rmc.name_english AS category_name_english, " +
	        "rmc.name_hindi AS category_name_hindi, " +
	        "rmc.name_gujarati AS category_name_gujarati, " +

	        "r.name_english AS raw_material_name_english, " +
	        "r.name_hindi AS raw_material_name_hindi, " +
	        "r.name_gujarati AS raw_material_name_gujarati, " +

	        "u.name_english AS unit_name_english, " +
	        "u.name_hindi AS unit_name_hindi, " +
	        "u.name_gujarati AS unit_name_gujarati, " +

	        "r.supplier_rate AS supp_rate, " +
	        "IFNULL(ermd.qty, 0) AS qty, " +
	        "IFNULL(ermd.total_rate, 0) AS totalRate, " +

	        "u.unit_id, " +
	        "r.raw_material_id, " +
	        "rmc.raw_matrial_cat_id, " +
	        "r.file "+

	        "FROM rawmaterial r " +

	        "JOIN raw_material_category rmc " +
	        "ON r.raw_material_cat_id = rmc.raw_matrial_cat_id " +

	        "JOIN units u ON u.unit_id = r.unit_id " +

	        "LEFT JOIN event_raw_material_disposable ermd  " +
	        "ON r.raw_material_id = ermd.raw_material_id " +
	        "AND ermd.user_id = :userId " +
	        "AND ermd.event_id = :eventId " +
	        "WHERE rmc.raw_material_cat_type_id IN (2,4) " +
	        "AND r.user_id = :userId " +
	        "AND r.is_delete = FALSE " +
	        "AND rmc.user_id = :userId " +
	        "AND rmc.is_delete = FALSE " +
	        "AND u.is_delete = FALSE " +
	        "AND (:rawCategoryId IS NULL OR :rawCategoryId = -1 OR rmc.raw_matrial_cat_id = :rawCategoryId)"+
	        "AND (:isAllItems = 1 OR :qty IS NULL OR :qty = -1 OR ermd.qty != 0) " + 
	        "ORDER BY rmc.sequence ASC, r.sequence ASC ",

	        nativeQuery = true)
	List<Object[]> findRawMaterialDisposable(
	        @Param("eventId") Long eventId,
	        @Param("userId") Long userId,
	        @Param("rawCategoryId") Long rawCategoryId, 
	        @Param("qty") Long qty,
	        @Param("isAllItems") Integer isAllItems);
	
	
    boolean existsByEventIdAndIsDeleteFalse(Long eventId);

    // Master query — get from raw_material_category + rawmaterial directly
    @Query(value = "SELECT " +
            "rmc.raw_matrial_cat_id AS catId, " +
            "rmc.name_english      AS categoryName, " +
            "r.raw_material_id     AS rawMaterialId, " +
            "r.name_english        AS rawMaterialName, " +
            "r.supplier_rate       AS suppRate, " +
            "u.name_english        AS unitName " +  
            "FROM raw_material_category rmc " +
            "LEFT JOIN rawmaterial r " +
            "ON r.raw_material_cat_id = rmc.raw_matrial_cat_id " +
            "AND r.is_delete = false " +
            "AND r.is_active = true " +
            "LEFT JOIN units u ON u.unit_id = r.unit_id " + 
            "WHERE rmc.user_id = :userId " +
            "AND rmc.raw_material_cat_type_id IN (2, 4) " +
            "AND rmc.is_delete = false " +
            "AND rmc.is_active = true " +
            "ORDER BY rmc.sequence, r.sequence",
            nativeQuery = true)
    List<Object[]> findMasterRatesByUserId(@Param("userId") Long userId);
    
    

	void deleteAllByEventAndUserIdAndRawMaterialCat(EventMasterEntity event, Long id,
			RawMaterialCategoryMasterEntity cat);
	
	@Query(value = " "
			+ " SELECT "
			+ " 	SUM(COALESCE(totalRate, 0))"
			+ " FROM EventRawMaterialDisposableEntity"
			+ " WHERE event_id = :eventId ")
	BigDecimal getTotalDisposibleAmount(@Param("eventId") Long eventId); 
	
	List<EventRawMaterialDisposableEntity> findByEvent_Id(Long eventId);
}