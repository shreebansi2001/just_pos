package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.crmportal.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountTransferResponseDto {

	private Long id;

	private AccountType fromType;

	private AccountType toType;
	
	private Long fromAccountId;
	
	private String fromAccountName;
	
	private Long toAccountId;
	
	private String toAccountName;
	
	private BigDecimal amount;
	
	private String date;
	
	private String notes;
	
	private Boolean isDelete;
	
	private String createdAt;
	
	private String updatedAt;
	
	private Long userId;
}
