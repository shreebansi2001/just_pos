package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberAllExpensesResponseDto {

	private Long memberId;
	private String memberName;
	
	private BigDecimal totalAmount;
	private BigDecimal totalPaidAmount;
	private BigDecimal totalUnPaidAmount;
	
	private List<ExpenseResponseDto> tripExpenses;
	private List<OfficeExpenseResponseDto> employeeExpenses;
	private List<OfficeExpenseResponseDto> officeExpenses;
	private List<OfficeExpenseResponseDto> otherExpenses;
	
}
