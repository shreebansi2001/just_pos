package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectedMenuItemForMenuAllocationResponseDto {
	
	private Long menuCategoryId;
	private String menuCategoryName;
	private String menuCategoryNameHindi;
	private String menuCategoryNameGujarati;
	private List<MenuPreparationItemForMenuAllocationResponseDto> selectedMenuPreparationItems;

}
