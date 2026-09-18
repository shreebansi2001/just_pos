package com.crmportal.request.dto;

import java.math.BigDecimal;

import com.crmportal.enums.EExpense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseItemRequestDto {

	private Long expenseItemId;
	
	private String itemName;
	
	private BigDecimal amount;
	
	private String itemPurchaseDate;
	
	private String paymentType;
	
	private String remarks;
	
	private EExpense userType;
	
	private Long expenseId;

	private Long userId;
	
	private Long eventId;
	
	private Long supplierId;
}
