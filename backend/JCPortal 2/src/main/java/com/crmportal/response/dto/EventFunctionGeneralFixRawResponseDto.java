package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionGeneralFixRawResponseDto {
	private Long id;
	private Long rawCatId;
	private String rawCatNameEnglish;
	private String rawCatNameHindi;
	private String rawCatNameGujarati;
	private Long rawId;
	private String rawNameEnglish;
	private String rawNameHindi;
	private String rawNameGujarati;
	private BigDecimal weightPer100pax;
	private BigDecimal supplierRate;
	private BigDecimal weight;
	private BigDecimal price;
	private Long eventFunctionId;
	private String functionNameEnglish;
	private String functionNameHindi;
	private String functionNameGujarati;
	private Integer pax;
	private UnitMasterResponseDto unit;
	private UnitHierarchyDto unitHierarchyDto;
	private Boolean rawMaterialChanged;
}
