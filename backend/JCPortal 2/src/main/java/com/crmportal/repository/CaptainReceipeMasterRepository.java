package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CaptainReceipeMasterEntity;

@Repository
public interface CaptainReceipeMasterRepository extends JpaRepository<CaptainReceipeMasterEntity, Long> {

	List<CaptainReceipeMasterEntity> findAllByUserIdAndIsDeleteFalse(Long userId);
	
	@Query(" SELECT crm "
			+ " FROM CaptainReceipeMasterEntity crm "
			+ " WHERE crm.isDelete = FALSE"
			+ " AND (:status IS NULL OR crm.isActive = :status) "
			+ " AND crm.userId = :userId ")
	List<CaptainReceipeMasterEntity> findAllByUserId(Long userId, Boolean status);
	
	Optional<CaptainReceipeMasterEntity> findByIdAndIsDeleteFalse(Long id);
	
	@Query(value = " "
			+ " SELECT "
			+ "		crm.name AS captain_receipe_name, "
			+ "		crm.weight AS weight, "
			+ "		crm.rate AS rate, "
			+ "		u.name_english AS unit_name "
			+ "	FROM captain_receipe_master crm "
			+ "	LEFT JOIN units u "
			+ "		ON u.unit_id = crm.unit_id "
			+ "	WHERE crm.user_id = :userId "
			+ "	AND crm.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getCaptainReceipeMasterExportData(@Param("userId") Long userId);
}
