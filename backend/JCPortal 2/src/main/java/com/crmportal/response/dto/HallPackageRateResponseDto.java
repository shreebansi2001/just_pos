package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HallPackageRateResponseDto {

	private Long id;
	private Long hallId;
	private String hallName;
	private Long packageId;
	private String packageName;
	private String packageNameHindi;
	private String packageNameGujarati;
	private Integer packageSequence;
	private String tierLabel;
	private Integer minGuests;
	private Integer tierSequence;
	private BigDecimal price;
	private Boolean isActive;
}
