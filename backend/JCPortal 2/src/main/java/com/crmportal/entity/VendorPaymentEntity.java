package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vendor_payments")
public class VendorPaymentEntity {

	@Id
	@GeneratedValue
	@Column(name = "vendor_pay_id")
	private Long id;

	@Column(name = "payment_date")
	private LocalDate paymentDate;

	@Column(name = "payment_mode")
	private String paymentMode;

	@Column(name = "bank_id", nullable = true)
	private Long bankId;

	@Column(name = "cash_account_id")
	private Long cashAccountId;
	
	@Column(name = "reference_id")
	private String referenceId;

	@Column(name = "pay_amount")
	private BigDecimal payAmount = BigDecimal.ZERO;

	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "vendor_id")
	private Long vendorId;

	@Column(name = "vendor_cat")
	private String vendorCat;

	@Column(name = "invoice_code")
	private String invoiceCode;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "is_delete")
	private Boolean isDelete = false;

	@Column(name = "is_payable")
	private Boolean isPayable;
	
	@Column(name = "isOpb", nullable = false)
	private Boolean isOpb = false;

	@Column(name = "received_amount")
	private BigDecimal receivedAmount = BigDecimal.ZERO;
	
	@Column(name = "settlement_amount")
	private BigDecimal settlementAmount  = BigDecimal.ZERO;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserMasterEntity user;

}
