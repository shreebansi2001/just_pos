package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.OfficeExpenseDocEntity;
import com.crmportal.entity.OfficeExpenseEntity;

@Repository
public interface OfficeExpenseDocRepository extends JpaRepository<OfficeExpenseDocEntity, Long> {

	Optional<OfficeExpenseDocEntity> findByIdAndIsDeleteFalse(Long moduleRecordId);

	List<OfficeExpenseDocEntity> findAllByOfficeExpenseAndIsDeleteFalse(OfficeExpenseEntity officeExpenseEntity);

	boolean existsByIdAndIsDeleteFalse(Long id);

	@Query(
		    value = "SELECT doc_path FROM office_expense_doc " +
		            "WHERE office_expense_id = :id " +
		            "AND is_delete = FALSE",
		    nativeQuery = true
		)
		List<String> findAllPath(@Param("id") Long id);

}
