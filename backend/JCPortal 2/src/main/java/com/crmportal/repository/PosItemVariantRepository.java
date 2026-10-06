package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PosItemVariantEntity;

@Repository
public interface PosItemVariantRepository extends JpaRepository<PosItemVariantEntity, Long> {
	List<PosItemVariantEntity> findByItemId(Long itemId);

	List<PosItemVariantEntity> findByUserId(Long userId);

	List<PosItemVariantEntity> findByUserIdAndActiveTrue(Long userId);
}
