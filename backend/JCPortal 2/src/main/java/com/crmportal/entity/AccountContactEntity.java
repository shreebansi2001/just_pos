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

import com.crmportal.enums.EntryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_contact")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountContactEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_contact_id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "opening_balance")
	private BigDecimal openingBalance;
	
	@Column(name = "current_balance")
	private BigDecimal currentBalance;
	
	@Column(name = "opening_date")
	private LocalDate openingDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "entry_type")
	private EntryType entryType;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "member_id")
	private Long memberId;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
