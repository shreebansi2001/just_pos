package com.crmportal.response.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDownPaymentResponseDto {

	private Long id;
	private String paymentType;
	private String transactionDateTime;
	private String remarks;
	private Float amount; // paid amount , amount
	private Float paidAmount;
	private String payid; // transactionid
	private String docPath;
	private Boolean paymentDone;
}
