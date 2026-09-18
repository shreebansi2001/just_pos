package com.crmportal.request.dto;


import java.math.BigDecimal;

import com.crmportal.response.dto.PaymentResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPlansHistoryRequestDto {

	private Long userId;
	private Long planId;
	private Long CoupenId;
	private Long extraPayId;
	private String cgst;
	private String sgst;
	private BigDecimal cgstAmt;
	private BigDecimal sgstAmt;
	private BigDecimal finalTotal;
	PaymentResponseDto paymentData;
}
