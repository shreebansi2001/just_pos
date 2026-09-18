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
import com.crmportal.entity.OfficeExpenseEntity;
import com.crmportal.entity.OfficeExpensePayoutHistoryEntity;

@Repository
public interface OfficeExpensePayoutHistoryRepository extends JpaRepository<OfficeExpensePayoutHistoryEntity, Long>{

	List<OfficeExpensePayoutHistoryEntity> findAllByOfficeExpenseAndIsDeleteFalse(OfficeExpenseEntity entity);
	
	Optional<OfficeExpensePayoutHistoryEntity> findByIdAndIsDeleteFalse(Long id);
	
	@Query("SELECT COALESCE(SUM(e.amount),0) FROM OfficeExpensePayoutHistoryEntity e " +
		       "WHERE e.officeExpense = :expense AND e.isDelete = false ")
	BigDecimal getTotalPaidAmountByInvoice(@Param("expense") OfficeExpenseEntity expense);
	
	
	List<OfficeExpensePayoutHistoryEntity> findAllByCashTypeAndIsDeleteFalse(CashAccountEntity cashType);
	
}
