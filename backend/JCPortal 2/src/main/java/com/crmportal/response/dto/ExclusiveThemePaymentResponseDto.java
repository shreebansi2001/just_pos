package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExclusiveThemePaymentResponseDto {

	private Long id;
	
	private Long adminTemplateId;
	
	private Boolean isPayment;
	
	private String paymentType;
	
	private BigDecimal price;
}
