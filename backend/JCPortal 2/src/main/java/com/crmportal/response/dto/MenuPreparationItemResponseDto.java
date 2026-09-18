package com.crmportal.response.dto;

import java.math.BigDecimal;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuPreparationItemResponseDto {

	private String menuItemName;
	private String menuCategoryName;
	private BigDecimal itemPrice;
	private BigDecimal categoryPrice;
	private String itemSlogan;
	private String categorySlogan;
	private Long menuCategoryId;
	private Long menuItemId;
	private String imagePath;
	private Boolean isSelected;
	private Integer itemSortOrder;
	private Integer menuSortOrder;
	private String menuItemNameHindi;
	private String menuItemNameGujarati;
	private String menuCategoryNameHindi;
	private String menuCategoryNameGujarati;
	private String instructionEnglish;
	private String instructionHindi;
	private String instructionGujarati;
	private String reportNameEnglish;
	private String reportNameHindi;
	private String reportNameGujarati;
	private String url;
}


