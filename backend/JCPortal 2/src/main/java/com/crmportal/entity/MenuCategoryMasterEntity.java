package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@Table(name = "menucategory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCategoryMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_category_id")
	private Long id;
	
	@Column(name = "name_english")
	private String nameEnglish;
	
	@Column(name = "name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;
	
	@Column(name = "name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;
	
	@Column(name = "report_name_english")
	private String reportNameEnglish;
	
	@Column(name = "report_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String reportNameHindi;
	
	@Column(name = "report_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String reportNameGujarati;
	
	@Column(name = "menuslogan")
	private String menuSlogan;
	
	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price = BigDecimal.ZERO;
	
	@Column(name = "is_active")
	private Boolean isActive = true;
	
	@Column(name = "isPublished", nullable = false)
	private Boolean isPublished = true;
	
	@Column(name = "image_path")
	private String imagePath;
	
	@Column(name = "sequence")
	private Integer sequence;
	
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
	
//	@OneToMany(mappedBy = "menuCategory", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuItemMasterEntity> menuItems = new ArrayList<>();
//	
//	@OneToMany(mappedBy = "menuCategory", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuPreparationDetailsEntity> menuPreparationDetails = new ArrayList<>();
	
	@Column(name = "uuid", nullable = false)
	private String uuid;
}
