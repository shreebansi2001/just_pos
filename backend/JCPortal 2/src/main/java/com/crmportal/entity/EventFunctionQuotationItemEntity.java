package com.crmportal.entity;

import java.math.BigDecimal;
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
@Table(name = "quotation_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionQuotationItemEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "quotation_item_id")
	private Long id;
	
	@Column(name = "function_name",columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String functionName;
	
	@Column(name = "event_function_id",nullable = true)
	private Long eventFunctionId;	
	
	@Column(name = "function_date", columnDefinition = "DATETIME")
	private LocalDateTime functionDate;
	
	@Column(name = "pax")
	private Integer pax;
	
	@Column(name = "extra_pax")
	private Integer extraPax;
	
	@Column(name = "rate_per_plate",precision = 10, scale = 2)
	private BigDecimal ratePerPlate = BigDecimal.ZERO;
	
	@Column(name = "offered_rate",precision = 10, scale = 2)
	private BigDecimal offeredRate = BigDecimal.ZERO;
	
	@Column(name = "amount",precision = 10, scale = 2)
	private BigDecimal amount = BigDecimal.ZERO;
	
	@Column(name = "extra_tax", precision = 10, scale = 2)
	private BigDecimal extraTax = BigDecimal.ZERO;

	@Column(name = "tax_rate", precision = 10, scale = 2)
	private BigDecimal taxRate = BigDecimal.ZERO;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "isEventFunction", nullable = false)
	private Boolean isEventFunction;
	
	@Column(name = "default_function_id",nullable = true)
	private Long defaultFunctionId;	
	
	@Column(name = "isAddons",nullable = false)
	private Boolean isAddons = false;	

	@Column(name = "menu_cat_id",nullable = true)
	private Long menuCatId;	

	@Column(name = "item_id",nullable = true)
	private Long itemId;	
	
	@Column(name = "options", columnDefinition = "VARCHAR(255) DEFAULT ''")
	private String options;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quotation_id")
	private EventFunctionQuotationEntity eventFunctionQuotation;
	
	@Column(name = "custom_package_id")
	private Long customPackageId;

	@Column(name = "custom_package_name")
	private String customPackageName;

	@Column(name = "custom_package_price")
	private BigDecimal customPackagePrice;
	
	@Column(name = "is_locked")
	private Boolean isLocked = false;
	
	@Column(name = "extra_charges_id")
	private Long extraChargesId;

	@Column(name = "is_extra_charges", nullable = false)
	private Boolean isExtraCharges = false;
}
