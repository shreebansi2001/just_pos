package com.crmportal.response.dto;

import java.math.BigDecimal;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntryResponseDto {

	private Long id;
	
	private Long userId;
	
	private String voucherNo;
	
	private AccountType accountType;
	
	private EntryType entryType;
	
	private PaymentMode paymentMode;
	
	private Long incomeExpenseTypeId;
	
	private String incomeExpenseTypeName;
	
	private CashOpbResponseDto cashType;
	
	private BankDetailsResponseDto bankDetails;
	
	private String date;
	
	private Long accountContactId;
	
	private String accountContactName;
	
	private BigDecimal amount;
	
	private String notes;
	
	private SuperAdminInvoiceResponseDto invoice;
	
	private String referenceNo;
	
	private Boolean isDelete;
	
	private String createdAt;
	
	private String updatedAt;
	
	private String source;
}
