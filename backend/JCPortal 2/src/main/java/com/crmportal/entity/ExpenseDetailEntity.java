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
@Table(name = "expense_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDetailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "expense_date", columnDefinition = "DATETIME")
	private LocalDateTime expenseDate;
	
	@Column(name = "perticular")
	private String perticular;

	@Column(name = "payment_mode")
	private String paymentMode;
	
	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "km")
	private Long km;
	
	@Column(name = "doc_path")
	private String docPath;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@JoinColumn(name = "expense_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ExpenseEntity expense;
	
	@Column(name = "account_contact_id")
	private Long accountContactId;
	
	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private UserMasterEntity user;
}
