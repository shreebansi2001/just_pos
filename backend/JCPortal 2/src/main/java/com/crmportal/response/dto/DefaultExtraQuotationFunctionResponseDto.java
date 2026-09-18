package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultExtraQuotationFunctionResponseDto {

	private Long id;
	private String name;
	private Boolean isActive;
	private Long userId;
	private String nameHindi;
	private String nameGujarati;
	private BigDecimal price;
}
