package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemsRequestDto {

	private Long invoiceItemId;
	
	private Long planHistoryId;
	
	@NotBlank(message = "Item name is required")
	private String itemName;
	
	@NotNull(message = "Quantity is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Qty must be greater than 0")
	private BigDecimal qty;
	
	@NotNull(message = "Rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0")
	private BigDecimal rate;
	
	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
	private BigDecimal amount;
	
	private String description;
	
	private String hsnCode;
	
	private BigDecimal taxPercent; 
	
	private BigDecimal taxAmount;
}
