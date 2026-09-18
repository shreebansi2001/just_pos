package com.crmportal.request.dto;

import java.math.BigDecimal;

import com.crmportal.response.dto.PaymentResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpgradedListOfModulePaymentRequestDto {
	
	private Long upgradeModuleId;
	private BigDecimal payAmnt;
	private Boolean isOnline;
	private PaymentResponseDto paymentData;

}
