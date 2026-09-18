package com.crmportal.entity;

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
@Table(name = "raw_material_category_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialCategoryTypeMasterEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "raw_material_cat_type_id")
	private Long id;
	
	@Column(name = "name_english")
	private String nameEnglish;

	@Column(name = "name_hindi")
	private String nameHindi;

	@Column(name = "name_gujarati")
	private String nameGujarati;

	@Column(name = "is_manually")
	private Boolean isManually;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "is_active")
	private Boolean isActive = true;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;

//	 @OneToMany(mappedBy = "rawMaterialCatType", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<RawMaterialCategoryMasterEntity> rawMaterialCategories = new ArrayList<>();

}
