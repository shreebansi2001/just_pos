package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRawMaterialsResponseDto {

	private Long id;
	private BigDecimal weight;
	private UnitMasterResponseDto unit;
	private BigDecimal rate;
	private String venue;
	private Boolean isVisible;
	private RawMaterialMasterResponseDto rawMaterial;
}
