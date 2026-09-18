package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExclusiveThemePaymentRequestDto {

	private Long adminTemplateId;
	
	private BigDecimal price;
	
	
}
