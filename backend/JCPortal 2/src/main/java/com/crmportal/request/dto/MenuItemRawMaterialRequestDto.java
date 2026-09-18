package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRawMaterialRequestDto {

	private Long id;
	private BigDecimal weight;
	private Long unitId;
	private BigDecimal rate;
	private Long rawMaterialId;
	private String venue;
	private Boolean isVisible;
}
