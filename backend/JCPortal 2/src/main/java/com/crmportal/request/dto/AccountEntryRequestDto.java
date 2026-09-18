package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntryRequestDto {

	@NotNull(message = "User id is required.")
	private Long userId;
	
	@NotNull(message = "Voucher no. is required.")
	private String voucherNo;
	
	@NotNull(message = "Entry type is required.")
	private EntryType entryType;
	
	@NotNull(message = "Payment mode is required.")
	private PaymentMode paymentMode;
	
	@NotNull(message = "Income/Expense id is required.")
	private Long incomeExpenseTypeId;
	
	private Long cashTypeId;	
	
	private Long bankAccountId;
	
	@NotNull(message = "Date is required.")
	private String date;
	
	private Long accountContactId;
	
	private String accountContactName;
	
	@NotNull(message = "Amount is required.")
	private BigDecimal amount;
	
	private String notes;
	
	private Long invoiceId;
	
	private String referenceNo;
	
}
