package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripExpensePayoutRequestDto {

	private Long payoutId;

	@NotNull(message = "Expense id is required.")
	private Long expenseId;
	
	@NotNull(message = "Account type is required.")
	private AccountType accountType;
	
	@NotNull(message = "Payment mode is required.")
	private PaymentMode paymentMode;
	
	@NotNull(message = "Payment date is required.")
	private String paymentDate;
	
	private Long cashAccountId;
	
	private Long bankAccountId;
	
	private String transactionId;
	
	@NotNull(message = "Amount is required.")
	private BigDecimal payoutAmount;
	
	@NotNull(message = "Due amount is required.")
	private BigDecimal dueAmount;
	
	private String description;
	
	private String chequeNo;
	
}
