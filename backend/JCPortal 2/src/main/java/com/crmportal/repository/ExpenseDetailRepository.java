package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ExpenseDetailEntity;
import com.crmportal.entity.TripExpensePayoutHistoryEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface ExpenseDetailRepository extends JpaRepository<ExpenseDetailEntity, Long> {

	Optional<ExpenseDetailEntity> findByIdAndIsDeleteFalse(Long id);

	List<ExpenseDetailEntity> findByExpenseIdAndIsDeleteFalse(Long expenseId);
	
	List<ExpenseDetailEntity> findByExpenseIdInAndIsDeleteFalse(List<Long> expenseIds);

}
