package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
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
@Table(name = "cash_account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashAccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "account_name")
	private String accountName;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_type_id")
	private ContactTypeMasterEntity contactType;

	@Column(name = "opening_balance")
	private BigDecimal openingBalance = BigDecimal.ZERO;

	@Column(name = "current_balance")
	private BigDecimal currentBalance = BigDecimal.ZERO;

	@Column(name = "description")
	private String description;

	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "is_primary")
	private Boolean isPrimary = false;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

}
