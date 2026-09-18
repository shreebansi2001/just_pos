package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import com.crmportal.response.dto.PaymentResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpgradedModulePaymentRequestDto {

	private Long userId;
	private List<UserUpgradedListOfModulePaymentRequestDto> userUpgradedModulePayments;
	
}
