package com.crmportal.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseRequestEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.RawMaterialOPBResponseDto;

@Repository
public interface RawMaterialMasterRepository extends JpaRepository<RawMaterialMasterEntity, Long> {

	Optional<RawMaterialMasterEntity> findByNameEnglishAndUserAndRawMaterialCatAndIsDeleteFalse(String nameEnglish,
			UserMasterEntity user, RawMaterialCategoryMasterEntity rawMaterialMasterEntity);

	@Query("SELECT MAX(c.sequence) FROM RawMaterialMasterEntity c WHERE c.user.id = :userId AND c.isDelete = false")
	Integer findMaxSequenceByUserAndIsDeleteFalse(@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query("UPDATE RawMaterialMasterEntity c SET c.sequence = c.sequence + 1 "
			+ "WHERE c.user.id = :userId AND c.sequence >= :sequence AND c.isDelete = false AND c.rawMaterialCat = :rawMaterialCatMasterEntity")
	void shiftSequencesForUser(@Param("userId") Long userId, @Param("sequence") Integer sequence,
			RawMaterialCategoryMasterEntity rawMaterialCatMasterEntity);

	RawMaterialMasterEntity findByIdAndUserAndRawMaterialCatAndIsDeleteFalse(long id, UserMasterEntity user,
			RawMaterialCategoryMasterEntity rawMaterialCatMasterEntity);

	List<RawMaterialMasterEntity> findByUserAndSequenceAndIsDeleteFalse(UserMasterEntity user, Integer sequence);

	List<RawMaterialMasterEntity> findByUuid(String uuid);

	List<RawMaterialMasterEntity> findByUserAndIsGeneralFixTrue(UserMasterEntity user);

	@Query(value = "SELECT * FROM rawmaterial r " + "WHERE r.is_delete = FALSE " + "AND r.user_id = :userId "
			+ "AND (:typeId IS NULL OR r.raw_material_cat_id = :typeId) "
			+ "AND (:isActive IS NULL OR r.is_active = :isActive) " + "AND (:unitId IS NULL OR r.unit_id = :unitId) "
			+ "AND ( :name IS NULL OR " + "      LOWER(r.name_english) LIKE CONCAT('%', LOWER(:name), '%') OR "
			+ "      (r.raw_material_cat_id IS NOT NULL AND LOWER((SELECT c.name_english FROM raw_material_category c WHERE c.raw_matrial_cat_id = r.raw_material_cat_id)) LIKE CONCAT('%', LOWER(:name), '%')) OR "
			+ "      (r.unit_id IS NOT NULL AND LOWER((SELECT u.name_english FROM units u WHERE u.unit_id = r.unit_id)) LIKE CONCAT('%', LOWER(:name), '%')) "
			+ ")", countQuery = "SELECT COUNT(*) FROM rawmaterial r " + "WHERE r.is_delete = FALSE "
					+ "AND r.user_id = :userId " + "AND (:typeId IS NULL OR r.raw_material_cat_id = :typeId) "
					+ "AND (:isActive IS NULL OR r.is_active = :isActive) "
					+ "AND (:unitId IS NULL OR r.unit_id = :unitId) " + "AND ( :name IS NULL OR "
					+ "      LOWER(r.name_english) LIKE CONCAT('%', LOWER(:name), '%') OR "
					+ "      (r.raw_material_cat_id IS NOT NULL AND LOWER((SELECT c.name_english FROM raw_material_category c WHERE c.raw_matrial_cat_id = r.raw_material_cat_id)) LIKE CONCAT('%', LOWER(:name), '%')) OR "
					+ "      (r.unit_id IS NOT NULL AND LOWER((SELECT u.name_english FROM units u WHERE u.unit_id = r.unit_id)) LIKE CONCAT('%', LOWER(:name), '%')) "
					+ ")", nativeQuery = true)
	Page<RawMaterialMasterEntity> searchByNameOrType(@Param("userId") Long userId, @Param("typeId") Long typeId,
			@Param("isActive") Boolean isActive, @Param("unitId") Long unitId, @Param("name") String searchName,
			Pageable pageable);

	RawMaterialMasterEntity findByIdAndIsDeleteFalse(Long id);

	boolean existsByIdAndIsDeleteFalse(Long id);

	RawMaterialMasterEntity findByIdAndUserAndIsDeleteFalse(long id, UserMasterEntity user);

	Optional<RawMaterialMasterEntity> findByNameEnglishAndUserAndRawMaterialCatAndUnitAndIsDeleteFalse(
			String nameEnglish, UserMasterEntity user, RawMaterialCategoryMasterEntity rawMaterialCatMasterEntity,
			UnitMasterEntity unit);

	Optional<RawMaterialMasterEntity> findByNameEnglishAndUuidAndIsDeleteFalse(String nameEnglish, String uuid);

	Optional<RawMaterialMasterEntity> findByUserAndNameEnglishIgnoreCaseAndIsDeleteFalse(UserMasterEntity user,
			String categoryNameEnglish);

	Integer findMaxSequenceByUserAndIsDeleteFalseAndRawMaterialCat(Long id,
			RawMaterialCategoryMasterEntity rawMaterialCatMasterEntity);

	List<RawMaterialMasterEntity> findByUserAndSequenceAndIsDeleteFalseAndRawMaterialCat(UserMasterEntity user,
			Integer sequence, RawMaterialCategoryMasterEntity rawMaterialCatMasterEntity);

	List<RawMaterialMasterEntity> findAllByIdInAndIsDeleteFalse(List<Long> ids);

	@Query(value = "SELECT * FROM rawmaterial r " + "WHERE r.is_delete = FALSE " + "AND r.user_id = :userId "
			+ "AND (:isActive IS NULL OR r.is_active = :isActive)", nativeQuery = true)
	List<RawMaterialMasterEntity> searchByNameOrType(@Param("userId") Long userId, @Param("isActive") Boolean isActive);

	@Query(value = "SELECT * FROM rawmaterial r " + "WHERE r.is_delete = FALSE " + "AND r.user_id = :userId "
			+ "AND (:isGeneralFix IS NULL OR r.is_general_fix = :isGeneralFix)", countQuery = "SELECT COUNT(*) FROM rawmaterial r "
					+ "WHERE r.is_delete = FALSE " + "AND r.user_id = :userId "
					+ "AND (:isGeneralFix IS NULL OR r.is_general_fix = :isGeneralFix)", nativeQuery = true)
	Page<RawMaterialMasterEntity> searchByNameOrIsGeneralFix(Long userId, Boolean isGeneralFix, Pageable pageable);

	@Modifying
	@Query(value = "UPDATE rawmaterial SET raw_material_cat_id = :newCat WHERE raw_material_id IN (:rawMaterialIds) AND is_delete = FALSE AND user_id = :userId", nativeQuery = true)
	int updateRawMaterialItemCategory(@Param("rawMaterialIds") List<Long> rawMaterialIds, @Param("newCat") Long newCatId, Long userId);

	@Query(
		    value = "SELECT rm.raw_material_id, rm.name_english, rm.name_hindi, rm.name_gujarati, " +
		            "rmc.raw_matrial_cat_id, rmc.name_english AS cat_name_english, " +
		            "rmc.name_hindi AS cat_name_hindi, rmc.name_gujarati AS cat_name_gujarati, " +
		            "rms.party_id, pm.name_english AS party_name_english, " +
		            "pm.name_hindi AS party_name_hindi, pm.name_gujarati AS party_name_gujarati, " +
		            "rm.unit_id, rm.user_id " +
		            "FROM rawmaterial rm " +
		            "LEFT JOIN raw_material_category rmc " +
		            "       ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id " +
		            "LEFT JOIN raw_material_supplier rms " +
		            "       ON rm.raw_material_id = rms.raw_material_id " +
		            "LEFT JOIN partymaster pm " +
		            "       ON rms.party_id = pm.party_id " +
		            "WHERE rm.user_id = :userId AND rm.is_delete = FALSE " + 
		            "AND rm.raw_material_cat_id IN (:catIds) ",
		    countQuery = "SELECT COUNT(*) FROM rawmaterial rm " +
		    		 	 "WHERE rm.user_id = :userId AND rm.is_delete = FALSE " +
		    		 	"AND rm.raw_material_cat_id IN (:catIds) ",
		    nativeQuery = true
		)
	Page<Object[]> findByRawMaterialCatIdInAndUserIdAndIsDeleteFalse( @Param("catIds") List<Long> catIds, @Param("userId") Long userId, Pageable pageable);

	@Query(value = "SELECT raw_material_id FROM rawmaterial WHERE raw_material_cat_id IN (:catIds) AND user_id = :userId AND is_delete = FALSE", nativeQuery = true)
	List<Long> findRawMaterialIdByCatId(List<Long> catIds, Long userId);

	boolean existsByNameEnglishAndUserAndIsDeleteFalseAndUuid(String nameEnglish, UserMasterEntity userMasterEntity,
			String uuid);

	List<RawMaterialMasterEntity> findByUuidAndIsDeleteFalse(String newUuid);

	List<RawMaterialMasterEntity> findAllByRawMaterialCatAndIsDeleteFalse(
			RawMaterialCategoryMasterEntity categoryMasterEntity);
	
	@Query("SELECT r.opbStock FROM RawMaterialMasterEntity r WHERE r.id = :rawMaterialId")
	Double findOpbStockByRawMaterialId(@Param("rawMaterialId") Long rawMaterialId);
	
	// Get all raw materials by category
	Page<RawMaterialMasterEntity> findByRawMaterialCatIdAndIsDeleteFalse(Long categoryId, Pageable pageable);

	Page<RawMaterialMasterEntity> findAllByRawMaterialCat_IdAndIsDeleteFalse(Long categoryId, Pageable pageable);

	
	Page<RawMaterialMasterEntity> findByUserIdAndIsDeleteFalse(Long userId, Pageable pageable);

	Page<RawMaterialMasterEntity> findByRawMaterialCatIdAndUserIdAndIsDeleteFalse(Long categoryId, Long userId, Pageable pageable);
	
	// Items that exist in store issue with specific stocktype — all categories
	@Query(value = "SELECT DISTINCT rm.* FROM rawmaterial rm " +
	               "JOIN purchaseorderstoredetails d ON d.raw_material_id = rm.raw_material_id " +
	               "JOIN purchaseorderstore p ON d.storepo_id = p.storepo_id " +
	               "WHERE p.stock_type_id = :stockTypeId " +
	               "AND p.user_Id = :userId " +
	               "AND p.is_delete = 0 " +
	               "AND rm.is_delete = 0",
	       nativeQuery = true,
	       countQuery = "SELECT COUNT(DISTINCT rm.raw_material_id) FROM rawmaterial rm " +
	                    "JOIN purchaseorderstoredetails d ON d.raw_material_id = rm.raw_material_id " +
	                    "JOIN purchaseorderstore p ON d.storepo_id = p.storepo_id " +
	                    "WHERE p.stock_type_id = :stockTypeId " +
	                    "AND p.user_Id = :userId " +
	                    "AND p.is_delete = 0 " +
	                    "AND rm.is_delete = 0")
	Page<RawMaterialMasterEntity> findByStoreIssueStockTypeAndUserId(
	        @Param("stockTypeId") Long stockTypeId,
	        @Param("userId") Long userId,
	        Pageable pageable);

	// Items that exist in store issue with specific stocktype + category
	@Query(value = "SELECT DISTINCT rm.* FROM rawmaterial rm " +
	               "JOIN purchaseorderstoredetails d ON d.raw_material_id = rm.raw_material_id " +
	               "JOIN purchaseorderstore p ON d.storepo_id = p.storepo_id " +
	               "WHERE p.stock_type_id = :stockTypeId " +
	               "AND rm.raw_material_cat_id = :categoryId " +
	               "AND p.user_Id = :userId " +
	               "AND p.is_delete = 0 " +
	               "AND rm.is_delete = 0",
	       nativeQuery = true,
	       countQuery = "SELECT COUNT(DISTINCT rm.raw_material_id) FROM rawmaterial rm " +
	                    "JOIN purchaseorderstoredetails d ON d.raw_material_id = rm.raw_material_id " +
	                    "JOIN purchaseorderstore p ON d.storepo_id = p.storepo_id " +
	                    "WHERE p.stock_type_id = :stockTypeId " +
	                    "AND rm.raw_material_cat_id = :categoryId " +
	                    "AND p.user_Id = :userId " +
	                    "AND p.is_delete = 0 " +
	                    "AND rm.is_delete = 0")
	Page<RawMaterialMasterEntity> findByStoreIssueStockTypeAndCategoryAndUserId(
	        @Param("stockTypeId") Long stockTypeId,
	        @Param("categoryId") Long categoryId,
	        @Param("userId") Long userId,
	        Pageable pageable);
	
	Page<RawMaterialMasterEntity> findByIdInAndUserIdAndIsDeleteFalse(
	        List<Long> ids, Long userId, Pageable pageable);

	Page<RawMaterialMasterEntity> findByIdInAndRawMaterialCatIdAndUserIdAndIsDeleteFalse(
	        List<Long> ids, Long categoryId, Long userId, Pageable pageable);
	
	List<RawMaterialMasterEntity> findByIdInAndRawMaterialCatIdAndUserIdAndIsDeleteFalse(List<Long> ids, Long categoryId, Long userId);

	List<RawMaterialMasterEntity> findByUuidAndUserAndIsDeleteFalse(String uuid,UserMasterEntity userMasterEntity);
	
	@Query(value = " "
			+ " SELECT  "
			+ "		rm.name_english AS raw_material_name_english, "
			+ "		rm.name_gujarati AS raw_material_name_gujarati, "
			+ "		rm.name_hindi AS raw_material_name_hindi, "
			+ "		rm.supplier_rate AS supplier_rate, "
			+ "		rmc.name_english AS raw_material_cat_name_english, "
			+ "		u.name_english AS unit_name_english, "
			+ "		rm.opb_stock AS opb "
			+ "	FROM rawmaterial rm "
			+ "	INNER JOIN raw_material_category rmc  "
			+ "		ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
			+ "		AND rmc.is_delete = FALSE "
			+ "		AND rmc.user_id = :userId "
			+ "	INNER JOIN units u "
			+ "		ON u.unit_id = rm.unit_id "
			+ "		AND u.is_delete = FALSE "
			+ "		AND u.user_id = :userId "
			+ "	WHERE rm.user_id = :userId "
			+ "		AND rm.is_delete = FALSE "
			+ "	ORDER BY rm.sequence ASC "
			+ " ", nativeQuery = true)
	List<Object[]> getRawMaterialExportData(@Param("userId") Long userId);
	
	@Query("SELECT r.id FROM RawMaterialMasterEntity r " +
		       "WHERE r.user.id = :userId " +
		       "AND r.isDelete = false " +
		       "AND r.opbStock IS NOT NULL " +
		       "AND r.opbStock > 0")
		List<Long> findIdsByUserIdAndOpbStockGreaterThanZero(@Param("userId") Long userId);
	
	// With search — no category filter
	Page<RawMaterialMasterEntity> findByIdInAndUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(
	        List<Long> ids, Long userId, String search, Pageable pageable);

	// With search — with category filter
	Page<RawMaterialMasterEntity> findByIdInAndRawMaterialCatIdAndUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(
	        List<Long> ids, Long categoryId, Long userId, String search, Pageable pageable);
	
	Page<RawMaterialMasterEntity>
	findByUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(
	        Long userId,
	        String search,
	        Pageable pageable);

	Page<RawMaterialMasterEntity>
	findByRawMaterialCatIdAndUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(
	        Long categoryId,
	        Long userId,
	        String search,
	        Pageable pageable);

	 @Query("SELECT r "
	            + "FROM RawMaterialMasterEntity r "
	            + "JOIN FETCH r.rawMaterialCat rc "
	            + "LEFT JOIN FETCH r.unit u "
	            + "WHERE r.isDelete = false "
	            + "AND r.isGeneralFix = true "
	            + "AND rc.rawMaterialCatType.id = :categoryTypeId "
	            + "ORDER BY r.sequence ASC")
	    List<RawMaterialMasterEntity> findGeneralFixRawMaterials(
	            @Param("categoryTypeId") Long categoryTypeId
	    );


	    @Query("SELECT r "
	            + "FROM RawMaterialMasterEntity r "
	            + "JOIN FETCH r.rawMaterialCat rc "
	            + "LEFT JOIN FETCH r.unit u "
	            + "WHERE r.isDelete = false "
	            + "AND r.isGeneralFix = true "
	            + "AND rc.rawMaterialCatType.id = :categoryTypeId "
	            + "AND rc.id IN (:rawCatIds) "
	            + "ORDER BY r.sequence ASC")
	    List<RawMaterialMasterEntity> findGeneralFixRawMaterialsByCategory(
	            @Param("categoryTypeId") Long categoryTypeId,
	            @Param("rawCatIds") List<Long> rawCatIds
	    );

		Optional<RawMaterialMasterEntity> findByNameEnglishAndUserIdAndIsDeleteFalse(String rawMaterialName, Long userId);

}
