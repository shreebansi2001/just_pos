package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeExpenseResponseDto {
	private Long id;

	private String title;

	private BigDecimal expenseAmount;
	
	private BigDecimal remaingAmount;

	private BigDecimal payoutAmount;

	private String expenseDate;

	private String dueDate;

	private String paidDate;

	private String paymentMode;
	
	private String remarks;

	private Long accountContactId;
	
	private Long userId;
	
	private String accountContactName;
	
//	private String isPayout;
	
	private String status;
	
	private Long incomeExpenseTypeId;
	
	private String incomeExpenseTypeName;
	private List<OfficeExpenseDocResponseDto> files;
	private List<OfficeExpensePayoutResponseDto> payoutHistory;
}
