package com.crmportal.entity;

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

import lombok.Data;

@Entity
@Data
@Table(name = "store_manage_detail")
public class StoreManageDetailEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "store_manage_detail_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_manage_id")
	private StoreManageEntity storeManage;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_id")
	private RawMaterialMasterEntity rawMaterial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_cat_id")
	private RawMaterialCategoryMasterEntity rawMaterialCat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;
	
	@Column(name = "closing_stock")
	private Double closingStock = 0.0;

	@Column(name = "store_qty")
	private Double storeQty = 0.0;

	@Column(name = "increase_qty")
	private Double increaseQty = 0.0;

	@Column(name = "wastage_qty")
	private Double wastageQty = 0.0;

	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "user_id")
	private Long userId;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

}
