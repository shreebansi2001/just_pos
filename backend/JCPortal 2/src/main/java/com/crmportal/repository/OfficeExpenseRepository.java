package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.OfficeExpenseEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface OfficeExpenseRepository extends JpaRepository<OfficeExpenseEntity, Long> {

	Optional<OfficeExpenseEntity> findByIdAndIsDeleteFalse(Long id);

	List<OfficeExpenseEntity> findByUserIdAndIsDeleteFalse(Long userId);

//	List<OfficeExpenseEntity> findByExpenseTypeIgnoreCaseAndUserIdAndIsDeleteFalse(String expenseType, Long userId);

//	List<OfficeExpenseEntity> findByExpenseTypeIgnoreCaseAndIsDeleteFalse(String expenseType);

//	List<OfficeExpenseEntity> findByExpenseTypeIgnoreCaseAndIsDeleteFalseAndExpenseDateBetween(String expenseType,
//			LocalDateTime startDateFormated, LocalDateTime endDateFormated);
//
//	List<OfficeExpenseEntity> findByExpenseTypeIgnoreCaseAndUserIdAndIsDeleteFalseAndExpenseDateBetween(
//			String expenseType, Long userId, LocalDateTime startDateFormated, LocalDateTime endDateFormated);
	
//	@Query(" SELECT e FROM OfficeExpenseEntity e "
//			+ " WHERE "
//			+ " UPPER(e.expenseType) = :expenseType "
//			+ " AND (:startDateFormated IS NULL OR e.expenseDate >= :startDateFormated) "
//			+ " AND (:endDateFormated IS NULL OR e.expenseDate <= :endDateFormated) "
//			+ " AND e.isDelete = FALSE ")
//	List<OfficeExpenseEntity> getByExpenseTypeIgnoreCaseAndIsDeleteFalseAndExpenseDateBetween(String expenseType,
//			LocalDateTime startDateFormated, LocalDateTime endDateFormated);

//	@Query(" SELECT e FROM OfficeExpenseEntity e "
//			+ " WHERE "
//			+ " UPPER(e.expenseType) = :expenseType "
//			+ " AND (:startDateFormated IS NULL OR e.expenseDate >= :startDateFormated) "
//			+ " AND (:endDateFormated IS NULL OR e.expenseDate <= :endDateFormated) "
//			+ " AND e.isDelete = FALSE "
//			+ " AND e.user.id = :userId ")
//	List<OfficeExpenseEntity> getByExpenseTypeIgnoreCaseAndUserIdAndIsDeleteFalseAndExpenseDateBetween(
//			String expenseType, Long userId, LocalDateTime startDateFormated, LocalDateTime endDateFormated);
	
	@Query("SELECT e.status FROM OfficeExpensePayoutHistoryEntity e "
			+ " WHERE e.officeExpense.id = :expenseId and e.isDelete = false "
			+ " Order By e.createdAt DESC ")
	List<String> getExpensePaidStatus(@Param("expenseId") Long expenseId);

	@Query(value = ""
			+ "	SELECT   "
			+ "		oe.id AS office_expense_id,  "
			+ "		oe.title AS office_expense_title,  "
			+ "		oe.expense_amount AS expense_amount,  "
			+ "		oe.expense_date AS expense_date,  "
			+ "		oe.paid_date AS paid_date,  "
			+ "		oe.payment_mode AS payment_mode,  "
			+ "		oe.remarks AS remarks,  "
			+ "		oe.account_contact_id AS account_contact_id,  "
			+ "		oe.user_id AS user_id,  "
			+ "		ac.name AS account_contact_name,  "
			+ "		oe.doc_path AS doc_path,  "
			+ "		oe.income_expense_type_id AS income_expense_id,  "
			+ "		iet.name AS income_expense_type_name,  "
			+ "		oph.id AS payout_id,  "
			+ "		oph.bank_account_id AS bank_account_id,  "
			+ "		oph.payment_date AS payout_date,  "
			+ "		oph.transaction_id AS transaction_id,  "
			+ "		oph.cheque_no AS cheque_no,  "
			+ "		oph.amount AS payout_amount,  "
			+ "		oph.due_amount AS payout_due_amount,  "
			+ "		oph.payment_mode AS payout_mode,  "
			+ "		oph.cash_type_id AS cash_account_id,  "
			+ "		oph.status AS payout_status,  "
			+ "		oph.description AS payout_descripion,  "
			+ "		oph.is_delete AS payout_delete,  "
			+ "		oph.created_at AS payout_created_at,  "
			+ "		oph.updated_at AS payout_updated_at "
			+ "		FROM office_expenses oe  "
			+ "	LEFT JOIN office_expense_payout_history oph  "
			+ "		ON oph.office_expense_id = oe.id  "
			+ "		AND oph.is_delete = FALSE  "
			+ "	INNER JOIN account_contact ac   "
			+ "		ON ac.account_contact_id = oe.account_contact_id   "
			+ "		AND ac.is_delete = FALSE  "
			+ "	INNER JOIN income_expense_type iet "
			+ "		ON iet.type_id = oe.income_expense_type_id  "
			+ "		AND iet.is_delete = FALSE  "
			+ "	WHERE (  "
			+ "		(  "
			+ "			ac.user_id = :userId "
			+ "  		AND (:accountContactId = -1 OR oe.account_contact_id = :accountContactId)  "
			+ "			AND EXISTS (  "
			+ "				SELECT 1  "
			+ "				FROM user_basic_details ubd  "
			+ "				WHERE ubd.user_id = :userId  "
			+ "				AND ubd.role_id IN (1, 2)  "
			+ "			)  "
			+ "		)  "
			+ "		OR  "
			+ "		(  "
			+ "			ac.member_id = :userId  "
			+ "			AND NOT EXISTS (  "
			+ "				SELECT 1  	  "
			+ "				FROM user_basic_details ubd  "
			+ "				WHERE ubd.user_id = :userId  "
			+ "				AND ubd.role_id IN (1, 2)  "
			+ "			)  "
			+ "		)  "
			+ "	)  "
			+ "	AND oe.is_delete = FALSE "
			+ " AND (:incomeExpenseTypeId IS NULL OR iet.type_id = :incomeExpenseTypeId) "
			+ " AND (:startDate IS NULL OR oe.expense_date >= :startDate) "
			+ " AND (:endDate IS NULL OR oe.expense_date <= :endDate) ", nativeQuery = true)
	List<Object[]> getOfficeExpenseData(
			@Param("userId") Long userId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			@Param("incomeExpenseTypeId") Long incomeExpenseTypeId,
			@Param("accountContactId") Long accountContactId
	);
}
