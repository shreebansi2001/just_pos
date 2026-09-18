package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRawMaterialExportDataResponseDto {

	private String itemNameEnglish;

	private String rawMaterialNameEnglish;
	
	private BigDecimal weight;
	
	private String unitNameEnglish;
}
