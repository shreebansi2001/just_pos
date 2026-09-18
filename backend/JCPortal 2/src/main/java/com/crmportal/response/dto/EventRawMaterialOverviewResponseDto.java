package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRawMaterialOverviewResponseDto {
	
	private String date;
	private String agencyName;
	private BigDecimal qty;
	private String unitName;
	private BigDecimal totalRate;
}
