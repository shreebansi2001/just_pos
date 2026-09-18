package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPackageMenuCategoryResponseDto {

	private Long menuId;
	private String menuName;
	private Integer menuSortOrder;
	private String menuInstruction;
	private Integer anyItem;
}
