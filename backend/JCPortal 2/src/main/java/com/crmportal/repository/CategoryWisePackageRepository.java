package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CategoryWisePackageEntity;
import com.crmportal.enums.CategoryWisePackageType;

@Repository
public interface CategoryWisePackageRepository extends JpaRepository<CategoryWisePackageEntity, Long>{

	void deleteAllByUserId(Long userId);

	List<CategoryWisePackageEntity> findAllByUserId(Long userId);

	List<CategoryWisePackageEntity> findAllByUserIdAndMenuCategory_IdAndType(Long userId, Long menuCategoryId,
			CategoryWisePackageType type);

}
