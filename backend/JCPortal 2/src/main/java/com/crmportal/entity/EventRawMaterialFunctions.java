package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_raw_material_functions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRawMaterialFunctions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_raw_material_function_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_raw_material_id")
	private EventRawMaterialEntity eventRawMaterial;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "function_master_id")
	private FunctionMasterEntity function;
	
	@ManyToOne
    @JoinColumn(name = "event_function_id", nullable = false)
    private EventFunctionMasterEntity eventFunction;
	
	@Column(name = "qty")
	private Double qty=0.0;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "item_name")
	String itemName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private PartyMasterEntity supplier;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;
	
	@Column(name = "place")
	private String place;
	
	@Column(name = "price")
	private Double price=0.0;
	
	@Column(name = "rawmaterial_price")
	private Double rawMaterialPrice=0.0;
	
	@Column(name = "functiondatetime")
	String functiondatetime;
	
	@Column(name = "is_extra_field")
	private Boolean isExtraField = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_cat_id")
	private RawMaterialCategoryMasterEntity rawMaterialCat;
	
	@Column(name = "menuitemid")
	private Long menuitemid;
	

}
