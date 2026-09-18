package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRawMaterialDetailsResponseDto {

	private Long rawMaterialId;
	private String rawMaterialName;
	private BigDecimal price;
	private Long unitId;
	private String unitName;
	private String unitSymbole;
	private UnitHierarchyDto unitHierarchy;
}
