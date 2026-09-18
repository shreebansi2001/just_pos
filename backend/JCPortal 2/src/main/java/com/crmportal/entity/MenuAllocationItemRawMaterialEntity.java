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

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menuallocation_item_rawmaterial")
@Data
public class MenuAllocationItemRawMaterialEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menuallocation_item_rawmaterial_id")
	private Long id;
	
	@Column(name = "weight")
	private BigDecimal weight = BigDecimal.ZERO;
	
	@Column(name = "rate")
	private BigDecimal rate = BigDecimal.ZERO;
	
	@Column(name = "rawmaterial_weight")
	private BigDecimal rawMaterialWeight = BigDecimal.ZERO;
	
	@Column(name = "rawmaterial_rate")
	private BigDecimal rawmaterial_rate = BigDecimal.ZERO;
	
	@Column(name = "date_time",columnDefinition = "DATETIME")
	private LocalDateTime dateTime;
	
	@Column(name = "place")
	private String place;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = true)
    private PartyMasterEntity party;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = true)
    private UnitMasterEntity unit;
	
	@Column(name = "sup_rate",nullable = false)
	private BigDecimal supRate = BigDecimal.ZERO;

	@Column(name = "masterraw_unitid")
	private Long masterRawUnitId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_id")
	private RawMaterialMasterEntity rawMaterial;
	
	@ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItemMasterEntity menuItem;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventMasterEntity event;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventfunction_id", nullable = false)
    private EventFunctionMasterEntity eventFunction;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_new_raw", nullable = false)
	private Boolean isNewRaw = false;
}
