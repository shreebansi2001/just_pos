package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalDateAmount {

	private LocalDate date;
	
	private BigDecimal total;
	
	private BigDecimal paid;
	
	private BigDecimal pending;
}
