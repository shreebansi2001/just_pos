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
@Table(name = "bank_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "bank_name")
	private String bankName;
	
	@Column(name = "branch_name")
	private String branchName;
	
	@Column(name = "account_holder_name")
	private String accountHolderName;
	
	@Column(name = "account_no")
	private String accountNo;
	
	@Column(name = "ifsc_code")
	private String ifscCode;
	
	@Column(name = "upi_id")
	private String upiId;
	
	@Column(name = "is_primary")
	private Boolean isPrimary;

	@Column(name = "opening_balance")
	private BigDecimal openingBalance = BigDecimal.ZERO;
	
	@Column(name = "current_balance")
	private BigDecimal currentBalance = BigDecimal.ZERO;
	
	@Column(name = "opening_date")
	private LocalDate openingDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserMasterEntity user;
	
	@Column(name = "qr_code_path")
	private String qrCode;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
