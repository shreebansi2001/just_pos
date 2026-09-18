package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorPaymentResponseDto {

	private BigDecimal totalAmt;
	private BigDecimal paidAmt;
	private BigDecimal pendingAmt;
	private Long eventId;
	private String eventName;
	
	private List<AllVendorsPaymentResponseDto> vendorsPayment;
}
