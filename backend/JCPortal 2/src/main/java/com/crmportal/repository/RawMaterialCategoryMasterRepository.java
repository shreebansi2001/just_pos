package com.crmportal.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialCategoryTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface RawMaterialCategoryMasterRepository extends JpaRepository<RawMaterialCategoryMasterEntity, Long> {

	Optional<RawMaterialCategoryMasterEntity> findByIdAndIsDeleteFalse(Long userId);

	Optional<RawMaterialCategoryMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish,
			UserMasterEntity user);

	RawMaterialCategoryMasterEntity findByIdAndUserAndIsDeleteFalse(Long id, UserMasterEntity user);

	List<RawMaterialCategoryMasterEntity> findAllByUserAndRawMaterialCatTypeAndIsDeleteFalse(UserMasterEntity user,
			RawMaterialCategoryTypeMasterEntity rawMaterialCategoryTypeMasterEntity);

	List<RawMaterialCategoryMasterEntity> findAllByUserAndRawMaterialCatTypeAndIsDeleteFalseAndIsActive(
			UserMasterEntity user, RawMaterialCategoryTypeMasterEntity rawMaterialCategoryTypeMasterEntity,
			Boolean isActive);

	List<RawMaterialCategoryMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);
	
	List<RawMaterialCategoryMasterEntity> findByUuid(String uuid);

	List<RawMaterialCategoryMasterEntity> findAllByUserAndIsDeleteFalseAndIsActive(UserMasterEntity user,
			Boolean isActive);

	@Query("SELECT r FROM RawMaterialCategoryMasterEntity r " + "WHERE r.isDelete = false " + "AND r.user = :user "
			+ "AND (:type IS NULL OR r.rawMaterialCatType = :type) "
			+ "AND (:isActive IS NULL OR r.isActive = :isActive) "
			+ "AND (:name IS NULL OR LOWER(r.nameEnglish) LIKE LOWER(CONCAT('%', :name, '%')) "
			+ "OR LOWER(r.rawMaterialCatType.nameEnglish) LIKE LOWER(CONCAT('%', :name, '%')))")
	List<RawMaterialCategoryMasterEntity> searchByNameOrType(@Param("user") UserMasterEntity user,
			@Param("type") RawMaterialCategoryTypeMasterEntity type, @Param("isActive") Boolean isActive,
			@Param("name") String name);

	boolean existsByIdAndIsDeleteFalse(Long id);

	@Query("SELECT MAX(c.sequence) FROM RawMaterialCategoryMasterEntity c WHERE c.user.id = :userId AND c.isDelete = false")
	Integer findMaxSequenceByUserAndIsDeleteFalse(@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query("UPDATE RawMaterialCategoryMasterEntity c SET c.sequence = c.sequence + 1 "
			+ "WHERE c.user.id = :userId AND c.sequence >= :sequence AND c.isDelete = false")
	void shiftSequencesForUser(@Param("userId") Long userId, @Param("sequence") Integer sequence);

	List<RawMaterialCategoryMasterEntity> findByUserAndSequenceAndIsDeleteFalse(UserMasterEntity user,
			Integer sequence);

	@Query(value = "SELECT DISTINCT rmc.raw_matrial_cat_id, rmc.name_english, rmc.name_hindi, rmc.name_gujarati, rmc.sequence "
			+ "FROM menupreparationdetails mpd "
			+ "INNER JOIN menu_item_raw_material mrw ON mpd.menu_item_id = mrw.menu_item_id "
			+ "INNER JOIN rawmaterial rm ON mrw.raw_material_id = rm.raw_material_id "
			+ "INNER JOIN raw_material_category rmc ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
			+ "INNER JOIN menuallocation_item_rawmaterial mir ON mrw.menu_item_id = mir.menu_item_id "
			+ "LEFT JOIN menupreparation mp ON mpd.menu_preparation_id = mp.menu_preparation_id "
			+ "LEFT JOIN event_function ef ON mp.event_function_id = ef.event_function_id "
			+ "WHERE ef.event_id = :eventId AND mir.event_id = :eventId AND mrw.is_delete = FALSE ORDER BY rmc.sequence IS NULL, rmc.sequence", nativeQuery = true)
	List<Object[]> getRawMaterialCategoryByEventIdAndUserId(Long eventId);

	Optional<RawMaterialCategoryMasterEntity> findByNameEnglishAndUuidAndIsDeleteFalse(String nameEnglish, String uuid);

	List<RawMaterialCategoryMasterEntity> findByRawMaterialCatTypeIdAndIsDeleteFalse(Long rawMaterialCategoryTypeId);

	@Query(value =  " SELECT  " 
			+ " 	data.raw_matrial_cat_id, " 
			+ " 	data.name_english, " 
			+ " 	data.name_hindi, " 
			+ " 	data.name_gujarati, " 
			+ " 	data.sequence " 
			+ " FROM ( " 
			+ " 	SELECT DISTINCT " 
			+ " 	    rmc.raw_matrial_cat_id, " 
			+ " 	    rmc.name_english, " 
			+ " 	    rmc.name_hindi, " 
			+ " 	    rmc.name_gujarati, " 
			+ " 	    rmc.sequence " 
			+ " 	FROM menuallocation_item_rawmaterial ma " 
			+ " 	INNER JOIN rawmaterial rm " 
			+ " 	    ON ma.raw_material_id = rm.raw_material_id " 
			+ " 	INNER JOIN raw_material_category rmc " 
			+ " 	    ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id " 
			+ " 	WHERE ma.event_id = :eventId " 
			+ " 	  AND ma.is_delete = FALSE " 
			+ "  " 
			+ " 	UNION  " 
			+ "  " 
			+ " 	SELECT DISTINCT " 
			+ " 	    rmc.raw_matrial_cat_id, " 
			+ " 	    rmc.name_english, " 
			+ " 	    rmc.name_hindi, " 
			+ " 	    rmc.name_gujarati, " 
			+ " 	    rmc.sequence " 
			+ " 	FROM menu_allocation_item_captain_receipe maicr " 
			+ " 	INNER JOIN memuitems mi " 
			+ " 		ON mi.menu_item_id = maicr.menu_item_id " 
			+ " 	INNER JOIN menu_item_captain_receipe micr " 
			+ " 		ON micr.menu_item_id = mi.menu_item_id " 
			+ " 	INNER JOIN captain_receipe_master crm " 
			+ " 		ON crm.id = micr.captain_receipe_id " 
			+ " 	INNER JOIN captain_receipe_raw_material crrm " 
			+ " 		ON crrm.captain_receipe_id = crm.id " 
			+ " 	INNER JOIN rawmaterial rm " 
			+ " 		ON crrm.raw_item_id = rm.raw_material_id " 
			+ " 	INNER JOIN raw_material_category rmc " 
			+ " 		ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id " 
			+ " 	WHERE " 
			+ " 		maicr.event_id = :eventId " 
			+ " 		AND maicr.is_delete = FALSE " 
			+ " ) AS `data` " 
			+ " ORDER BY " 
			+ "     data.sequence IS NULL, " 
			+ "     data.sequence; " ,
	        nativeQuery = true)
	List<Object[]> getRawMaterialCategoryFromAllocationByEventId(Long eventId);

	@Query(value = "SELECT DISTINCT rmc.raw_matrial_cat_id, rmc.name_english, rmc.name_hindi, rmc.name_gujarati, rmc.sequence "
	        + "FROM event_raw_material erm "
	        + "INNER JOIN rawmaterial rm ON erm.raw_material_id = rm.raw_material_id "
	        + "INNER JOIN raw_material_category rmc ON rm.raw_material_cat_id = rmc.raw_matrial_cat_id "
	        + "WHERE erm.event_id = :eventId "
	        + "AND erm.is_delete = FALSE "
	        + "ORDER BY rmc.sequence IS NULL, rmc.sequence",
	        nativeQuery = true)
	List<Object[]> getRawMaterialCategoryFromRawMaterialByEventIdAndUserId(Long eventId);

	boolean existsByNameEnglishAndUserAndIsDeleteFalseAndUuid(String nameEnglish, UserMasterEntity userMasterEntity,
			String uuid);

	List<RawMaterialCategoryMasterEntity> findByRawMaterialCatTypeIdAndUserAndIsDeleteFalse(
			Long rawMaterialCategoryTypeId, UserMasterEntity user);

	List<RawMaterialCategoryMasterEntity> findByUuidAndIsDeleteFalse(String oldUuid);

	List<RawMaterialCategoryMasterEntity> findByUuidAndUserAndIsDeleteFalse(String uuid, UserMasterEntity userMasterEntity);
	
	@Query(value = " "
			+ " SELECT  "
			+ "		rmc.name_english AS cat_name_english, "
			+ "		rmc.name_gujarati AS cat_name_gujarati, "
			+ "		rmc.name_hindi AS cat_name_hindi, "
			+ "		rmct.name_english AS cat_type_english "
			+ "	FROM raw_material_category rmc "
			+ "	INNER JOIN raw_material_category_type rmct  "
			+ "		ON rmc.raw_material_cat_type_id = rmct.raw_material_cat_type_id  "
			+ "		AND rmct.is_delete = FALSE  "
			+ "	WHERE rmc.user_id = :userId "
			+ "		AND rmc.is_delete = FALSE "
			+ "	ORDER BY rmc.sequence ASC ", nativeQuery = true)
	List<Object[]> getRawMaterialCatExportData(@Param("userId") Long userId);

	@Modifying
	@Query(
	    "UPDATE RawMaterialCategoryMasterEntity r " +
	    "SET r.sequence = r.sequence + 1 " +
	    "WHERE r.user.id = :userId " +
	    "AND r.isDelete = false " +
	    "AND r.sequence >= :sequence"
	)
	int shiftSequencesForInsert(
	        @Param("userId") Long userId,
	        @Param("sequence") Integer sequence
	);

	Optional<RawMaterialCategoryMasterEntity> findByUserIdAndSequenceAndIsDeleteFalse(Long id,
			Integer requestedSequence);
}
