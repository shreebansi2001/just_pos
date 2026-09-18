package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuAllocationItemRequestDto {

	private Long id;
	
	private Integer quantity;
	
	private Long unitId;
	
	private BigDecimal price;
}
