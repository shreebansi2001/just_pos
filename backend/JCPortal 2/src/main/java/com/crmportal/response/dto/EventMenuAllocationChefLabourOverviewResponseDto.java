package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMenuAllocationChefLabourOverviewResponseDto {

	private String staffCategory;

	private Integer labour;

	private Integer helpers;

	private BigDecimal weight;

	private Long unitId;

	private String unitNameEnglish;

	private String unitNameHindi;

	private String unitNameGujarati;
}