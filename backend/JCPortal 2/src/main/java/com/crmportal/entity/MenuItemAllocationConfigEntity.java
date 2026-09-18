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
@Table(name = "menu_item_allocation_config")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuItemAllocationConfigEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_item_allocation_config_id")
	private Long id;
	
	@Column(name = "godown_location")
	private String godownLocation;
	
	@Column(name = "select_outside_agency")
	private Boolean selectOutsideAgency = Boolean.FALSE;
	
	@Column(name = "select_chef_labour_agency")
	private Boolean selectChefLabourAgency = Boolean.FALSE;
	
	@Column(name = "select_inside_agency")
	private Boolean selectInsideAgency = Boolean.FALSE;
	
	@Column(name = "allocation_type")
	private String allocationType;
	
	@Column(name = "quantity_per_100_person", precision = 10, scale = 2)
	private BigDecimal quantityPer100Person = BigDecimal.ZERO;
	
	@Column(name = "base_price", precision = 10, scale = 2)
	private BigDecimal basePrice = BigDecimal.ZERO;
	
	@Column(name = "counter_no")
	private Integer counterNo;
	
	@Column(name = "helper_no")
	private Integer helperNo;
	
	@Column(name = "price_per_labour", precision = 10, scale = 2)
	private BigDecimal pricePerLabour = BigDecimal.ZERO;
	
	@Column(name = "price_per_helper", precision = 10, scale = 2)
	private BigDecimal pricePerHelper = BigDecimal.ZERO;

	@Column(name = "notes")
	private String notes;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "number")
	private String number;
	
	@ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItemMasterEntity menuItem ;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private PartyMasterEntity party;
	

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_category_id")
    private ContactCategoryMasterEntity contact;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;
	
	
	
	
}
