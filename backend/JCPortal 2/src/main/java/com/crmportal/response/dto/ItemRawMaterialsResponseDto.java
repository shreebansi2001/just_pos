package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRawMaterialsResponseDto {

	private Long id;
	private BigDecimal weight;
	private Long  itemRawMatUnitId;
	private String  itemRawMatUnitName;
	private UnitHierarchyDto unitHierarchy;
	private Long itemId;
	private String itemName;
	private Long rawMatUnitId;
	private String rawMatUnitName;
	private BigDecimal supplierRate;
	private Boolean isVisible;
}
