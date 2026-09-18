package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLedgerDashboardResponseDto {

	private BigDecimal debit;
	
	private BigDecimal credit;
	
	private BigDecimal totalBalance;
	
	private BigDecimal openingBalance;
	
	private List<AccountLedgerResponseDto> transactions;
}
