package com.crmportal.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PaymentResponseDto {
	private String paymentorderid;
	private String payid;
	private String paysignature;
	private String paymentresponse;
	private Boolean paymentdone;
}
