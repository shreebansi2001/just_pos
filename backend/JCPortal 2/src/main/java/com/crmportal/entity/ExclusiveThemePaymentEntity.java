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
import org.hibernate.annotations.GeneratorType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exclusive_theme_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExclusiveThemePaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "admin_template_module_id")
	private Long adminTemplateId;

	@Column(name = "is_payment", nullable = false)
	private Boolean isPayment = false;

	@Column(name = "payment_type")
	private String paymentType;

	@Column(name = "price")
	private BigDecimal price;

	@Column(name = "payid")
	private String payid;
	
	@Column(name = "paysignature")
	private String paysignature;
	
	@Column(name = "paymentresponse", length = 15000)
	private String paymentresponse;
	
	@Column(name = "internalorderid")
	private String internalorderid;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

}
