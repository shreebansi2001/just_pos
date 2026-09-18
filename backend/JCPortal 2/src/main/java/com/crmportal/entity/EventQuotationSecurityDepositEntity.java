package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_quotation_security_deposit")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventQuotationSecurityDepositEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_quotation_security_deposit_id")
	private Long id;
	
	@Column(name = "event_id", nullable = false)
	private Long eventId;
	
	@Column(name = "quotation_id", nullable = false)
	private Long quotationId;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "cash_account_id")
	private Long cashAccountId;
	
	@Column(name = "bank_account_id")
	private Long bankAccountId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode")
	private PaymentMode paymentMode;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "payment_date_time", columnDefinition = "DATETIME")
	private LocalDateTime paymentDateTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "entry_type")
	private EntryType entryType;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
}
