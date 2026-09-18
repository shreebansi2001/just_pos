package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyMenuCategoryResponseDto {

	private Long menuCatId;
	private String menuCatNameEnglish;
	private String menuCatNameHindi;
	private String menuCatNameGujarati;
	
	private List<AgencyMenuItemResponseDto> menuItems;
}
