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
public class OfficeExpensePayoutRequestDto {

	private Long payoutId;
	
	@NotNull(message = "office expense id is required.")
	private Long officeExpenseId;

	@NotNull(message = "Account type is required.")
	private AccountType accountType;
	
	private PaymentMode paymentMode;

	private Long cashAccountId;
	
	private Long bankAccountId;
	
	@NotNull(message = "Payment date is required.")
	private String paymentDate;
	
	private String transactionId;
	
	private String chequeNo;

	@NotNull(message = "Amount is required.")
	private BigDecimal amount;
	
	@NotNull(message = "Due amount is required.")
	private BigDecimal dueAmount;
	
	private String description;
	
}
