package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInvoiceFunctionItemsResponseDto {

	private Long id;
	private String functionName;
	private String functionDate;
	private Integer pax;
	private Integer extraPax;
	private BigDecimal extraTax;
	private BigDecimal taxRate;
	private BigDecimal ratePerPlate;
	private BigDecimal offeredRate;
	private BigDecimal amount;
	private Boolean isEventFunction;
	private Long extraChargesId;
	private Boolean isExtraCharges;
}
