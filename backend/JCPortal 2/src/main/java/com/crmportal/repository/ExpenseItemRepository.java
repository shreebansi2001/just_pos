package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ExpenseItemEntity;

@Repository
public interface ExpenseItemRepository extends JpaRepository<ExpenseItemEntity, Long> {

	Optional<ExpenseItemEntity> findByExpenseItemIdAndIsDeleteFalse(Long expenseItemId);

	List<ExpenseItemEntity> findByExpense_ExpenseIdAndEventIdAndIsDeleteFalse(Long expenseId, Long eventId);

	@Query(value = "SELECT SUM(amount) FROM expense_items WHERE user_type = 'MANAGER' AND event_id = :eventId AND user_id = :userId ", nativeQuery = true)
	Long findTotalManagerUsedAmount(@Param("eventId") Long eventId, @Param("userId") Long userId);

}
