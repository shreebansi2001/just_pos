package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UnitStepwiseRangeEntity;

@Repository
public interface UnitStepwiseRangeRepository extends JpaRepository<UnitStepwiseRangeEntity, Long>{

	UnitStepwiseRangeEntity findByUnitAndIsDeleteFalse(UnitMasterEntity entity);

	UnitStepwiseRangeEntity findAllByUnit(UnitMasterEntity unit);

	List<UnitStepwiseRangeEntity> findAllByUuid(String uuid);

	List<UnitStepwiseRangeEntity> findAllByUuidAndIsDeleteFalse(String oldUuid);

	@Query(value = " "
			+ "	SELECT  "
			+ "		usr.step_value, "
			+ "		u.name_english "
			+ "	FROM unit_stepwise_ranges usr "
			+ "	INNER JOIN units u "
			+ "		ON u.unit_id = usr.unit_id "
			+ "		AND u.user_id = :userId "
			+ "	WHERE "
			+ "	usr.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getUnitStepWiseRangeExportData(@Param("userId") Long userId);
}
