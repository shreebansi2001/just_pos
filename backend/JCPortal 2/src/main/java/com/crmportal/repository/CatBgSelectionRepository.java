package com.crmportal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.crmportal.entity.CatBgSelectionEntity;

public interface CatBgSelectionRepository extends JpaRepository<CatBgSelectionEntity, Long> {

	List<CatBgSelectionEntity> findByUserIdAndIsDeleteFalse(Long userId);

	List<CatBgSelectionEntity> findByUserIdAndIsCatImgAndIsDeleteFalse(Long userId, Boolean isCatImg);

	Optional<CatBgSelectionEntity> findByIdAndIsDeleteFalse(Long id);
}