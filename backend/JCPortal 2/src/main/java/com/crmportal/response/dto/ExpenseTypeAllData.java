package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseTypeAllData {

	private Long typeId;
	private String typeName;
	
	private BigDecimal totalExpense;
	private BigDecimal totalPaidExpense;
	private BigDecimal totalUnpaidExpense;
	
	List<OfficeExpenseResponseDto> expenses;
}
