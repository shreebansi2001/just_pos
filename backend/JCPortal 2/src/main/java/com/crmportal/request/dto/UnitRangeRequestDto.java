package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitRangeRequestDto {
	private Double minValue;
	private Double maxValue;
	private Double roundOffValue;
}
