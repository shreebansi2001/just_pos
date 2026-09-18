package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;
import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.ERangeType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "units")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unit_id")
	private Long id;

	@Column(name = "name_english", nullable = false)
	private String nameEnglish;

	@Column(name = "name_hindi")
	private String nameHindi;

	@Column(name = "name_gujarati")
	private String nameGujarati;

	@Column(name = "symbol_english", nullable = false)
	private String symbolEnglish;

	@Column(name = "symbol_hindi")
	private String symbolHindi;

	@Column(name = "symbol_gujarati")
	private String symbolGujarati;

	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "is_parent_unit")
	private Boolean isParentUnit = Boolean.FALSE;

	@Column(name = "decimal_limit")
	private Integer decimalLimit;

	@ManyToOne
	@JoinColumn(name = "parent_unit_id", nullable = true)
	private UnitMasterEntity parentUnit;

	@Column(name = "equivalent_value")
	private Double equivalentValue;

	@Enumerated(EnumType.STRING)
	@Column(name = "range_type")
	private ERangeType rangeType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;

	@Column(name = "isPublished", nullable = false)
	private Boolean isPublished = true;

	@Column(name = "uuid", nullable = false)
	private String uuid;

	@OneToMany(mappedBy = "parentUnit", fetch = FetchType.LAZY)
	private List<UnitMasterEntity> children = new ArrayList<>();

	public void addChild(UnitMasterEntity child) {
		children.add(child);
		child.setParentUnit(this);
	}

	public void removeChild(UnitMasterEntity child) {
		children.remove(child);
		child.setParentUnit(null);
	}
//	 @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<RawMaterialMasterEntity> rawMaterials = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuItemAllocationConfigEntity> menuItemAllocationConfigs = new ArrayList<>();
//	 
//	 @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuItemRawMaterialEntity> menuItemRawMaterials = new ArrayList<>();
}
