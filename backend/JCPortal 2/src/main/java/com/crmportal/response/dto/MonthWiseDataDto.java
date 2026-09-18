package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthWiseDataDto {
	
	private String month;
	
	private BigDecimal totalAmount;
	
	private BigDecimal paidAmount;
	
	private BigDecimal pendingAmount;
	
	private String status;
}