package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialInMenuItemResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private UnitMasterResponseDto unit;
	private BigDecimal supplierRate;
	private Boolean isGeneralFix;
	private BigDecimal weightPer100Pax;
	private Integer sequence;
	private Long userId;
	private UnitHierarchyDto unitHierarchy;
	private String dailyConsumption;
}
