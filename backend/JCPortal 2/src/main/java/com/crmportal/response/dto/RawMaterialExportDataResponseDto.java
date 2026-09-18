package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialExportDataResponseDto {

	private String rawMaterialNameEnglish;
	
	private String rawMaterialNameHindi;
	
	private String rawMaterialNameGujarati;
	
	private BigDecimal supplierRate;
	
	private String rawCatNameEnglish;
	
	private String unitNameEnglish;
	
	private BigDecimal opb;
}
