package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.crmportal.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountTransferRequestDto {

	private Long id;
	
	private AccountType fromType;

	private AccountType toType;
	
	private Long fromAccountId;
	
	private Long toAccountId;
	
	private BigDecimal amount;
	
	private String date;
	
	private String notes;
	
	private Long userId;
	
}
