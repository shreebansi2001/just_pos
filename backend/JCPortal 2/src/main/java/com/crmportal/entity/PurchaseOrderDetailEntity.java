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
@Table(name = "purchaseorderdetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderDetailEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "podetail_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "po_id")
	private PurchaseOrderEntity po;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_id")
	private RawMaterialMasterEntity rawMaterial;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_cat_id")
	private RawMaterialCategoryMasterEntity rawMaterialCat;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;
	
	@Column(name = "hsccode", columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String hsccode;
	
	@Column(name = "cgst")
	private float cgst = 0.0f;
	
	@Column(name = "sgst")
	private float sgst = 0.0f;
	
	@Column(name = "igst")
	private float igst = 0.0f;
	
	@Column(name = "cess")
	private float cess = 0.0f;
	
	@Column(name = "qty")
	private double qty = 0.0;
	
	@Column(name = "price")
	private float price = 0.0f;
	
	@Column(name = "othercharge")
	private float othercharge;
	
	@Column(name = "total")
	private float total;
	
	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "is_add_in_stock",nullable = false)
	private Boolean isAddInStock = false;
	
}
