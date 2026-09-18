package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCategoryForTabkeMenuWithBgReportResponseDto {

	private Long menuCatId;
	private String catNameEnglish;
	private String catNameHindi;
	private String catNameGujarati;
	private List<MenuItemForTableMenuWithBgReportResponseDto> items;
}
