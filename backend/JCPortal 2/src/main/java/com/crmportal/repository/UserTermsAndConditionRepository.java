package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserTermsAndConditionEntity;

@Repository
public interface UserTermsAndConditionRepository extends JpaRepository<UserTermsAndConditionEntity, Long> {

	Optional<UserTermsAndConditionEntity> findByIdAndIsDeleteFalse(Long id);

	@Query("SELECT u, tcf FROM UserTermsAndConditionEntity u " +
		       "LEFT JOIN TermsAndConditionFeaturesEntity tcf " +
		       "ON u.id = tcf.userTermsConditionId AND tcf.isDelete = false " +
		       "WHERE u.user.id = :userId " +
		       "AND (:moduleName IS NULL OR u.nameEnglish = :moduleName) " +
		       "AND (:isActive IS NULL OR u.isActive = :isActive) " +
		       "AND u.isDelete = false")
		List<Object[]> findAllWithFeatures(
		        @Param("userId") Long userId,
		        @Param("moduleName") String moduleName,
		        @Param("isActive") Boolean isActive);

	UserTermsAndConditionEntity findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(Long userid, String string);

}
