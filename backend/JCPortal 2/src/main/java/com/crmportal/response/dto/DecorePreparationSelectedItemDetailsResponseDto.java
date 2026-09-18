package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePreparationSelectedItemDetailsResponseDto {

	private Long decoreCategoryId;
	private String decoreCategoryName;

	private Integer decoreCatSortOrder;
	private String decoreCatNotes;

	private String startTime;

	private String decoreCategoryNameHindi;
	private String decoreCategoryNameGujarati;

	private String decoreCatNotesHindi;
	private String decoreCatNotesGujarati;

	private Long catImgId;
	private Long bgImgId;
	private Integer catSpace;

	private String subCat;
	private String subCatHindi;
	private String subCatGujarati;

	private Boolean isDecoreCatAddons = false;
	private Integer anyItem;

	private List<DecorePreparationDetailsResponseDto> selectedItems;
}