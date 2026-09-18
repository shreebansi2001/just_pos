package com.crmportal.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.ExpenseEntity;
import com.crmportal.entity.TripExpensePayoutHistoryEntity;

@Repository
public interface TripExpensePayoutHistoryRepository extends JpaRepository<TripExpensePayoutHistoryEntity, Long> {

	Optional<TripExpensePayoutHistoryEntity> findByIdAndIsDeleteFalse(Long id);
	
	List<TripExpensePayoutHistoryEntity> findAllByExpenseAndIsDeleteFalse(ExpenseEntity expense);
	
	List<TripExpensePayoutHistoryEntity> findByExpenseIdInAndIsDeleteFalse(List<Long> expenseIds);
	
	@Query("SELECT COALESCE(SUM(e.amount),0) FROM TripExpensePayoutHistoryEntity e " +
		       "WHERE e.expense = :expense AND e.isDelete = false ")
	BigDecimal getTotalPaidAmountByInvoice(@Param("expense") ExpenseEntity expense);
	
	
	List<TripExpensePayoutHistoryEntity> findAllByCashTypeAndIsDeleteFalse(CashAccountEntity cashType);
	
}
