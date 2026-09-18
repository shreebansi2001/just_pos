package com.crmportal.response.dto;

import java.math.BigDecimal;

import com.itextpdf.layout.element.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllExpensesReportResponseDto {

	private Table table;
	private BigDecimal totalAmount;
	private BigDecimal totalPaidAmount;
	private BigDecimal totalUnPaidAmount;
}
