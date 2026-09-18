package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemaingDataResponseDto {
	
	private Long menuItemId;
	private String itemName;
	private Integer totalRawMaterial;
	private String slogan;
	private Long rawMaterialId;
	private String rawMaterialName;
	private BigDecimal rawMaterialRate;
}
