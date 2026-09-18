package com.crmportal.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface UnitMasterRepository extends JpaRepository<UnitMasterEntity, Long> {

	Optional<UnitMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	Optional<UnitMasterEntity> findByIdAndIsDeleteFalse(long id);

	List<UnitMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<UnitMasterEntity> findByUserAndIsActiveAndIsDeleteFalse(UserMasterEntity user, Boolean isActive);

	List<UnitMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String nameEnglish,
			UserMasterEntity user);

	List<UnitMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsActiveAndIsDeleteFalse(String nameEnglish,
			UserMasterEntity user, Boolean isActive);

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<UnitMasterEntity> findBySymbolEnglishAndIsDeleteFalse(String symbolEnglish);

	UnitMasterEntity findByIdAndIsParentUnitTrue(Long parentId);

	List<UnitMasterEntity> findAllByParentUnitAndIsDeleteFalseAndIsActiveTrue(UnitMasterEntity parent);

	UnitMasterEntity findByIdAndIsDeleteFalseAndIsActiveTrue(Long unitId);

	List<UnitMasterEntity> findByUuid(String oldUuid);

	List<UnitMasterEntity> findBySymbolEnglishAndUuidAndIsDeleteFalse(String symbolEnglish, String uuid);

	Optional<UnitMasterEntity> findByIdAndIsParentUnitFalse(Long id);

	@Query("SELECT u " + "FROM UnitMasterEntity u " + "LEFT JOIN FETCH u.parentUnit pu "
			+ "LEFT JOIN FETCH u.children c " + "WHERE u.id = :unitId " + "   OR pu.id = :unitId")
	List<UnitMasterEntity> findParentWithChildren(@Param("unitId") Long unitId);

	List<UnitMasterEntity> findAllByIdInAndIsDeleteFalseAndIsActiveTrue(Set<Long> ids);

	Optional<UnitMasterEntity> findByNameEnglishAndUserAndIsDeleteFalseAndUuid(String parentUnitName,
			UserMasterEntity userMasterEntity, String uuid);

	boolean existsByNameEnglishAndUserAndIsDeleteFalseAndUuid(String nameEnglish, UserMasterEntity userMasterEntity,
			String uuid);

	List<UnitMasterEntity> findByUuidAndIsDeleteFalse(String oldUuid);
	
	UnitMasterEntity findByParentUnitIdAndIsDeleteFalse(Long parentUnitId);

	List<UnitMasterEntity> findByUuidAndUserAndIsDeleteFalse(String uuid, UserMasterEntity userMasterEntity);

	@Query(value = " "
			+ " SELECT   "
			+ "		u1.name_english AS unit_name_english,  "
			+ "		u1.symbol_english AS unit_symbol_english,  "
			+ "		u1.name_hindi AS unit_name_hindi,  "
			+ "		u1.symbol_hindi AS unit_symbol_hindi,  "
			+ "		u1.name_gujarati AS unit_name_gujarati,  "
			+ "		u1.symbol_gujarati AS symbol_gujarati,  "
			+ "		u1.is_parent_unit AS is_parent_unit,  "
			+ "		u2.name_english AS parent_unit_name_english,"
			+ "		u1.equivalent_value AS equivalent_value, "
			+ "		u1.decimal_limit AS decimal_limit,"
			+ " 	u1.range_type AS range_type  "
			+ "	FROM units u1  "
			+ "	LEFT JOIN units u2  "
			+ "		ON u2.parent_unit_id = u1.unit_id  "
			+ "		AND u2.is_delete = FALSE  "
			+ "		AND u2.user_id = :userId  "
			+ "	WHERE u1.is_delete = FALSE  "
			+ "	AND u1.user_id = :userId ", nativeQuery = true)
	List<Object[]> getUnitExportData(@Param("userId") Long userId);

	@Query(" " +
		   " SELECT u.nameEnglish "+
		  "  FROM UnitMasterEntity u " +
		   " WHERE u.user = :user " +
		    "  AND u.isDelete = false ")
		List<String> findAllActiveNamesByUser(
		        @Param("user") UserMasterEntity user
		);
}
