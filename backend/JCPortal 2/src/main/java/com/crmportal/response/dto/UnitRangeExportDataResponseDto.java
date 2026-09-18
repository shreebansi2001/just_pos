package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitRangeExportDataResponseDto {

	private Double minValue;
	
	private Double maxValue;
	
	private Double roundValue;
	
	private String unitNameEnglish;
	
	private String rangeType;
}
