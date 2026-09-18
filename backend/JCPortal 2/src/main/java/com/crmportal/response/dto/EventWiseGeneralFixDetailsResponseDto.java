package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventWiseGeneralFixDetailsResponseDto {

	private Long rawMaterialId;
	private String rawMaterialName;
	private BigDecimal weightPer100Pax;
	private Long unitId;
	private String unitName;
	private BigDecimal requiredQty;
}
