package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemRawMaterialWeightRequestDto {

	private Long id;
	private BigDecimal finalWeight;
	private Long  finalUnitId;
	private Long rawMaterialUnitId;
	private BigDecimal supplierRate;
	private Long itemId;
	
	private Long newRawMaterialId;
}
