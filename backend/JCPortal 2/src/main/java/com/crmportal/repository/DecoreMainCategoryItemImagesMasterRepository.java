package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DecoreMainCategoryItemImagesMasterEntity;

@Repository
public interface DecoreMainCategoryItemImagesMasterRepository
		extends JpaRepository<DecoreMainCategoryItemImagesMasterEntity, Long> {

	List<DecoreMainCategoryItemImagesMasterEntity> findAllByDecoreItem_IdAndIsDeleteFalse(Long decoreItemId);

	Optional<DecoreMainCategoryItemImagesMasterEntity> findByIdAndIsDeleteFalse(Long id);
}