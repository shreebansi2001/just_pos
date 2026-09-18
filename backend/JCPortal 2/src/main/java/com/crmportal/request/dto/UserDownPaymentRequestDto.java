package com.crmportal.request.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDownPaymentRequestDto {

	private Long id;
	private String paymentType;
	private String transactionDate;
	private String remarks;
	private Float amount; // amount
	private Float paidAmount; // paid amount
	private String payid; // transactionid
	private MultipartFile docPath;
	
	
}
