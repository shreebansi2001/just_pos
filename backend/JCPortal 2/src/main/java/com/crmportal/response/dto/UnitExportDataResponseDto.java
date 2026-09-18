package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitExportDataResponseDto {

	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
	private String symbolEnglish;
	
	private String symbolHindi;
	
	private String symbolGujarati;
	
	private Boolean isParentUnit;
	
	private String parentNameEnglish;
	
	private Double equivalentValue;
	
	private Integer decimalLimit;
	
	private String rangeType;
}
