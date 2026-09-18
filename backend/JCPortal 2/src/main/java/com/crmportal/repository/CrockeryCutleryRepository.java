package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CrockeryCutleryEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface CrockeryCutleryRepository extends JpaRepository<CrockeryCutleryEntity, Long> {

	Optional<CrockeryCutleryEntity> findByIdAndIsDeleteFalse(Long id);

	List<CrockeryCutleryEntity> findByRawMaterialCategoryIdAndUserIdAndIsDeleteFalse(Long rawMaterialCatId,
			Long userId);

	Optional<CrockeryCutleryEntity> findByRawMaterialIdAndIsDeleteFalse(Long id);

	CrockeryCutleryEntity findByRawMaterialAndUserAndIsDeleteFalse(RawMaterialMasterEntity entity,
			UserMasterEntity user);

	List<CrockeryCutleryEntity> findAllByRawMaterialAndUserAndIsDeleteFalse(RawMaterialMasterEntity entity,
			UserMasterEntity user);

	void deleteAllByRawMaterialCategory_Id(Long id);

	void deleteByRawMaterial(RawMaterialMasterEntity entity);

	List<CrockeryCutleryEntity> findAllByRawMaterialCategoryAndUserAndIsDeleteFalse(
			RawMaterialCategoryMasterEntity category, UserMasterEntity user);

}
