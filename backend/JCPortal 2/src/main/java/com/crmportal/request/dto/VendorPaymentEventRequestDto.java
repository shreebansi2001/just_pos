package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorPaymentEventRequestDto {

	private Long eventId;
	private BigDecimal receivedAmount;
	private BigDecimal settlementAmount;
	private BigDecimal payAmount;

}
