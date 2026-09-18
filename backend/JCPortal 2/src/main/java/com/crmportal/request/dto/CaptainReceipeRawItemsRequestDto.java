package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptainReceipeRawItemsRequestDto {

	@NotNull(message = "Id is required.")
	private Long id;
	
	@NotNull(message = "Raw item id is required.")
	private Long rawItemId;
	
	@NotNull(message = "Qty is required.")
	private BigDecimal qty;
	
	@NotNull(message = "Unit id is required.")
	private Long unitId;
	
	@NotNull(message = "Rate is required.")
	private BigDecimal rate;

}
