package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitRangeResponseDto {
	private Long id;
	private Double minValue;
	private Double maxValue;
	private Double roundOffValue;
}
