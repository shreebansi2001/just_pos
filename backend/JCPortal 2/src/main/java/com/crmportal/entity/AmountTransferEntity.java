package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.crmportal.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "amount_transfer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountTransferEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transfer_id")
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "from_type")
	private AccountType fromType;

	@Enumerated(EnumType.STRING)
	@Column(name = "to_type")
	private AccountType toType;
	
	@Column(name = "from_account_id")
	private Long fromAccountId;
	
	@Column(name = "to_account_id")
	private Long toAccountId;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "notes")
	private String notes;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
