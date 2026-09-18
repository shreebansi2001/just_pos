package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "office_expenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeExpenseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "expense_amount")
	private BigDecimal expenseAmount;
	
//	@Column(name = "remaing_amount")
//	private BigDecimal remaingAmount;
//
//	@Column(name = "payout_amount")
//	private BigDecimal payoutAmount = BigDecimal.ZERO;
	
	@Column(name = "expense_date", columnDefinition = "DATETIME")
	private LocalDateTime expenseDate;
	
	@Column(name = "due_date", columnDefinition = "DATETIME")
	private LocalDateTime dueDate;
	
	@Column(name = "paid_date", columnDefinition = "DATETIME")
	private LocalDateTime paidDate;
	
	@Column(name = "payment_mode")
	private String paymentMode;

	@Column(name = "remarks")
	private String remarks;
	
//	@Column(name = "is_payout")
//	private String isPayout = "unpaid";

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "income_expense_type_id")
	private IncomeExpenseTypeEntity incomeExpenseType;
	
	@Column(name = "account_contact_id")
	private Long accountContactId;
	
	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private UserMasterEntity user;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

}
