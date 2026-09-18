package com.crmportal.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.MenuCategoryForMenuReportResponseDto;

@Repository
public interface MenuCategoryMasterRepository extends JpaRepository<MenuCategoryMasterEntity, Long> {

	Optional<MenuCategoryMasterEntity> findByUserAndNameEnglishIgnoreCaseAndIsDeleteFalse(UserMasterEntity user,
			String nameEnglish);

	@Query("SELECT MAX(c.sequence) FROM MenuCategoryMasterEntity c WHERE c.user.id = :userId AND c.isDelete = false")
	Integer findMaxSequenceByUserAndIsDeleteFalse(Long userId);

	@Modifying
	@Query("UPDATE MenuCategoryMasterEntity c SET c.sequence = c.sequence + 1 "
			+ "WHERE c.user.id = :userId AND c.isDelete = false AND c.sequence >= :sequence")
	void shiftSequencesForUser(Long userId, Integer sequence);

	Optional<MenuCategoryMasterEntity> findByUserAndSequenceAndIsDeleteFalse(UserMasterEntity user, Integer sequence);

	Optional<MenuCategoryMasterEntity> findByIdAndUserAndIsDeleteFalse(Long id, UserMasterEntity user);

	Optional<MenuCategoryMasterEntity> findByIdAndIsDeleteFalse(Long moduleId);

	List<MenuCategoryMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);
	
	List<MenuCategoryMasterEntity> findByUuid(String uuid);

	List<MenuCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String menuCategoryName,
			UserMasterEntity user);

	List<MenuCategoryMasterEntity> findAllByUserAndIsDeleteFalseAndIsActive(UserMasterEntity user, Boolean isActive);

	List<MenuCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(
			String menuCategoryName, UserMasterEntity user, Boolean isActive);

	Optional<MenuCategoryMasterEntity> findByNameEnglishAndUuidAndIsDeleteFalse(String nameEnglish,
			String uuid);

	@Query("SELECT DISTINCT new com.crmportal.response.dto.MenuCategoryForMenuReportResponseDto(mp.menuCategory.id , m.nameEnglish, m.nameHindi, m.nameGujarati, m.imagePath, mp.menuSlogan, mp.menuNotes) "
			+ "FROM com.crmportal.entity.MenuPreparationDetailsEntity mp "
			+ "JOIN com.crmportal.entity.MenuCategoryMasterEntity m ON mp.menuCategory.id = m.id "
			+ "WHERE mp.menuPreparation.id = :menuPreparationId")
	List<MenuCategoryForMenuReportResponseDto> findByMenuPreparationId(Long menuPreparationId);

	boolean existsByNameEnglishAndUserAndIsDeleteFalseAndUuid(String nameEnglish, UserMasterEntity userMasterEntity,
			String uuid);

	List<MenuCategoryMasterEntity> findAllByUserAndIsDeleteFalseOrderBySequenceAsc(UserMasterEntity user);

	List<MenuCategoryMasterEntity> findAllByUserAndIsDeleteFalseAndIsActiveOrderBySequenceAsc(UserMasterEntity user,
			Boolean isActive);

	List<MenuCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseOrderBySequenceAsc(
			String menuCategoryName, UserMasterEntity user);

	List<MenuCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActiveOrderBySequenceAsc(
			String menuCategoryName, UserMasterEntity user, Boolean isActive);

	List<MenuCategoryMasterEntity> findByUuidAndIsDeleteFalse(String oldUuid);

	List<MenuCategoryMasterEntity> findByUuidAndUserAndIsDeleteFalse(String uuid, UserMasterEntity userMasterEntity);
	
	@Query(value = " "
			+ " SELECT  "
			+ "		mc.name_english, "
			+ "		mc.name_hindi, "
			+ "		mc.name_gujarati, "
			+ "		mc.menuslogan "
			+ " FROM menucategory mc "
			+ " WHERE mc.is_delete = FALSE "
			+ " 	AND mc.user_id = :userId "
			+ " ORDER BY mc.sequence ASC ", nativeQuery = true)
	List<Object[]> getMenuCategoryExportData(@Param("userId") Long userId);
}
