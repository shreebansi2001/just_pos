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
@Table(name = "purchaseorder")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "po_id")
	private Long id;
	
	@Column(name = "pocode", unique = true, nullable = false, length = 30)
	private String pocode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id")
	private PartyMasterEntity supplier;
	
	@Column(name = "voucher", columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String voucher;
	
	@Column(name = "podate")
	private LocalDate podate;
	
	@Column(name = "billno" , columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String billno;
	
	@Column(name = "invoicetype" , columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String invoicetype;
	
	@Column(name = "remarks" , columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarks;
	
	@Column(name = "subamount")
	private float subamount = 0.0f;
	
	@Column(name = "discountper")
	private float discountper = 0.0f;
	
	@Column(name = "discountval")
	private float discountval = 0.0f;
	
	@Column(name = "adjustamount")
	private float adjustamount = 0.0f;
	
	@Column(name = "finalamount")
	private float finalamount = 0.0f;
	
	@Column(name = "potype")
	private int potype = 0;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stock_type_id", nullable = true)
	private StockTypeEntity stocktype;
	
	@Column(name = "is_price_update_master")
	private Boolean priceUpdateMaster;
	
	@Column(name = "grn_number")
	private String grnNumber;
}
