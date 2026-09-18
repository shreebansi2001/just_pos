package com.crmportal.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.ExistingItemRawResponseDto;

@Repository
public interface MenuItemMasterRepository extends JpaRepository<MenuItemMasterEntity, Long> {

	@Query("SELECT mi.id FROM MenuItemMasterEntity mi")
	List<Long> findAllIds();

	List<MenuItemMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	List<MenuItemMasterEntity> findByUserAndSequenceAndIsDeleteFalse(UserMasterEntity user, Integer sequence);

	@Query("SELECT MAX(m.sequence) FROM MenuItemMasterEntity m WHERE m.user.id = :userId AND m.isDelete = false")
	Integer findMaxSequenceByUserAndIsDeleteFalse(@Param("userId") Long userId);

	@Modifying
	@Query("UPDATE MenuItemMasterEntity m SET m.sequence = m.sequence + 1 "
			+ "WHERE m.user.id = :userId AND m.sequence >= :sequence AND m.isDelete = false")
	void shiftSequencesForUser(@Param("userId") Long userId, @Param("sequence") Integer sequence);

	Optional<MenuItemMasterEntity> findByIdAndIsDeleteFalse(Long id);

	List<MenuItemMasterEntity> findByUserAndIsDeleteFalse(UserMasterEntity user);

	List<MenuItemMasterEntity> findByUuid(String uuid);

