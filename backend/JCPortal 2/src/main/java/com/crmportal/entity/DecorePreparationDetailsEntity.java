package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "decore_preparation_details")
@Data
public class DecorePreparationDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "decore_preparation_details_id")
	private Long id;

	@Column(name = "starttime")
	private String startTime;

	@Column(name = "decoreitem_name")
	private String decoreItemName;

	@Column(name = "decoreitem_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String decoreItemNameHindi;

	@Column(name = "decoreitem_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String decoreItemNameGujarati;

	@Column(name = "decore_category_name")
	private String decoreCategoryName;

	@Column(name = "decore_category_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String decoreCategoryNameHindi;

	@Column(name = "decore_category_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String decoreCategoryNameGujarati;

	@Column(name = "decore_item_sortorder")
	private Integer decoreItemSortOrder;

	@Column(name = "decore_cat_sortorder")
	private Integer decoreCatSortOrder;

	@Column(name = "decore_item_price", precision = 10, scale = 2)
	private BigDecimal decoreItemPrice = BigDecimal.ZERO;

	@Column(name = "decore_item_notes")
	private String decoreItemNotes;

	@Column(name = "decore_item_notes_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String decoreItemNotesHindi;

	@Column(name = "decore_item_notes_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String decoreItemNotesGujarati;

	@Column(name = "decore_cat_notes")
	private String decoreCatNotes;

	@Column(name = "decore_cat_notes_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String decoreCatNotesHindi;

	@Column(name = "decore_cat_notes_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String decoreCatNotesGujarati;

	@Column(name = "decore_item_slogan")
	private String decoreItemSlogan;

	@Column(name = "decore_cat_slogan")
	private String decoreCatSlogan;

	@Column(name = "sub_cat")
	private String subCat;

	@Column(name = "sub_cat_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String subCatHindi;

	@Column(name = "sub_cat_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String subCatGujarati;

	@Column(name = "sub_item")
	private String subItem;

	@Column(name = "sub_item_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String subItemHindi;

	@Column(name = "sub_item_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String subItemGujarati;

	@Column(name = "is_decore_cat_addons", nullable = false)
	private Boolean isDecoreCatAddons = false;

	@Column(name = "is_decore_item_addons", nullable = false)
	private Boolean isDecoreItemAddons = false;

	@ManyToOne
	@JoinColumn(name = "decore_main_category_id", nullable = false)
	private DecoreMainCategoryMasterEntity decoreMainCategory;

	@ManyToOne
	@JoinColumn(name = "decore_item_id", nullable = false)
	private DecoreMainCategoryItemMasterEntity decoreItem;

	@ManyToOne
	@JoinColumn(name = "decore_preparation_id", nullable = false)
	private DecorePreparationEntity decorePreparation;

	@Column(name = "cat_img_id")
	private Long catImgId;

	@Column(name = "bg_img_id")
	private Long bgImgId;

	@Column(name = "cat_space")
	private Integer catSpace;

	@Column(name = "item_space")
	private Integer itemSpace;

	@Column(name = "any_item")
	private Integer anyItem;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "item_qty")
	private Integer itemQty;

	@Column(name = "vendor_id", nullable = true)
	private Long vendorId;
}