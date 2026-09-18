package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraPaymentRequestDto {
	private Long id;
	private BigDecimal price;
	private String description;
	private String name;
}
