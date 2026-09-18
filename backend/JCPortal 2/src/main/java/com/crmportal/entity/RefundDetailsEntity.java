package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "refund_details")
@AllArgsConstructor
@NoArgsConstructor
public class RefundDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refund_Details_id")
	private Long id;
	
	@Column(name = "refund_payment_mode")
	private String refundPaymentMode;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "refund_date")
	private LocalDate refundDate;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "refund_type")
	private String refundType;
	
	@Column(name = "refund_details")
	private String refundDetails;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "file")
	private String file;
	
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private UserMasterEntity user;
}
