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
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.Data;

@Entity
@Table(name = "quotations")
@Data
public class EventFunctionQuotationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "quotation_id")
	private Long id;

	@Column(name = "total_amount", precision = 10, scale = 2)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(name = "cash_payment", precision = 10, scale = 2, nullable = false)
	private BigDecimal cashPayment = BigDecimal.ZERO;

	@Column(name = "cheque_payment", precision = 10, scale = 2, nullable = false)
	private BigDecimal chequePayment = BigDecimal.ZERO;

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

	@Column(name = "notes", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String notes;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "is_extra_function", nullable = false)
	private Boolean isExtraFunction = true;

	@Column(name = "quotation_code")
	private String quotationCode;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;
//	
//	@OneToMany(mappedBy = "eventFunctionQuotation", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<EventFunctionQuotationItemEntity> functionQuotationItems = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;

	@Column(name = "gstnumber")
	private String gstnumber;

	@Column(name = "billingname")
	private String billingname;

	@Column(name = "duedate")
	private LocalDate duedate;

	@Column(name = "quotationdate")
	private LocalDate quotationdate;

	@Column(name = "pooja_rooms", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String poojaRooms;

	@Column(name = "iron_service", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String ironService;

	@Column(name = "venue_remark", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String venueRemark;

	@Column(name = "venue_total")
	private BigInteger venueTotal = BigInteger.ZERO;

	@Column(name = "is_locked")
	private Boolean isLocked = false;

	@Column(name = "is_decore", nullable = false)
	private Boolean isDecore = false;

	@Column(name = "transportation")
	private BigInteger transportation = BigInteger.ZERO;

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
