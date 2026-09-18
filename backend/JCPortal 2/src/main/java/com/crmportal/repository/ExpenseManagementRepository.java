package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ExpenseManagementEntity;
import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.enums.EExpense;

@Repository
public interface ExpenseManagementRepository extends JpaRepository<ExpenseManagementEntity, Long> {

	Optional<ExpenseManagementEntity> findByExpenseIdAndIsDeleteFalse(Long id);

	List<ExpenseManagementEntity> findAllByPartyIdAndIsDeleteFalse(Long partyId);

	Optional<ExpenseManagementEntity> findByExpenseIdAndPartyIdAndIsDeleteFalse(Long id, Long partyId);

	Optional<ExpenseManagementEntity> findByExpenseIdAndPartyIdAndEventIdAndIsDeleteFalse(Long expenseId, Long partyId,
			Long eventId);

	List<ExpenseManagementEntity> findAllByEventIdAndUserIdAndIsDeleteFalse(Long eventId,
			Long userId);

	List<ExpenseManagementEntity> findByUserTypeAndEventIdAndUserIdAndIsDeleteFalse(
			EExpense eUserType, Long eventId, Long userId);

	
	@Query(value = "SELECT SUM(amount) FROM expense_management WHERE user_type = :userType AND event_id = :eventId AND user_id = :userId AND is_delete = false", nativeQuery = true)
	Long findTotalExpenseByUserType(@Param("userType") String userType, @Param("eventId") Long eventId, @Param("userId") Long userId);

}
