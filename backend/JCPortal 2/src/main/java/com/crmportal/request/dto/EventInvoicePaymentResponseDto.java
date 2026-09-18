package com.crmportal.request.dto;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInvoicePaymentResponseDto {

	private Long id;
	private String advancePaymentDate;
	private String advancePaymentNotes;
	private BigInteger advancePayment;
	private Long bankId;
	private Long cashAccountId;
	private String paymentMode;
	private String vendorCode;
}
