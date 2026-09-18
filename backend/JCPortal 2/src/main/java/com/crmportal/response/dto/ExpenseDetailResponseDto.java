package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDetailResponseDto {

	private Long id;
	
	private String expenseDate;
	
	private String perticular;

	private String paymentMode;
	
	private BigDecimal amount;

	private String remarks;
	
	private Long km;
	
	private Long expenseId;
	
	private Long accountContactId;
	
	private Long userId;
	
	private String docPath;
	
}
