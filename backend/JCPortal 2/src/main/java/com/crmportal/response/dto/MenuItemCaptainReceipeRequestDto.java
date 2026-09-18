package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemCaptainReceipeRequestDto {

	private Long id;
	
	private BigDecimal weight;
	
	private Long unitId;
	
	private BigDecimal rate;
	
	private Long captainReceipeId;
	
	private String venue;
}
