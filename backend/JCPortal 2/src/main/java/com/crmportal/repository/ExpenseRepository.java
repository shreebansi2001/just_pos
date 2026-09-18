package com.crmportal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ExpenseEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

	Optional<ExpenseEntity> findByIdAndIsDeleteFalse(Long id);

	List<ExpenseEntity> findByUserIdAndIsDeleteFalse(Long userId);

	List<ExpenseEntity> findByExpenseTypeIgnoreCaseAndUserIdAndIsDeleteFalse(String expenseType, Long userId);

	List<ExpenseEntity> findByExpenseTypeIgnoreCaseAndIsDeleteFalse(String expenseType);

//	List<ExpenseEntity> findByExpenseTypeIgnoreCaseAndIsDeleteFalseAndFromDateBetween(String expenseType,
//			LocalDateTime startDateFormated, LocalDateTime endDateFormated);

//	List<ExpenseEntity> findByExpenseTypeIgnoreCaseAndUserIdAndIsDeleteFalseAndFromDateBetween(String expenseType,
//			Long userId, LocalDateTime startDateFormated, LocalDateTime endDateFormated);
	
	@Query(" SELECT e FROM ExpenseEntity e "
			+ " WHERE "
			+ " UPPER(e.expenseType) = :expenseType "
			+ " AND (:startDateFormated IS NULL OR e.fromDate >= :startDateFormated) "
			+ " AND (:endDateFormated IS NULL OR e.fromDate <= :endDateFormated) "
			+ " AND e.isDelete = FALSE ")
	List<ExpenseEntity> getByExpenseTypeIgnoreCaseAndIsDeleteFalseAndFromDateBetween(String expenseType,
			LocalDateTime startDateFormated, LocalDateTime endDateFormated);
	
	@Query(" SELECT e FROM ExpenseEntity e "
			+ " WHERE "
			+ " UPPER(e.expenseType) = :expenseType "
			+ " AND (:startDateFormated IS NULL OR e.fromDate >= :startDateFormated) "
			+ " AND (:endDateFormated IS NULL OR e.fromDate <= :endDateFormated) "
			+ " AND e.isDelete = FALSE"
			+ " AND e.user.id = :userId ")
	List<ExpenseEntity> getByExpenseTypeIgnoreCaseAndUserIdAndIsDeleteFalseAndFromDateBetween(String expenseType,
			Long userId, LocalDateTime startDateFormated, LocalDateTime endDateFormated);
	
	@Query("SELECT e.status FROM TripExpensePayoutHistoryEntity e "
			+ " WHERE e.expense.id = :expenseId and e.isDelete = false "
			+ " Order By e.createdAt DESC ")
	List<String> getExpensePaidStatus(@Param("expenseId") Long expenseId);
	
	@Query(value = ""
			+ " SELECT "
			+ " 	e.id AS trip_expense_id, "	
			+ " 	e.title AS trip_expense_title, " 				
			+ " 	e.from_city_id AS from_city_id, "
			+ " 	e.to_city_id AS to_city_id, "
			+ " 	e.from_date AS from_date, "
			+ " 	e.to_date AS to_date,"
			+ " 	e.due_date AS due_date,"
			+ " 	e.paid_date AS paid_date,"
			+ " 	e.total_amount AS total_amount,"
			+ " 	e.expense_type AS expense_type,"
			+ " 	e.remark AS remark,"
			+ "	  	e.account_contact_id AS account_contact_id, "
			+ " 	ac.name AS account_contact_name,"
			+ " 	e.user_id AS user_id,"
			+ " 	ed.id AS trip_detail_id,"
			+ " 	ed.expense_date AS expense_date,"
			+ " 	ed.perticular AS perticulars,"
			+ " 	ed.payment_mode AS payment_mode,"
			+ " 	ed.amount AS amount,"
			+ " 	ed.remarks AS detail_remark, "
			+ " 	ed.km AS km,"
			+ " 	ed.doc_path AS doc_path,"
			+ " 	tph.id AS payout_id,"
			+ " 	tph.bank_account_id AS bank_account_id,"
			+ " 	tph.payment_date AS payout_date,"
			+ " 	tph.transaction_id AS transaction_id,"
			+ " 	tph.cheque_no AS cheque_no,"
			+ " 	tph.amount AS payout_amount,"
			+ " 	tph.due_amount AS payout_due_amount,"
			+ " 	tph.payment_mode AS payout_payment_mode,"
			+ " 	tph.cash_type_id AS cash_account_id,"
			+ " 	tph.status AS payout_status,"
			+ " 	tph.description AS payout_description,"
			+ " 	tph.is_delete AS payout_delete,"
			+ " 	tph.created_at AS payout_created_at, "
			+ " 	tph.updated_at AS payout_updated_at "
			+ " FROM expenses e  "
			+ "	LEFT JOIN expense_details ed   "
			+ "		ON e.id = ed.expense_id   "
			+ "		AND ed.is_delete = FALSE  "
			+ "	LEFT JOIN trip_expense_payout_history tph   "
			+ "		ON tph.trip_expense_id = e.id   "
			+ "		AND tph.is_delete = FALSE  "
			+ "	INNER JOIN account_contact ac   "
			+ "		ON ac.account_contact_id = e.account_contact_id   "
			+ "		AND ac.is_delete = FALSE  "
			+ "	WHERE (  "
			+ "		(  "
			+ "			ac.user_id = :userId"
			+ "			AND (:accountContactId = -1 OR e.account_contact_id = :accountContactId) "
			+ "			AND EXISTS(  "
			+ "				SELECT 1  "
			+ "				FROM user_basic_details ubd  "
			+ "				WHERE ubd.user_id = :userId  "
			+ "				AND ubd.role_id IN (1, 2)  "
			+ "			)  "
			+ "		)  "
			+ "		OR  "
			+ "		(  "
			+ "			ac.member_id = :userId  "
			+ "			AND NOT EXISTS(  "
			+ "				SELECT 1  "
			+ "				FROM user_basic_details ubd  "
			+ "				WHERE ubd.user_id = :userId  "
			+ "				AND ubd.role_id IN (1, 2)  "
			+ "			)  "
			+ "		)  "
			+ "	)"
			+ " AND e.is_delete = FALSE"
			+ " AND (:startDate IS NULL OR e.from_date >= :startDate) "
			+ " AND (:endDate IS NULL OR e.from_date <= :endDate) ", nativeQuery = true)
	List<Object[]> getExpenseByUserId(
			@Param("userId") Long userId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			@Param("accountContactId") Long accountContactId
	);
	
}
