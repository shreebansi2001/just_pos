package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "menusubcategory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuSubCategoryMasterEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_sub_cat_id")
	private Long id;
	
	@Column(name = "nameEnglish")
	private String nameEnglish;
	
	@Column(name = "nameHindi")
	private String nameHindi;
	
	@Column(name = "nameGujarati")
	private String nameGujarati;
	
	@Column(name = "is_active")
	private Boolean isActive = true;
	
	@ManyToOne
    @JoinColumn(name = "menucategory_id", nullable = false)
    private MenuCategoryMasterEntity menuCategory;
	
	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserMasterEntity user;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
//	@OneToMany(mappedBy = "menuSubCategory", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuItemMasterEntity> menuItems = new ArrayList<>();
}
