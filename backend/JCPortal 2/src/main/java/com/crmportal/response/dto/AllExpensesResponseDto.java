package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllExpensesResponseDto {

	private BigDecimal totalTripExpense;
	private BigDecimal totalTripPaidAmount;
	private BigDecimal totalTripUnPaidAmount;
	
	private BigDecimal totalOfficeExpense;
	private BigDecimal totalOfficePaidAmount;
	private BigDecimal totalOfficeUnPaidAmount;
	
	private BigDecimal totalExpenses;
	private BigDecimal totalPaidAmount;
	private BigDecimal totalUnPaidAmount;
	
//	List<MemberAllExpensesResponseDto> members;
	
	private List<ExpenseResponseDto> tripExpenses;
//	private List<OfficeExpenseResponseDto> officeExpenses;
	
	private List<ExpenseTypeAllData> expenses;
}
