package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChefLabourItemRequestDto {
	
	private String allocation_type;

	private Integer counterNo;

	private BigDecimal pricePerLabour = BigDecimal.ZERO;
	
	private Integer helperNo;
	
	private BigDecimal pricePerHelper = BigDecimal.ZERO;

}
