package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_advance_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAdvancePaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_advance_payment_id")
	private Long id;
	
	@Column(name = "payment_date")
	private LocalDate paymentDate;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "payment_mode")
	private String paymentMode;
	
	@Column(name = "entry_by")
	private Long entryBy;
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "reference_id")
	private String referenceId;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "event_id")
	private Long eventId;
	
	@Column(name = "bank_id")
	private Long bankId;
	
	@Column(name = "cash_id")
	private Long cashId;
	
	@Column(name = "event_function_id")
	private Long eventFunctionId;
	
	@Column(name = "banquet_id")
	private Long banquetHallId;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updateAt;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
}
