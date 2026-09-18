package com.crmportal.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DecorePreparationEntity;
import com.crmportal.entity.EventFunctionMasterEntity;

@Repository
public interface DecorePreparationRepository extends JpaRepository<DecorePreparationEntity, Long> {

	Optional<DecorePreparationEntity> findByIdAndIsDeleteFalse(Long id);

	@Query("SELECT COALESCE(MAX(d.sortorder), 0) " + "FROM DecorePreparationEntity d "
			+ "WHERE d.eventFunction.id = :eventFunctionId")
	Integer findMaxSortOrder(@Param("eventFunctionId") Long eventFunctionId);

	void deleteByEventFunction(EventFunctionMasterEntity eventFunction);

	DecorePreparationEntity findByEventFunction_IdAndIsDeleteFalse(Long id);

	@Query("SELECT COALESCE(SUM(d.packagePrice), 0) " + "FROM DecorePreparationEntity d "
			+ "WHERE d.eventFunction.id = :eventFunctionId " + "AND d.isPackage = true " + "AND d.isDelete = false")
	BigDecimal getPackageRate(@Param("eventFunctionId") Long eventFunctionId);

	boolean existsByEventFunction_Id(Long eventFunctionId);

	boolean existsByEventFunction_IdAndIsDeleteFalse(Long eventFunctionId);

	List<DecorePreparationEntity> findByEventFunctionInAndIsDeleteFalse(List<EventFunctionMasterEntity> eventFunctions);

	@Query(value = "SELECT COUNT(DISTINCT e.event_id) " + "FROM decore_preparation dp "
			+ "JOIN event_function ef ON dp.event_function_id = ef.event_function_id "
			+ "JOIN events e ON ef.event_id = e.event_id " + "WHERE e.is_delete = FALSE " + "AND ef.is_delete = FALSE "
			+ "AND dp.is_delete = FALSE " + "AND (:userId = -1 OR e.user_id = :userId)", nativeQuery = true)
	Integer getDecoreCountByUserId(@Param("userId") Long userId);

	DecorePreparationEntity findByEventFunctionAndIsDeleteFalse(EventFunctionMasterEntity eventFunction);

	@Query(value =
	        "SELECT " +
	        " mi.name_english AS itemName, " +
	        " COALESCE(pc.name_english, c.name_english) AS categoryName, " +
	        " mi.price AS price, " +
	        " mi.slogan AS slogan, " +
	        " COALESCE(pc.category_slogan, c.category_slogan) AS categorySlogan, " +

	        " COALESCE(dpd.decore_main_category_id, c.decore_main_category_id) AS categoryId, " +
	        " mi.decore_item_id AS itemId, " +
	        " mi.url AS imagePath, " +

	        " CASE WHEN dpd.decore_preparation_details_id IS NOT NULL THEN 1 ELSE 0 END AS selected, " +

	        " dpd.decore_item_sortorder AS itemSortOrder, " +
	        " dpd.decore_cat_sortorder AS categorySortOrder, " +

	        " COALESCE(pc.name_hindi, c.name_hindi) AS categoryNameHindi, " +
	        " COALESCE(pc.name_gujarati, c.name_gujarati) AS categoryNameGujarati, " +

	        " mi.name_hindi AS itemNameHindi, " +
	        " mi.name_gujarati AS itemNameGujarati, " +

	        " mi.instruction_english AS instructionEnglish, " +
	        " mi.instruction_gujarati AS instructionGujarati, " +
	        " mi.instruction_hindi AS instructionHindi " +

	        "FROM decore_main_category_item mi " +

	        "JOIN decore_main_category c " +
	        "   ON mi.decore_main_category_id = c.decore_main_category_id " +

	        "LEFT JOIN decore_preparation_details dpd " +
	        "   ON dpd.decore_item_id = mi.decore_item_id " +
	        "   AND dpd.decore_preparation_id IN ( " +
	        "       SELECT dp.decore_preparation_id " +
	        "       FROM decore_preparation dp " +
	        "       WHERE dp.event_function_id = :eventFunctionId " +
	        "   ) " +

	        "LEFT JOIN decore_main_category pc " +
	        "   ON dpd.decore_main_category_id = pc.decore_main_category_id " +

	        "WHERE mi.is_delete = false " +
	        "AND mi.is_active = true " +
	        "AND c.is_delete = false " +
	        "AND c.is_active = true " +
	        "AND mi.user_id = :userId " +

	        "AND (:itemName IS NULL OR TRIM(:itemName) = '' " +
	        "     OR LOWER(mi.name_english) LIKE LOWER(CONCAT('%', :itemName, '%'))) " +

	        "AND (:decoreCategoryId = 0 OR COALESCE(dpd.decore_main_category_id, c.decore_main_category_id) = :decoreCategoryId) " +

	        "ORDER BY selected DESC, mi.name_english ASC",
	        nativeQuery = true)
	List<Object[]> getAllDecorePreparationItemsNative(
	        @Param("eventFunctionId") Long eventFunctionId,
	        @Param("userId") Long userId,
	        @Param("itemName") String itemName,
	        @Param("decoreCategoryId") Long decoreCategoryId
	);

}