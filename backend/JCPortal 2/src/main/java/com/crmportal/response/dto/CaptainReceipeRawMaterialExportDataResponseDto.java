package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptainReceipeRawMaterialExportDataResponseDto {

	private String captainReceipeName;
	
	private String rawMaterial;
	
	private BigDecimal weight;
	
	private String unitName;
}