	Page<MenuItemMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user, Pageable pageable);

	Page<MenuItemMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String itemName,
			UserMasterEntity user, Pageable pageable);

	Page<MenuItemMasterEntity> findAllByUserAndMenuSubCategory_IdAndIsDeleteFalse(UserMasterEntity user,
			Long menuSubCatId, Pageable pageable);

	Page<MenuItemMasterEntity> findAllByUserAndMenuCategory_IdAndMenuSubCategory_IdAndIsDeleteFalse(
			UserMasterEntity user, Long menuCatId, Long menuSubCatId, Pageable pageable);

	Page<MenuItemMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndMenuSubCategory_IdAndIsDeleteFalse(
			String itemName, UserMasterEntity user, Long menuSubCatId, Pageable pageable);

	Page<MenuItemMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndMenuCategory_IdAndMenuSubCategory_IdAndIsDeleteFalse(
			String itemName, UserMasterEntity user, Long menuCatId, Long menuSubCatId, Pageable pageable);

	@Query("SELECT m FROM MenuItemMasterEntity m "
	        + "LEFT JOIN m.menuCategory mc "
	        + "LEFT JOIN m.menuSubCategory msc "
	        + "WHERE m.user = :user "
	        + "AND m.isDelete = false "
	        + "AND (:menuCatId IS NULL OR :menuCatId = 0L OR m.menuCategory.id = :menuCatId) "
	        + "AND (:menuSubCatId IS NULL OR :menuSubCatId = 0L OR m.menuSubCategory.id = :menuSubCatId) "
	        + "AND ( "
	        + "    :isWithRecipe IS NULL "
	        + "    OR (:isWithRecipe = true AND EXISTS ( "
	        + "        SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "        WHERE mir.menuItem = m AND mir.isDelete = false "
	        + "    )) "
	        + "    OR (:isWithRecipe = false AND NOT EXISTS ( "
	        + "        SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "        WHERE mir.menuItem = m AND mir.isDelete = false "
	        + "    )) "
	        + ") "
	        + "AND (LOWER(m.nameEnglish) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
	        + "OR LOWER(mc.nameEnglish) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
	        + "OR LOWER(msc.nameEnglish) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
	Page<MenuItemMasterEntity> searchMenuItemsDynamic(
	        @Param("searchTerm") String searchTerm,
	        @Param("user") UserMasterEntity user,
	        @Param("menuCatId") Long menuCatId,
	        @Param("menuSubCatId") Long menuSubCatId,
	        @Param("isWithRecipe") Boolean isWithRecipe,
	        Pageable pageable);

	Optional<MenuItemMasterEntity> findByNameEnglishContainingIgnoreCaseAndUuid(String categoryEnglish, String uuid);

	Optional<MenuItemMasterEntity> findByNameEnglishAndUuid(String categoryEnglish, String uuid);

	Page<MenuItemMasterEntity> findAllByUserAndMenuCategory_IdAndIsDeleteFalse(UserMasterEntity user, Long menuCatId,
			Pageable pageable);

	Page<MenuItemMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndMenuCategory_IdAndIsDeleteFalse(
			String itemName, UserMasterEntity user, Long menuCatId, Pageable pageable);

	@Query(value = "SELECT " + "    menu_item_id as menuItemId, " + "    name_english as nameEnglish, "
			+ "    qty_current as qtyCurrent, " + "    qty_prev as qtyPrev, " + "    CAST(( " + "        CASE "
			+ "            WHEN qty_prev = 0 AND qty_current > 0 "
			+ "                THEN CONCAT('+', ROUND((qty_current / 1) * 10, 1)) " + "            WHEN qty_prev > 0 "
			+ "                THEN CONCAT( " + "                    IF(qty_current >= qty_prev, '+', ''), "
			+ "                    ROUND(((qty_current - qty_prev) / qty_prev) * 10, 1) " + "                ) "
			+ "            ELSE '0' " + "        END " + "    ) AS CHAR) AS statusValue, " + "    CASE "
			+ "        WHEN qty_prev = 0 AND qty_current > 0 THEN 'up' "
			+ "        WHEN qty_prev > 0 AND qty_current >= qty_prev THEN 'up' "
			+ "        WHEN qty_prev > 0 AND qty_current < qty_prev THEN 'down' " + "        ELSE 'neutral' "
			+ "    END AS statusDirection " + "FROM ( " + "    SELECT " + "        mp.menu_item_id, "
			+ "        mi.name_english, " + "        SUM(CASE "
			+ "            WHEN DATE(mp.created_at) BETWEEN :startDate AND :endDate " + "            THEN 1 ELSE 0 "
			+ "        END) AS qty_current, " + "        SUM(CASE " + "            WHEN DATE(mp.created_at) BETWEEN "
			+ "                DATE_SUB(:startDate, INTERVAL DATEDIFF(:endDate, :startDate) + 1 DAY) "
			+ "                AND DATE_SUB(:startDate, INTERVAL 1 DAY) " + "            THEN 1 ELSE 0 "
			+ "        END) AS qty_prev " + "    FROM menupreparationdetails mp "
			+ "    JOIN memuitems mi ON mi.menu_item_id = mp.menu_item_id " + "    WHERE mi.user_id = :userId "
			+ "    GROUP BY mp.menu_item_id, mi.name_english " + ") AS subquery "
			+ "ORDER BY qty_current DESC", nativeQuery = true)
	List<Object[]> getMostSellingItems(@Param("startDate") String startDate, @Param("endDate") String endDate,
			@Param("userId") Long userId);

	@Modifying
	@Query(value = "UPDATE memuitems SET menu_category_id = :newCatId WHERE menu_item_id IN :menuItemIds AND is_delete = FALSE AND user_id = :userId", nativeQuery = true)
	int updateMenuItemCategories(List<Long> menuItemIds, Long newCatId, Long userId);

	@Modifying
	@Query(value = "UPDATE memuitems SET menu_sub_category_id = :newMenuSubCatId WHERE menu_item_id IN :menuItemIds AND is_delete = FALSE AND user_id = :userId", nativeQuery = true)
	int updateMenuSubCategory(List<Long> menuItemIds, Long newMenuSubCatId, Long userId);

	@Modifying
	@Query(value = "UPDATE menu_item_allocation_config SET base_price = :price, unit_id = :unitId, quantity_per_100_person = :quantity WHERE menu_item_id = :id AND user_id = :userId", nativeQuery = true)
	int updateMenuAllocation(Long id, Integer quantity, Long unitId, BigDecimal price, Long userId);

	@Query(value = "SELECT m.menu_item_id AS menuItemId, " + "m.name_english AS nameEnglish, "
			+ "m.name_gujarati AS nameGujarati, " + "m.name_hindi AS nameHindi, "
			+ "m.menu_category_id AS menuCategoryId, " + "mc.name_english AS categoryNameEnglish, "
			+ "mc.name_gujarati AS categoryNameGujarati, " + "mc.name_hindi AS categoryNameHindi, "
			+ "m.menu_sub_category_id AS menuSubCategoryId, " + "ms.name_english AS subCategoryNameEnglish, "
			+ "ms.name_gujarati AS subCategoryNameGujarati, " + "ms.name_hindi AS subCategoryNameHindi, "
			+ "ml.menu_item_allocation_config_id AS menuItemAllocationConfigId, "
			+ "ml.allocation_type AS allocationType, " + "ml.base_price AS basePrice, " + "ml.counter_no AS counterNo, "
			+ "ml.godown_location AS godownLocation, " + "ml.price_per_helper AS pricePerHelper, "
			+ "ml.price_per_labour AS pricePerLabour, " + "ml.quantity_per_100_person AS quantityPer100Person, "
			+ "ml.select_chef_labour_agency AS selectChefLabourAgency, "
			+ "ml.select_outside_agency AS selectOutsideAgency, " + "ml.select_inside_agency AS selectInsideAgency, "
			+ "ml.party_id AS supplierId, " + "pm.name_english AS supplierNameEnglish, "
			+ "pm.name_hindi AS supplierNameHindi, " + "pm.name_gujarati AS supplierNameGujarati, "
			+ "ml.contact_category_id AS contactCategoryId, " + "cc.name_english AS contactNameEnglish, "
			+ "cc.name_hindi AS contactNameHindi, " + "cc.name_gujarati AS contactNameGujarati, "
			+ "u.unit_id AS unitId, " + "u.name_english AS unitNameEnglish, " + "u.name_gujarati AS unitNameGujarati, "
			+ "u.name_hindi AS unitNameHindi, " + "u.symbol_english AS symbolEnglish,"
			+ "u.symbol_gujarati AS symbolGujarati, " + "u.symbol_hindi AS symbolHindi, " + "m.user_id AS userId, "
			+ "CASE WHEN ml.number IS NOT NULL AND TRIM(ml.number) <> '' THEN ml.number ELSE pm.mobileno END AS number, "
			+ "ml.remarks AS remarks, ml.helper_no AS helperNo " + "FROM memuitems m "
			+ "JOIN menucategory mc ON m.menu_category_id = mc.menu_category_id "
			+ "LEFT JOIN menusubcategory ms ON m.menu_sub_category_id = ms.menu_sub_cat_id "
			+ "LEFT JOIN menu_item_allocation_config ml ON m.menu_item_id = ml.menu_item_id "
			+ "LEFT JOIN contact_category cc ON cc.contact_category_id = ml.contact_category_id "
			+ "LEFT JOIN partymaster pm ON pm.party_id = ml.party_id " + "LEFT JOIN units u ON ml.unit_id = u.unit_id "
			+ "WHERE m.menu_category_id IN (:menuCatIds) "
			+ "AND (:menuSubcatIds IS NULL OR m.menu_sub_category_id IN (:menuSubcatIds)) " + " AND (:type Is NULL "
			+ " OR ( " + " ( " + " :type = 'INSIDE'"
			+ " AND (ml.select_inside_agency = :inside OR ml.select_inside_agency IS NULL) "
			+ " AND (ml.select_outside_agency = :outside OR ml.select_outside_agency IS NULL) "
			+ " AND (ml.select_chef_labour_agency = :cheflabour OR ml.select_chef_labour_agency IS NULL) " + "	) "
			+ " OR (" + " (:type = 'OUTSIDE' OR :type = 'CHEFLABOUR') " + " AND ml.select_outside_agency = :outside "
			+ " AND ml.select_inside_agency = :inside " + " AND ml.select_chef_labour_agency = :cheflabour " + " ) "
			+ " ) " + " )" + "AND m.user_id = :userId " + "AND m.is_delete = FALSE", nativeQuery = true)
	List<Object[]> findByMenuCategoryIdInAndMenuSubCategoryIdInAndUserIdAndIsDeleteFalseAndSelectAgency(
			List<Long> menuCatIds, List<Long> menuSubcatIds, Long userId, Boolean inside, Boolean outside,
			Boolean cheflabour, String type);

	boolean existsByNameEnglishAndUserAndIsDeleteFalseAndUuid(String nameEnglish, UserMasterEntity userMasterEntity,
			String uuid);

	Page<MenuItemMasterEntity> findAllByUserAndIsDeleteFalseOrderBySequenceAsc(UserMasterEntity user,
			Pageable pageable);

	Page<MenuItemMasterEntity> findAllByUserAndMenuCategory_IdAndIsDeleteFalseOrderBySequenceAsc(UserMasterEntity user,
			Long menuCatId, Pageable pageable);

	Page<MenuItemMasterEntity> findAllByUserAndMenuSubCategory_IdAndIsDeleteFalseOrderBySequenceAsc(
			UserMasterEntity user, Long menuSubCatId, Pageable pageable);

	Page<MenuItemMasterEntity> findAllByUserAndMenuCategory_IdAndMenuSubCategory_IdAndIsDeleteFalseOrderBySequenceAsc(
			UserMasterEntity user, Long menuCatId, Long menuSubCatId, Pageable pageable);

	List<MenuItemMasterEntity> findByUuidAndIsDeleteFalse(String oldUuid);

	List<MenuItemMasterEntity> findAllByMenuCategoryAndIsDeleteFalse(MenuCategoryMasterEntity entity);

	@Query("SELECT new com.crmportal.response.dto.ExistingItemRawResponseDto(m.id, m.nameEnglish) " +
		       "FROM MenuItemMasterEntity m " +
		       "WHERE m.user.id = :userId " +
		       "AND m.isDelete = false " +
		       "AND m.isActive = true " +
		       "AND EXISTS ( " +
		       "   SELECT 1 FROM MenuItemRawMaterialEntity mir " +
		       "   WHERE mir.menuItem.id = m.id " +
		       "   AND mir.user.id = :userId " +
		       "   AND mir.isDelete = false " +
		       "   AND mir.isActive = true" +
		       ")")
		List<ExistingItemRawResponseDto> getAllExistingItems(Long userId);

	List<MenuItemMasterEntity> findByUuidAndUserAndIsDeleteFalse(String uuid, UserMasterEntity userMasterEntity);

	@Query(value = " "
			+ "	SELECT  "
			+ "		mi.name_english AS item_name_english, "
			+ "		mi.name_hindi AS item_name_hindi, "
			+ "		mi.name_gujarati AS item_name_gujarati, "
			+ "		mi.slogan AS slogan, "
			+ "		mc.name_english AS cat_name_english "
			+ "	FROM memuitems mi "
			+ "	INNER JOIN menucategory mc "
			+ "		ON mi.menu_category_id = mc.menu_category_id "
			+ "		AND mc.is_delete = FALSE "
			+ "		AND mc.user_id = :userId "
			+ "	WHERE mi.is_delete = FALSE "
			+ "		AND mi.user_id = :userId "
			+ "ORDER BY mi.sequence ASC", nativeQuery = true)
	List<Object[]> getMenuItemExportData(@Param("userId") Long userId);

	@Query("SELECT m FROM MenuItemMasterEntity m "
	        + "WHERE m.user = :user "
	        + "AND m.isDelete = false "
	        + "AND ( "
	        + "    :isWithRecipe IS NULL "
	        + "    OR ( "
	        + "        :isWithRecipe = true "
	        + "        AND EXISTS ( "
	        + "            SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "            WHERE mir.menuItem = m "
	        + "            AND mir.isDelete = false "
	        + "        ) "
	        + "    ) "
	        + "    OR ( "
	        + "        :isWithRecipe = false "
	        + "        AND NOT EXISTS ( "
	        + "            SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "            WHERE mir.menuItem = m "
	        + "            AND mir.isDelete = false "
	        + "        ) "
	        + "    ) "
	        + ")")
	Page<MenuItemMasterEntity> findAllByUserAndRecipeFilter(
	        @Param("user") UserMasterEntity user,
	        @Param("isWithRecipe") Boolean isWithRecipe,
	        Pageable pageable);
	
	@Query("SELECT m FROM MenuItemMasterEntity m "
	        + "WHERE m.user = :user "
	        + "AND m.menuCategory.id = :menuCatId "
	        + "AND m.isDelete = false "
	        + "AND ( "
	        + "    :isWithRecipe IS NULL "
	        + "    OR ( "
	        + "        :isWithRecipe = true "
	        + "        AND EXISTS ( "
	        + "            SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "            WHERE mir.menuItem = m "
	        + "            AND mir.isDelete = false "
	        + "        ) "
	        + "    ) "
	        + "    OR ( "
	        + "        :isWithRecipe = false "
	        + "        AND NOT EXISTS ( "
	        + "            SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "            WHERE mir.menuItem = m "
	        + "            AND mir.isDelete = false "
	        + "        ) "
	        + "    ) "
	        + ")")
	Page<MenuItemMasterEntity> findAllByUserAndMenuCategory_IdAndIsDeleteFalseAndRecipeFilter(
	        @Param("user") UserMasterEntity user,
	        @Param("menuCatId") Long menuCatId,
	        @Param("isWithRecipe") Boolean isWithRecipe,
	        Pageable pageable);
	
	@Query("SELECT m FROM MenuItemMasterEntity m "
	        + "WHERE m.user = :user "
	        + "AND m.menuSubCategory.id = :menuSubCatId "
	        + "AND m.isDelete = false "
	        + "AND ( "
	        + "    :isWithRecipe IS NULL "
	        + "    OR ( "
	        + "        :isWithRecipe = true "
	        + "        AND EXISTS ( "
	        + "            SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "            WHERE mir.menuItem = m "
	        + "            AND mir.isDelete = false "
	        + "        ) "
	        + "    ) "
	        + "    OR ( "
	        + "        :isWithRecipe = false "
	        + "        AND NOT EXISTS ( "
	        + "            SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "            WHERE mir.menuItem = m "
	        + "            AND mir.isDelete = false "
	        + "        ) "
	        + "    ) "
	        + ")")
	Page<MenuItemMasterEntity> findAllByUserAndMenuSubCategory_IdAndIsDeleteFalseAndRecipeFilter(
	        @Param("user") UserMasterEntity user,
	        @Param("menuSubCatId") Long menuSubCatId,
	        @Param("isWithRecipe") Boolean isWithRecipe,
	        Pageable pageable);
	
	@Query("SELECT m FROM MenuItemMasterEntity m "
	        + "WHERE m.user = :user "
	        + "AND m.menuCategory.id = :menuCatId "
	        + "AND m.menuSubCategory.id = :menuSubCatId "
	        + "AND m.isDelete = false "
	        + "AND ( "
	        + "    :isWithRecipe IS NULL "
	        + "    OR ( "
	        + "        :isWithRecipe = true "
	        + "        AND EXISTS ( "
	        + "            SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "            WHERE mir.menuItem = m "
	        + "            AND mir.isDelete = false "
	        + "        ) "
	        + "    ) "
	        + "    OR ( "
	        + "        :isWithRecipe = false "
	        + "        AND NOT EXISTS ( "
	        + "            SELECT 1 FROM MenuItemRawMaterialEntity mir "
	        + "            WHERE mir.menuItem = m "
	        + "            AND mir.isDelete = false "
	        + "        ) "
	        + "    ) "
	        + ")")
	Page<MenuItemMasterEntity> findAllByUserAndMenuCategory_IdAndMenuSubCategory_IdAndIsDeleteFalseAndRecipeFilter(
	        @Param("user") UserMasterEntity user,
	        @Param("menuCatId") Long menuCatId,
	        @Param("menuSubCatId") Long menuSubCatId,
	        @Param("isWithRecipe") Boolean isWithRecipe,
	        Pageable pageable);

	@Query("SELECT new com.crmportal.response.dto.ExistingItemRawResponseDto(m.id, m.nameEnglish) " +
		       "FROM MenuItemMasterEntity m " +
		       "WHERE m.user.id = :userId " +
		       "AND m.isDelete = false " +
		       "AND m.isActive = true " +
		       "AND EXISTS ( " +
		       "   SELECT 1 FROM MenuItemCaptainReceipeEntity micr " +
		       "   WHERE micr.menuItem.id = m.id " +
		       "   AND micr.user.id = :userId " +
		       "   AND micr.isDelete = false " +
		       "   AND micr.isActive = true" +
		       ")")
	List<ExistingItemRawResponseDto> getAllExistingItemsForCaptainRecipe(Long userId);
	
	
}
