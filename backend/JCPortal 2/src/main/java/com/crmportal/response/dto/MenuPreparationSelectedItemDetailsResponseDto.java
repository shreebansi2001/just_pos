package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuPreparationSelectedItemDetailsResponseDto {

	private Long menuCategoryId;
	private String menuCategoryName;
	private Integer menuSortOrder;
	private String menuSlogan;
	private String menuNotes;
	private String startTime;
	private String menuCategoryNameHindi;
	private String menuCategoryNameGujarati;
	private String menuNotesHindi;
	private String menuNotesGujarati;
	private Long catImgId;
	private Long bgImgId;
	private Integer catSpace;
	private String subCat;
	private String subCatHindi;
	private String subCatGujarati;
	private Boolean isMenuCatAddons = false;
	private Integer anyItem;
	private BigDecimal categoryPrice;
	private String catNickNameEnglish;
	private String catNickNameHindi;
	private String catNickNameGujarati;
	private String categoryStatus;
	private Boolean changedAfterCompletion;
	private String catHeadingEnglish = "";
	private String catHeadingHindi = "";
	private String catHeadingGujarati = "";
	private List<MenuPreparationDetailsResponseDto> selectedMenuPreparationItems;

	
}
