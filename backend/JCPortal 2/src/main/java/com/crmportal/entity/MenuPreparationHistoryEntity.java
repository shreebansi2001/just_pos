package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.ChangeStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_preparation_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuPreparationHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_preparation_history_id")
	private Long id;

	@Column(name = "starttime")
	private String startTime;

	@Column(name = "menuitem_name")
	private String menuItemName;

	@Column(name = "menuitem_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuItemNameHindi;

	@Column(name = "menuitem_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuItemNameGujarati;

	@Column(name = "menu_category_name")
	private String menuCategoryName;

	@Column(name = "menu_category_name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuCategoryNameHindi;

	@Column(name = "menu_category_name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuCategoryNameGujarati;

	@Column(name = "item_sortorder")
	private Integer itemSortOrder;

	@Column(name = "menu_sortorder")
	private Integer menuSortOrder;

	@Column(name = "item_price", precision = 10, scale = 2)
	private BigDecimal itemPrice = BigDecimal.ZERO;

	@Column(name = "category_price", precision = 10, scale = 2)
	private BigDecimal categoryPrice = BigDecimal.ZERO;

	@Column(name = "item_notes", columnDefinition = "TEXT")
	private String itemNotes;

	@Column(name = "item_notes_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemNotesHindi;

	@Column(name = "item_notes_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemNotesGujarati;

	@Column(name = "menu_notes", columnDefinition = "TEXT")
	private String menuNotes;

	@Column(name = "menu_notes_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuNotesHindi;

	@Column(name = "menu_notes_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String menuNotesGujarati;

	@Column(name = "item_slogan", columnDefinition = "TEXT")
	private String itemSlogan;

	@Column(name = "menu_slogan", columnDefinition = "TEXT")
	private String menuSlogan;

	@Column(name = "sub_cat")
	private String subCat;

	@Column(name = "sub_cat_hindi")
	private String subCatHindi;

	@Column(name = "sub_cat_gujarati")
	private String subCatGujarati;

	@Column(name = "sub_item")
	private String subItem;

	@Column(name = "sub_item_hindi")
	private String subItemHindi;

	@Column(name = "sub_item_gujarati")
	private String subItemGujarati;

	@Column(name = "isMenuCatAddons", nullable = false)
	private Boolean isMenuCatAddons = false;

	@Column(name = "isItemAddons", nullable = false)
	private Boolean isItemAddons = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "menu_category_id", nullable = false)
	private MenuCategoryMasterEntity menuCategory;

	@ManyToOne
	@JoinColumn(name = "menu_item_id", nullable = false)
	private MenuItemMasterEntity menuItem;

	@ManyToOne
	@JoinColumn(name = "menu_preparation_id", nullable = false)
	private MenuPreparationEntity menuPreparation;

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

	@Column(name = "is_cat_image")
	private Boolean isCatImage;

	@Enumerated(EnumType.STRING)
	@Column(name = "category_status")
	private ChangeStatus categoryStatus = ChangeStatus.NORMAL;

	@Enumerated(EnumType.STRING)
	@Column(name = "item_status")
	private ChangeStatus itemStatus = ChangeStatus.NORMAL;

	@Column(name = "changed_after_completion")
	private Boolean changedAfterCompletion = false;

	@Column(name = "item_heading", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemHeading;

	@Column(name = "item_heading_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemHeadingHindi;

	@Column(name = "item_heading_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String itemHeadingGujarati;

	@Column(name = "cat_heading_english")
	private String catHeadingEnglish;
	
	@Column(name = "cat_heading_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String catHeadingHindi;

	@Column(name = "cat_heading_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String catHeadingGujarati;
}
