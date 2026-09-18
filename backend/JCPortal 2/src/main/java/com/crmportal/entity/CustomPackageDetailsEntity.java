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
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "custom_package_details")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomPackageDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "custom_package_details_id")
	private Long id;
	
	@Column(name = "menu_name")
	private String menuName;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "menu_instruction")
	private String menuInstruction;
	
	@Column(name = "menu_sortorder")
	private Integer menuSortOrder;

	@Column(name = "item_instruction")
	private String itemInstruction;
	
	@Column(name = "item_sortorder")
	private Integer itemSortOrder;

	@Column(name = "any_item")
	private Integer anyItem;
	
	@ManyToOne
    @JoinColumn(name = "menu_category_id", nullable = false)
    private MenuCategoryMasterEntity menuCategory;
	
	@ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = true)
    private MenuItemMasterEntity menuItem;
	
	@Column(name = "item_price")
	private BigDecimal itemPrice = BigDecimal.ZERO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne
    @JoinColumn(name = "custom_package_id", nullable = false)
    private CustomPackageEntity customPackage;
	
	@Column(name = "cat_nick_name_english")
	private String catNickNameEnglish;

	@Column(name = "cat_nick_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String catNickNameHindi;

	@Column(name = "cat_nick_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String catNickNameGujarati;

	@Column(name = "item_nick_name_english")
	private String itemNickNameEnglish;

	@Column(name = "item_nick_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemNickNameHindi;

	@Column(name = "item_nick_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemNickNameGujarati;
}
