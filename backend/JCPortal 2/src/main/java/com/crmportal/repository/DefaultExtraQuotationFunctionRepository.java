package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.DefaultExtraQuotationFunctionEntity;

@Repository
public interface DefaultExtraQuotationFunctionRepository
		extends JpaRepository<DefaultExtraQuotationFunctionEntity, Long> {

	DefaultExtraQuotationFunctionEntity findByIdAndIsDeleteFalse(Long id);

	boolean existsByNameIgnoreCaseAndUserIdAndIsDeleteFalse(String name, Long id);

	boolean existsByNameIgnoreCaseAndUserIdAndIdNotAndIsDeleteFalse(String name, Long id, Long id2);

	@Query(value = "SELECT * FROM extra_quotation_function eqf " +
	        "WHERE eqf.is_delete = FALSE " +
	        "AND eqf.user_id = :userId " +
	        "AND (:isActive IS NULL OR eqf.is_active = :isActive)", 
	        nativeQuery = true)
	List<DefaultExtraQuotationFunctionEntity> getAll(Long userId, Boolean isActive);

}
