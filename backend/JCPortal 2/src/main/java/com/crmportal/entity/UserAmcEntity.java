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

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "user_amc_details")
@AllArgsConstructor
@NoArgsConstructor
public class UserAmcEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "amc_id")
	private Long id;
	
	@Column(name = "amc_type")
	private String amcType;

	@Column(name = "amc_amount")
	private BigDecimal amcAmount;

	@Column(name = "amc_date")
	private LocalDate amcDate;
	
	@Column(name = "amc_remarks")
	private String amcRemarks;

	@Column(name = "amc_recivable_amount")
	private BigDecimal amcRecivableAmount;

	@Column(name = "amc_recivable_date")
	private LocalDate amcRecivableDate;

	@Column(name = "status")
	private String status;

	@Column(name = "file")
	private String file;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private UserMasterEntity user;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
}
