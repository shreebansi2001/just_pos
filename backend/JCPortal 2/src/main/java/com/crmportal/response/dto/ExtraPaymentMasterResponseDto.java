package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraPaymentMasterResponseDto {

	private Long id;
	private String name;
	private BigDecimal price;
	private String description;
	private String createdAt;
	private Boolean isDelete;
}
