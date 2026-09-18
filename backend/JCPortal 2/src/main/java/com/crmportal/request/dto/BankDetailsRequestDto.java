package com.crmportal.request.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDetailsRequestDto {
	
	private Long id;
	
	private String bankName;
	
	private String branchName;
	
	private String accountHolderName;
	
	private String accountNo;
	
	private String ifscCode;
	
	private Boolean isPrimary;
	
	private String upiId;
	
	private Long userId;
	
	private MultipartFile qrCodeImage;
	
	private BigDecimal openingBalance = BigDecimal.ZERO;
	
	private BigDecimal currentBalance = BigDecimal.ZERO;
	
	private String openingDate;
}
