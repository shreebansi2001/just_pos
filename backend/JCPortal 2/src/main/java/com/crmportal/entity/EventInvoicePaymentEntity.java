package com.crmportal.entity;

import java.math.BigInteger;
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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_invoice_payment")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventInvoicePaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invoice_payment_id")
	private Long id;
	
	@Column(name = "advance_payment_date", columnDefinition = "DATETIME")
	private LocalDateTime advancePaymentDate;
	
	@Column(name = "advance_payment_notes")
	private String advancePaymentNotes;

	@Column(name = "advance_payment")
	private BigInteger advancePayment = BigInteger.ZERO;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_id")
	private EventInvoiceEntity eventInvoice;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;
	
	@Column(name = "bank_id", nullable = true)
	private Long bankId;

	@Column(name = "cash_account_id",nullable = true)
	private Long cashAccountId;
	
	@Column(name = "payment_mode")
	private String paymentMode;

	@Column(name = "vendor_code")
	private String vendorCode;

}
