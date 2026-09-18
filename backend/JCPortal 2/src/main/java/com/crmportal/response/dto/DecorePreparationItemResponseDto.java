package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePreparationItemResponseDto {

	private String decoreItemName;
	private String decoreCategoryName;

	private BigDecimal itemPrice;

	private String decoreItemSlogan;
	private String categorySlogan;

	private Long decoreCategoryId;
	private Long decoreItemId;

	private List<String> imagePath;

	private Boolean isSelected;

	private Integer itemSortOrder;
	private Integer decoreSortOrder;

	private String decoreItemNameHindi;
	private String decoreItemNameGujarati;

	private String decoreCategoryNameHindi;
	private String decoreCategoryNameGujarati;

	private String instructionEnglish;
	private String instructionHindi;
	private String instructionGujarati;
}