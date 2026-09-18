package com.crmportal.response.dto;

import com.crmportal.enums.ChangeStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCategoryForPreparationResponseDto {

	private Long menuCategoryId;
	private String menuCategoryName;
	private Integer menuSortOrder;
	private String menuSlogan;
	private String menuNotes;
	private String menuCategoryNameHindi;
	private String menuCategoryNameGujarati;
	private String menuNotesHindi;
	private String menuNotesGujarati;
	private String startTime;
	private Boolean isMenuCatAddons;
	private Long catImgId;
	private Long bgImgId;
	private Integer catSpace;
	private Integer anyItem;
	private String subCat;
	private String subCatHindi;
	private String subCatGujarati;
	private String catNickNameEnglish;
	private String catNickNameHindi;
	private String catNickNameGujarati;
	private ChangeStatus categoryStatus;
	private Boolean changedAfterCompletion;
	private String catHeadingEnglish;
	private String catHeadingHindi;
	private String catHeadingGujarati;
}
