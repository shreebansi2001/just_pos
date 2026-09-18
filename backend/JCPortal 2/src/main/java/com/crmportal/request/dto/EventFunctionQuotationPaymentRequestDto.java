package com.crmportal.request.dto;

import java.math.BigInteger;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionQuotationPaymentRequestDto {

	private Long id;
	private String advancePaymentDate;
	private String advancePaymentNotes;
	private BigInteger advancePayment;
	private Long bankId;
	private Long cashAccountId;
	private String paymentMode;
}
