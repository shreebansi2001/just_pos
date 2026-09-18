package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoicePaymentDto {

	private Long invoicePaymentId;
	private BigDecimal amount;
	private String paymentDate;
	private String paymentMode;

	private Long bankAccountId;
	private Long cashTypeId;

	private String status;
}
