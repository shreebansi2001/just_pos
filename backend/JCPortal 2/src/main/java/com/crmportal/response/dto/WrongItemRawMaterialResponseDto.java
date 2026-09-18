package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WrongItemRawMaterialResponseDto {

	private Long itemRawMaterialId;
	private Long itemRawMaterialUnitId;
	private String itemRawMaterialUnitName;
	private BigDecimal weight;

	private Long rawMaterialId;
	private String rawMaterialName;
	private Long rawMaterialUnitId;
	private String rawMaterialUnitName;

	private Long itemId;
	private String itemName;

	private BigDecimal finalWeight;
	
	private BigDecimal supplierRate;
	
	private UnitHierarchyDto unit;

}
