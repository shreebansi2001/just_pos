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
import org.hibernate.annotations.Fetch;
import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "from_city_id")
	private Long fromCityId;

	@Column(name = "to_city_id")
	private Long toCityId;
	
	@Column(name = "from_date", columnDefinition = "DATETIME")
	private LocalDateTime fromDate;
	
	@Column(name = "to_date", columnDefinition = "DATETIME")
	private LocalDateTime toDate;
	
	@Column(name = "due_date", columnDefinition = "DATETIME")
	private LocalDateTime dueDate;

	@Column(name = "paid_date", columnDefinition = "DATETIME")
	private LocalDateTime paidDate;
	
	@Column(name = "total_amount")
	private BigDecimal totalAmount;

//	@Column(name = "remaing_amount")
//	private BigDecimal remaingAmount;
//
//	@Column(name = "payout_amount")
//	private BigDecimal payoutAmount = BigDecimal.ZERO;

	@Column(name = "expense_type")
	private String expenseType;
	
	@Column(name = "remark")
	private String remark;
	
//	@Column(name = "is_payout")
//	private String isPayout = "unpaid";
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@Column(name = "account_contact_id")
	private Long accountContactId;
	
	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private UserMasterEntity user;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
}
