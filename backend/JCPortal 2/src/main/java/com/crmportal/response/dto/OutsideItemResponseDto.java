package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutsideItemResponseDto {
	
	private BigDecimal quantityPer100Person = BigDecimal.ZERO;
	
	private BigDecimal basePrice = BigDecimal.ZERO;
	
	private BigDecimal pricePerLabour = BigDecimal.ZERO;
	
	private BigDecimal pricePerHelper = BigDecimal.ZERO;

	private ContactCategoryMasterResponseDto contactCategory;
	
	private UnitMasterResponseDto unit;
	
}
