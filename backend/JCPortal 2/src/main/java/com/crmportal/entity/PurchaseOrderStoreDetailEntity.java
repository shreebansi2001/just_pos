package com.crmportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchaseorderstoredetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderStoreDetailEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "storepodetail_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "storepo_id")
	private PurchaseOrderStoreEntity po;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_id")
	private RawMaterialMasterEntity rawMaterial;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_cat_id")
	private RawMaterialCategoryMasterEntity rawMaterialCat;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;
	
	@Column(name = "qty")
	private double qty = 0;
	
	@Column(name = "closing_stock")
	private Double closingStock = 0.0;
	
	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "is_add_in_stock",nullable = false)
	private Boolean isAddInStock = false;
	
}
