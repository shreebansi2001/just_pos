package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMenuAllocationChefLabourOverviewRequestDto {
	private String staffCategory;
	private Integer labour;
	private Integer helpers;
	private BigDecimal weight;
	private Long unitId;
}
