package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DecoreMainCategoryItemMasterEntity;
import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.entity.DecorePreparationDetailsEntity;
import com.crmportal.entity.DecorePreparationEntity;
import com.crmportal.response.dto.DecoreCategoryForPreparationResponseDto;

@Repository
public interface DecorePreparationDetailsRepository extends JpaRepository<DecorePreparationDetailsEntity, Long> {

	void deleteAllByDecorePreparation(DecorePreparationEntity decorePreparation);

	List<DecorePreparationDetailsEntity> findAllByDecorePreparation(DecorePreparationEntity decorePreparation);

	List<DecorePreparationDetailsEntity> findByDecorePreparationIn(List<DecorePreparationEntity> preparations);

	List<DecorePreparationDetailsEntity> findByDecorePreparationId(Long decorePreparationId);

	List<DecorePreparationDetailsEntity> findAllByDecoreMainCategory(DecoreMainCategoryMasterEntity category);

	List<DecorePreparationDetailsEntity> findAllByDecoreMainCategoryIdIn(List<Long> categoryIds);

	List<DecorePreparationDetailsEntity> findAllByDecoreMainCategoryAndDecorePreparationOrderByDecoreCatSortOrderAscDecoreItemSortOrderAsc(
			DecoreMainCategoryMasterEntity category, DecorePreparationEntity decorePreparation);

	Optional<DecorePreparationDetailsEntity> findByDecorePreparationAndDecoreItemAndDecoreMainCategory(
			DecorePreparationEntity decorePreparation, DecoreMainCategoryItemMasterEntity item,
			DecoreMainCategoryMasterEntity category);

	boolean existsByDecorePreparation(DecorePreparationEntity decorePreparation);

	boolean existsByDecoreItem(DecoreMainCategoryItemMasterEntity item);

	@Query("SELECT DISTINCT dpd.decoreItem.id " + "FROM DecorePreparationDetailsEntity dpd "
			+ "WHERE dpd.decorePreparation.eventFunction.event.id = :eventId "
			+ "AND dpd.decorePreparation.eventFunction.id = :eventFunctionId")
	List<Long> findDistinctDecoreItemIdsByEventAndEventFunction(@Param("eventId") Long eventId,
			@Param("eventFunctionId") Long eventFunctionId);

	@Query(value = "SELECT dpid.decore_item_id, " + " di.name_english, di.name_hindi, di.name_gujarati, "
			+ " COUNT(dpid.decore_item_id) AS totalCount " + "FROM decore_preparation_details dpid "
			+ "INNER JOIN decore_main_category_item di " + "ON di.decore_item_id = dpid.decore_item_id "
			+ "GROUP BY dpid.decore_item_id, di.name_english, di.name_hindi, di.name_gujarati "
			+ "ORDER BY totalCount DESC " + "LIMIT 10", nativeQuery = true)
	List<Object[]> getTopUsedDecoreItems();
	
	@Query("SELECT DISTINCT new com.crmportal.response.dto.DecoreCategoryForPreparationResponseDto(" +
	        "dpd.decoreMainCategory.id, " +
	        "dpd.decoreCategoryName, " +
	        "dpd.decoreCatSortOrder, " +
	        "dpd.decoreCatSlogan, " +
	        "dpd.decoreCatNotes, " +
	        "dpd.decoreCategoryNameHindi, " +
	        "dpd.decoreCategoryNameGujarati, " +
	        "dpd.decoreCatNotesHindi, " +
	        "dpd.decoreCatNotesGujarati, " +
	        "dpd.startTime, " +
	        "dpd.isDecoreCatAddons, " +
	        "dpd.catImgId, " +
	        "dpd.bgImgId, " +
	        "dpd.catSpace, " +
	        "dpd.anyItem, " +
	        "dpd.subCat, " +
	        "dpd.subCatHindi, " +
	        "dpd.subCatGujarati ) " +

	        "FROM DecorePreparationDetailsEntity dpd " +
	        "WHERE dpd.decorePreparation.eventFunction.id = :eventFunctionId " +
	        "AND dpd.decorePreparation.isDelete = false " +

	        "ORDER BY dpd.decoreCatSortOrder ASC")
	List<DecoreCategoryForPreparationResponseDto>
	findAllByEventFunctionIdAndIsDeleteFalse(
	        @Param("eventFunctionId") Long eventFunctionId);

}