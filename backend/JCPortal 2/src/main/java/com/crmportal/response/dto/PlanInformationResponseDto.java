package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanInformationResponseDto {
	
	private Long id;
	private String planName;

    private String planStartDate;

    private String planEndDate;

    private BigDecimal amount;

}
