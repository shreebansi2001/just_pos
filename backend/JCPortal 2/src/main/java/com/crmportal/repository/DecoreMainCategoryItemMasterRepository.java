package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DecoreMainCategoryItemMasterEntity;
import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface DecoreMainCategoryItemMasterRepository
		extends JpaRepository<DecoreMainCategoryItemMasterEntity, Long> {

	Optional<DecoreMainCategoryItemMasterEntity> findByIdAndIsDeleteFalse(Long id);

	List<DecoreMainCategoryItemMasterEntity> findByUserAndIsDeleteFalse(UserMasterEntity user);

	List<DecoreMainCategoryItemMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish,
			UserMasterEntity user);

	boolean existsByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	Page<DecoreMainCategoryItemMasterEntity> findAllByUser_IdAndIsDeleteFalse(Long userId, Pageable pageable);

	@Query("SELECT d FROM DecoreMainCategoryItemMasterEntity d " + "WHERE d.isDelete = false "
			+ "AND d.user.id = :userId "
			+ "AND (:itemName IS NULL OR LOWER(d.nameEnglish) LIKE LOWER(CONCAT('%', :itemName, '%'))) "
			+ "AND (:categoryId IS NULL OR d.decoreMainCategory.id = :categoryId) "
			+ "AND (:isActive IS NULL OR d.isActive = :isActive)")
	Page<DecoreMainCategoryItemMasterEntity> search(@Param("userId") Long userId, @Param("itemName") String itemName,
			@Param("categoryId") Long categoryId, @Param("isActive") Boolean isActive, Pageable pageable);
	

	@Query("SELECT MAX(i.sequence) FROM DecoreMainCategoryItemMasterEntity i "
			+ "WHERE i.decoreMainCategory.id = :categoryId " + "AND i.isDelete = false")
	Integer findMaxSequenceByCategory(@Param("categoryId") Long categoryId);

	@Modifying
	@Query("UPDATE DecoreMainCategoryItemMasterEntity i " + "SET i.sequence = i.sequence + 1 "
			+ "WHERE i.decoreMainCategory.id = :categoryId " + "AND i.sequence >= :sequence "
			+ "AND i.isDelete = false")
	void shiftSequencesForCategory(@Param("categoryId") Long categoryId, @Param("sequence") Integer sequence);

	Optional<DecoreMainCategoryItemMasterEntity> findByDecoreMainCategoryAndSequenceAndIsDeleteFalse(
			DecoreMainCategoryMasterEntity category, Integer newSequence);

}