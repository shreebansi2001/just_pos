package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExclusivePaymentRequestDto {

	private Long adminTemplateId;

	private BigDecimal price;

	private Boolean isPayment;

	private String payid;

	private String paysignature;

	private String paymentresponse;

	private String internalorderid;
	
	private String paymentType;

}
