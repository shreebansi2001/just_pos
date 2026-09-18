package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Table(name = "rawmaterial")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RawMaterialMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "raw_material_id")
	private Long id;

	@Column(name = "name_english")
	private String nameEnglish;

	@Column(name = "name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;

	@Column(name = "name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;

	@Column(name = "supplier_rate")
	private BigDecimal supplierRate = BigDecimal.ZERO;

	@Column(name = "daily_consumption")
	private String dailyConsumption;

	@Column(name = "isGeneralFix")
	private Boolean isGeneralFix;

	@Column(name = "isPublished", nullable = false)
	private Boolean isPublished = true;

	@Column(name = "weight_per_100_pax")
	private BigDecimal weightPer100Pax = BigDecimal.ZERO;

	@Column(name = "sequence")
	private Integer sequence;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;

	@Column(name = "file")
	private String file;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_cat_id")
	private RawMaterialCategoryMasterEntity rawMaterialCat;

	@Column(name = "opb_stock", nullable = false)
	private BigDecimal opbStock = BigDecimal.ZERO;

	@Column(name = "min_stock", nullable = false)
	private BigDecimal minStock = BigDecimal.ZERO;

	@Column(name = "expiry_date", nullable = true)
	private LocalDate expiryDate;

//	 @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<RawMaterialSupplierEntity> rawMaterialSuppliers = new ArrayList<>();

	@Column(name = "uuid", nullable = false)
	private String uuid;

	@Column(name = "is_apply_cal", nullable = false)
	private Boolean isApplyCal = true;

	@Column(name = "cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	@Column(name = "sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	@Column(name = "igst")
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Column(name = "cess")
	private BigDecimal cess = BigDecimal.ZERO;

	@Column(name = "max_stock", nullable = false)
	private BigDecimal maxStock = BigDecimal.ZERO;
	
	@Column(name = "lead_time", nullable = false)
	private BigDecimal leadTime = BigDecimal.ONE;
}
