package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DecorePackageEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface DecorePackageRepository extends JpaRepository<DecorePackageEntity, Long> {

	@Query("SELECT MAX(d.sequence) FROM DecorePackageEntity d " + "WHERE d.user.id = :userId AND d.isDelete = false")
	Integer findMaxSequenceByUserAndIsDeleteFalse(@Param("userId") Long userId);

	@Modifying
	@Query("UPDATE DecorePackageEntity d SET d.sequence = d.sequence + 1 "
			+ "WHERE d.user.id = :userId AND d.sequence >= :sequence AND d.isDelete = false")
	void shiftSequencesForUser(@Param("userId") Long userId, @Param("sequence") Integer sequence);

	@Query("SELECT d FROM DecorePackageEntity d " + "WHERE d.id = :id AND d.isDelete = false")
	Optional<DecorePackageEntity> findActiveById(@Param("id") Long id);

	Optional<DecorePackageEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	List<DecorePackageEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<DecorePackageEntity> findAllByUserAndIsActiveAndIsDeleteFalse(UserMasterEntity user, Boolean isActive);

	List<DecorePackageEntity> findAllByUserAndNameEnglishContainingIgnoreCaseAndIsDeleteFalse(UserMasterEntity user,
			String nameEnglish);

	List<DecorePackageEntity> findAllByUserAndNameEnglishContainingIgnoreCaseAndIsActiveAndIsDeleteFalse(
			UserMasterEntity user, String nameEnglish, Boolean isActive);

	boolean existsByIdAndIsDeleteFalse(Long id);

	@Query("SELECT d FROM DecorePackageEntity d " + "WHERE d.id = :id AND d.isDelete = false")
	DecorePackageEntity findByIdAndIsDeleteFalse(@Param("id") Long id);
}