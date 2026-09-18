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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_plan_histories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPlansHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plan_history_id")
	private Long id;

	@Column(name = "start_date", columnDefinition = "DATETIME")
	private LocalDateTime startDate;

	@Column(name = "end_date", columnDefinition = "DATETIME")
	private LocalDateTime endDate;

	@Column(name = "is_active")
	private Boolean isActive = true;
	
	@Column(name = "discount_price")
	private BigDecimal discountPrice = BigDecimal.ZERO;
	
	@Column(name = "cgst")
	private String cgst;
	
	@Column(name = "sgst")
	private String sgst;

	@Column(name = "cgst_amt")
	private BigDecimal cgstAmt = BigDecimal.ZERO;

	@Column(name = "sgst_amt")
	private BigDecimal sgstAmt = BigDecimal.ZERO;
	
	@Column(name = "extra_amount")
	private String extraAmount;
	
	@Column(name = "plan_amount")
	private String planAmount;
	
	@Column(name = "plan_base_amount")
	private String planBaseAmount;
	
	@Column(name = "total_price")
	private BigDecimal totalPrice = BigDecimal.ZERO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id", nullable = true)
	private PlansEntity plan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coupen_id")
	private CoupenEntity coupen;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "extra_pay_id")
	private ExtraPaymentEntity extraPay;
	
	@Column(name = "paymentdone")
	private Boolean paymentdone;
	
}
