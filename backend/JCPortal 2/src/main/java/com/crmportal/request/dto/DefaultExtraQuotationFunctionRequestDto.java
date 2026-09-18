package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultExtraQuotationFunctionRequestDto {

	private Long id;
	private String name;
	private String nameHindi;
	private String nameGujarati;
	private BigDecimal price;
	private Long userId;
}
