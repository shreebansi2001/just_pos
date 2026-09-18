package com.crmportal.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "event_invoice")
@Data
public class EventInvoiceEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invoice_id")
	private Long id;

	@Column(name = "total_amount", precision = 10, scale = 2)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(name = "cgst")
	private String cgst;

	@Column(name = "cgst_amnt", precision = 10, scale = 2)
	private BigDecimal cgstAmnt = BigDecimal.ZERO;

	@Column(name = "sgst")
	private String sgst;

	@Column(name = "sgst_amnt", precision = 10, scale = 2)
	private BigDecimal sgstAmnt = BigDecimal.ZERO;

	@Column(name = "igst")
	private String igst;

	@Column(name = "igst_amnt", precision = 10, scale = 2)
	private BigDecimal igstAmnt = BigDecimal.ZERO;

	@Column(name = "discount")
	private BigDecimal discount = BigDecimal.ZERO;

	@Column(name = "round_off")
	private BigInteger roundOff = BigInteger.ZERO;

	@Column(name = "grand_total")
	private BigInteger grandTotal = BigInteger.ZERO;

	@Column(name = "sub_total")
	private BigInteger subTotal = BigInteger.ZERO;

	@Column(name = "remaining_amount")
	private BigInteger remainingAmount = BigInteger.ZERO;

	@Column(name = "notes")
	private String notes;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "invoice_code", unique = true)
	private String invoiceCode;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;

//	@OneToMany(mappedBy = "eventInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<EventInvoiceFunctionItemEntity> functionInvoiceItems = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;

	@Column(name = "gstnumber")
	private String gstnumber;

	@Column(name = "billingname")
	private String billingname;

	@Column(name = "billingaddress")
	private String billingaddress;

	@Column(name = "shipname")
	private String shipname;

	@Column(name = "shipaddress")
	private String shipaddress;

	@Column(name = "duedate")
	private LocalDate duedate;

	@Column(name = "cash_payment", precision = 10, scale = 2, nullable = false)
	private BigDecimal cashPayment = BigDecimal.ZERO;

	@Column(name = "cheque_payment", precision = 10, scale = 2, nullable = false)
	private BigDecimal chequePayment = BigDecimal.ZERO;

	@Column(name = "food_tax")
	private String foodTax;

	@Column(name = "food_tax_amount")
	private BigInteger foodTaxAmount = BigInteger.ZERO;

	@Column(name = "food_tax_total_amount")
	private BigInteger foodTaxTotalAmount = BigInteger.ZERO;

	@Column(name = "service_tax")
	private String serviceTax;

	@Column(name = "service_tax_amount")
	private BigInteger serviceTaxAmount = BigInteger.ZERO;

	@Column(name = "service_tax_total_amount")
	private BigInteger serviceTaxTotalAmount = BigInteger.ZERO;

	@Column(name = "vat_tax")
	private String vatTax;

	@Column(name = "vat_tax_amount")
	private BigInteger vatTaxAmount = BigInteger.ZERO;

	@Column(name = "vat_tax_total_amount")
	private BigInteger vatTaxTotalAmount = BigInteger.ZERO;

	@Column(name = "discount_percentage")
	private String discountPct;
	
	@Column(name = "is_discount_percent")
	private Boolean isDiscountPercent;
	
}
