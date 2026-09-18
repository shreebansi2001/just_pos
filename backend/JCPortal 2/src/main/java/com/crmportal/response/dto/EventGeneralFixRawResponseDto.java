package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventGeneralFixRawResponseDto {
	private Long id;
	private Long rawCatId;
	private String rawCatNameEnglish;
	private String rawCatNameHindi;
	private String rawCatNameGujarati;
	private Long rawId;
	private String rawNameEnglish;
	private String rawNameHindi;
	private String rawNameGujarati;
	private BigDecimal weight;
	private BigDecimal price;
	private BigDecimal weightPer100pax;
	private BigDecimal supplierRate;
	private UnitMasterResponseDto unit;
	private UnitHierarchyDto unitHierarchyDto;
	private List<EventFunctionGeneralFixRawResponseDto> eventFunctionGeneralFixRaws;
}
