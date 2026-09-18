package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialCatExportDataResponseDto {

	private String catNameEnglish;
	
	private String catNameHindi;
	
	private String catNameGujarati;
	
	private String catTypeEnglish;
}
