package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMisMatchedUnitsRequestDto {

	private Long itemRawMaterialId;
	private BigDecimal finalWeight;
	private Long itemRawMaterialUnitId;
	private Long userId;	
	private Long rawMaterialUnitId;
	private BigDecimal supplierRate;
	private Long itemId;
	
}
