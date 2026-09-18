package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.TermsAndConditionFeaturesEntity;

@Repository
public interface TermsAndConditionFeaturesRepository extends JpaRepository<TermsAndConditionFeaturesEntity, Long>{

	void deleteAllByUserTermsConditionId(Long id);

	List<TermsAndConditionFeaturesEntity> findByUserTermsConditionIdAndIsDeleteFalse(Long id);
	
	@Query(value = "SELECT tcf.* FROM terms_condition_features tcf " +
            "JOIN user_terms_conditions utc ON utc.terms_id = tcf.user_term_condition_id " +
            "WHERE utc.user_id = :userId " +
            "AND tcf.is_delete = false " +
            "AND utc.is_delete = false " +
            "AND utc.is_active = true " +
            "ORDER BY tcf.tcf_id ASC",
    nativeQuery = true)
	List<TermsAndConditionFeaturesEntity> findActiveTermsByUserId(@Param("userId") Long userId);

}
