package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PosCategoryEntity;

@Repository
public interface PosCategoryRepository extends JpaRepository<PosCategoryEntity, Long> {
	List<PosCategoryEntity> findByActiveTrueOrderBySortOrderAsc();

	List<PosCategoryEntity> findAllByOrderBySortOrderAsc();

	// User-wise queries
	List<PosCategoryEntity> findByUserIdOrderBySortOrderAsc(Long userId);

	List<PosCategoryEntity> findByUserIdAndActiveTrueOrderBySortOrderAsc(Long userId);
}
