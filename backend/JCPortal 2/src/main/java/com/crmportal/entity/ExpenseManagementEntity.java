package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.EExpense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expense_management")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseManagementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long expenseId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "mobile_no")
	private String mobileNo;

	@Column(name = "payment_type")
	private String paymentType;

	@Column(name = "remark")
	private String remark;
	
	@Column(name = "description")
	private String description;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "country_code")
	private String countryCode;
	
	@Column(name = "gstin")
	private String gstin;

	@Column(name = "building_address")
	private String buildingAddress;

	@Column(name = "area")
	private String area;

	@Column(name = "pincode")
	private String pincode;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;
	
	@Column(name = "document", nullable = true)
	private String document;
	
	@Column(name = "doc_path", nullable = true)
	private String docPath;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_type")
	private EExpense userType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private EventMasterEntity event;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = true)
	private RoleMasterEntity role;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private PartyMasterEntity party;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id")
	private UserMasterEntity manager;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserMasterEntity user;
	
}
