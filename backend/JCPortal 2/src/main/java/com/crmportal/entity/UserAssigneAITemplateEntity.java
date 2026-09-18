package com.crmportal.entity;

import java.math.BigDecimal;
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
@Table(name = "user_ai_template")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAssigneAITemplateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_ai_template_id")
	private Long id;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "ai_template_id")
	private Long aiTemplateId;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "start_date", columnDefinition = "DATETIME")
	private LocalDateTime startDate;

	@Column(name = "end_date", columnDefinition = "DATETIME")
	private LocalDateTime endDate;
	
	@Column(name = "is_pay_done", nullable = false)
	private Boolean isPayDone = false;
	
	@Column(name = "pay_amount")
	private BigDecimal payAmount = BigDecimal.ZERO;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

}
