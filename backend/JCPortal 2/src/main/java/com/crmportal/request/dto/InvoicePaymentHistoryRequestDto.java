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
public class InvoicePaymentHistoryRequestDto {

	@NotNull(message = "Invoice Id is required.")
	private Long invoiceId;
	
	private Long bankAccountId;
	
	@NotNull(message = "Payment Date is required.")
	private String payment_date;
	
	private String transactionId;
	
	private String chequeNo;
	
	@NotNull(message = "Amount is required.")
	private BigDecimal amount;
	
	@NotNull(message = "Due Amount is required.")
	private BigDecimal dueAmount;
	
	@NotNull(message = "Payment mode is Required.")
	private PaymentMode paymentMode;
	
	@NotNull(message = "Account type is Required.")
	private AccountType accountType;
	
	private Long cashTypeId;
}
