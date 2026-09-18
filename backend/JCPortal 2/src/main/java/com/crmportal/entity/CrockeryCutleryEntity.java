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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.mapper.RawMaterialCategoryMasterMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crockery_cutlery")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrockeryCutleryEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_category_id")
	private RawMaterialCategoryMasterEntity rawMaterialCategory;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_id")
	private RawMaterialMasterEntity rawMaterial;
	
	@Column(name = "raw_material_name_english")
	private String rawMaterialNameEnglish;

	@Column(name = "raw_material_name_hindi")
	private String rawMaterialNameHindi;

	@Column(name = "raw_material_name_Gujarati")
	private String rawMaterialNameGujarati;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserMasterEntity user;
	
	@Column(name = "range_0_to_100")
	private BigDecimal r_0_to_100;

	@Column(name = "range_101_to_200")
	private BigDecimal r_101_to_200;

	@Column(name = "range_201_to_300")
	private BigDecimal r_201_to_300;

	@Column(name = "range_301_to_400")
	private BigDecimal r_301_to_400;

	@Column(name = "range_401_to_500")
	private BigDecimal r_401_to_500;

	@Column(name = "range_501_to_600")
	private BigDecimal r_501_to_600;

	@Column(name = "range_601_to_700")
	private BigDecimal r_601_to_700;

	@Column(name = "range_701_to_800")
	private BigDecimal r_701_to_800;

	@Column(name = "range_801_to_900")
	private BigDecimal r_801_to_900;

	@Column(name = "range_901_to_1000")
	private BigDecimal r_901_to_1000;

	@Column(name = "range_1001_to_1100")
	private BigDecimal r_1001_to_1100;

	@Column(name = "range_1101_to_1200")
	private BigDecimal r_1101_to_1200;

	@Column(name = "range_1201_to_1300")
	private BigDecimal r_1201_to_1300;

	@Column(name = "range_1301_to_1400")
	private BigDecimal r_1301_to_1400;

	@Column(name = "range_1401_to_1500")
	private BigDecimal r_1401_to_1500;

	@Column(name = "range_1501_to_1600")
	private BigDecimal r_1501_to_1600;

	@Column(name = "range_1601_to_1700")
	private BigDecimal r_1601_to_1700;

	@Column(name = "range_1701_to_1800")
	private BigDecimal r_1701_to_1800;

	@Column(name = "range_1801_to_1900")
	private BigDecimal r_1801_to_1900;

	@Column(name = "range_1901_to_2000")
	private BigDecimal r_1901_to_2000;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
}
