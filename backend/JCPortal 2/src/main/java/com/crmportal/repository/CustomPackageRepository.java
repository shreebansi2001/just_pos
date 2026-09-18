package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CustomPackageDetailsEntity;
import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface CustomPackageRepository extends JpaRepository<CustomPackageEntity, Long>{


    @Query("SELECT MAX(m.sequence) FROM CustomPackageEntity m WHERE m.user.id = :userId AND m.isDelete = false")
    Integer findMaxSequenceByUserAndIsDeleteFalse(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE CustomPackageEntity m SET m.sequence = m.sequence + 1 " +
           "WHERE m.user.id = :userId AND m.sequence >= :sequence AND m.isDelete = false")
    void shiftSequencesForUser(@Param("userId") Long userId, @Param("sequence") Integer sequence);

	CustomPackageEntity findByIdAndIsDeleteFalse(Long id);

	List<MenuItemMasterEntity> findByUserAndSequenceAndIsDeleteFalse(UserMasterEntity user, Integer sequence);

	Optional<CustomPackageEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	List<CustomPackageEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<CustomPackageEntity> findAllByUserAndIsActiveAndIsDeleteFalse(UserMasterEntity user, Boolean isActive);

	List<CustomPackageEntity> findAllByUserAndNameEnglishContainingIgnoreCaseAndIsDeleteFalse(UserMasterEntity user, String nameEnglish);

	List<CustomPackageEntity> findAllByUserAndNameEnglishContainingIgnoreCaseAndIsActiveAndIsDeleteFalse(UserMasterEntity user, String nameEnglish, Boolean isActive);

	boolean existsByIdAndIsDeleteFalse(Long id);
}
