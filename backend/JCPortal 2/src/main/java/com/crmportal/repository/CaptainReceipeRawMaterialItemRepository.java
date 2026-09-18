package com.crmportal.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CaptainReceipeRawMaterialItemEntity;

@Repository
public interface CaptainReceipeRawMaterialItemRepository extends JpaRepository<CaptainReceipeRawMaterialItemEntity, Long> {

	List<CaptainReceipeRawMaterialItemEntity> findByCaptainReceipeIdInAndIsDeleteFalse(Set<Long> captainReceipeIds);
	
	List<CaptainReceipeRawMaterialItemEntity> findByCaptainReceipeIdAndIsDeleteFalse(Long captainReceipeId);
	
	List<CaptainReceipeRawMaterialItemEntity> findAllByCaptainReceipeIdInAndIsDeleteFalse(List<Long> captReceipeIds);
	
	@Query(value = " "
			+ " SELECT "
			+ "		crm.name AS captain_receipe_name, "
			+ "		rm.name_english AS raw_material_name, "
			+ "		crrm.qty AS qty, "
			+ "		u.name_english AS unit_name "
			+ "	FROM captain_receipe_raw_material crrm "
			+ "	LEFT JOIN captain_receipe_master crm "
			+ "		ON crm.id = crrm.captain_receipe_id "
			+ "	LEFT JOIN units u "
			+ "		ON u.unit_id = crrm.unit_id "
			+ "	LEFT JOIN rawmaterial rm "
			+ "		ON rm.raw_material_id = crrm.raw_item_id "
			+ "	WHERE crm.user_id = :userId "
			+ "	AND crrm.is_delete = FALSE "
			+ "	AND crm.is_delete = FALSE ", nativeQuery = true)
	List<Object[]> getCaptainReceipeRawMaterialExportData(@Param("userId") Long userId);
}
