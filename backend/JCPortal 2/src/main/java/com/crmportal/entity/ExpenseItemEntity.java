package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.microsoft.schemas.compatibility.AlternateContentDocument.AlternateContent.Fallback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.crmportal.enums.EExpense;

@Entity
@Table(name = "expense_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long expenseItemId;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "item_purchase_date", columnDefinition = "DATETIME")
	private LocalDateTime itemPurchaseDate;
	
	@Column(name = "payment_type")
	private String paymentType;
	
	@Column(name = "remarks")
	private String remarks;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "user_type")
	private EExpense userType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expense_id", nullable = false)
	private ExpenseManagementEntity expense;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private EventMasterEntity event;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserMasterEntity user;
	
	@Column(name = "supplier_id")
	private Long supplierId;
}
