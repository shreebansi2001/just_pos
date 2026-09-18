package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HallPackageRateRequestDto {

	private Long id;
	private Long hallId;
	private Long packageId;
	private Integer packageSequence;
	private String tierLabel;
	private Integer minGuests;
	private Integer tierSequence;
	private BigDecimal price;
	private Long userId;
}
