package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLadgerResponseDto {

	private String invoiceNo;
	private String accountName;
	private String date;
	private String type;
	private BigDecimal settlementAmnt;
	private BigDecimal creditCrAmnt;
	private BigDecimal debitDrAmnt;
	private BigDecimal totalAmnt;
	private String remarks;

}
