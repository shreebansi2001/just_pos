package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChefLabourItemResponseDto {

	private String allocation_type;
	private Integer counterNo;
	private BigDecimal pricePerLabour;
	private BigDecimal pricePerHelper;
	private BigDecimal basePrice;
	private BigDecimal qtyPer100Person;
}
