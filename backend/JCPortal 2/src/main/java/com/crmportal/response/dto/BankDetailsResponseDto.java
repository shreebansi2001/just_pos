package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDetailsResponseDto {

	private Long id;

	private String bankName;

	private String branchName;

	private String accountHolderName;

	private String accountNo;

	private String ifscCode;

	private Boolean isPrimary;

	private String upiId;
	
	private Long userId;
	
	private String qrCodePath;
	
	private BigDecimal openingBalance;
	
	private BigDecimal currentBalance;
	
	private String openingDate;

}
