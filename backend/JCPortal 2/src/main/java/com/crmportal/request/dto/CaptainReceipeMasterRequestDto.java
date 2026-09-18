package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptainReceipeMasterRequestDto {

	@NotNull(message = "Id is required.")
	private Long id;
	
	@NotNull(message = "Name is required.")
	private String name;
	
	@NotNull(message = "User id is required.")
	private Long userId;
	
	@NotNull(message = "Weight is required.")
	private BigDecimal weight;
	
	@NotNull(message = "Unit id is required.")
	private Long unitId;
	
	@NotNull(message = "Rate is required.")
	private BigDecimal rate;
	
	@Valid
	@NotEmpty(message = "Minimum 1 raw item is required.")
	private Set<CaptainReceipeRawItemsRequestDto> rawItems;
}
