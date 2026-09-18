package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPlansHistoryResponseDto {

	private Long id;
	private String startDate;
	private String endDate;
	private Boolean IsActive;
	private Long userId;
	private String planAmount;
	private String planBaseAmount;
	private PlansResponseDto plan;
	
}
