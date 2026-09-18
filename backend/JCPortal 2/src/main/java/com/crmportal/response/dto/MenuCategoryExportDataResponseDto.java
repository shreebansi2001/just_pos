package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCategoryExportDataResponseDto {

	private String menuCatNameEnglish;
	
	private String menuCatNameHindi;
	
	private String menuCatNameGujarati;
	
	private String menuSlogan;
}
