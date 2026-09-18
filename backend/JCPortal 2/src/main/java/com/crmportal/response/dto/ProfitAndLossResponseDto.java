package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfitAndLossResponseDto {

	private BigDecimal totalRevenue;
	
	private BigDecimal totalExpence;
	
	private BigDecimal netProfit;
	
	private BigDecimal profitMargin;

	private List<IncomeListResponseDto> payments;
}
