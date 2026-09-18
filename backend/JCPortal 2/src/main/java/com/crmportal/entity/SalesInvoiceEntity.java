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

import com.crmportal.enums.AccountType;
import com.crmportal.response.dto.SalesInvoiceResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_invoice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesInvoiceEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "invoice_no")
	private String invoiceNo;
	
	@Column(name = "payment_date", columnDefinition = "DATETIME")
	private LocalDateTime paymentDate;
	
	@Column(name = "invoice_amount")
	private BigDecimal invoiceAmount;
	
	@Column(name = "due_amount")
	private BigDecimal dueAmount;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "total_amount")
	private BigDecimal totalAmount;
	
	@Column(name = "payment_mode")
	private String paymentMode;
	
	@Column(name = "reference")
	private String reference;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type")
	private AccountType accountType;
	
	@Column(name = "bank_id")
	private Long bankId;
	
	@Column(name = "cash_id")
	private Long cashId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserMasterEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
