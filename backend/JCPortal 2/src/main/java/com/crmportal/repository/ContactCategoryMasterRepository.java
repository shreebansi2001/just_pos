package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface ContactCategoryMasterRepository extends JpaRepository<ContactCategoryMasterEntity, Long>{

	Optional<ContactCategoryMasterEntity> findByNameEnglishAndIsDeleteFalse(String nameEnglish);

	Optional<ContactCategoryMasterEntity> findByIdAndIsDeleteFalse(long id);

	List<ContactCategoryMasterEntity> findAllByIsDeleteFalse();

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<ContactCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndIsDeleteFalse(String categoryName);

	Optional<ContactCategoryMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish,
			UserMasterEntity user);

	List<ContactCategoryMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	@Query("SELECT r FROM ContactCategoryMasterEntity r " + "WHERE r.isDelete = false " + "AND r.user = :user "
			+ "AND (:categoryName IS NULL OR LOWER(r.nameEnglish) LIKE LOWER(CONCAT('%', :categoryName, '%')) "
			+ "OR LOWER(r.contactType.nameEnglish) LIKE LOWER(CONCAT('%', :categoryName, '%')))")
	List<ContactCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String categoryName,
			UserMasterEntity user);

	 @Query("SELECT MAX(c.sequence) FROM ContactCategoryMasterEntity c WHERE c.user.id = :userId AND c.isDelete = false")
	    Integer findMaxSequenceByUserAndIsDeleteFalse(@Param("userId") Long userId);

	    @Modifying
	    @Transactional
	    @Query("UPDATE ContactCategoryMasterEntity c SET c.sequence = c.sequence + 1 " +
	           "WHERE c.user.id = :userId AND c.sequence >= :sequence AND c.isDelete = false")
	    void shiftSequencesForUser(@Param("userId") Long userId, @Param("sequence") Integer sequence);

		List<ContactCategoryMasterEntity> findByUserAndSequenceAndIsDeleteFalse(UserMasterEntity user,
				Integer sequence);

		List<ContactCategoryMasterEntity> findAllByUserAndContactTypeAndIsDeleteFalse(UserMasterEntity user,
				ContactTypeMasterEntity contactType);

		List<ContactCategoryMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndContactTypeAndIsDeleteFalse(
				String categoryName, UserMasterEntity user, ContactTypeMasterEntity contactType);
		
		Optional<ContactCategoryMasterEntity> findFirstByUserIdAndContactTypeIdAndIsDeleteFalseOrderBySequenceAsc(
	            Long userId,
	            Long contactTypeId);

}
