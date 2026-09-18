package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIMenuCategoryWithItemsResponseDto {

	private String menuItemName;
	private String menuCategoryName;
	private Long menuCategoryId;
	private Long menuItemId;
}
