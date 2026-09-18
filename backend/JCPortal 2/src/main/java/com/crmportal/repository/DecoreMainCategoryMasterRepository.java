package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface DecoreMainCategoryMasterRepository extends JpaRepository<DecoreMainCategoryMasterEntity, Long> {

	Optional<DecoreMainCategoryMasterEntity> findByUserAndNameEnglishIgnoreCaseAndIsDeleteFalse(UserMasterEntity user,
			String nameEnglish);

	@Query("SELECT MAX(c.sequence) " + "FROM DecoreMainCategoryMasterEntity c " + "WHERE c.user.id = :userId "
			+ "AND c.isDelete = false")
	Integer findMaxSequenceByUserAndIsDeleteFalse(Long userId);

	@Modifying
	@Query("UPDATE DecoreMainCategoryMasterEntity c " + "SET c.sequence = c.sequence + 1 "
			+ "WHERE c.user.id = :userId " + "AND c.isDelete = false " + "AND c.sequence >= :sequence")
	void shiftSequencesForUser(Long userId, Integer sequence);

	Optional<DecoreMainCategoryMasterEntity> findByUserAndSequenceAndIsDeleteFalse(UserMasterEntity user,
			Integer sequence);

	Optional<DecoreMainCategoryMasterEntity> findByIdAndUserAndIsDeleteFalse(Long id, UserMasterEntity user);

	Optional<DecoreMainCategoryMasterEntity> findByIdAndIsDeleteFalse(Long id);

	List<DecoreMainCategoryMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<DecoreMainCategoryMasterEntity> findByUuid(String uuid);

	List<DecoreMainCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(
			String categoryName, UserMasterEntity user);

	List<DecoreMainCategoryMasterEntity> findAllByUserAndIsDeleteFalseAndIsActive(UserMasterEntity user,
			Boolean isActive);

	List<DecoreMainCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(
			String categoryName, UserMasterEntity user, Boolean isActive);

	Optional<DecoreMainCategoryMasterEntity> findByNameEnglishAndUuidAndIsDeleteFalse(String nameEnglish, String uuid);

	boolean existsByNameEnglishAndUserAndIsDeleteFalseAndUuid(String nameEnglish, UserMasterEntity user, String uuid);

	List<DecoreMainCategoryMasterEntity> findAllByUserAndIsDeleteFalseOrderBySequenceAsc(UserMasterEntity user);

	List<DecoreMainCategoryMasterEntity> findAllByUserAndIsDeleteFalseAndIsActiveOrderBySequenceAsc(
			UserMasterEntity user, Boolean isActive);

	List<DecoreMainCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseOrderBySequenceAsc(
			String categoryName, UserMasterEntity user);

	List<DecoreMainCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActiveOrderBySequenceAsc(
			String categoryName, UserMasterEntity user, Boolean isActive);

	List<DecoreMainCategoryMasterEntity> findByUuidAndIsDeleteFalse(String uuid);

	List<DecoreMainCategoryMasterEntity> findByUuidAndUserAndIsDeleteFalse(String uuid, UserMasterEntity user);

}