package com.crmportal.entity;

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
@Table(name = "purchaseorderstore")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderStoreEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "storepo_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cr_id", nullable = true)
	private ChefRequisitionEntity chefRequisition;  // nullable — default null
	
	@Column(name = "crcode", nullable = true, length = 30)
	private String crcode; 
	
	@Column(name = "pocode", unique = true, nullable = false, length = 30)
	private String pocode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id", nullable = true)
	private PartyMasterEntity party;
	
	@Column(name = "voucher", columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String voucher;
	
	@Column(name = "podate")
	private LocalDate podate;
	
	@Column(name = "invoicetype" , columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String invoicetype;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stock_type_id")
	private StockTypeEntity stocktype;
	
	@Column(name = "remarks" , columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarks;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;
	
	@Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
	private String status = "PENDING";
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kitchen_type_id")
	private StockTypeEntity kitchentype;
	
	@Column(name = "eventId")
	private Long eventId;
}
