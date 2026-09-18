package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UnitRangeEntity;

@Repository
public interface UnitRangeRepository extends JpaRepository<UnitRangeEntity, Long>{

	List<UnitRangeEntity> findAllByUnitAndIsDeleteFalse(UnitMasterEntity entity);

	List<UnitRangeEntity> findAllByUuid(String uuid);

	List<UnitRangeEntity> findAllByUuidAndIsDeleteFalse(String oldUuid);

	@Query(value = " "
			+ " SELECT   "
			+ "		ur.max_value,  "
			+ "		ur.min_value,  "
			+ "		ur.round_off_value,  "
			+ "		u.name_english,  "
			+ "		ur.range_type  "
			+ "	FROM unit_ranges ur  "
			+ "	INNER JOIN units u  "
			+ "		ON u.unit_id = ur.unit_id  "
			+ "		AND u.user_id = :userId  "
			+ "	WHERE "
			+ "	ur.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getUnitRangeExportData(@Param("userId") Long userId);
}
