package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CustomPackageDetailsEntity;
import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface CustomPackageDetailsRepository extends JpaRepository<CustomPackageDetailsEntity, Long>{

	void deleteByCustomPackage(CustomPackageEntity entity);

	List<CustomPackageDetailsEntity> findAllByCustomPackageAndUserAndIsDeleteFalse(CustomPackageEntity savedPackage,
			UserMasterEntity user);
	
	@Query("SELECT DISTINCT cp.menuCategory.id, cp.menuCategory.nameEnglish, cp.menuSortOrder, cp.menuInstruction, cp.anyItem FROM CustomPackageDetailsEntity cp " +
		       "WHERE cp.customPackage = :entity AND cp.user = :user AND cp.isDelete = false")
	List<Object[]> findDistinctMenuCategoriesByCustomPackageAndUserAndIsDeleteFalse(
		        CustomPackageEntity entity,
		        UserMasterEntity user);

	List<CustomPackageDetailsEntity> findAllByCustomPackageAndUserAndMenuCategory_IdAndIsDeleteFalse(
			CustomPackageEntity entity, UserMasterEntity user, Long menuCategoryId);

	List<CustomPackageDetailsEntity> findAllByCustomPackageAndIsDeleteFalse(CustomPackageEntity entity);
	
	@Query(value = " SELECT " +
	        " cpd.custom_package_details_id, " +

	        " CASE " +
	        "     WHEN :lang = 1 THEN mc.name_hindi " +
	        "     WHEN :lang = 2 THEN mc.name_gujarati " +
	        "     ELSE mc.name_english " +
	        " END AS menu_name, " +

	        " CASE " +
	        "     WHEN :lang = 1 THEN mi.name_hindi " +
	        "     WHEN :lang = 2 THEN mi.name_gujarati " +
	        "     ELSE mi.name_english " +
	        " END AS item_name, " +

	        " cpd.menu_instruction, " +
	        " cpd.menu_sortorder, " +

	        " cpd.item_instruction, " +
	        " cpd.item_sortorder, " +

	        " cpd.any_item, " +
	        " cpd.item_price, " +
	        " cpd.menu_category_id " +

	        " FROM custom_package_details cpd " +

	        " LEFT JOIN menucategory mc " +
	        "     ON mc.menu_category_id = cpd.menu_category_id " +

	        " LEFT JOIN memuitems mi " +
	        "     ON mi.menu_item_id = cpd.menu_item_id " +

	        " WHERE cpd.custom_package_id = :packageId " +
	        " AND cpd.user_id = :userId " +
	        " AND cpd.is_delete = false " +
	        " AND cpd.item_name IS NOT NULL " +
	        " ORDER BY cpd.menu_sortorder, cpd.item_sortorder ",nativeQuery = true)
	List<Object[]> getPackageDetailsLangWise(Long packageId, Long userId, Integer lang);
	
	@Query("SELECT cd.menuItem.id FROM CustomPackageDetailsEntity cd " +
		       "WHERE cd.customPackage.id = :packageId " +
		       "AND cd.menuItem IS NOT NULL " +
		       "AND cd.isDelete = false")
		List<Long> findMenuItemIdsByPackageId(@Param("packageId") Long packageId);

		@Query("SELECT cd.menuCategory.id FROM CustomPackageDetailsEntity cd " +
		       "WHERE cd.customPackage.id = :packageId " +
		       "AND cd.isDelete = false")
		List<Long> findMenuCategoryIdsByPackageId(@Param("packageId") Long packageId);

		@Query("SELECT cd.menuCategory.id FROM CustomPackageDetailsEntity cd " +
		       "WHERE cd.customPackage.id = :packageId " +
		       "AND cd.menuItem IS NULL " +
		       "AND cd.isDelete = false")
		List<Long> findAnyItemCategoryIdsByPackageId(@Param("packageId") Long packageId);
		
		@Query("SELECT cd FROM CustomPackageDetailsEntity cd " +
			       "WHERE cd.customPackage.id = :packageId " +
			       "AND cd.isDelete = false " +
			       "ORDER BY cd.menuSortOrder ASC, cd.itemSortOrder ASC")
			List<CustomPackageDetailsEntity> findByPackageId(
			        @Param("packageId") Long packageId);

}
