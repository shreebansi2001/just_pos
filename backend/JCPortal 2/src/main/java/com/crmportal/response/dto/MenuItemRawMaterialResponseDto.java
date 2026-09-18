package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRawMaterialResponseDto {

	private Long rawMaterialId;
	
	private String rawMaterial;
	
	private BigDecimal weight;

	private Long unitId;
	
	private String unitName;
	
	private Long menuItemId;
}
